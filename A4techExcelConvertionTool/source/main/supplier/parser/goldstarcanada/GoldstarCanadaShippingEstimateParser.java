package parser.goldstarcanada;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;

public class GoldstarCanadaShippingEstimateParser {
	
	public ShippingEstimate getShippingEstimateValues(String length,String width,String height,
			                                         String weight,String numberOfItems){
		ShippingEstimate shippingEstimate = new ShippingEstimate();
		Dimensions dimension = null;
		List<NumberOfItems> numberOfItem = null;
		List<Weight> listOfWeightObj = null;
		Weight  weightObj = null;
		NumberOfItems numberOfItemsObj = null;
		if(!length.equals(ApplicationConstants.CONST_STRING_ZERO)){
			dimension = new Dimensions();
			dimension.setLength(length);
			dimension.setWidth(width);
			dimension.setHeight(height);
			dimension.setLengthUnit(ApplicationConstants.CONST_STRING_INCHES);
			dimension.setWidthUnit(ApplicationConstants.CONST_STRING_INCHES);
			dimension.setHeightUnit(ApplicationConstants.CONST_STRING_INCHES);
		}
		if(!weight.equals(ApplicationConstants.CONST_STRING_ZERO)){
			listOfWeightObj = new ArrayList<Weight>();
			weightObj = new Weight();
			weightObj.setValue(weight);
			weightObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_WEIGHT);
			listOfWeightObj.add(weightObj);
		}
		if(!numberOfItems.equals(ApplicationConstants.CONST_STRING_ZERO)){
			numberOfItem = new ArrayList<NumberOfItems>();
			numberOfItemsObj = new NumberOfItems();
			numberOfItemsObj.setValue(numberOfItems);
			numberOfItemsObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_NUMBER_UNIT_CARTON);
			numberOfItem.add(numberOfItemsObj);
		}
		if(dimension != null){
			shippingEstimate.setDimensions(dimension);
		}
		if(numberOfItem != null){
			shippingEstimate.setNumberOfItems(numberOfItem);
		}
		if(listOfWeightObj != null){
			shippingEstimate.setWeight(listOfWeightObj);
		}
		
		return shippingEstimate;
	}

}
