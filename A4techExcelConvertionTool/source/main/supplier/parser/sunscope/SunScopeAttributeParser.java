package parser.sunscope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.OtherSize;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.headWear.HeadWearColorMapping;

public class SunScopeAttributeParser {
	private SunScopePriceGridParser sunScopePriceGridParser;
	private static Logger _LOGGER = Logger.getLogger(SunScopePriceGridParser.class);
  public Product keepExistingProductData(Product existingProduct){
	  Product newProduct = new Product();
	  ProductConfigurations oldConfig = existingProduct.getProductConfigurations();
	  ProductConfigurations newConfig = new ProductConfigurations();
	  if(!CollectionUtils.isEmpty(existingProduct.getImages())){
		  newProduct.setImages(existingProduct.getImages());
	  }
	  if(!CollectionUtils.isEmpty(existingProduct.getCategories())){
		  newProduct.setCategories(existingProduct.getCategories());
	  }
	  if(!CollectionUtils.isEmpty(existingProduct.getProductKeywords())){
		  newProduct.setProductKeywords(existingProduct.getProductKeywords());
	  }
	  if(!StringUtils.isEmpty(existingProduct.getSummary())){
		  newProduct.setSummary(existingProduct.getSummary());
	  }	  
	  if(!CollectionUtils.isEmpty(oldConfig.getThemes())){
			newConfig.setThemes(oldConfig.getThemes());
			List<Theme> themeList = oldConfig.getThemes().stream().map(themeVal ->{
				themeVal.setName(themeVal.getName());
				if(themeVal.getName().equals("ECO FRIENDLY")){
					themeVal.setName("Eco & Environmentally Friendly");
				}
				return themeVal;
			}).collect(Collectors.toList()); 
			newConfig.setThemes(themeList);
		}
	  newProduct.setProductConfigurations(newConfig);
	  return newProduct;
  }
public Size getProductSize(String sizeVal){
	  Size sizeObj = new Size();
	  Dimension dimensionObj =  new Dimension();
	  Values valuesObj = new Values();
	  List<Values> valuesList = new ArrayList<>();
	  if(sizeVal.contains("Dia") || sizeVal.contains("dia")){
		  valuesObj = getSizeForDiameter(sizeVal);
		  valuesList.add(valuesObj);
		  dimensionObj.setValues(valuesList);
		  sizeObj.setDimension(dimensionObj);
	  } else if(sizeVal.contains("wide")){ 
		 OtherSize othervalues = getSizeForOther(sizeVal);
		  sizeObj.setOther(othervalues);
	  } else{
		  sizeVal = sizeVal.replaceAll("[^0-9xX/ ]", ""); 
		  valuesObj = getOverAllSizeValObj(sizeVal);
		  valuesList.add(valuesObj);
		  dimensionObj.setValues(valuesList);
		  sizeObj.setDimension(dimensionObj);
	  }
	  return sizeObj;
  }
  private Values getOverAllSizeValObj(String val){
		//Overall Size: 23.5" x 23.5"
		String[] values = null;
		if(val.contains("x")){
			values = val.split("x");
		} else {
			values = val.split("X");
		}
		Value valObj1 = null;
		Value valObj2 = null;
		Value valObj3 = null;
		List<Value> listOfValue = new ArrayList<>();
		if(values.length == ApplicationConstants.CONST_INT_VALUE_ONE){
			valObj1 = getValueObj(values[0].trim(), "Length", "in"); 
			  listOfValue.add(valObj1);
		} else if(values.length == ApplicationConstants.CONST_INT_VALUE_TWO){
			 valObj1 = getValueObj(values[0].trim(), "Length", "in");
			 valObj2 = getValueObj(values[1].trim(), "width", "in");
			 listOfValue.add(valObj1);
		     listOfValue.add(valObj2);
		} else if(values.length == ApplicationConstants.CONST_INT_VALUE_THREE){
			 valObj1 = getValueObj(values[0].trim(), "Length", "in");
			 valObj2 = getValueObj(values[1].trim(),"width", "in");
			 if(!StringUtils.isEmpty(values[2].trim())){
				 valObj3 = getValueObj(values[2].trim(), "Height", "in");
				 listOfValue.add(valObj3);
			 }
			 listOfValue.add(valObj1);
		     listOfValue.add(valObj2);
		}
		 Values valuesObj = new Values(); 
		 valuesObj.setValue(listOfValue);
		 return valuesObj;
	}
  private Values getSizeForDiameter(String val){
	  List<Value> listOfValue = new ArrayList<>();
	  Values valuesObj = new Values(); 
	  String[] values = val.split("x");
	  String unit1 = "",unit2 = "";
	  if(values[0].contains("ia")){//ia means Dia/dia
		  unit1 = "Dia";
		  unit2 = "Length";
	  } else {
		  unit1 = "Length";
		  unit2 = "Dia";
	  }
	  String val1 = values[0].replaceAll("[^0-9/ ]", "").trim(); 
	  String val2 = values[0].replaceAll("[^0-9/ ]", "").trim(); 
	  Value valObj1 = getValueObj(val1, unit1, "in");
	  Value valObj2 = getValueObj(val2,unit2, "in");
	  listOfValue.add(valObj1);
	  listOfValue.add(valObj2);
	  valuesObj.setValue(listOfValue);
	  return valuesObj;
  }
  private OtherSize getSizeForOther(String val){
	  OtherSize otherSize = new OtherSize();
	  List<Value> listOfValue = new ArrayList<>();
		Value valObj1 = getValueObj(val);
		 listOfValue.add(valObj1);
		 otherSize.setValues(listOfValue);
	     return otherSize;
  }
	private Value getValueObj(String value,String attribute,String unit){
		Value valueObj = new Value();
		if(!StringUtils.isEmpty(attribute)){
			valueObj.setAttribute(attribute);
		}
		valueObj.setUnit(unit);
		valueObj.setValue(value);
		return valueObj;
	}
	private Value getValueObj(String value){
		Value valueObj = new Value();
		valueObj.setValue(value);
		return valueObj;
	}
	public Volume getItemWeightvolume(String itemWeightValue){
		List<Value> listOfValue = null;
		List<Values> listOfValues = null;
		Volume volume  = new Volume();
		Values values = new Values();
		Value valueObj = new Value();
			listOfValue = new ArrayList<>();
			listOfValues = new ArrayList<>();
			valueObj.setValue(itemWeightValue);
			valueObj.setUnit("oz");
			listOfValue.add(valueObj);
			values.setValue(listOfValue);
			listOfValues.add(values);
			volume.setValues(listOfValues);
		return volume;
	}
	public List<Packaging> getProductPackaging(String packVal){
		List<Packaging> packagingList = new ArrayList<>();
		Packaging packObj = new Packaging();
		if(packVal.equalsIgnoreCase("Bulk Packed")){
			packVal = "Bulk";
		} else if(packVal.equalsIgnoreCase("Polybagged")){
			packVal = "Poly Bag";
		} else if(packVal.equalsIgnoreCase("Gift Box")){
			packVal = "Gift Boxes";
		}
		packObj.setName(packVal);
		packagingList.add(packObj);
		return packagingList;
	}
	public ShippingEstimate getShippingEstimation(String shippingDimensions,String shippingNoItems){
		String[] shippingNoOfItemsValues = shippingNoItems.split(",");
		ShippingEstimate shippingEstimation = new ShippingEstimate();
		List<Weight> listOfWeight = new ArrayList<>();
		List<NumberOfItems> listOfNumberOfItems = null;
		for (String shippingVal : shippingNoOfItemsValues) {
			  if(shippingVal.contains("shippingWt")){
				  shippingVal = shippingVal.split(":")[1];
				  listOfWeight = getShippingWeight(shippingVal.trim());
			  } else if(shippingVal.contains("shippingQty")){
				  shippingVal = shippingVal.split(":")[1];
				  listOfNumberOfItems = getShippingNumberOfItems(shippingVal.trim());
			  }
		}
		Dimensions dimensions = getShippingDimensions(shippingDimensions);
		shippingEstimation.setDimensions(dimensions);
		shippingEstimation.setNumberOfItems(listOfNumberOfItems);
		shippingEstimation.setWeight(listOfWeight);
		return shippingEstimation;
	}
	private List<NumberOfItems> getShippingNumberOfItems(String val){
		  List<NumberOfItems> listOfNumberOfItems = new ArrayList<>();
		  NumberOfItems numberOfItemsObj = new NumberOfItems();
		  numberOfItemsObj.setValue(val);
		  numberOfItemsObj.setUnit("per Case");
		  listOfNumberOfItems.add(numberOfItemsObj);
		  return listOfNumberOfItems;
	  }
	  private List<Weight> getShippingWeight(String val){
		  List<Weight> listOfShippingWt = new ArrayList<>();
		  Weight weightObj = new Weight();
		  weightObj.setValue(val);
		  weightObj.setUnit("lbs");
		  listOfShippingWt.add(weightObj);
		  return listOfShippingWt;
	  }
	  private Dimensions getShippingDimensions(String val){
		  String[] vals = null;
		  String length = "";
		  String width = "";
		  String height = "";
	    	 vals = val.split("x");
	    	 length = vals[0];
	    	 width = vals[1];
    		 height = vals[2];
		  Dimensions dimensionsObj = new Dimensions();
		  if(!StringUtils.isEmpty(length)){
			  dimensionsObj.setLength(length.trim());
			  dimensionsObj.setLengthUnit("in");
		  }
		  if(!StringUtils.isEmpty(width)){
			  dimensionsObj.setWidth(width.trim());
			  dimensionsObj.setWidthUnit("in");
		  }
		  if(!StringUtils.isEmpty(height)){
			  dimensionsObj.setHeight(height.trim());
			  dimensionsObj.setHeightUnit("in");
		  }
		  return dimensionsObj;
	  }
	  public List<ImprintSize> getProductImprintSize(Set<String> imprSizes){
		List<ImprintSize> imprintSizeList = imprSizes.stream().map(imprintSizeName -> {
			ImprintSize imprintSizeObj = new ImprintSize();
			imprintSizeObj.setValue(imprintSizeName);
			return imprintSizeObj;
		}).collect(Collectors.toList());
		  return imprintSizeList;
	  }
     public List<PriceGrid> getPriceAndUpcharges(Map<String, StringBuilder> pricesMap,List<PriceGrid> existingPriceGrid){
    	 if(CollectionUtils.isEmpty(existingPriceGrid)){
    		 existingPriceGrid = new ArrayList<>();
    	 }
		try {
			for (Map.Entry<String, StringBuilder> prices : pricesMap.entrySet()) {
				String imprintMethodVal = prices.getKey();
				if (!StringUtils.isEmpty(imprintMethodVal)) {
					imprintMethodVal = getImprintMethodForPrice(imprintMethodVal);
					StringBuilder allValue = prices.getValue();
					String[] allVals = CommonUtility.getValuesOfArray(allValue.toString(), "__");
					for (String priceType : allVals) {
						if (priceType.contains("runCharge") || priceType.contains("setUp")) {
							existingPriceGrid = getupcharges(priceType, imprintMethodVal, existingPriceGrid);
						} else {// it is base price
							existingPriceGrid = getBasePrices(priceType, imprintMethodVal, existingPriceGrid);
						}
					}
				}
			}
		} catch (Exception exce) {
			_LOGGER.error("Unable to parse prices:" + exce.getClass());
		}
    	 return existingPriceGrid;
     }
     private List<PriceGrid> getupcharges(String val,String imprintMethodVals,List<PriceGrid> existingPriceGrid){
    	 String[] vals = CommonUtility.getValuesOfArray(val, ":");
    	 String priceVal = vals[1];
    	 if(val.contains("runCharge")){
			existingPriceGrid = sunScopePriceGridParser.getUpchargePriceGrid("1", priceVal, "C", "Imprint Method",
					false, "USD", "", imprintMethodVals, "Run Charge", "Other", 1, existingPriceGrid, "", "");
    	 } else {//setup charge
    		 existingPriceGrid = sunScopePriceGridParser.getUpchargePriceGrid("1", priceVal, "C", "Imprint Method",
 					false, "USD", "", imprintMethodVals, "Set-up Charge", "Other", 1, existingPriceGrid, "", "");
    	 }
    	 return existingPriceGrid;
     }
     private List<PriceGrid> getBasePrices(String val,String imprintMethodVals,List<PriceGrid> existingPriceGrid){
    	String[] priceIncludeAndPrices = CommonUtility.getValuesOfArray(val, "###");
    	String priceInclude = priceIncludeAndPrices[1];
    	String price = priceIncludeAndPrices[0];
    	String[] prices = CommonUtility.getValuesOfArray(price, ApplicationConstants.CONST_DELIMITER_UNDER_SCORE);
    	StringBuilder pricess = new StringBuilder();
    	StringBuilder quantity =new StringBuilder();
    	for (String priceVal : prices) {
			String[] qtyAndPrice = CommonUtility.getValuesOfArray(priceVal, ApplicationConstants.CONST_DELIMITER_COLON);
			quantity.append(qtyAndPrice[0]).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			pricess.append(qtyAndPrice[1]).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		}
		existingPriceGrid = sunScopePriceGridParser.getBasePriceGrid(pricess.toString(), quantity.toString(), "C",
				"USD", priceInclude, true, false, imprintMethodVals, "Imprint Method", existingPriceGrid, "", "");
    	 return existingPriceGrid;
     }
     private String getImprintMethodForPrice(String imprMethodVal){
    	 imprMethodVal = imprMethodVal.replaceAll("[^A-Za-z-/& ]", "");
    	if(imprMethodVal.equals("Blank")){
    		imprMethodVal = "Unimprinted";
    	} else if(imprMethodVal.equals("Deboss/Foil")){
    		imprMethodVal = "Deboss/Foil";
    	} else{
    		if(imprMethodVal.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)){
				imprMethodVal = imprMethodVal.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH,
						ApplicationConstants.CONST_DELIMITER_COMMA);
    		}
    	}
    	return imprMethodVal;
     }
     public List<ImprintMethod> getProductImprintMethod(Set<String> imprMethodVals){
    	 List<ImprintMethod> imprintMethodList = new ArrayList<>();
    	 ImprintMethod imprintMethodObj = null;
    	 for (String imprintMethodName : imprMethodVals) {
    		 if(StringUtils.isEmpty(imprintMethodName)){
    			 continue;
    		 }
    		 imprintMethodObj = new ImprintMethod();
    		 String group = SunScopeColorAndImprintMethodMapping.getImprintMethodGroup(imprintMethodName.trim());
    		 if(group.contains(",")){//Debossed,Foil Stamped,Pad Print
				imprintMethodList = Arrays.stream(CommonUtility.getValuesOfArray(group, ",")).map(imprMethdName -> {
					ImprintMethod imprintMethodObj1 = new ImprintMethod();
					imprintMethodObj1.setType(imprMethdName);
					imprintMethodObj1.setAlias(imprMethdName);
					return imprintMethodObj1;
				}).collect(Collectors.toList());
    		 } else {
    			 if(group.contains("=")){//Other=SpectaDomeTM
    				 String[] imprVals = CommonUtility.getValuesOfArray(group, "=");
    				 imprintMethodObj.setType(imprVals[0]);
    				 imprintMethodObj.setAlias(imprVals[1]);
    			 } else{  //Silkscreen
    				 imprintMethodObj.setType(group);
    				 imprintMethodObj.setAlias(group);
    			 }
    			 imprintMethodList.add(imprintMethodObj);
    		 }
		}
    	 return imprintMethodList;
     }
     public List<Color> getProductColor(String colorVal){
    	 List<Color> colorList = new ArrayList<>();
    	 Color colorObj = null;
    	 String[] colorVals = CommonUtility.getValuesOfArray(colorVal, ",");
    	 for (String colorName : colorVals) {
    		 colorName = colorName.trim();
			colorObj = new Color();
			String colorGroup = SunScopeColorAndImprintMethodMapping.getColorGroup(colorName);
			if(colorGroup.contains("Combo")){
				colorObj = getColorCombo(colorGroup,colorName);
				colorList.add(colorObj);
			} else if(colorGroup.equals("Other")){
				 if(colorName.contains("and")){
					 String[] colorValss = CommonUtility.getValuesOfArray(colorName, "and");
					 for (String colorNamee : colorValss) {
							colorObj = new Color();
							colorNamee = colorNamee.trim();
							String colorGrup = SunScopeColorAndImprintMethodMapping.getColorGroup(colorNamee);
							if(colorGroup.contains("Combo")){
								colorObj = getColorCombo(colorGroup,colorNamee);
							}else {
								 colorObj.setName(colorGrup); 
								 colorObj.setAlias(colorNamee);
							}
							colorList.add(colorObj);
					 }
				 } else {
					 colorObj.setName(colorGroup); 
					 colorObj.setAlias(colorName);
					 colorList.add(colorObj);
				 }
			} else {
				 colorObj.setName(colorGroup); 
				 colorObj.setAlias(colorName);
				 colorList.add(colorObj);
			}
		}
    	 return colorList;
     }
     private Color getColorCombo(String comboVal,String alias){
 		Color colorObj = new Color();
 		List<Combo> listOfComos = new ArrayList<>();
 		Combo comboObj1 = new Combo();
 		Combo comboObj2 = new Combo();
 		String[] comboColors = CommonUtility.getValuesOfArray(comboVal,":");
 		colorObj.setName(comboColors[0]);
 		comboObj1.setName(comboColors[2]);
 		comboObj1.setType(ApplicationConstants.CONST_STRING_SECONDARY);
 		colorObj.setAlias(alias);
 		if(comboColors.length == 5){
 			comboObj2.setName(comboColors[4]);
 			comboObj2.setType(ApplicationConstants.CONST_STRING_TRIM);
 			listOfComos.add(comboObj2);
 		} 
 		listOfComos.add(comboObj1);
 		colorObj.setCombos(listOfComos);
 		return colorObj;
 	}
	public SunScopePriceGridParser getSunScopePriceGridParser() {
		return sunScopePriceGridParser;
	}
	public void setSunScopePriceGridParser(SunScopePriceGridParser sunScopePriceGridParser) {
		this.sunScopePriceGridParser = sunScopePriceGridParser;
	}
}
