package com.a4tech.RFGLine.products.parser;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Color;
import com.a4tech.product.model.ImprintColor;
import com.a4tech.product.model.ImprintColorValue;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.util.ApplicationConstants;


public class RFGProductAttributeParser {

	public List<Color> getColorCriteria(String color) {
		List<Color> colorList = new ArrayList<Color>();

		Color colorObj = null;
		String colorArr[] = color.split(ApplicationConstants.CONST_DELIMITER_COMMA);



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
		ImprintSizeValue = ImprintSizeValue.replace("\"",ApplicationConstants.CONST_STRING_EMPTY);
		ImprintSizeObj.setValue(ImprintSizeValue);
		ImprintSizeList.add(ImprintSizeObj);
		return ImprintSizeList;
	}

	public ImprintColor getImprintColor(String ImprintColorValue) {
		ImprintColor imprintColorObj = new ImprintColor();
		List<ImprintColorValue> impcolorValuesList = new ArrayList<ImprintColorValue>();
		String imprintArr[] = ImprintColorValue.split(ApplicationConstants.CONST_DELIMITER_COMMA);
		imprintColorObj.setType(ApplicationConstants.CONST_STRING_IMPRNT_COLR);
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
		
		List<OptionValue> valuesList = new ArrayList<OptionValue>();
		OptionValue optionValueObj = new OptionValue();
		optionValueObj.setValue("Per Color");
		valuesList.add(optionValueObj);
		optionObj.setOptionType("Imprint");
		optionObj.setName("Custom PMS Color Imprint");
		optionObj.setValues(valuesList);
		optionObj.setAdditionalInformation(OptionValue);
		optionObj.setCanOnlyOrderOne(false);
		optionObj.setRequiredForOrder(false);
		optionsList.add(optionObj);

	
		return optionsList;
	}
	
	
	
	
}
