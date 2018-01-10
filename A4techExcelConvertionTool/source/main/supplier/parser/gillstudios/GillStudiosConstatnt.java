package parser.gillstudios;
import java.util.Map;
import java.util.TreeMap;

public class GillStudiosConstatnt {

	public static Map<String, String> GCOLOR_MAP =new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
	
	
	
	static{
		GCOLOR_MAP.put("NAME","COLOR GROUP");
		GCOLOR_MAP.put("WHITE","MEDIUM WHITE");
		GCOLOR_MAP.put("CLEAR","CLEAR");
		GCOLOR_MAP.put("WHITE GLOSS","MEDIUM WHITE");
		GCOLOR_MAP.put("YELLOW GLOSS","MEDIUM YELLOW");
		GCOLOR_MAP.put("WHITE MATTE","MEDIUM WHITE");
		GCOLOR_MAP.put("RED FLUORESCENT","BRIGHT RED");
		GCOLOR_MAP.put("YELLOW FLUORESCENT","MEDIUM YELLOW");
		GCOLOR_MAP.put("GREEN FLUORESCENT","BRIGHT GREEN");
		GCOLOR_MAP.put("LIGHT ORANGE FLUORESCENT","LIGHT ORANGE");
		GCOLOR_MAP.put("PINK FLUORESCENT","BRIGHT PINK");
		GCOLOR_MAP.put("SHINY GOLD","GOLD METAL");
		GCOLOR_MAP.put("SHINY SILVER","SILVER METAL");
		GCOLOR_MAP.put("MATTE GOLD","GOLD METAL");
		GCOLOR_MAP.put("MATTE SILVER","SILVER METAL");
		GCOLOR_MAP.put("FROSTY CLEAR","CLEAR");
		GCOLOR_MAP.put("MULTI COLOR","MULTI COLOR");
		GCOLOR_MAP.put("BLACK","MEDIUM BLACK");
		GCOLOR_MAP.put("GOLD","GOLD METAL");
		GCOLOR_MAP.put("BRIGHT WHITE","BRIGHT WHITE");
		GCOLOR_MAP.put("YELLOW","MEDIUM YELLOW");
		GCOLOR_MAP.put("CHROME","CHROME METAL");
		GCOLOR_MAP.put("BRUSHED CHROME","CHROME METAL");
		GCOLOR_MAP.put("BRUSHED GOLD","GOLD METAL");
		GCOLOR_MAP.put("BUFF","LIGHT BROWN");
		GCOLOR_MAP.put("NATURAL","MEDIUM WHITE");
		GCOLOR_MAP.put("RED","MEDIUM RED");
		GCOLOR_MAP.put("BLUE","MEDIUM BLUE");
		GCOLOR_MAP.put("GREEN","MEDIUM GREEN");
		GCOLOR_MAP.put("METALLIC GOLD","GOLD METAL");
		GCOLOR_MAP.put("METALLIC SILVER","SILVER METAL");
		GCOLOR_MAP.put("CARMINE RED","MEDIUM RED");
		GCOLOR_MAP.put("TEAL","MEDIUM GREEN");
		GCOLOR_MAP.put("PEACOCK BLUE","MEDIUM BLUE");
		GCOLOR_MAP.put("ULTRA BLUE","MEDIUM BLUE");
		GCOLOR_MAP.put("PURPLE","MEDIUM PURPLE");
		GCOLOR_MAP.put("MIRROR FINISH CHROME","CHROME METAL");
		GCOLOR_MAP.put("MIRROR FINISH GOLD","GOLD METAL");
		GCOLOR_MAP.put("MIRROR CHROME","CHROME METAL");
		GCOLOR_MAP.put("MIRROR GOLD","GOLD METAL");
		GCOLOR_MAP.put("BURGUNDY","DARK RED");
		GCOLOR_MAP.put("MAGENTA","BRIGHT PINK");
		GCOLOR_MAP.put("ORANGE","MEDIUM ORANGE");
		GCOLOR_MAP.put("BEIGE","LIGHT BROWN");
		GCOLOR_MAP.put("LIME GREEN","LIGHT GREEN");
		GCOLOR_MAP.put("KELLY GREEN","BRIGHT GREEN");
		GCOLOR_MAP.put("HUNTER GREEN","DARK GREEN");
		GCOLOR_MAP.put("BLUEBERRY","MEDIUM BLUE");
		GCOLOR_MAP.put("ROYAL BLUE","MEDIUM BLUE");
		GCOLOR_MAP.put("NAVY BLUE","DARK BLUE");
		GCOLOR_MAP.put("IVORY","MEDIUM WHITE");
		GCOLOR_MAP.put("CHARCOAL","MEDIUM GRAY");
		GCOLOR_MAP.put("GRAY","MEDIUM GRAY");
		GCOLOR_MAP.put("NAVY","DARK BLUE");
		GCOLOR_MAP.put("FOREST GREEN","MEDIUM GREEN");
		GCOLOR_MAP.put("BLACK/BROWN BOX","BLACK:COMBO:BROWN");
		GCOLOR_MAP.put("BLACK/SILVER TIN","BLACK:COMBO:SILVER");
		GCOLOR_MAP.put("NATURAL STONE COASTER/BROWN BOX","GRAY:COMBO:BROWN");
		GCOLOR_MAP.put("BROWN/BROWN BOX","MEDIUM BROWN");
		GCOLOR_MAP.put("WHITE/BROWN BOX","WHITE:COMBO:BROWN");
		GCOLOR_MAP.put("NATURAL BEIGE/BROWN BOX","LIGHT BROWN:COMBO:BROWN");
		GCOLOR_MAP.put("NATURAL STONE/BROWN BOX","GRAY:COMBO:BROWN");
		GCOLOR_MAP.put("NATURAL STONE","MEDIUM GRAY");
		GCOLOR_MAP.put("BLACK/GRAY","BLACK:COMBO:GRAY");
		GCOLOR_MAP.put("WHITE/ASSORTED","WHITE:COMBO:ASSORTED");

		
	}
	public static String getColorGroup(String colorName){
		return GCOLOR_MAP.get(colorName.toUpperCase());
	}
    /*static {
    	GCOLOR_MAP.put("Name","Color Group");
    	GCOLOR_MAP.put("White","Medium White");
    	GCOLOR_MAP.put("Clear","Clear");
    	GCOLOR_MAP.put("White Gloss","Medium White");
    	GCOLOR_MAP.put("Yellow Gloss","Medium Yellow");
    	GCOLOR_MAP.put("White Matte","Medium White");
    	GCOLOR_MAP.put("Red Fluorescent","Bright Red");
    	GCOLOR_MAP.put("Yellow Fluorescent","Medium Yellow");
    	GCOLOR_MAP.put("Green Fluorescent","Bright Green");
    	GCOLOR_MAP.put("Light Orange Fluorescent","Light Orange");
    	GCOLOR_MAP.put("Pink Fluorescent","Bright Pink");
    	GCOLOR_MAP.put("Shiny Gold","Gold Metal");
    	GCOLOR_MAP.put("Shiny Silver","Silver Metal");
    	GCOLOR_MAP.put("Matte Gold","Gold Metal");
    	GCOLOR_MAP.put("Matte Silver","Silver Metal");
    	GCOLOR_MAP.put("Frosty Clear","Clear");
    	GCOLOR_MAP.put("Multi color","Multi Color");
    	GCOLOR_MAP.put("Black","Medium Black");
    	GCOLOR_MAP.put("Gold","Gold Metal");
    	GCOLOR_MAP.put("Bright White","Bright White");
    	GCOLOR_MAP.put("Yellow","Medium Yellow");
    	GCOLOR_MAP.put("Chrome","Chrome Metal");
    	GCOLOR_MAP.put("Brushed Chrome","Chrome Metal");
    	GCOLOR_MAP.put("Brushed Gold","Gold Metal");
    	GCOLOR_MAP.put("Buff","Light Brown");
    	GCOLOR_MAP.put("Natural","Medium White");
    	GCOLOR_MAP.put("Red","Medium Red");
    	GCOLOR_MAP.put("Blue","Medium Blue");
    	GCOLOR_MAP.put("Green","Medium Green");
    	GCOLOR_MAP.put("Metallic Gold","Gold Metal");
    	GCOLOR_MAP.put("Metallic Silver","Silver Metal");
    	GCOLOR_MAP.put("Carmine Red","Medium Red");
    	GCOLOR_MAP.put("Teal","Medium Green");
    	GCOLOR_MAP.put("Peacock Blue","Medium Blue");
    	GCOLOR_MAP.put("Ultra Blue","Medium Blue");
    	GCOLOR_MAP.put("Purple","Medium Purple");
    	GCOLOR_MAP.put("Mirror Finish Chrome","Chrome Metal");
    	GCOLOR_MAP.put("Mirror Finish Gold","Gold Metal");
    	GCOLOR_MAP.put("Mirror Chrome","Chrome Metal");
    	GCOLOR_MAP.put("Mirror Gold","Gold Metal");
    	GCOLOR_MAP.put("Burgundy","Dark Red");
    	GCOLOR_MAP.put("Magenta","Bright Pink");
    	GCOLOR_MAP.put("Orange","Medium Orange");
    	GCOLOR_MAP.put("Beige","Light Brown");
    	GCOLOR_MAP.put("Lime Green","Light Green");
    	GCOLOR_MAP.put("Kelly Green","Bright Green");
    	GCOLOR_MAP.put("Hunter Green","Dark Green");
    	GCOLOR_MAP.put("Blueberry","Medium Blue");
    	GCOLOR_MAP.put("Royal Blue","Medium Blue");
    	GCOLOR_MAP.put("Navy Blue","Dark Blue");
    	GCOLOR_MAP.put("Ivory","Medium White");
    	GCOLOR_MAP.put("Charcoal","Medium Gray");
    	GCOLOR_MAP.put("Gray","Medium Gray");
    	GCOLOR_MAP.put("Navy","Dark Blue");
    	GCOLOR_MAP.put("Forest Green","Medium Green");
    	GCOLOR_MAP.put("Black/Brown Box","Black:Combo:Brown");
    	GCOLOR_MAP.put("Black/Silver Tin","Black:Combo:Silver");
    	GCOLOR_MAP.put("Natural Stone Coaster/Brown Box","Gray:Combo:Brown");
    	GCOLOR_MAP.put("Brown/Brown Box","Medium Brown");
    	GCOLOR_MAP.put("White/Brown Box","White:Combo:Brown");
    	GCOLOR_MAP.put("Natural Beige/Brown Box","Light Brown:Combo:Brown");
    	GCOLOR_MAP.put("Natural Stone/Brown Box","Gray:Combo:Brown");
    	GCOLOR_MAP.put("Natural Stone","Medium Gray");
    	GCOLOR_MAP.put("Black/Gray","Black:Combo:Gray");
    	GCOLOR_MAP.put("White/Assorted","White:Combo:Assorted");
		
	}*/

    

	
}
