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
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductNumber;
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
		List<String> listKeyword = new ArrayList<String>();
		
		Product productExcelObj = new Product();
		ProductConfigurations productConfigObj = new ProductConfigurations();
		ProductionTime prodtimeObj=new ProductionTime();
		RushTime rushserviceObj=new RushTime();
        StringBuilder ImprintMethod=new StringBuilder();
        StringBuilder ImprintSize=new StringBuilder();
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
		String ShippingItem=null;
		boolean T =true;

		String Quantity1=null;
		String Quantity2=null;
		String Quantity3=null;
		String Quantity4=null;
		String Quantity5=null;
		String Quantity6=null;
		
		String ListPrice1=null;
		String ListPrice2=null;
		String ListPrice3=null;
		String ListPrice4=null;
		String ListPrice5=null;
		String ListPrice6=null;
		
		String ListAllprice=null;
		String ListAllquantity=null;
		String SetUpcharge3=null;
		String SetUpcharge2=null;
		String SetUpcharge1=null;

		String Runcharge1=null;
		String Runcharge2=null;
		String Runcharge3=null;

		String ImprintSize1=null;
		String ImprintSize2=null;
	
		String ImprintMethod1=null;
		String ImprintMethod2=null;
		String ImprintMethod3=null;
		
		String ExstngDescription=null;

	
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
						if (columnIndex + 1 == 1) {
							if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
								xid = cell.getStringCellValue();
							} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								xid = String.valueOf((int) cell
										.getNumericCellValue());
							} else {
								ProdNo = CommonUtility
										.getCellValueStrinOrInt(cell2Data);
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
								    listProductionTime = new ArrayList<ProductionTime>();
								    listColor = new ArrayList<Color>();
									listimprintMethods = new ArrayList<ImprintMethod>();
									listimprintSize = new ArrayList<ImprintSize>();
									listKeyword = new ArrayList<String>();
								    prodtimeObj=new ProductionTime();
						            rushserviceObj=new RushTime();
							        ImprintMethod=new StringBuilder();
							        ImprintSize=new StringBuilder();
							        listOfQuantity = new StringBuilder();
									listOfPrices = new StringBuilder();
							        shippingEstimateObj=new ShippingEstimate();
							        sizeObj=new Size();
							    	 Quantity1=null;
									 Quantity2=null;
									 Quantity3=null;
									 Quantity4=null;
									 Quantity5=null;
									 Quantity6=null;
									
									 ListPrice1=null;
									 ListPrice2=null;
									 ListPrice3=null;
									 ListPrice4=null;
									 ListPrice5=null;
									 ListPrice6=null;
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
										// productConfigObj=existingApiProduct.getProductConfigurations();
									 
										List<Image> Img = existingApiProduct
												.getImages();
										productExcelObj.setImages(Img);
										
										
										ExstngDescription=existingApiProduct.getDescription();
										productExcelObj.setDescription(ExstngDescription);
										
										List<Theme> themeList=existingApiProduct.getProductConfigurations().getThemes();
										if(themeList != null){
								       	for(int i=0;i<themeList.size();i++)
								       	{
										String name=themeList.get(i).getName();
										
										if(name.contains("ECO FRIENDLY"))
									    {
											Theme themeObj=new Theme();
											themeList.remove(i);
											name=name.replace("ECO FRIENDLY", "Eco & Environmentally Friendly");
											themeObj.setName(name);
											themeList.add(themeObj);
											
										}
								       	}
								    	 productConfigObj.setThemes(themeList);
										}
								    	 
								    	 List<String>categoriesList=existingApiProduct.getCategories();
								    	 productExcelObj.setCategories(categoriesList);
								 	 
								    	 List<Availability>availibilityList=new ArrayList<Availability>();
								    	 productExcelObj.setAvailability(availibilityList);
								    	 
								    	 List<ImprintSize>imprintList=new ArrayList<ImprintSize>();
								    	 productConfigObj.setImprintSize(imprintList);
									 
								    	List<ProductNumber>ProdNolist=new ArrayList<ProductNumber>();
								    	 productExcelObj.setProductNumbers(ProdNolist);
								    	 
								    	 
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
							/*String Inventory=cell.getStringCellValue();
							
							if (!StringUtils.isEmpty(Inventory)) {
                            Inventory invtObj=new Inventory();
                            invtObj.setInventoryLink(Inventory);
							productExcelObj.setInventory(invtObj);
							}
							*/
							productName = cell.getStringCellValue();
							int len=productName.length();
							 if(len>60){
								String strTemp=productName.substring(0, 60);
								int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
								productName=(String) strTemp.subSequence(0, lenTemp);
							}
							productExcelObj.setName(productName);

							break;	
							
						case 12: // Name
							/*productName = cell.getStringCellValue();
							int len=productName.length();
							 if(len>60){
								String strTemp=productName.substring(0, 60);
								int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
								productName=(String) strTemp.subSequence(0, lenTemp);
							}
							productExcelObj.setName(productName);
*/

							break;	
					
						case 14: // ShortDescription
							String Summary=cell.getStringCellValue();
							if (!StringUtils.isEmpty(Summary)) {
							int Summarylength=Summary.length();	
							if(Summarylength > 130){
						    Summary=Summary.substring(0, 130);
							}
							productExcelObj.setSummary(Summary);	
							}
							
							break;	
							
						case 15: // LongDescription
							String description = CommonUtility.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(ExstngDescription)) {
								description=description.replace("?","").replace("ã","").replace("¡", "").replace(":", "");
								productExcelObj.setDescription(description);
							}
							
//							if (!StringUtils.isEmpty(description)) {
//								description=description.replace("?","").replace("ã","").replace("¡", "").replace(":", "");
//								productExcelObj.setDescription(description);
//							} else {
//								productExcelObj
//										.setDescription(productName);
//							}

							break;	
					
					/*	case 19: // Large Image URL0
							 Image1=cell.getStringCellValue();
							 if (!StringUtils.isEmpty(Image1)) {	
								Image=Image.append(Image1).append(",");
							  }

							break;	
					
						case 22: // Large Image URL1
							 Image2=cell.getStringCellValue();
							 if (!StringUtils.isEmpty(Image2)) {	
									Image=Image.append(Image2).append(""
											+ ""
											+ ""
											+ ",");
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
							 Image10=CommonUtility.getCellValueStrinOrInt(cell);
							 if (!StringUtils.isEmpty(Image10)) {	
									Image=Image.append(Image10).append(",");
									Image10=Image1.toString();
									 listImage=maxplusAttribute.getImages(Image10);
								  }
							 productExcelObj.setImages(listImage);


							break;	*/
		
						case 46: // Price1
		                    ListPrice1=CommonUtility.getCellValueStrinOrDecimal(cell);

							break;	
							
							
						case 47: // QtyBreak1
							 Quantity2=CommonUtility.getCellValueStrinOrInt(cell);

							break;	
							
							
						case 48: // Price2
		                    ListPrice2=CommonUtility.getCellValueStrinOrDecimal(cell);


							break;	
							
							
						case 49: // QtyBreak2
							 Quantity3=CommonUtility.getCellValueStrinOrInt(cell);
							 
							break;	
							
							
						case 50: // Price3
		                    ListPrice3=CommonUtility.getCellValueStrinOrDecimal(cell);


							break;	
							
							
						case 51: // QtyBreak3
							 Quantity4=CommonUtility.getCellValueStrinOrInt(cell);

							break;	
							
							
						case 52: // Price4
		                    ListPrice4=CommonUtility.getCellValueStrinOrDecimal(cell);


							break;	
							
							
						case 53: // QtyBreak4
							 Quantity5=CommonUtility.getCellValueStrinOrInt(cell);

							break;	
							
							
						case 54: // Price5
		                    ListPrice5=CommonUtility.getCellValueStrinOrDecimal(cell);
							
							break;	
							
							
						case 55: // QtyBreak5
							 Quantity6=CommonUtility.getCellValueStrinOrInt(cell);
							 
							 
						  break;	
							
						case 56: // Price6
		                 ListPrice6=CommonUtility.getCellValueStrinOrDecimal(cell);
		         
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
							
						case 89://Setup Charge 3
							SetUpcharge3=CommonUtility.getCellValueStrinOrInt(cell);
							SetUpcharge3=SetUpcharge3.replaceAll("[^0-9.%/ ]","");
							
							break;	
							
						case 90://Setup Charge 2
							SetUpcharge2=CommonUtility.getCellValueStrinOrInt(cell);
							SetUpcharge2=SetUpcharge2.replaceAll("[^0-9.%/ ]","");

							
							break;
							
						case 91: // Imprint Area 3
                         ImprintSize1=cell.getStringCellValue();
						if (!StringUtils.isEmpty(ImprintSize1)) {							
                        ImprintSize=ImprintSize.append(ImprintSize1).append(",");
						}
							
							break;	
							
						case 92: // Imprint Area 2
	                     ImprintSize2=cell.getStringCellValue();
						if (!StringUtils.isEmpty(ImprintSize2) && !ImprintSize2.equalsIgnoreCase(ImprintSize1)) {							
                        ImprintSize=ImprintSize.append(ImprintSize2).append(",");
						}				

							break;	
							
						case 93: // Imprint Area
	                    String ImprintSize3=cell.getStringCellValue();
						if ((!StringUtils.isEmpty(ImprintSize3)&& !ImprintSize3.equalsIgnoreCase(ImprintSize1))
								&& !ImprintSize3.equalsIgnoreCase(ImprintSize2)) {							
                        ImprintSize=ImprintSize.append(ImprintSize3).append(",");	
						}
                        String Imprintsize=ImprintSize.toString();
                        listimprintSize=maxplusAttribute.getImprintSize(Imprintsize);
                        productConfigObj.setImprintSize(listimprintSize);
					

							break;	
							
							
						case 94: // Imprint Method 3
							 ImprintMethod1=cell.getStringCellValue();
							if (!StringUtils.isEmpty(ImprintMethod1)) {							
								ImprintMethod=ImprintMethod.append(ImprintMethod1).append(",");
							}
							break;	
							
							
						case 95: // Imprint Method 2
							 ImprintMethod2=cell.getStringCellValue();
							if (!StringUtils.isEmpty(ImprintMethod2)&& !ImprintMethod2.equalsIgnoreCase(ImprintMethod1)) {							
								ImprintMethod=ImprintMethod.append(ImprintMethod2).append(",");


							}
							break;	
							
							
						case 96: // Imprint Method
							 ImprintMethod3=cell.getStringCellValue();
							if ((!StringUtils.isEmpty(ImprintMethod3)&& !ImprintMethod3.equalsIgnoreCase(ImprintSize1))
								&& !ImprintMethod3.equalsIgnoreCase(ImprintMethod2)) {							
								ImprintMethod=ImprintMethod.append(ImprintMethod3).append(",");
							}
							String ImprintMthod=ImprintMethod.toString();
							listimprintMethods=maxplusAttribute.getImprintMethod(ImprintMthod);
                            productConfigObj.setImprintMethods(listimprintMethods);
							break;	
							
							
						case 97: // Size
							String Size=cell.getStringCellValue();
							if (!StringUtils.isEmpty(Size)) {							
							sizeObj=maxplusAttribute.getSize(Size);	
							productConfigObj.setSizes(sizeObj);	
							}	
							break;	
							
						case 98: // Rush Service
							String RushService=cell.getStringCellValue();
							if (!StringUtils.isEmpty(RushService)) {							
								rushserviceObj.setAvailable(true);	
								productConfigObj.setRushTime(rushserviceObj);
							}

							break;	
							
							
						case 99: // Product Color
                           String ProductColor=cell.getStringCellValue();
                           ProductColor=ProductColor.replace("As Shown", "");
                           if (!StringUtils.isEmpty(ProductColor)) {		
                        	   
                        	 listColor=maxplusAttribute.getColorCriteria(ProductColor); 
                        	 productConfigObj.setColors(listColor);  
                           }
							
							
							break;	
							
							
						case 100: // Approximate Production Time
							String ProductionTime=cell.getStringCellValue();
							if (!StringUtils.isEmpty(ProductionTime)) {
								prodtimeObj.setBusinessDays("7-10");
								listProductionTime.add(prodtimeObj);
							    productConfigObj.setProductionTime(listProductionTime);	
							}
							break;	
							
							
						case 101: // Items Per Carton
							 ShippingItem=cell.getStringCellValue();

							break;	
							
							
						case 102: // Weight Per Carton (lbs.)
							String ShippingWeight=cell.getStringCellValue();
							shippingEstimateObj=maxplusAttribute.getShippingestimete(ShippingItem,ShippingWeight);
							productConfigObj.setShippingEstimates(shippingEstimateObj);

							break;	
							
						case 103:
							SetUpcharge1=CommonUtility.getCellValueStrinOrInt(cell);
							SetUpcharge1=SetUpcharge1.replaceAll("[^0-9.%/ ]","");
							
							break;
							
						case 104:
							Runcharge1=CommonUtility.getCellValueStrinOrInt(cell);
							Runcharge1=Runcharge1.replaceAll("[^0-9.%/ ]","").trim();
							break;
							
						case 107:
							Runcharge2=CommonUtility.getCellValueStrinOrInt(cell);
							Runcharge2=Runcharge2.replaceAll("[^0-9.%/ ]","").trim();						
							break;
							
						case 108:
							Runcharge3=CommonUtility.getCellValueStrinOrInt(cell);
							Runcharge3=Runcharge3.replaceAll("[^0-9.%/ ]","").trim();					
							break;
						
						case 110://Additional Notes
							String AdditionalproductInfo=cell.getStringCellValue();
							if (!StringUtils.isEmpty(AdditionalproductInfo)) {
						    	AdditionalproductInfo=AdditionalproductInfo.replace("<br>", "");
								productExcelObj.setAdditionalProductInfo(AdditionalproductInfo);
							}
							
							break;
							
						
							
						case 111:
							String Keyword=cell.getStringCellValue();
							if (!StringUtils.isEmpty(Keyword)) {
							String keywordArr[]=Keyword.split(",");
							for (String KeywordName : keywordArr) {
								listKeyword.add(KeywordName);
							}
							productExcelObj.setProductKeywords(listKeyword);	
							}
							break;
							
											
						
						case 129:// MinimumQty		
							Quantity1=CommonUtility.getCellValueStrinOrInt(cell);
							break;	
							
							
						

						} // end inner while loop

					}
					// set product configuration objects

					// end inner while loop
					productExcelObj.setPriceType("L");
					productExcelObj.setCanOrderLessThanMinimum(T);
			
	
			        listOfPrices=listOfPrices.append(ListPrice1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).
							 append(ListPrice2).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(ListPrice3).
					 append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(ListPrice4);
			        
			   	listOfQuantity=listOfQuantity.append(Quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).
							 append(Quantity2).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(Quantity3).
					 append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(Quantity4);
						
			    	   if (!StringUtils.isEmpty(ListPrice5)) {
				        	listOfPrices=listOfPrices.append
									 (ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(ListPrice5);
				        	listOfQuantity=listOfQuantity.append(ApplicationConstants
									 .PRICE_SPLITTER_BASE_PRICEGRID).append(Quantity5);
				        }
			    	
			    	
			        if (!StringUtils.isEmpty(ListPrice6)) {
			        	listOfPrices=listOfPrices.append
								 (ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(ListPrice6);
			        	listOfQuantity=listOfQuantity.append(ApplicationConstants
								 .PRICE_SPLITTER_BASE_PRICEGRID).append(Quantity6);
			        }
					 ListAllprice=listOfPrices.toString();
					 ListAllquantity=listOfQuantity.toString();
				
					
					priceGrids = pricegrid.getPriceGrids(ListAllprice,
							ListAllquantity, "R", "USD",
					         "", true, "N",productName ,"",priceGrids);

		        	String RepeatImprintMethod="";
					
					if (!StringUtils.isEmpty(ImprintMethod1)) {
						
						RepeatImprintMethod=RepeatImprintMethod.concat(ImprintMethod1);
						
						priceGrids = pricegrid
								.getUpchargePriceGrid("1", SetUpcharge3,
										"V",
										"Imprint Method", "false", "USD",
										ImprintMethod1,
										"Set-up Charge", "Other",
										new Integer(1), priceGrids);	
						
						priceGrids = pricegrid
								.getUpchargePriceGrid("1", Runcharge3,
										"V",
										"Imprint Method", "false", "USD",
										ImprintMethod1,
										"Run Charge", "Other",
										new Integer(1), priceGrids);	
					}
					 if(!StringUtils.isEmpty(ImprintMethod2))
					{
						 
					if(!RepeatImprintMethod.equalsIgnoreCase(""))
					{
					RepeatImprintMethod=RepeatImprintMethod.concat(",").concat(ImprintMethod2);
					}else
					{
						RepeatImprintMethod=RepeatImprintMethod.concat(ImprintMethod2);	
					}

						priceGrids = pricegrid
								.getUpchargePriceGrid("1", SetUpcharge2,
										"V",
										"Imprint Method", "false", "USD",
										ImprintMethod2,
										"Set-up Charge", "Other",
										new Integer(1), priceGrids);	
						
						priceGrids = pricegrid
								.getUpchargePriceGrid("1", Runcharge2,
										"V",
										"Imprint Method", "false", "USD",
										ImprintMethod2,
										"Run Charge", "Other",
										new Integer(1), priceGrids);	
					}
					 if(!StringUtils.isEmpty(ImprintMethod3))
					{
						 if(!RepeatImprintMethod.equalsIgnoreCase("")) {
							RepeatImprintMethod=RepeatImprintMethod.concat(",").concat(ImprintMethod3);	 
						 }else
						 {
							RepeatImprintMethod=RepeatImprintMethod.concat(ImprintMethod3);
						 }

						priceGrids = pricegrid
								.getUpchargePriceGrid("1", SetUpcharge1,
										"V",
										"Imprint Method", "false", "USD",
										ImprintMethod3,
										"Set-up Charge", "Other",
										new Integer(1), priceGrids);
						
						priceGrids = pricegrid
								.getUpchargePriceGrid("1", Runcharge1,
										"V",
										"Imprint Method", "false", "USD",
										ImprintMethod3,
										"Run Charge", "Other",
										new Integer(1), priceGrids);	
	
					}
					 
			 
					 
				
					
						priceGrids = pricegrid
								.getUpchargePriceGrid("1", "25",
										"V",
										"Imprint Method", "false", "USD",
										RepeatImprintMethod,
										"Set-up Charge", "Other",
										new Integer(1), priceGrids);
				
					
					 
					 
						priceGrids = pricegrid
								.getUpchargePriceGrid("1", "25",
										"V",
										"Less than Minimum", "false", "USD",
										"Can order less than minimum",
										"Less than Minimum Charge", "Other",
										new Integer(1), priceGrids);		 
					 
					 
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
			
			priceGrids = new ArrayList<PriceGrid>();
		    listProductionTime = new ArrayList<ProductionTime>();
		    listColor = new ArrayList<Color>();
			listimprintMethods = new ArrayList<ImprintMethod>();
			listimprintSize = new ArrayList<ImprintSize>();
			listKeyword = new ArrayList<String>();
		    prodtimeObj=new ProductionTime();
            rushserviceObj=new RushTime();
	        ImprintMethod=new StringBuilder();
	        ImprintSize=new StringBuilder();
	        listOfQuantity = new StringBuilder();
			listOfPrices = new StringBuilder();
	        shippingEstimateObj=new ShippingEstimate();
	        sizeObj=new Size();
	     	 Quantity1=null;
			 Quantity2=null;
			 Quantity3=null;
			 Quantity4=null;
			 Quantity5=null;
			 Quantity6=null;
			
			 ListPrice1=null;
			 ListPrice2=null;
			 ListPrice3=null;
			 ListPrice4=null;
			 ListPrice5=null;
			 ListPrice6=null;
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
