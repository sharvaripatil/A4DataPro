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

import parser.maxplus.MaxpluProductAttributeParser;
import parser.maxplus.MaxplusPriceGridParser;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Inventory;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class MaxplusMapping implements IExcelParser {
	private static final Logger _LOGGER = Logger
			.getLogger(MaxplusMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private MaxpluProductAttributeParser maxplusAttribute;
	private MaxplusPriceGridParser pricegrid; 
	
	@Override
	public String readExcel(String accessToken, Workbook workbook,
			Integer asiNumber, int batchId) {		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		Set<String> productXids = new HashSet<String>();
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		List<ProductionTime> listProductionTime = new ArrayList<ProductionTime>();
		List<Color> listColor = new ArrayList<Color>();
		List<ImprintMethod> listimprintMethods = new ArrayList<ImprintMethod>();
		List<ImprintSize> listimprintSize = new ArrayList<ImprintSize>();
		List<Image> listImage= new ArrayList<Image>();

		
		


		
		Product productExcelObj = new Product();
		ProductConfigurations productConfigObj = new ProductConfigurations();
		ProductionTime prodtimeObj=new ProductionTime();
		RushTime rushserviceObj=new RushTime();
        StringBuilder ImprintMethod=new StringBuilder();
        StringBuilder ImprintSize=new StringBuilder();
        StringBuilder Image=new StringBuilder();
    	StringBuilder listOfQuantity = new StringBuilder();
		StringBuilder listOfPrices = new StringBuilder();
		
        ShippingEstimate shippingEstimateObj=new ShippingEstimate();
        Size sizeObj=new Size();

		
		String productName = null;
		String productId = null;
		String finalResult = null;
		Product existingApiProduct = null;
		int columnIndex = 0;
		String xid = null;
		Cell cell2Data = null;
		String ProdNo = null;
		String AdditionalInfo1=null;
		String ShippingItem=null;
		String Image1=null;
		String Image2=null;
		String Image3=null;
		String Image4=null;
		String Image5=null;
		String Image6=null;
		String Image7=null;
		String Image8=null;
		String Image9=null;
		String Image10=null;
		String Quantity1=null;
		String Quantity2=null;
		String Quantity3=null;
		String Quantity4=null;
		String Quantity5=null;

		String ListPrice1=null;
		String ListPrice2=null;
		String ListPrice3=null;
		String ListPrice4=null;
		String ListPrice5=null;
		
		String ListAllprice=null;
		String ListAllquantity=null;



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
									 productExcelObj=existingApiProduct;
									 productConfigObj=existingApiProduct.getProductConfigurations();
									 
										List<Image> Img = existingApiProduct
												.getImages();
										productExcelObj.setImages(Img);
										
										List<Theme> themeList=productConfigObj.getThemes();
								    	 productConfigObj.setThemes(themeList);
								    	 
								    	 List<String>categoriesList=existingApiProduct.getCategories();
								    	 productExcelObj.setCategories(categoriesList);
									 
									 
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
							String Inventory=cell.getStringCellValue();
							
							if (!StringUtils.isEmpty(Inventory)) {
                            Inventory invtObj=new Inventory();
                            invtObj.setInventoryLink(Inventory);
							productExcelObj.setInventory(invtObj);
							}
							
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
						    Summary=Summary.substring(0, 130);
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
							 Image1=cell.getStringCellValue();
							 if (!StringUtils.isEmpty(Image1)) {	
								Image=Image.append(Image1).append(",");
							  }

							break;	
					
						case 22: // Large Image URL1
							 Image2=cell.getStringCellValue();
							 if (!StringUtils.isEmpty(Image2)) {	
									Image=Image.append(Image2).append(",");
								  }

							break;	
							
						case 25: // Large Image URL2
							 Image3=cell.getStringCellValue();
							 if (!StringUtils.isEmpty(Image3)) {	
									Image=Image.append(Image3).append(",");
								  }

							break;	
							
						case 28: // Large Image URL3
							 Image4=cell.getStringCellValue();
							 if (!StringUtils.isEmpty(Image4)) {	
									Image=Image.append(Image4).append(",");
								  }
							break;	
							
						case 31: // Large Image URL4
							 Image5=cell.getStringCellValue();
							 if (!StringUtils.isEmpty(Image5)) {	
									Image=Image.append(Image5).append(",");
								  }
							break;	
							
						case 34: // Large Image URL5
							 Image6=cell.getStringCellValue();
							 if (!StringUtils.isEmpty(Image6)) {	
									Image=Image.append(Image6).append(",");
								  }
							break;	
							
						case 37: // Large Image URL6
							 Image7=cell.getStringCellValue();
							 if (!StringUtils.isEmpty(Image7)) {	
									Image=Image.append(Image7).append(",");
								  }
							break;	
							
						case 40: // Large Image URL7
							 Image8=cell.getStringCellValue();
							 if (!StringUtils.isEmpty(Image8)) {	
									Image=Image.append(Image8).append(",");
								  }
							break;	
							
						case 43: // Large Image URL8
							 Image9=cell.getStringCellValue();
							 if (!StringUtils.isEmpty(Image9)) {	
									Image=Image.append(Image9).append(",");
								  }
							break;	
	
						case 46: // Large Image URL9
							 Image10=cell.getStringCellValue();
							 if (!StringUtils.isEmpty(Image10)) {	
									Image=Image.append(Image10).append(",");
									Image10=Image1.toString();
									 listImage=maxplusAttribute.getImages(Image10);
								  }
							 productExcelObj.setImages(listImage);


							break;	
													
							
						case 67: // Price1
		                    ListPrice1=CommonUtility.getCellValueStrinOrDecimal(cell);

							break;	
							
							
						case 68: // QtyBreak1
							 Quantity1=CommonUtility.getCellValueStrinOrInt(cell);


							break;	
							
							
						case 69: // Price2
		                    ListPrice2=CommonUtility.getCellValueStrinOrDecimal(cell);


							break;	
							
							
						case 70: // QtyBreak2
							 Quantity2=CommonUtility.getCellValueStrinOrInt(cell);

							break;	
							
							
						case 71: // Price3
		                    ListPrice3=CommonUtility.getCellValueStrinOrDecimal(cell);


							break;	
							
							
						case 72: // QtyBreak3
							 Quantity3=CommonUtility.getCellValueStrinOrInt(cell);

							break;	
							
							
						case 73: // Price4
		                    ListPrice4=CommonUtility.getCellValueStrinOrDecimal(cell);


							break;	
							
							
						case 74: // QtyBreak4
							 Quantity4=CommonUtility.getCellValueStrinOrInt(cell);

							break;	
							
							
						case 75: // Price5
		                    ListPrice5=CommonUtility.getCellValueStrinOrDecimal(cell);
							 listOfPrices=listOfPrices.append(ListPrice1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).
									 append(ListPrice2).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(ListPrice3).
							 append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(ListPrice4).append
							 (ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(ListPrice5);
							 ListAllprice=listOfPrices.toString();

							break;	
							
							
						case 76: // QtyBreak5
							 Quantity5=CommonUtility.getCellValueStrinOrInt(cell);
							 listOfQuantity=listOfQuantity.append(Quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).
									 append(Quantity2).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(Quantity3).
							 append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(Quantity4).append
							 (ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(Quantity5);
								 ListAllquantity=listOfQuantity.toString();
							break;	
							
							
						case 77: // Price6
		              //      ListPrice6=CommonUtility.getCellValueStrinOrDecimal(cell);


							break;	
							
							
						case 78: // QtyBreak6
					//		 Quantity6=CommonUtility.getCellValueStrinOrInt(cell);

							break;	
							
							
						case 79: // Price7
		             //       ListPrice7=CommonUtility.getCellValueStrinOrDecimal(cell);


							break;	
							
							
						case 80: // QtyBreak7
						//	 Quantity7=CommonUtility.getCellValueStrinOrInt(cell);

							break;	
							
							
						case 81: // Price8
		             //       ListPrice8=CommonUtility.getCellValueStrinOrDecimal(cell);


							break;	
							
							
						case 82: // QtyBreak8
						//	 Quantity8=CommonUtility.getCellValueStrinOrInt(cell);

							break;	
							
							
						case 83: // Price9
		              //      ListPrice9=CommonUtility.getCellValueStrinOrDecimal(cell);


							break;	
							
							
						case 84: // QtyBreak9
					//		 Quantity9=CommonUtility.getCellValueStrinOrInt(cell);

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
                        String ImprintSize1=cell.getStringCellValue();
						if (!StringUtils.isEmpty(ImprintSize1)) {							
                        ImprintSize=ImprintSize.append(ImprintSize1).append(",");	
						}
							
							break;	
							
						case 113: // Imprint Area 2
	                    String ImprintSize2=cell.getStringCellValue();
						if (!StringUtils.isEmpty(ImprintSize2)) {							
                        ImprintSize=ImprintSize.append(ImprintSize2).append(",");	
						}				

							break;	
							
						case 114: // Imprint Area
	                    String ImprintSize3=cell.getStringCellValue();
						if (!StringUtils.isEmpty(ImprintSize3)) {							
                        ImprintSize=ImprintSize.append(ImprintSize3).append(",");	
                        String Imprintsize=ImprintSize.toString();
                        listimprintSize=maxplusAttribute.getImprintSize(Imprintsize);
	
                        productConfigObj.setImprintSize(listimprintSize);
						}

							break;	
							
							
						case 115: // Imprint Method 3
							String ImprintMethod1=cell.getStringCellValue();
							if (!StringUtils.isEmpty(ImprintMethod1)) {							
								ImprintMethod=ImprintMethod.append(ImprintMethod1).append(",");
							}
							break;	
							
							
						case 116: // Imprint Method 2
							String ImprintMethod2=cell.getStringCellValue();
							if (!StringUtils.isEmpty(ImprintMethod2)) {							
								ImprintMethod=ImprintMethod.append(ImprintMethod2).append(",");


							}
							break;	
							
							
						case 117: // Imprint Method
							String ImprintMethod3=cell.getStringCellValue();
							if (!StringUtils.isEmpty(ImprintMethod3)) {							
								ImprintMethod=ImprintMethod.append(ImprintMethod3).append(",");
								String ImprintMthod=ImprintMethod.toString();
								listimprintMethods=maxplusAttribute.getImprintMethod(ImprintMthod);
                                productConfigObj.setImprintMethods(listimprintMethods);
							}
							break;	
							
							
						case 118: // Size
							String Size=cell.getStringCellValue();
							if (!StringUtils.isEmpty(Size)) {							
							sizeObj=maxplusAttribute.getSize(Size);	
							productConfigObj.setSizes(sizeObj);	
							}	
							break;	
							
						case 119: // Rush Service
							String RushService=cell.getStringCellValue();
							if (!StringUtils.isEmpty(RushService)) {							
								rushserviceObj.setAvailable(true);	
								productConfigObj.setRushTime(rushserviceObj);
							}

							break;	
							
							
						case 120: // Product Color
                           String ProductColor=cell.getStringCellValue();
                           ProductColor=ProductColor.replace("As Shown", "");
                           if (!StringUtils.isEmpty(ProductColor)) {		
                        	   
                        	 listColor=maxplusAttribute.getColorCriteria(ProductColor); 
                        	 productConfigObj.setColors(listColor);  
                           }
							
							
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
							 ShippingItem=cell.getStringCellValue();

							break;	
							
							
						case 123: // Weight Per Carton (lbs.)
							String ShippingWeight=cell.getStringCellValue();
							shippingEstimateObj=maxplusAttribute.getShippingestimete(ShippingItem,ShippingWeight);
							productConfigObj.setShippingEstimates(shippingEstimateObj);

							break;	
							
							
						case 124: // Setup Charge

							break;	
							
							
						case 125: // Running Charge

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
					
					priceGrids = pricegrid.getPriceGrids(ListAllprice,
							ListAllquantity, "R", "USD",
					         "", true, "N",productName ,""/*,priceGrids*/);

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
	
	public MaxpluProductAttributeParser getMaxplusAttribute() {
		return maxplusAttribute;
	}

	public void setMaxplusAttribute(MaxpluProductAttributeParser maxplusAttribute) {
		this.maxplusAttribute = maxplusAttribute;
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

	public MaxplusPriceGridParser getPricegrid() {
		return pricegrid;
	}

	public void setPricegrid(MaxplusPriceGridParser pricegrid) {
		this.pricegrid = pricegrid;
	}
	
	

}
