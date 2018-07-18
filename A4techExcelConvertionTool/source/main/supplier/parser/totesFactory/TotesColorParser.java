package parser.totesFactory;

import java.util.ArrayList;
import java.util.List;
import com.a4tech.product.model.Color;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;


public class TotesColorParser {

  public List<Color> getColorCriteria(String colorValue) {
		
		List<Color> listOfProductColors = new ArrayList<>();
		colorValue=colorValue.replace("Natural/", "");
		Color colorObj = new Color();
		
		
		colorValue=colorValue.replace("/", ",");
		
		String[] colorValues = CommonUtility.getValuesOfArray(colorValue,
				ApplicationConstants.CONST_DELIMITER_COMMA);
		
		for (String colorName : colorValues) {
			
			colorObj = new Color();
			if(TotesLookupData.COLOR_MAP.containsKey(colorName)) {
				colorObj.setName(colorName);
			}else
			{
				colorObj.setName("Other");

			}
			colorObj.setAlias(colorName);

			listOfProductColors.add(colorObj);
		}
		

		return listOfProductColors;


	}

	}


