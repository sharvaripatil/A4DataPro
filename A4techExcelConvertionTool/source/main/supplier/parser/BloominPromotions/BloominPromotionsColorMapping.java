package parser.BloominPromotions;

import java.util.HashMap;
import java.util.Map;

public class BloominPromotionsColorMapping {
public static Map<String, String> COLOR_MAP =new HashMap<String, String>();
	
	static{
		COLOR_MAP.put("Multi color","Multi Color");
		COLOR_MAP.put("White","Medium White");
		COLOR_MAP.put("Blue","Medium Blue");
		COLOR_MAP.put("Glow In The Dark","Light Green");
		COLOR_MAP.put("Rainbow","Multi Color");
		COLOR_MAP.put("Silver","Silver Metal");
		COLOR_MAP.put("Red","Medium Red");
		COLOR_MAP.put("Black","Medium Black");
		COLOR_MAP.put("Yellow","Medium Yellow");
		COLOR_MAP.put("Gold","Dark Yellow");
		COLOR_MAP.put("Camoflage","Multi Color");
		COLOR_MAP.put("Green","Medium Green");
		COLOR_MAP.put("Gray","Medium Gray");
		COLOR_MAP.put("Camouflage","Multi Color");
		COLOR_MAP.put("Natural","Other");
		COLOR_MAP.put("Translucent Blue","Clear Blue");
		COLOR_MAP.put("Translucent Red","Clear Red");
		COLOR_MAP.put("Translucent Clear","Clear");
		COLOR_MAP.put("Translucent Green","Light Green");
		COLOR_MAP.put("Orange","Medium Orange");
		COLOR_MAP.put("Purple","Medium Purple");
		COLOR_MAP.put("Pink","Medium Pink");
		COLOR_MAP.put("Brown","Medium Brown");
		COLOR_MAP.put("Golden","Dark Yellow");
		COLOR_MAP.put("Royal Blue","Bright Blue");

	}
	public static String getColorGroup(String colorName){
		String colorGroup = COLOR_MAP.get(colorName);
		return colorGroup == null?"Other":colorGroup;
	}
}
