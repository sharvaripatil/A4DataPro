package com.a4tech.product.riversend.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.a4tech.dataStore.ProductDataStore;
import com.a4tech.product.broberry.parser.BroberryProductAttributeParser;
import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.util.ApplicationConstants;

public class RiverEndAttributeParser {

	private static final Logger _LOGGER = Logger.getLogger(BroberryProductAttributeParser.class);
	
	public List<Color> getColorCriteria(Set <String> colorSet) {
		Color colorObj = null;
		List<Color> colorList = new ArrayList<Color>();
		Iterator<String> colorIterator=colorSet.iterator();
		try {
			List<Combo> comboList = null;
			
		while (colorIterator.hasNext()) {
			String value = (String) colorIterator.next();
			
			String tempcolorArray[]=value.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
			for (String colorVal : tempcolorArray) {
			String strColor=colorVal;
			strColor=strColor.replaceAll("&","/");
			strColor=strColor.replaceAll(" w/","/");
			strColor=strColor.replaceAll(" W/","/");
			boolean isCombo = false;
				colorObj = new Color();
				comboList = new ArrayList<Combo>();
    			isCombo = isComboColors(strColor);
				if (!isCombo) {
					String colorName=MappingClass.COLOR_MAP.get(strColor.trim());
					if(StringUtils.isEmpty(colorName)){
						colorName=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
					}
					colorObj.setName(colorName);
					colorObj.setAlias(colorVal.trim());
					colorList.add(colorObj);
				} else {
					//245-Mid Brown/Navy
					String colorArray[] = strColor.split(ApplicationConstants.CONST_DELIMITER_FSLASH);
					//if(colorArray.length==2){
					String combo_color_1=MappingClass.COLOR_MAP.get(colorArray[0].trim());
					if(StringUtils.isEmpty(combo_color_1)){
						combo_color_1=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
					}
					colorObj.setName(combo_color_1);
					colorObj.setAlias(strColor);
					
					Combo comboObj = new Combo();
					String combo_color_2=MappingClass.COLOR_MAP.get(colorArray[1].trim());
					if(StringUtils.isEmpty(combo_color_2)){
						combo_color_2=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
					}
					comboObj.setName(combo_color_2.trim());
					comboObj.setType(ApplicationConstants.CONST_STRING_SECONDARY);
					if(colorArray.length==3){
						String combo_color_3=MappingClass.COLOR_MAP.get(colorArray[2].trim());
						if(StringUtils.isEmpty(combo_color_3)){
							combo_color_3=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
						}
						Combo comboObj2 = new Combo();
						comboObj2.setName(combo_color_3.trim());
						comboObj2.setType(ApplicationConstants.CONST_STRING_TRIM);
						comboList.add(comboObj2);
					}
					comboList.add(comboObj);
					colorObj.setCombos(comboList);
					colorList.add(colorObj);
					//}else if(colorArray.length==3){
						/*String combo_color_1=ApplicationConstants.COLOR_MAP.get(colorArray[0].trim());
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
						String combo_color_3=ApplicationConstants.COLOR_MAP.get(colorArray[2].trim());
						if(StringUtils.isEmpty(combo_color_3)){
							combo_color_3=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
						}
						Combo comboObj2 = new Combo();
						comboObj2.setName(combo_color_3.trim());
						comboObj2.setType(ApplicationConstants.CONST_STRING_TRIM);
						comboList.add(comboObj2);
						colorObj.setCombos(comboList);
						colorList.add(colorObj);*/
					// }
				 	}
		}
		}
		} catch (Exception e) {
			_LOGGER.error("Error while processing Color :" + e.getMessage());
			return new ArrayList<Color>();
		}
		_LOGGER.info("Colors Processed");
		return colorList;
		
	}
	
	private boolean isComboColors(String value) {
		boolean result = false;
		if (value.contains("/")) {
			result = true;
		}
		return result;
	}
	
	public Size getProductSize(List<String> sizeValues){
		Size size = new Size();
		Apparel appareal = new Apparel();
		List<Value> values=new ArrayList<>();
		//Dimension dimensionObj = new Dimension();
		//List<Values> valuesList = new ArrayList<Values>();
		Value valueObj=new Value();
		int count=1;
		try{
		for (String string : sizeValues) {
			//string=ApplicationConstants.SIZE_MAP.get(string);
			valueObj=new Value();
			if(count==1){
				appareal.setType("Standard & Numbered");
				valueObj.setValue(string);
				//ProductDataStore.saveSizesBrobery(sizeArr[1]);
				values.add(valueObj);
				
			}else{
			valueObj.setValue(string);
			//ProductDataStore.saveSizesBrobery(sizeArr[1]);
			values.add(valueObj);
			}
			count=0;
		}
		appareal.setValues(values);
		size.setApparel(appareal);
		}catch(Exception e){
			_LOGGER.error("error while processing sizes" +e.getMessage());
			return new Size();
		}
		return size;
	}

}
