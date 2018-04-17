package parser.solidDimension;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import parser.goldstarcanada.GoldstarCanadaLookupData;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class SolidDimesionAttributeParser {
	private LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;
    private SolidDimensionPriceGridParser solidDimensionPriceGridParser;
	private static final Logger _LOGGER = Logger
			.getLogger(SolidDimesionAttributeParser.class);
public Product getExistingProductData(Product existingProduct , ProductConfigurations existingProductConfig){
		
		ProductConfigurations newProductConfigurations=new ProductConfigurations();
		Product newProduct=new Product();
		//Categories (if present)
		//Images (if present)
		//Summary (if present)
		//Product Colors (if present)
		//Linename (if present)
		
		try{
			//categories
			List<String> listCategories=new ArrayList<String>();
			listCategories=existingProduct.getCategories();
			if(!CollectionUtils.isEmpty(listCategories)){
				newProduct.setCategories(listCategories);
			}
		//Images
		List<Image> images=existingProduct.getImages();
		if(!CollectionUtils.isEmpty(images)){
			newProduct.setImages(images);
		}
		
		
		//Summary (if present)
		String strSummry=existingProduct.getSummary();
		if(!StringUtils.isEmpty(strSummry)){
			newProduct.setSummary(strSummry);
		}
		
		//Product Colors (if present)
		List<Color> colors=existingProductConfig.getColors();
		if(!CollectionUtils.isEmpty(colors)){
			newProductConfigurations.setColors(colors);
		}
		
		//Linename (if present)
		List<String>  lineList=existingProduct.getLineNames();
		if(!CollectionUtils.isEmpty(lineList)){
			newProduct.setLineNames(lineList);
		}
		
		newProduct.setProductConfigurations(newProductConfigurations);
		}catch(Exception e){
			_LOGGER.error("Error while processing Existing Product Data " +e.getMessage());
		}
		 _LOGGER.info("Completed processing Existing Data");
		return newProduct;
		
	}
	public List<Values> getValues(String dimensionValue, String dimensionUnits,
			String dimensionType, List<Values> valuesList) {
		if(CollectionUtils.isEmpty(valuesList)){
			valuesList=new ArrayList<>();
		}
		String dimensionValueArr[] = dimensionValue
				.split(ApplicationConstants.CONST_DIMENSION_SPLITTER);
		String dimensionUnitsArr[] = dimensionUnits
				.split(ApplicationConstants.CONST_DIMENSION_SPLITTER);
		String dimensionTypeArr[] = dimensionType
				.split(ApplicationConstants.CONST_DIMENSION_SPLITTER);

		ArrayList<Value> valueList = new ArrayList<Value>();
		//List<Values> valueslist = new ArrayList<Values>();

		Values valuesObj = new Values();
		Value valueObj = null;

		for (int i = 0; i < dimensionValueArr.length; i++) {
			valueObj = new Value();
			valueObj.setValue(dimensionValueArr[i]);
			valueObj.setUnit(GoldstarCanadaLookupData.Dimension1Units
					.get(dimensionUnitsArr[i]));
			valueObj.setAttribute(GoldstarCanadaLookupData.Dimension1Type
					.get(dimensionTypeArr[i]));
			valueList.add(valueObj);
		}

		valuesObj.setValue(valueList);
		valuesList.add(valuesObj);

		return valuesList;
	}

	public List<ImprintMethod> getImprintMethodValues(String imprintMethod,
			List<ImprintMethod> imprintMethodList) {
		//String tempAlias=imprintMethod;
		String tempStr[]=imprintMethod.split(",");
		ImprintMethod imprMethod = new ImprintMethod();
		//List<ImprintMethod> imprintMethodsList = new ArrayList<ImprintMethod>();
		//////////////
		if(CollectionUtils.isEmpty(imprintMethodList)){
			imprintMethodList = new ArrayList<>();
		}
		for (String impValue : tempStr) {
			String tempAlias=impValue.trim();
			impValue=impValue.toUpperCase().trim();
			impValue = impValue.replace("4-COLOR PROCESS", "Full Color");
			impValue = impValue.replace("PAD PRINTED", "Pad Print");
			impValue = impValue.replace("SCREEN PRINTED", "Silkscreen");
			impValue = impValue.replace("FULL-COLOR DIGITAL","Full Color");
			if (impValue.contains("LASER")){
				impValue="Laser Engraved";
			}
			List<String> finalImprintValues = getImprintValue(impValue
					.toUpperCase().trim());
			if(!CollectionUtils.isEmpty(finalImprintValues)){
				 String value =  finalImprintValues.get(finalImprintValues.size() -1);
			//for (String innerValue : finalImprintValues) {
				imprMethod = new ImprintMethod();
				imprMethod.setAlias(tempAlias);
				imprMethod.setType(value);
				imprintMethodList.add(imprMethod);
			//}
			}else{
				imprMethod = new ImprintMethod();
				imprMethod.setAlias(tempAlias);
				imprMethod.setType("Other");
				imprintMethodList.add(imprMethod);
			}
		}
		///////////////////
		if (imprintMethodList.isEmpty()) {
			imprMethod.setAlias("Unimprinted");
			imprMethod.setType("Unimprinted");
			imprintMethodList.add(imprMethod);
		}
		return imprintMethodList;
	}

	public List<String> getImprintValue(String value) {
		List<String> imprintLookUpValue = lookupServiceDataObj
				.getImprintMethods();
		List<String> finalImprintValues = imprintLookUpValue.stream()
				.filter(impntName -> value.contains(impntName))
				.collect(Collectors.toList());

		return finalImprintValues;
	}

	public List<ImprintMethod> addUNImprintMethod(
			List<ImprintMethod> imprintMethodList) {
	//	List<ImprintMethod> imprintMethodList = new ArrayList<>();
		if(CollectionUtils.isEmpty(imprintMethodList)){
			imprintMethodList = new ArrayList<>();
		}
		ImprintMethod imprintMethodObj = new ImprintMethod();
		imprintMethodObj.setAlias("Unimprinted");
		imprintMethodObj.setType("Unimprinted");
		imprintMethodList.add(imprintMethodObj);
		//productConfigObj.setImprintMethods(imprintMethodList);
		return imprintMethodList;
	}

	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}

	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}

	public LookupRestService getLookupRestServiceObj() {
		return lookupRestServiceObj;
	}

	public void setLookupRestServiceObj(LookupRestService lookupRestServiceObj) {
		this.lookupRestServiceObj = lookupRestServiceObj;
	}

	public List<Origin> getOriginValues(String originValue) {
		List<Origin> listOfOrigin = new ArrayList<Origin>();
		Origin origin = new Origin();
		if (originValue
				.equalsIgnoreCase("DE")) {
			originValue ="DENMARK";
		} else if (originValue.equalsIgnoreCase(ApplicationConstants.CONST_STRING_COUNTRY_CODE_US)) {
			originValue = ApplicationConstants.CONST_STRING_COUNTRY_NAME_USA;
		} else if (originValue.equalsIgnoreCase("CN")) {
			originValue = ApplicationConstants.CONST_STRING_COUNTRY_NAME_CHINA;
		}else if (originValue.equalsIgnoreCase("TH")) {
			originValue = "THAILAND";
		}else if (originValue.equalsIgnoreCase("TA")) {
			originValue = "TAJIKISTAN";//ApplicationConstants.CONST_STRING_COUNTRY_NAME_CHINA;
		}else if (originValue.equalsIgnoreCase("MX")) {
			originValue = "MEXICO";//ApplicationConstants.CONST_STRING_COUNTRY_NAME_CHINA;
		}else if (originValue.equalsIgnoreCase("TW")) {
			originValue = "TAIWAN";//ApplicationConstants.CONST_STRING_COUNTRY_NAME_CHINA;
		}else if (originValue.equalsIgnoreCase("CH")) {
			originValue = "SWITZERLAND";//ApplicationConstants.CONST_STRING_COUNTRY_NAME_CHINA;
		}
		
		origin.setName(originValue);
		listOfOrigin.add(origin);
		return listOfOrigin;
	}

	public String getCountryCodeConvertName(String countryCode) {
		String countryName = null;
		if (countryCode
				.equalsIgnoreCase(ApplicationConstants.CONST_STRING_COUNTRY_CODE_CH)) {
			countryName = ApplicationConstants.CONST_STRING_COUNTRY_NAME_CHINA;
		} else if (countryCode
				.equalsIgnoreCase(ApplicationConstants.CONST_STRING_COUNTRY_CODE_US)) {
			countryName = ApplicationConstants.CONST_STRING_COUNTRY_NAME_USA;
		} else {

		}
		return countryName;
	}

	public RushTime getRushTimeValues(String rushProdTimeLo,
			String rushProdTimeH) {

		RushTime rushtimeObj = new RushTime();
		rushtimeObj.setAvailable(ApplicationConstants.CONST_BOOLEAN_TRUE);
		String FinalrushTime = rushProdTimeLo.concat("-").concat(rushProdTimeH);
		RushTimeValue RushTimeValue = new RushTimeValue();
		List<RushTimeValue> rushTimeList = new ArrayList<RushTimeValue>();

		RushTimeValue.setBusinessDays(FinalrushTime);
		RushTimeValue.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
		rushTimeList.add(RushTimeValue);
		rushtimeObj.setRushTimeValues(rushTimeList);
		return rushtimeObj;
	}

	/*public List<Packaging> getPackageValues(String packageValues) {
		List<Packaging> listOfPackage = new ArrayList<Packaging>();
		Packaging packaging = null;
		if (packageValues.contains(ApplicationConstants.CONST_DELIMITER_COMMA)) {
			String[] packValues = packageValues
					.split(ApplicationConstants.CONST_DELIMITER_COMMA);
			for (String pack : packValues) {
				packaging = new Packaging();
				packaging.setName(pack);
				listOfPackage.add(packaging);
			}
		} else {
			packaging = new Packaging();
			packaging.setName(packageValues);
			listOfPackage.add(packaging);
		}
		return listOfPackage;

	}*/

	public ShippingEstimate getShippingEstimateValues(String length,
			String width, String height, String weight, String numberOfItems) {
		ShippingEstimate shippingEstimate = new ShippingEstimate();
		Dimensions dimension = null;
		List<NumberOfItems> numberOfItem = null;
		List<Weight> listOfWeightObj = null;
		Weight weightObj = null;
		NumberOfItems numberOfItemsObj = null;
		if (!length.equals(ApplicationConstants.CONST_STRING_ZERO)) {
			dimension = new Dimensions();
			dimension.setLength(length);
			dimension.setWidth(width);
			dimension.setHeight(height);
			dimension.setLengthUnit(ApplicationConstants.CONST_STRING_INCHES);
			dimension.setWidthUnit(ApplicationConstants.CONST_STRING_INCHES);
			dimension.setHeightUnit(ApplicationConstants.CONST_STRING_INCHES);
		}
		if (!weight.equals(ApplicationConstants.CONST_STRING_ZERO)) {
			listOfWeightObj = new ArrayList<Weight>();
			weightObj = new Weight();
			weightObj.setValue(weight);
			weightObj
					.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_WEIGHT);
			listOfWeightObj.add(weightObj);
		}
		if (!numberOfItems.equals(ApplicationConstants.CONST_STRING_ZERO)) {
			numberOfItem = new ArrayList<NumberOfItems>();
			numberOfItemsObj = new NumberOfItems();
			numberOfItemsObj.setValue(numberOfItems);
			numberOfItemsObj
					.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_NUMBER_UNIT_CARTON);
			numberOfItem.add(numberOfItemsObj);
		}
		if (dimension != null) {
			shippingEstimate.setDimensions(dimension);
		}
		if (numberOfItem != null) {
			shippingEstimate.setNumberOfItems(numberOfItem);
		}
		if (listOfWeightObj != null) {
			shippingEstimate.setWeight(listOfWeightObj);
		}

		return shippingEstimate;
	}
	
	/*public List<ImprintSize> getimprintsize(StringBuilder firstImprintSize,String imprintlocation) {
	    List<ImprintSize> imprintSizeList =new ArrayList<ImprintSize>();
	    ImprintSize impsizeobj;
		if(imprintlocation.contains("Imprint")){
		
		String imprintsize1=imprintlocation.substring(8, 18);
		   impsizeobj=new ImprintSize();
		   impsizeobj.setValue(imprintsize1);
		   imprintSizeList.add(impsizeobj);

		}
		
		String ImprintSizeValue=firstImprintSize.toString().replace("null x null","");
		ImprintSizeValue=ImprintSizeValue.replace("null", "");	    
		String ImprintsizeArr[]=ImprintSizeValue.split(",");
		
		
	   for (String Value : ImprintsizeArr) {
		   impsizeobj=new ImprintSize();
		   impsizeobj.setValue(Value);
		   imprintSizeList.add(impsizeobj);
	      }		
		
		
		
		return imprintSizeList;
	}*/
	
	public List<ImprintSize> getimprintsize(String firstImprintSize,List<ImprintSize> imprintSizeList) {
		 if(CollectionUtils.isEmpty(imprintSizeList)){
			 imprintSizeList=new ArrayList<ImprintSize>();
		 }
		String tempStr[]=firstImprintSize.toString().split("___");
		// List<ImprintSize> imprintSizeList =new ArrayList<ImprintSize>();
		 List<String> dupList =new ArrayList<String>();
	for (String impValue : tempStr) {
		String ImprintSizeValue=impValue.replace("null xnull","");
		ImprintSizeValue=ImprintSizeValue.replace("null", "");
		ImprintSizeValue=ImprintSizeValue.trim();
	    ImprintSize impsizeobj;
	    if(!StringUtils.isEmpty(ImprintSizeValue)){
	    	if(ImprintSizeValue.endsWith("x")){
	    		ImprintSizeValue = ImprintSizeValue.substring(0, ImprintSizeValue.length() - 1);
	    	}
		String ImprintsizeArr[]=ImprintSizeValue.split(",");
	   for (String Value : ImprintsizeArr) {
		   if(!dupList.contains(Value)){
			   if(Value.endsWith("x")){
				   Value = Value.substring(0, Value.length() - 1);
		    	}
		   impsizeobj=new ImprintSize();
		   impsizeobj.setValue(Value);
		   imprintSizeList.add(impsizeobj); 
		   }
		   dupList.add(Value);
		   		}
	    	}
		}
	return imprintSizeList;
	}
	public boolean getCategory(String value){
		boolean flag=false;
		try{
		List<String> listOfLookupCategories = lookupServiceDataObj.getCategories();
		if(listOfLookupCategories.contains(value)){
			flag=true;
		}	
		}
		catch (Exception e) {
			_LOGGER.error("Error while getting Category look up values :"+e.getMessage()); 
			return flag;
		}
		return flag;	
	}
	
	public List<AdditionalColor> getAdditionalColor(String colorValue){
		List<AdditionalColor> additionalColorList = new ArrayList<>();
		AdditionalColor additionalColorObj = new AdditionalColor();
		additionalColorObj.setName(colorValue);
		additionalColorList.add(additionalColorObj);
		return additionalColorList;
	}
	public List<PriceGrid> getImprintMethodUpcharges(Map<String, String> upchargeValues,List<ImprintMethod> imprintMethodList,List<PriceGrid> existingPriceGrid){
		String imprintMethods = imprintMethodList.stream().map(ImprintMethod::getAlias)
															.collect(Collectors.joining(","));
		String strImpArr[]=imprintMethods.split(",");
		for (String impMtdValue : strImpArr) {
		
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
			 } else if(upchargeType.equalsIgnoreCase("dieCharge")){
				 upChargeTypeVal = "Die Charge";
			 } else if(upchargeType.equalsIgnoreCase("toolingCharge")){
				 upChargeTypeVal = "Tooling Charge";
			 } else if(upchargeType.equalsIgnoreCase("repeateCharge")){
				 upChargeTypeVal = "Re-order Charge";
			 }
			/* String qaunitity="1___";
				int qantyLen=prices.split("___").length;
				if(qantyLen>1){
					qaunitity=String.join("", Collections.nCopies(qantyLen, qaunitity));
				}*/
			 String upUsageType="Other";
			 if( upChargeTypeVal.equals("Set-up Charge")){
				 upUsageType="Per Order";
			 }
			existingPriceGrid = solidDimensionPriceGridParser.getUpchargePriceGrid("1", priceVal, disCount, "Imprint method", "n",
					"USD", impMtdValue, upChargeTypeVal, upUsageType,1, "1___1___1___1___1___1___1___1___1___1", existingPriceGrid);
		}
	}
		
		return existingPriceGrid;
	}
	public List<PriceGrid> getAdditionalColorRunUpcharge(String discountCode,String quantity,String prices,List<PriceGrid> existingPriceGrid,String upchargeType){
	  
		int qantyLen=prices.split("___").length;
		String quantityTemp = "1";
		if(qantyLen>1){
		for (int i = 2; i <=qantyLen; i++) {
			quantityTemp=quantityTemp.concat("___")+Integer.toString(i);
		}
		}
		existingPriceGrid = solidDimensionPriceGridParser.getUpchargePriceGrid(quantityTemp, prices, discountCode, "Additional Colors", "n",
				"USD", "Additional Color",upchargeType, "Other", 1, "1___1___1___1___1___1___1___1___1___1",  existingPriceGrid);
	   
		return existingPriceGrid;
	}
	
	
	public List<PriceGrid> getAdditionalColorUpcharge(String discountCode,String prices,List<PriceGrid> existingPriceGrid,String upchargeType){
		  // String disCountCode = getAdditionalColorDiscountCode(discountCode);
			/*String qaunitity="1___";
			int qantyLen=prices.split("___").length;
			if(qantyLen>1){
				qaunitity=String.join("", Collections.nCopies(qantyLen, qaunitity));
			}*/
			
			//chnaged on 13 june after review
			int qantyLen=prices.split("___").length;
			String quantityTemp = "1";
			if(qantyLen>1){
			for (int i = 2; i <=qantyLen; i++) {
				quantityTemp=quantityTemp.concat("___")+Integer.toString(i);
			}
			}
		   
			existingPriceGrid = solidDimensionPriceGridParser.getUpchargePriceGrid(quantityTemp, prices, discountCode, "Additional Colors", "n",
					"USD", "Additional Color",upchargeType, "Other", 1, "1___1___1___1___1___1___1___1___1___1",  existingPriceGrid);
			
			return existingPriceGrid;
		}
	
	@SuppressWarnings("unused")
	public List<Color> getProductColors(String colorValuee,String xid){
		List<Color> listOfColors = new ArrayList<>();
		
		Color colorObj = null;
		//Iterator<String> colorIterator=colorValuee.iterator();
		try {
			List<Combo> comboList = null;
		//while (colorIterator.hasNext()) {
			String color =colorValuee; //(String) colorIterator.next();
			color=color.replaceAll("\\|",",");
		String[] colors =getValuesOfArray(color, ",");
		for (String colorName : colors) {
			if(StringUtils.isEmpty(colorName)){
				continue;
			}
			colorName=colorName.replaceAll("&","/");
			colorName=colorName.replaceAll(" w/","/");
			colorName=colorName.replaceAll(" W/","/");
			//colorName = colorName.trim();
			
			colorObj = new Color();
			//String colorGroup = SolidDimApplicationConstatnt.getColorGroup(colorName.trim());
			
			//if (colorGroup == null) {
				//if (colorGroup!=null && colorGroup.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
			if (colorName!=null && (colorName.contains("/") || colorName.contains(ApplicationConstants.CONST_DELIMITER_FSLASH))) {
				
				/*if(colorGroup==null){
					colorGroup=colorName;
				}*/
				colorName=colorName.replaceAll("&","/");
				colorName=colorName.replaceAll(" w/","/");
				colorName=colorName.replaceAll(" W/","/");
				
				//if (colorName.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
					if(isComboColor(colorName)){
						List<Combo> listOfCombo = null;
						String[] comboColors = CommonUtility.getValuesOfArray(colorName,
								ApplicationConstants.CONST_DELIMITER_FSLASH);
						String colorFirstName = SolidDimApplicationConstatnt.getColorGroup(comboColors[0].trim());
						colorObj.setName(colorFirstName == null?"Other":colorFirstName);
						int combosSize = comboColors.length;
						if (combosSize == ApplicationConstants.CONST_INT_VALUE_TWO) {
							String colorComboFirstName = SolidDimApplicationConstatnt.getColorGroup(comboColors[1].trim());
							colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
							listOfCombo = getColorsCombo(colorComboFirstName, ApplicationConstants.CONST_STRING_EMPTY,
									combosSize);
						} else{
							String colorComboFirstName = SolidDimApplicationConstatnt.getColorGroup(comboColors[1].trim());
							colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
							
							String colorComboSecondName = SolidDimApplicationConstatnt.getColorGroup(comboColors[2].trim());
							colorComboSecondName = colorComboSecondName == null?"Other":colorComboSecondName;
							listOfCombo = getColorsCombo(colorComboFirstName,colorComboSecondName, combosSize);
						}
						String alias = colorName.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH, "-");
						colorObj.setAlias(alias);
						colorObj.setCombos(listOfCombo);
					} else {
						String[] comboColors = CommonUtility.getValuesOfArray(colorName,
								ApplicationConstants.CONST_DELIMITER_FSLASH);
						String mainColorGroup = SolidDimApplicationConstatnt.getColorGroup(comboColors[0].trim());
						if(mainColorGroup != null){
							colorObj.setName(mainColorGroup);
							colorObj.setAlias(colorName);
						} else {
							colorObj.setName(ApplicationConstants.CONST_VALUE_TYPE_OTHER);
							colorObj.setAlias(colorName);
						}
					}
				/*} else {
					if (colorGroup == null) {
					colorGroup = ApplicationConstants.CONST_VALUE_TYPE_OTHER;
					}
					colorObj.setName(colorGroup);
					colorObj.setAlias(colorName);
				}*/
			} else {
				String colorGroup = SolidDimApplicationConstatnt.getColorGroup(colorName.trim());
				if (colorGroup == null) {
					colorGroup = ApplicationConstants.CONST_VALUE_TYPE_OTHER;
					}
				colorObj.setName(colorGroup);
				colorObj.setAlias(colorName);
			}
			listOfColors.add(colorObj);
		}//for end
		//}//while end
		}catch(Exception e){
			_LOGGER.error("Error while processing color: "+e.getMessage() +"Color Error For xid:"+xid);
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
	
	public static boolean isComboColor(String colorValue){
    	String[] colorVals = CommonUtility.getValuesOfArray(colorValue, "/");
    	String mainColor       = null;
    	String secondaryColor  = null;
    	String thirdColor      = null;
    	if(colorVals.length == ApplicationConstants.CONST_INT_VALUE_TWO){
    		 mainColor = SolidDimApplicationConstatnt.getColorGroup(colorVals[0].trim());
    		 secondaryColor = SolidDimApplicationConstatnt.getColorGroup(colorVals[1].trim());
    		 if(mainColor != null && secondaryColor != null){
    			 return true;
    		 }
    	} else if(colorVals.length == ApplicationConstants.CONST_INT_VALUE_THREE){
    		 mainColor      = SolidDimApplicationConstatnt.getColorGroup(colorVals[0].trim());
    		 secondaryColor = SolidDimApplicationConstatnt.getColorGroup(colorVals[1].trim());
    		 thirdColor     = SolidDimApplicationConstatnt.getColorGroup(colorVals[2].trim());
    		 if(mainColor != null && secondaryColor != null && thirdColor != null){
    			 return true;
    		 }
    	} else{
    		
    	}
    	return false;
    }
	
	public List<Packaging> getPackageValues(String packageValues){
		List<Packaging> listOfPackage = new ArrayList<Packaging>();
		Packaging packaging = null;
		String[] packValues = packageValues.split(ApplicationConstants.CONST_DELIMITER_COMMA);
			for (String pack : packValues) {
				packaging = new Packaging();
			   packaging.setName(pack);
			   listOfPackage.add(packaging);
			}
		return listOfPackage;
		
	}
	
	public static String[] getValuesOfArray(String data,String delimiter){
		   if(!StringUtils.isEmpty(data)){
			   return data.split(delimiter);
		   }
		   return null;
	   }
	public SolidDimensionPriceGridParser getSolidDimensionPriceGridParser() {
		return solidDimensionPriceGridParser;
	}
	public void setSolidDimensionPriceGridParser(
			SolidDimensionPriceGridParser solidDimensionPriceGridParser) {
		this.solidDimensionPriceGridParser = solidDimensionPriceGridParser;
	}
   
	public static List<Object> getValuesObj(String dimensionValue, String dimensionUnits,
			String dimensionType, List<Object> valuesList) {
		if(CollectionUtils.isEmpty(valuesList)){
			valuesList=new ArrayList<>();
		}
		String dimensionValueArr[] = dimensionValue
				.split(ApplicationConstants.CONST_DIMENSION_SPLITTER);
		String dimensionUnitsArr[] = dimensionUnits
				.split(ApplicationConstants.CONST_DIMENSION_SPLITTER);
		String dimensionTypeArr[] = dimensionType
				.split(ApplicationConstants.CONST_DIMENSION_SPLITTER);

		//ArrayList<Value> valueList = new ArrayList<Value>();
		//List<Values> valueslist = new ArrayList<Values>();

		Value valueObj = null;

		for (int i = 0; i < dimensionValueArr.length; i++) {
			valueObj = new Value();
			valueObj.setValue(dimensionValueArr[i]);
			valueObj.setUnit(GoldstarCanadaLookupData.Dimension1Units
					.get(dimensionUnitsArr[i]));
			valueObj.setAttribute(GoldstarCanadaLookupData.Dimension1Type
					.get(dimensionTypeArr[i]));
			valuesList.add(valueObj);
		}

		//valuesList.setValue(valueList);
		//aluesList.add(valuesObj);

		return valuesList;
	}
}
