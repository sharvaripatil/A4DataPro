package com.a4tech.v2.criteria.parser;

import org.apache.log4j.Logger;

import com.a4tech.util.ApplicationConstants;
import com.a4tech.v2.core.model.SameDayRush;

public class ProductSameDayParser {
	
	private Logger              _LOGGER              = Logger.getLogger(getClass());
	
	public SameDayRush getSameDayRush(String value){
		SameDayRush sdayObj=new SameDayRush();
		try{
		if(value.contains(ApplicationConstants.CONST_DELIMITER_COLON)){
		String samedyArr[]=value.split(ApplicationConstants.CONST_DELIMITER_COLON);
		
		if(samedyArr.length==2 && samedyArr[0].equalsIgnoreCase(ApplicationConstants.CONST_CHAR_Y)){
			sdayObj.setAvailable(true);
			sdayObj.setDetails(samedyArr[1]);
		}else if(samedyArr.length==1 && samedyArr[0].equalsIgnoreCase(ApplicationConstants.CONST_CHAR_Y)){
			sdayObj.setAvailable(true);
			sdayObj.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
		}
		}else if(value.equalsIgnoreCase(ApplicationConstants.CONST_CHAR_Y)){
			sdayObj.setAvailable(true);
			sdayObj.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
		}else{
			sdayObj.setAvailable(false);
			sdayObj.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
			
		}
		
		}catch(Exception e){
			_LOGGER.error("Error while processing SameDay Parser :"+e.getMessage());           
		   	return null;
		   	
		   }
		return sdayObj;
	}

}
