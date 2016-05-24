package com.a4tech.product.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(Include.NON_NULL)
public class RushTimeValue {
	@JsonProperty("BusinessDays")
    private String businessDays=null;
    @JsonProperty("Details")
    private String  details;
	public String getBusinessDays() {
		return businessDays;
	}
	public void setBusinessDays(String businessDays) {
		this.businessDays = businessDays;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}

}
