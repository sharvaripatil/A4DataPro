package com.a4tech.product.criteria.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.Origin;
import com.a4tech.util.ApplicationConstants;

public class ProductOriginParser {

	private Logger              _LOGGER              = Logger.getLogger(getClass());
	
	public List<Origin> getOriginCriteria(String origin){
		List<Origin> originList =new ArrayList<>();
		try{ 
		 origin = origin.trim();
		String originArr[] = origin.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		Origin originObj = null;
		for (String tempOrigin : originArr) {
			 originObj = new Origin();
			 originObj.setName(tempOrigin);
 			originList.add(originObj);
		  }
		}catch(Exception e){
			_LOGGER.error("Error while processing Product Origin :"+e.getMessage());            
		   	return new ArrayList<Origin>();
		   	
		   }
		return originList;
		
	}
}
