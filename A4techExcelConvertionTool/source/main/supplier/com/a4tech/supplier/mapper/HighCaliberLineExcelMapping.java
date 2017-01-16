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

import parser.highcaliberline.HighCaliberAttributeParser;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Artwork;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintColor;
import com.a4tech.product.model.ImprintColorValue;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Inventory;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.Size;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HighCaliberLineExcelMapping implements IExcelParser{

	
	private static final Logger _LOGGER = Logger.getLogger(HighCaliberLineExcelMapping.class);
	PostServiceImpl postServiceImpl;
	ProductDao productDaoObj;
	HighCaliberAttributeParser highCaliberAttributeParser;
	@Autowired
	ObjectMapper mapperObj;
	/*ProductSizeParser sizeParser;
	ProductImprintMethodParser imprintMethodParser;
	BBIPriceGridParser bbiPriceGridParser;*/
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
		  
		  
		  String priceType    = null;
		  String basePriceName = null;
		  String priceIncludes = null;
		  String upChargeName = null;
		  String upChargeQur = null;
		  String upchargeType = null;
		  String upChargeDetails = null;
		  String upChargeLevel = null;
		  String imprintMethodValue=null;
		  String imprintMethodValueUpchrg=null;
		  String listPrice = null;
		  String discCode=null;
		  String priceIncludeUp=null;
		  String artWork=null;
		  String artWorkChrg=null;
		  String logoCharg=null;
		  String copyChrg=null;
		  String artWrkVal=null;
		  String productDescription=null;
		  boolean existingFlag=false;
		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		StringBuilder listOfQuantity = new StringBuilder();
		StringBuilder listOfPrices = new StringBuilder();
		StringBuilder listOfDiscount = new StringBuilder();
		List<ImprintMethod> imprintMethods = new ArrayList<ImprintMethod>();
		List<ProductionTime> listOfProductionTime = new ArrayList<ProductionTime>();
		Set<String>  productXids = new HashSet<String>();
		
		 List<String> repeatRows = new ArrayList<>();
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
								 
								   // Add repeatable sets here
								 	productExcelObj.setPriceGrids(priceGrids);
								 	productExcelObj.setProductConfigurations(productConfigObj);
								 	 _LOGGER.info("Product Data : "
												+ mapperObj.writeValueAsString(productExcelObj));
								 	int num = 0;//postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId);
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
								    	    productExcelObj=existingApiProduct;
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
					case 6://Decoration
						//ignore this column
						break;
					case 7://Web Link
						String productDataSheet=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(productDataSheet)){
						productExcelObj.setProductDataSheet(productDataSheet);
						}
						break;
					case 8://ART Template Url

						break;
					case 9://Download Image Url
						if(!existingFlag){//image only for new product
						String image=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(image)){
							List<Image> listOfImages = highCaliberAttributeParser.getImages(image);
							productExcelObj.setImages(listOfImages);
							}
						 }
						break;
					case 10://Item Color
						String colorValue=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(colorValue)){
							 List<Color> colorList=highCaliberAttributeParser.getColorCriteria(colorValue);
							productConfigObj.setColors(colorList);
							}
						break;
					case 11://Item Size

						break;
					case 12://Length

						break;
					case 13://Width

						break;
					case 14://Height

						break;
					case 15://Carton Weight

						break;
					case 16://Carton Qty

						break;
					case 17://Standard Production Time

						break;
					case 18://Item MOQ  // q1

						break;
					case 19://Qty 2

						break;
					case 20://Qty 3

						break;
					case 21://Qty 4

						break;
					case 22://Qty 5

						break;
					case 23://Price 1

						break;
					case 24://Price 2

						break;
					case 25://Price 3

						break;
					case 26://Price 4
						break;
					case 27://Price 5
						break;
					case 28://Rush Cost "R"
						break;
					case 29://MOQ

						break;
					case 30://Qty 2

						break;
					case 31://Qty 3
						break;
					case 32://Qty 4
						break;
					case 33://Qty 5
						break;
					case 34://Price 1
						break;
					case 35://Price 2
						break;
					case 36://Price 3
						break;
					case 37://Price 4
						break;
					case 38://Price 5
						break;
					case 39://Ocean Service
						break;
					case 40://MOQ

						break;
					case 41://Qty 2

						break;
					case 42://Qty 3

						break;
					case 43://Qty 4
						break;
					case 44://Qty 5
						break;

					case 45://Price 1

						break;
					case 46://Price 2
						break;
					case 47://Price 3
						break;
					case 48://Price 4
						break;
					case 49://Price 5
						break;
					
					}
				} // end inner while loop
			
			
			
			/*if(!StringUtils.isEmpty(listOfPrices)){
				////imp code
				priceGrids=	 new ArrayList<PriceGrid>();
				////
				priceGrids = bbiPriceGridParser.getPriceGrids(listOfPrices.toString(),listOfQuantity.toString(), discCode,ApplicationConstants.CONST_STRING_CURRENCY_USD,
						priceIncludes,ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, productName,ApplicationConstants.CONST_STRING_EMPTY,priceGrids);	
			}
			
			if(!StringUtils.isEmpty(imprintMethodValue)&& !StringUtils.isEmpty(imprintMethodValueUpchrg)){
				//$2.50 (v)
				discCode=imprintMethodValueUpchrg.substring(imprintMethodValueUpchrg.indexOf("(")+1, imprintMethodValueUpchrg.indexOf(")"));
				imprintMethodValueUpchrg=imprintMethodValueUpchrg.substring(imprintMethodValueUpchrg.indexOf("$")+1, imprintMethodValueUpchrg.indexOf("("));
				
			priceGrids = bbiPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,imprintMethodValueUpchrg.trim(),discCode ,
					"IMMD:"+imprintMethodValue,ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, imprintMethodValue, 
					ApplicationConstants.CONST_STRING_RUN_CHARGE, ApplicationConstants.CONST_VALUE_TYPE_OTHER, ApplicationConstants.CONST_INT_VALUE_ONE, priceGrids);
		    }
			if(!StringUtils.isEmpty(artWrkVal)&&!StringUtils.isEmpty(artWorkChrg)){
				discCode=artWorkChrg.substring(artWorkChrg.indexOf("(")+1, artWorkChrg.indexOf(")"));
				artWorkChrg=artWorkChrg.substring(artWorkChrg.indexOf(":")+1, artWorkChrg.indexOf("("));
				priceGrids = bbiPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,artWorkChrg.trim(), discCode,
						"ARTW:"+artWrkVal,ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, artWrkVal, 
						ApplicationConstants.CONST_STRING_ARTWK_CHARGE, ApplicationConstants.CONST_VALUE_TYPE_OTHER, 2, priceGrids);
			}
			if(!StringUtils.isEmpty(imprintMethodValue)&&!StringUtils.isEmpty(logoCharg)){
				discCode=logoCharg.substring(logoCharg.indexOf("(")+1, logoCharg.indexOf(")"));
				logoCharg=logoCharg.substring(logoCharg.indexOf(":")+1, logoCharg.indexOf("("));
				priceGrids = bbiPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,logoCharg.trim(), discCode,
						"IMMD:"+imprintMethodValue,ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, imprintMethodValue, 
						ApplicationConstants.CONST_STRING_SETUP_CHARGE, ApplicationConstants.CONST_VALUE_TYPE_OTHER, 3, priceGrids);
			}*/
			
			/*if(!StringUtils.isEmpty(imprintMethodValue)&&!StringUtils.isEmpty(copyChrg)){
				discCode=copyChrg.substring(copyChrg.indexOf("(")+1, copyChrg.indexOf(")"));
				copyChrg=copyChrg.substring(copyChrg.indexOf(":")+1, copyChrg.indexOf("("));
				
				priceGrids = bbiPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,copyChrg.trim(), discCode,
						"IMMD:"+imprintMethodValue,ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, imprintMethodValue, 
						ApplicationConstants.CONST_STRING_COPY_CHARGE, ApplicationConstants.CONST_VALUE_TYPE_OTHER, 4, priceGrids);
			}*/
			
			}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);		 
				ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: "
				+e.getMessage()+" at column number(increament by 1)"+columnIndex);
				productDaoObj.save(apiResponse.getErrors(),
						productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);
				}
	}
	workbook.close();
	
	 	productExcelObj.setPriceGrids(priceGrids);
	 	productExcelObj.setProductConfigurations(productConfigObj);
	 	_LOGGER.info("Product Data : "
				+ mapperObj.writeValueAsString(productExcelObj));
	 	int num = 0;//postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId);
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

		upChargeQur = null;
		listOfPrices = new StringBuilder();
		listOfProductionTime=new ArrayList<ProductionTime>();
		imprintMethods = new ArrayList<ImprintMethod>();
		artWork=null;
		artWorkChrg=null;
		logoCharg=null;
		copyChrg=null;
		artWrkVal=null;
		imprintMethodValueUpchrg=null;
		imprintMethodValue=null;
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
	
}



