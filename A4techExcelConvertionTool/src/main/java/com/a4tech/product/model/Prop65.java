package com.a4tech.product.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Prop65 {

	@JsonProperty("WarningCode")
	private String warningCode ="";
	@JsonProperty("WarningText")
	private String warningText ="";
	@JsonProperty("Chemicals")
	private List<Chemical> chemicals = new ArrayList<>();

	public String getWarningCode() {
		return warningCode;
	}

	public void setWarningCode(String warningCode) {
		this.warningCode = warningCode;
	}
	public String getWarningText() {
		return warningText;
	}

	public void setWarningText(String warningText) {
		this.warningText = warningText;
	}

	public List<Chemical> getChemicals() {
		return chemicals;
	}

	public void setChemicals(List<Chemical> chemicals) {
		this.chemicals = chemicals;
	}
}
