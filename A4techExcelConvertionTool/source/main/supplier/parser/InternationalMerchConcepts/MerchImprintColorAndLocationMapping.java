package parser.InternationalMerchConcepts;

import java.util.HashMap;
import java.util.Map;

public class MerchImprintColorAndLocationMapping {
public static Map<String, String> IMPRINT_COLOR_LOCATION_MAP =new HashMap<String, String>();
public static Map<String, String> PACKAGING_MAP =new HashMap<String, String>();
	
	static{
		IMPRINT_COLOR_LOCATION_MAP.put("Laser in matte Silver","matte Silver");
		IMPRINT_COLOR_LOCATION_MAP.put("Laser in matte silver, Screen","matte Silver");
		IMPRINT_COLOR_LOCATION_MAP.put("Laser in Matte Silver, Screen","matte Silver");
		IMPRINT_COLOR_LOCATION_MAP.put("Lasers in Gold, Screen","Gold");
		IMPRINT_COLOR_LOCATION_MAP.put("Screen, Lasers in Gold","Gold");
		IMPRINT_COLOR_LOCATION_MAP.put("Lasers in gold, Screen","Gold");
		IMPRINT_COLOR_LOCATION_MAP.put("Screen (on handle)","on handle");
		IMPRINT_COLOR_LOCATION_MAP.put("Screen (on pouch)","on pouch");
		IMPRINT_COLOR_LOCATION_MAP.put("Laser, Screen (one location pen or box)","Pen,Box");
		IMPRINT_COLOR_LOCATION_MAP.put("Pen: Laser, Screen. Laser on engraving plate also available. Setup & run charges apply.","Pen,Engraving Plate");
		IMPRINT_COLOR_LOCATION_MAP.put("Pen: Laser, Screen  Keychain: Laser","Pen,Keychain");
		IMPRINT_COLOR_LOCATION_MAP.put("Laser on clip or engraving plate, screen on gift box. Setup & run charges apply.","clip,engraving plate,gift box");
		IMPRINT_COLOR_LOCATION_MAP.put("Laser on Base included, Sandblast Etch on Globe (additional charges apply)","Base,Globe");
		PACKAGING_MAP.put("Self-Adhesive Poly Bag", "Poly Bag");
		PACKAGING_MAP.put("Each with a Printed Paper Instruction Sleeve, Bulk Packed", "Bulk");
		PACKAGING_MAP.put("Poly Bag 6 each", "Poly Bag");
		PACKAGING_MAP.put("Bulk Packed", "Bulk");
			}
	public static String getImprintColorAndImprintGroup(String imprintMethodValue){
		return IMPRINT_COLOR_LOCATION_MAP.get(imprintMethodValue);
	}
	public static String getPackagingGroup(String packVal){
		return PACKAGING_MAP.get(packVal);
	}
}
