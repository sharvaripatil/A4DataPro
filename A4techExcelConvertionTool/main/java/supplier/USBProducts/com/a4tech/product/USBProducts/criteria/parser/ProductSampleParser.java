package com.a4tech.product.USBProducts.criteria.parser;

import org.apache.log4j.Logger;

import com.a4tech.product.model.Samples;
import com.a4tech.util.ApplicationConstants;

public class ProductSampleParser {

	private Logger              _LOGGER              = Logger.getLogger(getClass());
	
	public Samples getSampleCriteria(String prodsample, String specSample,
			boolean flag) {
		Samples samplesObj = new Samples();
		try{
		String prodSampleValue = prodsample;
		String specSampleValue = prodsample;

		if (flag) {
			String specSampleArr[] = specSampleValue.split(ApplicationConstants.CONST_DELIMITER_COLON);
			if(specSampleArr.length==2){
				String value=specSampleArr[0];
				if(value.equalsIgnoreCase(ApplicationConstants.CONST_CHAR_Y)){
					samplesObj.setSpecSampleAvailable(true);
					samplesObj.setSpecInfo(specSampleArr[1]);
				}
				else{
					samplesObj.setSpecSampleAvailable(false);
					samplesObj.setSpecInfo(ApplicationConstants.CONST_STRING_EMPTY);
				}
			}
			}else{
				samplesObj.setSpecSampleAvailable(false);
				samplesObj.setSpecInfo(ApplicationConstants.CONST_STRING_EMPTY);
			}
		

	

		if (prodSampleValue!=null && !prodSampleValue.isEmpty()) {
			String prodSampleArr[] = prodSampleValue.split(ApplicationConstants.CONST_DELIMITER_COLON);
			if(prodSampleArr.length==2){
				String value=prodSampleArr[0];
				if(value.equalsIgnoreCase(ApplicationConstants.CONST_CHAR_Y)){
					samplesObj.setProductSampleAvailable(true);
					samplesObj.setProductSampleInfo(prodSampleArr[1]);
				}
				else{
					samplesObj.setProductSampleAvailable(false);
					samplesObj.setProductSampleInfo(ApplicationConstants.CONST_STRING_EMPTY);
				}
			}
			}else{
				samplesObj.setProductSampleAvailable(false);
				samplesObj.setProductSampleInfo(ApplicationConstants.CONST_STRING_EMPTY);
			}
		
		specSampleValue=null;
		prodSampleValue=null;
		}catch(Exception e){
			_LOGGER.error("Error while processing Sample Parser :"+e.getMessage());          
		   	return null;
		   	
		   }
		return samplesObj;

	}
}
