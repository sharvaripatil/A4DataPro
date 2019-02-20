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
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.Size;
import com.a4tech.product.service.imple.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.BloominPromotions.BloominPromotionsAttributeParser;
import parser.BloominPromotions.BloominPromotionsPriceGridParser;

public class BloominPromotionsMapper implements IExcelParser {

	private static final Logger _LOGGER = Logger.getLogger(BloominPromotionsMapper.class);

	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private BloominPromotionsAttributeParser bloominPromotionAttributeParser;
	private BloominPromotionsPriceGridParser bloominPromotionPriceGridParser;

	@Override
	public String readExcel(String accessToken, Workbook workbook, Integer asiNumber, int batchId,
			String environmentType) {

		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String> productXids = new HashSet<String>();
		Product productExcelObj = new Product();
		ProductConfigurations productConfigObj = new ProductConfigurations();
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		StringBuilder listOfQuantity = new StringBuilder();
		StringBuilder listOfPrices = new StringBuilder();
		StringBuilder listOfDiscount = new StringBuilder();
		List<String> ProductProcessedList = new ArrayList<>();
		List<String> repeatRows = new ArrayList<>();
		try {
			_LOGGER.info("Total sheets in excel::" + workbook.getNumberOfSheets());
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = sheet.iterator();
			_LOGGER.info("Started Processing Product");
			Product existingApiProduct = null;
			String xid = null;
			StringBuilder keywords = new StringBuilder();
			StringBuilder setupCharge = new StringBuilder();
			String asiPrdNo = "";
			Set<String> imageList = new HashSet<>();
			while (iterator.hasNext()) {
				try {
					Row nextRow = iterator.next();
					if (nextRow.getRowNum() == 0)
						continue;
					// this value is check first column or not becuase first
					// column is skipped if value is not present
					boolean isFirstColum = true;
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					if (xid != null) {
						productXids.add(xid);
						repeatRows.add(xid);
					}
					boolean checkXid = false;
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();

						int columnIndex = cell.getColumnIndex();
						if (columnIndex + 1 == 1) {
							xid = getProductXid(nextRow);
							checkXid = true;
							isFirstColum = false;
						} else {
							checkXid = false;
							if (isFirstColum) {
								xid = getProductXid(nextRow);
								checkXid = true;
								isFirstColum = false;
							}
						}
						if (checkXid) {
							if (!productXids.contains(xid)) {
								if (nextRow.getRowNum() != 1) {
									System.out.println("Java object converted to JSON String, written to file");
									productExcelObj.setPriceType("L");
									List<String> productKeyWords = bloominPromotionAttributeParser
											.getProductKeywords(keywords.toString());
									productExcelObj.setProductKeywords(productKeyWords);
									if (!StringUtils.isEmpty(setupCharge)) {
										List<ImprintMethod> imprintMethodList = bloominPromotionAttributeParser
												.getImprintMethodValues("Printed");
										productConfigObj.setImprintMethods(imprintMethodList);
										priceGrids = bloominPromotionAttributeParser
												.getSetupCharge(setupCharge.toString(), priceGrids);
									}
									priceGrids = bloominPromotionPriceGridParser.getBasePriceGrids(listOfPrices.toString(),
											listOfQuantity.toString(), listOfDiscount.toString(), "USD", "", true, "False", "", "",
											priceGrids);
									List<Image> listOfImages = bloominPromotionAttributeParser.getImages(imageList);
									productExcelObj.setImages(listOfImages);
									String summary = getSummary(productExcelObj.getDescription(), productExcelObj.getName());
									productExcelObj.setSummary(summary);
									productExcelObj.setProductConfigurations(productConfigObj);
									productExcelObj.setPriceGrids(priceGrids);
									int num = postServiceImpl.postProduct(accessToken, productExcelObj, asiNumber,
											batchId, environmentType);
									// ProductProcessedList.add(productExcelObj.getExternalProductId());
									if (num == 1) {
										numOfProductsSuccess.add("1");
									} else if (num == 0) {
										numOfProductsFailure.add("0");
									} else {
									}
									_LOGGER.info("list size>>>>>>>" + numOfProductsSuccess.size());
									_LOGGER.info("Failure list size>>>>>>>" + numOfProductsFailure.size());
									priceGrids = new ArrayList<PriceGrid>();
									listOfPrices = new StringBuilder();
									listOfQuantity = new StringBuilder();
									productConfigObj = new ProductConfigurations();
									repeatRows.clear();
									keywords = new StringBuilder();
									listOfDiscount = new StringBuilder();
									asiPrdNo = "";
									setupCharge = new StringBuilder();
									imageList = new HashSet<>();
								}
								if (!productXids.contains(xid)) {
									productXids.add(xid.trim());
									repeatRows.add(xid.trim());
								}
								existingApiProduct = postServiceImpl.getProduct(accessToken, xid, environmentType);
								if (existingApiProduct == null) {
									_LOGGER.info("Existing Xid is not available,product treated as new product");
									productExcelObj = new Product();
									productExcelObj.setExternalProductId(xid);
								} else {
									productExcelObj = existingApiProduct;
									productConfigObj = existingApiProduct.getProductConfigurations();
									productExcelObj = bloominPromotionAttributeParser
											.keepExistingProductData(existingApiProduct);
									productConfigObj = productExcelObj.getProductConfigurations();
									productExcelObj.setExternalProductId(xid);
									/*
									 * String confthruDate=existingApiProduct.
									 * getPriceConfirmedThru();
									 * productExcelObj.setPriceConfirmedThru(
									 * confthruDate);
									 */
								}
								// productExcelObj = new Product();
							}
						} else {
							if (productXids.contains(xid) && repeatRows.size() != 1) {
								if (isRepeateColumn(columnIndex + 1)) {
									continue;
								}
							}
						}

						switch (columnIndex + 1) {

						case 1:// xid
							productExcelObj.setExternalProductId(xid);

							break;
						case 4: // productId
							asiPrdNo = cell.getStringCellValue();
							asiPrdNo = getFinalProductNo(asiPrdNo);
							productExcelObj.setAsiProdNo(asiPrdNo);
							break;
						case 5:// name
							String prdName = cell.getStringCellValue();
							if (prdName.toUpperCase().contains(asiPrdNo) || prdName.toUpperCase().contains(asiPrdNo.toUpperCase())) {
								prdName = CommonUtility.removeSpecificWord(prdName, asiPrdNo);
								if (prdName.contains("()")) {
									prdName = prdName.replaceAll("[()]", "").trim();
								}
								prdName = prdName.replaceAll(asiPrdNo, "");
							}
							productExcelObj.setName(CommonUtility.getStringLimitedChars(prdName, 60));
							break;
						case 10:// description
							String description = cell.getStringCellValue();
							if(!StringUtils.isEmpty(description)){
								if (description.contains("<BR>")) {
									description = description.replaceAll("<BR>", ",");
								}
								if (description.toUpperCase().contains(asiPrdNo)) {
									description = CommonUtility.removeSpecificWord(description, asiPrdNo);
								}
								productExcelObj.setDescription(CommonUtility.getStringLimitedChars(description, 800));
							}
							break;
						case 11:// AdditionalProductInfo
							String additionalInfo = cell.getStringCellValue();
							if (!StringUtils.isEmpty(additionalInfo)) {
								if (additionalInfo.contains("<BR>")) {
									additionalInfo = additionalInfo.replaceAll("<BR>", ",");
								}
								productExcelObj.setAdditionalProductInfo(additionalInfo);
							}
							break;
						case 12:// DistributorViewOnly
							// String
							// distributorViewOnly=cell.getStringCellValue();
							// there is no data in this column
							// productExcelObj.setDistributorViewOnly(true);
							break;

						case 15: // size
							String size = CommonUtility.getCellValueDouble(cell);
							if (!StringUtils.isEmpty(size)) {
								Size sizeObj = null;
								if (size.equalsIgnoreCase("various") || size.equalsIgnoreCase("varies")) {
									sizeObj = bloominPromotionAttributeParser.getProductSizeAsOther(size);
								} else {
									sizeObj = bloominPromotionAttributeParser.getProductSize(size);
								}
								productConfigObj.setSizes(sizeObj);
							}
							break;

						case 17:// fob
							String fobVal = CommonUtility.getCellValueStrinOrInt(cell);
							List<FOBPoint> listOfFobPoint = bloominPromotionAttributeParser.getFobPoint(fobVal,
									accessToken, environmentType);
							productExcelObj.setFobPoints(listOfFobPoint);
							break;

						case 19: // origin
							String originVal = cell.getStringCellValue();
							if (!StringUtils.isEmpty(originVal) && !originVal.equals("Other/None")) {
								List<Origin> listOfOrigin = bloominPromotionAttributeParser.getOriginValues(originVal);
								productConfigObj.setOrigins(listOfOrigin);
							}
							break;

						case 47: // Images
							String image = cell.getStringCellValue();
							if (!StringUtils.isEmpty(image)) {
								imageList.add(image);
								/*
								 * List<Image> listOfImages =
								 * bloominPromotionAttributeParser.getImages(
								 * image);
								 * productExcelObj.setImages(listOfImages);
								 */
							}
							break;
						case 48: // production Time
							String prdTime = CommonUtility.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(prdTime)) {
								List<ProductionTime> productionTimeList = bloominPromotionAttributeParser
										.getProductionTime(prdTime);
								productConfigObj.setProductionTime(productionTimeList);
							}
							break;

						case 54: // Description
						case 55: // keywords
						case 56: // Colors
						case 57: // Themes
						case 58:// size -- value
							String productKeyword = cell.getStringCellValue();
							if (!StringUtils.isEmpty(productKeyword)) {
								productKeyword = productKeyword.trim();
								keywords.append(productKeyword).append(",");
								/*		*/
							}

							break;
						case 66: // setup charge-qty
							String qty = CommonUtility.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(qty)) {
								setupCharge.append(qty).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							}
							break;

						case 68: // price
							String price = CommonUtility.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(price)) {
								setupCharge.append(price).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							}
							break;

						case 69: // discount
							String discount = CommonUtility.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(discount)) {
								setupCharge.append(discount);
							}
							break;
						case 72: // qty
						case 73:
						case 74:
						case 75:
						case 76:
							String quantity = CommonUtility.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(quantity) && !quantity.equals("0")) {
								listOfQuantity.append(quantity)
										.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							}
							break;

						case 82: // prices --list price
						case 83:
						case 84:
						case 85:
						case 86:
							String prices = CommonUtility.getCellValueDouble(cell);
							if (!StringUtils.isEmpty(prices) && !prices.equals("0")) {
								listOfPrices.append(prices).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							}
							break;
						case 87: // discount
						case 88:
						case 89:
						case 90:
						case 91:
							String basePricediscount = CommonUtility.getCellValueDouble(cell);
							if (!StringUtils.isEmpty(basePricediscount) && !basePricediscount.equals("0")) {
								listOfDiscount.append(basePricediscount)
										.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							}
							break;
						} // end switch
					} // end inner while loop
						// set product configuration objects
						// end inner while loop
						// it is store xid if once mapping completed
					/*priceGrids = bloominPromotionPriceGridParser.getBasePriceGrids(listOfPrices.toString(),
							listOfQuantity.toString(), listOfDiscount.toString(), "USD", "", true, "False", "", "",
							priceGrids);*/
				} catch (Exception e) {
					_LOGGER.error("Error while Processing ProductId and cause :"
							+ productExcelObj.getExternalProductId() + " " + e.getMessage());
				}
			}
			workbook.close();
			/////////////////////
			productExcelObj.setPriceType("L");
			List<String> productKeyWords = bloominPromotionAttributeParser.getProductKeywords(keywords.toString());
			productExcelObj.setProductKeywords(productKeyWords);
			if (!StringUtils.isEmpty(setupCharge)) {
				List<ImprintMethod> imprintMethodList = bloominPromotionAttributeParser
						.getImprintMethodValues("Printed");
				productConfigObj.setImprintMethods(imprintMethodList);
				priceGrids = bloominPromotionAttributeParser.getSetupCharge(setupCharge.toString(), priceGrids);
			}
			List<Image> listOfImages = bloominPromotionAttributeParser.getImages(imageList);
			productExcelObj.setImages(listOfImages);
			priceGrids = bloominPromotionPriceGridParser.getBasePriceGrids(listOfPrices.toString(),
					listOfQuantity.toString(), listOfDiscount.toString(), "USD", "", true, "False", "", "",
					priceGrids);
			String summary = getSummary(productExcelObj.getDescription(), productExcelObj.getName());
			productExcelObj.setSummary(summary);
			productExcelObj.setProductConfigurations(productConfigObj);
			productExcelObj.setPriceGrids(priceGrids);
			int num = postServiceImpl.postProduct(accessToken, productExcelObj, asiNumber, batchId, environmentType);
			if (num == 1) {
				numOfProductsSuccess.add("1");
			} else if (num == 0) {
				numOfProductsFailure.add("0");
			} else {

			}
			_LOGGER.info("list size>>>>>>" + numOfProductsSuccess.size());
			_LOGGER.info("Failure list size>>>>>>" + numOfProductsFailure.size());
			finalResult = numOfProductsSuccess.size() + "," + numOfProductsFailure.size();
			productDaoObj.saveErrorLog(asiNumber, batchId);
			return finalResult;
		} catch (Exception e) {
			_LOGGER.error("Error while Processing excel sheet " + e.getMessage());
			return finalResult;
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet " + e.getMessage());

			}
			_LOGGER.info("Complted processing of excel sheet ");
			_LOGGER.info("Total no of product:" + numOfProductsSuccess.size());
		}
	}

	private String getProductXid(Row row) {
		Cell xidCell = row.getCell(ApplicationConstants.CONST_NUMBER_ZERO);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if (StringUtils.isEmpty(productXid)) {
			xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_THREE);
			productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		productXid = getFinalProductNo(productXid);
		return productXid.trim();
	}

	public boolean isRepeateColumn(int columnIndex) {
		if (columnIndex != 47) {
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}

	private String getFinalProductNo(String prdNo) {
		if (prdNo.contains("Vintage")) {
			prdNo = prdNo.replaceAll("Vintage", "VTG");
		}
		if (prdNo.contains("BagTag")) {
			prdNo = prdNo.replaceAll("BagTag", "BT");
		}
		if(prdNo.contains("GIC/RIC-Earth/Recycle Symbol")){
			prdNo = prdNo.replaceAll("GIC/RIC-Earth/Recycle Symbol", "GIC/RIC-ERS");
		}
		return prdNo;
	}

	private String getSummary(String desc, String name) {
		String summary = "";
		if (!StringUtils.isEmpty(desc)) {
			if (desc.contains(".")) {
				summary = desc.substring(0, desc.indexOf(".") + 1);
			} else {
				summary = desc;
			}
			if (summary.length() > 130) {
				summary = name;
			}
		} else {
			summary = name;
		}

		return summary;
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

	public BloominPromotionsAttributeParser getBloominPromotionAttributeParser() {
		return bloominPromotionAttributeParser;
	}

	public void setBloominPromotionAttributeParser(BloominPromotionsAttributeParser bloominPromotionAttributeParser) {
		this.bloominPromotionAttributeParser = bloominPromotionAttributeParser;
	}

	public BloominPromotionsPriceGridParser getBloominPromotionPriceGridParser() {
		return bloominPromotionPriceGridParser;
	}

	public void setBloominPromotionPriceGridParser(BloominPromotionsPriceGridParser bloominPromotionPriceGridParser) {
		this.bloominPromotionPriceGridParser = bloominPromotionPriceGridParser;
	}

}
