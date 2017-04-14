package parser.tomaxusa;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.Shape;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.TradeName;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class TomaxUsaAttributeParser {
	
	private static final Logger _LOGGER = Logger.getLogger(TomaxUsaAttributeParser.class);
	
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
		return shippingEstObj;
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
	
	public List<ImprintSize> getImprintSize(String imprintMethValue){
		List<ImprintSize> listOfImprintSize = null;
		listOfImprintSize = new ArrayList<ImprintSize>();
		ImprintSize imprSizeObj = new ImprintSize();
		imprSizeObj.setValue(imprintMethValue);
		listOfImprintSize.add(imprSizeObj);
		return listOfImprintSize;
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
		//keywords
		List<String> productKeywords=existingProduct.getProductKeywords();
		if(!CollectionUtils.isEmpty(productKeywords)){
			newProduct.setProductKeywords(productKeywords);
		}
		//product colors
		List<Color> colors =existingProductConfig.getColors();
		if(!CollectionUtils.isEmpty(colors)){
			newProductConfigurations.setColors(colors);
		}
		//shape
		List<Shape> shapes =existingProductConfig.getShapes();
		if(!CollectionUtils.isEmpty(productKeywords)){
			newProductConfigurations.setShapes(shapes);
		}
		//material
		List<Material>   materials=existingProductConfig.getMaterials();
		if(!CollectionUtils.isEmpty(productKeywords)){
			newProductConfigurations.setMaterials(materials);
		}
		newProduct.setProductConfigurations(newProductConfigurations);
		}catch(Exception e){
			_LOGGER.error("Error while processing Existing Product Data " +e.getMessage());
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
			if(StringUtils.isEmpty(colorName)){
				continue;
			}
			colorName=colorName.replaceAll("&","/");
			colorName=colorName.replaceAll(" w/","/");
			colorName=colorName.replaceAll(" W/","/");
			//colorName = colorName.trim();
			
			colorObj = new Color();
			String colorGroup = TomaxConstants.getColorGroup(colorName.trim());
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
						String colorFirstName = TomaxConstants.getColorGroup(comboColors[0].trim());
						colorObj.setName(colorFirstName == null?"Other":colorFirstName);
						int combosSize = comboColors.length;
						if (combosSize == ApplicationConstants.CONST_INT_VALUE_TWO) {
							String colorComboFirstName = TomaxConstants.getColorGroup(comboColors[1].trim());
							colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
							listOfCombo = getColorsCombo(colorComboFirstName, ApplicationConstants.CONST_STRING_EMPTY,
									combosSize);
						} else{
							String colorComboFirstName = TomaxConstants.getColorGroup(comboColors[1].trim());
							colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
							
							String colorComboSecondName = TomaxConstants.getColorGroup(comboColors[2].trim());
							colorComboSecondName = colorComboSecondName == null?"Other":colorComboSecondName;
							listOfCombo = getColorsCombo(colorComboFirstName,colorComboSecondName, combosSize);
						}
						String alias = colorGroup.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH, "-");
						colorObj.setAlias(alias);
						colorObj.setCombos(listOfCombo);
					} else {
						String[] comboColors = CommonUtility.getValuesOfArray(colorGroup,
								ApplicationConstants.CONST_DELIMITER_FSLASH);
						String mainColorGroup = TomaxConstants.getColorGroup(comboColors[0].trim());
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
    		 mainColor = TomaxConstants.getColorGroup(colorVals[0].trim());
    		 secondaryColor = TomaxConstants.getColorGroup(colorVals[1].trim());
    		 if(mainColor != null && secondaryColor != null){
    			 return true;
    		 }
    	} else if(colorVals.length == ApplicationConstants.CONST_INT_VALUE_THREE){
    		 mainColor      = TomaxConstants.getColorGroup(colorVals[0].trim());
    		 secondaryColor = TomaxConstants.getColorGroup(colorVals[1].trim());
    		 thirdColor     = TomaxConstants.getColorGroup(colorVals[2].trim());
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
					String colorName=TomaxConstants.TCOLOR_MAP.get(strColor.trim());
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
					String combo_color_1=TomaxConstants.TCOLOR_MAP.get(colorArray[0].trim());
					//String combo_color_1=HCLCOLOR_MAP.get(colorArray[0].trim());
					if(StringUtils.isEmpty(combo_color_1)){
						combo_color_1=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
					}
					colorObj.setName(combo_color_1);
					colorObj.setAlias(strColor);
					
					Combo comboObj = new Combo();
					String combo_color_2=TomaxConstants.TCOLOR_MAP.get(colorArray[1].trim());
					//String combo_color_2=HCLCOLOR_MAP.get(colorArray[1].trim());
					if(StringUtils.isEmpty(combo_color_2)){
						combo_color_2=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
					}
					comboObj.setName(combo_color_2.trim());
					comboObj.setType(ApplicationConstants.CONST_STRING_SECONDARY);
					if(colorArray.length==3){
						String combo_color_3=TomaxConstants.TCOLOR_MAP.get(colorArray[2].trim());
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

	private boolean isComboColors1(String value) {
	boolean result = false;
	if (value.contains("/")) {
		result = true;
	}
	return result;
	}
	
	public Size getSizes(String sizeValue) {
		Size sizeObj = new Size();
		String sizeGroup="dimension";
		String unitValue="in";
		String attriValueLen="Length";
		//1.75 DIA x 4.5
		try{
			String DimenArr[] = {sizeValue} ;
			 if(sizeValue.contains("x")){
				 DimenArr=sizeValue.split("x");
				 sizeGroup="dimension";
			 }
			
			if (sizeGroup.equals("dimension")) {
			Dimension dimensionObj = new Dimension();
			List<Values> valuesList = new ArrayList<Values>();
			List<Value> valuelist;
			Values valuesObj = null;
				Value valObj;
				valuesObj = new Values();
				valuelist = new ArrayList<Value>(); 
				int count=1;
				for (String value1 : DimenArr) {
					valObj = new Value();
					if(value1.contains("DIA")){
						attriValueLen="DIA";
						value1=value1.replace("DIA", "").trim();
					}
					if(value1.contains("shaker")){
						value1=value1.replace("(shaker)", "").trim();
					}
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
			valuesList.add(valuesObj);
			dimensionObj.setValues(valuesList);
			sizeObj.setDimension(dimensionObj);
		}
		}
		catch(Exception e)
		{
			_LOGGER.error("Error while processing Size :"+e.getMessage());
			return null;
		}
		return sizeObj;
	}
	
	 
	
}
