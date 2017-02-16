package com.a4tech.apparel.product.mapping;

import java.util.HashMap;
import java.util.Map;

public class ApparelColorMapping {
	public static Map<String, String> COLOR_MAP =new HashMap<String, String>();
	static{
		COLOR_MAP.put("True Navy-Gray 264","Medium Gray");
		COLOR_MAP.put("True Navy 260","Dark Blue");
		COLOR_MAP.put("Gray 115","Medium Gray");
		COLOR_MAP.put("Navy 040","Dark Blue");
		COLOR_MAP.put("Black 010","Medium Black");
		COLOR_MAP.put("Forest 020","Dark Green");
		COLOR_MAP.put("Maroon 030","Dark Red");
		COLOR_MAP.put("Red 060","Medium Red");
		COLOR_MAP.put("Royal 070","Bright Blue");
		COLOR_MAP.put("White 080","Medium White");
		COLOR_MAP.put("Dark Navy 095","Dark Blue");
		COLOR_MAP.put("Black","Medium Black");
		COLOR_MAP.put("Purple 050","Medium Purple");
		COLOR_MAP.put("Charcoal Heather 110","Medium Gray");
		COLOR_MAP.put("Yellow 150","Medium Yellow");
		COLOR_MAP.put("Blue Fog 262","Medium Blue");
		COLOR_MAP.put("Oxford Heather 114","Light Gray");
		COLOR_MAP.put("Olive 284","Dark Green");
		COLOR_MAP.put("Plum 055","Medium Red");
		COLOR_MAP.put("Teal 232","Medium Green");
		COLOR_MAP.put("Pink-Reflective 188","Bright Pink");
		COLOR_MAP.put("Buttercup-Reflective 154","Medium Yellow");
		COLOR_MAP.put("Coral-Reflective 256","Medium Orange");
		COLOR_MAP.put("Red-Reflective 166","Medium Red");
		COLOR_MAP.put("Wave-Reflective 192","Light Blue");
		COLOR_MAP.put("Hot Pink-Reflective 334","Bright Pink");
		COLOR_MAP.put("Black-Reflective 210","Medium Black");
		COLOR_MAP.put("Mint-Reflective 323","Light Green");
		COLOR_MAP.put("Aqua-Reflective 236","Light Blue");
		COLOR_MAP.put("True Navy-Reflective 263","Dark Blue");
		COLOR_MAP.put("Violet-Reflective 353","Medium Purple");
		COLOR_MAP.put("Periwinkle 352","Light Purple");
		COLOR_MAP.put("Limited Quantity Bright Coral 358","Bright Orange");
		COLOR_MAP.put("Oxford Heather Gray 114","Medium Gray");
		COLOR_MAP.put("Lime Green 158","Light Green");
		COLOR_MAP.put("Saddle 274","Light Brown");
		COLOR_MAP.put("Black 006","Medium Black");
		COLOR_MAP.put("Columbia Blue 089","Medium Blue");
		COLOR_MAP.put("Stone 140","Light Green");
		COLOR_MAP.put("Pink 182","Medium Pink");
		COLOR_MAP.put("Royal 060","Medium Blue");
		COLOR_MAP.put("Coral 255","Medium Pink");
		COLOR_MAP.put("Mint 322","Light Green");
		COLOR_MAP.put("Aqua 228","Light Blue");
		COLOR_MAP.put("Hot Pink","Medium Pink");
		COLOR_MAP.put("Blush Pink 186","Medium Pink");
		COLOR_MAP.put("Cardinal 036","Medium Red");
		COLOR_MAP.put("Orange 250","Medium Orange");
		COLOR_MAP.put("Golden Yellow 150","Medium Yellow");
		COLOR_MAP.put("Marine Blue","Medium Green");
		COLOR_MAP.put("Storm Blue 266","Medium Blue");
		COLOR_MAP.put("Pink 180","Medium Pink");
		COLOR_MAP.put("Indigo Blue 179","Medium Blue");
		COLOR_MAP.put("Dark Charcoal Heather 111","Dark Gray");
		COLOR_MAP.put("Bright Coral 352","Bright Orange");
		COLOR_MAP.put("Navy Heather 240","Dark Blue");
		COLOR_MAP.put("Oatmeal Heather 129","Light Brown");
		COLOR_MAP.put("Jade Heather 230","Medium Green");
		COLOR_MAP.put("Heather Gray 116","Medium Gray");
		COLOR_MAP.put("Royal Blue 070","Bright Blue");
		COLOR_MAP.put("Purple Reign 053","Dark Purple");
		COLOR_MAP.put("Flamingo 389","Medium Pink");
		COLOR_MAP.put("Sea Glass 235","Medium Green");
		COLOR_MAP.put("Cobalt 191","Medium Blue");
		COLOR_MAP.put("Black Heather 104","Medium Black");
		COLOR_MAP.put("Brown Heather 279","Medium Brown");
		COLOR_MAP.put("Blue Heather 293","Medium Blue");
		COLOR_MAP.put("Orchid Heather 057","Medium Purple");
		COLOR_MAP.put("Graphite Heather 208","Medium Gray");
		COLOR_MAP.put("Black-Gray 006","Medium Black");
		COLOR_MAP.put("Passion Pink 337","Medium Pink");
		COLOR_MAP.put("Garnet 169","Medium Red");
		COLOR_MAP.put("Berry 122","Medium Purple");
		COLOR_MAP.put("Dove White 083","Medium White");
		COLOR_MAP.put("Shadow 001","Medium Black");
		COLOR_MAP.put("Steel Gray 204","Medium Gray");
		COLOR_MAP.put("Pewter 205","Medium Gray");
		COLOR_MAP.put("Wave-Reflectrive 192","Medium Blue");
		COLOR_MAP.put("Nautical Blue 175","Bright Blue");
		COLOR_MAP.put("Teal 328","Medium Green");
		COLOR_MAP.put("Cloud 206","Medium Gray");
		COLOR_MAP.put("Ash Gray 209","Light Gray");
		COLOR_MAP.put("Blue Ink 177","Dark Blue");
		COLOR_MAP.put("Blue Steel 173","Medium Blue");
		COLOR_MAP.put("Lead 202","Medium Black");
		COLOR_MAP.put("Jade 330","Medium Green");
		COLOR_MAP.put("Royal/Black 078","Medium Black");
		COLOR_MAP.put("Navy Blue","Dark Blue");
		COLOR_MAP.put("White","Medium White");
		COLOR_MAP.put("Charcoal 201","Medium Black");
		COLOR_MAP.put("Carolina Blue 268","Medium Blue");
		COLOR_MAP.put("Melon 37","Medium Pink");
		COLOR_MAP.put("Baby (Navy) Blue 410","Dark Blue");
		COLOR_MAP.put("Dark Gray Heather 511","Dark Gray");
		COLOR_MAP.put("Navy Blue 040","Dark Blue");
		COLOR_MAP.put("Bright Coral 358","Bright Pink");
		COLOR_MAP.put("Hot Pink 335","Bright Pink");
		COLOR_MAP.put("Multi color 100","Multi Color");
		COLOR_MAP.put("Cream 366","Medium White");
		COLOR_MAP.put("Black 210","Medium Black");
		COLOR_MAP.put("Gray 316","Medium Gray");
		COLOR_MAP.put("Orange 450","Medium Orange");
		COLOR_MAP.put("Red 166","Medium Red");
		COLOR_MAP.put("Hot Pink 334","Bright Pink");
		COLOR_MAP.put("Aqua 236","Light Blue");
		COLOR_MAP.put("Navy Blue 263","Dark Blue");
		COLOR_MAP.put("Light Gray Heather 514","Light Gray");
		COLOR_MAP.put("Ivory Heather 518","Medium White");
		COLOR_MAP.put("Kelly Green 145","Medium Green");
		COLOR_MAP.put("Graphite Navy 290","Dark Blue");
		COLOR_MAP.put("Khaki 130","Medium Brown");
		COLOR_MAP.put("Gray","Medium Gray");
		COLOR_MAP.put("Forest Green 020","Medium Green");

	}
	
	public static String getColorGroup(String colorName){
		return COLOR_MAP.get(colorName);
	}
}