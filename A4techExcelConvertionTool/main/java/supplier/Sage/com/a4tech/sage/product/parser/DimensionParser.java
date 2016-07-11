package com.a4tech.sage.product.parser;

import java.util.ArrayList;
import java.util.List;

import org.junit.internal.runners.model.EachTestNotifier;

import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.sage.product.util.LookupData;
import com.a4tech.util.ApplicationConstants;

public class DimensionParser {
	public List<Values> getValues(String dimensionValue, String dimensionUnits,
			String dimensionType) {

		String dimensionValueArr[] = dimensionValue
				.split(ApplicationConstants.CONST_DIMENSION_SPLITTER);
		String dimensionUnitsArr[] = dimensionUnits
				.split(ApplicationConstants.CONST_DIMENSION_SPLITTER);
		String dimensionTypeArr[] = dimensionType
				.split(ApplicationConstants.CONST_DIMENSION_SPLITTER);

		ArrayList<Value> valueList = new ArrayList<Value>();
		List<Values> valueslist = new ArrayList<Values>();

		Values valuesObj = new Values();
		Value valueObj = null;

		for (int i = 0; i < dimensionValueArr.length; i++) {
			valueObj = new Value();
			valueObj.setValue(dimensionValueArr[0]);
			valueObj.setUnit(dimensionUnitsArr[0]);
			valueObj.setCriteriaType(dimensionTypeArr[0]);
			valueList.add(valueObj);
		}

		valuesObj.setValue(valueList);
		valueslist.add(valuesObj);

		return valueslist;
	}

}