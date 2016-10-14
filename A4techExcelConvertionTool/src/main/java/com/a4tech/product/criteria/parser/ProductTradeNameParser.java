package com.a4tech.product.criteria.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.util.ApplicationConstants;

public class ProductTradeNameParser {
           
	private Logger              _LOGGER              = Logger.getLogger(getClass());
	
	public List<String> getTradeNameCriteria(String tradename){
		List<String> tradenameList =new ArrayList<String>();
		try{
		String tradeNameValue = tradename;
		String tradeArr[] = tradeNameValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		
		for (String tempTrade : tradeArr) {
 			tradenameList.add(tempTrade);
		}
		}catch(Exception e){
			_LOGGER.error("Error while processing Product TradeName :"+e.getMessage());       
		   	return new ArrayList<String>();
		   	
		   }
		return tradenameList;
		
	}
}
