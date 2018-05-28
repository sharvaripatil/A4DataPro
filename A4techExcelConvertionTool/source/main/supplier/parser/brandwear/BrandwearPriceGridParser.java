package parser.brandwear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.Price;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.PriceUnit;
import com.a4tech.product.model.Size;
import com.a4tech.util.ApplicationConstants;


public class BrandwearPriceGridParser {
	
	private static final Logger _LOGGER = Logger
			.getLogger(BrandwearPriceGridParser.class);
	public List<PriceGrid> getPriceGrids(String listOfPrices,
		    String listOfQuan, String listOfDisc,
			String currency, String priceInclude, boolean isBasePrice,
			String isQur, String priceName, String criterias,
			String sizeValue, List<PriceGrid> existingPriceGrid) {

		Integer sequence = 1;
	//	List<PriceGrid> priceGridsList = new ArrayList<PriceGrid>();

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
		existingPriceGrid.add(priceGrid);
		return existingPriceGrid;

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
       		price.setDiscountCode(discount[i]);
			priceUnit
					.setItemsPerUnit(ApplicationConstants.CONST_STRING_VALUE_ONE);
			price.setPriceUnit(priceUnit);
			listOfPrices.add(price);
		}
		return listOfPrices;
	}

	
	

	
	
	

	public List<PriceGrid> getRepeatablePriceGrids(String listOfPrices,
		    String listOfQuan, String listOfDisc,
			String currency, String priceInclude, boolean isBasePrice,
			String isQur, String priceName, String criterias,/*List<Value> ExstngvalueObj,*/String sizeValue, List<PriceGrid> exstingPriceGrid) {

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
		
		priceGrid.setIsBasePrice(isBasePrice);
		priceGrid.setSequence(sequence);
		priceGrid.setPriceIncludes(priceInclude);
		
		PriceConfigurationObj.setCriteria("Size");
		
		List<Object>valueObjList=new ArrayList<>();
		Object obj= new Object();
	

		if (sizeValue.contains("@") && !sizeValue.contains("One") ) {
			
		
		 String sizearr[]= {"S","M","L","XL"};	
	
		for (String sizeName : sizearr) {
			
			PriceConfigurationObj=new PriceConfiguration();
 			obj= new Object();	
		    valueObjList=new ArrayList<>();
			obj=sizeName;

			PriceConfigurationObj.setCriteria("Size");
			valueObjList.add(obj);
 			PriceConfigurationObj.setValue(valueObjList);
			priceConfigurationList.add(PriceConfigurationObj);

		}
		}
	
	else
		{
		   sizeValue=sizeValue.replace("@", "");
			PriceConfigurationObj=new PriceConfiguration();
 			obj= new Object();	
		    valueObjList=new ArrayList<>();
			obj=sizeValue;

			PriceConfigurationObj.setCriteria("Size");
			valueObjList.add(obj);
 			PriceConfigurationObj.setValue(valueObjList);
			priceConfigurationList.add(PriceConfigurationObj);
			
			
		}
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
