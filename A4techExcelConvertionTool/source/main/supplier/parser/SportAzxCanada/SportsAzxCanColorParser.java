package parser.SportAzxCanada;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class SportsAzxCanColorParser {
	private static final Logger _LOGGER = Logger
			.getLogger(SportsAzxCanColorParser.class);
	@SuppressWarnings("unused")
	public List<Color> getProductColors(String colorValuee,String xid){
		List<Color> listOfColors = new ArrayList<>();
		
		Color colorObj = null;
		//Iterator<String> colorIterator=colorValuee.iterator();
		try {
			List<Combo> comboList = null;
		//while (colorIterator.hasNext()) {
			String color =colorValuee; //(String) colorIterator.next();
			color=color.replaceAll("\\|",",");
		String[] colors =getValuesOfArray(color, ",");
		HashSet<String> colorSet=new HashSet<String>();
		for (String clrValue : colors) {
			colorSet.add(clrValue.trim());
		}
		for (String colorName : colorSet) {
			if(StringUtils.isEmpty(colorName)){
				continue;
			}
			colorName=colorName.replaceAll("&","/");
			colorName=colorName.replaceAll(" w/","/");
			colorName=colorName.replaceAll(" W/","/");
			//colorName = colorName.trim();
			
			colorObj = new Color();
			//String colorGroup = SolidDimApplicationConstatnt.getColorGroup(colorName.trim());
			
			//if (colorGroup == null) {
				//if (colorGroup!=null && colorGroup.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
			if (colorName!=null && (colorName.contains("/") || colorName.contains(ApplicationConstants.CONST_DELIMITER_FSLASH))) {
				
				/*if(colorGroup==null){
					colorGroup=colorName;
				}*/
				colorName=colorName.replaceAll("&","/");
				colorName=colorName.replaceAll(" w/","/");
				colorName=colorName.replaceAll(" W/","/");
				
				//if (colorName.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
					if(isComboColor(colorName)){
						List<Combo> listOfCombo = null;
						String[] comboColors = CommonUtility.getValuesOfArray(colorName,
								ApplicationConstants.CONST_DELIMITER_FSLASH);
						String colorFirstName = SportsCanadaApplicationConstatnt.getColorGroup(comboColors[0].trim());
						colorObj.setName(colorFirstName == null?"Other":colorFirstName);
						int combosSize = comboColors.length;
						if (combosSize == ApplicationConstants.CONST_INT_VALUE_TWO) {
							String colorComboFirstName = SportsCanadaApplicationConstatnt.getColorGroup(comboColors[1].trim());
							colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
							listOfCombo = getColorsCombo(colorComboFirstName, ApplicationConstants.CONST_STRING_EMPTY,
									combosSize);
						} else{
							String colorComboFirstName = SportsCanadaApplicationConstatnt.getColorGroup(comboColors[1].trim());
							colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
							
							String colorComboSecondName = SportsCanadaApplicationConstatnt.getColorGroup(comboColors[2].trim());
							colorComboSecondName = colorComboSecondName == null?"Other":colorComboSecondName;
							listOfCombo = getColorsCombo(colorComboFirstName,colorComboSecondName, combosSize);
						}
						String alias = colorName.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH, "-");
						colorObj.setAlias(alias);
						colorObj.setCombos(listOfCombo);
					} else {
						String[] comboColors = CommonUtility.getValuesOfArray(colorName,
								ApplicationConstants.CONST_DELIMITER_FSLASH);
						String mainColorGroup = SportsCanadaApplicationConstatnt.getColorGroup(comboColors[0].trim());
						if(mainColorGroup != null){
							colorObj.setName(mainColorGroup);
							colorObj.setAlias(colorName);
						} else {
							colorObj.setName(ApplicationConstants.CONST_VALUE_TYPE_OTHER);
							colorObj.setAlias(colorName);
						}
					}
				/*} else {
					if (colorGroup == null) {
					colorGroup = ApplicationConstants.CONST_VALUE_TYPE_OTHER;
					}
					colorObj.setName(colorGroup);
					colorObj.setAlias(colorName);
				}*/
			} else {
				String colorGroup = SportsCanadaApplicationConstatnt.getColorGroup(colorName.trim());
				if (colorGroup == null) {
					colorGroup = ApplicationConstants.CONST_VALUE_TYPE_OTHER;
					}
				colorObj.setName(colorGroup);
				colorObj.setAlias(colorName);
			}
			listOfColors.add(colorObj);
		}//for end
		//}//while end
		}catch(Exception e){
			_LOGGER.error("Error while processing color: "+e.getMessage() +"Color Error For xid:"+xid);
		}
		return listOfColors;
		}
		
	private List<Combo> getColorsCombo(String firstValue,String secondVal,int comboLength){
		List<Combo> listOfCombo = new ArrayList<>();
		Combo comboObj1 = new Combo();
		Combo comboObj2 = new Combo();
		comboObj1.setName(firstValue);
		comboObj1.setType(ApplicationConstants.CONST_STRING_SECONDARY);
		comboObj2.setName(secondVal);
		comboObj2.setType(ApplicationConstants.CONST_STRING_TRIM);
		if(comboLength == ApplicationConstants.CONST_INT_VALUE_TWO){
			listOfCombo.add(comboObj1);
		} else {
			listOfCombo.add(comboObj1);
			listOfCombo.add(comboObj2);
		}
		return listOfCombo;
	}
	
	public static boolean isComboColor(String colorValue){
    	String[] colorVals = CommonUtility.getValuesOfArray(colorValue, "/");
    	String mainColor       = null;
    	String secondaryColor  = null;
    	String thirdColor      = null;
    	if(colorVals.length == ApplicationConstants.CONST_INT_VALUE_TWO){
    		 mainColor = SportsCanadaApplicationConstatnt.getColorGroup(colorVals[0].trim());
    		 secondaryColor = SportsCanadaApplicationConstatnt.getColorGroup(colorVals[1].trim());
    		 if(mainColor != null && secondaryColor != null){
    			 return true;
    		 }
    	} else if(colorVals.length == ApplicationConstants.CONST_INT_VALUE_THREE){
    		 mainColor      = SportsCanadaApplicationConstatnt.getColorGroup(colorVals[0].trim());
    		 secondaryColor = SportsCanadaApplicationConstatnt.getColorGroup(colorVals[1].trim());
    		 thirdColor     = SportsCanadaApplicationConstatnt.getColorGroup(colorVals[2].trim());
    		 if(mainColor != null && secondaryColor != null && thirdColor != null){
    			 return true;
    		 }
    	} else{
    		
    	}
    	return false;
    }
	
	public static String[] getValuesOfArray(String data,String delimiter){
		   if(!StringUtils.isEmpty(data)){
			   return data.split(delimiter);
		   }
		   return null;
	   }
}
