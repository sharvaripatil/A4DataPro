package parser.bayState;

import java.util.HashMap;
import java.util.Map;

public class BayStateColorAndMaterialMapping {
public static Map<String, String> COLOR_MAP =new HashMap<String, String>();
public static Map<String, String> MATERIAL_MAP =new HashMap<String, String>();
	static{
		
		COLOR_MAP.put("Black","Medium Black");
		COLOR_MAP.put("Natural Wood","Light Brown");
		COLOR_MAP.put("White","Medium White");
		COLOR_MAP.put("Yellow","Medium Yellow");
		COLOR_MAP.put("Lime Green","Bright Green");
		COLOR_MAP.put("Red","Medium Red");
		COLOR_MAP.put("Hot Pink","Bright Pink");
		COLOR_MAP.put("Process Blue","Medium Blue");
		COLOR_MAP.put("Reflex Blue","Medium Blue");
		COLOR_MAP.put("Green","Medium Green");
		COLOR_MAP.put("Purple","Medium Purple");
		COLOR_MAP.put("Brown","Medium Brown");
		COLOR_MAP.put("Pink","Medium Pink");
		COLOR_MAP.put("Blue","Medium Blue");
		COLOR_MAP.put("Assorted","Assorted");
		COLOR_MAP.put("Translucent Blue","Clear Blue");
		COLOR_MAP.put("Silver","Silver Metal");
		COLOR_MAP.put("Cream","Light Yellow");
		COLOR_MAP.put("Clear","Clear");
		COLOR_MAP.put("Orange","Medium Orange");
		COLOR_MAP.put("Baby Blue","Light Blue");
		COLOR_MAP.put("Teal","Medium Green");
		COLOR_MAP.put("Translucent Red","Clear Red");
		COLOR_MAP.put("Translucent Green","Clear Green");
		COLOR_MAP.put("Translucent Orange","Clear Orange");
		COLOR_MAP.put("Translucent","Clear");
		COLOR_MAP.put("Cornflower Blue","Medium Blue");
		COLOR_MAP.put("Seafoam Green","Light Green");
		COLOR_MAP.put("Crystal Blue","Medium Blue");
		COLOR_MAP.put("Burgundy","Dark Red");
		COLOR_MAP.put("Metallic Blue","Metallic Blue");
		COLOR_MAP.put("Metallic Silver","Silver Metal");
		COLOR_MAP.put("Metallic Red","Metallic Red");
		COLOR_MAP.put("Metallic Green","Metallic Green");
		COLOR_MAP.put("Metallic Purple","Metallic Purple");
		COLOR_MAP.put("Deep Blue","Medium Blue");
		COLOR_MAP.put("Azure Blue","Light Blue");
		COLOR_MAP.put("Neon Orange","Bright Orange");
		COLOR_MAP.put("Neon Green","Bright Green");
		COLOR_MAP.put("Neon Yellow","Bright Yellow");
		COLOR_MAP.put("Neon Pink","Bright Pink");
		COLOR_MAP.put("Neon Blue","Bright Blue");
		COLOR_MAP.put("Athletic Yellow","Medium Yellow");
		COLOR_MAP.put("Royal Blue","Bright Blue");
		COLOR_MAP.put("Frosted","Light White");
		COLOR_MAP.put("Recycled Black","Medium Black");
		COLOR_MAP.put("Granite","Medium Gray");
		COLOR_MAP.put("Dark Blue","Dark Blue");
		COLOR_MAP.put("Polished Chrome","Chrome Metal");
		COLOR_MAP.put("Translucent Purple","Clear Purple");
		COLOR_MAP.put("Translucent Pink","Clear Pink");
		COLOR_MAP.put("Translucent Yellow","Clear Yellow");
		COLOR_MAP.put("Carribean Blue","Medium Blue");
		COLOR_MAP.put("Cranberry","Medium Red");
		COLOR_MAP.put("Slate Blue","Medium Blue");
		COLOR_MAP.put("French Vanilla","Medium White");
		COLOR_MAP.put("Forest Green","Dark Green");
		COLOR_MAP.put("Kelly Green","Medium Green");
		COLOR_MAP.put("Navy Blue","Dark Blue");
		COLOR_MAP.put("Maroon","Dark Red");
		COLOR_MAP.put("Translucent Black","Clear Black");
		COLOR_MAP.put("Translucent Brown","CLEAR BROWN");
		COLOR_MAP.put("Blue Plaid","Medium Blue");
		COLOR_MAP.put("Red Plaid","Medium Red");
		COLOR_MAP.put("Yellow Plaid","Medium Yellow");
		COLOR_MAP.put("Solid Red","Medium Red");
		COLOR_MAP.put("Caribbean Blue","Medium Blue");
		COLOR_MAP.put("Translucent Violet","Clear Purple");
		COLOR_MAP.put("Beige","Light Brown");
		COLOR_MAP.put("Gray","Medium Gray");
		COLOR_MAP.put("Light Blue","Light Blue");
		COLOR_MAP.put("Tanslucent Blue","Clear Blue");
		COLOR_MAP.put("Safety Orange","Bright Orange");
		COLOR_MAP.put("Sangria Red","Dark Red");
		COLOR_MAP.put("Seaform Green","Light Green");
		COLOR_MAP.put("Sunflower Yellow","Medium Yellow");
		COLOR_MAP.put("Antique White","Medium White");
		COLOR_MAP.put("Charcoal Gray","Medium Gray");
		COLOR_MAP.put("Burnt Orange","Medium Orange");
		COLOR_MAP.put("Camo","Multi Color");
		COLOR_MAP.put("Fuschia","Dark Pink");
		COLOR_MAP.put("Crimson","Medium Red");
		COLOR_MAP.put("True Life Camo","Multi Color");
		COLOR_MAP.put("Ivory","Medium White");
		COLOR_MAP.put("Mustard Yellow","Dark Yellow");
		COLOR_MAP.put("Dark Navy Blue","Dark Blue");
		COLOR_MAP.put("Gun Metal","Medium Gray");
		COLOR_MAP.put("Mustard","Dark Yellow");
		COLOR_MAP.put("Gun Powder","Medium Gray");
		COLOR_MAP.put("Natural Bamboo","Light Brown");
		COLOR_MAP.put("Trim in Black","Medium Black");
		COLOR_MAP.put("Trim in Green","Medium Green");
		COLOR_MAP.put("Trim in Red","Medium Red");
		COLOR_MAP.put("Champagne","Light Brown");
		COLOR_MAP.put("Coral","Medium Orange");
		COLOR_MAP.put("Red with White Exterior","Medium Red:Combo:Medium White:Secondary");
		COLOR_MAP.put("White with White Trim","Medium White");
		COLOR_MAP.put("White with Black","Medium White:Combo:Medium Black:Secondary");
		COLOR_MAP.put("White with Red Roller","Medium White:Combo:Medium Red:Secondary");
		COLOR_MAP.put("Blue Grip and Crystal Blue Brush Handle","Medium Blue:Combo:Transparent Blue:Secondary");
		COLOR_MAP.put("White Case With Charcoal Sponge","Medium White:Combo:Dark Black:Secondary");
		COLOR_MAP.put("Rainbow","Multi Color");
		COLOR_MAP.put("Stock Art as Shown","Other");
		COLOR_MAP.put("White Base with Rainbow Translucent Boxes","Medium White:combo:Multi Color:Secondary");
		COLOR_MAP.put("White Base with Translucent Pill Boxes","Medium White:combo:Clear:Secondary");
		COLOR_MAP.put("White Base-Crystal Blue-Lavender","Medium White:Combo:Clear Blue:Secondary:Clear Purple:Trim");
		COLOR_MAP.put("White Base-Rainbow Translucent Boxes","Medium White:Combo:Clear Multi Color:Secondary");
		
		
	// Material Mapping
		MATERIAL_MAP.put("Crystal Styrene", "ABS Plastic");
		MATERIAL_MAP.put("Open Cell Foam", "Foam");
		MATERIAL_MAP.put("Bamboo", "Other Wood/Paper");
		MATERIAL_MAP.put("Melamine", "Other");
		MATERIAL_MAP.put("ABS with Nylon Wheel", "ABS Plastic:Combo:Nylon");
		MATERIAL_MAP.put("ABS stainless steel blade", "ABS Plastic:Combo:Stainless Steel");
		MATERIAL_MAP.put("Impact Plastic", "Plastic");
		MATERIAL_MAP.put("Bio-Degradable Plastic", "Plastic");
		MATERIAL_MAP.put("Recycled Paper", "Paper");
		MATERIAL_MAP.put("Plastice", "Plastic");
		MATERIAL_MAP.put("ABS Plastic", "Plastic");//
		MATERIAL_MAP.put("Plated Steel", "Steel");
		MATERIAL_MAP.put("Polycarbonate", "Polycarbonate(PC)");
		MATERIAL_MAP.put("ABS Plastic", "Plastic");
       
	}
	public static String getColorGroup(String colorName){
		//return COLOR_MAP.get(colorName.toUpperCase());// this is used to Pro golf Supplier
		String group = COLOR_MAP.get(colorName);// this is used to Ball Pro supplier
		return group == null?"Other":group;
	}
	public static String getMaterialGroup(String colorName){
		return MATERIAL_MAP.get(colorName.trim());
	}
}
