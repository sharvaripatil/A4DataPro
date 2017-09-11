package com.a4tech.supplier.mapper;

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
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class MaxplusMapping implements IExcelParser {
	private static final Logger _LOGGER = Logger
			.getLogger(MaxplusMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	
	@Override
	public String readExcel(String accessToken, Workbook workbook,
			Integer asiNumber, int batchId) {		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		Set<String> productXids = new HashSet<String>();
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		List<Availability> listOfavaibility = new ArrayList<Availability>();
		List<ProductionTime> listProductionTime = new ArrayList<ProductionTime>();


		
		Product productExcelObj = new Product();
		ProductConfigurations productConfigObj = new ProductConfigurations();
		ProductionTime prodtimeObj=new ProductionTime();
		RushTime rushserviceObj=new RushTime();

		String productName = null;
		String productId = null;
		String finalResult = null;
		Product existingApiProduct = null;
		int columnIndex = 0;
		String xid = null;
		Cell cell2Data = null;
		String ProdNo = null;
		String AdditionalInfo1=null;
		

		try {

			_LOGGER.info("Total sheets in excel::"
					+ workbook.getNumberOfSheets());
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = sheet.iterator();
			_LOGGER.info("Started Processing Product");

			while (iterator.hasNext()) {

				try {
					Row nextRow = iterator.next();
					if (nextRow.getRowNum() < 1)
						continue;
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					if (productId != null) {
						productXids.add(productId);
					}
					boolean checkXid = false;

					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						/* int */columnIndex = cell.getColumnIndex();
						cell2Data = nextRow.getCell(3);
						if (columnIndex + 1 == 1) {
							if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
								xid = cell.getStringCellValue();
							} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								xid = String.valueOf((int) cell
										.getNumericCellValue());
							} else {
								ProdNo = CommonUtility
										.getCellValueStrinOrInt(cell2Data);
								ProdNo = ProdNo.substring(0, 14);
								xid = ProdNo;
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

										productExcelObj.setAvailability(listOfavaibility);
									productExcelObj.setPriceGrids(priceGrids);
			
									productExcelObj.setProductConfigurations(productConfigObj);
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

									productConfigObj = new ProductConfigurations();
							

								}
								if (!productXids.contains(xid)) {
									productXids.add(xid.trim());
								}
								existingApiProduct = postServiceImpl
										.getProduct(accessToken,
												xid = xid.replace("\t", ""));
								if (existingApiProduct == null) {
									_LOGGER.info("Existing Xid is not available,product treated as new product");
									productExcelObj = new Product();
								} else {
									// productExcelObj=existingApiProduct;
									// productConfigObj=existingApiProduct.getProductConfigurations();
								}
								// productExcelObj = new Product();
							}
							
						}

						switch (columnIndex + 1) {
						case 1://XID

							productExcelObj.setExternalProductId(xid);
							break;

						case 2:// Sku
							
							 ProdNo=cell.getStringCellValue();
							 productExcelObj.setAsiProdNo(ProdNo);

							break;
							
						case 11: // Product URL

							break;	
							
						case 12: // Name
							productName = cell.getStringCellValue();
							int len=productName.length();
							 if(len>60){
								String strTemp=productName.substring(0, 60);
								int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
								productName=(String) strTemp.subSequence(0, lenTemp);
							}
							productExcelObj.setName(productName);


							break;	
					
						case 15: // ShortDescription
							String Summary=cell.getStringCellValue();
							if (!StringUtils.isEmpty(Summary)) {
							productExcelObj.setSummary(Summary);	
							}
							break;	
							
						case 16: // LongDescription
							String description = cell.getStringCellValue();
							description=description.replace("?","").replace("ã","").replace("¡", "").replace(":", "");
							if (!StringUtils.isEmpty(description)) {
								productExcelObj.setDescription(description);
							} else {
								productExcelObj
										.setDescription(productName);
							}

							break;	
					
						case 19: // Large Image URL0

							break;	
							
						case 20: // Image

							break;	
							
						case 21: // Small Image URL1

							break;	
							
						case 22: // Large Image URL1

							break;	
							
						case 23: // Image

							break;	
							
						case 24: // Small Image URL2

							break;	
							
						case 25: // Large Image URL2

							break;	
							
						case 26: // Image

							break;	
							
						case 27: // Small Image URL3

							break;	
							
						case 28: // Large Image URL3

							break;	
							
						case 29: // Image

							break;	
							
						case 30: // Small Image URL4

							break;	
							
						case 31: // Large Image URL4

							break;	
							
						case 32: // Image

							break;	
							
						case 33: // Small Image URL5

							break;	
							
						case 34: // Large Image URL5

							break;	
							
						case 35: // Image

							break;	
							
						case 36: // Small Image URL6

							break;	
							
						case 37: // Large Image URL6

							break;	
							
						case 38: // Image

							break;	
							
						case 39: // Small Image URL7

							break;	
							
						case 40: // Large Image URL7

							break;	
							
						case 41: // Image

							break;	
							
						case 42: // Small Image URL8

							break;	
							
						case 43: // Large Image URL8

							break;	
							
						case 44: // Image

							break;	
							
						case 45: // Small Image URL9

							break;	
							
						case 46: // Large Image URL9

							break;	
													
							
						case 67: // Price1

							break;	
							
							
						case 68: // QtyBreak1

							break;	
							
							
						case 69: // Price2

							break;	
							
							
						case 70: // QtyBreak2

							break;	
							
							
						case 71: // Price3

							break;	
							
							
						case 72: // QtyBreak3

							break;	
							
							
						case 73: // Price4

							break;	
							
							
						case 74: // QtyBreak4

							break;	
							
							
						case 75: // Price5

							break;	
							
							
						case 76: // QtyBreak5

							break;	
							
							
						case 77: // Price6

							break;	
							
							
						case 78: // QtyBreak6

							break;	
							
							
						case 79: // Price7

							break;	
							
							
						case 80: // QtyBreak7

							break;	
							
							
						case 81: // Price8

							break;	
							
							
						case 82: // QtyBreak8

							break;	
							
							
						case 83: // Price9

							break;	
							
							
						case 84: // QtyBreak9

							break;	
							
							
						case 85: // Price10

							break;	
							
					
							
						case 107: // Price Point

							break;	
							
							
						case 108: // 

							break;	
							
							
						case 109: // Laser Engraving

							break;	
							
							
						case 110: // Setup Charge 3

							break;	
						
						case 111: // Setup Charge 2

							break;	
							
						case 112: // Imprint Area 3

							break;	
							
							
						case 113: // Imprint Area 2

							break;	
							
							
						case 114: // Imprint Area

							break;	
							
							
						case 115: // Imprint Method 3

							break;	
							
							
						case 116: // Imprint Method 2

							break;	
							
							
						case 117: // Imprint Method

							break;	
							
							
						case 118: // Size

							break;	
							
							
						case 119: // Rush Service
							String RushService=cell.getStringCellValue();
							if (!StringUtils.isEmpty(RushService)) {							
								rushserviceObj.setAvailable(true);	
								productConfigObj.setRushTime(rushserviceObj);
							}

							break;	
							
							
						case 120: // Product Color

							break;	
							
							
						case 121: // Approximate Production Time
							String ProductionTime=cell.getStringCellValue();
							if (!StringUtils.isEmpty(ProductionTime)) {
								prodtimeObj.setBusinessDays("7-10");
								listProductionTime.add(prodtimeObj);
							    productConfigObj.setProductionTime(listProductionTime);	
							}
							break;	
							
							
						case 122: // Items Per Carton

							break;	
							
							
						case 123: // Weight Per Carton (lbs.)

							break;	
							
							
						case 124: // Setup Charge

							break;	
							
							
						case 125: // Running Charge

							break;	
							
							
						case 126: // Imprint Includes

							break;	
							
							
						case 127: // Repeat Setup Charge

							break;	
							
							
						case 128: // Running Charge 2

							break;	
							
							
						case 129: // Running Charge 3

							break;	
							
							
						case 130: // Less Than Minimum Charge

							break;	
							
							
						case 131: // Additional Notes
							 AdditionalInfo1=cell.getStringCellValue();
							

							break;	
											
						case 150: // MinimumQty
							
							String AdditionalproductInfo2=cell.getStringCellValue();
							if (!StringUtils.isEmpty(AdditionalproductInfo2)) {
								AdditionalproductInfo2=AdditionalInfo1.concat("Minimum quantity order is:"+AdditionalproductInfo2);
								productExcelObj.setAdditionalProductInfo(AdditionalproductInfo2);
							}else
							{
							productExcelObj.setAdditionalProductInfo(AdditionalInfo1);
							}

							break;	
							
							
						

						} // end inner while loop

					}
					// set product configuration objects

					// end inner while loop
					productExcelObj.setPriceType("L");
					
					/*priceGrids = bellaPricegrid.getPriceGrids(
							"",
							"", "", "USD",
							"", true, "true",
							productName, "", priceGrids);*/
									

				} catch (Exception e) {
					_LOGGER.error("Error while Processing ProductId and cause :"
							+ productExcelObj.getExternalProductId()
							+ " "
							+ e.getMessage() + "case" + columnIndex);
				}
			}
			workbook.close();
			productExcelObj.setAvailability(listOfavaibility);
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
			productDaoObj.saveErrorLog(asiNumber, batchId);
			productConfigObj = new ProductConfigurations();

			return finalResult;
		} catch (Exception e) {
			_LOGGER.error("Error while Processing excel sheet "
					+ e.getMessage());
			return finalResult;
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet "
						+ e.getMessage());

			}
			_LOGGER.info("Complted processing of excel sheet ");
			_LOGGER.info("Total no of product:" + numOfProductsSuccess.size());
		}

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
	
	

}
