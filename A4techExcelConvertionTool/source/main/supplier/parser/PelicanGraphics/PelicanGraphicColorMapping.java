package parser.PelicanGraphics;

import java.util.HashMap;
import java.util.Map;

public class PelicanGraphicColorMapping {
public static Map<String, String> COLOR_MAP =new HashMap<String, String>();
	
	static{
		COLOR_MAP.put("Multi color","Multi Color");
		COLOR_MAP.put("Yellow","Medium Yellow");
		COLOR_MAP.put("Black","Medium Black");
		COLOR_MAP.put("Green","Medium Green");
		COLOR_MAP.put("Assorted","Assorted");
		COLOR_MAP.put("White","Medium White");
		COLOR_MAP.put("Silver","Silver Metal");
		COLOR_MAP.put("Red","Medium Red");
		COLOR_MAP.put("Blue","Medium Blue");
		COLOR_MAP.put("Gray","Medium Gray");
		COLOR_MAP.put("Teal Green","Light Green");
		COLOR_MAP.put("Orange","Medium Orange");
		COLOR_MAP.put("Aluminum","Silver Metal");
		COLOR_MAP.put("Burgundy Red","Medium Red");
		COLOR_MAP.put("Purple","Medium Purple");
		COLOR_MAP.put("Royal Blue","Medium Blue");
		COLOR_MAP.put("Navy Blue","Dark Blue");
		COLOR_MAP.put("Turquoise Blue","Light Blue");
		COLOR_MAP.put("Fuchsia","Medium Pink");
		COLOR_MAP.put("Neon Green","Medium Green");
		COLOR_MAP.put("Pink","Medium Pink");
		COLOR_MAP.put("Brown","Medium Brown");
		COLOR_MAP.put("Camouflage","Camouflage Green");
		COLOR_MAP.put("Hot Pink","Bright Pink");
		COLOR_MAP.put("Lime Green","Bright Green");
		COLOR_MAP.put("Maroon Red","Dark Red");
		COLOR_MAP.put("Blue-White","Multi Color");
		COLOR_MAP.put("Black-White","Multi Color");
		COLOR_MAP.put("Maroon","Dark Red");
		COLOR_MAP.put("Light Blue","Light Blue");
		COLOR_MAP.put("Light Green","Light Green");
		COLOR_MAP.put("Gold","Gold Metal");
		COLOR_MAP.put("Cambridge Blue","Medium Blue");
		COLOR_MAP.put("Charcoal","Medium Gray");
		COLOR_MAP.put("Multi color(Camo)","Multi Color");
		COLOR_MAP.put("Natural","DARK WHITE");
		COLOR_MAP.put("Shell Yellow","Medium Yellow");
		COLOR_MAP.put("BP Green","Medium Green");
		COLOR_MAP.put("Dark Blue","Dark Blue");
		COLOR_MAP.put("Chevron Blue","Medium Blue");
		COLOR_MAP.put("Camo Green","Camouflage Green");
		COLOR_MAP.put("Mobil Red","Medium Red");
		COLOR_MAP.put("Satin Gold","Dark Yellow");
		COLOR_MAP.put("Golden Yellow","Medium Yellow");
		COLOR_MAP.put("Tan","Medium Brown");

	}
	public static String getColorGroup(String colorName){
		String colorGroup = COLOR_MAP.get(colorName);
		return colorGroup == null?"Other":colorGroup;
	}
}
