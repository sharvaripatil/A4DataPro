package parser.evansManufacturing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.Price;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.PriceUnit;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class EveansManufacturePriceGridParser {

	private Logger              _LOGGER              = Logger.getLogger(getClass());
	public List<PriceGrid> getBasePriceGrid(String listOfNetCost,
		    String listOfQuan, String discountCode,
			String currency, String priceInclude, boolean isBasePrice,
			boolean qurFlag, String priceName, String criterias,
			List<PriceGrid> existingPriceGrid,String priceUnitName,String optionName) {
		_LOGGER.info("Enter Price Grid Parser class");
		try{
			if(CollectionUtils.isEmpty(existingPriceGrid)){
				existingPriceGrid = new ArrayList<>();
			}
			List<PriceConfiguration> configuration = new ArrayList<>();
		Integer sequence = 1;
		PriceGrid priceGrid = new PriceGrid();
		String[] pricesForNetCost = listOfNetCost
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] listOfQuans = listOfQuan
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] discountCodes = discountCode
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		
		priceGrid.setCurrency(currency);
		priceGrid.setDescription(priceName);
		priceInclude = CommonUtility.getStringLimitedChars(priceInclude, 100);
		priceGrid.setPriceIncludes(priceInclude);
		priceGrid.setIsQUR(qurFlag);
			if (!priceGrid.getIsQUR() && !CommonUtility.isdescending(pricesForNetCost)) {
				priceGrid.setIsQUR(ApplicationConstants.CONST_BOOLEAN_TRUE);
			}
				/*.setIsQUR(qurFlag.equals("n") ? ApplicationConstants.CONST_BOOLEAN_FALSE
						: ApplicationConstants.CONST_BOOLEAN_TRUE);*/
		priceGrid.setIsBasePrice(isBasePrice);
		priceGrid.setSequence(sequence);
		List<Price> listOfPrice = null;
		if (!priceGrid.getIsQUR()) {
			listOfPrice = getPrices(pricesForNetCost, listOfQuans, discountCodes);
		} else {
			listOfPrice = new ArrayList<Price>();
		}
		priceGrid.setPrices(listOfPrice);
		if (criterias != null && !criterias.isEmpty()) {
			String[] criteriaVals = criterias.split(":");
			configuration = getConfigurations(criteriaVals[0],criteriaVals[1],optionName);
		}
		priceGrid.setPriceConfigurations(configuration);
		existingPriceGrid.add(priceGrid);
		}catch(Exception e){
			_LOGGER.error("Error while processing PriceGrid: "+e.getMessage());
		}
		return existingPriceGrid;

	}

	public List<Price> getPrices(String[] prices, String[] quantity, String[] disCodes) {

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
				String quanty = quantity[PriceNumber];
				if(quanty.equals("0")){
					quanty = "1";
				}
				price.setQty(Integer.valueOf(quanty));
			} catch (NumberFormatException nfe) {
				price.setQty(ApplicationConstants.CONST_INT_VALUE_ONE);
				_LOGGER.error("Invalid Price Quantity Value: "+quantity[PriceNumber]);
			}
	         //price.setNetCost(prices[PriceNumber]);
			String listPrice = prices[PriceNumber];
			if(listPrice.contains("$")){
				listPrice = listPrice.replace("$", ApplicationConstants.CONST_STRING_EMPTY).trim();
			}
			price.setPrice(listPrice);
			try {
				if(StringUtils.isEmpty(disCodes[PriceNumber])){
					price.setDiscountCode("R");
				} else {
					price.setDiscountCode(disCodes[PriceNumber]);
				}
			}catch(ArrayIndexOutOfBoundsException exce){
				price.setDiscountCode("R");
				_LOGGER.error("Invalid Discount code,Set default discount code:R");
			}
			
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
	public List<PriceConfiguration> getConfigurations(String criterias,String value,String optionName) {
		List<PriceConfiguration> priceConfiguration = new ArrayList<PriceConfiguration>();
		PriceConfiguration configs = null;
		try{
			if(criterias.contains(ApplicationConstants.CONST_STRING_COMMA_SEP)){
				String[] configValues = criterias.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
				for (String Value : configValues) {
					configs = new PriceConfiguration();
					String[] configValue = Value.split(":");
					configs.setCriteria(configValue[0]);
					configs.setValue(Arrays.asList((Object) configValue[1]));
					if(!StringUtils.isEmpty(optionName)){
						configs.setOptionName(optionName);
					}
					priceConfiguration.add(configs);
				}
			}else{
				if(value.contains(",")){
					 if(value.contains("&")){
						 value = value.replaceAll("&", ",");
					 } else if(value.contains("and")){
						 value = value.replaceAll("and", ",");
					 }
					 String[] configVals = CommonUtility.getValuesOfArray(value, ",");
					 for (String configValue : configVals) {
						 configValue = configValue.trim();
						 configs = new PriceConfiguration();
							configs.setCriteria(criterias);
							configs.setValue(Arrays.asList((Object) configValue));
							if(!StringUtils.isEmpty(optionName)){
								configs.setOptionName(optionName);
							}
							priceConfiguration.add(configs);
					}
				} else {
					configs = new PriceConfiguration();
					configs.setCriteria(criterias);
					configs.setValue(Arrays.asList((Object) value));
					if(!StringUtils.isEmpty(optionName)){
						configs.setOptionName(optionName);
					}
					priceConfiguration.add(configs);	
				}
				
			}
		}catch(Exception e){
			_LOGGER.error("Error while processing Upcharge PriceGrid: "+e.getMessage());
		}
		return priceConfiguration;
	}
	public List<PriceGrid> getUpchargePriceGrid(String quantity, String prices,
			String discounts, String upChargeCriterias, boolean qurFlag,
			String currency, String upChargeValue, String upChargeType,
			String upchargeUsageType, Integer upChargeSequence,
			List<PriceGrid> existingPriceGrid,String optionName,String priceInclude,String serviceCharge) {
		try{
			if(CollectionUtils.isEmpty(existingPriceGrid)){
				existingPriceGrid = new ArrayList<>();
			}
		List<PriceConfiguration> configuration = null;
		PriceGrid priceGrid = new PriceGrid();
		String[] upChargePrices = prices
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] upChargeQuantity = quantity
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] upChargeDiscounts = discounts
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		priceGrid.setCurrency(currency);
		priceGrid.setDescription(upChargeValue);
		priceGrid.setIsQUR(qurFlag);
		priceGrid.setIsBasePrice(ApplicationConstants.CONST_BOOLEAN_FALSE);
		priceGrid.setSequence(upChargeSequence);
		priceGrid.setUpchargeType(upChargeType);
		priceGrid.setUpchargeUsageType(upchargeUsageType);
		priceGrid.setServiceCharge(serviceCharge);
		priceGrid.setPriceIncludes(priceInclude);
		List<Price> listOfPrice = null;
		if (!priceGrid.getIsQUR()) {
			listOfPrice = getPrices(upChargePrices, upChargeQuantity, upChargeDiscounts);
		} else {
			listOfPrice = new ArrayList<Price>();
		}

		priceGrid.setPrices(listOfPrice);
		if (upChargeCriterias != null && !upChargeCriterias.isEmpty()) {
			configuration = getConfigurations(upChargeCriterias,upChargeValue,optionName);
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
