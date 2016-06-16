package com.a4tech.product.USBProducts.criteria.parser;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.WarrantyInformation;
import com.a4tech.util.ApplicationConstants;

public class ProductWarrantyParser {
	
	public Product getWarrantyAndDescriptionProduct(Product product ,ProductConfigurations configuration, 
			                                                                   String productFeatures){
		List<WarrantyInformation> listOfWarrantyInfo = new ArrayList<WarrantyInformation>();
		WarrantyInformation warranty = null;
		StringBuilder productDescri = new StringBuilder();
		if(productFeatures.contains(ApplicationConstants.CONST_DELIMITER_PIPE)){
			 String[] features = productFeatures.split(ApplicationConstants.CONST_DELIMITER_SPLITTING_PIPE);
			 for (String feature : features) {
				 	if(feature.contains("warranty")){
				 		warranty = new WarrantyInformation();
				 		warranty.setName("WARRANTY LENGTH");
				 		warranty.setComments(feature);
				 		listOfWarrantyInfo.add(warranty);
				 	}else{
				 		 productDescri.append(feature + " ");
				 	}
			}
			
		}else{
			if(productFeatures.contains("warranty")){
		 		warranty = new WarrantyInformation();
		 		warranty.setName("WARRANTY LENGTH");
		 		warranty.setComments(productFeatures);
		 		listOfWarrantyInfo.add(warranty);
		 	}
			productDescri.append(product.getName());
		}
		
		warranty = new WarrantyInformation();
		warranty.setName("WARRANTY AVAILABLE");
		listOfWarrantyInfo.add(warranty);
		configuration.setWarranty(listOfWarrantyInfo);
		product.setProductConfigurations(configuration);
		product.setDescription(productDescri.toString());
		return product;
	}

}
