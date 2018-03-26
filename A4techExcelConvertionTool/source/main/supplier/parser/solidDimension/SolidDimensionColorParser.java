package parser.solidDimension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class SolidDimensionColorParser {

	public List<Color> getColorCriteria1(String colorValue) {
		colorValue=colorValue.replace("Pen","").replace("Handle","").replace("Flag","");
		List<Color> colorlist = new ArrayList<Color>();
		//Set<Color> colorslist = new HashSet<Color>();
 		String colorArr[] = colorValue
				.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
 		Set<String> colorsList = new HashSet<String>(Arrays.asList(colorArr));// remove duplicate values
		Color colorObj = null;
		for (String outervalue : colorsList) {
		
			colorObj =  new Color();
			if (outervalue.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
				

				String colorArr1[] = outervalue
						.split(ApplicationConstants.CONST_DELIMITER_FSLASH);
				colorArr1[0]=colorArr1[0].trim();
				colorArr1[1]=colorArr1[1].trim();

				List<Combo> combolist = new ArrayList<Combo>();
			for (int i=0;i<1;i++) {
			    	colorObj =  new Color();
					Combo combovalue = new Combo();
					String color2 = SolidDimApplicationConstatnt.COLOR_MAP.get(colorArr1[1]);
					color2 = color2 == null?"Other":color2;
					combovalue.setName(color2);
					combovalue.setType("trim");
					combolist.add(combovalue);
				
				colorObj.setCombos(combolist);
				String color1 = SolidDimApplicationConstatnt.COLOR_MAP.get(colorArr1[0]);
				color1 = color1 == null?"Other":color1;
				colorObj.setName(color1);
				colorObj.setAlias(outervalue);
				//colorlist.add(colorObj);
				//colorlist.add(colorObj);
			}
			} else {
				outervalue=outervalue.trim();
				String outerColor = SolidDimApplicationConstatnt.COLOR_MAP.get(outervalue);
				outerColor = outerColor == null?"Other":outerColor;
				colorObj.setName(outerColor);
				colorObj.setAlias(outervalue);
				//colorlist.add(colorObj);
				
			}
			colorlist.add(colorObj);
		}
     //   colorlist.addAll(colorslist);
		return colorlist;

	}
	public List<Color> getColorCriteria(String colorValue) {
		colorValue=colorValue.replace("Pen","").replace("Handle","").replace("Flag","");
		List<Color> colorlist = new ArrayList<Color>();
		String colorArr[] = colorValue
				.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		Set<String> colorsSet = new HashSet<>();
		for (String colorVal : colorArr) {//avoid duplicate values
			colorVal = colorVal.trim();
			colorsSet.add(colorVal);
		}
 		//Set<String> colorsList = new HashSet<String>(Arrays.asList(colorArr));// remove duplicate values
 		Color colorObj = null;
 		for (String colorName : colorsSet) {
 			colorObj = new Color();
 			//colorName = colorName.trim();
 			if(colorName.contains("/")){
 				colorObj = getColorCombo(colorName);
 			} else {
 				String colorGroup = SolidDimApplicationConstatnt.getColorGroup(colorName);
 				colorObj.setName(colorGroup);
 				colorObj.setAlias(colorName);
 			}
 			colorlist.add(colorObj);
		}
 		return colorlist;
	}
	private Color getColorCombo(String comboVal){
        Color colorObj = new Color();
        List<Combo> listOfComos = new ArrayList<>();
        Combo comboObj1 = new Combo();
        Combo comboObj2 = new Combo();
        String[] comboColors = CommonUtility.getValuesOfArray(comboVal,
                                      ApplicationConstants.CONST_DELIMITER_FSLASH);
        colorObj.setName(
        		SolidDimApplicationConstatnt.getColorGroup(comboColors[ApplicationConstants.CONST_NUMBER_ZERO].toLowerCase()));
        comboObj1.setName(
        		SolidDimApplicationConstatnt.getColorGroup(comboColors[ApplicationConstants.CONST_INT_VALUE_ONE].toLowerCase()));
        comboObj1.setType(ApplicationConstants.CONST_STRING_SECONDARY);
        colorObj.setAlias(comboVal.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH,
                    ApplicationConstants.CONST_DELIMITER_HYPHEN));
        if(comboColors.length == 3){
              comboObj2.setName(
            		  SolidDimApplicationConstatnt.getColorGroup(comboColors[ApplicationConstants.CONST_INT_VALUE_TWO].toLowerCase()));
              comboObj2.setType(ApplicationConstants.CONST_STRING_TRIM);
              listOfComos.add(comboObj2);
        } 
        listOfComos.add(comboObj1);
        colorObj.setCombos(listOfComos);
        return colorObj;
  }

}
