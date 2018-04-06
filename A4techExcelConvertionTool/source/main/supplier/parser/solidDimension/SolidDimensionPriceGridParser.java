package parser.solidDimension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.a4tech.product.model.Price;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.PriceUnit;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.LookupData;

public class SolidDimensionPriceGridParser {

	private Logger              _LOGGER              = Logger.getLogger(getClass());
	public List<PriceGrid> getPriceGrids(String listOfPrices,
		    String listOfQuan, String discountCodes,
			String currency, String priceInclude, boolean isBasePrice,
			String qurFlag, String priceName, String criterias,String priceUnitArr,
			List<PriceGrid> existingPriceGrid,String sizeValues){
		_LOGGER.info("Enter Price Grid Parser class");
		try{
		Integer sequence = 1;
		if(!CollectionUtils.isEmpty(existingPriceGrid)){
			sequence=existingPriceGrid.size()+1;
		}
		
		List<PriceConfiguration> configuration = null;
		PriceGrid priceGrid = new PriceGrid();
		String[] prices = listOfPrices
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] quantity = listOfQuan
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] priceUnit= priceUnitArr
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] discArr= {};
		priceGrid.setCurrency(currency);
		priceGrid.setDescription(priceName);
		priceGrid.setPriceIncludes(priceInclude);
		priceGrid
				.setIsQUR(qurFlag.equalsIgnoreCase("False") ? ApplicationConstants.CONST_BOOLEAN_FALSE
						: ApplicationConstants.CONST_BOOLEAN_TRUE);
		priceGrid.setIsBasePrice(isBasePrice);
		priceGrid.setSequence(sequence);
		List<Price> listOfPrice = null;
		if (!priceGrid.getIsQUR()) {//breaking here
			//String qaunitity="1___";
			int priceLen=listOfPrices.split("___").length;
			//int discCodeLen=discountCodes.length();
			if(priceLen>1 ){
				String tempDiscCode=discountCodes.concat("___");
				discountCodes=String.join("", Collections.nCopies(priceLen, tempDiscCode));
				discArr=discountCodes.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			}else{
				discArr=discountCodes.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			}
			//listOfPrice = getPrices(prices, quantity, String.join(",", discArr),priceUnit); //discArr
			listOfPrice = getPrices(prices, quantity, discArr,priceUnit);
		} else {
			listOfPrice = new ArrayList<Price>();
		}
		priceGrid.setPrices(listOfPrice);
		if (criterias != null && !criterias.isEmpty()) {
			configuration = getConfigurations(criterias,priceName,sizeValues);
			priceGrid.setPriceConfigurations(configuration);
		}
		
		existingPriceGrid.add(priceGrid);
		}catch(Exception e){
			_LOGGER.error("Error while processing PriceGrid: "+e.getLocalizedMessage());
		}
		return existingPriceGrid;

	}

	public List<Price> getPrices(String[] prices, String[] quantity, String discount[],String[] priceUnitArr) {

		List<Price> listOfPrices = new ArrayList<Price>();
		try{
		for (int PriceNumber = 0, sequenceNum = 1; PriceNumber < prices.length && PriceNumber < quantity.length
				      && PriceNumber < discount.length; PriceNumber++, sequenceNum++) {

			Price price = new Price();
			PriceUnit priceUnit = new PriceUnit();
			price.setSequence(sequenceNum);
			try {
				price.setQty(Integer.valueOf(quantity[PriceNumber]));
			} catch (NumberFormatException nfe) {
				price.setQty(ApplicationConstants.CONST_NUMBER_ZERO);
			}
			price.setPrice(prices[PriceNumber]);
			//price.setDiscountCode(Character.toString(discount.charAt(PriceNumber)));
			price.setDiscountCode(discount[PriceNumber]);
			/*priceUnit
					.setItemsPerUnit(ApplicationConstants.CONST_STRING_VALUE_ONE);*/
			if(priceUnitArr[PriceNumber].equals("1000")){
				priceUnit.setName("1000");
			}
			
			priceUnit.setItemsPerUnit(priceUnitArr[PriceNumber]);
			price.setPriceUnit(priceUnit);
			listOfPrices.add(price);
		}
		}catch(Exception ex){
			_LOGGER.error("error while processing pricess "+ex.getMessage());
		}
		return listOfPrices;
	}

	public List<PriceConfiguration> getConfigurations(String criterias,String UpchargeName,String sizeValues) {
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

		}else if(criterias.toUpperCase().equals("SIZE")){
			String arr[]=sizeValues.split("#####");
			List<Object> objList=new ArrayList<>();
			objList =SolidDimesionAttributeParser.getValuesObj(arr[0], arr[1], arr[2],objList);
			configs = new PriceConfiguration();
			configs.setCriteria(criterias);
			configs.setValue(objList);
			priceConfiguration.add(configs);
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

	public List<PriceGrid> getUpchargePriceGrid(String quantity, String prices,
			String discounts, String upChargeCriterias, String qurFlag,
			String currency, String upChargeName, String upChargeType,
			String upchargeUsageType, Integer upChargeSequence,String priceUnitArr,
			List<PriceGrid> existingPriceGrid) {
		try{
		List<PriceConfiguration> configuration = null;
		PriceGrid priceGrid = new PriceGrid();
		String[] upChargePrices = prices
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] upChargeQuantity = quantity
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] upPriceunit = priceUnitArr
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String upChargeDiscount = discounts;
		String[] upDiscArr ={};
		priceGrid.setCurrency(currency);
		priceGrid.setDescription(upChargeName);
		priceGrid
				.setIsQUR((qurFlag.equalsIgnoreCase("Y")) ? ApplicationConstants.CONST_BOOLEAN_TRUE
						: ApplicationConstants.CONST_BOOLEAN_FALSE);
		priceGrid.setIsBasePrice(ApplicationConstants.CONST_BOOLEAN_FALSE);
		priceGrid.setSequence(upChargeSequence);
		priceGrid.setUpchargeType(upChargeType);
		priceGrid.setUpchargeUsageType(upchargeUsageType);
		priceGrid.setServiceCharge("Required");
		List<Price> listOfPrice = null;
		if (!priceGrid.getIsQUR()) {
			
			/*//breaking here
			//String qaunitity="1___";
			int priceLen=prices.split("___").length;
			//int discCodeLen=discountCodes.length();
			if(priceLen>1 ){
				String tempDiscCode=discounts.concat("___");
				discounts=String.join("", Collections.nCopies(priceLen, tempDiscCode));
				upDiscArr=discounts.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			}else{
				upDiscArr[0]=discounts;
			}*/
			
			int priceLen=prices.split("___").length;
			//int discCodeLen=discountCodes.length();
			if(priceLen>1 ){
				String tempDiscCode=discounts.concat("___");
				discounts=String.join("", Collections.nCopies(priceLen, tempDiscCode));
				upDiscArr=discounts.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			}else{
				upDiscArr=discounts.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			}
			
		
			
			 listOfPrice =
			 //getPrices(upChargePrices,upChargeQuantity,String.join(",", upDiscArr),upPriceunit);
					 getPrices(upChargePrices,upChargeQuantity,upDiscArr,upPriceunit);
		} else {
			listOfPrice = new ArrayList<Price>();
		}

		priceGrid.setPrices(listOfPrice);
		if (upChargeCriterias != null && !upChargeCriterias.isEmpty()) {
			configuration = getConfigurations(upChargeCriterias,upChargeName,null);
		}
		priceGrid.setPriceConfigurations(configuration);
		existingPriceGrid.add(priceGrid);
		}catch(Exception e){
			_LOGGER.error("Error while processing UpchargePriceGrid: "+e.getMessage());
		}
		return existingPriceGrid;
	}
	public List<PriceGrid> getPriceGridsQur( ) 
	{
		List<PriceGrid> newPriceGrid=new ArrayList<PriceGrid>();
		try{
			Integer sequence = 1;
			//List<PriceConfiguration> configuration = null;
			PriceGrid priceGrid = new PriceGrid();
			priceGrid.setIsBasePrice(true);
			priceGrid.setIsQUR(ApplicationConstants.CONST_BOOLEAN_TRUE);
			priceGrid.setDescription(ApplicationConstants.CONST_STRING_EMPTY);
			priceGrid.setPriceIncludes(ApplicationConstants.CONST_STRING_EMPTY);
			priceGrid.setSequence(sequence);
			priceGrid.setCurrency(ApplicationConstants.CONST_STRING_CURRENCY_USD);
			List<Price>	listOfPrice = new ArrayList<Price>();
			priceGrid.setPrices(listOfPrice);
			//priceGrid.setPriceConfigurations(configuration);
			newPriceGrid.add(priceGrid);
	}catch(Exception e){
		_LOGGER.error("Error while processing PriceGrid: "+e.getMessage());
	}
		_LOGGER.info("PriceGrid Processed");
		return newPriceGrid;
}

}
