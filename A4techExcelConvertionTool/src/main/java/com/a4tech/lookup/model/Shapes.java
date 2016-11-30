package com.a4tech.lookup.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Shapes {
	@JsonProperty("shapes")
	List<String> shapeValues = null;

	public List<String> getShapeValues() {
		return shapeValues;
	}

	public void setShapeValues(List<String> shapeValues) {
		this.shapeValues = shapeValues;
	}

}
