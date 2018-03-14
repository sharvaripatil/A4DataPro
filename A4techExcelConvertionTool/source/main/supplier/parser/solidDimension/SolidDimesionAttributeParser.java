package parser.solidDimension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
			valueObj.setUnit(GoldstarCanadaLookupData.Dimension1Units
					.get(dimensionUnitsArr[i]));
			valueObj.setAttribute(GoldstarCanadaLookupData.Dimension1Type
					.get(dimensionTypeArr[i]));
			valueList.add(valueObj);
		}

		valuesObj.setValue(valueList);
		valueslist.add(valuesObj);

		return valueslist;
	}

	public List<ImprintMethod> getImprintMethodValues(String imprintMethod,
			List<ImprintMethod> imprintMethodList) {
		List<ImprintMethod> imprintMethodsList = new ArrayList<ImprintMethod>();
		imprintMethod = imprintMethod.replace("4-color process", "Full Color");
		imprintMethod = imprintMethod.replace("Pad printed", "Pad Print");
		imprintMethod = imprintMethod.replace("Screen printed", "Silkscreen");
		imprintMethod = imprintMethod.replace("Full-color digital",
				"Full Color");

		ImprintMethod imprMethod = new ImprintMethod();
		ImprintMethod imprMethod1 = new ImprintMethod();

		if (imprintMethod.contains("Laser")) {
			imprMethod = new ImprintMethod();
			imprMethod.setAlias("Laser Engraved");
			imprMethod.setType("Laser Engraved");
			imprintMethodsList.add(imprMethod);
		}

		else {
			List<String> finalImprintValues = getImprintValue(imprintMethod
					.toUpperCase().trim());
			for (String innerValue : finalImprintValues) {
				imprMethod = new ImprintMethod();
				imprMethod.setAlias(innerValue);
				imprMethod.setType(innerValue);
				imprintMethodsList.add(imprMethod);
			}
			if (imprintMethodsList.isEmpty()) {
				imprMethod1.setAlias("Printed");
				imprMethod1.setType("Printed");
				imprintMethodsList.add(imprMethod1);
			}
		}
		return imprintMethodsList;
	}

	public List<String> getImprintValue(String value) {
		List<String> imprintLookUpValue = lookupServiceDataObj
				.getImprintMethods();
		List<String> finalImprintValues = imprintLookUpValue.stream()
				.filter(impntName -> value.contains(impntName))
				.collect(Collectors.toList());

		return finalImprintValues;
	}

	public ProductConfigurations addImprintMethod(
			ProductConfigurations productConfigObj) {
		List<ImprintMethod> imprintMethodList = new ArrayList<>();
		ImprintMethod imprintMethodObj = new ImprintMethod();
		imprintMethodObj.setAlias("Unimprinted");
		imprintMethodObj.setType("Unimprinted");
		imprintMethodList.add(imprintMethodObj);
		productConfigObj.setImprintMethods(imprintMethodList);
		return productConfigObj;
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

	public List<Packaging> getPackageValues(String packageValues) {
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

	}

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
			existingPriceGrid = solidDimensionPriceGridParser.getUpchargePriceGrid("1", priceVal, disCount, "Imprint method", "n",
					"USD", imprintMethods, upChargeTypeVal, "Other",1, "1___1___1___1___1___1___1___1___1___1", existingPriceGrid);
		}
		return existingPriceGrid;
	}
	public List<PriceGrid> getAdditionalColorRunUpcharge(String discountCode,String quantity,String prices,List<PriceGrid> existingPriceGrid,String upchargeType){
	  
		
		existingPriceGrid = solidDimensionPriceGridParser.getUpchargePriceGrid(quantity, prices, discountCode, "Additional Colors", "n",
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
   
	
}
