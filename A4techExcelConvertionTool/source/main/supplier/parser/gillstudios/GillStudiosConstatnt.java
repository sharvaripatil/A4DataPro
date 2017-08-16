package parser.gillstudios;
import java.util.Map;
import java.util.TreeMap;

public class GillStudiosConstatnt {

	public static Map<String, String> GCOLOR_MAP =new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
	
    static {
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
		
	}

    public static String getColorGroup(String colorName){
		return GCOLOR_MAP.get(colorName.toUpperCase());
	}

	
}
