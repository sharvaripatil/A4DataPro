package com.a4tech.product.kuku.criteria.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Personalization;
import com.a4tech.util.ApplicationConstants;

public class PersonlizationParser {
private Logger              _LOGGER              = Logger.getLogger(getClass());
	
	public List<Personalization> getPersonalizationCriteria(String persValue){
		
		List<Personalization> listPersonalz =new ArrayList<Personalization>();
		
		Personalization persObj=null;
				persObj=new Personalization();
	 			persObj.setType(ApplicationConstants.CONST_STRING_PERSONALIZATION);
	 			persObj.setAlias(ApplicationConstants.CONST_STRING_PERSONALIZATION);
	 			listPersonalz.add(persObj);
	 			_LOGGER.info("PERSONALIZATION processed");
		return listPersonalz;
		
	}
}
