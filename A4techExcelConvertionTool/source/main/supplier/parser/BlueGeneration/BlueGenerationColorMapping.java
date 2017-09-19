package parser.BlueGeneration;

import java.util.HashMap;
import java.util.Map;

public class BlueGenerationColorMapping {
public static Map<String, String> COLOR_MAP =new HashMap<String, String>();
	
	static{
		
		COLOR_MAP.put("Yellow","Medium Yellow");
		COLOR_MAP.put("Dark Yellow","Dark Yellow");
		COLOR_MAP.put("Salmon","Medium Pink");
		COLOR_MAP.put("Burnt Orange","Dark Orange");
		COLOR_MAP.put("Orange","Medium Orange");
		COLOR_MAP.put("Mulberry","Medium Purple");
		COLOR_MAP.put("Berry","Medium Purple");
		COLOR_MAP.put("Red","Medium Red");
		COLOR_MAP.put("Burgundy","Dark Red");
		COLOR_MAP.put("Purple","Medium Purple");
		COLOR_MAP.put("Aqua","Light Blue");
		COLOR_MAP.put("Light Blue","Light Blue");
		COLOR_MAP.put("Turquoise","Light Blue");
		COLOR_MAP.put("French Blue","Medium Blue");
		COLOR_MAP.put("Royal","Bright Blue");
		COLOR_MAP.put("Navy","Dark Blue");
		COLOR_MAP.put("Cactus","Medium Green");
		COLOR_MAP.put("Sage","Medium Green");
		COLOR_MAP.put("Kelly","Medium Green");
		COLOR_MAP.put("Hunter","Dark Green");
		COLOR_MAP.put("Natural","Medium White");
		COLOR_MAP.put("Tan","Light Brown");
		COLOR_MAP.put("White","Medium White");
		COLOR_MAP.put("Gray","Medium Gray");
		COLOR_MAP.put("Graphite","Medium Gray");
		COLOR_MAP.put("Black","Medium Black");
		COLOR_MAP.put("Maize","Light Yellow");
		COLOR_MAP.put("Sangria","Medium Pink");
		COLOR_MAP.put("Kiwi","Light Green");
		COLOR_MAP.put("Olive","Dark Green");
		COLOR_MAP.put("Jade","Medium Green");
		COLOR_MAP.put("Chocolate","Medium Brown");
		COLOR_MAP.put("Gray Heather","Medium Gray");
		COLOR_MAP.put("Violet","Medium Purple");
		COLOR_MAP.put("Pink","Medium Pink");
		COLOR_MAP.put("Bright Pink","Bright Pink");
		COLOR_MAP.put("Royal Blue","Bright Blue");
		COLOR_MAP.put("Blue","Medium Blue");
		COLOR_MAP.put("Navy Blue","Dark Blue");
		COLOR_MAP.put("Khaki","Light Brown");
		COLOR_MAP.put("Green","Medium Green");
		COLOR_MAP.put("Light Green","Light Green");
		COLOR_MAP.put("Vegas Gold","Dark Yellow");
		COLOR_MAP.put("Optic Yellow","Medium Yellow");
		COLOR_MAP.put("Safety Orange","Bright Orange");
		COLOR_MAP.put("Teal","Medium Green");
		COLOR_MAP.put("Mango","Light Orange");
		COLOR_MAP.put("Grape","Medium Purple");
		COLOR_MAP.put("Polar Navy","Dark Blue");
		COLOR_MAP.put("Polar Gray","Medium Gray");
		COLOR_MAP.put("Polar Black","Medium Black");
		COLOR_MAP.put("Polar Red","Medium Red");
		COLOR_MAP.put("Polar Blue","Medium Blue");
		COLOR_MAP.put("Polar Hunter","Medium Green");
		COLOR_MAP.put("Polar Orange","Medium Orange");
		COLOR_MAP.put("Polar Pink","Medium Pink");
		COLOR_MAP.put("Coral","Light Pink");
		COLOR_MAP.put("Faded Blue","Light Blue");
		COLOR_MAP.put("Vintage Blue","Bright Blue");
		COLOR_MAP.put("Gold","Dark Yellow");
		COLOR_MAP.put("Dark Pink","Dark Pink");
		COLOR_MAP.put("Team Purple","Medium Purple");
		COLOR_MAP.put("Carolina Blue","Medium Blue");
		COLOR_MAP.put("Hunter Green","Medium Green");
		COLOR_MAP.put("Oxford Green","Light Green");
		COLOR_MAP.put("Oxford Blue","Light Blue");
		COLOR_MAP.put("Multi color","Multi Color");
		COLOR_MAP.put("Plum","Light Purple");
		COLOR_MAP.put("Charcoal","Dark Gray");
		COLOR_MAP.put("Rose","Medium Pink");
		COLOR_MAP.put("Slate Blue","Medium Blue");
		COLOR_MAP.put("Oxford Burgundy","Dark Red");
		COLOR_MAP.put("Burgundy-White Stripe","Dark Red");
		COLOR_MAP.put("Blue-White Stripe","Medium Blue");
		COLOR_MAP.put("Green-White Stripe","Medium Green");
		COLOR_MAP.put("String","Medium Brown");
		COLOR_MAP.put("Kelly Green","Medium Green");
		COLOR_MAP.put("Neon Pink","Bright Pink");
		COLOR_MAP.put("Turquoise Blue","Medium Blue");
		COLOR_MAP.put("Heather Gray","Medium Gray");
		COLOR_MAP.put("Heather Navy","Dark Blue");
		COLOR_MAP.put("Heather Black","Medium Black");
		COLOR_MAP.put("Heather Royal","Bright Blue");
		COLOR_MAP.put("Heather Kelly Green","Medium Green");
		COLOR_MAP.put("Heather Turquoise","Medium Blue");
		COLOR_MAP.put("Heather Red","Medium Red");
		COLOR_MAP.put("Heather Royal Blue","Bright Blue");
		COLOR_MAP.put("Tropic","Multi Color");
		COLOR_MAP.put("Cocktail","Multi Color");
		COLOR_MAP.put("Floral","Multi Color");
		COLOR_MAP.put("Hibiscus","Multi Color");
		COLOR_MAP.put("Forest","Medium Green");
		COLOR_MAP.put("Turquioise","Medium Blue");
		COLOR_MAP.put("Lt Blue","Light Blue");
		COLOR_MAP.put("Red-Black","Medium Red");
		COLOR_MAP.put("Black-Graphite","Medium Black");
		COLOR_MAP.put("Tan-Black","Medium Brown");
		COLOR_MAP.put("Graphite-Black","Medium Gray");
		COLOR_MAP.put("Light Blue-Navy","Light Blue");
		COLOR_MAP.put("Batik","Clear");

       
	}
	public static String getColorGroup(String colorName){
		//return COLOR_MAP.get(colorName.toUpperCase());// this is used to Pro golf Supplier
		return COLOR_MAP.get(colorName);// this is used to Ball Pro supplier
	}
}
