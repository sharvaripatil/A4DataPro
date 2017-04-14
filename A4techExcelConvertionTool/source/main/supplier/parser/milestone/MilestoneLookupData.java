package parser.milestone;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public  class MilestoneLookupData {
	
	public static  Map<String,String> Dimension1Units =new HashMap<String, String>();
	public static  Map<String,String> Dimension1Type =new HashMap<String, String>();
	public static Map<String, String> COLOR_MAP =new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

	static{
		Dimension1Units.put("1","in");
		Dimension1Units.put("2","ft");
		Dimension1Units.put("3","yds");
		Dimension1Units.put("4","mm");
		Dimension1Units.put("5","cm");
		Dimension1Units.put("6","meter");
		Dimension1Units.put("7","mil");
	
		
		Dimension1Type.put("1", "Length");
		Dimension1Type.put("2", "Width");
		Dimension1Type.put("3", "Height");
		Dimension1Type.put("4", "Depth");
		Dimension1Type.put("5", "Dia");
		Dimension1Type.put("6", "Thickness");
		

		COLOR_MAP.put("Clear","Clear");
		COLOR_MAP.put("Jade Green","Medium Green");
		COLOR_MAP.put("Black","Medium Black");
		COLOR_MAP.put("Blue","Medium Blue");
		COLOR_MAP.put("Gold","Gold Metal");
		COLOR_MAP.put("Red","Medium Red");
		COLOR_MAP.put("Frosted White","Medium White");
		COLOR_MAP.put("Silver","Silver Metal");
		COLOR_MAP.put("Rosewood Brown","Medium Brown");
		COLOR_MAP.put("Rosewood Brown Finish","Medium Brown");
		COLOR_MAP.put("Gray","Medium Gray");
		COLOR_MAP.put("Champagne","Light Yellow");
		COLOR_MAP.put("Gunmetal","Medium Gray");
		COLOR_MAP.put("Green","Medium Green");
		COLOR_MAP.put("Matte Silver","Silver Metal");
		COLOR_MAP.put("Pewter","Medium Gray");
		COLOR_MAP.put("Aztec Gold","Gold Metal");
		COLOR_MAP.put("Cherrywood Brown","Medium Brown");
		COLOR_MAP.put("Black Marble","Medium Black");
		COLOR_MAP.put("Cherrywood, Brown","Medium Brown");
		COLOR_MAP.put("Brown","Medium Brown");
		COLOR_MAP.put("Pink","Medium Pink");
		COLOR_MAP.put("Bronze","Bronze Metal");
		COLOR_MAP.put("White","Medium White");
		COLOR_MAP.put("Orange","Medium Orange");
		COLOR_MAP.put("Copper","Copper Metal");
		COLOR_MAP.put("Dark Pewter","Dark Gray");
		COLOR_MAP.put("Petwer","Medium Gray");
		COLOR_MAP.put("Walnut Brown","Medium Brown");
		COLOR_MAP.put("Black Ash","Medium Black");
		COLOR_MAP.put("Bamboo Brown","Light Brown");
		COLOR_MAP.put("Assorted","Assorted");
		COLOR_MAP.put("Maple Brown","Medium Brown");
		COLOR_MAP.put("Purple","Medium Purple");
		COLOR_MAP.put("Black Back","Medium Black");
		COLOR_MAP.put("Silver Accents","Silver Metal");
		COLOR_MAP.put("Smoked Black","Medium Black");
		COLOR_MAP.put("Brown Wood","Medium Brown");
		COLOR_MAP.put("Yellow","Medium Yellow");
		COLOR_MAP.put("Wood Brown","Medium Brown");
		COLOR_MAP.put("Brass Gold","Gold Metal");
		COLOR_MAP.put("Brass","Brass Metal");
		COLOR_MAP.put("Siiver","Silver Metal");
		COLOR_MAP.put("Rosewood, Brown","Medium Brown");
		COLOR_MAP.put("Brown Rosewood","Medium Brown");
		COLOR_MAP.put("Turquoise Blue","Light Blue");


		
	}
	
	public static String getDimensionUnits(int dimUnitid){
		return Dimension1Units.get(dimUnitid);
	}
	
	public static String getDimensionType(int dimTypeid){
		return Dimension1Type.get(dimTypeid);
	}

	public static Map<String, String> getCOLOR_MAP() {
		return COLOR_MAP;
	}

	
	

}

