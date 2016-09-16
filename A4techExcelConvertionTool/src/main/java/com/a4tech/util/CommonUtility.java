package com.a4tech.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.util.StringUtils;

public class CommonUtility {
	
	//String[] priceQuantityIndex = {"8","9","10","11","12","13","14","15","16","17","18","19","20","21"};
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
	
	public static String getCellValueDouble(Cell cell){
		String value = "";
		try{
			if(cell.getCellType() == Cell.CELL_TYPE_STRING){
				value = cell.getStringCellValue();
			}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
				double doubleValue = cell.getNumericCellValue();
				value = String.valueOf(doubleValue);
			}
		}catch(Exception e){
			_LOGGER.error("Cell value convert into Double: "+e.getMessage());
		}
		
		return value;
	}
	
	public static String getCellValueStrinOrInt(Cell cell){
		String value = "";
		try{
			if(cell.getCellType() == Cell.CELL_TYPE_STRING){
				value = cell.getStringCellValue();
			}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
				int numericValue = (int) cell.getNumericCellValue();
				value = String.valueOf(numericValue);
			}
		}catch(Exception e){
			_LOGGER.error("Cell value convert into String/Int format: "+e.getMessage());
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
   public static boolean checkZeroAndEmpty(String value){
		   
		   if(ApplicationConstants.CONST_STRING_ZERO.equals(value) || 
				                                      ApplicationConstants.CONST_STRING_EMPTY.equals(value)){
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
}
