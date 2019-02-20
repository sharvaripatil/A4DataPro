package com.a4tech.dataStore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.a4tech.product.dao.entity.SupplierProductColors;
import com.a4tech.product.model.Product;
import com.a4tech.product.service.ProductService;

public class ProductDataStore {
	public static Map<String, Product> storeProduct = new HashMap<String, Product>();
	public static Set<String> productColorNames = new HashSet<>();
	public static Set<String> productSizesBrobery= new HashSet<>();
	public static Map<Integer, List<SupplierProductColors>> productColorsMap = new HashMap<>();
	@Autowired
	private ProductService productService;
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
	
	public static void saveSizesBrobery(String colorName){
		productSizesBrobery.add(colorName);
	}
    
	public static Set<String> getSizesBrobery(){
		return productSizesBrobery;
	}
	
	public static void clearSizesBrobery(){
		productSizesBrobery.clear();
	}
	public Map<String, String> getProductColors(Integer asiNumber){
		List<SupplierProductColors> colorsList ;
		if(productColorsMap.containsKey(asiNumber)) {
			colorsList = productColorsMap.get(asiNumber);
		} else {
			colorsList = productService.getSupplierColorsByAsiNumber(asiNumber);
			productColorsMap.put(asiNumber, colorsList);
		}
		if(!CollectionUtils.isEmpty(colorsList)) {
			return colorsList.stream()
					.collect(Collectors.toMap(SupplierProductColors::getColorName, SupplierProductColors::getColorGroup));
		}
		return null;
		
	}
}
