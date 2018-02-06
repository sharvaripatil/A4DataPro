package parser.towelSpecialties;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.AdditionalLocation;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.AvailableVariations;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
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

import parser.headWear.HeadWearColorMapping;


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
		if(!StringUtils.isEmpty(existingProduct.getName())){
			newProduct.setName(existingProduct.getName());
		}
		if(!StringUtils.isEmpty(existingProduct.getDescription())){
			newProduct.setDescription(existingProduct.getDescription());
		}
		newProduct.setProductConfigurations(newConfig);
		return newProduct;
	}
	
	public List<Color> getProductColor1(String colorName,String colorGroup){
		List<Color> listOfColors = new ArrayList<>();
		Color colorObj = new Color();
		colorObj.setName(colorGroup);
		colorObj.setAlias(colorName);
		listOfColors.add(colorObj);
		return listOfColors;
	}
	public List<Color> getProductColor(String colorVal){
		List<Color> listOfColors = new ArrayList<>();
		Color colorObj = null;
		if(colorVal.contains("and")){
			colorVal = colorVal.replaceAll("and", ",");
		}
		String[] colorVals = CommonUtility.getValuesOfArray(colorVal, ",");
		for (String colorName : colorVals) {
			colorName = colorName.trim();
			if(StringUtils.isEmpty(colorName)){
				continue;
			}
			colorObj = new Color();
			String colorGroup = TowelSpecQtyAndColorMapping.getColorGroup(colorName.toUpperCase());
			if(colorGroup.equals("Other")){
				if(colorName.contains(".")){
					colorName = colorName.replaceAll("\\.", "").trim();
					colorGroup = TowelSpecQtyAndColorMapping.getColorGroup(colorName.toUpperCase());
				}
			}
			if(colorName.contains("/") || colorName.contains("&")){
				listOfColors = getColorCombo("not used",colorName,listOfColors);
			} else if(colorGroup.contains("Combo")){
				listOfColors = getColorCombo(colorName,colorGroup,listOfColors);
			} else {
				colorObj.setAlias(colorName);
				colorObj.setName(colorGroup);
				listOfColors.add(colorObj);
			}
		}
		return listOfColors;
	}
	private List<Color> getColorCombo(String alias ,String comboVals,List<Color> colorsList){
        Color colorObj = new Color();
        List<Combo> listOfComos = new ArrayList<>();
        Combo comboObj1 = new Combo();
        Combo comboObj2 = new Combo();
        String[] comboColors = null;
        String[] combos = CommonUtility.getValuesOfArray(comboVals, ",");
        for (String comboVal : combos) {
        	if(comboVal.contains("/")){
            	comboColors = CommonUtility.getValuesOfArray(comboVal,
                        ApplicationConstants.CONST_DELIMITER_FSLASH);
            } else if(comboVal.contains("&")){
            	comboColors = CommonUtility.getValuesOfArray(comboVal,"&");
            }/* else if(comboVal.contains("Combo")){
            	comboColors = CommonUtility.getValuesOfArray(comboVal,":");
            }*/ else {
            	
            }
        	if(comboVal.contains("Combo")){
        		//Orange:Combo:Black=orange with black border -- case_1
        		//WHITE WITH RED","White:Combo:Red _case_2
        		String[] comboColorss = null;
        		if(comboVal.contains("=")){
        			String[] comboAlias = CommonUtility.getValuesOfArray(comboVal,"=");
                	comboColorss = CommonUtility.getValuesOfArray(comboAlias[0],":");
                	alias = comboAlias[1];
        		} else {
        			comboColorss = CommonUtility.getValuesOfArray(comboVal,":");
        		}
        		
         		colorObj.setName(comboColorss[0]);
         		comboObj1.setName(comboColorss[2]);
         		comboObj1.setType(ApplicationConstants.CONST_STRING_SECONDARY);
         		colorObj.setAlias(alias);
         		if(comboColorss.length == 5){
         			comboObj2.setName(comboColorss[4]);
         			comboObj2.setType(ApplicationConstants.CONST_STRING_TRIM);
         			listOfComos.add(comboObj2);
         		}
            } else { 
            	colorObj.setName(
                		TowelSpecQtyAndColorMapping.getColorGroup(comboColors[ApplicationConstants.CONST_NUMBER_ZERO].toUpperCase()));
                comboObj1.setName(
                		TowelSpecQtyAndColorMapping.getColorGroup(comboColors[ApplicationConstants.CONST_INT_VALUE_ONE].toUpperCase()));
                comboObj1.setType(ApplicationConstants.CONST_STRING_SECONDARY);
                colorObj.setAlias(comboVal.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH,
                            ApplicationConstants.CONST_DELIMITER_HYPHEN));
                if(comboColors.length == 3){
                      comboObj2.setName(
                    		  TowelSpecQtyAndColorMapping.getColorGroup(comboColors[ApplicationConstants.CONST_INT_VALUE_TWO].toUpperCase()));
                      comboObj2.setType(ApplicationConstants.CONST_STRING_TRIM);
                      listOfComos.add(comboObj2);
                }	
            }
             
            listOfComos.add(comboObj1);
            colorObj.setCombos(listOfComos);
            colorsList.add(colorObj);
		}
          return colorsList;
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
		prdTime = prdTime.replaceAll(" ", "");
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
			
		}String[] imprMethodVals = null;
		if(value.equalsIgnoreCase("Screenprinted (1 color) on chair, tote and towel")){
			imprMethodVals=	new String[] {value};
		} else if(value.equalsIgnoreCase("Screenprinted, Colors") || value.equalsIgnoreCase("Screenprinted, White") ||
				value.equalsIgnoreCase("Screenprint (white, 1 color)") || value.equalsIgnoreCase("Screenprint (white, multicolor)")){
			imprMethodVals=	new String[] {value};
		}
		else {
			imprMethodVals = CommonUtility.getValuesOfArray(value, ",");
		}
		
		for (String imprMethodName : imprMethodVals) {
			imprMethodName = imprMethodName.trim();
			imprintMethodObj = new ImprintMethod();
			if(imprMethodName.contains("Blank") && !imprMethodName.equalsIgnoreCase("Screenprinted Blanket & Tote")){
				alias = "Unimprinted"; groupName = "Unimprinted";
			} else if(imprMethodName.equalsIgnoreCase("LSSFHM - Decorated (1 color on towel, ColorFusion on bag)") ||
					imprMethodName.equalsIgnoreCase("LSSHFS - Decorated (1 color on towel and tote)") ) {
				alias = imprMethodName; groupName = "Other";
			}
			else if(imprMethodName.equalsIgnoreCase("Screenprinted (1 color) on chair, tote and towel")){
				alias = imprMethodName; groupName = "Silkscreen";
			} else if(imprMethodName.contains("Screenprint") || imprMethodName.contains("screenprint")){
				alias = imprMethodName; groupName = "Silkscreen";
			} else if(imprMethodName.equalsIgnoreCase("Embroidered") || imprMethodName.equalsIgnoreCase("Embroidery") ||
				imprMethodName.equalsIgnoreCase("Tone on Tone with embroidery") ||
				imprMethodName.equalsIgnoreCase("Tone on Tone and Embroidery") || imprMethodName.contains("Embroidered")
				|| imprMethodName.contains("Embroidery") || imprMethodName.contains("Embroider") ){
				alias = imprMethodName; groupName = "Embroidered";
			}else if(imprMethodName.contains("Printed")){
				alias = imprMethodName; groupName = "Printed";
			} else if(imprMethodName.equalsIgnoreCase("Laser Patch") || imprMethodName.equalsIgnoreCase("Laser")){
				alias = imprMethodName; groupName = "Laser Engraved";
			} else if(imprMethodName.contains("Tone on Tone") || imprMethodName.contains("ColorFusion")
					  || imprMethodName.contains("Colorfusion") || imprMethodName.contains("Decorated")
					  || imprMethodName.equalsIgnoreCase("Label") || imprMethodName.equalsIgnoreCase("Custom Fiber Reactive")
					  || imprMethodName.equalsIgnoreCase("tote and towel")){
				alias = imprMethodName; groupName = "Other";
			} else if(imprMethodName.equalsIgnoreCase("Embroidery digitization charge up to 8000 stitches") 
					|| imprMethodName.equalsIgnoreCase("Embroidery digitization charge over 8000 stitches")){
				alias = imprMethodName; groupName = "Embroidered";
			}
			/*boolean isImprMethodAvailable = false;
			for (ImprintMethod methodVal : imprintMethodList) {
				  if(!methodVal.getAlias().equalsIgnoreCase(alias)){
					  isImprMethodAvailable
				  }
			}*/
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
	    		 String[] valsss = valss[1].split("x");
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
			imprSizeVal = imprSizeVal.trim();
			if(imprSizeVal.contains("http")){//check value contains html tag or not
				imprSizeVal = imprSizeVal.split("\\.")[0];
			}
			// this condition avoid duplicate values in list
			/*if(!imprintSizeList.stream().map(ImprintSize::getValue).filter(imprSizeVal::equals).findFirst().isPresent()){
				imprintSizeObj.setValue(imprSizeVal);
				imprintSizeList.add(imprintSizeObj);
			}*/
			if(!imprintSizeList.stream().map(ImprintSize::getValue).anyMatch(imprSizeVal::equals)){
				imprintSizeObj.setValue(imprSizeVal);
				imprintSizeList.add(imprintSizeObj);
			}
		}
		 return imprintSizeList;
	 }
	public Product getUpchargesBasedOnImprintMethod(String value, String upchargeName, String imprintMethodInclude,
			                                                                                       Product product) {
		 List<PriceGrid> priceGrid = product.getPriceGrids();
		 ProductConfigurations config = product.getProductConfigurations();
		 String priceVal = null;
		  if(value.contains("No charge for 144 + gift set;")){
			  priceVal = value.split(":")[1];
			  priceVal = priceVal.replaceAll("[^0-9\\.]", "");// remove unnecessary characters from given string except numbers
			  priceVal =   priceVal.substring(0, priceVal.length() - 1); // remove last character(150.00.)
			  priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", priceVal, "v", "Imprint Method", false,
						"USD", imprintMethodInclude, upchargeName, "Digitizing Charge",
						"Other", 1, priceGrid, "", "");
		  } else if(value.contains("same artwork on both")){
			  List<AdditionalLocation> additionalLocList = getAdditionalLocation("Second Side Imprint");
			  config.setAdditionalLocations(additionalLocList);
			  //Screen charge: For first order, $45.00(v) per side for same artwork on both sides. 
			  //Otherwise, add $45.00(v) for second side. For re-orders, $22.50(v).
			priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "45.00", "v", "Additional Location", false,
					"USD", imprintMethodInclude, "Second Side Imprint", "Screen charge",
					"Other", 1, priceGrid, "", "");
			priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "22.50", "v", "Imprint Method", false,
					"USD", imprintMethodInclude, upchargeName, "Re-Order Charge",
					"Other", 1, priceGrid, "", "");
			priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "45.00", "v", "Imprint Method", false,
					"USD", imprintMethodInclude, upchargeName, "Screen charge",
					"Other", 1, priceGrid, "", "");
		  } else if(value.contains("8000 stitches")){
			  priceGrid = towelPriceGridParser.getUpchargePriceGrid("1___1", "75.00___45.00", "v_v", "Imprint Method", false,
						"USD", imprintMethodInclude, upchargeName, "Screen charge",
						"Other", 1, priceGrid, "", "");
		  } else if(value.contains("Over 8,000 stitches")){
			  priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "75", "v", "Imprint Method", false,
						"USD", imprintMethodInclude, "Embroidery digitization charge up to 8000 stitches", "Digitizing Charge",
						"Other", 1, priceGrid, "", "");
			  priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "10", "v", "Imprint Method", false,
						"USD", imprintMethodInclude, "Embroidery digitization charge over 8000 stitches", "Digitizing Charge",
						"Other", 1, priceGrid, "", "");
		  } else if(value.contains("second color")){
			  List<AdditionalColor> additionalColorList = getAdditionalColors("First Color", "Second Color");
			 // $65.00(v) for first color on blanket and tote. Maximum second color on blanket only: $65.00(v). 
			 // Add the values of "First Color" and "Second Color" to the Additional Colors field, and base the upcharges of these.
			priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "65.00", "v", "Additional Colors", false, "USD",
					imprintMethodInclude, "First Color", "Add. Color Charge", "Other", 1, priceGrid, "", "");
			priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "65.00", "v", "Additional Colors", false, "USD",
					imprintMethodInclude, "Second Color", "Add. Color Charge", "Other", 1, priceGrid, "", "");
			config.setAdditionalColors(additionalColorList);
		  } else if(value.contains("each additional color")){
			  //Set Up Charge: For 2 colors on towel and 1 on bag - $285.00 (v) for first order; $82.50(v) for re-orders.  
			  //Add $120.00(v) for each additional color on towel for first order; $60.00(v) for re-order.
			  priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "285.00", "v", "Imprint Method", false, "USD",
					  imprintMethodInclude, upchargeName, "Set-up Charge", "Other", 1, priceGrid, "", "");
			 priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "82.50", "v", "Imprint Method", false, "USD",
					 imprintMethodInclude, upchargeName, "Re-Order Charge", "Other", 1, priceGrid, "", "");
			 priceGrid = towelPriceGridParser.getUpchargePriceGrid("1___1", "120.00___60.00", "v___v", "Additional Colors", false, "USD",
					 imprintMethodInclude, "Each Additional Color", "Re-Order Charge", "Other", 1, priceGrid, "", ""); 
			 List<AdditionalColor> additionalColorList = getAdditionalColors("Each Additional Color", "");
			  config.setAdditionalColors(additionalColorList);
		  } else if(value.contains("one color on blanket")){
			  //Screen charge: For first order - $120.00(v) for one color on blanket and $45.00(v) for one color on backpack.
			  //For re-orders, $60.00(v) for blanket, $22.50(v) for backpack
			  List<Option> optionsList = getProductOptions("Product Imprint", "Blanket Imprint,Backpack Imprint");
			  config.setOptions(optionsList);
			  priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "120.00", "v", "Imprint Option", false, "USD",
					  imprintMethodInclude, "Blanket Imprint", "Screen Charge", "Other", 1, priceGrid, "Product Imprint", "");
			  priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "45.00", "v", "Imprint Option", false, "USD",
					  imprintMethodInclude, "Backpack Imprint", "Screen Charge", "Other", 1, priceGrid, "Product Imprint", "");
			  priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "60.00", "v", "Imprint Option", false, "USD",
					  imprintMethodInclude, "Blanket Imprint", "Re-Order Charge", "Other", 1, priceGrid, "Product Imprint", "");
			  priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "22.50", "v", "Imprint Option", false, "USD",
					  imprintMethodInclude, "Backpack Imprint", "Re-Order Charge", "Other", 1, priceGrid, "Product Imprint", "");
		} else if (value.contains("Set Up Charge") || value.contains("Set up charge") || value.contains("Screen Charge")
				|| value.contains("Screen charge") || value.contains("Art set-up charge")) {
			String[] upchargeValues = null;
			String price1 = null;
			if((value.contains("towel") || value.contains("Towel")) && (value.contains("bag") || value.contains("Bag"))){
				List<Option> optionsList = getProductOptions("Product Imprint", "Towel Imprint,Bag Imprint");
				config.setOptions(optionsList);
				if(value.contains("unlimited colors")){
					//Set Up Charge: $165.00(v) for one color on towel and unlimited colors on the bag; $82.50(v)/color for re-orders.
					priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "165.00", "v", "Imprint Option", false, "USD",
							imprintMethodInclude, "Towel Imprint", "Set-up Charge", "Other", 1, priceGrid, "Product Imprint", "");
				  priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "82.50", "v", "Imprint Option", false, "USD",
						  imprintMethodInclude, "Bag Imprint", "Re-Order Charge", "Other", 1, priceGrid, "Product Imprint", "");
				}else if(value.contains("165.00")){
					//Screen Charge: $165.00(v) for one color on towel and bag; $82.50(v)/color for re-orders.
					priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "165.00", "v", "Imprint Option", false, "USD",
							imprintMethodInclude, "Towel Imprint", "Screen Charge", "Other", 1, priceGrid, "Product Imprint", "");
				  priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "82.50", "v", "Imprint Option", false, "USD",
						  imprintMethodInclude, "Bag Imprint", "Re-Order Charge", "Other", 1, priceGrid, "Product Imprint", "");
				} else {
				//Towel - $45.00 (v)/color for first order; $22.50(v)/color for re-orders. Bag: $45.00(v); $22.50(v) for re-orders.
					priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "45.00", "v", "Imprint Option", false, "USD",
							imprintMethodInclude, "Towel Imprint", "Set-up Charge", "Other", 1, priceGrid, "Product Imprint", "");
				  priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "22.50", "v", "Imprint Option", false, "USD",
						  imprintMethodInclude, "Bag Imprint", "Re-Order Charge", "Other", 1, priceGrid, "Product Imprint", "");
				}
			}
			if(value.contains("Set Up Charge") || value.contains("Set up charge")){
					upchargeValues = CommonUtility.getValuesOfArray(value, ";");
					if(upchargeValues.length > 1){
					  price1 = upchargeValues[0];
					  String price2 = upchargeValues[1];
					  price1 = price1.replaceAll("[^0-9\\.]", "");
		  			 price2 = price2.replaceAll("[^0-9\\.]", "");
		  			price2 =   price2.substring(0, price2.length() - 1);
		  			priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", price2, "v", "Imprint Method", false, "USD",
		  					imprintMethodInclude, upchargeName, "Re-Order Charge", "Other", 1, priceGrid, "", "");
					} else {
						 price1 = upchargeValues[0];
						price1 = price1.replaceAll("[^0-9\\.]", "");
					}
					price1 =   price1.substring(0, price1.length() - 1);
					priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", price1, "v", "Imprint Method", false, "USD",
							imprintMethodInclude, upchargeName, "Set-up Charge", "Other", 1, priceGrid, "", "");	
			} else if(value.contains("Screen Charge") || value.contains("Screen charge")){
				upchargeValues = CommonUtility.getValuesOfArray(value, ";");
                if(upchargeValues.length > 1){
  				   price1 = upchargeValues[0];
  				String price2 = upchargeValues[1];
  				  price1 = price1.replaceAll("[^0-9\\.]", "");
  				 price2 = price2.replaceAll("[^0-9\\.]", "");
  				priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", price2, "v", "Imprint Method", false, "USD",
  						imprintMethodInclude, upchargeName, "Re-Order Charge", "Other", 1, priceGrid, "", "");
				} else {
					 price1 = upchargeValues[0];
					price1 = price1.replaceAll("[^0-9\\.]", "");
				}
                priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", price1, "v", "Imprint Method", false, "USD",
                		imprintMethodInclude, upchargeName, "Screen Charge", "Other", 1, priceGrid, "", "");
			} else if(value.contains("Art set-up charge")){
				priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", "65", "v", "Imprint Method", false, "USD",
						imprintMethodInclude, upchargeName, "Set-up Charge", "Other", 1, priceGrid, "", "");
			}  
		  }else if(value.contains(";")){//it is same for screen charge condition
				String[] upchargeValues = CommonUtility.getValuesOfArray(value, ";");
				String price1 = "";
				String price2 = "";
                if(upchargeValues.length > 1){
  				   price1 = upchargeValues[0];
  				 price2 = upchargeValues[1];
  				  price1 = price1.replaceAll("[^0-9\\.]", "");
  				 price2 = price2.replaceAll("[^0-9\\.]", "");
  				priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", price2, "v", "Imprint Method", false, "USD",
  						imprintMethodInclude, upchargeName, "Re-Order Charge", "Other", 1, priceGrid, "", "");
				} else {
					 price1 = upchargeValues[0];
					price1 = price1.replaceAll("[^0-9\\.]", "");
				}
                priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", price1, "v", "Imprint Method", false, "USD",
                		imprintMethodInclude, upchargeName, "Screen Charge", "Other", 1, priceGrid, "", "");
			}  else{//$45.00(v)
			  value = value.replaceAll("[^0-9\\.]", "");
			  if(value.substring(value.length()-1).equals(".")){
				  value = value.substring(0, value.length()-1);
			  }
			  priceGrid = towelPriceGridParser.getUpchargePriceGrid("1", value, "v", "Imprint Method", false, "USD",
					  imprintMethodInclude, upchargeName, "Imprint Method Charge", "Other", 1, priceGrid, "", "");
		  }
		 product.setProductConfigurations(config);
		 product.setPriceGrids(priceGrid);
		 return product;
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
	 private List<AdditionalLocation> getAdditionalLocation(String locVal){
		 List<AdditionalLocation> additionalLocList = new ArrayList<>();
		 AdditionalLocation addLocObj = new AdditionalLocation();
		 addLocObj.setName(locVal);
		 additionalLocList.add(addLocObj);
		 return additionalLocList;
	 }
	//public List<Availability> getProductAvailablity(Set<String> parentList,Set<String> childList){
		public List<Availability> getProductAvailablity(List<String> childListOfProTime ,List<String> parentListImprint){
			List<Availability> listOfAvailablity = new ArrayList<>();
			Availability  availabilityObj = new Availability();
			AvailableVariations  AvailableVariObj = null;
			List<AvailableVariations> listOfVariAvail = new ArrayList<>();
			List<Object> listOfParent = null;
			List<Object> listOfChild = null;
			for (String ParentValue : parentListImprint) { //String childValue : childList
				 for (String childValue : childListOfProTime) {//String ParentValue : parentList
					 AvailableVariObj = new AvailableVariations();
					 listOfParent = new ArrayList<>();
					 listOfChild = new ArrayList<>();
					 listOfParent.add(ParentValue.trim());
					 listOfChild.add(childValue.trim()+" business days");
					 AvailableVariObj.setParentValue(listOfParent);
					 AvailableVariObj.setChildValue(listOfChild);
					 listOfVariAvail.add(AvailableVariObj);
				}
			}
			availabilityObj.setAvailableVariations(listOfVariAvail);
			//availabilityObj.setParentCriteria(ApplicationConstants.CONST_STRING_PRODUCT_COLOR);
			//availabilityObj.setChildCriteria(ApplicationConstants.CONST_STRING_SIZE);
			availabilityObj.setParentCriteria("Imprint Method");
			availabilityObj.setChildCriteria("Production Time");
			listOfAvailablity.add(availabilityObj);
			return listOfAvailablity;
		}
     public List<AvailableVariations> getProductAvailabilityVariations(String parentVal,String childVal,List<AvailableVariations> listOfAvailabli){
    	 AvailableVariations  AvailableVariObj = new AvailableVariations();
    	 List<Object> listOfParent = new ArrayList<>();
			List<Object> listOfChild = new ArrayList<>();
			listOfParent.add(parentVal.trim());
			 listOfChild.add(childVal.trim()+" business days");
			 AvailableVariObj.setParentValue(listOfParent);
			 AvailableVariObj.setChildValue(listOfChild);
			 listOfAvailabli.add(AvailableVariObj);
    	 return listOfAvailabli;
     }
	 public TowelSpecPriceGridParser getTowelPriceGridParser() {
			return towelPriceGridParser;
		}

		public void setTowelPriceGridParser(TowelSpecPriceGridParser towelPriceGridParser) {
			this.towelPriceGridParser = towelPriceGridParser;
		}

}
