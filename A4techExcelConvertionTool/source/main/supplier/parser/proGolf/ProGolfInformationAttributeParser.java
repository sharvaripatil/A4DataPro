package parser.proGolf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.AvailableVariations;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Configurations;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.SameDayRush;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.TradeName;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class ProGolfInformationAttributeParser {
	private LookupServiceData lookupServiceData;
	private ProGolfPriceGridParser proGolfPriceGridParser;
	private static List<String> fobPoints = null;
	
	public List<TradeName> getTradeNames(String value){
		List<TradeName> listOfTradeNames = new ArrayList<>();
		TradeName tradeNameObj  = new TradeName();
		if(!"Travel Chair".equalsIgnoreCase(value)){
			tradeNameObj.setName(value);
			listOfTradeNames.add(tradeNameObj);
		}
		return listOfTradeNames;
	}
	public Size getProductSize(String value){
		Size size = new Size();
		Apparel apparealObj = new Apparel();
		apparealObj.setType("Standard & Numbered");
		List<Value> listOfValue = getSizeValues(value);
		apparealObj.setValues(listOfValue);
		size.setApparel(apparealObj);
		return size;
	}
	private List<Value> getSizeValues(String sizeVals){
		List<Value> listOfValue = new ArrayList<>();
		Value valueObj = null;
		String[] sizes = CommonUtility.getValuesOfArray(sizeVals, 
				ApplicationConstants.CONST_DELIMITER_SPLITTING_PIPE);
		for (String sizeVal : sizes) {
			valueObj = new Value();
			sizeVal = getStandrdSizeValues(sizeVal);
			valueObj.setValue(sizeVal);
			listOfValue.add(valueObj);
		}
		return listOfValue;
	}
	private String getStandrdSizeValues(String sizeVal){
		if(sizeVal.contains("SMALL")){
			sizeVal = sizeVal.replaceAll("SMALL", "S");
		} 
         if(sizeVal.contains("MEDIUM") || sizeVal.contains("Medium")){
			sizeVal = sizeVal.replaceAll("MEDIUM", "M");
			sizeVal = sizeVal.replaceAll("Medium", "M");
		}
         if(sizeVal.contains("large") || sizeVal.contains("LARGE")){
			sizeVal = sizeVal.replaceAll("LARGE", "L");
			sizeVal = sizeVal.replaceAll("large", "L");
		}
		if(sizeVal.equalsIgnoreCase("OSFM")) {
			sizeVal = "One Size";
		}
		if(sizeVal.contains("XL")){
			int noOfXs = StringUtils.countOccurrencesOf(sizeVal, "X");
			if(noOfXs == ApplicationConstants.CONST_INT_VALUE_TWO){
				sizeVal = "2XL";
			} else if(noOfXs == ApplicationConstants.CONST_INT_VALUE_THREE){
				sizeVal = "3XL";
			} else if(noOfXs == ApplicationConstants.CONST_INT_VALUE_FOUR){
				sizeVal = "4XL";
			} else if(noOfXs == ApplicationConstants.CONST_INT_VALUE_FIVE){
				sizeVal = "5XL";
			}
		}
		return sizeVal;
	}
	public ProductConfigurations getImprintSizes(String value, ProductConfigurations existingConfig){
		List<ImprintSize> listOfImprintSize = new ArrayList<>();
		List<ImprintLocation> listOfImprintLocation = new ArrayList<>();
		ImprintSize imprintSize = null;
		ImprintLocation imprintLoc = null;
		String[] imprSizes = CommonUtility.getValuesOfArray(value, 
				                      ApplicationConstants.CONST_DELIMITER_SPLITTING_PIPE);
		for (String imprSize : imprSizes) {
			if(imprSize.contains(":")){
				String[] sizess = imprSize.split(":");
				imprintSize = new ImprintSize();
				imprintLoc = new ImprintLocation();
				imprintLoc.setValue(sizess[ApplicationConstants.CONST_NUMBER_ZERO]);
				imprintSize.setValue(sizess[ApplicationConstants.CONST_INT_VALUE_ONE]);
				listOfImprintSize.add(imprintSize);
				listOfImprintLocation.add(imprintLoc);
			}else{
				 imprintSize = new ImprintSize();
				 imprintSize.setValue(imprSize);
				 listOfImprintSize.add(imprintSize);
			}
		}
		existingConfig.setImprintSize(listOfImprintSize);
		existingConfig.setImprintLocation(listOfImprintLocation);
		return existingConfig;
	}
	public List<Option> getProductOption(String values,String optionType,String optionName,boolean isRequiredForOrder,
			      List<Option> listOfOptions){
		if(CollectionUtils.isEmpty(listOfOptions)){
			listOfOptions = new ArrayList<>();
		}
		List<OptionValue> listOfOptionValue = new ArrayList<>();
		Option optionObj = new Option();
		OptionValue optionValObj = null;
		String[] optionVals = CommonUtility.getValuesOfArray(values,
				ApplicationConstants.CONST_DELIMITER_SPLITTING_PIPE);
		for (String optionVal : optionVals) {
			optionValObj = new OptionValue();
			if(optionVal.contains("°")){
				optionVal = optionVal.replaceAll("°", "");
			}
			if(optionVal.equalsIgnoreCase("Regular")){
				continue;
			}
			if(optionVal.contains("Cadet")){
				optionVal = "Cadet Left";
			} else if(optionVal.contains("Left")){
				optionVal = "Regular Left";
			} else if(optionVal.contains("Right")){
				optionVal = "Regular Right";
			}
			optionValObj.setValue(optionVal);
			listOfOptionValue.add(optionValObj);
		}
		optionObj.setValues(listOfOptionValue);
		optionObj.setName(optionName);
		optionObj.setOptionType(optionType);
		optionObj.setRequiredForOrder(isRequiredForOrder);
		listOfOptions.add(optionObj);
		return listOfOptions;
	}
	public List<Color> getProductColors(String color){
		List<Color> listOfColors = new ArrayList<>();
		Color colorObj = null;
		String finalColors =  Arrays.stream(color.split("\\|")).map(String::trim).distinct().collect(Collectors.joining("|"));
		String[] colors = CommonUtility.getValuesOfArray(finalColors, ApplicationConstants.CONST_DELIMITER_SPLITTING_PIPE);
		for (String colorName : colors) {
			if(colorName.contains("Tin")){
				colorName = colorName.replaceAll("Tin", "");
			} else if(colorName.contains("Box")){
				colorName = colorName.replaceAll("Box", "");
			} else if(colorName.contains("Case")){
				colorName = colorName.replaceAll("Case", "");
			}
			//colorName = colorName.trim();
			if(StringUtils.isEmpty(colorName)){
				continue;
			}
			colorObj = new Color();
			String colorGroup = ProGolfColorMapping.getColorGroup(colorName.trim());
			if (colorGroup == null) {
				if(colorName.equalsIgnoreCase("WHITE W/RED")){
					colorName = "WHITE/RED";
				}
				if(colorName.equalsIgnoreCase("Yellow w/ Black")){
					colorName = "Yellow/Black";
				}
				if (colorName.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
					if(CommonUtility.isComboColor(colorName)){
						List<Combo> listOfCombo = null;
						String[] comboColors = CommonUtility.getValuesOfArray(colorName,
								ApplicationConstants.CONST_DELIMITER_FSLASH);
						String colorFirstName = ProGolfColorMapping.getColorGroup(comboColors[0].trim());
						colorObj.setName(colorFirstName == null?"Other":colorFirstName);
						int combosSize = comboColors.length;
						if (combosSize == ApplicationConstants.CONST_INT_VALUE_TWO) {
							String colorComboFirstName = ProGolfColorMapping.getColorGroup(comboColors[1].trim());
							colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
							listOfCombo = getColorsCombo(colorComboFirstName, ApplicationConstants.CONST_STRING_EMPTY,
									combosSize);
						} else{
							String colorComboFirstName = ProGolfColorMapping.getColorGroup(comboColors[1].trim());
							colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
							
							String colorComboSecondName = ProGolfColorMapping.getColorGroup(comboColors[2].trim());
							colorComboSecondName = colorComboSecondName == null?"Other":colorComboSecondName;
							listOfCombo = getColorsCombo(colorComboFirstName,colorComboSecondName, combosSize);
						}
						String alias = colorName.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH, "-");
						colorObj.setAlias(alias);
						colorObj.setCombos(listOfCombo);
					} else {
						String[] comboColors = CommonUtility.getValuesOfArray(colorName,
								ApplicationConstants.CONST_DELIMITER_FSLASH);
						String mainColorGroup = ProGolfColorMapping.getColorGroup(comboColors[0].trim());
						if(mainColorGroup != null){
							colorObj.setName(mainColorGroup);
							colorObj.setAlias(colorName);
						} else {
							colorObj.setName(ApplicationConstants.CONST_VALUE_TYPE_OTHER);
							colorObj.setAlias(colorName);
						}
					}
					
				} else {
					colorGroup = ApplicationConstants.CONST_VALUE_TYPE_OTHER;
					colorObj.setName(colorGroup);
					colorObj.setAlias(colorName);
				}
			} else {
				colorObj.setName(colorGroup);
				colorObj.setAlias(colorName);
			}
			listOfColors.add(colorObj);
		}
		return listOfColors;
	}
	private List<Combo> getColorsCombo(String firstValue,String secondVal,int comboLength){
		List<Combo> listOfCombo = new ArrayList<>();
		Combo comboObj1 = new Combo();
		Combo comboObj2 = new Combo();
		comboObj1.setName(firstValue);
		comboObj1.setType(ApplicationConstants.CONST_STRING_SECONDARY);
		comboObj2.setName(secondVal);
		comboObj2.setType(ApplicationConstants.CONST_STRING_TRIM);
		if(comboLength == ApplicationConstants.CONST_INT_VALUE_TWO){
			listOfCombo.add(comboObj1);
		} else {
			listOfCombo.add(comboObj1);
			listOfCombo.add(comboObj2);
		}
		return listOfCombo;
	}
	public Product getFeaturesParser(String value,Product existingProduct){
		ProductConfigurations productConfig = existingProduct.getProductConfigurations();
		String distributorComments = existingProduct.getDistributorOnlyComments();
		List<PriceGrid> priceGrids = existingProduct.getPriceGrids();
		String[] values = null;
		if(value.contains("Returns") || value.contains("Not Returnable") || value.contains("not returnable")){
			value = value.replaceAll(ApplicationConstants.CONST_DELIMITER_SPLITTING_PIPE, "");
			distributorComments = distributorComments.replaceAll("\\|", "");
			distributorComments = CommonUtility.appendStrings(distributorComments, value, " ");
		} else if(value.contains("Lead Time")){
			if(value.equalsIgnoreCase("Lead Time:|Lead Time: Stock Products 1-2 Business Days                      Logoed Products 2-4 Weeks")){
				value = "Lead Time:|Lead Time: Stock Products 1-2 Business Days,Logoed Products 2-4 Weeks";
			}
			//it is remove "|Lead Time:" if any data follow by leadTime
			value = value.substring(value.indexOf(":") + 1);
			if(value.contains("Lead Time")){//Lead Time|3-5 Business Days
				value = value.replaceAll("Lead Time", "");
				value = value.replaceAll("\\|", "");
			}
			value = value.trim();
			//value = removeTrailData(value, ".*:");
			if(value.contains(";")){
				values = CommonUtility.getValuesOfArray(value, ApplicationConstants.CONST_DELIMITER_SEMICOLON);
			} else{
				values = CommonUtility.getValuesOfArray(value, ApplicationConstants.CONST_STRING_COMMA_SEP);
			}
			List<ProductionTime> listOfProductionTime = getProductionTime(values,productConfig.getProductionTime());
			productConfig.setProductionTime(listOfProductionTime);
		} else if(value.equalsIgnoreCase("|Rush Services Available: (Minimum order of 12 dz for Rush Services)")){
			if(productConfig.getRushTime() == null){
				RushTime rushTimeOb = new RushTime();
				rushTimeOb.setAvailable(true);
				productConfig.setRushTime(rushTimeOb);
			}
		} else if(value.equalsIgnoreCase("|Same day production: $20.00 (A) additional per dozen")){
			SameDayRush sameDayRush = new SameDayRush();
			sameDayRush.setAvailable(true);
			sameDayRush.setDetails("");
			priceGrids = proGolfPriceGridParser.getUpchargePriceGrid("1", "20", "A",
					"SDRU", false, "USD", "Same Day Service", "Rush Service Charge", "Other",
					1, priceGrids, "","dozen");
			productConfig.setSameDayRush(sameDayRush);
		} else if(value.equalsIgnoreCase("|2 day production: $14.00 (A) additional per dozen")){
			RushTime rushTime = getProductRushTime("2", productConfig.getRushTime());
			productConfig.setRushTime(rushTime);
			priceGrids = proGolfPriceGridParser.getUpchargePriceGrid("1", "14", "A",
					"RUSH", false, "USD", "2 business days", "Rush Service Charge", "Other",
					1, priceGrids, "","dozen");
		} else if(value.equalsIgnoreCase("|3 day production: $10.00 (A) additional per dozen")){
			RushTime rushTime = getProductRushTime("3", productConfig.getRushTime());
			productConfig.setRushTime(rushTime);
			priceGrids = proGolfPriceGridParser.getUpchargePriceGrid("1", "14", "A",
					"RUSH", false, "USD", "3 business days", "Rush Service Charge", "Other",
					1, priceGrids, "","dozen");
		} else if(value.equalsIgnoreCase("|4 day production: $8.00 (A) additional per dozen")){
			RushTime rushTime = getProductRushTime("4", productConfig.getRushTime());
			productConfig.setRushTime(rushTime);
			priceGrids = proGolfPriceGridParser.getUpchargePriceGrid("1", "14", "A",
					"RUSH", false, "USD", "4 business days", "Rush Service Charge", "Other",
					1, priceGrids, "","dozen");
		} else if(value.contains("Imprint Area")){
			value = value.substring(value.indexOf("|") + 1);
			if(value.contains("Imprint Area")){
				value = value.substring(value.indexOf(":") + 1);
			}
			if(value.equalsIgnoreCase("Pole 1 Pole 2") || value.equalsIgnoreCase("Pole 1 or Pole 2")){
				value = "Pole 1 ,Pole 2";
			}
			if(!value.equalsIgnoreCase("Blank")){
				if(value.contains(";")){
					values = CommonUtility.getValuesOfArray(value, ApplicationConstants.CONST_DELIMITER_SEMICOLON);
				} else{
					values = CommonUtility.getValuesOfArray(value, ApplicationConstants.CONST_STRING_COMMA_SEP);
				}
				List<ImprintLocation> listOfImprintLoc = getImprintLocations(values,productConfig.getImprintLocation());
				productConfig.setImprintLocation(listOfImprintLoc);
			}	
		} else if(value.contains("Imprint size")){
			value = value.substring(value.indexOf(":") + 1);
			if(value.contains("Max size")){
				value = value.replaceAll("Imprint size\\|Max size", "").trim();
			}
			List<ImprintSize> listOfImprintSize = getImprintSizes(value, productConfig.getImprintSize());
			productConfig.setImprintSize(listOfImprintSize);
		} else if(value.contains("Ball Type")){
		}
		else if(value.contains("Minimum")){
			value = value.substring(value.indexOf("|") + 1);
			distributorComments = CommonUtility.appendStrings(distributorComments, value, " ");
		} else if(value.contains("Blank") || value.contains("Imprint Method")){
			List<ImprintMethod> listOfImprintMethods = null;
			if(value.contains("Blank")){
				values = new String[] {"Unimprinted"};
				listOfImprintMethods = getProductImprintMethods(values,productConfig.getImprintMethods());
			}else{
				value = value.substring(value.indexOf(":") + 1);
				values = CommonUtility.getValuesOfArray(value, ApplicationConstants.CONST_STRING_COMMA_SEP);
				 listOfImprintMethods = getProductImprintMethods(values,productConfig.getImprintMethods());
			}
			
			productConfig.setImprintMethods(listOfImprintMethods);
		} else if(value.contains("Shipping Info") || value.contains("FOB|")){
			existingProduct.setAdditionalShippingInfo("Product will be ship from the closest location with inventory to the shipping location.8 different warehouse throughout the US");
			distributorComments = CommonUtility.appendStrings(distributorComments, "Call customer service for FOB", " ");
		} else if(value.contains("Set up Fee")){
		  // set up charge values already present in product pricing sheet	
		} else if(value.contains("Packaged") || value.contains("packaged")){
			if(value.contains("bulk")){
				List<Packaging> listOfPackages = getPackagingValues("Bulk");
				productConfig.setPackaging(listOfPackages);
			}
		} else if(value.contains("Choose color")) {
			List<Color> existingColors = productConfig.getColors();
			if(CollectionUtils.isEmpty(existingColors)){
				value = value.substring(value.indexOf(":") + 1);
				value = value.replaceAll("Yellow.", "Yellow");
				value = value.replaceAll(",", "\\|");
			}
			existingColors = getProductColors(value);
			productConfig.setColors(existingColors);
		} else if(value.contains("Colors available")){
			// availability create based on color and Product Options
			existingProduct = getAvailabilityAndProductNumbers(value, existingProduct);
		} else if(value.contains("Available")){
			// no need create availability as per client feedback
		} else {
			distributorComments = distributorComments.replaceAll("\\|", "");
			distributorComments = CommonUtility.appendStrings(distributorComments, value, " ");
		}
		existingProduct.setProductConfigurations(productConfig);
		existingProduct.setDistributorOnlyComments(distributorComments);
		existingProduct.setPriceGrids(priceGrids);
		return existingProduct;
	}
	private List<ProductionTime> getProductionTime(String[] productionTimeValues,List<ProductionTime> existingProductionTime){
		ProductionTime productionTimeObj = null;
		if(CollectionUtils.isEmpty(existingProductionTime)){
			existingProductionTime = new ArrayList<>();
		}
		for (String productionTimes : productionTimeValues) {
			productionTimeObj = new ProductionTime();
			String details = productionTimes;
			String productionTime = productionTimes.replaceAll("\\(.*?\\)", "").trim();
			productionTime = productionTime.replaceAll("[^0-9-]", "").trim();
			if(StringUtils.isEmpty(productionTime)){
				continue;
			}
			if(details.contains("Weeks") || details.contains("weeks")){
				int prdTimeLength = productionTime.length();
				if(prdTimeLength >3){
					productionTime = productionTime.substring(0,3);
				}
				productionTime = CommonUtility.convertProductionTimeWeekIntoDays(productionTime);
			}
			if(!isProductionTimeAvailable(productionTime, existingProductionTime)){
				productionTimeObj.setBusinessDays(productionTime);
				if(details.contains("business days") || details.contains("Business Days")){
					details = details.replaceAll("business days", "");
					details = details.replaceAll("Business Days", "");
				}
				if(!productionTime.equalsIgnoreCase(details.trim())){
					productionTimeObj.setDetails(details);
				} else{
					productionTimeObj.setDetails("");
				}
				existingProductionTime.add(productionTimeObj);
			}
		}
		return existingProductionTime;
	}
	public List<ProductionTime> getProductionTime(String productionTimeValues,String details,List<ProductionTime> existingProductionTime){
		ProductionTime productionTimeObj = new ProductionTime();
			if(CollectionUtils.isEmpty(existingProductionTime)){
				existingProductionTime = new ArrayList<>();
			}
			if(details.contains("Week") || details.contains("week")){
				productionTimeValues = CommonUtility.convertProductionTimeWeekIntoDays(productionTimeValues);
			}if(productionTimeValues.contains("-")){
				if(!isProductionTimeAvailable(productionTimeValues, existingProductionTime)){
					productionTimeObj.setBusinessDays(productionTimeValues);
					/*if(details.contains("business days") || details.contains("Business Days")){
						details = details.replaceAll("business days", "");
						details = details.replaceAll("Business Days", "");
					}*/
					productionTimeObj.setDetails("");
					existingProductionTime.add(productionTimeObj);
				}
			}
			
		return existingProductionTime;
	}
	public List<ProductionTime> getProductionTime(String productionTime,List<ProductionTime> existingProductionTime){
		ProductionTime productionTimeObj = null;
		if(CollectionUtils.isEmpty(existingProductionTime)){
			existingProductionTime = new ArrayList<>();
		}
		if(!isProductionTimeAvailable(productionTime, existingProductionTime)){
			productionTimeObj = new ProductionTime();
			productionTimeObj.setBusinessDays(productionTime);
			productionTimeObj.setDetails("");
			existingProductionTime.add(productionTimeObj);
		}
		return existingProductionTime;
	}
	public List<ImprintMethod> getProductImprintMethods(String[] imprintMethodVals,List<ImprintMethod> existingImprintMethods){
		ImprintMethod imprintMethodObj = null;
		String imprintMethodType = "";
		if(CollectionUtils.isEmpty(existingImprintMethods)){
			existingImprintMethods = new ArrayList<>();
		}
		for (String imprintMethodVal : imprintMethodVals) {
			imprintMethodVal = imprintMethodVal.trim();
			if(imprintMethodVal.equalsIgnoreCase("Stock Product")){
				continue;
			} else if(imprintMethodVal.equalsIgnoreCase("N/A")){
				continue;
			} else if(isImprintMethodAvailable(imprintMethodVal, existingImprintMethods)){
				continue;
			} else{
				
			}
			imprintMethodObj = new ImprintMethod();
			  if(imprintMethodVal.equals("Unimprinted") || 
					 imprintMethodVal.equalsIgnoreCase("blank") ||
					    imprintMethodVal.equalsIgnoreCase("Blank")){
				  imprintMethodType = "UNIMPRINTED";
				  imprintMethodVal = "UNIMPRINTED";
				  //imprintMethodObj.setAlias("UNIMPRINTED");
			  } else if(imprintMethodVal.equalsIgnoreCase("Embroidery")){
				  imprintMethodType = "Embroidered";
			  } else if(imprintMethodVal.equalsIgnoreCase("decorative")){
				  imprintMethodType = "Other";
			  } else if(imprintMethodVal.equalsIgnoreCase("Pad Printing")){
				  imprintMethodType = "Pad Print";
			  } else {
				  imprintMethodType = imprintMethodVal;
			  }
			  imprintMethodObj.setAlias(imprintMethodVal);
			  imprintMethodObj.setType(imprintMethodType);
			  existingImprintMethods.add(imprintMethodObj);
		}
		return existingImprintMethods;
	}
	public List<ImprintLocation> getImprintLocations(String[] imprintLocValues,List<ImprintLocation> existingImprintLoc){
		ImprintLocation imprintLocationObj = null;
		if(CollectionUtils.isEmpty(existingImprintLoc)){
			existingImprintLoc = new ArrayList<>();
		}
		for (String locationVal : imprintLocValues) {
			if(locationVal.equalsIgnoreCase("Not Available as Logo Product") || locationVal.equalsIgnoreCase("N/A")){
				continue;
			}
			if(locationVal.contains(":")){
				locationVal = locationVal.split(":")[0];
			}
			if(isImprintLocationAvailable(locationVal, existingImprintLoc)){
				continue;
			}
			if(locationVal.contains("”")){
				locationVal = locationVal.replaceAll("”", "");
			}
			imprintLocationObj = new ImprintLocation();
			imprintLocationObj.setValue(locationVal);
			existingImprintLoc.add(imprintLocationObj);
		}
		return existingImprintLoc;
	}
	
	public boolean isImprintMethodAvailable(String imprintMethodName,List<ImprintMethod> listOfImprintMrthods){
		for (ImprintMethod imprintMethod : listOfImprintMrthods) {
			if(imprintMethod.getAlias().equalsIgnoreCase(imprintMethodName)){
				return true;
			}
		}
		return false;
	}
	public Volume  getProductItemWeight(String weightVal,String unit){
		Volume volumeObj = new Volume();
		Values valuesObj = new Values();
		Value valueObj = new Value();
		List<Values> listOfValues = new ArrayList<>();
		List<Value> listOfValue = new ArrayList<>();
		valueObj.setUnit("lbs");
		valueObj.setValue(weightVal);
		listOfValue.add(valueObj);
		valuesObj.setValue(listOfValue);
		listOfValues.add(valuesObj);
		volumeObj.setValues(listOfValues);
		return volumeObj;
	}
	public ShippingEstimate getProductShippingEstimation(String shippingDimensionVal,String shippingWeight,String shippingDimentionUnit,String shippingWeightUnit){
		ShippingEstimate shippingEstimObj = new ShippingEstimate();
		String[] shippingValues = CommonUtility.getValuesOfArray(shippingDimensionVal, ",");
		if(!"0".equals(shippingValues[0])){
			List<NumberOfItems> listOfNumberOfItems = getShippingNumberOfItems(shippingValues[0], "per Carton");
			if(shippingValues.length>1){
				Dimensions dimensionsObj = getShippingDimension(shippingValues[1], shippingValues[2], shippingValues[3],
						                                        shippingDimentionUnit);
				 shippingEstimObj.setDimensions(dimensionsObj);
			}
			if(!StringUtils.isEmpty(shippingWeight)){
				List<Weight> listOfWeight = getShippingWeight(shippingWeight, shippingWeightUnit);
				 shippingEstimObj.setWeight(listOfWeight);
			}
			shippingEstimObj.setNumberOfItems(listOfNumberOfItems);
		}
		return shippingEstimObj;
	}
	
	private List<NumberOfItems> getShippingNumberOfItems(String value,String unit){
		List<NumberOfItems> listOfNumberOfItems = new ArrayList<>();
		NumberOfItems numberOfItemsObj = new NumberOfItems();
		numberOfItemsObj.setValue(value);
		numberOfItemsObj.setUnit(unit);
		listOfNumberOfItems.add(numberOfItemsObj);
		return listOfNumberOfItems;
	}
	private List<Weight> getShippingWeight(String weightVal,String unit){
		List<Weight> listOfWeight = new ArrayList<>();
		Weight weightObj = new Weight();
		weightObj.setUnit("lbs");
		weightObj.setValue(weightVal);
		listOfWeight.add(weightObj);
		return listOfWeight;
	}
	private Dimensions getShippingDimension(String length,String width,String height,String unit){
		Dimensions dimensionsObj = new Dimensions();//inches LBS
		unit = "in";// defalut unit
		dimensionsObj.setHeight(height);
		dimensionsObj.setHeightUnit(unit);
		dimensionsObj.setLength(length);
		dimensionsObj.setLengthUnit(unit);
		dimensionsObj.setWidth(width);
		dimensionsObj.setWidthUnit(unit);
		return dimensionsObj;
	}
	public List<FOBPoint> getFobPoint(String value,String authToken){
		List<FOBPoint> listOfFobPoint = new ArrayList<>();
		if(fobPoints == null){
			fobPoints = lookupServiceData.getFobPoints(authToken);
		}
		value = value.replaceAll("[^0-9]", "").trim();
		String data = value;
		String finalFobValue = fobPoints.stream().filter(fobValue -> fobValue.contains(data))
				                              .collect(Collectors.joining());
		if(!StringUtils.isEmpty(finalFobValue)){
			FOBPoint fobPointObj = new FOBPoint();
			fobPointObj.setName(finalFobValue);
			listOfFobPoint.add(fobPointObj);
		}
		return listOfFobPoint;
	}
	public Size getShippingProductSize(String value,String sizeUnit,Size existingSizes){
		return existingSizes;
	}
	public RushTime getProductRushTime(String rushTime,RushTime existingRushTime){
		List<RushTimeValue> listOfRushTimeValues = null;
		RushTimeValue rushTimeValueObj = new RushTimeValue();
		if(existingRushTime == null){
			existingRushTime = new RushTime();
			existingRushTime.setAvailable(ApplicationConstants.CONST_BOOLEAN_TRUE);
			listOfRushTimeValues = new ArrayList<>();
		} else {
			listOfRushTimeValues = existingRushTime.getRushTimeValues();
			if(CollectionUtils.isEmpty(listOfRushTimeValues)){
				listOfRushTimeValues = new ArrayList<>();
			}
		}
		rushTimeValueObj.setBusinessDays(rushTime);
		rushTimeValueObj.setDetails("");
		listOfRushTimeValues.add(rushTimeValueObj);
		existingRushTime.setRushTimeValues(listOfRushTimeValues);
		return existingRushTime;
	}
	public boolean isProductionTimeAvailable(String productionTime,List<ProductionTime> listOfPrdTime){
		for (ProductionTime productiTime : listOfPrdTime) {
			if(productiTime.getBusinessDays().equalsIgnoreCase(productionTime)){
				return true;
			}
		}
		return false;
	}
	public boolean isImprintLocationAvailable(String imprintLoc,List<ImprintLocation> listOfImprintloc){
		for (ImprintLocation location : listOfImprintloc) {
			if(location.getValue().trim().equalsIgnoreCase(imprintLoc)){
				return true;
			}
		}
		return false;
	}
	public Product keepExistingProductData(Product existingProduct){
		Product newProduct = new Product();
		ProductConfigurations newConfiguration = new ProductConfigurations();
		ProductConfigurations oldConfiguration = existingProduct.getProductConfigurations();
		if(!CollectionUtils.isEmpty(oldConfiguration.getThemes())){
			newConfiguration.setThemes(oldConfiguration.getThemes());
		}
		if(!CollectionUtils.isEmpty(existingProduct.getLineNames())){
			List<String> lineNames = getExistingLineNames(existingProduct.getLineNames());
			newProduct.setLineNames(lineNames);
		}
		newProduct.setProductConfigurations(newConfiguration);
		return newProduct;
	}
	private List<String> getExistingLineNames(List<String> existingLineNames){
		List<String> newLineNames = existingLineNames.stream()
				.filter(lineName -> lineName.contains("Pro Golf Premiums Line")).collect(Collectors.toList());
		return newLineNames;
	}
	private List<Packaging> getPackagingValues(String value){
		Packaging packObj = new Packaging();
		List<Packaging> listOfPackaging = new ArrayList<>();
		packObj.setName(value);
		listOfPackaging.add(packObj);
		return listOfPackaging;
	}
	private Availability getCololrOptionAvailablity(Map<String, String> colorOptionMap){
		Availability  availabilityObj = new Availability();
		AvailableVariations  AvailableVariObj = null;
		List<AvailableVariations> listOfVariAvail = new ArrayList<>();
		List<Object> listOfParent = null;
		List<Object> listOfChild = null;
		for (Map.Entry<String, String> colorOptionEntry: colorOptionMap.entrySet()) {
			String optionVal = colorOptionEntry.getKey();
			String[] colors = colorOptionEntry.getValue().split(",");
			 for (String childValue : colors) {
				 AvailableVariObj = new AvailableVariations();
				 listOfParent = new ArrayList<>();
				 listOfChild = new ArrayList<>();
				 listOfParent.add(optionVal.trim());
				 listOfChild.add(childValue.trim());
				 AvailableVariObj.setParentValue(listOfParent);
				 AvailableVariObj.setChildValue(listOfChild);
				 listOfVariAvail.add(AvailableVariObj);
			 }
		}
		availabilityObj.setAvailableVariations(listOfVariAvail);
		availabilityObj.setParentCriteria("Product Option");
		availabilityObj.setChildCriteria("Product Color");
		availabilityObj.setParentOptionName("Hand");
		return availabilityObj;
	}
	private Product getAvailabilityAndProductNumbers(String values,Product productObj){
		List<ProductNumber> listOfProductNumber = new ArrayList<>();
		List<Availability> listOfAvailability = new ArrayList<>();
		ProductNumber productNumberObj = null;
		Availability  availabilityObj  = null;
		String[] availables = values.split("\\.");
		Map<String, String> colorsMap = new HashMap<>();
		for (String  value: availables) {
			if(StringUtils.isEmpty(value) || value.equals(" ")){
				continue;
			}
			String[] vals = value.split(":");
			String colors = vals[1].trim();
			String optionVal = vals[0].trim();
			optionVal = optionVal.replaceAll("- Colors Available", "");
			if(optionVal.contains("Colors")){
				optionVal = optionVal.replaceAll("- Colors available", "");
			}
			optionVal = optionVal.replaceAll("\\|", "").trim();
			String optionProductNo = CommonUtility.extractValueSpecialCharacter("(", ")", optionVal);
			optionVal = optionVal.replaceAll("\\(.+?\\)","").trim();
			//String finalOptionValue  = getOptionValue(optionVal);
			productNumberObj = getProductNumbers(optionProductNo, optionVal);
			colorsMap.put(optionVal, colors);
			listOfProductNumber.add(productNumberObj);
		}
		availabilityObj = getCololrOptionAvailablity(colorsMap);
		listOfAvailability.add(availabilityObj);
		productObj.setAvailability(listOfAvailability);
		productObj.setProductNumbers(listOfProductNumber);
		return productObj;
	}
    private ProductNumber getProductNumbers(String prdNo,String value){
    	ProductNumber productNumberObj = new ProductNumber();
    	productNumberObj.setProductNumber(prdNo);
    	List<Configurations> listOfConfig = new ArrayList<>();
    	Configurations configObj = new Configurations();
    	configObj.setCriteria("Product Option");
    	configObj.setOptionName("Hand");
    	configObj.setValue(Arrays.asList(value));
    	listOfConfig.add(configObj);
    	productNumberObj.setConfigurations(listOfConfig);
    	return productNumberObj;
    }

    public List<ImprintSize> getImprintSizes(String value,List<ImprintSize> existingImprintSizes){
    	if(CollectionUtils.isEmpty(existingImprintSizes)){
    		existingImprintSizes = new ArrayList<>();
    	}
    	ImprintSize imprSizeObj = new ImprintSize();
    	imprSizeObj.setValue(value);
    	existingImprintSizes.add(imprSizeObj);
    	return existingImprintSizes;
    }
   public Product setBasePriceGridImprintMethodAndOptions(Product product){
	   List<PriceGrid> existingPriceGrid = product.getPriceGrids();
	   ProductConfigurations existingConfig = product.getProductConfigurations();
	   List<ImprintMethod> imprintMethods = existingConfig.getImprintMethods();
	   List<Option> listOfOptions = existingConfig.getOptions();
		List<OptionValue> listOfOptionVal = null;
	   for (Option option : listOfOptions) {
			  if(option.getOptionType().equalsIgnoreCase("Product")){
				  listOfOptionVal = option.getValues();
				  break;
			  }
		}
	   List<PriceGrid> newPriceGrid = new ArrayList<>();
	   String optionVal = listOfOptionVal.stream().map(OptionValue::getValue).collect(Collectors.joining(","));
	   String imprintMethodVal = imprintMethods.stream().map(ImprintMethod::getAlias).collect(Collectors.joining());
	   String[] optionVals = CommonUtility.getValuesOfArray(optionVal, ",");
	   String option1 = optionVals[0];
	   String option2 = optionVals[1];
	   existingPriceGrid = proGolfPriceGridParser.getBasePriceGrid("", "", "", 
			   "USD", "", true, true, "QUR grid", 
			   "", existingPriceGrid, "", "Golf Ball Model");
	   for (PriceGrid priceGrid : existingPriceGrid) {
		    if(priceGrid.getIsBasePrice() && priceGrid.getDescription().equalsIgnoreCase("decorative")){
		    	List<PriceConfiguration> listOfPriceConfig = new ArrayList<>();
		    	PriceConfiguration config1 = getOptionPriceConfiguration("Imprint Method", imprintMethodVal, "");
		    	PriceConfiguration config2 = getOptionPriceConfiguration("Product Option", option1, "Golf Ball Model");
		    	listOfPriceConfig.add(config1);
		    	listOfPriceConfig.add(config2);
		    	priceGrid.setPriceConfigurations(listOfPriceConfig);
		    	priceGrid.setDescription(option1);
		    	newPriceGrid.add(priceGrid);
		    } else if(priceGrid.getIsBasePrice() && priceGrid.getDescription().equalsIgnoreCase("QUR grid")) {
		    	List<PriceConfiguration> listOfPriceConfig = new ArrayList<>();
		    	PriceConfiguration config1 = getOptionPriceConfiguration("Imprint Method", imprintMethodVal, "");
		    	PriceConfiguration config2 = getOptionPriceConfiguration("Product Option", option2, "Golf Ball Model");
		    	listOfPriceConfig.add(config1);
		    	listOfPriceConfig.add(config2);
		    	priceGrid.setPriceConfigurations(listOfPriceConfig);
		    	priceGrid.setDescription(option2);
		    	newPriceGrid.add(priceGrid);
		    }
		    else {
		    	newPriceGrid.add(priceGrid);
		    }
	}
	  product.setPriceGrids(newPriceGrid);
	   return product;
   }
  private PriceConfiguration getOptionPriceConfiguration(String criteria,String Value,String optionName){
	  PriceConfiguration config = new PriceConfiguration();
	  config.setCriteria(criteria);
	  config.setValue(Arrays.asList(Value));
	  if(!StringUtils.isEmpty(optionName)){
		  config.setOptionName(optionName);
	  }
	  return config;
  }
  public Size getProductSizeArc(String val){
	  Size sizeObj = new Size();
	  Dimension dimentionObj = new Dimension();
	  List<Values> listOfValues = new ArrayList<>();
	  Values valuesObj = new Values();
	  List<Value> listOfValue = new ArrayList<>();
	  Value valueObj = new Value();
	  valueObj.setAttribute("Arc");
	  valueObj.setUnit("in");
	  valueObj.setValue(val);
	  /*String[] sizeVals = CommonUtility.getValuesOfArray(val, ",");
	  for (int sizeIndex = 0; sizeIndex < sizeVals.length; sizeIndex++) {
		  valueObj = new Value();
		  if(sizeIndex == 0){
			  valueObj.setAttribute("Length");
		  } else if(sizeIndex == 1){
			  valueObj.setAttribute("Width");
		  } else if(sizeIndex == 2){
			  valueObj.setAttribute("Height");
		  }
		  valueObj.setUnit("in");
		  valueObj.setValue(sizeVals[sizeIndex]);
		  listOfValue.add(valueObj);
	}*/
	  listOfValue.add(valueObj);
	  valuesObj.setValue(listOfValue);
	  listOfValues.add(valuesObj);
	  dimentionObj.setValues(listOfValues);
	 sizeObj.setDimension(dimentionObj);
	 return sizeObj;
  }
	public LookupServiceData getLookupServiceData() {
		return lookupServiceData;
	}
	public void setLookupServiceData(LookupServiceData lookupServiceData) {
		this.lookupServiceData = lookupServiceData;
	}
	public ProGolfPriceGridParser getProGolfPriceGridParser() {
		return proGolfPriceGridParser;
	}
	public void setProGolfPriceGridParser(ProGolfPriceGridParser proGolfPriceGridParser) {
		this.proGolfPriceGridParser = proGolfPriceGridParser;
	}
}
