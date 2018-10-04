package com.a4tech.product.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Chemical {
	@JsonProperty("Name")
	private String name = "";
	@JsonProperty("RiskCode")
	private String riskCode ="";
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRiskCode() {
		return riskCode;
	}
	public void setRiskCode(String riskCode) {
		this.riskCode = riskCode;
	}

}
