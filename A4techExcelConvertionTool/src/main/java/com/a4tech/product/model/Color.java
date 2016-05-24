package com.a4tech.product.model;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(Include.NON_NULL)
public class Color {

    @JsonProperty("Name")
    private String      name;
    @JsonProperty("Alias")
    private String      alias;
    @JsonProperty("RGBHex")
    private String      RGBHex;
    @JsonProperty("Combos")
    private List<Combo> combos = null;
    
    @JsonProperty("CustomerOrderCode")
    private String customerOrderCode;

	@JsonProperty("Name")
    public String getName() {
        return name;
    }

    @JsonProperty("Name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("Alias")
    public String getAlias() {
        return alias;
    }

    @JsonProperty("Alias")
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * @return the rGBHex
     */
    @JsonProperty("RGBHex")
    public String getRGBHex() {
        return RGBHex;
    }

    /**
     * @param rGBHex
     *            the rGBHex to set
     */
    @JsonProperty("RGBHex")
    public void setRGBHex(String rGBHex) {
        RGBHex = rGBHex;
    }

    /**
     * @return the combos
     */
    public List<Combo> getCombos() {
        return combos;
    }

    /**
     * @param combos
     *            the combos to set
     */
    
    public void setCombos(List<Combo> combos) {
        this.combos = combos;
    }

    /**
     * @return the customerOrderCode
     */
    @JsonProperty("CustomerOrderCode")
    public String getCustomerOrderCode() {
        return customerOrderCode;
    }

    /**
     * @param customerOrderCode the customerOrderCode to set
     */
    @JsonProperty("CustomerOrderCode")
    public void setCustomerOrderCode(String customerOrderCode) {
        this.customerOrderCode = customerOrderCode;
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

}
