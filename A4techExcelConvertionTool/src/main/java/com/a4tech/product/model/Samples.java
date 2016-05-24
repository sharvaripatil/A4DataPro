package com.a4tech.product.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Samples {

    @JsonProperty("SpecSampleAvailable")
    private Boolean specSampleAvailable;
    @JsonProperty("SpecInfo")
    private String  specInfo;
    @JsonProperty("ProductSampleAvailable")
    private Boolean productSampleAvailable;
    @JsonProperty("ProductSampleInfo")
    private String  productSampleInfo;

    /**
     * @return the specSampleAvailable
     */
    public Boolean getSpecSampleAvailable() {
        return specSampleAvailable;
    }

    /**
     * @param specSampleAvailable
     *            the specSampleAvailable to set
     */
    public void setSpecSampleAvailable(Boolean specSampleAvailable) {
        this.specSampleAvailable = specSampleAvailable;
    }

    /**
     * @return the specDetails
     */
    public String getSpecInfo() {
        return specInfo;
    }

    /**
     * @param specDetails
     *            the specDetails to set
     */
    public void setSpecInfo(String specDetails) {
        this.specInfo = specDetails;
    }

    /**
     * @return the productSampleAvailable
     */
    public Boolean getProductSampleAvailable() {
        return productSampleAvailable;
    }

    /**
     * @param productSampleAvailable
     *            the productSampleAvailable to set
     */
    public void setProductSampleAvailable(Boolean productSampleAvailable) {
        this.productSampleAvailable = productSampleAvailable;
    }

    /**
     * @return the productSampleDetails
     */
    public String getProductSampleInfo() {
        return productSampleInfo;
    }

    /**
     * @param productSampleDetails
     *            the productSampleDetails to set
     */
    public void setProductSampleInfo(String productSampleDetails) {
        this.productSampleInfo = productSampleDetails;
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
    @JsonIgnore
    public boolean isNull() {
        if (this == null) {
            return true;
        }
        if (this.equals(new Samples())) {
            return true;
        }
        if (this.specSampleAvailable == null && this.productSampleAvailable == null) {
            return true;
        }
        if (this.specSampleAvailable == null && this.productSampleAvailable != null) {
            this.specSampleAvailable = false;
        }
        
        if (this.specSampleAvailable != null && this.productSampleAvailable == null) {
            this.productSampleAvailable = false;
        }
        return false;
    }

}
