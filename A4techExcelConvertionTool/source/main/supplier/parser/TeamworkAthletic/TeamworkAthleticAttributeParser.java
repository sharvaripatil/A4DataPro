package parser.TeamworkAthletic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.OtherSize;
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
import com.a4tech.product.model.Volume;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class TeamworkAthleticAttributeParser {
	private LookupServiceData lookupServiceDataObj;
	private TeamworkAthleticPriceGridParser teamWorkPriceGridParser;
	private static List<String> lookupFobPoints = null;
	
	public Product keepExistingProductData(Product existingProduct){
		ProductConfigurations oldProductConfig = existingProduct.getProductConfigurations();
		Product newProduct = new Product();
		ProductConfigurations newProductConfig = new ProductConfigurations();
		if(!CollectionUtils.isEmpty(existingProduct.getCategories())){
			newProduct.setCategories(existingProduct.getCategories());
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
		 if(!CollectionUtils.isEmpty(oldProductConfig.getImprintMethods())){
			 newProductConfig.setImprintMethods(oldProductConfig.getImprintMethods());
		 }
		 if(!CollectionUtils.isEmpty(existingProduct.getImages())){
			 newProduct.setImages(existingProduct.getImages());
		 }
		newProduct.setProductConfigurations(newProductConfig);
		return newProduct;
	}
/*	public Size getProductSize(String dimensionValue, String dimensionUnits,
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
	}*/
	
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
	
	
	public List<Origin> getOriginValues(String originValue){
		List<Origin> listOfOrigin   = new ArrayList<Origin>();
		Origin origin = new Origin();
		if(originValue.equalsIgnoreCase("United States")){
			originValue = ApplicationConstants.CONST_STRING_COUNTRY_NAME_USA;
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

	public List<ImprintMethod> getImprintMethodValues(String imprintMethodValue) {
		ImprintMethod imprMethod = new ImprintMethod();
		List<ImprintMethod> imprintMethodList = new ArrayList<ImprintMethod>();
		imprMethod.setAlias(imprintMethodValue);
		imprMethod.setType(imprintMethodValue);
		imprintMethodList.add(imprMethod);
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
	public List<Color> getProductColor(Set<String> colorsList){
		List<Color> listOfProductColor = new ArrayList<>();
		Color colorObj = null;
		for (String colorName : colorsList) {
			colorObj = new Color();
			//colorName = colorName.replaceAll("/", "-");
			String colorGroup = TeamworkAthleticColorMapping.getColorGroup(colorName);
			colorObj.setName(colorGroup);
			colorObj.setAlias(colorName);
			listOfProductColor.add(colorObj);
		}
		
		return listOfProductColor;
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
	public List<ProductionTime> getProductionTime(String startBusinessDay){
		List<ProductionTime> productionTimeList = new ArrayList<>();
		ProductionTime productionTime = new ProductionTime();
		productionTime.setBusinessDays(startBusinessDay);
		productionTime.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
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
			existingPriceGrid = teamWorkPriceGridParser.getUpchargePriceGrid("1", priceVal, disCount, "Imprint method", "n",
					"USD", imprintMethods, upChargeTypeVal, upchargeUsageType,serviceCharge, 1, existingPriceGrid);
		}
		return existingPriceGrid;
	}
	public List<PriceGrid> getAdditionalColorUpcharge(String discountCode,String prices,List<PriceGrid> existingPriceGrid,String upchargeType,String qty){
	   String disCountCode = getAdditionalColorDiscountCode(discountCode);
	   existingPriceGrid = teamWorkPriceGridParser.getUpchargePriceGrid(qty, prices, disCountCode, "Additional Colors", "n",
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
	public List<String> getProductKeywords(List<String> keywords){
		Set<String> keyList = new HashSet<>();
		for (String keyword : keywords) {
			String[] keys = CommonUtility.getValuesOfArray(keyword, " ");
			for (String keyName : keys) {
				keyName = keyName.trim();
				keyList.add(keyName);
			}
		}
		List<String> productKeyWords = keyList.stream().limit(30).collect(Collectors.toList());
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
	
	public List<Image> getImages(String imageUrl){
		List<Image> listOfImages = new ArrayList<>();
		Image imageObj  = new Image();
		int imageRank = 1;
			imageObj.setImageURL(imageUrl);
			imageObj.setIsvirtualized(false);
			imageObj.setRank(imageRank);
			imageObj.setDescription("");
			imageObj.setIsPrimary(true);
			 //List<Configurations>  configurationList = getImageConfiguration(color);
		     imageObj.setConfigurations(new ArrayList<>());
			listOfImages.add(imageObj);
			imageRank++;
		return listOfImages;
	}
	public List<Image> getImages(Set<String> imagelist){
		List<Image> listOfImages = new ArrayList<>();
		Image imageObj  = null;
		int imageRank = 1;
		for (String imageUrl : imagelist) {
			imageObj  = new Image();
			imageObj.setImageURL(imageUrl);
			imageObj.setIsvirtualized(false);
			imageObj.setRank(imageRank);
			imageObj.setDescription("");
			if(imageRank == 1){
				imageObj.setIsPrimary(true);	
			} else {
				imageObj.setIsPrimary(false);
			}
		    imageObj.setConfigurations(new ArrayList<>());
			imageRank++;
			listOfImages.add(imageObj);
		}
		return listOfImages;
	}
	public List<PriceGrid> getSetupCharge(String val,List<PriceGrid> priceGrid){
		String[] vals = CommonUtility.getValuesOfArray(val, ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		priceGrid = teamWorkPriceGridParser.getUpchargePriceGrid(vals[0], vals[1], vals[2], "Imprint method", "n",
				"USD", "Printed", "Set-up Charge", "Other","Required", 1, priceGrid);
		
		return priceGrid;
	}
	public Size getProductSize(List<String> sizeList){
		Size sizeObj = new Size();
		Apparel apparelObj = new Apparel();
		List<Value> valueList = new ArrayList<>();
		Value valueObj = null;
		for (String sizeVal : sizeList) {
			valueObj = new Value();
			valueObj.setValue(sizeVal);
			valueList.add(valueObj);
		}
		apparelObj.setValues(valueList);
		apparelObj.setType("Standard & Numbered");
		sizeObj.setApparel(apparelObj);
		return sizeObj;
	}
	 	public String getExactSizeValue(String sizeVal) {
          String finalSizeVal = "";
		switch (sizeVal) {
		case "XX-Large":
			finalSizeVal = "2XL";
			break;
		case "XXX-Large":
			finalSizeVal = "3XL";
			break;
		case "XXXX-Large":
			finalSizeVal = "4XL";
			break;
		case "Large":
			finalSizeVal = "L";
			break;
		case "Medium":
			finalSizeVal = "M";
			break;
		case "Small":
			finalSizeVal = "S";
			break;
		case "X-Large":
			finalSizeVal = "XL";
			break;
		case "X-Small":
			finalSizeVal = "XS";
			break;
		case "M/L TODO_SIZE":
			finalSizeVal = "M/L";
			break;
		case "U TODO_SIZE":
			finalSizeVal = "U TODO_SIZE";
			break;
		case "XX-Small":
			finalSizeVal = "2XS";
			break;
		case "XXXXX-Large":
			finalSizeVal = "5XL";
			break;

		}
		return finalSizeVal;
	}
	 	public ShippingEstimate getShippingEstimateValues(String shippingItemWeight){
			ShippingEstimate shippingEstimate = new ShippingEstimate();
	        List<Weight> listOfWeightObj = null;
	        Weight  weightObj = null;
	       if(!shippingItemWeight.equals(ApplicationConstants.CONST_STRING_ZERO)){
	    	   listOfWeightObj = new ArrayList<Weight>();
	    	   weightObj = new Weight();
	    	   weightObj.setValue(shippingItemWeight);
	    	   weightObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_WEIGHT);
	    	   listOfWeightObj.add(weightObj);
	       }
	       if(listOfWeightObj != null){
	    	   shippingEstimate.setWeight(listOfWeightObj);
	       }

	       return shippingEstimate;
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
	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}
	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}

	public TeamworkAthleticPriceGridParser getTeamWorkPriceGridParser() {
		return teamWorkPriceGridParser;
	}

	public void setTeamWorkPriceGridParser(TeamworkAthleticPriceGridParser teamWorkPriceGridParser) {
		this.teamWorkPriceGridParser = teamWorkPriceGridParser;
	}
	
	
}




