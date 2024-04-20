package com.example.application.data.service;

import com.example.application.data.entity.Author;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {

    private final AuthorRepository repository;

    public AuthorService(AuthorRepository repository) {
        this.repository = repository;
    }

    public Optional<Author> get(Long id) {
        return repository.findById(id);
    }

    public Author update(Author entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Author> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Author> list(Pageable pageable, Specification<Author> filter) {
        return repository.findAll(filter, pageable);
    }
    
    public List<Author> getAll(){
    	return repository.findAll();
    }
    
    public int count() {
        return (int) repository.count();
    }

}
