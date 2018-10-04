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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import parser.AccessLine.AccessLineAttributeParser;
import parser.primeline.PrimeLineConstants;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.Price;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AccessLineMapping {
private static final Logger _LOGGER = Logger.getLogger(TomaxUsaMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	@Autowired
	ObjectMapper mapperObj;
	
	//TomaxUsaAttributeParser tomaxUsaAttributeParser;
	//TomaxProductTabParser tomaxProductTabParser;
	
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
	
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  Product existingApiProduct = null;
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  List<PriceGrid> priceGrids = null;
		  Set<String> listOfColors = new HashSet<>();
		  String colorCustomerOderCode ="";
		  List<String> repeatRows = new ArrayList<>();
		 
		  String productName=null;
		  ShippingEstimate	shippingEstObj=new ShippingEstimate();
		  StringBuilder listOfPrices = new StringBuilder();
		  StringBuilder listOfQuantity = new StringBuilder();
		  StringBuilder listOfNetPrices = new StringBuilder();
		  StringBuilder listOfDiscount = new StringBuilder();
		  String basePricePriceInlcude="";
		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(1);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		
	    String xid = null;
	    int columnIndex=0;
	    String q1 = null,q2= null,q3= null,q4= null,q5= null;
		while (iterator.hasNext()) {
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum()==0){
			continue;
			}
			
			/*if (nextRow.getRowNum() == 0)
				continue;*/
			
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if(xid != null){
				productXids.add(xid);
				repeatRows.add(xid);
			}
			
			 boolean checkXid  = false; //imp line
			//boolean checkXid  = true; //imp line
			//xid=getProductXid(nextRow);
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
			    columnIndex = cell.getColumnIndex();
				if(columnIndex + 1 == 1){
					xid = getProductXid(nextRow);//CommonUtility.getCellValueStrinOrInt(cell);//
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							 /*if( !StringUtils.isEmpty(listOfPrices.toString())){
							 priceGrids=new ArrayList<PriceGrid>();
							 priceGrids = tomaxPriceGridParser.getPriceGrids(listOfPrices.toString(),listOfQuantity.toString(), 
										"R",ApplicationConstants.CONST_STRING_CURRENCY_USD,
										"",ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
										productName,null,1,priceGrids);
							 }*/
							 
							 /*String listOfPrices,String netPrices,
							    String listOfQuan, String discountCodes,
								String currency, String priceInclude, boolean isBasePrice,
								String qurFlag, String priceName, String criterias,
								List<PriceGrid> existingPriceGrid*/
								
							 priceGrids = pioneerPriceGridParserr.getPriceGrids(listOfPrices.toString(),listOfNetPrices.toString(),listOfQuantity.toString(), 
									 listOfDiscount.toString(),ApplicationConstants.CONST_STRING_CURRENCY_USD,
									 basePricePriceInlcude,ApplicationConstants.CONST_BOOLEAN_TRUE, 
										ApplicationConstants.CONST_STRING_FALSE, productName,null,priceGrids);
							 if(CollectionUtils.isEmpty(priceGrids)){
									//priceGrids = AccessLineAttributeParser.getPriceGridsQur();	
								}
							 	productExcelObj.setPriceType("L");
							 	productExcelObj.setPriceGrids(priceGrids);
							 	productExcelObj.setProductConfigurations(productConfigObj);
							 /*_LOGGER.info("Product Data : "
										+ mapperObj.writeValueAsString(productExcelObj));*/
							 
						if(!StringUtils.isEmpty(productExcelObj.getExternalProductId())){
							 int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId, environmentType);
							 	if(num ==1){
							 		numOfProductsSuccess.add("1");
							 	}else if(num == 0){
							 		numOfProductsFailure.add("0");
							 	}else{
							 		
							 	}
							 	_LOGGER.info("list size>>>>>>>"+numOfProductsSuccess.size());
							 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
						 }
							 	
								priceGrids = new ArrayList<PriceGrid>();
								productConfigObj = new ProductConfigurations();	
								repeatRows.clear();
								listOfPrices = new StringBuilder();
							    listOfQuantity = new StringBuilder();
							    shippingEstObj=new ShippingEstimate();

						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    	repeatRows.add(xid);
						    }
						    productExcelObj = new Product();
						    existingApiProduct = postServiceImpl.getProduct(accessToken, xid, null); 
						     if(existingApiProduct == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						    	// productExcelObj = tomaxUsaAttributeParser.getExistingProductData(existingApiProduct, existingApiProduct.getProductConfigurations());
						    	 productConfigObj=productExcelObj.getProductConfigurations();
								
						     }
					 }
				}else{
					if(productXids.contains(xid) && repeatRows.size() != 1){
						 if(isRepeateColumn(columnIndex+1)){
							 continue;
						 }
					}
				}
		
				switch (columnIndex + 1) {
				case 1://Existing Xids
					break;
				case 2://Access Line Item No. 
					break;
				case 3://Description
					 productName=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(productName)){
							productName=CommonUtility.getStringLimitedChars(productName, 60);
							productName=CommonUtility.removeRestrictSymbols(productName);
							productExcelObj.setName(productName);
						}
					break;
				case 4://
					break;
				case 5://Features
					String descripton=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(descripton)){
						 descripton=CommonUtility.getStringLimitedChars(descripton, 800);
						 descripton=CommonUtility.removeRestrictSymbols(descripton);
						 descripton=descripton.replace("�", "");
						 descripton=descripton.replace("\n", "");
						 
						 productExcelObj.setDescription(descripton);
						
						/* String nameTemp=productExcelObj.getName();
							 if(StringUtils.isEmpty(nameTemp)){
								 productName=CommonUtility.getStringLimitedChars(descripton, 60);
								 productExcelObj.setName(productName);
							 }*/
							}else{
								productExcelObj.setDescription(productName);
							}
					
					break;
				case 6://Colors
					break;
				case 7://Category
					String Category = cell.getStringCellValue();
					
					if(!StringUtils.isEmpty(Category)){
					   List<String> categories = CommonUtility.getStringAsList(Category,
							   										ApplicationConstants.CONST_DELIMITER_COMMA);
					   productExcelObj.setCategories(categories);
					}
					break;
				case 8://
					break;
				case 9://
					break;
				case 10://
					break;
				case 11://
					break;
				case 12://
					break;
				case 13://
					break;
				case 14://
					break;
				case 15://
					break;
				case 16://
					break;
				case 17://
					break;
				case 18://
					break;
				case 19://
					break;
				case 20://Price
					break;
				case 21://C.C
					break;
				case 22://Imprint
					break;
				case 23://Batteries
					break;
				case 24://Shipping Vendor 3
					break;
				case 25://Total
					break;
				case 26://
					break;
				case 27://
					break;
				case 28://50
					break;
				case 29://100
					break;
				case 30://200
					break;
				case 31://300
					break;
				case 32://300
					String	netPrice1=null;
					netPrice1=CommonUtility.getCellValueStrinOrDecimal(cell);
					netPrice1=netPrice1.replaceAll(" ","");
					netPrice1 = netPrice1.replaceAll("\\(.*\\)", "");
					if(!StringUtils.isEmpty(netPrice1) ){
						listOfNetPrices.append(netPrice1.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					}
					break;
				case 33://500
					String	netPrice2=null;
					netPrice2=CommonUtility.getCellValueStrinOrDecimal(cell);
					netPrice2=netPrice2.replaceAll(" ","");
					netPrice2 = netPrice2.replaceAll("\\(.*\\)", "");
					if(!StringUtils.isEmpty(netPrice2) ){
						listOfNetPrices.append(netPrice2.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					}
					break;
				case 34://1000
					String	netPrice3=null;
					netPrice3=CommonUtility.getCellValueStrinOrDecimal(cell);
					netPrice3=netPrice3.replaceAll(" ","");
					netPrice3 = netPrice3.replaceAll("\\(.*\\)", "");
					if(!StringUtils.isEmpty(netPrice3) ){
						listOfNetPrices.append(netPrice3.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					}
					break;
				case 35://2500
					String	netPrice4=null;
					netPrice4=CommonUtility.getCellValueStrinOrDecimal(cell);
					netPrice4=netPrice4.replaceAll(" ","");
					netPrice4 = netPrice4.replaceAll("\\(.*\\)", "");
					if(!StringUtils.isEmpty(netPrice4) ){
						listOfNetPrices.append(netPrice4.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					}
					break;
				case 36://5000
					String	netPrice5=null;
					netPrice5=CommonUtility.getCellValueStrinOrDecimal(cell);
					netPrice5=netPrice5.replaceAll(" ","");
					netPrice5 = netPrice5.replaceAll("\\(.*\\)", "");
					if(!StringUtils.isEmpty(netPrice5) ){
						listOfNetPrices.append(netPrice5.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					}
					break;
				case 37://
					break;
				case 38://300
					String	listPrice1=null;
					listPrice1=CommonUtility.getCellValueStrinOrDecimal(cell);
					listPrice1=listPrice1.replaceAll(" ","");
					listPrice1 = listPrice1.replaceAll("\\(.*\\)", "");
					if(!StringUtils.isEmpty(listPrice1)){
						listOfPrices.append(listPrice1.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						 }
					break;
				case 39://500
					String	listPrice2=null;
					listPrice2=CommonUtility.getCellValueStrinOrDecimal(cell);
					listPrice2=listPrice2.replaceAll(" ","");
					listPrice2 = listPrice2.replaceAll("\\(.*\\)", "");
					if(!StringUtils.isEmpty(listPrice2)){
						listOfPrices.append(listPrice2.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						 }
					break;
				case 40://1000
					String	listPrice3=null;
					listPrice3=CommonUtility.getCellValueStrinOrDecimal(cell);
					listPrice3=listPrice3.replaceAll(" ","");
					listPrice3 = listPrice3.replaceAll("\\(.*\\)", "");
					if(!StringUtils.isEmpty(listPrice3)){
						listOfPrices.append(listPrice3.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						 }
					break;
				case 41://2500
					String	listPrice4=null;
					listPrice4=CommonUtility.getCellValueStrinOrDecimal(cell);
					listPrice4=listPrice4.replaceAll(" ","");
					listPrice4 = listPrice4.replaceAll("\\(.*\\)", "");
					if(!StringUtils.isEmpty(listPrice4)){
						listOfPrices.append(listPrice4.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						 }
					break;
				case 42://5000
					String	listPrice5=null;
					listPrice5=CommonUtility.getCellValueStrinOrDecimal(cell);
					listPrice5=listPrice5.replaceAll(" ","");
					listPrice5 = listPrice5.replaceAll("\\(.*\\)", "");
					if(!StringUtils.isEmpty(listPrice5)){
						listOfPrices.append(listPrice5.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						 }
					break;
				case 43://Code
					
					String	discountCode=null;
					discountCode=CommonUtility.getCellValueStrinOrDecimal(cell);
					if(!StringUtils.isEmpty(discountCode) && !discountCode.toUpperCase().equals("NULL")){
						listOfDiscount=PrimeLineConstants.DISCOUNTCODE_MAP.get(discountCode.trim());
						if(StringUtils.isEmpty(listOfDiscount)){
							listOfDiscount.append("Z___Z___Z___Z___Z"); 
						}
						}else{
							listOfDiscount.append("Z___Z___Z___Z___Z"); 
			         }
					break;
				case 44://Set-Up Charge:
					break;
				case 45://Normal Production Time
					
					//production Time
					List<ProductionTime> listOfProductionTime = new ArrayList<ProductionTime>();
					String prodTimeLo = null;
					int tempVal;
					ProductionTime productionTime = new ProductionTime();
					prodTimeLo=CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(prodTimeLo)){
					prodTimeLo=prodTimeLo.toLowerCase();
				    prodTimeLo=prodTimeLo.replaceAll("days",ApplicationConstants.CONST_STRING_EMPTY);
				    prodTimeLo=prodTimeLo.replaceAll("working",ApplicationConstants.CONST_STRING_EMPTY);
					productionTime.setBusinessDays(prodTimeLo);
					productionTime.setDetails(ApplicationConstants.CONST_STRING_DAYS);
					listOfProductionTime.add(productionTime);
					productConfigObj.setProductionTime(listOfProductionTime);
					}
					break;
				case 46://FOB
					String fobPoint=CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(fobPoint)){
						fobPoint=fobPoint.toUpperCase().trim();
						 if(fobPoint.contains("NJ")){
						FOBPoint fobPointObj=new FOBPoint();
						List<FOBPoint> listfobPoints = new ArrayList<FOBPoint>();
						
							fobPointObj.setName("Mount Holly, NJ 08060 USA");
						
						listfobPoints.add(fobPointObj);
						productExcelObj.setFobPoints(listfobPoints);
						 }
					}
					break;
				case 47://Imprint Method
					String impmtdValue=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(impmtdValue) && !impmtdValue.equals("0") && !impmtdValue.equals("n/a")){
						 productConfigObj= AccessLineAttributeParser.getImprintMethod(impmtdValue,productConfigObj);
						// productConfigObj.setImprintMethods(listOfImprintMethod);
						productExcelObj.setProductConfigurations(productConfigObj);
					    }
					break;
				case 48://Price Includes
					basePricePriceInlcude=CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(basePricePriceInlcude)){
						basePricePriceInlcude=basePricePriceInlcude.replace("Price includes","");
					}else{
						basePricePriceInlcude="";
					}
					break;
				case 49://Rush Service
					break;
				case 50://Additional color/position
					break;
				case 51://Item Size
					break;
				case 52://Imprint Area
					break;
				case 53://Paper / Email Proofs:
					break;
				case 54://PMS Matching
					break;
				case 55://Absolute Minimum
					
					String imprintMethodValueUpchrg=CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(imprintMethodValue)&& !StringUtils.isEmpty(imprintMethodValueUpchrg)){
						if(!imprintMethodValue.equals("None")){
						priceGrids = pioneerPriceGridParserr.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,imprintMethodValueUpchrg, "R",
								"Less than Minimum"+":"+"Can order less than minimum",ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, "","Can order less than minimum", 
								"Less than Minimum Charge", ApplicationConstants.CONST_VALUE_TYPE_OTHER,"Optional", ApplicationConstants.CONST_INT_VALUE_ONE, priceGrids);
						}
						}
					break;
				case 56://Packaging
					String packaging=cell.getStringCellValue();
					if(!StringUtils.isEmpty(packaging)){
						if(!packaging.equals("N/A")){
					List<Packaging> packagingList =new ArrayList<Packaging>();
					Packaging packObj;
					try{
					String packagingArr[] = packaging.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					for (String tempPackaging : packagingArr) {
						packObj=new Packaging();
						packObj.setName(tempPackaging);
			 			packagingList.add(packObj);
					}
					productConfigObj.setPackaging(packagingList);
					}catch(Exception e){
						_LOGGER.error("Error while processing Product Packaging :"+e.getMessage());
					   }
						}
					}
					break;
				case 57://SHIPPING noi
					String shippingItem=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(shippingItem)){
						 shippingEstObj=AccessLineAttributeParser.getShippingEstimates(shippingItem,"",shippingEstObj,"NOI","");
						 productConfigObj.setShippingEstimates(shippingEstObj);
					 }
					break;
				case 58://wt
					String shippingWt=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(shippingWt)){
						 shippingEstObj=AccessLineAttributeParser.getShippingEstimates(shippingWt,"",shippingEstObj,"WT","");
						 productConfigObj.setShippingEstimates(shippingEstObj);
					 }
					break;
				case 59://H
					String shippingHT=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(shippingHT)){
						 shippingEstObj=AccessLineAttributeParser.getShippingEstimates("",shippingHT,shippingEstObj,"SDIM","H");
						 productConfigObj.setShippingEstimates(shippingEstObj);
					 }
					break;
				case 60://W
					String shippingWT=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(shippingWT)){
						 shippingEstObj=AccessLineAttributeParser.getShippingEstimates("",shippingWT,shippingEstObj,"SDIM","W");
						 productConfigObj.setShippingEstimates(shippingEstObj);
					 }
					break;
				case 61://L
					String shippingLen=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(shippingLen)){
						 shippingEstObj=AccessLineAttributeParser.getShippingEstimates("",shippingLen,shippingEstObj,"SDIM","L");
						 productConfigObj.setShippingEstimates(shippingEstObj);
					 }
					break;
				case 62://origin
					String origin =cell.getStringCellValue();
					if(!StringUtils.isEmpty(origin)){
					
					List<Origin> productOrigin = new ArrayList<Origin>();
					Origin originObj = new Origin();
					originObj.setName("U.S.A.");
					productOrigin.add(originObj);
					productConfigObj.setOrigins(productOrigin);
					}
					break;
				case 63://
					break;
				case 64://50
					break;
				case 65://100
					break;
				case 66://200
					break;
				case 67://300
					break;
				case 68://500
					break;
				case 69://1000
					break;
				case 70://2500
					break;
				case 71://5000
					break;
				case 72://10000
					break;
				case 73://
					break;
				case 74://Vendor
					break;
				case 75://Charge
					break;
				case 76://Set Up Charge Profit
					break;
				case 77://
					break;
				case 78://50
					break;
				case 79://100
					break;
				case 80://200
					break;
				case 81://300
					break;
				case 82://500
					break;
				case 83://1000
					break;
				case 84://2500
					break;
				case 85://5000
					break;
				case 86://10000
					break;

				}  // end inner while loop					 
			}		
			}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);		 
				ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: "
				+e.getMessage()+" at column number(increament by 1)"+columnIndex);
				productDaoObj.save(apiResponse.getErrors(),
						productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);
				}
		}
		workbook.close();
		 // do criteria processing over here
		 /*if( !StringUtils.isEmpty(listOfPrices.toString())){
			 priceGrids=new ArrayList<PriceGrid>();
			 priceGrids = tomaxPriceGridParser.getPriceGrids(listOfPrices.toString(),listOfQuantity.toString(), 
						"R",ApplicationConstants.CONST_STRING_CURRENCY_USD,
						"",ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
						productName,null,1,priceGrids);
			 }*/
			 if(CollectionUtils.isEmpty(priceGrids)){
					//priceGrids = tomaxPriceGridParser.getPriceGridsQur();	
				}
			 	productExcelObj.setPriceType("L");
			 	productExcelObj.setPriceGrids(priceGrids);
			 	productExcelObj.setProductConfigurations(productConfigObj);
		 	_LOGGER.info("Product Data : "
					+ mapperObj.writeValueAsString(productExcelObj));
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId, environmentType);
		 	if(num ==1){
		 		numOfProductsSuccess.add("1");
		 	}else if(num == 0){
		 		numOfProductsFailure.add("0");
		 	}
		 	_LOGGER.info("list size>>>>>>"+numOfProductsSuccess.size());
		 	_LOGGER.info("Failure list size>>>>>>"+numOfProductsFailure.size());
	       finalResult = numOfProductsSuccess.size() + "," + numOfProductsFailure.size();
	    productDaoObj.saveErrorLog(asiNumber,batchId);
		priceGrids = new ArrayList<PriceGrid>();
		productConfigObj = new ProductConfigurations();
		listOfPrices = new StringBuilder();
	    listOfQuantity = new StringBuilder();
	    shippingEstObj=new ShippingEstimate();
		repeatRows.clear();
		return finalResult;
		}catch(Exception e){
			_LOGGER.error("Error while Processing excel sheet " +e.getMessage());
			return finalResult;
		}finally{
			try {
				workbook.close();
			} catch (IOException e) { 
				_LOGGER.error("Error while Processing excel sheet" +e.getMessage());
	
			}
				_LOGGER.info("Complted processing of excel sheet ");
				_LOGGER.info("Total no of product:"+numOfProductsSuccess.size() );
		}
		
	}

	

	public String getProductXid(Row row){
		Cell xidCell =  row.getCell(0);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid) || productXid.trim().equalsIgnoreCase("#N/A")){
		     xidCell = row.getCell(1);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}
	
	public boolean isRepeateColumn(int columnIndex){
		if(columnIndex != 1&&columnIndex != 2){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	
	public static List<PriceGrid> getPriceGrids(String basePriceName) 
	{
		
		List<PriceGrid> newPriceGrid=new ArrayList<PriceGrid>();
		try{
			Integer sequence = 1;
			List<PriceConfiguration> configuration = null;
			PriceGrid priceGrid = new PriceGrid();
			priceGrid.setCurrency(ApplicationConstants.CONST_STRING_CURRENCY_USD);
			priceGrid.setDescription(basePriceName);
			priceGrid.setPriceIncludes(ApplicationConstants.CONST_STRING_EMPTY);
			priceGrid.setIsQUR(ApplicationConstants.CONST_BOOLEAN_TRUE);
			priceGrid.setIsBasePrice(true);
			priceGrid.setSequence(sequence);
			List<Price>	listOfPrice = new ArrayList<Price>();
			priceGrid.setPrices(listOfPrice);
			priceGrid.setPriceConfigurations(configuration);
			newPriceGrid.add(priceGrid);
	}catch(Exception e){
		_LOGGER.error("Error while processing PriceGrid: "+e.getMessage());
	}
		return newPriceGrid;
}

	public static List<PriceGrid> getPriceGridsQur( ) 
	{
		List<PriceGrid> newPriceGrid=new ArrayList<PriceGrid>();
		try{
			Integer sequence = 1;
			//List<PriceConfiguration> configuration = null;
			PriceGrid priceGrid = new PriceGrid();
			priceGrid.setIsBasePrice(true);
			priceGrid.setIsQUR(ApplicationConstants.CONST_BOOLEAN_TRUE);
			priceGrid.setDescription(ApplicationConstants.CONST_STRING_EMPTY);
			priceGrid.setPriceIncludes(ApplicationConstants.CONST_STRING_EMPTY);
			priceGrid.setSequence(sequence);
			priceGrid.setCurrency(ApplicationConstants.CONST_STRING_CURRENCY_USD);
			List<Price>	listOfPrice = new ArrayList<Price>();
			priceGrid.setPrices(listOfPrice);
			//priceGrid.setPriceConfigurations(configuration);
			newPriceGrid.add(priceGrid);
	}catch(Exception e){
		_LOGGER.error("Error while processing PriceGrid: "+e.getMessage());
	}
		_LOGGER.info("PriceGrid Processed");
		return newPriceGrid;
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
	
	public static final String CONST_STRING_COMBO_TEXT = "Combo";
	
	public ObjectMapper getMapperObj() {
		return mapperObj;
	}
	
	public void setMapperObj(ObjectMapper mapperObj) {
		this.mapperObj = mapperObj;
	}
}