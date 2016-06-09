package com.a4tech.v2.criteria.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.util.ApplicationConstants;
import com.a4tech.v2.core.model.BlendMaterial;
import com.a4tech.v2.core.model.Combo;
import com.a4tech.v2.core.model.Material;



public class ProductMaterialParser {

	 private Logger              _LOGGER              = Logger.getLogger(getClass());
	 
	 public List<Material> getMaterialCriteria(String material){//Cotton,Other=othertest,
		//material="Acrylic Plastic:Combo:Polyethylene (PE)=Acrylic Plastic,Blend:Wool:70:Canvas:30=Blend: Wool/Canvas (70/30);
		
			List<Material> mtrlList=new ArrayList<Material>();
			try{
			List<BlendMaterial> blendList=new ArrayList<BlendMaterial>();
			Material mtrlObj ;
			Combo combo;
			BlendMaterial blendObj;
			 String tempValue;
			String mtrlArr[]=material.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
			boolean isCombo=false;
			boolean isBlendValue=false;
			boolean isPairValue=false;
			for (String value : mtrlArr) {
				mtrlObj=new Material();
				combo=new Combo();
	            String originalValue = value;
	             tempValue=value;
	            int index = value.indexOf(ApplicationConstants.CONST_STRING_EQUAL);

	            if (index != -1) {
	                value = value.substring(0, index);
	                originalValue = originalValue.substring(index + 1);
	                isPairValue=true;
	            }
	            isCombo = isComboMaterial(value);
	 		    isBlendValue=isBlendMaterial(value);
	            
	            if(index==-1 &&!isCombo &&!isBlendValue){
	            	mtrlObj.setName(value);
	            	mtrlObj.setAlias(value);
	            }else if(isPairValue &&!isCombo &&!isBlendValue){
	            	mtrlObj.setName(value);
	            	mtrlObj.setAlias(originalValue);
	            }else if(isCombo && !isBlendValue){
	               String mtrlArray[]=value.split(ApplicationConstants.CONST_DELIMITER_COLON);
	        		String primryMtrlName=mtrlArray[0];
	        		//System.out.println("main material name "+primryMtrlName);
	        		String alias=null;
	                int indexAlias = tempValue.indexOf(ApplicationConstants.CONST_STRING_EQUAL);
	                 
	                if (indexAlias != -1) {
	                	 alias = tempValue.substring(indexAlias + 1);
	                 }
	                 int comboIndex=tempValue.indexOf(ApplicationConstants.CONST_STRING_COMBO_TEXT);
	                 String comboStr=tempValue.substring(comboIndex+6);//(comboIndex + 6);
	                 String comboMtrlName=comboStr.substring(0, comboStr.indexOf(ApplicationConstants.CONST_STRING_EQUAL));
	                 //System.out.println("comboMtrlName "+comboMtrlName);
	                 
	                 mtrlObj.setName(primryMtrlName);
	                 mtrlObj.setAlias(alias);
	                 combo.setName(comboMtrlName);
	                 mtrlObj.setCombo(combo);	
	            }else if(isBlendValue && !isCombo){
	            	String blendArr[]=tempValue.split(ApplicationConstants.CONST_STRING_EQUAL);
	            	
	            	BlendMaterial blendObj1=new BlendMaterial();
	            	BlendMaterial blendObj2=new BlendMaterial();
	            	String values=blendArr[0];
	            	String valuesArr[]=values.split(ApplicationConstants.CONST_DELIMITER_COLON);
	            	String alias=blendArr[1];
	            	
	            	String mainName[]=alias.split(ApplicationConstants.CONST_DELIMITER_COLON);
	            	
	            	mtrlObj.setName(mainName[0]);
	            	mtrlObj.setAlias(alias);
	            	
	            	blendObj1.setName(valuesArr[1]);
	            	blendObj1.setPercentage(valuesArr[2]);
	            	blendObj2.setName(valuesArr[3]);
	            	blendObj2.setPercentage(valuesArr[4]);
	            	blendList.add(blendObj1);
	            	blendList.add(blendObj2);
	            	mtrlObj.setBlendMaterials(blendList);
	            	
	            	
	            }
	            mtrlList.add(mtrlObj);
			}
			}catch(Exception e){
				_LOGGER.error("Error while processing Material :"+e.getMessage());             
			   	return null;
			   	
			   }
			return mtrlList;
		}
	
	
	private boolean isComboMaterial(String value) {
    	boolean result = false;
    	if(value.contains(ApplicationConstants.COMBO_VALUES_SEPARATOR)) {
    		String comboValues[] = value.split(ApplicationConstants.COMBO_VALUES_SEPARATOR);
    		result = (comboValues.length % 2 == 1) && comboValues[ApplicationConstants.COMBO_TEXT_VALUE_INDEX].equalsIgnoreCase(ApplicationConstants.CONST_STRING_COMBO_TEXT);
    	}
    	return result;
}
	
	private boolean isBlendMaterial(String value) {
    	boolean result = false;
    	/*if(value.contains(COMBO_VALUES_SEPARATOR)) {
    		String comboValues[] = value.split(COMBO_VALUES_SEPARATOR);
    		result = (comboValues.length % 2 == 1) && comboValues[COMBO_TEXT_VALUE_INDEX].equalsIgnoreCase( CONST_STRING_BLEND_TEXT);
    	}*/
    	
    	if(value.contains(ApplicationConstants.CONST_STRING_BLEND_TEXT)){
    		result=true;
    	}
    	return result;
   }   
	
	
}
