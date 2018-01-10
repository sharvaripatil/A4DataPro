package parser.sunGraphix;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.BlendMaterial;
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
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.Personalization;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;
import com.a4tech.product.model.Weight;
import com.a4tech.supplier.mapper.SunGraphixMapping;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class SunGraphixAttributeParser {
	private static final Logger _LOGGER = Logger.getLogger(SunGraphixAttributeParser.class);
	private LookupServiceData lookupServiceData;
	@SuppressWarnings("unused")
	public List<Color> getProductColors(String color){
		List<Color> listOfColors = new ArrayList<>();
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
			String colorGroup = SunGraphixConstants.getColorGroup(colorName.trim());
			//if (colorGroup == null) {
				//if (colorGroup!=null && colorGroup.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
			if (colorName.contains("/") || colorGroup.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
				
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
						String colorFirstName = SunGraphixConstants.getColorGroup(comboColors[0].trim());
						colorObj.setName(colorFirstName == null?"Other":colorFirstName);
						int combosSize = comboColors.length;
						if (combosSize == ApplicationConstants.CONST_INT_VALUE_TWO) {
							String colorComboFirstName = SunGraphixConstants.getColorGroup(comboColors[1].trim());
							colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
							listOfCombo = getColorsCombo(colorComboFirstName, ApplicationConstants.CONST_STRING_EMPTY,
									combosSize);
						} else{
							String colorComboFirstName = SunGraphixConstants.getColorGroup(comboColors[1].trim());
							colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
							
							String colorComboSecondName = SunGraphixConstants.getColorGroup(comboColors[2].trim());
							colorComboSecondName = colorComboSecondName == null?"Other":colorComboSecondName;
							listOfCombo = getColorsCombo(colorComboFirstName,colorComboSecondName, combosSize);
						}
						String alias = colorGroup.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH, "-");
						colorObj.setAlias(alias);
						colorObj.setCombos(listOfCombo);
					} else {
						String[] comboColors = CommonUtility.getValuesOfArray(colorGroup,
								ApplicationConstants.CONST_DELIMITER_FSLASH);
						String mainColorGroup = SunGraphixConstants.getColorGroup(comboColors[0].trim());
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
    		 mainColor = SunGraphixConstants.getColorGroup(colorVals[0].trim());
    		 secondaryColor = SunGraphixConstants.getColorGroup(colorVals[1].trim());
    		 if(mainColor != null && secondaryColor != null){
    			 return true;
    		 }
    	} else if(colorVals.length == ApplicationConstants.CONST_INT_VALUE_THREE){
    		 mainColor      = SunGraphixConstants.getColorGroup(colorVals[0].trim());
    		 secondaryColor = SunGraphixConstants.getColorGroup(colorVals[1].trim());
    		 thirdColor     = SunGraphixConstants.getColorGroup(colorVals[2].trim());
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
	public ProductConfigurations getImprintMethod(String data,ProductConfigurations productConfigObj){
		//List<ImprintMethod> imprintMethodsList = new ArrayList<ImprintMethod>();
		List<ImprintMethod> listOfImprintMethod = new ArrayList<>();
		if(!CollectionUtils.isEmpty(productConfigObj.getImprintMethods())){
			List<ImprintMethod> listOfImprintMethodTemp = productConfigObj.getImprintMethods();
			listOfImprintMethod.addAll(listOfImprintMethodTemp);
		}
		
		ImprintMethod	imprintMethodObj = null;
		String[] imprintMethodValues = data.split(ApplicationConstants.CONST_DELIMITER_COMMA);
		for (String imprintMethodName : imprintMethodValues) {
			if(StringUtils.isEmpty(imprintMethodName)){
				continue;
			}
			imprintMethodObj = new ImprintMethod();
			String alias = "";
			if(imprintMethodName.contains("Foil")){
				alias = imprintMethodName.trim().toUpperCase();
				//imprintMethodName=imprintMethodName.replace("Foil", "Foil Stamped");
				imprintMethodName = "Foil Stamped";
		    } else if(imprintMethodName.contains("Deboss")){
		    	alias = imprintMethodName.trim().toUpperCase();
    		    //imprintMethodName=imprintMethodName.replaceAll("Deboss", "Debossed");
    		    imprintMethodName = "Debossed";
	        } else if(imprintMethodName.contains("4C")){
	        	 alias = imprintMethodName.trim().toUpperCase();
	        	 imprintMethodName = "Full Color";
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
public List<Personalization> getPersonalizationCriteria(String persValue){
		
		List<Personalization> listPersonalz =new ArrayList<Personalization>();
		
		Personalization persObj=null;
				persObj=new Personalization();
	 			persObj.setType(ApplicationConstants.CONST_STRING_PERSONALIZATION);
	 			persObj.setAlias(ApplicationConstants.CONST_STRING_PERSONALIZATION);
	 			listPersonalz.add(persObj);
	 			_LOGGER.info("PERSONALIZATION processed");
		return listPersonalz;
		
	}

	public List<ImprintSize> getProductImprintSize(String imprSize){
		//String imprintSizeValue = imprSize.split("IMPRINT SIZE ")[1].trim();
		List<ImprintSize> listOfImprintSize = new ArrayList<>();
		ImprintSize imprSizeObj = new ImprintSize();
		imprSizeObj.setValue(imprSize);
		listOfImprintSize.add(imprSizeObj);
		return listOfImprintSize;
	}
	public Size getSizes(String sizeValue,Size existingSizeObj,Dimension exisDimensionObj,ArrayList<Values> exisValuesList) {
		//Size sizeObj = new Size();
		String sizeGroup="dimension";
		String unitValue="in";
		String attriValueLen="Length";
		//1.75 DIA x 4.5
		try{
			sizeValue=sizeValue.toUpperCase();
			/*if(tempValue.contains("FEET IN")){
				tempValue=tempValue.replace("FEET IN LENGTH", "FEE");
			}*/
			//sizeValue=removeSpecialChar(sizeValue,1);
			sizeValue=sizeValue.replaceAll("”","");
			//sizeValue=sizeValue.replaceAll("”","\"");
			//sizeValue=sizeValue.replaceAll("\"","INC");
			sizeValue=sizeValue.replaceAll("\"","");
			sizeValue=sizeValue.replaceAll(";",",");
			sizeValue=sizeValue.replaceAll(":","");
			//sizeValue=sizeValue.replaceAll(".","");
			String valuesArr[]=sizeValue.split(",");
			
			for (String tempValue : valuesArr) {
			/////////////////
			String DimenArr[] = {tempValue} ;
			 if(tempValue.contains("X")){
				 DimenArr=tempValue.split("X");
				 sizeGroup="dimension";
			 }
			
			if (sizeGroup.equals("dimension")) {
			//Dimension dimensionObj = new Dimension();
			//List<Values> valuesList = new ArrayList<Values>();
			List<Value> valuelist;
			Values valuesObj = null;
			Value valObj;
			
				valuesObj = new Values();
				valuelist = new ArrayList<Value>(); 
				int count=1;
				for (String value1 : DimenArr) {
					valObj = new Value();
					/*if(value1.contains("DIA")){
						attriValueLen="DIA";
						value1=value1.replace("DIA", "").trim();
					}
					if(value1.contains("shaker")){
						value1=value1.replace("(shaker)", "").trim();
					}*/
					if(count==1){
						valObj.setAttribute(attriValueLen);
					}else if(count==2){
						valObj.setAttribute("Width");
					} else if(count==3){
						valObj.setAttribute("Height");
					}
					valObj.setValue(value1.trim());
					valObj.setUnit(unitValue);
					valuelist.add(valObj);
					valuesObj.setValue(valuelist);
					count++;
				}
				
				exisValuesList.add(valuesObj);
			}
			exisDimensionObj.setValues(exisValuesList);
			existingSizeObj.setDimension(exisDimensionObj);
		}
		}
		catch(Exception e)
		{
			_LOGGER.error("Error while processing Size :"+e.getMessage());
			return null;
		}
		return existingSizeObj;
	}
	
	private boolean isComboColors1(String value) {
	boolean result = false;
	if (value.contains("/")) {
		result = true;
	}
	return result;
	}
	public List<Packaging> getPackaging(String packValue){
		List<Packaging> listOfPackaging = null;
		if(!StringUtils.isEmpty(packValue)){
			listOfPackaging = new ArrayList<Packaging>();
			Packaging packObj = new Packaging();
			packObj.setName(packValue);
			listOfPackaging.add(packObj);
		}
		return listOfPackaging;
	}
	public  List<Option> getOptions(String optionName,String  optionDataValue,String additionalInfo,boolean canOrder,boolean reqOrder,String optionType) {
		List<Option> optionList=new ArrayList<>();
		try{
		Option optionObj=new Option();
		      List<OptionValue> valuesList=new ArrayList<OptionValue>();
				 OptionValue optionValueObj=new OptionValue();
					  optionValueObj.setValue(optionDataValue.trim());
					  valuesList.add(optionValueObj);
					  optionObj.setOptionType("optionType");
					  optionObj.setName(optionName);
					  optionObj.setValues(valuesList); 
					  optionObj.setAdditionalInformation(additionalInfo);
					  optionObj.setCanOnlyOrderOne(canOrder);
					  optionObj.setRequiredForOrder(reqOrder);
					  optionList.add(optionObj);
				  
		   }catch(Exception e){
			   _LOGGER.error("Error while processing Options :"+e.getMessage());          
		      return new ArrayList<Option>();
		      
		     }
		  return optionList;
		  
		 }
	public ShippingEstimate getShippingEstimates( String shippingValue,ShippingEstimate shippingEstObj,String str) {
		//ShippingEstimate shipingObj = new ShippingEstimate();
		if(str.equals("NOI")){
		List<NumberOfItems> listOfNumberOfItems = new ArrayList<NumberOfItems>();
		NumberOfItems itemObj = new NumberOfItems();
		itemObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_NUMBER_UNIT_CASE);
		itemObj.setValue(shippingValue);
		listOfNumberOfItems.add(itemObj);
		shippingEstObj.setNumberOfItems(listOfNumberOfItems);
		}
		
		if (str.equals("WT")) {
			List<Weight> listOfWeight = new ArrayList<Weight>();
			Weight weightObj = new Weight();
			weightObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_WEIGHT);
			weightObj.setValue(shippingValue);
			listOfWeight.add(weightObj);
			shippingEstObj.setWeight(listOfWeight);
		}
		
		if (str.equals("SDIM")) {
			String unit="in";
			shippingValue=shippingValue.toUpperCase();
			shippingValue=shippingValue.replaceAll("”","\"");
			shippingValue=shippingValue.replaceAll("\"","");
			shippingValue=shippingValue.replaceAll(";",",");
			shippingValue=shippingValue.replaceAll(":","");
			//sizeValue=sizeValue.replaceAll(".","");
			String valuesArr[]=shippingValue.split(",");
			if(valuesArr.length>1){
				//shippingValue=valuesArr[0];
			}else{
			//shippingValue=removeSpecialChar(shippingValue,1);
			String shipDimenArr[] = shippingValue.split("X");
			List<Dimensions> dimenlist = new ArrayList<Dimensions>();
			Dimensions dimensionObj = new Dimensions();
			for (int i = 0; i <= shipDimenArr.length - 1; i++) {
			//	String[] shipDimenArr1 = shipDimenArr[i].split(ApplicationConstants.CONST_DELIMITER_COLON);
				if (i == 0) {
					dimensionObj.setLength(shipDimenArr[0]);
					dimensionObj.setLengthUnit(unit);
				} else if (i == 1) {
					dimensionObj.setWidth(shipDimenArr[1]);
					dimensionObj.setWidthUnit(unit);
				} else if (i == 2) {
					dimensionObj.setHeight(shipDimenArr[2]);
					dimensionObj.setHeightUnit(unit);
				}
				dimenlist.add(dimensionObj);
			}
			shippingEstObj.setDimensions(dimensionObj);
			}
		}
		return shippingEstObj;
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
	public List<ProductionTime> getProductionTime(String value){
		List<ProductionTime> listOfProTime = null;
		if(!StringUtils.isEmpty(value)){
			listOfProTime = new ArrayList<ProductionTime>();
			ProductionTime prodTimeObj = new ProductionTime();
			prodTimeObj.setBusinessDays(value);
			prodTimeObj.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
			listOfProTime.add(prodTimeObj);
		}
		return listOfProTime;
	}
	
public List<Material> getMaterialList(String originalMaterialvalue){
		
		Material materialObj = new Material();
		List<Material> listOfMaterial = new ArrayList<>();
		/*if(!StringUtils.isEmpty(originalMaterialvalue)){
			originalMaterialvalue = CommonUtility.removeSpecialSymbols(originalMaterialvalue,specialCharacters);
			originalMaterialvalue = originalMaterialvalue.replaceAll("é", "e");
		}*/
		List<String> listOfLookupMaterial = getMaterialType(originalMaterialvalue.toUpperCase());
		if(!listOfLookupMaterial.isEmpty()){
			int numOfMaterials = listOfLookupMaterial.size();
			  if(numOfMaterials == ApplicationConstants.CONST_INT_VALUE_ONE){ // this condition used to single material value(E.X 100% smooth knit polyester)
				  materialObj = getMaterialValue(listOfLookupMaterial.toString(), originalMaterialvalue);
				  listOfMaterial.add(materialObj);
			  }else if(numOfMaterials == ApplicationConstants.CONST_INT_VALUE_TWO){
				   materialObj = getMaterialValue(listOfLookupMaterial.toString(), originalMaterialvalue, // this condition used to two material value(E.X 100% polyester fleece, 300gsm or 8.85 oz./yd2 )
						                                  ApplicationConstants.CONST_STRING_COMBO_TEXT);
				   listOfMaterial.add(materialObj);
			  }else if(isBlendMaterial(originalMaterialvalue)){  // this condition for blend material
				 String[] values = CommonUtility.getValuesOfArray(originalMaterialvalue,ApplicationConstants.CONST_DELIMITER_FSLASH);
		    	 BlendMaterial blentMaterialObj = null;
		    	 List<BlendMaterial> listOfBlendMaterial = new ArrayList<>();
				     if(values.length == ApplicationConstants.CONST_INT_VALUE_TWO){
				    	 for (String materialValue : values) {
				    		 blentMaterialObj = new BlendMaterial();
				    		 String mtrlType = getMaterialType(materialValue.toUpperCase()).toString();
				    		 if(materialValue.contains(ApplicationConstants.CONST_DELIMITER_PERCENT_SIGN)){
								  String percentage = materialValue.split(ApplicationConstants.CONST_DELIMITER_PERCENT_SIGN)[0];
								  blentMaterialObj.setName(mtrlType);
								  blentMaterialObj.setPercentage(percentage);
								  listOfBlendMaterial.add(blentMaterialObj);
							  }
				    		 materialObj.setBlendMaterials(listOfBlendMaterial);
				    		 listOfMaterial.add(materialObj);
				    	 }
				     }else{ // this condition for combo and blend values
				    	 Combo comboObj = new Combo();
				    	 for (String materialValue : values) {
				    		 blentMaterialObj = new BlendMaterial();
							  String mtrlType = getMaterialType(materialValue.toUpperCase()).toString();
							  if(materialValue.contains(ApplicationConstants.CONST_DELIMITER_PERCENT_SIGN)){
								  String percentage = materialValue.split(ApplicationConstants.CONST_DELIMITER_PERCENT_SIGN)[0];
								  if(!StringUtils.isEmpty(mtrlType)){
									  blentMaterialObj.setName(mtrlType);  
								  }else{
									blentMaterialObj.setName(ApplicationConstants.CONST_STRING_OTHER_FABRIC);  
								  }
								  blentMaterialObj.setPercentage(percentage);
								  listOfBlendMaterial.add(blentMaterialObj);
							  }else{
								  materialObj.setName(mtrlType);
								  materialObj.setAlias(originalMaterialvalue);  
							  }
						} 
				    	 comboObj.setBlendMaterials(listOfBlendMaterial);
				    	 comboObj.setName(ApplicationConstants.CONST_STRING_BLEND_TEXT);
				    	 materialObj.setCombo(comboObj);
				    	 listOfMaterial.add(materialObj);
				     }	        
			  }
		}else{ // used for Material is not available in lookup, then it goes in Others
			materialObj = getMaterialValue(ApplicationConstants.CONST_VALUE_TYPE_OTHER, originalMaterialvalue);
			listOfMaterial.add(materialObj);
		}
		return listOfMaterial;
	}

public List<String> getMaterialType(String value){
	List<String> listOfLookupMaterials = lookupServiceData.getMaterialValues();
	List<String> finalMaterialValues = listOfLookupMaterials.stream()
			                                  .filter(mtrlName -> value.contains(mtrlName))
			                                  .collect(Collectors.toList());
                                             
			
	return finalMaterialValues;	
}
/*
 * @author Venkat
 * @param String String ,type of material alias name
 * @description this method design for setting the material name and alias name for single value
 * @return Material Object 
 */
public Material getMaterialValue(String name,String alias){
	Material materialObj = new Material();
	name = CommonUtility.removeCurlyBraces(name);
	materialObj.setName(name);
	materialObj.setAlias(alias);
	return materialObj;
}
/*
 * @author Venkat
 * @param String String ,type of material alias name
 * @description this method design for setting the material name and alias name for combo 
 * @return Material Object 
 */
public Material getMaterialValue(String name,String alias ,String materialType){
	Material materialObj = new Material();
	 Combo comboObj = null;
	 String[] materials = null;
	 name = CommonUtility.removeCurlyBraces(name);
	if(name.contains(ApplicationConstants.CONST_DELIMITER_COMMA)){
		materials = name.split(ApplicationConstants.CONST_DELIMITER_COMMA); 
		materialObj.setName(materials[0]);
		materialObj.setAlias(alias);
		comboObj = new Combo();
    	comboObj.setName(materials[1]);
    	materialObj.setCombo(comboObj);
	}
	return materialObj;
}

public boolean isBlendMaterial(String data){
	if(data.contains(ApplicationConstants.CONST_DELIMITER_FSLASH) && 
			              data.contains(ApplicationConstants.CONST_DELIMITER_PERCENT_SIGN)){
		return true;
	}else if(data.split(ApplicationConstants.CONST_DELIMITER_FSLASH).length == 
			                    ApplicationConstants.CONST_INT_VALUE_THREE){
		return true;
	}
	return false;
}
	public LookupServiceData getLookupServiceData() {
		return lookupServiceData;
	}
	public void setLookupServiceData(LookupServiceData lookupServiceData) {
		this.lookupServiceData = lookupServiceData;
	}
	/*public List<Color> getColorCriteria(String colorValue) {
	
	Color colorObj = null;
	List<Color> colorList = new ArrayList<Color>();
	//HighCaliberConstants
	try {
	//Map<String, String> HCLCOLOR_MAP=new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
	// Map<String, String> HCLCOLOR_MAP =HighCaliberConstants.getHCLCOLOR_MAP();
		List<Combo> comboList = null;
		String value = colorValue;
		String tempcolorArray[]=value.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		for (String colorVal : tempcolorArray) {
		String strColor=colorVal;
		strColor=strColor.replaceAll("&","/");
		//strColor=strColor.replaceAll(" w/","/");
		//strColor=strColor.replaceAll(" W/","/");
		boolean isCombo = false;
			colorObj = new Color();
			comboList = new ArrayList<Combo>();
			isCombo = isComboColors(strColor);
			if(isCombo){
				if(HighCaliberConstants.HCLCOLOR_MAP.get(strColor.trim())!=null){
				//if(HCLCOLOR_MAP.get(strColor.trim())!=null){
					isCombo=false;
				}
			}
			
			if (!isCombo) {
				String colorName=SunGraphixConstants.TCOLOR_MAP.get(strColor.trim());
				//String colorName=HCLCOLOR_MAP.get(strColor.trim());
				if(StringUtils.isEmpty(colorName)){
					colorName=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
				}
				colorObj.setName(colorName);
				colorObj.setAlias(colorVal.trim());
				colorList.add(colorObj);
			} else {
				//245-Mid Brown/Navy
				String colorArray[] = strColor.split(ApplicationConstants.CONST_DELIMITER_FSLASH);
				//if(colorArray.length==2){
				String combo_color_1=SunGraphixConstants.TCOLOR_MAP.get(colorArray[0].trim());
				//String combo_color_1=HCLCOLOR_MAP.get(colorArray[0].trim());
				if(StringUtils.isEmpty(combo_color_1)){
					combo_color_1=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
				}
				colorObj.setName(combo_color_1);
				colorObj.setAlias(strColor);
				
				Combo comboObj = new Combo();
				String combo_color_2=SunGraphixConstants.TCOLOR_MAP.get(colorArray[1].trim());
				//String combo_color_2=HCLCOLOR_MAP.get(colorArray[1].trim());
				if(StringUtils.isEmpty(combo_color_2)){
					combo_color_2=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
				}
				comboObj.setName(combo_color_2.trim());
				comboObj.setType(ApplicationConstants.CONST_STRING_SECONDARY);
				if(colorArray.length==3){
					String combo_color_3=SunGraphixConstants.TCOLOR_MAP.get(colorArray[2].trim());
					//String combo_color_3=HCLCOLOR_MAP.get(colorArray[2].trim());
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
				colorList.add(colorObj);
			 	}
	}
	//}
	} catch (Exception e) {
		_LOGGER.error("Error while processing Color :" + e.getMessage());
		return new ArrayList<Color>();
	}
	_LOGGER.info("Colors Processed");
	return colorList;
	}*/
}
