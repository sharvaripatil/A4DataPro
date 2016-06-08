package com.a4tech.product.USBProducts.criteria.parser;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

import com.a4tech.util.ApplicationConstants;

public class ProductPackagingParser {

	private Logger              _LOGGER              = Logger.getLogger(getClass());
	
	public List<String> getPackagingCriteria(String packaging){
		List<String> packagingList =new ArrayList<String>();
		try{
		String packagingValue = packaging;
		String packagingArr[] = packagingValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		
		for (String tempPackaging : packagingArr) {
 			packagingList.add(tempPackaging);
		}
		}catch(Exception e){
			_LOGGER.error("Error while processing Product Packaging :"+e.getMessage());             
		   	return null;
		   	
		   }
		 
		return packagingList;
		
	}
}
