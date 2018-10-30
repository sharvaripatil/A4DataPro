package parser.digiSpec;

import java.util.HashMap;
import java.util.Map;

public class DigiSpecColorAndMaterialMapping {
public static Map<String, String> COLOR_MAP =new HashMap<String, String>();
public static Map<String, String> MATERIAL_MAP = new HashMap<>();
	
	static{
		COLOR_MAP.put("white","Medium White");
		COLOR_MAP.put("all colors","Assorted");
		COLOR_MAP.put("red","Medium Red");
		COLOR_MAP.put("blue","Medium Blue");
		COLOR_MAP.put("light blue","Light Blue");
		COLOR_MAP.put("green","Medium Green");
		COLOR_MAP.put("purple","Medium Purple");
		COLOR_MAP.put("brown","Medium Brown");
		COLOR_MAP.put("yellow","Medium Yellow");
		COLOR_MAP.put("orange","Medium Orange");
		COLOR_MAP.put("tan","Light Brown");
		COLOR_MAP.put("burgundy","Dark Red");
		COLOR_MAP.put("black","Medium Black");
		COLOR_MAP.put("pink","Medium Pink");
		COLOR_MAP.put("maroon","Medium Red");
		COLOR_MAP.put("gray","Medium Gray");
		COLOR_MAP.put("grey","Medium Gray");
		COLOR_MAP.put("navy blue","Dark Blue");

		MATERIAL_MAP.put("lo-tac adhesive","Other");
		MATERIAL_MAP.put("Odorless Latex-Free SBR synthetic rubber","Other");
		MATERIAL_MAP.put("paperboard","Paper");
		MATERIAL_MAP.put("post-consumer recycled paper","Paper");
		MATERIAL_MAP.put("rubber","Rubber");
		MATERIAL_MAP.put("plastic","Plastic");
		MATERIAL_MAP.put("polypropylene","Polypropylene");
		MATERIAL_MAP.put("open cell natural rubber","Rubber");
		MATERIAL_MAP.put("polyester","Polyester");

	}
	
	public static String getColorGroup(String colorName){
		return COLOR_MAP.get(colorName);
	}
	
	public static String getMaterialGroup(String materialName){
		return MATERIAL_MAP.get(materialName);
	}
}
