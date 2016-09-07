package com.a4tech.RFGLine.products.parser;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Weight;



public class ShippingEstimationParser {
	public ShippingEstimate getShippingEstimateValues(String ShipQty1,String ShipWeight1,String ShipLength1,String ShipWidth1,
			String ShipHeight1){
		ShippingEstimate shippingEstimate = new ShippingEstimate();	
		
		
		
		List<NumberOfItems> numberOfItem = new ArrayList<NumberOfItems>();
		NumberOfItems itemObj=new NumberOfItems();
		if(!StringUtils.isEmpty(ShipQty1)&& !ShipQty1.equals("0") ){
		itemObj.setValue(ShipQty1);
		itemObj.setUnit("case");
		numberOfItem.add(itemObj);
		shippingEstimate.setNumberOfItems(numberOfItem);
		}
		
		
		
		List<Weight> listOfWeightObj = new ArrayList<Weight>();
		Weight  weightObj = new Weight();
		if(!StringUtils.isEmpty(ShipWeight1)&& !ShipWeight1.equals("0")){
		weightObj.setValue(ShipWeight1);
		weightObj.setUnit("lbs");
		listOfWeightObj.add(weightObj);
		shippingEstimate.setWeight(listOfWeightObj);
		}
		
		
		
		Dimensions dimension =new Dimensions();
		if(!StringUtils.isEmpty(ShipLength1)&& !ShipLength1.equals("0") ){
      	dimension.setLength(ShipLength1);
      	dimension.setLengthUnit("in");
      	shippingEstimate.setDimensions(dimension);
		}
		if(!StringUtils.isEmpty(ShipWidth1)&& !ShipWidth1.equals("0")  ){
      	dimension.setWidth(ShipWidth1);
     	dimension.setWidthUnit("in");
     	shippingEstimate.setDimensions(dimension);
		}
		if(!StringUtils.isEmpty(ShipHeight1)&& !ShipHeight1.equals("0")  ){
      	dimension.setHeight(ShipHeight1);
      	dimension.setHeightUnit("in");
      	shippingEstimate.setDimensions(dimension);
		}
	
      	
	
		
		return shippingEstimate;		
}
}