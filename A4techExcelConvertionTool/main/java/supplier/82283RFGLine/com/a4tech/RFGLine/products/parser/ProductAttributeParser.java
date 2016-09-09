package com.a4tech.RFGLine.products.parser;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Color;
import com.a4tech.product.model.ImprintSize;

public class ProductAttributeParser {

	public List<Color> getColorCriteria(String color) {
		List<Color> colorList = new ArrayList<Color>();

		Color colorObj = null;
		String colorArr[] = color.split(",");

		for (String value : colorArr) {
			colorObj = new Color();
			colorObj.setName(value);
			colorObj.setAlias(value);
			colorList.add(colorObj);
		}
		return colorList;
	    }

	    public List<ImprintSize> getImprintSize(String ImprintSizeValue) {
		List<ImprintSize> ImprintSizeList = new ArrayList<ImprintSize>();
		
		ImprintSize ImprintSizeObj=new ImprintSize();
		ImprintSizeValue=ImprintSizeValue.replace("\"", "");
		ImprintSizeObj.setValue(ImprintSizeValue);
		ImprintSizeList.add(ImprintSizeObj);
		return ImprintSizeList;
	    }
	
}
