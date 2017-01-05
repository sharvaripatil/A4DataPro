package com.a4tech.product.broberry.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.dataStore.ProductDataStore;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.broberry.mapping.BroberryExcelMapping;
import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.AvailableVariations;
import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.TradeName;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;
import com.a4tech.product.model.Configurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.util.ApplicationConstants;


public class BroberryProductAttributeParser {
	private static final Logger _LOGGER = Logger.getLogger(BroberryProductAttributeParser.class);
	private LookupServiceData lookupServiceDataObj;
	
	
	public List<Color> getColorCriteria(Set <String> colorSet) {
		Color colorObj = null;
		List<Color> colorList = new ArrayList<Color>();
		Iterator<String> colorIterator=colorSet.iterator();
		try {
			List<Combo> comboList = null;
			
		while (colorIterator.hasNext()) {
			String value = (String) colorIterator.next();
			boolean isCombo = false;
				colorObj = new Color();
				comboList = new ArrayList<Combo>();
    			isCombo = isComboColors(value);
				if (!isCombo) {
					String colorName=ApplicationConstants.COLOR_MAP.get(value.trim());
					if(StringUtils.isEmpty(colorName)){
						colorName=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
					}
					colorObj.setName(colorName);
					colorObj.setAlias(value.trim());
					colorList.add(colorObj);
				} else {
					//245-Mid Brown/Navy
					String colorArray[] = value.split(ApplicationConstants.CONST_DELIMITER_FSLASH);
					String combo_color_1=ApplicationConstants.COLOR_MAP.get(colorArray[0].trim());
					if(StringUtils.isEmpty(combo_color_1)){
						combo_color_1=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
					}
					colorObj.setName(combo_color_1);
					colorObj.setAlias(value);
					
					Combo comboObj = new Combo();
					String combo_color_2=ApplicationConstants.COLOR_MAP.get(colorArray[1].trim());
					if(StringUtils.isEmpty(combo_color_2)){
						combo_color_2=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
					}
					comboObj.setName(combo_color_2.trim());
					comboObj.setType(ApplicationConstants.CONST_STRING_SECONDARY);
					comboList.add(comboObj);
					colorObj.setCombos(comboList);
					colorList.add(colorObj);
				}
		}
		} catch (Exception e) {
			_LOGGER.error("Error while processing Color :" + e.getMessage());
			return new ArrayList<Color>();
		}
		_LOGGER.info("Colors Processed");
		return colorList;
		
	}	
	
	public  List<ProductNumber> getProductNumer(HashMap<String, String> productNumberMap){//productNumberMap
		List<ProductNumber> pnumberList=new ArrayList<ProductNumber>();
		ProductNumber pnumberObj=new ProductNumber();
		try{
		List<Configurations> configList=new ArrayList<Configurations>();
		List<Object> valueObj;
		Configurations configObj;
		 Iterator mapItr = productNumberMap.entrySet().iterator();
		    while (mapItr.hasNext()) {
		    	pnumberObj=new ProductNumber();
		    	configList=new ArrayList<Configurations>();
		        Map.Entry values = (Map.Entry)mapItr.next();
		        pnumberObj.setProductNumber(values.getKey().toString());
		    	configObj=new Configurations();
				valueObj= new ArrayList<Object>();
				configObj.setCriteria("Product Color");
				valueObj.add(values.getValue());
				configObj.setValue(valueObj);
				configList.add(configObj);
				pnumberObj.setConfigurations(configList);
				pnumberList.add(pnumberObj);
		    }
		}catch(Exception e){
			_LOGGER.error("Error while processing Product Number :"+e.getMessage());             
		   	return new ArrayList<ProductNumber>();
		   }
		_LOGGER.info("ProductNumbers Processed");
		return pnumberList;		
	}
	
	private boolean isComboColors(String value) {
		boolean result = false;
		if (value.contains("/")) {
			result = true;
		}
		return result;
	}

public Volume getItemWeight(String FabricWT)
{
	  Volume itemWeight=new Volume();
	  List<Values> valuesList = new ArrayList<Values>(); 
	  List<Value> valueList = new ArrayList<Value>(); 

	  Value valueObj=new Value(); 
	  Values valuesObj=new Values(); 

	  FabricWT=FabricWT.replaceAll("Ounces","");
	  valueObj.setValue(FabricWT.trim());
	  valueObj.setUnit("oz");
	  valueList.add(valueObj);
	  
	  valuesObj.setValue(valueList);
	  valuesList.add(valuesObj);
	  
	  itemWeight.setValues(valuesList); 
	return itemWeight;
	
}

public Size getProductSize(List<String> sizeValues){
		Size size = new Size();
		Apparel appareal = new Apparel();
		List<Value> values=new ArrayList<>();
		Dimension dimensionObj = new Dimension();
		List<Values> valuesList = new ArrayList<Values>();
		Value valueObj=new Value();
		int count=1;
		String sizeArr[]={};
		try{
			sizeValues=BroberryProductAttributeParser.removeDuplicateSizes(sizeValues);
		for (String string : sizeValues) {
			//string=ApplicationConstants.SIZE_MAP.get(string);
			valueObj=new Value();
			if(string.contains("Dimension")){
				sizeArr=string.split(ApplicationConstants.CONST_SIZE_DELIMITER);
				//Apparel-Waist/Inseam
				string=sizeArr[1];
					String DimenArr[] = string.split(ApplicationConstants.CONST_DELIMITER_SEMICOLON);
					List<Value> valuelist=new ArrayList<Value>();
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
					valuesObj.setValue(valuelist);
					valuesList.add(valuesObj);
					dimensionObj.setValues(valuesList);
					size.setDimension(dimensionObj);
			}else{
			if(count==1){
				sizeArr=string.split(ApplicationConstants.CONST_SIZE_DELIMITER);
				//Apparel-Waist/Inseam
				String tempValue=sizeArr[0];
				if(tempValue.contains("Pants")){
					tempValue="Apparel-Waist/Inseam";
				}else if(tempValue.contains("Numbered")){
					tempValue="Standard & Numbered";
				}
				appareal.setType(tempValue);
				valueObj.setValue(sizeArr[1]);
				ProductDataStore.saveSizesBrobery(sizeArr[1]);
				values.add(valueObj);
				
			}else{
			sizeArr=string.split(ApplicationConstants.CONST_SIZE_DELIMITER);
			valueObj.setValue(sizeArr[1]);
			ProductDataStore.saveSizesBrobery(sizeArr[1]);
			values.add(valueObj);
			}
			count=0;
			appareal.setValues(values);
			size.setApparel(appareal);
		}
		}
		}catch(Exception e){
			_LOGGER.error("error while processing sizes" +e.getMessage());
			return new Size();
		}
		_LOGGER.info("Sizes Processed");
		return size;
	}


/*
public  List<Option> getOptions(ArrayList<String> optionValues) {
	List<Option> optionList=new ArrayList<>();
	Option optionObj=new Option();
	   try{
		   List<OptionValue> valuesList=new ArrayList<OptionValue>();
			 OptionValue optionValueObj=null;
			  for (String optionDataValue: optionValues) {
				  optionObj=new Option();
				  valuesList=new ArrayList<OptionValue>();
				  optionValueObj=new OptionValue();
				  optionValueObj.setValue(optionDataValue);
				  valuesList.add(optionValueObj);
				  optionObj.setOptionType("Product");
				 // optionObj.setName("Size Choice "+optionDataValue);
				  optionObj.setName("Style "+optionDataValue);
				  optionObj.setValues(valuesList); 
				  optionObj.setAdditionalInformation("");
				  optionObj.setCanOnlyOrderOne(false);
				  optionObj.setRequiredForOrder(true);
				  optionList.add(optionObj);
			  }
			  
	   }catch(Exception e){
		   _LOGGER.error("Error while processing Options :"+e.getMessage());          
	      return new ArrayList<Option>();
	      
	     }
	  return optionList;
	  
	 }
*/


public  List<Option> getOptions(ArrayList<String> optionValues,Product productExcelObj) {
	List<Option> optionList=new ArrayList<>();
	try{
		
		List<Option> optionExistList=new ArrayList<Option>();
		if(productExcelObj.getProductConfigurations()!=null){
			if(productExcelObj.getProductConfigurations().getOptions()!=null){
			optionExistList=productExcelObj.getProductConfigurations().getOptions();
			}
		}
	
	Option optionObj=new Option();
	   
		   for (Option option : optionExistList) {
			   optionList.add(option);
		}
		   
		   List<OptionValue> valuesList=new ArrayList<OptionValue>();
			 OptionValue optionValueObj=null;
			  for (String optionDataValue: optionValues) {
				  optionValueObj=new OptionValue();
				  optionValueObj.setValue(optionDataValue.trim());
				  valuesList.add(optionValueObj);
			  }
				  optionObj.setOptionType("Product");
				  optionObj.setName("Style");
				  optionObj.setValues(valuesList); 
				  optionObj.setAdditionalInformation("");
				  optionObj.setCanOnlyOrderOne(false);
				  optionObj.setRequiredForOrder(true);
				  optionList.add(optionObj);
			  //}
			  
	   }catch(Exception e){
		   _LOGGER.error("Error while processing Options :"+e.getMessage());          
	      return new ArrayList<Option>();
	      
	     }
	  return optionList;
	  
	 }

	public static  List<String> removeDuplicateSizes(List<String> sizeValues){
		HashSet<String> sizeSet=new HashSet<String>();
		for (String string : sizeValues) {
		string=ApplicationConstants.SIZE_MAP.get(string);
		sizeSet.add(string);
		}
		return new ArrayList<String>(sizeSet);
	}
	
	
	public static  List<String> removeDuplicateSizesForAvail(List<String> sizeValues){
		HashSet<String> sizeSet=new HashSet<String>();
		for (String string : sizeValues) {
		string=ApplicationConstants.SIZE_MAP.get(string);
		String strArr[]=string.split("___");
		sizeSet.add(strArr[1]);
		}
		return new ArrayList<String>(sizeSet);
	}
	
	//public List<Availability> getProductAvailablity(Set<String> parentList,Set<String> childList){
		public List<Availability> getProductAvailablity(HashMap<String, LinkedList<String>> tempMap) {	
		List<Availability> listOfAvailablity = new ArrayList<>();
		try{
		Availability  availabilityObj = new Availability();
		AvailableVariations  AvailableVariObj = null;
		List<AvailableVariations> listOfVariAvail = new ArrayList<>();
		List<Object> listOfParent = null;
		List<Object> listOfChild = null;
		
		for (Map.Entry<String,LinkedList<String>> entry : tempMap.entrySet()) {
		    String ParentValue = entry.getKey();
		    //ParentValue=ApplicationConstants.OPTION_MAP.get(ParentValue);
		    LinkedList<String> childList = entry.getValue();
		    List<String> childListTemp = new ArrayList<String>();
		    for (String string : childList) {
		    	childListTemp.add(ParentValue+ApplicationConstants.CONST_CHAR_SMALL_X+string);
			}
		    ParentValue=ApplicationConstants.OPTION_MAP.get(ParentValue);
		    childListTemp=removeDuplicateSizesForAvail(childListTemp);
		    for (String childValue : childListTemp) {
				 AvailableVariObj = new AvailableVariations();
				 listOfParent = new ArrayList<>();
				 listOfChild = new ArrayList<>();
				 listOfParent.add(childValue);
				 listOfChild.add(ParentValue);
				 AvailableVariObj.setParentValue(listOfParent);
				 AvailableVariObj.setChildValue(listOfChild);
				 listOfVariAvail.add(AvailableVariObj);
			}
		    
		}
		availabilityObj.setAvailableVariations(listOfVariAvail);
		availabilityObj.setParentCriteria(ApplicationConstants.CONST_STRING_SIZE);
		availabilityObj.setChildCriteria("Product Option");
		listOfAvailablity.add(availabilityObj);
		}catch(Exception e){
		   _LOGGER.error("Error while processing Options :"+e.getMessage());          
	   return new ArrayList<Availability>();
	   
		}
		return listOfAvailablity;
		}

		public boolean getCategory(String value){
			boolean flag=false;
			try{
			List<String> listOfLookupCategories = lookupServiceDataObj.getCategories();
			if(listOfLookupCategories.contains(value)){
				flag=true;
			}	
			}
			catch (Exception e) {
				_LOGGER.error("Error while getting Category look up values :"+e.getMessage()); 
				return flag;
			}
			return flag;	
		}

		public Product getExistingProductData(Product existingProduct , ProductConfigurations existingProductConfig){
			
			ProductConfigurations newProductConfigurations=new ProductConfigurations();
			Product newProduct=new Product();
			//PriceGrid newPriceGrid =new PriceGrid();
			
			//if(!StringUtils.isEmpty(productKeywords)){
			List<PriceGrid> newPriceGrid=new ArrayList<PriceGrid>();
			
			List<String> lineNames=existingProduct.getLineNames();
			try{
			if(!CollectionUtils.isEmpty(lineNames)){
				newProduct.setLineNames(lineNames);
			}
			List<Catalog> catalogs=existingProduct.getCatalogs();
			if(!CollectionUtils.isEmpty(catalogs)){
				newProduct.setCatalogs(catalogs);
			}
			List<Image> images=existingProduct.getImages();
			if(!CollectionUtils.isEmpty(images)){
				newProduct.setImages(images);
			}
			
			List<String> productKeywords=existingProduct.getProductKeywords();
			if(!CollectionUtils.isEmpty(productKeywords)){
				newProduct.setProductKeywords(productKeywords);
			}
			String additionalProductInfo=existingProduct.getAdditionalProductInfo();
			if(!StringUtils.isEmpty(additionalProductInfo)){
				newProduct.setAdditionalProductInfo(additionalProductInfo);
			}
			
			List<ImprintMethod> imprintMethods=existingProductConfig.getImprintMethods();
			if(!CollectionUtils.isEmpty(imprintMethods)){
				newProductConfigurations.setImprintMethods(imprintMethods);
			}
			List<Theme> themes=existingProductConfig.getThemes();
			if(!CollectionUtils.isEmpty(themes)){
				newProductConfigurations.setThemes(themes);
			}
			
			 List<TradeName> tradeName=existingProductConfig.getTradeNames();
			 if(!CollectionUtils.isEmpty(tradeName)){
				 newProductConfigurations.setTradeNames(tradeName);
				}
			 
			 List<ProductionTime> productionTime=existingProductConfig.getProductionTime();
			 if(!CollectionUtils.isEmpty(productionTime)){
				 newProductConfigurations.setProductionTime(productionTime);
				}
			
			 List<Option> Options=existingProductConfig.getOptions();///very imp
			 if(!CollectionUtils.isEmpty(Options)){
			 Options=getExistingOption(Options);
			 if(!CollectionUtils.isEmpty(Options)){
				 newProductConfigurations.setOptions(Options);
			 }
			}
			 
			 newPriceGrid= existingProduct.getPriceGrids();//very imp
			 if(!CollectionUtils.isEmpty(newPriceGrid)){
				 newPriceGrid=getUpcharegPriceGrid(newPriceGrid);
				 newProduct.setPriceGrids(newPriceGrid);
			 }
			 
			newProduct.setPriceType(existingProduct.getPriceType());
			newProduct.setProductConfigurations(newProductConfigurations);
			}catch(Exception e){
				_LOGGER.error("Error while processing Existing Product Data " +e.getMessage());
			}
			 _LOGGER.info("Completed processing Existing Data");
			return newProduct;
			
		}
		
		
		public static List<Option> getExistingOption(List<Option> Options){
			List<Option> newOptions=new ArrayList<Option>();
			for (Option option : Options) {
				if(option.getOptionType().equalsIgnoreCase("Shipping")){
					newOptions.add(option);
				}
			}
			return newOptions;
		}
		
		public static List<PriceGrid>  getUpcharegPriceGrid(List<PriceGrid> newPriceGrid ){
			List<PriceGrid> upchgPriceGrid=new ArrayList<PriceGrid>();
			
			for (PriceGrid priceGrid : newPriceGrid) {
				if(priceGrid.getIsBasePrice()==false && priceGrid.getUpchargeType().equalsIgnoreCase("Shipping Charge")){
					upchgPriceGrid.add(priceGrid);
			}
			}
			return upchgPriceGrid;
		
		}
		
		
		
		public LookupServiceData getLookupServiceDataObj() {
			return lookupServiceDataObj;
		}

		public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
			this.lookupServiceDataObj = lookupServiceDataObj;
		}
/*public List<Availability> getProductAvailablity(Set<String> parentList,Set<String> childList){
	List<Availability> listOfAvailablity = new ArrayList<>();
	try{
	Availability  availabilityObj = new Availability();
	AvailableVariations  AvailableVariObj = null;
	List<AvailableVariations> listOfVariAvail = new ArrayList<>();
	List<Object> listOfParent = null;
	List<Object> listOfChild = null;
	for (String ParentValue : parentList) { 
		 for (String childValue : childList) {
			 AvailableVariObj = new AvailableVariations();
			 listOfParent = new ArrayList<>();
			 listOfChild = new ArrayList<>();
			 listOfParent.add(ParentValue);
			 listOfChild.add(childValue);
			 AvailableVariObj.setParentValue(listOfParent);
			 AvailableVariObj.setChildValue(listOfChild);
			 listOfVariAvail.add(AvailableVariObj);
		}
	}
	availabilityObj.setAvailableVariations(listOfVariAvail);
	availabilityObj.setParentCriteria(ApplicationConstants.CONST_STRING_SIZE);
	availabilityObj.setChildCriteria("Product Option");
	listOfAvailablity.add(availabilityObj);
	ProductDataStore.clearSizesBrobery();
	}catch(Exception e){
	   _LOGGER.error("Error while processing Options :"+e.getMessage());          
   return new ArrayList<Availability>();
   
	}
	return listOfAvailablity;
	}
*/	
}
