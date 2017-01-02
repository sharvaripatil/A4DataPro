package com.a4tech.bambam.product.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.util.ApplicationConstants;

public class BamPackagingParser {
	private static final Logger _LOGGER = Logger.getLogger(BamPackagingParser.class);
	
	public ProductConfigurations getPackagingAndShipping(String packValue,ProductConfigurations existingConfig,
			                                                                        boolean isShippingProcess){
		String[] packValues = null;
		List<Packaging> listOfPack = new ArrayList<>();
		ShippingEstimate shippingEstObj = null;
		try{
			if(packValue.contains(ApplicationConstants.SQUARE_BRACKET_OPEN)){
				if(packValue.contains(ApplicationConstants.CONST_DELIMITER_DOT)){
					//Packaged 100 sheets per polybag.2,[000 units/ctn - Carton Dimensions:  25 x 45 x 15 cm]
					packValues = packValue.split(ApplicationConstants.CONST_DELIMITER_DOT);
					for (String pakaData : packValues) {
						  if(pakaData.contains(ApplicationConstants.SQUARE_BRACKET_OPEN)){
							  if(isShippingProcess){
								  pakaData = pakaData.replaceAll("[,\\[]", "").replaceAll("\\]","");
								  //2000 units/ctn - Carton Dimensions:  29 x 27 x 19 cm
								  shippingEstObj = getShippingEstVals(pakaData);
								  existingConfig.setShippingEstimates(shippingEstObj);
							  } 
						  }else{
							  Packaging packOb = getPackageValObj(pakaData);
							  listOfPack.add(packOb);
							  existingConfig.setPackaging(listOfPack);
						  }
					}
					
				} else if(packValue.contains(ApplicationConstants.CONST_DELIMITER_COMMA)){
					//2,[000 units/ctn - Carton Dimensions:  29 x 27 x 19 cm]
				    //[200 units/ctn - Carton Dimensions:  29 x 27 x 19 cm],Poly Bag
					packValue = removeSpecialCharacters(packValue);
					packValues = packValue.split(ApplicationConstants.CONST_DELIMITER_COMMA);
					for (String packVal : packValues) {
						  if(packVal.contains("Carton")){
							  if(isShippingProcess){
								  shippingEstObj = getShippingEstVals(packVal);
								  existingConfig.setShippingEstimates(shippingEstObj);
							  } 
						  }else {
							  Packaging packOb = getPackageValObj(packVal);
							  listOfPack.add(packOb);
							  existingConfig.setPackaging(listOfPack);
						  }	  
					}
			    }else{
			    	if(isShippingProcess){
			    		packValue = packValue.replaceAll("[\\[]", "").replaceAll("\\]","");
				    	if(packValue.contains("Carton")){
				    		shippingEstObj = getShippingEstVals(packValue);
							existingConfig.setShippingEstimates(shippingEstObj);
				    	}	 
			    	}
			    }
			}else {
				boolean isShipping = isShipping(packValue);
				if(isShipping){
					if(isShippingProcess){
						String[] vals = packValue.split(ApplicationConstants.CONST_DELIMITER_HYPHEN);
						shippingEstObj = new ShippingEstimate();
						for (String shippingVal : vals) {
							 if(shippingVal.contains("cm")){
								 shippingVal = shippingVal.replace("cm", "");
								 String[] dimentions = shippingVal.split("x");
								Dimensions dimeObj = getDimentionVals(dimentions);
								shippingEstObj.setDimensions(dimeObj);
							 }else if(shippingVal.contains("pcs")){
								 String shiWt = packValue.split("pcs")[0];
								 List<NumberOfItems> listOfshippingItems =  getSgippingNumberOfItems(shiWt);
								 shippingEstObj.setNumberOfItems(listOfshippingItems);
							 }
						}
					}	
				}else{
					packValues = packValue.split(ApplicationConstants.CONST_DELIMITER_COLON);
					Packaging packagingObj = null;
					for (String packVals : packValues) {
						packagingObj = new Packaging();
						packagingObj.setName(packVals);
						listOfPack.add(packagingObj);	
					}
					existingConfig.setPackaging(listOfPack);
				}
				
			}
		}catch(Exception exce){
			_LOGGER.error("unable to parse Packaging and Shipping Estmation values::"+exce.getMessage());
		}
		
		return existingConfig;
		
	}

	private Packaging getPackageValObj(String packgName){
		Packaging packObj = new Packaging();
		packObj.setName(packgName);
		return packObj;
	}
	
	private ShippingEstimate getShippingEstVals(String shippingVal){
		ShippingEstimate shippingEstObj = new ShippingEstimate();
		//2000 units/ctn - Carton Dimensions:  29 x 27 x 19 cm
		try{
			String[] values = shippingVal.split(ApplicationConstants.CONST_DELIMITER_COLON);
			for (String value : values) {
				if(value.contains("units/ctn")){
					String[] vals = value.split("x");
					List<NumberOfItems> listOfItems = getSgippingNumberOfItems(vals[0]);
					shippingEstObj.setNumberOfItems(listOfItems);
				}else if(value.contains("cm")){
					value = value.replace("cm","");
					String[] vals = value.split("x");
					Dimensions dimvalues = getDimentionVals(vals);
					shippingEstObj.setDimensions(dimvalues);
				}else{
					
				}
			}
		}catch(Exception exce){
			_LOGGER.error("unable to parse shippingEstmate: "+exce.getMessage());
			return new ShippingEstimate();
		}
		
		return shippingEstObj;
	}
	private Dimensions getDimentionVals(String[] values){
		Dimensions dimensionObj = new Dimensions();
		for (int dimNo = 0; dimNo < values.length; dimNo++) {
			if (dimNo == 0) {
				dimensionObj.setLength(values[dimNo]);
				dimensionObj.setHeightUnit(ApplicationConstants.CONST_STRING_CENTI_METER);
			} else if (dimNo == 1) {
				dimensionObj.setWidth(values[dimNo]);
				dimensionObj.setLengthUnit(ApplicationConstants.CONST_STRING_CENTI_METER);
			} else if (dimNo == 2) {
				dimensionObj.setHeight(values[dimNo]);
				dimensionObj.setWidthUnit(ApplicationConstants.CONST_STRING_CENTI_METER);
			}

		}
		return dimensionObj;
	}
	
	private List<NumberOfItems> getSgippingNumberOfItems(String noOfItems){
		NumberOfItems numberOfItemObj = new NumberOfItems();
		List<NumberOfItems> listOfItems = new ArrayList<>();
		numberOfItemObj.setValue(noOfItems);
		numberOfItemObj.setUnit("Carton");
		listOfItems.add(numberOfItemObj);
		return listOfItems;
	}
	
	private String removeSpecialCharacters(String data){
		int commaIndex = data.indexOf(ApplicationConstants.CONST_STRING_COMMA_SEP);
		if(commaIndex >=5){
			data = data.replaceAll("\\[", "").replaceAll("\\]", "");
		}else{
			data = data.replaceAll("[,\\[]", "").replaceAll("\\]","");
		}
		return data;
	}
	private boolean isShipping(String value){
		if(value.contains("cm") || value.contains("carton") || value.contains("ctn")){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	
}
