package com.a4tech.product.criteria.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.Shape;
import com.a4tech.util.ApplicationConstants;

public class ProductShapeParser {

	private Logger              _LOGGER              = Logger.getLogger(getClass());
	
	public List<Shape> getShapeCriteria(String shape){
		List<Shape> shapeList=new ArrayList<Shape>();
		try{
		Shape shapeObj;
		String shapeArr[]=shape.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		
		for (String string : shapeArr) {
			shapeObj=new Shape();
			
			shapeObj.setName(string);
			
			shapeList.add(shapeObj);
		}
		}catch(Exception e){
			_LOGGER.error("Error while processing Shape :"+e.getMessage());          
		   	return null;
		   	
		   }
		return shapeList;
		
	}
}
