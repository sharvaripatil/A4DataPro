package parser.InternationalMerchConcepts;

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
import com.a4tech.util.LookupData;

public class MerchPriceGridParser {
	enum options{
		Shipping,Product,Impirnt
	}

	private Logger              _LOGGER              = Logger.getLogger(getClass());
	public List<PriceGrid> getBasePriceGrid(String listOfNetCost,
		    String listOfQuan, String discountCode,
			String currency, String priceInclude, boolean isBasePrice,
			boolean qurFlag, String priceName, String criterias,
			List<PriceGrid> existingPriceGrid,String priceUnitName,String optionName,String productNumber) {
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
		priceGrid.setDescription(CommonUtility.getStringLimitedChars(priceName, 100));
		priceInclude = CommonUtility.getStringLimitedChars(priceInclude, 100);
		priceGrid.setPriceIncludes(priceInclude);
		priceGrid.setIsQUR(qurFlag);
			if (!priceGrid.getIsQUR() && !CommonUtility.isdescending(pricesForNetCost)) {
				priceGrid.setIsQUR(ApplicationConstants.CONST_BOOLEAN_TRUE);
			}
		priceGrid.setIsBasePrice(isBasePrice);
		priceGrid.setSequence(sequence);
		priceGrid.setProductNumber(productNumber);
		List<Price> listOfPrice = null;
		if (!priceGrid.getIsQUR()) {
			listOfPrice = getPrices(pricesForNetCost, listOfQuans, discountCodes,priceUnitName);
		} else {
			listOfPrice = new ArrayList<Price>();
		}
		priceGrid.setPrices(listOfPrice);
		if (criterias != null && !criterias.isEmpty()) {
			String[] criteriaVals = criterias.split(":");
			configuration = getConfigurations(criteriaVals[0],criteriaVals[1],optionName);
			//configuration = getConfigurations(criterias,priceName,optionName);
		}
		if(!CollectionUtils.isEmpty(configuration)){
			priceGrid.setPriceConfigurations(configuration);
		}
		existingPriceGrid.add(priceGrid);
		}catch(Exception e){
			_LOGGER.error("Error while processing PriceGrid: "+e.getMessage());
		}
		return existingPriceGrid;

	}

	public List<Price> getPrices(String[] prices, String[] quantity, String[] disCodes,String priceUnitName) {

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
				price.setDiscountCode(disCodes[PriceNumber]);
			}catch(ArrayIndexOutOfBoundsException exce){
				price.setDiscountCode("R");
				_LOGGER.error("Invalid Discount code,Set default discount code:R");
			}
			if(priceUnitName.equalsIgnoreCase("dozen")){
				priceUnit.setName("Dozen");
				priceUnit.setItemsPerUnit("12");
			} else{
				priceUnit
				.setItemsPerUnit(ApplicationConstants.CONST_STRING_VALUE_ONE);
			}
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
			if(value.contains(ApplicationConstants.CONST_STRING_COMMA_SEP)){
				String[] configValues = value.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
				for (String Value : configValues) {
					configs = new PriceConfiguration();
					configs = new PriceConfiguration();
					configs.setCriteria(criterias);
					configs.setValue(Arrays.asList((Object) Value));
					if(!StringUtils.isEmpty(optionName)){
						configs.setOptionName(optionName);
					}
					priceConfiguration.add(configs);
				}
			}else{
				configs = new PriceConfiguration();
				configs.setCriteria(criterias);
				configs.setValue(Arrays.asList((Object) value));
				if(!StringUtils.isEmpty(optionName)){
					configs.setOptionName(optionName);
				}
				priceConfiguration.add(configs);
			}
		}catch(Exception e){
			_LOGGER.error("Error while processing Upcharge PriceGrid: "+e.getMessage());
		}
		return priceConfiguration;
	}
	public List<PriceGrid> getUpchargePriceGrid(String quantity, String prices,
			String discounts, String upChargeCriterias, boolean qurFlag,
			String currency,String priceInclude,String upChargeValue, String upChargeType,
			String upchargeUsageType, Integer upChargeSequence,
			List<PriceGrid> existingPriceGrid,String optionName,String priceUnitName) {
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
		if(upChargeValue.contains("|")){
			upChargeValue = upChargeValue.replaceAll("\\|", ",");
		}
		if(upChargeValue.equals("Art Services")){
			priceGrid.setDescription("Logo Modification");
		} else {
			priceGrid.setDescription(upChargeValue);	
		}
		
		priceGrid.setIsQUR(qurFlag);
		priceGrid.setIsBasePrice(ApplicationConstants.CONST_BOOLEAN_FALSE);
		priceGrid.setSequence(upChargeSequence);
		priceGrid.setUpchargeType(upChargeType);
		priceGrid.setUpchargeUsageType(upchargeUsageType);
		priceGrid.setServiceCharge("Optional");
		priceGrid.setPriceIncludes(priceInclude);
		List<Price> listOfPrice = null;
		if (!priceGrid.getIsQUR()) {
			listOfPrice = getPrices(upChargePrices, upChargeQuantity, upChargeDiscounts,priceUnitName);
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
