package com.a4tech.product.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@JsonInclude(Include.NON_NULL)
public class Availability {
	@JsonProperty("ParentCriteria")
	private String parentCriteria;
	@JsonProperty("ChildCriteria")
	private String childCriteria;
	@JsonProperty("ParentOptionName")
	private String parentOptionName;
	@JsonProperty("ChildOptionName")
	private String childOptionName;
	@JsonProperty("AvailableVariations")
	private List<AvailableVariations> availableVariations;
	public String getParentCriteria() {
		return parentCriteria;
	}
	public void setParentCriteria(String parentCriteria) {
		this.parentCriteria = parentCriteria;
	}
	public String getChildCriteria() {
		return childCriteria;
	}
	public void setChildCriteria(String childCriteria) {
		this.childCriteria = childCriteria;
	}
	public String getParentOptionName() {
		return parentOptionName;
	}
	public void setParentOptionName(String parentOptionName) {
		this.parentOptionName = parentOptionName;
	}
	public String getChildOptionName() {
		return childOptionName;
	}
	public void setChildOptionName(String childOptionName) {
		this.childOptionName = childOptionName;
	}
	public List<AvailableVariations> getAvailableVariations() {
		return availableVariations;
	}
	public void setAvailableVariations(List<AvailableVariations> availableVariations) {
		this.availableVariations = availableVariations;
	}
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Availability [parentCriteria=" + parentCriteria + ", childCriteria=" + childCriteria + ", parentOptionName="
                + parentOptionName + ", childOptionName=" + childOptionName + ", availableVariations=" + availableVariations + "]";
    }
	
	
	
}
