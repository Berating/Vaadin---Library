package com.example.application.data.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Books extends AbstractEntity {

	private String bookName;
	private LocalDate dateOfRelease;
	@ManyToOne
	private BookStore BookStore;
	private String genreBook;
	
	
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public LocalDate getDateOfRelease() {
		return dateOfRelease;
	}
	public void setDateOfRelease(LocalDate dateOfRelease) {
		this.dateOfRelease = dateOfRelease;
	}
	public BookStore getBookStore() {
		return BookStore;
	}
	public void setBookStore(BookStore bookStore) {
		BookStore = bookStore;
	}
	public String getGenreBook() {
		return genreBook;
	}
	public void setGenreBook(String genreBook) {
		this.genreBook = genreBook;
	}
	
	
	
	
}
