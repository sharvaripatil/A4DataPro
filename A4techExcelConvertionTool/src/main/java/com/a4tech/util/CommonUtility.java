package com.a4tech.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.springframework.util.StringUtils;

public class CommonUtility {
	

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
		if(cell.getCellType() == Cell.CELL_TYPE_STRING){
			value = cell.getStringCellValue();
		}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
			double doubleValue = cell.getNumericCellValue();
			value = String.valueOf(doubleValue);
		}
		return value;
	}
	
	public static String getCellValueStrinOrInt(Cell cell){
		String value = "";
		if(cell.getCellType() == Cell.CELL_TYPE_STRING){
			value = cell.getStringCellValue();
		}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
			int numericValue = (int) cell.getNumericCellValue();
			value = String.valueOf(numericValue);
		}
		return value;
	}

}
