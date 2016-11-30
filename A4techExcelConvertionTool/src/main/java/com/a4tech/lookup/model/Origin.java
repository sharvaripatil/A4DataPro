package com.a4tech.lookup.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Origin {
	@JsonProperty("origins")
   List<String> listOfOrigins = null;

public List<String> getListOfOrigins() {
	return listOfOrigins;
}

public void setListOfOrigins(List<String> listOfOrigins) {
	this.listOfOrigins = listOfOrigins;
}
   
}
