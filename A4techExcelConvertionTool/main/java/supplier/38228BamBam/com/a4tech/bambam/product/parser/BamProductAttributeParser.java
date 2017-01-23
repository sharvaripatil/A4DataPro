package com.a4tech.bambam.product.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Inventory;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.a4tech.util.LookupData;

public class BamProductAttributeParser {
  
   private final static Logger _LOGGER  = Logger.getLogger(BamProductAttributeParser.class);
   public static final String   CONST_STRING_COMBO_TEXT = "Combo";
   private LookupServiceData lookupServiceData;
   
 
   public List<ImprintSize> getImprintSizes(String imprvalue){
		List<ImprintSize> listOfImprintSize = new ArrayList<>();
		ImprintSize imprintSizeObj = null;
		if (imprvalue.contains(ApplicationConstants.CONST_STRING_COMMA_SEP)) {
			imprvalue = imprvalue.replaceAll(
					ApplicationConstants.CONST_STRING_COMMA_SEP,
					ApplicationConstants.CONST_DELIMITER_SEMICOLON);
		}
		String[] imprValues = CommonUtility.getValuesOfArray(imprvalue,
				ApplicationConstants.CONST_DELIMITER_SEMICOLON);
		for (String imprSizeName : imprValues) {
			imprintSizeObj = new ImprintSize();
			imprintSizeObj.setValue(imprSizeName);
			listOfImprintSize.add(imprintSizeObj);
		}
		return listOfImprintSize;
	}
   public List<Origin> getOrigins(String originName){
	   List<Origin> listOfOrigin = new ArrayList<>();
	   Origin originObj = new Origin();
	   if(originName.equalsIgnoreCase("US")){
		   originName = "U.S.A.";
	   }
	   originObj.setName(originName);
	   listOfOrigin.add(originObj);
	   return listOfOrigin;	   
   }
   public List<String> getCategories(String category){
	   List<String> listOfCategories = new ArrayList<>();
	   String[] categories = CommonUtility.getValuesOfArray(category, ApplicationConstants.CONST_STRING_COMMA_SEP);
	   for (String categoryName : categories) {
		   if(lookupServiceData.isCategory(categoryName.toUpperCase())){
			   listOfCategories.add(categoryName);
		   }
	}
	   return listOfCategories;
   }
   public Product getProductionTimes(String prdTime,Product existingProduct){
	   ProductConfigurations existingConfiguration = existingProduct.getProductConfigurations();
	   List<ProductionTime> listOfPrdTime = new ArrayList<>();
	   if(prdTime.contains("wks")){
		   listOfPrdTime = getProductionTime(prdTime);
		   existingConfiguration.setProductionTime(listOfPrdTime);
	   }else if(prdTime.equalsIgnoreCase("Check inventory as limited amount kept in VA")){
		   existingProduct.setAdditionalProductInfo(prdTime);
	   } else if(prdTime.equalsIgnoreCase("In stock; call to confirm in current inventory.")){
		   Inventory inventoryVal = getProductInventory();
		   existingProduct.setInventory(inventoryVal);
		   existingProduct.setAdditionalProductInfo("call to confirm in current inventory");
	   } else if(prdTime.contains("Free Shipping, delivery within")){
		   existingProduct.setAdditionalProductInfo(prdTime);
	   } else if(prdTime.equalsIgnoreCase("1-2 days5 to ship out if we have in stock.")){
		   listOfPrdTime = getPrdTimeValue("1-2");
		   existingConfiguration.setProductionTime(listOfPrdTime);
		   existingProduct.setAdditionalProductInfo("5 days to ship out if we have in stock.");
	   }
	   existingProduct.setProductConfigurations(existingConfiguration);
	   return existingProduct;
   }
   private Inventory getProductInventory(){
	   Inventory inventoryObj = new Inventory();
	   inventoryObj.setInventoryStatus("In Stock");
	   return inventoryObj;
   }
   private List<ProductionTime> getProductionTime(String val){
	   List<ProductionTime> listOfPrdTime = new ArrayList<>();
	   ProductionTime prdTimeObj = new ProductionTime();
	   String[] vals = val.split("wks");
	   String prdTime = vals[0];
	   StringBuilder businessDays = new StringBuilder();
	   if(prdTime.contains("-")){
		   String[] times = prdTime.split("-");
		  int time1 =  Integer.parseInt(times[0].trim())*5;
		  int time2 =  Integer.parseInt(times[1].trim())*5;
		  businessDays.append(time1).append("-").append(time2);
	   } else{
		   int time =  Integer.parseInt(prdTime)*5;
		   businessDays.append(time);
	   }
	   prdTimeObj.setBusinessDays(businessDays.toString());
	   prdTimeObj.setDetails(val);
	   listOfPrdTime.add(prdTimeObj);
	   return listOfPrdTime;
   }
   private List<ProductionTime> getPrdTimeValue(String businessDays){
	   List<ProductionTime> listOfPrdTime = new ArrayList<>();
	   ProductionTime prdTimeObj = new ProductionTime();
	   prdTimeObj.setBusinessDays(businessDays);
	   prdTimeObj.setDetails("");
	   listOfPrdTime.add(prdTimeObj);
	   return listOfPrdTime;
   }
	public LookupServiceData getLookupServiceData() {
		return lookupServiceData;
	}
	public void setLookupServiceData(LookupServiceData lookupServiceData) {
		this.lookupServiceData = lookupServiceData;
	}
	
}
