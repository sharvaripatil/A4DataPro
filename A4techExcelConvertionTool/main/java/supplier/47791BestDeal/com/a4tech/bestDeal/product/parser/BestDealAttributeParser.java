package com.a4tech.bestDeal.product.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.AdditionalLocation;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.Personalization;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.Shape;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.TradeName;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class BestDealAttributeParser {
	private LookupServiceData lookupServiceData;
    private BestDealPriceGridParser priceGridParser;
	
	public List<String> getProductKeywords(String value){
		List<String> listOfKeywords = new ArrayList<>();
			 if(value.contains(ApplicationConstants.CONST_DELIMITER_COMMA)){
				 String[] keyWords = value.split(ApplicationConstants.CONST_DELIMITER_COMMA);
				 for (String key : keyWords) {
					     key = CommonUtility.removeSpecialSymbols(key.trim(), ApplicationConstants.CHARACTERS_NUMBERS_PATTERN);
					     listOfKeywords.add(key);
				}
				 
			 }
		return listOfKeywords;
	}
	
	public List<Color> getProductColor(String colorValue){
		List<Color> listOfColor = new ArrayList<>();
		Color colorObj = new Color();
		if(colorValue.equalsIgnoreCase("Jade")){
			colorObj.setName("Medium Green");
		}else{
			colorObj.setName(colorValue);
		}
		colorObj.setAlias(colorValue);
		listOfColor.add(colorObj);
		return listOfColor;
	}
	public List<Shape> getProductShapes(String shapeValue){
		List<Shape> listOfShape = new ArrayList<>();
		Shape shapeObj = new Shape();
		if(lookupServiceData.isShape(shapeValue.toUpperCase())){
			shapeObj.setName(shapeValue);
			listOfShape.add(shapeObj);
			return listOfShape;
		}
		return null;
	}
 public Size getProductSize(String sizeValue){
	 Size sizeObj = new Size();
	 if(sizeValue.contains(ApplicationConstants.CONST_DELIMITER_HYPHEN)){
		 //sizeValue = CommonUtility.removeSpecialSymbols(sizeValue,"[-DecanterTumbler]").trim();
		 sizeValue = sizeValue.replaceAll("[-DecanterTumbler]", ApplicationConstants.CONST_VALUE_TYPE_SPACE);
	 }
	 sizeValue = sizeValue.replace("\"", "");
	 String[] sizes = sizeValue.split("x");
	 Dimension dimensionObj = new Dimension();
	 List<Values> listOfValues = new ArrayList<>();
	 List<Value> listOfValue   = new ArrayList<>();
	 Values valuesObj = new Values();
	 Value valueObj = null;
	 for(int sizeNum=0;sizeNum<3;sizeNum++){
		      if(sizeNum == 0){
		    	  valueObj = getDimensionValue(sizes[sizeNum], ApplicationConstants.CONST_STRING_INCHES, 
		    			                                             ApplicationConstants.CONST_STRING_LENGTH);
		      } else if(sizeNum == 1){
		    	  valueObj = getDimensionValue(sizes[sizeNum], ApplicationConstants.CONST_STRING_INCHES, 
		    			                                              ApplicationConstants.CONST_STRING_WIDTH);
		      } else if(sizeNum == 2){
		    	  valueObj = getDimensionValue(sizes[sizeNum], ApplicationConstants.CONST_STRING_INCHES, 
		    			                                             ApplicationConstants.CONST_STRING_HEIGHT);
		      }
		      listOfValue.add(valueObj);
	 }
	 valuesObj.setValue(listOfValue);
	 listOfValues.add(valuesObj);
	 dimensionObj.setValues(listOfValues);
	 sizeObj.setDimension(dimensionObj);
	 return sizeObj;
 }
 
 public Value getDimensionValue(String value,String unit,String attribute){
	 Value valueObj = new Value();
	 valueObj.setAttribute(attribute);
	 valueObj.setUnit(unit);
	 valueObj.setValue(value.trim());
	 return valueObj;	 
 }
 
 public List<Material> getProductMaterial(String materialValue){
	 List<Material> listOfMaterial = new ArrayList<>();
	 Material mtrlObj =  new Material();
	 Combo comboObj = null;
	 if(materialValue.contains(ApplicationConstants.CONST_DELIMITER_COMMA)){
		 String[] materils  = materialValue.split(ApplicationConstants.CONST_DELIMITER_COMMA);
		 StringBuilder matlAlias = new StringBuilder();
		 for (int matlIndex =0 ; matlIndex <2;matlIndex++) {
			      String mtrlName = materils[matlIndex];
			      String mtrlType = lookupServiceData.getMaterialTypeValue(mtrlName.toUpperCase());
			      if(matlIndex == 0){
			    	  mtrlObj.setName(mtrlType);
			    	  matlAlias.append(mtrlName).append(ApplicationConstants.CONST_DELIMITER_HYPHEN);
			      }else {
			    	  comboObj = new Combo();
			    	  comboObj.setName(mtrlType);
			    	  matlAlias.append(mtrlName);
			    	  mtrlObj.setCombo(comboObj);
			      }
		 }
		 mtrlObj.setAlias(matlAlias.toString());
	 } else{
		 String mtrlType = lookupServiceData.getMaterialTypeValue(materialValue.toUpperCase());
      	  mtrlObj.setName(mtrlType);
      	  mtrlObj.setAlias(materialValue);
	 }
	 listOfMaterial.add(mtrlObj);
	 return listOfMaterial;
 }
 
 public List<Origin> getProductOrigin(String countryName){
	   List<Origin> listOfOrigin = null;
	   Origin originObj = null;
	   if(lookupServiceData.isOrigin(countryName.toUpperCase())){
		   listOfOrigin = new ArrayList<>();
		   originObj = new Origin();
		   originObj.setName(countryName);
		   listOfOrigin.add(originObj);
	   } 
	 return listOfOrigin;
 }
 
 public List<ProductionTime> getProductionTime(String prdTime){
	 List<ProductionTime> listOfPrdTime = new ArrayList<>();
	 ProductionTime prdTimeObj = new ProductionTime();
	 prdTimeObj.setBusinessDays(prdTime);
	 prdTimeObj.setDetails(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
	 listOfPrdTime.add(prdTimeObj);
	 return listOfPrdTime;
 }
 
 public Product getProductRushTimeAndUpCharge(Product existingProduct, String rushValue,
		 										ProductConfigurations existingConfig,List<PriceGrid> existingPriceGrid){
	 String[] rushVal = rushValue.split(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
	 String rushTime = rushVal[0];
	 RushTime rushTimeValue = getProductRushTime(rushTime);
	 existingConfig.setRushTime(rushTimeValue);
	 List<PriceGrid> priceGrid = getRushTimeUpchargeGrid(rushTime,rushVal,existingPriceGrid);
	  if(priceGrid != null){
		  existingProduct.setPriceGrids(priceGrid);
	  }
	  existingProduct.setProductConfigurations(existingConfig);
	 return existingProduct;
 }
 
	public RushTime getProductRushTime(String rushTime) {
		
		List<RushTimeValue> listOfRushTimeValue = new ArrayList<>();
		RushTimeValue rushTimeValueObj = new RushTimeValue();
		RushTime rushTimeObj = new RushTime();
			rushTimeValueObj.setBusinessDays(rushTime);
			rushTimeValueObj.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
			listOfRushTimeValue.add(rushTimeValueObj);
			rushTimeObj.setRushTimeValues(listOfRushTimeValue);
			rushTimeObj.setAvailable(ApplicationConstants.CONST_BOOLEAN_TRUE);	
	  return rushTimeObj;
	}
	
	public List<PriceGrid> getRushTimeUpchargeGrid(String rushTime,String[] rushVal,List<PriceGrid> existingPriceGrid){
		
		String priceVaue = getRushTimePrice(rushVal);
		String finalRushTime = CommonUtility.appendStrings(rushTime, "business days", ApplicationConstants.CONST_VALUE_TYPE_SPACE);
		if(priceVaue != null){
			 List<PriceGrid> priceGrid = priceGridParser.getUpchargePriceGrid("1", priceVaue, ApplicationConstants.CONST_STRING_DISCOUNT_CODE_Z, 
					  "Rush Service", "n", ApplicationConstants.CONST_STRING_CURRENCY_USD, 
					  finalRushTime, "Rush Service Charge", "Other",1, existingPriceGrid);
			 return priceGrid;
		}
		return null;
	}
	
	private String getRushTimePrice(String[] rushVal){
		for (String price : rushVal) {
			   if(price.contains("$")){
				   return price;
			   }
		}
		return null;
	}
	
	public List<ImprintMethod> getProductImprintMethods(String imprMethodvalue){
		  String imprMethodType = getImprintMethodType(imprMethodvalue);
		  List<ImprintMethod> listOfImprMethod = new ArrayList<>();
		  ImprintMethod imprintMethodObj = new ImprintMethod();
		  imprintMethodObj.setType(imprMethodType);
		  imprintMethodObj.setAlias(imprMethodvalue);
		 listOfImprMethod.add(imprintMethodObj);
		return listOfImprMethod;
	}
	
	private String getImprintMethodType(String value){
		  String imprMethodType = null;
		  if(value.contains("Etch")){
			  imprMethodType = "Etched";
		  } else if(value.contains("UV")){
			  imprMethodType = ApplicationConstants.CONST_VALUE_TYPE_OTHER;
		  } else if(value.contains("Laser Engraving")){
			  imprMethodType = "Laser Engraved";
		  }else{
			  imprMethodType = ApplicationConstants.CONST_VALUE_TYPE_OTHER;
		  }
		return imprMethodType;
	}
	
	public Product getproductLineNameShippingFob(String data,Product existingProduct,ProductConfigurations existingConfig){
		//ProductConfigurations existingConfig = existingProduct.getProductConfigurations();
		 if(data.contains(ApplicationConstants.CONST_DELIMITER_SEMICOLON)){
			 data = data.replaceAll(ApplicationConstants.CONST_DELIMITER_SEMICOLON, 
					                                   ApplicationConstants.CONST_DELIMITER_COMMA);
			 String[] allData = data.split(ApplicationConstants.CONST_DELIMITER_COMMA);
			 String attributeValue = null;
			 for (String value : allData) {
				    value = value.trim();
				    if(value.contains("LINE NAME")){
				    	attributeValue = getFieldName(value, "LINE NAME");
				    	List<String> lineNames = Arrays.asList(attributeValue);
				    	existingProduct.setLineNames(lineNames);
				    } else if(value.contains("SHIPPING WEIGHT")){
				    	attributeValue = getFieldName(value, "SHIPPING WEIGHT");
				    	ShippingEstimate shippingEstObj = getProductShippingWeight(attributeValue);
				    	existingConfig.setShippingEstimates(shippingEstObj);
				    } else if(value.contains("FOB POINT")){
				    	attributeValue = getFieldName(value, "FOB POINT");
				    	List<FOBPoint> listOfFobPoint = getFobPoints(attributeValue);
				    	existingProduct.setFobPoints(listOfFobPoint);
				    } else if(value.contains("PACKAGING")){
				    	attributeValue = getFieldName(value, "PACKAGING");
				    	List<Packaging> listOfPackaging = getProductPackaging(attributeValue);
				    	existingConfig.setPackaging(listOfPackaging);
				    } else if(value.contains("Sold Unimprinted")){
				    	attributeValue = getFieldName(value, "Sold Unimprinted:");
				    	if(attributeValue.equalsIgnoreCase(ApplicationConstants.CONST_STRING_YES)){
				    		List<ImprintMethod> listOfImprintMethod = getImprintMethod("UNIMPRINTED",
				    				                                         existingConfig.getImprintMethods());
				    		existingConfig.setImprintMethods(listOfImprintMethod);
				    	}	
				    } else if(value.contains("Rush Service")){
				    	attributeValue = getFieldName(value, "Rush Service:");
				    	if(attributeValue.equalsIgnoreCase(ApplicationConstants.CONST_STRING_YES)){
				    		continue;
				    	}
				    } else if(value.contains("Personalization")){
				    	attributeValue = getFieldName(value, "Personalization:");
				    	if(attributeValue.equalsIgnoreCase(ApplicationConstants.CONST_STRING_YES)){
				    		List<Personalization> listOfPersonalization = getProductPersonalization(
				    													ApplicationConstants.CONST_STRING_PERSONALIZATION);
				    		existingConfig.setPersonalization(listOfPersonalization);
				    	}
				    	
				    } else if(value.contains("Full Color")){
				    	attributeValue = getFieldName(value, "Full Color Process:");
				    	if(attributeValue.equalsIgnoreCase(ApplicationConstants.CONST_STRING_YES)){
				    		List<ImprintMethod> listOfImprintMethod = getImprintMethod("Full Color",
				    														existingConfig.getImprintMethods());
				    			existingConfig.setImprintMethods(listOfImprintMethod);
				    	}
				    	
				    } else{
				    	
				   }	    
			}
		 }
		 existingProduct.setProductConfigurations(existingConfig);		
		return existingProduct;
	}
	private ShippingEstimate getProductShippingWeight(String weightValue){
		ShippingEstimate shippingEstObj = new ShippingEstimate();
		List<Weight> listOfWeight = new ArrayList<>();
		Weight weightObj = new Weight();
		String[] wtValues = weightValue.split(" ");
		for (String weightData : wtValues) {
			if(StringUtils.isEmpty(weightData)){
				continue;
			}
			 if(!weightData.equalsIgnoreCase("lbs")){
				 weightValue = weightData;
			 }
		}
		weightObj.setValue(weightValue);
		weightObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_WEIGHT);
		listOfWeight.add(weightObj);
		shippingEstObj.setWeight(listOfWeight);
		return shippingEstObj;
	}
	
	private List<FOBPoint> getFobPoints(String value){
		List<FOBPoint> listOfFobPoint = new ArrayList<>();
		FOBPoint fobPointObj = new FOBPoint();
		fobPointObj.setName(value.trim());
		listOfFobPoint.add(fobPointObj);
		return listOfFobPoint;
	}
	private List<Packaging> getProductPackaging(String value){
		   if(value.contains("Gift")){
			   value = "Gift Boxes";
		   }
		List<Packaging> listOfPackaging = new ArrayList<>();
		Packaging packagingObj = new Packaging();
		packagingObj.setName(value);
		listOfPackaging.add(packagingObj);
		return listOfPackaging;
	}
	private List<ImprintMethod> getImprintMethod(String vaue,List<ImprintMethod> existingImprintMethod){
		ImprintMethod imprintMethodObj = new ImprintMethod();
		imprintMethodObj.setType(vaue);
		imprintMethodObj.setAlias(vaue);
		existingImprintMethod.add(imprintMethodObj);
		
		return existingImprintMethod;
	}
	private List<Personalization> getProductPersonalization(String value){
		List<Personalization> listOfPersonalization = new ArrayList<>();
		Personalization personaObj = new Personalization();
		personaObj.setAlias(value);
		personaObj.setType(value);
		listOfPersonalization.add(personaObj);
		return listOfPersonalization;
	}
	
	public Product getImprintSizeAndAddlocation(String value,Product existingProduct){
		ProductConfigurations existingConfig = existingProduct.getProductConfigurations();
	     String[] values = value.split(ApplicationConstants.CONST_DELIMITER_COMMA);
	     for (String name : values) {
			  if(name.contains("IMPRINT SIZE")){
				  List<ImprintSize> listOfImprintSize = getProductImprintSize(name);
				  existingConfig.setImprintSize(listOfImprintSize);
			  } else if(name.contains("IMPRINT METHOD")){
				  continue;
			  } else if(name.contains("Additional Engraving")){
				  List<AdditionalLocation> listOfAddLoc = getProductAdditionalLocation("Additional Engraving Location");
				  existingConfig.setAdditionalLocations(listOfAddLoc);
			  } else{
				  
			  }
		}
		existingProduct.setProductConfigurations(existingConfig);
		
		return existingProduct;
	}
	
	private List<ImprintSize> getProductImprintSize(String imprSize){
		String imprintSizeValue = imprSize.split("IMPRINT SIZE ")[1].trim();
		List<ImprintSize> listOfImprintSize = new ArrayList<>();
		ImprintSize imprSizeObj = new ImprintSize();
		imprSizeObj.setValue(imprintSizeValue);
		listOfImprintSize.add(imprSizeObj);
		return listOfImprintSize;
	}
	 
	private List<AdditionalLocation> getProductAdditionalLocation(String addLocation){
		List<AdditionalLocation> listOfAddLoc = new ArrayList<>();
		AdditionalLocation addLocObj = new AdditionalLocation();
		addLocObj.setName(addLocation);
		listOfAddLoc.add(addLocObj);
		return listOfAddLoc;
		
	}
	public List<PriceGrid> getAdditionalUpchargeGPriceGrid(String name,List<PriceGrid> existingPriceGrid){
		if(name.contains("Additional Engraving")){
			String upChargeUseageType = null;
			if(name.contains("/Piece")){
				upChargeUseageType = "Per Quantity";
			}else{
				upChargeUseageType = "Other";
			}
			existingPriceGrid = priceGridParser.getUpchargePriceGrid("1", "10", "V",
					 "Additional Location", "n", "USD", "Additional Engraving Location", "Add. Location Charge",
					 upChargeUseageType, 1, existingPriceGrid);
		} 
		return existingPriceGrid;
	}
	
	public List<PriceGrid> getLessThanMiniUpCharge(String name,List<PriceGrid> existingPriceGrid){
		//Minimum Order Quantity required per item is indicated as the first column quantity break.
		//There is a $40 net under minimum charge.
		name = name.replaceAll("[^0-9]", "");
		existingPriceGrid = priceGridParser.getUpchargePriceGrid("1", name.trim(), "Z",
				 "Less than Minimum", "n", "USD", "Can order less than minimum", "Less than Minimum Charge",
				 "Other", 1, existingPriceGrid);
		return existingPriceGrid;
	}
	/*author : venkat
	 *description :this method design for find the value of the attribute
	 *   e.x. LINE NAME Best Deal Awards ,value : Best Deal Awards
	 * 
	 */
	private String getFieldName(String value,String splittingValue){//LINE NAME Best Deal Awards
		String[] names = value.split(splittingValue);
		   return names[1];
	}
	
	public List<TradeName> getProductTradeName(String Tradename){
		List<TradeName> listOfTradeNames = new ArrayList<>();
		TradeName tradeNameObj = new TradeName();
		tradeNameObj.setName(Tradename);
		listOfTradeNames.add(tradeNameObj);
		return listOfTradeNames;
	}
 public LookupServiceData getLookupServiceData() {
		return lookupServiceData;
	}

	public void setLookupServiceData(LookupServiceData lookupServiceData) {
		this.lookupServiceData = lookupServiceData;
	}
	
	public BestDealPriceGridParser getPriceGridParser() {
		return priceGridParser;
	}

	public void setPriceGridParser(BestDealPriceGridParser priceGridParser) {
		this.priceGridParser = priceGridParser;
	}

}
