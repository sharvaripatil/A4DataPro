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
import org.springframework.util.StringUtils;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class EveanManufacturePricingMapping{
	private static final Logger _LOGGER = Logger.getLogger(EveanManufacturePricingMapping.class);
    private EveansManufacturePriceGridParser eveanManufacturePriceGridParser;
    private PostServiceImpl 				postServiceImpl;
	private ProductDao 						productDaoObj;
	
	public Map<String, Product> readMapper(Map<String, Product> productMaps, Sheet sheet,String accessToken,int asiNumber,int batchId,String environmentType) {

		int columnIndex = 0;
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		Set<String> productXids = new HashSet<String>();
		List<String> repeatRows = new ArrayList<>();
		String productId = null;
		StringJoiner listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfPrices   = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String headerName ="";
		List<String> productIds = new ArrayList<>();
		List<PriceGrid> priceGrids = null;
		boolean isFirstProduct = true;
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
					productId = getSkuValue(nextRow);
					if(isFirstProduct){
						existingProduct = productMaps.get(productId);
						priceGrids = existingProduct.getPriceGrids();
						isFirstProduct = false;
					}
					productIds.add(productId);
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					/*if (xid != null) {
						productXids.add(xid);
					}*/
					boolean checkXid = false;

					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						columnIndex = cell.getColumnIndex();

						if (columnIndex + 1 == 1) {
							//Cell cell2 = nextRow.getCell(1);
						//	prdXid = CommonUtility.getCellValueStrinOrInt(cell2);
							checkXid = true;
						} else {
							checkXid = false;
						}
						if (checkXid) {
							if (!productXids.contains(productId)) {
								if (nextRow.getRowNum() != 1) {
									if (!StringUtils.isEmpty(existingProduct.getExternalProductId())) {
									}
									existingProduct.setPriceType("L");
									existingProduct.setPriceGrids(priceGrids);
									int num = postServiceImpl.postProduct(accessToken, existingProduct, asiNumber,
											batchId, environmentType);
								 	if(num ==1){
								 		numOfProductsSuccess.add("1");
								 	}else if(num == 0){
								 		numOfProductsFailure.add("0");
								 	}else{
								 		
								 	}		
							 	_LOGGER.info("list size>>>>>>>"+numOfProductsSuccess.size());
							 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
								//	productMaps.put(existingProduct.getProductLevelSku(), existingProduct);
									repeatRows.clear();
									existingProduct = productMaps.get(productId);
									priceGrids = existingProduct.getPriceGrids();
									//priceGrids = new ArrayList<>();
									//basePriceGrids = new ArrayList<>();
									 listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
									 listOfPrices   = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
									 listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								}
								if (!productXids.contains(productId)) {
									productXids.add(productId);
									repeatRows.add(productId);
								}
							}
							 listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							 listOfPrices   = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							 listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						} else {
							if (productXids.contains(productId) && repeatRows.size() != 1) {
								if (isRepeateColumn(columnIndex + 1)) {
									continue;
								}
							}
						}
						headerName = getHeaderName(columnIndex, headerRow);
						switch (columnIndex+1) {
						case 1:
							break;
						case 2:
							break;
						case 3:
							break;
						case 4: //
							break;
						case 5://quantity
						case 8:
						case 11:
						case 14:
						case 17:
						case 20:
							String priceQty = CommonUtility.getCellValueDouble(cell);
							if(!StringUtils.isEmpty(priceQty)){
								
								listOfQuantity.add(priceQty);
							}
							break;
						case 6: //price
						case 9:
						case 12: 
						case 15:
						case 18:
						case 21:
							String listPrice = CommonUtility.getCellValueStrinOrInt(cell);
							if(!StringUtils.isEmpty(listPrice)){
								listOfPrices.add(listPrice);
							}
						break;
						
						case 7: //discount code
						case 10:
						case 13:
						case 16: 
						case 19: 
						case 22:
							String discountCode = CommonUtility.getCellValueStrinOrInt(cell);
							if(!StringUtils.isEmpty(discountCode)){
								listOfDiscount.add(discountCode);
							}
						break;
						} // end inner while loop

					}	
					priceGrids = eveanManufacturePriceGridParser.getBasePriceGrid(listOfPrices.toString(), listOfQuantity.toString(),
							listOfDiscount.toString(), "USD", existingProduct.getDistributorOnlyComments(), true, false,
							"", "", priceGrids, "", "");
					existingProduct.setDistributorOnlyComments("");
				} catch (Exception e) {
					_LOGGER.error(
							"Error while Processing ProductId and cause :" + existingProduct.getExternalProductId()
									+ " " + e.getMessage() + "at column number(increament by 1):" + headerName);
					ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: "
							+e.getMessage()+" at column number(increament by 1)"+columnIndex);
					productDaoObj.save(apiResponse.getErrors(),
							existingProduct.getExternalProductId()+"-Failed", asiNumber, batchId);
				}
			}
			if (!StringUtils.isEmpty(existingProduct.getExternalProductId())) {

			}
			repeatRows.clear();
			existingProduct.setPriceType("L");
			existingProduct.setPriceGrids(priceGrids);
			int num = postServiceImpl.postProduct(accessToken, existingProduct, asiNumber,
					batchId, environmentType);
		 	if(num ==1){
		 		numOfProductsSuccess.add("1");
		 	}else if(num == 0){
		 		numOfProductsFailure.add("0");
		 	}else{
		 		
		 	}		
	 	_LOGGER.info("list size>>>>>>>"+numOfProductsSuccess.size());
	 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
			//productMaps.put(existingProduct.getProductLevelSku(), existingProduct);
	 	 productDaoObj.saveErrorLog(asiNumber,batchId);
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
	/*private String getProductXid(Row row) {
		Cell xidCell = row.getCell(ApplicationConstants.CONST_NUMBER_ZERO);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if (StringUtils.isEmpty(productXid)) {
			xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
			productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}*/
	
	private String getSkuValue(Row row){
		Cell xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
		String skuVal = CommonUtility.getCellValueStrinOrInt(xidCell);
		return skuVal;
	}
 
public EveansManufacturePriceGridParser getEveanManufacturePriceGridParser() {
	return eveanManufacturePriceGridParser;
}
public void setEveanManufacturePriceGridParser(EveansManufacturePriceGridParser eveanManufacturePriceGridParser) {
	this.eveanManufacturePriceGridParser = eveanManufacturePriceGridParser;
}
public void setPostServiceImpl(PostServiceImpl postServiceImpl) {
	this.postServiceImpl = postServiceImpl;
}
public void setProductDaoObj(ProductDao productDaoObj) {
	this.productDaoObj = productDaoObj;
}
}
