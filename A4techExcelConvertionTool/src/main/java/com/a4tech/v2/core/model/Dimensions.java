package com.a4tech.v2.core.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(Include.NON_NULL)
public class Dimensions {

    @JsonProperty("Length")
    private String length;
    @JsonProperty("Width")
    private String width;
    @JsonProperty("Height")
    private String height;
    @JsonProperty("LengthUnit")
    private String lengthUnit;
    @JsonProperty("WidthUnit")
    private String widthUnit;
    @JsonProperty("HeightUnit")
    private String heightUnit;
    public String getLengthUnit() {
		return lengthUnit;
	}

	public void setLengthUnit(String lengthUnit) {
		this.lengthUnit = lengthUnit;
	}

	public String getWidthUnit() {
		return widthUnit;
	}

	public void setWidthUnit(String widthUnit) {
		this.widthUnit = widthUnit;
	}

	public String getHeightUnit() {
		return heightUnit;
	}

	public void setHeightUnit(String heightUnit) {
		this.heightUnit = heightUnit;
	}

	@JsonProperty("Length")
    public String getLength() {
        return length;
    }

    @JsonProperty("Length")
    public void setLength(String length) {
        this.length = length;
    }

    @JsonProperty("Width")
    public String getWidth() {
        return width;
    }

    @JsonProperty("Width")
    public void setWidth(String width) {
        this.width = width;
    }

    @JsonProperty("Height")
    public String getHeight() {
        return height;
    }

    @JsonProperty("Height")
    public void setHeight(String height) {
        this.height = height;
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
