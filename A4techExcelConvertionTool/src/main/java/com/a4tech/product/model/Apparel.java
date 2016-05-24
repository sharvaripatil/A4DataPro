package com.a4tech.product.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Apparel {
    @JsonProperty("Type")
    private String      type;
    @JsonProperty("Values")
    private List<Value> values;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Value> getValues() {
        return values;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }

}
