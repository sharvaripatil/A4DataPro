package com.a4tech.product.ESPTemplate.parser;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.OtherSize;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;

public class SizeParser {
	public Size getSizes(String sizeValue) 
	{
		Size sizeObj = new Size();
		OtherSize otherObj = new OtherSize();
		Value valObj = new Value();
		
		List<Value> otherList = new ArrayList<Value>();
		
		valObj.setValue(sizeValue);
		otherList.add(valObj);
		otherObj.setValues(otherList);
		sizeObj.setOther(otherObj);
		

		return sizeObj;
		
	}
}
