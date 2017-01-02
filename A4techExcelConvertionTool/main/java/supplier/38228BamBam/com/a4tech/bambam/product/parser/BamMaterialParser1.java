package com.a4tech.bambam.product.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.BlendMaterial;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Material;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class BamMaterialParser1 {
	private static final Logger _LOGGER = Logger.getLogger(BamMaterialParser1.class);
	
	public List<Material> getProductMaterials(String mtrlValue){
		List<Material> listOfMaterial = new ArrayList<>();
		List<BlendMaterial> listOfBlend = null;
		BlendMaterial blendMaterialObj = null;
		Material materialObj = null;
		Combo comboObj   = null;
		StringBuilder comboAlias  = null;
		String[] mtrlVals = CommonUtility.getValuesOfArray(mtrlValue, ApplicationConstants.CONST_STRING_COMMA_SEP);
		for (String materal : mtrlVals) {
			materialObj = new Material();
			comboAlias  = new StringBuilder();
			 if(isCombo(materal)){
				 comboObj = new Combo();
				 if(isBlend(materal)){
					 listOfBlend = new ArrayList<>();
				 }else{
					 String[] combos = CommonUtility.getValuesOfArray(mtrlValue, 
							                    ApplicationConstants.CONST_STRING_COMMA_SEP);
					 for (String comboVal : combos) {
						  if(isCombo(comboVal)){
							  String[] comboVals = CommonUtility.getValuesOfArray(mtrlValue, ApplicationConstants.COMBO_VALUES_SEPARATOR);
							//  comboObj.se
						  }
					}
				 }
				 
			 } else if(isBlend(materal)){
				 
			 }else{
				 materialObj = getMaterialValueObject(materal);
				 listOfMaterial.add(materialObj);
			 }	 
		}
		return listOfMaterial;
	}
	
	private boolean isCombo(String value){
		if(value.contains(ApplicationConstants.CONST_STRING_COMBO_TEXT)){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
	    return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	private boolean isBlend(String value){
		if(value.contains(ApplicationConstants.CONST_STRING_BLEND_TEXT)){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	private Material getMaterialValueObject(String value){
		Material mtrlObj  = new Material();
		 if(value.contains(ApplicationConstants.CONST_STRING_EQUAL)){
			 String[] mtrls = CommonUtility.getValuesOfArray(value, ApplicationConstants.CONST_STRING_EQUAL);
			 String alias = mtrls[ApplicationConstants.CONST_INT_VALUE_ONE];
			 if(alias.contains(ApplicationConstants.SQUARE_BRACKET_OPEN)){
				 alias = CommonUtility.removeCurlyBraces(alias);
			 }
			 mtrlObj.setName(mtrls[ApplicationConstants.CONST_NUMBER_ZERO]);
			 mtrlObj.setName(alias);
			 return mtrlObj;
		 }
		 return mtrlObj;
	}

}
