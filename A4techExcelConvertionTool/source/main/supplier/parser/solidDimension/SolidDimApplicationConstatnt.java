package parser.solidDimension;

import java.util.Map;
import java.util.TreeMap;

public class SolidDimApplicationConstatnt {
	
	public static Map<String, String> COLOR_MAP =new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
    static {
	COLOR_MAP.put("NAME","COLOR GROUP");
	COLOR_MAP.put("TRANSLUCENT BLUE","CLEAR BLUE");
	COLOR_MAP.put("BLACK","MEDIUM BLACK");
	COLOR_MAP.put("MAPLE","LIGHT BROWN");
	COLOR_MAP.put("HONEY MAPLE","MEDIUM BROWN");
	COLOR_MAP.put("NATURAL CHERRY","MEDIUM RED");
	COLOR_MAP.put("ROSEWOOD","MEDIUM RED");
	COLOR_MAP.put("DARK RED MAHOGANY","DARK RED");
	COLOR_MAP.put("WALNUT","MEDIUM BROWN");
	COLOR_MAP.put("DARK WALNUT","DARK BROWN");
	COLOR_MAP.put("BLACK STAIN","MEDIUM BLACK");
	COLOR_MAP.put("BLACK LACQUER","MEDIUM BLACK");
	COLOR_MAP.put("AGED CHERRY","MEDIUM RED");
	COLOR_MAP.put("BLACK WALNUT","MULTI COLOR");
	COLOR_MAP.put("OAK","LIGHT BROWN");
	COLOR_MAP.put("BRASS","BRASS METAL");
	COLOR_MAP.put("SILVER","SILVER METAL");
	COLOR_MAP.put("CLEAR","CLEAR");
	COLOR_MAP.put("JADE","MEDIUM GREEN");
	COLOR_MAP.put("ALDER","MEDIUM BROWN");
	COLOR_MAP.put("GRAY","MEDIUM GRAY");
	COLOR_MAP.put("TRANSLUCENT RED","CLEAR RED");
	COLOR_MAP.put("TRANSLUCENT BLACK","CLEAR BLACK");
	COLOR_MAP.put("TRANSLUCENT YELLOW","CLEAR YELLOW");
	COLOR_MAP.put("TRANSLUCENT CLEAR","CLEAR");
	COLOR_MAP.put("BLUE","MEDIUM BLUE");
	COLOR_MAP.put("RED","MEDIUM RED");
	COLOR_MAP.put("WHITE","MEDIUM WHITE");
	COLOR_MAP.put("GOLD","GOLD METAL");
	COLOR_MAP.put("BRONZE","BRONZE METAL");
	COLOR_MAP.put("BIRCH","LIGHT BROWN");
	COLOR_MAP.put("MAPLE-WALNUT","MULTI COLOR");
	COLOR_MAP.put("LIGHT BROWN","LIGHT BROWN");
	COLOR_MAP.put("BAMBOO","LIGHT BROWN");
	COLOR_MAP.put("BURGUNDY","DARK RED");
	COLOR_MAP.put("BLACK SATIN","MEDIUM BLACK");
	COLOR_MAP.put("CHERRY","MEDIUM RED");
	COLOR_MAP.put("WHITE LACQUER","MEDIUM WHITE");
    }
    
    public static String getColorGroup(String colorName){
    	colorName=colorName.trim().toUpperCase();
    	String colorGroup = COLOR_MAP.get(colorName);
    	colorGroup = colorGroup == null?"Other":colorGroup;
    	return colorGroup;
    }
}
