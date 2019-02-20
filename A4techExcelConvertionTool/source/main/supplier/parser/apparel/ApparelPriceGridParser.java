package parser.apparel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.Price;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.PriceUnit;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
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
		List<PriceConfiguration> configuration = null;
		String[] pricesForNetCost = listOfNetCost
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] listOfQuans = listOfQuan
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

		
		priceGrid.setCurrency(currency);
		priceGrid.setDescription(priceName);
		priceGrid.setPriceIncludes(priceInclude);
		priceGrid
				.setIsQUR(qurFlag.equals("n") ? ApplicationConstants.CONST_BOOLEAN_FALSE
						: ApplicationConstants.CONST_BOOLEAN_TRUE);
		if(!CommonUtility.isdescending(pricesForNetCost))
		{
			priceGrid.setIsQUR(ApplicationConstants.CONST_BOOLEAN_TRUE);
		
		}
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
			configuration = getBasePriceConfigurations(criterias);
		}
		priceGrid.setPriceConfigurations(configuration);
		existingPriceGrid.add(priceGrid);
		}catch(Exception e){
			_LOGGER.error("Error while processing PriceGrid: "+e.getMessage());
		}
		return existingPriceGrid;

	}

	public List<Price> getPrices(String[] prices, String[] quantity, String disCodes) {

		List<Price> listOfPrices = new ArrayList<Price>();
	try{
		for (int PriceNumber = ApplicationConstants.CONST_NUMBER_ZERO, sequenceNum = ApplicationConstants.CONST_INT_VALUE_ONE; PriceNumber < prices.length && PriceNumber < quantity.length; 
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
				configs.setCriteria(config[ApplicationConstants.CONST_NUMBER_ZERO]);
			}catch(ArrayIndexOutOfBoundsException aie){
				_LOGGER.error("Error while processing priceconfiguration" + aie.getMessage());
			}
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
				.setIsQUR((qurFlag.equalsIgnoreCase(ApplicationConstants.CONST_CHAR_N)) ? ApplicationConstants.CONST_BOOLEAN_FALSE
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
		return ApplicationConstants.CONST_STRING_EMPTY;
	}
	
	public List<PriceGrid> sizePrices(Map<String, String> sizesData,List<PriceGrid> existingPriceGrid){
		String price = ApplicationConstants.CONST_STRING_EMPTY;
		int firstSize = 1;
		String sizeValues = ApplicationConstants.CONST_STRING_EMPTY;
		if (isSingleBasePrice(sizesData.values())) {
			Set<String> basePriceName = sizesData.keySet();
			Collection<String> priceVals = sizesData.values();
			String finalpriceNames = String.join(ApplicationConstants.CONST_STRING_COMMA_SEP, basePriceName);
			String priceVal = priceVals.iterator().next();
			existingPriceGrid = sizePrices(ApplicationConstants.CONST_STRING_EMPTY,finalpriceNames, priceVal, existingPriceGrid);
		} else {
			for (Map.Entry<String, String> sizeEntry : sizesData.entrySet()) {
				String key = sizeEntry.getKey();
				String val = sizeEntry.getValue();
				if (firstSize == 1) {
					price = val;
					sizeValues = key;
				} else {
					if (price.equalsIgnoreCase(val)) {
						sizeValues = sizeValues + ApplicationConstants.CONST_STRING_COMMA_SEP + key;
					} else {
						if (!StringUtils.isEmpty(sizeValues)) {
							existingPriceGrid = sizePrices(sizeValues, sizeValues,price, existingPriceGrid);
						}
						sizeValues = key;
						price = val;
					}
				}

				firstSize++;
			}
			if (!StringUtils.isEmpty(sizeValues)) {
				existingPriceGrid = sizePrices(sizeValues, sizeValues,price, existingPriceGrid);
			}
		}
		return existingPriceGrid;

	}
	
	private List<PriceGrid> sizePrices(String criteriaValues,String basePriceName,String price,List<PriceGrid> existingPriceGrid){
		String[] prices = price.split("%%%");
		String listOfPrices = prices[0];
		String listOfQuantity = prices[1];
		existingPriceGrid = getPriceGrids(listOfPrices.toString(), 
				listOfQuantity.toString(), "P", "USD",
				         ApplicationConstants.CONST_STRING_EMPTY, true, "n", basePriceName,criteriaValues,existingPriceGrid);
		return existingPriceGrid;
	}
	
	public List<PriceConfiguration> getBasePriceConfigurations(String criteria) {
		List<PriceConfiguration> listOfpriceConfiguration = new ArrayList<PriceConfiguration>();
		PriceConfiguration configuraion = null;
		String[] criterias = criteria.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		
		try{
			for (String CriteriaVal : criterias) {
				configuraion = new PriceConfiguration();
				configuraion.setCriteria("Size");
				configuraion.setValue(Arrays.asList(CriteriaVal));
				listOfpriceConfiguration.add(configuraion);
			}
		
		}catch(Exception e){
			_LOGGER.error("Error while base price configuration create: "+e.getMessage());
		}
		return listOfpriceConfiguration;
	}
	private boolean isSingleBasePrice(Collection<String> prices){
		int priceIndex = ApplicationConstants.CONST_INT_VALUE_ONE;
		String firstPrice = ApplicationConstants.CONST_STRING_EMPTY;
		for (String priceVal : prices) {
			   if(priceIndex == ApplicationConstants.CONST_INT_VALUE_ONE){
				   firstPrice = priceVal;
			   }
			   if(!firstPrice.equalsIgnoreCase(priceVal)){
				   return false;
			   }
			
			priceIndex++;
		}
		
		return true;
	}

}
