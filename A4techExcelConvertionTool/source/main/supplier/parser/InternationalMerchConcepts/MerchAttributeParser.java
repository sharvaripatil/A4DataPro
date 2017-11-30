package parser.InternationalMerchConcepts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.AdditionalLocation;
import com.a4tech.product.model.Artwork;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.AvailableVariations;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Configurations;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.ImprintColor;
import com.a4tech.product.model.ImprintColorValue;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.Shape;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.a4tech.util.LookupData;

public class MerchAttributeParser {
	private MerchPriceGridParser merchPriceGridParser;
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
				sizeVal = sizeVal.substring(0, sizeVal.length() - 1);// trim last character i.e :
                String[] sss = CommonUtility.getValuesOfArray(sizeVal, ":");
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
			}
			listOfValues.add(valuesObj);
		}
	   
	   dimentionObj.setValues(listOfValues);
		sizeObj.setDimension(dimentionObj);
		return sizeObj;
   }
   
   private String getFinalSizeValue(String val){
	   val = removeWordsFromSize(val);
	   val = val.replaceAll("[^0-9/,DWLHSQDiadiaxXDIA ]", "");
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
			if(StringUtils.isEmpty(sizeUnit)){
				sizeUnit = "L";// default value assign
			}
			sizeUnit = LookupData.getSizeUnit(sizeUnit);
			String ss = sizeVal+":"+sizeUnit;
			sizess.append(ss).append(":");
		}   
	   }  
	   return sizess.toString();
   }
   private String removeWordsFromSize(String size){
	   if(size.contains("Holder")){
		   size = size.replaceAll("Holder", "");
	   } else if(size.contains("box")){
		   size = size.replaceAll("Holder", "");
	   } else if(size.contains("expanded")){
		   size = size.replaceAll("expanded", "");
	   } else if(size.contains("Tray")){
		   size = size.replaceAll("Tray", "");
	   }
	   return size;
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
  public List<Color> getProductColor(List<String> colorsList){
	  List<Color> colorList = new ArrayList<>();
	  Color colorObj = null;
	  for (String colorVal : colorsList) {
		  String[] colors = CommonUtility.getValuesOfArray(colorVal, ",");
		  for (String colorName : colors) {
			colorObj = new Color();
			String colorGroup = MerchColorMapping.getColorGroup(colorName);
			if(colorGroup!= null){
				colorObj.setName(colorGroup);
				colorObj.setAlias(colorName);
			} else {
				if(colorName.contains("-")){
					colorObj = getColorCombo(colorName);
				} else {
					colorObj.setName("Other");
					colorObj.setAlias(colorName);
				}
			}
			colorList.add(colorObj);
		}
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
  public List<Option> getProductOption(String optionName,String optionType,String optionValues){
	  Option optionObj = null;
	  List<OptionValue> valuesList=new ArrayList<OptionValue>();
	  OptionValue optionValueObj=null;
	  String[] optionVals = CommonUtility.getValuesOfArray(optionValues, ",");
	  List<Option> listOfOptins = new ArrayList<>();
	  for (String optionVal : optionVals) {
		  optionObj=new Option();
		  if(optionVal.contains(":")){//Rollerball: black ink, Ballpoint: blue ink
			  String[] optionNameAndVal = CommonUtility.getValuesOfArray(optionVal, ":");
			  optionName = optionNameAndVal[0];
			  optionVal = optionNameAndVal[1];
		  }
		  optionValueObj = new OptionValue();
		  optionValueObj.setValue(optionVal);
		  valuesList.add(optionValueObj);
		  optionObj.setName(optionName);
		  optionObj.setOptionType(optionType);
		  optionObj.setValues(valuesList);
		  optionObj.setCanOnlyOrderOne(ApplicationConstants.CONST_BOOLEAN_FALSE);
		  optionObj.setRequiredForOrder(ApplicationConstants.CONST_BOOLEAN_FALSE);
		  optionObj.setAdditionalInformation("");
		  listOfOptins.add(optionObj);
	}
	  return listOfOptins;
  }
  public Product getProductImprintMethod(String imprMethodVal,Product existingProduct){
	  ProductConfigurations config = existingProduct.getProductConfigurations();
	  List<PriceGrid> priceGrids = existingProduct.getPriceGrids();
	  if(CollectionUtils.isEmpty(priceGrids)){
		  priceGrids = new ArrayList<>();
	  }
	  String tempImprintVal = imprMethodVal;
	  List<ImprintMethod> imprintMethodList = new ArrayList<>();
	  ImprintMethod imprintMethodObj = null;
	  if(!tempImprintVal.contains("(")){
		  tempImprintVal = tempImprintVal.replaceAll(" or ", ",");  
	  }
	  String[] imprMethodvals = CommonUtility.getValuesOfArray(tempImprintVal, ",");
	  String[] imprVals = null;
	  for (String imprintMethodName : imprMethodvals) {
		  imprintMethodName = imprintMethodName.trim();
		  if(imprintMethodName.equals("N-A")){
			  continue;
		  }
		  imprintMethodObj = new ImprintMethod();
		  String group = MerchImprintMethodMapping.getImprintMethodGroup(imprintMethodName);
		  if(group == null){
			  imprintMethodObj.setType("Other");
			  imprintMethodObj.setAlias(imprintMethodName);
			  imprintMethodList.add(imprintMethodObj);
		  } else if(group.contains(",")){
			  imprVals = CommonUtility.getValuesOfArray(imprintMethodName, ",");
			  for (String imprName : imprVals) {
				  imprintMethodObj = new ImprintMethod();
				  imprVals = CommonUtility.getValuesOfArray(imprName, "=");
				  imprintMethodObj.setType(imprVals[0]);
				  imprintMethodObj.setAlias(imprVals[1]);
				  imprintMethodList.add(imprintMethodObj);
			}
		  } else {
			  imprVals = CommonUtility.getValuesOfArray(group, "=");
			  imprintMethodObj.setType(imprVals[0]);
			  imprintMethodObj.setAlias(imprVals[1]);
			  imprintMethodList.add(imprintMethodObj);
		  }
	}
		if (imprMethodVal.contains("Laser in matte Silver") || imprMethodVal.contains("Laser in Matte Silver")
				|| imprMethodVal.contains("Lasers in Gold")
				|| imprMethodVal.contains("Lasers in gold")) {
			String imprVal = MerchImprintColorAndLocationMapping.getImprintColorAndImprintGroup(imprMethodVal);
			ImprintColor imprintColors = getProductImprintColors(imprVal);
			config.setImprintColors(imprintColors);

		} else if (imprMethodVal.equalsIgnoreCase("Screen (on handle)")
				|| imprMethodVal.equalsIgnoreCase("Screen (on pouch)")
				|| imprMethodVal.equalsIgnoreCase("Laser, Screen (one location pen or box)")
				|| imprMethodVal.equalsIgnoreCase(
						"Pen: Laser, Screen. Laser on engraving plate also available. Setup & run charges apply.")
				|| imprMethodVal.equalsIgnoreCase("Pen: Laser, Screen  Keychain: Laser")
				|| imprMethodVal.equalsIgnoreCase(
						"Laser on clip or engraving plate, screen on gift box. Setup & run charges apply.")
				|| imprMethodVal.equalsIgnoreCase(
						"Laser on Base included, Sandblast Etch on Globe (additional charges apply) -- base, globe")) {
			String imprLoc = MerchImprintColorAndLocationMapping.getImprintColorAndImprintGroup(imprMethodVal);
			List<ImprintLocation> imprintLocationList = getProductImprintLocation(imprLoc);
			config.setImprintLocation(imprintLocationList);
		}
		if(imprMethodVal.equalsIgnoreCase("Deep Laser, Screen, Laser Polish (Add $.56(G) Per Unit)")){
			//Laser Engraved=Laser Polish Create upcharge based off of Laser Polish ($.56(G) Per Quantity)
			priceGrids = merchPriceGridParser.getUpchargePriceGrid("1", "0.56", "G", "Imprint Method", false, "USD",
					"", "Laser Polish", "Imprint Method Charge", "Other", 1, priceGrids, "", "");
		}
		String imprintMethodValues = imprintMethodList.stream().map(ImprintMethod::getAlias)
				.collect(Collectors.joining(","));
		existingProduct.setDeliveryOption(imprintMethodValues);// for reference purpose use in upcharges
		config.setImprintMethods(imprintMethodList);
		existingProduct.setPriceGrids(priceGrids);
		existingProduct.setProductConfigurations(config);
	  return existingProduct;
  }
  private ImprintColor getProductImprintColors(String imprColor){
	   List<ImprintColorValue> listOfImprintColorVals = new ArrayList<>();
	   ImprintColorValue imprColorValObj = new ImprintColorValue();
	   ImprintColor imprintColorObj = new ImprintColor();
	   imprColorValObj.setName(imprColor);
	   listOfImprintColorVals.add(imprColorValObj);
	   imprintColorObj.setValues(listOfImprintColorVals);
	   imprintColorObj.setType(ApplicationConstants.CONST_STRING_IMPRNT_COLR);
	   return imprintColorObj;
  }	
  private List<ImprintLocation> getProductImprintLocation(String imprLocation){
	  List<ImprintLocation> imprintLocationList = new ArrayList<>();
	  ImprintLocation imprintLocationObj = null;
	  String[] imprLocations = CommonUtility.getValuesOfArray(imprLocation, ",");
	  for (String imprLocName : imprLocations) {
		  imprintLocationObj = new ImprintLocation();
		  imprintLocationObj.setValue(imprLocName);
		  imprintLocationList.add(imprintLocationObj);
	}
	  return imprintLocationList;
  }
  public Product getProductPackaging(String packVal,Product existingProduct){
	  ProductConfigurations config = existingProduct.getProductConfigurations();
	  List<PriceGrid> priceGrids = existingProduct.getPriceGrids();
	  if(CollectionUtils.isEmpty(priceGrids)){
		  priceGrids = new ArrayList<>();
	  }
	  List<Packaging> packagingList = null;
	 if(packVal.equalsIgnoreCase("Bulk. Packaging Options: White Gift Box $0.70(G). Brown Mailer Box $1.20(G)")){
		 packagingList = getPackageValues("Bulk,White Gift Box,Brown Mailer Box");
		 priceGrids = merchPriceGridParser.getUpchargePriceGrid("1", "0.70", "G", "Packaging", false, "USD",
					"", "White Gift Box", "Packaging Charge", "Other", 1, priceGrids, "", "");
		 priceGrids = merchPriceGridParser.getUpchargePriceGrid("1", "1.20", "G", "Packaging", false, "USD",
					"", "Brown Mailer Box", "Packaging Charge", "Other", 1, priceGrids, "", "");
	 } else if(packVal.equalsIgnoreCase("Bulk. Packaging Options: Brown Mailer Box $1.20(G)")){
		 packagingList = getPackageValues("Bulk,Brown Mailer Box");
		 priceGrids = merchPriceGridParser.getUpchargePriceGrid("1", "1.20", "G", "Packaging", false, "USD",
					"", "Brown Mailer Box", "Packaging Charge", "Other", 1, priceGrids, "", "");
	 } else {
		 packagingList = getPackageValues(packVal);
	 }
	  config.setPackaging(packagingList);
	   existingProduct.setPriceGrids(priceGrids);
	   existingProduct.setProductConfigurations(config);
	  return existingProduct;
  }
  private List<Packaging> getPackageValues(String packVal){
	  List<Packaging> packagingList = new ArrayList<>();
	  String[] packVals = CommonUtility.getValuesOfArray(packVal, ",");
	  Packaging packagingObj = null;
	  for (String packName : packVals) {
		  packagingObj = new Packaging();
		  String group = MerchImprintColorAndLocationMapping.getPackagingGroup(packName);
		  if(group!= null){
			  packagingObj.setName(group);
		  } else {
			  packagingObj.setName(packName);	  
		  } 
		  packagingList.add(packagingObj);
	}
	  return packagingList;
  }
  public Product getProductionTime(String prdTimeVal,Product existingProdcut){
	  ProductConfigurations configuration = existingProdcut.getProductConfigurations();
	  List<ImprintMethod> imprintMethodList = configuration.getImprintMethods();
	  if(CollectionUtils.isEmpty(imprintMethodList)){
		  imprintMethodList = new ArrayList<>();
	  }
		String existingImprintMethods = imprintMethodList.stream().map(ImprintMethod::getAlias)
				.collect(Collectors.joining(","));
		if(prdTimeVal.equalsIgnoreCase("Blank: 2 business days; Embroidery: 7-10 business days")){
			List<String> imprMethods = Arrays.asList("Embroidery,Unimprinted");
			ImprintMethod imprMethObj = null;
			for (String imprintMethodName : imprMethods) {
				if(!existingImprintMethods.contains(imprintMethodName)){
					imprMethObj = new ImprintMethod();
					imprMethObj.setAlias(imprintMethodName);
					if(imprintMethodName.equalsIgnoreCase("Embroidery")){
						imprMethObj.setType("Embroidered");
					} else {
						imprMethObj.setType(imprintMethodName);
					}
					imprintMethodList.add(imprMethObj);
				}
			}
			Map<String, String> imprAndPrdTimeAvailMap = new HashMap<>();
			imprAndPrdTimeAvailMap.put("Unimprinted", "2 business days");
			imprAndPrdTimeAvailMap.put("Embroidery", "7-10 business days");
			List<Availability> availabilityList = getProductAvailablity(imprAndPrdTimeAvailMap);
			existingProdcut.setAvailability(availabilityList);
			prdTimeVal = "2 business days;7-10 business days";
		}
	  List<ProductionTime> productionTimeList = new ArrayList<>();
	  ProductionTime prdTimeObj = null;
	  prdTimeVal = prdTimeVal.replaceAll(";", ",");
	  String[] prodTimes = CommonUtility.getValuesOfArray(prdTimeVal,",");
	  for (String prdTime : prodTimes) {
		  prdTimeObj = new ProductionTime();
		  prdTime = prdTime.replaceAll("[^0-9-]", "").trim();
		  prdTimeObj.setBusinessDays(prdTime);
		  prdTimeObj.setDetails("");
		  productionTimeList.add(prdTimeObj);
	}
	  configuration.setProductionTime(productionTimeList);
	  existingProdcut.setProductConfigurations(configuration);
	  return existingProdcut;
  }
  public List<Availability> getProductAvailablity(Map<String, String> availMap){
	  
		List<Availability> listOfAvailablity = new ArrayList<>();
		Availability  availabilityObj = new Availability();
		AvailableVariations  AvailableVariObj = null;
		List<AvailableVariations> listOfVariAvail = new ArrayList<>();
		List<Object> listOfParent = null;
		List<Object> listOfChild = null;
		for (Map.Entry<String, String> entry: availMap.entrySet()) {
			String childImprintMethod = entry.getKey();
			String parentPrdTimeValue = entry.getValue();
			AvailableVariObj = new AvailableVariations();
			 listOfParent = new ArrayList<>();
			 listOfChild = new ArrayList<>();
			 listOfParent.add(parentPrdTimeValue);
			 listOfChild.add(childImprintMethod);
			 AvailableVariObj.setParentValue(listOfParent);
			 AvailableVariObj.setChildValue(listOfChild);
			 listOfVariAvail.add(AvailableVariObj);
		}
		availabilityObj.setAvailableVariations(listOfVariAvail);
		availabilityObj.setParentCriteria("Production Time");
		availabilityObj.setChildCriteria(ApplicationConstants.CONST_STRING_IMPRINT_METHOD);
		listOfAvailablity.add(availabilityObj);
		return listOfAvailablity;
	}
 public Product getProductRushTime(String rushTimeVal,Product existingProduct){
	 ProductConfigurations configuration = existingProduct.getProductConfigurations();
	 List<PriceGrid> priceGrid = existingProduct.getPriceGrids();
	 if(CollectionUtils.isEmpty(priceGrid)){
		 priceGrid = new ArrayList<>();
	 }
	 List<RushTimeValue> listOfRushTimeValue = new ArrayList<>();
		RushTimeValue rushTimeValueObj = null;
		RushTime rushTimeObj = new RushTime();
		String[] rushTimes = CommonUtility.getValuesOfArray(rushTimeVal, ",");
		String priceVal = null, rushTime = null,rushDetails = null;
		for (String rushTimVal : rushTimes) {
			rushTimeValueObj = new RushTimeValue();
			     priceVal = null; rushTime = null;rushDetails = null;
			    if(rushTimVal.contains("3/4")){//3/4 days: $50.00(G)
			    	rushTime = "3-4";priceVal = "50.00";rushDetails = "";
			    } else if(rushTimVal.equalsIgnoreCase("3 day rush:  30% of order total. $60.00(G) minimum")){
			    	rushTime = "3";priceVal = "";rushDetails = "30% of order total. $60.00(G) minimum";
			    } else if(rushTimVal.equalsIgnoreCase("Rush service may be available. Contact IMC for details")){
			    	rushTime = "";priceVal = "";rushDetails = "Rush service may be available. Contact IMC for details.";
			    } else if(rushTimVal.equalsIgnoreCase("2 days (laser only):  $100.00(G)")){
			    	rushTime = "2";priceVal = "100.00";rushDetails = "";
			    } else{
			    	
			    }
			    rushTimeValueObj.setBusinessDays(rushTime);
		    	rushTimeValueObj.setDetails(rushDetails);
				listOfRushTimeValue.add(rushTimeValueObj);
				if(!StringUtils.isEmpty(priceVal)){
				priceGrid = merchPriceGridParser.getUpchargePriceGrid("1", priceVal, "G", "Rush Service", false, "USD",
						"", rushTime + " business days", "Rush Service Charge", "Other", 1, priceGrid, "", "");
				}
		}
			rushTimeObj.setRushTimeValues(listOfRushTimeValue);
			rushTimeObj.setAvailable(ApplicationConstants.CONST_BOOLEAN_TRUE);
			configuration.setRushTime(rushTimeObj);
			existingProduct.setPriceGrids(priceGrid);
	 return existingProduct; 
 }
 public Product getUpchargeImprintMethdoColumns(String val,Product existingProduct,String upchargeType,String priceInclude){
	 List<PriceGrid> priceGrids = existingProduct.getPriceGrids();
	 String[] vals = CommonUtility.getValuesOfArray(val, ",");
	 ProductConfigurations config = existingProduct.getProductConfigurations();
	 List<ImprintMethod> imprintMethodList = config.getImprintMethods();
	 String screenAlias = getImprintMethodAliasName(imprintMethodList, "Silkscreen");
	 if(StringUtils.isEmpty(screenAlias)){
		 imprintMethodList = getImprintMethod("Screen", "Silkscreen", imprintMethodList);
		 screenAlias = "Screen";
	 }
	 //String priceInclude = "";
	 for (String priceVal : vals) {
		    if(priceVal.equalsIgnoreCase("$85.00 (G) 1-color wrap")){
		    	 priceVal = "85.00";priceInclude = "1-color wrap";
		    } else if(priceVal.equalsIgnoreCase("")){// future purpse
		    	
		    } else if(priceVal.equalsIgnoreCase("")){//future purpose
		    	
		    } else {
		    	priceVal = priceVal.replaceAll("[^0-9.]", "");
		    }
		    if(!StringUtils.isEmpty(screenAlias)){
		    	priceGrids = merchPriceGridParser.getUpchargePriceGrid("1", priceVal, "G", "Imprint Method", false, "USD", priceInclude,
		    			screenAlias, upchargeType, "Other", 1, priceGrids, "", "");	
		    }
		    //priceInclude = "";
	}
	 config.setImprintMethods(imprintMethodList);
	 existingProduct.setProductConfigurations(config);
	 existingProduct.setPriceGrids(priceGrids);
	 return existingProduct;
 }
 public Product getUpchargeAdditionalColorAndLocation(String val,Product existingProduct){
	 List<PriceGrid> priceGrids = existingProduct.getPriceGrids();
	 ProductConfigurations config = existingProduct.getProductConfigurations();
	 List<ImprintMethod> imprintMethodList = config.getImprintMethods();
	 List<AdditionalColor> additionalColorList = config.getAdditionalColors();
	 List<AdditionalLocation> additionalLocationList = config.getAdditionalLocations();
	 if(CollectionUtils.isEmpty(additionalColorList)){
		 additionalColorList = getAdditionalColor("Additional Color");
	 }
	 if(CollectionUtils.isEmpty(additionalLocationList)){
		 additionalLocationList = getAdditionalLocation("Additional Location");
	 }
	 if(val.equalsIgnoreCase("Laser $.38(G); Screen $.25(G) per unit + setup")){
		 String laserEngravedAlias = getImprintMethodAliasName(imprintMethodList, "Laser Engraved");
		 String silkscreenAlias = getImprintMethodAliasName(imprintMethodList, "Silkscreen");
		 priceGrids = merchPriceGridParser.getUpchargePriceGrid("1", "0.38", "G", "Imprint Method", false, "USD", "per unit + setup",
				 laserEngravedAlias, "Run Charge", "Per Quantity", 1, priceGrids, "", "");
		 priceGrids = merchPriceGridParser.getUpchargePriceGrid("1", "0.25", "G", "Imprint Method", false, "USD", "per unit + setup",
				 silkscreenAlias, "Run Charge", "Per Quantity", 1, priceGrids, "", "");
	 } else {//$9.00 (G) + setup(remaining values like that)
		 String addLocVals = additionalLocationList.stream().map(AdditionalLocation::getName).collect(Collectors.joining(","));
		 String addColorVals = additionalColorList.stream().map(AdditionalColor::getName).collect(Collectors.joining(","));
		 val = val.replaceAll("[^0-9.]", "").trim();
		/* if(val.contains("per unit")){//$4.50(G) per unit + setup
			 val = val.replaceAll("[^0-9.]", "").trim();priceInclude="per unit + setup";
		 } else {//$9.00 (G) + setup
			 val = val.replaceAll("[^0-9.]", "").trim();priceInclude="setup";
		 }*/
		 priceGrids = merchPriceGridParser.getUpchargePriceGrid("1", val, "G", "Additional Location", false, "USD", "per unit + setup",
				 addLocVals, "Add. Location Charge", "Other", 1, priceGrids, "", "");
		 priceGrids = merchPriceGridParser.getUpchargePriceGrid("1", val, "G", "Additional Colors", false, "USD", "per unit + setup",
				 addColorVals, "Add. Color Charge", "Other", 1, priceGrids, "", "");
	 }
	 config.setAdditionalColors(additionalColorList);
	 config.setAdditionalLocations(additionalLocationList);
	 existingProduct.setProductConfigurations(config);
	 existingProduct.setPriceGrids(priceGrids);
	 return existingProduct;
 }
 private String getImprintMethodAliasName(List<ImprintMethod> imprList,String imprintMethodType){
	 for (ImprintMethod imprintMethod : imprList) {
		if(imprintMethod.getType().equalsIgnoreCase(imprintMethodType)){
			return imprintMethod.getAlias();
		}
	}
	 return "";
 }
 private List<AdditionalColor> getAdditionalColor(String addClrVal){
	 List<AdditionalColor> additionnalColorList = new ArrayList<>();
	 AdditionalColor addColorObj = new AdditionalColor();
	 addColorObj.setName(addClrVal);
	 additionnalColorList.add(addColorObj);
	 return additionnalColorList;
 }
 private List<AdditionalLocation> getAdditionalLocation(String addLocVal){
	 List<AdditionalLocation> additionalLocationList = new ArrayList<>();
	 AdditionalLocation addLocObj = new AdditionalLocation();
	 addLocObj.setName(addLocVal);
	 additionalLocationList.add(addLocObj);
	 return additionalLocationList;
 }
 public Product getUpchargeBasedOnScreenReOrderSetup(String val,Product existingProduct){
	 List<PriceGrid> priceGrids = existingProduct.getPriceGrids();
	 ProductConfigurations config = existingProduct.getProductConfigurations();
	 List<ImprintMethod> imprintMethodList = config.getImprintMethods();
	 String imprintMethodAlias = "";
	 if(val.equalsIgnoreCase("Laser-NC, Screen-$37.50 (G)")){
		 //Laser-NC, Screen-$37.50 (G)" the upcharges should be based on the Silkscreen Imprint Method. 
		 imprintMethodAlias = getImprintMethodAliasName(imprintMethodList, "Silkscreen");
		 if(StringUtils.isEmpty(imprintMethodAlias)){
			 imprintMethodList = getImprintMethod("Screen", "Silkscreen", imprintMethodList);
			 imprintMethodAlias = "Screen";
			 priceGrids = merchPriceGridParser.getUpchargePriceGrid("1", "37.50", "G", "Imprint Method", false, "USD", "",
					 imprintMethodAlias, "Re-Order Charge", "Other", 1, priceGrids, "", "");
		 }
	 } else if(val.equalsIgnoreCase("Laser-NC, Insert-$37.50 (G)")){
		 //the upcharges should be based on whatever Imprint Method that exists that's not Laser Engraved. 
		 //If no other imprint methods currently exist please create one using "Printed=Insert".
		 imprintMethodAlias = imprintMethodList.stream()
					.filter(imprMethod -> !imprMethod.getType().equals("Laser Engraved")).map(ImprintMethod::getAlias)
					.collect(Collectors.joining(","));
			if(StringUtils.isEmpty(imprintMethodAlias)){
				 imprintMethodList = getImprintMethod("Insert", "Printed", imprintMethodList);
				 imprintMethodAlias = "Insert";
			}
		 priceGrids = merchPriceGridParser.getUpchargePriceGrid("1", "37.50", "G", "Imprint Method", false, "USD", "",
				 imprintMethodAlias, "Re-Order Charge", "Other", 1, priceGrids, "", "");
	 } else {//$31.25 (G)
		 val = val.replaceAll("[^0-9.]", "").trim();
		 imprintMethodAlias = imprintMethodList.stream().map(ImprintMethod::getAlias).collect(Collectors.joining(","));
		 priceGrids = merchPriceGridParser.getUpchargePriceGrid("1", val, "G", "Imprint Method", false, "USD", "",
				 imprintMethodAlias, "Re-Order Charge", "Other", 1, priceGrids, "", "");
	 }
	 config.setImprintMethods(imprintMethodList);
	 existingProduct.setProductConfigurations(config);
	 existingProduct.setPriceGrids(priceGrids);
	return  existingProduct;
 }
 private List<ImprintMethod> getImprintMethod(String alias,String type,List<ImprintMethod> existingImprintMethod){
	 ImprintMethod imprintMethodObj = new ImprintMethod();
	 if(CollectionUtils.isEmpty(existingImprintMethod)){
		 existingImprintMethod = new ArrayList<>();
	 }
	 imprintMethodObj.setType(type);
	 imprintMethodObj.setAlias(alias);
	 existingImprintMethod.add(imprintMethodObj);
	 return existingImprintMethod;
 }
 public Product getUpchargeBasedOnLessThanMin(String val,Product existingProduct){
	 List<PriceGrid> priceGrids = existingProduct.getPriceGrids();
	 existingProduct.setCanOrderLessThanMinimum(true);
	 if(val.equalsIgnoreCase("$37.50 (G) + $5.25 (G) per unit")){
		 priceGrids = merchPriceGridParser.getUpchargePriceGrid("1___1","37.50___5.25", "G___G", "Less than Minimum", false, "USD", "",
				 "Can order less than minimum", "Less than Minimum Charge", "Per Order", 1, priceGrids, "", "");
	 } else{
		 val = val.replaceAll("[^0-9.]", "").trim();
		 priceGrids = merchPriceGridParser.getUpchargePriceGrid("1", val, "G", "Less than Minimum", false, "USD", "",
				 "Can order less than minimum", "Less than Minimum Charge", "Per Order", 1, priceGrids, "", "");
	 }
	 existingProduct.setPriceGrids(priceGrids);
	return existingProduct;
 }
 public Product getUpchargeBasedOnLogoModification(String priceVal,Product existingProduct){// it is used to artwork 
	 List<PriceGrid> priceGrids = existingProduct.getPriceGrids();
	 ProductConfigurations config = existingProduct.getProductConfigurations();
	 List<Artwork> artworkList = getArtWork("Art Services","Logo Modification");
	 priceVal = priceVal.replaceAll("[^0-9.]", "").trim(); 
	 priceGrids = merchPriceGridParser.getUpchargePriceGrid("1", priceVal, "G", "Artwork & Proofs", false, "USD", "",
			 "Art Services", "Artwork Charge", "Per Order", 1, priceGrids, "", "");
	 config.setArtwork(artworkList);
	 existingProduct.setPriceGrids(priceGrids);
 return existingProduct;
 }
private List<Artwork> getArtWork(String artworkVal,String comment){
	List<Artwork> artworkList = new ArrayList<>();
	Artwork artworkObj = new Artwork();
	artworkObj.setValue(artworkVal);
	artworkObj.setComments(comment);
	artworkList.add(artworkObj);
	return artworkList;
}
public Product getUpchargeBasedOnTapeCharge(String val,Product existingProduct){
	 List<PriceGrid> priceGrids = existingProduct.getPriceGrids();
	 List<ImprintMethod> imprintMethodList = existingProduct.getProductConfigurations().getImprintMethods();
	 String imprMethodVals = imprintMethodList.stream().map(ImprintMethod::getAlias).collect(Collectors.joining(","));
	 if(val.equalsIgnoreCase("$150.00(G) + $3.75(G) per unit 8K stitches, 6 colors")){
		 priceGrids = merchPriceGridParser.getUpchargePriceGrid("1", "150.00", "G", "Imprint Method", false, "USD", "",
				 imprMethodVals, "Tape Charge", "Per Order", 1, priceGrids, "", "");
		 priceGrids = merchPriceGridParser.getUpchargePriceGrid("1", "37.50", "G", "Imprint Method", false, "USD", "Per unit 8K stitches, 6 colors",
				 imprMethodVals, "Run Charge", "Other", 1, priceGrids, "", "");
	 }
	 existingProduct.setPriceGrids(priceGrids);
    return existingProduct;
}
public ShippingEstimate getProductShippingEstimates(String shippingNoOfItems,String dimensions,String weight) {
	ShippingEstimate shippingEstimateObj = new ShippingEstimate();
	List<NumberOfItems> numberOfItems = null;
	Dimensions dimensionsObj = null;
	List<Weight> shippingWeight = null;
	if(!StringUtils.isEmpty(shippingNoOfItems)){
		numberOfItems = getShippingNumberOfItems(shippingNoOfItems);
	}
	if(!StringUtils.isEmpty(dimensions)){
		dimensionsObj = getShippingDimensions(dimensions);	
	}
	if(!StringUtils.isEmpty(weight)){
		shippingWeight = getShippingWeight(weight);
	}
	shippingEstimateObj.setNumberOfItems(numberOfItems);
	shippingEstimateObj.setWeight(shippingWeight);
	shippingEstimateObj.setDimensions(dimensionsObj);
	return shippingEstimateObj;
}

private List<NumberOfItems> getShippingNumberOfItems(String val) {
	List<NumberOfItems> listOfNumberOfItems = new ArrayList<>();
	NumberOfItems numberOfItemsObj = new NumberOfItems();
	numberOfItemsObj.setValue(val);
	numberOfItemsObj.setUnit("Per Carton");
	listOfNumberOfItems.add(numberOfItemsObj);
	return listOfNumberOfItems;
}

private List<Weight> getShippingWeight(String val) {
	List<Weight> listOfShippingWt = new ArrayList<>();
	Weight weightObj = new Weight();
	weightObj.setValue(val);
	weightObj.setUnit("lbs");
	listOfShippingWt.add(weightObj);
	return listOfShippingWt;
}

private Dimensions getShippingDimensions(String val) {//W X H X L
	String[] vals = val.split("x");
	Dimensions dimensionsObj = new Dimensions();	
		dimensionsObj.setLength(vals[2].trim());
		dimensionsObj.setWidth(vals[0].trim());
		dimensionsObj.setHeight(vals[1].trim());
		dimensionsObj.setLengthUnit(ApplicationConstants.CONST_STRING_INCHES);
		dimensionsObj.setWidthUnit(ApplicationConstants.CONST_STRING_INCHES);
		dimensionsObj.setHeightUnit(ApplicationConstants.CONST_STRING_INCHES);	
	return dimensionsObj;
}
public Product getBasePriceColumns(String qty,String prices,String discount,String priceName,String priceInclude,Product existingProduct,String imprintMethodType){
	 List<PriceGrid> priceGrids = existingProduct.getPriceGrids();
	 ProductConfigurations config = existingProduct.getProductConfigurations();
	 String criteria = "";
	 if(!StringUtils.isEmpty(imprintMethodType)){
		 List<ImprintMethod> imprintMethodList = config.getImprintMethods();
		 String imprMethodAlias = getImprintMethodAliasName(imprintMethodList, imprintMethodType);
		 if(StringUtils.isEmpty(imprMethodAlias)){
			 imprintMethodList = getImprintMethod(imprintMethodType, imprintMethodType, imprintMethodList);
			 imprMethodAlias = imprintMethodType;
		 }
		 if(StringUtils.isEmpty(priceName)){
			 priceName =  imprintMethodType;
		 }
		 criteria = "Imprint Method:"+imprMethodAlias;
		 config.setImprintMethods(imprintMethodList);
	 }
		priceGrids = merchPriceGridParser.getBasePriceGrid(prices, qty, discount, "USD", priceInclude, true, false,
				priceName,criteria, priceGrids, "", "", "");
		existingProduct.setProductConfigurations(config);
		existingProduct.setPriceGrids(priceGrids);
   return existingProduct;
}
public List<ProductNumber> getProductNumbers(Map<String, String> prdNumbers){
	List<ProductNumber> productNumberList = new ArrayList<>();
	ProductNumber productNumberObj = null;
	for (Map.Entry<String,String> numbers : prdNumbers.entrySet()) {
		String prdNo = numbers.getKey();
		String colorVal = numbers.getValue();
		 productNumberObj = new ProductNumber();
		 productNumberObj.setProductNumber(prdNo);
			List<Configurations> listOfConfig = new ArrayList<>();
			Configurations configObj = new Configurations();
			configObj.setCriteria("Product Color");
			configObj.setValue(Arrays.asList(colorVal));
			listOfConfig.add(configObj);
			productNumberObj.setConfigurations(listOfConfig);
			productNumberList.add(productNumberObj);
	}
	return productNumberList;
}
public List<Shape> getProductShape(String shapeVal){
	List<Shape> shapeList = new ArrayList<>();
	Shape shapeObj = new Shape();
	shapeObj.setName(shapeVal);
	shapeList.add(shapeObj);
	return shapeList;
}
  public MerchPriceGridParser getMerchPriceGridParser() {
		return merchPriceGridParser;
	}

	public void setMerchPriceGridParser(MerchPriceGridParser merchPriceGridParser) {
		this.merchPriceGridParser = merchPriceGridParser;
	}
}
