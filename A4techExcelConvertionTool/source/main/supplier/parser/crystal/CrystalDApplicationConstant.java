package parser.crystal;

import java.util.Map;
import java.util.TreeMap;

public class CrystalDApplicationConstant {
	
	public static Map<String, String> IMPRINT_METHOD_MAP =new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
	
	 static {
		 
		 IMPRINT_METHOD_MAP.put("UNIMPRINTED","UNIMPRINTED");
		 IMPRINT_METHOD_MAP.put("Deep Etch","ETCHED");
		 IMPRINT_METHOD_MAP.put("Illumachrome Plus™","FULL COLOR");
		 IMPRINT_METHOD_MAP.put("Illumachrome™","FULL COLOR");
		 IMPRINT_METHOD_MAP.put("Metal Lasering","LASER ENGRAVED");
		// IMPRINT_METHOD_MAP.put("Optional Colorfill","");//REMAIN
		 IMPRINT_METHOD_MAP.put("Wood Lasering","LASER ENGRAVED");
		 IMPRINT_METHOD_MAP.put("Sublimation","SUBLIMATION");
		 IMPRINT_METHOD_MAP.put("Subsurface Imaging","ETCHED");
		 IMPRINT_METHOD_MAP.put("3D subsurface","OTHER");
		 IMPRINT_METHOD_MAP.put("Lasering","LASER ENGRAVED");
		 
		 
	 }

}
