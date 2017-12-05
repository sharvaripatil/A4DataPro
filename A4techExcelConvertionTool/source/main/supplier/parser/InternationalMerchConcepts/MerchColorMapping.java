package parser.InternationalMerchConcepts;

import java.util.HashMap;
import java.util.Map;

public class MerchColorMapping {
public static Map<String, String> COLOR_MAP =new HashMap<String, String>();
	static{
		COLOR_MAP.put("Black","Medium Black");
		COLOR_MAP.put("Pure White","Medium White");
		COLOR_MAP.put("Red","Medium Red");
		COLOR_MAP.put("Blue","Medium Blue");
		COLOR_MAP.put("Gray","Medium Gray");
		COLOR_MAP.put("White","Medium White");
		COLOR_MAP.put("Silver","Silver Metal");
		COLOR_MAP.put("Clear","Clear");
		COLOR_MAP.put("Yellow","Medium Yellow");
		COLOR_MAP.put("Green","Medium Green");
		COLOR_MAP.put("Charcoal","Medium Gray");
		COLOR_MAP.put("Navy Blue","Dark Blue");
		COLOR_MAP.put("Orange","Medium Orange");
		COLOR_MAP.put("Multicolored","Multi Color");
		COLOR_MAP.put("Chrome","Chrome Metal");
		COLOR_MAP.put("Matte Silver","Silver Metal");
		COLOR_MAP.put("Burgundy","Dark Red");
		COLOR_MAP.put("Satin Gold","Dark Yellow");
		COLOR_MAP.put("Gunmetal","Medium Gray");
		COLOR_MAP.put("Satin Nickel","Nickel Metal");
		COLOR_MAP.put("Brown","Medium Brown");
		COLOR_MAP.put("Pink","Medium Pink");
		COLOR_MAP.put("Khaki","Light Brown");
		COLOR_MAP.put("Navy","Dark Blue");
		COLOR_MAP.put("Gold","Dark Yellow");
		COLOR_MAP.put("Royal Blue","Bright Blue");
		COLOR_MAP.put("Fuchsia","Bright Pink");
		COLOR_MAP.put("French Blue","Medium Blue");
		COLOR_MAP.put("Rust","Dark Orange");
		COLOR_MAP.put("Cocoa","Medium Brown");
		COLOR_MAP.put("Oatmeal","Medium Brown");
		COLOR_MAP.put("Heather Gray","Medium Gray");
		COLOR_MAP.put("Olive","Dark Green");
		COLOR_MAP.put("Purple","Medium Purple");
		COLOR_MAP.put("Sky Blue","Light Blue");
		COLOR_MAP.put("Assorted","Assorted");
		COLOR_MAP.put("Satin Chrome","Chrome Metal");
		COLOR_MAP.put("Matte Blue","Medium Blue");
		COLOR_MAP.put("Matte Burgundy","Dark Red");
		COLOR_MAP.put("Ivory","Medium White");
		COLOR_MAP.put("Sage","Medium Green");
		COLOR_MAP.put("Light Green","Light Green");
		COLOR_MAP.put("Stone","Medium White");
		COLOR_MAP.put("Turquoise","Medium Green");
		COLOR_MAP.put("Nickel","Nickel Metal");
		COLOR_MAP.put("Denim","Medium Blue");
		COLOR_MAP.put("Light Blue","Light Blue");
		COLOR_MAP.put("Apple Green","Medium Green");
		COLOR_MAP.put("Forest Green","Medium Green");
		COLOR_MAP.put("Hunter Green","Dark Green");
		COLOR_MAP.put("Black Marble","Medium Black");
		COLOR_MAP.put("Blue Marble","Medium Blue");
		COLOR_MAP.put("Burgundy Marble","Dark Red");
		COLOR_MAP.put("Green Marble","Medium Green");
		COLOR_MAP.put("Stone Black","Medium Black");
		COLOR_MAP.put("Navy Stone","Dark Blue");
		COLOR_MAP.put("Black Stone","Medium Black");
		COLOR_MAP.put("Natural","Medium White");
		COLOR_MAP.put("Cobalt Blue","Medium Blue");
		COLOR_MAP.put("Maroon","Dark Red");
		COLOR_MAP.put("Pearl Silver","Silver Metal");
		COLOR_MAP.put("Aqua","Medium Blue");
		COLOR_MAP.put("Poppy Red","Medium Red");
		COLOR_MAP.put("Teal","Medium Green");
		COLOR_MAP.put("Clear Blue","Clear Blue");
		COLOR_MAP.put("Rosewood","Medium Brown");
		COLOR_MAP.put("Light Brown","Light Brown");
		COLOR_MAP.put("Dark Brown","Dark Brown");
		COLOR_MAP.put("White/Red","Medium White:Combo:Medium Red");
		COLOR_MAP.put("Semi-Clear","Clear");
		COLOR_MAP.put("Copper","Copper Metal");
		COLOR_MAP.put("Army Green","Dark Green");
		COLOR_MAP.put("Steel Blue","Medium Blue");
		COLOR_MAP.put("Scarlet","Medium Red");
		COLOR_MAP.put("Ivory White","Medium White");
		COLOR_MAP.put("Tan","Light Brown");
		COLOR_MAP.put("Brown Tortoise","Medium Brown");
		COLOR_MAP.put("Twig Brown","Light Brown");
		COLOR_MAP.put("Amber Brown","Medium Brown");
		COLOR_MAP.put("Straw Brown","Light Brown");
		COLOR_MAP.put("Scarlet Red","Medium Red");
		COLOR_MAP.put("Bronze","Medium Brown");
		COLOR_MAP.put("Rose Gold","Medium Pink");
		COLOR_MAP.put("Stone Blue","Medium Blue");
		COLOR_MAP.put("Charcoal Gray","Medium Gray");
		COLOR_MAP.put("Chili Red","Medium Red");
		COLOR_MAP.put("Opal White","Medium Green");
		COLOR_MAP.put("Tangerine Orange","Medium Orange");
       
	}
	public static String getColorGroup(String colorName){
		return COLOR_MAP.get(colorName);// this is used to Ball Pro supplier
	}
}
