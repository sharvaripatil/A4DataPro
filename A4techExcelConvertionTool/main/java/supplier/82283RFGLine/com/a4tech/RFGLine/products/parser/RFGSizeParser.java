package com.a4tech.RFGLine.products.parser;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.util.ApplicationConstants;


public class RFGSizeParser {
 
	
	public Size getSizes(String descriptionSize)
	{
  Size sizeObj=new Size();
  Dimension dimensionObj= new Dimension();
  

  String descrSizeArr[]=descriptionSize.split("x|X");

	List<Values> valuesList = new ArrayList<Values>();
	List<Value> valuelist =  new ArrayList<Value>();
	Values valuesObj = new Values();
	Value valObj=null;
	if(descrSizeArr.length==3)
	{
	if(!StringUtils.isEmpty(descrSizeArr[0]))
	{
		valObj=new Value();
		descrSizeArr[0]=descrSizeArr[0].replaceAll("'", ApplicationConstants.CONST_STRING_EMPTY);
		if(descrSizeArr[0].contains("W")){
		descrSizeArr[0]=descrSizeArr[0].replace("W",ApplicationConstants.CONST_STRING_EMPTY);
		valObj.setAttribute("Width");
		}else if(descrSizeArr[0].contains("L")){
		descrSizeArr[0]=descrSizeArr[0].replace("L",ApplicationConstants.CONST_STRING_EMPTY);
		valObj.setAttribute("Length");
		}else if(descrSizeArr[0].contains("H")){
        descrSizeArr[0]=descrSizeArr[0].replace("H",ApplicationConstants.CONST_STRING_EMPTY);
		valObj.setAttribute("Height");
		}else
		{
		valObj.setAttribute("Length");
		}
	    valObj.setValue(descrSizeArr[0].trim());
		valObj.setUnit(ApplicationConstants.CONST_STRING_INCHES);
		valuelist.add(valObj);
	}
	 if(!StringUtils.isEmpty(descrSizeArr[1]))
	{
	   valObj=new Value();
		if(descrSizeArr[1].contains("H")){
		descrSizeArr[1]=descrSizeArr[1].replace("H",ApplicationConstants.CONST_STRING_EMPTY);
		valObj.setAttribute("Height");}
		
		else if(descrSizeArr[1].contains("W")){
		descrSizeArr[1]=descrSizeArr[1].replace("W",ApplicationConstants.CONST_STRING_EMPTY);
		valObj.setAttribute("Width");}

		else if(descrSizeArr[1].contains("D")){
		descrSizeArr[1]=descrSizeArr[1].replace("D",ApplicationConstants.CONST_STRING_EMPTY);
		valObj.setAttribute("Depth");}
		
		else{
		valObj.setAttribute("Width");
		}
		valObj.setValue(descrSizeArr[1].trim());
		valObj.setUnit(ApplicationConstants.CONST_STRING_INCHES);
		valuelist.add(valObj);
	}
	 if(!StringUtils.isEmpty(descrSizeArr[2]))
	{
		valObj=new Value();
		if(descrSizeArr[2].contains("D")){
		descrSizeArr[2]=descrSizeArr[2].replace("D",ApplicationConstants.CONST_STRING_EMPTY);
		valObj.setAttribute("Depth");}

		
		else if(descrSizeArr[2].contains("Gusset")){
		descrSizeArr[2]=descrSizeArr[2].replace("Gusset",ApplicationConstants.CONST_STRING_EMPTY);
		valObj.setAttribute("Depth");
		}
		else if(descrSizeArr[2].contains("G")){
		descrSizeArr[2]=descrSizeArr[2].replace("G",ApplicationConstants.CONST_STRING_EMPTY);
		valObj.setAttribute("Depth");
		}
		else if(descrSizeArr[2].contains("W")){
		descrSizeArr[2]=descrSizeArr[2].replace("W",ApplicationConstants.CONST_STRING_EMPTY);
		valObj.setAttribute("Width");
		}
		else if(descrSizeArr[2].contains("H")){
		descrSizeArr[2]=descrSizeArr[2].replace("H",ApplicationConstants.CONST_STRING_EMPTY);
		valObj.setAttribute("Height");}

		else if(descrSizeArr[2].contains("Length")){
		descrSizeArr[2]=descrSizeArr[2].replace("Length",ApplicationConstants.CONST_STRING_EMPTY);
		valObj.setAttribute("Length");}
		else{
		valObj.setAttribute("Height");}

		valObj.setValue(descrSizeArr[2].trim());
		valObj.setUnit(ApplicationConstants.CONST_STRING_INCHES);
		valuelist.add(valObj);

	}
	}
	
	else if(descrSizeArr.length==2){
		if(!StringUtils.isEmpty(descrSizeArr[0]))
		{
			 valObj=new Value();
			if(descrSizeArr[0].contains("W")){
			descrSizeArr[0]=descrSizeArr[0].replace("W",ApplicationConstants.CONST_STRING_EMPTY);
			valObj.setAttribute("Width");}
			
			else if(descrSizeArr[0].contains("L")){
			descrSizeArr[0]=descrSizeArr[0].replace("L",ApplicationConstants.CONST_STRING_EMPTY);
			valObj.setAttribute("Length");}

			else if(descrSizeArr[0].contains("H")){
			descrSizeArr[0]=descrSizeArr[0].replace("H",ApplicationConstants.CONST_STRING_EMPTY);
			valObj.setAttribute("Height");}

			else{
				valObj.setAttribute("Length");}
			
			valObj.setValue(descrSizeArr[0].trim());
			valObj.setUnit(ApplicationConstants.CONST_STRING_INCHES);
			valuelist.add(valObj);
		}
		 if(!StringUtils.isEmpty(descrSizeArr[1]))
		{
		   valObj=new Value();
		   if(descrSizeArr[1].contains("H")){
			descrSizeArr[1]=descrSizeArr[1].replace("H",ApplicationConstants.CONST_STRING_EMPTY);
			valObj.setAttribute("Height");}
		    else if(descrSizeArr[1].contains("W")){
			descrSizeArr[1]=descrSizeArr[1].replace("W",ApplicationConstants.CONST_STRING_EMPTY);
			valObj.setAttribute("Width");
		    }
		    else if(descrSizeArr[1].contains("w")){
			descrSizeArr[1]=descrSizeArr[1].replace("w",ApplicationConstants.CONST_STRING_EMPTY);
			valObj.setAttribute("Width");
			}
			else{
				valObj.setAttribute("Width");}
			valObj.setValue(descrSizeArr[1].trim());
			valObj.setUnit(ApplicationConstants.CONST_STRING_INCHES);
			valuelist.add(valObj);
		}
	}	
	

	valuesObj.setValue(valuelist);
	valuesList.add(valuesObj);
	dimensionObj.setValues(valuesList);
	sizeObj.setDimension(dimensionObj);

		
		return sizeObj;
	}
}

