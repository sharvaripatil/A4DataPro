package com.a4tech.sage.product.parser;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.ImprintMethod;
import com.a4tech.util.ApplicationConstants;

public class ImprintMethodParser {
	
	public List<ImprintMethod> getImprintMethodValues(String imprintMethodValue,
			                                           List<ImprintMethod> imprintMethodList){
		 ImprintMethod imprMethod = null;
		if(imprintMethodValue.equals(ApplicationConstants.CONST_STRING_HOT_FOIL_STAMPED)){
			imprMethod = new ImprintMethod();
			imprMethod.setAlias(ApplicationConstants.CONST_STRING_HOT_STAMPED);
			imprMethod.setType(ApplicationConstants.CONST_STRING_HOT_STAMPED);
			imprintMethodList.add(imprMethod);
		}else if(imprintMethodValue.contains(ApplicationConstants.CONST_DELIMITER_COMMA)){
			String[] methodValues = imprintMethodValue.split(ApplicationConstants.CONST_DELIMITER_COMMA);
			for (String imprMethodValue : methodValues) {
				imprMethod = new ImprintMethod();
				imprMethod.setAlias(imprMethodValue);
				imprMethod.setType(imprMethodValue);
				imprintMethodList.add(imprMethod);
			}
		}else if(imprintMethodValue.equalsIgnoreCase(ApplicationConstants.CONST_STRING_TRUE)){
			imprMethod = new ImprintMethod();
			imprMethod.setAlias(ApplicationConstants.CONST_STRING_UNIMPRINTED);
			imprMethod.setType(ApplicationConstants.CONST_STRING_UNIMPRINTED);
			imprintMethodList.add(imprMethod);
		} else{
			imprMethod = new ImprintMethod();
			imprMethod.setAlias(imprintMethodValue);
			imprMethod.setType(imprintMethodValue);
			imprintMethodList.add(imprMethod);
		}
		return imprintMethodList;
	}

}
