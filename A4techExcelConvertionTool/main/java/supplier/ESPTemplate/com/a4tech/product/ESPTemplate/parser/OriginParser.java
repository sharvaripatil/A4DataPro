package com.a4tech.product.ESPTemplate.parser;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Origin;
import com.a4tech.util.ApplicationConstants;

public class OriginParser {
	
	public List<Origin> getOriginValues(String originValue){
		List<Origin> listOfOrigin   = new ArrayList<Origin>();
		Origin origin = new Origin();
		if(originValue.equalsIgnoreCase(ApplicationConstants.CONST_STRING_COUNTRY_CODE_CN)){
			originValue = ApplicationConstants.CONST_STRING_COUNTRY_NAME_CHINA;
		}
		origin.setName(originValue);
		listOfOrigin.add(origin);
		return listOfOrigin;
	}
}
