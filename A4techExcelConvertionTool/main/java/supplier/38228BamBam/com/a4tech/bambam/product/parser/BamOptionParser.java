package com.a4tech.bambam.product.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.AdditionalLocation;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class BamOptionParser {
	private static final Logger _LOGGER = Logger.getLogger(BamOptionParser.class);
	private BamPriceGridParser bamPriceGridParser ;
	
	public Product getOptionSheetData(String value,Product existingProduct,List<ImprintMethod> existingImprintMethods){
		  ProductConfigurations existingConfig = existingProduct.getProductConfigurations();
		  List<PriceGrid> existingPriceGrid    = existingProduct.getPriceGrids();
		  List<ImprintLocation> listOfImprintLoc = new ArrayList<>();
		  try{
			 if(value.contains("prices published are kits that are in stock in Virginia")){
				  existingProduct.setDistributorOnlyComments(" Limited quantities are stocked in house; prices published are kits that are in stock in Virginia warehouse or coming in on the Super Saver Service level.");
			  } else if(value.equalsIgnoreCase("Spot Color Set Up $47.00 per color (V)")){
				  existingImprintMethods = getImpintMethodObjVal("Spot Color", "Full Color", existingImprintMethods);
				  existingPriceGrid = bamPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,"47","V" ,
							"IMMD",ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, "Spot Color", 
							"Set-up Charge", ApplicationConstants.CONST_VALUE_TYPE_OTHER, ApplicationConstants.CONST_INT_VALUE_ONE,"", existingPriceGrid);
			  } else if(value.equalsIgnoreCase("Full Bleed $0.07 (R)")){
				  existingImprintMethods = getImpintMethodObjVal("Full Bleed", "Full Color", existingImprintMethods);
				  existingPriceGrid = bamPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,"0.07","R" ,
							"IMMD",ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, "Full Bleed", 
							"Set-up Charge", ApplicationConstants.CONST_VALUE_TYPE_OTHER, ApplicationConstants.CONST_INT_VALUE_ONE,"", existingPriceGrid);
			  } else if(value.contains("Note: If artwork is not identical on both sides")){
				  
				  List<AdditionalLocation> listOfAdditionalLoc = getAdditionalLocationVal("Second Side");
				  existingPriceGrid = bamPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,"47","V" ,
							"ADLN",ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, "Second Side", 
							"Set-up Charge", ApplicationConstants.CONST_VALUE_TYPE_OTHER, ApplicationConstants.CONST_INT_VALUE_ONE,
							"Per color for second side if artwork not identical on both sides.", existingPriceGrid);
				  existingConfig.setAdditionalLocations(listOfAdditionalLoc);//
			  } else if(value.equalsIgnoreCase("Barrel Set Up per color $30.00 (U)")){
				  listOfImprintLoc = getImprintLocationVal("Barrel",listOfImprintLoc);
				  existingPriceGrid = bamPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,"30","U" ,
							"IMLO",ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, "Barrel", 
							"Set-up Charge", ApplicationConstants.CONST_VALUE_TYPE_OTHER, ApplicationConstants.CONST_INT_VALUE_ONE,
							"per color", existingPriceGrid);
				  existingConfig.setImprintLocation(listOfImprintLoc);
			  } else if(value.equalsIgnoreCase("Clip or Barrel Set Up per color $30.00 (U)")){
				  listOfImprintLoc = getImprintLocationVal("Clip or Barrel",listOfImprintLoc);
				  existingPriceGrid = bamPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,"30","U" ,
							"IMLO",ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, "Clip or Barrel", 
							"Set-up Charge", ApplicationConstants.CONST_VALUE_TYPE_OTHER, ApplicationConstants.CONST_INT_VALUE_ONE,
							"per color", existingPriceGrid);
				  existingConfig.setImprintLocation(listOfImprintLoc);
			  } else if(value.equalsIgnoreCase("Default finishing is Whipstitch.") || 
					    value.equalsIgnoreCase("Not water proof or resistant") ||
					    value.equalsIgnoreCase("NOTE:  Plates for this product can only be stored and last up to 6 months.") ||
					    value.equalsIgnoreCase("Stock Slogan #1 \"Texting Kills") ||
					    value.equals("Size of thumb band must be specified!")){
				  String desc = existingProduct.getDescription();
				  desc = CommonUtility.appendStrings(desc, value, " ");
				  existingProduct.setDescription(desc);
			  } else if(value.equalsIgnoreCase("This product is only available on the Super Service level.")){
				  existingProduct.setDistributorOnlyComments(value);
			  } else if(value.contains("polybagged.") || value.contains("bagged") || value.contains("poly")
					    || value.contains("Poly") || value.contains("bulk")){//individually
				  String packValue = "Poly Bag";
				  if(value.contains("individually")){
					  packValue = "Individual Poly Bag";
				  } else if(value.contains("bulk")){
					  packValue = "Bulk";
				  }
				  List<Packaging> listOfPackaging = getPackagingList(packValue);
				  existingConfig.setPackaging(listOfPackaging);
			  } else if(value.contains("lbs /")){
				  ShippingEstimate shippingEstimateObj = getShippingEstimation(value);
				  existingConfig.setShippingEstimates(shippingEstimateObj);
			  } else if(value.equalsIgnoreCase("Decorative Edge Options -Wave, Swirl or Diamond $0.65 (T).")){
				  //Option Name = Decorative Edge Options, Option Values = Wave, Swirl, Diamond.
				  //Upcharge set with all three option values as criteria, 
				  //Type: Product Option Charge, Level: Per Quantity, $0.65 (T).
				  String optionName = "Decorative Edge Options";
				  String optionType = "Product";
				  List<String> optionValues = Arrays.asList("Wave","Swirl","Diamond");
				  List<Option> listOfOptions = getOptions(optionName, optionType, optionValues,"");
				  existingConfig.setOptions(listOfOptions);
				   String val = "Wave,Swirl,Diamond";
				  existingPriceGrid = bamPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,"0.65","T" ,
							"PROP:Decorative Edge Options",ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, val, 
							"Product Option Charge", "Per Quantity", ApplicationConstants.CONST_INT_VALUE_ONE,
							 "",existingPriceGrid);
			  } else if(value.contains("for extra mold")){
				  //Imprint Option: Extra Mold, Values: Change of Design, Change in size.
				  String optionName = "Extra Mold";
				  String optionType = "Imprint";
				  List<String> optionValues = Arrays.asList("Change of Design","Change in size");
				  List<Option> listOfOptions = getOptions(optionName, optionType, optionValues,"");
				  existingConfig.setOptions(listOfOptions);
				   String val = "Change of Design";
				  existingPriceGrid = bamPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,"70","V" ,
							"IMOP:Extra Mold",ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, val, 
							"Imprint Option Charge", "Other", ApplicationConstants.CONST_INT_VALUE_ONE,
							 "for extra mold (Change in Design)",existingPriceGrid);
				  val = "Change in size";
				  existingPriceGrid = bamPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,"70","V" ,
							"IMOP:Extra Mold",ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, val, 
							"Imprint Option Charge", "Other", ApplicationConstants.CONST_INT_VALUE_ONE,
							 "for extra mold (Change in size)",existingPriceGrid);
			  } else if(value.equalsIgnoreCase("Full Color Insert Card / 1 side $0.05 (R)")){
				  // Option Name: Insert Card, Option Value: Full Color Insert Card, Option Additional Information: 1 side. 
				  //Upcharge level per quantity.
				  String optionName = "Insert Card";
				  String optionType = "Product";
				  List<String> optionValues = Arrays.asList("Full Color Insert Card");
				  List<Option> listOfOptions = getOptions(optionName, optionType, optionValues,"1 side");
				  existingConfig.setOptions(listOfOptions);
				   String val = "Full Color Insert Card";
				  existingPriceGrid = bamPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,"0.65","T" ,
							"PROP:Insert Card",ApplicationConstants.CONST_CHAR_Y,  ApplicationConstants.CONST_STRING_CURRENCY_USD, val, 
							"Product Option Charge", "Per Quantity", ApplicationConstants.CONST_INT_VALUE_ONE,
							 "",existingPriceGrid);
			  } else if(value.contains("CMYK or Gradient Printing Available")) {
				  String optionName = "Printing Type";
				  String optionType = "Imprint";
				  List<String> optionValues = Arrays.asList("CMYK","Gradient");
				  List<Option> listOfOptions = getOptions(optionName, optionType, optionValues,"1 side");
				  existingConfig.setOptions(listOfOptions);
			  }
			  else if(value.equalsIgnoreCase("No additional color run charges apply; set ups apply per color.")){
				  // ignore
			  } else if(value.equalsIgnoreCase("Logo Set Up $50.00 (V)")){
				  existingImprintMethods = getImpintMethodObjVal("Printed Logo", "Printed", existingImprintMethods);
				  existingPriceGrid = bamPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,"50.00","V" ,
							"IMMD",ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, "Printed Logo", 
							"Imprint Method Charge", "Other", ApplicationConstants.CONST_INT_VALUE_ONE,
							 "",existingPriceGrid);
			  } else if(value.equalsIgnoreCase("Less Than 500 units Set Up $77.00 (V) per imprint color/location and Custom Pantone Material will apply.")){
				  existingProduct.setCanOrderLessThanMinimum(ApplicationConstants.CONST_BOOLEAN_TRUE);
				  existingPriceGrid = bamPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,"77","V" ,
							"LMIN",ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, "Can order less than minimum", 
							"Less Than Minimum Charge", ApplicationConstants.CONST_VALUE_TYPE_OTHER, ApplicationConstants.CONST_INT_VALUE_ONE,
							 "per imprint color/location and Custom Pantone Material will apply.",existingPriceGrid);
			  } else if(value.contains("Card Packaging")){
				  if(value.contains("$0.05")){
					  List<Packaging> existingPackaging = existingConfig.getPackaging();
					  if(CollectionUtils.isEmpty(existingPackaging)){
						  existingPackaging = new ArrayList<>();
					  }
					  existingPackaging = getProductPackaging("Card Packaging", existingPackaging);
					  existingPriceGrid = bamPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,"0.05","R" ,
								"PCKG",ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, "Card Packaging", 
								"Run Charge", "Per Quantity", ApplicationConstants.CONST_INT_VALUE_ONE,
								 "",existingPriceGrid);
					  existingConfig.setPackaging(existingPackaging);
				  }
				  
			  }
			  else if(value.contains("Additional Color/Location") || value.contains("Additional Color/ Location")){
				   
				  List<AdditionalLocation> listOfAdditionalLoc = getAdditionalLocationVal("Additional Location");
			      List<AdditionalColor> listOfAdditionColor = getAdditionalColorVal("Additional Color");
			      existingConfig.setAdditionalLocations(listOfAdditionalLoc);
				  existingConfig.setAdditionalColors(listOfAdditionColor);
			      String priceValue = "";
			      String priceChargeType = "Set-up Charge";
			      if(value.contains("36")){
			    	  priceValue = "36.00";
			    	  priceChargeType = "Set-up Charge";
			      } else if(value.contains("0.03")){
			    	  priceValue = "0.03";
			    	  priceChargeType = "Run Charge";
			      } else if(value.contains("0.08")){
			    	  priceValue = "0.08";
			    	  priceChargeType = "Run Charge";
			      } else {
			    	  
			      }
			      boolean isDifferUpcharge = true;
			      if(value.equalsIgnoreCase("Additional Color/Location Run Charge $0.045 (R)")){
			    	  isDifferUpcharge = false;
				   } else{
					   isDifferUpcharge = true;
				   }
			      if(isDifferUpcharge){
			    	  existingPriceGrid = bamPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,priceValue,"R" ,
								"ADLN",ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, "Additional Location", 
								priceChargeType, "Per Quantity", ApplicationConstants.CONST_INT_VALUE_ONE,
								"", existingPriceGrid);
					  existingPriceGrid = bamPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,priceValue,"R" ,
								"ADCL",ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, "Additional Color", 
								priceChargeType, "Per Quantity", ApplicationConstants.CONST_INT_VALUE_ONE,
								 "",existingPriceGrid);
			      }
			 
			  
			  }
			  else if(value.contains("Additional Color") || value.contains("Additional Run Charge")){
				  List<AdditionalColor> listOfAdditionColor = getAdditionalColorVal("Additional Color");
				  existingConfig.setAdditionalColors(listOfAdditionColor);
				  String priceValue = "";
				  String priceInclude = "";
				  if(value.contains(";")){
					  priceInclude = value.split(";")[1];
				  }
				  if(value.contains("0.05")){//Run Charge R
					  priceValue = "0.05";
				  } else if(value.contains("0.10")){//Per Quantity
					  priceValue = "0.10"; 
				  } else if(value.contains("0.02")){
					  priceValue = "0.02";
				  } else if(value.contains("0.19")){
					  priceValue = "0.19";
				  } else if(value.contains("0.04")){
					  priceValue = "0.04";
				  } else if(value.contains("0.06")){
					  priceValue = "0.06";
				  } else if(value.contains("0.07")){
					  priceValue = "0.07";
				  } else if(value.contains("0.03")){
					  priceValue = "0.03";
				  } else if(value.contains("0.15")){
					  priceValue = "0.15";
				  } else if(value.contains("47")){//
					  priceValue = "47";
				  } else if(value.contains("3.73")){
					  priceValue = "3.73";
				  } else if(value.contains("0.19")){
					  priceValue = "0.19";
				  } else if(value.contains("0.32")){
					  priceValue = "0.32";
				  }
				  if(!StringUtils.isEmpty(priceValue)){
					  existingPriceGrid = bamPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,priceValue,"R" ,
								"ADCL",ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, "Additional Color", 
								"Run Charge", "Per Quantity", ApplicationConstants.CONST_INT_VALUE_ONE,
								 priceInclude,existingPriceGrid);
				  }
				 
			  }
		  }catch(Exception exce){
			  _LOGGER.error("unable to parser option sheet: "+exce.getMessage());
		  }
		  existingConfig.setImprintMethods(existingImprintMethods);
		  existingProduct.setProductConfigurations(existingConfig);
		  existingProduct.setPriceGrids(existingPriceGrid);
		  return existingProduct;
	  }
	  
	  private List<ImprintMethod> getImpintMethodObjVal(String value,String type,List<ImprintMethod> existingImprintMethod){
		  if(CollectionUtils.isEmpty(existingImprintMethod)){
			  existingImprintMethod = new ArrayList<>(); 
		  }
		  ImprintMethod imprintMethodObj = new ImprintMethod();
		  imprintMethodObj.setType(type);
		  imprintMethodObj.setAlias(value);
		  existingImprintMethod.add(imprintMethodObj);
		  return existingImprintMethod;
	  }
	  
	  private List<Packaging> getPackagingList(String value){
		  List<Packaging> listOfPackaging = new ArrayList<>();
		  Packaging packObj = new Packaging();
		  packObj.setName(value);
		  listOfPackaging.add(packObj);
		  return listOfPackaging;
	  }
	  private ShippingEstimate getShippingEstimation(String value){
		  //Carton Size: 20"x14"x8" / 50 lbs / 500 units,3000 units / 36 lbs / 40x40x32cm
		  ShippingEstimate shippingEstimateObj = new ShippingEstimate();
		  String[] shippingValues = null;
          if(value.contains("Carton")){
        	  shippingValues =  CommonUtility.getValuesOfArray(value, ":");
        	 value = shippingValues[1];
          } 
          shippingValues =  CommonUtility.getValuesOfArray(value, "/");
          String[] shippings = null;
          String temp;
          List<NumberOfItems> listOfNumberOfItems = null;
          List<Weight> listOfShippingWt = null;
          Dimensions shippingDimensions = null;
          for (String val : shippingValues) {
        	  if(val.contains("units")){
        		  shippings = CommonUtility.getValuesOfArray(val, "units");
        		  temp  = shippings[0].trim();
        		  listOfNumberOfItems = getShippingNumberOfItems(temp);
              } else if(val.contains("lbs")){
            	  shippings = CommonUtility.getValuesOfArray(val, "lbs");
            	  temp  = shippings[0].trim();
            	  listOfShippingWt = getShippingWeight(temp);
              } else{
            	  val = val.replaceAll("[^0-9.x ]", "");
            	  shippingDimensions = getShippingDimensions(val);
              }
		}
          shippingEstimateObj.setNumberOfItems(listOfNumberOfItems);
          shippingEstimateObj.setWeight(listOfShippingWt);
          shippingEstimateObj.setDimensions(shippingDimensions);
		  return shippingEstimateObj;
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
		  String[] vals = val.split("x");
		  Dimensions dimensionsObj = new Dimensions();
		  dimensionsObj.setLength(vals[0].trim());
		  dimensionsObj.setWidth(vals[1].trim());
		  dimensionsObj.setHeight(vals[2].trim());
		  dimensionsObj.setLengthUnit("in");
		  dimensionsObj.setWidth("in");
		  dimensionsObj.setHeightUnit("in");
		  return dimensionsObj;
	  }
	  private List<Option> getOptions(String optionName,String optionType,List<String> optionValues,String additionalInfo){
		  ////Option Name = Decorative Edge Options, Option Values = Wave, Swirl, Diamond.
		  Option optionObj=new Option();
		  List<OptionValue> valuesList=new ArrayList<OptionValue>();
		  OptionValue optionValueObj=null;
		  List<Option> listOfOptins = new ArrayList<>();
		  for (String optionVal : optionValues) {
			  optionValueObj = new OptionValue();
			  optionValueObj.setValue(optionVal);
			  valuesList.add(optionValueObj);
		}
		  optionObj.setName(optionName);
		  optionObj.setOptionType(optionType);
		  optionObj.setValues(valuesList);
		  optionObj.setCanOnlyOrderOne(ApplicationConstants.CONST_BOOLEAN_FALSE);
		  optionObj.setRequiredForOrder(ApplicationConstants.CONST_BOOLEAN_FALSE);
		  optionObj.setAdditionalInformation(additionalInfo);
		  listOfOptins.add(optionObj);
		  return listOfOptins;
		  
	  }
	  private List<AdditionalColor> getAdditionalColorVal(String data){
		  List<AdditionalColor> listOfAdditinalColor =new ArrayList<>();
		  AdditionalColor additiColorObj = new AdditionalColor();
		  additiColorObj.setName(data);
		  listOfAdditinalColor.add(additiColorObj);
		  return listOfAdditinalColor;
	  }
	  private List<AdditionalLocation>  getAdditionalLocationVal(String data){
		  List<AdditionalLocation> listOfAdditinalColor =new ArrayList<>();
		  AdditionalLocation additiLocObj = new AdditionalLocation();
		  additiLocObj.setName(data);
		  listOfAdditinalColor.add(additiLocObj);
		  return listOfAdditinalColor;
	  }
	  private List<ImprintLocation>  getImprintLocationVal(String data,List<ImprintLocation> listOfImprintLoc){
		  ImprintLocation imprintLocation = new ImprintLocation();
		  imprintLocation.setValue(data);
		  listOfImprintLoc.add(imprintLocation);
		  return listOfImprintLoc;
	  }
	  private List<Packaging> getProductPackaging(String data,List<Packaging> listOfPackaging){
		  Packaging packObj = new Packaging();
		  packObj.setName(data);
		  listOfPackaging.add(packObj);
		  return listOfPackaging;
	  }
	  public BamPriceGridParser getBamPriceGridParser() {
			return bamPriceGridParser;
		}

		public void setBamPriceGridParser(BamPriceGridParser bamPriceGridParser) {
			this.bamPriceGridParser = bamPriceGridParser;
		}

}
