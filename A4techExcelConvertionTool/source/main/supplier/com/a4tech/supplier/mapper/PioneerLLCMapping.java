package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

import parser.BagMakers.BagMakerAttributeParser;
import parser.BagMakers.BagMakersPriceGridParser;
import parser.PioneerLLC.PioneerLLCAttributeParser;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.kuku.criteria.parser.KukuPriceGridParser;
import com.a4tech.product.kuku.criteria.parser.PersonlizationParser;
import com.a4tech.product.kuku.criteria.parser.ProductColorParser;
import com.a4tech.product.kuku.criteria.parser.ProductImprintMethodParser;
import com.a4tech.product.kuku.criteria.parser.ProductMaterialParser;
import com.a4tech.product.kuku.criteria.parser.ProductPackagingParser;
import com.a4tech.product.kuku.criteria.parser.ProductShippingEstimationParser;
import com.a4tech.product.kuku.criteria.parser.ProductSizeParser;
import com.a4tech.product.kuku.mapping.KukuProductsExcelMapping;
import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.AdditionalLocation;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.Personalization;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PioneerLLCMapping implements IExcelParser{

	
	private static final Logger _LOGGER = Logger.getLogger(PioneerLLCMapping.class);
	PostServiceImpl postServiceImpl;
	ProductDao productDaoObj;
	BagMakerAttributeParser bagMakerAttributeParser;
	BagMakersPriceGridParser bagMakersPriceGridParser;
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
		  
		  String shippinglen="";
		  String shippingWid="";
		  String shippingH="";
		   String shippingWeightValue="";
		  String noOfitem="";
		  boolean existingFlag=false;
		  String plateScreenCharge="";
		  String plateScreenChargeCode="";
		  String plateReOrderCharge="";
		  String plateReOrderChargeCode="";
		  String extraColorRucnChrg="";
		  String extraLocRunChrg="";
		  String extraLocColorScreenChrg="";
		  StringBuilder listOfQuantity = new StringBuilder();
		  StringBuilder listOfPrices = new StringBuilder();
		  StringBuilder listOfDiscount = new StringBuilder();
		  String basePricePriceInlcude="";
		  String tempQuant1="";
		  try{
			  Cell cell2Data = null;
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
	
		Set<String>  productXids = new HashSet<String>();
		 List<String> repeatRows = new ArrayList<>();
		 ShippingEstimate ShipingObj=new ShippingEstimate();
		 Dimensions dimensionObj=new Dimensions();
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
					/*Cell cell = cellIterator.next();
					  columnIndex = cell.getColumnIndex();
					if(columnIndex + 1 == 1){
						xid = getProductXid(nextRow);//CommonUtility.getCellValueStrinOrInt(cell);//
						checkXid = true;
					}else{
						checkXid = false;
					}*/
				
					
				 /*columnIndex = cell.getColumnIndex();
				 if (columnIndex == 1) {
						xid = getProductXid(nextRow);
						checkXid = true;
					} else {
						checkXid = false;
					}
				if(columnIndex + 1 == 1){
					xid = getProductXid(nextRow);
					checkXid = true;
				}*/
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
				        /*ProdNo = CommonUtility
				          .getCellValueStrinOrInt(cell2Data);
				        ProdNo = ProdNo.substring(0, 14);*/
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
								   // Add repeatable sets here
								 productExcelObj=bagMakersPriceGridParser.getPricingData(listOfPrices.toString(), listOfQuantity.toString(), listOfDiscount.toString(), basePricePriceInlcude, 
											plateScreenCharge, plateScreenChargeCode,
										    plateReOrderCharge, plateReOrderChargeCode, priceGrids, 
										    productExcelObj, productConfigObj);
								 	productExcelObj.setPriceType("L");
								 	//productExcelObj.setPriceGrids(priceGrids);
								 	productExcelObj.setProductConfigurations(productConfigObj);
								 	/* _LOGGER.info("Product Data : "
												+ mapperObj.writeValueAsString(productExcelObj));
								 	*/
								 	/*if(xidList.contains(productExcelObj.getExternalProductId().trim())){
								 		productExcelObj.setAvailability(new ArrayList<Availability>());
								 	}*/
								 	productExcelObj.setMakeActiveDate("2018-01-01T00:00:00");//priceConfirmedThru
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
									priceGrids = new ArrayList<PriceGrid>();
									productConfigObj = new ProductConfigurations();
									 ShipingObj=new ShippingEstimate();
									 dimensionObj=new Dimensions();
									 setUpchrgesVal="";
									 plateScreenCharge="";
							  		 plateScreenChargeCode="";
							  		 plateReOrderCharge="";
							  		 plateReOrderChargeCode="";
							  		 extraColorRucnChrg="";
							  	     extraLocRunChrg="";
							  	     extraLocColorScreenChrg="";
							  	    listOfQuantity = new StringBuilder();
							  	    listOfPrices = new StringBuilder();
							  	    listOfDiscount = new StringBuilder();
							  	    basePricePriceInlcude="";
							  	    tempQuant1="";
							  	   

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
								    	    productExcelObj=bagMakerAttributeParser.getExistingProductData(existingApiProduct, existingApiProduct.getProductConfigurations());
											productConfigObj=productExcelObj.getProductConfigurations();
											existingFlag=true;
										   // priceGrids = productExcelObj.getPriceGrids();
								     }
						 }
					}
					
					
					
					switch (columnIndex + 1) {
					case 1://XID
						break;
					case 2://××Prod # (up to 14 characters)
						String productNo = CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(productNo)){
						  productExcelObj.setAsiProdNo(productNo);
						}
						break;
					case 3://××Product Name (Up to 50 characters)
						String productName = cell.getStringCellValue();
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
						List<String> productKeywords = CommonUtility.getStringAsList(keywords,
                                ApplicationConstants.CONST_DELIMITER_COMMA);
						productExcelObj.setProductKeywords(productKeywords);
						/*List<String> productKeywordsTemp=new ArrayList<String>();
						for (String keyword : productKeywords) {
							if(keyword.length()<=30){
								productKeywordsTemp.add(keyword);
							}
						}*/
						
						}
						break;
					case 5://Summary
						String summary = CommonUtility.getCellValueStrinOrInt(cell);
						 if(!StringUtils.isEmpty(summary)){
							 productExcelObj.setSummary(CommonUtility.getStringLimitedChars(summary, 130));
						 }
						break;
					case 6://××Description (Up to 450 characters.)
						String description =CommonUtility.getCellValueStrinOrInt(cell);
						//description = CommonUtility.removeSpecialSymbols(description,specialCharacters);
						 if(!StringUtils.isEmpty(description)){
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
						break;
					case 8://Net price 1
						break;
					case 9://Retail Price 1
						break;
					case 10://Qty 1
						break;
					case 11://Net price 1
						break;
					case 12://Retail Price 1
						break;
					case 13://Qty 1
						break;
					case 14://Net price 1
						break;
					case 15://Retail Price 1
						break;
					case 16://Qty 1
						break;
					case 17://Net price 1
						break;
					case 18://Retail Price 1
						break;
					case 19://Qty 1
						break;
					case 20://Net price 1
						break;
					case 21://Retail Price 1
						break;
					case 22://Product Colors
						
						
						
						
						break;
					case 23://Materials
						break;
					case 24://Sizes
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
						
						
						String imprintMethodValue=CommonUtility.getCellValueStrinOrInt(cell);
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
						break;
					case 30://Carton
						break;
					case 31://Gross Weight
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
		productExcelObj=bagMakersPriceGridParser.getPricingData(listOfPrices.toString(), listOfQuantity.toString(), listOfDiscount.toString(),basePricePriceInlcude,  
																plateScreenCharge, plateScreenChargeCode,
															    plateReOrderCharge, plateReOrderChargeCode, priceGrids, 
															    productExcelObj, productConfigObj);
		
	 	productExcelObj.setPriceType("L");
	 	//productExcelObj.setPriceGrids(priceGrids);
	 	productExcelObj.setProductConfigurations(productConfigObj);
	 	/* _LOGGER.info("Product Data : "
					+ mapperObj.writeValueAsString(productExcelObj));
	 	*/
	 	/*if(xidList.contains(productExcelObj.getExternalProductId().trim())){
	 		productExcelObj.setAvailability(new ArrayList<Availability>());
	 	}*/
	 	productExcelObj.setMakeActiveDate("2018-01-01T00:00:00");
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
		priceGrids = new ArrayList<PriceGrid>();
		productConfigObj = new ProductConfigurations();
		ShipingObj=new ShippingEstimate();
		dimensionObj=new Dimensions();
		setUpchrgesVal="";
		plateScreenCharge="";
 		 plateScreenChargeCode="";
 		 plateReOrderCharge="";
 		 plateReOrderChargeCode="";
 		 extraColorRucnChrg="";
 	     extraLocRunChrg="";
 	     extraLocColorScreenChrg="";
 	    listOfQuantity = new StringBuilder();
  	    listOfPrices = new StringBuilder();
  	    listOfDiscount = new StringBuilder();
  	   basePricePriceInlcude="";
  	   tempQuant1="";
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


	public BagMakerAttributeParser getBagMakerAttributeParser() {
		return bagMakerAttributeParser;
	}


	public void setBagMakerAttributeParser(
			BagMakerAttributeParser bagMakerAttributeParser) {
		this.bagMakerAttributeParser = bagMakerAttributeParser;
	}


	public BagMakersPriceGridParser getBagMakersPriceGridParser() {
		return bagMakersPriceGridParser;
	}


	public void setBagMakersPriceGridParser(
			BagMakersPriceGridParser bagMakersPriceGridParser) {
		this.bagMakersPriceGridParser = bagMakersPriceGridParser;
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

}
