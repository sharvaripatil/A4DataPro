package parser.goldstarcanada;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ProductConfigurations;


public class GoldstarCanadaImprintMethodParser {
	
	private LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;
	
	public List<ImprintMethod> getImprintMethodValues(String imprintMethod,
			                                           List<ImprintMethod> imprintMethodList){
		List<ImprintMethod> imprintMethodsList = new ArrayList<ImprintMethod>();
		imprintMethod=imprintMethod.replace("4-color process", "Full Color");
		imprintMethod=imprintMethod.replace("Pad printed", "Pad Print");
		imprintMethod=imprintMethod.replace("Screen printed", "Silkscreen");
		imprintMethod=imprintMethod.replace("Full-color digital", "Full Color");

		ImprintMethod imprMethod = new ImprintMethod();
		ImprintMethod imprMethod1 = new ImprintMethod();

		
		if(imprintMethod.contains("Laser"))
		{
			imprMethod = new ImprintMethod();
			 imprMethod.setAlias("Laser Engraved");
			 imprMethod.setType("Laser Engraved");
			 imprintMethodsList.add(imprMethod);
		}

		else{
		List<String> finalImprintValues = getImprintValue(imprintMethod.toUpperCase().trim());	
		for (String innerValue : finalImprintValues) {
		    	 imprMethod = new ImprintMethod();
				 imprMethod.setAlias(innerValue);
				 imprMethod.setType(innerValue);
				 imprintMethodsList.add(imprMethod);  
		}
	   if(imprintMethodsList.isEmpty()){
		 imprMethod1.setAlias("Printed");
		 imprMethod1.setType("Printed");
		 imprintMethodsList.add(imprMethod1);
	   }
		}
		return imprintMethodsList;
	}

	
	public List<String> getImprintValue(String value){
		List<String> imprintLookUpValue = lookupServiceDataObj.getImprintMethods();
		List<String> finalImprintValues = imprintLookUpValue.stream()
				                                  .filter(impntName -> value.contains(impntName))
				                                  .collect(Collectors.toList());
                                                 
				
		return finalImprintValues;	
	}
	public  ProductConfigurations addImprintMethod(ProductConfigurations productConfigObj){
        List<ImprintMethod> imprintMethodList = new ArrayList<>();
        ImprintMethod imprintMethodObj = new ImprintMethod();
        imprintMethodObj.setAlias("Unimprinted");
        imprintMethodObj.setType("Unimprinted");
        imprintMethodList.add(imprintMethodObj);
        productConfigObj.setImprintMethods(imprintMethodList);
       return productConfigObj;
	} 

	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}

	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}

	public LookupRestService getLookupRestServiceObj() {
		return lookupRestServiceObj;
	}

	public void setLookupRestServiceObj(LookupRestService lookupRestServiceObj) {
		this.lookupRestServiceObj = lookupRestServiceObj;
	}
	
	
	
	
	
	
	
	
}
