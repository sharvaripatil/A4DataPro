package com.a4tech.product.model;

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
    
    @JsonProperty("Shapes")
    private List<Shape>              shapes               = null;
    
    @JsonProperty("Themes")
    private List<String>			  themes				= null;
    @JsonProperty("Options")
    private List<Option>              options              = null;
    @JsonProperty("Origins")
    private List<Origin>              origins              = null;
    @JsonProperty("Packaging")
    private List<String>              packaging            = null;
    @JsonProperty("TradeNames")
    private List<TradeName>              tradeNames           = null;
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
    private List<AdditionalLocation>              additionalLocations  = null;
    @JsonProperty("ImprintSize")
    private List<ImprintSize> imprintSize = null;
    
    @JsonProperty("ItemWeight")
    private Volume itemWeight = null;

    @JsonProperty("ImprintLocation")
    private List<ImprintLocation> imprintLocation = null;

    @JsonProperty("Carrier")
    private List<CarrierInformation> carrier = null;
    
    @JsonProperty("Warranty")
    private List<WarrantyInformation> warranty = null; 

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
    public List<Origin> getOrigins() {
        return origins;
    }

    @JsonProperty("Origins")
    public void setOrigins(List<Origin> origins) {
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
    public List<TradeName> getTradeNames() {
        return tradeNames;
    }

    @JsonProperty("TradeNames")
    public void setTradeNames(List<TradeName> tradeNames) {
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
    public List<AdditionalLocation> getAdditionalLocations() {
        return additionalLocations;
    }

    @JsonProperty("AdditionalLocations")
    public void setAdditionalLocations(List<AdditionalLocation> additionalLocations) {
        this.additionalLocations = additionalLocations;
    }

    public List<ImprintSize> getImprintSize() {
        return imprintSize;
    }

    public void setImprintSize(List<ImprintSize> imprintSize) {
        this.imprintSize = imprintSize;
    }

    public List<ImprintLocation> getImprintLocation() {
        return imprintLocation;
    }

    public void setImprintLocation(List<ImprintLocation> imprintLocation) {
        this.imprintLocation = imprintLocation;
    }

    public List<CarrierInformation> getCarrier() {
        return carrier;
    }

    public void setCarrier(List<CarrierInformation> carrier) {
        this.carrier = carrier;
    }

    public List<WarrantyInformation> getWarranty() {
        return warranty;
    }

    public void setWarranty(List<WarrantyInformation> warranty) {
        this.warranty = warranty;
    }

    @JsonProperty("ImprintColors")
    public ImprintColor getImprintColors() {
        return imprintColors;
    }

    @JsonProperty("ImprintColors")
    public void setImprintColors(ImprintColor imprintColors) {
        this.imprintColors = imprintColors;
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
    
    @XmlElement(name = "ItemWeight")
    public Volume getItemWeight() {
        return itemWeight;
    }
    public void setItemWeight(Volume itemWeight) {
        this.itemWeight = itemWeight;
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
