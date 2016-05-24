package com.a4tech.product.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Size {

    @JsonProperty("Apparel")
    private Apparel   apparel;
    @JsonProperty("Capacity")
    private Capacity  capacity;
    @JsonProperty("Dimension")
    private Dimension dimension;
    @JsonProperty("Volume")
    private Volume    volume;
    @JsonProperty("Other")
    private OtherSize other;

    public Apparel getApparel() {
        return apparel;
    }

    public void setApparel(Apparel apparel) {
        this.apparel = apparel;
    }

    public Capacity getCapacity() {
        return capacity;
    }

    public void setCapacity(Capacity capacity) {
        this.capacity = capacity;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public Volume getVolume() {
        return volume;
    }

    public void setVolume(Volume volume) {
        this.volume = volume;
    }

    public OtherSize getOther() {
        return other;
    }

    public void setOther(OtherSize other) {
        this.other = other;
    }

}
