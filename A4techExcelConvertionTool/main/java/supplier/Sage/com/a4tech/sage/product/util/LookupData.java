package com.a4tech.sage.product.util;

import java.util.HashMap;
import java.util.Map;

public  class LookupData {
	
	public static  Map<Integer,String> Dimension1Units =new HashMap<Integer, String>();
	public static  Map<Integer,String> Dimension1Type =new HashMap<Integer, String>();

	static{
		Dimension1Units.put(1,"in");
		Dimension1Units.put(2,"ft");
		Dimension1Units.put(3,"yds");
		Dimension1Units.put(4,"mm");
		Dimension1Units.put(5,"cm");
		Dimension1Units.put(6,"meter");
		Dimension1Units.put(7,"mil");
	
		
		Dimension1Type.put(1, "Length");
		Dimension1Type.put(2, "Width");
		Dimension1Type.put(3, "Height");
		Dimension1Type.put(4, "Depth");
		Dimension1Type.put(5, "Dia");
		Dimension1Type.put(6, "Thickness");

		
	}
	
	public static String getDimensionUnits(int dimUnitid){
		return Dimension1Units.get(dimUnitid);
	}
	
	public static String getDimensionType(int dimTypeid){
		return Dimension1Type.get(dimTypeid);
	}

}

