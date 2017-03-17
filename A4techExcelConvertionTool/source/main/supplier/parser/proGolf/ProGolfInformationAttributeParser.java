package parser.proGolf;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

public class ProGolfInformationAttributeParser {
	private LookupServiceData lookupServiceData ;
	private static List<String> fobPoints = null;
	
	public List<TradeName> getTradeNames(String value){
		List<TradeName> listOfTradeNames = new ArrayList<>();
		TradeName tradeNameObj  = new TradeName();
		tradeNameObj.setName(value);
		listOfTradeNames.add(tradeNameObj);
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
		String[] sizes = CommonUtility.getValuesOfArray(sizeVals, ApplicationConstants.CONST_DELIMITER_SPLITTING_PIPE);
		for (String sizeVal : sizes) {
			valueObj = new Value();
			valueObj.setValue(sizeVal);
			listOfValue.add(valueObj);
		}
		return listOfValue;
	}
	public List<ImprintSize> getImprintSizes(String value){
		List<ImprintSize> listOfImprintSize = new ArrayList<>();
		ImprintSize imprintSize = null;
		String[] imprSizes = CommonUtility.getValuesOfArray(value, ApplicationConstants.CONST_DELIMITER_SPLITTING_PIPE);
		for (String imprSize : imprSizes) {
			 imprintSize = new ImprintSize();
			 imprintSize.setValue(imprSize);
			 listOfImprintSize.add(imprintSize);
		}
		return listOfImprintSize;
	}
	public List<Option> getProductOption(String values,String optionType,String optionName,boolean isRequiredForOrder,
			      List<Option> listOfOptions){
		List<OptionValue> listOfOptionValue = new ArrayList<>();
		Option optionObj = new Option();
		OptionValue optionValObj = null;
		String[] optionVals = CommonUtility.getValuesOfArray(values,
				ApplicationConstants.CONST_DELIMITER_SPLITTING_PIPE);
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
		String[] colors = CommonUtility.getValuesOfArray(color, ApplicationConstants.CONST_DELIMITER_SPLITTING_PIPE);
		for (String colorName : colors) {
			colorName = colorName.trim();
			if(StringUtils.isEmpty(colorName)){
				continue;
			}
			colorObj = new Color();
			String colorGroup = ProGolfColorMapping.getColorGroup(colorName);
			if (colorGroup == null) {
				if (colorName.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
					List<Combo> listOfCombo = null;
					String[] comboColors = CommonUtility.getValuesOfArray(colorName,
							ApplicationConstants.CONST_DELIMITER_FSLASH);
					colorObj.setName(comboColors[0].trim());
					int combosSize = comboColors.length;
					if (combosSize == ApplicationConstants.CONST_INT_VALUE_TWO) {
						listOfCombo = getColorsCombo(comboColors[1].trim(), ApplicationConstants.CONST_STRING_EMPTY,
								combosSize);
					} else{
						listOfCombo = getColorsCombo(comboColors[1].trim(), comboColors[2].trim(), combosSize);
					}
					String alias = colorName.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH, "-");
					colorObj.setAlias(alias);
					colorObj.setCombos(listOfCombo);
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
		String[] values = null;
		if(value.contains("Returns") || value.contains("Not Returnable") || value.contains("not returnable")){
			value = value.replaceAll(ApplicationConstants.CONST_DELIMITER_SPLITTING_PIPE, "");
			distributorComments = CommonUtility.appendStrings(distributorComments, value, " ");
		} else if(value.contains("Lead Time")){
			if(value.equalsIgnoreCase("Lead Time:|Lead Time: Stock Products 1-2 Business Days                      Logoed Products 2-4 Weeks")){
				value = "Lead Time:|Lead Time: Stock Products 1-2 Business Days,Logoed Products 2-4 Weeks";
			}
			value = removeTrailData(value, ".*:");
			if(value.contains(";")){
				values = CommonUtility.getValuesOfArray(value, ApplicationConstants.CONST_DELIMITER_SEMICOLON);
			} else{
				values = CommonUtility.getValuesOfArray(value, ApplicationConstants.CONST_STRING_COMMA_SEP);
			}
			List<ProductionTime> listOfProductionTime = getProductionTime(values,productConfig.getProductionTime());
			productConfig.setProductionTime(listOfProductionTime);
		} else if(value.contains("Imprint Area")){
			value = value.substring(value.indexOf("|") + 1);
			if(value.contains("Imprint Area")){
				value = value.substring(value.indexOf(":") + 1);
			}
			if(value.contains(";")){
				values = CommonUtility.getValuesOfArray(value, ApplicationConstants.CONST_DELIMITER_SEMICOLON);
			} else{
				values = CommonUtility.getValuesOfArray(value, ApplicationConstants.CONST_STRING_COMMA_SEP);
			}
			List<ImprintLocation> listOfImprintLoc = getImprintLocations(values,productConfig.getImprintLocation());
			productConfig.setImprintLocation(listOfImprintLoc);
		} else if(value.contains("Minimum")){
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
		} else if(value.contains("Available")){
			// no need create availability as per client feedback
		} else if(value.contains("Set up Fee")){
		  // set up charge values already present in product pricing sheet	
		}else {
			distributorComments = CommonUtility.appendStrings(distributorComments, value, " ");
		}
		
		existingProduct.setDistributorOnlyComments(distributorComments);
		return null;
	}
	private String removeTrailData(String data,String specialCharacters){
		data = data.replaceFirst(specialCharacters, "");//".*:"
		if(data.contains("|")){
			 data = data.substring(data.indexOf("|") + 1);
		}
		return data;
	}
	private List<ProductionTime> getProductionTime(String[] productionTimeValues,List<ProductionTime> existingProductionTime){
		ProductionTime productionTimeObj = null;
		for (String productionTime : productionTimeValues) {
			productionTimeObj = new ProductionTime();
			String details = productionTime;
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
			productionTimeObj.setBusinessDays(productionTime);
			productionTimeObj.setDetails(details);
			existingProductionTime.add(productionTimeObj);
		}
		return existingProductionTime;
	}
	public List<ProductionTime> getProductionTime(String productionTimeValues,String details,List<ProductionTime> existingProductionTime){
		ProductionTime productionTimeObj = null;
			productionTimeObj = new ProductionTime();
			if(details.contains("Weeks") || details.contains("weeks")){
				productionTimeValues = CommonUtility.convertProductionTimeWeekIntoDays(productionTimeValues);
			}
			productionTimeObj.setBusinessDays(productionTimeValues);
			productionTimeObj.setDetails(details);
			existingProductionTime.add(productionTimeObj);
		return existingProductionTime;
	}
	public List<ImprintMethod> getProductImprintMethods(String[] imprintMethodVals,List<ImprintMethod> existingImprintMethods){
		ImprintMethod imprintMethodObj = null;
		String imprintMethodType = "";
		for (String imprintMethodVal : imprintMethodVals) {
			if(imprintMethodVal.equalsIgnoreCase("Stock Product")){
				continue;
			} else if(imprintMethodVal.equalsIgnoreCase("N/A")){
				continue;
			} else if(isImprintMethodAvailable(imprintMethodVal, existingImprintMethods)){
				continue;
			} else{
				
			}
			imprintMethodObj = new ImprintMethod();
			  if(imprintMethodVal.equals("Unimprinted")){
				  imprintMethodType = "UNIMPRINTED";
				  imprintMethodObj.setAlias("UNIMPRINTED");
			  } else if(imprintMethodVal.equalsIgnoreCase("Embroidery")){
				  imprintMethodType = "Embroidered";
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
		for (String locationVal : imprintLocValues) {
			if(locationVal.equalsIgnoreCase("Not Available as Logo Product") || locationVal.equalsIgnoreCase("N/A")){
				continue;
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
		valueObj.setUnit(unit);
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
		weightObj.setUnit(unit);
		weightObj.setValue(weightVal);
		listOfWeight.add(weightObj);
		return listOfWeight;
	}
	private Dimensions getShippingDimension(String length,String width,String height,String unit){
		Dimensions dimensionsObj = new Dimensions();
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
		Dimension existingDimension = existingSizes.getDimension();
		List<Values> existingValues = existingDimension.getValues();
		
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
		}
		rushTimeValueObj.setBusinessDays(rushTime);
		rushTimeValueObj.setDetails("");
		listOfRushTimeValues.add(rushTimeValueObj);
		existingRushTime.setRushTimeValues(listOfRushTimeValues);
		return existingRushTime;
	}
	
	public LookupServiceData getLookupServiceData() {
		return lookupServiceData;
	}
	public void setLookupServiceData(LookupServiceData lookupServiceData) {
		this.lookupServiceData = lookupServiceData;
	}
}
