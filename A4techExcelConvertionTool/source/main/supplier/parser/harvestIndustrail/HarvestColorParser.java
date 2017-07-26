package parser.harvestIndustrail;

import java.util.ArrayList;
import java.util.List;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;


public class HarvestColorParser {

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
			String colorLookUpName=HarvestLookupData.COLOR_MAP.get(colorName.trim()).trim();
			if(colorLookUpName.contains("Secondary"))
			{
				Combo comboObj1=new Combo();
				String comboArr[]=colorLookUpName.split(":");
				colorObj.setAlias(OriginalcolorName);
				colorObj.setName(comboArr[0]);
				
				comboObj.setType("secondary");
				comboObj.setName(comboArr[2]);
				

				comboObj1.setType("trim");
				comboObj1.setName(comboArr[4]);
				
				
				combolist.add(comboObj);
				combolist.add(comboObj1);
				
				colorObj.setCombos(combolist);
				
			}
			else if(colorLookUpName.contains("Combo"))
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
			listOfProductColors.add(colorObj);

		}
		return listOfProductColors;
	}

	}

