package com.a4tech.product.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(Include.NON_NULL)
public class BatteryInformation {
	
	@JsonProperty("Name")
	private String name;

	@JsonProperty("Comments")
	private String comments;

	@JsonProperty("CustomerOrderCode")
	private String customerOrderCode;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getCustomerOrderCode() {
        return customerOrderCode;
    }

	public void setCustomerOrderCode(String customerOrderCode) {
        this.customerOrderCode = customerOrderCode;
    }

}
