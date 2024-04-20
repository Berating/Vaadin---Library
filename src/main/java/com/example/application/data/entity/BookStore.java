package com.example.application.data.entity;

import java.time.LocalDate;

import javax.persistence.Entity;

@Entity
public class BookStore extends AbstractEntity{
	
	private String storeName;
	private String address;
	private LocalDate dateOfEstablishment;
	private String gsm;
	
	
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public LocalDate getDateOfEstablishment() {
		return dateOfEstablishment;
	}
	public void setDateOfEstablishment(LocalDate dateOfEstablishment) {
		this.dateOfEstablishment = dateOfEstablishment;
	}
	public String getGsm() {
		return gsm;
	}
	public void setGsm(String gsm) {
		this.gsm = gsm;
	}
}
