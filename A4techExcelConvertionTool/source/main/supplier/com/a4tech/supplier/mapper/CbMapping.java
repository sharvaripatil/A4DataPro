package com.a4tech.supplier.mapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

import parser.cutter.CutterBuckMaterialParser;
import parser.cutter.CutterBuckPriceGridParser;
import parser.cutter.CutterBuckSheetParser;
import parser.cutter.CutterBuckSizeParser;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.AdditionalLocation;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.Size;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class CbMapping implements IExcelParser{

	private static final Logger _LOGGER = Logger
			.getLogger(CbMapping.class);

	private LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;
	private PostServiceImpl postServiceImpl;  
	private ProductDao productDaoObj;

	private CutterBuckMaterialParser cutterBuckMaterialParserObj;
	private CutterBuckSizeParser cutterBuckSizeParserObj;
	private CutterBuckPriceGridParser cutterBuckPriceObj;
    private CutterBuckSheetParser cutterBuckSheetObj;
    private HashMap<String, Product> SheetMap =new HashMap<String, Product>();
    private HashMap<String, String> ProductNoMap =new HashMap<String, String>();
 
	@Override
	public String readExcel(String accessToken, Workbook workbook,
			Integer asiNumber, int batchId, String environmentType) {
		int columnIndex = 0;

		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		Set<String> productXids = new HashSet<String>();

		List<String> listOfLookupLinenames =new ArrayList<String>();
		List<String> listOfLinenames =new ArrayList<String>();
		List<Material> listOfMaterial = new ArrayList<>();
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		ProductConfigurations productConfigObj = new ProductConfigurations();


		Product existingApiProduct = null;
		Product productExcelObj = new Product();
		String productName = null;
		String finalResult = null;
		String productId = null;
		String NetCost=null;
		String ListPrice=null;
		String asiProdNo = null;
		
		try {

			_LOGGER.info("Total sheets in excel::"
					+ workbook.getNumberOfSheets());
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = sheet.iterator();
			_LOGGER.info("Started Processing Product");

	        StringBuilder listOfQuantity = new StringBuilder();
			StringBuilder listOfPrices = new StringBuilder();		
			String xid = null;
			Cell cell2Data = null;
			String ProdNo = null;
			String quantity = null;
			String listPrice = null;
			String ProductName=null;
			String quoteUponRequest = "false";
		

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
					    xid = null;
					    columnIndex = cell.getColumnIndex();
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
									xid = newXIDArr[1].replace("\"", "").replace(")", "");
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
							//	if (nextRow.getRowNum() != 7) {
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

									listOfQuantity = new StringBuilder();
									listOfPrices = new StringBuilder();
									priceGrids = new ArrayList<PriceGrid>();
	
									productConfigObj = new ProductConfigurations();
								
								}
								if (!productXids.contains(xid)) {
									productXids.add(xid.trim());
								}
							
								existingApiProduct = postServiceImpl
										.getProduct(accessToken,
												xid, environmentType);
											
								if (existingApiProduct == null) {
									_LOGGER.info("Existing Xid is not available,product treated as new product");
									productExcelObj = new Product();
								} else {
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
						
						case 1://XIDs Mapped						
							 productId =  CommonUtility.getCellValueStrinOrInt(cell);
							 if(!StringUtils.isEmpty(productId)){
							 productExcelObj.setExternalProductId(productId);
					      	}else{
					      		 productId=xid;
								 productExcelObj.setExternalProductId(productId);
						      		
					      	}
							 break;
							 
						case 2://Collection

							break;
						case 3://Size


					
							break;
						case 4://UPC

	
							
							break;
				
						case 5:// Material Number



							break;

						case 6: // WHSL

						
		
							
							break;
							
						case 7://Color Name

	
						
							break;
							
						case 8://Label

							break;
						case 9://Low Res Image

							
	
							
							break;
							
						case 10://High Res Image

					
							break;
							
					    	case 11://Sizes Available

							
							break;
					        case 12://Season

							
							break;
							
	                    	case 13://Year

							
							break;
	                    	case 14://Item Description

								
							break;
							
	                    	case 15://Style Number

								
							break;
							
	                    	case 16://MSRP

								
							break;
							
	                    	case 17://Material Content

								
							break;
							
	                    	case 18://Attribute Description

								
							break;
							
	                    	case 19://CareBleach

								
							break;
							
	                    	case 20://Care Drying Instructions

								
							break;
							
	                    	case 21://Care Washing Instructions

								
							break;
							
	                    	case 22://Country Of Origin

								
							break;
						} // end inner while loop

					}
					// set product configuration objects

					// end inner while loop


					

				
					
		
		
					
					
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
		
	public ProductDao getProductDaoObj() {
		return productDaoObj;
	}

	public void setProductDaoObj(ProductDao productDaoObj) {
		this.productDaoObj = productDaoObj;
	}

	public CutterBuckMaterialParser getCutterBuckMaterialParserObj() {
		return cutterBuckMaterialParserObj;
	}

	public void setCutterBuckMaterialParserObj(
			CutterBuckMaterialParser cutterBuckMaterialParserObj) {
		this.cutterBuckMaterialParserObj = cutterBuckMaterialParserObj;
	}


	public CutterBuckSizeParser getCutterBuckSizeParserObj() {
		return cutterBuckSizeParserObj;
	}

	public void setCutterBuckSizeParserObj(
			CutterBuckSizeParser cutterBuckSizeParserObj) {
		this.cutterBuckSizeParserObj = cutterBuckSizeParserObj;
	}

	public CutterBuckPriceGridParser getCutterBuckPriceObj() {
		return cutterBuckPriceObj;
	}

	public void setCutterBuckPriceObj(CutterBuckPriceGridParser cutterBuckPriceObj) {
		this.cutterBuckPriceObj = cutterBuckPriceObj;
	}

	public CutterBuckSheetParser getCutterBuckSheetObj() {
		return cutterBuckSheetObj;
	}

	public void setCutterBuckSheetObj(CutterBuckSheetParser cutterBuckSheetObj) {
		this.cutterBuckSheetObj = cutterBuckSheetObj;
	}

	public HashMap<String, Product> getSheetMap() {
		return SheetMap;
	}

	public void setSheetMap(HashMap<String, Product> sheetMap) {
		SheetMap = sheetMap;
	}

	public PostServiceImpl getPostServiceImpl() {
		return postServiceImpl;
	}
	public void setPostServiceImpl(PostServiceImpl postServiceImpl) {
		this.postServiceImpl = postServiceImpl;
	}

	public HashMap<String, String> getProductNoMap() {
		return ProductNoMap;
	}


	public void setProductNoMap(HashMap<String, String> productNoMap) {
		ProductNoMap = productNoMap;
	}



	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}



	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}



	public LookupRestService getLookupRestServiceObj() {
		return lookupRestServiceObj;
	}



	public void setLookupRestServiceObj(LookupRestService lookupRestServiceObj) {
		this.lookupRestServiceObj = lookupRestServiceObj;
	}


	
			}

