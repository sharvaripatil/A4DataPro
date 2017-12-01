package parser.headWear;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Dimensions;
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

public class HeadWearAttributeParser {
	private LookupServiceData lookupServiceDataObj;
	private HeadWearPriceGridParser headWearPriceGridParser;

	public Product keepExistingProductData(Product existingProduct){
		  Product newProduct = new Product();
		  ProductConfigurations newConfig = new ProductConfigurations();
		  if(!CollectionUtils.isEmpty(existingProduct.getImages())){
			  newProduct.setImages(existingProduct.getImages());
		  }
	    if(!CollectionUtils.isEmpty(existingProduct.getCategories())){
	    	newProduct.setCategories(existingProduct.getCategories());
	    }
	    if(!StringUtils.isEmpty(existingProduct.getSummary())){
	    	newProduct.setSummary(existingProduct.getSummary());
	    }
		  newProduct.setProductConfigurations(newConfig);
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
				if(packageVal.equalsIgnoreCase("Polybag") || packageVal.equalsIgnoreCase("Polybagged")){
					packageVal = "Poly Bag";
				} else if(packageVal.equalsIgnoreCase("Bulk.")){
					packageVal = "Bulk";
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
	public List<ImprintMethod> getImprintMethodValues(String imprintMethodValue,List<ImprintMethod> existingImprMethodList){
		ImprintMethod imprMethod = new ImprintMethod();
		String imprMethodGroup = "";
		 if(imprintMethodValue.equalsIgnoreCase("Embroidery")){
			 imprMethodGroup = "Embroidered";
		 } else {
			 imprMethodGroup = imprintMethodValue;
		 }
			   imprMethod.setAlias(imprintMethodValue);
			   imprMethod.setType(imprMethodGroup);
			   existingImprMethodList.add(imprMethod);
		return existingImprMethodList;
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
			colorName = colorName.trim();
			colorObj = new Color();
			if(colorName.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)){
				colorObj = getColorCombo(colorName);
			} else {
				String colorGroup = HeadWearColorMapping.getColorGroup(colorName.toLowerCase());
				colorObj.setName(colorGroup);
				colorObj.setAlias(colorName);
			}
		   listOfProductColor.add(colorObj);
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
				HeadWearColorMapping.getColorGroup(comboColors[ApplicationConstants.CONST_NUMBER_ZERO].toLowerCase()));
		comboObj1.setName(
				HeadWearColorMapping.getColorGroup(comboColors[ApplicationConstants.CONST_INT_VALUE_ONE].toLowerCase()));
		comboObj1.setType(ApplicationConstants.CONST_STRING_SECONDARY);
		colorObj.setAlias(comboVal.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH,
				ApplicationConstants.CONST_DELIMITER_HYPHEN));
		if(comboColors.length == 3){
			comboObj2.setName(
					HeadWearColorMapping.getColorGroup(comboColors[ApplicationConstants.CONST_INT_VALUE_TWO].toLowerCase()));
			comboObj2.setType(ApplicationConstants.CONST_STRING_TRIM);
			listOfComos.add(comboObj2);
		} 
		listOfComos.add(comboObj1);
		colorObj.setCombos(listOfComos);
		return colorObj;
	}
	public List<Theme> getProductThemes(String theme){
 	   List<Theme> themeList = new ArrayList<>();
 	   Theme themeObj = null;
 	   String[] themes = CommonUtility.getValuesOfArray(theme, ",");
 	   for (String themeVal : themes) {
 		   themeObj = new Theme();
			   themeVal = themeVal.trim();
			   	if(themeVal.equalsIgnoreCase("Sport")){
			   		themeVal = "Sports";
			   	}
				if(lookupServiceDataObj.isTheme(themeVal)){
					themeObj.setName(themeVal);
					themeList.add(themeObj);
				}
			}
 	   return themeList;
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
	public List<PriceGrid> getImprintMethodUpcharges(Map<String, String> upchargeValues,List<ImprintMethod> imprintMethodList,List<PriceGrid> existingPriceGrid){
		String imprintMethods = imprintMethodList.stream().map(ImprintMethod::getAlias)
															.collect(Collectors.joining(","));
		for (Map.Entry<String, String>  values: upchargeValues.entrySet()) {
			 String upchargeType =  values.getKey();
			 String[] upchareVal = values.getValue().split("_");
			 String priceVal = upchareVal[0];
			 String disCount = upchareVal[1];
			 String upChargeTypeVal = "";
			 if(upchargeType.equalsIgnoreCase("setupCharge")){
				 upChargeTypeVal = "Set-up Charge";
			 } else if(upchargeType.equalsIgnoreCase("screenCharge")){
				 upChargeTypeVal = "Screen Charge";
			 } else if(upchargeType.equalsIgnoreCase("plateCharge")){
				 upChargeTypeVal = "Plate Charge";
			 } else if(upchargeType.equalsIgnoreCase("diaCharge")){
				 upChargeTypeVal = "Die Charge";
			 } else if(upchargeType.equalsIgnoreCase("toolingCharge")){
				 upChargeTypeVal = "Tooling Charge";
			 } else if(upchargeType.equalsIgnoreCase("repeateCharge")){
				 
			 }
			existingPriceGrid = headWearPriceGridParser.getUpchargePriceGrid("1", priceVal, disCount, "Imprint method", "n",
					"USD", imprintMethods, upChargeTypeVal, "Other", 1, existingPriceGrid);
		}
		return existingPriceGrid;
	}
	public List<PriceGrid> getAdditionalColorUpcharge(String discountCode,String prices,List<PriceGrid> existingPriceGrid,String upchargeType){
	   String disCountCode = getAdditionalColorDiscountCode(discountCode);
	   existingPriceGrid = headWearPriceGridParser.getUpchargePriceGrid("1", prices, disCountCode, "Additional Colors", "n",
				"USD", "Additional Color",upchargeType, "Other", 1, existingPriceGrid);
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
		keywords = CommonUtility.removeDuplicateValues(keywords);
		List<String> productKeyWords = new ArrayList<>();
		for (String keywordName : keywords) {
			if(keywordName.contains("®")){
				keywordName = keywordName.replaceAll("®", "").trim();
			}
		   productKeyWords.add(keywordName);	
		}
		return productKeyWords;
	}
	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}
	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}
	public HeadWearPriceGridParser getHeadWearPriceGridParser() {
		return headWearPriceGridParser;
	}
	public void setHeadWearPriceGridParser(HeadWearPriceGridParser headWearPriceGridParser) {
		this.headWearPriceGridParser = headWearPriceGridParser;
	}
}




