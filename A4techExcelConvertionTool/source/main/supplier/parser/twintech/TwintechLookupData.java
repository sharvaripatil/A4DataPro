package parser.twintech;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public  class TwintechLookupData {
	
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
		

		COLOR_MAP.put("Red 199 C","Medium Red");	
		COLOR_MAP.put("Orange 021 C","Medium Orange");	
		COLOR_MAP.put("Orange 158 C","Medium Orange");	
		COLOR_MAP.put("Lime Green 375 C","Medium Green");	
		COLOR_MAP.put("Green 362 C","Medium Green");	
		COLOR_MAP.put("Light Blue 2995 C","Light Blue");	
		COLOR_MAP.put("Royal Blue 300 C","Bright Blue");	
		COLOR_MAP.put("Violet C","Medium Purple");	
		COLOR_MAP.put("Brown 478 C","Medium Brown");	
		COLOR_MAP.put("Pink 211 C","Medium Pink");	
		COLOR_MAP.put("Black","Medium Black");	
		COLOR_MAP.put("Cool Gray 9C","Medium Gray");	
		COLOR_MAP.put("White","Medium White");	
		COLOR_MAP.put("Yellow 123 C","Medium Yellow");	
		COLOR_MAP.put("Gray 9C","Medium Gray");	
		COLOR_MAP.put("Blue","Medium Blue");	
		COLOR_MAP.put("Dark Blue","Dark Blue");	
		COLOR_MAP.put("Clear Blue","Clear Blue");	
		COLOR_MAP.put("Light Blue","Light Blue");	
		COLOR_MAP.put("Red","Medium Red");	
		COLOR_MAP.put("Clear Red","Clear Red");	
		COLOR_MAP.put("Pink","Medium Pink");	
		COLOR_MAP.put("Orange","Medium Orange");	
		COLOR_MAP.put("Clear Orange","Clear Orange");	
		COLOR_MAP.put("Yellow","Medium Yellow");	
		COLOR_MAP.put("Green","Medium Green");	
		COLOR_MAP.put("Light Green","Light Green");	
		COLOR_MAP.put("Purple","Medium Purple");	
		COLOR_MAP.put("Light Blue","Light Blue");	
		COLOR_MAP.put("Dark Blue","Dark Blue");	
		COLOR_MAP.put("Lime Green","Medium Green");	
		COLOR_MAP.put("Dark Red","Dark Red");	
		COLOR_MAP.put("Red 703 C","Medium Red");	
		COLOR_MAP.put("Green 360 C","Medium Green");	
		COLOR_MAP.put("Dark Blue 7687 C","Dark Blue");	
		COLOR_MAP.put("Red 193 C","Medium Red");	
		COLOR_MAP.put("Orange 1665 C","Medium Orange");	
		COLOR_MAP.put("Yellow 1235 C","Medium Yellow");	
		COLOR_MAP.put("Lime Green 360 C","Medium Green");	
		COLOR_MAP.put("Teal 7465 C","Medium Green");	
		COLOR_MAP.put("Blue 280 C","Medium Blue");	
		COLOR_MAP.put("Purple 254 C","Medium Purple");	
		COLOR_MAP.put("Brown","Medium Brown");	
		COLOR_MAP.put("Light Brown","Light Brown");	
		COLOR_MAP.put("Light Green","Light Green");	
		COLOR_MAP.put("Dark Green","Dark Green");	
		COLOR_MAP.put("Silver","Silver Metal");	
		COLOR_MAP.put("Red 7417 C","Medium Red");	
		COLOR_MAP.put("Orange 804 C","Medium Orange");	
		COLOR_MAP.put("Lime Green 365 C","Medium Green");	
		COLOR_MAP.put("Light Blue 319 C","Light Blue");	
		COLOR_MAP.put("Royal Blue 801 C","Bright Blue");	
		COLOR_MAP.put("Pink 1895 C","Medium Pink");	
		COLOR_MAP.put("Gold","Dark Yellow");	
		COLOR_MAP.put("Baby Blue","Light Blue");	
		COLOR_MAP.put("Teal","Medium Green");	
		COLOR_MAP.put("Navy Blue","Dark Blue");	
		COLOR_MAP.put("Red 186 C","Medium Red");	
		COLOR_MAP.put("Green 375 C","Medium Green");	
		COLOR_MAP.put("Blue 2995 C","Medium Blue");	
		COLOR_MAP.put("Blue Reflex Blue C","Medium Blue");	
		COLOR_MAP.put("Purple Violet C","Medium Purple");	
		COLOR_MAP.put("Gloss Coated Gold","Dark Yellow");	
		COLOR_MAP.put("Gloss Coated Silver","Medium Gray");	
		COLOR_MAP.put("Rubber Coated Red","Medium Red");	
		COLOR_MAP.put("Rubber Coated Blue","Medium Blue");	
		COLOR_MAP.put("Rubber Coated Green","Medium Green");	
		COLOR_MAP.put("Rubber Coated Teal Green","Medium Green");	
		COLOR_MAP.put("Rubber Coated Orange","Medium Orange");	
		COLOR_MAP.put("Rubber Coated Yellow","Medium Yellow");	
		COLOR_MAP.put("Rubber Coated Pink","Medium Pink");	
		COLOR_MAP.put("Rubber Coated Purple","Medium Purple");	
		COLOR_MAP.put("Teal Green","Medium Green");	
		COLOR_MAP.put("Metallic","Metallic Gray");	
		COLOR_MAP.put("Dark Pink","Dark Pink");	
		COLOR_MAP.put("White/Translucent","Clear White");	
		COLOR_MAP.put("Dark Wood","Dark Brown");	
		COLOR_MAP.put("Light Wood","Light Brown");	
		COLOR_MAP.put("Assorted","Assorted");	
		COLOR_MAP.put("Gray","Medium Gray");	
		COLOR_MAP.put("Rose Gold","Gold Metal");	
		COLOR_MAP.put("Cardboard Brown","Medium Brown");	
		COLOR_MAP.put("Multi color","Multi Color");	
		COLOR_MAP.put("Ivory Beige","IRIDESCENT BROWN");	
		COLOR_MAP.put("Wood","Light Brown");	
		COLOR_MAP.put("Metallic Gray","Metallic Gray");	
		COLOR_MAP.put("Light Red","Light Red");	


		
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

