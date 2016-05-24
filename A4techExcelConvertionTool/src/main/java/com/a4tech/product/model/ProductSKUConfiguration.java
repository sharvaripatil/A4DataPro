package com.a4tech.product.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)

@XmlSeeAlso({ValueWrapper.class})
public class ProductSKUConfiguration {

	@JsonProperty("Criteria")
    private String criteria;
    
    @JsonProperty("Value")
    @XmlElement(name="Value")
    private List<Object> value;
   // @JsonProperty("Value")

 //   private BaseValue value;

	@JsonProperty("Criteria")
    public String getCriteria() {
        return criteria;
    }

    @JsonProperty("Criteria")
    public void setCriteria(String criteria) {
        this.criteria = criteria;
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
