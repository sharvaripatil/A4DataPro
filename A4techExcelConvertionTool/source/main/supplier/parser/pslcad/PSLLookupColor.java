package parser.pslcad;
import java.util.Map;
import java.util.TreeMap;

public class PSLLookupColor {
	public static Map<String, String> COLOR_MAP =new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
	
	
	static{
	
		COLOR_MAP.put("Black","Medium Black");
		COLOR_MAP.put("Blue","Medium Blue");
		COLOR_MAP.put("Light Blue","Light Blue");
		COLOR_MAP.put("Orange","Medium Orange");
		COLOR_MAP.put("Bright Orange","Bright Orange");
		COLOR_MAP.put("White","Medium White");
		COLOR_MAP.put("Silver","Silver Metal");
		COLOR_MAP.put("Green","Medium Green");
		COLOR_MAP.put("Gunmetal","Medium Gray");
		COLOR_MAP.put("Gold","Dark Yellow");
		COLOR_MAP.put("Red","Medium Red");
		COLOR_MAP.put("Yellow","Medium Yellow");
		COLOR_MAP.put("Gray","Medium Gray");
		COLOR_MAP.put("Dark Gray","Dark Gray");
		COLOR_MAP.put("Lime Green","Light Green");
		COLOR_MAP.put("Pink","Medium Pink");
		COLOR_MAP.put("Bright Pink","Bright Pink");
		COLOR_MAP.put("Ice Blue","Light Blue");
		COLOR_MAP.put("Gun","Other");
		COLOR_MAP.put("Brown","Medium Brown");
		COLOR_MAP.put("Doctor","Other");
		COLOR_MAP.put("Nurse","Other");
		COLOR_MAP.put("Surgeon","Other");
		COLOR_MAP.put("Business Man","Other");
		COLOR_MAP.put("Hostess","Other");
		COLOR_MAP.put("Pilot","Other");
		COLOR_MAP.put("Construction","Other");
		COLOR_MAP.put("Express","Other");
		COLOR_MAP.put("Nice Guy","Other");
		COLOR_MAP.put("Santa","Other");
		COLOR_MAP.put("Assorted","Assorted");
		COLOR_MAP.put("Multi color","Multi Color");
		COLOR_MAP.put("Purple","Medium Purple");
		COLOR_MAP.put("Rose Gold","Medium Pink");
		COLOR_MAP.put("Gun Metal","Medium Gray");
		COLOR_MAP.put("Teal","Medium Green");
	
		
		
	}


	public static Map<String, String> getCOLOR_MAP() {
		return COLOR_MAP;
	}





	

	
}
