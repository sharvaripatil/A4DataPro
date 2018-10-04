package parser.AccessLine;

import java.util.Map;
import java.util.TreeMap;

public class AccessLineConstants {
	public static Map<String, String> ACESSCOLOR_MAP =new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);//new HashMap<String, String>();
	public static Map<String, StringBuilder> ACDISCOUNTCODE_MAP =new TreeMap<String, StringBuilder>(String.CASE_INSENSITIVE_ORDER);//new HashMap<String, String>();
	static StringBuilder dsic1 = new StringBuilder();
	static StringBuilder dsic2 = new StringBuilder();
	static StringBuilder dsic3 = new StringBuilder();
	static StringBuilder dsic4 = new StringBuilder();
	static StringBuilder dsic5 = new StringBuilder();
	static StringBuilder dsic6 = new StringBuilder();
	static StringBuilder dsic7 = new StringBuilder();
	static StringBuilder dsic8 = new StringBuilder();
	static StringBuilder dsic9 = new StringBuilder();
	
	//sta
	static {
		dsic1.append("C___C___C___C___D");
		dsic2.append("C___C___C___C___C");
		dsic2.append("R___R___R___R___R");
		dsic3.append("C___C___C___C___C");
		dsic4.append("C___C___C___C___C");
		dsic5.append("C___C___C___C___C");
		dsic6.append("A___A___B___C___C");
		dsic7.append("A___A___A___B___C");
		dsic8.append("G___G___G___G___G");
		
		ACDISCOUNTCODE_MAP.put("5R",dsic9);
		ACDISCOUNTCODE_MAP.put("4CD",dsic1);
		ACDISCOUNTCODE_MAP.put("5C",dsic2);
		ACDISCOUNTCODE_MAP.put("4C",dsic3);
		ACDISCOUNTCODE_MAP.put("C",dsic4);
		ACDISCOUNTCODE_MAP.put("3C",dsic5);
		ACDISCOUNTCODE_MAP.put("2ABC",dsic6);
		ACDISCOUNTCODE_MAP.put("3ABC",dsic7);
		ACDISCOUNTCODE_MAP.put("G",dsic8);
		/*PRIMECOLOR_MAP.put("Name","Color Group");
		PRIMECOLOR_MAP.put("Black","Medium Black");
		PRIMECOLOR_MAP.put("Dark Brown","Dark Brown");
		PRIMECOLOR_MAP.put("Red","Medium Red");
		PRIMECOLOR_MAP.put("Navy","Dark Blue");
		PRIMECOLOR_MAP.put("British Tan","Medium Brown");
		PRIMECOLOR_MAP.put("Brown","Medium Brown");
		PRIMECOLOR_MAP.put("Tan","Light Brown");*/
	}
}
