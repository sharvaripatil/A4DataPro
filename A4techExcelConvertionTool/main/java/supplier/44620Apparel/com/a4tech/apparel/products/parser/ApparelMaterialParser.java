package com.a4tech.apparel.products.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.BlendMaterial;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Material;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class ApparelMaterialParser {
	private LookupServiceData lookupServiceData;
	private String specialCharacters = "[™®]";
	
	//@author Venkat
	public List<Material> getMaterialList(String originalMaterialvalue){
		
		Material materialObj = new Material();
		List<Material> listOfMaterial = new ArrayList<>();
		if(!StringUtils.isEmpty(originalMaterialvalue)){
			originalMaterialvalue = CommonUtility.removeSpecialSymbols(originalMaterialvalue,specialCharacters);
			originalMaterialvalue = originalMaterialvalue.replaceAll("é", "e");
		}
		List<String> listOfLookupMaterial = getMaterialType(originalMaterialvalue.toUpperCase());
		if(!listOfLookupMaterial.isEmpty()){
			int numOfMaterials = listOfLookupMaterial.size();
			if(!originalMaterialvalue.contains("/yd") && isBlendMaterial(originalMaterialvalue)){  
				 String[] values = CommonUtility.getValuesOfArray(originalMaterialvalue,ApplicationConstants.CONST_DELIMITER_FSLASH);
		    	 BlendMaterial blentMaterialObj = null;
		    	 List<BlendMaterial> listOfBlendMaterial = new ArrayList<>();
				     if(values.length == ApplicationConstants.CONST_INT_VALUE_TWO){
				    	 for (String materialValue : values) {
				    		 blentMaterialObj = new BlendMaterial();
				    		 if(materialValue.contains("blend")){
				    			 materialValue = materialValue.replaceAll("blend",ApplicationConstants.CONST_STRING_EMPTY);
				    			}
				    		 if(materialValue.contains("Blend")){
				    			 materialValue = materialValue.replaceAll("Blend",ApplicationConstants.CONST_STRING_EMPTY);
				    			}
				    		 List<String> matlTypeList = getMaterialType(materialValue.toUpperCase());
				    		 String mtrlType = ApplicationConstants.CONST_STRING_EMPTY;
				    		 if(!CollectionUtils.isEmpty(matlTypeList)){
				    			 mtrlType = matlTypeList.get(ApplicationConstants.CONST_NUMBER_ZERO);
				    		 }  else {
				    			 mtrlType = "Other Fabric";  
				    		 }
				    		 if(materialValue.contains(ApplicationConstants.CONST_DELIMITER_PERCENT_SIGN)){
								  String percentage = materialValue.split(ApplicationConstants.CONST_DELIMITER_PERCENT_SIGN)[0];
								  percentage = percentage.replaceAll("[^0-9]", ApplicationConstants.CONST_STRING_EMPTY);
								  blentMaterialObj.setName(mtrlType);
								  blentMaterialObj.setPercentage(percentage.trim());
								  listOfBlendMaterial.add(blentMaterialObj);
							  }
				    	 }
				    	 materialObj.setName("Blend");
				    	 materialObj.setAlias(originalMaterialvalue);
				    	 materialObj.setBlendMaterials(listOfBlendMaterial);
			    		 listOfMaterial.add(materialObj);
				     }else{
				    	 Combo comboObj = new Combo();
				    	 for (String materialValue : values) {
				    		 blentMaterialObj = new BlendMaterial();
							  String mtrlType = getMaterialType(materialValue.toUpperCase()).toString();
							  if(materialValue.contains(ApplicationConstants.CONST_DELIMITER_PERCENT_SIGN)){
								  String percentage = materialValue.split(ApplicationConstants.CONST_DELIMITER_PERCENT_SIGN)[0];
								  if(!StringUtils.isEmpty(mtrlType)){
									  blentMaterialObj.setName(mtrlType);  
								  }else{
									blentMaterialObj.setName(ApplicationConstants.CONST_STRING_OTHER_FABRIC);  
								  }
								  blentMaterialObj.setPercentage(percentage.trim());
								  listOfBlendMaterial.add(blentMaterialObj);
							  }else{
								  materialObj.setName(mtrlType);
								  materialObj.setAlias(originalMaterialvalue);  
							  }
						} 
				    	 comboObj.setBlendMaterials(listOfBlendMaterial);
				    	 comboObj.setName(ApplicationConstants.CONST_STRING_BLEND_TEXT);
				    	 materialObj.setCombo(comboObj);
				    	 listOfMaterial.add(materialObj);
				     }	        
			  } else if(numOfMaterials == ApplicationConstants.CONST_INT_VALUE_ONE){ 
				  materialObj = getMaterialValue(listOfLookupMaterial.toString(), originalMaterialvalue);
				  listOfMaterial.add(materialObj);
			  }else if(numOfMaterials == ApplicationConstants.CONST_INT_VALUE_TWO){
				   materialObj = getMaterialValue(listOfLookupMaterial.toString(), originalMaterialvalue, 
						                                  ApplicationConstants.CONST_STRING_COMBO_TEXT);
				   listOfMaterial.add(materialObj);
			  }
		}else{ 
			materialObj = getMaterialValue(ApplicationConstants.CONST_VALUE_TYPE_OTHER, originalMaterialvalue);
			listOfMaterial.add(materialObj);
		}
		return listOfMaterial;
	}
	
	// @author Venkat
	 
	public List<String> getMaterialType(String value){
		
		List<String> listOfLookupMaterials = lookupServiceData.getMaterialValues();
		List<String> finalMaterialValues = listOfLookupMaterials.stream()
				                                  .filter(mtrlName -> value.contains(mtrlName))
				                                  .collect(Collectors.toList());
                                                 
				
		return finalMaterialValues;	
	}
	/*
	 * @author Venkat
	 * @param String String ,type of material alias name
	 * @description this method design for setting the material name and alias name for single value
	 * @return Material Object 
	 */
	public Material getMaterialValue(String name,String alias){
		Material materialObj = new Material();
		name = CommonUtility.removeCurlyBraces(name);
		materialObj.setName(name);
		materialObj.setAlias(alias);
		return materialObj;
	}
	/*
	 * @author Venkat
	 * @param String String ,type of material alias name
	 * @description this method design for setting the material name and alias name for combo 
	 * @return Material Object 
	 */
	public Material getMaterialValue(String name,String alias ,String materialType){
		Material materialObj = new Material();
		 Combo comboObj = null;
		 String[] materials = null;
		 name = CommonUtility.removeCurlyBraces(name);
		if(name.contains(ApplicationConstants.CONST_DELIMITER_COMMA)){
			materials = name.split(ApplicationConstants.CONST_DELIMITER_COMMA); 
			materialObj.setName(materials[0]);
			materialObj.setAlias(alias);
			comboObj = new Combo();
        	comboObj.setName(materials[1]);
        	materialObj.setCombo(comboObj);
		}
		return materialObj;
	}
	/*
	 * @author Venkat
	 * @param String
	 * @description This method is check giving value is combo or not
	 * @return boolean
	 */
	public boolean iscombo(String data){  //100% polyester fleece, 300gsm or 8.85 oz./yd2
		  if(data.contains("/yd")){
			  return true;
		  }
		return false;
	}
	/*
	 * @author Venkat
	 * @param String
	 * @description This method is check giving value is blend material or not
	 * @return boolean
	 */
	public boolean isBlendMaterial(String data){
		boolean isBlend = false;
		if(data.contains(ApplicationConstants.CONST_DELIMITER_FSLASH) && 
				              data.contains(ApplicationConstants.CONST_DELIMITER_PERCENT_SIGN)){
			isBlend = true;
		}else if(data.split(ApplicationConstants.CONST_DELIMITER_FSLASH).length == 
				                    ApplicationConstants.CONST_INT_VALUE_THREE){
			isBlend = true;
		}
		if(isBlend){
			
		}
		return isBlend;
	}
	public LookupServiceData getLookupServiceData() {
		return lookupServiceData;
	}
	public void setLookupServiceData(LookupServiceData lookupServiceData) {
		this.lookupServiceData = lookupServiceData;
	}
	
}
