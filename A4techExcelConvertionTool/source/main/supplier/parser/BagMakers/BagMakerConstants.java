package parser.BagMakers;

import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import parser.tomaxusa.TomaxConstants;

public class BagMakerConstants {
	private static Logger _LOGGER = Logger.getLogger(TomaxConstants.class);
	public static Map<String, String> BMCOLOR_MAP =new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);//new HashMap<String, String>();
	 static {
		 BMCOLOR_MAP.put("NAME","Color Group");
		 BMCOLOR_MAP.put("RED","Medium Red");
		 BMCOLOR_MAP.put("BLACK","Medium Black");
		 BMCOLOR_MAP.put("ROYAL BLUE","Bright Blue");
		 BMCOLOR_MAP.put("WHITE","Medium White");
		 BMCOLOR_MAP.put("BURGUNDY","Dark Red");
		 BMCOLOR_MAP.put("HUNTER GREEN","Medium Green");
		 BMCOLOR_MAP.put("KHAKI","Light Brown");
		 BMCOLOR_MAP.put("NAVY BLUE","Dark Blue");
		 BMCOLOR_MAP.put("TEAL","Medium Green");
		 BMCOLOR_MAP.put("BROWN KRAFT","Medium Brown");
		 BMCOLOR_MAP.put("BROWN","Medium Brown");
		 BMCOLOR_MAP.put("BRITE BLUE","Bright Blue");
		 BMCOLOR_MAP.put("LIME GREEN","Bright Green");
		 BMCOLOR_MAP.put("BRITE PINK","Bright Pink");
		 BMCOLOR_MAP.put("GRAPE","Medium Purple");
		 BMCOLOR_MAP.put("CLEAR","Clear");
		 BMCOLOR_MAP.put("MULTI COLOR","Multi Color");
		 BMCOLOR_MAP.put("CUSTOM","Other");
		 BMCOLOR_MAP.put("GREEN","Medium Green");
		 BMCOLOR_MAP.put("ORANGE","Medium Orange");
		 BMCOLOR_MAP.put("SILVER","Silver Metal");
		 BMCOLOR_MAP.put("BLUE","Medium Blue");
		 BMCOLOR_MAP.put("LAVENDER","Medium Purple");
		 BMCOLOR_MAP.put("TANGERINE","Medium Orange");
		 BMCOLOR_MAP.put("YELLOW","Medium Yellow");
		 BMCOLOR_MAP.put("BUTTERCUP YELLOW","Medium Yellow");
		 BMCOLOR_MAP.put("SCARLET RED","Medium Red");
		 BMCOLOR_MAP.put("PURPLE","Medium Purple");
		 BMCOLOR_MAP.put("MIDNIGHT BLUE","Dark Blue");
		 BMCOLOR_MAP.put("PARADE BLUE","Medium Blue");
		 BMCOLOR_MAP.put("HOLIDAY GREEN","Medium Green");
		 BMCOLOR_MAP.put("CLARET","Medium Red");
		 BMCOLOR_MAP.put("FRENCH VANILLA","Medium White");
		 BMCOLOR_MAP.put("FIESTA BLUE","Medium Blue");
		 BMCOLOR_MAP.put("CITRUS GREEN","Medium Green");
		 BMCOLOR_MAP.put("GOLD","Dark Yellow");
		 BMCOLOR_MAP.put("NATURAL","Medium White");
		 BMCOLOR_MAP.put("FROSTED","Medium White");
		 BMCOLOR_MAP.put("FROSTED CLEAR","Clear");
		 BMCOLOR_MAP.put("EGGSHELL","Medium White");
		 BMCOLOR_MAP.put("CERISE","Other");
		 BMCOLOR_MAP.put("LIME","Light Green");
		 BMCOLOR_MAP.put("PINK","Medium Pink");
		 BMCOLOR_MAP.put("DARK GREEN","Dark Green");
		 BMCOLOR_MAP.put("LIGHT BLUE","Light Blue");
		 BMCOLOR_MAP.put("PANSY PURPLE","Medium Purple");
		 BMCOLOR_MAP.put("CHAMPAGNE","Medium Brown");
		 BMCOLOR_MAP.put("TROPICAL GREEN (KELLY)","Medium Green");
		 BMCOLOR_MAP.put("GRAY","Medium Gray");
		 BMCOLOR_MAP.put("TEAL BLUE","Medium Blue");
		 BMCOLOR_MAP.put("BUFF","Light Brown");
		 BMCOLOR_MAP.put("BRIGHT BLUE","Bright Blue");
		 BMCOLOR_MAP.put("FOREST","Medium Green");
		 BMCOLOR_MAP.put("BRICK RED","Medium Red");
		 BMCOLOR_MAP.put("DUBONNET","Other");
		 BMCOLOR_MAP.put("ROYAL","Medium Blue");
		 BMCOLOR_MAP.put("NAVY","Dark Blue");
		 BMCOLOR_MAP.put("HUNTER","Dark Green");
		 BMCOLOR_MAP.put("BRIGHT PINK","Bright Pink");
	 }
	 public static String getColorGroup(String colorName){
			return BMCOLOR_MAP.get(colorName.toUpperCase());
		}
}
