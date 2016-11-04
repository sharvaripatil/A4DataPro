package com.a4tech.apparel.products.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.a4tech.dataStore.ProductDataStore;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.AdditionalLocation;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.Personalization;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class ApparealProductAttributeParser {
	private LookupServiceData lookupServiceData;
	
	public List<Color> getProductColors(Set<String> colorNames,Map<String, String> ids){
		 Color colorObj = null;
		 Combo comboObj = null;
		 List<Combo> listOfCombo = null;
		 String[] colors;
		 String aliasName ="";
		 List<Color> listtOfColor = new ArrayList<>();
		 for (String colorName : colorNames) {
			 colorObj = new Color();
			 listOfCombo = new ArrayList<>();
			 colorName = colorName.trim();
			String colorCode = ids.get(colorName);
		    if(colorName.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)){
			 colors = colorName.split(ApplicationConstants.CONST_DELIMITER_FSLASH);
			 String primaryColor = colors[ApplicationConstants.CONST_NUMBER_ZERO];
			 String secondaryColor = colors[ApplicationConstants.CONST_INT_VALUE_ONE];
			int noOfColors = colors.length;
			comboObj = new Combo();
			if(noOfColors == ApplicationConstants.CONST_INT_VALUE_TWO){
				colorObj.setName(primaryColor);
				comboObj.setName(secondaryColor);
				comboObj.setType(ApplicationConstants.CONST_STRING_SECONDARY);
				aliasName = CommonUtility.appendStrings(primaryColor, secondaryColor,
                        						ApplicationConstants.CONST_DELIMITER_HYPHEN);
				listOfCombo.add(comboObj);
				colorObj.setAlias(aliasName);
				colorObj.setCombos(listOfCombo);
				ProductDataStore.saveColorNames(aliasName);
			}else if(noOfColors == ApplicationConstants.CONST_INT_VALUE_THREE){
				String thirdColor = colors[ApplicationConstants.CONST_INT_VALUE_TWO];
				colorObj.setName(primaryColor);
				for(int count=1;count <=2;count++){
					comboObj = new Combo();
					if(count == ApplicationConstants.CONST_INT_VALUE_ONE){
						comboObj.setName(secondaryColor);
						comboObj.setType(ApplicationConstants.CONST_STRING_SECONDARY);
						aliasName = CommonUtility.appendStrings(primaryColor, secondaryColor,
                                                   ApplicationConstants.CONST_DELIMITER_HYPHEN);
					}else{
						comboObj.setName(thirdColor);
						comboObj.setType(ApplicationConstants.CONST_STRING_TRIM);
						aliasName = CommonUtility.appendStrings(aliasName, thirdColor,
                                                          ApplicationConstants.CONST_DELIMITER_HYPHEN);
					}
					listOfCombo.add(comboObj);
				}
				colorObj.setAlias(aliasName);
				colorObj.setCombos(listOfCombo);
				ProductDataStore.saveColorNames(aliasName);
				
			}else{
				
			}
			  
		  }else{
			  colorObj.setName(colorName);
			  colorObj.setAlias(colorName);
			  colorObj.setCustomerOrderCode(colorCode);
			  ProductDataStore.saveColorNames(colorName);
		  }
		listtOfColor.add(colorObj);
	}
		return listtOfColor;
	}
   
	public Set<Value> getSizeValues(String sizeValue,Set<Value> existingSizeValues){
		Value valueObj = new Value();
		valueObj.setValue(sizeValue);
		existingSizeValues.add(valueObj);
		
		return existingSizeValues;
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
	
	public List<ImprintMethod> getImprintMethod(String data){
		
		data = data.replaceAll("(&|and|or)", ApplicationConstants.CONST_DELIMITER_COMMA);
		ImprintMethod	imprintMethodObj = null;	
		List<ImprintMethod> listOfImprintMethod = new ArrayList<>();
		String[] imprintMethodValues = data.split(ApplicationConstants.CONST_DELIMITER_COMMA);
		for (String imprintMethodName : imprintMethodValues) {
			imprintMethodObj = new ImprintMethod();
			if(imprintMethodName.contains("Embroidery")){
			imprintMethodName=imprintMethodName.replace("Embroidery", "Embroidered");
		    }
	    	if(imprintMethodName.contains(" Appliqué")){
    		imprintMethodName=imprintMethodName.replaceAll("é", "e");
	        }
			  if(lookupServiceData.isImprintMethod(imprintMethodName)){
				  imprintMethodObj.setType(imprintMethodName);
				  imprintMethodObj.setAlias(imprintMethodName);
			  }else{
				  imprintMethodObj.setType(ApplicationConstants.CONST_VALUE_TYPE_OTHER);
				  imprintMethodObj.setAlias(imprintMethodName);
			  }
			  listOfImprintMethod.add(imprintMethodObj);
		}
		return listOfImprintMethod;
	}
	public List<ProductionTime> getProductionTimeList(String prdTime){
		List<ProductionTime> listOfProductionTime = new ArrayList<>();
		ProductionTime proTimeObj = null;
		if(prdTime.contains("Blank: 24 hours")){
			proTimeObj = new ProductionTime();
			proTimeObj.setBusinessDays("1");
			proTimeObj.setDetails("Blank: 24 hours");
			listOfProductionTime.add(proTimeObj);
		}
		if(prdTime.contains("5-10"))
		{
			proTimeObj = new ProductionTime();
			proTimeObj.setBusinessDays("5-10");
			proTimeObj.setDetails("Decorated");
			listOfProductionTime.add(proTimeObj);
		}
		if(prdTime.contains("7-10"))
		{
			proTimeObj = new ProductionTime();
			proTimeObj.setBusinessDays("7-10");
			proTimeObj.setDetails("Decorated - after proof approval");
			listOfProductionTime.add(proTimeObj);
		}
		
		return listOfProductionTime;
	}
	
	public Product setImrintSizeAndLocation(String imprintValue,Product existingProduct){
		  ProductConfigurations productConfigObj = existingProduct.getProductConfigurations();
		  ImprintLocation ImprintLocationObj = new ImprintLocation();
		  ImprintSize ImprintSizeObj = new ImprintSize();
		  List<ImprintSize> ImprintSizeList = null;
		  List<ImprintLocation> ImprintLocationList = new ArrayList<>();
		  AdditionalLocation additionalLoactionObj = null;
		  List<AdditionalLocation> additionalLoaction  = null;
	        imprintValue=imprintValue.replace("other",ApplicationConstants.CONST_VALUE_TYPE_OTHER);
	     if(imprintValue.contains(ApplicationConstants.PARENTHESE_OPEN_SYMBOL))
	     {
	    	 ImprintSizeList = new ArrayList<>();
	     String imprintSize= imprintValue.substring(imprintValue.indexOf("size")+5, imprintValue.indexOf
	    		                                          (ApplicationConstants.PARENTHESE_CLOSE_SYMBOL));
	     ImprintSizeObj.setValue(imprintSize);
	     ImprintSizeList.add(ImprintSizeObj);
	     productConfigObj.setImprintSize(ImprintSizeList);
	     ImprintLocationObj.setValue(imprintValue.substring(0,imprintValue.indexOf
	    		                                          (ApplicationConstants.PARENTHESE_OPEN_SYMBOL)));
	     ImprintLocationList.add(ImprintLocationObj);
	     
	     }else if(imprintValue.contains(ApplicationConstants.CONST_VALUE_TYPE_OTHER))
	     {
	    	 additionalLoactionObj = new AdditionalLocation();
	    	 additionalLoaction = new ArrayList<>();
	     String  imprintLocation=imprintValue.replace("Standard location:", ApplicationConstants.CONST_STRING_BIG_SPACE);
	     String imprintLocationArr[]=imprintLocation.split(ApplicationConstants.CONST_VALUE_TYPE_OTHER) ;
	     ImprintLocationObj.setValue(imprintLocationArr[0].trim()); 
	     ImprintLocationList.add(ImprintLocationObj);
	     additionalLoactionObj.setName(ApplicationConstants.CONST_VALUE_TYPE_OTHER+imprintLocationArr[1].trim());
	     additionalLoaction.add(additionalLoactionObj);
	     productConfigObj.setAdditionalLocations(additionalLoaction); 
	                  }else
	     {
	      ImprintLocationObj.setValue(imprintValue);
	      ImprintLocationList.add(ImprintLocationObj);
	      
	     }
	     productConfigObj.setImprintLocation(ImprintLocationList); 
		existingProduct.setProductConfigurations(productConfigObj);
		
		return existingProduct;
	}
	
	public List<Packaging> getProductPackaging(String packValue){
		packValue = packValue.replaceAll("\"",ApplicationConstants.CONST_STRING_EMPTY);
		packValue = packValue.replaceAll(ApplicationConstants.CONST_STRING_NEWLINECHARS,
				                                   ApplicationConstants.CONST_STRING_BIG_SPACE);
		List<Packaging> listOfpackaging = new ArrayList<Packaging>();
		Packaging pack = null;
    	if(packValue.contains("Decorated"))
		{
    		for(int index=1 ;index <=2 ;index++){
    			 pack = new Packaging();
    			  if(index == 1){
    				pack = getPackaging("Blank: Individually folded in polybags");  
    			  }else if(index ==2){
    				  pack = getPackaging("Decorated: Bulk folded");  
    			  }
    			  listOfpackaging.add(pack);
    		}
		}else{
			pack = getPackaging(packValue);
			listOfpackaging.add(pack);
		}
		return listOfpackaging;
		
	}
	public  Packaging getPackaging(String packName)
	{
		Packaging packObj = new Packaging();
		packObj.setName(packName);
		return packObj;
	}
	
	public List<Personalization> getPersonalizationList(List<Personalization> existingPersList){
		List<Personalization> listOfPersonalization = new ArrayList<>();
		Personalization personalObj = null;
		if(existingPersList != null){
			for (Personalization personalization : existingPersList) {
				personalObj = new Personalization();
				  if(personalization.getType().equalsIgnoreCase("Personalization")  && 
						          personalization.getAlias().equalsIgnoreCase("Personalized")){
					  personalObj.setType(ApplicationConstants.CONST_STRING_PERSONALIZATION);
					  personalObj.setAlias(ApplicationConstants.CONST_STRING_PERSONALIZATION);
					  listOfPersonalization.add(personalObj);
				  }else{
					  listOfPersonalization.add(personalization);
				  }  
			}
		}
		
		return listOfPersonalization;
	}
	
	public LookupServiceData getLookupServiceData() {
		return lookupServiceData;
	}

	public void setLookupServiceData(LookupServiceData lookupServiceData) {
		this.lookupServiceData = lookupServiceData;
	}
	
	
	 
}
