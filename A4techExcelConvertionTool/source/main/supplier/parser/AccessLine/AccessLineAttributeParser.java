package parser.AccessLine;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import parser.sunGraphix.SunGraphixAttributeParser;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;

public class AccessLineAttributeParser {
	private static final Logger _LOGGER = Logger.getLogger(AccessLineAttributeParser.class);
	private static LookupServiceData lookupServiceData;
	
	public static ShippingEstimate getShippingEstimates( String shippingValue,String sdimVAl,ShippingEstimate shippingEstObj,String str,String sdimType) {
	//ShippingEstimate shipingObj = new ShippingEstimate();
	if(str.equals("NOI")){
		List<NumberOfItems> listOfNumberOfItems = new ArrayList<NumberOfItems>();
		NumberOfItems itemObj = new NumberOfItems();
		itemObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_NUMBER_UNIT_CASE);
		itemObj.setValue(shippingValue.trim());
		listOfNumberOfItems.add(itemObj);
		shippingEstObj.setNumberOfItems(listOfNumberOfItems);
	}
	
	if (str.equals("WT")) {
		List<Weight> listOfWeight = new ArrayList<Weight>();
		Weight weightObj = new Weight();
		weightObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_WEIGHT);
		weightObj.setValue(shippingValue);
		listOfWeight.add(weightObj);
		shippingEstObj.setWeight(listOfWeight);
	}
	
	if (str.equals("SDIM")) {
		Dimensions dimensionObj=shippingEstObj.getDimensions();
		Dimensions tempObj=new Dimensions();
		if(dimensionObj== null || dimensionObj.equals(tempObj)){
			dimensionObj=new Dimensions();
		}
		String unit="in";
				//Dimensions dimensionObj = new Dimensions();
		if (sdimType.equals("L")){//if (sdimType.equals("L")){
				dimensionObj.setLength(sdimVAl.trim());
				dimensionObj.setLengthUnit(unit);
		}
		if (sdimType.equals("W")){
				dimensionObj.setWidth(sdimVAl.trim());
				dimensionObj.setWidthUnit(unit);
		}
		if (sdimType.equals("H")){
				dimensionObj.setHeight(sdimVAl.trim());
				dimensionObj.setHeightUnit(unit);
		}
		shippingEstObj.setDimensions(dimensionObj);
				}
	return shippingEstObj;
}
	
	public static ProductConfigurations getImprintMethod(String data,ProductConfigurations prodConfig){
		List<ImprintMethod> listOfImprintMethod = prodConfig.getImprintMethods();
		try{
		//List<ImprintMethod> imprintMethodsList = new ArrayList<ImprintMethod>();
			String imprintMethod="";
			String imprintLocation="";
			ImprintMethod	imprintMethodObj = null;
			List<ImprintLocation> values=new ArrayList<ImprintLocation>();
			String[] imprintMethodValues = data.split(ApplicationConstants.CONST_DELIMITER_COMMA);
			for (String imprintMethodName : imprintMethodValues) {
			if(imprintMethodName.contains("on") || imprintMethodName.contains("behind") || imprintMethodName.contains("below")){
				if(imprintMethodName.contains("on")){
					String str[]=imprintMethodName.split("on");
					imprintMethod=str[0];
					imprintLocation="on "+str[1];
					//imprintLocation=imprintLocation.substring(imprintLocation.indexOf("on"),imprintLocation.length());
				}else if(imprintMethodName.contains("behind")){
					String str[]=imprintMethodName.split("behind");
					imprintMethod=str[0];
					imprintLocation="behind "+str[1];
				}else if(imprintMethodName.contains("below")){
					String str[]=imprintMethodName.split("below");
					imprintMethod=str[0];
					imprintLocation="below "+str[1];
				}
				
				
				ImprintLocation locObj=new ImprintLocation();
				locObj.setValue(imprintLocation);
				values.add(locObj);
				
			}
			
		if(CollectionUtils.isEmpty(listOfImprintMethod)){
			//List<ImprintMethod> listOfImprintMethodTemp = productConfigObj.getImprintMethods();
			listOfImprintMethod=new ArrayList<ImprintMethod>();
		}
		
	
			if(StringUtils.isEmpty(imprintMethodName)){
				continue;
			}
			imprintMethodObj = new ImprintMethod();
			String alias = "";
			if(imprintMethodName.contains("Foil")){
				alias = imprintMethodName.trim().toUpperCase();
				//imprintMethodName=imprintMethodName.replace("Foil", "Foil Stamped");
				imprintMethodName = "Foil Stamped";
		    } else if(imprintMethodName.contains("Deboss")){
		    	alias = imprintMethodName.trim().toUpperCase();
    		    //imprintMethodName=imprintMethodName.replaceAll("Deboss", "Debossed");
    		    imprintMethodName = "Debossed";
	        } else if(imprintMethodName.contains("4C")){
	        	 alias = imprintMethodName.trim().toUpperCase();
	        	 imprintMethodName = "Full Color";
	        }
			imprintMethodName = imprintMethodName.trim().toUpperCase();
			  if(lookupServiceData.isImprintMethod(imprintMethodName)){
				  imprintMethodObj.setType(imprintMethodName);
				  if(StringUtils.isEmpty(alias)){
					  imprintMethodObj.setAlias(imprintMethodName);
				  } else{
					  imprintMethodObj.setAlias(alias);
				  }
				  
			  }else{
				  imprintMethodObj.setType(ApplicationConstants.CONST_VALUE_TYPE_OTHER);
				  imprintMethodObj.setAlias(imprintMethodName);
			  }
			  listOfImprintMethod.add(imprintMethodObj);
			  //productConfigObj.setImprintMethods(listOfImprintMethod);
		}
			prodConfig.setImprintLocation(values);
			prodConfig.setImprintMethods(listOfImprintMethod);
		}catch(Exception e){
			_LOGGER.error("Error while processing Size :"+e.getMessage());
			return prodConfig;
		}
		return prodConfig;
	}

	public LookupServiceData getLookupServiceData() {
		return lookupServiceData;
	}

	public void setLookupServiceData(LookupServiceData lookupServiceData) {
		this.lookupServiceData = lookupServiceData;
	}
	
	
}
