package parser.highcaliberline;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.AdditionalLocation;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Configurations;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.Personalization;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.SameDayRush;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class HighCaliberAttributeParser {
	private static final Logger _LOGGER = Logger.getLogger(HighCaliberAttributeParser.class);
	private LookupServiceData objLookUpService;
	HighCaliberPriceGridParser highCalPriceGridParser;
	
	
public Product getExistingProductData(Product existingProduct , ProductConfigurations existingProductConfig){
		
		ProductConfigurations newProductConfigurations=new ProductConfigurations();
		Product newProduct=new Product();
		List<String> listCategories=new ArrayList<String>();
		try{
			if(existingProductConfig==null){
				return new Product();
			}
			
			//Image
			List<Image> imagesList=existingProduct.getImages();
			if(!CollectionUtils.isEmpty(imagesList)){
				List<Image> newImagesList=new ArrayList<Image>();
				for (Image image : imagesList) {
					image.setConfigurations( new ArrayList<Configurations>());
					newImagesList.add(image);
				}
				newProduct.setImages(imagesList);
			}
			
			//Categories
			listCategories=existingProduct.getCategories();
			if(!CollectionUtils.isEmpty(listCategories)){
				newProduct.setCategories(listCategories);
			}
		 
			List<String> productKeywords=existingProduct.getProductKeywords();
			if(!CollectionUtils.isEmpty(productKeywords)){
				newProduct.setProductKeywords(productKeywords);
			}
			
			
		newProduct.setProductConfigurations(newProductConfigurations);
		}catch(Exception e){
			_LOGGER.error("Error while processing Existing Product Data " +e.getMessage());
			newProduct.setProductConfigurations(newProductConfigurations);
			return newProduct;
		}
		 _LOGGER.info("Completed processing Existing Data");
		return newProduct;
	}


	public List<PriceGrid> getUpchargeData(String setUpchrgesVal,String repeatUpchrgesVal,Product productExcelObj,ProductConfigurations productConfigObj,List<PriceGrid> priceGrids){
		 // here I need to work on upcharges pricegrids
		 //Set-Up charges upcharges
		try{
		 if(!StringUtils.isEmpty(setUpchrgesVal)){
			 priceGrids=productExcelObj.getPriceGrids();//ithe gadbad hotey
			 /////////
			 if(CollectionUtils.isEmpty(priceGrids)){
				 priceGrids=new ArrayList<PriceGrid>();
			 }
			 ///////
			 int i=priceGrids.size();
			 String tempSetupCharge=setUpchrgesVal;
			 /////////
			 String tempPriceIncludeSetUp="";
			 if(setUpchrgesVal.toLowerCase().contains("per")){
			  tempPriceIncludeSetUp=setUpchrgesVal.substring(setUpchrgesVal.indexOf("per"));
			 }
			 /////////
			 tempSetupCharge=setUpchrgesVal.substring(setUpchrgesVal.indexOf("$")+1, setUpchrgesVal.indexOf("("));
			 if(setUpchrgesVal.toUpperCase().contains("COLOR") || setUpchrgesVal.toUpperCase().contains("LOCATION")){
				 //setUpchrgesVal=setUpchrgesVal.substring(setUpchrgesVal.indexOf("$")+1, setUpchrgesVal.indexOf("("));
				 // here i will send the colorlist of product config to get alias name 
				 List<Color> colorList=productConfigObj.getColors();
				 List<String> tempClrListAlias=new ArrayList<String>();
				 for (Color objColor : colorList) {
					 tempClrListAlias.add(objColor.getAlias());
				}
				 if(!CollectionUtils.isEmpty(colorList)){
					 /*(String listOfPrices, String listOfQuan, String discountCodes,
								String currency, String priceInclude, boolean isBasePrice,
								String qurFlag, String priceName, String criterias,Integer sequence,String upChargeType,String upchargeUsageType,
								List<PriceGrid> existingPriceGrid) */
						for (String finalAlias : tempClrListAlias) {
							priceGrids=highCalPriceGridParser.getPriceGrids(tempSetupCharge,"1","V",
									"USD",tempPriceIncludeSetUp,false,"False", finalAlias,"Product Color", i+1,"Set-up Charge", "Other",priceGrids);
							i++;
						}
					}
			 }else{//this is for imprint methods
				 //here i have to check the imprint method name for the value
				 List<ImprintMethod> listOfImprintMethod=productConfigObj.getImprintMethods();
				 
				 if(!CollectionUtils.isEmpty(listOfImprintMethod)){
					 /*(String listOfPrices, String listOfQuan, String discountCodes,
								String currency, String priceInclude, boolean isBasePrice,
								String qurFlag, String priceName, String criterias,Integer sequence,String upChargeType,String upchargeUsageType,
								List<PriceGrid> existingPriceGrid) */
					 List<String> tempImpListAlias=new ArrayList<String>();
					 tempImpListAlias=getImprintAliasList(listOfImprintMethod);
					 if(!CollectionUtils.isEmpty(tempImpListAlias)){ 
						for (String finalAlias : tempImpListAlias) {
							priceGrids=highCalPriceGridParser.getPriceGrids(tempSetupCharge,"1","V",
									"USD",tempPriceIncludeSetUp,false,"False", finalAlias,"Imprint Method", i+1,"Set-up Charge", "Other",priceGrids);
							i++;
						}
					 }
					}
			 }
		 }
		 
		//Repeat-Up charges upcharges
		 if(!StringUtils.isEmpty(repeatUpchrgesVal)){
			 priceGrids=productExcelObj.getPriceGrids();
			 /////////
			 if(CollectionUtils.isEmpty(priceGrids)){
				 priceGrids=new ArrayList<PriceGrid>();
			 }
			 ///////
			 
			 int i=priceGrids.size();
			 String tempReSetupCharge=repeatUpchrgesVal;
			 /////////
			 String tempPriceIncludeRepeat="";
			 if(setUpchrgesVal.toLowerCase().contains("per")){
				 tempPriceIncludeRepeat=repeatUpchrgesVal.substring(repeatUpchrgesVal.indexOf("per"));
			 }
			 /////////
			 tempReSetupCharge=repeatUpchrgesVal.substring(repeatUpchrgesVal.indexOf("$")+1, repeatUpchrgesVal.indexOf("("));
			 if(repeatUpchrgesVal.toUpperCase().contains("COLOR") || repeatUpchrgesVal.toUpperCase().contains("LOCATION")){
				 //setUpchrgesVal=setUpchrgesVal.substring(setUpchrgesVal.indexOf("$")+1, setUpchrgesVal.indexOf("("));
				 // here i will send the colorlist of product config to get alias name 
				 List<Color> colorList=productConfigObj.getColors();
				 List<String> tempClrListAlias=new ArrayList<String>();
				 for (Color objColor : colorList) {
					 tempClrListAlias.add(objColor.getAlias());
				}
				 if(!CollectionUtils.isEmpty(colorList)){
					 /*(String listOfPrices, String listOfQuan, String discountCodes,
								String currency, String priceInclude, boolean isBasePrice,
								String qurFlag, String priceName, String criterias,Integer sequence,String upChargeType,String upchargeUsageType,
								List<PriceGrid> existingPriceGrid) */
						for (String finalAlias : tempClrListAlias) {
							priceGrids=highCalPriceGridParser.getPriceGrids(tempReSetupCharge,"1","V",
									"USD",tempPriceIncludeRepeat,false,"False", finalAlias,"Product Color", i+1,"Re-order Charge", "Other",priceGrids);
							i++;
						}
					}
			 }else{//this is for imprint methods
				 //here i have to check the imprint method name for the value
				 List<ImprintMethod> listOfImprintMethod=productConfigObj.getImprintMethods();
				 
				 if(!CollectionUtils.isEmpty(listOfImprintMethod)){
					 /*(String listOfPrices, String listOfQuan, String discountCodes,
								String currency, String priceInclude, boolean isBasePrice,
								String qurFlag, String priceName, String criterias,Integer sequence,String upChargeType,String upchargeUsageType,
								List<PriceGrid> existingPriceGrid) */
					 List<String> tempImpListAlias=new ArrayList<String>();
					 tempImpListAlias=getImprintAliasList(listOfImprintMethod);
					 if(!CollectionUtils.isEmpty(tempImpListAlias)){ 
						for (String finalAlias : tempImpListAlias) {
							priceGrids=highCalPriceGridParser.getPriceGrids(tempReSetupCharge,"1","V",
									"USD",tempPriceIncludeRepeat,false,"False", finalAlias,"Imprint Method", i+1,"Re-order Charge", "Other",priceGrids);
							i++;
						}
					 }
					}
			 }
		 }
		}catch(Exception e){
			_LOGGER.error("Error while processing upcharge data "+e.getMessage());
		}
		 return priceGrids;
	}
	
	public LookupServiceData getObjLookUpService() {
		return objLookUpService;
	}

	public void setObjLookUpService(LookupServiceData objLookUpService) {
		this.objLookUpService = objLookUpService;
	}

	public List<Image> getImages(String image){
		List<String> imagesList=new ArrayList<String>();
		imagesList.add(image);
		List<Image> imgList=new ArrayList<Image>();
		int rank=1;
		for (String imageStr : imagesList) {
			Image ImgObj= new Image();
			if(!imageStr.contains("http://")){//http://
				imageStr="http://"+imageStr;
			}
			ImgObj.setImageURL(imageStr);
	        if(rank==1){
	        ImgObj.setRank(rank);
	        ImgObj.setIsPrimary(ApplicationConstants.CONST_BOOLEAN_TRUE);
	        }else{
	        ImgObj.setRank(rank);
	        ImgObj.setIsPrimary(ApplicationConstants.CONST_BOOLEAN_FALSE);
	        }
	        imgList.add(ImgObj);
	        rank++;
		}
		return imgList;
	}
	
	// color parsing
		@SuppressWarnings("unused")
		public List<Color> getProductColors(String color){
			List<Color> listOfColors = new ArrayList<>();
			String colorGroup=null;
			try{
			Color colorObj = null;
			color=color.replaceAll("\\|",",");
			String[] colors =getValuesOfArray(color, ",");
			for (String colorName : colors) {
				if(StringUtils.isEmpty(colorName)){
					continue;
				}
				colorName=colorName.replaceAll("&","/");
				colorName=colorName.replaceAll(" w/","/");
				colorName=colorName.replaceAll(" W/","/");
				//colorName = colorName.trim();
				
				colorObj = new Color();
				 colorGroup = HighCaliberConstants.getColorGroup(colorName.trim());
				/////////////////////////
				if(colorGroup!=null){
				if(colorGroup.toUpperCase().contains("COMBO")){
					colorGroup=colorGroup.replaceAll(":","");
					colorGroup=colorGroup.replace("Combo","/");
					colorName=colorGroup;
				}
				}
				/////////////////////////
				//if (colorGroup != null) {
					//if (colorGroup!=null && colorGroup.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
				//if (colorName.contains("/") || colorGroup.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) { //imp step
				if (colorName.contains("/")) {
					if(colorGroup==null){
						colorGroup=colorName;
					}
					colorGroup=colorGroup.replaceAll("&","/");
					colorGroup=colorGroup.replaceAll(" w/","/");
					colorGroup=colorGroup.replaceAll(" W/","/");
					
					//if (colorName.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
						if(isComboColor(colorGroup)){
							List<Combo> listOfCombo = null;
							String[] comboColors = CommonUtility.getValuesOfArray(colorGroup,
									ApplicationConstants.CONST_DELIMITER_FSLASH);
							String colorFirstName = HighCaliberConstants.getColorGroup(comboColors[0].trim());
							colorObj.setName(colorFirstName == null?"Other":colorFirstName);
							int combosSize = comboColors.length;
							if (combosSize == ApplicationConstants.CONST_INT_VALUE_TWO) {
								String colorComboFirstName = HighCaliberConstants.getColorGroup(comboColors[1].trim());
								colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
								listOfCombo = getColorsCombo(colorComboFirstName, ApplicationConstants.CONST_STRING_EMPTY,
										combosSize);
							} else{
								String colorComboFirstName = HighCaliberConstants.getColorGroup(comboColors[1].trim());
								colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
								
								String colorComboSecondName = HighCaliberConstants.getColorGroup(comboColors[2].trim());
								colorComboSecondName = colorComboSecondName == null?"Other":colorComboSecondName;
								listOfCombo = getColorsCombo(colorComboFirstName,colorComboSecondName, combosSize);
							}
							String alias = colorGroup.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH, "-");
							colorObj.setAlias(alias);
							colorObj.setCombos(listOfCombo);
						} else {
							String[] comboColors = CommonUtility.getValuesOfArray(colorGroup,
									ApplicationConstants.CONST_DELIMITER_FSLASH);
							String mainColorGroup = HighCaliberConstants.getColorGroup(comboColors[0].trim());
							if(mainColorGroup != null){
								colorObj.setName(mainColorGroup);
								colorObj.setAlias(colorName);
							} else {
								colorObj.setName(ApplicationConstants.CONST_VALUE_TYPE_OTHER);
								colorObj.setAlias(colorName);
							}
						}
					/*} else {
						if (colorGroup == null) {
						colorGroup = ApplicationConstants.CONST_VALUE_TYPE_OTHER;
						}
						colorObj.setName(colorGroup);
						colorObj.setAlias(colorName);
					}*/
				} else {
					if (colorGroup == null) {
						colorGroup = ApplicationConstants.CONST_VALUE_TYPE_OTHER;
						}
					colorObj.setName(colorGroup);
					colorObj.setAlias(colorName);
				}
				listOfColors.add(colorObj);
			}
			}catch(Exception e){
				_LOGGER.error("Error while processing color: "+colorGroup+" "+e.getMessage());
			}
			return listOfColors;
		}
		private List<Combo> getColorsCombo(String firstValue,String secondVal,int comboLength){
			List<Combo> listOfCombo = new ArrayList<>();
			Combo comboObj1 = new Combo();
			Combo comboObj2 = new Combo();
			comboObj1.setName(firstValue);
			comboObj1.setType(ApplicationConstants.CONST_STRING_SECONDARY);
			comboObj2.setName(secondVal);
			comboObj2.setType(ApplicationConstants.CONST_STRING_TRIM);
			if(comboLength == ApplicationConstants.CONST_INT_VALUE_TWO){
				listOfCombo.add(comboObj1);
			} else {
				listOfCombo.add(comboObj1);
				listOfCombo.add(comboObj2);
			}
			return listOfCombo;
		}
		
		public static boolean isComboColor(String colorValue){
	    	String[] colorVals = CommonUtility.getValuesOfArray(colorValue, "/");
	    	String mainColor       = null;
	    	String secondaryColor  = null;
	    	String thirdColor      = null;
	    	if(colorVals.length == ApplicationConstants.CONST_INT_VALUE_TWO){
	    		 mainColor = HighCaliberConstants.getColorGroup(colorVals[0].trim());
	    		 secondaryColor = HighCaliberConstants.getColorGroup(colorVals[1].trim());
	    		 if(mainColor != null && secondaryColor != null){
	    			 return true;
	    		 }
	    	} else if(colorVals.length == ApplicationConstants.CONST_INT_VALUE_THREE){
	    		 mainColor      = HighCaliberConstants.getColorGroup(colorVals[0].trim());
	    		 secondaryColor = HighCaliberConstants.getColorGroup(colorVals[1].trim());
	    		 thirdColor     = HighCaliberConstants.getColorGroup(colorVals[2].trim());
	    		 if(mainColor != null && secondaryColor != null && thirdColor != null){
	    			 return true;
	    		 }
	    	} else{
	    		
	    	}
	    	return false;
	    }
		
		public static String[] getValuesOfArray(String data,String delimiter){
			   if(!StringUtils.isEmpty(data)){
				   return data.split(delimiter);
			   }
			   return null;
		   }
	
	
	public ShippingEstimate getShippingEstimates(String shippinglen,String shippingWid,String shippingH, String shippingWeightValue,
			String noOfitem,ShippingEstimate ShipingObj) {
		//ShippingEstimate ItemObject = new ShippingEstimate();
		try{
		List<NumberOfItems> listOfNumberOfItems = new ArrayList<NumberOfItems>();
		List<Weight> listOfWeight = new ArrayList<Weight>();
		NumberOfItems itemObj = new NumberOfItems();
	
			//List<Dimensions> dimenlist = new ArrayList<Dimensions>();
			Dimensions dimensionObj = new Dimensions();
			
				if(!StringUtils.isEmpty(shippinglen.trim())){
				dimensionObj.setLength(shippinglen.trim());
				dimensionObj.setLengthUnit("in");
				ShipingObj.setDimensions(dimensionObj);
				}
				if(!StringUtils.isEmpty(shippingWid.trim())){
				dimensionObj.setWidth(shippingWid.trim());
				dimensionObj.setWidthUnit("in");
				ShipingObj.setDimensions(dimensionObj);
				}
				if(!StringUtils.isEmpty(shippingH.trim())){
				dimensionObj.setHeight(shippingH.trim());
				dimensionObj.setHeightUnit("in");
				ShipingObj.setDimensions(dimensionObj);
				}
				//dimenlist.add(dimensionObj);
				//ShipingObj.setDimensions(dimensionObj);
				
				//shippingWeightValue
				if(!StringUtils.isEmpty(shippingWeightValue.trim())){
					if(shippingWeightValue.equalsIgnoreCase("0") || shippingWeightValue.equalsIgnoreCase("NO")){
					
					}else{
						Weight weightObj = new Weight();
						weightObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_WEIGHT);
						weightObj.setValue(shippingWeightValue);
						listOfWeight.add(weightObj);
						ShipingObj.setWeight(listOfWeight);
					}
				
				}
				
				//shippingNoofItem
				if(!StringUtils.isEmpty(noOfitem.trim())){
					if(noOfitem.equalsIgnoreCase("0") || noOfitem.equalsIgnoreCase("NO")){
						
					}else{
					itemObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_NUMBER_UNIT_CARTON);
					itemObj.setValue(noOfitem);
					listOfNumberOfItems.add(itemObj);
					ShipingObj.setNumberOfItems(listOfNumberOfItems);
					}
			
				}
		}catch(Exception e){
			_LOGGER.error("Error while processing Shipping Estimate :"+e.getMessage());
			return new ShippingEstimate();
		}
		return ShipingObj;

	}
	
	public List<ProductionTime> getProdTimeCriteria(String prodTimeValue,String DetailsValue,List<ProductionTime> prodTimeList){
		//List<ProductionTime> prodTimeList =new ArrayList<ProductionTime>();
		try{
			ProductionTime prodTimeObj= new ProductionTime();
	 					prodTimeObj.setBusinessDays(prodTimeValue);
	 					prodTimeObj.setDetails(DetailsValue);
	 			prodTimeList.add(prodTimeObj);//}
			}catch(Exception e){
			_LOGGER.error("Error while processing Production Time :"+e.getMessage());
	        return new ArrayList<ProductionTime>();
		   }return prodTimeList;
		}
	
	public RushTime getRushTimeValues(String rushTimeValue,String details){
		RushTime rushObj=new RushTime();
		try{ 
		List<RushTimeValue> rushValueTimeList =new ArrayList<RushTimeValue>();
		RushTimeValue rushValueObj=new RushTimeValue();
 		rushValueObj.setBusinessDays(rushTimeValue);
 		rushValueObj.setDetails(details);
 		rushValueTimeList.add(rushValueObj);
 		rushObj.setAvailable(true);
		rushObj.setRushTimeValues(rushValueTimeList);
		}catch(Exception e){
			_LOGGER.error("Error while processing RushTime :"+e.getMessage());             
		   	return new RushTime();
		   	
		   }
		return rushObj;
		
	}
	public List<Packaging> getPackageValues(String packageValues){
		List<Packaging> listOfPackage = new ArrayList<Packaging>();
		Packaging packaging = null;
		String[] packValues = packageValues.split(ApplicationConstants.CONST_DELIMITER_COMMA);
			for (String pack : packValues) {
				packaging = new Packaging();
			   packaging.setName(pack);
			   listOfPackage.add(packaging);
			}
		return listOfPackage;
		
	}
	
	public List<ImprintMethod> getImprintMethods(List<String> listOfImprintMethods){
		List<ImprintMethod> listOfImprintMethodsNew = new ArrayList<ImprintMethod>();
		for (String value : listOfImprintMethods) {
			ImprintMethod imprintMethodObj =new ImprintMethod();
			if(objLookUpService.isImprintMethod(value.toUpperCase())){
				imprintMethodObj.setAlias(value);
				imprintMethodObj.setType(value);
			}else{
				imprintMethodObj.setAlias(value);
				imprintMethodObj.setType("OTHER");
			}
			listOfImprintMethodsNew.add(imprintMethodObj);
		}
		
		
		return listOfImprintMethodsNew;
		
	}
	
	/*public List<ImprintLocation>  getImprintLocationVal(List<String> listOfImprintValues){
		List<ImprintLocation> listOfImprintLoc=new ArrayList<ImprintLocation>();
		for (String value : listOfImprintValues) {
			ImprintLocation imprintLocation = new ImprintLocation();
			  imprintLocation.setValue(value);
			  listOfImprintLoc.add(imprintLocation);
		}
		  return listOfImprintLoc;
	  }*/

	public List<AdditionalLocation>  getOptionalLocationVal(List<String> listOflocValues){
		List<AdditionalLocation> listOfOpLoc=new ArrayList<AdditionalLocation>();
		for (String value : listOflocValues) {
			AdditionalLocation opLocation = new AdditionalLocation();
			opLocation.setName(value);
			listOfOpLoc.add(opLocation);
		}
		  return listOfOpLoc;
	  }
	
	public ProductConfigurations  getImprintLocationVal(String imprintValue,ProductConfigurations productConfigObj){
		List<ImprintLocation> listOfImprintLoc=new ArrayList<ImprintLocation>();
		List<ImprintSize> listOfImprintSize=new ArrayList<ImprintSize>();
		ImprintLocation impLocObj=null;
		ImprintSize impSizeObj=null;
		try{
			if(imprintValue.contains("Helvetica") && imprintValue.contains("0px;>") && imprintValue.contains("</span>")){
				int pos=imprintValue.lastIndexOf("0px;>");
				int pos1=imprintValue.lastIndexOf("</span>");
				imprintValue=imprintValue.substring(pos+1,pos1);
			}
			
			
		imprintValue=imprintValue.replaceAll("<br />", ",");
		imprintValue=imprintValue.replaceAll("<br/>", ",");
		imprintValue=removeSpecialChar(imprintValue);
		imprintValue=imprintValue.replace("(", ",");
		imprintValue=imprintValue.replace(")", ",");
		String strTempArr []=imprintValue.split(",");
		for (String strTemp : strTempArr) {
			if(strTemp.contains(":")){		
				String strValueArr[]=strTemp.split(":");
				if(strValueArr.length>1){
					String loc=strValueArr[0];
					String size=strValueArr[1];
					impLocObj=new ImprintLocation();
				    impSizeObj=new ImprintSize();
				    impLocObj.setValue(loc);
				    listOfImprintLoc.add(impLocObj);
				    impSizeObj.setValue(size);
				    listOfImprintSize.add(impSizeObj);
				}else{
					impLocObj=new ImprintLocation();
					impLocObj.setValue(strTemp);
					listOfImprintLoc.add(impLocObj);
				}
			}else if(strTemp.contains("x")){
				impSizeObj=new ImprintSize();
				impSizeObj.setValue(strTemp);
				listOfImprintSize.add(impSizeObj);
			}else{
				impLocObj=new ImprintLocation();
				impLocObj.setValue(strTemp);
				listOfImprintLoc.add(impLocObj);
			}
		}
		if(CollectionUtils.isEmpty(listOfImprintLoc)){
			productConfigObj.setImprintLocation(listOfImprintLoc);
			}
			if(CollectionUtils.isEmpty(listOfImprintSize)){
			productConfigObj.setImprintSize(listOfImprintSize);
			}
		}catch(Exception e){
			_LOGGER.error("Error while processing imprint area value"+e.getMessage());
			if(CollectionUtils.isEmpty(listOfImprintLoc)){
			productConfigObj.setImprintLocation(listOfImprintLoc);
			}
			if(CollectionUtils.isEmpty(listOfImprintSize)){
			productConfigObj.setImprintSize(listOfImprintSize);
			}
		}
		return productConfigObj;
	  }
	
	public static String removeSpecialChar(String tempValue){
		tempValue=tempValue.replaceAll("(</p>|<p>|&rdquo;|&nbsp;|&ldquo;|<span style=color: #ff0000; font-size: small;>|<span style=color: #ff0000;>|</span style=color: #ff0000;>|<em>|</em>|</strong>|<strong>|</span>|<span>|<p class=p1>)", "");
		tempValue=tempValue.replaceAll("\\(","");
		tempValue=tempValue.replaceAll("\\)","");
	return tempValue;

	}


	public HighCaliberPriceGridParser getHighCalPriceGridParser() {
		return highCalPriceGridParser;
	}


	public void setHighCalPriceGridParser(
			HighCaliberPriceGridParser highCalPriceGridParser) {
		this.highCalPriceGridParser = highCalPriceGridParser;
	}
	public static List<String> getImprintAliasList(List<ImprintMethod> listOfImprintMethod){
		ArrayList<String> tempList=new ArrayList<String>();
		
		for (ImprintMethod tempMthd : listOfImprintMethod) {
			String strTemp=tempMthd.getAlias();
			if(strTemp.contains("LASER")|| strTemp.contains("DIRECT")|| strTemp.contains("VINYL")||strTemp.contains("DYE")||strTemp.contains("HEAT")||strTemp.contains("EPOXY")){
				tempList.add(strTemp);
			}
		}
		return tempList;
		
	}
}
