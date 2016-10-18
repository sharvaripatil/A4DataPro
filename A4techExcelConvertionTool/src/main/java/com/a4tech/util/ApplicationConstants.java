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
    public static final String  CONST_STRING_PERSONALIZATION                = "PERSONALIZATION";
    public static final String  CONST_STRING_PRINTED                        =  "Printed";
    
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
	public static final String  CONST_DELIMITER_PERCENT_SIGN                = "%";
	
	public static final int     CONST_NUMBER_ZERO                           = 0;
	public static final int     CONST_NEGATIVE_NUMBER_ONE					= -1;
	
	public static final String  CONST_STRING_FALSE						= "False";
	public static final String  CONST_STRING_DISCOUNT_CODE_Z			= "Z";
	public static final String  CONST_STRING_SERVICECHARGE				= "Required";
	public static final String  CONST_STRING_YES						= "Yes";
	public static final String  CONST_STRING_CURRENCY_USD               ="USD";
	public static final String  CONST_STRING_IMMD_CHARGE			    = "Imprint Method Charge";
	public static final String	CONST_STRING_FULLCOLOR					="FULL COLOR";
	public static final String	CONST_STRING_DAYS					    ="days";
	public static final String	CONST_SHIPPING_UNIT_KG					="kg";
	public static final String	CONST_SHIPPING_UNIT_GRAMS				="grams";
	public static final String  CONST_STRING_IMPRINT_METHOD             = "Imprint Method";
	public static final String  CONST_STRING_WARRANTY_AVAILABLE         = "WARRANTY AVAILABLE";
	public static final String  CONST_STRING_WARRANTY_LENGTH			= "WARRANTY LENGTH";
	public static final String  CONST_STRING_LIFE_TIME                  = "Lifetime";
	public static final String  CONST_STRING_GRAPHIC					= "Graphic";
	public static final String  CONST_STRING_HARDWARE					= "Hardware";
	public static final String  CONST_STRING_WARRANTY_CHARGE_TYPE		= "Warranty Charge";
	public static final String  CONST_STRING_WARRANTY_INFORMATION 		= "Warranty Information";
	public static final String  CONST_REDIRECT_URL               		= "redirect:redirect.htm";
	public static final String  CONST_STRING_HOME						= "home";
	public static final String  CONST_STRING_SUCCESS					= "success";
	public static final String  CONST_STRING_FILE_NAME                  = "fileName";
	public static final String  SUCCESS_PRODUCTS_COUNT      			= "successProductsCount";
	public static final String  FAILURE_PRODUCTS_COUNT      			= "failureProductsCount";
	public static final String  CONST_STRING_SUCCESS_MSG			    = "successmsg";
	public static final String  CONST_STRING_UN_AUTHORIZED				= "unAuthorized";
	public static final String  CONST_STRING_ERROR_PAGE					= "errorPage";
	public static final String	CONST_STRING_XLS						= "xls";
	public static final String	CONST_STRING_XLSX						= "xlsx";
	public static final String	CONST_STRING_INVALID_UPLOAD_FILE		= "invalidUploadFile";
	public static final String	CONST_STRING_INVALID_DETAILS			= "invalidDetails";
	public static final String  MAIL_SEND_SUCCESS_MESSAGE				= "Email has been sent Successfully !!!";
	public static final String  CONST_STRING_CSV						= "csv";
	public static final String  CONST_STRING_DOT_TXT                    = ".txt";
	public static final int     CONST_INT_VALUE_TWO                     =  2;
	public static final int     CONST_INT_VALUE_THREE                   =  3;
	public static final String  CONST_STRING_OTHER_FABRIC				= "Other Fabric";
	public static final String  CONST_STRING_PRODUCT_COLOR              = "Product Color";
	public static final String  CONST_STRING_SIZE 		                = "Size";
	
	
	public static final String  CONST_STRING_COUNTRY_CODE_CN            = "CN";
	public static final String  CONST_STRING_AND					    = "and";
	public static final String  CONST_STRING_PLUS				        = "\\+";
	public static final String  CONST_STRING_RUN_CHARGE			    	= "Run Charge";
	public static final String  CONST_STRING_COPY_CHARGE			    ="Copy Changes Charge";
	public static final String  CONST_STRING_SETUP_CHARGE			    ="Set-up Charge";
	public static final String  CONST_STRING_ARTWK_CHARGE			    ="Artwork Charge";
	public static final String  CONST_DELIMITER_DOT						= ".";
	public static final String  CONST_STRING_IMPRNT_COLR				= "COLR";
	public static final String  CONST_STRING_IMPRINT_CODE             = "IMMD";
	public static final String  CONST_STRING_NEWLINECHARS             ="(\r\n|\n)";	
	public static final String  CONST_STRING_NEWLINE          		  ="\n";	
	
	public static final String	CONST_STRING_SHIPPING_NUMBER_UNIT_CASE   = "per Case";
	public static final String	CONST_STRING_BIG_SPACE   				= "   ";
	
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
	    	SUPPLIER_EMAIL_ID_MAP.put("91561", "sharvari.patil@a4technology.com");
	    	SUPPLIER_EMAIL_ID_MAP.put("40445", "amey.more@a4technology.com");
	    	SUPPLIER_EMAIL_ID_MAP.put("82283", "sharvari.patil@a4technology.com");
	    	SUPPLIER_EMAIL_ID_MAP.put("91284", "amey.more@a4technology.com");
	    	
	    	SUPPORT_EMAIL_ID_MAP.put("dev_1", "amey.more@a4technology.com");
	    	SUPPORT_EMAIL_ID_MAP.put("dev_2", "venkateswarlu.nidamanuri@a4technology.com");
	    	SUPPORT_EMAIL_ID_MAP.put("dev_3", "sharvari.patil@a4technology.com");
	    	SUPPORT_EMAIL_ID_MAP.put("sysadmin_1", "salman@a4technology.com");
	    	SUPPORT_EMAIL_ID_MAP.put("sysadmin_2", "soheb.khan@a4technology.com");
	    	
	    }
    
}
