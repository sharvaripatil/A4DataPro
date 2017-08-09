package parser.BagMakers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.AdditionalLocation;
import com.a4tech.product.model.BlendMaterial;
import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class BagMakerAttributeParser {

	private static final Logger _LOGGER = Logger.getLogger(BagMakerAttributeParser.class);
	private LookupServiceData objLookUpService;
	
	public List<ImprintMethod> getImprintMethods(List<String> listOfImprintMethods){
		List<ImprintMethod> listOfImprintMethodsNew = new ArrayList<ImprintMethod>();
		for (String value : listOfImprintMethods) {
			value=value.trim();
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
	
	public List<ImprintSize> getImprintSize(String imprintMethValue,List<ImprintSize> listOfImprintSize){
		if(CollectionUtils.isEmpty(listOfImprintSize)){
			listOfImprintSize=new ArrayList<ImprintSize>();
		}
		ImprintSize imprSizeObj = new ImprintSize();
		imprSizeObj.setValue(imprintMethValue);
		listOfImprintSize.add(imprSizeObj);
		return listOfImprintSize;
	}

public Product getExistingProductData(Product existingProduct , ProductConfigurations existingProductConfig){
		
		ProductConfigurations newProductConfigurations=new ProductConfigurations();
		Product newProduct=new Product();
		List<String> listCategories=new ArrayList<String>();
		List<Catalog> listCatalog=new ArrayList<Catalog>();
		try{
			if(existingProductConfig==null){
				return new Product();
			}
			
		/*	//Image
			List<Image> imagesList=existingProduct.getImages();
			if(!CollectionUtils.isEmpty(imagesList)){
				List<Image> newImagesList=new ArrayList<Image>();
				for (Image image : imagesList) {
					image.setConfigurations( new ArrayList<Configurations>());
					newImagesList.add(image);
				}
				newProduct.setImages(imagesList);
			}*/
			
			//Categories
			listCategories=existingProduct.getCategories();
			if(!CollectionUtils.isEmpty(listCategories)){
				newProduct.setCategories(listCategories);
			}
		 
			List<String> productKeywords=existingProduct.getProductKeywords();
			if(!CollectionUtils.isEmpty(productKeywords)){
				newProduct.setProductKeywords(productKeywords);
			}
			listCatalog=existingProduct.getCatalogs();
			if(!CollectionUtils.isEmpty(listCatalog)){
				newProduct.setCatalogs(listCatalog);
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


@SuppressWarnings("unused")
public List<Color> getProductColors(String color){
	List<Color> listOfColors = new ArrayList<>();
	try{
	Color colorObj = null;
	color=color.replaceAll("\\|",",");
	String[] colors =getValuesOfArray(color, ",");
	for (String colorName : colors) {
		colorName=colorName.trim();
		if(StringUtils.isEmpty(colorName)){
			continue;
		}
		colorName=colorName.replaceAll("&","/");
		colorName=colorName.replaceAll(" w/","/");
		colorName=colorName.replaceAll(" W/","/");
		//colorName = colorName.trim();
		
		colorObj = new Color();
		String colorGroup = BagMakerConstants.getColorGroup(colorName.trim());
		//if (colorGroup == null) {
			//if (colorGroup!=null && colorGroup.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
		//if (colorName.contains("/") || colorGroup.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
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
					String colorFirstName = BagMakerConstants.getColorGroup(comboColors[0].trim());
					colorObj.setName(colorFirstName == null?"Other":colorFirstName);
					int combosSize = comboColors.length;
					if (combosSize == ApplicationConstants.CONST_INT_VALUE_TWO) {
						String colorComboFirstName = BagMakerConstants.getColorGroup(comboColors[1].trim());
						colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
						listOfCombo = getColorsCombo(colorComboFirstName, ApplicationConstants.CONST_STRING_EMPTY,
								combosSize);
					} else{
						String colorComboFirstName = BagMakerConstants.getColorGroup(comboColors[1].trim());
						colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
						
						String colorComboSecondName = BagMakerConstants.getColorGroup(comboColors[2].trim());
						colorComboSecondName = colorComboSecondName == null?"Other":colorComboSecondName;
						listOfCombo = getColorsCombo(colorComboFirstName,colorComboSecondName, combosSize);
					}
					String alias = colorGroup.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH, "-");
					colorObj.setAlias(alias);
					colorObj.setCombos(listOfCombo);
				} else {
					String[] comboColors = CommonUtility.getValuesOfArray(colorGroup,
							ApplicationConstants.CONST_DELIMITER_FSLASH);
					String mainColorGroup = BagMakerConstants.getColorGroup(comboColors[0].trim());
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
		_LOGGER.error("Error while processing color: "+e.getMessage());
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
		 mainColor = BagMakerConstants.getColorGroup(colorVals[0].trim());
		 secondaryColor = BagMakerConstants.getColorGroup(colorVals[1].trim());
		 if(mainColor != null && secondaryColor != null){
			 return true;
		 }
	} else if(colorVals.length == ApplicationConstants.CONST_INT_VALUE_THREE){
		 mainColor      = BagMakerConstants.getColorGroup(colorVals[0].trim());
		 secondaryColor = BagMakerConstants.getColorGroup(colorVals[1].trim());
		 thirdColor     = BagMakerConstants.getColorGroup(colorVals[2].trim());
		 if(mainColor != null && secondaryColor != null && thirdColor != null){
			 return true;
		 }
	} else{
		
	}
	return false;
	}


public Size getSizes(String sizeValue) {
	Size sizeObj = new Size();
	try{
		sizeValue=sizeValue.toUpperCase();
		sizeValue=sizeValue.replace("W", "");
		sizeValue=sizeValue.replace("H", "");
		sizeValue=sizeValue.replace("L", "");
		sizeValue=sizeValue.replace("\"", "");
		
		//if (sizeGroup.equals("dimension")) {
		Dimension dimensionObj = new Dimension();
		//String DimenArr[] = sizeValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		String DimenArr[] = sizeValue.split("X");
		List<Values> valuesList = new ArrayList<Values>();
		List<Value> valuelist = new ArrayList<Value>();
		Values valuesObj = new Values();
		Value valObj;
			int count=1;
			for (String value1 : DimenArr) {
				valObj = new Value();
				//String[] DimenArr2 = value1.split(ApplicationConstants.CONST_DELIMITER_COLON);
				//valObj.setAttribute(DimenArr2[0]);
				
				if(DimenArr.length==3 || DimenArr.length==4){
					if(count==1){
						DimenArr[0]=DimenArr[0].replace("YDS", "");
						valObj.setValue(DimenArr[0].trim());
						if(sizeValue.toUpperCase().contains("YDS")){
							
							valObj.setUnit("yds");
							valObj.setAttribute("Length");
						}else{
							valObj.setUnit("in");
							valObj.setAttribute("Width");
						}
						
					}else if(count==2){
						DimenArr[1]=DimenArr[1].replace("YDS", "");
						valObj.setValue(DimenArr[1].trim());
						if(sizeValue.toUpperCase().contains("YDS")){
							valObj.setUnit("in");
							valObj.setAttribute("Width");
						}else{
						valObj.setUnit("in");
						valObj.setAttribute("Length");//Length
						}
					} else if(count==3){
						valObj.setValue(DimenArr[2].trim());
						valObj.setUnit("in");
						valObj.setAttribute("Height");
					}
				}else if(DimenArr.length==2){
					if(count==1){
						DimenArr[0]=DimenArr[0].replace("YDS", "");
						valObj.setValue(DimenArr[0].trim());
						if(sizeValue.toUpperCase().contains("YDS")){
							valObj.setUnit("yds");
							valObj.setAttribute("Length");
						}else{
							valObj.setUnit("in");
							valObj.setAttribute("Width");
						}
						
					}else if(count==2){
						valObj.setValue(DimenArr[1].trim());
						valObj.setUnit("in");
						valObj.setAttribute("Height");
					} 
				}
				
				//valObj.setValue(DimenArr[0].trim());
				//valObj.setUnit(DimenArr[1].trim());
				if(count!=4){
				valuelist.add(valObj);
				valuesObj.setValue(valuelist);
				}
				count++;
			}
		valuesList.add(valuesObj);
		dimensionObj.setValues(valuesList);
		sizeObj.setDimension(dimensionObj);
				//}
			}
		catch(Exception e)
		{
			_LOGGER.error("Error while processing Size :"+e.getMessage());
			return sizeObj;
		}
		return sizeObj;
	}

public ShippingEstimate getShippingEstimates(String shippinglen,String shippingWid,String shippingH, String shippingWeightValue,
		String noOfitem,Dimensions dimensionObj,ShippingEstimate ShipingObj) {
	//ShippingEstimate ItemObject = new ShippingEstimate();
	try{
	List<NumberOfItems> listOfNumberOfItems = new ArrayList<NumberOfItems>();
	List<Weight> listOfWeight = new ArrayList<Weight>();
	NumberOfItems itemObj = new NumberOfItems();

		//List<Dimensions> dimenlist = new ArrayList<Dimensions>();
		//Dimensions dimensionObj = new Dimensions();
		
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
public  List<Option> getOptions(String optionName,String  optionDataValue) {
	List<Option> optionList=new ArrayList<>();
	try{
	Option optionObj=new Option();
	      List<OptionValue> valuesList=new ArrayList<OptionValue>();
			 OptionValue optionValueObj=new OptionValue();
				  optionValueObj.setValue(optionDataValue.trim());
				  valuesList.add(optionValueObj);
				  optionObj.setOptionType("Product");
				  optionObj.setName(optionName);
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

	public List<AdditionalLocation> getAdditionalLocation(String loctionVal){
			List<AdditionalLocation> listOfAdditionalLoc = new ArrayList<>();
			AdditionalLocation additionalLocObj = new AdditionalLocation();
			additionalLocObj.setName(loctionVal);
			listOfAdditionalLoc.add(additionalLocObj);
			return listOfAdditionalLoc;
	}
	
	public List<AdditionalColor> getAdditionalColor(String colorVal){
		List<AdditionalColor> listOfAdditionalColor = new ArrayList<>();
		AdditionalColor additionalColorObj = new AdditionalColor();
		additionalColorObj.setName(colorVal);
		listOfAdditionalColor.add(additionalColorObj);
		return listOfAdditionalColor;
	}
	
	public List<Material> getMaterialList(String materialValue1) {
		Material materialObj = new Material();
		List<Material> listOfMaterial = new ArrayList<>();
		materialValue1=materialValue1.replace("Tote", "");
		materialValue1=materialValue1.replace("tote", "");
		String finalTempAliasVal=materialValue1;
		
		 if(materialValue1.contains("cotton fleece"))
	        {
	        	materialValue1=materialValue1.replace("cotton fleece","Cotton");
	        }
		
		List<String> listOfLookupMaterial = getMaterialType(materialValue1.toUpperCase());
		if(!listOfLookupMaterial.isEmpty()){
			int numOfMaterials = listOfLookupMaterial.size();
	

			boolean flag=false;
	
					if(numOfMaterials == 1 && !flag){ // this condition used to single material value(E.X 100% Cotton)	  
				
				materialObj = getMaterialValue(listOfLookupMaterial.toString(), finalTempAliasVal);//
				  listOfMaterial.add(materialObj);
				  
				  
			  }else if(numOfMaterials == 2 || numOfMaterials == 3){   // this condition for blend material
				  
				String[] values = materialValue1.split(",");

				  if(materialValue1.contains("Cotton/Coton"))
			        {
			        	materialValue1=materialValue1.replace("Cotton/Coton","Cotton");
			        }
				 
				
				
		    	 BlendMaterial blentMaterialObj = null;
		    	 List<BlendMaterial> listOfBlendMaterial = new ArrayList<>();
		    
		    	  if(values.length == 2 || values.length == 3 ||values.length == 4 ){
		    		  if(values.length == 4 && values[2].contains("Spandex"))
		    		  {
		    			  values[2]=values[2].replaceAll(values[2], "");
		    		  }
		    		 
		    		  int percentage3=0;
				    	 for (String materialValue : values) {
				    		 blentMaterialObj = new BlendMaterial();
				    		 String mtrlType = getMaterialType(materialValue.toUpperCase()).toString();
				    		
				    		 if(materialValue.contains("%")){
						 		  String percentage1 = materialValue.split("%")[0];
						 		 if(materialValue==values[0])
								  {
						           int percentage2 = Integer.parseInt(percentage1);
								   percentage3=(100-percentage2);
								  }
							
								  materialObj.setName("BLEND");
								  materialObj.setAlias(finalTempAliasVal); 
								  
								  if(!StringUtils.isEmpty(mtrlType)){
									  mtrlType=CommonUtility.removeCurlyBraces(mtrlType);
								  }
								  if(!StringUtils.isEmpty(mtrlType)){
								  blentMaterialObj.setName(mtrlType);
								  }else{
									  blentMaterialObj.setName("Other Fabric"); 
								  }
								  blentMaterialObj.setPercentage(percentage1);
								  if(materialValue==values[1])
								  {
									  blentMaterialObj.setPercentage(Integer.toString(percentage3));
								  }
								  
								
								  listOfBlendMaterial.add(blentMaterialObj);
							  }
				    		
				    	 }
				    	    materialObj.setBlendMaterials(listOfBlendMaterial);
				    		listOfMaterial.add(materialObj);
				     }
				     
				     
				     
				     
			  }
		}else{ // used for Material is not available in lookup, then it goes in Others
			if(materialValue1.equalsIgnoreCase("Unassigned")){
				
			}else{
			materialObj = getMaterialValue("Other",finalTempAliasVal);
			listOfMaterial.add(materialObj);
			}
		}
		
	return listOfMaterial;
	}
		
	public List<String> getMaterialType(String value){
		List<String> listOfLookupMaterials = objLookUpService.getMaterialValues();
		List<String> finalMaterialValues = listOfLookupMaterials.stream()
				                                  .filter(mtrlName -> value.contains(mtrlName))
				                                  .collect(Collectors.toList());
                                                 
				
		return finalMaterialValues;	
	}
		
	public Material getMaterialValue(String name,String alias){
		Material materialObj = new Material();
		name = CommonUtility.removeCurlyBraces(name);
		materialObj.setName(name);
		materialObj.setAlias(alias);
		return materialObj;
	}
	
	
	public static String[] getValuesOfArray(String data,String delimiter){
	   if(!StringUtils.isEmpty(data)){
		   return data.split(delimiter);
	   }
	   return null;
   }
	
	public LookupServiceData getObjLookUpService() {
		return objLookUpService;
	}

	public void setObjLookUpService(LookupServiceData objLookUpService) {
		this.objLookUpService = objLookUpService;
	}

}
