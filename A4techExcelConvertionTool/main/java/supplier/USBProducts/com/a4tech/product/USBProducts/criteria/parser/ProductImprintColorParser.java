package com.a4tech.product.USBProducts.criteria.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.a4tech.product.model.ImprintColor;
import com.a4tech.product.model.ImprintColorValue;
import com.a4tech.product.model.ImprintMethod;



public class ProductImprintColorParser { 
	
	private Logger              _LOGGER              = Logger.getLogger(getClass());
	public List<ImprintColorValue> getImprintColorCriteria(String imprintColorValue){
		List<ImprintColorValue> impcolorValuesList =new ArrayList<ImprintColorValue>();
		try{
		String impValue = imprintColorValue;
		String imprintArr[] = impValue.split(",");
		Set<String> setOfImprintColors = new HashSet<>(Arrays.asList(imprintArr));
		ImprintColorValue impclrObj=null;
		for (String tempImpint : setOfImprintColors) {
 			impclrObj=new ImprintColorValue();
 			impclrObj.setName(tempImpint.trim());
 			impcolorValuesList.add(impclrObj);
		}
 		 
		}catch(Exception e){
			_LOGGER.error("Error while processing Imprint Color :"+e.getMessage());           
			
   	
   }
 		return impcolorValuesList;
		
		
	}
	
	public StringBuilder getImprintColorValues(String values,StringBuilder allValues){
		
		String imprintArr[] = values.split("\\|");
		//StringBuilder imprintColors = new StringBuilder();
		for (String value : imprintArr) {
			allValues.append(value +",");
		}
		return allValues;
	}
}
