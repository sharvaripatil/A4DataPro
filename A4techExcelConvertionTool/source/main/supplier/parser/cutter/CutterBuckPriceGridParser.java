package parser.cutter;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Price;
import com.a4tech.product.model.PriceGrid;


public class CutterBuckPriceGridParser {

	public List<PriceGrid> getPriceGrids(String listPrice, /*String netCost,*/
			Integer listOfQuan, String currency, String priceInclude, boolean isBasePrice,
			String isQur, String priceName, String criterias,
			List<PriceGrid> existingPriceGrid) {
		
		
		Integer sequence = 1;
		PriceGrid priceGrid = new PriceGrid();
		List<PriceGrid> listPriceGrid=new ArrayList<PriceGrid>();
		List<Price> listOfPrices = new ArrayList<Price>();
		priceGrid.setCurrency(currency);
		priceGrid.setDescription(priceName);

		priceGrid.setIsBasePrice(isBasePrice);
		priceGrid.setSequence(sequence);
		if(isQur=="N"){
			Price price = new Price();
			price.setSequence(sequence);
			price.setQty(listOfQuan);
			price.setPrice(listPrice);
			//price.setNetCost(netCost);
			listOfPrices.add(price);
		

		priceGrid.setPrices(listOfPrices);
		}
		listPriceGrid.add(priceGrid);
		
		return listPriceGrid;
	}
}
