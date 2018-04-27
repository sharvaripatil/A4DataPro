package parser.evansManufacturing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.AdditionalLocation;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Configurations;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.a4tech.util.LookupData;

import parser.headWear.HeadWearColorMapping;

public class EveanManufactureAttributeParser {
	private EveansManufacturePriceGridParser eveanPriceGridParser;
	private LookupServiceData                lookupServiceData;
	private static List<String>              lookupFobPoints = null;
	String currencyType = "CAD";
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
	public List<String> getProductKeywords(String key,String prdName) {
		List<String> keyList = new ArrayList<>();
		String[] keys = CommonUtility.getValuesOfArray(key, "\\|");
		List<String> processedKeyWords = new ArrayList<>();
		for (String keyName : keys) {
			keyName = keyName.trim();
			keyName = keyName.replaceAll("™", "");
			if(!processedKeyWords.contains(keyName.toUpperCase())){
				if(keyName.length()>30){
					if(keyName.contains("Jo Bee")){
						keyName = keyName.replaceAll("Jo Bee", "Jo-Bee");
					}
						if(prdName.equalsIgnoreCase(keyName)){
							continue;
						}
						if(keyName.contains("W/")){
							keyName = keyName.replaceAll("W/", "");
						}
						String [] keyNames = CommonUtility.getValuesOfArray(keyName, " ");
						for (String keyss : keyNames) {
							keyList.add(keyss);
						}
					
				} else {
					keyList.add(keyName);	
				}
				processedKeyWords.add(keyName.toUpperCase());
			}
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
		  
          List<AdditionalColor> listOfAdditionalColor = configuration.getAdditionalColors();
          List<AdditionalLocation> listOfAdditionalLocation = configuration.getAdditionalLocations();
          List<ImprintLocation>  listOfImprintLocation = configuration.getImprintLocation();
		  List<PriceGrid> prieGridList = product.getPriceGrids();
		  List<ImprintMethod>   listOfImprintMethod = configuration.getImprintMethods();
		  if(CollectionUtils.isEmpty(prieGridList)){
			  prieGridList = new ArrayList<>();
		  }
		  if(CollectionUtils.isEmpty(listOfAdditionalColor)){
			  listOfAdditionalColor = new ArrayList<>();
		  }
          if(CollectionUtils.isEmpty(listOfAdditionalLocation)){
        	  listOfAdditionalLocation = new ArrayList<>();
		  }
          if(CollectionUtils.isEmpty(listOfImprintLocation)){
        	  listOfImprintLocation = new ArrayList<>();
		  }
          if(CollectionUtils.isEmpty(listOfImprintMethod)){
        	  listOfImprintMethod = new ArrayList<>();
		  }
		  String priceVal = "";
		  
		  priceVal = runChargeVal.replaceAll("[^0-9.]", "");
		  String priceInclude = runChargeVal.split("\\)")[1].trim();
		  if(runChargeVal.contains("additional color/position")){//additional color & additional location
			  listOfAdditionalColor = getAdditinalColor("Additional Color",listOfAdditionalColor);
			  listOfAdditionalLocation = getAdditinalLocation("Additional Position",listOfAdditionalLocation);
			prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Colors", false, currencyType,
					"Additional Color", "Add. Color Charge", "Other", 1, prieGridList, "", "per unit, per additional color");
			prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Location", false, currencyType,
					"Additional Position", "Add. Location Charge", "Other", 1, prieGridList, "", "per unit, per additional position");
		  } else if(runChargeVal.contains("second color/position")){//additional color & additional location
			  listOfAdditionalColor = getAdditinalColor("Second color",listOfAdditionalColor);
			  listOfAdditionalLocation = getAdditinalLocation("Additional Position",listOfAdditionalLocation);
			prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Colors", false, currencyType,
					"Second color", "Add. Color Charge", "Other", 1, prieGridList, "", "per unit, per additional color");
			prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Location", false, currencyType,
					"Additional Position", "Add. Location Charge", "Other", 1, prieGridList, "", "per unit, per additional position");
		  } else if(runChargeVal.contains("second laser engraved position")){// imprint method & additional location
			  listOfImprintMethod = getImprintMethod("Laser Engraved");
			  listOfAdditionalLocation = getAdditinalLocation("Second Position",listOfAdditionalLocation);
			  priceVal = runChargeVal.replaceAll("[^0-9.]", "");
			prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V",
					"Imprint Method:Laser Engraved,Additional Location:Second Position", false, currencyType,
					"Laser Engraved,Second Position", "Add. Location Charge", "Per Quantity", 1, prieGridList, "", "");
		  } else if(runChargeVal.contains("laser imprint")){//imprint method
			  listOfImprintMethod = getImprintMethod("laser imprint");
			  prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Imprint Method", false, currencyType,
						"laser imprint", "Run Charge", "Per Quantity", 1, prieGridList, "", "per unit, per additional color");
		  } else if(runChargeVal.contains("additional color")){ // additional color
			  listOfAdditionalColor = getAdditinalColor("Additional Color",listOfAdditionalColor);
				prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Colors", false, currencyType,
						"Additional Color", "Add. Color Charge", "Other", 1, prieGridList, "", "per unit, per additional color");
		  } else if(runChargeVal.contains("second position")){// additional location
			  listOfAdditionalLocation = getAdditinalLocation("Second Position",listOfAdditionalLocation);
			  prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Location", false, currencyType,
						"Second Position", "Add. Location Charge", "Other", 1, prieGridList, "", "per unit, per additional position");
		} else if(runChargeVal.contains("metallic thread color")){
			 listOfAdditionalColor = getAdditinalColor("Metallic thread color",listOfAdditionalColor);
			prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Colors", false, currencyType,
					"Metallic thread color", "Add. Color Charge", "Other", 1, prieGridList, "", priceInclude);
		} else if (runChargeVal.contains("additional position")
				|| runChargeVal.contains("additional lasered position")) {// additional
																			// location
			 listOfAdditionalLocation = getAdditinalLocation("Additional Position",listOfAdditionalLocation);
			  prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Location", false, currencyType,
						"Additional Position", "Add. Location Charge", "Other", 1, prieGridList, "", "per unit, per additional position");
		  } else if(runChargeVal.contains("second color")){ // additional color
			  listOfAdditionalColor    = getAdditinalColor("Second color",listOfAdditionalColor);
			prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Colors", false, currencyType,
					"Second color", "Add. Color Charge", "Other", 1, prieGridList, "", "");
		  } else if(runChargeVal.contains("position")){
			  listOfAdditionalLocation = getAdditinalLocation("Position",listOfAdditionalLocation);
				prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Location", false, currencyType,
						"Position", "Add. Location Charge", "Other", 1, prieGridList, "", "per unit, per additional position");
		  } else if(runChargeVal.contains("laser engraving")){//imprint method
			  listOfImprintMethod = getImprintMethod("laser engraving");
			  prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Imprint Method", false, currencyType,
						"laser engraving", "Imprint Method Charge", "Other", 1, prieGridList, "", priceInclude);
		  } else if(runChargeVal.contains("second imprint")){// additional location
			  listOfAdditionalLocation = getAdditinalLocation("second imprint",listOfAdditionalLocation);
			  prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Additional Location", false, currencyType,
						"second imprint", "Add. Location Charge", "Other", 1, prieGridList, "", priceInclude);
		} else if(runChargeVal.contains("laser imprint on clip")){
			 listOfImprintMethod = getImprintMethod("laser imprint");
			  prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Imprint Method", false, currencyType,
						"laser imprint", "Imprint Method Charge", "Other", 1, prieGridList, "", priceInclude);
		} else if (runChargeVal.contains("imprint on pen") || runChargeVal.contains("imprint on ball marker")
				|| runChargeVal.contains("imprint on eraser") || runChargeVal.contains("imprint on cup")
				|| runChargeVal.contains("imprint on barrel") || runChargeVal.contains("imprint on divot repair")
				|| runChargeVal.contains("second imprint on snack bag")) { // imprint location
			String runChareValue = runChargeVal.split("imprint")[1].trim();
			listOfImprintLocation = getImprintLocation(runChareValue,listOfImprintLocation);
			 prieGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Imprint Location", false, currencyType,
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
          List<PriceGrid> priceGridList = product.getPriceGrids();
		  if(isSpecialWordsSetupcharge(setupChargeVal)){
			  return product;
		  }
		  List<PriceGrid> priceGrid = product.getPriceGrids();
		  if(CollectionUtils.isEmpty(priceGrid)){
			  priceGrid = new ArrayList<>();
		  }
		  if(CollectionUtils.isEmpty(listOfAdditionalColor)){
			  listOfAdditionalColor = new ArrayList<>();
		  }
          if(CollectionUtils.isEmpty(listOfAdditionalLocation)){
        	  listOfAdditionalLocation = new ArrayList<>();
		  }
          if(CollectionUtils.isEmpty(listOfImprintLocation)){
        	  listOfImprintLocation = new ArrayList<>();
		  }
          
		  String[] setupChargeVals = null;
		  List<ImprintMethod> imprintMethodList = configuration.getImprintMethods();
		  String imprintMethodVal  = "";
		  if(imprintMethodList != null){
			   imprintMethodVal = imprintMethodList.stream().map(imprObj->imprObj.getAlias()).collect(Collectors.joining(","));  
		  }
		  
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
					 }  else {
						 setupChargeVals = new String[]{setupChargeVal}; 
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
			  String priceInclude = "";
			  if(upchargeVal.contains(")")){
				  String[] priceIncludes = CommonUtility.getValuesOfArray(upchargeVal, "\\)");
				  if(priceIncludes.length ==2){
					  priceInclude = priceIncludes[1].trim();  
				  }
			  }
			 if(upchargeVal.contains("imprint")){
				 if(columnName.equals("setupCharge")){
					 upchargeType = "Set-up Charge"; 
				 } else {
					 upchargeType = "Re-order Charge";
					 if(!upchargeVal.contains("imprint")){
						 priceGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", "Imprint Method", false, currencyType,
								 imprintMethodVal, "Re-order Charge", "Other", 1, priceGridList, "", priceInclude); 
					 }
				 }
				  criteriaType = "Imprint Location";
				  upchargeName = upchargeVal.split("imprint")[1].trim();
				  listOfImprintLocation = getImprintLocation(upchargeName, listOfImprintLocation);
			} else if (upchargeVal.contains("color/position") || upchargeVal.contains("per position")
					|| upchargeVal.contains("per color")) {
				 if(columnName.equals("setupCharge")){
					 upchargeType = "Set-up Charge";  
				 } else {
					 upchargeType = "Re-order Charge";
				 }				 
				 if(upchargeVal.contains("color/position")){
					 priceInclude = "per color/position";
				 } else if(upchargeVal.contains("per position")){
					 priceInclude = "per position";
				 } else if(upchargeVal.contains("per color")){
					 priceInclude = "per color";
				 }
				 criteriaType = "Imprint Method";
				 upchargeName =imprintMethodVal;
			 } /*else if(upchargeVal.contains(" per position")){
				 if(columnName.equals("setupCharge")){
					 upchargeType = "Set-up Charge";   
				 } else {
					 upchargeType = "Re-order Charge";
				 }
				  listOfAdditionalLocation = getAdditinalLocation("Additional Position",listOfAdditionalLocation);
				  criteriaType = "Additional Location";
				  upchargeName = "Additional Position";
						 
			 } */else if(upchargeVal.contains("additional color")){
				 if(columnName.equals("setupCharge")){
					 upchargeType = "Set-up Charge";   
				 } else {
					 upchargeType = "Re-order Charge";
				 }
				 listOfAdditionalColor = getAdditinalColor("Additional Color",listOfAdditionalColor);
				  criteriaType = "Additional Colors";
				  upchargeName = "Additional Color";
			 } else if(upchargeVal.contains("second color")){
				 if(columnName.equals("setupCharge")){
					 upchargeType = "Set-up Charge";   
				 } else {
					 upchargeType = "Re-order Charge";
				 }
				 listOfAdditionalColor    = getAdditinalColor("Second color",listOfAdditionalColor);
				  criteriaType = "Additional Colors";
				  upchargeName = "Second color";
			 } else {
				 if(columnName.equals("setupCharge")){
					 upchargeType = "Set-up Charge";   
				 } else {
					 upchargeType = "Re-order Charge";
				 }
				 //listOfAdditionalColor = getAdditinalColor("Additional Color",listOfAdditionalColor);
				  criteriaType = "Imprint Method";
				  upchargeName = imprintMethodVal;
			 }
			 priceGridList = eveanPriceGridParser.getUpchargePriceGrid("1", priceVal, "V", criteriaType, false, currencyType,
					 upchargeName, upchargeType, "Other", 1, priceGridList, "", priceInclude);
		}
		  configuration.setAdditionalColors(listOfAdditionalColor);
		  configuration.setAdditionalLocations(listOfAdditionalLocation);
		  configuration.setImprintLocation(listOfImprintLocation);
		  product.setPriceGrids(priceGridList);
		  return product;
	  }
	public ProductConfigurations getProductImprintMethods(String val,ProductConfigurations configuration){
		List<ImprintLocation> imprintLocationList = configuration.getImprintLocation();
		List<ImprintMethod> imprintMethodList = configuration.getImprintMethods();
		ImprintMethod imprintMethodObj = null;  
		if(CollectionUtils.isEmpty(imprintMethodList)){
			imprintMethodList = new ArrayList<>();
		}
		String imprintMethodgroups = EveanManufColorAndImprintMethodMapping.getImprintMethodGroup(val);
		String[] imprMethodVals = CommonUtility.getValuesOfArray(imprintMethodgroups, ",");
		for (String imprVal : imprMethodVals) {
			imprintMethodObj = new ImprintMethod();
			  if(imprVal.contains("ImprintLocation")){
				  imprintLocationList = getImprintLocation(imprVal.split("=")[1].trim(), imprintLocationList);
				  continue;
			  } else if(imprVal.equals("Other")){
				  imprintMethodObj.setType("Other");
				  imprintMethodObj.setAlias(val);
			  } else {
				  String[] imprMethod = imprVal.split("=");
				  imprintMethodObj.setType(imprMethod[0]);
				  imprintMethodObj.setAlias(imprMethod[1]);
			  }
			  imprintMethodList.add(imprintMethodObj);
		}
		configuration.setImprintLocation(imprintLocationList);
		configuration.setImprintMethods(imprintMethodList);
		return configuration;
	}
	public Size getProductSize(String sizeName){
		Size sizeObj = new Size();
		Values valuesObj = null;
		 Dimension dimentionObj = new Dimension();
		 List<Values> listOfValues = new ArrayList<>();
		if(sizeName.contains(";")){
			sizeName = sizeName.split(";")[0];
		}
		if(sizeName.contains("sitting") || sizeName.contains("Up to") || sizeName.contains("Snack Bag") || sizeName.contains("Lunch Bag")
				|| sizeName.contains("Gusset")){
			sizeName = formatSizeValue(sizeName);
		}
		if (sizeName.contains("L") || sizeName.contains("H") || sizeName.contains("W")
					|| sizeName.contains("D") || sizeName.contains("SQ") || sizeName.contains("DIA") 
					|| sizeName.contains("Dia") || sizeName.contains("dia") || sizeName.contains("Long")
					|| sizeName.contains("long")) {
		 sizeName = getFinalSizeValue(sizeName);
			String lastChar = sizeName.substring(sizeName.length() - 1);
			if(lastChar.equals(":")){
				sizeName = sizeName.substring(0, sizeName.length() - 1);// trim last character i.e :
			}
         String[] sss = CommonUtility.getValuesOfArray(sizeName, ":");
        if(sss.length == 2){
     	   String finalSize = sss[0];
     	   valuesObj = getOverAllSizeValObj(finalSize, sss[1], "", "");
        } else if(sss.length == 4){
     	   String finalSize = sss[0] + "x"+sss[2];
				valuesObj = getOverAllSizeValObj(finalSize, sss[1], sss[3], "");
        } else if(sss.length == 6){
     	   String finalSize = sss[0] + "x"+sss[2]+ "x"+sss[4];
				valuesObj = getOverAllSizeValObj(finalSize, sss[1], sss[3], sss[5]);
        } 
	 } 
		listOfValues.add(valuesObj);
		dimentionObj.setValues(listOfValues);
		sizeObj.setDimension(dimentionObj);
		return sizeObj;
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
		 AdditionalColor additionalColorObj = null;
		 String allAdditionalColor = additionalColorList.stream().map(addColor -> addColor.getName())
					.collect(Collectors.joining(","));
		 if(!allAdditionalColor.contains(addColorVal)){
			 additionalColorObj = new AdditionalColor();
			 additionalColorObj.setName(addColorVal);
			 additionalColorList.add(additionalColorObj);
		 }
	     return additionalColorList;
	 }
	 private List<ImprintLocation> getImprintLocation(String imprLocVal,List<ImprintLocation> imprintLocationList){
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
	 public ProductConfigurations getImprintLocationAndImprintSize(String imprVal,ProductConfigurations configuration){
		 List<ImprintLocation> imprintLocationList = configuration.getImprintLocation();
		 List<ImprintSize>     imprintSizeList = new ArrayList<>();
		 if(CollectionUtils.isEmpty(imprintLocationList)){
			 imprintLocationList = new ArrayList<>();
		 }
		 ImprintSize imprintSizeObj = null;
		 ImprintLocation  imprintLocationObj = null;
		 String[] imprLocAndSizes = CommonUtility.getValuesOfArray(imprVal, ",");
		 String allImrintLocations = imprintLocationList.stream().map(location -> location.getValue())
					.collect(Collectors.joining(","));
		 List<String> tempImprLoc = new ArrayList<>();
		 List<String> tempImprSize = new ArrayList<>();
		 for (String imprName : imprLocAndSizes) {
			 imprintSizeObj = new ImprintSize();
			 imprintLocationObj = new ImprintLocation();
			 String[] imprVals = null;
			 if(imprName.contains(":")){
				 imprVals = CommonUtility.getValuesOfArray(imprName, ":");
			 } else if(imprName.contains("-")){
				 imprVals = CommonUtility.getValuesOfArray(imprName, "-");
			 } else {
				 
				 if(!tempImprSize.contains(imprName)){
					 imprintSizeObj.setValue(imprName);
					 imprintSizeList.add(imprintSizeObj);	
					 tempImprSize.add(imprName);
				 } 
				 
			 }
			 if(imprVals !=null){
				 String imprSize = imprVals[1].trim();
				 if(!tempImprSize.contains(imprSize)){
					 imprintSizeObj.setValue(imprSize);
					 imprintSizeList.add(imprintSizeObj);
					 tempImprSize.add(imprSize);
				 } 
				
				String imprLocVal =  imprVals[0].trim();
				 if(!allImrintLocations.contains(imprLocVal) && !tempImprLoc.contains(imprLocVal)){
					 imprintLocationObj.setValue(imprLocVal);
					 imprintLocationList.add(imprintLocationObj);	
					 tempImprLoc.add(imprLocVal);
				 }
			 }
		}
		 configuration.setImprintLocation(imprintLocationList);
		 configuration.setImprintSize(imprintSizeList);
		 return configuration;
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
	 private String getFinalSizeValue(String val){
		 //  val = removeWordsFromSize(val);
		 if(val.contains("natural")){
			 val = val.replaceAll("natural", "");
		 }
		   val = val.replaceAll("[^0-9/,DWLHSQDiadiaxXDIA. ]", "");
		   String[] sizes = null;
		   StringBuilder sizess = new StringBuilder();
		   if(val.contains("SQ")){
			   sizess = getSizeSqure(val);
		   } else{
			   if(val.contains("x")){
				   sizes = CommonUtility.getValuesOfArray(val, "x");
			   } else {
				   sizes = CommonUtility.getValuesOfArray(val, "X");
			   }
			   for (String size : sizes) {
				String sizeVal =  size.replaceAll("[^0-9/ ]", "").trim();
				String sizeUnit = size.replaceAll("[^a-zA-Z]", "").trim();
				String temp = sizeUnit; 
				if(StringUtils.isEmpty(sizeUnit)){
					sizeUnit = "L";// default value assign
				}
				if(sizeUnit.equalsIgnoreCase("aDia")){
					sizeUnit = "Dia";
				} else if(sizeUnit.equals("LL")){
					sizeUnit = "L";
				} else{
					
				}
				sizeUnit = LookupData.getSizeUnit(sizeUnit);
				if(sizeUnit == null){
					if(!temp.equalsIgnoreCase("dia")){
						temp = temp.replaceAll("[^LHWD]", "").trim();// HERE trim all unnecessary characters
						if(StringUtils.isEmpty(temp)){
							temp = "L";
						}
						sizeUnit = LookupData.getSizeUnit(temp);
					}
				}
				String ss = sizeVal+":"+sizeUnit;
				sizess.append(ss).append(":");
			}   
		   }  
		   return sizess.toString();
	 }
	 private StringBuilder getSizeSqure(String sizeVal){
		   StringBuilder finlSize = new StringBuilder();
		   if(sizeVal.contains("x")){//7/8" SQ x 3 3/4" L
			   String[] sss = CommonUtility.getValuesOfArray(sizeVal, "x");
			   String s1 = sss[1];
			   String squreVal = sss[0];
			   String unit1 = null,unit2 = null,unit3 = null;
			   if(s1.contains("L")){
				   unit1 = "Width";unit2 = "Height";unit3 = "Length";
			   } else if(s1.contains("H")){
				   unit1 = "Width";unit2 = "Length";unit3 = "Height";
			   } else if(s1.contains("D")){
				   unit1 = "Width";unit2 = "Length";unit3 = "Depth";
			   }
			   s1  = s1.replaceAll("[^0-9/ ]", "");
			   squreVal  = squreVal.replaceAll("[^0-9/ ]", "");
			   finlSize.append(squreVal).append(":").append(unit1).append(":").append(squreVal).append(":").append(unit2)
				.append(":").append(s1).append(":").append(unit3);
			} else{//7/8 SQ
				sizeVal  = sizeVal.replaceAll("[^0-9/ ]", "");
				finlSize.append(sizeVal).append(":").append("Length").append(":").append(sizeVal).append(":").append("Width");
			}	   
		   return finlSize;
	 }
	 private Values getOverAllSizeValObj(String val,String unit1,String unit2,String unit3){
			//Overall Size: 23.5" x 23.5"
		 if( val.contains("(colorss)")){
			val= val.replaceAll("[(colorss)]", "");
		 }
		 if(val.contains("long")){
			 val = val.replaceAll("long", "");
		 }
			String[] values = null;
			if(val.contains("X")){
				values = val.split("X");
			} else {
				values = val.split("x");
			}
			Value valObj1 = null;
			Value valObj2 = null;
			Value valObj3 = null;
			List<Value> listOfValue = new ArrayList<>();
			if(values.length == ApplicationConstants.CONST_INT_VALUE_ONE){
				 valObj1 = getValueObj(values[0].trim(), unit1, "in");
				  listOfValue.add(valObj1);
			} else if(values.length == ApplicationConstants.CONST_INT_VALUE_TWO){
				 valObj1 = getValueObj(values[0].trim(), unit1, "in");
				 valObj2 = getValueObj(values[1].trim(), unit2, "in");
				 listOfValue.add(valObj1);
			     listOfValue.add(valObj2);
			} else if(values.length == ApplicationConstants.CONST_INT_VALUE_THREE){
				 valObj1 = getValueObj(values[0].trim(), unit1, "in");
				 valObj2 = getValueObj(values[1].trim(),unit2, "in");
				 valObj3 = getValueObj(values[2].trim(), unit3, "in");
				 listOfValue.add(valObj1);
			     listOfValue.add(valObj2);
			     listOfValue.add(valObj3);
			}
			 Values valuesObj = new Values(); 
			 valuesObj.setValue(listOfValue);
			 return valuesObj;
		}
	private Value getValueObj(String value,String attribute,String unit){
			Value valueObj = new Value();
			valueObj.setAttribute(attribute);
			valueObj.setUnit(unit);
			valueObj.setValue(value);
			return valueObj;
		}
   private String formatSizeValue(String sizeVal){
	   if(sizeVal.contains(";")){
		   sizeVal = sizeVal.split(";")[0];
	   }
	   if(sizeVal.contains("sitting") || sizeVal.contains("Up to")){
		   sizeVal = sizeVal.replaceAll("[^0-9/]", "");
		   sizeVal = sizeVal+"L";
	   } else if(sizeVal.contains("Snack Bag")){
		   sizeVal = sizeVal.split("-")[1].trim();
	   } else if(sizeVal.contains("Lunch Bag")){
		   if(sizeVal.contains("-")){
			   sizeVal = sizeVal.split("-")[1].trim();
		   } else {
			   sizeVal = sizeVal.split(":")[1].trim();
		   }
	   } else if(sizeVal.contains("Gusset")){
		   String[] sizess = sizeVal.split("x");
		   sizeVal = sizess[0]+sizess[1];
	   }
	   return sizeVal;
   }
   public Product getProductPackaging(String packVal,Product product){
	   ProductConfigurations configuration = product.getProductConfigurations();
	   List<PriceGrid> priceGrids = product.getPriceGrids();
	   List<Packaging> packagingList = new ArrayList<>();
	   Packaging packagingObj = null;
	   Packaging packagingObj1 = null;
	   String[] packValues = CommonUtility.getValuesOfArray(packVal, ";");
	   for (String packName : packValues) {
		packagingObj = new Packaging();
		packagingObj1 = new Packaging();
		if(packName.equalsIgnoreCase("For individual polybagging, add $.10(V) each")){
			packagingObj.setName("Individual Poly Bag");
			priceGrids = eveanPriceGridParser.getUpchargePriceGrid("1", ".10", "v", "Packaging", false, currencyType,
						"Individual Poly Bag", "Packaging Charge", "Per Quantity", 1, priceGrids, "", "");
		} else if(packName.equalsIgnoreCase("For individual shrink wrapping, add $.10(V) each")){
			packagingObj.setName("Shrink Wrap");
			priceGrids = eveanPriceGridParser.getUpchargePriceGrid("1", ".10", "v", "Packaging", false, currencyType,
						"Shrink Wrap", "Packaging Charge", "Per Quantity", 1, priceGrids, "", "");
		} else if(packName.equalsIgnoreCase("12 pieces per box, 30-40 dozen per case")){
			packagingObj.setName("12:per box");
		} else if(packName.contains("Individually polybagged") || packName.contains("individually polybagged")){
			packagingObj.setName("Individual Poly Bag");
			if(packName.contains("Straws packaged") || packName.contains("straws packaged")){
			  product.setAdditionalShippingInfo("Straws packaged separately");	
			}
		} else if(packName.equalsIgnoreCase("Polybagged 10 pieces per pack")){
			packagingObj.setName("Poly Bag");
			packagingObj1.setName("10:Per Pack");
			packagingList.add(packagingObj1);
		} else if(packName.equalsIgnoreCase("Shrink Wrapped 10 pieces per pack")){
			packagingObj.setName("Shrink Wrap");
			packagingObj1.setName("10:Per Pack");
			packagingList.add(packagingObj1);
		} else if(packName.equalsIgnoreCase("Individually boxed with instructions")){
			packagingObj.setName(packName);
		} else if(packName.equalsIgnoreCase("Shipped flat, bulk packed")){
			packagingObj.setName("Bulk");
			product.setAdditionalShippingInfo("Shipped flat");
		} else if(packName.equalsIgnoreCase("Shipped unassembled and polybagged")){
			packagingObj.setName("Poly Bag");
			product.setAdditionalShippingInfo("Shipped Unassembled");
		} else if(packName.equalsIgnoreCase("Safety sealed for your protection")){
			product.setAdditionalShippingInfo(packName);
		} else if(packName.equalsIgnoreCase("Bulk. 160 per inner box")){
			packagingObj.setName("Bulk");
			packagingObj1.setName("160:Per Box");
		} else  if(packName.contains("Bulk") || packName.contains("bulk")){
			packagingObj.setName("Bulk");
		} else {
			packagingObj.setName(packName);
		}
		packagingList.add(packagingObj);
	}
	   product.setPriceGrids(priceGrids);
	   configuration.setPackaging(packagingList);
	   product.setProductConfigurations(configuration);
	   return product;
   }
   public Volume getProductItemWeightvolume(String itemWeightValue){
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
	
   public List<FOBPoint> getFobPoint(final String  value,String authToken,String environment){
		List<FOBPoint> listOfFobPoint = new ArrayList<>();
		if(lookupFobPoints == null){
			lookupFobPoints = lookupServiceData.getFobPoints(authToken,environment);
		}
		String finalFobValue = lookupFobPoints.stream().filter(fobValue -> fobValue.contains(value))
				                              .collect(Collectors.joining());
		if(!StringUtils.isEmpty(finalFobValue)){
			FOBPoint fobPointObj = new FOBPoint();
			fobPointObj.setName(finalFobValue);
			listOfFobPoint.add(fobPointObj);
		}
		return listOfFobPoint;
	} 
   public List<Image> getImages(List<String> colorImgVals){
		List<Image> listOfImages = new ArrayList<>();
		Image imageObj = null;
		int imageRank = 1;
		for (String colorImgVal : colorImgVals) {
			String[] vals = CommonUtility.getValuesOfArray(colorImgVal,"##");
			imageObj = new Image();
			String color = vals[0];
			String imgUrl = vals[1];
			imageObj = new Image();
			imageObj.setImageURL(imgUrl);
			imageObj.setIsvirtualized(false);
			imageObj.setRank(imageRank);
			imageObj.setDescription("");
			if(imageRank == 1){
				imageObj.setIsPrimary(true);
			} else {
				imageObj.setIsPrimary(false);
			}
			 List<Configurations>  configurationList = getImageConfiguration(color);
		     imageObj.setConfigurations(configurationList);
			listOfImages.add(imageObj);
			imageRank++;
		}
		return listOfImages;
	}
  private List<Configurations> getImageConfiguration(String val){
	  List<Configurations>  configurationList = new ArrayList<>();
	  Configurations configObj = new Configurations();
	  val = val.replaceAll("/", "-");
	  configObj.setCriteria("Product Color");
	  configObj.setValue(Arrays.asList(val));
	  configurationList.add(configObj);
	  return configurationList;
  }
  
  public List<Color> getProductColor(List<String> colors){
		List<Color> listOfProductColor = new ArrayList<>();
		Color colorObj = null;
		List<String> processedColorList = new ArrayList<>();
		for (String colorName : colors) {
			colorName = colorName.trim();
			if(!processedColorList.contains(colorName)){
				colorObj = new Color();
				if(colorName.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)){
					colorObj = getColorCombo(colorName);
				} else {
					String colorGroup = EveanManufColorAndImprintMethodMapping.getColorGroup(colorName);
					colorObj.setName(colorGroup);
					colorObj.setAlias(colorName);
				}
			   listOfProductColor.add(colorObj);	
			   processedColorList.add(colorName);
			}
			
		}
		return listOfProductColor;
	}
	private Color getColorCombo(String comboVal){
		Color colorObj = new Color();
		List<Combo> listOfComos = new ArrayList<>();
		Combo comboObj1 = new Combo();
		Combo comboObj2 = new Combo();
		String[] comboColors = CommonUtility.getValuesOfArray(comboVal,
				                  ApplicationConstants.CONST_DELIMITER_FSLASH);
		colorObj.setName(
				EveanManufColorAndImprintMethodMapping.getColorGroup(comboColors[ApplicationConstants.CONST_NUMBER_ZERO]));
		comboObj1.setName(
				EveanManufColorAndImprintMethodMapping.getColorGroup(comboColors[ApplicationConstants.CONST_INT_VALUE_ONE]));
		comboObj1.setType(ApplicationConstants.CONST_STRING_SECONDARY);
		colorObj.setAlias(comboVal.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH,
				ApplicationConstants.CONST_DELIMITER_HYPHEN));
		if(comboColors.length == 3){
			comboObj2.setName(
					EveanManufColorAndImprintMethodMapping.getColorGroup(comboColors[ApplicationConstants.CONST_INT_VALUE_TWO]));
			comboObj2.setType(ApplicationConstants.CONST_STRING_TRIM);
			listOfComos.add(comboObj2);
		} 
		listOfComos.add(comboObj1);
		colorObj.setCombos(listOfComos);
		return colorObj;
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
		public LookupServiceData getLookupServiceData() {
			return lookupServiceData;
		}
		public void setLookupServiceData(LookupServiceData lookupServiceData) {
			this.lookupServiceData = lookupServiceData;
		}
}
