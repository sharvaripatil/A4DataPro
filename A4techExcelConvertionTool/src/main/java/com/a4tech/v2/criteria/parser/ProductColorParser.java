package com.a4tech.v2.criteria.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.v2.core.model.Color;
import com.a4tech.v2.core.model.Combo;


public class ProductColorParser {
	private Logger              _LOGGER              = Logger.getLogger(getClass()); 
	
	private final static String COMBO_VALUES_SEPARATOR = ":";
	 private final static Integer COMBO_TEXT_VALUE_INDEX = 1;
	 public static final String   CONST_STRING_COMBO_TEXT = "Combo";
	   
	
	 public List<Color> getColorCriteria(String color){
		 Color colorObj = null;
		 List<Color> colorList=new ArrayList<Color>();
		try{
			//color="Black|Blue";
			//color=color.replaceAll("|", "___");
		//String colorArr[]=color.split(ApplicationConstants.CONST_DELIMITER_PIPE);
			String colorArr[]=color.split("\\|");
		 boolean isCombo=false;
		 List<Combo> comboList=null;
		 Combo comboObj=new Combo();
		for (String value : colorArr) {
			colorObj=new Color();
			//String tempColor=value;
			comboList	=new ArrayList<Combo>();
			
            String originalValue = value;
            String teampValue=value;
          /*  int index = value.indexOf(ApplicationConstants.CONST_STRING_EQUAL);

            if (index != -1) {
                value = value.substring(0, index);
                originalValue = originalValue.substring(index + 1);
            }
            */
            // 
            isCombo = isComboColors(value);
            
            if(!isCombo){
            	colorObj.setName(value.trim());
            	colorObj.setAlias(value.trim());
            	colorList.add(colorObj);
            }else{ 
        		String colorArray[]=value.split("/");//ApplicationConstants.CONST_DELIMITER_FSLASH);
        		
        		//String name=colorArray[0];
        		
        		String alias=value.replaceAll("/","-");//ApplicationConstants.CONST_DELIMITER_FSLASH, ApplicationConstants.CONST_DELIMITER_HYPHEN);
                
                	// Color colorObj1=new Color();
        		colorObj.setName(colorArray[0].trim());
        		colorObj.setAlias(alias);
                	 
                	 Combo combotemp=new Combo();
                	 combotemp.setName(colorArray[1].trim());
                	 combotemp.setType("Secondary");
                	 
                	 comboList.add(combotemp);
                	 
                	 colorObj.setCombos(comboList);
                	 colorList.add(colorObj);
                 
            }  
		}

		}catch(Exception e){
			_LOGGER.error("Error while processing Color :"+e.getMessage());
         return null;	
         }
		
		return colorList;
		
	}
	
	
	private boolean isComboColors(String value) {
    	boolean result = false;
    	if(value.contains("/")) {
    		result=true;
    			}
    	return result;
    }
			
}
