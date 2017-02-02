package com.a4tech.lookup.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Catalog {
	@JsonProperty("Name")
	private String name;
	@JsonProperty("MediaCitations")
	private List<MediaCitation> listOfMediaCitation;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<MediaCitation> getListOfMediaCitation() {
		return listOfMediaCitation;
	}
	public void setListOfMediaCitation(List<MediaCitation> listOfMediaCitation) {
		this.listOfMediaCitation = listOfMediaCitation;
	}

}
