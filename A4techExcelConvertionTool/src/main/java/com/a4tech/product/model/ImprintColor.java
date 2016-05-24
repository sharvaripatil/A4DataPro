package com.a4tech.product.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImprintColor {

	@JsonProperty("Type")
	private String type;

	@JsonProperty("Values")
	private List<ImprintColorValue> values = new ArrayList<>();

	@JsonProperty("Type")
	public String getType() {
		return type;
	}

	@JsonProperty("Type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("Values")
	public List<ImprintColorValue> getValues() {
		return values;
	}

	@JsonProperty("Values")
	public void setValues(List<ImprintColorValue> values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return "\"ImprintColors\" : {\"Type\": \"" + this.getType() + "\", \"Values\" : [" + this.getValues() + "]}";
	}

}
