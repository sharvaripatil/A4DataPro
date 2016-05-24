package com.a4tech.product.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SameDayRush {

	@JsonProperty("Available")
	private boolean available;

	@JsonProperty("Details")
	private String details;

	public boolean isAvailable() {
		return available;
	}
	
	public void setAvailable(boolean available) {
		this.available = available;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
}
