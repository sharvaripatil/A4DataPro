package parser.ballPro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class BallProPricingMapping{
	private static final Logger _LOGGER = Logger.getLogger(BallProPricingMapping.class);
    private BallProPriceGridParser ballProPriceGridParser;
	private BallProInformationAttributeParser ballProAttributeParser;
	
	public Map<String, Product> readMapper(Map<String, Product> productMaps, Sheet sheet) {

		int columnIndex = 0;

		Set<String> productXids = new HashSet<String>();
		List<String> repeatRows = new ArrayList<>();
		//Product productExcelObj = new Product();
		ProductConfigurations productConfig = null;
		String productId = null;
		//String xid = null;
	   //String prdXid = null;
		StringJoiner listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfPrices   = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String priceType    = "";
		String  type  = "";
		String headerName ="";
		List<String> productIds = new ArrayList<>();
		List<PriceGrid> priceGrids = null;
		//String skuId = "";
		boolean isFirstProduct = true;
		//List<String> basePriceGrids = new ArrayList<>();
		String priceUnitName = "";
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
					/*if (nextRow.getRowNum() == ApplicationConstants.CONST_INT_VALUE_ONE) {
						productId = getProductXid(nextRow);
						Cell cell1 = nextRow.getCell(1);
						skuId = CommonUtility.getCellValueStrinOrInt(cell1);
					}*/	
					//prdXid = CommonUtility.getCellValueStrinOrInt(cell1);
					// this condition used to check xid is present list or not ,
					// if xid present in Map means already fetch product from
					// Map
					/*if (!productIds.contains(productId)) {
						existingProduct = productMaps.get(productId);
					}*/
					if(isFirstProduct){
						existingProduct = productMaps.get(productId);
						productConfig = existingProduct.getProductConfigurations();
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
									if(countNoOfBasePriceGrids(priceGrids) == ApplicationConstants.CONST_INT_VALUE_ONE){
										priceGrids = removeConfiguration(priceGrids);
									}
									existingProduct.setPriceGrids(priceGrids);
									existingProduct.setProductConfigurations(productConfig);
									productMaps.put(existingProduct.getProductLevelSku(), existingProduct);
									_LOGGER.info("Processed xid/Sku: "+existingProduct.getProductLevelSku());
									repeatRows.clear();
									existingProduct = productMaps.get(productId);
									if(existingProduct == null){
										_LOGGER.warn("Product is not available: "+productId);
										break;
									}
									productConfig = existingProduct.getProductConfigurations();
									priceGrids = existingProduct.getPriceGrids();
									//priceGrids = new ArrayList<>();
									//basePriceGrids = new ArrayList<>();
									 listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
									 listOfPrices   = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
									 listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
									 priceUnitName = "";
								}
								if (!productXids.contains(productId)) {
									productXids.add(productId);
									repeatRows.add(productId);
								}
							}
							 listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							 listOfPrices   = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							 listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							 priceUnitName = "";
						} else {
							if (productXids.contains(productId) && repeatRows.size() != 1) {
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
						case "price_unit":
							priceUnitName = cell.getStringCellValue();
							break;
						case "Price_1": //price
						case "Price_2":
						case "Price_3":
						case "Price_4":
						case "Price_5":
							String listPrice = CommonUtility.getCellValueDouble(cell);
							if(!StringUtils.isEmpty(listPrice) && !type.equalsIgnoreCase("special")){
								listOfPrices.add(listPrice);
							}
							break;
						case "Qty_1_Min": //quantity
						case "Qty_2_Min":
						case "Qty_3_Min":
						case "Qty_4_Min": 
						case "Qty_5_Min": 
							String priceQty = CommonUtility.getCellValueStrinOrInt(cell);
							if(!StringUtils.isEmpty(priceQty)&& !type.equalsIgnoreCase("special")){
								listOfQuantity.add(priceQty);
							}
						break;
						
						case "Code_1": //discount code
						case "Code_2":
						case "Code_3":
						case "Code_4": 
						case "Code_5": 
							String discountCode = CommonUtility.getCellValueStrinOrInt(cell);
							if(!StringUtils.isEmpty(discountCode) && !type.equalsIgnoreCase("special")){
								listOfDiscount.add(discountCode);
							}
						break;
						} // end inner while loop

					}
					boolean isQurFlag = false;
					String basePriceName = "";
					String basePriceCriteria = "";
					String imprintMethodVal = "";
					if("call_for_price".equalsIgnoreCase(priceType)){
						isQurFlag = true;
					}//decorative
					if("decorative".equalsIgnoreCase(type)){
						basePriceName = "decorative";
						basePriceCriteria = "decorative";
						imprintMethodVal = "decorative";
					} else if(type.equalsIgnoreCase("blank")) {
						basePriceCriteria = "UNIMPRINTED";
						imprintMethodVal = "UNIMPRINTED";
					}
					if(StringUtils.isEmpty(priceUnitName)){
						priceUnitName = "";
					}
					    if(!type.equalsIgnoreCase("special") && !"special_blank".equalsIgnoreCase(type)){
					    //	String imprintMethodvals = "";
					    	/*if(basePriceCriteria.equals("decorative")){
					    		// caller method used to collecting existing imprint methods values if type is decorative
					    		 imprintMethodvals = getImprintMethodValues(productConfig.getImprintMethods());
					    		if(StringUtils.isEmpty(imprintMethodvals)){
					    			List<ImprintMethod> imprintMethods =proGolfAttributeParser.
							    			getProductImprintMethods(CommonUtility.getValuesOfArray("Imprint", ","), 
							    					productConfig.getImprintMethods());
					    			productConfig.setImprintMethods(imprintMethods);
					    			basePriceCriteria = "Imprint";
					    		} else {
					    			basePriceCriteria = imprintMethodvals;
					    		}
					    	}*/
					    	basePriceCriteria = "IMMD"+":"+basePriceCriteria; 
					    		priceGrids = ballProPriceGridParser.getBasePriceGrid(listOfPrices.toString(), 
										listOfQuantity.toString(), listOfDiscount.toString(), "USD",
										         "", true, isQurFlag, basePriceName,basePriceCriteria,priceGrids,priceUnitName,"");
						    //	basePriceGrids.add("1");
					 
					    	if(!StringUtils.isEmpty(imprintMethodVal) && !imprintMethodVal.equalsIgnoreCase("decorative")){
					    		List<ImprintMethod> imprintMethods =ballProAttributeParser.
						    			getProductImprintMethods(CommonUtility.getValuesOfArray(imprintMethodVal, ","), 
						    					productConfig.getImprintMethods());
					    		productConfig.setImprintMethods(imprintMethods);
					    	}
					    }					
				} catch (Exception e) {
					_LOGGER.error(
							"Error while Processing ProductId and cause :" + existingProduct.getExternalProductId()
									+ " " + e.getMessage() + "at column number(increament by 1):" + headerName);
				}
			}
			if (!StringUtils.isEmpty(existingProduct.getExternalProductId())) {

			}
			repeatRows.clear();
			existingProduct.setPriceType("L");
			if(countNoOfBasePriceGrids(priceGrids) == ApplicationConstants.CONST_INT_VALUE_ONE){
				priceGrids = removeConfiguration(priceGrids);
			}
			existingProduct.setPriceGrids(priceGrids);
			existingProduct.setProductConfigurations(productConfig);
			productMaps.put(existingProduct.getProductLevelSku(), existingProduct);
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
	private List<PriceGrid> removeConfiguration(List<PriceGrid> oldPriceGrid){
		List<PriceGrid> newPriceGrid = new ArrayList<>();
		 for (PriceGrid priceGrid : oldPriceGrid) {
			   if(priceGrid.getIsBasePrice()){
				   priceGrid.setPriceConfigurations(new ArrayList<>());
				   newPriceGrid.add(priceGrid);
			   } else {
				   newPriceGrid.add(priceGrid);
			   }
			  
		}
		return newPriceGrid;
	}
	private String getSkuValue(Row row){
		Cell xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
		String skuVal = CommonUtility.getCellValueStrinOrInt(xidCell);
		return skuVal;
	}
  /*private String getImprintMethodValues(List<ImprintMethod> imprintMethods){
	  if(CollectionUtils.isEmpty(imprintMethods)){
		  return "";
	  }
		String imprintMethodValues = imprintMethods.stream().map(ImprintMethod::getAlias)
				.collect(Collectors.joining(","));
		return imprintMethodValues;
  }*/
   private List<String> getAllSizeValues(Size sizeObj){
	 Apparel apparealObj = sizeObj.getApparel();
	List<Value> listOfValues = apparealObj.getValues();
	List<String> sizeAttributeVals = listOfValues.stream().map(Value::getValue).collect(Collectors.toList());
	   return sizeAttributeVals;
   }
   private String getSizeCommonValues(List<String> supplierSizes,List<String> standrdSizes){
	   standrdSizes.retainAll(supplierSizes);
	   String finalSizeValues = standrdSizes.stream().collect(Collectors.joining(","));
	   return finalSizeValues;
   }
   private int countNoOfBasePriceGrids(List<PriceGrid> priceGrid){
   	int basePriceCount = 0;
   	for (PriceGrid priceGrid2 : priceGrid) {
			if(priceGrid2.getIsBasePrice() == true){
				basePriceCount++;
			}
		}
   	return basePriceCount;
   }
   public BallProPriceGridParser getBallProPriceGridParser() {
		return ballProPriceGridParser;
	}
	public void setBallProPriceGridParser(BallProPriceGridParser ballProPriceGridParser) {
		this.ballProPriceGridParser = ballProPriceGridParser;
	}
	public BallProInformationAttributeParser getBallProAttributeParser() {
		return ballProAttributeParser;
	}
	public void setBallProAttributeParser(BallProInformationAttributeParser ballProAttributeParser) {
		this.ballProAttributeParser = ballProAttributeParser;
	}
}
