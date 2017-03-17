package parser.proGolf;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.util.StringUtils;

import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class ProGolfPricingMapping{
	private static final Logger _LOGGER = Logger.getLogger(ProGolfPricingMapping.class);
    private ProGolfPriceGridParser proGolfPriceGridParser;
	
	public Map<String, Product> readMapper(HashMap<String, Product> productMaps, Sheet sheet) {

		int columnIndex = 0;

		Set<String> productXids = new HashSet<String>();
		List<String> repeatRows = new ArrayList<>();
		ProductConfigurations productConfigObj = new ProductConfigurations();
		//Product productExcelObj = new Product();
		String productId = null;
		String xid = null;
		String prdXid = null;
		StringJoiner listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfPrices   = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String priceType    = "";
		String  type  = "";
		String headerName ="";
		List<String> productIds = new ArrayList<>();
		List<PriceGrid> priceGrids = new ArrayList<>();
		String skuId = "";
		try {
			Row  headerRow = null;
			Iterator<Row> iterator = sheet.iterator();
			Product existingProduct = null;
			while (iterator.hasNext()) {
				try {
					Row nextRow = iterator.next();
					if (nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO) {
						headerRow = nextRow;
						continue;
					}
					if (nextRow.getRowNum() == ApplicationConstants.CONST_INT_VALUE_ONE) {
						Cell cell1 = nextRow.getCell(1);
						skuId = CommonUtility.getCellValueStrinOrInt(cell1);
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
									existingProduct.setProductConfigurations(productConfigObj);
									if (!StringUtils.isEmpty(existingProduct.getExternalProductId())) {
									}
									existingProduct.setPriceType("L");
									productMaps.put(prdXid, existingProduct);
									repeatRows.clear();

								}
								priceGrids = new ArrayList<>();
								if (!productXids.contains(xid)) {
									productXids.add(xid);
									repeatRows.add(xid);
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
						case "Price_Type":
                         priceType = cell.getStringCellValue();

							break;
						case "type":
							type = cell.getStringCellValue();
							break;
						case "Price_1": //price
						case "Price_2":
						case "Price_3":
						case "Price_4":
						case "Price_5":
							String listPrice = CommonUtility.getCellValueDouble(cell);
							if(!StringUtils.isEmpty(listPrice)){
								listOfPrices.add(listPrice);
							}
							break;
						case "Qty_1_Min": //quantity
						case "Qty_2_Min":
						case "Qty_3_Min":
						case "Qty_4_Min": 
						case "Qty_5_Min": 
							String priceQty = CommonUtility.getCellValueStrinOrInt(cell);
							if(!StringUtils.isEmpty(priceQty)){
								listOfQuantity.add(priceQty);
							}
						break;
						
						case "Code_1": //discount code
						case "Code_2":
						case "Code_3":
						case "Code_4": 
						case "Code_5": 
							String discountCode = CommonUtility.getCellValueStrinOrInt(cell);
							if(!StringUtils.isEmpty(discountCode)){
								listOfDiscount.add(discountCode);
							}
						break;
						} // end inner while loop

					}
					boolean isQurFlag = false;
					String basePriceName = "";
					if("call_for_price".equalsIgnoreCase(priceType)){
						isQurFlag = true;
					}//decorative
					if("decorative".equalsIgnoreCase("type")){
						basePriceName = "decorative";
					}
					    if(!type.equalsIgnoreCase("special")){
					    	priceGrids = proGolfPriceGridParser.getBasePriceGrid(listOfPrices.toString(), 
									listOfQuantity.toString(), listOfDiscount.toString(), "USD",
									         "", true, isQurFlag, basePriceName,"",priceGrids);	
					    }					
				} catch (Exception e) {
					_LOGGER.error(
							"Error while Processing ProductId and cause :" + existingProduct.getExternalProductId()
									+ " " + e.getMessage() + "at column number(increament by 1):" + headerName);
				}
			}

			existingProduct.setProductConfigurations(productConfigObj);

			if (!StringUtils.isEmpty(existingProduct.getExternalProductId())) {

			}
			repeatRows.clear();
			productConfigObj = new ProductConfigurations();
			return productMaps;
		} catch (Exception e) {
			_LOGGER.error(
					"Error while Processing " + sheet.getSheetName() + "+ sheet ,Error message: " + e.getMessage());
			return productMaps;
		} finally {
		}
	}
	public boolean isRepeateColumn(int columnIndex) {
		if (columnIndex != 1) {
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	private String getHeaderName(int columnIndex,Row headerRow){
		 Cell cell2 =  headerRow.getCell(columnIndex);  
		 String headerName=CommonUtility.getCellValueStrinOrInt(cell2);
		//columnIndex = ProGolfHeaderMapping.getHeaderIndex(headerName);
		return headerName;
	}
	public ProGolfPriceGridParser getProGolfPriceGridParser() {
		return proGolfPriceGridParser;
	}
	public void setProGolfPriceGridParser(ProGolfPriceGridParser proGolfPriceGridParser) {
		this.proGolfPriceGridParser = proGolfPriceGridParser;
	}
}
