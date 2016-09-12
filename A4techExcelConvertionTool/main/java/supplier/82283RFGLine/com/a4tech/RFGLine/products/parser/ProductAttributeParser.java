package com.a4tech.RFGLine.products.parser;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Color;
import com.a4tech.product.model.ImprintColor;
import com.a4tech.product.model.ImprintColorValue;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;

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

		ImprintSize ImprintSizeObj = new ImprintSize();
		ImprintSizeValue = ImprintSizeValue.replace("\"", "");
		ImprintSizeObj.setValue(ImprintSizeValue);
		ImprintSizeList.add(ImprintSizeObj);
		return ImprintSizeList;
	}

	public ImprintColor getImprintColor(String ImprintColorValue) {
		ImprintColor imprintColorObj = new ImprintColor();
		List<ImprintColorValue> impcolorValuesList = new ArrayList<ImprintColorValue>();
		String imprintArr[] = ImprintColorValue.split(",");
		imprintColorObj.setType("COLR");
		ImprintColorValue impclrObj = null;
		for (String tempImpint : imprintArr) {
			impclrObj = new ImprintColorValue();
			impclrObj.setName(tempImpint);
			impcolorValuesList.add(impclrObj);
		}
		imprintColorObj.setValues(impcolorValuesList);
		return imprintColorObj;
	}

	
	public List<Option> getOption(String OptionValue) {
		List<Option> optionsList=new ArrayList<Option>();
		Option optionObj=new Option();
		
		List<String> values = new ArrayList<String>();
		values.add("Per Color");
		optionObj.setOptionType("Imprint Option");
		optionObj.setName("Custom PMS Color Imprint");
		optionObj.setValues(values);
		optionObj.setAdditionalInformation(OptionValue);
		optionObj.setCanOnlyOrderOne(false);
		optionObj.setRequiredForOrder(false);
		optionsList.add(optionObj);

	
		return optionsList;
	}
	
	
	
	
}
