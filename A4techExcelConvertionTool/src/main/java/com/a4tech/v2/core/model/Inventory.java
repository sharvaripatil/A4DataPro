package com.a4tech.v2.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Inventory {
	
	
	@JsonProperty("InventoryLink")
    private String                inventoryLink="";
    
	

	@JsonProperty("InventoryStatus")
    private String                inventoryStatus="";
	
	@JsonProperty("InventoryQuantity")
    private String                inventoryQuantity="";

	public String getInventoryLink() {
		return inventoryLink;
	}

	public void setInventoryLink(String inventoryLink) {
		this.inventoryLink = inventoryLink;
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
	
	
	
}

