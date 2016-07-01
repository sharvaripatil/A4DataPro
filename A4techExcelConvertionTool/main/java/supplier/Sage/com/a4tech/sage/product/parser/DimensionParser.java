package com.a4tech.sage.product.parser;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.sage.product.util.LookupData;



public class DimensionParser {

	public List<Values> getValues(String dimensionValue,
			String dimensionUnits, String dimensionType) {
		   List<Values> valuesList = new ArrayList<Values>();
		
			 Value  valueObj1 = new Value();
			 List<Value> valueList = new ArrayList<Value>();
			 
			 Values valuesObj = new Values();
			 valueObj1.setValue(dimensionValue);
			 valueObj1.setUnit(LookupData.getDimensionUnits(Integer.parseInt(dimensionUnits)));
			 valueObj1.setCriteriaType(LookupData.getDimensionType(Integer.parseInt(dimensionType)));
		     valueList.add(valueObj1);
		     valuesObj.setValue(valueList);
		     valuesList.add(valuesObj);
		   return valuesList;

}
}