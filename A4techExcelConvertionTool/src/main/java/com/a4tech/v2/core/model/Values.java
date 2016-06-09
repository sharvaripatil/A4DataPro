package com.a4tech.v2.core.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name = "value")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(Include.NON_NULL)
public class Values {

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Value")
    private List<Value> value;
    
    // STORY: VELO-8350
    // Author: ZAhmed, Date: 11/04/2015, Fix Version 1.3.10
    // Changes: Need an additional field for API to expose the RADAR Field for different criteria: CustomValueCode
    @XmlElement(name = "CustomerOrderCode")
    @JsonProperty("CustomerOrderCode")
    private String customerOrderCode;


    @JsonProperty("Type")
    public String getType() {
        return type;
    }

    @JsonProperty("Type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("Value")
    public List<Value> getValue() {
        return value;
    }

    @JsonProperty("Value")
    public void setValue(List<Value> value) {
        this.value = value;
    }

    public String getCustomerOrderCode() {
        return customerOrderCode;
    }

    public void setCustomerOrderCode(String customerOrderCode) {
        this.customerOrderCode = customerOrderCode;
    }

}
