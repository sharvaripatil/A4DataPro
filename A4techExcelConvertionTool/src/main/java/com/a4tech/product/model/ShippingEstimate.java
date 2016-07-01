package com.a4tech.product.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
@XmlRootElement(name = "ShippingEstimate")
@JsonInclude(Include.NON_NULL)
public class ShippingEstimate {

    @JsonProperty("NumberOfItems")
    private List<NumberOfItems> numberOfItems;
    @JsonProperty("Dimensions")
    private Dimensions    dimensions;
    @JsonProperty("Weight")
    private List<Weight>       weight;
    @JsonProperty("Dimensions")
    public Dimensions getDimensions() {
        return dimensions;
    }
    
	@JsonProperty("Dimensions")
    public void setDimensions(Dimensions dimensions) {
        this.dimensions = dimensions;
    }
	@JsonProperty("Weight")
    public List<Weight> getWeight() {
		return weight;
	}
	@JsonProperty("Weight")
	public void setWeight(List<Weight> weight) {
		this.weight = weight;
	}

	@JsonProperty("NumberOfItems")
    public List<NumberOfItems> getNumberOfItems() {
		return numberOfItems;
	}
    @JsonProperty("NumberOfItems")
	public void setNumberOfItems(List<NumberOfItems> numberOfItems) {
		this.numberOfItems = numberOfItems;
	}


}
