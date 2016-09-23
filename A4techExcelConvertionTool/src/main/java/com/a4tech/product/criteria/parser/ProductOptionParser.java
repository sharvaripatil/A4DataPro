package com.a4tech.product.criteria.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.util.ApplicationConstants;

public class ProductOptionParser {
	
	private Logger              _LOGGER              = Logger.getLogger(getClass());
	public Option getOptions(String optionTypeValue, String optionNameValue,
			String optionValue, String canOrderValue, String reqOrderValue,
			String optionInfoValue) {

		Option optionObj=new Option();
		   try{
		  List<OptionValue> valuesList=new ArrayList<OptionValue>();
		  String optionTypeArr[]=optionTypeValue.split(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
		  String valuesArr[]=optionValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		  OptionValue optionValueObj=null;
		  
		  for (String string : valuesArr) {
			  optionValueObj=new OptionValue();
			  optionValueObj.setValue(string);
			  valuesList.add(optionValueObj);
		  }
		  
		  boolean canordeFlag=false;
		  boolean requiredFlag=false;
		  if(canOrderValue.equalsIgnoreCase(ApplicationConstants.CONST_STRING_CAPITAL_Y)){
		   canordeFlag=true;
		  }
		  if(reqOrderValue.equalsIgnoreCase(ApplicationConstants.CONST_STRING_CAPITAL_Y)){
		   requiredFlag=true;
		  }
		  
		  optionObj.setOptionType(optionTypeArr[0]);
		  optionObj.setName(optionNameValue);
		  optionObj.setValues(valuesList);
		  optionObj.setAdditionalInformation(optionInfoValue);
		  optionObj.setCanOnlyOrderOne(canordeFlag);
		  optionObj.setRequiredForOrder(requiredFlag);
		   }catch(Exception e){
			   _LOGGER.error("Error while processing Options :"+e.getMessage());          
		      return null;
		      
		     }
		  return optionObj;
		  
		 }
}