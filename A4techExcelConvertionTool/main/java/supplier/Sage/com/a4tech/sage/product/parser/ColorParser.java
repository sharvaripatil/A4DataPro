package com.a4tech.sage.product.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.util.ApplicationConstants;

public class ColorParser {

	public List<Color> getColorCriteria(String colorValue) {
		List<Color> colorlist = new ArrayList<Color>();
		Set<Color> colorslist = new HashSet<Color>();
		String colorArr[] = colorValue
				.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		Color colorObj = null;
		for (String value : colorArr) {
			colorObj = new Color();
			if (value.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {

				String colorArr1[] = colorValue
						.split(ApplicationConstants.CONST_DELIMITER_FSLASH);
				List<Combo> combolist = new ArrayList<Combo>();
				for (String value1 : colorArr1) {
					Combo combovalue = new Combo();
					combovalue.setName(value1);
					combovalue.setType("trim");
					combolist.add(combovalue);
				}
				colorObj.setCombos(combolist);
				colorObj.setName(value);
				colorObj.setAlias(value);
				//colorlist.add(colorObj);
				colorslist.add(colorObj);
			} else {
				colorObj.setName(value);
				colorObj.setAlias(value);
				//colorlist.add(colorObj);
				colorslist.add(colorObj);
			}

		}
        colorlist.addAll(colorslist);
		return colorlist;

	}
}
