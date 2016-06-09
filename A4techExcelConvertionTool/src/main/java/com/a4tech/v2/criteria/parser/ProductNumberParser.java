package com.a4tech.v2.criteria.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.LookupData;
import com.a4tech.v2.core.model.Configurations;
import com.a4tech.v2.core.model.ProductNumber;

public class ProductNumberParser {
	
	private Logger              _LOGGER              = Logger.getLogger(getClass());

	public ProductNumber getProductNumer(String productNumberCriteria1,String productNumberCriteria2,String productNumber ){
		List<ProductNumber> pnumberList=new ArrayList<ProductNumber>();
		ProductNumber pnumberObj=new ProductNumber();
		try{
		List<Configurations> configList=new ArrayList<Configurations>();
		List<Object> Value;
		Configurations configObj;
		/*Map<String, String> criCodeMap=new HashMap<String, String>();
		criCodeMap=CommonUtilites.getMap();*/
		
		
		pnumberObj.setProductNumber(productNumber);
		if(productNumberCriteria1!=null && !productNumberCriteria1.isEmpty()){
			String pnumberArr[]=productNumberCriteria1.split(ApplicationConstants.CONST_DELIMITER_COLON);
			configObj=new Configurations();
			Value= new ArrayList<Object>();
			configObj.setCriteria(LookupData.getCriteriaValue(pnumberArr[0]));
			Value.add(pnumberArr[1]);
			
			configObj.setValue(Value);
			configList.add(configObj);
			
		}
		
		if(productNumberCriteria2!=null && !productNumberCriteria2.isEmpty()){
			String pnumberArr[]=productNumberCriteria2.split(ApplicationConstants.CONST_DELIMITER_COLON);
			configObj=new Configurations();
			Value= new ArrayList<Object>();
			configObj.setCriteria(LookupData.getCriteriaValue(pnumberArr[0]));
			Value.add(pnumberArr[1]);
			
			configObj.setValue(Value);
			configList.add(configObj);
			
		}
		pnumberObj.setConfigurations(configList);
		}catch(Exception e){
			_LOGGER.error("Error while processing Product Number :"+e.getMessage());             
		   	return null;
		   	
		   }
		return pnumberObj;		
	}
}
