package parser.tomaxusa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.util.CommonUtility;

public class TomaxSizeParser {

	private static final Logger _LOGGER = Logger.getLogger(TomaxSizeParser.class);
	
	private static HashMap<String, String> sizeDimMap=new HashMap<String, String>();
	private static HashMap<String, String> sizeUnits=new HashMap<String, String>();
	//private static ArrayList<String> dimList=new ArrayList<String>();
	//private static ArrayList<String> unitList=new ArrayList<String>();
	
	private static LinkedHashSet<String> dimList=new LinkedHashSet<String>();
	private static LinkedHashSet<String> unitList=new LinkedHashSet<String>();
	
	static{
		sizeDimMap.put("Arc","Arc");
		sizeDimMap.put("Area", "Area");
		sizeDimMap.put("Circumference", "Circumference");
		sizeDimMap.put("D", "Depth");
		sizeDimMap.put("DIA", "Dia");
		sizeDimMap.put("H", "Height");
		sizeDimMap.put("LONG", "Length");
		sizeDimMap.put("T", "Thickness");
		sizeDimMap.put("W", "Width");
		sizeDimMap.put("L", "Length");
		sizeDimMap.put("LENGTH", "Length");
		
		sizeUnits.put("CM", "cm");
		sizeUnits.put("FEET", "ft");
		sizeUnits.put("FEE", "ft");
		sizeUnits.put("FT", "ft");
		sizeUnits.put("INC", "in");
		sizeUnits.put("MTR", "meter");
		sizeUnits.put("ML", "mil");
		sizeUnits.put("MM", "mm");
		sizeUnits.put("ST", "sq ft.");
		sizeUnits.put("SI", "sq in.");
		sizeUnits.put("YD", "yds");
		
		dimList.add("Circumference");
		dimList.add("Arc");
		dimList.add("DIA");
		dimList.add("Area");
		dimList.add("LENGTH");
		dimList.add("LONG");
		dimList.add("D");
		dimList.add("H");
		dimList.add("T");
		dimList.add("W");
		dimList.add("L");
		
		unitList.add("CM");
		unitList.add("FEET");
		unitList.add("FEE");
		unitList.add("INC");
		unitList.add("MTR");
		unitList.add("ML");
		unitList.add("MM");
		unitList.add("ST");
		unitList.add("SI");
		unitList.add("YD");
		unitList.add("FT");
		//dimList.add("");
		
	}
	public ProductConfigurations getSizes(String sizeValue,ProductConfigurations existingConfig) {
		Size sizeObj = new Size();
		String sizeGroup="dimension";
		String unitValue="in";
		String attriValue="Length";
		//1.75 DIA x 4.5
		boolean flag=false;
		try{
			//String DimenArr[] = {sizeValue} ;
			if(sizeValue.toUpperCase().contains("IMPRINT")){
				try{
				List<ImprintSize> listOfImprintSize = new ArrayList<>();
				List<ImprintLocation> listOfImprintLoc=new ArrayList<ImprintLocation>();
				ImprintSize imprSizeObj = new ImprintSize();
				ImprintLocation imprintLocationObj = new ImprintLocation();
				sizeValue=sizeValue.toUpperCase();
				sizeValue=sizeValue.replaceAll("”","\"");
				sizeValue=sizeValue.replaceAll(";",",");
				sizeValue=sizeValue.replaceAll("Φ","");
				//sizeValue=sizeValue.replaceAll(":","");
				//Φ
				String tempArr[]=sizeValue.split(",");
				for (String imprintValue : tempArr) {
					if(imprintValue.contains(":")){
						String tempValue[]=imprintValue.split(":");
						if(tempValue.length==2){
						String imprintLocation=tempValue[0];
						String imrpintSizeValue=tempValue[1];
						imprintLocation=imprintLocation.toUpperCase();
						imrpintSizeValue=imrpintSizeValue.toUpperCase();
						imprintLocation=imprintLocation.replace("IMPRINT", "");
						if(imprintLocation.contains("(") && imprintLocation.contains(")")){
						imprintLocation=CommonUtility.extractValueSpecialCharacter("(", ")", imprintLocation);
						}
						imprSizeObj=getProductImprintSize(imrpintSizeValue);
						listOfImprintSize.add(imprSizeObj);
						imprintLocationObj=getImprintLocationVal(imprintLocation);
						listOfImprintLoc.add(imprintLocationObj);
						}else{
							imprSizeObj=getProductImprintSize(imprintValue);
							listOfImprintSize.add(imprSizeObj);
						}
					}else{
						imprSizeObj=getProductImprintSize(imprintValue);
						listOfImprintSize.add(imprSizeObj);
					}	
				}
				existingConfig.setImprintLocation(listOfImprintLoc);
				existingConfig.setImprintSize(listOfImprintSize);
				}catch(Exception e){
					_LOGGER.error("Error while processng imprint values");
				}
			}
			else{
				Dimension dimensionObj = new Dimension();
				List<Values> valuesList = new ArrayList<Values>();
				List<Value> valuelist = null;
				Values valuesObj = new Values();
					Value valObj;
					sizeValue=sizeValue.replaceAll("”","\"");
					sizeValue=sizeValue.replaceAll("\"","INC");
					sizeValue=sizeValue.replaceAll(";",",");
					sizeValue=sizeValue.replaceAll(":","");
					sizeValue = sizeValue.replace(".", "");
					sizeValue=sizeValue.replaceAll("Φ","");
					String valuesArr[]=sizeValue.split(",");
					
					for (String tempValue : valuesArr) {
						valuesObj = new Values();
						tempValue=tempValue.toUpperCase();
						if(tempValue.contains("FT LONG") || tempValue.contains("FEET LONG")){
							
							tempValue=tempValue.substring(0, (tempValue.indexOf("F")));
							tempValue=tempValue.trim();
							
							tempValue=tempValue+" FEE";
							//tempValue=tempValue.replace("FEET IN LENGTH", "FEE");
						}
					if(tempValue.contains("FEET IN")){
						tempValue=tempValue.replace("FEET IN LENGTH", "FEE");
					}
					if(tempValue.contains("IN LENGTH")){
						tempValue=tempValue.replace("IN LENGTH", "L");
					}
					if(tempValue.contains("FT")){
						tempValue=tempValue.replace("FT", "FEE");
					}
					tempValue=removeSpecialChar(tempValue,1);
					String DimenArr[]=tempValue.split("X");
				valuelist = new ArrayList<Value>(); 
				int tempCount=0;
				for (String value : DimenArr) {
					value=value.toUpperCase();
					valObj = new Value();
					String tempAttri="";
					if(tempCount==1){
						tempAttri="W";
						tempCount++;
					}else if(tempCount==2){
						tempAttri="H";
					}else{
						 tempAttri=getAttriTypeValue(value);
					}
					
					if(tempAttri.contains("-DEFAULT")){
						tempAttri=tempAttri.replace("-DEFAULT", "");
						tempCount++;
					}
					attriValue=getAttriValue(tempAttri);
					value=removeSpecialChar(value, 2);
					String tempUnitValue=getUNitTypeValue(value);
					unitValue=getUnitValue(tempUnitValue);
					value=removeSpecialChar(value, 3);
					valObj.setAttribute(attriValue);
					valObj.setValue(value.trim());
					valObj.setUnit(unitValue);
					valuelist.add(valObj);
				}	
				valuesObj.setValue(valuelist);
				valuesList.add(valuesObj);
				
			}
				
				///////////////
			
			dimensionObj.setValues(valuesList);
			sizeObj.setDimension(dimensionObj);
		//}
		}
			existingConfig.setSizes(sizeObj);
		}
		catch(Exception e)
		{
			_LOGGER.error("Error while processing Size :"+e.getMessage());
			existingConfig.setSizes(sizeObj);
			return existingConfig;
		}
		return existingConfig;
	}
	
	public static String removeSpecialChar(String tempValue,int number){
		if(number==1){
		tempValue=tempValue.replaceAll("(CABLE|LENGTH|CLEAR|CASE|ONE|SIDE|EARPHONE|INCLUDE|HOOK|WHITE|BOX|DELUXE|BULK|MEASURE|WITH|CARABINER|ITEM|SINGLE|EAR|INCLUDE)", "");
		tempValue=tempValue.replaceAll("\\(","");
		tempValue=tempValue.replaceAll("\\)","");
		}
		
		if(number==2){
			tempValue=tempValue.replaceAll("(DIA|LONG|LENGTH|Arc|Area|Circumference|L|D|H|T|W)", "");
			tempValue=tempValue.replaceAll("\\(","");
			tempValue=tempValue.replaceAll("\\)","");
			}
		
		if(number==3){
			tempValue=tempValue.replaceAll("(CM|FEET|FEE|FT|INC|MTR|ML|MM|ST|SI|YD)", "");
			tempValue=tempValue.replaceAll("\\(","");
			tempValue=tempValue.replaceAll("\\)","");
			}

	return tempValue;

	}
	
	
	public static String getAttriTypeValue(String sizeDim){
		    if(sizeDim != null){
		    	 for (String dim : dimList) {
					    if(sizeDim.contains(dim)){
					    	return dim;
					    }
				}
		    }
		return "LENGTH-DEFAULT";
	}
	
	public static String getAttriValue(String attriVal){
		
	    if(sizeDimMap.containsKey(attriVal)){
	    return	sizeDimMap.get(attriVal);
	    }
	return "Length";
	}
	
	
	public static String getUNitTypeValue(String sizeUnit){
	    if(sizeUnit != null){
	    	 for (String dim : unitList) {
				    if(sizeUnit.contains(dim)){
				    	return dim;
				    }
			}
	    }
	return "INC";
	}

	public static String getUnitValue(String UnitVal){
		if(sizeUnits.containsKey(UnitVal)){
			return	sizeUnits.get(UnitVal);
		}
		return "INC";
	}
	
	public Size getSizes(String sizeValue,Size existingSizeObj,Dimension exisDimensionObj,List<Values> exisValuesList) {
		//Size sizeObj = new Size();
		String sizeGroup="dimension";
		String unitValue="in";
		String attriValueLen="Length";
		//1.75 DIA x 4.5
		try{
			sizeValue=sizeValue.toUpperCase();
			/*if(tempValue.contains("FEET IN")){
				tempValue=tempValue.replace("FEET IN LENGTH", "FEE");
			}*/
			sizeValue=removeSpecialChar(sizeValue,1);
			sizeValue=sizeValue.replaceAll("”","");
			//sizeValue=sizeValue.replaceAll("”","\"");
			//sizeValue=sizeValue.replaceAll("\"","INC");
			sizeValue=sizeValue.replaceAll("\"","");
			sizeValue=sizeValue.replaceAll(";",",");
			sizeValue=sizeValue.replaceAll(":","");
			//sizeValue=sizeValue.replaceAll(".","");
			String valuesArr[]=sizeValue.split(",");
			
			for (String tempValue : valuesArr) {
			/////////////////
			String DimenArr[] = {tempValue} ;
			 if(tempValue.contains("X")){
				 DimenArr=tempValue.split("X");
				 sizeGroup="dimension";
			 }
			
			if (sizeGroup.equals("dimension")) {
			//Dimension dimensionObj = new Dimension();
			//List<Values> valuesList = new ArrayList<Values>();
			List<Value> valuelist;
			Values valuesObj = null;
			Value valObj;
			
				valuesObj = new Values();
				valuelist = new ArrayList<Value>(); 
				int count=1;
				for (String value1 : DimenArr) {
					valObj = new Value();
					/*if(value1.contains("DIA")){
						attriValueLen="DIA";
						value1=value1.replace("DIA", "").trim();
					}
					if(value1.contains("shaker")){
						value1=value1.replace("(shaker)", "").trim();
					}*/
					if(count==1){
						valObj.setAttribute(attriValueLen);
					}else if(count==2){
						valObj.setAttribute("Width");
					} else if(count==3){
						valObj.setAttribute("Height");
					}
					valObj.setValue(value1.trim());
					valObj.setUnit(unitValue);
					valuelist.add(valObj);
					valuesObj.setValue(valuelist);
					count++;
				}
				
				exisValuesList.add(valuesObj);
			}
			exisDimensionObj.setValues(exisValuesList);
			existingSizeObj.setDimension(exisDimensionObj);
		}
		}
		catch(Exception e)
		{
			_LOGGER.error("Error while processing Size :"+e.getMessage());
			return null;
		}
		return existingSizeObj;
	}
	
	public static ImprintSize getProductImprintSize(String imprintSizeValue){
		ImprintSize imprSizeObj = new ImprintSize();
  		imprSizeObj.setValue(imprintSizeValue);
  		return imprSizeObj;
	}
  
  public static ImprintLocation  getImprintLocationVal(String  impLocValue){
	   ImprintLocation imprintLocation = new ImprintLocation();
  	   imprintLocation.setValue(impLocValue);
  	   return imprintLocation;
  		}
	}
