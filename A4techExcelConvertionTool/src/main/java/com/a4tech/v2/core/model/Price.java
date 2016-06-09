package com.a4tech.v2.core.model;

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
    @JsonProperty("Price")
    private String    price;
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

    @JsonProperty("Price")
    public String getPrice() {
        return price;
    }

    @JsonProperty("Price")
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
