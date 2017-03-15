package parser.primeline;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.Capacity;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Configurations;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.Image;
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
		if(detailType.contains("Imprint Area")){
		List<ImprintSize> listOfImprintSize=featureProductconfigs.getImprintSize();
		if(CollectionUtils.isEmpty(listOfImprintSize)){
				listOfImprintSize=new ArrayList<ImprintSize>();
		}
		  listOfImprintSize=getProductImprintSize(detail,listOfImprintSize);
		  featureProductconfigs.setImprintSize(listOfImprintSize);
		
		}else if(detailType.contains("Imprint Method")){//pending
			List<ImprintMethod> listOfImprintMethods=featureProductconfigs.getImprintMethods();
			if(CollectionUtils.isEmpty(listOfImprintMethods)){
				listOfImprintMethods=new ArrayList<ImprintMethod>();
			}
			PrimeLineAttributeParser.getImprintMethods(detail, listOfImprintMethods);
			//productconfigs.setImprintMethods(imprintMethods);
		
		}else if(detailType.contains("Keywords")){
			ArrayList<String> productKeywordsOld=(ArrayList<String>) featureExcelObj.getProductKeywords();
			ArrayList<String> productKeywordsNew = (ArrayList<String>) CommonUtility.getStringAsList(detail,ApplicationConstants.CONST_DELIMITER_COMMA);
			if(!CollectionUtils.isEmpty(productKeywordsOld)){
				productKeywordsNew.addAll(productKeywordsOld);
			}
			featureExcelObj.setProductKeywords(productKeywordsNew);
		}else if(detailType.contains("Note") || detailType.contains("Closeout Price Note") || detailType.contains("Price Note") ||
				detailType.contains("Ideas") || detailType.contains("Artwork")){
			//String distriComments="";
			String distriCommentsTemp=	featureExcelObj.getDistributorOnlyComments();
			if(!StringUtils.isEmpty(distriCommentsTemp)){
				distriCommentsTemp=distriCommentsTemp+detail;
				detail=distriCommentsTemp;
			}
			featureExcelObj.setDistributorOnlyComments(detail);
			
			
		}else if(detailType.contains("Packaging")){
			List<Packaging> listOfPackaging=featureProductconfigs.getPackaging();
			if(CollectionUtils.isEmpty(listOfPackaging)){
				listOfPackaging=new ArrayList<Packaging>();
			}
			listOfPackaging=getPackaging(detail, listOfPackaging);
			featureProductconfigs.setPackaging(listOfPackaging);
		}else if(detailType.contains("Delivery")){//prod time //data is invalid not able to recognise the time from it
			//pending
		}else if(detailType.contains("Batteries")){//battery //same goes for battery need to confirm with them
			//pending
		}else if(detailType.contains("Size")){//size 
			// if sizes are not present in itemmaster tab den only assign it to the product.
			//sizes are very complex will be done at last
			Size newSize=featureProductconfigs.getSizes();
			//newSize.
			//if(newSize.toString().equals("{}")){// i am looking for some other alternative
			if(newSize!=null){// i am looking for some other alternative
				//newSize.s
			}
		}else if(detailType.contains("Colors")){//colors
			// if colors are not present in itemmaster tab den only assign it to the product.
			List<Color> colorList =featureProductconfigs.getColors(); 
			if(CollectionUtils.isEmpty(colorList)){
				List<Color> colorListNew = new ArrayList<Color>();
				colorListNew=PrimeLineAttributeParser.getColor(detail,colorListNew);//here send value of detail as list
				featureProductconfigs.setColors(colorListNew);
			}
		}else if(detailType.contains("Ink Cartridge")){//option
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
		}else if(detailType.contains("Romance Copy")){//dont know
			
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
	
	public static List<ImprintMethod> getImprintMethods(String value,List<ImprintMethod> listOfImprintMethods){
		//List<ImprintMethod> listOfImprintMethods = new ArrayList<ImprintMethod>();
		ImprintMethod imprintMethodObj =new ImprintMethod();
		if(lookupServiceDataObj.isImprintMethod(value)){
			imprintMethodObj.setAlias(value);
			imprintMethodObj.setType(value);
		}else{
			imprintMethodObj.setAlias(value);
			imprintMethodObj.setType("OTHER");
		}
		listOfImprintMethods.add(imprintMethodObj);
		return listOfImprintMethods;
		
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
	
	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}

	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}
	
	

}
