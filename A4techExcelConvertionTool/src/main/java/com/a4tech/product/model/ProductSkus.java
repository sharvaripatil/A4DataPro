package com.a4tech.product.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(Include.NON_NULL)
public class ProductSkus {

    @JsonProperty("SKU")
    private String SKU;
    @JsonProperty("Hazmat")
    private String hazmat;
    @JsonProperty("CloseOut")
    private boolean closeOut;
    @JsonProperty("Configurations")
    private List<ProductSKUConfiguration> criteria;
    
	@JsonProperty("SKU")
    public String getSKU() {
        return SKU;
    }

    @JsonProperty("SKU")
    public void setSKU(String SKU) {
        this.SKU = SKU;
    }
    @JsonProperty("Inventory")
	private Inventory Inventory;
    @JsonProperty("Inventory")
	public Inventory getInventory() {
		return Inventory;
	}
    @JsonProperty("Inventory")
	public void setInventory(Inventory inventory) {
		this.Inventory = inventory;
	}
    @JsonProperty("Configurations")
    public List<ProductSKUConfiguration> getConfigurations() {
        return criteria;
    }

    @JsonProperty("Configurations")
    public void setConfigurations(List<ProductSKUConfiguration> criteria) {
        this.criteria = criteria;
    }
    @JsonProperty("Hazmat")
    public String getHazmat() {
		return hazmat;
	}
    @JsonProperty("Hazmat")
	public void setHazmat(String hazmat) {
		this.hazmat = hazmat;
	}
    @JsonProperty("CloseOut")
	public boolean isCloseOut() {
		return closeOut;
	}
    @JsonProperty("CloseOut")
	public void setCloseOut(boolean closeOut) {
		this.closeOut = closeOut;
	
    }
}
