package com.a4tech.product.broberry.parser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.Inventory;
import com.a4tech.product.model.ProductSKUConfiguration;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.util.ApplicationConstants;


public class BroberrySkuParser {
	private static final Logger _LOGGER = Logger.getLogger(BroberrySkuParser.class);

	
	public ProductSkus getProductRelationSkus(ProductSkus existingProductSku,String sizeValue,String colorValue ,String skuValue) {
		
		ProductSkus skuObj=new ProductSkus();
		List<ProductSKUConfiguration> ProductSkusList =new ArrayList<ProductSKUConfiguration>();
		ProductSKUConfiguration productskuconfObj=new ProductSKUConfiguration();
		ProductSKUConfiguration productskuconfObj1=new ProductSKUConfiguration();

		try {
		
		skuObj.setConfigurations(ProductSkusList);
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
			productskuconfObj1.setValue(Arrays.asList(sizeValue1[1]));
		    ProductSkusList.add(productskuconfObj1);
		}
			productskuconfObj.setCriteria("Product Color");
			productskuconfObj.setValue(Arrays.asList(colorValue));
		    ProductSkusList.add(productskuconfObj);
		
		
		InvenObj.setInventoryLink("");
		InvenObj.setInventoryQuantity("");
		InvenObj.setInventoryStatus("");
		existingProductSku.setConfigurations(ProductSkusList);
		existingProductSku.setSKU(skuValue);
		existingProductSku.setInventory(InvenObj);
		  
		
		
	} catch (Exception e) {
		_LOGGER.error("Error while processing sku :" + e.getMessage());
	
	}
	_LOGGER.info("SKU processed");
	return existingProductSku;
	}
}	