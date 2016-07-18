package com.a4tech.product.USBProducts.criteria.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.a4tech.product.model.ImprintMethod;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.v2.core.model.Artwork;

public class ProductImprintMethodParser {

	private Logger              _LOGGER              = Logger.getLogger(getClass());
	
	public List<ImprintMethod> getImprintCriteria(String imprintValue){
		
		List<ImprintMethod> impmthdList =new ArrayList<ImprintMethod>();
		Set<ImprintMethod> setOfImprintMethod =  new HashSet<ImprintMethod>();
		ImprintMethod imprintObj=null;
		try{
			if(imprintValue.contains(ApplicationConstants.CONST_STRING_COMMA_SEP)){
				String imprintArr[] = imprintValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
				for (String imprintMethod : imprintArr) {
					imprintObj=new ImprintMethod();
		 			imprintObj.setType(imprintMethod.trim());
		 			imprintObj.setAlias(imprintMethod.trim());
		 			setOfImprintMethod.add(imprintObj);
				}
			}
	
		}catch(Exception e){
			_LOGGER.error("Error while processing Imprint Method :"+e.getMessage());             
		   	return null;
		   	
		   }
		impmthdList.addAll(setOfImprintMethod);
		return impmthdList;
		
	}
}
