package parser.gillstudios;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Configurations;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.OtherSize;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;


public class GillStudiosAttributeParser {
	private static final Logger _LOGGER = Logger.getLogger(GillStudiosAttributeParser.class);
	private GillStudiosPriceGridParser gillStudiosPriceGridParser;
	private LookupServiceData lookupServiceDataObj;
public Product getExistingProductData(Product existingProduct , ProductConfigurations existingProductConfig){
		
		ProductConfigurations newProductConfigurations=new ProductConfigurations();
		Product newProduct=new Product();
		
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
		
		//themes
		List<Theme>	themes=existingProductConfig.getThemes();
		if(!CollectionUtils.isEmpty(themes)){
			List<Theme>	themesTemp=new ArrayList<Theme>();
			for (Theme theme : themes) {
				Theme themeObj=new Theme();
				String tempValue=theme.getName();
				tempValue=tempValue.trim();
				if(tempValue.toUpperCase().contains("ECO") || tempValue.toUpperCase().contains("FRIENDLY")){
				String	themeName="ECO & ENVIRONMENTALLY FRIENDLY";
				themeObj.setName(themeName);
				themesTemp.add(themeObj);
				}else{
					themeObj.setName(tempValue);
					themesTemp.add(themeObj);
				}
			}
			newProductConfigurations.setThemes(themesTemp);
		}
		//catalogs
		List<Catalog>	catlogsList=existingProduct.getCatalogs();
		if(!CollectionUtils.isEmpty(catlogsList)){
			newProduct.setCatalogs(catlogsList);
		}
		
		newProduct.setProductConfigurations(newProductConfigurations);
		}catch(Exception e){
			_LOGGER.error("Error while processing Existing Product Data " +e.getMessage());
		}
		 _LOGGER.info("Completed processing Existing Data");
		return newProduct;
		
	}
	
	
	public List<Values> getValues(String dimensionValue, String dimensionUnits,
			String dimensionType) {

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

		for (int i = 0; i < dimensionValueArr.length; i++) {
			valueObj = new Value();
			valueObj.setValue(dimensionValueArr[i]);
			valueObj.setUnit(GillStudiosLookupData.Dimension1Units.get(dimensionUnitsArr[i]));
			valueObj.setAttribute(GillStudiosLookupData.Dimension1Type.get(dimensionTypeArr[i]));
			valueList.add(valueObj);
		}

		valuesObj.setValue(valueList);
		valueslist.add(valuesObj);

		return valueslist;
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
		}else{
			
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
		}else{
			
		}
		return countryName;
	}
	
	public RushTime getRushTimeValues(String rushTime ,RushTime existingRushTime){
		existingRushTime.setAvailable(ApplicationConstants.CONST_BOOLEAN_TRUE);
		List<RushTimeValue> existingRushTimeValues = existingRushTime.getRushTimeValues();
		if(existingRushTimeValues == null){
			existingRushTimeValues = new ArrayList<RushTimeValue>();
		}
		RushTimeValue newRushTimeValue = new RushTimeValue();
		newRushTimeValue.setBusinessDays(rushTime);
		newRushTimeValue.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
		existingRushTimeValues.add(newRushTimeValue);
		existingRushTime.setRushTimeValues(existingRushTimeValues);
		return existingRushTime;
	}
	
	public List<Packaging> getPackageValues(String packageValues){
		List<Packaging> listOfPackage = new ArrayList<Packaging>();
		Packaging packaging = null;
		if(packageValues.contains(ApplicationConstants.CONST_DELIMITER_COMMA)){
			String[] packValues = packageValues.split(ApplicationConstants.CONST_DELIMITER_COMMA);
			for (String pack : packValues) {
				packaging = new Packaging();
			   packaging.setName(pack);
			   listOfPackage.add(packaging);
			}
		}else{
			packaging = new Packaging();
			   packaging.setName(packageValues);
			   listOfPackage.add(packaging);
		}
		return listOfPackage;
		
	}
	
	
	public ShippingEstimate getShippingEstimateValues(String length,String width,String height,
				                                         String weight,String numberOfItems){
			ShippingEstimate shippingEstimate = new ShippingEstimate();
			Dimensions dimension = null;
			List<NumberOfItems> numberOfItem = null;
			List<Weight> listOfWeightObj = null;
			Weight  weightObj = null;
			NumberOfItems numberOfItemsObj = null;
			if(!length.equals(ApplicationConstants.CONST_STRING_ZERO)){
				dimension = new Dimensions();
				dimension.setLength(length);
				dimension.setWidth(width);
				dimension.setHeight(height);
				dimension.setLengthUnit(ApplicationConstants.CONST_STRING_INCHES);
				dimension.setWidthUnit(ApplicationConstants.CONST_STRING_INCHES);
				dimension.setHeightUnit(ApplicationConstants.CONST_STRING_INCHES);
			}
			if(!weight.equals(ApplicationConstants.CONST_STRING_ZERO)){
				listOfWeightObj = new ArrayList<Weight>();
				weightObj = new Weight();
				
				if(weight.contains("0.") && weight.length()>=6){
					weight=weight.substring(0,weight.indexOf(".")+4);
				}
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
	
	
	
	public List<ImprintSize> getimprintsize(StringBuilder firstImprintSize) {
			String tempStr[]=firstImprintSize.toString().split("___");
			 List<ImprintSize> imprintSizeList =new ArrayList<ImprintSize>();
			 List<String> dupList =new ArrayList<String>();
		for (String impValue : tempStr) {
			String ImprintSizeValue=impValue.replace("null xnull","");
			ImprintSizeValue=ImprintSizeValue.replace("null", "");
			ImprintSizeValue=ImprintSizeValue.trim();
		    ImprintSize impsizeobj;
		    if(!StringUtils.isEmpty(ImprintSizeValue)){
			String ImprintsizeArr[]=ImprintSizeValue.split(",");
		   for (String Value : ImprintsizeArr) {
			   if(!dupList.contains(Value)){
			   impsizeobj=new ImprintSize();
			   impsizeobj.setValue(Value);
			   imprintSizeList.add(impsizeobj); 
		   }
		   dupList.add(Value);}
		    }
		}
			return imprintSizeList;
	}
	// color parsing
	@SuppressWarnings("unused")
	public List<Color> getProductColors(String color,List<Color> listOfColors){
		//List<Color> listOfColors = new ArrayList<>();
		String colorGroup=null;
		try{
			 if(CollectionUtils.isEmpty(listOfColors)){
				 listOfColors=new ArrayList<Color>();
			 }
			
			
		Color colorObj = null;
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
			 colorGroup = GillStudiosConstatnt.getColorGroup(colorName.trim());
			/////////////////////////
			if(colorGroup!=null){
			if(colorGroup.toUpperCase().contains("COMBO")){
				colorGroup=colorGroup.replaceAll(":","");
				colorGroup=colorGroup.replace("COMBO","/");
				colorName=colorGroup;
			}
			}
			/////////////////////////
			//if (colorGroup != null) {
				//if (colorGroup!=null && colorGroup.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
			//if (colorName.contains("/") || colorGroup.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) { //imp step
			if (colorName.contains("/")) {
				if(colorGroup==null){
					colorGroup=colorName;
				}
				colorGroup=colorGroup.replaceAll("&","/");
				colorGroup=colorGroup.replaceAll(" w/","/");
				colorGroup=colorGroup.replaceAll(" W/","/");
				
				//if (colorName.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
					if(isComboColor(colorGroup)){
						List<Combo> listOfCombo = null;
						String[] comboColors = CommonUtility.getValuesOfArray(colorGroup,
								ApplicationConstants.CONST_DELIMITER_FSLASH);
						String colorFirstName = GillStudiosConstatnt.getColorGroup(comboColors[0].trim());
						colorObj.setName(colorFirstName == null?"Other":colorFirstName);
						int combosSize = comboColors.length;
						if (combosSize == ApplicationConstants.CONST_INT_VALUE_TWO) {
							String colorComboFirstName = GillStudiosConstatnt.getColorGroup(comboColors[1].trim());
							colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
							listOfCombo = getColorsCombo(colorComboFirstName, ApplicationConstants.CONST_STRING_EMPTY,
									combosSize);
						} else{
							String colorComboFirstName = GillStudiosConstatnt.getColorGroup(comboColors[1].trim());
							colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
							
							String colorComboSecondName = GillStudiosConstatnt.getColorGroup(comboColors[2].trim());
							colorComboSecondName = colorComboSecondName == null?"Other":colorComboSecondName;
							listOfCombo = getColorsCombo(colorComboFirstName,colorComboSecondName, combosSize);
						}
						String alias = colorGroup.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH, "-");
						colorObj.setAlias(alias);
						colorObj.setCombos(listOfCombo);
					} else {
						String[] comboColors = CommonUtility.getValuesOfArray(colorGroup,
								ApplicationConstants.CONST_DELIMITER_FSLASH);
						String mainColorGroup = GillStudiosConstatnt.getColorGroup(comboColors[0].trim());
						String alias = colorName.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH, "-");
						if(mainColorGroup != null){
							colorObj.setName(mainColorGroup);
							colorObj.setAlias(alias);
						} else {
							colorObj.setName(ApplicationConstants.CONST_VALUE_TYPE_OTHER);
							colorObj.setAlias(alias);
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
				String alias = colorName.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH, "-");
				if (colorGroup == null) {
					colorGroup = ApplicationConstants.CONST_VALUE_TYPE_OTHER;
					}
				colorObj.setName(colorGroup);
				colorObj.setAlias(alias);
			}
			listOfColors.add(colorObj);
		}
		}catch(Exception e){
			_LOGGER.error("Error while processing color: "+colorGroup+" "+e.getMessage());
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
    		 mainColor = GillStudiosConstatnt.getColorGroup(colorVals[0].trim());
    		 secondaryColor = GillStudiosConstatnt.getColorGroup(colorVals[1].trim());
    		 if(mainColor != null && secondaryColor != null){
    			 return true;
    		 }
    	} else if(colorVals.length == ApplicationConstants.CONST_INT_VALUE_THREE){
    		 mainColor      = GillStudiosConstatnt.getColorGroup(colorVals[0].trim());
    		 secondaryColor = GillStudiosConstatnt.getColorGroup(colorVals[1].trim());
    		 thirdColor     = GillStudiosConstatnt.getColorGroup(colorVals[2].trim());
    		 if(mainColor != null && secondaryColor != null && thirdColor != null){
    			 return true;
    		 }
    	} else{
    		
    	}
    	return false;
    }
	
	public static String[] getValuesOfArray(String data,String delimiter){
		   if(!StringUtils.isEmpty(data)){
			   return data.split(delimiter);
		   }
		   return null;
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
			existingPriceGrid = gillStudiosPriceGridParser.getUpchargePriceGrid("1", priceVal, disCount, "Imprint method", "n",
					"USD", imprintMethods, upChargeTypeVal, "Other",1, "1___1___1___1___1___1___1___1___1___1", existingPriceGrid);
		}
		return existingPriceGrid;
	}
	public List<PriceGrid> getAdditionalColorRunUpcharge(String discountCode,String quantity,String prices,List<PriceGrid> existingPriceGrid,String upchargeType){
	  
		
		existingPriceGrid = gillStudiosPriceGridParser.getUpchargePriceGrid(quantity, prices, discountCode, "Additional Colors", "n",
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
		   
			existingPriceGrid = gillStudiosPriceGridParser.getUpchargePriceGrid(quantityTemp, prices, discountCode, "Additional Colors", "n",
					"USD", "Additional Color",upchargeType, "Other", 1, "1___1___1___1___1___1___1___1___1___1",  existingPriceGrid);
			
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

	public List<Theme> getProductTheme(String themeVal){
		List<Theme> listOfTheme = new ArrayList<>();
		Theme themeObj = null;
		String[] themes = CommonUtility.getValuesOfArray(themeVal, ApplicationConstants.CONST_DELIMITER_COMMA);
		for (String themeName : themes) {
			if(lookupServiceDataObj.isTheme(themeName.toUpperCase().trim())){
				themeObj = new Theme();
				if(themeName.toUpperCase().contains("ECO")){
					themeName="ECO & ENVIRONMENTALLY FRIENDLY";
				}
				themeObj.setName(themeName);
				listOfTheme.add(themeObj);
			}
		}
		return listOfTheme;
	}
	
	
	public Size getSizes(String sizeValue,Size sizeObj,List<Value> otherList) {
		//Size sizeObj = new Size();
		try{
			OtherSize otherObj = new OtherSize();
			if(CollectionUtils.isEmpty(otherList)){
				otherList = new ArrayList<Value>();
			}
			//String otherArr[] = sizeValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
			//List<Value> otherList = new ArrayList<Value>();
			//for (String value : sizeValueList) {
				
				Value valObj = new Value();
				valObj.setValue(sizeValue);
				otherList.add(valObj);
			//}
			otherObj.setValues(otherList);
			sizeObj.setOther(otherObj);
		}
		
		catch(Exception e)
		{
			
			_LOGGER.error("Error while processing Size :"+e.getMessage());
			return null;
		}
		return sizeObj;
	}
	
	public  List<ProductNumber> getProductNumer(HashMap<String, String> productNumberMap){//productNumberMap
		List<ProductNumber> pnumberList=new ArrayList<ProductNumber>();
		ProductNumber pnumberObj=new ProductNumber();
		try{
		List<Configurations> configList=new ArrayList<Configurations>();
		List<Object> valueObj;
		Configurations configObj;
		 Iterator mapItr = productNumberMap.entrySet().iterator();
		    while (mapItr.hasNext()) {
		    	pnumberObj=new ProductNumber();
		    	configList=new ArrayList<Configurations>();
		        Map.Entry values = (Map.Entry)mapItr.next();
		        pnumberObj.setProductNumber(values.getKey().toString());
		    	configObj=new Configurations();
				valueObj= new ArrayList<Object>();
				configObj.setCriteria("Product Color");
				valueObj.add(values.getValue());
				configObj.setValue(valueObj);
				configList.add(configObj);
				pnumberObj.setConfigurations(configList);
				pnumberList.add(pnumberObj);
		    }
		}catch(Exception e){
			_LOGGER.error("Error while processing Product Number :"+e.getMessage());             
		   	return new ArrayList<ProductNumber>();
		   }
		_LOGGER.info("ProductNumbers Processed");
		return pnumberList;		
	}
	
	public GillStudiosPriceGridParser getGillStudiosPriceGridParser() {
		return gillStudiosPriceGridParser;
	}


	public void setGillStudiosPriceGridParser(
			GillStudiosPriceGridParser gillStudiosPriceGridParser) {
		this.gillStudiosPriceGridParser = gillStudiosPriceGridParser;
	}


	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}


	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}
	
	
	
}
