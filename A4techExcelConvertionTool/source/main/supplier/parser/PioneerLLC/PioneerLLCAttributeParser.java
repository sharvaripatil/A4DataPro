package parser.PioneerLLC;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.ImprintMethod;

public class PioneerLLCAttributeParser {

	private static final Logger _LOGGER = Logger.getLogger(PioneerLLCAttributeParser.class);
	private static LookupServiceData lookupServiceDataObj;
	
	public List<ImprintMethod> getImprintMethods(String imprintValue){
		List<ImprintMethod> listOfImprintMethodsNew = new ArrayList<ImprintMethod>();
		//for (String value : listOfImprintMethods) {
			ImprintMethod imprintMethodObj =new ImprintMethod();
			if(lookupServiceDataObj.isImprintMethod(imprintValue.toUpperCase())){
				imprintMethodObj.setAlias(imprintValue);
				imprintMethodObj.setType(imprintValue);
			}else{
				imprintMethodObj.setAlias(imprintValue);
				imprintMethodObj.setType("OTHER");
			}
			listOfImprintMethodsNew.add(imprintMethodObj);
		//}
		
		
		return listOfImprintMethodsNew;
		
	}

	public static LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}

	public static void setLookupServiceDataObj(
			LookupServiceData lookupServiceDataObj) {
		PioneerLLCAttributeParser.lookupServiceDataObj = lookupServiceDataObj;
	}
	
	
	
	
	
}
