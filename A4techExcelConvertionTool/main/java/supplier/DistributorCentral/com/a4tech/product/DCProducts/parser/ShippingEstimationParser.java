package com.a4tech.product.DCProducts.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;



public class ShippingEstimationParser {

	private Logger              _LOGGER              = Logger.getLogger(getClass());
	
	public ShippingEstimate getShippingEstimates(String shippingValue) {
		/*Author :Amey
		Changes done:
	    Done code changes for Shipping Estimate for following scenarios
		1)25 lbs per 200 pads
		2)11 lbs per 250
		3)1.0 lbs
		
		Note: Other values processing not done as exact requirement is not freeze from business  side or any particular 
		rule is not decided for stating value for ShippingEstimate ,Currently String keeps on changing for different values. 
		*/
		String shippingdimensionValue=null;
		String shippingWeightValue=null;
		String shippingitemValue=null;
		
		ShippingEstimate ItemObject = new ShippingEstimate();
		try{
		if(shippingValue.contains("lbs")){
			shippingValue=	shippingValue.replaceAll("lbs", "lbs,");
			String shipArray[]=shippingValue.split(ApplicationConstants.CONST_DELIMITER_COMMA);
			if(shipArray.length==2){
				shippingWeightValue=shipArray[0];
				shippingWeightValue=shippingWeightValue.trim().replaceAll(ApplicationConstants.CONST_VALUE_TYPE_SPACE, ApplicationConstants.CONST_DELIMITER_COLON);
				shippingitemValue=shipArray[1];
				shippingitemValue=shippingitemValue.trim().replaceAll(ApplicationConstants.CONST_VALUE_TYPE_SPACE, ApplicationConstants.CONST_DELIMITER_COLON);
			}else{
				shippingWeightValue=shipArray[0];
				shippingWeightValue=shippingWeightValue.trim().replaceAll(ApplicationConstants.CONST_VALUE_TYPE_SPACE, ApplicationConstants.CONST_DELIMITER_COLON);
			}
		}
		
		
		
		List<NumberOfItems> listOfNumberOfItems = new ArrayList<NumberOfItems>();
		List<Weight> listOfWeight = new ArrayList<Weight>();

		NumberOfItems itemObj = new NumberOfItems();
		if (shippingitemValue != null && !shippingitemValue.isEmpty()) {
			String shipItemArr[] = shippingitemValue.split(ApplicationConstants.CONST_DELIMITER_COLON);
			
			if(shipItemArr.length==3){
				itemObj.setUnit(shipItemArr[0] +ApplicationConstants.CONST_VALUE_TYPE_SPACE+shipItemArr[2]);
				itemObj.setValue(shipItemArr[1]);
					
			}else if(shipItemArr.length==2){
				itemObj.setUnit(ApplicationConstants.CONST_STRING_EMPTY);
				itemObj.setValue(shipItemArr[1]);
			}
			
			listOfNumberOfItems.add(itemObj);
			ItemObject.setNumberOfItems(listOfNumberOfItems);
			
		}

		if (shippingWeightValue != null && !shippingWeightValue.isEmpty()) {
			Weight weightObj = new Weight();
			String shipweightArr[] = shippingWeightValue.split(ApplicationConstants.CONST_DELIMITER_COLON);
			
			if(shipweightArr.length==2){
			weightObj.setUnit(shipweightArr[1]);
			weightObj.setValue(shipweightArr[0]);
			}else{
				weightObj.setUnit("lbs");
				weightObj.setValue(shippingWeightValue);
			}
			
			listOfWeight.add(weightObj);
			ItemObject.setWeight(listOfWeight);
		}

		/*if (shippingdimensionValue != null && !shippingdimensionValue.isEmpty()) {
			String shipDimenArr[] = shippingdimensionValue.split(ApplicationConstants.CONST_DELIMITER_SEMICOLON);
			List<Dimensions> dimenlist = new ArrayList<Dimensions>();
			Dimensions dimensionObj = new Dimensions();
			for (int i = 0; i <= shipDimenArr.length - 1; i++) {

				String[] shipDimenArr1 = shipDimenArr[i].split(ApplicationConstants.CONST_DELIMITER_COLON);
				if (i == 0) {
					dimensionObj.setHeight(shipDimenArr1[0]);
					dimensionObj.setHeightUnit(shipDimenArr1[1]);
				} else if (i == 1) {
					dimensionObj.setLength(shipDimenArr1[0]);
					dimensionObj.setLengthUnit(shipDimenArr1[1]);
				} else if (i == 2) {

					dimensionObj.setWidth(shipDimenArr1[0]);
					dimensionObj.setWidthUnit(shipDimenArr1[1]);
				}

				dimenlist.add(dimensionObj);
			}
			ItemObject.setDimensions(dimensionObj);
		}*/
		}catch(Exception e){
			_LOGGER.error("Error while processing Shipping Estimate :"+e.getMessage());
			return null;
		}
		return ItemObject;

	}
}
