package com.a4tech.lookup.service;

import java.util.List;

import com.a4tech.lookup.model.Catalog;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.util.ApplicationConstants;

public class LookupServiceData {
	
	private LookupRestService  lookupRestService;
	public static List<String> imprintMethods 		= null;
	public static List<String> materialValues 		= null;
	public static List<String> shapes 				= null;
	public static List<String> listOfOrigins 		= null;
	public List<String> 	   listOfFobPoints 		= null;
	public List<String>        lineNames 			= null;
	public List<String>        tradeNames  			= null;
	public static List<String> categories 			= null;
	public static List<Catalog> catalogs            = null;
	public static List<String>   themes  		    = null;
    public static List<String> packages             = null;
    
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
	public List<String> getFobPoints(String authToken){
		if(listOfFobPoints == null){
			listOfFobPoints = lookupRestService.getFobPoints(authToken);
		}
		return listOfFobPoints;
	}
	public List<String> getLineNames(String authToken){
		if(lineNames == null){
			lineNames = lookupRestService.getLineNames(authToken);
		}
		return lineNames;
	}
	public List<String> getCategories(){
		if(categories == null){
			categories = lookupRestService.getCategories();
		}
		return categories;
	}
	public List<String> getTradeNames(String tradeName){
		if(tradeNames == null){
			tradeNames = lookupRestService.getTradeNames(tradeName);
		}
		return tradeNames;
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
     public boolean isCategory(String categoryName){
    	 if(categories == null){
    		 categories = getCategories();
 		}
 		return categories.contains(categoryName);
     }
     
     public List<Catalog> getCatalog(String authToken){
    	 if(catalogs == null){
    		 catalogs = lookupRestService.getCatalogs(authToken);
    	 }
    	 return catalogs;
     }
     
     public List<String> getTheme(String authToken){
    	 if(themes == null){
    		 themes = lookupRestService.getTheme();
    	 }
    	 return themes;
     }
     public boolean isTradeName(String tradeName){
 		if(tradeNames == null){
 			tradeNames = getTradeNames(tradeName);
 		}
 		if(tradeNames != null){
 			return tradeNames.contains(tradeName);
 		}
 		return false;
 	}
    public boolean isTheme(String themeVal){
    	if(themes == null){
    		themes = getTheme(themeVal);
 		}
 		if(themes != null){
 			return themes.contains(themeVal);
 		}
    	return false;
    }
     public List<String> getPackageValues(){
    	 if(packages == null){
    		 packages = lookupRestService.getPackages();
    	 }
    	 return packages;
     }
 	
	public LookupRestService getLookupRestService() {
		return lookupRestService;
	}
	public void setLookupRestService(LookupRestService lookupRestService) {
		this.lookupRestService = lookupRestService;
	}
	
	
}
