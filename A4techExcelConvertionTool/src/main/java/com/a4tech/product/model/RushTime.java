package com.a4tech.product.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(Include.NON_NULL)
public class RushTime {
	@JsonProperty("Available")
	private boolean available;

	@JsonProperty("Values")
	private List<RushTimeValue> rushTimeValues=null;

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public List<RushTimeValue> getRushTimeValues() {
		return rushTimeValues;
	}

	public void setRushTimeValues(List<RushTimeValue> rushTimeValues) {
		this.rushTimeValues = rushTimeValues;
	}
	
    
}
