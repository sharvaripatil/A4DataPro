package com.a4tech.product.criteria.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.TradeName;
import com.a4tech.util.ApplicationConstants;

public class ProductTradeNameParser {
           
	private Logger              _LOGGER              = Logger.getLogger(getClass());
	
	public List<TradeName> getTradeNameCriteria(String tradename){
		List<TradeName> tradenameList =new ArrayList<>();
		try{
		String tradeNameValue = tradename;
		String tradeArr[] = tradeNameValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		TradeName tradeNameObj = null;
		for (String tempTrade : tradeArr) {
			tradeNameObj = new TradeName();
			tradeNameObj.setName(tempTrade);
 			tradenameList.add(tradeNameObj);
		}
		}catch(Exception e){
			_LOGGER.error("Error while processing Product TradeName :"+e.getMessage());       
		   	return new ArrayList<TradeName>();
		   	
		   }
		return tradenameList;
		
	}
}
