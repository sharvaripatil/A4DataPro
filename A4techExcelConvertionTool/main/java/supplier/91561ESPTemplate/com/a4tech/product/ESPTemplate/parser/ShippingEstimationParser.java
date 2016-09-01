package com.a4tech.product.ESPTemplate.parser;

import java.util.ArrayList;
import java.util.List;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;

public class ShippingEstimationParser {


	public ShippingEstimate getShippingEstimates(String shippingWeightValue) {

		ShippingEstimate ItemObject = new ShippingEstimate();		
		
		List<Weight> shipingEstObj = new ArrayList<Weight>();
		Weight weightObj = new Weight();
		
		if(shippingWeightValue.contains(ApplicationConstants.CONST_SHIPPING_UNIT_KG))
		{
			String newshippingWeightValue=shippingWeightValue.replace(ApplicationConstants.CONST_SHIPPING_UNIT_KG,ApplicationConstants.CONST_STRING_EMPTY);
			weightObj.setValue(newshippingWeightValue);
			weightObj.setUnit(ApplicationConstants.CONST_SHIPPING_UNIT_KG);
             shipingEstObj.add(weightObj);

		}

		else if(shippingWeightValue.contains("g")){  
		  
		  String newshippingWeightValue=shippingWeightValue.replace("g","");
		  weightObj.setValue(newshippingWeightValue);
		  weightObj.setUnit(ApplicationConstants.CONST_SHIPPING_UNIT_GRAMS);
		  shipingEstObj.add(weightObj);
		}
		else
		{
			  weightObj.setValue(shippingWeightValue);
			  weightObj.setUnit(ApplicationConstants.CONST_SHIPPING_UNIT_KG);
	          shipingEstObj.add(weightObj);
	
		}
		  ItemObject.setWeight(shipingEstObj);
		
		return ItemObject;

	}
}
