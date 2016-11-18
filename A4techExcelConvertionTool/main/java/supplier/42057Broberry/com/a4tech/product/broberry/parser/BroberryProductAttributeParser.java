package com.a4tech.product.broberry.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.a4tech.product.broberry.mapping.BroberryExcelMapping;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;
import com.a4tech.product.model.Configurations;
import com.a4tech.product.model.ProductNumber;
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
					String alias = value.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH,ApplicationConstants.CONST_DELIMITER_HYPHEN);
					String combo_color_1=ApplicationConstants.COLOR_MAP.get(colorArray[0].trim());
					if(StringUtils.isEmpty(combo_color_1)){
						combo_color_1=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
					}
					colorObj.setName(combo_color_1);
					colorObj.setAlias(alias);
					
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
	
}
