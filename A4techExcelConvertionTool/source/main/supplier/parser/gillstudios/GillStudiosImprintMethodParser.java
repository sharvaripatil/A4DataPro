package parser.gillstudios;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.model.ImprintMethod;

public class GillStudiosImprintMethodParser {
	
	private LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;
	
	public List<ImprintMethod> getImprintMethodValues(String imprintMethod,
			                                           List<ImprintMethod> imprintMethodList){
		try{
		//List<ImprintMethod> imprintMethodsList = new ArrayList<ImprintMethod>();
			if(CollectionUtils.isEmpty(imprintMethodList)){
				imprintMethodList = new ArrayList<ImprintMethod>();
			}
		imprintMethod=imprintMethod.toUpperCase();
		imprintMethod=imprintMethod.replace("4-COLOR PROCESS", "Full Color");
		imprintMethod=imprintMethod.replace("PAD PRINTED", "Pad Print");//FULL COLOR DYE-SUBLIMATION
		imprintMethod=imprintMethod.replace("FULL COLOR DYE-SUBLIMATION", "Full Color");//FULL COLOR DYE-SUBLIMATION
		imprintMethod=imprintMethod.replace("SCREEN PRINTED", "Silkscreen");
		ImprintMethod imprMethod = new ImprintMethod();
		
		if(imprintMethod.equalsIgnoreCase("true"))
		{
			imprMethod = new ImprintMethod();
			 imprMethod.setAlias("UNIMPRINTED");
			 imprMethod.setType("UNIMPRINTED");
			 imprintMethodList.add(imprMethod);
		}
		else{
		List<String> finalImprintValues = getImprintValue(imprintMethod.toUpperCase().trim());
		if(!CollectionUtils.isEmpty(finalImprintValues)){
			for (String innerValue : finalImprintValues) {
		    	 imprMethod = new ImprintMethod();
				 imprMethod.setAlias(imprintMethod);//imprMethod.setAlias(innerValue);
				 imprMethod.setType(innerValue);
				 imprintMethodList.add(imprMethod);  
		}
		}else{
			imprMethod = new ImprintMethod();
			 imprMethod.setAlias(imprintMethod);
			 imprMethod.setType("OTHER");
			 imprintMethodList.add(imprMethod);
		
		}
		}
		}catch(Exception e){
			
		}
		return imprintMethodList;
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
