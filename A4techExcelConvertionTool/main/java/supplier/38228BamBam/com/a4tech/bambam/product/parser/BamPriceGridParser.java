package com.a4tech.bambam.product.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.Price;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.PriceUnit;
import com.a4tech.product.model.Value;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.a4tech.util.LookupData;

public class BamPriceGridParser {
	
	private final static Logger    _LOGGER    = Logger.getLogger(BamPriceGridParser.class);
	public List<PriceGrid> getBasePriceGrids(String listOfPrices,String listOfQuan,String listOfDisc ,String currency,String priceInclude,
			                 boolean isBasePrice ,String isQur,String priceName,String criterias ,List<PriceGrid> existingPriceGrid){
		try{
		Integer sequence =1;
		List<PriceConfiguration> configuration = null ;
		PriceGrid priceGrid = new PriceGrid();
		String[] prices = listOfPrices.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] quantity = listOfQuan.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] discount = listOfDisc.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		
		
		priceGrid.setCurrency(currency);
		priceName = getProductonRushVal(priceName, criterias);
		priceGrid.setDescription(priceName);
		priceGrid.setIsQUR(isQur.equalsIgnoreCase("Y")?ApplicationConstants.CONST_BOOLEAN_TRUE:ApplicationConstants.CONST_BOOLEAN_FALSE);
		priceGrid.setIsBasePrice(isBasePrice);
		priceGrid.setSequence(sequence);
		List<Price> listOfPrice = null;
		if(!priceGrid.getIsQUR()){
			listOfPrice = getPrices(prices,quantity,discount);
		}else{
			listOfPrice = new ArrayList<Price>();
		}
		priceGrid.setPrices(listOfPrice);
		if(criterias != null && !criterias.isEmpty()){
		   configuration = getConfigurations(criterias);
		}
		priceGrid.setPriceConfigurations(configuration);
		existingPriceGrid.add(priceGrid);
	}catch (Exception e){
		_LOGGER.error("Error creating price grid: "+e.getMessage());
	}
		return existingPriceGrid; 
		
	}
	
	public List<Price> getPrices(String[] prices, String[] quantity , String[] discount){
		List<Price> listOfPrices = new ArrayList<Price>();
		 try{
		  
		   for(int i=0,j=1;i<prices.length && i<quantity.length&&i<discount.length;i++,j++){
			 
			   Price price = new Price();
			 PriceUnit priceUnit = new PriceUnit();
			 price.setSequence(j);
			 try{
				 price.setQty(Integer.valueOf(quantity[i])); 
			 }catch(NumberFormatException nfe){
				 price.setQty(0);
			 }
			 price.setPrice(prices[i]);
			 price.setDiscountCode(discount[i]);
			 priceUnit.setItemsPerUnit(ApplicationConstants.CONST_STRING_VALUE_ONE);
			 price.setPriceUnit(priceUnit);
			 listOfPrices.add(price);
		   }
	}catch (Exception e){
		_LOGGER.error("Error creating prices: "+e.getMessage());
	}
		return listOfPrices;
	}
	
	public List<PriceConfiguration> getConfigurations(String criterias){//ADLN:[Mold: 1 side medallion]	
		boolean optionFlag = false;
		boolean timeFlag  = false;
		List<PriceConfiguration> listOfpriceConfiguration = new ArrayList<>();
		try{
		String[] config =null;
		PriceConfiguration configs = null;
		if(criterias.contains(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID)){
			String[] configuraions = criterias.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			for (String criteria : configuraions) {
				configs = new PriceConfiguration();
		        config = criteria.split(":",ApplicationConstants.CONST_INT_VALUE_TWO);//DIMS:Length:1 1/4:in
				String criteriaValue = LookupData.getCriteriaValue(config[0]);
				if(isOption(config[0])){
					optionFlag = ApplicationConstants.CONST_BOOLEAN_TRUE;
				}else{ 
					optionFlag = ApplicationConstants.CONST_BOOLEAN_FALSE;
				}
				if(isProductionOrRush(config[0])){
					timeFlag = ApplicationConstants.CONST_BOOLEAN_TRUE;
				}else{
					timeFlag = ApplicationConstants.CONST_BOOLEAN_FALSE;
				}
				if(config[0].equalsIgnoreCase("DIMS")){
					criteriaValue = "Size";
				}
				//configuraion.setCriteria(criteriaValue);
				if(config[0].equalsIgnoreCase("LMIN")){
					config[1] = "Can order less than minimum";
				}
				if(criteriaValue.equalsIgnoreCase("Size")){
					configs = getSizeConfiguration(criteria);
					listOfpriceConfiguration.add(configs);
				} else if(config[1].contains(",")){
					String[] values = config[1].split(",");
					for (String Value : values) {
						if(timeFlag){
							Value = Value.concat(" ").concat("business days");
						}
						configs = new PriceConfiguration();
						configs.setCriteria(criteriaValue);
						if(Value.contains(ApplicationConstants.SQUARE_BRACKET_OPEN)){
							Value = CommonUtility.removeCurlyBraces(Value);
						}
						configs.setValue(Arrays.asList((Object)Value));
						listOfpriceConfiguration.add(configs);
					}					
				}else{
					configs = new PriceConfiguration();
					configs.setCriteria(criteriaValue);
					if(optionFlag){
						String optionName = config[1];
						String optionValue = config[2];
						if(optionName.contains(ApplicationConstants.SQUARE_BRACKET_OPEN)){
							optionName = CommonUtility.removeCurlyBraces(optionName);
						}
						String[] optValues = CommonUtility.getValuesOfArray(optionValue, ApplicationConstants.CONST_STRING_COMMA_SEP);
						for (String optVal : optValues) {
							configs = new PriceConfiguration();
							configs.setCriteria(criteriaValue);
							configs.setOptionName(optionName);
							if(optVal.contains(ApplicationConstants.SQUARE_BRACKET_OPEN)){
								optVal = CommonUtility.removeCurlyBraces(optVal);
							}
							configs.setValue(Arrays.asList((Object)optVal));
							listOfpriceConfiguration.add(configs);
						}
						/*if(optionName.contains(ApplicationConstants.SQUARE_BRACKET_OPEN)){
							optionName = CommonUtility.removeCurlyBraces(optionName);
						}
						if(optionValue.contains(ApplicationConstants.SQUARE_BRACKET_OPEN)){
							optionValue = CommonUtility.removeCurlyBraces(optionValue);
						}
						if(optionValue.contains(ApplicationConstants.CONST_STRING_COMMA_SEP)){
							List<Object> listOfValues = new ArrayList<>();
							Object obj = null;
							String[] optValues = CommonUtility.getValuesOfArray(optionValue, ApplicationConstants.CONST_STRING_COMMA_SEP);
							for (String optVal : optValues) {
								obj = new Object();
								listOfValues.add(optVal);
							}
							configs.setValue(listOfValues);
						}else{
							configs.setValue(Arrays.asList((Object)optionValue));
						}
						configs.setOptionName(optionName);*/
						
					}else{
						String val = config[1];
						if(timeFlag){
							val = val.concat(" ").concat("business days");
						}
						if(val.contains(ApplicationConstants.SQUARE_BRACKET_OPEN)){
							val = CommonUtility.removeCurlyBraces(val);
						}
						configs.setValue(Arrays.asList((Object)val));
					}
					listOfpriceConfiguration.add(configs);
				}
			}
			
		}else{
			configs = new PriceConfiguration();
			config = criterias.split(":");
			String criteriaValue = LookupData.getCriteriaValue(config[0]);
			configs.setCriteria(criteriaValue);
			configs.setValue(Arrays.asList((Object)config[1]));
			listOfpriceConfiguration.add(configs);
		}
		}catch(Exception e){
			_LOGGER.error("Error creating price configuration: "+e.getMessage());
		}
		return listOfpriceConfiguration;
	}
	
	public List<PriceGrid> getUpchargePriceGrid(String quantity,String prices ,String discounts, String upChargeCriterias , String qurFlag,String currency
			                           ,String upChargeName, String upChargeType,String upchargeUsageType,Integer upChargeSequence ,String serviceCharge ,List<PriceGrid> existingPriceGrid){
		try{
		List<PriceConfiguration> configuration = null ;
		PriceGrid priceGrid = new PriceGrid();
		String[] upChargePrices = prices.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] upChargeQuantity = quantity.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] upChargeDiscount = discounts.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		
		
		priceGrid.setCurrency(currency);
		priceGrid.setDescription(upChargeName);
		priceGrid.setIsQUR((qurFlag.trim().equalsIgnoreCase("Y"))?ApplicationConstants.CONST_BOOLEAN_TRUE: ApplicationConstants.CONST_BOOLEAN_FALSE);
		priceGrid.setIsBasePrice(ApplicationConstants.CONST_BOOLEAN_FALSE);
		priceGrid.setSequence(upChargeSequence);
		priceGrid.setUpchargeType(upChargeType);
		priceGrid.setUpchargeUsageType(upchargeUsageType);
		priceGrid.setServiceCharge(serviceCharge);
		List<Price> listOfPrice = null;
		if(!priceGrid.getIsQUR()){
			 listOfPrice = getPrices(upChargePrices,upChargeQuantity,upChargeDiscount);
		}else{
			listOfPrice = new ArrayList<Price>();
		}
		
		priceGrid.setPrices(listOfPrice);
		if(upChargeCriterias != null && !upChargeCriterias.isEmpty()){
		   configuration = getConfigurations(upChargeCriterias);
		}
		priceGrid.setPriceConfigurations(configuration);
		existingPriceGrid.add(priceGrid);
	}catch(Exception e){
		_LOGGER.error("Error creating upcharge price grid: "+e.getMessage());
	}
		return existingPriceGrid;
	}
	
	public boolean isOption(String value){
		if("IMOP".equals(value) || "PROP".equals(value) || "SHOP".equals(value)){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	
	public String getProductonRushVal(String basePriceName,String criteriaValue){
		if (!StringUtils.isEmpty(criteriaValue)) {
			if (criteriaValue.contains("RUSH")
					|| (criteriaValue.contains("PRTM"))) {
				basePriceName = CommonUtility.appendStrings(basePriceName,
						ApplicationConstants.CONST_STRING_BUSINESS_DAYS,
						ApplicationConstants.CONST_VALUE_TYPE_SPACE);
				return basePriceName;
			}	
		}
		return basePriceName;
	}
	
	public boolean isProductionOrRush(String value){
		if(value.equalsIgnoreCase("RUSH") || value.equalsIgnoreCase("PRTM")){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	
	public PriceConfiguration getSizeConfiguration(String configVal){
		  PriceConfiguration configObj = new PriceConfiguration();
		  configVal = configVal.replace("DIMS:", "");
		  String[] sizeVal = configVal.split(ApplicationConstants.CONST_DELIMITER_SEMICOLON);
		  Value valObj = null;
		  List<Object> listOfSize = new ArrayList<>();
		  for (String val : sizeVal) {
			   valObj = new Value();
			     String[] sVals = val.split(ApplicationConstants.CONST_DELIMITER_COLON);
			     valObj.setAttribute(sVals[0]);//DIMS:Length:2 1/4:in	
				 valObj.setValue(sVals[1]);
				 valObj.setUnit(sVals[2]);
				 listOfSize.add(valObj);
		}
		  configObj.setValue(listOfSize);
		  configObj.setCriteria("Size");
 		return configObj;
	}

}
