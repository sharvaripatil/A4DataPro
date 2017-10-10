package parser.twintech;

import java.util.ArrayList;
import java.util.List;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class RoelProductAttributeParser {

	public List<String> getCompliancecert(String complnceValue) {
		List<String> complianceList = new ArrayList<String>();
		if(complnceValue.contains(","))
		{		
			String certArr[]=complnceValue.split(",");
			
			for (String certName : certArr) {
				complianceList.add(certName);
			}		
		}
		else
		{
			complianceList.add(complnceValue);
		}
		return complianceList;
	}

	
	
	
	
	public List<Color> getColorCriteria(String colorValue) {
		
		List<Color> listOfProductColors = new ArrayList<>();
		List<Combo> combolist = new ArrayList<>();

		Color colorObj = new Color();
		Combo comboObj=new Combo();
		
		String[] colorValues = CommonUtility.getValuesOfArray(colorValue,
				ApplicationConstants.CONST_DELIMITER_COMMA);
		for (String colorName : colorValues) {
			colorObj = new Color();
			 comboObj=new Combo();
			String OriginalcolorName = colorName.trim();
			if(TwintechLookupData.COLOR_MAP.containsKey(colorName)){
			String colorLookUpName=TwintechLookupData.COLOR_MAP.get(colorName.trim()).trim();
		
		 if(colorLookUpName.contains("Combo"))
			{ String comboArr[]=colorLookUpName.split(":");
			colorObj.setAlias(OriginalcolorName);
			colorObj.setName(comboArr[0]);
			
			comboObj.setType("secondary");
			comboObj.setName(comboArr[2]);
			combolist.add(comboObj);
			colorObj.setCombos(combolist);
				
			}else
			{
			colorObj.setAlias(OriginalcolorName);
			colorObj.setName(colorLookUpName);
			}
			}else
			{
				colorObj.setAlias(OriginalcolorName);
				colorObj.setName("Other");
			}
			//if(){
			listOfProductColors.add(colorObj);
			//}
		}
		return listOfProductColors;
	}
	
}
