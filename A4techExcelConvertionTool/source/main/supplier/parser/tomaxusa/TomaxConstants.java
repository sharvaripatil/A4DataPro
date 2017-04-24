package parser.tomaxusa;

import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;


public class TomaxConstants {
	private static Logger _LOGGER = Logger.getLogger(TomaxConstants.class);
	public static Map<String, String> TCOLOR_MAP =new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);//new HashMap<String, String>();
	 static {
		 TCOLOR_MAP.put("BLACK","Medium Black");
		 TCOLOR_MAP.put("METALLIC BLACK","Metallic Black");
		 TCOLOR_MAP.put("BLUE","Medium Blue");
		 TCOLOR_MAP.put("METALLIC BLUE","Metallic Blue");
		 TCOLOR_MAP.put("LIGHT BLUE","Light Blue");
		 TCOLOR_MAP.put("BRIGHT BLUE","Bright Blue");
		 TCOLOR_MAP.put("GREEN","Medium Green");
		 TCOLOR_MAP.put("ORANGE","Medium Orange");
		 TCOLOR_MAP.put("METALLIC ORANGE","Metallic Orange");
		 TCOLOR_MAP.put("RED","Medium Red");
		 TCOLOR_MAP.put("METALLIC RED","Metallic Red");
		 TCOLOR_MAP.put("SILVER","Silver Metal");
		 TCOLOR_MAP.put("WHITE","Medium White");
		 TCOLOR_MAP.put("GOLD","Metallic Yellow");
		 TCOLOR_MAP.put("LIGHT BLUE","Light Blue");
		 TCOLOR_MAP.put("PINK","Medium Pink");
		 TCOLOR_MAP.put("YELLOW","Medium Yellow");
		 TCOLOR_MAP.put("PURPLE","Medium Purple");
		 TCOLOR_MAP.put("CHARCOAL","Medium Black");
		 TCOLOR_MAP.put("BROWN","Medium Brown");
		 TCOLOR_MAP.put("CLEAR","Clear");
		 TCOLOR_MAP.put("WHITE-SILVER","Multi Color");
		 TCOLOR_MAP.put("MULTI COLOR","Multi Color");
		 TCOLOR_MAP.put("GUN METAL","Medium Green");
		 TCOLOR_MAP.put("GUNMETAL","Medium Gray");
		 TCOLOR_MAP.put("ROYAL BLUE","Bright Blue");
		 TCOLOR_MAP.put("Titanium","Titanium Metal");
		 TCOLOR_MAP.put("ROSEGOLD","Medium Pink");
		 TCOLOR_MAP.put("MAPLE","Light Brown");
		 TCOLOR_MAP.put("ROSEWOOD","Medium Red");
		 TCOLOR_MAP.put("WALNUT","Medium Brown");
		 TCOLOR_MAP.put("MACARON BLUE","Medium Blue");
		 TCOLOR_MAP.put("GM","Gunmetal");
		 TCOLOR_MAP.put("PK","Pink");
		 TCOLOR_MAP.put("SL","Silver");
		 TCOLOR_MAP.put("KS","black/silver trim");
		 TCOLOR_MAP.put("KR","black/red trim");
		 TCOLOR_MAP.put("WB","white/blue trim");
		 TCOLOR_MAP.put("WS","white/silver trim");
		 TCOLOR_MAP.put("BK","black");
		 TCOLOR_MAP.put("BL","blue");
		 TCOLOR_MAP.put("GD","Gold");
		 TCOLOR_MAP.put("OR","Orange");
		 TCOLOR_MAP.put("RD","Red");
		 TCOLOR_MAP.put("SL","silver");
		 TCOLOR_MAP.put("YL","Yellow");
		 TCOLOR_MAP.put("TAN","Tan ");
		 TCOLOR_MAP.put("GN","Green");
		 TCOLOR_MAP.put("LBL","light blue");
		 TCOLOR_MAP.put("BR","brown");
		 TCOLOR_MAP.put("PU","purple");
		 TCOLOR_MAP.put("WT","white");
		 TCOLOR_MAP.put("TQ","teal/turquoise");

	 }
	 public static String getColorGroup(String colorName){
			return TCOLOR_MAP.get(colorName.toUpperCase());
		}
}
