package com.a4tech.bambam.product.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.AdditionalLocation;
import com.a4tech.product.model.Artwork;
import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Configurations;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintColor;
import com.a4tech.product.model.ImprintColorValue;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Inventory;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.Personalization;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.ProductSKUConfiguration;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.SameDayRush;
import com.a4tech.product.model.Samples;
import com.a4tech.product.model.Shape;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.TradeName;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.a4tech.util.LookupData;

public class BamProductAttributeParser {
   private LookupData lookupData;
   private final static Logger _LOGGER  = Logger.getLogger(BamProductAttributeParser.class);
   private final static String COMBO_VALUES_SEPARATOR = ":";
   private final static Integer COMBO_TEXT_VALUE_INDEX = 1;
   public static final String   CONST_STRING_COMBO_TEXT = "Combo";
   public List<String> getProductLines(String prdLines,Integer supplierNo){
	   List<String> listOfLineNames = null;
	   List<String> lookupLineNames = lookupData.getLineNames(String.valueOf(supplierNo));
	   String lineNameArr[] = prdLines
				.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		if (!CollectionUtils.isEmpty(lookupLineNames)) {
			listOfLineNames = new ArrayList<>();
			for (String lineName : lineNameArr) {
				if (lookupLineNames.contains(lineName)) {
					listOfLineNames.add(lineName);
				}
			}
			return listOfLineNames;
		}
	   return new ArrayList<>();
   }
   public List<Catalog> getCatalogs(String catalogValue) {
		List<Catalog> catalogList = new ArrayList<Catalog>();
		try{
		Catalog catalog = null;
		catalogValue = catalogValue.trim();
		String catalogArr[] = catalogValue.split(",");
	

		for (int i = 0; i <= catalogArr.length - 1; i++) {
			catalog = new Catalog();
			catalogValue = catalogArr[i];
			String[] catalogInfo = catalogValue.split(":");
			if (catalogInfo.length == 3) {
				catalog.setCatalogName(catalogInfo[0]);
				catalog.setCatalogPage(catalogInfo[2]);
			} else if (catalogInfo.length == 2) {
				catalog.setCatalogName(catalogInfo[0]);
				catalog.setCatalogPage("");
			}
			catalogList.add(catalog);
		}
		}
		catch(Exception e){
			_LOGGER.error("Error while processing catalog :"+e.getMessage());
			return new ArrayList<Catalog>();
		}
		return catalogList;
	}
   public List<Color> getColorValues(String color){
		List<Color> colorList=new ArrayList<Color>();
		
		try{
		Color colorObj;
		color = color.trim();
		String colorArr[]=color.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		 boolean isCombo=false;
		 List<Combo> comboList=null;
		 Combo comboObj=new Combo();
		for (String value : colorArr) 
		{
			
			colorObj=new Color();
			//String tempColor=value;
			comboList	=new ArrayList<Combo>();
			
           String originalValue = value;
           String teampValue=value;
           int index = value.indexOf(ApplicationConstants.CONST_STRING_EQUAL);

           if (index != -1) {
               value = value.substring(0, index);
               originalValue = originalValue.substring(index + 1);
           }
           
           // 
           isCombo = isComboColors(value);
           
           if(!isCombo){
           	colorObj.setName(value);
           	colorObj.setAlias(originalValue);
           	colorList.add(colorObj);
           }else{ 
       		String colorArray[]=value.split(ApplicationConstants.CONST_DELIMITER_COLON);
       		
       		String name=colorArray[0];
       		
       		String alias=null;
                int indexAlias = teampValue.indexOf(ApplicationConstants.CONST_STRING_EQUAL);
                
                if (indexAlias != -1) {
               	 alias = teampValue.substring(indexAlias + 1);
                }
                int comboIndex=teampValue.indexOf(CONST_STRING_COMBO_TEXT);
                String comboStr=teampValue.substring(comboIndex+6);//(comboIndex + 6);
                String FinalComboStr=comboStr.substring(0, comboStr.indexOf(ApplicationConstants.CONST_STRING_EQUAL));
                //System.out.println("FinalComboStr "+FinalComboStr);
                	 
                
                String finalarray[]=FinalComboStr.split(ApplicationConstants.CONST_DELIMITER_COLON);
                if(finalarray.length==2){
               	 Color colorObj1=new Color();
               	 colorObj1.setName(name);
               	 colorObj1.setAlias(alias);
               	 
               	 Combo combotemp=new Combo();
               	 combotemp.setName(finalarray[0]);
               	 combotemp.setType(finalarray[1]);
               	 
               	 comboList.add(combotemp);
               	 
               	 colorObj1.setCombos(comboList);
               	 colorList.add(colorObj1);
                }else if(finalarray.length==4){
               	 Color colorObj2=new Color();
               	 colorObj2.setName(name);
                    colorObj2.setAlias(alias);
               	
               	 Combo combo1=new Combo();
               	 Combo combo2=new Combo();
               	 combo1.setName(finalarray[0]);
               	 combo1.setType(finalarray[1]);
               	 combo2.setName(finalarray[2]);
               	 combo2.setType(finalarray[3]);
               	 comboList.add(combo1);
               	 comboList.add(combo2);
               	 colorObj2.setCombos(comboList);
               	 colorList.add(colorObj2);
                }
           }  
		}

		}catch(Exception e){
			_LOGGER.error("Error while processing Color :"+e.getMessage());
        return new  ArrayList<Color>();	
        }
		
		return colorList;
		
	}
	private boolean isComboColors(String value) {
   	boolean result = false;
   	if(value.contains(COMBO_VALUES_SEPARATOR)) {
   		String comboValues[] = value.split(COMBO_VALUES_SEPARATOR);
   		result = (comboValues.length % 2 == 0) && comboValues[COMBO_TEXT_VALUE_INDEX].equalsIgnoreCase( CONST_STRING_COMBO_TEXT);
   	}
   	return result;
   }
	public List<Shape> getShapeCriteria(String shape){
		List<Shape> shapeList=new ArrayList<Shape>();
		try{
		Shape shapeObj;
		String shapeArr[]=shape.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		
		for (String string : shapeArr) {
			shapeObj=new Shape();
			
			shapeObj.setName(string);
			
			shapeList.add(shapeObj);
		}
		}catch(Exception e){
			_LOGGER.error("Error while processing Shape :"+e.getMessage());          
		   	return new ArrayList<Shape>();
		   	
		   }
		return shapeList;
		
	}
    public List<Theme> getThemeCriteria(String theme){
		
		List<Theme> themeList = new ArrayList<>();
		Theme themeObj = null;
		try {
			String themeArr[] = theme
					.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
			for (String tempTheme : themeArr) {
				themeObj = new Theme();
				themeObj.setName(tempTheme);
				themeList.add(themeObj);
			}
			return themeList;
		} catch (Exception e) {
			_LOGGER.error("Error while processing Product Theme :"
					+ e.getMessage());
			return new ArrayList<Theme>();
		}
	}
    public List<TradeName> getTradeNames(String tradename){
		List<TradeName> tradenameList =new ArrayList<>();
		try{
		String tradeNameValue = tradename;
		String tradeArr[] = tradeNameValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		TradeName tradeNameObj = null;
		for (String tempTrade : tradeArr) {
			tradeNameObj = new TradeName();
			tradeNameObj.setName(tempTrade);
 			tradenameList.add(tradeNameObj);
		}
		}catch(Exception e){
			_LOGGER.error("Error while processing Product TradeName :"+e.getMessage());       
		   	return new ArrayList<TradeName>();
		   	
		   }
		return tradenameList;
		
	}
    public List<Origin> getProductOrigins(String origin){
		List<Origin> originList =new ArrayList<>();
		try{ 
		 origin = origin.trim();
		String originArr[] = origin.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		Origin originObj = null;
		for (String tempOrigin : originArr) {
			 originObj = new Origin();
			 originObj.setName(tempOrigin);
 			originList.add(originObj);
		  }
		}catch(Exception e){
			_LOGGER.error("Error while processing Product Origin :"+e.getMessage());            
		   	return new ArrayList<Origin>();
		   	
		   }
		return originList;
		
	}
public List<ImprintMethod> getImprintMethodValues(String imprintValue){
		
		List<ImprintMethod> impmthdList =new ArrayList<ImprintMethod>();
		try{
		String impValue = imprintValue;
		String imprintArr[] = impValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		ImprintMethod imprintObj=null;
		for (String tempImpint : imprintArr) {
			tempImpint = tempImpint.trim();
			String values[] = tempImpint.split(ApplicationConstants.CONST_STRING_EQUAL);
 			imprintObj=new ImprintMethod();
 			imprintObj.setType(values[0]);
 			imprintObj.setAlias(values[1]);
 			impmthdList.add(imprintObj);
		}
		
		}catch(Exception e){
			_LOGGER.error("Error while processing Imprint Method :"+e.getMessage());             
		   	return new ArrayList<ImprintMethod>();
		   	
		   }
		return impmthdList;
		
	}

	public ImprintColor getImprintColorValues(String imprintColorValue) {
		ImprintColor imprintColorObj = new ImprintColor();
		try {
			String impValue = imprintColorValue;
			String imprintArr[] = impValue.split(",");
			List<ImprintColorValue> impcolorValuesList = new ArrayList<ImprintColorValue>();
			imprintColorObj.setType("COLR");
			ImprintColorValue impclrObj = null;

			for (String tempImpint : imprintArr) {
				impclrObj = new ImprintColorValue();
				impclrObj.setName(tempImpint);
				impcolorValuesList.add(impclrObj);
			}
			imprintColorObj.setValues(impcolorValuesList);
		} catch (Exception e) {
			_LOGGER.error("Error while processing Imprint Color :"
					+ e.getMessage());
			return new ImprintColor();

		}

		return imprintColorObj;

	}

	public List<Artwork> getArtworkValues(String artwork) {
		List<Artwork> artworkList = new ArrayList<Artwork>();
		try {

			Artwork artObj = null;
			String artArr[] = artwork
					.split(ApplicationConstants.CONST_STRING_COMMA_SEP);

			for (String tempArt : artArr) {
				artObj = new Artwork();
				String tempValues[] = tempArt
						.split(ApplicationConstants.CONST_DELIMITER_COLON);

				if (tempValues.length == 2) {
					artObj.setValue(tempValues[0]);
					artObj.setComments(tempValues[1]);
				} else if (tempValues.length == 1) {
					artObj.setValue(tempValues[0]);
					artObj.setComments(ApplicationConstants.CONST_STRING_EMPTY);
				}

				artworkList.add(artObj);

			}

		} catch (Exception e) {
			_LOGGER.error("Error while processing Artwork :" + e.getMessage());
			return new ArrayList<Artwork>();

		}
		return artworkList;

	}
	public  List<Personalization> getPersonalizationValues(String personalizevalue) {

		List<Personalization> personaliseList = new ArrayList<Personalization>();
		try{
		String PersonalizationArr[] = personalizevalue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		for (int i = 0; i <= PersonalizationArr.length - 1; i++) {
			Personalization perObj = new Personalization();
			String pers = PersonalizationArr[i];
			String[] temp = null;
			if (pers.contains(ApplicationConstants.CONST_STRING_EQUAL)) {
				temp = pers.split(ApplicationConstants.CONST_STRING_EQUAL);
				perObj.setType(temp[0]);
				perObj.setAlias(temp[1]);
			} else {
				perObj.setType(temp[1]);
			}

			personaliseList.add(perObj);
		}
		}catch(Exception e){
			_LOGGER.error("Error while processing Personalization :"+e.getMessage());
			return new ArrayList<Personalization>();
		}
		return personaliseList;

	}
	public Samples getProductSampleValues(String prodsample, String specSample,
			boolean flag) {
		Samples samplesObj = new Samples();
		try{
		String prodSampleValue = prodsample;
		String specSampleValue = prodsample;

		if (flag) {
			String specSampleArr[] = specSampleValue.split(ApplicationConstants.CONST_DELIMITER_COLON);
			if(specSampleArr.length==2){
				String value=specSampleArr[0];
				if(value.equalsIgnoreCase(ApplicationConstants.CONST_CHAR_Y)){
					samplesObj.setSpecSampleAvailable(true);
					samplesObj.setSpecInfo(specSampleArr[1]);
				}
				else{
					samplesObj.setSpecSampleAvailable(false);
					samplesObj.setSpecInfo(ApplicationConstants.CONST_STRING_EMPTY);
				}
			}
			}else{
				samplesObj.setSpecSampleAvailable(false);
				samplesObj.setSpecInfo(ApplicationConstants.CONST_STRING_EMPTY);
			}
		if (prodSampleValue!=null && !prodSampleValue.isEmpty()) {
			String prodSampleArr[] = prodSampleValue.split(ApplicationConstants.CONST_DELIMITER_COLON);
			if(prodSampleArr.length==2){
				String value=prodSampleArr[0];
				if(value.equalsIgnoreCase(ApplicationConstants.CONST_CHAR_Y)){
					samplesObj.setProductSampleAvailable(true);
					samplesObj.setProductSampleInfo(prodSampleArr[1]);
				}
				else{
					samplesObj.setProductSampleAvailable(false);
					samplesObj.setProductSampleInfo(ApplicationConstants.CONST_STRING_EMPTY);
				}
			}
			}else{
				samplesObj.setProductSampleAvailable(false);
				samplesObj.setProductSampleInfo(ApplicationConstants.CONST_STRING_EMPTY);
			}
		
		specSampleValue=null;
		prodSampleValue=null;
		}catch(Exception e){
			_LOGGER.error("Error while processing Sample Parser :"+e.getMessage());          
		   	return new Samples();
		   	
		   }
		return samplesObj;

	}

	public List<ProductionTime> getProductionTimeValues(String prodTimeValue) {

		List<ProductionTime> prodTimeList = new ArrayList<ProductionTime>();
		try {
			String tempValue = prodTimeValue;
			String regex = null;
			String prodTimetArr[] = tempValue
					.split(ApplicationConstants.CONST_STRING_COMMA_SEP);

			ProductionTime prodTimeObj = null;
			for (String tempProdTime : prodTimetArr) {
				prodTimeObj = new ProductionTime();
				String value = tempProdTime;
				String valueArr[] = value
						.split(ApplicationConstants.CONST_DELIMITER_COLON);

				if (valueArr.length == 2) {
					prodTimeObj.setBusinessDays(valueArr[0]);
					prodTimeObj.setDetails(valueArr[1]);
				} else if (valueArr.length == 1) {
					regex = "\\d+";

					if (valueArr[0].matches(regex)) {
						prodTimeObj.setBusinessDays(valueArr[0]);
						prodTimeObj
								.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
					} else {
						prodTimeObj
								.setBusinessDays(ApplicationConstants.CONST_STRING_EMPTY);
						prodTimeObj.setDetails(valueArr[1]);
					}

				}
				tempValue = null;
				value = null;
				regex = null;
				prodTimeList.add(prodTimeObj);

			}

		} catch (Exception e) {
			_LOGGER.error("Error while processing Production Time :"
					+ e.getMessage());
			return new ArrayList<ProductionTime>();

		}

		return prodTimeList;
	}
	public RushTime getRushTimeValues(String rushTimeValue){
		RushTime rushObj=new RushTime();
		try{ 
		String tempValue = rushTimeValue;
		String rushTimetArr[] = tempValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		List<RushTimeValue> rushValueTimeList =new ArrayList<RushTimeValue>();
		
		RushTimeValue rushValueObj=null;
		for (String tempRushTime : rushTimetArr) {
			
 			rushValueObj=new RushTimeValue();
 			String value=tempRushTime;
 			String valueArr[]=value.split(ApplicationConstants.CONST_DELIMITER_COLON);
 			
 			if(valueArr.length==2){
 				rushValueObj.setBusinessDays(valueArr[0]);
 				rushValueObj.setDetails(valueArr[1]);
 			}else if(valueArr.length==1){
 				String regex = "\\d+";
 				
 				if(valueArr[0].matches(regex)){
 					rushValueObj.setBusinessDays(valueArr[0]);
 					rushValueObj.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
 				}else{
 					rushValueObj.setBusinessDays(ApplicationConstants.CONST_STRING_EMPTY);
 					rushValueObj.setDetails(valueArr[1]);
 				}
 				
 			}
 			
 			
 			rushValueTimeList.add(rushValueObj);
			
		}
 		rushObj.setAvailable(true);
		rushObj.setRushTimeValues(rushValueTimeList);
		}catch(Exception e){
			_LOGGER.error("Error while processing RushTime :"+e.getMessage());             
		   	return new RushTime();
		   	
		   }
		return rushObj;
		
	}
	public SameDayRush getSameDaySeviceValues(String value){
		SameDayRush sdayObj=new SameDayRush();
		try{
		if(value.contains(ApplicationConstants.CONST_DELIMITER_COLON)){
		String samedyArr[]=value.split(ApplicationConstants.CONST_DELIMITER_COLON);
		
		if(samedyArr.length==2 && samedyArr[0].equalsIgnoreCase(ApplicationConstants.CONST_CHAR_Y)){
			sdayObj.setAvailable(true);
			sdayObj.setDetails(samedyArr[1]);
		}else if(samedyArr.length==1 && samedyArr[0].equalsIgnoreCase(ApplicationConstants.CONST_CHAR_Y)){
			sdayObj.setAvailable(true);
			sdayObj.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
		}
		}else if(value.equalsIgnoreCase(ApplicationConstants.CONST_CHAR_Y)){
			sdayObj.setAvailable(true);
			sdayObj.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
		}else{
			sdayObj.setAvailable(false);
			sdayObj.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
			
		}
		
		}catch(Exception e){
			_LOGGER.error("Error while processing SameDay Parser :"+e.getMessage());           
		   	return new SameDayRush();
		   	
		   }
		return sdayObj;
	}
	public List<Packaging> getPackagingValues(String packaging){
		List<Packaging> packagingList =new ArrayList<Packaging>();
		Packaging pack = null;
		try{
		String packagingValue = packaging;
		String packagingArr[] = packagingValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		
		for (String tempPackaging : packagingArr) {
			tempPackaging = CommonUtility.removeCurlyBraces(tempPackaging);
			pack = new Packaging();
			pack.setName(tempPackaging);
 			packagingList.add(pack);
		}
		}catch(Exception e){
			_LOGGER.error("Error while processing Product Packaging :"+e.getMessage());             
		   	return new ArrayList<Packaging>();
		   	
		   } 
		return packagingList;
		
	}
	public ProductSkus getProductRelationSkus(String skuCriteria1, String skuCriteria2,
			   String sku, String skuinventoryLink, String skuinventoryStatus,
			   String skuinventoryQty) {
			  ProductSkus pskuObj=new ProductSkus();
			  try{
			  //List<ProductNumber> pnumberList=new ArrayList<ProductNumber>();
			  
			  //ProductNumber pnumberObj2=new ProductNumber();
			  List<ProductSKUConfiguration> configList=new ArrayList<ProductSKUConfiguration>();
			  List<Object> Value;
			  ProductSKUConfiguration configObj;
			  
			  pskuObj.setSKU(sku);
			  if(skuCriteria1!=null && !skuCriteria1.isEmpty()){
			   String pskuArr[]=skuCriteria1.split(ApplicationConstants.CONST_DELIMITER_COLON);
			   configObj=new ProductSKUConfiguration();
			   Value= new ArrayList<Object>();
			   configObj.setCriteria(LookupData.getCriteriaValue(pskuArr[0]));
			   Value.add(pskuArr[1]);
			   
			   configObj.setValue(Value);
			   configList.add(configObj);
			   
			  }
			  
			  if(skuCriteria2!=null && !skuCriteria2.isEmpty()){
			   String pskuArr[]=skuCriteria2.split(ApplicationConstants.CONST_DELIMITER_COLON);
			   configObj=new ProductSKUConfiguration();
			   Value= new ArrayList<Object>();
			   configObj.setCriteria(LookupData.getCriteriaValue(pskuArr[0]));
			   Value.add(pskuArr[1]);
			   
			   configObj.setValue(Value);
			   configList.add(configObj);
			   
			  }
			  
			  pskuObj.setConfigurations(configList);
			  
			  //pnumberList.add(pnumberObj);
			  Inventory inventory =new Inventory();
			  if(skuinventoryLink!=null && !skuinventoryLink.isEmpty()){
			   inventory.setInventoryLink(skuinventoryLink);
			  }else{
			   inventory.setInventoryLink(ApplicationConstants.CONST_STRING_EMPTY);
			  }
			  if(skuinventoryStatus!=null && !skuinventoryStatus.isEmpty()){
			   inventory.setInventoryStatus(skuinventoryStatus);
			  }else{
			   inventory.setInventoryStatus(ApplicationConstants.CONST_STRING_EMPTY);
			  }
			  if(skuinventoryQty!=null && !skuinventoryQty.isEmpty()){
			   inventory.setInventoryQuantity(skuinventoryQty);
			  }else{
			   inventory.setInventoryQuantity(ApplicationConstants.CONST_STRING_EMPTY);
			  }
			  pskuObj.setInventory(inventory);
			  }catch(Exception e){
				_LOGGER.error("Error while processing Product SKUs :"+e.getMessage());            
			    return new ProductSkus();
			     }
			  return pskuObj;
	}
	public ProductNumber getProductNumbers(String productNumberCriteria1,String productNumberCriteria2,String productNumber ){
		List<ProductNumber> pnumberList=new ArrayList<ProductNumber>();
		ProductNumber pnumberObj=new ProductNumber();
		try{
		List<Configurations> configList=new ArrayList<Configurations>();
		List<Object> Value;
		Configurations configObj;
		/*Map<String, String> criCodeMap=new HashMap<String, String>();
		criCodeMap=CommonUtilites.getMap();*/
		
		
		pnumberObj.setProductNumber(productNumber);
		if(productNumberCriteria1!=null && !productNumberCriteria1.isEmpty()){
			String pnumberArr[]=productNumberCriteria1.split(ApplicationConstants.CONST_DELIMITER_COLON);
			configObj=new Configurations();
			Value= new ArrayList<Object>();
			configObj.setCriteria(LookupData.getCriteriaValue(pnumberArr[0]));
			Value.add(pnumberArr[1]);
			
			configObj.setValue(Value);
			configList.add(configObj);
			
		}
		
		if(productNumberCriteria2!=null && !productNumberCriteria2.isEmpty()){
			String pnumberArr[]=productNumberCriteria2.split(ApplicationConstants.CONST_DELIMITER_COLON);
			configObj=new Configurations();
			Value= new ArrayList<Object>();
			configObj.setCriteria(LookupData.getCriteriaValue(pnumberArr[0]));
			Value.add(pnumberArr[1]);
			
			configObj.setValue(Value);
			configList.add(configObj);
			
		}
		pnumberObj.setConfigurations(configList);
		}catch(Exception e){
			_LOGGER.error("Error while processing Product Number :"+e.getMessage());             
		   	return new ProductNumber();
		   	
		   }
		return pnumberObj;		
	}
	
 public List<Image> getProductImages(String images){
	 List<Image> listOfImages = new ArrayList<>();
	 Image imageObj = null;
	 String imgArr[] = images.split(ApplicationConstants.CONST_DELIMITER_COMMA);
		for(int initialImg =0,rankNum=1;initialImg<=imgArr.length-1;initialImg++,rankNum++)
		{
			imageObj = new Image();
			 if(initialImg == ApplicationConstants.CONST_NUMBER_ZERO){
				 imageObj.setImageURL(imgArr[initialImg]);
				 imageObj.setRank(rankNum);
				 imageObj.setIsPrimary(ApplicationConstants.CONST_BOOLEAN_TRUE);
			 } else if (initialImg == ApplicationConstants.CONST_INT_VALUE_ONE){
				 imageObj.setImageURL(imgArr[initialImg]);
				 imageObj.setRank(rankNum);
				 imageObj.setIsPrimary(ApplicationConstants.CONST_BOOLEAN_FALSE);
		    }
			 listOfImages.add(imageObj);
	   }	 
	 return listOfImages;
 }
 public List<Option> getOptions(String optionTypeValue, String optionNameValue,
			String optionValue, String canOrderValue, String reqOrderValue,
			String optionInfoValue,List<Option> existingOptions) {

		Option optionObj=new Option();
		   try{
		  List<OptionValue> valuesList=new ArrayList<OptionValue>();
		  String optionTypeArr[]=optionTypeValue.split(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
		  String valuesArr[]=optionValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		  OptionValue optionValueObj=null;
		  
		  for (String optionVal : valuesArr) {
			  if(optionVal.contains(ApplicationConstants.SQUARE_BRACKET_OPEN)){
				  optionVal = CommonUtility.removeCurlyBraces(optionVal);
				  if(optionVal.contains(ApplicationConstants.CONST_DELIMITER_COLON)){
					  optionVal = optionVal.split(ApplicationConstants.CONST_DELIMITER_COLON)[1];
				  }
			  }
			  optionValueObj=new OptionValue();
			  optionValueObj.setValue(optionVal);
			  valuesList.add(optionValueObj);
		  }
		  
		  boolean canordeFlag=false;
		  boolean requiredFlag=false;
		  if(canOrderValue.equalsIgnoreCase(ApplicationConstants.CONST_STRING_CAPITAL_Y)){
		   canordeFlag=true;
		  }
		  if(reqOrderValue.equalsIgnoreCase(ApplicationConstants.CONST_STRING_CAPITAL_Y)){
		   requiredFlag=true;
		  }
		  
		  optionObj.setOptionType(optionTypeArr[0]);
		  optionObj.setName(optionNameValue);
		  optionObj.setValues(valuesList);
		  optionObj.setAdditionalInformation(optionInfoValue);
		  optionObj.setCanOnlyOrderOne(canordeFlag);
		  optionObj.setRequiredForOrder(requiredFlag);
		   }catch(Exception e){
			   _LOGGER.error("Error while processing Options :"+e.getMessage());          
		      return existingOptions;
		      
		     }
		   existingOptions.add(optionObj);
		  return existingOptions;	  
}
 
 public List<ImprintSize> getImprintSize(String imprSizeValue){
	 List<ImprintSize> listOfImprSize = new ArrayList<>();
	 ImprintSize imprSizeObj = null;
	 imprSizeValue = CommonUtility.removeCurlyBraces(imprSizeValue).trim();
	 String[] imprSizeValues = imprSizeValue.split(ApplicationConstants.CONST_DELIMITER_COMMA);
	 for (String sizeValue : imprSizeValues) {
		 imprSizeObj = new ImprintSize();
		 imprSizeObj.setValue(sizeValue);
		 listOfImprSize.add(imprSizeObj);
	}
	 return listOfImprSize;
 }
 
	public List<ImprintLocation> getImprintLocation(String imprLoc) {
		List<ImprintLocation> listOfImprLoc = new ArrayList<>();
		ImprintLocation imprLocObj = null;
		imprLoc = CommonUtility.removeCurlyBraces(imprLoc).trim();
		String[] imprLocValues = imprLoc
				.split(ApplicationConstants.CONST_DELIMITER_COMMA);
		for (String locValue : imprLocValues) {
			imprLocObj = new ImprintLocation();
			imprLocObj.setValue(locValue);
			listOfImprLoc.add(imprLocObj);
		}
		return listOfImprLoc;
	}
	
	public List<AdditionalColor> getAdditionalColor(String addColor) {
		List<AdditionalColor> listOfAddColors = new ArrayList<>();
		AdditionalColor addColorObj = null;
		addColor = CommonUtility.removeCurlyBraces(addColor).trim();
		String[] addColors = addColor
				.split(ApplicationConstants.CONST_DELIMITER_COMMA);
		for (String colorName : addColors) {
			addColorObj = new AdditionalColor();
			addColorObj.setName(colorName);
			listOfAddColors.add(addColorObj);
		}
		return listOfAddColors;
	}
	public List<AdditionalLocation> getAdditionalLocation(String addLoc) {
		List<AdditionalLocation> listOfAddLoc = new ArrayList<>();
		AdditionalLocation addLocObj = null;
		addLoc = CommonUtility.removeCurlyBraces(addLoc).trim();
		String[] addLocations = addLoc
				.split(ApplicationConstants.CONST_DELIMITER_COMMA);
		for (String locName : addLocations) {
			addLocObj = new AdditionalLocation();
			addLocObj.setName(locName);
			listOfAddLoc.add(addLocObj);
		}
		return listOfAddLoc;
	}

	public LookupData getLookupData() {
		return lookupData;
	}

	public void setLookupData(LookupData lookupData) {
		this.lookupData = lookupData;
	}
   
	
}
