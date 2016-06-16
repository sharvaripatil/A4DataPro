package com.a4tech.product.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class ImprintMethod {

    @JsonProperty("Type")
    private String        type;
    @JsonProperty("Alias")
    private String        alias;
    @JsonProperty("CustomerOrderCode")
    private String customerOrderCode;


    @JsonProperty("Type")
    public String getType() {
        return type;
    }

    @JsonProperty("Type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("Alias")
    public String getAlias() {
        return alias;
    }

    @JsonProperty("Alias")
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @JsonProperty("CustomerOrderCode")
    public String getCustomerOrderCode() {
		return customerOrderCode;
	}

    @JsonProperty("CustomerOrderCode")
	public void setCustomerOrderCode(String customerOrderCode) {
		this.customerOrderCode = customerOrderCode;
	} 
    public int hashCode(){
        int hashcode = 0;
        hashcode = type.hashCode();
        hashcode = hashcode + alias.hashCode();
        return hashcode;
    }
     
    public boolean equals(Object obj){
        if (obj instanceof ImprintMethod) {
        	ImprintMethod artwork = (ImprintMethod) obj;
            //return (pp.item.equals(this.item));
        	return (artwork.getType().equalsIgnoreCase(this.type) && 
        			                             artwork.getAlias().equalsIgnoreCase(this.alias));
        } else {
            return false;
        }
    }
  

}
