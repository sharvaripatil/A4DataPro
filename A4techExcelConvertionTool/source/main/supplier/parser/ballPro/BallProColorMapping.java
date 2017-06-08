package parser.ballPro;

import java.util.HashMap;
import java.util.Map;

public class BallProColorMapping {
public static Map<String, String> COLOR_MAP =new HashMap<String, String>();
	
	static{
		
		COLOR_MAP.put("Antique Brass","Brass Metal");
		COLOR_MAP.put("Antique Gold","Gold Metal");
		COLOR_MAP.put("Antique Nickel","Nickel Metal");
		COLOR_MAP.put("Antique Silver","Silver Metal");
		COLOR_MAP.put("Beige","Light Brown");
		COLOR_MAP.put("Black","Medium Black");
		COLOR_MAP.put("Blue","Medium Blue");
		COLOR_MAP.put("Brass","Brass Metal");
		COLOR_MAP.put("Bright Crimson","Medium Red");
		COLOR_MAP.put("Brown","Medium Brown");
		COLOR_MAP.put("Burgundy","Dark Red");
		COLOR_MAP.put("Charcoal","Medium Gray");
		COLOR_MAP.put("Chartreuse","Medium Yellow");
		COLOR_MAP.put("Cherry","Medium Red");
		COLOR_MAP.put("Citrus Green","Bright Green");
		COLOR_MAP.put("Citrus Orange","Bright Orange");
		COLOR_MAP.put("Citrus Pink","Bright Pink");
		COLOR_MAP.put("Clear","Clear");
		COLOR_MAP.put("Clown","Multi Color");
		COLOR_MAP.put("College Navy","Dark Blue");
		COLOR_MAP.put("Copper","Copper Metal");
		COLOR_MAP.put("Cranberry","Dark Red");
		COLOR_MAP.put("Dark Blue","Dark Blue");
		COLOR_MAP.put("Dark Green","Dark Green");
		COLOR_MAP.put("Evergreen","Medium Green");
		COLOR_MAP.put("Fluorescent Yellow","Bright Yellow");
		COLOR_MAP.put("Fuchsia","Bright Pink");
		COLOR_MAP.put("Full Color","Multi Color");
		COLOR_MAP.put("Glass Ghost","Clear White");
		COLOR_MAP.put("Glow","Other");
		COLOR_MAP.put("Gold","Gold Metal");
		COLOR_MAP.put("Gray","Medium Gray");
		COLOR_MAP.put("Green","Medium Green");
		COLOR_MAP.put("Gunmetal","Medium Gray");
		COLOR_MAP.put("High Optic Yellow","Medium Yellow");
		COLOR_MAP.put("Hi-Vis Green","Bright Green");
		COLOR_MAP.put("Hot Head","Medium Red");
		COLOR_MAP.put("Hot Pink","Medium Pink");
		COLOR_MAP.put("Hunter","Dark Green");
		COLOR_MAP.put("Indigo","Dark Blue");
		COLOR_MAP.put("Khaki","Light Brown");
		COLOR_MAP.put("Light Blue","Light Blue");
		COLOR_MAP.put("Light Pink","Multi Color");
		COLOR_MAP.put("Lime","Bright Green");
		COLOR_MAP.put("Lime Green","Bright Green");
		COLOR_MAP.put("Mallard","Dark Green");
		COLOR_MAP.put("Maroon","Dark Red");
		COLOR_MAP.put("Multi Color","Multi Color");
		COLOR_MAP.put("Natural","DARK WHITE");
		COLOR_MAP.put("Navy","Dark Blue");
		COLOR_MAP.put("Navy Blue","Dark Blue");
		COLOR_MAP.put("Neon Pink","Bright Pink");
		COLOR_MAP.put("Neon Yellow","Bright Yellow");
		COLOR_MAP.put("Nickel","Nickel Metal");
		COLOR_MAP.put("Orange","Medium Orange");
		COLOR_MAP.put("Pearl White","Medium White");
		COLOR_MAP.put("Pewter","Pewter Metal");
		COLOR_MAP.put("Photo Blue","Medium Blue");
		COLOR_MAP.put("Pink","Medium Pink");
		COLOR_MAP.put("Polished Gold","Gold Metal");
		COLOR_MAP.put("Polished Silver","Silver Metal");
		COLOR_MAP.put("Pool Blue","Medium Blue");
		COLOR_MAP.put("Purple","Medium Purple");
		COLOR_MAP.put("Red","Medium Red");
		COLOR_MAP.put("Red Head","Medium Red");
		COLOR_MAP.put("Royal Blue","Medium Blue");
		COLOR_MAP.put("Rust","Medium Orange");
		COLOR_MAP.put("Satin Silver","Silver Metal");
		COLOR_MAP.put("Silver","Medium Yellow");
		COLOR_MAP.put("Silver Blue","Medium Blue");
		COLOR_MAP.put("Sky Blue","Light Blue");
		COLOR_MAP.put("Slate Blue","Medium Blue");
		COLOR_MAP.put("Solid Black","Medium Black");
		COLOR_MAP.put("Solid Yellow","Medium Yellow");
		COLOR_MAP.put("Stone","Multi Color");
		COLOR_MAP.put("Tan","Light Brown");
		COLOR_MAP.put("Tangerine","Medium Orange");
		COLOR_MAP.put("Turquoise","Light Green");
		COLOR_MAP.put("University Red","Medium Red");
		COLOR_MAP.put("Varsity Blue","Medium Blue");
		COLOR_MAP.put("Walnut","Light Brown");
		COLOR_MAP.put("White","Medium White");
		COLOR_MAP.put("Yellow","Medium Yellow");
		COLOR_MAP.put("Blue Aster","Medium Blue");
		COLOR_MAP.put("Metallic Red","Metallic Red");
       
	}
	public static String getColorGroup(String colorName){
		//return COLOR_MAP.get(colorName.toUpperCase());// this is used to Pro golf Supplier
		return COLOR_MAP.get(colorName);// this is used to Ball Pro supplier
	}
}
