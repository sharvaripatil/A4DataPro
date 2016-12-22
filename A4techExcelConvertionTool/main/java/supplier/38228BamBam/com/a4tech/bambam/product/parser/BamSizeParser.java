package com.a4tech.bambam.product.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Capacity;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.OtherSize;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;
import com.a4tech.util.ApplicationConstants;

public class BamSizeParser {
	private final static Logger   _LOGGER   = Logger.getLogger(BamSizeParser.class);
	public Size getSizes(String sizeGroup, String sizeValue) {
		Size sizeObj = new Size();
		try{
		
		//shippingitemValue != null && !shippingitemValue.isEmpty()
		if (sizeGroup.contains(ApplicationConstants.CONST_VALUE_TYPE_DIMENSION)) {
			Dimension dimensionObj = new Dimension();
			String DimenArr[] = sizeValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
			List<Values> valuesList = new ArrayList<Values>();
			List<Value> valuelist;
			Values valuesObj = null;
			Value valObj;
		
			for (String value : DimenArr) {
			String[] DimenArr1 = value.split(ApplicationConstants.CONST_DELIMITER_SEMICOLON);
				valuesObj = new Values();
				valuelist = new ArrayList<Value>();
				for (String value1 : DimenArr1) {
					valObj = new Value();
					String[] DimenArr2 = value1.split(ApplicationConstants.CONST_DELIMITER_COLON);
					valObj.setAttribute(DimenArr2[0]);
					valObj.setValue(DimenArr2[1]);
					valObj.setUnit(DimenArr2[2]);
					valuelist.add(valObj);
					valuesObj.setValue(valuelist);
				}
				valuesList.add(valuesObj);
			}

			dimensionObj.setValues(valuesList);
			sizeObj.setDimension(dimensionObj);
		}

		if (sizeGroup.contains(ApplicationConstants.CONST_VALUE_TYPE_CAPACITY)) {

			Capacity capacityObj = new Capacity();
			String capacityArr[] = sizeValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
			List<Value> capacityvalueList = new ArrayList<Value>();
              for (String value : capacityArr) {
				String capacityArr1[] = value.split(ApplicationConstants.CONST_DELIMITER_COLON);
				Value valObjc = new Value();
				valObjc.setValue(capacityArr1[0]);
				valObjc.setUnit(capacityArr1[1]);
				capacityvalueList.add(valObjc);
				capacityObj.setValues(capacityvalueList);
			}

			sizeObj.setCapacity(capacityObj);
		}

		if (sizeGroup.contains(ApplicationConstants.CONST_VALUE_TYPE_VOLUME)) {

			Volume volumeObj = new Volume();
			String volumeArr[] = sizeValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
			List<Values> volumeouterList = new ArrayList<Values>();
			List<Value> volumeinnerList; // = new ArrayList<Value>();
			Values valuesObj;// = new Values();
			Value valObjc;
             for (String value : volumeArr) {
				String volumeArr1[] = value.split(ApplicationConstants.CONST_DELIMITER_COLON);
				valObjc = new Value();
				valuesObj = new Values();
				volumeinnerList = new ArrayList<Value>();
				valObjc.setValue(volumeArr1[0]);
				valObjc.setUnit(volumeArr1[1]);
				volumeinnerList.add(valObjc);

				valuesObj.setValue(volumeinnerList);

				volumeouterList.add(valuesObj);
			}

			volumeObj.setValues(volumeouterList);

			sizeObj.setVolume(volumeObj);

		}

		if (sizeGroup.contains(ApplicationConstants.CONST_VALUE_TYPE_APPAREL)) {

			Apparel apparelObj = new Apparel();
			String apparelArr[] = sizeValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
			List<Value> apparelList = new ArrayList<Value>();
			for (String value : apparelArr) {
				
				Value valObjc = new Value();
				valObjc.setValue(value);
				apparelObj.setType(sizeGroup);

				apparelList.add(valObjc);
				apparelObj.setValues(apparelList);

			}
			sizeObj.setApparel(apparelObj);

		}

		if (sizeGroup.contains(ApplicationConstants.CONST_VALUE_TYPE_OTHER)) {

			OtherSize otherObj = new OtherSize();
			String otherArr[] = sizeValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
			List<Value> otherList = new ArrayList<Value>();
			for (String value : otherArr) {
				
				Value valObj = new Value();
				valObj.setValue(value);
				otherList.add(valObj);
			}
			otherObj.setValues(otherList);
			sizeObj.setOther(otherObj);

		}

		}
		
		catch(Exception e)
		{
			
			_LOGGER.error("Error while processing Size :"+e.getMessage());
			return new Size();
		}
		return sizeObj;
	}

}
