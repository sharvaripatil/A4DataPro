package com.a4tech.lookup.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Materials {
	 @JsonProperty("materials")
	 List<String> materialValues = null;

	public List<String> getMaterialValues() {
		return materialValues;
	}

	public void setMaterialValues(List<String> materialValues) {
		this.materialValues = materialValues;
	}	
}
