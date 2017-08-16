package com.a4tech.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.util.StringUtils;

import com.a4tech.core.errors.ErrorMessage;
import com.a4tech.core.errors.ErrorMessageList;

import parser.proGolf.ProGolfColorMapping;

public class CommonUtility {
	
   private static Logger _LOGGER = Logger.getLogger(CommonUtility.class);
	public static boolean isEmptyOrNull(String str) {
		return (str != null && !" ".equals(str));
	}
	
	public static String getFileExtension(String fileName){
		
		return  fileName.substring(fileName.lastIndexOf('.')+1);
	}
	
	public static List<String> getStringAsList(String value,String splitter){
		List<String> data = null;
		if(!StringUtils.isEmpty(value)){
			data = new ArrayList<String>();
			String[] values = value.split(splitter);
			for (String attribute : values) {
				if(!StringUtils.isEmpty(attribute)){
					data.add(attribute);
				}
			}
			return data;
		}
		return new ArrayList<String>();
	}
	public static List<String> getStringAsList(String value){
		List<String> listOfValues = Arrays.asList(value
				.split(ApplicationConstants.CONST_STRING_COMMA_SEP));
		return listOfValues;
	}
	public static String getCellValueDouble(Cell cell) {
		String value = ApplicationConstants.CONST_STRING_EMPTY;
		try {
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				value = cell.getStringCellValue().trim();
			} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				double doubleValue = cell.getNumericCellValue();
				value = String.valueOf(doubleValue).trim();
			}else if(cell.getCellType() == Cell.CELL_TYPE_ERROR){
				//value = String.valueOf(cell.getErrorCellValue());
				value = Byte.toString(cell.getErrorCellValue()).trim();
				value="";
			}
		} catch (Exception e) {
			_LOGGER.error("Cell value convert into Double: " + e.getMessage());
		}

		return value;
	}
	
	public static String getCellValueStrinOrInt(Cell cell) {
		String value = ApplicationConstants.CONST_STRING_EMPTY;
		try {
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				value = cell.getStringCellValue().trim();
			} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				int numericValue = (int) cell.getNumericCellValue();
				value = String.valueOf(numericValue).trim();
			}else if(cell.getCellType() == Cell.CELL_TYPE_ERROR){
				//value = String.valueOf(cell.getErrorCellValue());
				value = Byte.toString(cell.getErrorCellValue()).trim();
				value="";
			}else if(cell.getCellType() == Cell.CELL_TYPE_BOOLEAN){
				//value = String.valueOf(cell.getErrorCellValue());
				boolean val = cell.getBooleanCellValue();
				value=String.valueOf(val);
			}
			
		} catch (Exception e) {
			_LOGGER.error("Cell value convert into String/Int format: "
					+ e.getMessage());
		}

		return value;
	}
	
	public static String getCellValueStrinOrDecimal(Cell cell){
		String value = ApplicationConstants.CONST_STRING_EMPTY;
		try{
	if(cell.getCellType() == Cell.CELL_TYPE_STRING){
		value = cell.getStringCellValue();
		}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
			value = String.valueOf(BigDecimal.valueOf(cell.getNumericCellValue()));
		}
	}catch(Exception e){
		_LOGGER.error("Cell value convert into String/decimal: "+e.getMessage());
	}
		return value;
	}
	public static boolean isPriceQuantity(int indexNumber){
		if(indexNumber >=9 && indexNumber <=22){
			return true;
		}else{
			return false;
		}	
	}
   public static String[] getValuesOfArray(String data,String delimiter){
	   if(!StringUtils.isEmpty(data)){
		   return data.split(delimiter);
	   }
	   return null;
   }
   /* @Author  Venkat ,13/09/2016
    * @Param   String (Value) 
    * @Description This method is checking value is zero or blank
    * @ReturnType boolean
    */
	public static boolean checkZeroAndEmpty(String value) {

		if (ApplicationConstants.CONST_STRING_ZERO.equals(value)
				|| ApplicationConstants.CONST_STRING_EMPTY.equals(value)) {
			return true;
		}
		return false;
	}
   
   public static boolean isBlank(String value){
	   if(value.equals(" ")){
		   return true;
	   }
	   return false;
   }
   /*@author Venkat
    *@param String,it is file extension name i.e xls,csv..
    *@description This method is valid for file extension weather it is xls,xlsx ,csv format or
    *                                                                           any other format
    * @ return boolean ,if filename having xls,xlsx ,csv then return true else false
    */
	public static boolean isValidFormat(String fileName) {

		if (ApplicationConstants.CONST_STRING_XLS.equalsIgnoreCase(fileName)
				|| ApplicationConstants.CONST_STRING_XLSX.equalsIgnoreCase(fileName)
				|| ApplicationConstants.CONST_STRING_CSV.equalsIgnoreCase(fileName)) {
			return true;

		}
		return false;
	}
	/*
	 * @author Venkat
	 * @param String ,response message
	 * @description this method is design for converting error response message 
	 *                                   converting into errorMessageList format
	 * @return errorMessageList 
	 */
	public static ErrorMessageList responseconvertErrorMessageList(
			String response) {
		ErrorMessageList responseList = new ErrorMessageList();
		List<ErrorMessage> errorList = new ArrayList<ErrorMessage>();
		ErrorMessage errorMsgObj = new ErrorMessage();
		errorMsgObj.setMessage(response);
		errorList.add(errorMsgObj);
		if (response.contains("java.net.UnknownHostException")
				|| response.contains("java.net.NoRouteToHostException")) {
			errorMsgObj
					.setReason("Product is unable to process due to Internet service down");
		} else if (response.equalsIgnoreCase("500 Internal Server Error")) {
			errorMsgObj
					.setReason("Product is unable to process due to ASI server issue");
		} else if (response.contains("java.net.SocketTimeoutException")) {
			errorMsgObj
					.setReason("Product is unable to process due to ASI server not responding");
		}else if (response.contains("Product Data issue")) {
			errorMsgObj.setReason(response);
			errorMsgObj.setMessage("Product Data issue in Supplier Sheet");
		}
		responseList.setErrors(errorList);
		return responseList;
	}
	/*
	 * author Venkat 13/10/2016
	 * @param String OriginalValue,String String SpecialSymbol
	 * @description This method is remove special symbol in given value
	 * @return String,it returns finalValue  
	 */
	public static String removeSpecialSymbols(String value,String symbol){
		String finalValue = value.replaceAll(symbol, ApplicationConstants.CONST_STRING_EMPTY);
		return finalValue;
	}
	/*@author Venkat 18/10/2016
	 *@param String,String,String 
	 *@description This method design for concatenate two string by delimiter
	 *@return String 
	 */
	public static String appendStrings(String src, String destination ,String delimiter){
		  if(!StringUtils.isEmpty(destination)){
			  src = src.concat(delimiter).concat(destination);
			  return src;
		  }else {
			  return src;
		  }
	}
	
  public static String removeCurlyBraces(String source){
	  if(source.contains(ApplicationConstants.SQUARE_BRACKET_OPEN))
	  {
		  source = source.replace(ApplicationConstants.SQUARE_BRACKET_OPEN, 
				                                        ApplicationConstants.CONST_STRING_EMPTY);
	  }
	  if(source.contains(ApplicationConstants.SQUARE_BRACKET_CLOSE)){
		  source = source.replace(ApplicationConstants.SQUARE_BRACKET_CLOSE, 
				                                  ApplicationConstants.CONST_STRING_EMPTY);
	  }
	  return source; //exponential
  }
  
  public static String convertExponentValueIntoNumber(String exponentValue){
	  try{
		  BigDecimal bigDecimal = new BigDecimal(exponentValue);
		  long number = bigDecimal.longValue();
		  String value = Long.toString(number);
		  return value;  
	  }catch(NumberFormatException nfe){
		  return ApplicationConstants.CONST_STRING_EMPTY;
	  }
	  
  }
  
  public static String getStringLimitedChars(String value, int noOfCharacters){
	  int len=value.length();
      if(len>noOfCharacters){
      String strTemp=value.substring(ApplicationConstants.CONST_NUMBER_ZERO, noOfCharacters);
      if(strTemp.contains(" ")){
    	  int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
          value= (String) strTemp.subSequence(ApplicationConstants.CONST_NUMBER_ZERO, lenTemp);  
      } else {
    	  value = strTemp;
      }
    }
      return value;
  }
  
   /*
    * description : this method used to check price values is descending or not,
    *                      if values are not descending order then return false
    *      e.g. : 15,12,10  -- return true
    *             15,12,15  -- return false
    */
	public static boolean isdescending(String[] prices){
		double[] doubleprices=convertStringArrintoDoubleArr(prices);
		  for (int i = 0; i < doubleprices.length-1; i++) {
		      if (doubleprices[i] < doubleprices[i+1]) {
		          return false;
		      }
		  }
		  return true; 
		 }
  
	public static double[] convertStringArrintoDoubleArr(String[] value)
	{
		
		double[] doubleprices = Arrays.stream(value).mapToDouble(Double::parseDouble).toArray();
		return doubleprices;
		
	}
  
	/*
	 * author Amey 27/3/2016
	 * @description This method is remove restrict symbol in given value
	 * chars are replace as per feedback provided by michael
	 */
	public static String removeRestrictSymbols(String value){
		value=value.replaceAll("±", "");
		value=value.replaceAll("’", "single quote");
		value=value.replaceAll("`", "single quote");
		value=value.replaceAll("‘", "single quote");
		value=value.replaceAll("“", "double quote");
		value=value.replaceAll("”", "double quote");
		value=value.replaceAll("–", "dash");
		value=value.replaceAll("®", "(R)");
		value=value.replaceAll("™", "(TM)");
		value=value.replaceAll("°", " the word degrees");
		value=value.replaceAll("×", "x");
		value=value.replaceAll("¿", "");
		value=value.replaceAll("•", "");
		value=value.replaceAll("…", "Three periods");
		value=value.replaceAll("€", "");
		value=value.replaceAll("\\|", ",");
		value=value.replaceAll("½", "1/2");
		value=value.replaceAll("¾", "3/4");
		value=value.replaceAll("¼", "1/4");
		value = value.replaceAll("\\[", "");
		value = value.replaceAll("\\]", "");
		value=value.replaceAll("<", "");
		value=value.replaceAll(">", "");
		value=value.replaceAll("", "");
		value=value.replaceAll("—", "");
		value=value.replaceAll("¡", "");
		value=value.replaceAll("ñ", "");
		value=value.replaceAll("~", "");
		value=value.replaceAll("†", "");
		return value;
	}
  
  
	/*
	 * Author      : venkat
	 * Description : This method used to check price confirm through date is future date or past date,if date is past no need
	 *                assign value
	 *  Param      : string date(supplier given date)
	 *  Return     : true/false 
	 */
	public static boolean isPriceConfirmThroughDate(String date){
		Date current = new Date();
		String myFormatString = "dd-MM-yyyy";//new format for goldbond
		//String myFormatString = "yy-MM-dd";
		SimpleDateFormat dateFormat = new SimpleDateFormat(myFormatString);
		Date supplierGivenDate;
		try {
			supplierGivenDate = dateFormat.parse(date);
			Long supplierGivenTime = supplierGivenDate.getTime();
			Date nextDate = new Date(supplierGivenTime);
			if (nextDate.after(current) || (nextDate.equals(current))) {
				return true;
			} else {
				return false;
			}
		} catch (ParseException exce) {
			_LOGGER.error("unable to parse dates: " + exce.getMessage());
		}
		return false;
	}
    /*Author :Venakt
     * Description : this method can used to convert production time weeks into business days
     *               e.g. : 4 weeks into 20 business days  
     * 
     */
	public static String convertProductionTimeWeekIntoDays(String productionTime) {
		if (productionTime.contains("-")) {
			String[] productionTimes = productionTime.split("-");
			int productionStartTime = Integer.parseInt(productionTimes[0]) * ApplicationConstants.CONST_INT_VALUE_FIVE;
			int productionEndTime = Integer.parseInt(productionTimes[1]) * ApplicationConstants.CONST_INT_VALUE_FIVE;
			productionTime = productionStartTime + "-" + productionEndTime;
		} else {
			int productionStartTime = Integer.parseInt(productionTime) * ApplicationConstants.CONST_INT_VALUE_FIVE;
			productionTime = productionStartTime + "";
		}
		return productionTime;
	}
	/*Author :Venakt
     * Description : this method can used to convert production time weeks into business days
     *               e.g. : 4 weeks into 20 business days  
     * param : productionTimeValue,delimiter
     */
	public static String convertProductionTimeWeekIntoDays(String productionTime , String delimiter) {
		
			String[] productionTimes = productionTime.split(delimiter);
			int productionStartTime = Integer.parseInt(productionTimes[0].trim()) * ApplicationConstants.CONST_INT_VALUE_FIVE;
			int productionEndTime = Integer.parseInt(productionTimes[1].trim()) * ApplicationConstants.CONST_INT_VALUE_FIVE;
			productionTime = productionStartTime + "-" + productionEndTime;
		
		return productionTime;
	}
	/*
	 * Author      : venkat
	 * Description : this method used to extract values between specialCharacter like (1132),{01245}..
	 *               e.g. I/p : Regular Left (041238) - Colors Available
	 *                    O/p :041238 
	 */
	public static String extractValueSpecialCharacter(String specialCharOpen,String specialCharClose,String src){
		String finalVal = src.substring(src.indexOf(specialCharOpen) + 1, src.indexOf(specialCharClose));
		return finalVal;
	}
	/* Author       : Venkat
	 * Description : This method used for color value is part of color grouping or not,if all colors are 
	 *              part of color grouping then we need to create combo other wise it treate as single color only        
	 * Parm         : String colorValue
	 * Retrun      : true(all colors are part of color mapping group)
	 * 
	 */
	 public static boolean isComboColor(String colorValue){
	    	String[] colorVals = null;
	    	if(colorValue.contains(",")){
	    		colorVals = CommonUtility.getValuesOfArray(colorValue, ",");
	    	} else if(colorValue.contains("/")){
	    		colorVals = CommonUtility.getValuesOfArray(colorValue, "/");
	    	}
	    	String mainColor       = null;
	    	String secondaryColor  = null;
	    	String thirdColor      = null;
	    	if(colorVals.length == ApplicationConstants.CONST_INT_VALUE_TWO){
	    		 mainColor = ProGolfColorMapping.getColorGroup(colorVals[0].trim());
	    		 secondaryColor = ProGolfColorMapping.getColorGroup(colorVals[1].trim());
	    		 if(mainColor != null && secondaryColor != null){
	    			 return true;
	    		 }
	    	} else if(colorVals.length == ApplicationConstants.CONST_INT_VALUE_THREE){
	    		 mainColor      = ProGolfColorMapping.getColorGroup(colorVals[0].trim());
	    		 secondaryColor = ProGolfColorMapping.getColorGroup(colorVals[1].trim());
	    		 thirdColor     = ProGolfColorMapping.getColorGroup(colorVals[2].trim());
	    		 if(mainColor != null && secondaryColor != null && thirdColor != null){
	    			 return true;
	    		 }
	    	} else{
	    		
	    	}
	    	return false;
	    }
	 /*
	  * @author Venkat
	  * @description This method is valid to two business days range is it valid range or not 
	  *                    means low value to high value
	  * @ param String startDay, String  endDay
	  * @return True/False,if value low value to high value(10-15) returns true otherwise returns False
	  */
	 public static boolean isValidBusinessDays(int startDay,int endDay){
		 if(startDay >= endDay){
			 return false;
		 }
		 return true;
	 }
	 /*
	  * @author      :Venkat
	  * @description : this method used to remove specific word in given source data
	  *                e.g if asiProductNo is present in Product Name,description and summary,we must remove same word in 
	  *                 Name/Description /summary other wise system does not allow   
	  */
	 public static String removeSpecificWord(String source,String specificWord){
		 source = source.replaceAll("(?i)"+specificWord, "").trim();
		 return source;
	 }
	 /*
	  * @author      :Venkat ,19/05/2017
	  * @description : This method used to remove duplicate values in array of string(String[])
	  *  @param      : string[]
	  *  @return     : string[]
	  */
	 public static String[] removeDuplicateValues(String[] values){
		 // remove spaces before words(" mumbai","pune")
		 values = org.apache.commons.lang3.StringUtils.stripAll(values);
		 List<String> uniqueColorList=new IgnoreCaseStringList();
		for (String value : values) {
			  if(!uniqueColorList.contains(value)){
				  uniqueColorList.add(value);
			  }
		}
		 values = new HashSet<String>(uniqueColorList).toArray(new String[0]);
		 return values;
	 }
	/* public class IgnoreCaseStringList extends ArrayList<String>{
		@Override
		public boolean contains(Object o) {
			String paramStr = (String)o;
	        for (String name : this) {
	            if (paramStr.equalsIgnoreCase(name)) 
	            	return true;
	        }
	        return false;		
		}
	 }*/
}
