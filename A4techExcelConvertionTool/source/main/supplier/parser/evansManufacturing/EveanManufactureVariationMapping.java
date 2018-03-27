package parser.evansManufacturing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class EveanManufactureVariationMapping{
	private static final Logger _LOGGER = Logger.getLogger(EveanManufactureVariationMapping.class);
    private EveansManufacturePriceGridParser proGolfPriceGridParser;
	
	public Map<String, Product> readMapper(Map<String, Product> productMaps, Sheet sheet) {

		int columnIndex = 0;

		Set<String> productXids = new HashSet<String>();
		List<String> repeatRows = new ArrayList<>();
		ProductConfigurations productConfigObj = new ProductConfigurations();
		Product productExcelObj = new Product();
		//String productId = null;
		String xid = null;
		String prdXid = null;
		List<String> productIds = new ArrayList<>();
		String headerName =  "";
		StringJoiner listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfPrices   = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String priceType = "";
		List<PriceGrid> priceGrids = null;
		boolean isSinglePriceGrid = true;// this sheet has contain single price grid only 
		try {
			Iterator<Row> iterator = sheet.iterator();
			Product existingProduct = null;
			Row  headerRow = null;
			while (iterator.hasNext()) {

				try {
					Row nextRow = iterator.next();

					if (nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO) {
						headerRow = nextRow;
						continue;
					}
					Cell cell1 = nextRow.getCell(1);
					prdXid = CommonUtility.getCellValueStrinOrInt(cell1);
					// this condition used to check xid is present list or not ,
					// if xid present in Map means already fetch product from
					// Map
					if (!productIds.contains(prdXid)) {
						existingProduct = productMaps.get(prdXid);
					}
					productIds.add(prdXid);
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					if (xid != null) {
						productXids.add(xid);
					}
					boolean checkXid = false;

					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						columnIndex = cell.getColumnIndex();

						if (columnIndex + 1 == 1) {
							Cell cell2 = nextRow.getCell(1);
							prdXid = CommonUtility.getCellValueStrinOrInt(cell2);
							checkXid = true;
						} else {
							checkXid = false;
						}

						if (checkXid) {
							if (!productXids.contains(xid)) {
								if (nextRow.getRowNum() != 1) {
									productExcelObj.setProductConfigurations(productConfigObj);
									if (!StringUtils.isEmpty(productExcelObj.getExternalProductId())) {
									}
									productMaps.put(prdXid, existingProduct);
									repeatRows.clear();

								}
								if (!productXids.contains(xid)) {
									productXids.add(xid);
									repeatRows.add(xid);
								}
								priceGrids = existingProduct.getPriceGrids();
								if(CollectionUtils.isEmpty(priceGrids)){
									priceGrids = new ArrayList<>();
								}

							}
						} else {

							if (productXids.contains(xid) && repeatRows.size() != 1) {
								if (isRepeateColumn(columnIndex + 1)) {
									continue;
								}
							}
						}
						headerName = getHeaderName(columnIndex, headerRow);
						switch (headerName) {
						case "ATTR_Golf Ball Model":// XID
							// ignore this filed since same data present in productionInformation sheet for same product
							break;

						case "type":// MaterialNumber
							break;
						case "Price_1": //price
						case "Price_2":
						case "Price_3":
							String listPrice = CommonUtility.getCellValueDouble(cell);
							if(!StringUtils.isEmpty(listPrice)){
								listOfPrices.add(listPrice);
							}
							break;
						case "Qty_1_Min": //quantity
						case "Qty_2_Min":
						case "Qty_3_Min":
						
							String priceQty = CommonUtility.getCellValueStrinOrInt(cell);
							if(!StringUtils.isEmpty(priceQty)){
								listOfQuantity.add(priceQty);
							}
						break;
						
						case "Code_1": //discount code
						case "Code_2":
						case "Code_3":
						
							String discountCode = CommonUtility.getCellValueStrinOrInt(cell);
							if(!StringUtils.isEmpty(discountCode)){
								listOfDiscount.add(discountCode);
							} 	

						} // end inner while loop

					}
					if(!priceType.equalsIgnoreCase("special")){
						if(isSinglePriceGrid){
							priceGrids = proGolfPriceGridParser.getBasePriceGrid(listOfPrices.toString(), 
									listOfQuantity.toString(), listOfDiscount.toString(), "USD",
									         "", true, false, priceType,"",priceGrids,"","");	
							isSinglePriceGrid = false;
						}
				    	
				    }	
				} catch (Exception e) {
					_LOGGER.error(
							"Error while Processing ProductId and cause :" + productExcelObj.getExternalProductId()
									+ " " + e.getMessage() + "at column number(increament by 1):" + columnIndex);
				}
			}
			 if(!priceType.equalsIgnoreCase("special")){
			    	priceGrids = proGolfPriceGridParser.getBasePriceGrid(listOfPrices.toString(), 
							listOfQuantity.toString(), listOfDiscount.toString(), "USD",
							         "", true, false, priceType,"",priceGrids,"","");	
			    }	
			if (!StringUtils.isEmpty(productExcelObj.getExternalProductId())) {

			}
			repeatRows.clear();
			existingProduct.setPriceGrids(priceGrids);
			productMaps.put(prdXid, existingProduct);
			return productMaps;
		} catch (Exception e) {
			_LOGGER.error(
					"Error while Processing " + sheet.getSheetName() + "+ sheet ,Error message: " + e.getMessage());
			return productMaps;
		} finally {
		}

	}
	private String getHeaderName(int columnIndex,Row headerRow){
		 Cell cell2 =  headerRow.getCell(columnIndex);  
		 String headerName=CommonUtility.getCellValueStrinOrInt(cell2);
		//columnIndex = ProGolfHeaderMapping.getHeaderIndex(headerName);
		return headerName;
	}

	public boolean isRepeateColumn(int columnIndex) {

		if (columnIndex != 1) {
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	public EveansManufacturePriceGridParser getProGolfPriceGridParser() {
		return proGolfPriceGridParser;
	}
	public void setProGolfPriceGridParser(EveansManufacturePriceGridParser proGolfPriceGridParser) {
		this.proGolfPriceGridParser = proGolfPriceGridParser;
	}
}
