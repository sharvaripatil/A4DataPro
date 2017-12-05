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

import parser.tekweld.TekweldProductAttributeParser;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.Size;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.CommonUtility;

public  class TekweldMapping implements IExcelParser {
	
	private static final Logger _LOGGER = Logger
			.getLogger(TekweldMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private TekweldProductAttributeParser tekweldAttribute;


	public String readExcel(String accessToken, Workbook workbook,
			Integer asiNumber, int batchId, String environmentType) {		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		Set<String> productXids = new HashSet<String>();
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();

		
		Product productExcelObj = new Product();
		ProductConfigurations productConfigObj = new ProductConfigurations();
    	StringBuilder listOfQuantity = new StringBuilder();
		StringBuilder listOfPrices = new StringBuilder();		
		String productName = null;
		String productId = null;
		String finalResult = null;
		Product existingApiProduct = null;
		int columnIndex = 0;
		String xid = null;
		Cell cell2Data = null;
		String ProdNo = null;
		boolean T =true;

		String ExstngDescription=null;
		List<ProductionTime> listofProductionTime = new ArrayList<ProductionTime>();
		List<ImprintSize> listofImprintSize = new ArrayList<ImprintSize>();
		Size sizeObj=new Size();
	
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
						cell2Data = nextRow.getCell(1);
						if (columnIndex + 1 == 2) {
							if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
								xid = cell.getStringCellValue();
							} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								xid = String.valueOf((int) cell
										.getNumericCellValue());
							} else {
								String newXID=cell2Data.toString();
								if(newXID.contains(","))
								{
									String newXIDArr[]=newXID.split(",");
									xid = newXIDArr[1];
								}
								else{
									xid = newXID;

								}
								
								
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
									productExcelObj.setProductConfigurations(productConfigObj);
						

									int num = postServiceImpl.postProduct(
											accessToken, productExcelObj,
											asiNumber, batchId, environmentType);
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
							        listOfQuantity = new StringBuilder();
									listOfPrices = new StringBuilder();
							    	
									productConfigObj = new ProductConfigurations();
							

								}
								if (!productXids.contains(xid)) {
									productXids.add(xid.trim());
								}
								existingApiProduct = postServiceImpl
										.getProduct(accessToken,
												xid = xid.replace("\t", ""), environmentType);
								if (existingApiProduct == null) {
									_LOGGER.info("Existing Xid is not available,product treated as new product");
									productExcelObj = new Product();
								} else {
										// productExcelObj=existingApiProduct;
										// productConfigObj=existingApiProduct.getProductConfigurations();
									List<Image> Img = existingApiProduct
											.getImages();
									productExcelObj.setImages(Img);
								 	 
							    	 List<String>categoriesList=existingApiProduct.getCategories();
							    	 productExcelObj.setCategories(categoriesList);
								    	 
								}
								// productExcelObj = new Product();
							}
							
						}

						switch (columnIndex + 1) {
					
						case 1://

							
							break;

						case 2:// ITEM
						    ProdNo=cell.getStringCellValue();
						    if (!StringUtils.isEmpty(ProdNo)) {
							productExcelObj.setExternalProductId(xid);
							productExcelObj.setAsiProdNo(ProdNo);
						    }

							break;
							
						case 3://Item Name 
							String ProductName=cell.getStringCellValue();
						    if (!StringUtils.isEmpty(ProductName)) {
						    productExcelObj.setName(ProductName);
						    productExcelObj.setSummary(ProductName);

						    }
							break;
							
						case 4:// Description 
							String description = CommonUtility.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(description)) {
								productExcelObj.setDescription(description);
							}
						

							break;
							
						case 5:// QTY 1

							break;
							
						case 6:// QTY 1 Price
		
		
							break;
							
						case 7:// QTY 2
							

							break;
							
						case 8:// QTY 2 Price
						

							break;
							
						case 9://QTY 3
 						
						

							break;
							
						case 10:// QTY 3 Price
			
					

							break;
							
							
						case 11: // QTY 4



							break;	
							
						case 12: // QTY 4 Price

		
							break;	
					
						case 13: // QTY 5

							break;	
							
						case 14: // QTY 5 Price

							

							break;	
					
						case 15: //QTY 6 

							break;	
					
						case 16: // QTY 6 Price

							
							break;	
							
						case 17: //BLANK PRICE

							
							break;	
							
						case 18: // CODE (EX: 5C)

							
							break;	
							
						case 19: //ITEM SIZE
							String ProductSize=cell.getStringCellValue();
							if (!StringUtils.isEmpty(ProductSize)) {
								sizeObj=tekweldAttribute.getProductsize(ProductSize);
								productConfigObj.setSizes(sizeObj);
							}
							
							break;	
							
						case 20: //IMPRINT SIZE
							String ImpringSizeValue=cell.getStringCellValue();
							if (!StringUtils.isEmpty(ImpringSizeValue)) {
								ImprintSize imprintsizeObj=new ImprintSize();
								imprintsizeObj.setValue(ImpringSizeValue);
								listofImprintSize.add(imprintsizeObj);
								productConfigObj.setImprintSize(listofImprintSize);
							}
							break;	
							
						case 21: //4CP Digital Printing Y OR N

						
							break;	
							
						case 22: // Setup

							
							break;	
							
						case 23: //Setup Code

						
							break;	
	
						case 24: //2nd Color / Location 

							
							break;	
		
						case 25: // Setup 2nd color


							break;	
							
							
						case 26: // 2nd color Setup Code


							break;	
							
							
						case 27: // Board or foam inserts



							break;	
							
							
						case 28: // Packing

							 
							break;	
							
							
						case 29: // Assemble box



							break;	
							
							
						case 30: // Insert Items:


							break;	
							
							
						case 31: // production time
							
							String ProductionTime=cell.getStringCellValue();
							if (!StringUtils.isEmpty(ProductionTime)) {
								ProductionTime prodTimeObj=new ProductionTime();
								prodTimeObj.setBusinessDays("10");
								prodTimeObj.setDetails(ProductionTime);
								listofProductionTime.add(prodTimeObj);
								productConfigObj.setProductionTime(listofProductionTime);
							}
							break;
							
						case 32: // Shipping
							
							String DistrubutorComment=cell.getStringCellValue();
							if (!StringUtils.isEmpty(DistrubutorComment)) {
							productExcelObj.setDistributorOnlyComments(DistrubutorComment);
							}
							break;	
							
			
						} // end inner while loop

					}
					// set product configuration objects

					// end inner while loop
					productExcelObj.setPriceType("L");
					productExcelObj.setCanOrderLessThanMinimum(T);
			
	
						/*priceGrids = pricegrid
								.getUpchargePriceGrid("1", "25",
										"V",
										"Less than Minimum", "false", "USD",
										"Can order less than minimum",
										"Less than Minimum Charge", "Other",
										new Integer(1), priceGrids);		 */
					 
					 
					productExcelObj.setPriceGrids(priceGrids);
					productExcelObj.setProductConfigurations(productConfigObj);		
					
					
				} catch (Exception e) {
					_LOGGER.error("Error while Processing ProductId and cause :"
								+ productExcelObj.getExternalProductId()
							+ " "
							+ e.getMessage() + "case" + columnIndex);
				}
			}
			workbook.close();

			int num = postServiceImpl.postProduct(accessToken, productExcelObj,
					asiNumber, batchId, environmentType);
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
			
			priceGrids = new ArrayList<PriceGrid>();
	        listOfQuantity = new StringBuilder();
			listOfPrices = new StringBuilder();

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


	public TekweldProductAttributeParser getTekweldAttribute() {
		return tekweldAttribute;
	}


	public void setTekweldAttribute(TekweldProductAttributeParser tekweldAttribute) {
		this.tekweldAttribute = tekweldAttribute;
	}



	

}
