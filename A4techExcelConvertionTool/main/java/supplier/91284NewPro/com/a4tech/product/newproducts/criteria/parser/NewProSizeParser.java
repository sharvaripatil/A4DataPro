package com.a4tech.product.newproducts.criteria.parser;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.newproducts.mapping.NewProductsExcelMapping;
public class NewProSizeParser {

	private static final Logger _LOGGER = Logger.getLogger(NewProSizeParser.class);
	
	public static Product getSizes(String sizeValue,Product excelProductObj,ProductConfigurations productConfigObj){
		int index=validSize(sizeValue);
		if(index==-1){
		
			if(sizeValue.contains("(")&&sizeValue.contains(")")){
				
				String descrtn = sizeValue.substring(sizeValue.indexOf("(")+1,sizeValue.indexOf(")"));
				sizeValue=sizeValue.replace("("+descrtn+")", "");
				String tempDesctn=excelProductObj.getDescription();
				tempDesctn=tempDesctn+descrtn;
				excelProductObj.setDescription(tempDesctn);
				
				
			}
			Size sizeObj = new Size();
			String sizeGroup="dimension";
			String unitValue="in";
			String attriValueLen="Length";
			//1.75 DIA x 4.5
			try{
				String DimenArr[] = {sizeValue} ;
				sizeValue=sizeValue.replace("-"," ");
				sizeValue=sizeValue.replace("\"","");
				 if(sizeValue.contains("x")){
					 DimenArr=sizeValue.split("x");
					 sizeGroup="dimension";
				 }
				
				if (sizeGroup.equals("dimension")) {
				Dimension dimensionObj = new Dimension();
				List<Values> valuesList = new ArrayList<Values>();
				List<Value> valuelist;
				Values valuesObj = null;
				Value valObj;
			
					valuesObj = new Values();
					valuelist = new ArrayList<Value>(); 
					int count=1;
					for (String value1 : DimenArr) {
						valObj = new Value();
						if(value1.contains("dia")||value1.contains("DIA")||value1.contains("Dia")){
							attriValueLen="DIA";
							value1=value1.replaceAll("(dia|DIA|Dia)", "");
						}
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
					
					valuesList.add(valuesObj);
				//}

				dimensionObj.setValues(valuesList);
				sizeObj.setDimension(dimensionObj);
			}
				productConfigObj.setSizes(sizeObj);
				excelProductObj.setProductConfigurations(productConfigObj);
			}
			
			catch(Exception e)
			{
				
				_LOGGER.error("Error while processing Size :"+e.getMessage());
				return null;
			}
		}else{
			String tempDesctn=excelProductObj.getDescription();
			tempDesctn=tempDesctn+sizeValue;
			excelProductObj.setDescription(tempDesctn);
		}
		return excelProductObj;
	}
	
	public static int validSize(String tempVal){
		return StringUtils.indexOfAny(tempVal, new String[]{"Bowl","Coaster","up","ea","Holder","Tools","handle","Expands","closed","Hands","exp","Expands","Floor","@","Seat","diameter","to","Board","Only","each","coaster",
				"Servers","Chair","Workstation","Sm","Desk","Nappies","Round","Tool","Square","Individual","adj","Drawers","Retracts","Inner","Copartment","Salad","Open"});
		
	}
}
