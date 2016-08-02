package com.a4tech.product.DCProducts.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.Origin;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class ProductOriginParser {

	private Logger              _LOGGER              = Logger.getLogger(getClass());
	
	public List<Origin> getOriginCriteria(String customercode,String origin){
		List<Origin> originList =new ArrayList<Origin>();
		Origin originObj = new Origin();
		
		try{ 
		if(CommonUtility.isEmptyOrNull(customercode)){
			originObj.setCustomerOrderCode(customercode);
		}
     	if(CommonUtility.isEmptyOrNull(origin)){
     		originObj.setName(origin);
     	}
		originList.add(originObj);
		}
		catch(Exception e){
			_LOGGER.error("Error while processing Product Origin :"+e.getMessage());            
		   	return null;
		   	
		   }
		return originList;
		
	}
}
