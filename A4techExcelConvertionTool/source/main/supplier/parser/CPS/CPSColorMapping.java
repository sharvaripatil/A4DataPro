package parser.CPS;

import java.util.HashMap;
import java.util.Map;

public class CPSColorMapping {
public static Map<String, String> COLOR_MAP =new HashMap<String, String>();
	
	static{
		
		COLOR_MAP.put("Royal Blue","Bright Blue");
		COLOR_MAP.put("Red","Medium Red");
		COLOR_MAP.put("Kelly Green","Medium Green");
		COLOR_MAP.put("Black","Medium Black");
		COLOR_MAP.put("Multi color","Multi Color");
		COLOR_MAP.put("Translucent Clear","Clear");
		COLOR_MAP.put("Translucent Smoke","Clear Gray");
		COLOR_MAP.put("Translucent Red","Clear Red");
		COLOR_MAP.put("Translucent Blue","Clear Blue");
		COLOR_MAP.put("Translucent Green","Clear Green");
		COLOR_MAP.put("Translucent Frost","Clear White");
		COLOR_MAP.put("Translucent Purple","Clear Purple");
		COLOR_MAP.put("Translucent Aqua","Clear Green");
		COLOR_MAP.put("Assorted","Assorted");
		COLOR_MAP.put("White","Medium White");
		COLOR_MAP.put("Yellow","Medium Yellow");
		COLOR_MAP.put("Pink","Medium Pink");
		COLOR_MAP.put("Purple","Medium Purple");
		COLOR_MAP.put("Orange","Medium Orange");
		COLOR_MAP.put("Frost","Medium White");
		COLOR_MAP.put("Clear","Clear");
		COLOR_MAP.put("Natural","Light White");
		COLOR_MAP.put("Navy","Dark Blue");
		COLOR_MAP.put("Forest Green","Dark Green");
		COLOR_MAP.put("Granite","Medium Gray");
		COLOR_MAP.put("Pearl White","Medium White");
		COLOR_MAP.put("Green","Medium Green");
		COLOR_MAP.put("Neon Orange","Bright Orange");
		COLOR_MAP.put("Neon Pink","Bright Pink");
		COLOR_MAP.put("Neon Green","Bright Green");
		COLOR_MAP.put("Translucent Edge-Glow Green","Clear Green");
		COLOR_MAP.put("Silver","Medium Gray");
		COLOR_MAP.put("Stainless Steel","Silver Metal");
		COLOR_MAP.put("Brushed Stainless Steel","Silver Metal");
		COLOR_MAP.put("Brushed Steel","Silver Metal");
		COLOR_MAP.put("Blue","Medium Blue");
		COLOR_MAP.put("Metallic Blue","Metallic Blue");
		COLOR_MAP.put("Metallic Red","Metallic Red");
		COLOR_MAP.put("Translucent Pink","Clear Pink");
		COLOR_MAP.put("Lime Green","Bright Green");
		COLOR_MAP.put("Navy Blue","Dark Blue");
		COLOR_MAP.put("Pearl Green","Medium Green");
		COLOR_MAP.put("Glow-in-the-Dark","Other");
		COLOR_MAP.put("Chrome","Chrome Metal");
		COLOR_MAP.put("Dark Green","Dark Green");
		COLOR_MAP.put("Camouflage","Multi Color");
		COLOR_MAP.put("Pearl Blue","Medium Blue");
		COLOR_MAP.put("Ivory","Medium White");
		COLOR_MAP.put("Light Blue","Light Blue");
		COLOR_MAP.put("Midnight Blue","Dark Blue");
		COLOR_MAP.put("Royal","Bright Blue");
		COLOR_MAP.put("Aqua","Medium Green");
       
	}
	public static String getColorGroup(String colorName){
		//return COLOR_MAP.get(colorName.toUpperCase());// this is used to Pro golf Supplier
		return COLOR_MAP.get(colorName);// this is used to Ball Pro supplier
	}
}
