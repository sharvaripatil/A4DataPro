package com.a4tech.bambam.product.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BamUtility {
 public static Map<String, List<String>> optionValues = new HashMap<>();
 
 public static void saveOptions(String optionType,List<String> values){
	 optionValues.put(optionType, values);
 }
 
 public static List<String> getOptions(String optionType){
	 return optionValues.get(optionType);
 }
 
 public static void clearOptions(){
	 optionValues.clear();
 }
}
