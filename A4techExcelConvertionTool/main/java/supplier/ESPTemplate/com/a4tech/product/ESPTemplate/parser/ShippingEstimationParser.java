package com.a4tech.product.ESPTemplate.parser;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;

public class ShippingEstimationParser {


	public ShippingEstimate getShippingEstimates(String shippingWeightValue) {

		ShippingEstimate ItemObject = new ShippingEstimate();

		String shippingWeightValuenew=	shippingWeightValue.replaceAll(ApplicationConstants.CONST_STRING_SHIPPINGWT_UNIT_KG, "");
		
		
		List<Weight> shipingEstObj = new ArrayList<Weight>();
		Weight weightObj = new Weight();
		
		if(shippingWeightValue.contains("kg"))
		{
			String newshippingWeightValue=shippingWeightValue.replace("kg","");
			weightObj.setValue(newshippingWeightValue);
			weightObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPINGWT_UNIT_KG);
             shipingEstObj.add(weightObj);

		}

		else if(shippingWeightValue.contains("g")){  
		  
		  String newshippingWeightValue=shippingWeightValue.replace("g","");
		  weightObj.setValue(newshippingWeightValue);
		  weightObj.setUnit("grams");
		  shipingEstObj.add(weightObj);
		}
		else
		{
			  weightObj.setValue(shippingWeightValue);
			  weightObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPINGWT_UNIT_KG);
	          shipingEstObj.add(weightObj);
	
		}
		  ItemObject.setWeight(shipingEstObj);
		
		return ItemObject;

	}
}
