package parser.BagMakers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Price;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.PriceUnit;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.LookupData;

public class BagMakersPriceGridParser {
	private static Logger              _LOGGER       =  Logger.getLogger(BagMakersPriceGridParser.class);
	public static List<PriceGrid> getPriceGrids(String listOfPrices, String listOfQuan, String discountCodes,
			String currency, String priceInclude, boolean isBasePrice,
			String qurFlag, String priceName, String criterias,Integer sequence,String upChargeType,String upchargeUsageType,
			List<PriceGrid> existingPriceGrid) 
			{
		try{
			if(CollectionUtils.isEmpty(existingPriceGrid)){
				existingPriceGrid=new ArrayList<PriceGrid>();
				sequence=1;
			}else{
				sequence= existingPriceGrid.size()+1;
			}
		//Integer sequence = 1;
		List<PriceConfiguration> configuration = null;
		PriceGrid priceGrid = new PriceGrid();
		String[] prices = listOfPrices
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] quantity = listOfQuan
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		
		priceGrid.setCurrency(currency);
		priceGrid.setDescription(priceName);
		priceGrid.setPriceIncludes(priceInclude);
		priceGrid
				.setIsQUR(qurFlag.equalsIgnoreCase(ApplicationConstants.CONST_STRING_FALSE) ? ApplicationConstants.CONST_BOOLEAN_FALSE
						: ApplicationConstants.CONST_BOOLEAN_TRUE);
		if(!isBasePrice){
			if(criterias.contains("Product Option")){
				priceGrid.setServiceCharge("Optional");
			}else{
				priceGrid.setServiceCharge(ApplicationConstants.CONST_STRING_SERVICECHARGE);
			}
			
			priceGrid.setUpchargeType(upChargeType);
			priceGrid.setUpchargeUsageType(upchargeUsageType);
			//UpchargeUsageType
		}
		priceGrid.setIsBasePrice(isBasePrice);
		priceGrid.setSequence(sequence);
		List<Price> listOfPrice = null;
		if (!priceGrid.getIsQUR()) {
			listOfPrice = getPrices(prices, quantity, discountCodes);
		} else {
			listOfPrice = new ArrayList<Price>();
		}
		priceGrid.setPrices(listOfPrice);
		if (criterias != null && !criterias.isEmpty()) {
			configuration = getConfigurations(criterias+":"+priceName);//because over here pricename & criteria value is same
		}
		priceGrid.setPriceConfigurations(configuration);
		existingPriceGrid.add(priceGrid);
		}catch(Exception e){
			_LOGGER.error("Error while processing PriceGrid: "+e.getMessage());
		}
		return existingPriceGrid;

	}

	public static List<Price> getPrices(String[] prices, String[] quantity, String discount) {

		List<Price> listOfPrices = new ArrayList<Price>();
		try{
			int tempLen=prices.length;
			StringBuilder tempv = new StringBuilder() ;
			for (int i = 1; i <=tempLen; i++) {
				tempv.append(discount);
				tempv=tempv.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			}
			
			discount=tempv.toString();
		for (int PriceNumber = 0, sequenceNum = 1; PriceNumber < prices.length && PriceNumber < quantity.length
				      && PriceNumber < discount.length(); PriceNumber++, sequenceNum++) {

			Price price = new Price();
			PriceUnit priceUnit = new PriceUnit();
			String temp[]=discount.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			price.setSequence(sequenceNum);
			try {
				price.setQty(Integer.valueOf(quantity[PriceNumber].replace(".0","")));
			} catch (NumberFormatException nfe) {
				price.setQty(ApplicationConstants.CONST_NUMBER_ZERO);
			}
			price.setPrice(prices[PriceNumber]);
			price.setDiscountCode(temp[PriceNumber]);
			priceUnit
					.setItemsPerUnit(ApplicationConstants.CONST_STRING_VALUE_ONE);
			price.setPriceUnit(priceUnit);
			listOfPrices.add(price); 
		}
		}
		catch (Exception e) {
			_LOGGER.error("Error while processing PriceGrid Prices: "+e.getMessage());
		}
		return listOfPrices;
	}
	
	public static List<PriceConfiguration> getConfigurations(String criterias) {
		List<PriceConfiguration> priceConfiguration = new ArrayList<PriceConfiguration>();
		String[] config = null;
		PriceConfiguration configs = null;
		try{
		if (criterias
				.contains(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID)) {
			//criterias=criterias.split(criterias.lastIndexOf(":"));
			if(criterias.contains("Additional Color, Addiotnal Location")){
				criterias=criterias.replace(":Additional Color, Addiotnal Location", "");
				
			}
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
						if(criteriaValue.equals("Add bottom board")){
						configs.setOptionName("Optional Bottom Board");
						}
						priceConfiguration.add(configs);
					}
				} else {
					configs = new PriceConfiguration();
					configs.setCriteria(criteriaValue);
					configs.setValue(Arrays.asList((Object) config[1]));
					if(criteriaValue.equals("Add bottom board")){
					configs.setOptionName("Optional Bottom Board");
					}
					priceConfiguration.add(configs);
				}
			}

		} else {
			configs = new PriceConfiguration();
			config = criterias.split(ApplicationConstants.CONST_DELIMITER_COLON);
			//String criteriaValue = LookupData.getCriteriaValue(config[0]);
			configs.setCriteria(config[0]);
			configs.setValue(Arrays.asList((Object) config[1]));
			if(config[1].equals("Add bottom board")){
				configs.setOptionName("Optional Bottom Board");
				}
			priceConfiguration.add(configs);
		}
		}catch(Exception e){
			_LOGGER.error("Error while processing PriceGrid PriceConfiguration: "+e.getMessage());
		}
		return priceConfiguration;
	}

	
	public static List<PriceGrid> getPriceGridsQur( ) 
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
	
	//List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
	
	public Product getPricingData(String listOfPrices,String listOfQuantity,String listOfDiscount,String basePricePriceInlcude, String plateScreenCharge,String plateScreenChargeCode,
			String plateReOrderCharge,String plateReOrderChargeCode,List<PriceGrid> priceGrids,Product productExcelObj,ProductConfigurations productConfigObj){
		try{
		if(!StringUtils.isEmpty(listOfPrices)){
			priceGrids =getPriceGrids(listOfPrices.toString(),listOfQuantity.toString(),listOfDiscount.toString(),
					ApplicationConstants.CONST_STRING_CURRENCY_USD,basePricePriceInlcude,ApplicationConstants.CONST_BOOLEAN_TRUE,
					ApplicationConstants.CONST_STRING_FALSE,ApplicationConstants.CONST_STRING_EMPTY,"",1,"","",
					priceGrids);	
		}
	 if(!StringUtils.isEmpty(plateScreenCharge)){
		 plateScreenCharge=plateScreenCharge.toUpperCase();
		 if(plateScreenCharge.contains("NO SET UP") || plateScreenCharge.contains("MULTI")){
			 productExcelObj.setDistributorOnlyComments(plateScreenCharge);
		 }else{
			 List<ImprintMethod> tempList=productConfigObj.getImprintMethods();
			 if(!CollectionUtils.isEmpty(tempList)){
				 plateScreenCharge=plateScreenCharge.replaceAll("$", "");
				 for (ImprintMethod imprintMethod : tempList) {
					//get alias over here
					 String tempALias=imprintMethod.getAlias();
					 priceGrids=getPriceGrids(
							 plateScreenCharge, "1", plateScreenChargeCode,
								ApplicationConstants.CONST_STRING_CURRENCY_USD,"Plate/Screen Charge",false,
								"false",tempALias,"Imprint Method",new Integer(1),"Screen Charge", "Per Order",
								priceGrids);	
				}
			 }
			 
		 }
	 }
	 if(!StringUtils.isEmpty(plateReOrderCharge)){
		 plateReOrderCharge=plateReOrderCharge.toUpperCase();
		 if(plateReOrderCharge.contains("NO SET UP") || plateReOrderCharge.contains("MULTI")){
			 productExcelObj.setDistributorOnlyComments(plateReOrderCharge);
		 }else{
			 List<ImprintMethod> tempList=productConfigObj.getImprintMethods();
			 if(!CollectionUtils.isEmpty(tempList)){
				 plateReOrderCharge=plateReOrderCharge.replaceAll("$", "");
				 for (ImprintMethod imprintMethod : tempList) {
					//get alias over here
					 String tempALias=imprintMethod.getAlias();
					 priceGrids=getPriceGrids(
							 plateReOrderCharge, "1", plateReOrderChargeCode,
								ApplicationConstants.CONST_STRING_CURRENCY_USD,"Reorder Plate/Screen Charge",false,
								"false",tempALias,"Imprint Method",new Integer(1),"Re-order Charge", "Per Quantity",
								priceGrids);	
				}
			 }
			 
		 }
	 }
	 
	 if(CollectionUtils.isEmpty(priceGrids)){
			priceGrids = getPriceGridsQur();	
		}
	 productExcelObj.setPriceGrids(priceGrids);
		}catch(Exception ex){
			_LOGGER.error("Error while processing pricegrid "+ex.getMessage());
		}
	return productExcelObj;
	
	}

	public Product getPricingData(StringBuilder listOfPrices,
			StringBuilder listOfQuantity, StringBuilder listOfDiscount,
			String plateScreenCharge, String plateScreenChargeCode,
			String plateReOrderCharge, String plateReOrderChargeCode,
			List<PriceGrid> priceGrids, Product productExcelObj,
			ProductConfigurations productConfigObj) {
		// TODO Auto-generated method stub
		return null;
	}
}
