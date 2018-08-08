package parser.radious;

import java.util.ArrayList;
import java.util.List;
import com.a4tech.product.model.Color;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;


public class RadiousColorParser {

	public List<Color> getColorCriteria(String colorValue) {
		
		List<Color> listOfProductColors = new ArrayList<>();

		Color colorObj = new Color();
		
		String[] colorValues = CommonUtility.getValuesOfArray(colorValue,
				ApplicationConstants.CONST_DELIMITER_COMMA);
		
		for (String colorName : colorValues) {
			
			colorObj = new Color();
			if(RadiousLookupData.COLOR_MAP.containsKey(colorName.trim())) {
				colorObj.setName(RadiousLookupData.getCOLOR_MAP().get(colorName.trim()));
			}else
			{
				colorObj.setName("Other");

			}
			colorObj.setAlias(colorName.trim());

			listOfProductColors.add(colorObj);
		}
		

		return listOfProductColors;


	}

	}

