package parser.sportsmanBag;

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
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.a4tech.util.LookupData;

public class SportsManBagAttributeParser {

	private LookupServiceData lookupServiceData;
	private static List<String> listOfLookupMaterials = null;
	private static Logger _LOGGER = Logger.getLogger(SportsManBagAttributeParser.class);
	public Product keepExistingProductData(Product existingProduct){
		ProductConfigurations oldProductConfig = existingProduct.getProductConfigurations();
		Product newProduct = new Product();
		ProductConfigurations newProductConfig = new ProductConfigurations();
		if(!CollectionUtils.isEmpty(existingProduct.getImages())){
			newProduct.setImages(existingProduct.getImages());
		}
		if(!CollectionUtils.isEmpty(existingProduct.getCategories())){
			newProduct.setCategories(existingProduct.getCategories());
		}
		if(!CollectionUtils.isEmpty(existingProduct.getCatalogs())){
			newProduct.setCatalogs(existingProduct.getCatalogs());
		}
		if(!CollectionUtils.isEmpty(existingProduct.getProductKeywords())){
			newProduct.setProductKeywords(existingProduct.getProductKeywords());
		}
		if(!StringUtils.isEmpty(existingProduct.getSummary())){
			String summary = existingProduct.getSummary();
			if(summary.contains("Velcro")){
				summary = summary.replaceAll("Velcro", "");
			}
			newProduct.setSummary(summary);
		}
		 if(!CollectionUtils.isEmpty(oldProductConfig.getThemes())){
			 newProductConfig.setThemes(oldProductConfig.getThemes());
				List<Theme> themeList = oldProductConfig.getThemes().stream().map(themeVal ->{
					themeVal.setName(themeVal.getName());
					if(themeVal.getName().equals("ECO FRIENDLY")){
						themeVal.setName("Eco & Environmentally Friendly");
					}
					return themeVal;
				}).collect(Collectors.toList()); 
				newProductConfig.setThemes(themeList);
			}
		newProduct.setProductConfigurations(newProductConfig);
		return newProduct;
	}
	public List<Color> getProductColor(String color){
		List<Color> colorsList = new ArrayList<>();
		Color colorObj = null;
		String[] colors = CommonUtility.getValuesOfArray(color, ",");
		for (String colorName : colors) {
			colorName = colorName.trim();
			if(StringUtils.isEmpty(colorName)){
				continue;
			}
			if(colorName.contains("®")){
				colorName = colorName.replaceAll("®", "");
			}
			colorObj = new Color();
			if(colorName.contains("/")){
				colorObj = getColorCombo(colorName, "/");
			} else {
				String colorGroup = SportsManBagColorMapping.getColorGroup(colorName);
				colorObj.setName(colorGroup);
				colorObj.setAlias(colorName);
			}
			colorsList.add(colorObj);	
		}
		return colorsList;
	}
	private Color getColorCombo(String comboVal,String colorDelimiter){
		Color colorObj = new Color();
		List<Combo> listOfComos = new ArrayList<>();
		Combo comboObj = new Combo();
		Combo comboObj2 = new Combo();
		String[] comboColors = CommonUtility.getValuesOfArray(comboVal,colorDelimiter);
		colorObj.setName(
				SportsManBagColorMapping.getColorGroup(comboColors[ApplicationConstants.CONST_NUMBER_ZERO]));
		comboObj.setName(
				SportsManBagColorMapping.getColorGroup(comboColors[ApplicationConstants.CONST_INT_VALUE_ONE]));
		comboObj.setType(ApplicationConstants.CONST_STRING_SECONDARY);
		if(comboColors.length == 3){
            comboObj2.setName(
            		SportsManBagColorMapping.getColorGroup(comboColors[ApplicationConstants.CONST_INT_VALUE_TWO].toUpperCase()));
            comboObj2.setType(ApplicationConstants.CONST_STRING_TRIM);
            listOfComos.add(comboObj2);
      }	
		colorObj.setAlias(comboVal.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH,
				ApplicationConstants.CONST_DELIMITER_HYPHEN));
		listOfComos.add(comboObj);
		colorObj.setCombos(listOfComos);
		return colorObj;
	}
	public ShippingEstimate getShippingEstimateValues(String numberOfItems){
		ShippingEstimate shippingEstimate = new ShippingEstimate();
		List<NumberOfItems> numberOfItem = null;
        NumberOfItems numberOfItemsObj = null;
       if(!numberOfItems.equals(ApplicationConstants.CONST_STRING_ZERO)){
    	   numberOfItem = new ArrayList<NumberOfItems>();
    	   numberOfItemsObj = new NumberOfItems();
    	   numberOfItemsObj.setValue(numberOfItems);
    	   numberOfItemsObj.setUnit("Per Case");
    	   numberOfItem.add(numberOfItemsObj);
       }
       if(numberOfItem != null){
    	   shippingEstimate.setNumberOfItems(numberOfItem);
       }
       return shippingEstimate;
	}
 public List<Material> getProductMaterial(String material){
	 List<Material> materialList = new ArrayList<>();
	 String[] materials = null;
	 if(material.equalsIgnoreCase("50% Polyester (6 1/4% Recycled),46% Cotton (6 1/4% Organic), 4% Rayon, Fabric Washed")){
		 materials = new String[]{material};
	 } else {
		  materials = CommonUtility.getValuesOfArray(material, ",");	 
	 }
	
	 Material materialObj = null;
		for (String mtrlName : materials) {
			try {
				mtrlName = mtrlName.trim();
				materialObj = new Material();
				if(isSpecialMaterial(mtrlName)){
					_LOGGER.info("Unable to parser material Value,we have set materia Name as Other: "+mtrlName);
					materialObj.setName("Other");
					materialObj.setAlias(mtrlName);
				}else if (isBlendMaterial(mtrlName)) {
                   materialObj = getBlendMaterial(mtrlName);
				} else if (isComboMaterial(mtrlName)) {
					// materialObj.setCombo(combos);
					materialObj = getMaterialCombo(mtrlName);
				} else {
					materialObj.setName(getMaterialType(mtrlName.toUpperCase()));
					materialObj.setAlias(mtrlName);
				}
				materialList.add(materialObj);
			} catch (Exception exce) {
				_LOGGER.info("Unable to parser material Value,we have set materia Name as Other: "+mtrlName);
				materialObj.setName("Other");
				materialObj.setAlias(mtrlName);
			}
		}
	 return materialList;
 }
 private Material getMaterialCombo(String val){
		Material materialObj = new Material();
		try {
			Combo combo = new Combo();
			String[] materialCombos = CommonUtility.getValuesOfArray(val, "/");
			// materialObj.set
			materialObj.setName(getMaterialType(materialCombos[0]).toUpperCase());
			combo.setName(getMaterialType(materialCombos[1]).toUpperCase());
			materialObj.setCombo(combo);
			materialObj.setAlias(val);
		} catch (Exception exce) {
			_LOGGER.error("Unable to parser combo material Value,we have set materia Name as Other: " + val);
			materialObj.setName("Other");
			materialObj.setAlias(val);
		}
		return materialObj;
 }
 private Material getBlendMaterial(String val){
	 Material materialObj = new Material();
	 List<BlendMaterial> blendMaterialList = new ArrayList<>();
		try {
			String percentages = val.replaceAll("[^%]", "");
			String slah = val.replaceAll("[^/]", "");
			int noOfPercentages = percentages.length();
			int noOfSlash = slah.length();
			String[] allMaterials = val.split("/");
			String m1 = "";
			String m2 = "";
			String firstPercent = "";String secondPer = "";
			if ((noOfPercentages == 2 && noOfSlash == 1) ||(noOfPercentages == 1 && noOfSlash == 1)) {// 65% polyetser/ cotton 35%
				m1 = allMaterials[0];
				m2 = allMaterials[1];
				firstPercent = m1.replaceAll("[^0-9]", "").trim();
				secondPer = m2.replaceAll("[^0-9]", "").trim();
				blendMaterialList = getBlendMaterialsList(firstPercent, m1, blendMaterialList);
				blendMaterialList = getBlendMaterialsList(secondPer, m2, blendMaterialList);
				materialObj.setName("Blend");
				materialObj.setBlendMaterials(blendMaterialList);
				materialObj.setAlias(val);
			} else if (noOfPercentages == 3 && noOfSlash == 2) {// 50%wool/30%acrylic/20%Nylon
				m1 = allMaterials[0];
				m2 = allMaterials[1];
				firstPercent = m1.replaceAll("[^0-9]", "").trim();
				secondPer = m2.replaceAll("[^0-9]", "").trim();
				int secondVal = 100-Integer.parseInt(firstPercent);
				//materialObj.setb
				blendMaterialList = getBlendMaterialsList(firstPercent, m1, blendMaterialList);
				blendMaterialList = getBlendMaterialsList(String.valueOf(secondVal), m2, blendMaterialList);
				materialObj.setName("Blend");
				materialObj.setBlendMaterials(blendMaterialList);
				materialObj.setAlias(val);
			} else {
				materialObj.setAlias("Other");
				materialObj.setName(val);
			}
			
		} catch (Exception exce) {
			_LOGGER.error("Unable to parser blend material Value,we have set materia Name as Other: " + val);
			materialObj.setName("Other");
			materialObj.setAlias(val);
		}
		return materialObj;
	}
  private List<BlendMaterial> getBlendMaterialsList(String percentage,String materialVal,List<BlendMaterial> listOfBlendMaterials){
	  BlendMaterial blendMaterialObj = new BlendMaterial();
	  materialVal = materialVal.replaceAll("[^a-zA-Z]", "").trim();
	  materialVal = getMaterialType(materialVal.toUpperCase());
	  if(materialVal.equals("Other")){
		  materialVal = "Other Fabric";
	  }
	  blendMaterialObj.setName(materialVal);
	  blendMaterialObj.setPercentage(percentage);
	  listOfBlendMaterials.add(blendMaterialObj);
	  return listOfBlendMaterials;
  }
	 private boolean isBlendMaterial(String val){
	 if(val.contains("/") && val.contains("%")){
		 return true;
	 }
	 return false;
 }
 private boolean isComboMaterial(String val){
	 if(val.contains("/")){
		 return true;
	 }
	 return false;
 }
 private boolean isSpecialMaterial(String mtrlVal){
	 if(mtrlVal.equalsIgnoreCase("Brushed cotton/polyester blend 65% cotton/35% polyester") || 
	    mtrlVal.equalsIgnoreCase("50% Polyester (6 1/4% Recycled),46% Cotton (6 1/4% Organic), 4% Rayon, Fabric Washed") ||
	    mtrlVal.equalsIgnoreCase("Brushed cotton/polyester blend 100% cotton brushed twill crown")){
		 return true;
	 }
	 
	 return false;
 }
 public String getMaterialType(String value){
	 if(listOfLookupMaterials == null){
		 listOfLookupMaterials = lookupServiceData.getMaterialValues();;
	 }
		//List<String> listOfLookupMaterials = lookupServiceData.getMaterialValues();
		String materialGroup = listOfLookupMaterials.stream()
				                                  .filter(mtrlName -> value.contains(mtrlName))
				                                  .collect(Collectors.joining());
		if(StringUtils.isEmpty(materialGroup)){
			materialGroup = "Other";
		}
		return materialGroup;	
	}
 public Size getProductSize(String sizeVal, String sheetName){
	 Size sizeObj = new Size();
	 Values valuesObj = null;
	 Dimension dimentionObj = new Dimension();
	 List<Values> listOfValues = new ArrayList<>();
	 List<Value> listOfValue = new ArrayList<>();
	 String[] sizeVals = CommonUtility.getValuesOfArray(sizeVal, ",");
	 for (String sizeName : sizeVals) {
		 if(sheetName.equals("Bags") || sheetName.equals("Aprons and Towels")){
			 if (sizeName.contains("L") || sizeName.contains("H") || sizeName.contains("W")
	 					|| sizeName.contains("D") || sizeName.contains("SQ") || sizeName.contains("DIA") 
	 					|| sizeName.contains("Dia") || sizeName.contains("dia")) {
				 sizeName = getFinalSizeValue(sizeName);
					String lastChar = sizeName.substring(sizeName.length() - 1);
					if(lastChar.equals(":")){
						sizeName = sizeName.substring(0, sizeName.length() - 1);// trim last character i.e :
					}
	             String[] sss = CommonUtility.getValuesOfArray(sizeName, ":");
	            if(sss.length == 2){
	         	   String finalSize = sss[0];
	         	   valuesObj = getOverAllSizeValObj(finalSize, sss[1], "", "");
	            } else if(sss.length == 4){
	         	   String finalSize = sss[0] + "x"+sss[2];
						valuesObj = getOverAllSizeValObj(finalSize, sss[1], sss[3], "");
	            } else if(sss.length == 6){
	         	   String finalSize = sss[0] + "x"+sss[2]+ "x"+sss[4];
						valuesObj = getOverAllSizeValObj(finalSize, sss[1], sss[3], sss[5]);
	            } 
			 } else {
				 valuesObj = getOverAllSizeValObj(sizeName, "Length", "Width", "Height");
			 }
			 listOfValues.add(valuesObj);
		 } else {
			 valuesObj = new Values();
				Value valueObj =  getValueObj(sizeName);
				listOfValue.add(valueObj);
			}
		 }
	 if(sheetName.equals("Bags") || sheetName.equals("Aprons and Towels")){
		 dimentionObj.setValues(listOfValues);
		 sizeObj.setDimension(dimentionObj);
	 } else {
		 OtherSize otherSize = new OtherSize();
		 otherSize.setValues(listOfValue);
		 sizeObj.setOther(otherSize);
	 }
	 return sizeObj;
	}	 
 private Value getValueObj(String value){
	Value valueObj = new Value();
	valueObj.setValue(value);
	return valueObj;
}
 private String getFinalSizeValue(String val){
	 //  val = removeWordsFromSize(val);
	   val = val.replaceAll("[^0-9/,DWLHSQDiadiaxXDIA. ]", "");
	   String[] sizes = null;
	   StringBuilder sizess = new StringBuilder();
	   if(val.contains("SQ")){
		   sizess = getSizeSqure(val);
	   } else{
		   if(val.contains("x")){
			   sizes = CommonUtility.getValuesOfArray(val, "x");
		   } else {
			   sizes = CommonUtility.getValuesOfArray(val, "X");
		   }
		   for (String size : sizes) {
			String sizeVal =  size.replaceAll("[^0-9/ ]", "").trim();
			String sizeUnit = size.replaceAll("[^a-zA-Z]", "").trim();
			String temp = sizeUnit; 
			if(StringUtils.isEmpty(sizeUnit)){
				sizeUnit = "L";// default value assign
			}
			if(sizeUnit.equalsIgnoreCase("aDia")){
				sizeUnit = "Dia";
			} else if(sizeUnit.equals("LL")){
				sizeUnit = "L";
			} else{
				
			}
			sizeUnit = LookupData.getSizeUnit(sizeUnit);
			if(sizeUnit == null){
				if(!temp.equalsIgnoreCase("dia")){
					temp = temp.replaceAll("[^LHWD]", "").trim();// HERE trim all unnecessary characters
					if(StringUtils.isEmpty(temp)){
						temp = "L";
					}
					sizeUnit = LookupData.getSizeUnit(temp);
				}
			}
			String ss = sizeVal+":"+sizeUnit;
			sizess.append(ss).append(":");
		}   
	   }  
	   return sizess.toString();
 }
 private StringBuilder getSizeSqure(String sizeVal){
	   StringBuilder finlSize = new StringBuilder();
	   if(sizeVal.contains("x")){//7/8" SQ x 3 3/4" L
		   String[] sss = CommonUtility.getValuesOfArray(sizeVal, "x");
		   String s1 = sss[1];
		   String squreVal = sss[0];
		   String unit1 = null,unit2 = null,unit3 = null;
		   if(s1.contains("L")){
			   unit1 = "Width";unit2 = "Height";unit3 = "Length";
		   } else if(s1.contains("H")){
			   unit1 = "Width";unit2 = "Length";unit3 = "Height";
		   } else if(s1.contains("D")){
			   unit1 = "Width";unit2 = "Length";unit3 = "Depth";
		   }
		   s1  = s1.replaceAll("[^0-9/ ]", "");
		   squreVal  = squreVal.replaceAll("[^0-9/ ]", "");
		   finlSize.append(squreVal).append(":").append(unit1).append(":").append(squreVal).append(":").append(unit2)
			.append(":").append(s1).append(":").append(unit3);
		} else{//7/8 SQ
			sizeVal  = sizeVal.replaceAll("[^0-9/ ]", "");
			finlSize.append(sizeVal).append(":").append("Length").append(":").append(sizeVal).append(":").append("Width");
		}	   
	   return finlSize;
 }
 private Values getOverAllSizeValObj(String val,String unit1,String unit2,String unit3){
		//Overall Size: 23.5" x 23.5"
		String[] values = null;
		if(val.contains("X")){
			values = val.split("X");
		} else {
			values = val.split("x");
		}
		Value valObj1 = null;
		Value valObj2 = null;
		Value valObj3 = null;
		List<Value> listOfValue = new ArrayList<>();
		if(values.length == ApplicationConstants.CONST_INT_VALUE_ONE){
			 valObj1 = getValueObj(values[0].trim(), unit1, "in");
			  listOfValue.add(valObj1);
		} else if(values.length == ApplicationConstants.CONST_INT_VALUE_TWO){
			 valObj1 = getValueObj(values[0].trim(), unit1, "in");
			 valObj2 = getValueObj(values[1].trim(), unit2, "in");
			 listOfValue.add(valObj1);
		     listOfValue.add(valObj2);
		} else if(values.length == ApplicationConstants.CONST_INT_VALUE_THREE){
			 valObj1 = getValueObj(values[0].trim(), unit1, "in");
			 valObj2 = getValueObj(values[1].trim(),unit2, "in");
			 valObj3 = getValueObj(values[2].trim(), unit3, "in");
			 listOfValue.add(valObj1);
		     listOfValue.add(valObj2);
		     listOfValue.add(valObj3);
		}
		 Values valuesObj = new Values(); 
		 valuesObj.setValue(listOfValue);
		 return valuesObj;
	}
private Value getValueObj(String value,String attribute,String unit){
		Value valueObj = new Value();
		valueObj.setAttribute(attribute);
		valueObj.setUnit(unit);
		valueObj.setValue(value);
		return valueObj;
	}

public List<ImprintMethod> getProductImprintMethods(String imprintMethodName){
	List<ImprintMethod> imprintMethodList = new ArrayList<>();
	ImprintMethod imprintMethodObj = new ImprintMethod();
	imprintMethodObj.setType("Unimprinted");
	imprintMethodObj.setAlias("Unimprinted");
	imprintMethodList.add(imprintMethodObj);
	return imprintMethodList;
	
}
 public LookupServiceData getLookupServiceData() {
		return lookupServiceData;
	}
	public void setLookupServiceData(LookupServiceData lookupServiceData) {
		this.lookupServiceData = lookupServiceData;
	}
}
