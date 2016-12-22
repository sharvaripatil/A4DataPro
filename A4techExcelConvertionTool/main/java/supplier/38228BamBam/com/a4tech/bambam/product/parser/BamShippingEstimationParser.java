package com.a4tech.bambam.product.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;

public class BamShippingEstimationParser {
private final static Logger  _LOGGER  = Logger.getLogger(BamShippingEstimationParser.class);
	
	public ShippingEstimate getShippingEstimatesValues(String shippingitemValue,
			String shippingdimensionValue, String shippingWeightValue) {

		
		ShippingEstimate ItemObject = new ShippingEstimate();
		
		try{
		List<NumberOfItems> listOfNumberOfItems = new ArrayList<NumberOfItems>();
		List<Weight> listOfWeight = new ArrayList<Weight>();

		NumberOfItems itemObj = new NumberOfItems();
		if (shippingitemValue != null && !shippingitemValue.isEmpty()) {
			String shipItemArr[] = shippingitemValue.split(ApplicationConstants.CONST_DELIMITER_COLON);
			itemObj.setUnit(shipItemArr[1]);
			itemObj.setValue(shipItemArr[0]);
			listOfNumberOfItems.add(itemObj);
			ItemObject.setNumberOfItems(listOfNumberOfItems);
		}

		if (shippingWeightValue != null && !shippingWeightValue.isEmpty()) {
			Weight weightObj = new Weight();
			String shipweightArr[] = shippingWeightValue.split(ApplicationConstants.CONST_DELIMITER_COLON);
			weightObj.setUnit(shipweightArr[1]);
			weightObj.setValue(shipweightArr[0]);
			listOfWeight.add(weightObj);
			ItemObject.setWeight(listOfWeight);
		}

		if (shippingdimensionValue != null && !shippingdimensionValue.isEmpty()) {
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
		}
		}catch(Exception e){
			_LOGGER.error("Error while processing Shipping Estimate :"+e.getMessage());
			return new ShippingEstimate();
		}
		return ItemObject;

	}
}
