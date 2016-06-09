package com.a4tech.v2.criteria.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.v2.core.model.ImprintColorValue;


public class ProductImprintColorParser { 
	
	private Logger              _LOGGER              = Logger.getLogger(getClass());
	public List<ImprintColorValue> getImprintColorCriteria(String imprintColorValue){
		List<ImprintColorValue> impcolorValuesList =new ArrayList<ImprintColorValue>();
		try{
		String impValue = imprintColorValue;
		String imprintArr[] = impValue.split("\\|");
		
		 
		ImprintColorValue impclrObj=null;
		
		
		for (String tempImpint : imprintArr) {
 			impclrObj=new ImprintColorValue();
 			impclrObj.setName(tempImpint.trim());
 			impcolorValuesList.add(impclrObj);
		}
 		 
		}catch(Exception e){
			_LOGGER.error("Error while processing Imprint Color :"+e.getMessage());           
			
   	
   }
 		
 		return impcolorValuesList;
		
		
	}
}
