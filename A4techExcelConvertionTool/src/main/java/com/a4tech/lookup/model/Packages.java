package com.a4tech.lookup.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Packages {
	@JsonProperty("packages")
   List<String> listOfPackages = null;

	public List<String> getListOfPackages() {
		return listOfPackages;
	}

	public void setListOfPackages(List<String> listOfPackages) {
		this.listOfPackages = listOfPackages;
	}


   
}
