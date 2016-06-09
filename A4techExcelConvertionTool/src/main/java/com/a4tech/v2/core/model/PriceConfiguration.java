package com.a4tech.v2.core.model;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
//@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = As.PROPERTY, property = "@class")
public class PriceConfiguration {

    @JsonProperty("Criteria")
    private String criteria;
    @JsonProperty("Value")
    //@XmlElement(name="Value")
    //@XmlAnyElement(lax=true)
    private List<Object> value;
    
    @JsonProperty("OptionName")
    private String optionName;

    @JsonProperty("Criteria")
    public String getCriteria() {
        return criteria;
    }

    @JsonProperty("Criteria")
    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }
    

	/**
     * @return the optionName
     */
    public String getOptionName() {
        return optionName;
    }

    /**
     * @param optionName
     *            the optionName to set
     */
    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }
    @JsonProperty("Value")
	public List<Object> getValue() {
		return value;
	}
    @JsonProperty("Value")
	public void setValue(List<Object> value) {
		this.value = value;
	}

}
