package com.a4tech.product.kuku.criteria.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;

public class ProductShippingEstimationParser {

	private Logger              _LOGGER              = Logger.getLogger(getClass());
	
	public ShippingEstimate getShippingEstimates( String shippingWeightValue) {
		ShippingEstimate shipingObj = new ShippingEstimate();
		List<Weight> listWeights=new ArrayList<Weight>();
		
		try{
			Weight weightObj = new Weight();
			if(shippingWeightValue.contains(ApplicationConstants.CONST_SHIPPING_UNIT_KG)){
				shippingWeightValue=shippingWeightValue.replaceAll(ApplicationConstants.CONST_SHIPPING_UNIT_KG,ApplicationConstants.CONST_STRING_EMPTY);
			
			}
			if(shippingWeightValue.contains("s")){
				shippingWeightValue=shippingWeightValue.replaceAll("s",ApplicationConstants.CONST_STRING_EMPTY);
			}
			weightObj.setUnit(ApplicationConstants.CONST_SHIPPING_UNIT_KG);
			weightObj.setValue(shippingWeightValue.trim());
			listWeights.add(weightObj);
			shipingObj.setWeight(listWeights);
		
		}catch(Exception e){
			_LOGGER.error("Error while processing Shipping Estimate :"+e.getMessage());
			return null;
		}
		return shipingObj;

	}
}
