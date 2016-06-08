package com.a4tech.product.USBProducts.criteria.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.ImprintMethod;
import com.a4tech.util.ApplicationConstants;

public class ProductImprintMethodParser {

	private Logger              _LOGGER              = Logger.getLogger(getClass());
	
	public List<ImprintMethod> getImprintCriteria(String imprintValue){
		
		List<ImprintMethod> impmthdList =new ArrayList<ImprintMethod>();
		try{
		//String impValue = imprintValue;
		//String imprintArr[] = impValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		ImprintMethod imprintObj=null;
		//for (String tempImpint : imprintArr) {
 			imprintObj=new ImprintMethod();
 			imprintObj.setType(imprintValue.trim());
 			imprintObj.setAlias(imprintValue.trim());
 			impmthdList.add(imprintObj);
		//}
		
		}catch(Exception e){
			_LOGGER.error("Error while processing Imprint Method :"+e.getMessage());             
		   	return null;
		   	
		   }
		return impmthdList;
		
	}
}
