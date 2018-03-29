package parser.evansManufacturing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.AdditionalLocation;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class EveanManufactureAttributeParser {
	private EveansManufacturePriceGridParser eveanPriceGridParser;
	
	public Product keepExistingProductData(Product existingProduct){
		  Product newProduct = new Product();
		  ProductConfigurations oldConfig = existingProduct.getProductConfigurations();
		  ProductConfigurations newConfig = new ProductConfigurations();
		  if(!CollectionUtils.isEmpty(existingProduct.getCategories())){
			  newProduct.setCategories(existingProduct.getCategories());
		  }
		  if(!StringUtils.isEmpty(existingProduct.getSummary())){
			  newProduct.setSummary(existingProduct.getSummary());
		  }
		  if(!CollectionUtils.isEmpty(existingProduct.getImages())){
			  newProduct.setImages(existingProduct.getImages());
		  }
		  if(!CollectionUtils.isEmpty(existingProduct.getComplianceCerts())){
			  newProduct.setComplianceCerts(existingProduct.getComplianceCerts());
		  }
		  if(!CollectionUtils.isEmpty(oldConfig.getColors())){
			  newConfig.setColors(oldConfig.getColors());
		  }
		  if(!CollectionUtils.isEmpty(existingProduct.getProductKeywords())){
			  newProduct.setProductKeywords(existingProduct.getProductKeywords());
		  }
		  if(!CollectionUtils.isEmpty(existingProduct.getFobPoints())){
			  newProduct.setFobPoints(existingProduct.getFobPoints());
		  }
		  newProduct.setProductConfigurations(newConfig);
		  return newProduct;
	  }
	public List<String> getProductKeywords(String key) {
		List<String> keyList = new ArrayList<>();
		String[] keys = CommonUtility.getValuesOfArray(key, "\\|");
		for (String keyName : keys) {
			keyName = keyName.replaceAll("™", "");
			keyList.add(keyName);
		}
		return keyList;
	}
	public List<ProductionTime> getProductionTime(String prdVal){
		List<ProductionTime> productionTimeList = new ArrayList<>();
		ProductionTime prdTimeObj = new ProductionTime();
		String prdTime = "";
		if(prdVal.contains("week")){
			prdTime = CommonUtility.convertProductionTimeWeekIntoDays(prdVal);
		} else {
			prdTime = prdVal.replaceAll("[^0-9- ]", "").trim();
		}
		prdTimeObj.setBusinessDays(prdTime);
		prdTimeObj.setDetails(prdVal);
		productionTimeList.add(prdTimeObj);
		return productionTimeList;
	}
	public ShippingEstimate getShippingEstimation(String shippingWtvalue,String shippingDimension){
		String[] shippingValues = shippingWtvalue.split(",");
		ShippingEstimate shippingEstimation = new ShippingEstimate();
		Dimensions dimensions = null;
		List<Weight> listOfWeight = new ArrayList<>();
		List<NumberOfItems> listOfNumberOfItems = null;
		for (String shippingVal : shippingValues) {
			   if(shippingVal.contains("shippingWt")){
				  shippingVal = shippingVal.split(":")[1];
				  listOfWeight = getShippingWeight(shippingVal.trim());
			  } else if(shippingVal.contains("shippingQty")){
				  shippingVal = shippingVal.split(":")[1];
				  listOfNumberOfItems = getShippingNumberOfItems(shippingVal.trim());
			  }
		}
		if(!StringUtils.isEmpty(shippingDimension)){
			dimensions = getShippingDimensions(shippingDimension);
	 		shippingEstimation.setDimensions(dimensions);	
		}
		shippingEstimation.setNumberOfItems(listOfNumberOfItems);
		shippingEstimation.setWeight(listOfWeight);
		return shippingEstimation;
	}
	 private List<Weight> getShippingWeight(String val){
		  List<Weight> listOfShippingWt = new ArrayList<>();
		  Weight weightObj = new Weight();
		  weightObj.setValue(val);
		  weightObj.setUnit("lbs");
		  listOfShippingWt.add(weightObj);
		  return listOfShippingWt;
	  }
	private Dimensions getShippingDimensions(String val){
		  String[] vals = val.split("x");;
		  String length = vals[0];
		  String width = vals[1];
		  String height =vals[2];
		  Dimensions dimensionsObj = new Dimensions();
		  if(!StringUtils.isEmpty(length)){
			  dimensionsObj.setLength(length.trim());
			  dimensionsObj.setLengthUnit("in");
		  }
		  if(!StringUtils.isEmpty(width)){
			  dimensionsObj.setWidth(width.trim());
			  dimensionsObj.setWidthUnit("in");
		  }
		  if(!StringUtils.isEmpty(height)){
			  dimensionsObj.setHeight(height.trim());
			  dimensionsObj.setHeightUnit("in");
		  }
		  return dimensionsObj;
	  }
	  public Volume getItemWeightvolume(String itemWeightValue){
			List<Value> listOfValue = null;
			List<Values> listOfValues = null;
			Volume volume  = new Volume();
			Values values = new Values();
			Value valueObj = new Value();
			if(!itemWeightValue.equals(ApplicationConstants.CONST_STRING_ZERO)){
				listOfValue = new ArrayList<>();
				listOfValues = new ArrayList<>();
				valueObj.setValue(itemWeightValue);
				valueObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_WEIGHT);
				listOfValue.add(valueObj);
				values.setValue(listOfValue);
				listOfValues.add(values);
				volume.setValues(listOfValues);
			}
			return volume;
		}
	  private List<NumberOfItems> getShippingNumberOfItems(String val){
		  List<NumberOfItems> listOfNumberOfItems = new ArrayList<>();
		  NumberOfItems numberOfItemsObj = new NumberOfItems();
		  numberOfItemsObj.setValue(val);
		  numberOfItemsObj.setUnit("per Carton");
		  listOfNumberOfItems.add(numberOfItemsObj);
		  return listOfNumberOfItems;
	  }
	  
	  public Product getRunUpcharge(Product product,String runChargeVal){
		  if(isSpecialWordsRuncharge(runChargeVal)){
			  return product;
		  }
		  ProductConfigurations configuration = product.getProductConfigurations();
		  String priceVal = "";
		  List<AdditionalLocation> listOfAdditionalLocation = null;
		  List<AdditionalColor> listOfAdditionalColor = null;
		  List<ImprintLocation> listOfImprintLocation = null;
		  List<ImprintMethod>   listOfImprintMethod = null;
		  List<PriceGrid> prieGridList = new ArrayList<>();
		  priceVal = runChargeVal.replaceAll("[^0-9.]", "");
		  String priceInclude = runChargeVal.split("\\)")[1].trim();
		  if(runChargeVal.contains("additional color/position")){//additional color & additional location
			  listOfAdditionalColor = getAdditinalColor("Additional Color",listOfAdditionalColor);
			  listOfAdditionalLocation = getAdditinalLocation("Additional Position",listOfAdditionalLocation);
			prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Colors", false, "USD",
					"Additional Color", "Add. Color Charge", "Other", 1, prieGridList, "", "per unit, per additional color");
			prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Location", false, "USD",
					"Additional Position", "Add. Location Charge", "Other", 1, prieGridList, "", "per unit, per additional position");
		  } else if(runChargeVal.contains("second color/position")){//additional color & additional location
			  listOfAdditionalColor = getAdditinalColor("Second color",listOfAdditionalColor);
			  listOfAdditionalLocation = getAdditinalLocation("Additional Position",listOfAdditionalLocation);
			prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Colors", false, "USD",
					"Second color", "Add. Color Charge", "Other", 1, prieGridList, "", "per unit, per additional color");
			prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Location", false, "USD",
					"Additional Position", "Add. Location Charge", "Other", 1, prieGridList, "", "per unit, per additional position");
		  } else if(runChargeVal.contains("second laser engraved position")){// imprint method & additional location
			  listOfImprintMethod = getImprintMethod("Laser Engraved");
			  listOfAdditionalLocation = getAdditinalLocation("Second Position",listOfAdditionalLocation);
			  priceVal = runChargeVal.replaceAll("[^0-9.]", "");
			prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V",
					"Imprint Method:Laser Engraved,Additional Location:Second Position", false, "USD",
					"Laser Engraved,Second Position", "Add. Location Charge", "Per Quantity", 1, prieGridList, "", "");
		  } else if(runChargeVal.contains("laser imprint")){//imprint method
			  listOfImprintMethod = getImprintMethod("laser imprint");
			  prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Imprint Method", false, "USD",
						"laser imprint", "Run Charge", "Per Quantity", 1, prieGridList, "", "per unit, per additional color");
		  } else if(runChargeVal.contains("additional color")){ // additional color
			  listOfAdditionalColor = getAdditinalColor("Additional Color",listOfAdditionalColor);
				prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Colors", false, "USD",
						"Additional Color", "Add. Color Charge", "Other", 1, prieGridList, "", "per unit, per additional color");
		  } else if(runChargeVal.contains("second position")){// additional location
			  listOfAdditionalLocation = getAdditinalLocation("Second Position",listOfAdditionalLocation);
			  prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Location", false, "USD",
						"Additional Position", "Add. Location Charge", "Other", 1, prieGridList, "", "per unit, per additional position");
		} else if(runChargeVal.contains("metallic thread color")){
			 listOfAdditionalColor = getAdditinalColor("Metallic thread color",listOfAdditionalColor);
			prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Colors", false, "USD",
					"Metallic thread color", "Add. Color Charge", "Other", 1, prieGridList, "", priceInclude);
		} else if (runChargeVal.contains("additional position")
				|| runChargeVal.contains("additional lasered position")) {// additional
																			// location
			 listOfAdditionalLocation = getAdditinalLocation("additional position",listOfAdditionalLocation);
			  prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Location", false, "USD",
						"Additional Position", "Add. Location Charge", "Other", 1, prieGridList, "", "per unit, per additional position");
		  } else if(runChargeVal.contains("second color")){ // additional color
			  listOfAdditionalColor    = getAdditinalColor("Second color",listOfAdditionalColor);
			prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Colors", false, "USD",
					"Second color", "Add. Color Charge", "Other", 1, prieGridList, "", "");
		  } else if(runChargeVal.contains("position")){
			  listOfAdditionalLocation = getAdditinalLocation("Position",listOfAdditionalLocation);
				prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Location", false, "USD",
						"Position", "Add. Location Charge", "Other", 1, prieGridList, "", "per unit, per additional position");
		  } else if(runChargeVal.contains("laser engraving")){//imprint method
			  listOfImprintMethod = getImprintMethod("laser engraving");
			  prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Imprint Method", false, "USD",
						"laser engraving", "Imprint Method Charge", "Other", 1, prieGridList, "", priceInclude);
		  } else if(runChargeVal.contains("second imprint")){// additional location
			  listOfAdditionalLocation = getAdditinalLocation("second imprint",listOfAdditionalLocation);
			  prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Location", false, "USD",
						"second imprint", "Add. Location Charge", "Other", 1, prieGridList, "", priceInclude);
		} else if(runChargeVal.contains("laser imprint on clip")){
			 listOfImprintMethod = getImprintMethod("laser imprint");
			  prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Imprint Method", false, "USD",
						"laser imprint", "Imprint Method Charge", "Other", 1, prieGridList, "", priceInclude);
		} else if (runChargeVal.contains("imprint on pen") || runChargeVal.contains("imprint on ball marker")
				|| runChargeVal.contains("imprint on eraser") || runChargeVal.contains("imprint on cup")
				|| runChargeVal.contains("imprint on barrel") || runChargeVal.contains("imprint on divot repair")
				|| runChargeVal.contains("second imprint on snack bag")) { // imprint location
			String runChareValue = runChargeVal.split("imprint")[1].trim();
			listOfImprintLocation = getImprintLocation(runChareValue,listOfImprintLocation);
			 prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Imprint Location", false, "USD",
					 runChareValue, "Imprint Location Charge", "Other", 1, prieGridList, "", priceInclude);
		  } 
		  configuration.setAdditionalColors(listOfAdditionalColor);
		  configuration.setAdditionalLocations(listOfAdditionalLocation);
		  configuration.setImprintMethods(listOfImprintMethod);
		  configuration.setImprintLocation(listOfImprintLocation);
		  product.setPriceGrids(prieGridList);
		  product.setProductConfigurations(configuration);
		  return product;
	  }
	  public Product getSetUpcharge(Product product,String setupChargeVal, String columnName){
		  ProductConfigurations configuration = product.getProductConfigurations();
          List<AdditionalColor> listOfAdditionalColor = configuration.getAdditionalColors();
          List<AdditionalLocation> listOfAdditionalLocation = configuration.getAdditionalLocations();
          List<ImprintLocation>  listOfImprintLocation = configuration.getImprintLocation();
          List<PriceGrid> prieGridList = product.getPriceGrids();
		  if(isSpecialWordsSetupcharge(setupChargeVal)){
			  return product;
		  }
		  List<PriceGrid> priceGrid = product.getPriceGrids();
		  if(CollectionUtils.isEmpty(priceGrid)){
			  priceGrid = new ArrayList<>();
		  }
		  String[] setupChargeVals = null;
		  if(setupChargeVal.contains(";")){
			   setupChargeVals = CommonUtility.getValuesOfArray(setupChargeVal, ";");  
		  } else if(setupChargeVal.contains(".")){
			  setupChargeVals = CommonUtility.getValuesOfArray(setupChargeVal, "\\.");
		  } else {
			  if(setupChargeVal.contains("imprint")){
				  int pricesCount = setupChargeVal.replaceAll("[^$]", "").trim().length();
					 if(pricesCount > 1){//$55(V) for imprint on pouch, pencils & ruler $55(V) for imprint on eraser or second imprint on pouch
						// this is used for there is delimiter for splitting above values 
						 setupChargeVals = CommonUtility.getValuesOfArray(setupChargeVal, "\\$");
					 }  
			  } else {
				  setupChargeVals = new String[]{setupChargeVal};  
			  }
		  }
		  for (String upchargeVal : setupChargeVals) {
			  if(StringUtils.isEmpty(upchargeVal)){
				  continue;
			  }
			  String priceVal = upchargeVal.replaceAll("[^0-9.]", "").trim();
			 String upchargeType= "";
			  String upchargeName = "";String criteriaType = "";
			  String priceInclude = upchargeVal.split("\\)")[1].trim();
			 if(upchargeVal.contains("imprint")){
				 if(columnName.equals("setupCharge")){
					 upchargeType = "Set-up Charge"; 
				 } else {
					 upchargeType = "Re-order Charge";
				 }
				  criteriaType = "Imprint Location";
				  upchargeName = upchargeVal.split("imprint")[1].trim();
			 } else if(upchargeVal.contains("color/position")){
				 if(columnName.equals("setupCharge")){
					 upchargeType = "Add. Color Charge";  
				 } else {
					 upchargeType = "Re-order Charge";
				 }
				 criteriaType = "Additional Colors:Additional Color,Additional Location:Additional Position";
				 upchargeName = "";
			 } else if(upchargeVal.contains(" per position")){
				 if(columnName.equals("setupCharge")){
					 upchargeType = "Imprint Location Charge";   
				 } else {
					 upchargeType = "Re-order Charge";
				 }
				  listOfAdditionalLocation = getAdditinalLocation("Additional Position",listOfAdditionalLocation);
				  criteriaType = "Imprint Location";
				  upchargeName = "Additional Position";
						 
			 } else if(upchargeVal.contains("per color") || upchargeVal.contains("additional color")){
				 if(columnName.equals("setupCharge")){
					 upchargeType = "Add. Color Charge";   
				 } else {
					 upchargeType = "Re-order Charge";
				 }
				 listOfAdditionalColor = getAdditinalColor("Additional Color",listOfAdditionalColor);
				  criteriaType = "Additional Colors";
				  upchargeName = "Additional Color";
			 } else if(upchargeVal.contains("second color")){
				 if(columnName.equals("setupCharge")){
					 upchargeType = "Add. Color Charge";   
				 } else {
					 upchargeType = "Re-order Charge";
				 }
				 listOfAdditionalColor    = getAdditinalColor("Second color",listOfAdditionalColor);
				  criteriaType = "Additional Colors";
				  upchargeName = "Second color";
			 }
			 prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", criteriaType, false, "USD",
					 upchargeName, upchargeType, "Other", 1, prieGridList, "", priceInclude);
		}
		  configuration.setAdditionalColors(listOfAdditionalColor);
		  configuration.setAdditionalLocations(listOfAdditionalLocation);
		  configuration.setImprintLocation(listOfImprintLocation);
		  product.setPriceGrids(prieGridList);
		  return product;
	  }
	 public List<AdditionalLocation> getAdditinalLocation(String addLocVal,List<AdditionalLocation> additionalLocationList){
		 AdditionalLocation additionalLocationObj = null;
		 String allAdditionalLocation = additionalLocationList.stream().map(addLocation -> addLocation.getName())
					.collect(Collectors.joining(","));
		 if(!allAdditionalLocation.contains(addLocVal)){
			 additionalLocationObj = new AdditionalLocation();
			 additionalLocationObj.setName(addLocVal);
			 additionalLocationList.add(additionalLocationObj);
		 }
	     return additionalLocationList;
	 }
	 public List<AdditionalColor> getAdditinalColor(String addColorVal,List<AdditionalColor> additionalColorList){
		 List<AdditionalColor> listOfAdditionalColor = new ArrayList<>();
		 AdditionalColor additionalColorObj = null;
		 String allAdditionalColor = additionalColorList.stream().map(addColor -> addColor.getName())
					.collect(Collectors.joining(","));
		 if(!allAdditionalColor.contains(addColorVal)){
			 additionalColorObj = new AdditionalColor();
			 additionalColorObj.setName(addColorVal);
			 listOfAdditionalColor.add(additionalColorObj);
		 }
	     return listOfAdditionalColor;
	 }
	 public List<ImprintLocation> getImprintLocation(String imprLocVal,List<ImprintLocation> imprintLocationList){
		 ImprintLocation imprintLocationObj = new ImprintLocation();
		String allImrintLocations = imprintLocationList.stream().map(location -> location.getValue())
				.collect(Collectors.joining(","));
		 if(imprLocVal.contains("&")){
			 imprLocVal = imprLocVal.replaceAll("&", ",");
		 } else if(imprLocVal.contains("and")){
			 imprLocVal = imprLocVal.replaceAll("and", ",");
		 }
		 String[] imprLocs = CommonUtility.getValuesOfArray(imprLocVal, ",");
		 for (String imprLocName : imprLocs) {
			 imprLocName = imprLocName.trim();
			 if(allImrintLocations.contains(imprLocName)){
				 continue;
			 }
			 imprintLocationObj = new ImprintLocation();
			 imprintLocationObj.setValue(imprLocName);
			 imprintLocationList.add(imprintLocationObj);
		}
	     return imprintLocationList; 
	 }
	 public List<ImprintMethod> getImprintMethod(String imprMethodVal){
		 List<ImprintMethod> imprintMethodList = new ArrayList<>();
		 ImprintMethod imprintMethodObj = new ImprintMethod();
		 String imprintMethodType = null;
		 if(imprMethodVal.equals("Laser Engraved") || imprMethodVal.equals("laser imprint")){
			 imprintMethodType = "Laser Engraved";
		 }
		 imprintMethodObj.setType(imprintMethodType);
		 imprintMethodObj.setAlias(imprMethodVal);
		 imprintMethodList.add(imprintMethodObj);
		 return imprintMethodList;
	 }
  private boolean isSpecialWordsRuncharge(String val){
		if (val.equalsIgnoreCase("For two or more colors see vibrant color option below")
				|| val.equalsIgnoreCase("For two or more colors, see vibrant color option below") ||
			    val.equalsIgnoreCase("For two or more colors, see vibrant color option") ||
			    val.equalsIgnoreCase("For three or more colors, see vibrant color option below")
			    || val.equalsIgnoreCase("For two or more colors, see vibrant color option below")
			    || val.equalsIgnoreCase("For three or more colors, see vibrant color option")) {
             return true;
		}
		return false;
  }
  private boolean isSpecialWordsSetupcharge(String val){
		if (val.equalsIgnoreCase("FREE")
				|| val.contains("FREE for first color")
				|| val.equals("FREE for imprint on pen.")){
           return true;
		}
		return false;
}
	 public EveansManufacturePriceGridParser getEveanPriceGridParser() {
			return eveanPriceGridParser;
		}
		public void setEveanPriceGridParser(EveansManufacturePriceGridParser eveanPriceGridParser) {
			this.eveanPriceGridParser = eveanPriceGridParser;
		}
}
