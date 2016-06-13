package com.a4tech.v2.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(Include.NON_NULL)
public class Artwork {

    @JsonProperty("Value")
    private String value;
    @JsonProperty("Comments")
    private String comments;

    @JsonProperty("Value")
    public String getValue() {
        return value;
    }

    @JsonProperty("Value")
    public void setValue(String value) {
        this.value = value;
    }

    @JsonProperty("Comments")
    public String getComments() {
        return comments;
    }

    @JsonProperty("Comments")
    public void setComments(String comments) {
        this.comments = comments;
    }
    
    public int hashCode(){
        int hashcode = 0;
        hashcode = value.hashCode();
        hashcode = hashcode + comments.hashCode();
        return hashcode;
    }
     
    public boolean equals(Object obj){
        if (obj instanceof Artwork) {
        	Artwork artwork = (Artwork) obj;
            //return (pp.item.equals(this.item));
        	return (artwork.getComments().equalsIgnoreCase(this.comments) && 
        			                             artwork.getValue().equalsIgnoreCase(this.value));
        } else {
            return false;
        }
    }
  
}
