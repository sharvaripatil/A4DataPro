package com.a4tech.apparel.product.parser;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Availability;
import com.a4tech.product.model.AvailableVariations;
import com.a4tech.util.ApplicationConstants;

/*@author Venkat
 *@param List List ,ParentList ChildList
 *@description This class design for parsing availablity's of the 
 *             product with different parent and child values
 *@return List of Availability              
 */
public class ApparealAvailabilityParser {
	
	public List<Availability> getProductAvailablity(List<String> parentList,List<String> childList){
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
				 listOfParent.add(ParentValue);
				 listOfChild.add(childValue);
				 AvailableVariObj.setParentValue(listOfParent);
				 AvailableVariObj.setChildValue(listOfChild);
				 listOfVariAvail.add(AvailableVariObj);
			}
		}
		availabilityObj.setAvailableVariations(listOfVariAvail);
		availabilityObj.setParentCriteria(ApplicationConstants.CONST_STRING_PRODUCT_COLOR);
		availabilityObj.setChildCriteria(ApplicationConstants.CONST_STRING_SIZE);
		listOfAvailablity.add(availabilityObj);
		
		return listOfAvailablity;
	}

}
