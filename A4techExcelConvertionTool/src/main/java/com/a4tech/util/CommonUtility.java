package com.a4tech.util;

public class CommonUtility {
	

	public static boolean isEmptyOrNull(String str) {
		return (str != null && !"".equals(str));
	}
	
	public static String getFileExtension(String fileName){
		
		return  fileName.substring(fileName.lastIndexOf('.')+1);
	}

}
