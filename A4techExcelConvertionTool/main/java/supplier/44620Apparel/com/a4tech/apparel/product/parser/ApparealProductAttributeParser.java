package com.a4tech.apparel.product.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.a4tech.dataStore.ProductDataStore;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class ApparealProductAttributeParser {
	
	public Set<Color> getProductColors(String colorName,String colorCode ,Set<Color> existingColorsList){
		 Color colorObj = new Color();
		 Combo comboObj = null;
		 List<Combo> listOfCombo = new ArrayList<>();
		 String[] colors;
		 String aliasName ="";
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
				colorObj.setCombos(listOfCombo);
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
		  existingColorsList.add(colorObj);
		return existingColorsList;
	}
   
	public Set<Value> getSizeValues(String sizeValue,Set<Value> existingSizeValues){
		Value valueObj = new Value();
		valueObj.setValue(sizeValue);
		existingSizeValues.add(valueObj);
		
		return existingSizeValues;
	}
	
	public Volume getItemWeightvolume(String itemWeightValue){
		List<Value> listOfValue = new ArrayList<>();
		List<Values> listOfValues = new ArrayList<>();
		Volume volume  = new Volume();
		Values values = new Values();
		Value valueObj = new Value();
		valueObj.setValue(itemWeightValue);
		valueObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_WEIGHT);
		listOfValue.add(valueObj);
		values.setValue(listOfValue);
		listOfValues.add(values);
		volume.setValues(listOfValues);
		return volume;
	}
}
