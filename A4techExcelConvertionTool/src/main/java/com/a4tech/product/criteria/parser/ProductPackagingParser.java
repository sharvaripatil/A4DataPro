package com.a4tech.product.criteria.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.Packaging;
import com.a4tech.util.ApplicationConstants;

public class ProductPackagingParser {

	private Logger              _LOGGER              = Logger.getLogger(getClass());
	
	public List<Packaging> getPackagingCriteria(String packaging){
		List<Packaging> packagingList =new ArrayList<Packaging>();
		Packaging pack = null;
		try{
		String packagingValue = packaging;
		String packagingArr[] = packagingValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		
		for (String tempPackaging : packagingArr) {
			pack = new Packaging();
			pack.setName(tempPackaging);
 			packagingList.add(pack);
		}
		}catch(Exception e){
			_LOGGER.error("Error while processing Product Packaging :"+e.getMessage());             
		   	return new ArrayList<Packaging>();
		   	
		   } 
		return packagingList;
		
	}
}
