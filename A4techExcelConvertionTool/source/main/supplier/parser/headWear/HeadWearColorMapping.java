package parser.headWear;

import java.util.HashMap;
import java.util.Map;

public class HeadWearColorMapping {
public static Map<String, String> COLOR_MAP =new HashMap<String, String>();
	
	static{
		COLOR_MAP.put("black","Medium Black");
		COLOR_MAP.put("navy blue","Dark Blue");
		COLOR_MAP.put("royal blue","Bright Blue");
		COLOR_MAP.put("red","Medium Red");
		COLOR_MAP.put("stone","Medium Gray");
		COLOR_MAP.put("forest green","Medium Green");
		COLOR_MAP.put("beige","Light Brown");
		COLOR_MAP.put("maroon red","Medium Purple");
		COLOR_MAP.put("white","Medium White");
		COLOR_MAP.put("gray","Medium Gray");
		COLOR_MAP.put("camouflage leaf brown","Camouflage Brown");
		COLOR_MAP.put("leaf brown camouflage","Camouflage Brown");
		COLOR_MAP.put("orange","Medium Orange");
		COLOR_MAP.put("bottle green","Medium Green");
		COLOR_MAP.put("sky blue","Light Blue");
		COLOR_MAP.put("dark beige","Dark Brown");
		COLOR_MAP.put("army green","Dark Green");
		COLOR_MAP.put("dark gray","Dark Gray");
		COLOR_MAP.put("brown","Medium Brown");
		COLOR_MAP.put("cyan blue","Light Blue");
		COLOR_MAP.put("green","Medium Green");
		COLOR_MAP.put("charcoal gray","Dark Gray");
		COLOR_MAP.put("gold","Gold Metal");
		COLOR_MAP.put("khaki beige","Light Brown");
		COLOR_MAP.put("natural beige","Light Brown");
		COLOR_MAP.put("clay beige","Light Brown");
		COLOR_MAP.put("olive green","Dark Green");
		COLOR_MAP.put("yellow","Medium Yellow");
		COLOR_MAP.put("pink","Medium Pink");
		COLOR_MAP.put("bright green","Bright Green");
		COLOR_MAP.put("assorted","Multi Color");
		COLOR_MAP.put("khaki green","Medium Green");
		COLOR_MAP.put("stone beige","Light Brown");
		COLOR_MAP.put("emerald green","Dark Green");
		COLOR_MAP.put("hot pink","Bright Pink");
		COLOR_MAP.put("purple","Medium Purple");
		COLOR_MAP.put("lime green","Bright Green");
		COLOR_MAP.put("clay brown","Medium Brown");
		COLOR_MAP.put("cranberry red","Dark Red");
		COLOR_MAP.put("lemon yellow","Medium Yellow");
		COLOR_MAP.put("light pink","Light Pink");
		COLOR_MAP.put("mauve purple","Light Purple");
		COLOR_MAP.put("mustard yellow","Medium Yellow");
		COLOR_MAP.put("ocean blue","Dark Blue");
		COLOR_MAP.put("powder blue","Light Blue");
		COLOR_MAP.put("rust red","Medium Red");
		COLOR_MAP.put("forest","Medium Green");
		COLOR_MAP.put("navy","Dark Blue");
		COLOR_MAP.put("light blue","Light Blue");
		COLOR_MAP.put("olive green camouflage","Camouflage Green");
		COLOR_MAP.put("khaki beige camouflage","Camouflage Brown");
		COLOR_MAP.put("charcoal","Dark Gray");
		COLOR_MAP.put("emerald","Dark Green");
		COLOR_MAP.put("luminescent green","Bright Green");
		COLOR_MAP.put("luminescent orange","Bright Orange");
		COLOR_MAP.put("maroon","Dark Red");
		COLOR_MAP.put("sky","Light Blue");
		COLOR_MAP.put("cyan","Light Blue");
		COLOR_MAP.put("camouflage green","Camouflage Green");
		COLOR_MAP.put("royal","Bright Blue");
		COLOR_MAP.put("washed camouflage leaf brown","Camouflage Brown");
		COLOR_MAP.put("digital camouflage green","Camouflage Green");
		COLOR_MAP.put("true timber camouflage green","Camouflage Green");
		COLOR_MAP.put("true timber camouflage brown","Camouflage Brown");
		COLOR_MAP.put("conceal green","Medium Green");
		COLOR_MAP.put("charcoal grey","Dark Gray");
		COLOR_MAP.put("light gray","Light Gray");
		COLOR_MAP.put("tan beige","Light Brown");
		COLOR_MAP.put("tan","Light Brown");
		COLOR_MAP.put("silver","Silve Metal");
		COLOR_MAP.put("bottle","Clear");
		COLOR_MAP.put("blue","Medium Blue");
	    COLOR_MAP.put("grey","Medium Gray");

	}
	
	public static String getColorGroup(String colorName){
		String colorGroup = COLOR_MAP.get(colorName);
		return colorGroup == null ?"Other": colorGroup;
	}
	
	
}
