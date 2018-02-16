package parser.goldbond;

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
import com.a4tech.util.CommonUtility;
import com.a4tech.util.LookupData;

public class GoldbondPriceGridParser {

	private Logger              _LOGGER              = Logger.getLogger(getClass());
	public List<PriceGrid> getBasePriceGrids(String listOfPrices,
		    String listOfQuan, String discountCodes,
			String currency, String priceInclude, boolean isBasePrice,
			boolean isQurFlag, String priceName, String criterias,
			List<PriceGrid> existingPriceGrid,String priceUnitName) {
		_LOGGER.info("Enter Price Grid Parser class");
		try{
		Integer sequence = 1;
		List<PriceConfiguration> configuration = null;
		PriceGrid priceGrid = new PriceGrid();
		String[] prices = listOfPrices
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] quantity = listOfQuan
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] discountCode = discountCodes
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		
		priceGrid.setCurrency(currency);
		priceGrid.setDescription(priceName);
		priceGrid.setPriceIncludes(CommonUtility.getStringLimitedChars(priceInclude, 100));
		priceGrid.setIsQUR(isQurFlag);
		if(StringUtils.isEmpty(listOfPrices)){
			priceGrid.setIsQUR(ApplicationConstants.CONST_BOOLEAN_TRUE);
		}
		if (!priceGrid.getIsQUR() && !CommonUtility.isdescending(prices)) {
			priceGrid.setIsQUR(ApplicationConstants.CONST_BOOLEAN_TRUE);
		}
		priceGrid.setIsBasePrice(isBasePrice);
		priceGrid.setSequence(sequence);
		List<Price> listOfPrice = null;
		if (!priceGrid.getIsQUR()) {
			listOfPrice = getPrices(prices, quantity, discountCode,"");
		} else {
			listOfPrice = new ArrayList<Price>();
			priceGrid.setPriceIncludes("");
		}
		priceGrid.setPrices(listOfPrice);
		if (criterias != null && !criterias.isEmpty()) {
			//configuration = getConfigurations(criterias);
		}
		//priceGrid.setPriceConfigurations(configuration);
		existingPriceGrid.add(priceGrid);
		}catch(Exception e){
			_LOGGER.error("Error while processing Base PriceGrid: "+e.getMessage());
		}
		return existingPriceGrid;

	}

	public List<Price> getPrices(String[] netPrices, String[] quantity, String[] disCodes,String priceUnitName) {

		List<Price> listOfPrices = new ArrayList<Price>();
	try{
		for (int PriceNumber = 0, sequenceNum = 1; PriceNumber < netPrices.length && PriceNumber < quantity.length
				      && PriceNumber < disCodes.length; PriceNumber++, sequenceNum++) {

			Price price = new Price();
			PriceUnit priceUnit = new PriceUnit();
			price.setSequence(sequenceNum);
			try {
				price.setQty(Integer.valueOf(quantity[PriceNumber]));
			} catch (NumberFormatException nfe) {
				price.setQty(ApplicationConstants.CONST_NUMBER_ZERO);
			}
			//price.setNetCost(netPrices[PriceNumber]);
			price.setPrice(netPrices[PriceNumber]);
			price.setDiscountCode(disCodes[PriceNumber]);
			if(priceUnitName.equalsIgnoreCase("dozen")){
				priceUnit.setName("Dozen");
				priceUnit.setItemsPerUnit("12");
			} else if(priceUnitName.equalsIgnoreCase("case")){
				priceUnit.setName("Case");
				priceUnit.setItemsPerUnit("16");
			}
			else{
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
			String currency,String priceInclude, String upChargeValue, String upChargeType,
			String upchargeUsageType, Integer upChargeSequence,
			List<PriceGrid> existingPriceGrid,String optionName,String priceUnitName) {
		try{
		List<PriceConfiguration> configuration = null;
		PriceGrid priceGrid = new PriceGrid();
		String[] upChargePrices = prices
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] upChargeQuantity = quantity
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] upChargeDiscount = discounts
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

		priceGrid.setCurrency(currency);
		priceGrid.setDescription(upChargeValue);
		priceGrid.setIsQUR(qurFlag);
		priceGrid.setIsBasePrice(ApplicationConstants.CONST_BOOLEAN_FALSE);
		priceGrid.setSequence(upChargeSequence);
		priceGrid.setUpchargeType(upChargeType);
		priceGrid.setUpchargeUsageType(upchargeUsageType);
		priceGrid.setServiceCharge("Required");
		priceGrid.setPriceIncludes(CommonUtility.getStringLimitedChars(priceInclude, 100));
		List<Price> listOfPrice = null;
		if (!priceGrid.getIsQUR() && !CommonUtility.isdescending(upChargePrices)) {
			priceGrid.setIsQUR(ApplicationConstants.CONST_BOOLEAN_TRUE);
		}
		if (!priceGrid.getIsQUR()) {
			listOfPrice = getPrices(upChargePrices, upChargeQuantity, upChargeDiscount,priceUnitName);
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

}
