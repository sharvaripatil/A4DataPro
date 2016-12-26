package com.a4tech.lookup.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FobPoints {
	@JsonProperty("FobPoints")
	private List<String> fobPoints = null;

	public List<String> getFobPoints() {
		return fobPoints;
	}

	public void setFobPoints(List<String> fobPoints) {
		this.fobPoints = fobPoints;
	}

}
