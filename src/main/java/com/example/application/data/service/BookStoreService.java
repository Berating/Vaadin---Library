package com.example.application.data.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.application.data.entity.BookStore;

@Service
public class BookStoreService {

	private final BookStoreRepository repository;

    public BookStoreService(BookStoreRepository repository) {
        this.repository = repository;
    }

    public Optional<BookStore> get(Long id) {
        return repository.findById(id);
    }

    public BookStore update(BookStore entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<BookStore> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<BookStore> list(Pageable pageable, Specification<BookStore> filter) {
        return repository.findAll(filter, pageable);
    }
    
    public List<BookStore> getAll(){
    	return repository.findAll();
    }
    
    public int count() {
        return (int) repository.count();
    }
	
	
	
}
