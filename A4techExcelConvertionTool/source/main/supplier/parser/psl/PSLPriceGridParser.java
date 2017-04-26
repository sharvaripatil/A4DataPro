package parser.psl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.Price;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.PriceUnit;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.LookupData;

public class PSLPriceGridParser {

	private Logger              _LOGGER              = Logger.getLogger(getClass());
	public List<PriceGrid> getPriceGrids(String listOfPrices,
		    String listOfQuan, String listOfDisc,
			String currency, String priceInclude, boolean isBasePrice,
			String isQur, String priceName, String criterias/*,
			List<PriceGrid> existingPriceGrid*/) {

		Integer sequence = 1;
		List<PriceGrid> priceGridsList = new ArrayList<PriceGrid>();

		PriceGrid priceGrid = new PriceGrid();
		String[] prices = listOfPrices
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] quantity = listOfQuan
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] discount = listOfDisc
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		
		

	priceGrid.setIsQUR(isQur.equalsIgnoreCase(ApplicationConstants.CONST_CHAR_Y) ? ApplicationConstants.CONST_BOOLEAN_TRUE
					: ApplicationConstants.CONST_BOOLEAN_FALSE);
		
		

		priceGrid.setCurrency(currency);
		priceGrid.setDescription("");
		priceGrid.setIsBasePrice(isBasePrice);
		priceGrid.setSequence(sequence);
		priceGrid.setPriceIncludes(priceInclude);
		List<Price> listOfPrice = null;
     	if (!priceGrid.getIsQUR()) {
			listOfPrice = getPrices(prices, quantity, discount);
		} else {
			listOfPrice = new ArrayList<Price>();
		}
		if(listOfPrice != null && !listOfPrice.isEmpty()){
			priceGrid.setPrices(listOfPrice);
		}
		priceGridsList.add(priceGrid);
		return priceGridsList;

	}
	public List<Price> getPrices(String[] prices,
			String[] quantity, String[] discount) {

		List<Price> listOfPrices = new ArrayList<Price>();
		for (int i = 0, j = 1; i < prices.length 
				&& i < quantity.length; i++, j++) {

			Price price = new Price();
			PriceUnit priceUnit = new PriceUnit();
			price.setSequence(j);
			try {
				price.setQty(Integer.valueOf(quantity[i]));
			} catch (NumberFormatException nfe) {
				_LOGGER.error("Error while processing quantity in PSLParser" + nfe.getMessage());
				price.setQty(0);
			}
			price.setPrice(prices[i]);
       		price.setDiscountCode(discount[0]);
			priceUnit
					.setItemsPerUnit(ApplicationConstants.CONST_STRING_VALUE_ONE);
			price.setPriceUnit(priceUnit);
			listOfPrices.add(price);
		}
		return listOfPrices;
	}

	public List<Price> getPrices(String[] prices, String[] quantity, String discount) {

		List<Price> listOfPrices = new ArrayList<Price>();
		for (int PriceNumber = 0, sequenceNum = 1; PriceNumber < prices.length && PriceNumber < quantity.length
				      && PriceNumber < discount.length(); PriceNumber++, sequenceNum++) {

			Price price = new Price();
			PriceUnit priceUnit = new PriceUnit();
			price.setSequence(sequenceNum);
			try {
				price.setQty(Integer.valueOf(quantity[PriceNumber]));
			} catch (NumberFormatException nfe) {
				price.setQty(ApplicationConstants.CONST_NUMBER_ZERO);
			}
			price.setPrice(prices[PriceNumber]);
			price.setDiscountCode(Character.toString(discount.charAt(PriceNumber)));
			priceUnit
					.setItemsPerUnit(ApplicationConstants.CONST_STRING_VALUE_ONE);
			price.setPriceUnit(priceUnit);
			listOfPrices.add(price);
		}
		return listOfPrices;
	}

	public List<PriceConfiguration> getConfigurations(String criterias,String UpchargeName) {
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
					config = criterias.split(ApplicationConstants.CONST_DELIMITER_COLON);
					//String criteriaValue = LookupData.getCriteriaValue(config[0]);
					configs.setCriteria(criteriaValue);
					configs.setValue(Arrays.asList((Object) config[1]));
					priceConfiguration.add(configs);
					
				}
			}

		} else {
			
			configs = new PriceConfiguration();
			configs.setCriteria(criterias);
			configs.setValue(Arrays.asList((Object) UpchargeName));
			priceConfiguration.add(configs);
			
			
		}
		}catch(Exception e){
			_LOGGER.error("Error while processing PriceGrid: "+e.getMessage());
		}
		return priceConfiguration;
	}


}
