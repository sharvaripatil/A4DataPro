package com.a4tech.product.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(Include.NON_NULL)
public class Product {

    @JsonProperty("ExternalProductId")
    private String                externalProductId;
    @JsonProperty("Name")
    private String                name;
    @JsonProperty("Description")
    private String                description;
    @JsonProperty("Summary")
    private String                summary=null;
    @JsonProperty("AsiProdNo")
    private String                asiProdNo;
    @JsonProperty("SKU")
    private String productLevelSku;
    @JsonProperty("Inventory")
    private Inventory inventory=null;
    @JsonProperty("ProductDataSheet")
    private String                productDataSheet;
    @JsonProperty("ShipperBillsBy")
    private String                shipperBillsBy;
    @JsonProperty("ProductBreakoutBy")
    private ProductBreakoutBy productBreakoutBy;
    
    public void setProductBreakoutBy(ProductBreakoutBy productBreakoutBy) {
        this.productBreakoutBy = productBreakoutBy;
    }

    @JsonProperty("BreakoutByPrice")
    private boolean               breakOutByPrice;
    @JsonProperty("CanShipInPlainBox")
    private boolean               canShipInPlainBox;

    // Fix for VELOEXTAPI-472
    @JsonProperty("SEOFlag")
    @XmlElement(name = "SEOFlag")
    private boolean seoFlag;
    
    @JsonProperty("LineNames")
    @XmlElementWrapper(name = "LineNames")
    @XmlElement(name = "LineName")
    private List<String>          lineNames               = null;
    @JsonProperty("Catalogs")
    @XmlElementWrapper(name = "Catalogs")
    @XmlElement(name = "Catalog")
    private List<Catalog>         catalogs                = null;
    @JsonProperty("DistributorOnlyComments")
    private String                distributorOnlyComments = null;
    @JsonProperty("ProductDisclaimer")
    private String                productDisclaimer       = null;
    @JsonProperty("AdditionalProductInfo")
    private String                additionalProductInfo   = null;
    @JsonProperty("AdditionalShippingInfo")
    private String                additionalShippingInfo  = null;
    @JsonProperty("PriceConfirmedThru")
    private String                priceConfirmedThru;
    @JsonProperty("CanOrderLessThanMinimum")
    private boolean               canOrderLessThanMinimum = false;
    @JsonProperty("Availability")
    private List<Availability>    availability;
    @JsonProperty("FOBPoints")
    @XmlElementWrapper(name = "FOBPoints")
    @XmlElement(name = "FOBPoint")
    private List<String>          fobPoints               = null;
    @JsonProperty("ProductKeywords")
    @XmlElementWrapper(name = "ProductKeywords")
    @XmlElement(name = "ProductKeyword")
    private List<String>          productKeywords         = null;
    @JsonProperty("Categories")
    @XmlElementWrapper(name = "Categories")
    @XmlElement(name = "Category")
    private List<String>          categories              = null;
    @JsonProperty("ComplianceCerts")
    @XmlElementWrapper(name = "ComplianceCerts")
    @XmlElement(name = "ComplianceCert")
    private List<String>          complianceCerts         = null;
    @JsonProperty("SafetyWarnings")
    @XmlElementWrapper(name = "SafetyWarnings")
    @XmlElement(name = "SafetyWarning")
    private List<String>          safetyWarnings          = null;
    @JsonProperty("Images")
    @XmlElementWrapper(name = "Images")
    @XmlElement(name = "Image")
    private List<Image>           images                  = null;
    @JsonProperty("PriceGrids")
    @XmlElementWrapper(name = "PriceGrids")
    @XmlElement(name = "PriceGrid")
    private List<PriceGrid>       priceGrids              = null;
    @JsonProperty("ProductNumbers")
    @XmlElementWrapper(name = "ProductNumbers")
    @XmlElement(name = "ProductNumber")
    private List<ProductNumber>   productNumbers          = null;
    @JsonProperty("ProductSKUs")
    @XmlElement(name = "ProductSkus")
    private List<ProductSkus>   productRelationSkus          = null;
    
    @JsonProperty("PriceType")
    @XmlElement(name="PriceType")
    private String priceType;
     @JsonProperty("DistributorViewOnly")
    @XmlElement(name="DistributorViewOnly")
    private boolean distributorViewOnly;
    public boolean isDistributorViewOnly() {
    return distributorViewOnly;
   }

     public void setDistributorViewOnly(boolean distributorViewOnly) {
    this.distributorViewOnly = distributorViewOnly;
    }
   
    
	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public String getProductLevelSku() {
		return productLevelSku;
	}

	public void setProductLevelSku(String productLevelSku) {
		this.productLevelSku = productLevelSku;
	}

	public List<ProductSkus> getProductRelationSkus() {
		return productRelationSkus;
	}

	public void setProductRelationSkus(List<ProductSkus> productRelationSkus) {
		this.productRelationSkus = productRelationSkus;
	}
    @JsonProperty("ProductConfigurations")
    private ProductConfigurations productConfigurations;

    public String getDistributorOnlyComments() {
        return distributorOnlyComments;
    }

    public void setDistributorOnlyComments(String distributorOnlyComments) {
        this.distributorOnlyComments = distributorOnlyComments;
    }

    public String getProductDisclaimer() {
        return productDisclaimer;
    }

    public void setProductDisclaimer(String productDisclaimer) {
        this.productDisclaimer = productDisclaimer;
    }

    public String getAdditionalProductInfo() {
        return additionalProductInfo;
    }

    public void setAdditionalProductInfo(String additionalProductInfo) {
        this.additionalProductInfo = additionalProductInfo;
    }

    public String getAdditionalShippingInfo() {
        return additionalShippingInfo;
    }

    public void setAdditionalShippingInfo(String additionalShippingInfo) {
        this.additionalShippingInfo = additionalShippingInfo;
    }

    public String getPriceConfirmedThru() {
        return priceConfirmedThru;
    }

    public void setPriceConfirmedThru(String priceConfirmedThru) {
        this.priceConfirmedThru = priceConfirmedThru;
    }

    /**
     * @return the canOrderLessThanMinimum
     */
    public boolean isCanOrderLessThanMinimum() {
        return canOrderLessThanMinimum;
    }

    /**
     * @param canOrderLessThanMinimum
     *            the canOrderLessThanMinimum to set
     */
    public void setCanOrderLessThanMinimum(boolean canOrderLessThanMinimum) {
        this.canOrderLessThanMinimum = canOrderLessThanMinimum;
    }

    /**
     * @return the breakOutByPrice
     */
    public boolean isBreakOutByPrice() {
        return breakOutByPrice;
    }

    /**
     * @param breakOutByPrice
     *            the breakOutByPrice to set
     */
    public void setBreakOutByPrice(boolean breakOutByPrice) {
        this.breakOutByPrice = breakOutByPrice;
    }

    public List<String> getLineNames() {
        return lineNames;
    }

    public void setLineNames(List<String> lineNames) {
        this.lineNames = lineNames;
    }

    public String getExternalProductId() {
        return externalProductId;
    }
    
    public void setExternalProductId(String externalProductId) {
        this.externalProductId = externalProductId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAsiProdNo() {
        return asiProdNo;
    }

    public void setAsiProdNo(String asiProdNo) {
        this.asiProdNo = asiProdNo;
    }

    public String getProductDataSheet() {
        return productDataSheet;
    }

    public void setProductDataSheet(String productDataSheet) {
        this.productDataSheet = productDataSheet;
    }

    public String getShipperBillsBy() {
        return shipperBillsBy;
    }

    public void setShipperBillsBy(String shipperBillsBy) {
        this.shipperBillsBy = shipperBillsBy;
    }

    /**
     * @return the availability
     */
    public List<Availability> getAvailability() {
        return availability;
    }

    /**
     * @param availability
     *            the availability to set
     */
    public void setAvailability(List<Availability> availability) {
        this.availability = availability;
    }

    public List<String> getProductKeywords() {
        return productKeywords;
    }

    public void setProductKeywords(List<String> productKeywords) {
        this.productKeywords = productKeywords;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getComplianceCerts() {
        return complianceCerts;
    }

    public void setComplianceCerts(List<String> complianceCerts) {
        this.complianceCerts = complianceCerts;
    }

    public List<String> getSafetyWarnings() {
        return safetyWarnings;
    }

    public void setSafetyWarnings(List<String> safetyWarnings) {
        this.safetyWarnings = safetyWarnings;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<PriceGrid> getPriceGrids() {
        return priceGrids;
    }

    public void setPriceGrids(List<PriceGrid> priceGrids) {
        this.priceGrids = priceGrids;
    }

    public List<ProductNumber> getProductNumbers() {
        return productNumbers;
    }

    public void setProductNumbers(List<ProductNumber> productNumbers) {
        this.productNumbers = productNumbers;
    }

    public ProductConfigurations getProductConfigurations() {
        return productConfigurations;
    }

    public void setProductConfigurations(ProductConfigurations productConfigurations) {
        this.productConfigurations = productConfigurations;
    }

    /**
     * @return the productBreakoutBy
     */
    public ProductBreakoutBy getProductBreakoutBy() {
        return productBreakoutBy;
    }

    @Override
	public String toString() {
		return "Product [externalProductId=" + externalProductId + ", name="
				+ name + ", description=" + description + ", summary="
				+ summary + ", asiProdNo=" + asiProdNo + ", productLevelSku="
				+ productLevelSku + ", inventory=" + inventory
				+ ", productDataSheet=" + productDataSheet
				+ ", shipperBillsBy=" + shipperBillsBy + ", productBreakoutBy="
				+ productBreakoutBy + ", breakOutByPrice=" + breakOutByPrice
				+ ", canShipInPlainBox=" + canShipInPlainBox + ", seoFlag="
				+ seoFlag + ", lineNames=" + lineNames + ", catalogs="
				+ catalogs + ", distributorOnlyComments="
				+ distributorOnlyComments + ", productDisclaimer="
				+ productDisclaimer + ", additionalProductInfo="
				+ additionalProductInfo + ", additionalShippingInfo="
				+ additionalShippingInfo + ", priceConfirmedThru="
				+ priceConfirmedThru + ", canOrderLessThanMinimum="
				+ canOrderLessThanMinimum + ", availability=" + availability
				+ ", fobPoints=" + fobPoints + ", productKeywords="
				+ productKeywords + ", categories=" + categories
				+ ", complianceCerts=" + complianceCerts + ", safetyWarnings="
				+ safetyWarnings + ", images=" + images + ", priceGrids="
				+ priceGrids + ", productNumbers=" + productNumbers
				+ ", productRelationSkus=" + productRelationSkus
				+ ", priceType=" + priceType + ", productConfigurations="
				+ productConfigurations + "]";
	}

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    public List<String> getFobPoints() {
        return fobPoints;
    }

    public void setFobPoints(List<String> fobPoints) {
        this.fobPoints = fobPoints;
    }

    public List<Catalog> getCatalogs() {
        return catalogs;
    }

    public void setCatalogs(List<Catalog> catalogs) {
        this.catalogs = catalogs;
    }

    /**
     * @return the canShipInPlainBox
     */
    public boolean isCanShipInPlainBox() {
        return canShipInPlainBox;
    }

    /**
     * @param canShipInPlainBox
     *            the canShipInPlainBox to set
     */
    public void setCanShipInPlainBox(boolean canShipInPlainBox) {
        this.canShipInPlainBox = canShipInPlainBox;
    }

	public boolean getSeoFlag() {
		return seoFlag;
	}

	public void setSeoFlag(boolean sEOFlag) {
		seoFlag = sEOFlag;
	}

    /**
     * @return the priceType
     */
    public String getPriceType() {
        return priceType;
    }

    /**
     * @param priceType the priceType to set
     */
    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    
}
