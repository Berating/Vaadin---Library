package com.example.application.data.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.application.data.entity.Books;

@Service
public class BooksService {

	private final BooksRepository repository;

    public BooksService(BooksRepository repository) {
        this.repository = repository;
    }

    public Optional<Books> get(Long id) {
        return repository.findById(id);
    }

    public Books update(Books entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Books> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Books> list(Pageable pageable, Specification<Books> filter) {
        return repository.findAll(filter, pageable);
    }
    
    public List<Books> getAll(){
    	return repository.findAll();
    }
    
    public int count() {
        return (int) repository.count();
    }
	
	
	
}
