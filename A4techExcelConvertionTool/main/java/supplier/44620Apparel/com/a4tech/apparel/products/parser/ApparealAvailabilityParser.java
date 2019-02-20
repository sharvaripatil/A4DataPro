package com.a4tech.apparel.products.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.a4tech.product.model.Availability;
import com.a4tech.product.model.AvailableVariations;
import com.a4tech.util.ApplicationConstants;

//@author Venkat            

public class ApparealAvailabilityParser {
	public List<Availability> getProductAvailablity(Set<String> childList ,Set<String> parentList){
		List<Availability> listOfAvailablity = new ArrayList<>();
		Availability  availabilityObj = new Availability();
		AvailableVariations  AvailableVariObj = null;
		List<AvailableVariations> listOfVariAvail = new ArrayList<>();
		List<Object> listOfParent = null;
		List<Object> listOfChild = null;
		for (String ParentValue : parentList) {
			 for (String childValue : childList) {
				 AvailableVariObj = new AvailableVariations();
				 listOfParent = new ArrayList<>();
				 listOfChild = new ArrayList<>();
				 listOfParent.add(ParentValue.trim());
				 listOfChild.add(childValue.trim());
				 AvailableVariObj.setParentValue(listOfParent);
				 AvailableVariObj.setChildValue(listOfChild);
				 listOfVariAvail.add(AvailableVariObj);
			}
		}
		availabilityObj.setAvailableVariations(listOfVariAvail);
		availabilityObj.setParentCriteria(ApplicationConstants.CONST_STRING_SIZE);
		availabilityObj.setChildCriteria(ApplicationConstants.CONST_STRING_PRODUCT_COLOR);
		listOfAvailablity.add(availabilityObj);
		
		return listOfAvailablity;
	}

}
