package com.a4tech.product.model;

import java.util.Comparator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(Include.NON_NULL)
public class Price implements Comparator<Price> {

    @JsonProperty("Sequence")
    private Integer    sequence;

    @JsonProperty("Qty")
    private Integer    qty;

    @JsonProperty("ListPrice")
    private String    price;

    @JsonProperty("NetCost")
    private String netCost;

    @JsonProperty("DiscountCode")
    private String    discountCode;

    @JsonProperty("PriceUnit")
    private PriceUnit priceUnit;
    
	@JsonProperty("Sequence")
    public Integer getSequence() {
        return sequence;
    }

    @JsonProperty("Sequence")
    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    @JsonProperty("Qty")
    public Integer getQty() {
        return qty;
    }

    @JsonProperty("Qty")
    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @JsonProperty("DiscountCode")
    public String getDiscountCode() {
        return discountCode;
    }

    @JsonProperty("DiscountCode")
    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    @JsonProperty("PriceUnit")
    public PriceUnit getPriceUnit() {
        return priceUnit;
    }

    @JsonProperty("PriceUnit")
    public void setPriceUnit(PriceUnit priceUnit) {
        this.priceUnit = priceUnit;
    }
    
    public String getNetCost() {
		return netCost;
	}

	public void setNetCost(String netCost) {
		this.netCost = netCost;
	}


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

	@Override
	public int compare(Price p1, Price p2) {
		if(null==p1.getSequence())
			p1.setSequence(0);
		if(null==p2.getSequence())
			p2.setSequence(0);
		return p1.getSequence()-p2.getSequence();
	}

}
