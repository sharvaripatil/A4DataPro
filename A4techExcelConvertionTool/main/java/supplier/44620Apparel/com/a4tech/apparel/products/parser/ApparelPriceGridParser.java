package com.a4tech.apparel.products.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.Price;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.PriceUnit;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.LookupData;

public class ApparelPriceGridParser {

	private Logger              _LOGGER              = Logger.getLogger(getClass());
	public List<PriceGrid> getPriceGrids(String listOfNetCost,
		    String listOfQuan, String discountCode,
			String currency, String priceInclude, boolean isBasePrice,
			String qurFlag, String priceName, String criterias,
			List<PriceGrid> existingPriceGrid) {
		_LOGGER.info("Enter Price Grid Parser class");
		try{
		Integer sequence = 1;
		PriceGrid priceGrid = new PriceGrid();
		String[] pricesForNetCost = listOfNetCost
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] listOfQuans = listOfQuan
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		/*String[] discountCode = discountCodes
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);*/
		
		priceGrid.setCurrency(currency);
		priceGrid.setDescription(priceName);
		priceGrid.setPriceIncludes(priceInclude);
		priceGrid
				.setIsQUR(qurFlag.equals("n") ? ApplicationConstants.CONST_BOOLEAN_FALSE
						: ApplicationConstants.CONST_BOOLEAN_TRUE);
		priceGrid.setIsBasePrice(isBasePrice);
		priceGrid.setSequence(sequence);
		List<Price> listOfPrice = null;
		if (!priceGrid.getIsQUR()) {
			listOfPrice = getPrices(pricesForNetCost, listOfQuans, discountCode);
		} else {
			listOfPrice = new ArrayList<Price>();
		}
		priceGrid.setPrices(listOfPrice);
		if (criterias != null && !criterias.isEmpty()) {
			//configuration = getConfigurations(criterias);
		}
		//priceGrid.setPriceConfigurations(configuration);
		existingPriceGrid.add(priceGrid);
		}catch(Exception e){
			_LOGGER.error("Error while processing PriceGrid: "+e.getMessage());
		}
		return existingPriceGrid;

	}

	public List<Price> getPrices(String[] prices, String[] quantity, String disCodes) {

		List<Price> listOfPrices = new ArrayList<Price>();
	try{
		for (int PriceNumber = 0, sequenceNum = 1; PriceNumber < prices.length && PriceNumber < quantity.length; 
				                                                             PriceNumber++, sequenceNum++) {
            if(StringUtils.isEmpty(prices[PriceNumber])){
            	continue;
            }
			Price price = new Price();
			PriceUnit priceUnit = new PriceUnit();
			price.setSequence(sequenceNum);
			try {
				price.setQty(Integer.valueOf(quantity[PriceNumber]));
			} catch (NumberFormatException nfe) {
				price.setQty(ApplicationConstants.CONST_NUMBER_ZERO);
			}
			price.setNetCost(prices[PriceNumber]);
			//price.setPrice(prices[PriceNumber]);
			price.setDiscountCode(disCodes);
			priceUnit
					.setItemsPerUnit(ApplicationConstants.CONST_STRING_VALUE_ONE);
			price.setPriceUnit(priceUnit);
			listOfPrices.add(price);
		}
	}catch(Exception e){
		_LOGGER.error("Error while processing prices: "+e.getMessage());
	}
		return listOfPrices;
  }

	public List<PriceConfiguration> getConfigurations(String criterias,String value) {
		List<PriceConfiguration> priceConfiguration = new ArrayList<PriceConfiguration>();
		String[] config = null;
		PriceConfiguration configs = null;
		try{
		if (criterias
				.contains(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID)) {
			String[] configuraions = criterias
					.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			for (String criteria : configuraions) {
				PriceConfiguration configuraion = new PriceConfiguration();
				config = criteria.split(ApplicationConstants.CONST_DELIMITER_COLON);
				String criteriaValue = LookupData.getCriteriaValue(config[0]);
				configuraion.setCriteria(criteriaValue);
				if (config[1].contains(ApplicationConstants.CONST_STRING_COMMA_SEP)) {
					String[] values = config[1].split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					for (String Value : values) {
						configs = new PriceConfiguration();
						configs.setCriteria(criteriaValue);
						configs.setValue(Arrays.asList((Object) Value));
						priceConfiguration.add(configs);
					}
				} else {
					configs = new PriceConfiguration();
					configs.setCriteria(criteriaValue);
					configs.setValue(Arrays.asList((Object) config[1]));
					priceConfiguration.add(configs);
				}
			}

		} else {
			configs = new PriceConfiguration();
			config = criterias.split(ApplicationConstants.CONST_DELIMITER_COLON);
			try{
				configs.setCriteria(config[0]);
			}catch(ArrayIndexOutOfBoundsException aie){
				_LOGGER.error("Error while processing priceconfiguration" + aie.getMessage());
			}
			
			//configs.setValue(Arrays.asList((Object) config[1]));
			configs.setValue(Arrays.asList((Object) value));
			priceConfiguration.add(configs);
		}
		}catch(Exception e){
			_LOGGER.error("Error while processing PriceGrid: "+e.getMessage());
		}
		return priceConfiguration;
	}

	public List<PriceGrid> getUpchargePriceGrid(String quantity, String prices,
			String discounts, String upChargeCriterias, String qurFlag,
			String currency, String upChargeValue, String upChargeType,
			String upchargeUsageType, Integer upChargeSequence,
			List<PriceGrid> existingPriceGrid) {
		try{
		List<PriceConfiguration> configuration = null;
		PriceGrid priceGrid = new PriceGrid();
		String[] upChargePrices = prices
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] upChargeQuantity = quantity
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

		priceGrid.setCurrency(currency);
		priceGrid.setDescription(upChargeValue);
		priceGrid
				.setIsQUR((qurFlag.equalsIgnoreCase("N")) ? ApplicationConstants.CONST_BOOLEAN_FALSE
						: ApplicationConstants.CONST_BOOLEAN_TRUE);
		priceGrid.setIsBasePrice(ApplicationConstants.CONST_BOOLEAN_FALSE);
		priceGrid.setSequence(upChargeSequence);
		priceGrid.setUpchargeType(upChargeType);
		priceGrid.setUpchargeUsageType("Other");
		priceGrid.setServiceCharge("Optional");
		List<Price> listOfPrice = null;
		if (!priceGrid.getIsQUR()) {
			listOfPrice = getPrices(upChargePrices, upChargeQuantity, discounts);
		} else {
			listOfPrice = new ArrayList<Price>();
		}

		priceGrid.setPrices(listOfPrice);
		if (upChargeCriterias != null && !upChargeCriterias.isEmpty()) {
			configuration = getConfigurations(upChargeCriterias,upChargeValue);
		}
		priceGrid.setPriceConfigurations(configuration);
		existingPriceGrid.add(priceGrid);
		}catch(Exception e){
			_LOGGER.error("Error while processing UpchargePriceGrid: "+e.getMessage());
		}
		return existingPriceGrid;
	}
	
	public static String getPriceQuantity(String value){
		if(!StringUtils.isEmpty(value)){
			String[] quantis = value.split(ApplicationConstants.CONST_DELIMITER_HYPHEN);
			return quantis[ApplicationConstants.CONST_INT_VALUE_ONE];
		}
		return "";
	}

}
