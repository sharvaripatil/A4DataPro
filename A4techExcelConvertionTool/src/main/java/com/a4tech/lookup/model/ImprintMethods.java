package com.a4tech.lookup.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImprintMethods {

	 @JsonProperty("imprintMethods")
	 List<String> imprintValues = null;

	public List<String> getImprintValues() {
		return imprintValues;
	}

	public void setImprintValues(List<String> imprintValues) {
		this.imprintValues = imprintValues;
	}
	 
}
