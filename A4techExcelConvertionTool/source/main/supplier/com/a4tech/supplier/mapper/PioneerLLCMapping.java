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

import parser.PioneerLLC.PioneerLLCAttributeParser;
import parser.PioneerLLC.PioneerPriceGridParserr;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PioneerLLCMapping implements IExcelParser{

	
	private static final Logger _LOGGER = Logger.getLogger(PioneerLLCMapping.class);
	PostServiceImpl postServiceImpl;
	ProductDao productDaoObj;
	PioneerPriceGridParserr pioneerPriceGridParserr;
	PioneerLLCAttributeParser  pioneerLLCAttributeParser;
	@Autowired
	ObjectMapper mapperObj;
	
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber,int batchId, String environmentType){
		int columnIndex = 0;
		  List<String> numOfProductsSuccess = new ArrayList<String>();
		  List<String> numOfProductsFailure = new ArrayList<String>();
		  String finalResult = null;
		  Set<String>  listOfProductXids = new HashSet<String>();
		  Product productExcelObj = new Product();  
		  Product existingApiProduct = null;
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  String shippingValue="";
		  boolean existingFlag=false;
		  StringBuilder listOfQuantity = new StringBuilder();
		  StringBuilder listOfPrices = new StringBuilder();
		  StringBuilder listOfNetPrices = new StringBuilder();
		  StringBuilder listOfDiscount = new StringBuilder();
		  String basePricePriceInlcude="";
		  String tempQuant1="";
		  String productName ="";
		  String imprintMethodValue="";
		  Set<String>  productXids = new HashSet<String>();
		  List<String> repeatRows = new ArrayList<>();
		  ShippingEstimate ShipingObj=new ShippingEstimate();
	      Dimensions dimensionObj=new Dimensions();
		  try{
			  Cell cell2Data = null;
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
	
		
		 String xid = null;
		while (iterator.hasNext()) {
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum() == 0)
				continue;
			
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if(xid != null){
				productXids.add(xid);
				repeatRows.add(xid);
			}
			
			 boolean checkXid  = false;
			
			 while (cellIterator.hasNext()) {
				
				 Cell cell = cellIterator.next();
				 columnIndex = cell.getColumnIndex();
				 cell2Data = nextRow.getCell(2);
				 if (columnIndex + 1 == 1) {
				       if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				        xid = cell.getStringCellValue();
				       } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				        xid = String.valueOf((int) cell
				          .getNumericCellValue());
				       } else {
				        
				        xid =  CommonUtility
						          .getCellValueStrinOrInt(cell2Data);
				       }
				       checkXid = true;
				       _LOGGER.info("XID is:"+xid);
				      } else {
				       checkXid = false;
				      }
					if(checkXid){
						 if(!productXids.contains(xid)){
							 if(nextRow.getRowNum() != 1){
								 
								 // Ineed to set pricegrid over here
								 
								 if( !StringUtils.isEmpty(listOfPrices.toString())){
									 //priceGrids=new ArrayList<PriceGrid>();
									 /*priceGrids = pioneerPriceGridParser.getPriceGrids(listOfPrices.toString(),listOfNetPrices.toString(),listOfQuantity.toString(), 
												"R",ApplicationConstants.CONST_STRING_CURRENCY_USD,
												"",ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
												productName,null,1,priceGrids);
									*/ 
									 String disc="R";
									 StringBuilder sb = new StringBuilder();
									 String arrLen[]=listOfPrices.toString().split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
									 int len=arrLen.length;
									 for (int i = 0; i <len; i++) {
										//disc=ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID+"R";
										 sb.append("R"+ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
									}
									 //System.out.println(sb.toString().split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).length);
									 priceGrids = pioneerPriceGridParserr.getPriceGrids(listOfPrices.toString(),listOfNetPrices.toString(),listOfQuantity.toString(), 
											 sb.toString(),ApplicationConstants.CONST_STRING_CURRENCY_USD,
												ApplicationConstants.CONST_STRING_EMPTY,ApplicationConstants.CONST_BOOLEAN_TRUE, 
												ApplicationConstants.CONST_STRING_FALSE, productName,null,priceGrids);
									 }
								
									 if(CollectionUtils.isEmpty(priceGrids)){
											priceGrids = pioneerPriceGridParserr.getPriceGridsQur();	
										}
								   // Add repeatable sets here
								/* productExcelObj=bagMakersPriceGridParser.getPricingData(listOfPrices.toString(), listOfQuantity.toString(), listOfDiscount.toString(), basePricePriceInlcude, 
											plateScreenCharge, plateScreenChargeCode,
										    plateReOrderCharge, plateReOrderChargeCode, priceGrids, 
										    productExcelObj, productConfigObj);*/
									 if(CollectionUtils.isEmpty(productConfigObj.getImprintMethods())){
										 List<ImprintMethod> listOfImprintMethod = new ArrayList<>();
										 ImprintMethod	imprintMethodObj = new ImprintMethod();	
										imprintMethodObj.setType("UNIMPRINTED");
										imprintMethodObj.setAlias("UNIMPRINTED");
										listOfImprintMethod.add(imprintMethodObj);
										productConfigObj.setImprintMethods(listOfImprintMethod);
									}
								 	productExcelObj.setPriceType("L");
								 	productExcelObj.setPriceGrids(priceGrids);
								 	productExcelObj.setProductConfigurations(productConfigObj);
								 	/* _LOGGER.info("Product Data : "
												+ mapperObj.writeValueAsString(productExcelObj));
								 	*/
								 	
								 	
								 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId, environmentType);
								 	if(num ==1){
								 		numOfProductsSuccess.add("1");
								 	}else if(num == 0) {
								 		numOfProductsFailure.add("0");
								 	}else{
								 		
								 		
								 	}
								 	_LOGGER.info("list size of success>>>>>>>"+numOfProductsSuccess.size());
								 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
									//reset all list and objects over here
								       //numOfProductsSuccess = new ArrayList<String>();
									   //numOfProductsFailure = new ArrayList<String>();
									   //finalResult = "";
									   listOfProductXids = new HashSet<String>();
									   productExcelObj = new Product();  
									   existingApiProduct = null;
									   productConfigObj=new ProductConfigurations();
									   priceGrids = new ArrayList<PriceGrid>();
									   shippingValue="";
									   existingFlag=false;
									   listOfQuantity = new StringBuilder();
									   listOfPrices = new StringBuilder();
									   listOfNetPrices = new StringBuilder();
									   listOfDiscount = new StringBuilder();
									   basePricePriceInlcude="";
									   tempQuant1="";
									   productName ="";
									   imprintMethodValue="";
									   productXids = new HashSet<String>();
									   repeatRows = new ArrayList<>();
									   ShipingObj=new ShippingEstimate();
								       dimensionObj=new Dimensions();
								    
							 }
							    if(!listOfProductXids.contains(xid)){
							    	listOfProductXids.add(xid);
							    }
								 productExcelObj = new Product();
								 existingApiProduct = postServiceImpl.getProduct(accessToken, xid, environmentType); 
								     if(existingApiProduct == null){
								    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
								    	 productExcelObj = new Product();
								    	 existingFlag=false;
								     }else{//need to confirm what existing data client wnts
								    	    productExcelObj=pioneerLLCAttributeParser.getExistingProductData(existingApiProduct, existingApiProduct.getProductConfigurations());
											productConfigObj=productExcelObj.getProductConfigurations();
											existingFlag=true;
										   // priceGrids = productExcelObj.getPriceGrids();
								     }
						 }
					}
					
					
					
					switch (columnIndex + 1) {
					case 1://XID
						productExcelObj.setExternalProductId(xid);
						break;
					case 2://××Prod # (up to 14 characters)
						String productNo = CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(productNo)){
						  productExcelObj.setAsiProdNo(productNo);
						}
						break;
					case 3://××Product Name (Up to 50 characters)
						 productName = cell.getStringCellValue();
						if(!StringUtils.isEmpty(productName)){
						int len=productName.length();
						 if(len>60){
							String strTemp=productName.substring(0, 60);
							int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
							productName=(String) strTemp.subSequence(0, lenTemp);
						}
						productExcelObj.setName(productName);
						}
						
						break;
					case 4://Keywords
						String keywords = CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(keywords)){
							List<String> productKeywords =new ArrayList<String>();
						 productKeywords = CommonUtility.getStringAsList(keywords,
                                ApplicationConstants.CONST_DELIMITER_COMMA);
						List<String> productKeywordsTemp =new ArrayList<String>();
						for (String string : productKeywords) {
							string = CommonUtility.removeSpecialSymbols(string.trim(), ApplicationConstants.CHARACTERS_NUMBERS_PATTERN);
							if(productKeywordsTemp.size()<30){
								if(string.length()<=30){
							productKeywordsTemp.add(string);
								}
							}
						  }
						//System.out.println(productKeywordsTemp.size());
						productExcelObj.setProductKeywords(productKeywordsTemp);				
						
						}
						break;
					case 5://Summary
						String summary = CommonUtility.getCellValueStrinOrInt(cell);
						 if(!StringUtils.isEmpty(summary)){
							 summary=summary.replace("tank", "");
							 summary=summary.replace("velcro", "");
							 summary=summary.replace("Iring", "");
							 summary=CommonUtility.removeRestrictSymbols(summary);
							 productExcelObj.setSummary(CommonUtility.getStringLimitedChars(summary, 130));
						 }
						break;
					case 6://××Description (Up to 450 characters.)
						String description =CommonUtility.getCellValueStrinOrInt(cell);
						description=CommonUtility.removeRestrictSymbols(description);
						//description = CommonUtility.removeSpecialSymbols(description,specialCharacters);
						 if(!StringUtils.isEmpty(description)){
							 description=description.replace("tank", "");
							 description=description.replace("velcro", "");
							 description=description.replace("Iring", "");
						int length=description.length();
						 if(length>800){
							String strTemp=description.substring(0, 800);
							int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
							description=(String) strTemp.subSequence(0, lenTemp);
						 }
						 productExcelObj.setDescription(description);
						 }
						break;
					case 7://Qty 1
						String	q1=null;
						q1=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(q1)){
							listOfQuantity.append(q1.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							
				         }
						break;
					case 8://Net price 1
						String	netPrice1=null;
						netPrice1=CommonUtility.getCellValueStrinOrDecimal(cell);
						netPrice1=netPrice1.replaceAll(" ","");
						netPrice1 = netPrice1.replaceAll("\\(.*\\)", "");
						if(!StringUtils.isEmpty(netPrice1) ){
							listOfNetPrices.append(netPrice1.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						}
						break;
					case 9://Retail Price 1
						String	listPrice1=null;
						listPrice1=CommonUtility.getCellValueStrinOrDecimal(cell);
						listPrice1=listPrice1.replaceAll(" ","");
						listPrice1 = listPrice1.replaceAll("\\(.*\\)", "");
						if(!StringUtils.isEmpty(listPrice1)){
							listOfPrices.append(listPrice1.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							 }
						break;
					case 10://Qty 2
						String	q2=null;
						q2=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(q2)){
							listOfQuantity.append(q2.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							
				         }
						break;
					case 11://Net price 2
						String	netPrice2=null;
						netPrice2=CommonUtility.getCellValueStrinOrDecimal(cell);
						netPrice2=netPrice2.replaceAll(" ","");
						netPrice2 = netPrice2.replaceAll("\\(.*\\)", "");
						if(!StringUtils.isEmpty(netPrice2) ){
							listOfNetPrices.append(netPrice2.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						}
						break;
					case 12://Retail Price 2
						String	listPrice2=null;
						listPrice2=CommonUtility.getCellValueStrinOrDecimal(cell);
						listPrice2=listPrice2.replaceAll(" ","");
						listPrice2 = listPrice2.replaceAll("\\(.*\\)", "");
						if(!StringUtils.isEmpty(listPrice2)){
							listOfPrices.append(listPrice2.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							 }
						break;
					case 13://Qty 3
						String	q3=null;
						q3=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(q3)){
							listOfQuantity.append(q3.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							
				         }
						break;
					case 14://Net price 3
						String	netPrice3=null;
						netPrice3=CommonUtility.getCellValueStrinOrDecimal(cell);
						netPrice3=netPrice3.replaceAll(" ","");
						netPrice3 = netPrice3.replaceAll("\\(.*\\)", "");
						if(!StringUtils.isEmpty(netPrice3) ){
							listOfNetPrices.append(netPrice3.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						}
						break;
					case 15://Retail Price 3
						String	listPrice3=null;
						listPrice3=CommonUtility.getCellValueStrinOrDecimal(cell);
						listPrice3=listPrice3.replaceAll(" ","");
						listPrice3 = listPrice3.replaceAll("\\(.*\\)", "");
						if(!StringUtils.isEmpty(listPrice3)){
							listOfPrices.append(listPrice3.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							 }
						break;
					case 16://Qty 4
						String	q4=null;
						q4=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(q4)){
							listOfQuantity.append(q4.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							
				         }
						break;
					case 17://Net price 4
						String	netPrice4=null;
						netPrice4=CommonUtility.getCellValueStrinOrDecimal(cell);
						netPrice4=netPrice4.replaceAll(" ","");
						netPrice4 = netPrice4.replaceAll("\\(.*\\)", "");
						if(!StringUtils.isEmpty(netPrice4) ){
							listOfNetPrices.append(netPrice4.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						}
						break;
					case 18://Retail Price 4
						String	listPrice4=null;
						listPrice4=CommonUtility.getCellValueStrinOrDecimal(cell);
						listPrice4=listPrice4.replaceAll(" ","");
						listPrice4 = listPrice4.replaceAll("\\(.*\\)", "");
						if(!StringUtils.isEmpty(listPrice4)){
							listOfPrices.append(listPrice4.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							 }
						break;
					case 19://Qty 5
						String	q5=null;
						q5=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(q5)){
							listOfQuantity.append(q5.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							
				         }
						break;
					case 20://Net price 5
						String	netPrice5=null;
						netPrice5=CommonUtility.getCellValueStrinOrDecimal(cell);
						netPrice5=netPrice5.replaceAll(" ","");
						netPrice5 = netPrice5.replaceAll("\\(.*\\)", "");
						if(!StringUtils.isEmpty(netPrice5) ){
							listOfNetPrices.append(netPrice5.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						}
						break;
					case 21://Retail Price 5
						String	listPrice5=null;
						listPrice5=CommonUtility.getCellValueStrinOrDecimal(cell);
						listPrice5=listPrice5.replaceAll(" ","");
						listPrice5 = listPrice5.replaceAll("\\(.*\\)", "");
						if(!StringUtils.isEmpty(listPrice5)){
							listOfPrices.append(listPrice5.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							 }
						break;
					case 22://Product Colors
						
						
						String colorValue=CommonUtility.getCellValueStrinOrInt(cell);
						 if(!StringUtils.isEmpty(colorValue)){
							 List<Color> colors =pioneerLLCAttributeParser.getProductColors(colorValue);
							 productConfigObj.setColors(colors);
						 }
						
						break;
					case 23://Materials
						String materials=cell.getStringCellValue();
						if(!StringUtils.isEmpty(materials)){
						List<Material> listOfMaterialList = pioneerLLCAttributeParser.getMaterialList(materials);
						productConfigObj.setMaterials(listOfMaterialList);
						 }
						break;
					case 24://Sizes
						
						String sizes=cell.getStringCellValue();
						if(!StringUtils.isEmpty(sizes)){
							productExcelObj = pioneerLLCAttributeParser.getSizes(sizes, productExcelObj);
							productConfigObj=productExcelObj.getProductConfigurations();
						
						 }
						break;
					case 25://Production Time (# of business days)
						
						String prodTimeLo = null;
						List<ProductionTime> listOfProductionTime = new ArrayList<ProductionTime>();
						ProductionTime productionTime = new ProductionTime();
						prodTimeLo=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(prodTimeLo)){
						prodTimeLo=prodTimeLo.toLowerCase().trim();
					    prodTimeLo=prodTimeLo.replaceAll(ApplicationConstants.CONST_STRING_DAYS,ApplicationConstants.CONST_STRING_EMPTY);
						String strArr[]=prodTimeLo.split("-");
					    for (String prodStr : strArr) {
					    	productionTime = new ProductionTime();
					    	  productionTime.setBusinessDays(prodStr.trim());
								productionTime.setDetails(ApplicationConstants.CONST_STRING_DAYS);
								listOfProductionTime.add(productionTime);
								
						}
						productConfigObj.setProductionTime(listOfProductionTime);
						}
						break;
					case 26://Product Origin (Country Product is made in)
						String origin = CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(origin)){
							if(origin.toUpperCase().equals("MX")){
								origin="Mexico";
							}else if(origin.toUpperCase().equals("CA")){
								origin="Canada";
							}else if(origin.toUpperCase().equals("UK")){
								origin="United Kingdom";
							}else if(origin.toUpperCase().equals("SE")){
								origin="Sweden";
							}else if(origin.toUpperCase().equals("CN")){
								origin="China";
							}else if(origin.toUpperCase().equals("GB")){
								origin="United Kingdom";
							}
						List<Origin> listOfOrigins = new ArrayList<Origin>();
						Origin origins = new Origin();
						origins.setName(origin);
						listOfOrigins.add(origins);
						productConfigObj.setOrigins(listOfOrigins);
						}
						break;
						
					case 27://Imprint Method
						 imprintMethodValue=CommonUtility.getCellValueStrinOrInt(cell);
						 if(!StringUtils.isEmpty(imprintMethodValue)){
							 if(!imprintMethodValue.equals("None"))
								{
							 List<ImprintMethod> listOfImprintMethodsNew = pioneerLLCAttributeParser.getImprintMethods(imprintMethodValue);
								productConfigObj.setImprintMethods(listOfImprintMethodsNew);
								}
						  }
						break;
					case 28://Imprint Size
						String imprintSize = CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(imprintSize)){
							if(!imprintSize.equals("None"))
							{
							ImprintSize imprintSizeObj = new ImprintSize();
							List<ImprintSize> listImprintSize=new ArrayList<ImprintSize>();
							imprintSizeObj.setValue(imprintSize);
							listImprintSize.add(imprintSizeObj);
							productConfigObj.setImprintSize(listImprintSize);
							}
						}
						break;
					case 29://Set Up Charges
						String imprintMethodValueUpchrg=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(imprintMethodValue)&& !StringUtils.isEmpty(imprintMethodValueUpchrg)){
							if(!imprintMethodValue.equals("None")){
							priceGrids = pioneerPriceGridParserr.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,imprintMethodValueUpchrg, "R",
									"Imprint Method"+":"+imprintMethodValue,ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, "",imprintMethodValue, 
									"Imprint Method Charge", ApplicationConstants.CONST_VALUE_TYPE_OTHER,"Required", ApplicationConstants.CONST_INT_VALUE_ONE, priceGrids);
							}
							}
						
						break;
					case 30://Carton
						
						shippingValue=cell.getStringCellValue();
						break;
					case 31://Gross Weight
						String shippingWeightValue = cell.getStringCellValue();
						ShippingEstimate shipobj = pioneerLLCAttributeParser.getShippingEstimates(shippingValue,shippingWeightValue );
						productConfigObj.setShippingEstimates(shipobj);
						
						break;	
					}
				} // end inner while loop
			}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);		 
				ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: "
				+e.getMessage()+" at column number(increament by 1)"+columnIndex);
				productDaoObj.save(apiResponse.getErrors(),
						productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);
				}
	}
	workbook.close();
	 	
	 // setting pricing over here
	 // need to create a map over here 
	 //color upcharges
	 //same goes here as well
	 //color & location
	 // same goes here as well
	 // Ineed to set pricegrid over here
     // Add repeatable sets here
	
	
	
	
	 if( !StringUtils.isEmpty(listOfPrices.toString())){
		 //priceGrids=new ArrayList<PriceGrid>();
		 /*priceGrids = pioneerPriceGridParser.getPriceGrids(listOfPrices.toString(),listOfNetPrices.toString(),listOfQuantity.toString(), 
					"R",ApplicationConstants.CONST_STRING_CURRENCY_USD,
					"",ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
					productName,null,1,priceGrids);
		*/ 
		 String disc="R";
		 StringBuilder sb = new StringBuilder();
		 String arrLen[]=listOfPrices.toString().split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		 int len=arrLen.length;
		 for (int i = 0; i <len; i++) {
			//disc=ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID+"R";
			 sb.append("R"+ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		}
		 //System.out.println(sb.toString().split(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).length);
		 priceGrids = pioneerPriceGridParserr.getPriceGrids(listOfPrices.toString(),listOfNetPrices.toString(),listOfQuantity.toString(), 
				 sb.toString(),ApplicationConstants.CONST_STRING_CURRENCY_USD,
					ApplicationConstants.CONST_STRING_EMPTY,ApplicationConstants.CONST_BOOLEAN_TRUE, 
					ApplicationConstants.CONST_STRING_FALSE, productName,null,priceGrids);
		 }
	
		 if(CollectionUtils.isEmpty(priceGrids)){
				priceGrids = pioneerPriceGridParserr.getPriceGridsQur();	
			}
	   // Add repeatable sets here
	/* productExcelObj=bagMakersPriceGridParser.getPricingData(listOfPrices.toString(), listOfQuantity.toString(), listOfDiscount.toString(), basePricePriceInlcude, 
				plateScreenCharge, plateScreenChargeCode,
			    plateReOrderCharge, plateReOrderChargeCode, priceGrids, 
			    productExcelObj, productConfigObj);*/
		 if(CollectionUtils.isEmpty(productConfigObj.getImprintMethods())){
			 List<ImprintMethod> listOfImprintMethod = new ArrayList<>();
			 ImprintMethod	imprintMethodObj = new ImprintMethod();	
			imprintMethodObj.setType("UNIMPRINTED");
			imprintMethodObj.setAlias("UNIMPRINTED");
			listOfImprintMethod.add(imprintMethodObj);
			productConfigObj.setImprintMethods(listOfImprintMethod);
		}
	 	productExcelObj.setPriceType("L");
	 	productExcelObj.setPriceGrids(priceGrids);
	 	productExcelObj.setProductConfigurations(productConfigObj);
	 	/* _LOGGER.info("Product Data : "
					+ mapperObj.writeValueAsString(productExcelObj));
	 	*/
	 	
	 	
	 	/* _LOGGER.info("Product Data : "
					+ mapperObj.writeValueAsString(productExcelObj));
	 	*/
	 	/*if(xidList.contains(productExcelObj.getExternalProductId().trim())){
	 		productExcelObj.setAvailability(new ArrayList<Availability>());
	 	}*/
	 	//productExcelObj.setMakeActiveDate("2018-01-01T00:00:00");
	 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId, environmentType);
	 	if(num ==1){
	 		numOfProductsSuccess.add("1");
	 	}else if(num == 0) {
	 		numOfProductsFailure.add("0");
	 	}else{
	 		
	 	}
	 	_LOGGER.info("list size>>>>>>"+numOfProductsSuccess.size());
	 	_LOGGER.info("Failure list size>>>>>>"+numOfProductsFailure.size());
       finalResult = numOfProductsSuccess.size() + "," + numOfProductsFailure.size();
       
       productDaoObj.saveErrorLog(asiNumber,batchId);

   	//reset all list and objects over here
       //numOfProductsSuccess = new ArrayList<String>();
	   //numOfProductsFailure = new ArrayList<String>();
	   //finalResult = "";
	   listOfProductXids = new HashSet<String>();
	   productExcelObj = new Product();  
	   existingApiProduct = null;
	   productConfigObj=new ProductConfigurations();
	   priceGrids = new ArrayList<PriceGrid>();
	   shippingValue="";
	   existingFlag=false;
	   listOfQuantity = new StringBuilder();
	   listOfPrices = new StringBuilder();
	   listOfNetPrices = new StringBuilder();
	   listOfDiscount = new StringBuilder();
	   basePricePriceInlcude="";
	   tempQuant1="";
	   productName ="";
	   imprintMethodValue="";
	   productXids = new HashSet<String>();
	   repeatRows = new ArrayList<>();
	   ShipingObj=new ShippingEstimate();
       dimensionObj=new Dimensions();
       return finalResult;
	}catch(Exception e){
		_LOGGER.error("Error while Processing excel sheet ,Error message: "+e.getMessage()+"for column"+columnIndex+1);
		return finalResult;
	}finally{
		try {
			workbook.close();
		} catch (IOException e) {
			_LOGGER.error("Error while Processing excel sheet, Error message: "+e.getMessage()+"for column" +columnIndex+1);

		}
			_LOGGER.info("Complted processing of excel sheet ");
			_LOGGER.info("Total no of product:"+numOfProductsSuccess.size() );
	}
	
}
	
	
	public String getProductXid(Row row){
		Cell xidCell =  row.getCell(0);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid) || productXid.trim().equalsIgnoreCase("#N/A")){
		     xidCell = row.getCell(2);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}
	
	
	
	public static String removeSpecialChar(String tempValue){
		tempValue=tempValue.trim();
		tempValue=tempValue.replaceAll("(Day|Service|Sheets|Packages|Roll|Rolls|Days|Hour|Hours|Week|Weeks|Rush|R|u|s|h)", "");
		tempValue=tempValue.replaceAll("\\(","");
		tempValue=tempValue.replaceAll("\\)","");
	return tempValue;
	}
	
	public static List<String> getImprintAliasList(List<ImprintMethod> listOfImprintMethod){
		ArrayList<String> tempList=new ArrayList<String>();
		
		for (ImprintMethod tempMthd : listOfImprintMethod) {
			String strTemp=tempMthd.getAlias();
			if(strTemp.contains("LASER")|| strTemp.contains("DIRECT")|| strTemp.contains("VINYL")||strTemp.contains("DYE")||strTemp.contains("HEAT")||strTemp.contains("EPOXY")){
				tempList.add(strTemp);
			}
		}
		return tempList;
		
	}
	
	public ProductDao getProductDaoObj() {
		return productDaoObj;
	}

	public void setProductDaoObj(ProductDao productDaoObj) {
		this.productDaoObj = productDaoObj;
	}

	public PostServiceImpl getPostServiceImpl() {
		return postServiceImpl;
	}

	public void setPostServiceImpl(PostServiceImpl postServiceImpl) {
		this.postServiceImpl = postServiceImpl;
	}


	public  static String getQuantValue(String value){
		String temp[]=value.split("-");
		return temp[0];
		
	}


	public PioneerLLCAttributeParser getPioneerLLCAttributeParser() {
		return pioneerLLCAttributeParser;
	}


	public void setPioneerLLCAttributeParser(
			PioneerLLCAttributeParser pioneerLLCAttributeParser) {
		this.pioneerLLCAttributeParser = pioneerLLCAttributeParser;
	}


	public PioneerPriceGridParserr getPioneerPriceGridParserr() {
		return pioneerPriceGridParserr;
	}


	public void setPioneerPriceGridParserr(
			PioneerPriceGridParserr pioneerPriceGridParserr) {
		this.pioneerPriceGridParserr = pioneerPriceGridParserr;
	}


	


	/*public PioneerPriceGridParser getPioneerPriceGridParser() {
		return pioneerPriceGridParser;
	}


	public void setPioneerPriceGridParser(
			PioneerPriceGridParser pioneerPriceGridParser) {
		this.pioneerPriceGridParser = pioneerPriceGridParser;
	}*/

}
