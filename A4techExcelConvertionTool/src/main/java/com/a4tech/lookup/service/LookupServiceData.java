package com.a4tech.lookup.service;

import java.util.List;

import com.a4tech.lookup.service.restService.LookupRestService;

public class LookupServiceData {
	
	private static LookupRestService lookupRestService;
	public static List<String> imprintMethods = null;
	public static List<String> materialValues = null;
	public static List<String> getImprintMethods(){
		 
		  if(imprintMethods == null){
			  imprintMethods = lookupRestService.getImprintMethodData();
			  return imprintMethods;
		  }
		return null;
	}
	public static List<String> getMaterialValues(){
		 
		  if(imprintMethods == null){
			  materialValues = lookupRestService.getMaterialsData();
			  return materialValues;
		  }
		return null;
	}
	public boolean isImprintMethod(String imprintValue){
		if(imprintMethods == null){
			imprintMethods = getImprintMethods();
		}
		if(imprintMethods != null){
			return imprintMethods.contains(imprintValue);
		}
		return false;
	}
	
	public boolean isMaterial(String matrlValue){
		if(materialValues == null){
			materialValues = getMaterialValues();
		}
		if(materialValues != null){
			return materialValues.contains(matrlValue);
		}
		return false;
	}
	public LookupRestService getLookupRestService() {
		return lookupRestService;
	}
	public void setLookupRestService(LookupRestService lookupRestService) {
		this.lookupRestService = lookupRestService;
	}
	
	
}
