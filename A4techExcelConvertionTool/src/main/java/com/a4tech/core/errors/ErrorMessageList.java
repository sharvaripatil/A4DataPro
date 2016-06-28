package com.a4tech.core.errors;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorMessageList {
	
	@JsonProperty("Errors")
	private List<ErrorMessage> errors;

	public List<ErrorMessage> getErrors() {
		return errors;
	}

	public void setErrors(List<ErrorMessage> errors) {
		this.errors = errors;
	}

}
