package com.a4tech.product.kuku.criteria.parser;

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

public class ProductSizeParser {

	private Logger              _LOGGER              = Logger.getLogger(getClass());
	
	public Size getSizes(String sizeValue) {
		Size sizeObj = new Size();
		String sizeGroup="dimension";
//11:in x 22:in
		//DimenArr 11:in , 22:in
		try{
			String DimenArr[] = {sizeValue} ;
			 if(sizeValue.contains("x")){
				 DimenArr=sizeValue.split("x");
				 sizeGroup="dimension";
			 }
			 else if(sizeValue.contains(",")){
				 DimenArr=sizeValue.split(",");
				 sizeGroup="dimension";
			 }
			 
			if(sizeValue.contains("oz")){
				sizeGroup="volume";
				sizeValue=sizeValue.replaceAll("oz", "");
			}
			
			if (sizeGroup.equals("dimension")) {
			Dimension dimensionObj = new Dimension();
			//String DimenArr[] = sizeValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
			List<Values> valuesList = new ArrayList<Values>();
			List<Value> valuelist;
			Values valuesObj = null;
			Value valObj;
		
		//	for (String value : DimenArr) {
				//11:in x 22:in
				//DimenArr 11:in , 22:in
			//String[] DimenArr1 = value.split(ApplicationConstants.CONST_DELIMITER_SEMICOLON);
				valuesObj = new Values();
				valuelist = new ArrayList<Value>(); 
				int count=1;
				for (String value1 : DimenArr) {
					valObj = new Value();
					String[] DimenArr2 = value1.split(ApplicationConstants.CONST_DELIMITER_COLON);
					//valObj.setAttribute(DimenArr2[0]);
					if(count==1){
						valObj.setAttribute("Length");
					}else if(count==2){
						valObj.setAttribute("Width");
					} else if(count==3){
						valObj.setAttribute("Height");
					}
					valObj.setValue(DimenArr2[0].trim());
					valObj.setUnit(DimenArr2[1].trim());
					valuelist.add(valObj);
					valuesObj.setValue(valuelist);
					count++;
				}
				
				valuesList.add(valuesObj);
			//}

			dimensionObj.setValues(valuesList);
			sizeObj.setDimension(dimensionObj);
		}
			
			if(sizeGroup.equals("volume")){
			Volume volumeObj = new Volume();
			//String volumeArr[] = sizeValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
			List<Values> volumeouterList = new ArrayList<Values>();
			List<Value> volumeinnerList; // = new ArrayList<Value>();
			Values valuesObj;// = new Values();
			Value valObjc;
           //  for (String value : volumeArr) {
				//String volumeArr1[] = value.split(ApplicationConstants.CONST_DELIMITER_COLON);
				valObjc = new Value();
				valuesObj = new Values();
				volumeinnerList = new ArrayList<Value>();
				valObjc.setValue(sizeValue);
				valObjc.setUnit("oz");
				volumeinnerList.add(valObjc);

				valuesObj.setValue(volumeinnerList);

				volumeouterList.add(valuesObj);
			//}

			volumeObj.setValues(volumeouterList);

			sizeObj.setVolume(volumeObj);
			
			
			}
		

		}
		
		catch(Exception e)
		{
			
			_LOGGER.error("Error while processing Size :"+e.getMessage());
			return null;
		}
		return sizeObj;
	}
}














/*	if (sizeGroup.contains(ApplicationConstants.CONST_VALUE_TYPE_CAPACITY)) {

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
	}*/

//	if (sizeGroup.contains(ApplicationConstants.CONST_VALUE_TYPE_VOLUME)) {

		

//	}

	/*if (sizeGroup.contains(ApplicationConstants.CONST_VALUE_TYPE_APPAREL)) {

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

	}*/