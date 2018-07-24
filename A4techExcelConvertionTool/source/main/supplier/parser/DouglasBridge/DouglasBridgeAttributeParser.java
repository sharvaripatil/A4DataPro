package parser.DouglasBridge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Weight;
import com.a4tech.sage.product.util.LookupData;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class DouglasBridgeAttributeParser {
	private LookupServiceData lookupServiceDataObj;
	private DouglasBridgePriceGridParser douglasBridgePriceGridParser;
	private static List<String> lookupFobPoints = null;
	List<String> lineNames = Arrays.asList("Douglasbridge", "MagiCatcher", "Simply Smashing", "Simply Smashing Canada",
			"Sniftypak");
	public Product keepExistingProductData(Product existingProduct){
		ProductConfigurations oldProductConfig = existingProduct.getProductConfigurations();
		Product newProduct = new Product();
		ProductConfigurations newProductConfig = new ProductConfigurations();
		if(!CollectionUtils.isEmpty(existingProduct.getImages())){
			newProduct.setImages(existingProduct.getImages());
		}
		if(!CollectionUtils.isEmpty(existingProduct.getCategories())){
			newProduct.setCategories(existingProduct.getCategories());
		}
		if(!CollectionUtils.isEmpty(existingProduct.getLineNames())){
			newProduct.setLineNames(existingProduct.getLineNames());
		}
		 if(!CollectionUtils.isEmpty(oldProductConfig.getThemes())){
			 newProductConfig.setThemes(oldProductConfig.getThemes());
				List<Theme> themeList = oldProductConfig.getThemes().stream().map(themeVal ->{
					themeVal.setName(themeVal.getName());
					if(themeVal.getName().equalsIgnoreCase("ECO FRIENDLY")){
						themeVal.setName("Eco & Environmentally Friendly");
					}
					return themeVal;
				}).collect(Collectors.toList()); 
				newProductConfig.setThemes(themeList);
			}
		newProduct.setProductConfigurations(newProductConfig);
		return newProduct;
	}
	public Size getProductSize(String dimensionValue, String dimensionUnits,
			String dimensionType) {
         Size sizeObj = new Size();
         Dimension dimensionObj = new Dimension();
		String dimensionValueArr[] = dimensionValue
				.split(ApplicationConstants.CONST_DIMENSION_SPLITTER);
		String dimensionUnitsArr[] = dimensionUnits
				.split(ApplicationConstants.CONST_DIMENSION_SPLITTER);
		String dimensionTypeArr[] = dimensionType
				.split(ApplicationConstants.CONST_DIMENSION_SPLITTER);
		ArrayList<Value> valueList = new ArrayList<Value>();
		List<Values> valueslist = new ArrayList<Values>();
		Values valuesObj = new Values();
		Value valueObj = null;
		for (int dimensionIndex = 0; dimensionIndex < dimensionValueArr.length; dimensionIndex++) {
			valueObj = new Value();
			valueObj.setValue(dimensionValueArr[dimensionIndex]);
			valueObj.setUnit(LookupData.Dimension1Units.get(dimensionUnitsArr[dimensionIndex]));
			valueObj.setAttribute(LookupData.Dimension1Type.get(dimensionTypeArr[dimensionIndex]));
			valueList.add(valueObj);
		}
		valuesObj.setValue(valueList);
		valueslist.add(valuesObj);
		dimensionObj.setValues(valueslist);
		sizeObj.setDimension(dimensionObj);
		return sizeObj;
	}
	
	public List<ImprintSize> getimprintsize(StringBuilder firstImprintSize) {
		String ImprintSizeValue=firstImprintSize.toString().replaceAll("xnull","");
		ImprintSizeValue=ImprintSizeValue.replace("null", "");
	    List<ImprintSize> imprintSizeList =new ArrayList<ImprintSize>();
	    ImprintSize impsizeobj;
		String ImprintsizeArr[]=ImprintSizeValue.split(",");
	   for (String Value : ImprintsizeArr) {
		   impsizeobj=new ImprintSize();
		   impsizeobj.setValue(Value);
		   imprintSizeList.add(impsizeobj);
	      }		
		return imprintSizeList;
	}
	public List<Packaging> getPackageValues(String packageValues){
		List<Packaging> listOfPackage = new ArrayList<Packaging>();
		Packaging packaging = null;
			String[] packValues = packageValues.split(ApplicationConstants.CONST_DELIMITER_COMMA);
			for (String packageVal : packValues) {
				packaging = new Packaging();
				if(packageVal.contains("Bulk")){
					packageVal = "Bulk";
				} else if(packageVal.equalsIgnoreCase("Individually poly bagged")){
					packageVal = "Individual Poly Bag";
				}
			   packaging.setName(packageVal);
			   listOfPackage.add(packaging);
			}
		return listOfPackage;
	}
	public ShippingEstimate getShippingEstimateValues(String shippingDimension, String weight,String numberOfItems){
		ShippingEstimate shippingEstimate = new ShippingEstimate();
		Dimensions dimension = null;
		List<NumberOfItems> numberOfItem = null;
        List<Weight> listOfWeightObj = null;
        Weight  weightObj = null;
        NumberOfItems numberOfItemsObj = null;
       if(!StringUtils.isEmpty(shippingDimension)){
    	   String[] dimensions = CommonUtility.getValuesOfArray(shippingDimension, ApplicationConstants.CONST_DELIMITER_COMMA);
    	   dimension = new Dimensions();
    	   dimension.setLength(dimensions[0]);
    	   dimension.setWidth(dimensions[1]);
    	   dimension.setHeight(dimensions[2]);
    	   dimension.setLengthUnit(ApplicationConstants.CONST_STRING_INCHES);
    	   dimension.setWidthUnit(ApplicationConstants.CONST_STRING_INCHES);
    	   dimension.setHeightUnit(ApplicationConstants.CONST_STRING_INCHES);
       }
       if(!weight.equals(ApplicationConstants.CONST_STRING_ZERO)){
    	   listOfWeightObj = new ArrayList<Weight>();
    	   weightObj = new Weight();
    	   weightObj.setValue(weight);
    	   weightObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_WEIGHT);
    	   listOfWeightObj.add(weightObj);
       }
       if(!numberOfItems.equals(ApplicationConstants.CONST_STRING_ZERO)){
    	   numberOfItem = new ArrayList<NumberOfItems>();
    	   numberOfItemsObj = new NumberOfItems();
    	   numberOfItemsObj.setValue(numberOfItems);
    	   numberOfItemsObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_NUMBER_UNIT_CARTON);
    	   numberOfItem.add(numberOfItemsObj);
       }
       if(dimension != null){
    	   shippingEstimate.setDimensions(dimension);
       }
       if(numberOfItem != null){
    	   shippingEstimate.setNumberOfItems(numberOfItem);
       }
       if(listOfWeightObj != null){
    	   shippingEstimate.setWeight(listOfWeightObj);
       }

       return shippingEstimate;
	}

	
	public List<Origin> getOriginValues(String originValue){
		List<Origin> listOfOrigin   = new ArrayList<Origin>();
		Origin origin = new Origin();
		if(originValue.equalsIgnoreCase(ApplicationConstants.CONST_STRING_COUNTRY_CODE_CH)){
			originValue = ApplicationConstants.CONST_STRING_COUNTRY_NAME_CHINA;
		}else if(originValue.equalsIgnoreCase(ApplicationConstants.CONST_STRING_COUNTRY_CODE_US)){
			originValue = ApplicationConstants.CONST_STRING_COUNTRY_NAME_USA;
		}else if(originValue.equalsIgnoreCase("CN")){
			originValue = ApplicationConstants.CONST_STRING_COUNTRY_NAME_CHINA;
		}else if(originValue.equalsIgnoreCase("CA")){
			originValue = "CANADA";
		}
		origin.setName(originValue);
		listOfOrigin.add(origin);
		return listOfOrigin;
	}
	
	public String  getCountryCodeConvertName(String countryCode){
		String countryName = null;
		if(countryCode.equalsIgnoreCase(ApplicationConstants.CONST_STRING_COUNTRY_CODE_CH)){
			countryName = ApplicationConstants.CONST_STRING_COUNTRY_NAME_CHINA;
		}else if(countryCode.equalsIgnoreCase(ApplicationConstants.CONST_STRING_COUNTRY_CODE_US)){
			countryName = ApplicationConstants.CONST_STRING_COUNTRY_NAME_USA;
		}else if(countryCode.equalsIgnoreCase("CN")){
			countryName = ApplicationConstants.CONST_STRING_COUNTRY_NAME_CHINA;
		}
		else if(countryCode.equalsIgnoreCase("CA")){
			countryName = "CANADA";
		}else{
			
		}
		return countryName;
	}
	public List<ImprintMethod> getImprintMethodValues(String imprintMethodValue){
		ImprintMethod imprMethod = null;
		List<ImprintMethod> imprintMethodList = new ArrayList<ImprintMethod>();
		String[] imprintMethodVals = imprintMethodValue.split(",");
		for (String imprintMethodName : imprintMethodVals) {
			imprMethod = new ImprintMethod();
			String imprintMethodType = "";
			   if(imprintMethodName.equalsIgnoreCase("Pad printed") || imprintMethodName.equalsIgnoreCase("Pad printing")){
				   imprintMethodType = "Pad Print";
			   } else if(imprintMethodName.equalsIgnoreCase("Screen printed")){
				   imprintMethodType = "Silkscreen";
			   }
			   imprMethod.setAlias(imprintMethodName);
			   imprMethod.setType(imprintMethodType);
			   imprintMethodList.add(imprMethod);
		}
		return imprintMethodList;
		}
	public List<ImprintMethod> getImprintMethodValues(List<ImprintMethod> imprintMethodList,String imprintMethodVal){
		ImprintMethod imprintMethod = new ImprintMethod();
		imprintMethod.setType(imprintMethodVal);
		imprintMethod.setAlias(imprintMethodVal);
		imprintMethodList.add(imprintMethod);
		return imprintMethodList;
	}
	public Option getImprintOption2(String ImprintOptionValue) {
		List<OptionValue> valuesList = new ArrayList<OptionValue>();
		OptionValue optionValueObj=new OptionValue();
		Option optionObj=new Option();
		
		optionObj.setOptionType("Imprint");
		optionObj.setName("Line Charge Option");
		optionValueObj.setValue("Line Charge");
		valuesList.add(optionValueObj);
		optionObj.setValues(valuesList);
		
		return optionObj;
	}
	public List<String> getImprintValue(String value){
		List<String> imprintLookUpValue = lookupServiceDataObj.getImprintMethods();
		List<String> finalMaterialValues = imprintLookUpValue.stream()
				                                  .filter(impntName -> value.contains(impntName))
				                                  .collect(Collectors.toList());
                                                 
				
		return finalMaterialValues;	
	}
	public List<Color> getProductColor(String color){
		List<Color> listOfProductColor = new ArrayList<>();
		Color colorObj = null;
		String[] colors = CommonUtility.getValuesOfArray(color, ApplicationConstants.CONST_DELIMITER_COMMA);
		for (String colorName : colors) {
			colorObj = new Color();
			colorName = colorName.trim();
			
			if(colorName.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)){
				String[] clrs = CommonUtility.getValuesOfArray(color, ApplicationConstants.CONST_DELIMITER_COMMA);
				if(clrs.length > 3){
					colorObj.setName("Multi color");
					colorObj.setAlias("Multi color");
				} else{
					colorObj = getColorCombo(colorName,ApplicationConstants.CONST_DELIMITER_FSLASH);					
				}
			}else {
				String colorGroup = DouglasBridgeColorMapping.getColorGroup(colorName);
				colorObj.setName(colorGroup);
				colorObj.setAlias(colorName);
			}
		   listOfProductColor.add(colorObj);
		}
		return listOfProductColor;
	}
	private Color getColorCombo(String comboVal,String colorDelimiter){
		Color colorObj = new Color();
		List<Combo> listOfComos = new ArrayList<>();
		Combo comboObj = new Combo();
		Combo comboObj1 = null;
		String[] comboColors = CommonUtility.getValuesOfArray(comboVal,colorDelimiter);
		colorObj.setName(
				DouglasBridgeColorMapping.getColorGroup(comboColors[ApplicationConstants.CONST_NUMBER_ZERO]));
		comboObj.setName(
				DouglasBridgeColorMapping.getColorGroup(comboColors[ApplicationConstants.CONST_INT_VALUE_ONE]));
		comboObj.setType(ApplicationConstants.CONST_STRING_SECONDARY);
		listOfComos.add(comboObj);
		if(comboColors.length == 3){
			comboObj1 = new Combo();
			comboObj1.setName(
					DouglasBridgeColorMapping.getColorGroup(comboColors[ApplicationConstants.CONST_INT_VALUE_TWO]));
			comboObj1.setType(ApplicationConstants.CONST_STRING_TRIM);
			listOfComos.add(comboObj1);
		}
		colorObj.setAlias(comboVal.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH,
				ApplicationConstants.CONST_DELIMITER_HYPHEN));
		colorObj.setCombos(listOfComos);
		return colorObj;
	}
	public List<Theme> getProductTheme(String themeVal){
		List<Theme> listOfTheme = new ArrayList<>();
		Theme themeObj = null;
		String[] themes = CommonUtility.getValuesOfArray(themeVal, ApplicationConstants.CONST_DELIMITER_COMMA);
		themes = CommonUtility.removeDuplicateValues(themes);
		for (String themeName : themes) {
			if(lookupServiceDataObj.isTheme(themeName.toUpperCase())){
				themeObj = new Theme();
				themeObj.setName(themeName);
				listOfTheme.add(themeObj);
			}
		}
		return listOfTheme;
	}
	public List<ProductionTime> getProductionTime(String startBusinessDay,String endBusinessDay){
		List<ProductionTime> productionTimeList = new ArrayList<>();
		ProductionTime productionTime = new ProductionTime();
		if (!startBusinessDay.equals("0") && CommonUtility.isValidBusinessDays(Integer.parseInt(startBusinessDay),
				Integer.parseInt(endBusinessDay))) {
			String prodTimeTotal="";
			prodTimeTotal=prodTimeTotal.concat(startBusinessDay).concat("-").concat(endBusinessDay);
			productionTime.setBusinessDays(prodTimeTotal);
			productionTime.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
		} else {
			productionTime.setBusinessDays(endBusinessDay);
			productionTime.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
		}
		productionTimeList.add(productionTime);
		return productionTimeList;
	}
	public RushTime getProductRushTime(String startBusinessDay,String endBusinessDay){
		RushTime rushTimeObj = new RushTime();
		rushTimeObj.setAvailable(ApplicationConstants.CONST_BOOLEAN_TRUE);
		List<RushTimeValue> rushTimeValueList = new ArrayList<>();
		RushTimeValue rushtimeValue = new RushTimeValue();
		if(CommonUtility.isValidBusinessDays(Integer.parseInt(startBusinessDay), Integer.parseInt(endBusinessDay))){
			StringBuilder rushBusinessDays= new StringBuilder();
			rushBusinessDays.append(startBusinessDay).append("-").append(endBusinessDay);
			rushtimeValue.setBusinessDays(rushBusinessDays.toString());
			rushtimeValue.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
		} else {
			rushtimeValue.setBusinessDays(endBusinessDay);
			rushtimeValue.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
		}
		rushTimeValueList.add(rushtimeValue);
		rushTimeObj.setRushTimeValues(rushTimeValueList);
		return rushTimeObj;
	}
	public RushTime getProductRushTime(RushTime rushTime){
		if(rushTime == null){
			 rushTime = new RushTime();
			 rushTime.setAvailable(ApplicationConstants.CONST_BOOLEAN_TRUE);
			List<RushTimeValue> rushTimeValueList = new ArrayList<>();
			RushTimeValue rushtimeValue = new RushTimeValue();
			rushTimeValueList.add(rushtimeValue);
			rushTime.setRushTimeValues(rushTimeValueList);	
		}
		
		return rushTime;
	}
	public List<PriceGrid> getImprintMethodUpcharges(Map<String, String> upchargeValues,List<ImprintMethod> imprintMethodList,List<PriceGrid> existingPriceGrid){
		String imprintMethods = imprintMethodList.stream().map(ImprintMethod::getAlias)
															.collect(Collectors.joining(","));
		for (Map.Entry<String, String>  values: upchargeValues.entrySet()) {
			 String upchargeType =  values.getKey();
			 String[] upchareVal = values.getValue().split("_");
			 String priceVal = upchareVal[0];
			 String disCount = upchareVal[1];
			 String upChargeTypeVal = "";
			 String upchargeUsageType = "Other";
			 String serviceCharge = "Required";
			 if(upchargeType.equalsIgnoreCase("setupCharge")){
				 upChargeTypeVal = "Set-up Charge";upchargeUsageType="Per Order";
			 } else if(upchargeType.equalsIgnoreCase("screenCharge")){
				 upChargeTypeVal = "Screen Charge";
			 } else if(upchargeType.equalsIgnoreCase("plateCharge")){
				 upChargeTypeVal = "Plate Charge";
			 } else if(upchargeType.equalsIgnoreCase("diaCharge")){
				 upChargeTypeVal = "Die Charge";
			 } else if(upchargeType.equalsIgnoreCase("toolingCharge")){
				 upChargeTypeVal = "Tooling Charge";
			 } else if(upchargeType.equalsIgnoreCase("repeateCharge")){
				 upChargeTypeVal = "Re-order Charge";upchargeUsageType="Per Order";
				 serviceCharge = "Optional";
			 }
			existingPriceGrid = douglasBridgePriceGridParser.getUpchargePriceGrid("1", priceVal, disCount, "Imprint method", "n",
					"USD", imprintMethods, upChargeTypeVal, upchargeUsageType,serviceCharge, 1, existingPriceGrid);
		}
		return existingPriceGrid;
	}
	public List<PriceGrid> getAdditionalColorUpcharge(String discountCode,String prices,List<PriceGrid> existingPriceGrid,String upchargeType,String qty){
	   String disCountCode = getAdditionalColorDiscountCode(discountCode);
	   existingPriceGrid = douglasBridgePriceGridParser.getUpchargePriceGrid(qty, prices, disCountCode, "Additional Colors", "n",
				"USD", "Additional Color",upchargeType, "Other","Required", 1, existingPriceGrid);
		return existingPriceGrid;
	}
	private String getAdditionalColorDiscountCode(String value){
		 int discountCodeLength = value.length();
		 if(discountCodeLength > ApplicationConstants.CONST_INT_VALUE_ONE){
			  value = value.substring(ApplicationConstants.CONST_NUMBER_ZERO , ApplicationConstants.CONST_INT_VALUE_ONE);
		 } else {
			 return value;
		 }
		return value;
	}
	public List<AdditionalColor> getAdditionalColor(String colorValue){
		List<AdditionalColor> additionalColorList = new ArrayList<>();
		AdditionalColor additionalColorObj = new AdditionalColor();
		additionalColorObj.setName(colorValue);
		additionalColorList.add(additionalColorObj);
		return additionalColorList;
	}
	public List<String> getProductKeywords(String keyword){
		String[] keywords = CommonUtility.getValuesOfArray(keyword, ApplicationConstants.CONST_STRING_COMMA_SEP);
		//Stream<String> keys = Arrays.stream(CommonUtility.getValuesOfArray(keyword, ApplicationConstants.CONST_STRING_COMMA_SEP));
		/*List<String> keyWordList = Arrays
				.stream(CommonUtility.getValuesOfArray(keyword, ApplicationConstants.CONST_STRING_COMMA_SEP))
				.map(key -> key).distinct().limit(30).collect(Collectors.toList());*/
		keywords = CommonUtility.removeDuplicateValues(keywords);
		List<String> productKeyWords = new ArrayList<>();
		for (String keywordName : keywords) {
			if(keywordName.contains("®")){
				keywordName = keywordName.replaceAll("®", "").trim();
			}
		   productKeyWords.add(keywordName.trim());	
		}
		return productKeyWords;
	}
	public List<FOBPoint> getFobPoint(final String  value,String authToken,String environment){
		List<FOBPoint> listOfFobPoint = new ArrayList<>();
		if(lookupFobPoints == null){
			lookupFobPoints = lookupServiceDataObj.getFobPoints(authToken,environment);
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
	public String removeLineNames(String value){
		for (String lineName : lineNames) {
			value = CommonUtility.removeSpecificWord(value, lineName);
		}
		return value;
	}
	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}
	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}
	public DouglasBridgePriceGridParser getDouglasBridgePriceGridParser() {
		return douglasBridgePriceGridParser;
	}
	public void setDouglasBridgePriceGridParser(DouglasBridgePriceGridParser douglasBridgePriceGridParser) {
		this.douglasBridgePriceGridParser = douglasBridgePriceGridParser;
	}
}




