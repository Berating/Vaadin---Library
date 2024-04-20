package com.example.application.data.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.application.data.entity.Author;
import com.example.application.data.entity.BookStore;

public interface BookStoreRepository
		extends
			JpaRepository<BookStore, Long>,
			JpaSpecificationExecutor<BookStore> {

}
