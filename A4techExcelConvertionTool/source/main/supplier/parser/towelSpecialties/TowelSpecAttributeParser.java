package parser.towelSpecialties;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;


public class TowelSpecAttributeParser {
	
	private TowelSpecPriceGridParser towelPriceGridParser;
	
	public  Product keepExistingProductData(Product existingProduct){
		ProductConfigurations existingConfig = existingProduct.getProductConfigurations();
		Product newProduct = new Product();
		ProductConfigurations newConfig = new ProductConfigurations();
		if(!CollectionUtils.isEmpty(existingProduct.getImages())){
		   newProduct.setImages(existingProduct.getImages());	
		}
		if(!CollectionUtils.isEmpty(existingConfig.getThemes())){
			newConfig.setThemes(existingConfig.getThemes());
		}
		if(!CollectionUtils.isEmpty(existingProduct.getProductKeywords())){
			newProduct.setProductKeywords(existingProduct.getProductKeywords());
		}
		if(!CollectionUtils.isEmpty(existingProduct.getCategories())){
			newProduct.setCategories(existingProduct.getCategories());
		}
		newProduct.setProductConfigurations(newConfig);
		return newProduct;
	}
	
	public List<Color> getProductColor(String colorName,String colorGroup){
		List<Color> listOfColors = new ArrayList<>();
		Color colorObj = new Color();
		colorObj.setName(colorGroup);
		colorObj.setAlias(colorName);
		listOfColors.add(colorObj);
		return listOfColors;
	}
	public List<ProductionTime> getProductionTime(String prdTime,List<ProductionTime> productionTimeList){
		ProductionTime productionTimeObj = new ProductionTime();
		//List<ProductionTime> listOfProductionTime = new ArrayList<>();
		if(prdTime.contains("week") || prdTime.contains("weeks")){
			if(prdTime.contains("-")){
				prdTime = prdTime.replaceAll("[^0-9- ]", "");
				prdTime = CommonUtility.convertProductionTimeWeekIntoDays(prdTime, "-");
			} else if(prdTime.contains("to")){
				prdTime = prdTime.replaceAll("[^0-9to ]", "");
				prdTime = CommonUtility.convertProductionTimeWeekIntoDays(prdTime, "to");
			}
		} else { //Days
			prdTime = prdTime.replaceAll("[^0-9- ]", "");
		}
		prdTime = prdTime.trim();
		if(!productionTimeList.stream().map(ProductionTime::getBusinessDays).anyMatch(prdTime::equals)){
			productionTimeObj.setBusinessDays(prdTime);
			productionTimeObj.setDetails("");
			productionTimeList.add(productionTimeObj);
		}
		return productionTimeList;
	}
	
	public List<ImprintMethod> getImprintMethods(String value ,List<ImprintMethod> imprintMethodList){
		
		//List<ImprintMethod> listOfImprintMethod = new ArrayList<>();
		ImprintMethod imprintMethodObj = null;
		String alias = null; String groupName = null;
		if(value.contains("(")){
			// no need to replace character
		} else if(value.contains("/")){
			value = value.replaceAll("/", ",");
		} else {
			
		}
		String[] imprMethodVals = CommonUtility.getValuesOfArray(value, ",");
		for (String imprMethodName : imprMethodVals) {
			imprMethodName = imprMethodName.trim();
			imprintMethodObj = new ImprintMethod();
			if(imprMethodName.contains("Blank")){
				alias = "Unimprinted"; groupName = "Unimprinted";
			} else if(imprMethodName.contains("Screenprint") || imprMethodName.contains("screenprint")){
				alias = imprMethodName; groupName = "Silkscreen";
			} else if(imprMethodName.equalsIgnoreCase("Embroidered") || imprMethodName.equalsIgnoreCase("Embroidery") ||
					imprMethodName.equalsIgnoreCase("Tone on Tone with embroidery") || imprMethodName.equalsIgnoreCase("Tone on Tone and Embroidery")){
				alias = imprMethodName; groupName = "Embroidered";
			}else if(imprMethodName.contains("Printed")){
				alias = imprMethodName; groupName = "Printed";
			} else if(imprMethodName.equalsIgnoreCase("Laser Patch")){
				alias = imprMethodName; groupName = "Laser Engraved";
			} else if(imprMethodName.contains("Tone on Tone") || imprMethodName.contains("ColorFusion")
					  || imprMethodName.contains("Colorfusion") || imprMethodName.contains("Decorated")
					  || imprMethodName.equalsIgnoreCase("Label") || imprMethodName.equalsIgnoreCase("Custom Fiber Reactive")
					  || imprMethodName.equalsIgnoreCase("tote and towel")){
				alias = imprMethodName; groupName = "Other";
			}
			if(!imprintMethodList.stream().map(ImprintMethod::getAlias).anyMatch(alias::equals)){
				imprintMethodObj.setAlias(alias);
				imprintMethodObj.setType(groupName);
				imprintMethodList.add(imprintMethodObj);
			}
		}
		return imprintMethodList;
	}
	public ShippingEstimate getShippingEstimation(String value){
		String[] shippingValues = value.split(",");
		ShippingEstimate shippingEstimation = new ShippingEstimate();
		Dimensions dimensions = null;
		List<Weight> listOfWeight = new ArrayList<>();
		List<NumberOfItems> listOfNumberOfItems = null;
		for (String shippingVal : shippingValues) {
			  if(shippingVal.contains("shippingDimention")){
				  shippingVal = shippingVal.split(":")[1];
				  shippingVal = shippingVal.replaceAll("[^0-9xX]", "");
				  dimensions = getShippingDimensions(shippingVal);
			  } else if(shippingVal.contains("shippingWt")){
				  shippingVal = shippingVal.split(":")[1];
				  listOfWeight = getShippingWeight(shippingVal.trim());
			  } else if(shippingVal.contains("shippingQty")){
				  shippingVal = shippingVal.split(":")[1];
				  listOfNumberOfItems = getShippingNumberOfItems(shippingVal.trim());
			  }
		}
		shippingEstimation.setDimensions(dimensions);
		shippingEstimation.setNumberOfItems(listOfNumberOfItems);
		shippingEstimation.setWeight(listOfWeight);
		return shippingEstimation;
	}
	private List<NumberOfItems> getShippingNumberOfItems(String val){
		  List<NumberOfItems> listOfNumberOfItems = new ArrayList<>();
		  NumberOfItems numberOfItemsObj = new NumberOfItems();
		  numberOfItemsObj.setValue(val);
		  numberOfItemsObj.setUnit("per Carton");
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
		  if(val.contains("X")){
			  String[] valss = val.split("X");
	    	 length = valss[0];
	    	 if(valss[1].contains("x")){
	    		 String[] valsss = val.split("x");
	    		 width = valsss[0];
	    		 height = valsss[1];
	    	 } else{
	    		 width = valss[1];
	    		 height = valss[2];
	    	 }
	     } else {
	    	 vals = val.split("x");
	    	 length = vals[0];
	    	 width = vals[1];
    		 height = vals[2];
	     }
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
	 public List<ImprintSize> getImprintSizes(String value,List<ImprintSize> imprintSizeList){
		 ImprintSize imprintSizeObj = null;
		 //List<ImprintSize> listOfImprintSize = new ArrayList<>();
		 value = value.replaceAll(";", ",");
		 String[] imprSizeValues = CommonUtility.getValuesOfArray(value, ",");
		 for (String imprSizeVal : imprSizeValues) {
			imprintSizeObj = new ImprintSize();
			if(imprSizeVal.contains("http")){//check value contains html tag or not
				imprSizeVal = imprSizeVal.split("\\.")[0];
			}
			// this condition avoid duplicate values in list
			if(!imprintSizeList.stream().map(ImprintSize::getValue).anyMatch(imprSizeVal::equals)){
				imprintSizeObj.setValue(imprSizeVal);
				imprintSizeList.add(imprintSizeObj);
			}
		}
		 return imprintSizeList;
	 }
	 public void getUpchargesBasedOnImprintMethod(String value,String upchargeName,Product product){
		 List<PriceGrid> priceGrid = product.getPriceGrids();
		 ProductConfigurations config = product.getProductConfigurations();
		 String priceInclude = null;
		 String priceVal = null;
		 String disc = null;
		  if(value.contains("No charge for 144 + gift set;")){
			  priceInclude = value;
			  priceVal = value.split(":")[1];
			  priceVal = priceVal.replaceAll("[^0-9\\.]", "");// remove unnecessary characters from given string except numbers
			  priceVal =   priceVal.substring(0, priceVal.length() - 1); // remove last character(150.00.)
			  disc = "V";
		  } else if(value.contains("second color")){
			  List<AdditionalColor> additionalColorList = getAdditionalColors("First Color", "Second Color");
			 // $65.00(v) for first color on blanket and tote. Maximum second color on blanket only: $65.00(v). 
			 // Add the values of "First Color" and "Second Color" to the Additional Colors field, and base the upcharges of these.
			priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "65.00", "v", "Additional Colors", false, "USD",
					"", "First Color", "Add. Color Charge", "Other", 1, priceGrid, "", "");
			priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "65.00", "v", "Additional Colors", false, "USD",
					"", "Second Color", "Add. Color Charge", "Other", 1, priceGrid, "", "");
			config.setAdditionalColors(additionalColorList);
		  } else if(value.contains("each additional color")){
			  //Set Up Charge: For 2 colors on towel and 1 on bag - $285.00 (v) for first order; $82.50(v) for re-orders.  
			  //Add $120.00(v) for each additional color on towel for first order; $60.00(v) for re-order.
			  priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "285.00", "v", "Imprint Method", false, "USD",
						"", upchargeName, "Set-up Charge", "Other", 1, priceGrid, "", "");
			 priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "82.50", "v", "Imprint Method", false, "USD",
						"", upchargeName, "Re-Order Charge", "Other", 1, priceGrid, "", "");
			 priceGrid = towelPriceGridParser.getUpchargePriceGrid("1___1", "120.00___60.00", "v___v", "Additional Colors", false, "USD",
						"", "Each Additional Color", "Re-Order Charge", "Other", 1, priceGrid, "", ""); 
			 List<AdditionalColor> additionalColorList = getAdditionalColors("Each Additional Color", "");
			  config.setAdditionalColors(additionalColorList);
		  } else if(value.contains("one color on blanket")){
			  //Screen charge: For first order - $120.00(v) for one color on blanket and $45.00(v) for one color on backpack.
			  //For re-orders, $60.00(v) for blanket, $22.50(v) for backpack
			  List<Option> optionsList = getProductOptions("Product Imprint", "Blanket Imprint,Backpack Imprint");
			  config.setOptions(optionsList);
			  priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "120.00", "v", "Imprint Option", false, "USD",
						"", "Blanket Imprint", "Screen Charge", "Other", 1, priceGrid, "Product Imprint", "");
			  priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "45.00", "v", "Imprint Option", false, "USD",
						"", "Backpack Imprint", "Screen Charge", "Other", 1, priceGrid, "Product Imprint", "");
			  priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "60.00", "v", "Imprint Option", false, "USD",
						"", "Blanket Imprint", "Re-Order Charge", "Other", 1, priceGrid, "Product Imprint", "");
			  priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "22.50", "v", "Imprint Option", false, "USD",
						"", "Backpack Imprint", "Re-Order Charge", "Other", 1, priceGrid, "Product Imprint", "");
		} else if (value.contains("Set Up Charge") || value.contains("Set up charge") || value.contains("Screen Charge")
				|| value.contains("Screen charge") || value.contains("Art set-up charge")) {
			String[] upchargeValues = null;
			String price1 = null;
			if(value.contains("Set Up Charge") || value.contains("Set up charge")){
				upchargeValues = CommonUtility.getValuesOfArray(value, ";");
				if(upchargeValues.length > 1){
				  price1 = upchargeValues[0];
				  String price2 = upchargeValues[1];
				  price1 = price1.replaceAll("[^0-9\\.]", "");
	  			 price2 = price2.replaceAll("[^0-9\\.]", "");
	  			priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", price2, "v", "Imprint Method", false, "USD",
						"", upchargeName, "Re-Order Charge", "Other", 1, priceGrid, "", "");
				} else {
					 price1 = upchargeValues[0];
					price1 = price1.replaceAll("[^0-9\\.]", "");
				}
				priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", price1, "v", "Imprint Method", false, "USD",
						"", upchargeName, "Set-up Charge", "Other", 1, priceGrid, "", "");
			} else if(value.contains("Screen Charge") || value.contains("Screen charge")){
				upchargeValues = CommonUtility.getValuesOfArray(value, ";");
                if(upchargeValues.length > 1){
  				   price1 = upchargeValues[0];
  				String price2 = upchargeValues[1];
  				  price1 = price1.replaceAll("[^0-9\\.]", "");
  				 price2 = price2.replaceAll("[^0-9\\.]", "");
  				priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", price2, "v", "Imprint Method", false, "USD",
						"", upchargeName, "Re-Order Charge", "Other", 1, priceGrid, "", "");
				} else {
					 price1 = upchargeValues[0];
					price1 = price1.replaceAll("[^0-9\\.]", "");
				}
                priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", price1, "v", "Imprint Method", false, "USD",
						"", upchargeName, "Screen Charge", "Other", 1, priceGrid, "", "");
			} else {
			
			}
			  
		  } else{//$45.00(v)
			  value = value.replaceAll("[^0-9\\.]", "");
			  priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", value, "v", "Imprint Method", false, "USD",
						"", upchargeName, "Imprint Method Charge", "Other", 1, priceGrid, "", "");

		  }
		 
		 product.setProductConfigurations(config);
	 }
	 
	 private List<AdditionalColor> getAdditionalColors(String color1,String color2){
		 List<AdditionalColor> additionalColorList = new ArrayList<>();
		 AdditionalColor additionalColorObj1 = new AdditionalColor();
		 AdditionalColor additionalColorObj2 = new AdditionalColor();
		 additionalColorObj1.setName(color1);
		 additionalColorObj2.setName(color2);
		 if(!StringUtils.isEmpty(color2)){
			 additionalColorList.add(additionalColorObj2);
		 }
		 additionalColorList.add(additionalColorObj1);
		 return additionalColorList;
	 }
	 private List<Option> getProductOptions(String optionName,String optionVal){
		 List<Option> optionsList = new ArrayList<>();
		 Option optionObj = new Option();
		 List<OptionValue> optionValuesList = new ArrayList<>();
		 OptionValue optionValObj = null;
		 String[] optionVals = CommonUtility.getValuesOfArray(optionVal, ",");
		 optionObj.setName(optionName);
		 optionObj.setOptionType("Imprint");
		 for (String val : optionVals) {
			 optionValObj = new OptionValue();
			 optionValObj.setValue(val);
			 optionValuesList.add(optionValObj);
		}
		 optionObj.setValues(optionValuesList);
		 optionsList.add(optionObj);
		 return optionsList;
	 }
	 public TowelSpecPriceGridParser getTowelPriceGridParser() {
			return towelPriceGridParser;
		}

		public void setTowelPriceGridParser(TowelSpecPriceGridParser towelPriceGridParser) {
			this.towelPriceGridParser = towelPriceGridParser;
		}

}
