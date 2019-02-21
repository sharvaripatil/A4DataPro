package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;

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
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.Volume;
import com.a4tech.product.service.imple.PostServiceImpl;
import com.a4tech.supplier.service.ISupplierParser;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.brandwear.BrandwearPriceGridParser;
import parser.brandwear.BrandwearProductAttribure;


public class BrandwearExcelMapping implements ISupplierParser {

	private static final Logger _LOGGER = Logger
			.getLogger(BrandwearExcelMapping.class);

	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private LookupServiceData lookupServiceDataObj;
	private BrandwearProductAttribure productAttributeObj;
	private BrandwearPriceGridParser brandwearpriceObj;

	
	@Override
	public String readExcel(String accessToken, Workbook workbook,
			Integer asiNumber, int batchId, String environmentType) {

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

		Volume ItemWeightObj = new Volume();

		List<Material> MaterialList = new ArrayList<Material>();

		List<Personalization> PersonalizationList = new ArrayList<Personalization>();
		Personalization personalizeObj = new Personalization();

		List<ImprintMethod> ImprintMethodList = new ArrayList<ImprintMethod>();
		
		List<Color> colorList = new ArrayList<Color>();
		
		Size sizeObj=new Size();

		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();


		String productName = null;
		String productId = null;
		String finalResult = null;
		Product existingApiProduct = null;
		int columnIndex = 0;
		String xid = null;
		Cell cell2Data = null;
		String ProdNo = null;
		String MaterialAliceName = "";
		String sizeValue = null;
		
		String ListPrice1=null;
		String ListPrice2=null;
		String ListPrice3=null;
	
		StringBuilder listOfPrices1 = new StringBuilder();
		StringBuilder listOfPrices2 = new StringBuilder();
		StringBuilder listOfPrices3 = new StringBuilder();
		StringBuilder listOfPrices4 = new StringBuilder();
		StringBuilder listOfPrices5 = new StringBuilder();


		CellValue pricecellValue=null;
		FormulaEvaluator formulaEval = workbook.getCreationHelper().createFormulaEvaluator();
		
		List<String> listcomplianceCerts = new ArrayList<String>();

		

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
 						cell2Data = nextRow.getCell(2);
 						String ID=cell2Data.toString();
 						if(ID.length()==4)
 						{
 							ID=ID.substring(0, 2);
 						}else
 						{
 							ID=ID.substring(0, 3);
 						}
 						ProdNo = ID;
 						if (columnIndex + 1 == 1) {
							if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
								xid = cell.getStringCellValue();
							} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								xid = String.valueOf((int) cell
										.getNumericCellValue());
							} else {
								
								xid = ProdNo;
							}
							checkXid = true;
						} else {
   							checkXid = false;
						}
 						if (checkXid) {
							if (!productXids.contains(xid)) {
								if (nextRow.getRowNum() != 5) {
									System.out
											.println("Java object converted to JSON String, written to file");

									productExcelObj.setPriceGrids(priceGrids);
									productExcelObj
											.setProductConfigurations(productConfigObj);

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

									productConfigObj = new ProductConfigurations();
									MaterialAliceName = "";
								    FOBList = new ArrayList<FOBPoint>();
								    fobObj = new FOBPoint();
									productionTimeList = new ArrayList<ProductionTime>();
									timeObj = new ProductionTime();
									originList = new ArrayList<Origin>();
									originObj = new Origin();
								    keywordList = new ArrayList<String>();
								    ImprintSizeList = new ArrayList<ImprintSize>();
									imprintSizeObj = new ImprintSize();
									ItemWeightObj = new Volume();
									MaterialList = new ArrayList<Material>();
									PersonalizationList = new ArrayList<Personalization>();
									personalizeObj = new Personalization();
									ImprintMethodList = new ArrayList<ImprintMethod>();
									colorList = new ArrayList<Color>();
									sizeObj=new Size();
									listcomplianceCerts = new ArrayList<String>();
									
									 listOfPrices1 = new StringBuilder();
									 listOfPrices2 = new StringBuilder();
									 listOfPrices3 = new StringBuilder();
									 listOfPrices4 = new StringBuilder();
									 listOfPrices5 = new StringBuilder();
									 
									priceGrids = new ArrayList<PriceGrid>();

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

							productExcelObj.setAsiProdNo(ProdNo);

							break;
						case 4:// Item Name

							productName = cell.getStringCellValue();
							productExcelObj.setName(productName);

							break;
						case 5:// Sizes

							 sizeValue = CommonUtility
									.getCellValueStrinOrInt(cell);
						
							
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
							 pricecellValue=formulaEval.evaluate(cell);
							 if(!pricecellValue.toString().contains("#")) {
							 ListPrice1=pricecellValue.toString().replaceAll("org.apache.poi.ss.usermodel.CellValue ", "").replaceAll("[^0-9|.x%/ ]", "");
							 }
							break;
						case 23: // '50-99 (B)
							 pricecellValue=formulaEval.evaluate(cell);
							 if(!pricecellValue.toString().contains("#")) {
							 ListPrice2=pricecellValue.toString().replaceAll("org.apache.poi.ss.usermodel.CellValue ", "").replaceAll("[^0-9|.x%/ ]", "");
							 }
							 break;

						case 24: // 100 + (C)
							 pricecellValue=formulaEval.evaluate(cell);
							 if(!pricecellValue.toString().contains("#")) {
							 ListPrice3=pricecellValue.toString().replaceAll("org.apache.poi.ss.usermodel.CellValue ", "").replaceAll("[^0-9|.x%/ ]", "");
							 }
							 
							 listOfPrices1=listOfPrices1.append(ListPrice1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).
									 append(ListPrice2).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(ListPrice3);
							
							break;

						case 25: // 1 - 2XL
							 pricecellValue=formulaEval.evaluate(cell);
							 if(!pricecellValue.toString().contains("#")) {
							 ListPrice1=pricecellValue.toString().replaceAll("org.apache.poi.ss.usermodel.CellValue ", "").replaceAll("[^0-9|.x%/ ]", "");
							 }
							break;

						case 26: // 50 - 2XL
							 pricecellValue=formulaEval.evaluate(cell);
							 if(!pricecellValue.toString().contains("#")) {
							 ListPrice2=pricecellValue.toString().replaceAll("org.apache.poi.ss.usermodel.CellValue ", "").replaceAll("[^0-9|.x%/ ]", "");
							 }

							break;

						case 27: // 100 - 2XL
							 pricecellValue=formulaEval.evaluate(cell);
							 if(!pricecellValue.toString().contains("#")) {
							 ListPrice3=pricecellValue.toString().replaceAll("org.apache.poi.ss.usermodel.CellValue ", "").replaceAll("[^0-9|.x%/ ]", "");
							 }
							 listOfPrices2=listOfPrices2.append(ListPrice1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).
									 append(ListPrice2).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(ListPrice3);
							
							break;

						case 28: // 1 - 3XL
							 pricecellValue=formulaEval.evaluate(cell);
							 if(!pricecellValue.toString().contains("#")) {
							 ListPrice1=pricecellValue.toString().replaceAll("org.apache.poi.ss.usermodel.CellValue ", "").replaceAll("[^0-9|.x%/ ]", "");
							 }

							break;

						case 29: // 50 - 3XL
							 pricecellValue=formulaEval.evaluate(cell);
							 if(!pricecellValue.toString().contains("#")) {
							 ListPrice2=pricecellValue.toString().replaceAll("org.apache.poi.ss.usermodel.CellValue ", "").replaceAll("[^0-9|.x%/ ]", "");
							
							 }


							break;

						case 30: // 100 - 3XL
							 pricecellValue=formulaEval.evaluate(cell);
							 if(!pricecellValue.toString().contains("#")) {
							 ListPrice3=pricecellValue.toString().replaceAll("org.apache.poi.ss.usermodel.CellValue ", "").replaceAll("[^0-9|.x%/ ]", "");
							 }
							 listOfPrices3=listOfPrices3.append(ListPrice1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).
									 append(ListPrice2).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(ListPrice3);
							
							break;

						case 31: // 1 - 4XL
							 pricecellValue=formulaEval.evaluate(cell);
							 if(!pricecellValue.toString().contains("#")) {
							 ListPrice1=pricecellValue.toString().replaceAll("org.apache.poi.ss.usermodel.CellValue ", "").replaceAll("[^0-9|.x%/ ]", "");
							 }

							break;
						case 32: // 50 - 4XL
							 pricecellValue=formulaEval.evaluate(cell);
							 if(!pricecellValue.toString().contains("#")) {
							 ListPrice2=pricecellValue.toString().replaceAll("org.apache.poi.ss.usermodel.CellValue ", "").replaceAll("[^0-9|.x%/ ]", "");
 						
							 }
							break;
						case 33: // 100 - 4XL
							 pricecellValue=formulaEval.evaluate(cell);
							 if(!pricecellValue.toString().contains("#")) {
							 ListPrice3=pricecellValue.toString().replaceAll("org.apache.poi.ss.usermodel.CellValue ", "").replaceAll("[^0-9|.x%/ ]", "");
							 }
							 listOfPrices4=listOfPrices4.append(ListPrice1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).
									 append(ListPrice2).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(ListPrice3);
							


							break;
						case 34:// 1 - 5XL
							 pricecellValue=formulaEval.evaluate(cell);
							 if(!pricecellValue.toString().contains("#")) {
							 ListPrice1=pricecellValue.toString().replaceAll("org.apache.poi.ss.usermodel.CellValue ", "").replaceAll("[^0-9|.x%/ ]", "");

							 }

							break;
						case 35:// 50 - 5XL
							 pricecellValue=formulaEval.evaluate(cell);
							 if(!pricecellValue.toString().contains("#")) {
							 ListPrice2=pricecellValue.toString().replaceAll("org.apache.poi.ss.usermodel.CellValue ", "").replaceAll("[^0-9|.x%/ ]", "");
							 }

							break;
						case 36:// 100 - 5XL
							 pricecellValue=formulaEval.evaluate(cell);
							 if(!pricecellValue.toString().contains("#")) {
							 ListPrice3=pricecellValue.toString().replaceAll("org.apache.poi.ss.usermodel.CellValue ", "").replaceAll("[^0-9|.x%/ ]", "");
							 }
							 listOfPrices5=listOfPrices5.append(ListPrice1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).
									 append(ListPrice2).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(ListPrice3);
							
							break;
						case 37: // Catalog Item Description
							String Description = cell.getStringCellValue();
							if (!StringUtils.isEmpty(Description)) {
								Description=Description.replace("®", "R").replace("’", "'");
								productExcelObj.setDescription(Description);
							}
							String Summary[]=Description.split("\\.");
							if(Summary[0].length()<130)
							{
								productExcelObj.setSummary(Summary[0].concat("."));
							}else
							{
								productExcelObj.setSummary(productName);
							}
							
							
							break;
						case 38:// Item Keywords-Tags- Hidden Keywords
							String Keywords = cell.getStringCellValue();
							Keywords=Keywords.replace("®", "R").replace("’","'");
								String KeywordArr[] = Keywords.toLowerCase().split(",");
                             if(!keywordList.contains(Keywords.toLowerCase())){
								for (String string : KeywordArr) {
								if(!(string.length()>30)){
									keywordList.add(string);
								}if(keywordList.size()==30){
										break;
								}}
								productExcelObj.setProductKeywords(keywordList);
							    }

							break;

						case 39:// Logoing Techniques
							String imprintMethod = cell.getStringCellValue();
							imprintMethod=imprintMethod.replace("®", "(R)").replace("’","'");
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
							String genderName=cell.getStringCellValue();
							if (!StringUtils.isEmpty(genderName)) {
							
								sizeObj= productAttributeObj
										.getSizeValue(sizeValue,genderName);
								productConfigObj.setSizes(sizeObj);
							}

							break;

						case 43:// Category

							break;
						case 44:// Fabric
						/*	String MaterialAlias = cell.getStringCellValue();
							MaterialAlias=MaterialAlias.replace("®", "R").replace(" ’", " '");

							if (!StringUtils.isEmpty(MaterialAlias)) {
								MaterialAliceName = MaterialAliceName.concat(
										MaterialAlias).concat("--");
							} else {
								MaterialAlias = "";
							}*/

							break;
						case 45:// Fabric Content
							String MaterialValues = cell.getStringCellValue();
						//	MaterialAliceName=MaterialValues.replace(",", " ").concat("--");
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
							String Personalization =cell.getStringCellValue();
							if (!StringUtils.isEmpty(Personalization)) {
								personalizeObj.setAlias("PERSONALIZATION");
								personalizeObj.setType("PERSONALIZATION");
								PersonalizationList.add(personalizeObj);
								productConfigObj
										.setPersonalization(PersonalizationList);
							}
							break;
						case 54:// Item Weight
							String Productweight = CommonUtility.getCellValueStrinOrDecimal(cell);
							if (!StringUtils.isEmpty(Productweight)) {
								ItemWeightObj = productAttributeObj
										.getitemWeight(Productweight);
								productConfigObj.setItemWeight(ItemWeightObj);
										
							}
							break;
						case 55:// Made in the USA
							String Origin = cell.getStringCellValue();
							if (!StringUtils.isEmpty(Origin)) {
								originObj.setName("CANADA");
								originList.add(originObj);
								productConfigObj.setOrigins(originList);
							}
							if(Origin.contains("TPL"))
							{
							 listcomplianceCerts.add("TPL Compliant");
							 productExcelObj.setComplianceCerts(listcomplianceCerts);
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
					
					priceGrids = brandwearpriceObj.getRepeatablePriceGrids(listOfPrices1.toString(),
					         "1___50___100", "A___B___C", "USD",
					         "", true, "N",productName ,"Size","@".concat(sizeValue),priceGrids);
					
					
					priceGrids = brandwearpriceObj.getRepeatablePriceGrids(listOfPrices2.toString(),
					         "1___50___100", "A___B___C", "USD",
					         "", true, "N",productName ,"Size","2XL",priceGrids);
					
				
					priceGrids = brandwearpriceObj.getRepeatablePriceGrids(listOfPrices3.toString(),
					         "1___50___100", "A___B___C", "USD",
					         "", true, "N",productName ,"Size","3XL",priceGrids);
					

					priceGrids = brandwearpriceObj.getRepeatablePriceGrids(listOfPrices4.toString(),
					         "1___50___100", "A___B___C", "USD",
					         "", true, "N",productName ,"Size","4XL",priceGrids);
					
					priceGrids = brandwearpriceObj.getRepeatablePriceGrids(listOfPrices5.toString(),
					         "1___50___100", "A___B___C", "USD",
					         "", true, "N",productName ,"Size","5XL",priceGrids);
						
				
				} catch (Exception e) {
					_LOGGER.error("Error while Processing ProductId and cause :"
							+ productExcelObj.getExternalProductId()
							+ " "
							+ e.getMessage() + "case" + columnIndex);
				}
			}
			workbook.close();

			productExcelObj.setPriceGrids(priceGrids);
			productExcelObj.setProductConfigurations(productConfigObj);

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
			productConfigObj = new ProductConfigurations();
			 MaterialAliceName = "";
		    FOBList = new ArrayList<FOBPoint>();
		    fobObj = new FOBPoint();
			productionTimeList = new ArrayList<ProductionTime>();
			timeObj = new ProductionTime();
			originList = new ArrayList<Origin>();
			originObj = new Origin();
		    keywordList = new ArrayList<String>();
		    ImprintSizeList = new ArrayList<ImprintSize>();
			imprintSizeObj = new ImprintSize();
			ItemWeightObj = new Volume();
			MaterialList = new ArrayList<Material>();
			PersonalizationList = new ArrayList<Personalization>();
			personalizeObj = new Personalization();
			ImprintMethodList = new ArrayList<ImprintMethod>();
			colorList = new ArrayList<Color>();
			sizeObj=new Size();
			
			 listOfPrices1 = new StringBuilder();
			 listOfPrices2 = new StringBuilder();
			 listOfPrices3 = new StringBuilder();
			 listOfPrices4 = new StringBuilder();
			 listOfPrices5 = new StringBuilder();
			 listcomplianceCerts = new ArrayList<String>();
			priceGrids = new ArrayList<PriceGrid>();


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

	public BrandwearPriceGridParser getBrandwearpriceObj() {
		return brandwearpriceObj;
	}

	public void setBrandwearpriceObj(BrandwearPriceGridParser brandwearpriceObj) {
		this.brandwearpriceObj = brandwearpriceObj;
	}

}
