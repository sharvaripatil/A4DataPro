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
import com.a4tech.product.service.imple.PostServiceImpl;
import com.a4tech.util.CommonUtility;

import parser.zenith.ZenithPriceGridParser;
import parser.zenith.ZenithProductAttributeParser;

public class ZenithExport implements IExcelParser  {

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
				case 3:// Sku
					String productCode = cell.getStringCellValue();
					if(productCode.length() > 14)
					{
						productExcelObj.setProductLevelSku(productCode);
					}else{
					productExcelObj.setAsiProdNo(productCode);
					}

					break;
					
				case 14:// Name
					productName = cell.getStringCellValue();
					if(productName.length() > 60)
					{
						productName=productName.substring(0, 60);
					}
					productExcelObj.setName(productName);

					break;
					
				case 20:// LongDescription
					String description = cell.getStringCellValue();
					if(description.length() > 800)
					{
						productName=productName.substring(0, 800);
					}
					productExcelObj.setDescription(description);

					break;
					
				case 51:// Weight

					break;
					
				case 54:// PackageLength

					break;
					
				case 55:// PackageWidth

					break;
					
				case 56:// PackageHeight

					break;
					
				case 61:// Price1
					String Price1=CommonUtility.getCellValueStrinOrDecimal(cell);
					if (!StringUtils.isEmpty(Price1)){			
			           	listOfPrices.append(Price1);

					}
					break;
					
				case 62:// QtyBreak1
					String Qty1=CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(Qty1)){			
			        	listOfQuantity.append(Qty1);

					}
					break;
					
				case 63:// Price2
					String Price2=CommonUtility.getCellValueStrinOrDecimal(cell);
					if (!StringUtils.isEmpty(Price2)){			
			           	listOfPrices.append(Price2);

					}
					break;
					
				case 64:// QtyBreak2
					String Qty2=CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(Qty2)){			
			        	listOfQuantity.append(Qty2);

					}
					break;
					
				case 65:// Price3
					String Price3=CommonUtility.getCellValueStrinOrDecimal(cell);
					if (!StringUtils.isEmpty(Price3)){			
			           	listOfPrices.append(Price3);						
					}
					break;
					
				case 66:// QtyBreak3
					String Qty3=CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(Qty3)){			
			        	listOfQuantity.append(Qty3);

					}
					break;
					
				case 67:// Price4

					break;
					
				case 68:// QtyBreak4

					break;
					
				case 69:// Price5

					break;
					
				case 70:// QtyBreak5

					break;
					
				case 71:// Price6

					break;
					
				case 72:// QtyBreak6

					break;
					
				case 73:// Price7

					break;
					
				case 74:// QtyBreak7

					break;
					
				case 75:// Price8

					break;
					
				case 76:// QtyBreak8

					break;
					
				case 77:// Price9

					break;
					
					
				case 78:// QtyBreak9

					break;
					
					
				case 79:// Price10

					break;
					
					
				case 101:// Field_1
				   colorName=cell.getStringCellValue();
					break;
					
				case 102:// Field_2
					String colorName1=cell.getStringCellValue();
					if (!StringUtils.isEmpty(colorName1)) {
						listOfColor=zenithtAttributeParser.getColorName(colorName,colorName1);
						productConfigObj.setColors(listOfColor);
					}

					break;
					
				case 103:// Field_10
					String packging=cell.getStringCellValue();
					if (!StringUtils.isEmpty(packging)) {
						listOfPackaging=zenithtAttributeParser.getPackaging(packging);
						productConfigObj.setPackaging(listOfPackaging);
					}
					break;
					
				case 104:// Field_11
					 sizeDimension=cell.getStringCellValue();

					break;
					
				case 105:// Field_12
					String sizeCapacity=cell.getStringCellValue();
					if (!StringUtils.isEmpty(sizeCapacity)) {
				//	sizeObj=zenithtAttributeParser.getSize(sizeDimension,sizeCapacity);
					}
					productConfigObj.setSizes(sizeObj);
					break;
					
				case 106:// Field_13
					String materialName=cell.getStringCellValue();
					if (!StringUtils.isEmpty(materialName)) {
						listOfMaterial=zenithtAttributeParser.getMaterial(materialName);
						productConfigObj.setMaterials(listOfMaterial);
					}
					break;
					
				case 108:// Field_18
					String addInfo1=cell.getStringCellValue();
					if (!StringUtils.isEmpty(addInfo1)) {
					addInfo.append(addInfo1).append(",");
					}
					break;
					
				case 109:// Field_19
					String addInfo2=cell.getStringCellValue();
					if (!StringUtils.isEmpty(addInfo2)) {
					addInfo.append(addInfo2);
					}					
					productExcelObj.setAdditionalProductInfo(addInfo.toString());
					break;
					
				case 110:// Field_29
					 noOfItems=cell.getStringCellValue();
					if (!StringUtils.isEmpty(noOfItems)) {
						noOfItems=noOfItems.replace("units", "");
					}

					break;
					
				case 111:// Field_30
				   shippingWeight=cell.getStringCellValue();
					if (!StringUtils.isEmpty(shippingWeight)) {
					shippingWeight=shippingWeight.replace("lbs", "");	
					}

					break;
					
				case 112:// Field_31
					String  shippingDimension=cell.getStringCellValue();
					if (!StringUtils.isEmpty(shippingDimension)) {
						shippingDimension=shippingDimension.replace("in", "");	
						shipping=zenithtAttributeParser.getShippingEstimation(noOfItems,shippingWeight,shippingDimension);
						productConfigObj.setShippingEstimates(shipping);
					}

					break;
					
				case 113:// Field_32
					String fobValue=cell.getStringCellValue();
					if (!StringUtils.isEmpty(fobValue)) {
						listOfFob=zenithtAttributeParser.getFobValue(fobValue);
						productExcelObj.setFobPoints(listOfFob);
					}
					break;
					
				case 114:// Field_33
				    String productionTime=cell.getStringCellValue();
					if (!StringUtils.isEmpty(productionTime)) {
						listOfProductionTime=zenithtAttributeParser.getProductionTime(productionTime);
						productConfigObj.setProductionTime(listOfProductionTime);
					}

					break;
					
				case 116:// Field_61

					break;
					
				case 117:// Field_80

					break;
					
				case 118:// Field_81

					break;
					
				case 126:// Keywords
					String productKeyword = cell.getStringCellValue();
					productKeyword=productKeyword.replace("®", "R").replace(" ’", " '");
					String KeywordArr[] = productKeyword.toLowerCase().split(",");
                   if(!productKeywords.contains(productKeyword.toLowerCase())){
				 	for (String string : KeywordArr) {
					if(!(string.length()>30)){
						productKeywords.add(string);
					}}
					productExcelObj.setProductKeywords(productKeywords);
				    }

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
