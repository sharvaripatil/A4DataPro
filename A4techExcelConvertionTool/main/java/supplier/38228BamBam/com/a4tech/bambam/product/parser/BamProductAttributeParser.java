package com.a4tech.bambam.product.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.bambam.util.BamBamColorMapping;
import com.a4tech.bambam.util.BamLookupData;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.BlendMaterial;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.ImprintColor;
import com.a4tech.product.model.ImprintColorValue;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Inventory;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class BamProductAttributeParser {
  
   private final static Logger _LOGGER  = Logger.getLogger(BamProductAttributeParser.class);
   public static final String   CONST_STRING_COMBO_TEXT = "Combo";
   private LookupServiceData lookupServiceData;
   private BamLookupData       bamLookupData;  
   
public List<ImprintSize> getImprintSizes(String imprvalue){
		List<ImprintSize> listOfImprintSize = new ArrayList<>();
		ImprintSize imprintSizeObj = null;
		if(imprvalue.contains("”")){
			imprvalue = imprvalue.replaceAll("”", "");
		}
		if (imprvalue.contains(ApplicationConstants.CONST_STRING_COMMA_SEP)) {
			imprvalue = imprvalue.replaceAll(
					ApplicationConstants.CONST_STRING_COMMA_SEP,
					ApplicationConstants.CONST_DELIMITER_SEMICOLON);
		}
		String[] imprValues = CommonUtility.getValuesOfArray(imprvalue,
				ApplicationConstants.CONST_DELIMITER_SEMICOLON);
		for (String imprSizeName : imprValues) {
			  if(imprSizeName.contains("(Medium)")){
				  imprSizeName = imprSizeName.replace("(Medium)", "Medium");
			  }
			imprintSizeObj = new ImprintSize();
			imprintSizeObj.setValue(imprSizeName);
			listOfImprintSize.add(imprintSizeObj);
		}
		return listOfImprintSize;
	}
   public List<Origin> getOrigins(String originName){
	   List<Origin> listOfOrigin = new ArrayList<>();
	   Origin originObj = new Origin();
	   if(originName.equalsIgnoreCase("US")){
		   originName = "U.S.A.";
	   }
	   originObj.setName(originName);
	   listOfOrigin.add(originObj);
	   return listOfOrigin;	   
   }
   public List<String> getCategories(String category){
	   List<String> listOfCategories = new ArrayList<>();
	   String[] categories = CommonUtility.getValuesOfArray(category, ApplicationConstants.CONST_STRING_COMMA_SEP);
	   for (String categoryName : categories) {
		   if(lookupServiceData.isCategory(categoryName.toUpperCase())){
			   listOfCategories.add(categoryName);
		   }
	}
	   return listOfCategories;
   }
   public Product getProductionTimes(String prdTime,Product existingProduct){
	   ProductConfigurations existingConfiguration = existingProduct.getProductConfigurations();
	   List<ProductionTime> listOfPrdTime = new ArrayList<>();
	   if(prdTime.contains("wks")){
		   listOfPrdTime = getProductionTime(prdTime);
		   existingConfiguration.setProductionTime(listOfPrdTime);
	   }else if(prdTime.equalsIgnoreCase("Check inventory as limited amount kept in VA")){
		   existingProduct.setAdditionalProductInfo(prdTime);
	   } else if(prdTime.equalsIgnoreCase("In stock; call to confirm in current inventory.")){
		   Inventory inventoryVal = getProductInventory();
		   existingProduct.setInventory(inventoryVal);
		   existingProduct.setAdditionalProductInfo("call to confirm in current inventory");
	   } else if(prdTime.contains("Free Shipping, delivery within")){
		   existingProduct.setAdditionalProductInfo(prdTime);
	   } else if(prdTime.equalsIgnoreCase("1-2 days5 to ship out if we have in stock.")){
		   listOfPrdTime = getPrdTimeValue("1-2");
		   existingConfiguration.setProductionTime(listOfPrdTime);
		   existingProduct.setAdditionalProductInfo("5 days to ship out if we have in stock.");
	   }
	   existingProduct.setProductConfigurations(existingConfiguration);
	   return existingProduct;
   }
   private Inventory getProductInventory(){
	   Inventory inventoryObj = new Inventory();
	   inventoryObj.setInventoryStatus("In Stock");
	   return inventoryObj;
   }
   private List<ProductionTime> getProductionTime(String val){
	   List<ProductionTime> listOfPrdTime = new ArrayList<>();
	   ProductionTime prdTimeObj = new ProductionTime();
	   String[] vals = val.split("wks");
	   String prdTime = vals[0];
	   StringBuilder businessDays = new StringBuilder();
	   if(prdTime.contains("-")){
		   String[] times = prdTime.split("-");
		  int time1 =  Integer.parseInt(times[0].trim())*5;
		  int time2 =  Integer.parseInt(times[1].trim())*5;
		  businessDays.append(time1).append("-").append(time2);
	   } else{
		   int time =  Integer.parseInt(prdTime.trim())*5;
		   businessDays.append(time);
	   }
	   prdTimeObj.setBusinessDays(businessDays.toString());
	   prdTimeObj.setDetails(val);
	   listOfPrdTime.add(prdTimeObj);
	   return listOfPrdTime;
   }
   private List<ProductionTime> getPrdTimeValue(String businessDays){
	   List<ProductionTime> listOfPrdTime = new ArrayList<>();
	   ProductionTime prdTimeObj = new ProductionTime();
	   prdTimeObj.setBusinessDays(businessDays);
	   prdTimeObj.setDetails("");
	   listOfPrdTime.add(prdTimeObj);
	   return listOfPrdTime;
   }
   public Product getProductMaterial(String materialVal,Product existingProduct){
	   List<Material> listOfMaterial = new ArrayList<>();
	   Material materialObj = null;
	   List<Color> listOfColor = null;
	   List<Option> listOfOtions = null;
	   ProductConfigurations existingConfig = existingProduct.getProductConfigurations();
	   try{
	   if(materialVal.contains(";")){
		   materialVal = materialVal.replaceAll(";", ",");
	   }
	   String[] mtrlVals = CommonUtility.getValuesOfArray(materialVal, ApplicationConstants.CONST_STRING_COMMA_SEP);
	   for (String materialName : mtrlVals) {
		    materialObj = new Material();
		    if(materialName.contains("Scents:")){//Scents: Lemon,
		    	// added in description
		    	String existingDesc = existingProduct.getDescription();
		    	existingDesc = existingDesc + " " + "Scents: Lemon";
		    	existingProduct.setDescription(existingDesc);
		    } else if(materialName.contains("shiny finish.") || materialName.equalsIgnoreCase("2 or 3 Stripes around bracelet running the circumference of the band per image.") ||
		    		   materialName.equalsIgnoreCase("buckle can be black (default) or white for no additional charge.") ||
		    		   materialName.equalsIgnoreCase("FDA Approved")){
		    	//ignore (shiny finish. Note: Matte finish suggested for black laser method.)
		    	continue;
		    } else if(materialName.contains("black or white") || materialName.contains("Black or White")){
		    	listOfColor = getColorsInMaterial(materialName);// setting colors
		    	existingConfig.setColors(listOfColor);
		    } else if(materialName.contains("Standard Coated Plastic or Matte Plastic or Transparent Plastic")){
		    	//Standard Coated Plastic or Matte Plastic or Transparent Plastic, Black UV 400 Lenses, FDA approved. Lens Color Options: Blue, Green, Red and Orange.
		    	materialObj = new Material();
		    	materialObj.setName("Plastic");
		    	materialObj.setAlias("Plastic");
		    	List<String> colors = Arrays.asList("Blue","Green","Red","Orange");
		    	listOfColor = getColorsInMaterial(colors);
		    	List<String> optionVals = Arrays.asList("Standard Coated","Matte","Transparent","Black UV 400 & make sure FDA is checked in certifications");
		    	listOfOtions = getOptionsInMaterial("Product","Style Option",optionVals, "");
		    	existingConfig.setOptions(listOfOtions);
		    } else if(materialName.contains("Standard Coated Plastic or Matte Plastic, Metallic")){
		      //Standard Coated Plastic or Matte Plastic, Metallic UV 400 Lenses Color Options: Blue, 
		      //Orange, Pink Silver, Gold, Rainbow. Cannot PMS match lens, FDA approved
		    	materialObj = new Material();
		    	materialObj.setName("Plastic");
		    	materialObj.setAlias("Plastic");
		    	List<String> colors = Arrays.asList("Blue","Pink","Silver","Orange","Gold","Multi Color");
		    	listOfColor = getColorsInMaterial(colors);
		    	List<String> optionVals = Arrays.asList("Standard Coated","Matte","Metallic UV 400 & make sure FDA is checked in certifications.");
		    	listOfOtions = getOptionsInMaterial("Product","Style Option",optionVals, "Cannot PMS match lens");
		    	existingConfig.setOptions(listOfOtions);
		    } else if(materialName.equalsIgnoreCase("600D Polyester with PVC Coating, PEVA Insulation, and Foam Lining.")) {
		    	Combo  comboObj = new Combo();
		    	materialObj = new Material();
		    	materialObj.setName("Polyester");
		    	comboObj.setName("PVC");
		    	materialObj.setAlias("600D Polyester with PVC Coating, PEVA Insulation, and Foam Lining.");
		    	materialObj.setCombo(comboObj);
		    } else if(isCombo(materialName)){
		    	Combo  comboObj = new Combo();
		    	materialObj = new Material();
		    	String[] comboVals = null;
		    	if(materialName.contains("/")){
		    		comboVals = CommonUtility.getValuesOfArray(materialName, "/");
		    	} else if(materialName.contains("&")){
		    		comboVals = CommonUtility.getValuesOfArray(materialName, "&");
		    	} else if(materialName.contains(" and ")){
		    		comboVals = CommonUtility.getValuesOfArray(materialName, " and ");
		    	} else{
		    		
		    	}
		    	materialObj.setName(comboVals[0]);
		    	comboObj.setName(comboVals[1]);
		    	materialObj.setAlias(materialName);
		    	materialObj.setCombo(comboObj);
		    } else if(isBlend(materialName)){
		    	materialObj = new Material();
		    	String alias = "";
		    	materialObj.setName("BLEND");
		    	List<BlendMaterial> listOfBlendMtrls = null;
		    	if(materialName.equalsIgnoreCase("92% Knitted Acrylic + 5% Spandex + 2% Other Fiber 28-30gms")){
		    		listOfBlendMtrls = getBlendMaterialValues("Acrylic", "Other Fabric", "92", "8");
		    		alias = "92% Knitted Acrylic + 5% Spandex + 2% Other Fiber 28-30gms (92/5/2)";
		    	} else if(materialName.equalsIgnoreCase("92% Knitted Acrylic + 5% Spandex 28-30gms")){
		    		listOfBlendMtrls = getBlendMaterialValues("Acrylic", "Other Fabric", "92", "8");
		    		alias = "92% Knitted Acrylic + 5% Spandex 28-30gms(92/5)";
		    	} else {//
		    		
		    		alias = materialName;
		    		listOfBlendMtrls = getBlendMaterialValues(materialName);
		    	}
		    	materialObj.setAlias(alias);
		    	materialObj.setBlendMaterials(listOfBlendMtrls);
		    } else{
		    	materialName = materialName.replaceAll("[^a-zA-Z ]", "").trim();
		    	String materialType = getTypeOfMaterial(materialName.toUpperCase());
		    	materialObj.setAlias(materialName);
		    	materialObj.setName(materialType);
		    }
		       if(!StringUtils.isEmpty(materialObj.getName())){
		    	   listOfMaterial.add(materialObj);
		       }
	     }
	   }catch (Exception e) {
			_LOGGER.error("unable to parse in Material: "+e.getMessage());
		  return existingProduct;
		}
	   existingConfig.setMaterials(listOfMaterial);
	   existingProduct.setProductConfigurations(existingConfig);
	   return existingProduct;
   }
   
   private String getTypeOfMaterial(String matrlVal){
	  List<String> lookupMaterials = lookupServiceData.getMaterialValues();
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
   private boolean isCombo(String value){
	   if((value.contains("/") || value.contains("&") || value.contains(" and")) && !value.contains("%") ){
		   return ApplicationConstants.CONST_BOOLEAN_TRUE;
	   }
	   return ApplicationConstants.CONST_BOOLEAN_FALSE;
   }
   private boolean isBlend(String value){
	   if((value.contains(" and") || value.contains("&") || value.contains("+")) && value.contains("%")){
		   return ApplicationConstants.CONST_BOOLEAN_TRUE;
	   }
	   return ApplicationConstants.CONST_BOOLEAN_FALSE;  
   }
   private List<Color> getColorsInMaterial(String value){
	   List<Color> listOfColor = new ArrayList<>();
	   Color colorObj1 = new Color();
	   Color colorObj2 = new Color();
	   colorObj1.setName("White");
	   colorObj1.setAlias("White");
	   colorObj2.setName("Black");
	   colorObj2.setAlias("Black");
	   listOfColor.add(colorObj1);
	   listOfColor.add(colorObj2);
	   return listOfColor;
   }
   private List<Color> getColorsInMaterial(List<String> colors){
	   List<Color> listOfColor = new ArrayList<>();
	   Color colorObj = null;
	   for (String colorName : colors) {
		   colorObj = new Color();
		   String alias = colorName;
		   if(colorName.equals("Multi Color")){
			   alias = "Rainbow";
		   }
		   alias = colorName +" " +"lenses";
		   colorObj.setName(colorName);
		   colorObj.setAlias(alias);
		   listOfColor.add(colorObj);
	}
	  return listOfColor;
   }
   private List<Option> getOptionsInMaterial(String optionType,String optionName,List<String> optionValues,String additionalInfo){
	   //Create a Product Option PROP:Style Option:Standard Coated, Matte, Metallic UV 400 & make sure FDA is checked in certifications. 
	  // Create a Product Option PROP:Style Option:Standard Coated, Matte, Transparent, Black UV 400 & make sure FDA is checked in certifications. 
	   List<Option> listOfOptions = new ArrayList<>();
	   List<OptionValue> listOfOptionValues = new ArrayList<>();
	   OptionValue optionValueObj = null;
	   Option optionObj = new Option();
	   optionObj.setOptionType(optionType);
	   optionObj.setName(optionName);
	   for (String optionValue : optionValues) {
		   optionValueObj = new OptionValue();
		   optionValueObj.setValue(optionValue);
		   listOfOptionValues.add(optionValueObj);
	}
	   optionObj.setValues(listOfOptionValues);
	   optionObj.setAdditionalInformation(additionalInfo);
	   optionObj.setRequiredForOrder(ApplicationConstants.CONST_BOOLEAN_FALSE);
	   optionObj.setCanOnlyOrderOne(ApplicationConstants.CONST_BOOLEAN_FALSE);
	   listOfOptions.add(optionObj);
	   return listOfOptions;
   }
   private List<BlendMaterial> getBlendMaterialValues(String val1,String val2,String per1,String per2){
	   List<BlendMaterial> listOfBlendMatrls = new ArrayList<>();
	   BlendMaterial blendMaterialObj1 = new BlendMaterial();
	   BlendMaterial blendMaterialObj2 = new BlendMaterial();
	   blendMaterialObj1.setName(val1);
	   blendMaterialObj1.setPercentage(per1);
	   blendMaterialObj2.setName(val2);
	   blendMaterialObj2.setPercentage(per2);
	   listOfBlendMatrls.add(blendMaterialObj1);
	   listOfBlendMatrls.add(blendMaterialObj2);
	   return listOfBlendMatrls;
   }
   private List<BlendMaterial> getBlendMaterialValues(String value){
	   /*
		 * 87% Polyester and 13% Nylon Ice Viscose
			95% Acrylic + 5% Spandex
			97% Polyester & 3% Spandex
			92% Polyester & 8% Spandex
		 */
	   String[] blendVals = null;
	   if(value.contains(" and")){
		   blendVals = CommonUtility.getValuesOfArray(value, " and");
	   } else if(value.contains("+")){
		   blendVals = CommonUtility.getValuesOfArray(value, "\\+");
	   } else if(value.contains("&")){
		   blendVals = CommonUtility.getValuesOfArray(value, "&");
	   } else {
		   
	   }
	   String[] belnd1 = blendVals[0].trim().split(" ");
	   String[] belnd2 =  blendVals[1].trim().split(" ");
	   String prece1 = belnd1[0].replace("%", "").trim();
	   String prece2 = belnd2[0].replace("%", "").trim();
	   List<BlendMaterial> listOfBlendMatrls = getBlendMaterialValues(belnd1[1].trim(), belnd2[1].trim(), prece1, prece2);
	   return listOfBlendMatrls;
   }
   public Product getProductColor(String colorValue,Product existingProduct){
	   List<Color> listOfColors = new ArrayList<>();
	   ProductConfigurations existingConfig = existingProduct.getProductConfigurations();
	   String oldDescription = existingProduct.getDescription();
	   Color colorObj = null;
	   String[] colors = null;
	   colorValue = colorValue.replaceAll(" and", ",");
	   if(colorValue.contains("Can be custom color but no guarantee on PMS Match.")){
		   //Factory Shades-Red, Black, Blue, Green, Gold, Silver. Can be custom color but no guarantee on PMS Match.
		    colorValue = "Red,Black,Green,Gold,Silver";
	   } else if(colorValue.contains("See image;")){
		   //shades of Black, Red, Orange, Blue, Purple and Green.
		   colorValue = "Black,Red,Orange,Blue,Purple,Green";
	   } else if(colorValue.contains("Custom; price includes 3 color design, any PMS.")){
		  
		   oldDescription = CommonUtility.appendStrings(oldDescription, colorValue, " ");
		   colorValue = "";
	   } else if(colorValue.contains(":")){
		   String[] clrs = CommonUtility.getValuesOfArray(colorValue, ApplicationConstants.CONST_STRING_COMMA_SEP);
		   colorValue = clrs[1];
	   } else if(colorValue.equalsIgnoreCase("Call for current colors and inventory") ||
			   colorValue.equalsIgnoreCase("Call for current colors in stock; ok to select a custom pantone at no charge.")){
		   oldDescription = CommonUtility.appendStrings(oldDescription, colorValue, " ");
		   colorValue = ""; 
	   } else if(colorValue.contains("(PU) Synthetic-Faux Leather-Black or Brown")){
		   //(PU) Synthetic-Faux Leather-Black or Brown; Stainless Steel
		   colorObj = new Color();
		   Combo comboObj = new Combo();
		   colorObj.setName("Black");
		   comboObj.setName("Brown");
		   comboObj.setType("secondary");
		   colorObj.setAlias("PU Synthetic-Faux Leather-Black or Brown");
		   List<Combo> listOfCombo = new ArrayList<>();
		   listOfCombo.add(comboObj);
		   colorObj.setCombos(listOfCombo);
		   listOfColors.add(colorObj);
		   colorValue = ""; 
	   } else if(colorValue.equalsIgnoreCase("No Colors Stock; Custom PMS Charge applies if under 3K units.")){
		   colorValue = ""; 
	   }
	   colors = CommonUtility.getValuesOfArray(colorValue, ApplicationConstants.CONST_STRING_COMMA_SEP);
	   if(!StringUtils.isEmpty(colors)){
		   for (String colorName : colors) {
			   if(!"".equals(colorName) && !" ".equals(colorName)){
				   colorName = colorName.trim();
				   colorObj = new Color();
				   String colorGroup = BamBamColorMapping.getColorGroup(colorName);
				   if(colorGroup == null){
					   colorGroup = "Other";
				   }
				   colorObj.setName(colorGroup);
				   colorObj.setAlias(colorName);
				   listOfColors.add(colorObj);
			   }  
		} 
	   }
	   existingProduct.setDescription(oldDescription);
	   existingConfig.setColors(listOfColors);
	   existingProduct.setProductConfigurations(existingConfig);
	   return existingProduct;
   }
   public ImprintColor getProductImprintColors(String imprColor){
	   List<ImprintColorValue> listOfImprintColorVals = new ArrayList<>();
	   ImprintColorValue imprColorValObj = null;
	   ImprintColor imprintColorObj = new ImprintColor();
	   imprColor = imprColor.replaceAll(" and",  ApplicationConstants.CONST_STRING_COMMA_SEP);
	   String[] imprColors = CommonUtility.getValuesOfArray(imprColor, ApplicationConstants.CONST_STRING_COMMA_SEP);
	   for (String color : imprColors) {
		   imprColorValObj = new ImprintColorValue();
		   imprColorValObj.setName(color.trim());
		   listOfImprintColorVals.add(imprColorValObj);
	}
	   imprintColorObj.setValues(listOfImprintColorVals);
	   imprintColorObj.setType(ApplicationConstants.CONST_STRING_IMPRNT_COLR);
	   return imprintColorObj;
   }
   
   public List<ImprintLocation> getProductImprintLocation(String imprLoc){
	   List<ImprintLocation> listOfImprintLoc = new ArrayList<>();
	   ImprintLocation imprintLocObj = null;
		imprLoc = imprLoc.replaceAll(ApplicationConstants.CONST_DELIMITER_SEMICOLON,
				ApplicationConstants.CONST_STRING_COMMA_SEP);
	   String[] locations = CommonUtility.getValuesOfArray(imprLoc, ApplicationConstants.CONST_STRING_COMMA_SEP);
	   for (String locationName : locations) {
		   imprintLocObj = new ImprintLocation();
		   imprintLocObj.setValue(locationName);
		   listOfImprintLoc.add(imprintLocObj);
	}
	   return listOfImprintLoc;
   }
   public List<FOBPoint> getFobPoints(String fobValue,String supplierNo,List<FOBPoint> existingFobPoint){
	   FOBPoint fobPointObj = null;
	   List<String> listOfLookupFobs = bamLookupData.getFobPoints(supplierNo);
	   if(!CollectionUtils.isEmpty(listOfLookupFobs)){
		   if(listOfLookupFobs.contains(fobValue)){
			   fobPointObj = new FOBPoint();;
			   fobPointObj.setName(fobValue);
			   existingFobPoint.add(fobPointObj);
		   }
	   }
	   return existingFobPoint;
	   
   }
   
   public List<ImprintMethod> getImprintMethods(String imprMethodVal, List<ImprintMethod> existingImprintMethods){
	   ImprintMethod imprintMethodObj = null;
	   if(StringUtils.isEmpty(imprMethodVal)){
		   imprintMethodObj = new ImprintMethod();
		   imprintMethodObj.setType("Printed");
		   imprintMethodObj.setAlias("Printed");
		   existingImprintMethods.add(imprintMethodObj);
	   } else {
		   if(imprMethodVal.equalsIgnoreCase("Embossed / debossed")){
			   imprMethodVal = imprMethodVal.replaceAll("/", "and");
		   }
		   String[] imprMethodVals = CommonUtility.getValuesOfArray(imprMethodVal, "and");
		   for (String imprintMethodName : imprMethodVals) {
			   imprintMethodObj = new ImprintMethod();
			   String type = "";
			   if(imprintMethodName.contains("No Imprint") || imprintMethodName.contains("N/A")){
				   type = "UNIMPRINTED";
				   imprintMethodName = "UNIMPRINTED";
			   } else {
				   type = getImprintMethodType(imprintMethodName);
			   }
			   imprintMethodObj.setType(type);
			   imprintMethodObj.setAlias(imprintMethodName);
			   existingImprintMethods.add(imprintMethodObj);
		  }
	   }   
	   return existingImprintMethods;
   }
   
   private String getImprintMethodType(String imprintMethodName){
	   String imprintMethodType;
	   if(lookupServiceData.isImprintMethod(imprintMethodName)){
		   imprintMethodType =   imprintMethodName;
	   } else {
		   if(imprintMethodName.contains("Transfer") || imprintMethodName.contains("transfer")){
			   imprintMethodType = "Heat Transfer";
		   } else if(imprintMethodName.contains("Printing") || imprintMethodName.contains("printing")){
			   imprintMethodType = "Printed";
		   } else if(imprintMethodName.contains("Pad") || imprintMethodName.contains("pad")){
			   imprintMethodType = "Pad Print";
		   } else if (imprintMethodName.contains("Four Color Process")){
			   imprintMethodType = "Full Color";
		   } else if(imprintMethodName.contains("Sublimated")){
			   imprintMethodType = "Sublimation";
		   } else if(imprintMethodName.contains("Printed")){
			   imprintMethodType = "Printed";
		   } else if(imprintMethodName.contains("Embroidery")){
			   imprintMethodType = "Embroidered";
		   } else if(imprintMethodName.contains("Etched")){
			   imprintMethodType = "Etched";
		   } else {
			   imprintMethodType = "Other";
		   }
	   }
	   return imprintMethodType;
   }
   
   public String getPriceInclude(String value){
	     String priceInclude = "";
	     if(value.equalsIgnoreCase("Price remains the same for 1 color on 1 side.")){
	    	 priceInclude   = "1 color on 1 side.";
	     } else if(value.equalsIgnoreCase("Includes CMYK set up in unit price.")){
	    	 priceInclude  = "CMYK set up";
	     } else if(value.equalsIgnoreCase("Set up included in unit price when using 1-4 STOCK Yarn Colors.")){
	    	 priceInclude  = "Setup when using 1-4 STOCK Yarn Colors";
	     } else if(value.equalsIgnoreCase("Set up included in unit price when using 1-6 STOCK Yarn Colors.")){
	    	 priceInclude  = "Setup when using 1-6 STOCK Yarn Colors";
	     } else if(value.equalsIgnoreCase("Set up included in unit price when using 1-4 stock yarn colors.")){
	    	 priceInclude  = "Setup when using 1-4 STOCK Yarn Colors";
	     } else if(value.equalsIgnoreCase("Set up included in unit price when using 1-6 STOCK YARN COLORS. Present your pantone colors and we will see what is available as a stock yarn closest to the colors specified.")){
	    	 priceInclude  = "Setup when using 1-6 STOCK Yarn Colors";
	     } else if(value.equalsIgnoreCase("Price published is 1 Color Logo repeated.")){
	    	 priceInclude  = "Price published is 1 Color Logo repeated.";
	     } else if(value.equalsIgnoreCase("1 color/1 side included in the unit price. Additional colors/location, charge applies.")){
	    	 priceInclude = "1 color on 1 side.";
	     } else {
	    	 
	     }
	   return priceInclude;
   }
   public List<String> getProductKeywords(String keyValue){
	   List<String> listOfKeywords = new ArrayList<>();
	   String[] keys = CommonUtility.getValuesOfArray(keyValue, ",");
	   for (String keyVal : keys) {// avoid duplicate keyword
		   keyVal = keyVal.trim();
		     if(!keyVal.equals("noisemakers")){
		    	 listOfKeywords.add(keyVal);
		     }
	}
	   return listOfKeywords;
   }
	public LookupServiceData getLookupServiceData() {
		return lookupServiceData;
	}
	public void setLookupServiceData(LookupServiceData lookupServiceData) {
		this.lookupServiceData = lookupServiceData;
	}
	 public BamLookupData getBamLookupData() {
			return bamLookupData;
		}
		public void setBamLookupData(BamLookupData bamLookupData) {
			this.bamLookupData = bamLookupData;
		}
	
}
