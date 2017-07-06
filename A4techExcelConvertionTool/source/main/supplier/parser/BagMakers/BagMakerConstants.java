package parser.BagMakers;

import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import parser.tomaxusa.TomaxConstants;

public class BagMakerConstants {
	private static Logger _LOGGER = Logger.getLogger(TomaxConstants.class);
	public static Map<String, String> BMCOLOR_MAP =new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);//new HashMap<String, String>();
	 static {
		 BMCOLOR_MAP.put("Red","Medium Red");
		 BMCOLOR_MAP.put("Black","Medium Black");
		 BMCOLOR_MAP.put("Royal Blue","Bright Blue");
		 BMCOLOR_MAP.put("White","Medium White");
		 BMCOLOR_MAP.put("Burgundy","Dark Red");
		 BMCOLOR_MAP.put("Hunter Green","Medium Green");
		 BMCOLOR_MAP.put("Khaki","Light Brown");
		 BMCOLOR_MAP.put("Navy Blue","Dark Blue");
		 BMCOLOR_MAP.put("Teal","Medium Green");
		 BMCOLOR_MAP.put("Brown Kraft","Medium Brown");
		 BMCOLOR_MAP.put("Brown","Medium Brown");
		 BMCOLOR_MAP.put("Brite Blue","Bright Blue");
		 BMCOLOR_MAP.put("Lime Green","Bright Green");
		 BMCOLOR_MAP.put("Brite Pink","Bright Pink");
		 BMCOLOR_MAP.put("Grape","Medium Purple");
		 BMCOLOR_MAP.put("Clear","Clear");
		 BMCOLOR_MAP.put("Multi color","Multi Color");
		 BMCOLOR_MAP.put("Custom","Other");
		 BMCOLOR_MAP.put("Green","Medium Green");
		 BMCOLOR_MAP.put("Orange","Medium Orange");
		 BMCOLOR_MAP.put("Silver","Silver Metal");
		 BMCOLOR_MAP.put("Blue","Medium Blue");
		 BMCOLOR_MAP.put("Lavender","Medium Purple");
		 BMCOLOR_MAP.put("Tangerine","Medium Orange");
		 BMCOLOR_MAP.put("Yellow","Medium Yellow");
		 BMCOLOR_MAP.put("Buttercup Yellow","Medium Yellow");
		 BMCOLOR_MAP.put("Scarlet Red","Medium Red");
		 BMCOLOR_MAP.put("Purple","Medium Purple");
		 BMCOLOR_MAP.put("Midnight Blue","Dark Blue");
		 BMCOLOR_MAP.put("Parade Blue","Medium Blue");
		 BMCOLOR_MAP.put("Holiday Green","Medium Green");
		 BMCOLOR_MAP.put("Claret","Medium Red");
		 BMCOLOR_MAP.put("French Vanilla","Medium White");
		 BMCOLOR_MAP.put("Fiesta Blue","Medium Blue");
		 BMCOLOR_MAP.put("Citrus Green","Medium Green");
		 BMCOLOR_MAP.put("Gold","Dark Yellow");
		 BMCOLOR_MAP.put("Natural","Medium White");
		 BMCOLOR_MAP.put("Frosted","Medium White");
		 BMCOLOR_MAP.put("Frosted Clear","Clear");
		 BMCOLOR_MAP.put("Eggshell","Medium White");
		 BMCOLOR_MAP.put("Cerise","Other");
		 BMCOLOR_MAP.put("Lime","Light Green");
		 BMCOLOR_MAP.put("Pink","Medium Pink");
		 BMCOLOR_MAP.put("Dark Green","Dark Green");
		 BMCOLOR_MAP.put("Light Blue","Light Blue");
		 BMCOLOR_MAP.put("Pansy Purple","Medium Purple");
		 BMCOLOR_MAP.put("Champagne","Medium Brown");
		 BMCOLOR_MAP.put("Tropical Green (Kelly)","Medium Green");
		 BMCOLOR_MAP.put("Gray","Medium Gray");
		 BMCOLOR_MAP.put("Teal Blue","Medium Blue");
		 BMCOLOR_MAP.put("Buff","Light Brown");
		 BMCOLOR_MAP.put("Bright Blue","Bright Blue");
		 BMCOLOR_MAP.put("Forest","Medium Green");
		 BMCOLOR_MAP.put("Brick Red","Medium Red");
		 BMCOLOR_MAP.put("Dubonnet","Other");
		 BMCOLOR_MAP.put("Royal","Medium Blue");
		 BMCOLOR_MAP.put("Navy","Dark Blue");
		 BMCOLOR_MAP.put("Hunter","Dark Green");
		 BMCOLOR_MAP.put("Bright Pink","Bright Pink");


	 }
	 public static String getColorGroup(String colorName){
			return BMCOLOR_MAP.get(colorName.toUpperCase());
		}
}
