package com.a4tech.product.ESPTemplate.parser;

import java.util.ArrayList;
import java.util.List;
import com.a4tech.product.model.Color;
import com.a4tech.util.ApplicationConstants;

public class ColorParser {

	public List<Color> getColorCriteria(String colorValue) {
		List<Color> colorlist = new ArrayList<Color>();
		// Set<Color> colorslist = new HashSet<Color>();
		Color colorObj = null;

		if (colorValue.contains(ApplicationConstants.CONST_STRING_COMMA_SEP)) {
			String colorArr[] = colorValue
					.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
			for (String value : colorArr) {
				colorObj = new Color();
				colorObj.setName(value);
				colorObj.setAlias(value);
				colorlist.add(colorObj);
			}
		}
		if (colorValue.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
			String colorArr[] = colorValue
					.split(ApplicationConstants.CONST_DELIMITER_FSLASH);
			for (String value : colorArr) {
				colorObj = new Color();
				colorObj.setName(value);
				colorObj.setAlias(value);
				colorlist.add(colorObj);

			}
		}
		return colorlist;

	}
}
