package parser.goldstarcanada;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.model.ImprintMethod;


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
