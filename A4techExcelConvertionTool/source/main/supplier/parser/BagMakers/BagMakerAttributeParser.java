package parser.BagMakers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Configurations;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.a4tech.v2.core.model.Image;

public class BagMakerAttributeParser {

	private static final Logger _LOGGER = Logger.getLogger(BagMakerAttributeParser.class);
	private LookupServiceData objLookUpService;
	
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

	public static String[] getValuesOfArray(String data,String delimiter){
	   if(!StringUtils.isEmpty(data)){
		   return data.split(delimiter);
	   }
	   return null;
   }

}
