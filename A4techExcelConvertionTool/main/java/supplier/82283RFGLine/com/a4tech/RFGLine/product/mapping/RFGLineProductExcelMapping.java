package com.a4tech.RFGLine.product.mapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;

import com.a4tech.RFGLine.products.parser.DescrptionParser;
import com.a4tech.RFGLine.products.parser.PriceGridParser;
import com.a4tech.RFGLine.products.parser.ShippingEstimationParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class RFGLineProductExcelMapping {

	private static final Logger _LOGGER = Logger
			.getLogger(RFGLineProductExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ShippingEstimationParser shippingParserObj;
	private PriceGridParser rfgPriceGridParserObj;
	private DescrptionParser  DescrptionParserObj;
	private ProductDao productDaoObj;

	public String readExcel(String accessToken, Workbook workbook,
			Integer asiNumber, int batchId) {

		Set<String> productXids = new HashSet<String>();
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		ProductConfigurations productConfigObj = new ProductConfigurations();
		ShippingEstimate ShipingItem = new ShippingEstimate();
		Product productExcelObj = new Product();
		FOBPoint fobPointObj=new FOBPoint();
		StringBuilder listOfQuantity = new StringBuilder();
		StringBuilder listOfPrices = new StringBuilder();
		StringBuilder listOfNetPrice = new StringBuilder();
		StringBuilder listOfDiscount = new StringBuilder();
		String productName = null;
		String finalResult = null;
		String productId = null;
		String priceIncludes = null;
		String quantity = null;
		 String ShipQty1=null;
		 String ShipWeight1=null;
		 String ShipLength1=null;
		 String ShipWidth1 = null;
	
		try {

			_LOGGER.info("Total sheets in excel::"
					+ workbook.getNumberOfSheets());
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = sheet.iterator();
			_LOGGER.info("Started Processing Product");

			while (iterator.hasNext()) {

				try {
					Row nextRow = iterator.next();

					if (nextRow.getRowNum() == 0)
						continue;
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					if (productId != null) {
						productXids.add(productId);
					}
					boolean checkXid = false;

					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						String xid = null;
						int columnIndex = cell.getColumnIndex();

						if (columnIndex + 1 == 1) {
							if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
								xid = cell.getStringCellValue();
							} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								xid = String.valueOf((int) cell
										.getNumericCellValue());
							} else {

							}
							checkXid = true;
						} else {
							checkXid = false;
						}
						if (checkXid) {
							if (!productXids.contains(xid)) {
								if (nextRow.getRowNum() != 1) {
									System.out
											.println("Java object converted to JSON String, written to file");
									productExcelObj.setPriceGrids(priceGrids);
									productExcelObj
											.setProductConfigurations(productConfigObj);
									int num = postServiceImpl.postProduct(
											accessToken, productExcelObj,
											asiNumber, batchId);
									if (num == 1) {
										numOfProductsSuccess.add("1");
									} else if (num == 0) {
										numOfProductsFailure.add("0");
									} else {

									}
									_LOGGER.info("list size>>>>>>>"
											+ numOfProductsSuccess.size());
									_LOGGER.info("Failure list size>>>>>>>"
											+ numOfProductsFailure.size());
									priceGrids = new ArrayList<PriceGrid>();
									productConfigObj = new ProductConfigurations();

								}
								if (!productXids.contains(xid)) {
									productXids.add(xid);
								}
								productExcelObj = new Product();
							}
						}

						switch (columnIndex + 1) {

						case 1:// SuplItemNo
							  if(cell.getCellType() == Cell.CELL_TYPE_STRING){ 
							    	productId = String.valueOf(cell.getStringCellValue().trim());
							    }else if
							     (cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
							    	productId = String.valueOf((int)cell.getNumericCellValue());
							     }
							     productExcelObj.setExternalProductId(productId);	
					   

							break;
						case 2:// ItemName
							productName = cell.getStringCellValue();
							if (!StringUtils.isEmpty(productName)) {
								productExcelObj.setName(cell
										.getStringCellValue());
							} else {
								productExcelObj
										.setName(ApplicationConstants.CONST_STRING_EMPTY);
							}

							break;
						case 3:// Description
							String Description=cell.getStringCellValue();
							_LOGGER.info("description"
									+ Description);
							productExcelObj= DescrptionParserObj.getDescription(Description,productExcelObj);
							
							break;
						case 4:// OriginationZipCode
							String fobpoint=null;
							if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
								fobpoint = String.valueOf(cell
										.getStringCellValue());
							} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								fobpoint = String.valueOf((int) cell
										.getNumericCellValue());
							}
							
							List<FOBPoint> fobPointList = new ArrayList<FOBPoint>();
							if(fobpoint.contains("33125")){
							fobPointObj.setName("Miami, FL 33125 USA");
							fobPointList.add(fobPointObj);
							productExcelObj.setFobPoints(fobPointList);
							}

							break;

						case 5:// ShipQty1
							
							 ShipQty1 = CommonUtility.getCellValueStrinOrInt(cell);

							break;

						case 6: // ShipWeight1
								 ShipWeight1 = CommonUtility.getCellValueStrinOrInt(cell);

							break;

						case 7:// ShipLength1
							 ShipLength1 = CommonUtility.getCellValueStrinOrInt(cell);
							
							break;

						case 8: // ShipWidth1
							 ShipWidth1 =CommonUtility.getCellValueStrinOrInt(cell);
							break;

						case 9: // ShipHeight1
						     String ShipHeight1 = CommonUtility.getCellValueStrinOrInt(cell);
						   ShipingItem = shippingParserObj.getShippingEstimateValues(ShipQty1, ShipWeight1, ShipLength1, ShipWidth1, ShipHeight1);
			               productConfigObj.setShippingEstimates(ShipingItem);

						
							break;

						case 10:// ShipQty2
							break;
						case 11: // ShipWeight2
							break;
                    	case 12: // ShipLength2
                    		break;
                        case 13: // ShipWidth2
                        	break;
						case 14: // ShipHeight2
							break;
						case 15:// ShipQty3
							break;
						case 16: // ShipWeight3
							break;
						case 17: // ShipLength3
							break;
						case 18: // ShipWidth3
							break;
						case 19:// ShipHeight3
							break;
						case 20:// ShipQty4
							break;
						case 21:// ShipWeight4
							break;
						case 22:// ShipLength4
							break;
                     	case 23:
							// ProductionTime
							String prodTimeLo=null;
						    List<ProductionTime> productionTimeList = new ArrayList<ProductionTime>();
						    ProductionTime productionTime = new ProductionTime();
						     prodTimeLo=CommonUtility.getCellValueStrinOrInt(cell);
						     productionTime.setBusinessDays(prodTimeLo);
						     productionTimeList.add(productionTime);
						     productConfigObj.setProductionTime(productionTimeList);
						   
							break;
						case 24:
							// ProductVersionName
							break;
						case 25:

							// SetupQty1

							break;

						case 26:
							// SetupNet1
							break;
						case 27:
							// SetupRetail1
						
							break;
						case 28:
							// SetupMargin1
							break;
						case 29:
							// SetupDisplay1
							break;
						case 30:// Qty1
						case 31:// Qty2
						case 32:// Qty3
						case 33:// Qty4
						case 34:// Qty5
						case 35:// Qty6
						case 36:// Qty7
						case 37:// Qty8
							try{
								if(cell.getCellType() == Cell.CELL_TYPE_STRING){
									quantity = cell.getStringCellValue();
							         if(!StringUtils.isEmpty(quantity) && !quantity.equals("0")){
							        	 listOfQuantity.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							         }
								}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
									int quantity1 = (int)cell.getNumericCellValue();
							         if(!StringUtils.isEmpty(quantity1) && quantity1 !=0){
							        	 listOfQuantity.append(quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							         }
								}else{
								}
							}catch (Exception e) {
								_LOGGER.info("Error in base price Quantity field");
							}
							break;
						case 38:// Net1
						case 39:// Net2
						case 40:// Net3
						case 41:// Net4
						case 42:// Net5
						case 43:// Net6
						case 44:// Net7
						case 45:// Net8
							if(cell.getCellType() == Cell.CELL_TYPE_STRING){
								quantity = cell.getStringCellValue();
						         if(!StringUtils.isEmpty(quantity)){
						        	 listOfNetPrice.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						         }
							}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
								double quantity1 = (double)cell.getNumericCellValue();
						         if(!StringUtils.isEmpty(quantity1)){
						        	 listOfNetPrice.append(quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						         }
							}else{
							}

							break;
						case 46: //Retail1
						case 47:// Retail2
						case 48: // Retail3
						case 49:// Retail4
						case 50:// Retail5
						case 51:// Retail6
						case 52:// Retail7
						case 53:// Retail8
							 try{
								 if(cell.getCellType() == Cell.CELL_TYPE_STRING){
										quantity = cell.getStringCellValue();
								         if(!StringUtils.isEmpty(quantity)&& !quantity.equals("0")){
								        	 listOfPrices.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								         }
									}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
										double quantity1 = (double)cell.getNumericCellValue();
								         if(!StringUtils.isEmpty(quantity1)){
								        	 listOfPrices.append(quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								         }
									}else{
									}  
							 }catch (Exception e) {
								_LOGGER.info("Error in base price prices field");
							}

							break;
						case 54:// Margin1
						case 55:// Margin2
						case 56:// Margin3
						case 57:// Margin4
						case 58:// Margin5
						case 59:// Margin6
						case 60:// Margin7
						case 61:// Margin8
							try{
 							if(cell.getCellType() == Cell.CELL_TYPE_STRING){
								quantity = cell.getStringCellValue();
						         if(!StringUtils.isEmpty(quantity) && !quantity.equals("0")){
						        	 listOfDiscount.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						         }
							}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
								double quantity1 = (double)cell.getNumericCellValue();
						         if(!StringUtils.isEmpty(quantity1)){
						        	 listOfDiscount.append(quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						         }
							}else{
							}  
							}catch (Exception e) {
								_LOGGER.info("Error in pricePerUnit field");
							}

							break;

						} // end inner while loop

					}
					// set product configuration objects
		

					// end inner while loop
					
					productExcelObj.setPriceType("L");
					if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
						priceGrids = rfgPriceGridParserObj.getPriceGrids(listOfPrices.toString(),listOfNetPrice.toString(), 
								         listOfQuantity.toString(), listOfDiscount.toString(), "USD",
								         priceIncludes, true, "N", productName,"",priceGrids);	
					}else{
						priceGrids = rfgPriceGridParserObj.getPriceGrids(listOfPrices.toString(),listOfNetPrice.toString(), 
						         listOfQuantity.toString(), listOfDiscount.toString(), "USD",
						         priceIncludes, true, "Y", productName,"",priceGrids);	
					}
					

					/*
					 * if(UpCharCriteria != null &&
					 * !UpCharCriteria.toString().isEmpty()){ priceGrids =
					 * priceGridParser
					 * .getUpchargePriceGrid(UpCharQuantity.toString(),
					 * UpCharPrices.toString(), UpCharDiscount.toString(),
					 * UpCharCriteria.toString(), upChargeQur, currencyType,
					 * upChargeName, upchargeType, upChargeLevel, new
					 * Integer(1), priceGrids); }
					 */

				
				
					listOfPrices = new StringBuilder();
					listOfNetPrice  = new StringBuilder();
					listOfQuantity  = new StringBuilder();
					listOfDiscount  = new StringBuilder();
				

				} catch (Exception e) {
					_LOGGER.error("Error while Processing ProductId and cause :"
							+ productExcelObj.getExternalProductId()
							+ " "
							+ e.getMessage());
				}
			}
			workbook.close();

			productExcelObj.setPriceGrids(priceGrids);
			productExcelObj.setProductConfigurations(productConfigObj);

			int num = postServiceImpl.postProduct(accessToken, productExcelObj,
					asiNumber, batchId);
			if (num == 1) {
				numOfProductsSuccess.add("1");
			} else if (num == 0) {
				numOfProductsFailure.add("0");
			} else {

			}
			_LOGGER.info("list size>>>>>>" + numOfProductsSuccess.size());
			_LOGGER.info("Failure list size>>>>>>"
					+ numOfProductsFailure.size());
			finalResult = numOfProductsSuccess.size() + ","
					+ numOfProductsFailure.size();
			productDaoObj.getErrorLog(asiNumber, batchId);
			return finalResult;
		} catch (Exception e) {
			_LOGGER.error("Error while Processing excel sheet ,Error message: "
					+ e.getMessage());
			return finalResult;
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet, Error message: "
						+ e.getMessage());

			}
			_LOGGER.info("Complted processing of excel sheet ");
			_LOGGER.info("Total no of product:" + numOfProductsSuccess.size());
		}

	}

	public DescrptionParser getDescrptionParserObj() {
		return DescrptionParserObj;
	}

	public void setDescrptionParserObj(DescrptionParser descrptionParserObj) {
		DescrptionParserObj = descrptionParserObj;
	}

	public PriceGridParser getRfgPriceGridParserObj() {
		return rfgPriceGridParserObj;
	}

	public void setRfgPriceGridParserObj(PriceGridParser rfgPriceGridParserObj) {
		this.rfgPriceGridParserObj = rfgPriceGridParserObj;
	}

	public PostServiceImpl getPostServiceImpl() {
		return postServiceImpl;
	}

	public void setPostServiceImpl(PostServiceImpl postServiceImpl) {
		this.postServiceImpl = postServiceImpl;
	}

	public ProductDao getProductDaoObj() {
		return productDaoObj;
	}

	public void setProductDaoObj(ProductDao productDaoObj) {
		this.productDaoObj = productDaoObj;
	}

	public ShippingEstimationParser getShippingParserObj() {
		return shippingParserObj;
	}

	public void setShippingParserObj(ShippingEstimationParser shippingParserObj) {
		this.shippingParserObj = shippingParserObj;
	}

	
	
	
	
}
