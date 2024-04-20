package com.example.application.views.bookstoreview;

import com.example.application.data.entity.BookStore;
import com.example.application.data.service.BookStoreService;
import com.example.application.views.MainLayout;
import com.example.application.views.bookstoreview.BookStoreView.Filters;
import com.vaadin.collaborationengine.CollaborationAvatarGroup;
import com.vaadin.collaborationengine.CollaborationBinder;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("BookStoreView")
@Route(value = "BookStoreView/:samplePersonID?/:action?(edit)", layout = MainLayout.class)
@Uses(Icon.class)
public class BookStoreView extends Div implements BeforeEnterObserver {

    private final String SAMPLEPERSON_ID = "samplePersonID";
    private final String SAMPLEPERSON_EDIT_ROUTE_TEMPLATE = "BookStoreView/%s/edit";

    private Grid<BookStore> grid = new Grid<>(BookStore.class, false);

    CollaborationAvatarGroup avatarGroup;
    
    private Filters filters;
    private TextField storeName;
    private TextField address;
    private DatePicker dateOfEstablishment;
    private TextField gsm;
    //private ComboBox<BookStore> secondBookStore = new ComboBox<BookStore>("Test BookStores");

    private final Button cancel = new Button("Cancel");
    private final Button delete = new Button("Delete");
    private final Button save = new Button("Save");

    private final CollaborationBinder<BookStore> binder;

    private BookStore bookStore;

    private final BookStoreService bookStoreService;

    public BookStoreView(BookStoreService bookStoreService) {
        this.bookStoreService = bookStoreService;
        addClassNames("bookStore-view-view");

        // UserInfo is used by Collaboration Engine and is used to share details
        // of users to each other to able collaboration. Replace this with
        // information about the actual user that is logged, providing a user
        // identifier, and the user's real name. You can also provide the users
        // avatar by passing an url to the image as a third parameter, or by
        // configuring an `ImageProvider` to `avatarGroup`.
        UserInfo userInfo = new UserInfo(UUID.randomUUID().toString(), "Steve Lange");
        
        filters = new Filters(() -> refreshGrid());
        VerticalLayout layout = new VerticalLayout(filters, createGrid());
        layout.setSizeUndefined();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
        
        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        avatarGroup = new CollaborationAvatarGroup(userInfo, null);
        avatarGroup.getStyle().set("visibility", "hidden");

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SAMPLEPERSON_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(BookStoreView.class);
            }
        });

        // Configure Form
        binder = new CollaborationBinder<>(BookStore.class, userInfo);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });
        
        delete.addClickListener(e -> {
            final Dialog dialog = new Dialog();
            final Button yes = new Button("Yes", removeL -> {
            	bookStoreService.delete(grid.asSingleSelect().getValue().getId());
                dialog.close();
                refreshGrid();
            });
            final Button no = new Button("No", removeL -> dialog.close());
            final HorizontalLayout removeDialogButtons = new HorizontalLayout(yes, no);
            dialog.setWidth("350px");
            dialog.setHeight("200px");
            dialog.add(new VerticalLayout(new H3("Remove entry?"), removeDialogButtons));
            dialog.open();
        });

        save.addClickListener(e -> {
            try {
                if (this.bookStore == null) {
                    this.bookStore = new BookStore();
                }
                binder.writeBean(this.bookStore);
                bookStoreService.update(this.bookStore);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(BookStoreView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }
    
    //Filter
    public static class Filters extends HorizontalLayout implements Specification<BookStore> {

        private final TextField storeName = new TextField("Store name");
        private final TextField address = new TextField("Store address");
        private final DatePicker startDate = new DatePicker("Date of Establishment");
        private final DatePicker endDate = new DatePicker();
        private final TextField gsm = new TextField("Phone number");
        
        
        public Filters(Runnable onSearch) {

            setWidthFull(); //setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);
            storeName.setPlaceholder("BookStore name");

            // Action buttons
            Button resetBtn = new Button("Reset");
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                storeName.clear();
                address.clear();
                startDate.clear();
                endDate.clear();
                gsm.clear();
                onSearch.run();
            });
            Button searchBtn = new Button("Search");
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());
            
            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");
            
            add(storeName, address, createDateRangeFilter(), gsm, actions);
            
        }
        

        private Component createDateRangeFilter() {
            startDate.setPlaceholder("From");

            endDate.setPlaceholder("To");

            // For screen readers
            setAriaLabel(startDate, "From date");
            setAriaLabel(endDate, "To date");

            FlexLayout dateRangeComponent = new FlexLayout(startDate, new Text(" – "), endDate);
            dateRangeComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
            dateRangeComponent.addClassName(LumoUtility.Gap.XSMALL);

            return dateRangeComponent;
        }

        private void setAriaLabel(DatePicker datePicker, String label) {
            datePicker.getElement().executeJs("const input = this.inputElement;" //
                    + "input.setAttribute('aria-label', $0);" //
                    + "input.removeAttribute('aria-labelledby');", label);
        }

        @Override
        public Predicate toPredicate(Root<BookStore> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            List<Predicate> predicates = new ArrayList<>();

            if (!storeName.isEmpty()) {
                String lowerCaseFilter = storeName.getValue().toLowerCase();
                Predicate firstNameMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("storeName")),
                        lowerCaseFilter + "%");
                predicates.add(criteriaBuilder.or(firstNameMatch));
            }
            if (!address.isEmpty()) {
                String lowerCaseFilter = address.getValue().toLowerCase();
                Predicate lastNameMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("address")),
                        lowerCaseFilter + "%");
                predicates.add(criteriaBuilder.or(lastNameMatch));
            }
            if (startDate.getValue() != null) {
                String databaseColumn = "dateOfEstablishment";
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(databaseColumn),
                        criteriaBuilder.literal(startDate.getValue())));
            }
            if (endDate.getValue() != null) {
                String databaseColumn = "dateOfEstablishment";
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.literal(endDate.getValue()),
                        root.get(databaseColumn)));
            }
            if (!gsm.isEmpty()) {
                String lowerCaseFilter = gsm.getValue().toLowerCase();
                Predicate lastNameMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("gsm")),
                        lowerCaseFilter + "%");
                predicates.add(criteriaBuilder.or(lastNameMatch));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }

        private String ignoreCharacters(String characters, String in) {
            String result = in;
            for (int i = 0; i < characters.length(); i++) {
                result = result.replace("" + characters.charAt(i), "");
            }
            return result;
        }

        private Expression<String> ignoreCharacters(String characters, CriteriaBuilder criteriaBuilder,
                Expression<String> inExpression) {
            Expression<String> expression = inExpression;
            for (int i = 0; i < characters.length(); i++) {
                expression = criteriaBuilder.function("replace", String.class, expression,
                        criteriaBuilder.literal(characters.charAt(i)), criteriaBuilder.literal(""));
            }
            return expression;
        }

    }
    //end of Filter
    
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> samplePersonId = event.getRouteParameters().get(SAMPLEPERSON_ID).map(Long::parseLong);
        if (samplePersonId.isPresent()) {
            Optional<BookStore> samplePersonFromBackend = bookStoreService.get(samplePersonId.get());
            if (samplePersonFromBackend.isPresent()) {
                populateForm(samplePersonFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested samplePerson was not found, ID = %d", samplePersonId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(BookStoreView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        storeName = new TextField("Store Name");
        address = new TextField("Store Address");
        dateOfEstablishment = new DatePicker("Date Of Birth");
        gsm = new TextField("Phone number");
        //secondBookStore = new ComboBox<BookStore>("Test BookStores");
        //secondBookStore.setItems(bookStoreService.getAll());
        //secondBookStore.setItemLabelGenerator(a->a.getBookStoreName());
        formLayout.add(storeName, address, dateOfEstablishment, gsm);

        editorDiv.add(avatarGroup, formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, delete, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
        grid.setItems(query -> bookStoreService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                filters).stream());
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(BookStore value) {
        this.bookStore = value;
        String topic = null;
        if (this.bookStore != null && this.bookStore.getId() != null) {
            topic = "samplePerson/" + this.bookStore.getId();
            avatarGroup.getStyle().set("visibility", "visible");
        } else {
            avatarGroup.getStyle().set("visibility", "hidden");
        }
        binder.setTopic(topic, () -> this.bookStore);
        avatarGroup.setTopic(topic);

    }
    
    private Component createGrid() {
        grid = new Grid<>(BookStore.class, false);
        grid.addColumn("storeName").setAutoWidth(true);
        grid.addColumn("address").setAutoWidth(true);
        grid.addColumn("dateOfEstablishment").setAutoWidth(true);
        grid.addColumn("gsm").setAutoWidth(true);
        //grid.addColumn("secondBookStore").setAutoWidth(true);

        grid.setItems(query -> bookStoreService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                filters).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }
    
}