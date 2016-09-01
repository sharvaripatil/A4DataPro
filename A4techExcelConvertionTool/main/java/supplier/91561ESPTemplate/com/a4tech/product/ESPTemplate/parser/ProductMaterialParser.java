package com.a4tech.product.ESPTemplate.parser;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Material;
import com.a4tech.util.ApplicationConstants;

public class ProductMaterialParser {
	public List<Material> getMaterialCriteria(String materialValue) {
		List<Material> materialList = new ArrayList<Material>();
		Material material = null;

		if (materialValue.contains(ApplicationConstants.CONST_STRING_COMMA_SEP)
				|| materialValue
						.contains(ApplicationConstants.CONST_DELIMITER_SEMICOLON)) {
			if (materialValue
					.contains(ApplicationConstants.CONST_DELIMITER_SEMICOLON)) {
				materialValue = materialValue.replace(
						ApplicationConstants.CONST_DELIMITER_SEMICOLON,
						ApplicationConstants.CONST_STRING_COMMA_SEP);
			}
			String[] arrayOfMaterial = materialValue
					.split(ApplicationConstants.CONST_STRING_COMMA_SEP);

			for (String materialTemp : arrayOfMaterial) {
				material = new Material();
				material.setName(materialTemp);
				material.setAlias(materialTemp);
				materialList.add(material);
			}
		}
		if (materialValue.contains(ApplicationConstants.CONST_STRING_AND)
				|| materialValue
						.contains(ApplicationConstants.CONST_STRING_PLUS)) {

			if (materialValue.contains(ApplicationConstants.CONST_STRING_PLUS)) {
				materialValue = materialValue.replace(
						ApplicationConstants.CONST_STRING_PLUS,
						ApplicationConstants.CONST_STRING_AND);
			}

			String[] arrayOfMaterial = materialValue
					.split(ApplicationConstants.CONST_STRING_AND);
			for (String value : arrayOfMaterial) {
				material = new Material();
				Combo materialCombo = new Combo();
				material.setName(arrayOfMaterial[0]);
				material.setAlias(arrayOfMaterial[0]);
				materialCombo.setName(arrayOfMaterial[1]);
				material.setCombo(materialCombo);

			}
			materialList.add(material);

		}

		else {
			material = new Material();
			material.setName(materialValue);
			material.setAlias(materialValue);
			materialList.add(material);
		}

		return materialList;

	}

}
