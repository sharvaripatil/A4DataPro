package com.a4tech.product.broberry.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.a4tech.dataStore.ProductDataStore;
import com.a4tech.product.broberry.mapping.BroberryExcelMapping;
import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.AvailableVariations;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;
import com.a4tech.product.model.Configurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.util.ApplicationConstants;


public class BroberryProductAttributeParser {
	private static final Logger _LOGGER = Logger.getLogger(BroberryProductAttributeParser.class);
	
	public List<Color> getColorCriteria(Set <String> colorSet) {
		Color colorObj = null;
		List<Color> colorList = new ArrayList<Color>();
		Iterator<String> colorIterator=colorSet.iterator();
		try {
			List<Combo> comboList = null;
			
		while (colorIterator.hasNext()) {
			String value = (String) colorIterator.next();
			boolean isCombo = false;
				colorObj = new Color();
				comboList = new ArrayList<Combo>();
    			isCombo = isComboColors(value);
				if (!isCombo) {
					String colorName=ApplicationConstants.COLOR_MAP.get(value.trim());
					if(StringUtils.isEmpty(colorName)){
						colorName=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
					}
					colorObj.setName(colorName);
					colorObj.setAlias(value.trim());
					colorList.add(colorObj);
				} else {
					//245-Mid Brown/Navy
					String colorArray[] = value.split(ApplicationConstants.CONST_DELIMITER_FSLASH);
					String combo_color_1=ApplicationConstants.COLOR_MAP.get(colorArray[0].trim());
					if(StringUtils.isEmpty(combo_color_1)){
						combo_color_1=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
					}
					colorObj.setName(combo_color_1);
					colorObj.setAlias(value);
					
					Combo comboObj = new Combo();
					String combo_color_2=ApplicationConstants.COLOR_MAP.get(colorArray[1].trim());
					if(StringUtils.isEmpty(combo_color_2)){
						combo_color_2=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
					}
					comboObj.setName(combo_color_2.trim());
					comboObj.setType(ApplicationConstants.CONST_STRING_SECONDARY);
					comboList.add(comboObj);
					colorObj.setCombos(comboList);
					colorList.add(colorObj);
				}
		}
		} catch (Exception e) {
			_LOGGER.error("Error while processing Color :" + e.getMessage());
			return new ArrayList<Color>();
		}
		_LOGGER.info("Colors Processed");
		return colorList;
		
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
		return pnumberList;		
	}
	
	private boolean isComboColors(String value) {
		boolean result = false;
		if (value.contains("/")) {
			result = true;
		}
		return result;
	}

public Volume getItemWeight(String FabricWT)
{
	  Volume itemWeight=new Volume();
	  List<Values> valuesList = new ArrayList<Values>(); 
	  List<Value> valueList = new ArrayList<Value>(); 

	  Value valueObj=new Value(); 
	  Values valuesObj=new Values(); 

	  FabricWT=FabricWT.replaceAll("Ounces","");
	  valueObj.setValue(FabricWT.trim());
	  valueObj.setUnit("Oz");
	  valueList.add(valueObj);
	  
	  valuesObj.setValue(valueList);
	  valuesList.add(valuesObj);
	  
	  itemWeight.setValues(valuesList); 
	return itemWeight;
	
}

public Size getProductSize(List<String> sizeValues){
		Size size = new Size();
		Apparel appareal = new Apparel();
		List<Value> values=new ArrayList<>();
		Dimension dimensionObj = new Dimension();
		List<Values> valuesList = new ArrayList<Values>();
		Value valueObj=new Value();
		int count=1;
		String sizeArr[]={};
		try{
		for (String string : sizeValues) {
			string=ApplicationConstants.SIZE_MAP.get(string);
			valueObj=new Value();
			if(string.contains("Dimension")){
				sizeArr=string.split(ApplicationConstants.CONST_SIZE_DELIMITER);
				//Apparel-Waist/Inseam
				string=sizeArr[1];
					String DimenArr[] = string.split(ApplicationConstants.CONST_DELIMITER_SEMICOLON);
					List<Value> valuelist=new ArrayList<Value>();
					Values valuesObj  = new Values();
					Value valObj;
					int valCount=1;
					for (String value : DimenArr) {
					String[] DimenArr1 = value.split(ApplicationConstants.CONST_DELIMITER_COLON);
					valObj = new Value();
						for (String value1 : DimenArr1) {
							if(valCount==1){
							valObj.setAttribute(value1);
							}else if(valCount==2){
							valObj.setValue(value1);
							}else if(valCount==3){
							valObj.setUnit(value1);
							}
							valCount++;
						}
						valuelist.add(valObj);
						valCount=1;
					}
					valuesObj.setValue(valuelist);
					valuesList.add(valuesObj);
					dimensionObj.setValues(valuesList);
					size.setDimension(dimensionObj);
			}else{
			if(count==1){
				sizeArr=string.split(ApplicationConstants.CONST_SIZE_DELIMITER);
				//Apparel-Waist/Inseam
				String tempValue=sizeArr[0];
				if(tempValue.contains("Pants")){
					tempValue="Apparel-Waist/Inseam";
				}else if(tempValue.contains("Numbered")){
					tempValue="Standard & Numbered";
				}
				appareal.setType(tempValue);
				valueObj.setValue(sizeArr[1]);
				ProductDataStore.saveSizesBrobery(sizeArr[1]);
				values.add(valueObj);
				
			}else{
			sizeArr=string.split(ApplicationConstants.CONST_SIZE_DELIMITER);
			valueObj.setValue(sizeArr[1]);
			ProductDataStore.saveSizesBrobery(sizeArr[1]);
			values.add(valueObj);
			}
			count=0;
			appareal.setValues(values);
			size.setApparel(appareal);
		}
		}
		}catch(Exception e){
			_LOGGER.error("error while processing sizes" +e.getMessage());
			return new Size();
		}
		return size;
	}

public  List<Option> getOptions(ArrayList<String> optionValues) {
	List<Option> optionList=new ArrayList<>();
	Option optionObj=new Option();
	   try{
		   List<OptionValue> valuesList=new ArrayList<OptionValue>();
			 OptionValue optionValueObj=null;
			  for (String optionDataValue: optionValues) {
				  optionObj=new Option();
				  valuesList=new ArrayList<OptionValue>();
				  optionValueObj=new OptionValue();
				  optionValueObj.setValue(optionDataValue);
				  valuesList.add(optionValueObj);
				  optionObj.setOptionType("Product");
				  optionObj.setName("Size Choice "+optionDataValue);
				  optionObj.setValues(valuesList); 
				  optionObj.setAdditionalInformation("");
				  optionObj.setCanOnlyOrderOne(false);
				  optionObj.setRequiredForOrder(true);
				  optionList.add(optionObj);
			  }
			  
	   }catch(Exception e){
		   _LOGGER.error("Error while processing Options :"+e.getMessage());          
	      return new ArrayList<Option>();
	      
	     }
	  return optionList;
	  
	 }

public List<Availability> getProductAvailablity(Set<String> parentList,Set<String> childList){
	List<Availability> listOfAvailablity = new ArrayList<>();
	try{
	Availability  availabilityObj = new Availability();
	AvailableVariations  AvailableVariObj = null;
	List<AvailableVariations> listOfVariAvail = new ArrayList<>();
	List<Object> listOfParent = null;
	List<Object> listOfChild = null;
	for (String ParentValue : parentList) { 
		 for (String childValue : childList) {
			 AvailableVariObj = new AvailableVariations();
			 listOfParent = new ArrayList<>();
			 listOfChild = new ArrayList<>();
			 listOfParent.add(ParentValue);
			 listOfChild.add(childValue);
			 AvailableVariObj.setParentValue(listOfParent);
			 AvailableVariObj.setChildValue(listOfChild);
			 listOfVariAvail.add(AvailableVariObj);
		}
	}
	availabilityObj.setAvailableVariations(listOfVariAvail);
	availabilityObj.setParentCriteria(ApplicationConstants.CONST_STRING_SIZE);
	availabilityObj.setChildCriteria("Product Option");
	listOfAvailablity.add(availabilityObj);
	ProductDataStore.clearSizesBrobery();
	}catch(Exception e){
	   _LOGGER.error("Error while processing Options :"+e.getMessage());          
   return new ArrayList<Availability>();
   
	}
	return listOfAvailablity;
	}
	
}
