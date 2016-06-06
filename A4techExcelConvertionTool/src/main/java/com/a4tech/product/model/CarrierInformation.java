package com.a4tech.product.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CarrierInformation {
    
    @JsonProperty("Value")
    private String value;
    
    @JsonProperty("CustomerOrderCode")
    private String customerOrderCode;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCustomerOrderCode() {
        return customerOrderCode;
    }

    public void setCustomerOrderCode(String customerOrderCode) {
        this.customerOrderCode = customerOrderCode;
    }

}
