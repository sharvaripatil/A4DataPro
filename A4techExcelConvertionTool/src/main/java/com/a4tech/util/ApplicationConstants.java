package com.a4tech.util;

import java.util.HashMap;
import java.util.Map;

public class ApplicationConstants {
   
	
	public static final String PRICE_SPLITTER_BASE_PRICEGRID              	= "___";
	public static final int CONST_INT_VALUE_ONE                     	    =  1;
	public static final String CONST_STRING_VALUE_ONE                     	= "1";
	public static final String CONST_STRING_CAPITAL_Y                     	= "Y";
	public final static String CONST_STRING_EMPTY  							= "";
	public static final String CONST_STRING_COMMA_SEP 						= ",";
    public static final String  CONST_PRICE_TYPE_CODE_LIST    				= "L";
    public static final String  CONST_PRICE_TYPE_CODE_NET 					= "N";
    public static final String  CONST_CHAR_N 								= "N";
    public static final String  CONST_CHAR_Y 								= "Y";
    public static final boolean  CONST_BOOLEAN_TRUE 						= true;
    public static final boolean  CONST_BOOLEAN_FALSE 						= false;
    public static final String 	CONST_STRING_EQUAL 							= "=";
    public static final String  CONST_VALUE_TYPE_CODE_LIST 					= "LIST";
    public static final String  CONST_VALUE_TYPE_CODE_NET 					= "NET";
    public static final String  CONST_DELIMITER_COLON 						= ":";
    public static final String  CONST_STRING_COMBO 							= "Combo";
    public static final String  CONST_DELIMITER_SEMICOLON 					= ";";
    public static final String  CONST_VALUE_TYPE_DIMENSION 					= "Dimension";
    public static final String  CONST_VALUE_TYPE_CAPACITY 					= "Capacity";
    public static final String  CONST_VALUE_TYPE_VOLUME 					= "Volume";
    public static final String  CONST_VALUE_TYPE_APPAREL 					= "Apparel";
    public static final String  CONST_VALUE_TYPE_OTHER 						= "Other";
    public static final String  CONST_VALUE_TYPE_SPACE 						= " ";
    public static final String 	DATE_FORMAT 								= "yyyy-MM-dd'T'HH:mm:ss.SZ";
    public static final String  CONST_STRING_UNIMPRINTED					= "UNIMPRINTED";
    public static final String  CONST_STRING_HOT_STAMPED					= "Hot Stamped";
    public static final String  CONST_STRING_HOT_FOIL_STAMPED			    = "Hot/foil stamped";
    public static final String  CONST_STRING_TRUE							= "True";
    public static final String  CONST_STRING_COUNTRY_CODE_CH                = "CH";
    public static final String  CONST_STRING_COUNTRY_CODE_US                = "US";
    public static final String  CONST_STRING_COUNTRY_NAME_USA			    = "U.S.A.";
    public static final String  CONST_STRING_COUNTRY_NAME_CHINA			    = "CHINA";
    public static final String  CONST_STRING_ZERO                           = "0";
    public static final String  CONST_STRING_INCHES							= "in";
    public static final String  CONST_STRING_SHIPPING_WEIGHT                = "lbs";
    public static final String	CONST_STRING_SHIPPING_NUMBER_UNIT_CARTON    = "per Carton";
    public static final String	CONST_STRING_DOWNLOAD_FILE_PATH 			= "D:\\A4 ESPUpdate\\ErrorFiles\\";
    public static final String	CONST_STRING_SECONDARY 						= "Secondary";
    
    public final static String  COMBO_VALUES_SEPARATOR 					    = ":";
    public final static Integer COMBO_TEXT_VALUE_INDEX 					    = 1;
	public static final String  CONST_STRING_COMBO_TEXT 					= "Combo";
	public static final String  CONST_STRING_BLEND_TEXT 					= "Blend";
	public static final String  CONST_MEDIATYPE 							= "application/json";
	public static final String 	CONST_ASI_NUMBER 							= "Asi";
	public static final String 	CONST_USERNAME 								= "Username";
	public static final String 	CONST_PASSWORD 								= "Password";
	
	public static final String  CONST_DELIMITER_PIPE                 		= "|";
	public static final String  CONST_DELIMITER_SPLITTING_PIPE              = "\\|";
	public static final String  CONST_DELIMITER_AMPERSAND    				= "&"; 
	public static final String  CONST_DELIMITER_COMMA						= ",";
	public static final String  CONST_DELIMITER_FSLASH 					    = "/";
	//public static final String  CONST_DELIMITER_BSLASH 					    = "\\";
	public static final String  CONST_DELIMITER_HYPHEN 					    = "-";
	public static final String	CONST_DIMENSION_SPLITTER					= "@@";
	
	public static final int     CONST_NUMBER_ZERO                           = 0;
	public static final int     CONST_NEGATIVE_NUMBER_ONE					= -1;
	
	public static final String  CONST_STRING_FALSE						= "False";
	public static final String  CONST_STRING_DISCOUNT_CODE_Z			= "Z";
	public static final String  CONST_STRING__SERVICECHARGE				= "Required";
	public static final String  CONST_STRING__YES						= "Yes";
	public static final String  CONST_STRING__PERSONALIZATION			= "PERSONALIZATION";
	public static final String  CONST_STRING__CURRENCY_USD              ="USD";
	public static final String  CONST_STRING_IMMD_CHARGE			    = "Imprint Method Charge";
	public static final String	CONST_STRING_FULLCOLOR					="FULL COLOR";
	public static final String	CONST_STRING_DAYS					    ="days";
	public static final String	CONST_SHIPPING_UNIT_KG					="kg";
	//public static final String  
    
    
	  public static Map<String, String> SUPPLIER_EMAIL_ID_MAP =new HashMap<String, String>();
	  public static Map<String, String> SUPPORT_EMAIL_ID_MAP =new HashMap<String, String>();
	    static {
	       
	    	SUPPLIER_EMAIL_ID_MAP.put("55201", "venkateswarlu.nidamanuri@a4technology.com");
	    	SUPPLIER_EMAIL_ID_MAP.put("55202", "azam.rizvi@a4technology.com");
	    	SUPPLIER_EMAIL_ID_MAP.put("55203", "sharvari.patil@a4technology.com");
	    	//SUPPLIER_EMAIL_ID_MAP.put("55204", "sharvari.patil@a4technology.com");
	    	SUPPLIER_EMAIL_ID_MAP.put("55204", "azam.rizvi@a4technology.com");
	    	SUPPLIER_EMAIL_ID_MAP.put("55205", "venkateswarlu.nidamanuri@a4technology.com");
	    	SUPPLIER_EMAIL_ID_MAP.put("65851", "amey.more@a4technology.com");
	    	SUPPORT_EMAIL_ID_MAP.put("dev_1", "amey.more@a4technology.com");
	    	SUPPORT_EMAIL_ID_MAP.put("dev_2", "venkateswarlu.nidamanuri@a4technology.com");
	    	SUPPORT_EMAIL_ID_MAP.put("dev_3", "sharvari.patil@a4technology.com");
	    	SUPPORT_EMAIL_ID_MAP.put("sysadmin_1", "salman@a4technology.com");
	    	SUPPORT_EMAIL_ID_MAP.put("sysadmin_2", "soheb.khan@a4technology.com");
	    	
	    }
    
}
