package com.a4tech.sage.product.parser;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Origin;
import com.a4tech.util.ApplicationConstants;

public class OriginParser {
	
	public List<Origin> getOriginValues(String originValue){
		List<Origin> listOfOrigin   = new ArrayList<Origin>();
		Origin origin = new Origin();
		if(originValue.equalsIgnoreCase(ApplicationConstants.CONST_STRING_COUNTRY_CODE_CH)){
			originValue = ApplicationConstants.CONST_STRING_COUNTRY_NAME_CHINA;
		}else if(originValue.equalsIgnoreCase(ApplicationConstants.CONST_STRING_COUNTRY_CODE_US)){
			originValue = ApplicationConstants.CONST_STRING_COUNTRY_NAME_USA;
		}else{
			
		}
		origin.setName(originValue);
		listOfOrigin.add(origin);
		return listOfOrigin;
	}
  
	public String  getCountryCodeConvertName(String countryCode){
		String countryName = null;
		if(countryCode.equalsIgnoreCase(ApplicationConstants.CONST_STRING_COUNTRY_CODE_CH)){
			countryName = ApplicationConstants.CONST_STRING_COUNTRY_NAME_CHINA;
		}else if(countryCode.equalsIgnoreCase(ApplicationConstants.CONST_STRING_COUNTRY_CODE_US)){
			countryName = ApplicationConstants.CONST_STRING_COUNTRY_NAME_USA;
		}else{
			
		}
		return countryName;
	}
}
