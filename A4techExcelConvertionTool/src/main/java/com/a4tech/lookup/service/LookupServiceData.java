package com.a4tech.lookup.service;

import java.util.List;

import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.util.ApplicationConstants;

public class LookupServiceData {
	
	private LookupRestService lookupRestService;
	public static List<String> imprintMethods = null;
	public static List<String> materialValues = null;
	public static List<String> shapes = null;
	public static List<String> listOfOrigins = null;
	
	public  List<String> getImprintMethods(){
		  if(imprintMethods == null){
			  imprintMethods = lookupRestService.getImprintMethodData();
			  return imprintMethods;
		  }
		return imprintMethods;
	}
	public  List<String> getMaterialValues(){
		 
		  if(materialValues == null){
			  materialValues = lookupRestService.getMaterialsData();
			  return materialValues;
		  }
		return materialValues;
	}
	public List<String> getShapeValues(){
		if(shapes == null){
			shapes = lookupRestService.getShapesData();
		}
		return shapes;
	}
	public List<String> getOrigins(){
		if(listOfOrigins == null){
			listOfOrigins = lookupRestService.getOrigins();
		}
		return listOfOrigins;
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
	
	public String getMaterialTypeValue(String materialName){
		if(materialValues == null){
			materialValues = getMaterialValues();
		}
		    if(materialValues != null){
		    	 for (String mtrlName : materialValues) {
					    if(materialName.contains(mtrlName)){
					    	return mtrlName;
					    }
				}
		    }
		return ApplicationConstants.CONST_VALUE_TYPE_OTHER;
	}
	
	public boolean isShape(String name){
		if(shapes == null){
			shapes = getShapeValues();
		}
		return shapes.contains(name);
	}
     public boolean isOrigin(String countryName){
		if(listOfOrigins == null){
			listOfOrigins = getOrigins();
		}
		return listOfOrigins.contains(countryName);
	}
	public LookupRestService getLookupRestService() {
		return lookupRestService;
	}
	public void setLookupRestService(LookupRestService lookupRestService) {
		this.lookupRestService = lookupRestService;
	}
	
	
}
