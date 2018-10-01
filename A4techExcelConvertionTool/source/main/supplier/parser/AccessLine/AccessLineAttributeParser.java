package parser.AccessLine;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;

public class AccessLineAttributeParser {
	
	
	public static ShippingEstimate getShippingEstimates( String shippingValue,String sdimVAl,ShippingEstimate shippingEstObj,String str,String sdimType) {
	//ShippingEstimate shipingObj = new ShippingEstimate();
	if(str.equals("NOI")){
		List<NumberOfItems> listOfNumberOfItems = new ArrayList<NumberOfItems>();
		NumberOfItems itemObj = new NumberOfItems();
		itemObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_NUMBER_UNIT_CASE);
		itemObj.setValue(shippingValue.trim());
		listOfNumberOfItems.add(itemObj);
		shippingEstObj.setNumberOfItems(listOfNumberOfItems);
	}
	
	if (str.equals("WT")) {
		List<Weight> listOfWeight = new ArrayList<Weight>();
		Weight weightObj = new Weight();
		weightObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_WEIGHT);
		weightObj.setValue(shippingValue);
		listOfWeight.add(weightObj);
		shippingEstObj.setWeight(listOfWeight);
	}
	
	if (str.equals("SDIM")) {
		Dimensions dimensionObj=shippingEstObj.getDimensions();
		Dimensions tempObj=new Dimensions();
		if(dimensionObj== null || dimensionObj.equals(tempObj)){
			dimensionObj=new Dimensions();
		}
		String unit="in";
				//Dimensions dimensionObj = new Dimensions();
		if (sdimType.equals("L")){//if (sdimType.equals("L")){
				dimensionObj.setLength(sdimVAl.trim());
				dimensionObj.setLengthUnit(unit);
		}
		if (sdimType.equals("W")){
				dimensionObj.setWidth(sdimVAl.trim());
				dimensionObj.setWidthUnit(unit);
		}
		if (sdimType.equals("H")){
				dimensionObj.setHeight(sdimVAl.trim());
				dimensionObj.setHeightUnit(unit);
		}
		shippingEstObj.setDimensions(dimensionObj);
				}
	return shippingEstObj;
}}
