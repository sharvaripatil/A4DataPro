package parser.radious;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;

public class RadiousAttribute {

	
	public ShippingEstimate getShippingEstimates(String cartonWeight,
			String cartonWidth, String cartonHeight, String cartonLength, String unitsperCarton) {

		
		ShippingEstimate ItemObject = new ShippingEstimate();
		
		List<NumberOfItems> listOfNumberOfItems = new ArrayList<NumberOfItems>();

		List<Object> shipingEstObj1 = new ArrayList<Object>();
		    NumberOfItems itemObj = new NumberOfItems();
			itemObj.setUnit("Carton");
			itemObj.setValue(cartonHeight);
			shipingEstObj1.add(itemObj);
			ItemObject.setNumberOfItems(listOfNumberOfItems);
	
	
		List<Weight> listOfWeight = new ArrayList<Weight>();
			Weight weightObj = new Weight();
			weightObj.setUnit("lbs");
			weightObj.setValue(cartonWeight);
			listOfWeight.add(weightObj);
			ItemObject.setWeight(listOfWeight);
			
	
		
			List<Dimensions> dimenlist = new ArrayList<Dimensions>();
			Dimensions dimensionObj = new Dimensions();
		 
	        dimensionObj.setHeight(cartonHeight);
			dimensionObj.setHeightUnit("in");
					
			dimensionObj.setLength(cartonLength);
			dimensionObj.setLengthUnit("in");

			dimensionObj.setWidth(cartonWidth);
			dimensionObj.setWidthUnit("in");

			dimenlist.add(dimensionObj);
			ItemObject.setDimensions(dimensionObj);
			
		
		return ItemObject;

	}
	
	
	
	
	
	
	
}
