package parser.primeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.AvailableVariations;
import com.a4tech.product.model.BatteryInformation;
import com.a4tech.product.model.Capacity;
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
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.Personalization;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.SameDayRush;
import com.a4tech.product.model.Shape;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class PrimeLineAttributeParser {
	
	private static final Logger _LOGGER = Logger.getLogger(PrimeLineAttributeParser.class);
	private static LookupServiceData lookupServiceDataObj;
	
	public Product featureCritriaparser(Product featureExcelObj,ProductConfigurations featureProductconfigs,String detailType,String detail){
		try{
		if(detailType.toUpperCase().contains("IMPRINT AREA")){
		List<ImprintSize> listOfImprintSize=featureProductconfigs.getImprintSize();
		if(CollectionUtils.isEmpty(listOfImprintSize)){
				listOfImprintSize=new ArrayList<ImprintSize>();
		}
		  listOfImprintSize=getProductImprintSize(detail,listOfImprintSize);
		  featureProductconfigs.setImprintSize(listOfImprintSize);
		
		}else if(detailType.toUpperCase().contains("IMPRINT METHOD")){//ignore for item feature tab
			/*List<ImprintMethod> listOfImprintMethods=featureProductconfigs.getImprintMethods();
			if(CollectionUtils.isEmpty(listOfImprintMethods)){
				listOfImprintMethods=new ArrayList<ImprintMethod>();
			}
			PrimeLineAttributeParser.getImprintMethods(detail, listOfImprintMethods);
			//productconfigs.setImprintMethods(imprintMethods);
		*/
		}else if(detailType.toUpperCase().contains("KEYWORDS")){
			ArrayList<String> productKeywordsOld=(ArrayList<String>) featureExcelObj.getProductKeywords();
			ArrayList<String> productKeywordsNew = (ArrayList<String>) CommonUtility.getStringAsList(detail,ApplicationConstants.CONST_DELIMITER_COMMA);
			if(!CollectionUtils.isEmpty(productKeywordsOld)){
				productKeywordsNew.addAll(productKeywordsOld);
			}
			featureExcelObj.setProductKeywords(productKeywordsNew);
		}else if(detailType.toUpperCase().contains("NOTE") || detailType.toUpperCase().contains("PRICE NOTE") || detailType.toUpperCase().contains("IDEAS") 
				|| detailType.toUpperCase().contains("ARTWORK")){
			//String distriComments="";
			String distriCommentsTemp=	featureExcelObj.getDistributorOnlyComments();
			if(!StringUtils.isEmpty(distriCommentsTemp)){
				distriCommentsTemp=distriCommentsTemp+detail;
				detail=distriCommentsTemp;
			}
			detail=CommonUtility.removeRestrictSymbols(detail);
			featureExcelObj.setDistributorOnlyComments(detail);
		}else if(detailType.toUpperCase().contains("PACKAGING")){
			List<Packaging> listOfPackaging=featureProductconfigs.getPackaging();
			if(CollectionUtils.isEmpty(listOfPackaging)){
				listOfPackaging=new ArrayList<Packaging>();
			}
			listOfPackaging=getPackaging(detail, listOfPackaging);
			featureProductconfigs.setPackaging(listOfPackaging);
		}else if(detailType.toUpperCase().contains("DELIVERY")){//prod time //data is invalid not able to recognise the time from it
			//IGNORE for item feature tab
		}else if(detailType.toUpperCase().contains("BATTERIES")){//battery //same goes for battery need to confirm with them
			List<BatteryInformation> batteryOld=featureProductconfigs.getBatteryInformation();
			List<BatteryInformation> batteryNew=new ArrayList<BatteryInformation>(); 
			 batteryNew = getBatteyInfo(detail, batteryNew);
			if(!CollectionUtils.isEmpty(batteryOld)){
				batteryNew.addAll(batteryOld);
			}
			featureProductconfigs.setBatteryInformation(batteryNew);
		}else if(detailType.toUpperCase().contains("SIZE")){//size //IGNORE for item feature
			// if sizes are not present in itemmaster tab den only assign it to the product.
			//sizes are very complex will be done at last
			//Size newSize=featureProductconfigs.getSizes();
			//newSize.
			//if(newSize.toString().equals("{}")){// i am looking for some other alternative
			//if(newSize!=null){// i am looking for some other alternative
			//newSize.s
			//}
		}else if(detailType.toUpperCase().contains("COLORS")){//colors
			// if colors are not present in itemmaster tab den only assign it to the product.
			List<Color> colorList =featureProductconfigs.getColors(); 
			if(CollectionUtils.isEmpty(colorList)){
				List<Color> colorListNew = new ArrayList<Color>();
				colorListNew=PrimeLineAttributeParser.getColor(detail,colorListNew);//here send value of detail as list
				featureProductconfigs.setColors(colorListNew);
			}
		}else if(detailType.toUpperCase().contains("CARTRIDGE")){//option
			// this is little tricky// if option is present in itemmaster tab keep that
			List<Option> listOfOption=featureProductconfigs.getOptions();
			// create option criteria over here
						if(CollectionUtils.isEmpty(listOfOption)){
						List<Option> listOfOptionNew =new ArrayList<Option>();			
						List<String> opntnList=new ArrayList<String>();
						String tempStrArr[]=detail.split(",");
						if(tempStrArr.length>1){
							opntnList=CommonUtility.getStringAsList(detail);
							listOfOptionNew =	getOptions(opntnList,"Ink Cartridge");
							featureProductconfigs.setOptions(listOfOptionNew);
						}else{//set value for additional product info
							String additionalInfo=	featureExcelObj.getAdditionalProductInfo();
							if(!StringUtils.isEmpty(additionalInfo)){
								additionalInfo=additionalInfo+detail;
								detail=additionalInfo;
							}
							featureExcelObj.setAdditionalProductInfo(detail);
						}
						}
		    }else if(detailType.toUpperCase().contains("ROMANCE")){
			    String descValue=	featureExcelObj.getDescription();
			    if(!StringUtils.isEmpty(descValue)){
				descValue=descValue+detail;
				detail=descValue;
			    }
			detail=CommonUtility.removeRestrictSymbols(detail);
			int length=detail.length();
			 if(length>800){
				String strTemp=detail.substring(0, 800);
				int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
				detail=(String) strTemp.subSequence(0, lenTemp);
			}
			featureExcelObj.setDescription(detail);
		}
		featureExcelObj.setProductConfigurations(featureProductconfigs);
	}catch(Exception e){
		_LOGGER.error("Error while processing "+detailType +" for value "+detail );
		featureExcelObj.setProductConfigurations(featureProductconfigs);
	}
		return featureExcelObj;
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
	
	public List<Image> getImages(String imagesVal){
		List<String> imagesList=new ArrayList<String>();
		imagesList.add(imagesVal);
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
	
	//Shape
	public List<Shape> getProductShapes(List<String> listShp){
		List<Shape> listOfShape = new ArrayList<>();
		Shape shapeObj = new Shape();
		for (String shape : listShp) {
			if(lookupServiceDataObj.isShape(shape.toUpperCase())){
				shapeObj = new Shape();
				shapeObj.setName(shape);
				listOfShape.add(shapeObj);
			}
		}
		return listOfShape;
	}
	
	///options
		public static List<Option> getOptions(List<String> opntnList,String optionNamee) {
			List<Option> optionList=new ArrayList<>();
			try{
				   Option optionObj=new Option();
				   List<OptionValue> valuesList=new ArrayList<OptionValue>();
					 OptionValue optionValueObj=null;
					  for (String optionDataValue: opntnList) {
						  optionValueObj=new OptionValue();
						  optionValueObj.setValue(optionDataValue.trim());
						  valuesList.add(optionValueObj);
					  }
						  optionObj.setOptionType("Product");
						  optionObj.setName(optionNamee.trim());
						  optionObj.setValues(valuesList); 
						  optionObj.setAdditionalInformation("");
						  optionObj.setCanOnlyOrderOne(false);
						  optionObj.setRequiredForOrder(true);
						  optionList.add(optionObj);
			   }catch(Exception e){
				   _LOGGER.error("Error while processing Options :"+e.getMessage());          
			      return new ArrayList<Option>();
			      
			     }
			  return optionList;
			  
		 }
	
				//////sizes
				public Size getSizes(List<String> sizList)
				{
					Size sizeObj = new Size();
					try{
						List<Value> capacityvalueList = new ArrayList<Value>();
						Capacity capacityObj = new Capacity();
					for (String sizeStr : sizList) {
			 			String capacityArr[] = sizeStr.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
			               for (String value : capacityArr) {
			 				//String capacityArr1[] = value.split(ApplicationConstants.CONST_DELIMITER_COLON);
			            	value=value.toUpperCase().replaceAll("GB", "");
			 				Value valObjc = new Value();
			 				valObjc.setValue(value.trim());
			 				valObjc.setUnit("GB");
			 				capacityvalueList.add(valObjc);
			 			}
					}
					capacityObj.setValues(capacityvalueList);
					sizeObj.setCapacity(capacityObj);
				}catch(Exception e){
					_LOGGER.error("Error while processing sizes");
					return new Size();
				}
					return sizeObj;
				}
		
		
	public Product getExistingProductData(Product existingProduct , ProductConfigurations existingProductConfig,String accessToken){
		
		ProductConfigurations newProductConfigurations=new ProductConfigurations();
		Product newProduct=new Product();
		//PriceGrid newPriceGrid =new PriceGrid();
		//if(!StringUtils.isEmpty(productKeywords)){
		List<String> listCategories=new ArrayList<String>();
		try{
			if(existingProductConfig==null){
				return new Product();
			}
			//Material
			List<Material> listMaterial= existingProductConfig.getMaterials();
			if(!CollectionUtils.isEmpty(listMaterial)){
				newProductConfigurations.setMaterials(listMaterial);
			}
			//Image
			List<Image> imagesList=existingProduct.getImages();
			if(!CollectionUtils.isEmpty(imagesList)){
				List<Image> newImagesList=new ArrayList<Image>();
				for (Image image : imagesList) {
					image.setConfigurations( new ArrayList<Configurations>());
					newImagesList.add(image);
				}
				//newProduct.setImages(images);
			}
			//Rush
			if(existingProductConfig.getRushTime()!=null){
			RushTime rushTime=existingProductConfig.getRushTime();
			if(rushTime.isAvailable()){
				newProductConfigurations.setRushTime(rushTime);
			}
			}
			//Production
			List<ProductionTime> productionTime=existingProductConfig.getProductionTime();
			if(!CollectionUtils.isEmpty(productionTime)){
				 newProductConfigurations.setProductionTime(productionTime);
			}
			//Same Day
			if(existingProductConfig.getSameDayRush()!=null){
			SameDayRush sameDayRush=existingProductConfig.getSameDayRush();
			if(sameDayRush.isAvailable()){
				newProductConfigurations.setSameDayRush(sameDayRush);
			}
			}
			//Sold Unimprinted
			List<ImprintMethod> imprintMethods=existingProductConfig.getImprintMethods();
			if(!CollectionUtils.isEmpty(imprintMethods)){// i have to keep unimprinted over here
				for (ImprintMethod imprintMethod : imprintMethods) {
					if(imprintMethod.getType().equalsIgnoreCase("UNIMPRINTED")){
						List<ImprintMethod> imprintMethodList=new ArrayList<ImprintMethod>();
						imprintMethodList.add(imprintMethod);
						newProductConfigurations.setImprintMethods(imprintMethodList);
					}
				}
			}
			
			//Personalization
			List<Personalization>    listPersonalization=existingProductConfig.getPersonalization();
			if(!CollectionUtils.isEmpty(listPersonalization)){
				newProductConfigurations.setPersonalization(listPersonalization);
			}
			
			//Categories
			listCategories=existingProduct.getCategories();
			if(!CollectionUtils.isEmpty(listCategories)){
				newProduct.setCategories(listCategories);
			}
		 
			String descValue=existingProduct.getDescription();
			if(!StringUtils.isEmpty(descValue) ){//
				newProduct.setDescription(descValue);
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
	
	public ShippingEstimate getShippingEstimates(String shippingitemValue,
		String shippingWeightValue,String dimLen,String dimHieght,String dimWidth) {
		ShippingEstimate shippObject = new ShippingEstimate();
		try{
		List<NumberOfItems> numberOfItems=new ArrayList<NumberOfItems>();
		List<Weight>       weightList=new ArrayList<Weight>();
		if(!StringUtils.isEmpty(shippingitemValue) && !shippingitemValue.equalsIgnoreCase("NULL")){//
			NumberOfItems itemObj = new NumberOfItems();
			itemObj.setValue(shippingitemValue);
			itemObj.setUnit("per Case");
			numberOfItems.add(itemObj);
			shippObject.setNumberOfItems(numberOfItems);
		}

		if(!StringUtils.isEmpty(shippingWeightValue) && !shippingitemValue.equalsIgnoreCase("NULL")){
			Weight weightObj = new Weight();
			shippingWeightValue=shippingWeightValue.replaceAll("lbs", "").trim();
			weightObj.setValue(shippingWeightValue);
			weightObj.setUnit("lbs");
			weightList.add(weightObj);
			shippObject.setWeight(weightList);
		}

			List<Dimensions> dimenlist = new ArrayList<Dimensions>();
			Dimensions dimensionObj = new Dimensions();
			if(!StringUtils.isEmpty(dimHieght) && !shippingitemValue.equalsIgnoreCase("NULL")){
			dimensionObj.setHeight(dimHieght);
			dimensionObj.setHeightUnit("in");
			}
			if(!StringUtils.isEmpty(dimLen) && !shippingitemValue.equalsIgnoreCase("NULL")){
			dimensionObj.setLength(dimLen);
			dimensionObj.setLengthUnit("in");
			}
			if(!StringUtils.isEmpty(dimWidth) && !shippingitemValue.equalsIgnoreCase("NULL")){
			if(dimWidth.contains("(")){
				dimWidth=dimWidth.replaceAll("\\(.*?\\)", "").trim();
			}
			dimensionObj.setWidth(dimWidth);
			dimensionObj.setWidthUnit("in");
			}
			dimenlist.add(dimensionObj);
			shippObject.setDimensions(dimensionObj);
		}catch(Exception e){
			_LOGGER.error("Error while processing Shipping Estimate :"+e.getMessage());
			return new ShippingEstimate();
		}
		return shippObject;
	}
	
	public List<String> getCategories(String category,List<String> listOfCategories){
		   //List<String> listOfCategories = new ArrayList<>();
		  // String[] categories = CommonUtility.getValuesOfArray(category, ApplicationConstants.CONST_STRING_COMMA_SEP);
		  // for (String categoryName : categories) {
			   if(lookupServiceDataObj.isCategory(category.toUpperCase())){
				   listOfCategories.add(category);
			   }
		//}
		   return listOfCategories;
	}
	
	public List<Material> getMaterials(List<String> materialList){
		
		List<Material> listOfProductMaterial = new ArrayList<Material>();
		Material materialObj = null;
		//if(!StringUtils.isEmpty(productMaterial)){
		for (String productMaterial : materialList) {
			String tempStr=productMaterial;
			materialObj = new Material();
			productMaterial=getTypeOfMaterial(productMaterial);
			//materialObj.setName(productMaterial);
			materialObj.setName("Leather");// i have to work on this
			materialObj.setAlias(tempStr);
			listOfProductMaterial.add(materialObj);
		}//}
		return listOfProductMaterial;
	}
	
	private String getTypeOfMaterial(String matrlVal){
	   	  List<String> lookupMaterials = lookupServiceDataObj.getMaterialValues();
	   	  for (String mtrlLookupName : lookupMaterials) {
	   		   if(matrlVal.contains(mtrlLookupName)){
	   			   return mtrlLookupName;
	   		   }		  
	   	}
	   	  if(matrlVal.contains("FABRIC")){
	   		  return "Other Fabric";
	   	  }
	   	  return ApplicationConstants.CONST_VALUE_TYPE_OTHER;
	   }

	private static List<ImprintSize> getProductImprintSize(String imprSize,List<ImprintSize> listOfImprintSize){
		//String imprintSizeValue = imprSize.split("IMPRINT SIZE ")[1].trim();
		//List<ImprintSize> listOfImprintSize = new ArrayList<>();
		ImprintSize imprSizeObj = new ImprintSize();
		imprSizeObj.setValue(imprSize);
		listOfImprintSize.add(imprSizeObj);
		return listOfImprintSize;
	}
	
	
	public static List<Color> getColor(String colorValue,List<Color> colorList){
		if(!StringUtils.isEmpty(colorValue)){
			String tempcolorArray[]=colorValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
			Color colorObj=null;
			List<Combo> comboList = null;
			for (String colorVal : tempcolorArray) {
				colorObj = new Color();
			String strColor=colorVal;
			strColor=strColor.replaceAll("&","/");
			strColor=strColor.replaceAll(" w/","/");
			strColor=strColor.replaceAll(" W/","/");
			boolean isCombo = false;
				colorObj = new Color();
				comboList = new ArrayList<Combo>();
    			isCombo = isComboColors(strColor);
				if (!isCombo) {
					String colorName=PrimeLineConstants.PRIMECOLOR_MAP.get(strColor.trim());
					if(StringUtils.isEmpty(colorName)){
						colorName=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
					}
					colorObj.setName(colorName);
					colorObj.setAlias(colorVal.trim());
					colorObj.setCustomerOrderCode("");
					colorList.add(colorObj);
				} else {
					//245-Mid Brown/Navy
					String colorArray[] = strColor.split(ApplicationConstants.CONST_DELIMITER_FSLASH);
					//if(colorArray.length==2){
					String combo_color_1=PrimeLineConstants.PRIMECOLOR_MAP.get(colorArray[0].trim());
					if(StringUtils.isEmpty(combo_color_1)){
						combo_color_1=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
					}
					colorObj.setName(combo_color_1);
					colorObj.setAlias(strColor);
					
					Combo comboObj = new Combo();
					String combo_color_2=PrimeLineConstants.PRIMECOLOR_MAP.get(colorArray[1].trim());
					if(StringUtils.isEmpty(combo_color_2)){
						combo_color_2=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
					}
					comboObj.setName(combo_color_2.trim());
					comboObj.setType(ApplicationConstants.CONST_STRING_SECONDARY);
					if(colorArray.length==3){
						String combo_color_3=PrimeLineConstants.PRIMECOLOR_MAP.get(colorArray[2].trim());
						if(StringUtils.isEmpty(combo_color_3)){
							combo_color_3=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
						}
						Combo comboObj2 = new Combo();
						comboObj2.setName(combo_color_3.trim());
						comboObj2.setType(ApplicationConstants.CONST_STRING_TRIM);
						comboList.add(comboObj2);
					}
					comboList.add(comboObj);
					colorObj.setCombos(comboList);
					colorObj.setCustomerOrderCode("");
					colorList.add(colorObj);
				 	}
				}
			}
		return colorList;
	}
	
	private static boolean isComboColors(String value) {
		boolean result = false;
		if (value.contains("/")) {
			result = true;
		}
		return result;
	}
	
	public List<ImprintMethod> getImprintMethods(List<String> listOfImprintMethods){
		List<ImprintMethod> listOfImprintMethodsNew = new ArrayList<ImprintMethod>();
		for (String value : listOfImprintMethods) {
			ImprintMethod imprintMethodObj =new ImprintMethod();
			if(lookupServiceDataObj.isImprintMethod(value)){
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
	
	public List<ImprintSize> getProductImprintSize(List<String> listOfImprintSize){
		//String imprintSizeValue = imprSize.split("IMPRINT SIZE ")[1].trim();
		List<ImprintSize> listOfImprintS = new ArrayList<>();
		for (String imprintSizeValue : listOfImprintSize) {
			ImprintSize imprSizeObj = new ImprintSize();
			imprSizeObj.setValue(imprintSizeValue);
			listOfImprintS.add(imprSizeObj);
		}
		return listOfImprintS;
	}
	
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
	
	
	public static List<Packaging> getPackaging(String packValue,List<Packaging> listOfPackaging){
		if(!StringUtils.isEmpty(packValue)){
			Packaging packObj = new Packaging();
			packObj.setName(packValue);
			listOfPackaging.add(packObj);
		}
		return listOfPackaging;
	}
	
	public static List<BatteryInformation> getBatteyInfo(String BatteryInfo,List<BatteryInformation> batteryInfoList) {
		//List<BatteryInformation> batteryInfoList = new ArrayList<BatteryInformation>();
		BatteryInformation batinfoObj=new BatteryInformation();
		batinfoObj.setName(BatteryInfo);
		batteryInfoList.add(batinfoObj);
		return batteryInfoList;
	}
	
	
	public List<ImprintLocation>  getImprintLocationVal(List<String> listOfImprintValues){
		
		List<ImprintLocation> listOfImprintLoc=new ArrayList<ImprintLocation>();
		for (String value : listOfImprintValues) {
			ImprintLocation imprintLocation = new ImprintLocation();
			  imprintLocation.setValue(value);
			  listOfImprintLoc.add(imprintLocation);
		}
		  return listOfImprintLoc;
	  }
	
	public List<Availability> getProductAvailablity(Set<String> childList ,Set<String> parentList,String childCriteria,String parentCriteria,List<Availability> listOfAvailablity){
		//List<Availability> listOfAvailablity = new ArrayList<>();
		if(CollectionUtils.isEmpty(listOfAvailablity)){
			listOfAvailablity= new ArrayList<>();
		}
		Availability  availabilityObj = new Availability();
		AvailableVariations  AvailableVariObj = null;
		List<AvailableVariations> listOfVariAvail = new ArrayList<>();
		List<Object> listOfParent = null;
		List<Object> listOfChild = null;
		for (String ParentValue : parentList) { //String childValue : childList
			 for (String childValue : childList) {//String ParentValue : parentList
				 AvailableVariObj = new AvailableVariations();
				 listOfParent = new ArrayList<>();
				 listOfChild = new ArrayList<>();
				 listOfParent.add(ParentValue.trim());
				 listOfChild.add(childValue.trim());
				 AvailableVariObj.setParentValue(listOfParent);
				 AvailableVariObj.setChildValue(listOfChild);
				 listOfVariAvail.add(AvailableVariObj);
			}
		}
		availabilityObj.setAvailableVariations(listOfVariAvail);
		availabilityObj.setParentCriteria(parentCriteria);
		availabilityObj.setChildCriteria(childCriteria);
		listOfAvailablity.add(availabilityObj);
		return listOfAvailablity;
	}
	
	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}

	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}
	
	

}
