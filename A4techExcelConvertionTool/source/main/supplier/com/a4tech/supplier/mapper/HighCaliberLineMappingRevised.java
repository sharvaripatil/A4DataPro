package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.functions.ImReal;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import parser.highcaliberline.HighCaliberAttributeParser;
import parser.highcaliberline.HighCaliberPriceGridParser;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.AdditionalLocation;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HighCaliberLineMappingRevised implements IExcelParser{

	
	private static final Logger _LOGGER = Logger.getLogger(HighCaliberLineMappingRevised.class);
	PostServiceImpl postServiceImpl;
	ProductDao productDaoObj;
	HighCaliberAttributeParser highCaliberAttributeParser;
	@Autowired
	ObjectMapper mapperObj;
	HighCaliberPriceGridParser highCalPriceGridParser;
	
	public static List<String> xidList = new ArrayList<String>();
	static {
		xidList.add("746-200198431");
		xidList.add("746-200192596");
		xidList.add("746-200192578");
		xidList.add("746-200198441");
		xidList.add("746-200218409");
		xidList.add("746-4996045");
		xidList.add("746-4963527");
		xidList.add("746-550041893");
		xidList.add("746-200262278");
		xidList.add("746-200324832");
		xidList.add("746-200324883");
		xidList.add("746-200257276");
	}
	
	@SuppressWarnings("finally")
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber,int batchId){
		int columnIndex = 0;
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		
		  Set<String>  listOfProductXids = new HashSet<String>();
		  Product productExcelObj = new Product();  
		  Product existingApiProduct = null;
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  String productId = null;
		  String shippinglen="";
		  String shippingWid="";
		  String shippingH="";
		   String shippingWeightValue="";
		  String noOfitem="";
		  boolean existingFlag=false;
		  String prodTime="";
		  String finalProdTimeVal="";
		  String rushTime="";
		  String finalRushTimeVal="";
		  String prodTimeVal2="";
		  String finalProdTimeVal2="";
		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
	
		Set<String>  productXids = new HashSet<String>();
		StringBuilder listOfQuantityProd1 = new StringBuilder();
		StringBuilder listOfPricesProd1 = new StringBuilder();
		
		StringBuilder listOfQuantityRush = new StringBuilder();
		StringBuilder listOfPricesRush = new StringBuilder();
		StringBuilder listOfQuantityProd2 = new StringBuilder();
		StringBuilder listOfPricesProd2 = new StringBuilder();
		
		 List<String> repeatRows = new ArrayList<>();
		 List<ProductionTime> prodTimeList=new ArrayList<ProductionTime>();
		 ShippingEstimate ShipingObj=new ShippingEstimate();
		 String setUpchrgesVal="";
		 String repeatUpchrgesVal="";
		 String priceInlcudeFinal="";
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
					if(columnIndex + 1 == 1){
						xid = getProductXid(nextRow);//CommonUtility.getCellValueStrinOrInt(cell);//
						checkXid = true;
					}else{
						checkXid = false;
					}
					if(checkXid){
						 if(!productXids.contains(xid)){
							 if(nextRow.getRowNum() != 1){
								 
								 boolean prod1flag=false;
								 boolean prod2flag=false;
								 boolean rushflag=false;
								 boolean priceFlag=false;
								 String flag="";
								 if(!StringUtils.isEmpty(finalProdTimeVal)&& !StringUtils.isEmpty(listOfPricesProd1.toString())){
									 prod1flag=true;
									 priceFlag=true;
									}else{
										prod1flag=false;
										flag="false;";
									}
								 
								 if(!StringUtils.isEmpty(finalRushTimeVal)&& !StringUtils.isEmpty(listOfPricesRush.toString())){
									 rushflag=true;
									 priceFlag=true;
									}else{
										flag=flag+"false;";
										rushflag=false;
									}
								 
								 if(!StringUtils.isEmpty(finalProdTimeVal2)&& !StringUtils.isEmpty(listOfPricesProd2.toString())){
										////imp code
									 	prod2flag=true;
									 	priceFlag=true;
									}else{
										flag=flag+"false;";
										prod2flag=false;
									}
								 	
								 if(priceFlag){
								 String tempCount[]=flag.split(";");
								 int countt=tempCount.length;//Integer.parseInt(tempCount.toString());
								 if(countt>1){
									 ///i have to create a price grid based on all criterias
									 if(prod1flag){
										 priceGrids = highCalPriceGridParser.getPriceGrids(listOfPricesProd1.toString(),listOfQuantityProd1.toString(), 
													"R",ApplicationConstants.CONST_STRING_CURRENCY_USD,
													"",ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
													finalProdTimeVal +" business days",null,1,"","",priceGrids);
									 }else if(rushflag){
										 priceGrids = highCalPriceGridParser.getPriceGrids(listOfPricesRush.toString(),listOfQuantityRush.toString(), 
													"R",ApplicationConstants.CONST_STRING_CURRENCY_USD,
													"",ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
													finalRushTimeVal +" business days",null,2,"","",priceGrids);
									 }else if(prod2flag){
										 priceGrids = highCalPriceGridParser.getPriceGrids(listOfPricesProd2.toString(),listOfQuantityProd2.toString(), 
													"R",ApplicationConstants.CONST_STRING_CURRENCY_USD,
													"",ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
													finalProdTimeVal2 +" business days",null,3,"","",priceGrids);
									 }
									 productExcelObj.setPriceGrids(priceGrids);
								 }else{
									 if(!StringUtils.isEmpty(finalProdTimeVal)&& !StringUtils.isEmpty(listOfPricesProd1.toString())){
										 priceGrids = highCalPriceGridParser.getPriceGrids(listOfPricesProd1.toString(),listOfQuantityProd1.toString(), 
													"R",ApplicationConstants.CONST_STRING_CURRENCY_USD,
													"",ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
													finalProdTimeVal +" business days","Production Time",1,"","",priceGrids);
										}
									 if(!StringUtils.isEmpty(finalRushTimeVal)&& !StringUtils.isEmpty(listOfPricesRush.toString())){
										 priceGrids = highCalPriceGridParser.getPriceGrids(listOfPricesRush.toString(),listOfQuantityRush.toString(), 
													"R",ApplicationConstants.CONST_STRING_CURRENCY_USD,
													"",ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
													finalRushTimeVal +" business days","Rush Service",2,"","",priceGrids);
										}
									 if(!StringUtils.isEmpty(finalProdTimeVal2)&& !StringUtils.isEmpty(listOfPricesProd2.toString())){
											priceGrids = highCalPriceGridParser.getPriceGrids(listOfPricesProd2.toString(),listOfQuantityProd2.toString(), 
													"R",ApplicationConstants.CONST_STRING_CURRENCY_USD,
													"",ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
													finalProdTimeVal2 +" business days","Production Time",3,"","",priceGrids);
										}
									 productExcelObj.setPriceGrids(priceGrids);
									 }
								 }
								 
								 // here I need to work on upcharges pricegrids
								 //Set-Up charges upcharges
								 if(!StringUtils.isEmpty(setUpchrgesVal)){
									 priceGrids=productExcelObj.getPriceGrids();//ithe gadbad hotey
									 /////////
									 if(CollectionUtils.isEmpty(priceGrids)){
										 priceGrids=new ArrayList<PriceGrid>();
									 }
									 ///////
									 int i=priceGrids.size();
									 String tempSetupCharge=setUpchrgesVal;
									 tempSetupCharge=setUpchrgesVal.substring(setUpchrgesVal.indexOf("$")+1, setUpchrgesVal.indexOf("("));
									 if(setUpchrgesVal.toUpperCase().contains("COLOR") || setUpchrgesVal.toUpperCase().contains("LOCATION")){
										 //setUpchrgesVal=setUpchrgesVal.substring(setUpchrgesVal.indexOf("$")+1, setUpchrgesVal.indexOf("("));
										 // here i will send the colorlist of product config to get alias name 
										 List<Color> colorList=productConfigObj.getColors();
										 List<String> tempClrListAlias=new ArrayList<String>();
										 for (Color objColor : colorList) {
											 tempClrListAlias.add(objColor.getAlias());
										}
										 if(!CollectionUtils.isEmpty(colorList)){
											 /*(String listOfPrices, String listOfQuan, String discountCodes,
														String currency, String priceInclude, boolean isBasePrice,
														String qurFlag, String priceName, String criterias,Integer sequence,String upChargeType,String upchargeUsageType,
														List<PriceGrid> existingPriceGrid) */
												for (String finalAlias : tempClrListAlias) {
													priceGrids=highCalPriceGridParser.getPriceGrids(tempSetupCharge,"1","V",
															"USD","",false,"False", finalAlias,"Product Color", i+1,"Set-up Charge", "Other",priceGrids);
													i++;
												}
											}
									 }else{//this is for imprint methods
										 //here i have to check the imprint method name for the value
										 List<ImprintMethod> listOfImprintMethod=productConfigObj.getImprintMethods();
										 
										 if(!CollectionUtils.isEmpty(listOfImprintMethod)){
											 /*(String listOfPrices, String listOfQuan, String discountCodes,
														String currency, String priceInclude, boolean isBasePrice,
														String qurFlag, String priceName, String criterias,Integer sequence,String upChargeType,String upchargeUsageType,
														List<PriceGrid> existingPriceGrid) */
											 List<String> tempImpListAlias=new ArrayList<String>();
											 tempImpListAlias=getImprintAliasList(listOfImprintMethod);
											 if(!CollectionUtils.isEmpty(tempImpListAlias)){ 
												for (String finalAlias : tempImpListAlias) {
													priceGrids=highCalPriceGridParser.getPriceGrids(tempSetupCharge,"1","V",
															"USD","",false,"False", finalAlias,"Imprint Method", i+1,"Set-up Charge", "Other",priceGrids);
													i++;
												}
											 }
											}
									 }
								 }
								 
								//Repeat-Up charges upcharges
								 if(!StringUtils.isEmpty(repeatUpchrgesVal)){
									 priceGrids=productExcelObj.getPriceGrids();
									 /////////
									 if(CollectionUtils.isEmpty(priceGrids)){
										 priceGrids=new ArrayList<PriceGrid>();
									 }
									 ///////
									 int i=priceGrids.size();
									 String tempReSetupCharge=repeatUpchrgesVal;
									 tempReSetupCharge=repeatUpchrgesVal.substring(repeatUpchrgesVal.indexOf("$")+1, repeatUpchrgesVal.indexOf("("));
									 if(repeatUpchrgesVal.toUpperCase().contains("COLOR") || repeatUpchrgesVal.toUpperCase().contains("LOCATION")){
										 //setUpchrgesVal=setUpchrgesVal.substring(setUpchrgesVal.indexOf("$")+1, setUpchrgesVal.indexOf("("));
										 // here i will send the colorlist of product config to get alias name 
										 List<Color> colorList=productConfigObj.getColors();
										 List<String> tempClrListAlias=new ArrayList<String>();
										 for (Color objColor : colorList) {
											 tempClrListAlias.add(objColor.getAlias());
										}
										 if(!CollectionUtils.isEmpty(colorList)){
											 /*(String listOfPrices, String listOfQuan, String discountCodes,
														String currency, String priceInclude, boolean isBasePrice,
														String qurFlag, String priceName, String criterias,Integer sequence,String upChargeType,String upchargeUsageType,
														List<PriceGrid> existingPriceGrid) */
												for (String finalAlias : tempClrListAlias) {
													priceGrids=highCalPriceGridParser.getPriceGrids(tempReSetupCharge,"1","V",
															"USD","",false,"False", finalAlias,"Product Color", i+1,"Re-order Charge", "Other",priceGrids);
													i++;
												}
											}
									 }else{//this is for imprint methods
										 //here i have to check the imprint method name for the value
										 List<ImprintMethod> listOfImprintMethod=productConfigObj.getImprintMethods();
										 
										 if(!CollectionUtils.isEmpty(listOfImprintMethod)){
											 /*(String listOfPrices, String listOfQuan, String discountCodes,
														String currency, String priceInclude, boolean isBasePrice,
														String qurFlag, String priceName, String criterias,Integer sequence,String upChargeType,String upchargeUsageType,
														List<PriceGrid> existingPriceGrid) */
											 List<String> tempImpListAlias=new ArrayList<String>();
											 tempImpListAlias=getImprintAliasList(listOfImprintMethod);
											 if(!CollectionUtils.isEmpty(tempImpListAlias)){ 
												for (String finalAlias : tempImpListAlias) {
													priceGrids=highCalPriceGridParser.getPriceGrids(tempReSetupCharge,"1","V",
															"USD","",false,"False", finalAlias,"Imprint Method", i+1,"Re-order Charge", "Other",priceGrids);
													i++;
												}
											 }
											}
									 }
								 }
								 // need to create a map over here 
								 //color upcharges
								 //same goes here as well
								 //color & location
								 // same goes here as well
								 if(CollectionUtils.isEmpty(priceGrids)){
										priceGrids = highCalPriceGridParser.getPriceGridsQur();	
									}
								 
								   // Add repeatable sets here
								 	productExcelObj.setPriceType("L");
								 	productExcelObj.setPriceGrids(priceGrids);
								 	productExcelObj.setProductConfigurations(productConfigObj);
								 	 _LOGGER.info("Product Data : "
												+ mapperObj.writeValueAsString(productExcelObj));
								 	
								 	/*if(xidList.contains(productExcelObj.getExternalProductId().trim())){
								 		productExcelObj.setAvailability(new ArrayList<Availability>());
								 	}*/
								 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId);
								 	if(num ==1){
								 		numOfProductsSuccess.add("1");
								 	}else if(num == 0) {
								 		numOfProductsFailure.add("0");
								 	}else{
								 		
								 	}
								 	_LOGGER.info("list size of success>>>>>>>"+numOfProductsSuccess.size());
								 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
									//reset all list and objects over here
									priceGrids = new ArrayList<PriceGrid>();
									productConfigObj = new ProductConfigurations();
									listOfQuantityProd1 = new StringBuilder();
									 listOfPricesProd1 = new StringBuilder();
									 listOfQuantityRush = new StringBuilder();
									 listOfPricesRush = new StringBuilder();
									 listOfQuantityProd2 = new StringBuilder();
									 listOfPricesProd2 = new StringBuilder();
									 prodTime="";
									 finalProdTimeVal="";
									 rushTime="";
									 finalRushTimeVal="";
									 prodTimeVal2="";
									 finalProdTimeVal2="";
									 prodTimeList=new ArrayList<ProductionTime>();
									 ShipingObj=new ShippingEstimate();
									 setUpchrgesVal="";
							 }
							    if(!listOfProductXids.contains(xid)){
							    	listOfProductXids.add(xid);
							    }
								 productExcelObj = new Product();
								 existingApiProduct = postServiceImpl.getProduct(accessToken, xid); 
								     if(existingApiProduct == null){
								    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
								    	 productExcelObj = new Product();
								    	 existingFlag=false;
								     }else{//need to confirm what existing data client wnts
								    	    productExcelObj=highCaliberAttributeParser.getExistingProductData(existingApiProduct, existingApiProduct.getProductConfigurations());
											productConfigObj=productExcelObj.getProductConfigurations();
											existingFlag=true;
										   // priceGrids = productExcelObj.getPriceGrids();
								     }
						 }
					}
					
					
					
					switch (columnIndex + 1) {
					case 1://XID
						/*productId=CommonUtility.getCellValueStrinOrInt(cell);
						/////imp code 
						existingApiProduct=postServiceImpl.getProduct(accessToken, productId);
						if(existingApiProduct!=null){
							_LOGGER.info("Product "+productId+" is an existing product Using existing product also");
							productExcelObj=existingApiProduct;
							productConfigObj=productExcelObj.getProductConfigurations();
						    priceGrids = productExcelObj.getPriceGrids();
						}else{
							_LOGGER.info("Product "+productId+"Not an existing product ,Creating new product");
							productExcelObj=new Product();
						}*/
						////*/
						productExcelObj.setExternalProductId(xid);
						break;
					
					case 2://HCL Item#
						String asiProductNo=CommonUtility.getCellValueStrinOrInt(cell);
						productExcelObj.setAsiProdNo(asiProductNo);
						
						break;
					case 3://Item Status
						//Ignore this column
						break;
					case 4://Item Name
						
						String productName = CommonUtility.getCellValueStrinOrInt(cell);
						//productName = CommonUtility.removeSpecialSymbols(productName,specialCharacters);
						int len=productName.length();
						 if(len>60){
							String strTemp=productName.substring(0, 60);
							int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
							productName=(String) strTemp.subSequence(0, lenTemp);
						}
						productExcelObj.setName(productName);
						 
		
						 
						break;
					case 5://Description
						String description =CommonUtility.getCellValueStrinOrInt(cell);
						//description = CommonUtility.removeSpecialSymbols(description,specialCharacters);
						int length=description.length();
						 if(length>800){
							String strTemp=description.substring(0, 800);
							int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
							description=(String) strTemp.subSequence(0, lenTemp);
						}
						productExcelObj.setDescription(description);
						
						break;
					case 6://Web Link
						String productDataSheet=CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(productDataSheet)){
					productExcelObj.setProductDataSheet(productDataSheet);
					}
						break;
					
					case 7://ART Template Url

						break;
					case 8://Download Image Url
						/*if(!existingFlag){//image only for new product
						String image=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(image)){
							List<Image> listOfImages = highCaliberAttributeParser.getImages(image);
							productExcelObj.setImages(listOfImages);
							}
						 }*/
						break;
					case 9://Item Color
						String colorValue=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(colorValue)){
							 List<Color> colorList=highCaliberAttributeParser.getProductColors(colorValue);
							productConfigObj.setColors(colorList);
							}
						break;
					case 10://Price Includes
						priceInlcudeFinal=CommonUtility.getCellValueStrinOrInt(cell);
						if(StringUtils.isEmpty(priceInlcudeFinal)){
							priceInlcudeFinal="";
						}
						break;
					case 11://Setup Charge
							setUpchrgesVal=CommonUtility.getCellValueStrinOrInt(cell);
						
						break;
					case 12://Repeat Setup Charge
						repeatUpchrgesVal=CommonUtility.getCellValueStrinOrInt(cell);
						break;
					case 13://Run Charge-ignore
						break;
					
					case 14://Item Size -ignore
						break;
					
					case 15://Imprint Method
					String	imprintMethodVal=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(imprintMethodVal)){
						imprintMethodVal=imprintMethodVal.toUpperCase();
						String tempImpArr[]=imprintMethodVal.split(",");
						List<ImprintMethod> listOfImprintMethod= highCaliberAttributeParser.getImprintMethods(Arrays.asList(tempImpArr));
						 productConfigObj.setImprintMethods(listOfImprintMethod);
						
						}
					
						break;
						
					case 16://Imprint Area
						String	imprintLocation=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(imprintLocation)){
							//String tempImpLocArr[]=imprintLocation.split(",");
							//List<ImprintLocation> listOfImprintLoc= highCaliberAttributeParser.getImprintLocationVal(Arrays.asList(tempImpLocArr));
							productConfigObj= highCaliberAttributeParser.getImprintLocationVal(imprintLocation,productConfigObj);
							
							//productConfigObj.setImprintLocation(listOfImprintLoc);
					     	}
						
						break;
						
					case 17://Optional Imprint Location-- waiting for client verification
						String optinalLoca=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(optinalLoca)){
							String tempArr[]=optinalLoca.split(",");
							List<AdditionalLocation> additionalLocations=highCaliberAttributeParser.getOptionalLocationVal(Arrays.asList(tempArr));
							productConfigObj.setAdditionalLocations(additionalLocations);
						
						}
						
						break;
						
					case 18://Additional Process--ignore
						break;
						
					case 19://Packaging Method
						String packValues=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(packValues)){
							List<Packaging> listOfPackage=highCaliberAttributeParser.getPackageValues(packValues);
							productConfigObj.setPackaging(listOfPackage);
						}
						break;
						
					case 20://Carton Dimension (LxWxH)
						shippinglen=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(shippinglen)){
						shippinglen=shippinglen.toUpperCase();
						shippinglen=shippinglen.replace("L","");
						shippinglen=shippinglen.replace("W","");
						shippinglen=shippinglen.replace("H","");
						String tempArr[]=shippinglen.split("X");
						shippinglen=tempArr[0];
						shippingWid=tempArr[1];
						shippingH=tempArr[2];
						
						ShipingObj =highCaliberAttributeParser.getShippingEstimates(shippinglen, shippingWid, shippingH, "", "",ShipingObj);
						productConfigObj.setShippingEstimates(ShipingObj);
						}
						break;
						
					case 21://Carton Weight
						 shippingWeightValue=CommonUtility.getCellValueStrinOrInt(cell);
						 if(!StringUtils.isEmpty(shippingWeightValue)){
						 shippingWeightValue=shippingWeightValue.toUpperCase();
						 ShipingObj =highCaliberAttributeParser.getShippingEstimates("", "", "", shippingWeightValue, "",ShipingObj);
						 productConfigObj.setShippingEstimates(ShipingObj);
						 }
						break;
					case 22://Carton Qty
						if(!StringUtils.isEmpty(prodTime)){
						 noOfitem=noOfitem.toUpperCase();
						 noOfitem=CommonUtility.getCellValueStrinOrInt(cell);
						 ShipingObj =highCaliberAttributeParser.getShippingEstimates("", "", "", "", noOfitem,ShipingObj);
						 productConfigObj.setShippingEstimates(ShipingObj);
						}
						break;
					case 23://Production TimeStandard Production Time
						break;
					case 24://Standard Production Time
						prodTime =CommonUtility.getCellValueStrinOrInt(cell);
						//boolean hourFlag=false;
						//boolean weekFlag=false;
						if(!StringUtils.isEmpty(prodTime)){
						String tempVal=prodTime;
							if(prodTime.contains("Week") || prodTime.contains("Weeks")){
								prodTime=removeSpecialChar(prodTime);
								prodTime=prodTime.trim();
								if(prodTime.contains("-")){
									String arrTemp[]=prodTime.split("-");
									prodTime=arrTemp[1];
									int inWeekVal=5 * Integer.parseInt(prodTime);
									prodTime=Integer.toString(inWeekVal);
									
								}else{
									prodTime=removeSpecialChar(prodTime);
									prodTime=prodTime.trim();
									int inWeekVal=5 * Integer.parseInt(prodTime);
									prodTime=Integer.toString(inWeekVal);
								}
							}
							if(prodTime.contains("Hour")|| prodTime.contains("Hours")){
								prodTime="1";
							}
							if(prodTime.contains("Day") || prodTime.contains("Days")){
								prodTime=removeSpecialChar(prodTime);
								prodTime=prodTime.trim();
							}
							finalProdTimeVal=prodTime;
						    prodTimeList=highCaliberAttributeParser.getProdTimeCriteria(prodTime.trim(), tempVal,prodTimeList);
							productConfigObj.setProductionTime(prodTimeList);
						}
						break;
					    
					case 25://Item MOQ  // q1
						String	q1=null;
						q1=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(q1)){
							listOfQuantityProd1.append(q1.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 26://Qty 2
						String	q2=null;
						q2=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(q2)){
							listOfQuantityProd1.append(q2.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 27://Qty 3
						String	q3=null;
						q3=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(q3)){
							listOfQuantityProd1.append(q3.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 28://Qty 4
						String	q4=null;
						q4=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(q4)){
							listOfQuantityProd1.append(q4.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 29://Qty 5
						String	q5=null;
						q5=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(q5)){
							listOfQuantityProd1.append(q5.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 30://Price 1
						String	listPrice1=null;
						listPrice1=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice1)){
							listOfPricesProd1.append(listPrice1.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 
				         }
						break;
					case 31://Price 2
						String	listPrice2=null;
						listPrice2=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice2)){
							listOfPricesProd1.append(listPrice2.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 
				         }
						break;
					case 32://Price 3
						String	listPrice3=null;
						listPrice3=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice3)){
							listOfPricesProd1.append(listPrice3.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 
				         }
						break;
					case 33://Price 4
						String	listPrice4=null;
						listPrice4=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice4)){
							listOfPricesProd1.append(listPrice4.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 
				         }
						break;
					case 34://Price 5
						String	listPrice5=null;
						listPrice5=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice5)){
							listOfPricesProd1.append(listPrice5.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 
				         }
						break;
					case 35://Rush Cost "R"
						////////////////
						
						rushTime =CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(rushTime)){
						String tempRushVal=rushTime;
							if(rushTime.contains("Week") || rushTime.contains("Weeks")){
								rushTime=removeSpecialChar(rushTime);
								rushTime=rushTime.trim();
								if(rushTime.contains("-")){
									String arrTemp[]=rushTime.split("-");
									rushTime=arrTemp[1];
									int inWeekVal=5 * Integer.parseInt(rushTime);
									rushTime=Integer.toString(inWeekVal);
									
								}else{
									rushTime=removeSpecialChar(rushTime);
									rushTime=rushTime.trim();
									int inWeekVal=5 * Integer.parseInt(rushTime);
									rushTime=Integer.toString(inWeekVal);
								}
							}
							if(rushTime.contains("Hour")|| rushTime.contains("Hours")){
								rushTime="1";
							}
							if(rushTime.contains("Day") || rushTime.contains("Days")){
								rushTime=removeSpecialChar(rushTime);
								rushTime=rushTime.trim();
							}
							finalRushTimeVal=rushTime;
							RushTime rushObj=highCaliberAttributeParser.getRushTimeValues(rushTime.trim(), tempRushVal);
							productConfigObj.setRushTime(rushObj);
						}
						
						///////////////////
						break;
					case 36://MOQ
						String	qr1=null;
						qr1=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(qr1)){
							listOfQuantityRush.append(qr1.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 37://Qty 2
						String	qr2=null;
						qr2=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(qr2)){
							listOfQuantityRush.append(qr2.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 38://Qty 3
						String	qr3=null;
						qr3=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(qr3)){
							listOfQuantityRush.append(qr3.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 39://Qty 4
						String	qr4=null;
						qr4=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(qr4)){
							listOfQuantityRush.append(qr4.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 40://Qty 5
						String	qr5=null;
						qr5=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(qr5)){
							listOfQuantityRush.append(qr5.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 41://Price 1
						String	rlistPrice1=null;
						rlistPrice1=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(rlistPrice1)){
							listOfPricesRush.append(rlistPrice1.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 
				         }
						break;
					case 42://Price 2
						String	rlistPrice2=null;
						rlistPrice2=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(rlistPrice2)){
							listOfPricesRush.append(rlistPrice2.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 
				         }
						break;
					case 43://Price 3
						String	rlistPrice3=null;
						rlistPrice3=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(rlistPrice3)){
							listOfPricesRush.append(rlistPrice3.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 
				         }
						break;
					case 44://Price 4
						String	rlistPrice4=null;
						rlistPrice4=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(rlistPrice4)){
							listOfPricesRush.append(rlistPrice4.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 
				         }
						break;
					case 45://Price 5
						String	rlistPrice5=null;
						rlistPrice5=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(rlistPrice5)){
							listOfPricesRush.append(rlistPrice5.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 
				         }
						break;
					case 46://Ocean Service
						
						prodTimeVal2 =CommonUtility.getCellValueStrinOrInt(cell);
						//boolean hourFlag=false;
						//boolean weekFlag=false;
						if(!StringUtils.isEmpty(prodTimeVal2)){
						String tempValue=prodTimeVal2;
							if(prodTimeVal2.contains("Week")||prodTimeVal2.contains("Weeks")){
								prodTimeVal2=removeSpecialChar(prodTimeVal2);
								prodTimeVal2=prodTimeVal2.trim();
								if(prodTimeVal2.contains("-")){
									String arrTemp[]=prodTimeVal2.split("-");
									prodTimeVal2=arrTemp[1];
									int inWeekVal=5 * Integer.parseInt(prodTimeVal2);
									prodTimeVal2=Integer.toString(inWeekVal);
									
								}else{
									prodTimeVal2=removeSpecialChar(prodTimeVal2);
									prodTimeVal2=prodTimeVal2.trim();
									int inWeekVal=5 * Integer.parseInt(prodTimeVal2);
									prodTimeVal2=Integer.toString(inWeekVal);
								}
							}
							if(prodTimeVal2.contains("Hour") || prodTimeVal2.contains("Hours")){
								prodTimeVal2="1";
							}
							if(prodTimeVal2.contains("Day") || prodTimeVal2.contains("Days")){
								prodTimeVal2=removeSpecialChar(prodTimeVal2);
								prodTimeVal2=prodTimeVal2.trim();
							}
							finalProdTimeVal2=prodTimeVal2;
							prodTimeList=highCaliberAttributeParser.getProdTimeCriteria(prodTimeVal2.trim(), tempValue,prodTimeList);
							productConfigObj.setProductionTime(prodTimeList);
						}
						break;
					case 47://MOQ
						String	pq1=null;
						pq1=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(pq1)){
							listOfQuantityProd2.append(pq1.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 48://Qty 2
						String	pq2=null;
						pq2=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(pq2)){
							listOfQuantityProd2.append(pq2.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 49://Qty 3
						String	pq3=null;
						pq3=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(pq3)){
							listOfQuantityProd2.append(pq3.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 50://Qty 4
						String	pq4=null;
						pq4=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(pq4)){
							listOfQuantityProd2.append(pq4.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 51://Qty 5
						String	pq5=null;
						pq5=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(pq5)){
							listOfQuantityProd2.append(pq5.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;

					case 52://Price 1
						String	plistPrice1=null;
						plistPrice1=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(plistPrice1)){
							listOfPricesProd2.append(plistPrice1.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 
				         }
						break;
					case 53://Price 2
						String	plistPrice2=null;
						plistPrice2=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(plistPrice2)){
							listOfPricesProd2.append(plistPrice2.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 
				         }
						break;
					case 54://Price 3
						String	plistPrice3=null;
						plistPrice3=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(plistPrice3)){
							listOfPricesProd2.append(plistPrice3.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 
				         }
						break;
					case 55://Price 4
						String	plistPrice4=null;
						plistPrice4=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(plistPrice4)){
							listOfPricesProd2.append(plistPrice4.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 
				         }
						break;
					case 56://Price 5
						String	plistPrice5=null;
						plistPrice5=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(plistPrice5)){
							listOfPricesProd2.append(plistPrice5.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 
				         }
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
	
	
	boolean prod1flag=false;
	 boolean prod2flag=false;
	 boolean rushflag=false;
	 boolean priceFlag=false;
	 String flag="";
	 
	 if(!StringUtils.isEmpty(finalProdTimeVal)&& !StringUtils.isEmpty(listOfPricesProd1.toString())){
		 prod1flag=true;
		 priceFlag=true;
		}else{
			prod1flag=false;
			flag="false;";
		}
	 
	 if(!StringUtils.isEmpty(finalRushTimeVal)&& !StringUtils.isEmpty(listOfPricesRush.toString())){
		 rushflag=true;
		 priceFlag=true;
		}else{
			flag=flag+"false;";
			rushflag=false;
		}
	 
	 if(!StringUtils.isEmpty(finalProdTimeVal2)&& !StringUtils.isEmpty(listOfPricesProd2.toString())){
			////imp code
		 	prod2flag=true;
		 	priceFlag=true;
		}else{
			flag=flag+"false;";
			prod2flag=false;
		}
	 	
	 if(priceFlag){
	 String tempCount[]=flag.split(";");
	 int countt=tempCount.length;//Integer.parseInt(tempCount.toString());
	 if(countt>1){
		 ///i have to create a price grid based on all criterias
		 if(prod1flag){
			 priceGrids = highCalPriceGridParser.getPriceGrids(listOfPricesProd1.toString(),listOfQuantityProd1.toString(), 
						"R",ApplicationConstants.CONST_STRING_CURRENCY_USD,
						priceInlcudeFinal,ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
						finalProdTimeVal +" business days",null,1,"","",priceGrids);
		 }else if(rushflag){
			 priceGrids = highCalPriceGridParser.getPriceGrids(listOfPricesRush.toString(),listOfQuantityRush.toString(), 
						"R",ApplicationConstants.CONST_STRING_CURRENCY_USD,
						priceInlcudeFinal,ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
						finalRushTimeVal +" business days",null,2,"","",priceGrids);
		 }else if(prod2flag){
			 priceGrids = highCalPriceGridParser.getPriceGrids(listOfPricesProd2.toString(),listOfQuantityProd2.toString(), 
						"R",ApplicationConstants.CONST_STRING_CURRENCY_USD,
						priceInlcudeFinal,ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
						finalProdTimeVal2 +" business days",null,3,"","",priceGrids);
		 }
		 productExcelObj.setPriceGrids(priceGrids);
	 }else{
		 if(!StringUtils.isEmpty(finalProdTimeVal)&& !StringUtils.isEmpty(listOfPricesProd1.toString())){
			 priceGrids = highCalPriceGridParser.getPriceGrids(listOfPricesProd1.toString(),listOfQuantityProd1.toString(), 
						"R",ApplicationConstants.CONST_STRING_CURRENCY_USD,
						priceInlcudeFinal,ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
						finalProdTimeVal +" business days","Production Time",1,"","",priceGrids);
			}
		 if(!StringUtils.isEmpty(finalRushTimeVal)&& !StringUtils.isEmpty(listOfPricesRush.toString())){
			 priceGrids = highCalPriceGridParser.getPriceGrids(listOfPricesRush.toString(),listOfQuantityRush.toString(), 
						"R",ApplicationConstants.CONST_STRING_CURRENCY_USD,
						priceInlcudeFinal,ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
						finalRushTimeVal +" business days","Rush Service",2,"","",priceGrids);
			}
		 if(!StringUtils.isEmpty(finalProdTimeVal2)&& !StringUtils.isEmpty(listOfPricesProd2.toString())){
				priceGrids = highCalPriceGridParser.getPriceGrids(listOfPricesProd2.toString(),listOfQuantityProd2.toString(), 
						"R",ApplicationConstants.CONST_STRING_CURRENCY_USD,
						priceInlcudeFinal,ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
						finalProdTimeVal2 +" business days","Production Time",3,"","",priceGrids);
			}
		 productExcelObj.setPriceGrids(priceGrids);
		 }
	 }
	 
	 // here I need to work on upcharges pricegrids
	 //Set-Up charges upcharges
	 if(!StringUtils.isEmpty(setUpchrgesVal)){
		 priceGrids=productExcelObj.getPriceGrids();//ithe gadbad hotey
		 /////////
		 if(CollectionUtils.isEmpty(priceGrids)){
			 priceGrids=new ArrayList<PriceGrid>();
		 }
		 ///////
		 int i=priceGrids.size();
		 String tempSetupCharge=setUpchrgesVal;
		 tempSetupCharge=setUpchrgesVal.substring(setUpchrgesVal.indexOf("$")+1, setUpchrgesVal.indexOf("("));
		 if(setUpchrgesVal.toUpperCase().contains("COLOR") || setUpchrgesVal.toUpperCase().contains("LOCATION")){
			 //setUpchrgesVal=setUpchrgesVal.substring(setUpchrgesVal.indexOf("$")+1, setUpchrgesVal.indexOf("("));
			 // here i will send the colorlist of product config to get alias name 
			 List<Color> colorList=productConfigObj.getColors();
			 List<String> tempClrListAlias=new ArrayList<String>();
			 for (Color objColor : colorList) {
				 tempClrListAlias.add(objColor.getAlias());
			}
			 if(!CollectionUtils.isEmpty(colorList)){
				 /*(String listOfPrices, String listOfQuan, String discountCodes,
							String currency, String priceInclude, boolean isBasePrice,
							String qurFlag, String priceName, String criterias,Integer sequence,String upChargeType,String upchargeUsageType,
							List<PriceGrid> existingPriceGrid) */
					for (String finalAlias : tempClrListAlias) {
						priceGrids=highCalPriceGridParser.getPriceGrids(tempSetupCharge,"1","V",
								"USD","",false,"False", finalAlias,"Product Color", i+1,"Set-up Charge", "Other",priceGrids);
						i++;
					}
				}
		 }else{//this is for imprint methods
			 //here i have to check the imprint method name for the value
			 List<ImprintMethod> listOfImprintMethod=productConfigObj.getImprintMethods();
			 
			 if(!CollectionUtils.isEmpty(listOfImprintMethod)){
				 /*(String listOfPrices, String listOfQuan, String discountCodes,
							String currency, String priceInclude, boolean isBasePrice,
							String qurFlag, String priceName, String criterias,Integer sequence,String upChargeType,String upchargeUsageType,
							List<PriceGrid> existingPriceGrid) */
				 List<String> tempImpListAlias=new ArrayList<String>();
				 tempImpListAlias=getImprintAliasList(listOfImprintMethod);
				 if(!CollectionUtils.isEmpty(tempImpListAlias)){ 
					for (String finalAlias : tempImpListAlias) {
						priceGrids=highCalPriceGridParser.getPriceGrids(tempSetupCharge,"1","V",
								"USD","",false,"False", finalAlias,"Imprint Method", i+1,"Set-up Charge", "Other",priceGrids);
						i++;
					}
				 }
				}
		 }
	 }
	 
	//Repeat-Up charges upcharges
	 if(!StringUtils.isEmpty(repeatUpchrgesVal)){
		 priceGrids=productExcelObj.getPriceGrids();
		 /////////
		 if(CollectionUtils.isEmpty(priceGrids)){
			 priceGrids=new ArrayList<PriceGrid>();
		 }
		 ///////
		 int i=priceGrids.size();
		 String tempReSetupCharge=repeatUpchrgesVal;
		 tempReSetupCharge=repeatUpchrgesVal.substring(repeatUpchrgesVal.indexOf("$")+1, repeatUpchrgesVal.indexOf("("));
		 if(repeatUpchrgesVal.toUpperCase().contains("COLOR") || repeatUpchrgesVal.toUpperCase().contains("LOCATION")){
			 //setUpchrgesVal=setUpchrgesVal.substring(setUpchrgesVal.indexOf("$")+1, setUpchrgesVal.indexOf("("));
			 // here i will send the colorlist of product config to get alias name 
			 List<Color> colorList=productConfigObj.getColors();
			 List<String> tempClrListAlias=new ArrayList<String>();
			 for (Color objColor : colorList) {
				 tempClrListAlias.add(objColor.getAlias());
			}
			 if(!CollectionUtils.isEmpty(colorList)){
				 /*(String listOfPrices, String listOfQuan, String discountCodes,
							String currency, String priceInclude, boolean isBasePrice,
							String qurFlag, String priceName, String criterias,Integer sequence,String upChargeType,String upchargeUsageType,
							List<PriceGrid> existingPriceGrid) */
					for (String finalAlias : tempClrListAlias) {
						priceGrids=highCalPriceGridParser.getPriceGrids(tempReSetupCharge,"1","V",
								"USD","",false,"False", finalAlias,"Product Color", i+1,"Re-order Charge", "Other",priceGrids);
						i++;
					}
				}
		 }else{//this is for imprint methods
			 //here i have to check the imprint method name for the value
			 List<ImprintMethod> listOfImprintMethod=productConfigObj.getImprintMethods();
			 
			 if(!CollectionUtils.isEmpty(listOfImprintMethod)){
				 /*(String listOfPrices, String listOfQuan, String discountCodes,
							String currency, String priceInclude, boolean isBasePrice,
							String qurFlag, String priceName, String criterias,Integer sequence,String upChargeType,String upchargeUsageType,
							List<PriceGrid> existingPriceGrid) */
				 List<String> tempImpListAlias=new ArrayList<String>();
				 tempImpListAlias=getImprintAliasList(listOfImprintMethod);
				 if(!CollectionUtils.isEmpty(tempImpListAlias)){ 
					for (String finalAlias : tempImpListAlias) {
						priceGrids=highCalPriceGridParser.getPriceGrids(tempReSetupCharge,"1","V",
								"USD","",false,"False", finalAlias,"Imprint Method", i+1,"Re-order Charge", "Other",priceGrids);
						i++;
					}
				 }
				}
		 }
	 }
	 // need to create a map over here 
	 //color upcharges
	 //same goes here as well
	 //color & location
	 // same goes here as well
	 if(CollectionUtils.isEmpty(priceGrids)){
			priceGrids = highCalPriceGridParser.getPriceGridsQur();	
		}
	 
	 productExcelObj.setPriceType("L");//need to cofirm
	 	productExcelObj.setPriceGrids(priceGrids);
	 	productExcelObj.setProductConfigurations(productConfigObj);
	 	_LOGGER.info("Product Data : "
				+ mapperObj.writeValueAsString(productExcelObj));
	 	/*if(xidList.contains(productExcelObj.getExternalProductId().trim())){
	 		productExcelObj.setAvailability(new ArrayList<Availability>());
	 	}*/
	 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId);
	 	if(num ==1){
	 		numOfProductsSuccess.add("1");
	 	}else if(num == 0){
	 		numOfProductsFailure.add("0");
	 	}else{
	 		
	 	}
	 	_LOGGER.info("list size>>>>>>"+numOfProductsSuccess.size());
	 	_LOGGER.info("Failure list size>>>>>>"+numOfProductsFailure.size());
       finalResult = numOfProductsSuccess.size() + "," + numOfProductsFailure.size();
       
       productDaoObj.saveErrorLog(asiNumber,batchId);

   	//reset all list and objects over here
		priceGrids = new ArrayList<PriceGrid>();
		productConfigObj = new ProductConfigurations();
		listOfQuantityProd1 = new StringBuilder();
		 listOfPricesProd1 = new StringBuilder();
		 listOfQuantityRush = new StringBuilder();
		 listOfPricesRush = new StringBuilder();
		 listOfQuantityProd2 = new StringBuilder();
		 listOfPricesProd2 = new StringBuilder();
		 prodTime="";
		 finalProdTimeVal="";
		 rushTime="";
		 finalRushTimeVal="";
		 prodTimeVal2="";
		 finalProdTimeVal2="";
		prodTimeList=new ArrayList<ProductionTime>();
		ShipingObj=new ShippingEstimate();
		setUpchrgesVal="";
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
		     xidCell = row.getCell(1);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}
	
	
	
	public static String removeSpecialChar(String tempValue){
		tempValue=tempValue.replaceAll("(Day|Service|Days|Hour|Hours|Week|Weeks|Rush|R|u|s|h)", "");
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


	public HighCaliberAttributeParser getHighCaliberAttributeParser() {
		return highCaliberAttributeParser;
	}


	public void setHighCaliberAttributeParser(
			HighCaliberAttributeParser highCaliberAttributeParser) {
		this.highCaliberAttributeParser = highCaliberAttributeParser;
	}
	
	

	public HighCaliberPriceGridParser getHighCalPriceGridParser() {
		return highCalPriceGridParser;
	}


	public void setHighCalPriceGridParser(
			HighCaliberPriceGridParser highCalPriceGridParser) {
		this.highCalPriceGridParser = highCalPriceGridParser;
	}

}



