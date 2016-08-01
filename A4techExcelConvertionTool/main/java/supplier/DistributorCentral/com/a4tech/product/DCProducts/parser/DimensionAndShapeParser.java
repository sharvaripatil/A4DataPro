package com.a4tech.product.DCProducts.parser;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Shape;
import com.a4tech.util.LookupData;


public class DimensionAndShapeParser {
	
	public List<Shape> getShapes(String value){
		List<Shape> listOfShapes  = new ArrayList<Shape>();
		if(LookupData.isShape(value)){
			Shape shape = new Shape();
			shape.setName(value);
			listOfShapes.add(shape);
			return listOfShapes;
		}
		return null;
	}

}
