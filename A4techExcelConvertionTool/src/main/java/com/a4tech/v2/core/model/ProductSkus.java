package com.a4tech.v2.core.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(Include.NON_NULL)
public class ProductSkus {

    @JsonProperty("SKU")
    private String SKU;
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
    
    /*
	@JsonProperty("InventoryLink")
	private String productInventoryLink = "";
	
	@JsonProperty("InventoryStatus")
	private String inventoryStatus = "";
	
	@JsonProperty("InventoryQuantity")
	private String inventoryQuantity = "";

	public String getProductInventoryLink() {
		return productInventoryLink;
	}

	public void setProductInventoryLink(String productInventoryLink) {
		this.productInventoryLink = productInventoryLink;
	}

	public String getInventoryStatus() {
		return inventoryStatus;
	}

	public void setInventoryStatus(String inventoryStatus) {
		this.inventoryStatus = inventoryStatus;
	}

	public String getInventoryQuantity() {
		return inventoryQuantity;
	}

	public void setInventoryQuantity(String inventoryQuantity) {
		this.inventoryQuantity = inventoryQuantity;
	}
	*/
    

	
	
}
