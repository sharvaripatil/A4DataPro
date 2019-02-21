package parser.apparel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.AdditionalLocation;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.Personalization;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductSKUConfiguration;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;
import com.a4tech.supplier.dataStore.SupplierDataStore;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;


public class ApparealProductAttributeParser {
	private LookupServiceData lookupServiceData;
	
	public List<Color> getProductColors1(Set<String> colorNames,Map<String, String> ids){
		 Color colorObj = null;
		Combo comboObj = null;
		 List<Combo> listOfCombo = null;
		 String[] colors;
		 String aliasName =ApplicationConstants.CONST_STRING_EMPTY;
		 List<Color> listtOfColor = new ArrayList<>();
		 for (String colorName : colorNames) {
			 colorObj = new Color();
			 listOfCombo = new ArrayList<>();
			 colorName = colorName.trim();
		    if(colorName.contains(ApplicationConstants.CONST_DELIMITER_FSLASH) || 
		    		colorName.contains(ApplicationConstants.CONST_DELIMITER_HYPHEN)){
		    	if(colorName.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)){
		    		 colors = colorName.split(ApplicationConstants.CONST_DELIMITER_FSLASH);		
		    	} else {
		    		 colors = colorName.split("-");
		    	}
			 String primaryColor = colors[ApplicationConstants.CONST_NUMBER_ZERO];
			 String secondaryColor = colors[ApplicationConstants.CONST_INT_VALUE_ONE];
			int noOfColors = colors.length;
			comboObj = new Combo();
			if(noOfColors == ApplicationConstants.CONST_INT_VALUE_TWO){
				colorObj.setName(ApparelColorMapping.getColorGroup(primaryColor.trim()));
				comboObj.setName(ApparelColorMapping.getColorGroup(secondaryColor));
				comboObj.setType(ApplicationConstants.CONST_STRING_SECONDARY);
				aliasName = CommonUtility.appendStrings(primaryColor, secondaryColor,
                        						ApplicationConstants.CONST_DELIMITER_HYPHEN);
				listOfCombo.add(comboObj);
				colorObj.setAlias(aliasName);
				colorObj.setCombos(listOfCombo);
				SupplierDataStore.saveColorNames(aliasName);
			}else if(noOfColors == ApplicationConstants.CONST_INT_VALUE_THREE){
				String thirdColor = colors[ApplicationConstants.CONST_INT_VALUE_TWO];
				colorObj.setName(primaryColor);
				for(int count=1;count <=2;count++){
					comboObj = new Combo();
					if(count == ApplicationConstants.CONST_INT_VALUE_ONE){
						comboObj.setName(ApparelColorMapping.getColorGroup(secondaryColor));
						comboObj.setType(ApplicationConstants.CONST_STRING_SECONDARY);
						aliasName = CommonUtility.appendStrings(primaryColor, secondaryColor,
                                                   ApplicationConstants.CONST_DELIMITER_HYPHEN);
					}else{
						comboObj.setName(ApparelColorMapping.getColorGroup(thirdColor));
						comboObj.setType(ApplicationConstants.CONST_STRING_TRIM);
						aliasName = CommonUtility.appendStrings(aliasName, thirdColor,
                                                          ApplicationConstants.CONST_DELIMITER_HYPHEN);
					}
					listOfCombo.add(comboObj);
				}
				colorObj.setAlias(aliasName);
				colorObj.setCombos(listOfCombo);
				SupplierDataStore.saveColorNames(aliasName);
				
			}else{
				
			}
			  
		  }else{
			  colorObj.setName(ApparelColorMapping.getColorGroup(colorName));
			  colorObj.setAlias(colorName);
			  //colorObj.setCustomerOrderCode(colorCode);
			  SupplierDataStore.saveColorNames(colorName);
		  }
		    listtOfColor.add(colorObj);
	}
		return listtOfColor;
	}
   
	public List<Color> getProductColors(Set<String> colorNames,Map<String, String> ids){
		List<Color> listOfProductColor = new ArrayList<>();
		Color colorObj = null;
		List<String> processedColorList = new ArrayList<>();
		for (String colorName : colorNames) {
			colorName = colorName.trim();
			if(!processedColorList.contains(colorName)){
				colorObj = new Color();
				if(colorName.contains(ApplicationConstants.CONST_DELIMITER_FSLASH) || colorName.contains("-")){
					if(colorName.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)){
						colorObj = getColorCombo(colorName,ApplicationConstants.CONST_DELIMITER_FSLASH);	
					} else {
						colorObj = getColorCombo(colorName,ApplicationConstants.CONST_DELIMITER_HYPHEN);
					}
					
				} else {
					String colorGroup = ApparelColorMapping.getColorGroup(colorName);
					colorObj.setName(colorGroup);
					colorObj.setAlias(colorName);
				}
			   listOfProductColor.add(colorObj);	
			   processedColorList.add(colorName);
			}
			
		}
		return listOfProductColor;
	}
	private Color getColorCombo(String comboVal,String delimiter){
		Color colorObj = new Color();
		List<Combo> listOfComos = new ArrayList<>();
		Combo comboObj1 = new Combo();
		Combo comboObj2 = new Combo();
		String[] comboColors = CommonUtility.getValuesOfArray(comboVal,delimiter);
		colorObj.setName(
				ApparelColorMapping.getColorGroup(comboColors[ApplicationConstants.CONST_NUMBER_ZERO]));
		comboObj1.setName(
				ApparelColorMapping.getColorGroup(comboColors[ApplicationConstants.CONST_INT_VALUE_ONE]));
		comboObj1.setType(ApplicationConstants.CONST_STRING_SECONDARY);
		colorObj.setAlias(comboVal.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH,
				ApplicationConstants.CONST_DELIMITER_HYPHEN));
		if(comboColors.length == ApplicationConstants.CONST_INT_VALUE_THREE){
			comboObj2.setName(
					ApparelColorMapping.getColorGroup(comboColors[ApplicationConstants.CONST_INT_VALUE_TWO]));
			comboObj2.setType(ApplicationConstants.CONST_STRING_TRIM);
			listOfComos.add(comboObj2);
		} 
		listOfComos.add(comboObj1);
		colorObj.setCombos(listOfComos);
		return colorObj;
	}
	
	
	public Set<Value> getSizeValues(String sizeValue,Set<Value> existingSizeValues){
		Value valueObj = new Value();
		valueObj.setValue(sizeValue);
		existingSizeValues.add(valueObj);
		
		return existingSizeValues;
	}
	
	public Volume getItemWeightvolume(String itemWeightValue){
		List<Value> listOfValue = null;
		List<Values> listOfValues = null;
		Volume volume  = new Volume();
		Values values = new Values();
		Value valueObj = new Value();
		if(!itemWeightValue.equals(ApplicationConstants.CONST_STRING_ZERO)){
			listOfValue = new ArrayList<>();
			listOfValues = new ArrayList<>();
			valueObj.setValue(itemWeightValue);
			valueObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_WEIGHT);
			listOfValue.add(valueObj);
			values.setValue(listOfValue);
			listOfValues.add(values);
			volume.setValues(listOfValues);
		}
		return volume;
	}
	
	public ProductConfigurations getImprintMethod(String data,ProductConfigurations productConfigObj){
		List<ImprintMethod> listOfImprintMethod = new ArrayList<>();
		if(!CollectionUtils.isEmpty(productConfigObj.getImprintMethods())){
			List<ImprintMethod> listOfImprintMethodTemp = productConfigObj.getImprintMethods();
			listOfImprintMethod.addAll(listOfImprintMethodTemp);
		}
		data = data.replaceAll("(&|and|or)", ApplicationConstants.CONST_DELIMITER_COMMA);
		ImprintMethod	imprintMethodObj = null;	
		
		if(data.trim().equalsIgnoreCase("Screen Print (1 color, 1 location only)")){
			data = "Screen Print";
		}
		String[] imprintMethodValues = data.split(ApplicationConstants.CONST_DELIMITER_COMMA);
		for (String imprintMethodName : imprintMethodValues) {
			if(StringUtils.isEmpty(imprintMethodName)){
				continue;
			}
			imprintMethodObj = new ImprintMethod();
			String alias = ApplicationConstants.CONST_STRING_EMPTY;
			if(imprintMethodName.contains("Embroidery")){
				alias = imprintMethodName.trim().toUpperCase();
			imprintMethodName=imprintMethodName.replace("Embroidery", "Embroidered");
		    } else if(imprintMethodName.contains(" Appliqué")){
    		  imprintMethodName=imprintMethodName.replaceAll("é", "e");
    		  alias = imprintMethodName.trim().toUpperCase();
	        } else if(imprintMethodName.contains("Screen Print")){
	        	 alias = imprintMethodName.trim().toUpperCase();
	        	imprintMethodName = "Silkscreen";
	        }
			imprintMethodName = imprintMethodName.trim().toUpperCase();
			  if(lookupServiceData.isImprintMethod(imprintMethodName)){
				  imprintMethodObj.setType(imprintMethodName);
				  if(StringUtils.isEmpty(alias)){
					  imprintMethodObj.setAlias(imprintMethodName);
				  } else{
					  imprintMethodObj.setAlias(alias);
				  }
				  
			  }else{
				  imprintMethodObj.setType(ApplicationConstants.CONST_VALUE_TYPE_OTHER);
				  imprintMethodObj.setAlias(imprintMethodName);
			  }
			  listOfImprintMethod.add(imprintMethodObj);
			  productConfigObj.setImprintMethods(listOfImprintMethod);
		}
		return productConfigObj;
	}
	public ProductConfigurations getProductionTimeList(String prdTime,ProductConfigurations productConfigObj){
		List<ProductionTime> listOfProductionTime = new ArrayList<>();
		ProductionTime proTimeObj = null;
		if(prdTime.contains("Blank: 24 hours")){
			proTimeObj = new ProductionTime();
			proTimeObj.setBusinessDays("1");
			proTimeObj.setDetails("Blank: 24 hours");
			listOfProductionTime.add(proTimeObj);
			productConfigObj.setProductionTime(listOfProductionTime);
			
			////////////////
			List<ImprintMethod> listOfImprintMethod = new ArrayList<>();
			if(!CollectionUtils.isEmpty(productConfigObj.getImprintMethods())){
				List<ImprintMethod> listOfImprintMethodTemp = productConfigObj.getImprintMethods();
				listOfImprintMethod.addAll(listOfImprintMethodTemp);
			}
			ImprintMethod	imprintMethodObj = new ImprintMethod();	
			imprintMethodObj.setType("UNIMPRINTED");
			imprintMethodObj.setAlias("UNIMPRINTED");
			listOfImprintMethod.add(imprintMethodObj);
			productConfigObj.setImprintMethods(listOfImprintMethod);
			////////////////////
			
		}
		if(prdTime.contains("5-10"))
		{
			proTimeObj = new ProductionTime();
			proTimeObj.setBusinessDays("5-10");
			proTimeObj.setDetails("Decorated");
			listOfProductionTime.add(proTimeObj);
			productConfigObj.setProductionTime(listOfProductionTime);
		}
		if(prdTime.contains("7-10"))
		{
			proTimeObj = new ProductionTime();
			proTimeObj.setBusinessDays("7-10");
			proTimeObj.setDetails("Decorated - after proof approval");
			listOfProductionTime.add(proTimeObj);
			productConfigObj.setProductionTime(listOfProductionTime);
		}
		
		return productConfigObj;
	}
	
	public Product setImrintSizeAndLocation(String imprintValue,Product existingProduct){
		  ProductConfigurations productConfigObj = existingProduct.getProductConfigurations();
		  ImprintLocation ImprintLocationObj = new ImprintLocation();
		  ImprintSize ImprintSizeObj = new ImprintSize();
		  List<ImprintSize> ImprintSizeList = null;
		  List<ImprintLocation> ImprintLocationList = new ArrayList<>();
		  AdditionalLocation additionalLoactionObj = null;
		  List<AdditionalLocation> additionalLoaction  = null;
	        imprintValue=imprintValue.replace("other",ApplicationConstants.CONST_VALUE_TYPE_OTHER);
	     if(imprintValue.contains(ApplicationConstants.PARENTHESE_OPEN_SYMBOL))
	     {
	    	 ImprintSizeList = new ArrayList<>();
	     String imprintSize= imprintValue.substring(imprintValue.indexOf("size")+5, imprintValue.indexOf
	    		                                          (ApplicationConstants.PARENTHESE_CLOSE_SYMBOL));
	     ImprintSizeObj.setValue(imprintSize);
	     ImprintSizeList.add(ImprintSizeObj);
	     productConfigObj.setImprintSize(ImprintSizeList);
	     ImprintLocationObj.setValue(imprintValue.substring(ApplicationConstants.CONST_NUMBER_ZERO,imprintValue.indexOf
	    		                                          (ApplicationConstants.PARENTHESE_OPEN_SYMBOL)));
	     ImprintLocationList.add(ImprintLocationObj);
	     
	     }else if(imprintValue.contains(ApplicationConstants.CONST_VALUE_TYPE_OTHER))
	     {
	    	 additionalLoactionObj = new AdditionalLocation();
	    	 additionalLoaction = new ArrayList<>();
	     String  imprintLocation=imprintValue.replace("Standard location:", ApplicationConstants.CONST_STRING_BIG_SPACE);
	     String imprintLocationArr[]=imprintLocation.split(ApplicationConstants.CONST_VALUE_TYPE_OTHER) ;
	     ImprintLocationList = getImprintLocations(imprintLocationArr[0].trim());
	     /*ImprintLocationObj.setValue(imprintLocationArr[0].trim()); 
	     ImprintLocationList.add(ImprintLocationObj);*/
	     additionalLoactionObj.setName(ApplicationConstants.CONST_VALUE_TYPE_OTHER+imprintLocationArr[1].trim());
	     additionalLoaction.add(additionalLoactionObj);
	     productConfigObj.setAdditionalLocations(additionalLoaction); 
	    } else if(imprintValue.contains(ApplicationConstants.CONST_STRING_COMMA_SEP)){
			ImprintLocationList = Arrays.stream(CommonUtility.getValuesOfArray(imprintValue, ApplicationConstants.CONST_STRING_COMMA_SEP)).map(str -> {
				ImprintLocation ImprintLocationObj1 = new ImprintLocation();
				ImprintLocationObj1.setValue(str);
				return ImprintLocationObj1;
			}).collect(Collectors.toList());
	    } else {
	      ImprintLocationObj.setValue(imprintValue);
	      ImprintLocationList.add(ImprintLocationObj);
	      
	     }
	     productConfigObj.setImprintLocation(ImprintLocationList); 
		existingProduct.setProductConfigurations(productConfigObj);
		
		return existingProduct;
	}
	
	public List<Packaging> getProductPackaging(String packValue){
		packValue = packValue.replaceAll(ApplicationConstants.CONST_BK_SLASH,ApplicationConstants.CONST_STRING_EMPTY);
		packValue = packValue.replaceAll(ApplicationConstants.CONST_STRING_NEWLINECHARS,
				                                   ApplicationConstants.CONST_STRING_BIG_SPACE);
		List<Packaging> listOfpackaging = new ArrayList<Packaging>();
		Packaging pack = null;
    	if(packValue.contains("Decorated"))
		{
    		for(int index=1 ;index <=ApplicationConstants.CONST_INT_VALUE_TWO ;index++){
    			 pack = new Packaging();
    			  if(index == ApplicationConstants.CONST_INT_VALUE_ONE){
    				pack = getPackaging("Blank: Individually folded in polybags");  
    			  }else if(index ==ApplicationConstants.CONST_INT_VALUE_TWO){
    				  pack = getPackaging("Decorated: Bulk folded");  
    			  }
    			  listOfpackaging.add(pack);
    		}
		}else{
			pack = getPackaging(packValue);
			listOfpackaging.add(pack);
		}
		return listOfpackaging;
		
	}
	public  Packaging getPackaging(String packName)
	{
		Packaging packObj = new Packaging();
		packObj.setName(packName);
		return packObj;
	}
	
	public List<Personalization> getPersonalizationList(List<Personalization> existingPersList){
		List<Personalization> listOfPersonalization = new ArrayList<>();
		Personalization personalObj = null;
		if(existingPersList != null){
			for (Personalization personalization : existingPersList) {
				personalObj = new Personalization();
				  if(personalization.getType().equalsIgnoreCase("Personalization")  && 
						          personalization.getAlias().equalsIgnoreCase("Personalized")){
					  personalObj.setType(ApplicationConstants.CONST_STRING_PERSONALIZATION);
					  personalObj.setAlias(ApplicationConstants.CONST_STRING_PERSONALIZATION);
					  listOfPersonalization.add(personalObj);
				  }else{
					  listOfPersonalization.add(personalization);
				  }  
			}
		}
		
		return listOfPersonalization;
	}
	public List<ProductSkus> getProductSkus(String colorVal,String sizeVal,String skuVal,
			                     List<ProductSkus> existingSkus){
		ProductSkus productSku = new ProductSkus();
		ProductSKUConfiguration colorSkuConfig = getSkuConfiguration("Product Color", colorVal);
		ProductSKUConfiguration sizeSkuConfig = getSkuConfiguration("Standard & Numbered", sizeVal);
		List<ProductSKUConfiguration> listSkuConfigs = new ArrayList<>();
		listSkuConfigs.add(colorSkuConfig);
		listSkuConfigs.add(sizeSkuConfig);
		productSku.setSKU(skuVal);
		productSku.setConfigurations(listSkuConfigs);
		existingSkus.add(productSku);
		return existingSkus;
		
	}
	private ProductSKUConfiguration getSkuConfiguration(String criteria,String val){
		ProductSKUConfiguration skuConfig = new ProductSKUConfiguration();
		skuConfig.setCriteria(criteria);
		skuConfig.setValue(Arrays.asList(val));
		return skuConfig;
	}
	public Product getExistingProductData(Product existingProduct){
		Product newProduct = new Product();
		ProductConfigurations newConfig = new ProductConfigurations();
		ProductConfigurations oldConfig = existingProduct.getProductConfigurations();
		if(!CollectionUtils.isEmpty(existingProduct.getImages())){
			List<Image> listOfImages = existingProduct.getImages().stream().map(image ->{
				image.setConfigurations(new ArrayList<>());
				return image;
			}).collect(Collectors.toList());
			newProduct.setImages(listOfImages);
		}
		if(!StringUtils.isEmpty(existingProduct.getSummary())){
			newProduct.setSummary(existingProduct.getSummary());
		}
		if(!CollectionUtils.isEmpty(existingProduct.getCategories())){
			newProduct.setCategories(existingProduct.getCategories());
		}
		if(!CollectionUtils.isEmpty(oldConfig.getColors())){
			newConfig.setColors(oldConfig.getColors());
		}
		if(oldConfig.getSizes() != null){
			newConfig.setSizes(oldConfig.getSizes());
		}
		if(!CollectionUtils.isEmpty(oldConfig.getTradeNames())){
			newConfig.setTradeNames(oldConfig.getTradeNames());
		}
		newProduct.setProductConfigurations(newConfig);

		return newProduct;
	}
	public Product getExistingProductData(Product existingProduct,List<ImprintMethod> existingImprintMethods){
		List<PriceGrid> newPriceGrids = new ArrayList<>();
		List<PriceGrid> oldPriceGrid = existingProduct.getPriceGrids();
		if(!CollectionUtils.isEmpty(oldPriceGrid)){
			for (PriceGrid priceGrid : oldPriceGrid) {
				   if(priceGrid.getIsBasePrice()){
					   newPriceGrids.add(priceGrid);
				   } else if(isImprintMethodUpcharge(priceGrid.getPriceConfigurations(),existingImprintMethods)){
					   continue;
				   } else {
					   newPriceGrids.add(priceGrid);
				   }
			}
			existingProduct.setPriceGrids(newPriceGrids);
		} else {
			return existingProduct;
		}
		return existingProduct;
	}
	
	// Author      : Venkat
	private boolean isImprintMethodUpcharge(List<PriceConfiguration> priceConfig,List<ImprintMethod> imprMethods){
		 for (PriceConfiguration priceConfiguration : priceConfig) {
			   if(priceConfiguration.getCriteria().equalsIgnoreCase("Imprint Method")){
				   for (ImprintMethod imprintMethodValue : imprMethods) {
					   if(priceConfiguration.getValue().contains(imprintMethodValue.getAlias())){
						   return  false;
					   }
				}
			   } else{
				   return false;
			   }
		}
		 return true;
	}
	public List<String> getProductCategories(String categoryVal){
		List<String> listOfCategories = new ArrayList<>();
		if(lookupServiceData.isCategory(categoryVal.trim())){
			listOfCategories.add(categoryVal);
		}
		return listOfCategories;
	}
	private List<ImprintLocation> getImprintLocations(String value){
		List<ImprintLocation> listOfImprintLoc = new ArrayList<>();
		ImprintLocation imprLocObj = null;
		value = value.replaceAll("\\.", ApplicationConstants.CONST_STRING_EMPTY);
		String[] locations = CommonUtility.getValuesOfArray(value, ",");
		for (String location : locations) {
			imprLocObj = new ImprintLocation();
			imprLocObj.setValue(location);
			listOfImprintLoc.add(imprLocObj);
		}
		return listOfImprintLoc;
	}
	public LookupServiceData getLookupServiceData() {
		return lookupServiceData;
	}

	public void setLookupServiceData(LookupServiceData lookupServiceData) {
		this.lookupServiceData = lookupServiceData;
	} 
}
