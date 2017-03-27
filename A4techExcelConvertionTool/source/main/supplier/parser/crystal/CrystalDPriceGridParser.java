package parser.crystal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.Price;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.PriceUnit;
import com.a4tech.product.model.Value;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.LookupData;

public class CrystalDPriceGridParser {
	
	private static final Logger _LOGGER = Logger
			.getLogger(CrystalDPriceGridParser.class);
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
				_LOGGER.error("Error while processing quantity in CrystalDPriceGridParser" + nfe.getMessage());
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

	public List<PriceGrid> getUpchargePriceGrid(String quantity, String prices,
			String discounts, String upChargeCriterias, String qurFlag,
			String currency, String upChargeName, String upChargeType,
			String upchargeUsageType, Integer upChargeSequence,
		List<PriceGrid> existingPriceGrid) {

	//	List<PriceGrid> priceGridsList = new ArrayList<PriceGrid>();

		List<PriceConfiguration> configuration = new ArrayList<PriceConfiguration>() ;
		//PriceConfiguration priceConfObj=new PriceConfiguration();
		PriceGrid priceGrid = new PriceGrid();
		String[] upChargePrices = prices
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] upChargeQuantity = quantity
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String[] upChargeDiscount = discounts
				.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

		priceGrid.setCurrency(currency);
		priceGrid.setDescription(upChargeName);
	/*	if(upChargeName.contains("Additional Imprint Color")|| upChargeName.contains("Additional Imprint Location"))
{
		priceGrid.setPriceIncludes("plus Setup");
}*/
		priceGrid.setServiceCharge(ApplicationConstants.CONST_STRING_SERVICECHARGE);
		priceGrid
				.setIsQUR((qurFlag.equalsIgnoreCase("false")) ? ApplicationConstants.CONST_BOOLEAN_FALSE
						: ApplicationConstants.CONST_BOOLEAN_TRUE);
		priceGrid.setIsBasePrice(ApplicationConstants.CONST_BOOLEAN_FALSE);
		priceGrid.setSequence(upChargeSequence);
		priceGrid.setUpchargeType(upChargeType);
		priceGrid.setUpchargeUsageType(upchargeUsageType);
		List<Price> listOfPrice = null;
		if (!priceGrid.getIsQUR()) {
			 listOfPrice =
			 getPrices(upChargePrices,upChargeQuantity,upChargeDiscount);
		} else {
			listOfPrice = new ArrayList<Price>();
		}

        priceGrid.setPrices(listOfPrice);
		if (upChargeCriterias != null && !upChargeCriterias.isEmpty()) {
		configuration = getConfigurations(upChargeCriterias,upChargeName);
		}
       /* if(upChargeName.contains("Personalization"))
        {
        	priceConfObj.setCriteria(upChargeCriterias);
        	priceConfObj.setValue(value);
        	configuration.add(priceConfObj);
        }*/
		priceGrid.setPriceConfigurations(configuration);
		existingPriceGrid.add(priceGrid);

		return existingPriceGrid;
	}

	
	public List<PriceConfiguration> getConfigurations(String criterias,String UpchargeName) {
		List<PriceConfiguration> priceConfiguration = new ArrayList<PriceConfiguration>();
		String[] config = null;
		PriceConfiguration configs = null;
		if (criterias
				.contains(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID)) {
			String[] configuraions = criterias
					.split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			for (String criteria : configuraions) {
				PriceConfiguration configuraion = new PriceConfiguration();
				config = criteria.split(":");
				String criteriaValue = LookupData.getCriteriaValue(config[0]);
				configuraion.setCriteria(criteriaValue);
				if (config[1].contains(",")) {
					String[] values = config[1].split(",");
					for (String Value : values) {
						configs = new PriceConfiguration();
						configs.setCriteria(criteriaValue);
						configs.setValue(Arrays.asList((Object) Value));
						priceConfiguration.add(configs);
					}
						configs = new PriceConfiguration();
					configs.setCriteria(criteriaValue);
					configs.setValue(Arrays.asList((Object) config[1]));
					priceConfiguration.add(configs);
				}
			}

			} else {
			configs = new PriceConfiguration();
			configs.setCriteria(criterias);
			configs.setValue(Arrays.asList((Object) UpchargeName));
			
			if(criterias.contains("Imprint Option"))
			{
				configs.setOptionName("Additional Imprinting Option");
			}
			priceConfiguration.add(configs);
		}
		return priceConfiguration;
	}

	
	
	

	public List<PriceGrid> getRepeatablePriceGrids(String listOfPrices,
		    String listOfQuan, String listOfDisc,
			String currency, String priceInclude, boolean isBasePrice,
			String isQur, String priceName, String criterias, String productNo, List<Value> ExstngvalueObj, List<PriceGrid> exstingPriceGrid) {

			Integer sequence = 1;
	
		List<PriceConfiguration> priceConfigurationList = new ArrayList<PriceConfiguration>();
		PriceConfiguration PriceConfigurationObj=new PriceConfiguration();
		
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
		
		String DescCriteria="";
		for (int i=0;i<ExstngvalueObj.size();i++) {
			DescCriteria=DescCriteria.concat(ExstngvalueObj.get(i).toString());
			if(i < ExstngvalueObj.size()-1)
			{
				DescCriteria=DescCriteria.concat(" x");
			}
			
		}
		DescCriteria=DescCriteria.replaceAll("[^0-9x%/ ]", "");
		priceGrid.setDescription(DescCriteria);
		
		
		priceGrid.setIsBasePrice(isBasePrice);
		priceGrid.setSequence(sequence);
		priceGrid.setPriceIncludes(priceInclude);
		priceGrid.setProductNumber(productNo);
		
		PriceConfigurationObj.setCriteria("Size");
		
		List<Object>valueObjList=new ArrayList<>();
		Object obj= new Object();
		if(ExstngvalueObj.size() == 2){
			
			
			for(int i=0;i<2;i++)
			{
				obj= new Object();
				obj=ExstngvalueObj.get(i);
				valueObjList.add(obj);
			}
			
			
		}
		else if (ExstngvalueObj.size() == 3){
			
			for(int i=0;i<3;i++)
			{
				obj= new Object();
				obj=ExstngvalueObj.get(i);
				valueObjList.add(obj);
			}
		
		
		}
	/*	Object obj= new Object();
		Object obj1= new Object();
		Object obj2= new Object();


		obj=ExstngvalueObj.get(0);
		obj1=ExstngvalueObj.get(1);
		obj2=ExstngvalueObj.get(2);
		

		List<Object>valueObjList=new ArrayList<>();
		valueObjList.add(obj);
		valueObjList.add(obj1);
		valueObjList.add(obj2);*/

		
		PriceConfigurationObj.setValue(valueObjList);
		
		priceConfigurationList.add(PriceConfigurationObj);
		priceGrid.setPriceConfigurations(priceConfigurationList);
		
		List<Price> listOfPrice = null;
     	if (!priceGrid.getIsQUR()) {
			listOfPrice = getPrices(prices, quantity, discount);
		} else {
			listOfPrice = new ArrayList<Price>();
		}
		if(listOfPrice != null && !listOfPrice.isEmpty()){
			priceGrid.setPrices(listOfPrice);
		}
		exstingPriceGrid.add(priceGrid);
		
		
		
		
		
		return exstingPriceGrid;
		
		
		
	}


}
