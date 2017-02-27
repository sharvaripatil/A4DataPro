package parser.goldbond;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.AdditionalLocation;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class GoldbondAttributeParser {
	
	String pattern_remove_specialSymbols = "[^0-9.x/ ]";
	private GoldbondPriceGridParser gbPriceGridParser;
	
	public Product keepExistingProductData(Product existingProduct){
		//Please keep the Categories and Themes for existing products.
		ProductConfigurations newConfiguration = new ProductConfigurations();
		ProductConfigurations existingConfiguration = existingProduct.getProductConfigurations();
		List<Theme> existingThemes = existingConfiguration.getThemes();
		if(!CollectionUtils.isEmpty(existingThemes)){
			newConfiguration.setThemes(existingThemes);
		}
		existingProduct.setProductConfigurations(newConfiguration);
		existingProduct.setAvailability(new ArrayList<>());
		existingProduct.setPriceGrids(new ArrayList<>());
		existingProduct.setImages(new ArrayList<>());
		existingProduct.setProductRelationSkus(new ArrayList<>());
		return existingProduct;
	}
	
	public Product getAdditionalColor(Product existingProduct,String value){
		   String priceVal = "";
		   String discountCode = "";
		   List<PriceGrid> existingPriceGrid = existingProduct.getPriceGrids();
		   ProductConfigurations existingConfiguration = existingProduct.getProductConfigurations();
		    if(value.equalsIgnoreCase("$50.00 (G) per color, applies to repeat orders")){
		    	priceVal = "50";
		    	discountCode = "G";
		    } else if(value.equalsIgnoreCase("$50.00 (G) per color")){
		    	priceVal = "50";
		    	discountCode = "G";
		    } else if(value.equalsIgnoreCase("$15.00 (A) per color")){
		    	priceVal = "15";
		    	discountCode = "A";
		    } else if(value.equalsIgnoreCase("$50.00 (G) per color, 2-color only (CANNOT BE CLOSE REGISTRATION)")){
		    	priceVal = "50";
		    	discountCode = "G";
		    } else if(value.equalsIgnoreCase("$50.00 (G) per color (multi-color imprint not available for two side imprint)")){
		    	priceVal = "50";
		    	discountCode = "G";
		    } else if(value.equalsIgnoreCase("$50.00 (G) per color (up to 2 colors), applies to repeat orders")){
		    	priceVal = "50";
		    	discountCode = "G";
		    } else {
		    	
		    }
		    List<AdditionalColor> listOfAdditionalColor = getAdditionalColors("additional color available");
		    existingConfiguration.setAdditionalColors(listOfAdditionalColor);
		    existingProduct.setProductConfigurations(existingConfiguration);
		return existingProduct;
	}
	public List<Color> getProductColors(String colors){
		  String[] allColors = colors.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		  List<Color> listOfColors = new ArrayList<>();
		  Color colorObj = null;
		  for (String colorName : allColors) {
			  colorObj = new Color();
			     String alias = colorName;
			     colorName = colorName.replaceAll("\\(.*?\\)", "").trim();
			     String colorGroup = GoldbondColorMapping.getColorGroup(colorName);
			     if(colorGroup == null){
			    	 colorGroup = "Other";
			     }
			  if(colorGroup.contains("Combo")){
				  colorObj = getColorCombo(colorName, alias, ":");
			  } else{
				  colorObj.setName(colorGroup);
				  colorObj.setAlias(alias);
			  }
			  listOfColors.add(colorObj);
		}
		return listOfColors;
	}
	public Size getProductSize(String sizeValues){
		Size sizeObj = new Size();
		Dimension dimentionObj = new Dimension();
		Values valuesObj = null;
		List<Values> listOfValues = new ArrayList<>();
		if(sizeValues.contains(";")){
			sizeValues = sizeValues.replaceAll(ApplicationConstants.CONST_DELIMITER_SEMICOLON, 
					                                     ApplicationConstants.CONST_STRING_COMMA_SEP);
		}
		String[] sizess = CommonUtility.getValuesOfArray(sizeValues, ApplicationConstants.CONST_STRING_COMMA_SEP);
		for (String sizeVal : sizess) {
			valuesObj = new Values();
			if(sizeVal.equalsIgnoreCase("Official size") || sizeVal.equalsIgnoreCase("Varies") ||
					sizeVal.equalsIgnoreCase("One size fits most") || sizeVal.equalsIgnoreCase("NFL Official Size")){
				continue;
			} else if(sizeVal.equalsIgnoreCase("29.5 Inches")){
				valuesObj = getOverAllSizeValObj("29.5", "Length", "", "");
			} else if(sizeVal.equalsIgnoreCase("7- 3/4 or larger size heads")){
				valuesObj = getOverAllSizeValObj("7-3/4", "Length", "", "");
			} else if (sizeVal.contains("H") && sizeVal.contains("L") && sizeVal.contains("D")) {
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				valuesObj = getOverAllSizeValObj(sizeVal, "Height", "Length", "Depth");
			} else if (sizeVal.contains("H") && sizeVal.contains("W") && sizeVal.contains("D")) {
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				valuesObj = getOverAllSizeValObj(sizeVal, "Height", "Width", "Depth");
			} else if (sizeVal.contains("L") && sizeVal.contains("W") && sizeVal.contains("D")) {
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				valuesObj = getOverAllSizeValObj(sizeVal, "Length", "Width", "Depth");
			} else if (sizeVal.contains("H") && sizeVal.contains("W")) {
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				valuesObj = getOverAllSizeValObj(sizeVal, "Height", "Width", "");
			} else if (sizeVal.contains("arc")) {
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				valuesObj = getOverAllSizeValObj(sizeVal, "Arc", "", "");
			} else if (sizeVal.contains("Dia") || sizeVal.contains("dia")) {
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				valuesObj = getOverAllSizeValObj(sizeVal, "Dia", "", "");
			} else if (sizeVal.contains("H")) {
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				valuesObj = getOverAllSizeValObj(sizeVal, "Height", "", "");
			} else if (sizeVal.contains("L")) {
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				valuesObj = getOverAllSizeValObj(sizeVal, "Length", "", "");
			} else {
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				valuesObj = getOverAllSizeValObj(sizeVal, "Length", "Width", "Height");
			}
			listOfValues.add(valuesObj);
		}
		dimentionObj.setValues(listOfValues);
		sizeObj.setDimension(dimentionObj);
		return sizeObj;
	}
	
	public List<ImprintSize> getProductImprintSize(String imprSizeVal){
		String imprintsize_remove_specialSymbols = "[^a-zA-Z0-9.x/: ]";
		List<ImprintSize> listOfImprintSizes = new ArrayList<>();
		ImprintSize imprintSizeObj = null;
		if(imprSizeVal.contains(";")){
			imprSizeVal = imprSizeVal.replaceAll(";", ",");
		} else if(imprSizeVal.contains("<br>")){
			imprSizeVal = imprSizeVal.replaceAll("<br>", ",");
		}
		
		String[] values = imprSizeVal.split(",");
		for (String sizeVal : values) {
			imprintSizeObj = new ImprintSize();
			sizeVal = sizeVal.replaceAll(imprintsize_remove_specialSymbols, "");
			if(sizeVal.contains("<")){
				sizeVal = sizeVal.replaceAll("\\<.*?\\>", "").trim();
			}
			imprintSizeObj.setValue(sizeVal);
			listOfImprintSizes.add(imprintSizeObj);
		}
		return listOfImprintSizes;
	}
	
	public Product getAdditonalLocaAndUpCharge(String value,Product existingProduct){
		ProductConfigurations configuration = existingProduct.getProductConfigurations();
		List<AdditionalLocation> locationList = getAdditionalLocation("Reverse Side Imprint");
		if(value.equalsIgnoreCase("$0.30 (G) ea.")){
			
		} else if(value.contains("$50.00 (G) plus $0.30 (G)")){
			
		} else if(value.equalsIgnoreCase("$0.30 (G) per cube location, ea.")){
			
		} else if(value.equalsIgnoreCase("$50.00 (G), applies to repeat orders")){
			
		} else if(value.equalsIgnoreCase("$50.00 (G) per logo, plus $0.80 (G) run charge ea. on two back (black) panels")){
			
		}else if(value.equalsIgnoreCase("$50.00 (G) per location plus add $0.30 (G) ea./location")){
				
		} else {
			
		}
		
		
		configuration.setAdditionalLocations(locationList);
		return existingProduct;
	}
	private List<AdditionalLocation> getAdditionalLocation(String locationVal){
		List<AdditionalLocation> locationList = new ArrayList<>();
		AdditionalLocation additionalLocaObj = new AdditionalLocation();
		additionalLocaObj.setName(locationVal);
		locationList.add(additionalLocaObj);
		return locationList;
	}
	private Value getValueObj(String value,String attribute,String unit){
		Value valueObj = new Value();
		valueObj.setAttribute(attribute);
		valueObj.setUnit(unit);
		valueObj.setValue(value);
		return valueObj;
	}
	private Values getOverAllSizeValObj(String val,String unit1,String unit2,String unit3){
		//Overall Size: 23.5" x 23.5"
		String[] values = val.split("x");
		Value valObj1 = null;
		Value valObj2 = null;
		Value valObj3 = null;
		List<Value> listOfValue = new ArrayList<>();
		if(values.length == ApplicationConstants.CONST_INT_VALUE_ONE){
			 valObj1 = getValueObj(values[0].trim(), "Length", unit1);
			  listOfValue.add(valObj1);
		} else if(values.length == ApplicationConstants.CONST_INT_VALUE_TWO){
			 valObj1 = getValueObj(values[0].trim(), "Length", unit1);
			 valObj2 = getValueObj(values[1].trim(), "Width", unit2);
			 listOfValue.add(valObj1);
		     listOfValue.add(valObj2);
		} else if(values.length == ApplicationConstants.CONST_INT_VALUE_THREE){
			 valObj1 = getValueObj(values[0].trim(), "Length", unit1);
			 valObj2 = getValueObj(values[1].trim(), "Width", unit2);
			 valObj3 = getValueObj(values[2].trim(), "Height", unit3);
			 listOfValue.add(valObj1);
		     listOfValue.add(valObj2);
		     listOfValue.add(valObj3);
		}
		 Values valuesObj = new Values(); 
		 valuesObj.setValue(listOfValue);
		 return valuesObj;
	}
	
	private List<AdditionalColor> getAdditionalColors(String value){
		    List<AdditionalColor> listOfAdditionalColors = new ArrayList<>();
		    AdditionalColor additionalColorObj = new AdditionalColor();
		    additionalColorObj.setName(value);
		    listOfAdditionalColors.add(additionalColorObj);
		    return listOfAdditionalColors;
	}
	private Color getColorCombo(String comboValue,String alias,String separator){
		  //Medium White:Combo:Medium Pink
		Color colorObj = new Color();
		List<Combo> listOfCombos = new ArrayList<>();
		Combo comboObj = new Combo();
		String[] combos = comboValue.split(separator);
		comboObj.setName(combos[2]);
		comboObj.setType("secondary");
		listOfCombos.add(comboObj);
		colorObj.setName(combos[0]);
		colorObj.setAlias(alias);
		colorObj.setCombos(listOfCombos);
		  return colorObj;
	}
	public GoldbondPriceGridParser getGbPriceGridParser() {
		return gbPriceGridParser;
	}

	public void setGbPriceGridParser(GoldbondPriceGridParser gbPriceGridParser) {
		this.gbPriceGridParser = gbPriceGridParser;
	}

	

}
