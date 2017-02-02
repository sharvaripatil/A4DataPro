package com.a4tech.bambam.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a4tech.lookup.service.LookupServiceData;

public class BamLookupData {
	private LookupServiceData lookupServiceData = null;
	private static final Map<String, List<String>> listOfFobPoints = new HashMap<>();
	
	public void loadFobPoints(String supplierNo,String authToken){
		List<String> SupplierLineNames = listOfFobPoints.get(supplierNo);
		if(SupplierLineNames == null){
			SupplierLineNames = lookupServiceData.getFobPoints(authToken);
			listOfFobPoints.put(supplierNo, SupplierLineNames);
		}
		
	}
	public List<String> getFobPoints(String supplierNo){
	     return	listOfFobPoints.get(supplierNo);
	}
	public LookupServiceData getLookupServiceData() {
		return lookupServiceData;
	}

	public void setLookupServiceData(LookupServiceData lookupServiceData) {
		this.lookupServiceData = lookupServiceData;
	}
}
