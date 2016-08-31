package com.a4tech.product.kuku.criteria.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.util.ApplicationConstants;

public class ProductColorParser {
	private Logger _LOGGER = Logger.getLogger(getClass());
	/*
	 * 
	 * Currently there are no  combo value for colors given in the kuku excel file ,so we are not processing them as we don't know how they 
	   will be represented in excel file. 
	 * 
	 * 
	 */
	
	public static final String CONST_STRING_COMBO_TEXT = "Combo";

	public List<Color> getColorCriteria(String color) {
		Color colorObj = null;
		List<Color> colorList = new ArrayList<Color>();
		try {
			String colorArr[] = color.split(ApplicationConstants.CONST_DELIMITER_COMMA);
			boolean isCombo = false;
			List<Combo> comboList = null;
			Combo comboObj = new Combo();
			for (String value : colorArr) {
				colorObj = new Color();
				comboList = new ArrayList<Combo>();

				String originalValue = value;
				String teampValue = value;
				isCombo = isComboColors(value);

				if (!isCombo) {
					colorObj.setName(value.trim());
					colorObj.setAlias(value.trim());
					colorList.add(colorObj);
				} else {
					String colorArray[] = value.split(ApplicationConstants.CONST_DELIMITER_FSLASH);
					String alias = value.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH,ApplicationConstants.CONST_DELIMITER_HYPHEN);
																

					colorObj.setName(alias);
					colorObj.setAlias(alias);

					Combo combotemp = new Combo();
					combotemp.setName(colorArray[1].trim());
					combotemp.setType(ApplicationConstants.CONST_STRING_SECONDARY);

					comboList.add(combotemp);

					colorObj.setCombos(comboList);
					colorList.add(colorObj);

				}
			}

		} catch (Exception e) {
			_LOGGER.error("Error while processing Color :" + e.getMessage());
			return null;
		}
		_LOGGER.info("Colors Processed");
		return colorList;

	}

	private boolean isComboColors(String value) {
		boolean result = false;
		if (value.contains("/")) {
			result = true;
		}
		return result;
	}

}
