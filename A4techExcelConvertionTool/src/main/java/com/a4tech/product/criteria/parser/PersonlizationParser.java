package com.a4tech.product.criteria.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.Personalization;
import com.a4tech.util.ApplicationConstants;


public class PersonlizationParser {
	
	private Logger              _LOGGER              = Logger.getLogger(getClass());
	
	public  List<Personalization> getPersonalization(
			String personalizevalue) {

		List<Personalization> personaliseList = new ArrayList<Personalization>();
		try{
		String PersonalizationArr[] = personalizevalue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		for (int i = 0; i <= PersonalizationArr.length - 1; i++) {
			Personalization perObj = new Personalization();
			String pers = PersonalizationArr[i];
			String[] temp = null;
			if (pers.contains(ApplicationConstants.CONST_STRING_EQUAL)) {
				temp = pers.split(ApplicationConstants.CONST_STRING_EQUAL);
				perObj.setType(temp[0]);
				perObj.setAlias(temp[1]);
			} else {
				perObj.setType(temp[1]);
			}

			personaliseList.add(perObj);
		}
		}catch(Exception e){
			_LOGGER.error("Error while processing Personalization :"+e.getMessage());
			return new ArrayList<Personalization>();
		}
		return personaliseList;

	}
}