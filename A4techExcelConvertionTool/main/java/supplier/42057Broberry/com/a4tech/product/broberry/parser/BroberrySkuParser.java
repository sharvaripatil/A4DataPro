package com.a4tech.product.broberry.parser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Inventory;
import com.a4tech.product.model.ProductSKUConfiguration;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.util.ApplicationConstants;


public class BroberrySkuParser {
	private static final Logger _LOGGER = Logger.getLogger(BroberrySkuParser.class);

	
	public List<ProductSkus> getProductRelationSkus(List<ProductSkus> existingProductSkuList,String sizeValue,String colorValue ,String skuValue) {
		
		ProductSkus skuObj=new ProductSkus();
		List<ProductSKUConfiguration> skusConfig =new ArrayList<ProductSKUConfiguration>();
		ProductSKUConfiguration productskuconfObj=new ProductSKUConfiguration();
		ProductSKUConfiguration productskuconfObj1=new ProductSKUConfiguration();
		
		try {
		
		Inventory InvenObj=new Inventory(); 
		if(sizeValue.contains("_")){
			if(sizeValue.contains("Dimension")){
				productskuconfObj1.setCriteria("SIZE - Dimension");
				}
			else if(sizeValue.contains("Apparel")){
				productskuconfObj1.setCriteria("Apparel-Waist/Inseam");
				}
			else if(sizeValue.contains("Standard"))	{
			    productskuconfObj1.setCriteria("Standard & Numbered");
			}

			String sizeValue1[]=sizeValue.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			if(sizeValue.contains("Dimension")){
				Size size = new Size();
				Apparel appareal = new Apparel();
				List<Value> values=new ArrayList<>();
				Dimension dimensionObj = new Dimension();
				List<Values> valuesList = new ArrayList<Values>();
				Value valueObj=new Value();//Length:6:ft;Width:3/4:in
				
					String DimenArr[] = sizeValue1[1].split(ApplicationConstants.CONST_DELIMITER_SEMICOLON);
					List<Object> valuelist=new ArrayList<Object>();
					Values valuesObj  = new Values();
					Value valObj;
					int valCount=1;
					for (String value : DimenArr) {
					String[] DimenArr1 = value.split(ApplicationConstants.CONST_DELIMITER_COLON);
					valObj = new Value();
						for (String value1 : DimenArr1) {
							if(valCount==1){
							valObj.setAttribute(value1);
							}else if(valCount==2){
							valObj.setValue(value1);
							}else if(valCount==3){
							valObj.setUnit(value1);
							}
							valCount++;
						}
						valuelist.add(valObj);
						valCount=1;
					}
					productskuconfObj1.setValue(valuelist);
			}else{
			productskuconfObj1.setValue(Arrays.asList(sizeValue1[1]));
			}
			skusConfig.add(productskuconfObj1);
			}
		
			productskuconfObj.setCriteria("Product Color");
			String colorValue1[]=colorValue.split(",");
			productskuconfObj.setValue(Arrays.asList(colorValue1[0]));
			skusConfig.add(productskuconfObj);
	
		InvenObj.setInventoryLink("");
		InvenObj.setInventoryQuantity("");
		InvenObj.setInventoryStatus("");
		skuObj.setConfigurations(skusConfig);
		skuObj.setSKU(skuValue);
		skuObj.setInventory(InvenObj);
		
		existingProductSkuList.add(skuObj); 
			
	} catch (Exception e) {
		_LOGGER.error("Error while processing sku :" + e.getMessage());
	
	}
	return existingProductSkuList;
	}
	
}	
