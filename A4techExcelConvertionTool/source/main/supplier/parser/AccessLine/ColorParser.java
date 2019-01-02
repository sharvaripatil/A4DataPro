package parser.AccessLine;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class ColorParser {
	
	
	public static List<Color> getColorCriteria(String colorValue) {
	
		List<Color> listOfProductColors = new ArrayList<>();
		Color colorObj = new Color();
		Combo comboObj=new Combo();
		List<Combo> combolist = new ArrayList<>();

		String[] colorValues = CommonUtility.getValuesOfArray(colorValue,
				ApplicationConstants.CONST_DELIMITER_COMMA);
		
		for (String colorName : colorValues) {			
			colorObj = new Color();
			colorName = colorName.trim();
			comboObj=new Combo();
			 
			if(colorName.contains("with"))
			{
				String orgColorAlias=colorName.replace("with", "-");
				String orgCombo[]=colorName.split("with");
				colorObj.setAlias(orgColorAlias);
				String mainName=AccesslineLookupData.COLOR_MAP.get(orgCombo[0].trim());
				
				if(StringUtils.isEmpty(mainName)){
					colorObj.setName(mainName);		
				}else{
					colorObj.setName(mainName);		
				}
				
				
				
				comboObj.setType("secondary");
				String namesecondary=AccesslineLookupData.COLOR_MAP.get(orgCombo[1].trim());
				if(StringUtils.isEmpty(namesecondary)){
					comboObj.setName("Other");	
				}else{
					comboObj.setName(namesecondary);
				}
				//comboObj.setName(AccesslineLookupData.COLOR_MAP.get(orgCombo[1].trim()));
				combolist.add(comboObj);
				colorObj.setCombos(combolist);
			}
			else if(colorName.contains("and"))
			{
				String orgCombo[]=colorName.split("and");
				colorObj.setAlias(colorName);
				String mainName=AccesslineLookupData.COLOR_MAP.get(orgCombo[0].trim());
				
				if(StringUtils.isEmpty(mainName)){
					colorObj.setName(mainName);		
				}else{
					colorObj.setName(mainName);		
				}		
				comboObj.setType("Accent");
				String nameAccent=AccesslineLookupData.COLOR_MAP.get(orgCombo[1].trim());
				if(StringUtils.isEmpty(nameAccent)){
					comboObj.setName("Other");	
				}else{
					comboObj.setName(nameAccent);
				}
				
				combolist.add(comboObj);
				colorObj.setCombos(combolist);
			}else if(colorName.contains("&")){
				String orgCombo[]=colorName.split("&");
				colorObj.setAlias(colorName);
				String mainName=AccesslineLookupData.COLOR_MAP.get(orgCombo[0].trim());
				if(StringUtils.isEmpty(mainName)){
					colorObj.setName(mainName);		
				}else{
					colorObj.setName(mainName);		
				}		
				comboObj.setType("trim");
				
				String nametrim=AccesslineLookupData.COLOR_MAP.get(orgCombo[1].trim());
				if(StringUtils.isEmpty(nametrim)){
					comboObj.setName("Other");	
				}else{
					comboObj.setName(nametrim);
				}
				
				//comboObj.setName(AccesslineLookupData.COLOR_MAP.get(orgCombo[1].trim()));
				combolist.add(comboObj);
				colorObj.setCombos(combolist);
			}
			
			else{
			
			colorObj.setAlias(colorName);
			String mainName=AccesslineLookupData.COLOR_MAP.get(colorName.trim());
			
			if(StringUtils.isEmpty(mainName)){
				colorObj.setName(mainName);		
			}else{
				colorObj.setName(mainName);		
			}
			//colorObj.setName(AccesslineLookupData.COLOR_MAP.get(colorName.trim()));
			}
			listOfProductColors.add(colorObj);

		}
		
		
		return listOfProductColors;

	
	}

}
