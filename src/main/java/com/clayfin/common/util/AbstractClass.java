package com.clayfin.common.util;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class AbstractClass {
	
	  @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	  protected Long id;
	  
	  @CreationTimestamp
	  @Column(name = "CREATED_AT")
	  private LocalDateTime createdTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(LocalDateTime createdTime) {
		this.createdTime = createdTime;
	}
	  
	  

}
