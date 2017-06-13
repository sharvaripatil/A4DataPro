package parser.twintech;

import java.util.ArrayList;
import java.util.List;
import parser.pslcad.PSLLookupColor;
import com.a4tech.product.model.Color;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;


public class TwintechColorParser {

	public List<Color> getColorCriteria(String colorValue) {
		List<Color> listOfProductColors = new ArrayList<>();

		Color colorObj = new Color();
		colorValue = colorValue.replace("/", ",");
		String[] colorValues = CommonUtility.getValuesOfArray(colorValue,
				ApplicationConstants.CONST_DELIMITER_COMMA);
		for (String colorName : colorValues) {
			colorObj = new Color();
			colorName = colorName.trim();
			colorObj.setAlias(colorName);
			colorObj.setName(PSLLookupColor.COLOR_MAP.get(colorName.trim()));
			listOfProductColors.add(colorObj);
		}
		return listOfProductColors;
	}

	}

