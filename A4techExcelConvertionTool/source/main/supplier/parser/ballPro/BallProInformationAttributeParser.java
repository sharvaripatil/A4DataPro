package parser.ballPro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.TradeName;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class BallProInformationAttributeParser {
	private LookupServiceData lookupServiceData;
	private BallProPriceGridParser ballProPriceGridParser;
	private static List<String> fobPoints = null;
	
	private static Logger _LOGGER  = Logger.getLogger(BallProInformationAttributeParser.class);
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
         if(sizeVal.contains("MEDIUM")){
			sizeVal = sizeVal.replaceAll("MEDIUM", "M");
		}
         if(sizeVal.contains("large") || sizeVal.contains("LARGE")){
			sizeVal = sizeVal.replaceAll("LARGE", "L");
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
	public List<Option> getProductOption(String values,String optionType,String optionName,boolean isRequiredForOrder){
		List<Option> listOfOptions = new ArrayList<>();
		List<OptionValue> listOfOptionValue = new ArrayList<>();
		Option optionObj = new Option();
		OptionValue optionValObj = null;
		String[] optionVals = CommonUtility.getValuesOfArray(values,
				ApplicationConstants.CONST_DELIMITER_COMMA);
		for (String optionVal : optionVals) {
			optionValObj = new OptionValue();
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
			//colorName = colorName.trim();
			if(StringUtils.isEmpty(colorName)){
				continue;
			}
			colorObj = new Color();
			String colorGroup = BallProColorMapping.getColorGroup(colorName.trim());
			if (colorGroup == null) {
				if (colorName.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
					if(CommonUtility.isComboColor(colorName)){
						List<Combo> listOfCombo = null;
						String[] comboColors = CommonUtility.getValuesOfArray(colorName,
								ApplicationConstants.CONST_DELIMITER_FSLASH);
						String colorFirstName = BallProColorMapping.getColorGroup(comboColors[0].trim());
						colorObj.setName(colorFirstName == null?"Other":colorFirstName);
						int combosSize = comboColors.length;
						if (combosSize == ApplicationConstants.CONST_INT_VALUE_TWO) {
							String colorComboFirstName = BallProColorMapping.getColorGroup(comboColors[1].trim());
							colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
							listOfCombo = getColorsCombo(colorComboFirstName, ApplicationConstants.CONST_STRING_EMPTY,
									combosSize);
						} else{
							String colorComboFirstName = BallProColorMapping.getColorGroup(comboColors[1].trim());
							colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
							
							String colorComboSecondName = BallProColorMapping.getColorGroup(comboColors[2].trim());
							colorComboSecondName = colorComboSecondName == null?"Other":colorComboSecondName;
							listOfCombo = getColorsCombo(colorComboFirstName,colorComboSecondName, combosSize);
						}
						String alias = colorName.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH, "-");
						colorObj.setAlias(alias);
						colorObj.setCombos(listOfCombo);
					} else {
						String[] comboColors = CommonUtility.getValuesOfArray(colorName,
								ApplicationConstants.CONST_DELIMITER_FSLASH);
						String mainColorGroup = BallProColorMapping.getColorGroup(comboColors[0].trim());
						if(mainColorGroup != null){
							colorObj.setName(mainColorGroup);
							colorObj.setAlias(colorName);
						} else {
							colorObj.setName(ApplicationConstants.CONST_VALUE_TYPE_OTHER);
							colorObj.setAlias(colorName);
						}
					}		
				} else if(colorName.equalsIgnoreCase("Blue Black Herring")){// this condition used for Ball Pro supplier
					//Medium Blue:Combo:Medium Black:Secondary=Blue Black Herring
					List<Combo> comboColorList = getColorsCombo("Medium Black", "", 2);
					colorObj.setCombos(comboColorList);
					colorObj.setName("Medium Blue");
					colorObj.setAlias(colorName);
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
		try{
		    if(value.contains("Imprint Area") || value.contains("Other")){
			value = value.substring(value.indexOf("|") + 1);
			if(value.contains("Imprint Area")){
				value = value.substring(value.indexOf(":") + 1);
			}
			productConfig = getImprintLocationAndSize(value, productConfig);	
		} else if(value.contains("Pik Color")){
			value = value.substring(value.indexOf("|") + 1);
			List<Option> options = getProductOption(value, "Product", "Pik Color", true);
			productConfig.setOptions(options);
		} else if(value.contains("Rush") || value.contains("Rus")){
			RushTime rushTime = getProductRushTime(value);
			productConfig.setRushTime(rushTime);
		} else if(value.contains("Bag Color")){
			//color value process not required because same values already present in color column
		} else if(value.contains("Additional Color Charges")){
			// no values append in additional color charges
		} else if(value.contains("Repeat Setup")){
			String[] vals = value.split("\\|");
			String imprintMethodName = vals[0];
			if(imprintMethodName.equalsIgnoreCase("Repeat setup charge")){
				imprintMethodName = "Printed";
			} 
			String[] priceVals = value.split(ApplicationConstants.CONST_DELIMITER_HYPHEN);
			String disCountCode = CommonUtility.extractValueSpecialCharacter("(", ")", priceVals[1]);
			String priceVal = priceVals[1].replaceAll("[^0-9]", "").trim();
			priceGrids = ballProPriceGridParser.getUpchargePriceGrid("1", priceVal, disCountCode, "Imprint Method", false, "USD", imprintMethodName,
					"Re-order Charge", "Other", 1, priceGrids, "", "");
		    }
		 } catch(Exception exce){
			  _LOGGER.error("Error in featues Column Parser: "+exce.getMessage());
		  }
		existingProduct.setProductConfigurations(productConfig);
		existingProduct.setDistributorOnlyComments(distributorComments);
		existingProduct.setPriceGrids(priceGrids);
		return existingProduct;
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
			if(locationVal.contains("â€�")){
				locationVal = locationVal.replaceAll("â€�", "");
			}
			imprintLocationObj = new ImprintLocation();
			if(locationVal.contains("?")){
				locationVal = locationVal.replaceAll("\\?", "").trim();
			}
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
	public RushTime getProductRushTime(String value){
		value = value.substring(value.indexOf("|") + 1);
		RushTime rushTime = new RushTime();
		List<RushTimeValue> listOfRushTimeValues = new ArrayList<>();
		RushTimeValue rushTimeValueObj = new RushTimeValue();
		rushTime.setAvailable(ApplicationConstants.CONST_BOOLEAN_TRUE);
		
		rushTimeValueObj.setBusinessDays("");
		rushTimeValueObj.setDetails(value);
		listOfRushTimeValues.add(rushTimeValueObj);
		rushTime.setRushTimeValues(listOfRushTimeValues);
		return rushTime;
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
		if(!CollectionUtils.isEmpty(existingProduct.getCategories())){
			newProduct.setCategories(existingProduct.getCategories());
		}
		if(!CollectionUtils.isEmpty(existingProduct.getProductKeywords())){
			newProduct.setProductKeywords(existingProduct.getProductKeywords());
		}
		if(!CollectionUtils.isEmpty(oldConfiguration.getMaterials())){
			newConfiguration.setMaterials(oldConfiguration.getMaterials());
		}
		if(!CollectionUtils.isEmpty(oldConfiguration.getOrigins())){
			newConfiguration.setOrigins(oldConfiguration.getOrigins());
		}
		if(!CollectionUtils.isEmpty(oldConfiguration.getProductionTime())){
			newConfiguration.setProductionTime(oldConfiguration.getProductionTime());
		}
		if(oldConfiguration.getRushTime() != null){
			newConfiguration.setRushTime(oldConfiguration.getRushTime());
		}
		newProduct.setProductConfigurations(newConfiguration);
		return newProduct;
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
	   existingPriceGrid = ballProPriceGridParser.getBasePriceGrid("", "", "", 
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
  public ProductConfigurations getImprintLocationAndSize(String imprintVal,ProductConfigurations productConfig){
	  List<ImprintSize> imprintSizeList = new ArrayList<>();
	  List<ImprintLocation> imprintLocList = new ArrayList<>();
	  ImprintSize imprintSize = null;
	  ImprintLocation imprintLoc = null;
	  String[] values = CommonUtility.getValuesOfArray(imprintVal, ApplicationConstants.CONST_STRING_COMMA_SEP);
	  for (String imprintVals : values) {
		  imprintSize = new ImprintSize();
		  imprintLoc = new ImprintLocation();
		  imprintVals = imprintVals.replaceAll("″", "\"");
		  if(imprintVals.contains(":")){
			  String[] vals = CommonUtility.getValuesOfArray(imprintVals,ApplicationConstants.CONST_DELIMITER_COLON);
			  imprintLoc.setValue(vals[0]);
			  imprintSize.setValue(vals[1]);
		  } else if(imprintVals.contains("-")){
			  String[] vals = CommonUtility.getValuesOfArray(imprintVals,"-");
			  if(!StringUtils.isEmpty(vals[ApplicationConstants.CONST_NUMBER_ZERO].trim())){
				  imprintLoc.setValue(vals[ApplicationConstants.CONST_NUMBER_ZERO].trim());  
			  }
			  imprintSize.setValue(vals[ApplicationConstants.CONST_INT_VALUE_ONE].trim());
		  } else {
			  imprintSize.setValue(imprintVals);
		  }
		  if(imprintLoc != null && !StringUtils.isEmpty(imprintLoc.getValue())){
	         imprintLocList.add(imprintLoc);
		  }
		  imprintSizeList.add(imprintSize);
	}
	  productConfig.setImprintLocation(imprintLocList);
	  productConfig.setImprintSize(imprintSizeList);
	  return productConfig;
  }
	public LookupServiceData getLookupServiceData() {
		return lookupServiceData;
	}
	public void setLookupServiceData(LookupServiceData lookupServiceData) {
		this.lookupServiceData = lookupServiceData;
	}
	public BallProPriceGridParser getBallProPriceGridParser() {
		return ballProPriceGridParser;
	}
	public void setBallProPriceGridParser(BallProPriceGridParser ballProPriceGridParser) {
		this.ballProPriceGridParser = ballProPriceGridParser;
	}
}
