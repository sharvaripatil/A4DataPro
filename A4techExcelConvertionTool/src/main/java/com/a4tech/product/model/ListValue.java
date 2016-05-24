package com.a4tech.product.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ListValue extends BaseValue {
	public ListValue() {
		// TODO Auto-generated constructor stub
	}

	public ListValue(List<Value> value) {
		this.value = value;
	}

	@JsonProperty("Value")
	private List<Value> value;

	@JsonProperty("Value")
	public List<Value> getValue() {
		return value;
	}

	@JsonProperty("Value")
	public void setValue(List<Value> value) {
		this.value = value;
	}

}
