package parser.highcaliberline;

import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

public class HighCaliberConstants {
	public static Map<String, String> HCLCOLOR_MAP =new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);//new HashMap<String, String>();
	@SuppressWarnings("unused")
	private static Logger _LOGGER = Logger.getLogger(HighCaliberConstants.class);

    SessionFactory sessionFactory;
	//public static Map<String, String> COLOR_MAP =new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);//new HashMap<String, String>();
/*	public static Map<String, String>  Colormap(){
		
		Session session = null;
		
		try{
			 session = sessionFactory.openSession();
		Criteria colorCri = session.createCriteria(HighCalColorEntity.class);
		  List<HighCalColorEntity> colorList = colorCri.list();
	        for(HighCalColorEntity tempColor : colorList){
	        	if(StringUtils.isEmpty(tempColor.getColorvalue())){
	        		continue;
	        	}
	        	String strTemp[]={};
	        	strTemp= tempColor.getColorvalue().split("===");
	        	HCLCOLOR_MAP.put(strTemp[0].trim(),strTemp[1].trim());
	        }
	}catch(Exception ex){
		_LOGGER.error("Error in dao block for highcaliber colors: "+ex.getMessage());
	}finally{
		if(session !=null){
			try{
				session.close();
			}catch(Exception ex){
				_LOGGER.warn("Error while close session object in highcaliber color class");
			}
			}
		}
		
		return HCLCOLOR_MAP;
	
	}
	
	
	
	public static Map<String, String> getHCLCOLOR_MAP() {
		if(CollectionUtils.isEmpty(HCLCOLOR_MAP)){
			HCLCOLOR_MAP=Colormap();
		}
		
		
		return HCLCOLOR_MAP;
	}
	public static void setHCLCOLOR_MAP(Map<String, String> hCLCOLOR_MAP) {
		HCLCOLOR_MAP = hCLCOLOR_MAP;
	}*/
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
  static {
	  HCLCOLOR_MAP.put("NAME","COLOR GROUP");
	  HCLCOLOR_MAP.put("YELLOW","MEDIUM YELLOW");
	  HCLCOLOR_MAP.put("FOREST GREEN","MEDIUM GREEN");
	  HCLCOLOR_MAP.put("TEAL","MEDIUM GREEN");
	  HCLCOLOR_MAP.put("NAVY BLUE","DARK BLUE");
	  HCLCOLOR_MAP.put("REFLEX BLUE","MEDIUM BLUE");
	  HCLCOLOR_MAP.put("ROYAL BLUE","BRIGHT BLUE");
	  HCLCOLOR_MAP.put("BLUE","MEDIUM BLUE");
	  HCLCOLOR_MAP.put("BURGUNDY","DARK RED");
	  HCLCOLOR_MAP.put("PURPLE","MEDIUM PURPLE");
	  HCLCOLOR_MAP.put("BROWN","MEDIUM BROWN");
	  HCLCOLOR_MAP.put("GRAY","MEDIUM GRAY");
	  HCLCOLOR_MAP.put("BLACK","MEDIUM BLACK");
	  HCLCOLOR_MAP.put("WHITE","MEDIUM WHITE");
	  HCLCOLOR_MAP.put("GREEN","MEDIUM GREEN");
	  HCLCOLOR_MAP.put("RED","MEDIUM RED");
	  HCLCOLOR_MAP.put("ORANGE","MEDIUM ORANGE");
	  HCLCOLOR_MAP.put("LIGHT BLUE","LIGHT BLUE");
	  HCLCOLOR_MAP.put("CUSTOM","OTHER");
	  HCLCOLOR_MAP.put("PROCESS YELLOW","MEDIUM YELLOW");
	  HCLCOLOR_MAP.put("CUSTOM COLORS","OTHER");
	  HCLCOLOR_MAP.put("SILVER","MEDIUM GRAY");
	  HCLCOLOR_MAP.put("SOLID WHITE","MEDIUM WHITE");
	  HCLCOLOR_MAP.put("TRANSLUCENT BLACK","CLEAR BLACK");
	  HCLCOLOR_MAP.put("CLEAR","CLEAR");
	  HCLCOLOR_MAP.put("PINK","MEDIUM PINK");
	  HCLCOLOR_MAP.put("SOLID BLUE","MEDIUM BLUE");
	  HCLCOLOR_MAP.put("SOLID GREEN","MEDIUM GREEN");
	  HCLCOLOR_MAP.put("SOLID ORANGE","MEDIUM ORANGE");
	  HCLCOLOR_MAP.put("SOLID PURPLE","MEDIUM PURPLE");
	  HCLCOLOR_MAP.put("SOLID RED","MEDIUM RED");
	  HCLCOLOR_MAP.put("SOLID YELLOW","MEDIUM YELLOW");
	  HCLCOLOR_MAP.put("SOLID BLACK","MEDIUM BLACK");
	  HCLCOLOR_MAP.put("MATTE BLACK","MEDIUM BLACK");
	  HCLCOLOR_MAP.put("MATTE BLUE","MEDIUM BLUE");
	  HCLCOLOR_MAP.put("MATTE RED","MEDIUM RED");
	  HCLCOLOR_MAP.put("MATTE WHITE","MEDIUM WHITE");
	  HCLCOLOR_MAP.put("CHARCOAL","MEDIUM GRAY");
	  HCLCOLOR_MAP.put("FROSTED CLEAR","CLEAR");
	  HCLCOLOR_MAP.put("LIME","LIGHT GREEN");
	  HCLCOLOR_MAP.put("FUCHSIA","MEDIUM PINK");
	  HCLCOLOR_MAP.put("SMOKE","MEDIUM GRAY");
	  HCLCOLOR_MAP.put("MAROON","DARK RED");
	  HCLCOLOR_MAP.put("GOLD","DARK YELLOW");
	  HCLCOLOR_MAP.put("BRIGHT PINK","BRIGHT PINK");
	  HCLCOLOR_MAP.put("BRIGHT GREEN","BRIGHT GREEN");
	  HCLCOLOR_MAP.put("BRIGHT ORANGE","BRIGHT ORANGE");
	  HCLCOLOR_MAP.put("DEEP ROYAL","BRIGHT BLUE");
	  HCLCOLOR_MAP.put("ROYAL","BRIGHT BLUE");
	  HCLCOLOR_MAP.put("NAVY","DARK BLUE");
	  HCLCOLOR_MAP.put("ASSORTED","ASSORTED");
	  HCLCOLOR_MAP.put("TRANSLUCENT BLUE","CLEAR BLUE");
	  HCLCOLOR_MAP.put("TRANSLUCENT GREEN","CLEAR GREEN");
	  HCLCOLOR_MAP.put("TRANSLUCENT PINK","LIGHT PINK");
	  HCLCOLOR_MAP.put("TRANSLUCENT PURPLE","CLEAR PURPLE");
	  HCLCOLOR_MAP.put("TRANSLUCENT RED","CLEAR RED");
	  HCLCOLOR_MAP.put("METALLIC SILVER","METALLIC GRAY");
	  HCLCOLOR_MAP.put("CHROME","CHROME METAL");
	  HCLCOLOR_MAP.put("KHAKI","LIGHT BROWN");
	  HCLCOLOR_MAP.put("MULTI COLOR","MULTI COLOR");
	  HCLCOLOR_MAP.put("CORAL PINK","MEDIUM PINK");
	  HCLCOLOR_MAP.put("BLUE-BLACK","MEDIUM BLUE");
	  HCLCOLOR_MAP.put("TRANSLUCENT YELLOW","CLEAR YELLOW");
	  HCLCOLOR_MAP.put("TRANSLUCENT SMOKE","CLEAR BLACK");
	  HCLCOLOR_MAP.put("TRANSLUCENT ORANGE","CLEAR ORANGE");
	  HCLCOLOR_MAP.put("STAINLESS STEEL","MEDIUM GRAY");
	  HCLCOLOR_MAP.put("WHITE-BLACK","MULTI COLOR");
	  HCLCOLOR_MAP.put("WHITE-BLUE","MULTI COLOR");
	  HCLCOLOR_MAP.put("WHITE-RED","MULTI COLOR");
	  HCLCOLOR_MAP.put("CYAN BLUE","LIGHT BLUE");
	  HCLCOLOR_MAP.put("LIGHT GREEN","LIGHT GREEN");
	  HCLCOLOR_MAP.put("PLUM","MEDIUM PURPLE");
	  HCLCOLOR_MAP.put("NEON YELLOW","BRIGHT YELLOW");
	  HCLCOLOR_MAP.put("LIME GREEN","BRIGHT GREEN");
	  HCLCOLOR_MAP.put("DARK GREEN","DARK GREEN");
	  HCLCOLOR_MAP.put("FROSTED","CLEAR");
	  HCLCOLOR_MAP.put("TRANSPARENT BLUE","MEDIUM BLUE");
	  HCLCOLOR_MAP.put("TRANSPARENT BLACK","MEDIUM BLACK");
	  HCLCOLOR_MAP.put("PEARL-RED","MULTI COLOR");
	  HCLCOLOR_MAP.put("PEARL-GREEN","MEDIUM GREEN");
	  HCLCOLOR_MAP.put("PEARL-BLACK","MULTI COLOR");
	  HCLCOLOR_MAP.put("FUSHIA","LIGHT PURPLE");
	  HCLCOLOR_MAP.put("GREY","MEDIUM GRAY");
	  HCLCOLOR_MAP.put("SHINY BLACK","MEDIUM BLACK");
	  HCLCOLOR_MAP.put("SHINY BLUE","MEDIUM BLUE");
	  HCLCOLOR_MAP.put("SHINY ORANGE","MEDIUM ORANGE");
	  HCLCOLOR_MAP.put("SHINY RED","MEDIUM RED");
	  HCLCOLOR_MAP.put("NEON GREEN","BRIGHT GREEN");
	  HCLCOLOR_MAP.put("NEON ORANGE","BRIGHT ORANGE");
	  HCLCOLOR_MAP.put("COPPER","MEDIUM BROWN");
	  HCLCOLOR_MAP.put("GUNMETAL GRAY","MEDIUM GRAY");
	  HCLCOLOR_MAP.put("TAN","LIGHT BROWN");
	  HCLCOLOR_MAP.put("COOL GREY","MEDIUM GRAY");
	  HCLCOLOR_MAP.put("JELLY BELLY","OTHER");
	  HCLCOLOR_MAP.put("CHARCOAL GRAY","DARK GRAY");
	  HCLCOLOR_MAP.put("RAINBOW","MULTI COLOR");
	}
  public static String getColorGroup(String colorName){
		return HCLCOLOR_MAP.get(colorName.toUpperCase());
	}

}
