package com.a4tech.sage.product.parser;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.util.ApplicationConstants;

public class RushTimeParser {
	
	public RushTime getRushTimeValues(String rushTime ,RushTime existingRushTime){
		existingRushTime.setAvailable(ApplicationConstants.CONST_BOOLEAN_TRUE);
		List<RushTimeValue> existingRushTimeValues = existingRushTime.getRushTimeValues();
		if(existingRushTimeValues == null){
			existingRushTimeValues = new ArrayList<RushTimeValue>();
		}
		RushTimeValue newRushTimeValue = new RushTimeValue();
		newRushTimeValue.setBusinessDays(rushTime);
		newRushTimeValue.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
		existingRushTimeValues.add(newRushTimeValue);
		existingRushTime.setRushTimeValues(existingRushTimeValues);
		return existingRushTime;
	}

}
