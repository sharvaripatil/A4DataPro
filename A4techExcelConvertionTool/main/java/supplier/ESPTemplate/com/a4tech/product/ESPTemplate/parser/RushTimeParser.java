package com.a4tech.product.ESPTemplate.parser;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.util.ApplicationConstants;

public class RushTimeParser {
	
	
	public RushTime getRushTimeValues(String rushTime ,String rushService){
		List<RushTimeValue> RushTimevalueList   = new ArrayList<RushTimeValue>();
		RushTime RushTimeObj=new RushTime();
		
		if(rushService.equalsIgnoreCase("Y"))
		{
		RushTimeObj.setAvailable(true);
		RushTimeValue RushTimeValueObj=new RushTimeValue();
		RushTimeValueObj.setBusinessDays(rushTime);	
		RushTimeValueObj.setDetails("");
		RushTimevalueList.add(RushTimeValueObj);
		
		RushTimeObj.setRushTimeValues(RushTimevalueList);
		}
		
		
		return RushTimeObj;	
	}

}
