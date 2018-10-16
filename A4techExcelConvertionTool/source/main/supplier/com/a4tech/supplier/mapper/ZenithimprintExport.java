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

import parser.zenith.ZenithPriceGridParser;
import parser.zenith.ZenithProductAttributeParser;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Configurations;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.CommonUtility;

public class ZenithimprintExport implements IExcelParser {

private static final Logger _LOGGER = Logger.getLogger(HarvestIndustrialExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private LookupServiceData lookupServiceDataObj;
	private ZenithProductAttributeParser zenithtAttributeParser;
	private ZenithPriceGridParser zenithPriceGridParser;  

	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		Set<String> productXids = new HashSet<String>();
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		List<ImprintLocation> listImprintLocation = new ArrayList<ImprintLocation>();
		List<ImprintMethod> listOfImprintMethods = new ArrayList<ImprintMethod>();
		List<Theme> themeList = new ArrayList<Theme>();
		 List<Theme> exstlist  = new ArrayList<Theme>();
		List<Color> color = new ArrayList<Color>();		
		List<String> productKeywords = new ArrayList<String>();
		List<ProductionTime> listOfProductionTime = new ArrayList<>();
		List<Packaging> listOfPackaging= new ArrayList<>();
		List<FOBPoint> listOfFob= new ArrayList<>();
		List<Color> listOfColor= new ArrayList<>();
		List<Material> listOfMaterial= new ArrayList<>();
		List<a> listOfAddColor= new ArrayList<>();


		Product productExcelObj = new Product();
		ProductConfigurations productConfigObj = new ProductConfigurations();
		ShippingEstimate shipping = new ShippingEstimate();
		String productName = null;
		String productId = null;
		String finalResult = null;
		RushTime rushTime = new RushTime();
		StringBuilder addInfo=new StringBuilder();
		String noOfItems="";
		String  shippingWeight="";
		Size sizeObj=new Size();
		StringBuilder listOfQuantity = new StringBuilder();
		StringBuilder listOfPrices = new StringBuilder();
		
		try {

			_LOGGER.info("Total sheets in excel::"
					+ workbook.getNumberOfSheets());
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = sheet.iterator();
			_LOGGER.info("Started Processing Product");

			String themeValue = null;
		
			String unitsPerCarton = null;
			Product existingApiProduct = null;
			Cell cell2Data = null;
			String colorName=null;
			String sizeDimension="";
			
			while (iterator.hasNext()) {

				try {
					Row nextRow = iterator.next();
					if (nextRow.getRowNum() < 1)//
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
						cell2Data = nextRow.getCell(1);
						if (columnIndex + 1 == 1) {
							if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
								xid = cell.getStringCellValue();
							} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								xid = String.valueOf((int) cell
										.getNumericCellValue());
							} else {
								String ProdNo = CommonUtility
										.getCellValueStrinOrInt(cell2Data);
								xid = ProdNo;

							}
							checkXid = true;
						} else {
							checkXid = false;
						}
						if (checkXid) {
							if (!productXids.contains(xid)) {
								if (nextRow.getRowNum() != 1) { //zenith export
									System.out
											.println("Java object converted to JSON String, written to file");

									/*if (!StringUtils.isEmpty(themeValue)) {
										productConfigObj.setThemes(themeList);
									}*/
									productConfigObj.setImprintLocation(listImprintLocation);
								
								
							
									if (!StringUtils.isEmpty(unitsPerCarton)) {
										productConfigObj
												.setShippingEstimates(shipping);
									}
									productConfigObj
											.setImprintMethods(listOfImprintMethods);

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

									
									priceGrids = new ArrayList<PriceGrid>();
									exstlist = new ArrayList<Theme>();
									listImprintLocation = new ArrayList<ImprintLocation>();
									listOfImprintMethods = new ArrayList<ImprintMethod>();
									color = new ArrayList<Color>();									
									rushTime = new RushTime();
									sizeObj=new Size();
									addInfo=new StringBuilder();
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
									productExcelObj = new Product();
									
									List<Image> Img = existingApiProduct.getImages();
									if(!StringUtils.isEmpty(Img)){
									 List<Configurations>confList=Img.get(0).getConfigurations();
										if (!StringUtils.isEmpty(confList)){	
											confList.remove(0);
										}
								}
									productExcelObj.setImages(Img);
									
							    	 themeList=productConfigObj.getThemes();
							    	 productConfigObj.setThemes(exstlist);
							    	 
							    	 List<String>categoriesList=existingApiProduct.getCategories();
							    	 productExcelObj.setCategories(categoriesList);
							    	 
							    	
							    	 							    	 
								}
								// productExcelObj = new Product();
							}
						}

					
				switch (columnIndex + 1) {
				case 1:// XID
 					productExcelObj.setExternalProductId(xid.trim());

					break;
			
				case 4:// name
					String prod_no=CommonUtility.getCellValueStrinOrInt(cell);
					String Arrprod[]=prod_no.split("-");
					productExcelObj.setAsiProdNo(Arrprod[0]);
					
					break;
					
				case 5:// imprint type
					
					break;
					
				case 6:// pms color charge sku
					
					break;
					
				case 8:// max colors
					String addColor=cell.getStringCellValue();
					if (!StringUtils.isEmpty(addColor)){
						
						
						
         	productConfigObj.setAdditionalColors();
					}
					break;
					
				case 9:// included running charge sku
					
					break;
					
				case 10:// location name
					
					break;
					
				case 11:// setup sku
					
					break;
					
				case 12:// running sku
					
					break;
					
				case 13:// PriceGrids
					
					break;
					
				case 14:// PriceGrids
					
					break;
					
				case 15:// PriceGrids
					
					break;
					
				case 16:// PriceGrids
					
					break;
					
				case 17:// PriceGrids
					
					break;
					
				case 18:// PriceGrids
					
					break;
					
				case 19:// PriceGrids
					
					break;
					
				case 20:// PriceGrids
					
					break;
				

								
				}  // end inner while loop
					 
		}
			// set  product configuration objects
			
			 // end inner while loop
			
				productExcelObj.setPriceType("L");
					
	
				priceGrids = zenithPriceGridParser.getPriceGrids(
								listOfPrices.toString(),
								listOfQuantity.toString(), "R", "USD",
								"", true, "false",
								productName, "", priceGrids);
					
				
					
			/*	priceGrids =  harvestPriceGridObj.getUpchargePriceGrid("1", Screencharge,
							Screenchargecode,
									"Imprint Method", "false", "USD",
									decorationMethod,
									"Screen Charge", "Other",
									new Integer(1),"Required","", priceGrids);	//screen charge
			
					*/
					
					
					 
	
				} catch (Exception e) {
					_LOGGER.error("Error while Processing ProductId and cause :"
							+ productExcelObj.getExternalProductId()
							+ " "
							+ e.getMessage());
				}
			}
			workbook.close();

			if (!StringUtils.isEmpty(themeValue)) {
				productConfigObj.setThemes(themeList);
			}
			productConfigObj.setImprintLocation(listImprintLocation);

			
			productConfigObj.setRushTime(rushTime);

			
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
			listImprintLocation = new ArrayList<ImprintLocation>();
			listOfImprintMethods = new ArrayList<ImprintMethod>();
			themeList = new ArrayList<Theme>();
			color = new ArrayList<Color>();
			productConfigObj = new ProductConfigurations();
			rushTime = new RushTime();
			sizeObj=new Size();
			addInfo=new StringBuilder();
			exstlist = new ArrayList<Theme>();
			listOfPrices = new StringBuilder();
			listOfQuantity = new StringBuilder();
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


	public ZenithProductAttributeParser getZenithtAttributeParser() {
		return zenithtAttributeParser;
	}


	public void setZenithtAttributeParser(
			ZenithProductAttributeParser zenithtAttributeParser) {
		this.zenithtAttributeParser = zenithtAttributeParser;
	}


	public ZenithPriceGridParser getZenithPriceGridParser() {
		return zenithPriceGridParser;
	}


	public void setZenithPriceGridParser(ZenithPriceGridParser zenithPriceGridParser) {
		this.zenithPriceGridParser = zenithPriceGridParser;
	}


	

}
