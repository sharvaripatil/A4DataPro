package com.a4tech.dataStore;

import java.util.HashMap;
import java.util.Map;

import com.a4tech.product.model.Product;

public class ProductDataStore {
	public static Map<String, Product> storeProduct = new HashMap<String, Product>();
	
	public static void setProduct(String productNo,Product product){
		storeProduct.put(productNo, product);
	}
	
	public static Product getProduct(String productNo){
		  Product product = storeProduct.get(productNo);
		  return product;	
	}

}
