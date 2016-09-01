package com.a4tech.product.kuku.criteria.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.Packaging;
import com.a4tech.util.ApplicationConstants;

public class ProductPackagingParser {

	private Logger              _LOGGER              = Logger.getLogger(getClass());
	
	public List<Packaging> getPackagingCriteria(String packaging){
		List<Packaging> packagingList =new ArrayList<Packaging>();
		Packaging packObj;
		try{
		String packagingArr[] = packaging.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		for (String tempPackaging : packagingArr) {
			packObj=new Packaging();
			packObj.setName(tempPackaging);
 			packagingList.add(packObj);
		}
		}catch(Exception e){
			_LOGGER.error("Error while processing Product Packaging :"+e.getMessage());             
		   	return null;
		   }
		 
		return packagingList;
		
	}
}
