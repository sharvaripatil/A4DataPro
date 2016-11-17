package com.a4tech.dataStore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.a4tech.product.model.Product;

public class ProductDataStore {
	public static Map<String, Product> storeProduct = new HashMap<String, Product>();
	public static Set<String> productColorNames = new HashSet<>();
	public static void setProduct(String productNo,Product product){
		storeProduct.put(productNo, product);
	}
	
	public static Product getProduct(String productNo){
		  Product product = storeProduct.get(productNo);
		  return product;	
	}
	
	public static void saveColorNames(String colorName){
		productColorNames.add(colorName);
	}
    
	public static Set<String> getColorNames(){
		return productColorNames;
	}
	
	public static void clearProductColorSet(){
		productColorNames.clear();
	}
}
