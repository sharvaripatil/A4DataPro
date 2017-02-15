package com.a4tech.lookup.model;

import java.util.List;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Themes {
	@JsonProperty("themes")
	private List<String> themes = null;

	public List<String> getThemes() {
		return themes;
	}

	public void setThemes(List<String> themes) {
		this.themes = themes;
	}





}
