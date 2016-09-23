package com.a4tech.product.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Option {

    @JsonProperty("OptionType")
    private String       optionType;
    @JsonProperty("Name")
    private String       name;
    @JsonProperty("Values")
    private List<OptionValue> values = new ArrayList<OptionValue>();
    @JsonProperty("AdditionalInformation")
    private String       additionalInformation;
    @JsonProperty("CanOnlyOrderOne")
    private Boolean      canOnlyOrderOne;
    @JsonProperty("RequiredForOrder")
    private Boolean      requiredForOrder;

    @JsonProperty("OptionType")
    public String getOptionType() {
        return optionType;
    }

    @JsonProperty("OptionType")
    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    @JsonProperty("Name")
    public void setName(String name) {
        this.name = name;
    }
    @JsonProperty("AdditionalInformation")
    public String getAdditionalInformation() {
        return additionalInformation;
    }
	@JsonProperty("AdditionalInformation")
    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }
	 @JsonProperty("Values")
    public List<OptionValue> getValues() {
		return values;
	}
	 @JsonProperty("Values")
	public void setValues(List<OptionValue> values) {
		this.values = values;
	}

	@JsonProperty("CanOnlyOrderOne")
    public Boolean getCanOnlyOrderOne() {
        return canOnlyOrderOne;
    }

    @JsonProperty("CanOnlyOrderOne")
    public void setCanOnlyOrderOne(Boolean canOnlyOrderOne) {
        this.canOnlyOrderOne = canOnlyOrderOne;
    }

    @JsonProperty("RequiredForOrder")
    public Boolean getRequiredForOrder() {
        return requiredForOrder;
    }

    @JsonProperty("RequiredForOrder")
    public void setRequiredForOrder(Boolean requiredForOrder) {
        this.requiredForOrder = requiredForOrder;
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
