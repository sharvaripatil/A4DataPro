package parser.PioneerLLC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
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
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.OtherSize;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class PioneerLLCAttributeParser {

	private static final Logger _LOGGER = Logger.getLogger(PioneerLLCAttributeParser.class);
	//private static LookupServiceData lookupServiceDataObj;
	private LookupServiceData lookupServiceData;
	
	private static HashMap<String, String> sizeDimMap=new HashMap<String, String>();
	private static HashMap<String, String> sizeUnits=new HashMap<String, String>();
	//private static ArrayList<String> dimList=new ArrayList<String>();
	//private static ArrayList<String> unitList=new ArrayList<String>();
	
	private static LinkedHashSet<String> dimList=new LinkedHashSet<String>();
	private static LinkedHashSet<String> unitList=new LinkedHashSet<String>();
	
	static{
		sizeDimMap.put("Arc","Arc");
		sizeDimMap.put("Area", "Area");
		sizeDimMap.put("Circumference", "Circumference");
		sizeDimMap.put("D", "Depth");
		sizeDimMap.put("DIA", "Dia");
		sizeDimMap.put("H", "Height");
		sizeDimMap.put("LONG", "Length");
		sizeDimMap.put("T", "Thickness");
		sizeDimMap.put("W", "Width");
		sizeDimMap.put("L", "Length");
		sizeDimMap.put("LENGTH", "Length");
		
		sizeUnits.put("CM", "cm");
		sizeUnits.put("FEET", "ft");
		sizeUnits.put("FEE", "ft");
		sizeUnits.put("FT", "ft");
		sizeUnits.put("INC", "in");
		sizeUnits.put("MTR", "meter");
		sizeUnits.put("ML", "mil");
		sizeUnits.put("MM", "mm");
		sizeUnits.put("ST", "sq ft.");
		sizeUnits.put("SI", "sq in.");
		sizeUnits.put("YD", "yds");
		
		dimList.add("Circumference");
		dimList.add("Arc");
		dimList.add("DIA");
		dimList.add("Area");
		dimList.add("LENGTH");
		dimList.add("LONG");
		dimList.add("D");
		dimList.add("H");
		dimList.add("T");
		dimList.add("W");
		dimList.add("L");
		
		unitList.add("CM");
		unitList.add("FEET");
		unitList.add("FEE");
		unitList.add("INC");
		unitList.add("MTR");
		unitList.add("ML");
		unitList.add("MM");
		unitList.add("ST");
		unitList.add("SI");
		unitList.add("YD");
		unitList.add("FT");
		//dimList.add("");
		
	}
	
public Product getExistingProductData(Product existingProduct , ProductConfigurations existingProductConfig){
		
		ProductConfigurations newProductConfigurations=new ProductConfigurations();
		Product newProduct=new Product();
		
		try{
			//categories
			List<String> listCategories=new ArrayList<String>();
			listCategories=existingProduct.getCategories();
			if(!CollectionUtils.isEmpty(listCategories)){
				newProduct.setCategories(listCategories);
			}
		//Images
		List<Image> images=existingProduct.getImages();
		if(!CollectionUtils.isEmpty(images)){
			newProduct.setImages(images);
		}
		//themes
				List<Theme>	themes=existingProductConfig.getThemes();
				if(!CollectionUtils.isEmpty(themes)){
					newProductConfigurations.setThemes(themes);
				}
		newProduct.setProductConfigurations(newProductConfigurations);
		}catch(Exception e){
			_LOGGER.error("Error while processing Existing Product Data " +e.getMessage());
		}
		 _LOGGER.info("Completed processing Existing Data");
		return newProduct;
		
	}
	public List<ImprintMethod> getImprintMethods(String imprintValue){
		List<ImprintMethod> listOfImprintMethodsNew = new ArrayList<ImprintMethod>();
		//for (String value : listOfImprintMethods) {
			ImprintMethod imprintMethodObj =new ImprintMethod();
			if(lookupServiceData.isImprintMethod(imprintValue.toUpperCase())){
				imprintMethodObj.setAlias(imprintValue);
				imprintMethodObj.setType(imprintValue);
			}else{
				imprintMethodObj.setAlias(imprintValue);
				imprintMethodObj.setType("OTHER");
			}
			listOfImprintMethodsNew.add(imprintMethodObj);
		//}
		
		
		return listOfImprintMethodsNew;
		
	}


	
	
	public ShippingEstimate getShippingEstimates(String shippingValue,
			String shippingWeightValue) {

		
		ShippingEstimate ItemObject = new ShippingEstimate();
		
		try{
			shippingValue=shippingValue.toLowerCase().trim();
			String shippingitemValue="";
			String shippingDimVal="";
			if(!StringUtils.isEmpty(shippingValue)){
			if(!shippingValue.equals("none"))
			{
			if(shippingValue.contains("/")){
				String tempValArr[]=shippingValue.split("/");
				shippingDimVal=tempValArr[0];
				shippingitemValue=tempValArr[1];
				
			}
		}
		}
			
		List<NumberOfItems> listOfNumberOfItems = new ArrayList<NumberOfItems>();
		List<Weight> listOfWeight = new ArrayList<Weight>();

		NumberOfItems itemObj = new NumberOfItems();
		
			if(!StringUtils.isEmpty(shippingitemValue)){
			shippingitemValue=shippingitemValue.replace("sets", "");
			shippingitemValue=shippingitemValue.replace("pcs", "");
			
			itemObj.setUnit("per Carton");
			itemObj.setValue(shippingitemValue);
			listOfNumberOfItems.add(itemObj);
			ItemObject.setNumberOfItems(listOfNumberOfItems);
		}

			if(!StringUtils.isEmpty(shippingWeightValue)){
			shippingWeightValue=shippingWeightValue.toLowerCase();
			if(shippingWeightValue.contains(",")){
				String tempArr[]=shippingWeightValue.split(",");
				shippingWeightValue=tempArr[0];
			}
			if(shippingWeightValue.contains("/")){
				String tempArrr[]=shippingWeightValue.split("/");
				shippingWeightValue=tempArrr[0];
			}
			String unitValue="";
			if(shippingWeightValue.contains("kg")){
				shippingWeightValue=shippingWeightValue.replace("kg", "");
				unitValue="kg";
			}else{
				shippingWeightValue=shippingWeightValue.replace("g", "");
				unitValue="grams";
			}
			
			Weight weightObj = new Weight();
			weightObj.setUnit(unitValue);
			weightObj.setValue(shippingWeightValue);
			listOfWeight.add(weightObj);
			ItemObject.setWeight(listOfWeight);
		}

			if(!StringUtils.isEmpty(shippingDimVal)){
			String unitVal="in";
			if(shippingDimVal.contains("cm")){
				shippingDimVal=shippingDimVal.replace("cm", "");
				unitVal="cm";
			}
			String shipDimenArr[] = shippingDimVal.split("*");
			List<Dimensions> dimenlist = new ArrayList<Dimensions>();
			Dimensions dimensionObj = new Dimensions();
			for (int i = 0; i <= shipDimenArr.length - 1; i++) {
				if (i == 0) {
					dimensionObj.setHeight(shipDimenArr[i]);
					dimensionObj.setHeightUnit(unitVal);
				} else if (i == 1) {
					dimensionObj.setLength(shipDimenArr[i]);
					dimensionObj.setLengthUnit(unitVal);
				} else if (i == 2) {

					dimensionObj.setWidth(shipDimenArr[i]);
					dimensionObj.setWidthUnit(unitVal);
				}

				dimenlist.add(dimensionObj);
			}
			ItemObject.setDimensions(dimensionObj);
		}
		}catch(Exception e){
			_LOGGER.error("Error while processing Shipping Estimate :"+e.getMessage());
			return new ShippingEstimate();
		}
		return ItemObject;

	}
	
	
	
public List<Material> getMaterialList(String originalMaterialvalue){
		
		Material materialObj = new Material();
		List<Material> listOfMaterial = new ArrayList<>();
		/*if(!StringUtils.isEmpty(originalMaterialvalue)){
			originalMaterialvalue = CommonUtility.removeSpecialSymbols(originalMaterialvalue,specialCharacters);
			originalMaterialvalue = originalMaterialvalue.replaceAll("é", "e");
		}*/
		
		if(originalMaterialvalue.equals("flannelette+nonwoven+wool")){
			originalMaterialvalue="Cotton";
		}else if(originalMaterialvalue.equals("Fla*+PP cotton")){
			originalMaterialvalue="Wool";
		}
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

public List<Color> getProductColors(String color){
	List<Color> listOfColors = new ArrayList<>();
	try{
	Color colorObj = null;
	color=color.replaceAll("\\|",",");
	color=color.replaceAll("-","/");
	color=color.replaceAll("+","/");
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
		String colorGroup = PioneerLLCConstants.getColorGroup(colorName.trim());
		if(colorGroup==null){
			_LOGGER.error("Not found in given mapping "+colorName);
		}
		
		boolean comboFlag=false;
		if (colorGroup == null) {
			if(colorName.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)){
			comboFlag=true;
			}
		}else if(colorGroup.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)){
			comboFlag=true;
		}
			
		if (comboFlag) {
			if(colorGroup==null){
				colorGroup=colorName;
			}
			colorGroup=colorGroup.replaceAll("&","/");
			colorGroup=colorGroup.replaceAll(" w/","/");
			colorGroup=colorGroup.replaceAll(" W/","/");
			colorGroup=colorGroup.replaceAll(" ","");
			
				if(isComboColor(colorGroup)){
					List<Combo> listOfCombo = null;
					String[] comboColors = CommonUtility.getValuesOfArray(colorGroup,
							ApplicationConstants.CONST_DELIMITER_FSLASH);
					String colorFirstName = PioneerLLCConstants.getColorGroup(comboColors[0].trim());
					colorObj.setName(colorFirstName == null?"Other":colorFirstName);
					int combosSize = comboColors.length;
					if (combosSize == ApplicationConstants.CONST_INT_VALUE_TWO) {
						String colorComboFirstName = PioneerLLCConstants.getColorGroup(comboColors[1].trim());
						colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
						listOfCombo = getColorsCombo(colorComboFirstName, ApplicationConstants.CONST_STRING_EMPTY,
								combosSize);
					} else{
						String colorComboFirstName = PioneerLLCConstants.getColorGroup(comboColors[1].trim());
						colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
						
						String colorComboSecondName = PioneerLLCConstants.getColorGroup(comboColors[2].trim());
						colorComboSecondName = colorComboSecondName == null?"Other":colorComboSecondName;
						listOfCombo = getColorsCombo(colorComboFirstName,colorComboSecondName, combosSize);
					}
					String alias = colorGroup.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH, "-");
					colorObj.setAlias(alias);
					colorObj.setCombos(listOfCombo);
				} else {
					String[] comboColors = CommonUtility.getValuesOfArray(colorGroup,
							ApplicationConstants.CONST_DELIMITER_FSLASH);
					String mainColorGroup = PioneerLLCConstants.getColorGroup(comboColors[0].trim());
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
		 mainColor = PioneerLLCConstants.getColorGroup(colorVals[0].trim());
		 secondaryColor = PioneerLLCConstants.getColorGroup(colorVals[1].trim());
		 if(mainColor != null && secondaryColor != null){
			 return true;
		 }
	} else if(colorVals.length == ApplicationConstants.CONST_INT_VALUE_THREE){
		 mainColor      = PioneerLLCConstants.getColorGroup(colorVals[0].trim());
		 secondaryColor = PioneerLLCConstants.getColorGroup(colorVals[1].trim());
		 thirdColor     = PioneerLLCConstants.getColorGroup(colorVals[2].trim());
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

public ProductConfigurations getSizes(String sizeValue,ProductConfigurations existingConfig) {
	Size sizeObj = new Size();
	String sizeGroup="dimension";
	String unitValue="in";
	String attriValue="Length";
	
	
	//1.75 DIA x 4.5
	boolean flag=false;
	try{
		if(sizeValue.contains("     ")){
			sizeValue=sizeValue.replaceAll("     ", "_____");
		}
		if(sizeValue.contains("_____")){
			String strArr[]=sizeValue.split("_____");
			
			String strTemp=strArr[0];
			if(strTemp.contains(":")){
			String strAAR[]=strTemp.split(":");
			
			sizeValue=strAAR[1].trim();
			}else{
				sizeValue=strArr[0].trim();
			}
			
		}
		if(sizeValue.equals("customized")){
			OtherSize otherSize = new OtherSize();
			List<Value> listOfVal = null;
			
				listOfVal = getApparelAndOtherValuesObj("customized");
			
			otherSize.setValues(listOfVal);
			sizeObj.setOther(otherSize);
			existingConfig.setSizes(sizeObj);
		}
		//String DimenArr[] = {sizeValue} ;
		//else{
			Dimension dimensionObj = new Dimension();
			List<Values> valuesList = new ArrayList<Values>();
			List<Value> valuelist = null;
			Values valuesObj = new Values();
				Value valObj;
				sizeValue=sizeValue.replaceAll("”","\"");
				sizeValue=sizeValue.replaceAll("\"","INC");
				sizeValue=sizeValue.replaceAll(";",",");
				sizeValue=sizeValue.replaceAll(":","");
				sizeValue = sizeValue.replace(".", "");
				sizeValue=sizeValue.replaceAll("Φ","");
				//String valuesArr[]=sizeValue.split(",");
				String valuesArr[]=sizeValue.split("*");
				for (String tempValue : valuesArr) {
					valuesObj = new Values();
					tempValue=tempValue.toUpperCase();
					if(tempValue.contains("FT LONG") || tempValue.contains("FEET LONG")){
						
						tempValue=tempValue.substring(0, (tempValue.indexOf("F")));
						tempValue=tempValue.trim();
						
						tempValue=tempValue+" FEE";
						//tempValue=tempValue.replace("FEET IN LENGTH", "FEE");
					}
				if(tempValue.contains("FEET IN")){
					tempValue=tempValue.replace("FEET IN LENGTH", "FEE");
				}
				if(tempValue.contains("IN LENGTH")){
					tempValue=tempValue.replace("IN LENGTH", "L");
				}
				if(tempValue.contains("FT")){
					tempValue=tempValue.replace("FT", "FEE");
				}
				tempValue=removeSpecialChar(tempValue,1);
				//String DimenArr[]=tempValue.split("X");
				String DimenArr[]=tempValue.split("*");
			valuelist = new ArrayList<Value>(); 
			int tempCount=0;
			for (String value : DimenArr) {
				value=value.toUpperCase();
				valObj = new Value();
				String tempAttri="";
				if(tempCount==1){
					tempAttri="W";
					tempCount++;
				}else if(tempCount==2){
					tempAttri="H";
				}else{
					 tempAttri=getAttriTypeValue(value);
				}
				
				if(tempAttri.contains("-DEFAULT")){
					tempAttri=tempAttri.replace("-DEFAULT", "");
					tempCount++;
				}
				attriValue=getAttriValue(tempAttri);
				value=removeSpecialChar(value, 2);
				String tempUnitValue=getUNitTypeValue(value);
				unitValue=getUnitValue(tempUnitValue);
				value=removeSpecialChar(value, 3);
				valObj.setAttribute(attriValue);
				valObj.setValue(value.trim());
				valObj.setUnit(unitValue);
				valuelist.add(valObj);
			}	
			valuesObj.setValue(valuelist);
			valuesList.add(valuesObj);
			
		}
			
			///////////////
		
		dimensionObj.setValues(valuesList);
		sizeObj.setDimension(dimensionObj);
	//}
	//}
		existingConfig.setSizes(sizeObj);
	}
	catch(Exception e)
	{
		_LOGGER.error("Error while processing Size :"+e.getMessage());
		existingConfig.setSizes(sizeObj);
		return existingConfig;
	}
	return existingConfig;
}

public static String getAttriTypeValue(String sizeDim){
    if(sizeDim != null){
    	 for (String dim : dimList) {
			    if(sizeDim.contains(dim)){
			    	return dim;
			    }
		}
    }
return "LENGTH-DEFAULT";
}

public static String getAttriValue(String attriVal){

if(sizeDimMap.containsKey(attriVal)){
return	sizeDimMap.get(attriVal);
}
return "Length";
}


public static String getUNitTypeValue(String sizeUnit){
if(sizeUnit != null){
	 for (String dim : unitList) {
		    if(sizeUnit.contains(dim)){
		    	return dim;
		    }
	}
}
return "INC";
}

public static String getUnitValue(String UnitVal){
if(sizeUnits.containsKey(UnitVal)){
	return	sizeUnits.get(UnitVal);
}
return "INC";
}

public static String removeSpecialChar(String tempValue,int number){
	if(number==1){
	tempValue=tempValue.replaceAll("(CABLE|LENGTH|CLEAR|CASE|ONE|SIDE|EARPHONE|INCLUDE|HOOK|WHITE|BOX|DELUXE|BULK|MEASURE|WITH|CARABINER|ITEM|SINGLE|EAR|INCLUDE)", "");
	tempValue=tempValue.replaceAll("\\(","");
	tempValue=tempValue.replaceAll("\\)","");
	}
	
	if(number==2){
		tempValue=tempValue.replaceAll("(DIA|LONG|LENGTH|Arc|Area|Circumference|L|D|H|T|W)", "");
		tempValue=tempValue.replaceAll("\\(","");
		tempValue=tempValue.replaceAll("\\)","");
		}
	
	if(number==3){
		tempValue=tempValue.replaceAll("(CM|FEET|FEE|FT|INC|MTR|ML|MM|ST|SI|YD)", "");
		tempValue=tempValue.replaceAll("\\(","");
		tempValue=tempValue.replaceAll("\\)","");
		}

return tempValue;

}
private static List<Value> getApparelAndOtherValuesObj(String val){
	 List<Value> listOfValue = new ArrayList<>();
	 Value valObj = new Value();
	 valObj.setValue(val);
	 listOfValue.add(valObj);
	 return listOfValue;
}
public LookupServiceData getLookupServiceData() {
	return lookupServiceData;
}
public void setLookupServiceData(LookupServiceData lookupServiceData) {
	this.lookupServiceData = lookupServiceData;
}



}
