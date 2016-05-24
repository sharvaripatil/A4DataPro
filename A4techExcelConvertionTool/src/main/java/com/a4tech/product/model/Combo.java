package com.a4tech.product.model;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(Include.NON_NULL)
public class Combo {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("BlendMaterials")
    private List<BlendMaterial> blendMaterials=null;
    @JsonProperty("RGBHex")
    private String rgbhex;
    

	public String getRgbhex() {
		return rgbhex;
	}

	public void setRgbhex(String rgbhex) {
		this.rgbhex = rgbhex;
	}

	public List<BlendMaterial> getBlendMaterials() {
		return blendMaterials;
	}

	public void setBlendMaterials(List<BlendMaterial> blendMaterials) {
		this.blendMaterials = blendMaterials;
	}

	/**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        this.type = type;
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
