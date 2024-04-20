package com.example.application.data.service;

import com.example.application.data.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AuthorRepository
        extends
            JpaRepository<Author, Long>,
            JpaSpecificationExecutor<Author> {

}
