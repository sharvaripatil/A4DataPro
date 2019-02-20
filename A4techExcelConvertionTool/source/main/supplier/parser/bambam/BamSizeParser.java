/**
 * 
 */
package parser.bambam;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.OtherSize;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

/**
 * @author Venkat
 *
 */
public class BamSizeParser {
  
	public Product getProductSizes(String sizeVals,Product existingProduct){
		Size sizeObj = new Size();
		Dimension dimentionObj = null;
		Values valuesObj = null;
		Apparel apparelObj = null;
		OtherSize otherSize = null;
		List<Values> listOfValues = new ArrayList<>();
		sizeVals = sizeVals.replaceAll(";", ",");
		String pattern_remove_specialSymbols = "[^0-9.x/ ]";
		ProductConfigurations existConfig = existingProduct.getProductConfigurations();
		String[] prdSizeValues = CommonUtility.getValuesOfArray(sizeVals, ApplicationConstants.CONST_DELIMITER_COMMA);
		if(sizeVals.contains("fits All")){
			apparelObj = new Apparel();
			List<Value> listOfVal = getApparelAndOtherValuesObj(sizeVals);
			apparelObj.setType("Standard & Numbered");
			apparelObj.setValues(listOfVal);
			sizeObj.setApparel(apparelObj);
		} else if (sizeVals.contains("circumference - Size") || sizeVals.contains("in circumference")
				|| sizeVals.contains("Corner to Corner")) {//Other
			otherSize = new OtherSize();
			List<Value> listOfVal = null;
			if(sizeVals.contains("Size 2")){
				listOfVal = getApparelAndOtherValuesObj("20-22 circumference");
			} else if(sizeVals.contains("in circumference")){
				listOfVal = getApparelAndOtherValuesObj("27-28 circumference");
			} else {
				listOfVal = getApparelAndOtherValuesObj(sizeVals.trim());
			}
			otherSize.setValues(listOfVal);
			sizeObj.setOther(otherSize);
		} else {
			dimentionObj = new Dimension();
			for (String sizeVal : prdSizeValues) {
				valuesObj = new Values();
				if(sizeVal.contains("banner") || sizeVal.contains("Banner")){
					valuesObj = getBannerSizes(sizeVal);
				} else if(sizeVal.contains("diameter") || sizeVal.contains("Diameter")){
					valuesObj = getDiameterSizes(sizeVal);
				} else if(sizeVal.contains("Overall Size:")){
					// Overall Size: 23.5" x 23.5"
					//sizeVal = sizeVal.replaceAll("\"", "").trim();
					//sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
					valuesObj = getOverAllSizeValObj("23.5 x 23.5","in");
				} else if(sizeVal.contains("flag") || sizeVal.contains("Flag")){
					sizeVal = sizeVal.replaceAll("\"", "").trim();
					String[] flagValues = null;
					if(sizeVal.contains("\n")){
						 flagValues = sizeVal.split("\n");
					} else{///
						 flagValues = sizeVal.split("/");
					}
					for (String flagValue : flagValues) {
						  if(flagValue.contains("flag") || flagValue.contains("Flag")){
							  valuesObj = getFlagSizeValObj(flagValue);
						  } else {
							  String existDesc = existingProduct.getDescription();
							  existDesc = CommonUtility.appendStrings(existDesc, flagValue, " ");
							  existingProduct.setDescription(existDesc);
						  }
					}	
				} else if(sizeVal.contains("Approximate Size")){
					//Approximate Size: 29.5" x 24" 
					sizeVal = sizeVal.replaceAll("\"", "").trim();
					sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
					valuesObj = getApproximateSizeValObj(sizeVal);
				} else if(sizeVal.contains("Medium") || sizeVal.contains("Large") || sizeVal.contains("(large)")){
					if(sizeVal.contains("Circumferences")){
						sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
						valuesObj = getOverAllSizeValObj("7.6","cm");
					} else{
						sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
						valuesObj = getOverAllSizeValObj(sizeVal,"cm");
					}
				} else if(sizeVal.contains("Lobster Claw")){
					sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
					valuesObj = getOverAllSizeValObj(sizeVal,"in");
				} else if(sizeVal.contains(" or")){
					sizeVal = sizeVal.replaceAll("\"", "").trim();
					//sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
					String[] sizeValss = sizeVal.split(" or");
					String orSize = sizeValss[0];
					orSize = orSize.replaceAll(pattern_remove_specialSymbols, "");
					valuesObj = getOverAllSizeValObj(orSize,"in");
				} else if(sizeVal.contains("fringe")){
					sizeVal = sizeVal.replaceAll("\"", "").trim();
					sizeVal = sizeVal.replaceAll("\\+", "x");
					sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
					valuesObj = getOverAllSizeValObj(sizeVal,"in");
				} else if(sizeVal.contains("gussets")){
					//15" x 16" with 6" gussets
					sizeVal = sizeVal.replaceAll("with", "x");
					sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
					valuesObj = getOverAllSizeValObj(sizeVal,"in");
				} else if(sizeVal.contains("thick")){
					String unit = "in";
					 if(sizeVal.equalsIgnoreCase("Length: 9cm x Thickness: 1cm")){
						 unit = "cm";
						 sizeVal = sizeVal.replaceAll("\\(.*\\)", "");
						 sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
						 valuesObj = getThicknessSizeValObj(sizeVal,unit);
					 }else if(sizeVal.equalsIgnoreCase("48.8 h x 32 widest point x 5.2mm thickness")){
						 unit = "mm";
						 sizeVal = sizeVal.replaceAll("\\(.*\\)", "");
						 sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
						 valuesObj = getThicknessSizeValObj(sizeVal,unit);
					 } else{
						 if(sizeVal.contains("mm")){
							 unit = "mm";
						 } else if(sizeVal.contains("cm")){
							 unit = "cm";
						 }
						 sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
						 valuesObj = getOverAllSizeValObj(sizeVal,unit);
					 }
				} else if(sizeVal.equalsIgnoreCase("Varies based on style")){ // set description
					String exidec  = existingProduct.getDescription();
					exidec = CommonUtility.appendStrings(exidec, sizeVal, " ");
					existingProduct.setDescription(exidec);
				} else if(sizeVal.contains("youth")){
					String[] sizes = sizeVal.split("/");
					String adultSize = sizes[0];
					adultSize = adultSize.replaceAll("\\(.*\\)", "");
					adultSize = adultSize.replaceAll(pattern_remove_specialSymbols, "");
					valuesObj = getOverAllSizeValObj(adultSize,"in");
				} else if(sizeVal.contains("Reel")){
					String[] sizes = sizeVal.split("/");
					String reelSize = sizes[0];
					reelSize = reelSize.replaceAll(pattern_remove_specialSymbols, "");
					valuesObj = getOverAllSizeValObj(reelSize.trim(),"in");
				} else if(sizeVal.equalsIgnoreCase("19.5\" x 27.5\" with 30\" pole") ||
						sizeVal.equalsIgnoreCase("14.5\" x 21.5\" with 21.5\" pole")){
					if(sizeVal.contains("19.5")){
						valuesObj = getOverAllSizeValObj("19.5x27.5","in");
					} else if(sizeVal.contains("14.5")){
						valuesObj = getOverAllSizeValObj("14.5x21.5","in");
					}
					
				} else if(sizeVal.equalsIgnoreCase("2.75\" - 4.3\" wide")){
					valuesObj = getOverAllSizeValObj("4.3","in");
				} else if(sizeVal.equalsIgnoreCase("17\" (deflated)  Giftcard: folded 7\" x 5\"")){
					valuesObj = getOverAllSizeValObj("7x5","in");
				} else if(sizeVal.equalsIgnoreCase("2.5\" x 3.5\" (5 mm thick)") ||
					sizeVal.equalsIgnoreCase("3\" x 4.3\" (5 mm thick)")){
					if(sizeVal.contains("2.5")){
						valuesObj = getOverAllSizeValObj("2.5x3.5","mm");
					} else {
						valuesObj = getOverAllSizeValObj("3x4.3","mm");
					}
					
				}
				else{
					if(sizeVal.contains("(")){
						sizeVal = sizeVal.replaceAll("\\(.*\\)", "");
					}
					sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
					valuesObj = getOverAllSizeValObj(sizeVal.trim(),"in");
				}
				listOfValues.add(valuesObj);
			}
			dimentionObj.setValues(listOfValues);
			sizeObj.setDimension(dimentionObj);
		}
	    existConfig.setSizes(sizeObj);
	    existingProduct.setProductConfigurations(existConfig);
	    return existingProduct;
	}
	private Values getBannerSizes(String sizeVal){
		List<Value> listOfValue = new ArrayList<>();
		Value valObj1 = null;
		Value valObj2 = null;
		Values valuesObj = new Values();
		if(sizeVal.contains("8"+"(banner)")){//23.5" x 4.25" (handles) / 23.5" x 8" (banner)
			valObj1 = getValueObj("23.5", "Length", "in");
			valObj2 = getValueObj("8", "Width", "in");
			listOfValue.add(valObj1);
			listOfValue.add(valObj2);
			valuesObj.setValue(listOfValue);
		} else if(sizeVal.contains("Barrel -") || sizeVal.contains("banner:") ||
				sizeVal.contains("2.75"+"(banner)") || sizeVal.contains("retractable banner")){//Barrel - 4" x 0.5" / Banner - 7.5" x 2.75"
			valObj1 = getValueObj("7.5", "Length", "in");                      // //banner: 7.5" x 2.75"
			valObj2 = getValueObj("2.75", "Width", "in");                     //6"x 0.5" (pen) / 7.5" x 2.75" (banner)
			listOfValue.add(valObj1);										// //7.5" x 2.75" retractable banner
			listOfValue.add(valObj2);
			valuesObj.setValue(listOfValue);
		} else{
			
		}
		return valuesObj;
	}
	
	private Values getDiameterSizes(String sizeVal){
		 Values valuesObj = new Values();
		 List<Value> listOfValue = new ArrayList<>();
		 Value valObj1 = null;
		 Value valObj2 = null;
		 if(sizeVal.equalsIgnoreCase("13.5"+"x 2" +"diameter (small end) x 6"+"diameter (large end)")){
			 valObj1 = getValueObj("13.5", "Length", "in");
			 valObj2 = getValueObj("6", "Dia", "in");
			 listOfValue.add(valObj1);
			 listOfValue.add(valObj2);
		 } else if(sizeVal.contains("diameter") || sizeVal.contains("Diameter")){
			 String[] sizeDiameters = null;
			 if(sizeVal.contains("diameter")){
				 sizeDiameters = CommonUtility.getValuesOfArray(sizeVal, "diameter");
			 } else if(sizeVal.contains("Diameter")){
				 sizeDiameters = CommonUtility.getValuesOfArray(sizeVal, "Diameter");
			 }
			 String sizeValue = sizeDiameters[0];
			 if(sizeValue.contains("mm")){
				 sizeValue = sizeValue.replace("mm", "");
			 }
			 sizeValue = sizeValue.replaceAll("\"", "").trim();
			 sizeVal = sizeVal.replaceAll("[^0-9.x ]", "");
			 valObj1 = getValueObj(sizeValue, "Dia", "in");
			 listOfValue.add(valObj1);
		 }
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
	private Values getOverAllSizeValObj(String val,String unit){
		//Overall Size: 23.5" x 23.5"
		String[] values = val.split("x");
		Value valObj1 = null;
		Value valObj2 = null;
		Value valObj3 = null;
		List<Value> listOfValue = new ArrayList<>();
		if(values.length == ApplicationConstants.CONST_INT_VALUE_ONE){
			 valObj1 = getValueObj(values[0].trim(), "Length", unit);
			  listOfValue.add(valObj1);
		} else if(values.length == ApplicationConstants.CONST_INT_VALUE_TWO){
			 valObj1 = getValueObj(values[0].trim(), "Length", unit);
			 valObj2 = getValueObj(values[1].trim(), "Width", unit);
			 listOfValue.add(valObj1);
		     listOfValue.add(valObj2);
		} else if(values.length == ApplicationConstants.CONST_INT_VALUE_THREE){
			 valObj1 = getValueObj(values[0].trim(), "Length", unit);
			 valObj2 = getValueObj(values[1].trim(), "Width", unit);
			 valObj3 = getValueObj(values[2].trim(), "Height", unit);
			 listOfValue.add(valObj1);
		     listOfValue.add(valObj2);
		     listOfValue.add(valObj3);
		}
		 Values valuesObj = new Values(); 
		 valuesObj.setValue(listOfValue);
		 return valuesObj;
	}
	private Values getFlagSizeValObj(String data){
		//12" x 16" (flag),Flag: 11" x 8"
		data = data.replaceAll("[^0-9.x ]", "");
		String[] values = data.split("x");
		Value valObj1 = getValueObj(values[0].trim(), "Length", "in");
		 Value valObj2 = getValueObj(values[1].trim(), "Width", "in");
		 Values valuesObj = new Values();
		 List<Value> listOfValue = new ArrayList<>();
		 listOfValue.add(valObj1);
		 listOfValue.add(valObj2);
		 valuesObj.setValue(listOfValue);
		 return valuesObj;
	}
	private Values getApproximateSizeValObj(String data){
		//12" x 16" (flag),Flag: 11" x 8"
		data = data.replaceAll("[^0-9.x ]", "");
		String[] values = data.split("x");
		Value valObj1 = getValueObj(values[1].trim(), "Length", "in");
		 Value valObj2 = getValueObj(values[2].trim(), "Width", "in");
		 Values valuesObj = new Values();
		 List<Value> listOfValue = new ArrayList<>();
		 listOfValue.add(valObj1);
		 listOfValue.add(valObj2);
		 valuesObj.setValue(listOfValue);
		 return valuesObj;
	}
	private List<Value> getApparelAndOtherValuesObj(String val){
		 List<Value> listOfValue = new ArrayList<>();
		 Value valObj = new Value();
		 valObj.setValue(val);
		 listOfValue.add(valObj);
		 return listOfValue;
	}
	private Values getThicknessSizeValObj(String val,String unit){
		//Overall Size: 23.5" x 23.5"
		val = val.replaceAll("[^0-9.x ]", "");
		String[] values = val.split("x");
		Value valObj1 = null;
		Value valObj2 = null;
		Value valObj3 = null;
		List<Value> listOfValue = new ArrayList<>();
		if(values.length == ApplicationConstants.CONST_INT_VALUE_ONE){
			 valObj1 = getValueObj(values[0].trim(), "Length", unit);
			  listOfValue.add(valObj1);
		} else if(values.length == ApplicationConstants.CONST_INT_VALUE_TWO){
			 valObj1 = getValueObj(values[0].trim(), "Length", unit);
			 valObj2 = getValueObj(values[1].trim(), "Thickness", unit);
			 listOfValue.add(valObj1);
		     listOfValue.add(valObj2);
		} else if(values.length == ApplicationConstants.CONST_INT_VALUE_THREE){
			 valObj1 = getValueObj(values[0].trim(), "Length", unit);
			 valObj2 = getValueObj(values[1].trim(), "Width", unit);
			 valObj3 = getValueObj(values[2].trim(), "Thickness", unit);
			 listOfValue.add(valObj1);
		     listOfValue.add(valObj2);
		     listOfValue.add(valObj3);
		}
		 Values valuesObj = new Values(); 
		 valuesObj.setValue(listOfValue);
		 return valuesObj;
	}
	
}
