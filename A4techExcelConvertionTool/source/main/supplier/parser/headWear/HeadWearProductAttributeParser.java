package parser.headWear;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.AvailableVariations;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Weight;
import com.a4tech.sage.product.util.LookupData;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.harvestIndustrail.HarvestLookupData;

public class HeadWearProductAttributeParser {
	private LookupServiceData lookupServiceDataObj;
	
	
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
			valueObj.setUnit(LookupData.Dimension1Units.get(dimensionUnitsArr[i]));
			valueObj.setAttribute(LookupData.Dimension1Type.get(dimensionTypeArr[i]));
			valueList.add(valueObj);
		}
		valuesObj.setValue(valueList);
		valueslist.add(valuesObj);
		return valueslist;
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
		}else if(countryCode.equalsIgnoreCase("CN")){
			countryName = ApplicationConstants.CONST_STRING_COUNTRY_NAME_CHINA;
		}
		else if(countryCode.equalsIgnoreCase("CA")){
			countryName = "CANADA";
		}else{
			
		}
		return countryName;
	}
	
	public List<ImprintMethod> getImprintMethodValues(String imprintMethod){

		ImprintMethod imprMethod = new ImprintMethod();
		List<ImprintMethod> imprintMethodList = new ArrayList<ImprintMethod>();
		String imprintMethodValueArr[]=imprintMethod.split("or");
		
		for (String imprintMethodValue : imprintMethodValueArr) {
			
			imprMethod = new ImprintMethod();
			imprMethod.setAlias(imprintMethodValue);
	  if(imprintMethodValue.contains("Embroidery"))
	  {
		  imprMethod.setType("Embroidered");
	  }else if(imprintMethodValue.contains("Offset printed") 
			  ||imprintMethodValue.contains("Pad printed"))
	  {
		  imprMethod.setType("Printed");
	  }else if(imprintMethodValue.contains("4-color process"))
	  {
		  imprMethod.setType("Full Color");

	  }else if(imprintMethodValue.contains("Sublimation"))
	  {
		  imprMethod.setType("Sublimation");
	  }else if(imprintMethodValue.contains("Silk Screen Printing") || imprintMethodValue.contains("Screen printed"))
	  {
		  imprMethod.setType("Silkscreen");
	  }
	  else if(imprintMethodValue.contains("Laser engraved") || imprintMethodValue.contains("Laser Engraving") )
	  {
		  imprMethod.setType("Laser engraved");
	  }else
	  {
		  imprMethod.setType("Printed");
	  }
	  imprMethod.setAlias(imprintMethodValue);	
	  imprintMethodList.add(imprMethod);

		}	
		return imprintMethodList;
	}

	  public List<ImprintSize> getimprintsize(String firstImprintSize) {
		List<ImprintSize> imprintSizeList =new ArrayList<ImprintSize>();
	    ImprintSize impsizeobj=new ImprintSize();
	    if(firstImprintSize.contains("|"))
	    {
	    	firstImprintSize=firstImprintSize.replace("| ", "@@");
	    	String ImprintValue[]=firstImprintSize.split("@@");
	    	if(!ImprintValue[0].trim().equalsIgnoreCase(ImprintValue[1].trim())){
	    	for (String Imprintvalue : ImprintValue) {
	    	  impsizeobj=new ImprintSize();	
	    	  impsizeobj.setValue(Imprintvalue);
	   		  imprintSizeList.add(impsizeobj);
			}	  
	    	}else
	    	{
	    	 impsizeobj=new ImprintSize();	
		     impsizeobj.setValue(ImprintValue[0]);
		   	 imprintSizeList.add(impsizeobj);
	    	}
	    }
	    else
	    {
	    	 impsizeobj=new ImprintSize();	
	    	  impsizeobj.setValue(firstImprintSize);
	   		  imprintSizeList.add(impsizeobj);
	     }
		return imprintSizeList;
	}
	
	 public List<ImprintLocation> getImprintMethodLocation(String imprintLocation) {
			List<ImprintLocation> listImprintLocation = new ArrayList<ImprintLocation>();
			ImprintLocation locationObj1 = new ImprintLocation();
			ImprintLocation locationObj2 = new ImprintLocation();
			locationObj1.setValue("Front");
			locationObj2.setValue("Back");
			listImprintLocation.add(locationObj1);
			listImprintLocation.add(locationObj2);
		return listImprintLocation;
	}

		public List<Availability> getAvaibilty(List<ImprintLocation> imprintLocationList, 
				List<ImprintSize> imprintSizeList) {
			List<Availability>  availabilityList= new ArrayList<Availability>(); 
			Availability avaibltyObj=new Availability();
			
			List<AvailableVariations>  avaiVaraitionList= new ArrayList<AvailableVariations>(); 
			AvailableVariations VariationObj=new AvailableVariations();
			
			List<Object>  locationList= new ArrayList<Object>(); 
			List<Object>  sizeList= new ArrayList<Object>(); 

			avaibltyObj.setParentCriteria("Imprint Location");
			avaibltyObj.setChildCriteria("Imprint Size");
			
		  for(int i=0;i<imprintLocationList.size();i++){
			String LocArr=imprintLocationList.get(i).getValue().toString().trim();
			String SizeArr=null;
			if(imprintSizeList.size()==2){
			 SizeArr=imprintSizeList.get(i).getValue().toString().trim();
			}else
			{
			 SizeArr=imprintSizeList.get(0).getValue().toString().trim();
	
			}
					 VariationObj = new AvailableVariations();
					 locationList = new ArrayList<>();
					 sizeList = new ArrayList<>();
					 locationList.add(LocArr);
					 sizeList.add(SizeArr);
					 VariationObj.setParentValue(locationList);
					 VariationObj.setChildValue(sizeList);
					 avaiVaraitionList.add(VariationObj);
		}
			avaibltyObj.setAvailableVariations(avaiVaraitionList);
			availabilityList.add(avaibltyObj);
			
			return availabilityList;	
		}	
	public RushTime getRushTimeValues(String rushProdTimeLo, String rushProdTimeH) {

		RushTime rushtimeObj=new RushTime();
		rushtimeObj.setAvailable(ApplicationConstants.CONST_BOOLEAN_TRUE);
		String FinalrushTime=rushProdTimeLo.concat("-").concat(rushProdTimeH);
		RushTimeValue RushTimeValue = new RushTimeValue();
		List<RushTimeValue> rushTimeList = new ArrayList<RushTimeValue>();

		RushTimeValue.setBusinessDays(FinalrushTime);
		RushTimeValue.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
		rushTimeList.add(RushTimeValue);
		rushtimeObj.setRushTimeValues(rushTimeList);		
		return rushtimeObj;
	}
	
       public List<ImprintSize> getimprintsize(StringBuilder firstImprintSize) {
		
		String ImprintSizeValue=firstImprintSize.toString().replace("null x null","");
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
       public List<Color> getColorCriteria(String colorValue) {
   		List<Color> listOfProductColors = new ArrayList<>();
   		List<Combo> combolist = new ArrayList<>();

   		Color colorObj = new Color();
   		Combo comboObj=new Combo();
   		
   		String[] colorValues = CommonUtility.getValuesOfArray(colorValue,
   				ApplicationConstants.CONST_DELIMITER_COMMA);
   		for (String colorName : colorValues) {
   			colorObj = new Color();
   			 comboObj=new Combo();
   			String OriginalcolorName = colorName.trim();
   			String colorLookUpName=HarvestLookupData.COLOR_MAP.get(colorName.trim()).trim();
   			if(colorLookUpName.contains("Secondary"))
   			{
   				Combo comboObj1=new Combo();
   				String comboArr[]=colorLookUpName.split(":");
   				colorObj.setAlias(OriginalcolorName);
   				colorObj.setName(comboArr[0]);
   				
   				comboObj.setType("secondary");
   				comboObj.setName(comboArr[2]);
   				

   				comboObj1.setType("trim");
   				comboObj1.setName(comboArr[4]);
   				
   				
   				combolist.add(comboObj);
   				combolist.add(comboObj1);
   				
   				colorObj.setCombos(combolist);
   				
   			}
   			else if(colorLookUpName.contains("Combo"))
   			{ String comboArr[]=colorLookUpName.split(":");
   			colorObj.setAlias(OriginalcolorName);
   			colorObj.setName(comboArr[0]);
   			
   			comboObj.setType("secondary");
   			comboObj.setName(comboArr[2]);
   			combolist.add(comboObj);
   			colorObj.setCombos(combolist);
   				
   			}else
   			{
   			colorObj.setAlias(OriginalcolorName);
   			colorObj.setName(colorLookUpName);
   			}
   			//if(){
   			listOfProductColors.add(colorObj);
   			//}
   		}
   		return listOfProductColors;
   	}
	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}
	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}
}




