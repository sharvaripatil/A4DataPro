package com.a4tech.sage.product.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.jasper.tagplugins.jstl.core.If;

import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.util.ApplicationConstants;

public class ColorParser {

	public List<Color> getColorCriteria(String colorValue) {
		List<Color> colorlist = new ArrayList<Color>();
		String colorArr[] = colorValue
				.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		Color colorObj = new Color();
		for (String value : colorArr) {
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
				colorlist.add(colorObj);
			} else {
				colorObj.setName(value);
				colorObj.setAlias(value);
				colorlist.add(colorObj);
			}

		}

		return colorlist;

	}
}
