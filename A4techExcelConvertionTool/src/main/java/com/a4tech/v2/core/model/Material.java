package com.a4tech.v2.core.model;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(Include.NON_NULL)
public class Material {

    @JsonProperty("Name")
    private String              name;
    @JsonProperty("Alias")
    private String              alias;
    @JsonProperty("Combo")
    private Combo         combos         = null;
    @JsonProperty("BlendMaterials")
    private List<BlendMaterial> blendMaterials = null;

    // STORY: VELO-8350
    // Author: ZAhmed, Date: 11/04/2015, Fix Version 1.3.10
    // Changes: Need an additional field for API to expose the RADAR Field for different criteria: CustomValueCode
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
     * @return the combos
     */
    @JsonProperty("Combo")
    public Combo getCombo() {
        return combos;
    }

    /**
     * @param combos
     *            the combos to set
     */
    @JsonProperty("Combo")
    public void setCombo(Combo combos) {
        this.combos = combos;
    }

    /**
     * @return the blendMaterials
     */
    public List<BlendMaterial> getBlendMaterials() {
        return blendMaterials;
    }

    /**
     * @param blendMaterials
     *            the blendMaterials to set
     */
    public void setBlendMaterials(List<BlendMaterial> blendMaterials) {
        this.blendMaterials = blendMaterials;
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

    /**
     * @return the customerOrderCode
     */
    public String getCustomerOrderCode() {
        return customerOrderCode;
    }

    /**
     * @param customerOrderCode the customerOrderCode to set
     */
    public void setCustomerOrderCode(String customerOrderCode) {
        this.customerOrderCode = customerOrderCode;
    }

}
