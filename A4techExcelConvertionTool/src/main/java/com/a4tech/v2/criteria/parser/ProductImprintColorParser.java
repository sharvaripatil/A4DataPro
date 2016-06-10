package com.a4tech.v2.criteria.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;




import com.a4tech.util.ApplicationConstants;
import com.a4tech.v2.core.model.ImprintColor;
import com.a4tech.v2.core.model.ImprintColorValue;


public class ProductImprintColorParser { 
	
	private Logger              _LOGGER              = Logger.getLogger(getClass());
	public ImprintColor getImprintColorCriteria(String imprintColorValue){
		ImprintColor imprintColorObj= new ImprintColor();
		try{
		String impValue = imprintColorValue;
		String imprintArr[] = impValue.split(ApplicationConstants.CONST_DELIMITER_SPLITTING_PIPE);
		List<ImprintColorValue> impcolorValuesList =new ArrayList<ImprintColorValue>();
		imprintColorObj.setType("COLR");
		ImprintColorValue impclrObj=null;
		
		
		for (String tempImpint : imprintArr) {
 			impclrObj=new ImprintColorValue();
 			impclrObj.setName(tempImpint);
 			impcolorValuesList.add(impclrObj);
		}
 		imprintColorObj.setValues(impcolorValuesList);
		}catch(Exception e){
			_LOGGER.error("Error while processing Imprint Color :"+e.getMessage());           
			return null;
   	
   }
 		
 		return imprintColorObj;
		
		
	}
}
