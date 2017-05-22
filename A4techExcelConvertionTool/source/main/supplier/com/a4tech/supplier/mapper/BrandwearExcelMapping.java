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

import parser.brandwear.BrandwearProductAttribure;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Personalization;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Theme;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.CommonUtility;

public class BrandwearExcelMapping implements IExcelParser {

	private static final Logger _LOGGER = Logger
			.getLogger(BrandwearExcelMapping.class);

	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private LookupServiceData lookupServiceDataObj;
	private BrandwearProductAttribure productAttributeObj;

	@Override
	public String readExcel(String accessToken, Workbook workbook,
			Integer asiNumber, int batchId) {

		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		Set<String> productXids = new HashSet<String>();

		Product productExcelObj = new Product();
		ProductConfigurations productConfigObj = new ProductConfigurations();

		List<FOBPoint> FOBList = new ArrayList<FOBPoint>();
		FOBPoint fobObj = new FOBPoint();

		List<ProductionTime> productionTimeList = new ArrayList<ProductionTime>();
		ProductionTime timeObj = new ProductionTime();

		List<Origin> originList = new ArrayList<Origin>();
		Origin originObj = new Origin();

		List<String> keywordList = new ArrayList<String>();

		List<ImprintSize> ImprintSizeList = new ArrayList<ImprintSize>();
		ImprintSize imprintSizeObj = new ImprintSize();

		ShippingEstimate shippingObj = new ShippingEstimate();

		List<Material> MaterialList = new ArrayList<Material>();

		List<Personalization> PersonalizationList = new ArrayList<Personalization>();
		Personalization personalizeObj = new Personalization();

		List<ImprintMethod> ImprintMethodList = new ArrayList<ImprintMethod>();
		
		List<Color> colorList = new ArrayList<Color>();
		

		String productName = null;
		String productId = null;
		String finalResult = null;
		Product existingApiProduct = null;
		int columnIndex = 0;
		String xid = null;
		Cell cell2Data = null;
		String ProdNo = null;
		String MaterialAliceName = "";

		try {

			_LOGGER.info("Total sheets in excel::"
					+ workbook.getNumberOfSheets());
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = sheet.iterator();
			_LOGGER.info("Started Processing Product");

			while (iterator.hasNext()) {

				try {
					Row nextRow = iterator.next();
					if (nextRow.getRowNum() < 5)
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

									List<Image> Img = existingApiProduct
											.getImages();
									productExcelObj.setImages(Img);

									List<Theme> themeList = productConfigObj
											.getThemes();
									productConfigObj.setThemes(themeList);

									List<String> categoriesList = existingApiProduct
											.getCategories();
									productExcelObj
											.setCategories(categoriesList);

								}
								// productExcelObj = new Product();
							}
						}

						switch (columnIndex + 1) {
						case 1:
							productExcelObj.setExternalProductId(xid);

							break;

						case 2:// Vol. 16 Page#

							break;
						case 3:// Item#

							String asiProdNo = CommonUtility
									.getCellValueStrinOrInt(cell);
							productExcelObj.setAsiProdNo(asiProdNo);

							break;
						case 4:// Item Name

							productName = cell.getStringCellValue();
							productExcelObj.setName(productName);

							break;
						case 5:// Sizes

							String sizeValue = CommonUtility
									.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(sizeValue)) {

							}
							break;

						case 6:// Color
							String colorValue = CommonUtility
									.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(colorValue)) {
								colorList = productAttributeObj
										.getColorValue(colorValue);
							}
							productConfigObj.setColors(colorList);
							break;

						case 7:// '1-49

							break;
						case 8: // '50-99

							break;
						case 9:// '100 +

							break;
						case 10: // 1 - 2XL

							break;
						case 11: // 50 - 2XL

							break;
						case 12: // 100 - 2XL

							break;
						case 13: // 1 - 3XL

							break;

						case 14: // 50 - 3XL
							break;

						case 15:// 100 - 3XL

							break;

						case 16: // 1 - 4XL

							break;

						case 17: // 50 - 4XL

							break;

						case 18: // 100 - 4XL

							break;

						case 19: // 1 - 5XL

							break;

						case 20: // 50 - 5XL

							break;

						case 21: // 100 - 5XL

							break;
						case 22:// 1-49 (A)

							break;
						case 23: // '50-99 (B)

							break;

						case 24: // 100 + (C)

							break;

						case 25: // 1 - 2XL

							break;

						case 26: // 50 - 2XL

							break;

						case 27: // 100 - 2XL

							break;

						case 28: // 1 - 3XL

							break;

						case 29: // 50 - 3XL

							break;

						case 30: // 100 - 3XL

							break;

						case 31: // 1 - 4XL

							break;
						case 32: // 50 - 4XL

							break;
						case 33: // 100 - 4XL

							break;
						case 34:// 1 - 5XL

							break;
						case 35:// 50 - 5XL

							break;
						case 36:// 100 - 5XL

							break;
						case 37: // Catalog Item Description
							String Description = cell.getStringCellValue();
							if (!StringUtils.isEmpty(Description)) {
								productExcelObj.setDescription(Description);
							}
							break;
						case 38:// Item Keywords-Tags- Hidden Keywords
							String Keywords = cell.getStringCellValue();
							if (!StringUtils.isEmpty(Keywords)) {
								String KeywordArr[] = Keywords.split(",");

								for (String string : KeywordArr) {
									keywordList.add(string);
								}
								productExcelObj.setProductKeywords(keywordList);
							}

							break;

						case 39:// Logoing Techniques
							String imprintMethod = cell.getStringCellValue();
							if (!StringUtils.isEmpty(imprintMethod)) {
								ImprintMethodList = productAttributeObj
										.getImprintMethod(imprintMethod);
								productConfigObj
										.setImprintMethods(ImprintMethodList);
							}
							break;
						case 40:// Item Image 1

							break;
						case 41:// Item Image 2

							break;

						case 42:// Gender

							break;

						case 43:// Category

							break;
						case 44:// Fabric
							String MaterialAlias = cell.getStringCellValue();
							if (!StringUtils.isEmpty(MaterialAlias)) {
								MaterialAliceName = MaterialAliceName.concat(
										MaterialAlias).concat("--");
							} else {
								MaterialAlias = "";
							}

							break;
						case 45:// Fabric Content
							String MaterialValues = cell.getStringCellValue();
							if (!StringUtils.isEmpty(MaterialValues)) {
								MaterialValues = MaterialAliceName
										.concat(MaterialValues);
								MaterialList = productAttributeObj
										.getMaterial(MaterialValues);
								productConfigObj.setMaterials(MaterialList);

							}

							break;
						case 46:// Fabric Description

							break;
						case 47:// Fabric Weight

							break;
						case 48:// Sage Item Description

							break;
						case 49:// SAGE Keywords

							break;
						case 50:// ASI Item Description

							break;
						case 51:// Normal Production Time (working days)
							String ProductionTime = cell.getStringCellValue();
							if (!StringUtils.isEmpty(ProductionTime)) {
								timeObj.setBusinessDays("14");
								timeObj.setDetails(ProductionTime);
								productionTimeList.add(timeObj);
								productConfigObj
										.setProductionTime(productionTimeList);
							}
							break;
						case 52:// Method of Imprinting

							break;
						case 53:// Personalization
							String Personalization = cell.getCellFormula();
							if (!StringUtils.isEmpty(Personalization)) {
								personalizeObj.setAlias("PERSONALIZATION");
								personalizeObj.setType("PERSONALIZATION");
								productConfigObj
										.setPersonalization(PersonalizationList);
							}
							break;
						case 54:// Shipping Weight
							String shippingWeight = cell.getStringCellValue();
							if (!StringUtils.isEmpty(shippingWeight)) {
								shippingObj = productAttributeObj
										.getshippingWeight(shippingWeight);
								productConfigObj
										.setShippingEstimates(shippingObj);
							}
							break;
						case 55:// Made in the USA
							String Origin = cell.getStringCellValue();
							if (!StringUtils.isEmpty(Origin)) {
								originObj.setName("CANADA");
								originList.add(originObj);
								productConfigObj.setOrigins(originList);
							}

							break;
						case 56:// F.O.B Point
							String FOBPint = CommonUtility
									.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(FOBPint)) {
								fobObj.setName("Langley, BC V2Z 2R1 Canada");
								FOBList.add(fobObj);
								productExcelObj.setFobPoints(FOBList);
							}

							break;
						case 57:// Imprint Size
							String ImprintSize = cell.getStringCellValue();
							if (!StringUtils.isEmpty(ImprintSize)) {
								imprintSizeObj.setValue(ImprintSize);
								ImprintSizeList.add(imprintSizeObj);
								productConfigObj
										.setImprintSize(ImprintSizeList);
							}
							break;
						case 58:// Setup Charges
							break;

						} // end inner while loop

					}
					// set product configuration objects

					// end inner while loop
					productExcelObj.setPriceType("L");

				} catch (Exception e) {
					_LOGGER.error("Error while Processing ProductId and cause :"
							+ productExcelObj.getExternalProductId()
							+ " "
							+ e.getMessage() + "case" + columnIndex);
				}
			}
			workbook.close();

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

	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}

	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}

	public BrandwearProductAttribure getProductAttributeObj() {
		return productAttributeObj;
	}

	public void setProductAttributeObj(
			BrandwearProductAttribure productAttributeObj) {
		this.productAttributeObj = productAttributeObj;
	}

}
