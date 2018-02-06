package parser.sunGraphix;

import java.util.Map;
import java.util.TreeMap;

public class SunGraphixConstants {
	public static Map<String, String> SUNCOLOR_MAP =new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);//new HashMap<String, String>();
	public static Map<String, StringBuilder> SUNDISCOUNTCODE_MAP =new TreeMap<String, StringBuilder>(String.CASE_INSENSITIVE_ORDER);//new HashMap<String, String>();
	
	static StringBuilder dsic1 = new StringBuilder();
	static StringBuilder dsic2 = new StringBuilder();
	static StringBuilder dsic3 = new StringBuilder();
	static StringBuilder dsic4 = new StringBuilder();
	static StringBuilder dsic5 = new StringBuilder();
	static StringBuilder dsic6 = new StringBuilder();
	static StringBuilder dsic7 = new StringBuilder();
	//static StringBuilder dsic6 = new StringBuilder();
	static {
		dsic1.append("C___C___C___C___C___C___C___C___C___C");
		dsic2.append("C___C___C___C___C");
		dsic3.append("A___A___B___B___C");
		dsic4.append("A___A___A___B___B___C");
		dsic5.append("A___A___A___A___A___A___A___A___A___A");
		dsic6.append("G___G___G___G___G___G___G___G___G___G");
		SUNDISCOUNTCODE_MAP.put("CCCCC",dsic1);
		SUNDISCOUNTCODE_MAP.put("CCCCCC",dsic2);
		SUNDISCOUNTCODE_MAP.put("CCCC",dsic1);
		SUNDISCOUNTCODE_MAP.put("AABBC",dsic3);
		SUNDISCOUNTCODE_MAP.put("AAABBC",dsic4);
		SUNDISCOUNTCODE_MAP.put("AAAAAA",dsic5);
		SUNDISCOUNTCODE_MAP.put("G",dsic6);
		SUNDISCOUNTCODE_MAP.put("A",dsic5);
		SUNDISCOUNTCODE_MAP.put("C",dsic1);
		SUNCOLOR_MAP.put("NAME","COLOR GROUP");
		SUNCOLOR_MAP.put("BLACK","MEDIUM BLACK");
		SUNCOLOR_MAP.put("DARK BLACK","DARK BLACK");
		SUNCOLOR_MAP.put("NAVY BLUE","DARK BLUE");
		SUNCOLOR_MAP.put("BURGUNDY","DARK RED");
		SUNCOLOR_MAP.put("RED","MEDIUM RED");
		SUNCOLOR_MAP.put("GREEN","MEDIUM GREEN");
		SUNCOLOR_MAP.put("WHITE","MEDIUM WHITE");
		SUNCOLOR_MAP.put("BLUE","MEDIUM BLUE");
		SUNCOLOR_MAP.put("TAN","LIGHT BROWN");
		SUNCOLOR_MAP.put("RASPBERRY","BRIGHT RED");
		SUNCOLOR_MAP.put("GRAY","DARK GRAY");
		SUNCOLOR_MAP.put("YELLOW","MEDIUM YELLOW");
		SUNCOLOR_MAP.put("PURPLE","MEDIUM PURPLE");
		SUNCOLOR_MAP.put("CHROME MATTE","MEDIUM GRAY");
		SUNCOLOR_MAP.put("IMPERIAL BLUE","MEDIUM BLUE");
		SUNCOLOR_MAP.put("CHROME","MEDIUM GRAY");
		SUNCOLOR_MAP.put("MATTE BLACK","MEDIUM BLACK");
		SUNCOLOR_MAP.put("NICKEL","MEDIUM GRAY");
		SUNCOLOR_MAP.put("GRAPHITE","MEDIUM GRAY");
		SUNCOLOR_MAP.put("PINK","MEDIUM PINK");
		SUNCOLOR_MAP.put("CHARCOAL","MEDIUM GRAY");
		SUNCOLOR_MAP.put("OCEANBLUE","MEDIUM BLUE");
		SUNCOLOR_MAP.put("BLUEGREEN","MEDIUM GREEN");
		SUNCOLOR_MAP.put("ORANGE","MEDIUM ORANGE");
		SUNCOLOR_MAP.put("FUSCHIA","BRIGHT RED");
		SUNCOLOR_MAP.put("AQUA","MEDIUM BLUE");
		SUNCOLOR_MAP.put("PEAR","BRIGHT GREEN");
		SUNCOLOR_MAP.put("RETRO BROWN","MEDIUM BROWN");
		SUNCOLOR_MAP.put("DARK AQUA","DARK BLUE");
		SUNCOLOR_MAP.put("DUCK EGG BLUE","MEDIUM BLUE");
		SUNCOLOR_MAP.put("NUDE","MEDIUM WHITE");

	}
	public static String getColorGroup(String colorName){
		return SUNCOLOR_MAP.get(colorName.toUpperCase());
	}
}
