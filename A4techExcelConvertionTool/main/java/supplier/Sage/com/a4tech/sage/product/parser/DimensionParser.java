package com.a4tech.sage.product.parser;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.sage.product.util.LookupData;



public class DimensionParser {

	public List<Values> getValues(double dimensionValue,
			double dimensionUnits, double dimensionType) {
		   List<Values> valuesList = new ArrayList<Values>();
		
			 Value  valueObj1 = new Value();
			 ArrayList<Value> valueList = new ArrayList<Value>();
			 
			 valueObj1.setValue(String.valueOf(dimensionValue));
			 valueObj1.setUnit(LookupData.getDimensionUnits((int)dimensionUnits));
			 valueObj1.setCriteriaType(LookupData.getDimensionType((int)dimensionType));
		     valueList.add(valueObj1);
			 
		   return valuesList;

}
}