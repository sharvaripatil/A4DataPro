package parser.SportAzxCanada;

import java.util.Map;
import java.util.TreeMap;

public class SportsCanadaApplicationConstatnt {
	public static Map<String, String> COLOR_MAP =new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
    static {
    	COLOR_MAP.put("NAME","COLOR GROUP");
    	COLOR_MAP.put("WHITE","MEDIUM WHITE");
    	COLOR_MAP.put("NAVY BLUE","DARK BLUE");
    	COLOR_MAP.put("ROYAL BLUE","BRIGHT BLUE");
    	COLOR_MAP.put("RED","MEDIUM RED");
    	COLOR_MAP.put("PURPLE","MEDIUM PURPLE");
    	COLOR_MAP.put("FOREST GREEN","DARK GREEN");
    	COLOR_MAP.put("BLACK","MEDIUM BLACK");
    	COLOR_MAP.put("GOLD","DARK YELLOW");
    	COLOR_MAP.put("COPPER","COPPER METAL");
    	COLOR_MAP.put("BABY BLUE","LIGHT BLUE");
    	COLOR_MAP.put("ORANGE","MEDIUM ORANGE");
    	COLOR_MAP.put("KELLY GREEN","BRIGHT GREEN");
    	COLOR_MAP.put("ATHLETIC GOLD","GOLD METAL");
    	COLOR_MAP.put("BURGUNDY RED","DARK RED");
    	COLOR_MAP.put("ASSORTED","ASSORTED");
    	COLOR_MAP.put("LIGHT GRAY","LIGHT GRAY");
    	COLOR_MAP.put("LIGHT PINK","LIGHT PINK");
    	COLOR_MAP.put("YELLOW","MEDIUM YELLOW");
    	COLOR_MAP.put("DARK GRAY","DARK GRAY");
    	COLOR_MAP.put("SILVER","SILVER METAL");
    	COLOR_MAP.put("CLEAR","CLEAR");
    	COLOR_MAP.put("FROSTED WHITE","MEDIUM WHITE");
    	COLOR_MAP.put("GREEN","MEDIUM GREEN");
    	COLOR_MAP.put("PINK","MEDIUM PINK");
    	COLOR_MAP.put("ALUMINUM SILVER","SILVER METAL");
    	COLOR_MAP.put("PLATINUM COOL GRAY","PLATINUM METAL");
    	COLOR_MAP.put("RASPBERRY RED","DARK RED");
    	COLOR_MAP.put("TRANSLUCENT PURPLE","CLEAR PURPLE");
    	COLOR_MAP.put("TRANSLUCENT RED","BRIGHT RED");
    	COLOR_MAP.put("TRANSLUCENT GREEN","BRIGHT GREEN");
    	COLOR_MAP.put("TRANSLUCENT BLUE","BRIGHT BLUE");
    	COLOR_MAP.put("FROSTED CLEAR","CLEAR");
    	COLOR_MAP.put("BLUE","MEDIUM BLUE");
    	COLOR_MAP.put("TRANSLUCENT","CLEAR");
    	COLOR_MAP.put("METALLIC GOLD","GOLD METAL");
    	COLOR_MAP.put("METALLIC SILVER","SILVER METAL");
    	COLOR_MAP.put("NEON GREEN","BRIGHT GREEN");
    	COLOR_MAP.put("NEON PINK","BRIGHT PINK");
    	COLOR_MAP.put("AQUA","LIGHT BLUE");
    	COLOR_MAP.put("FROSTED","CLEAR");
    	COLOR_MAP.put("BROWN","MEDIUM BROWN");
    	COLOR_MAP.put("ROYAL","MEDIUM BLUE");
    	COLOR_MAP.put("NAVY","DARK BLUE");
    	COLOR_MAP.put("GRAY","MEDIUM GRAY");
    	COLOR_MAP.put("CAP BLACK","MEDIUM BLACK");
    	COLOR_MAP.put("BLACK LANYARD","MEDIUM BLACK");
    	COLOR_MAP.put("FROSTED WHITE-FROSTED WHITE","MEDIUM WHITE");
    	COLOR_MAP.put("FROSTED WHITE-FROSTED","MEDIUM WHITE");
    	COLOR_MAP.put("LIGHT BLUE","LIGHT BLUE");
    	COLOR_MAP.put("LIGHT GREEN","LIGHT GREEN");
    	COLOR_MAP.put("GRAY","MEDIUM GRAY");
    	COLOR_MAP.put("LIGHT YELLOW","LIGHT YELLOW");
    	COLOR_MAP.put("RED","MEDIUM RED");
    	COLOR_MAP.put("BLUE","MEDIUM BLUE");
    	COLOR_MAP.put("GREEN","MEDIUM GREEN");
    	COLOR_MAP.put("YELLOW","MEDIUM YELLOW");
    	COLOR_MAP.put("ORANGE","MEDIUM ORANGE");
    	COLOR_MAP.put("BURGUNDY","DARK RED");

  }
    
    public static String getColorGroup(String colorName){
    	colorName=colorName.trim().toUpperCase();
    	String colorGroup = COLOR_MAP.get(colorName);
    	colorGroup = colorGroup == null?"Other":colorGroup;
    	return colorGroup;
    }
}
