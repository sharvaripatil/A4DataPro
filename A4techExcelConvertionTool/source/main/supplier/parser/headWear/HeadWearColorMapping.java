package parser.headWear;

import java.util.HashMap;
import java.util.Map;

public class HeadWearColorMapping {
public static Map<String, String> COLOR_MAP =new HashMap<String, String>();
public static Map<String, String> MATERIAL_MAP = new HashMap<>();
	
	static{
		COLOR_MAP.put("Black","Medium Black");
		COLOR_MAP.put("Mocha","Medium Brown");
		COLOR_MAP.put("Burgundy","Dark Red");
		COLOR_MAP.put("Brown","Medium Brown");
		COLOR_MAP.put("Dark Brown","Dark Brown");
		COLOR_MAP.put("Walnut","Medium Brown");
		COLOR_MAP.put("Chocolate Brown","Dark Brown");
		COLOR_MAP.put("Rosewood","Medium Red");
		COLOR_MAP.put("Black Crocodile","Medium Black");
		COLOR_MAP.put("Midnight Black","Medium Black");
		COLOR_MAP.put("Red","Medium Red");
		COLOR_MAP.put("Dark Brown","Dark Brown");
		COLOR_MAP.put("Perfect Purple","Light Purple");
		COLOR_MAP.put("Espresso Brown","Medium Brown");
		COLOR_MAP.put("Sandy Tan","Light Brown");
		COLOR_MAP.put("Rossa Red","Medium Red");
		COLOR_MAP.put("Sky Blue","Medium Blue");
		COLOR_MAP.put("Cameo Pink","Medium Pink");
		COLOR_MAP.put("Lime Green","Medium Green");
		COLOR_MAP.put("Daisy White","Medium White");
		COLOR_MAP.put("Brown Crocodile","Medium Brown");
		COLOR_MAP.put("Cognac Brown","Medium Brown");
		COLOR_MAP.put("Castlerock Gray","Medium Gray");
		COLOR_MAP.put("Breeze Beige","Light Brown");
		COLOR_MAP.put("Bramble Brown","Medium Brown");
		COLOR_MAP.put("Mustard Green","Medium Green");
		COLOR_MAP.put("Dove White","Medium White");
		COLOR_MAP.put("Pink","Medium Pink");
		COLOR_MAP.put("Blackwood","Medium Black");
		COLOR_MAP.put("Gold","Dark Yellow");
		COLOR_MAP.put("Silver","Medium Gray");

		MATERIAL_MAP.put("Top-Grain Leather","Leather");
		MATERIAL_MAP.put("Leatherette","Other");
		MATERIAL_MAP.put("Bonded Leather","Leather");
		MATERIAL_MAP.put("Crocodile Embossed Cowhide","Leather");
		MATERIAL_MAP.put("Italian Leather","Leather");
		MATERIAL_MAP.put("African Rosewood","Wood");
		MATERIAL_MAP.put("Suede","Suede");
		MATERIAL_MAP.put("Birchwood","Wood");
		MATERIAL_MAP.put("Faux Leather","Other");

	}
	
	public static String getColorGroup(String colorName){
		return COLOR_MAP.get(colorName);
	}
	
	public static String getMaterialGroup(String materialName){
		return MATERIAL_MAP.get(materialName);
	}
}
