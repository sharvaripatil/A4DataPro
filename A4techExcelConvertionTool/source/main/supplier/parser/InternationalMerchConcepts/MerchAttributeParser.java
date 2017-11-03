package parser.InternationalMerchConcepts;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.a4tech.util.LookupData;

public class MerchAttributeParser {
	public Product keepExistingProductData(Product existingProduct){
		//Please keep the Categories,Images and Themes for existing products.
		Product newProduct = new Product();
		ProductConfigurations newConfiguration = new ProductConfigurations();
		if(!CollectionUtils.isEmpty(existingProduct.getCategories())){
		  newProduct.setCategories(existingProduct.getCategories());
		}
		if(!StringUtils.isEmpty(existingProduct.getSummary())){
			newProduct.setSummary(existingProduct.getSummary());
		}
		if(!CollectionUtils.isEmpty(existingProduct.getFobPoints())){
			  newProduct.setFobPoints(existingProduct.getFobPoints());
		}
		if(!CollectionUtils.isEmpty(existingProduct.getCatalogs())){
			  newProduct.setCatalogs(existingProduct.getCatalogs());
		}
		newProduct.setProductConfigurations(newConfiguration);
		return newProduct;
	}
	
   public Size getProductSize(String val){
	   Size sizeObj = new Size();
	   Values valuesObj = null;
	   Dimension dimentionObj = new Dimension();
	   List<Values> listOfValues = new ArrayList<>();
	   val = val.replaceAll(";", ",");
	   String[] vals = null;
	   if(val.contains("Closed:")){
		   vals = new String[]{val};
	   } else {
		   vals = CommonUtility.getValuesOfArray(val, ",");
	   }
		for (String sizeVal : vals) {
			valuesObj = new Values();
			if (sizeVal.contains("CUBE") || sizeVal.contains("Cube") || sizeVal.contains("cube")) {
				String value = sizeVal.replaceAll("[^0-9/ ]", "");
				value = value + "x" + value + "x" + value;
				valuesObj = getOverAllSizeValObj(value, "Length", "Width", "Height");
			} else if (sizeVal.contains("globe")) {
				sizeVal = sizeVal.replaceAll("[^0-9/ ]", "");
				valuesObj = getOverAllSizeValObj(sizeVal, "Circumference", "", "");
			} else if(sizeVal.contains("adjustable strap")){
				continue;
			} else if(sizeVal.contains("handles") || sizeVal.contains("handle") ||
					sizeVal.contains("Strap") || sizeVal.contains("strap")){
				String value = sizeVal.replaceAll("[^0-9/ ]", "");
				valuesObj = getOverAllSizeValObj(value, "Length", "", "");
			} else if(sizeVal.equalsIgnoreCase("Closed: 14\" L, Open: 23\" L, 37\" Span")){
				sizeVal = "23X37";
				valuesObj = getOverAllSizeValObj(sizeVal, "Length", "Width", "");
			} else if(sizeVal.equalsIgnoreCase("Closed: 39 1/2\" L, Open: 57\" Span")){
				sizeVal = "39X57";
				valuesObj = getOverAllSizeValObj(sizeVal, "Length", "Width", "");
			} else if(sizeVal.contains("Span") || sizeVal.contains("span")){
				if(sizeVal.contains(",")){//33" L, 47" Span
					sizeVal  = sizeVal.replaceAll("[^0-9/, ]", "");
					String[] ss = CommonUtility.getValuesOfArray(sizeVal, ",");
					String finalSize = ss[0] + "x"+ss[1];
					valuesObj = getOverAllSizeValObj(finalSize, "Length", "Width", "");
				} else {
					String value = sizeVal.replaceAll("[^0-9/ ]", "");
					valuesObj = getOverAllSizeValObj(value, "Width", "", "");
				}
			} else if (sizeVal.contains("L") || sizeVal.contains("H") || sizeVal.contains("W")
					|| sizeVal.contains("D") || sizeVal.contains("SQ") || sizeVal.contains("DIA") 
					|| sizeVal.contains("Dia") || sizeVal.contains("dia")) {
				sizeVal = getFinalSizeValue(sizeVal);
               String[] sss = CommonUtility.getValuesOfArray(sizeVal, ":");
               if(sss.length == 2){
            	   String finalSize = sss[0];
            	   valuesObj = getOverAllSizeValObj(finalSize, sss[1], "", "");
               } else if(sss.length == 4){
            	   String finalSize = sss[0] + "x"+sss[2];
					valuesObj = getOverAllSizeValObj(finalSize, sss[1], sss[3], "");
               } else if(sss.length == 6){
            	   String finalSize = sss[0] + "x"+sss[2]+ "x"+sss[4];
					valuesObj = getOverAllSizeValObj(finalSize, sss[1], sss[3], sss[3]);
               }
			}
			listOfValues.add(valuesObj);
		}
	   
	   dimentionObj.setValues(listOfValues);
		sizeObj.setDimension(dimentionObj);
		return sizeObj;
   }
   
   private String getFinalSizeValue(String val){
	   val = val.replaceAll("[^0-9/,DWLDSQDiadiaxX ]", "");
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
			String sizeVal =  size.replaceAll("[^0-9/ ]", "");
			String sizeUnit = size.replaceAll("[^a-zA-Z]", "").trim();
			sizeUnit = LookupData.getSizeUnit(sizeUnit);
			String ss = sizeVal+":"+sizeUnit;
			sizess.append(ss).append("x");
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
		   finlSize.append(squreVal).append(":").append(unit1).append(squreVal).append(":").append(unit2)
			.append(s1).append(":").append(unit3);
		} else{//7/8 SQ
			sizeVal  = sizeVal.replaceAll("[^0-9/ ]", "");
			finlSize.append(sizeVal).append(":").append("Length").append(sizeVal).append(":").append("Width");
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
  public List<ImprintSize> getProductImprintSize(String imprSize){
	  imprSize = imprSize.replaceAll("or", ",");
	  imprSize = imprSize.replaceAll(";", ",");
	  List<ImprintSize> imprintSizeList = new ArrayList<>();
	  ImprintSize imprintSizeObj = null;
	  String[] imprSizes = CommonUtility.getValuesOfArray(imprSize, ",");
	  for (String imprSizeName : imprSizes) {
		  imprintSizeObj = new ImprintSize();
		  imprintSizeObj.setValue(imprSizeName);
		  imprintSizeList.add(imprintSizeObj);
	}
	  return imprintSizeList;
  }
  public List<Color> getProductColor(String color){
	  List<Color> colorList = new ArrayList<>();
	  Color colorObj = null;
	  String[] colors = CommonUtility.getValuesOfArray(color, ",");
	  for (String colorName : colors) {
		colorObj = new Color();
		String colorGroup = MerchColorMapping.getColorGroup(colorName);
		if(colorGroup!= null){
			colorObj.setName(colorGroup);
			colorObj.setAlias(colorName);
		} else {
			if(colorName.contains("-")){
				
			} else {
				colorObj.setName("Other");
				colorObj.setAlias(colorName);
			}
		}
		colorList.add(colorObj);
	}
	  return colorList;
  }
  
  public Color getColorCombo(String colorVal){
		Color colorObj = new Color();
		String[] colors = CommonUtility.getValuesOfArray(colorVal, "-");
		String primaryColor = colors[ApplicationConstants.CONST_NUMBER_ZERO];
		String secondaryColor = colors[ApplicationConstants.CONST_INT_VALUE_ONE];
		int noOfColors = colors.length;
		Combo comboObj = new Combo();
		List<Combo> listOfCombo = new ArrayList<>();
		if (noOfColors == ApplicationConstants.CONST_INT_VALUE_TWO) {
			colorObj.setName(primaryColor);
			comboObj.setName(secondaryColor);
			comboObj.setType(ApplicationConstants.CONST_STRING_SECONDARY);

			listOfCombo.add(comboObj);
			colorObj.setAlias(colorVal);
			colorObj.setCombos(listOfCombo);
		} else if (noOfColors == ApplicationConstants.CONST_INT_VALUE_THREE) {
			String thirdColor = colors[ApplicationConstants.CONST_INT_VALUE_TWO];
			colorObj.setName(primaryColor);
			for (int count = 1; count <= 2; count++) {
				comboObj = new Combo();
				if (count == ApplicationConstants.CONST_INT_VALUE_ONE) {
					comboObj.setName(secondaryColor);
					comboObj.setType(ApplicationConstants.CONST_STRING_SECONDARY);

				} else {
					comboObj.setName(thirdColor);
					comboObj.setType(ApplicationConstants.CONST_STRING_TRIM);

				}
				listOfCombo.add(comboObj);
			}
			colorObj.setAlias(colorVal);
			colorObj.setCombos(listOfCombo);
		}
		return colorObj;
	}
}
