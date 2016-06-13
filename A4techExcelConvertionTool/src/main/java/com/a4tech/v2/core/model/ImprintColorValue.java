package com.a4tech.v2.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class ImprintColorValue {

	@JsonProperty("Name")
	private String name;
	@JsonProperty("CustomerOrderCode")
	private String customerOrderCode;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCustomerOrderCode() {
		return customerOrderCode;
	}

	public void setCustomerOrderCode(String customerOrderCode) {
		this.customerOrderCode = customerOrderCode;
	}
	
	public String toString() {
		return "{\"Name\": \"" + this.getName() + "\", \"CustomerOrderCode\": \"" + this.getCustomerOrderCode() + "\"},";
	}

	public int hashCode(){
        int hashcode = 0;
        hashcode = name.hashCode();
        return hashcode;
    }
     
    public boolean equals(Object obj){
        if (obj instanceof ImprintColorValue) {
        	ImprintColorValue color = (ImprintColorValue) obj;
            //return (pp.item.equals(this.item));
        	return (color.getName().equalsIgnoreCase(this.name));
        } else {
            return false;
        }
    }
}
