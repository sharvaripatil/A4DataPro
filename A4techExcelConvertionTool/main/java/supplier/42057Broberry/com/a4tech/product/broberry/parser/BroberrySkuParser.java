package com.a4tech.product.broberry.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.internal.runners.model.EachTestNotifier;

import com.a4tech.product.model.ProductSKUConfiguration;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.util.ApplicationConstants;


public class BroberrySkuParser {
	// color set,sizes set ,XYZ sku,set sku no
	public ProductSkus getProductRelationSkus(Set<String> skuSet) {
		
    	ProductSkus pskuObj=new ProductSkus();	
	    ProductSKUConfiguration configObj= new ProductSKUConfiguration();
	    List<ProductSKUConfiguration> configList=new ArrayList<ProductSKUConfiguration>();
	    
	    Iterator<String> skuIterator=skuSet.iterator();

		while (skuIterator.hasNext()) {
			String skuValue = (String) skuIterator.next();
			pskuObj.setSKU(skuValue);	
	
			
			configObj.setCriteria("Product Color");
		
			
			configList.add(configObj);
			pskuObj.setConfigurations(configList);

		}
		  return pskuObj;	
	}

}
