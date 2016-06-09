package com.a4tech.v2.core.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Dimension {
    
	@JsonProperty("Values")
	private List<Values> values;
	
	public List<Values> getValues() {
		return values;
	}

	public void setValues(List<Values> values) {
		this.values = values;
	}

}
