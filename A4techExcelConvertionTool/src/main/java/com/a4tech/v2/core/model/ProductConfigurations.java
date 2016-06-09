package com.a4tech.v2.core.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(Include.NON_NULL)
public class ProductConfigurations {

    @JsonProperty("ImprintColors")
    private ImprintColor              imprintColors        = null;
    @JsonProperty("Samples")
    private Samples                   samples;
    @JsonProperty("Colors")
    private List<Color>               colors               = null;
    @JsonProperty("Materials")
    private List<Material>            materials            = null;
    @JsonProperty("Sizes")
    private Size                	  sizes                = null;
    
    // STORY: VELO-8350
    // Author: ZAhmed, Date: 11/04/2015, Fix Version 1.3.10
    // Changes: Need an additional field for API to expose the RADAR Field for different criteria: CustomValueCode
    // In case of Shapes, its just a List of strings so need to create a new object for Shape and convert the 
    // List string to List of Shapes.
    @JsonProperty("Shapes")
    private List<Shape>              shapes               = null;
    
    @JsonProperty("Themes")
    private List<String>			  themes				= null;
    @JsonProperty("Options")
    private List<Option>              options              = null;
    @JsonProperty("Origins")
    private List<String>              origins              = null;
    @JsonProperty("Packaging")
    private List<String>              packaging            = null;
    @JsonProperty("TradeNames")
    private List<String>              tradeNames           = null;
    @JsonProperty("ImprintMethods")
    private List<ImprintMethod>       imprintMethods       = null;
    @JsonProperty("Artwork")
    private List<Artwork> artwork = null;
    @JsonProperty("Personalization")
    private List<Personalization>    personalization	   = null;

    public List<Artwork> getArtwork() {
		return artwork;
	}

	public void setArtwork(List<Artwork> artwork) {
		this.artwork = artwork;
	}

	@JsonProperty("ProductionTime")
    private List<ProductionTime>      productionTime       = null;
    @JsonProperty("SameDayRush")
    private SameDayRush sameDayRush=null;
    
    public SameDayRush getSameDayRush() {
		return sameDayRush;
	}

	public void setSameDayRush(SameDayRush sameDayRush) {
		this.sameDayRush = sameDayRush;
	}

	@JsonProperty("RushTime")
    private RushTime            rushTime             = null;
    @JsonProperty("AdditionalColors")
    private List<String>              additionalColors     = null;
    @JsonProperty("AdditionalLocations")
    private List<String>              additionalLocations  = null;
    @JsonProperty("ImprintSizeLocations")
    private List<ImprintSizeLocation> imprintSizeLocations = null;
    @JsonProperty("ShippingEstimates")
    private ShippingEstimate    shippingEstimates    = null;

    @JsonProperty("Colors")
    @XmlElementWrapper(name = "Colors")
    @XmlElement(name = "Color")
    public List<Color> getColors() {
        return colors;
    }

    @JsonProperty("Colors")
    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    @JsonProperty("Materials")
    @XmlElementWrapper(name = "Materials")
    @XmlElement(name = "Material")
    public List<Material> getMaterials() {
        return materials;
    }

    @JsonProperty("Materials")
    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    @JsonProperty("Sizes")
    public Size getSizes() {
        return sizes;
    }

    @JsonProperty("Sizes")
    public void setSizes(Size sizes) {
        this.sizes = sizes;
    }

    @JsonProperty("Shapes")
    @XmlElementWrapper(name = "Shapes")
    @XmlElement(name = "Shape")
    public List<Shape> getShapes() {
        return shapes;
    }

    @JsonProperty("Shapes")
    public void setShapes(List<Shape> shapes) {
        this.shapes = shapes;
    }
    
    @XmlElementWrapper(name = "Themes")
    @XmlElement(name = "Theme")
    public List<String> getThemes() {
		return themes;
	}

	public void setThemes(List<String> themes) {
		this.themes = themes;
	}

	@JsonProperty("Options")
    @XmlElementWrapper(name = "Options")
    @XmlElement(name = "Option")
    public List<Option> getOptions() {
        return options;
    }

    @JsonProperty("Options")
    public void setOptions(List<Option> options) {
        this.options = options;
    }

    @JsonProperty("Origins")
    @XmlElementWrapper(name = "Origins")
    @XmlElement(name = "Origin")
    public List<String> getOrigins() {
        return origins;
    }

    @JsonProperty("Origins")
    public void setOrigins(List<String> origins) {
        this.origins = origins;
    }

    @JsonProperty("Packaging")
    @XmlElementWrapper(name = "Packagings")
    @XmlElement(name = "Packaging")
    public List<String> getPackaging() {
        return packaging;
    }

    @JsonProperty("Packaging")
    public void setPackaging(List<String> packaging) {
        this.packaging = packaging;
    }

    @JsonProperty("TradeNames")
    @XmlElementWrapper(name = "TradeNames")
    @XmlElement(name = "TradeName")
    public List<String> getTradeNames() {
        return tradeNames;
    }

    @JsonProperty("TradeNames")
    public void setTradeNames(List<String> tradeNames) {
        this.tradeNames = tradeNames;
    }

    @JsonProperty("ImprintMethods")
    @XmlElementWrapper(name = "ImprintMethods")
    @XmlElement(name = "ImprintMethod")
    public List<ImprintMethod> getImprintMethods() {
        return imprintMethods;
    }

    @JsonProperty("ImprintMethods")
    public void setImprintMethods(List<ImprintMethod> imprintMethods) {
        this.imprintMethods = imprintMethods;
    }

    @JsonProperty("ProductionTime")
    @XmlElementWrapper(name = "ProductionTimes")
    @XmlElement(name = "ProductionTime")
    public List<ProductionTime> getProductionTime() {
        return productionTime;
    }

    @JsonProperty("ProductionTime")
    public void setProductionTime(List<ProductionTime> productionTime) {
        this.productionTime = productionTime;
    }

    @JsonProperty("RushTime")
    public RushTime getRushTime() {
        return rushTime;
    }

    @JsonProperty("RushTime")
    public void setRushTime(RushTime rushTime) {
        this.rushTime = rushTime;
    }

    @JsonProperty("AdditionalColors")
    @XmlElementWrapper(name = "AdditionalColors")
    @XmlElement(name = "AdditionalColor")
    public List<String> getAdditionalColors() {
        return additionalColors;
    }

    @JsonProperty("AdditionalColors")
    public void setAdditionalColors(List<String> additionalColors) {
        this.additionalColors = additionalColors;
    }

    @JsonProperty("AdditionalLocations")
    @XmlElementWrapper(name = "AdditionalLocations")
    @XmlElement(name = "AdditionalLocation")
    public List<String> getAdditionalLocations() {
        return additionalLocations;
    }

    @JsonProperty("AdditionalLocations")
    public void setAdditionalLocations(List<String> additionalLocations) {
        this.additionalLocations = additionalLocations;
    }

    @JsonProperty("ImprintColors")
    public ImprintColor getImprintColors() {
        return imprintColors;
    }

    @JsonProperty("ImprintColors")
    public void setImprintColors(ImprintColor imprintColors) {
        this.imprintColors = imprintColors;
    }

    @JsonProperty("ImprintSizeLocations")
    @XmlElementWrapper(name = "ImprintSizeLocations")
    @XmlElement(name = "ImprintSizeLocation")
    public List<ImprintSizeLocation> getImprintSizeLocations() {
        return imprintSizeLocations;
    }

    @JsonProperty("ImprintSizeLocations")
    public void setImprintSizeLocations(List<ImprintSizeLocation> imprintSizeLocations) {
        this.imprintSizeLocations = imprintSizeLocations;
    }

    /**
     * @return the samples
     */
    @JsonProperty("Samples")
    public Samples getSamples() {
        return samples;
    }

    /**
     * @param samples
     *            the samples to set
     */
    @JsonProperty("Samples")
    public void setSamples(Samples samples) {
        this.samples = samples;
    }

    @JsonProperty("ShippingEstimates")
    public ShippingEstimate getShippingEstimates() {
        return shippingEstimates;
    }

    @JsonProperty("ShippingEstimates")
    public void setShippingEstimates(ShippingEstimate shippingEstimates) {
        this.shippingEstimates = shippingEstimates;
    }

    @JsonProperty("Personalization")
    @XmlElementWrapper(name = "Personalizations")
    @XmlElement(name = "Personalization")
	public List<Personalization> getPersonalization() {
		return personalization;
	}
    @JsonProperty("Personalization")
	public void setPersonalization(List<Personalization> personalization) {
		this.personalization = personalization;
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
