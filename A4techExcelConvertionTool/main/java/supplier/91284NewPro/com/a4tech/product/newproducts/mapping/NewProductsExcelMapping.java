package com.a4tech.product.newproducts.mapping;

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

import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Artwork;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.ImprintColor;
import com.a4tech.product.model.ImprintColorValue;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Inventory;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.TradeName;
import com.a4tech.product.model.Weight;
import com.a4tech.product.newproducts.criteria.parser.NewProPriceGridParser;
import com.a4tech.product.newproducts.criteria.parser.NewProProductImprintmethodParser;
import com.a4tech.product.newproducts.criteria.parser.NewProSizeParser;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;


public class NewProductsExcelMapping {
	
	private static final Logger _LOGGER = Logger.getLogger(NewProductsExcelMapping.class);
	PostServiceImpl postServiceImpl;
	ProductDao productDaoObj;
	//ProductSizeParser sizeParser;
	NewProProductImprintmethodParser newProimprintMethodParser;
	NewProPriceGridParser newProPriceGridParser;
	@SuppressWarnings("finally")
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber,int batchId){
		int columnIndex = 0;
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		
		  Set<String>  listOfProductXids = new HashSet<String>();
		  Product productExcelObj = new Product();  
		  Product existingApiProduct = new Product();
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  ShippingEstimate shipingObj = new ShippingEstimate();
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
		  String imprintMethodValueUpchrgDis=null;
		  String listPrice = null;
		  String discCode=null;
		  String priceIncludeUp=null;
		  String artWork=null;
		  String artWorkChrg=null;
		  String logoCharg=null;
		  String copyChrg=null;
		  String artWrkVal=null;
		  String productDescription=null;
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
		
		String productName = null;
		String q1 = null,q2= null,q3= null,q4= null,q5= null,q6=null;
		String tradeName=null;
		Dimensions dimensions=new Dimensions();
		while (iterator.hasNext()) {
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum() == 0){
				Cell cell1=nextRow.getCell(11);
				q1=String.valueOf(cell1.getNumericCellValue());
				
				cell1=nextRow.getCell(12);
				q2=String.valueOf(cell1.getNumericCellValue());
				cell1=nextRow.getCell(13);
				q3=String.valueOf(cell1.getNumericCellValue());
				cell1=nextRow.getCell(14);
				q4=String.valueOf(cell1.getNumericCellValue());
				cell1=nextRow.getCell(15);
				q5=String.valueOf(cell1.getNumericCellValue());
			}
			if (nextRow.getRowNum() == 0)
				continue;
			
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			 if(productId != null){
				 listOfProductXids.add(productId);
			 }
			 boolean checkXid  = false;
			
			 while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					String xid = null;
					  columnIndex = cell.getColumnIndex();
					if(columnIndex + 1 == 2){
						xid=CommonUtility.getCellValueStrinOrInt(cell);
						checkXid = true;
					}else{
						checkXid = false;
					}
					if(checkXid){
						 if(!listOfProductXids.contains(xid)){
							 if(nextRow.getRowNum() != 1){
								 System.out.println("Java object converted to JSON String, written to file");
								   // Add repeatable sets here
								 	productExcelObj.setPriceGrids(priceGrids);
								 	productExcelObj.setProductConfigurations(productConfigObj);
								 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId);
								 	if(num ==1){
								 		numOfProductsSuccess.add("1");
								 	}else if(num == 0) {
								 		numOfProductsFailure.add("0");
								 	}else{
								 		
								 	}
								 	_LOGGER.info("list size of success>>>>>>>"+numOfProductsSuccess.size());
									
									priceGrids = new ArrayList<PriceGrid>();
									productConfigObj = new ProductConfigurations();
									
									
							 }
							    if(!listOfProductXids.contains(xid)){
							    	listOfProductXids.add(xid);
							    }
								productExcelObj = new Product();
						 }
					}
					switch (columnIndex + 1) {
					case 1:
						  tradeName=cell.getStringCellValue();
						break;
					case 2:
						productId=CommonUtility.getCellValueStrinOrInt(cell);
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
						}
						////*/
						productExcelObj.setExternalProductId(productId);
						productExcelObj.setAsiProdNo(productId);
						break;
						
					case 3:
						// product description
						 productDescription = cell.getStringCellValue();
						 if(!StringUtils.isEmpty(productDescription)){
						productDescription=	productDescription.replaceAll(ApplicationConstants.CONST_STRING_NEWLINE,ApplicationConstants.CONST_VALUE_TYPE_SPACE);
						productExcelObj.setDescription(productDescription);
						
						int len=productDescription.length();
						if(len<=60){
							productName=productDescription;
							
						}else if(len>60){
							String strTemp=productDescription.substring(0, 60);
							int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
							productName=(String) strTemp.subSequence(0, lenTemp);
						}
						productExcelObj.setName(productName);
						 }
						break;
						
					case 4:
						String tempValue=productDescription;
						productDescription = cell.getStringCellValue();
						 if(!StringUtils.isEmpty(productDescription)){
							 productDescription=tempValue+ApplicationConstants.CONST_STRING_BIG_SPACE+productDescription;
							 productDescription=productDescription.replaceAll(ApplicationConstants.CONST_STRING_NEWLINE,ApplicationConstants.CONST_VALUE_TYPE_SPACE);
							 productExcelObj.setDescription(productDescription);
						 }else{
							 productExcelObj.setDescription(tempValue);
						 }
						break;
			
					case 5:
						String origin = cell.getStringCellValue();
						if(!StringUtils.isEmpty(origin)){
						List<Origin> listOfOrigins = new ArrayList<Origin>();
						Origin origins = new Origin();
						origins.setName(origin);
						listOfOrigins.add(origins);
						productConfigObj.setOrigins(listOfOrigins);
						}
						break;
					
					case 6: 
					String	numOfItem=CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(numOfItem)){
					
				     List<NumberOfItems> numberOfItems=new ArrayList<NumberOfItems>();
				     NumberOfItems numObj=new NumberOfItems();
				     numObj.setValue(numOfItem);
				     numObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_NUMBER_UNIT_CASE);
				     numberOfItems.add(numObj);
				     shipingObj.setNumberOfItems(numberOfItems);
				     productConfigObj.setShippingEstimates(shipingObj);
				     
					}
						break;
						
						
					case 7: 
						String	shipWT=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(shipWT)){
						 List<Weight>       weightList=new ArrayList<Weight>();
						 Weight wtObj=new Weight();
						 wtObj.setValue(shipWT);
						 wtObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_WEIGHT);
					     weightList.add(wtObj);
					     shipingObj.setWeight(weightList);
					     productConfigObj.setShippingEstimates(shipingObj);
						}
							break;
					
					case 8:
						String	sizevalue=CommonUtility.getCellValueStrinOrDecimal(cell);
						
						if(!StringUtils.isEmpty(sizevalue)){
						productExcelObj=NewProSizeParser.getSizes(sizevalue,productExcelObj,productConfigObj);
						}
						//sizes
						break;
					case 9:
						String	shipDimWid=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(shipDimWid)){
							dimensions.setWidth(shipDimWid);
							dimensions.setWidthUnit(ApplicationConstants.CONST_STRING_INCHES);
							shipingObj.setDimensions(dimensions);
							productConfigObj.setShippingEstimates(shipingObj);
						}
						
						break;
					case 10:
						String	shipDimLen=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(shipDimLen)){
							dimensions.setLength(shipDimLen);
							dimensions.setLengthUnit(ApplicationConstants.CONST_STRING_INCHES);
							shipingObj.setDimensions(dimensions);
							productConfigObj.setShippingEstimates(shipingObj);
						}
						break;
					case 11:
						String	shipDimH=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(shipDimH)){
							dimensions.setHeight(shipDimH);
							dimensions.setHeightUnit(ApplicationConstants.CONST_STRING_INCHES);
							shipingObj.setDimensions(dimensions);
							productConfigObj.setShippingEstimates(shipingObj);
						}
						break;
						
					case 12:
						String	listPrice1=null;
						listPrice1=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice1)){
				        	 listOfPrices.append(listPrice1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 listOfQuantity.append(q1.replace(".0","").trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
				
					case 13:
						String	listPrice2=null;
						listPrice2=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice2)){
				        	 listOfPrices.append(listPrice2).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 listOfQuantity.append(q2.replace(".0","").trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
						
					case 14:
						String	listPrice3=null;
						listPrice3=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice3)){
				        	 listOfPrices.append(listPrice3).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 listOfQuantity.append(q3.replace(".0","").trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
						
						
					case 15: 
						String	listPrice4=null;
						listPrice4=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice4)){
				        	 listOfPrices.append(listPrice4).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 listOfQuantity.append(q4.replace(".0","").trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					
						
					case 16:
						String	listPrice5=null;
						listPrice5=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice5)){
				        	 listOfPrices.append(listPrice5).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 listOfQuantity.append(q5.replace(".0","").trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
						
					
					case 17:
						 discCode = cell.getStringCellValue();
						 if(!StringUtils.isEmpty(discCode)){
							 discCode=discCode.replace("CLASS", "").trim();
						 }
						break;
						
					case 18:
					 	imprintMethodValue=CommonUtility.getCellValueStrinOrDecimal(cell);
						 if(!StringUtils.isEmpty(imprintMethodValue)&& !imprintMethodValue.equalsIgnoreCase("BLANK")){
							 if(imprintMethodValue.contains("Engrave")){
								 imprintMethodValue="Laser Engraved";
								}
							 imprintMethods = newProimprintMethodParser.getImprintCriteria(imprintMethodValue,imprintMethods);
								productConfigObj.setImprintMethods(imprintMethods); 
						  }
						break;
						
				
					case 19:
					 	
						imprintMethodValueUpchrg=CommonUtility.getCellValueStrinOrDecimal(cell);
						break;
						
						
					case 20:
					 	
						imprintMethodValueUpchrgDis=cell.getStringCellValue();
						if(!StringUtils.isEmpty(imprintMethodValueUpchrgDis)){
							imprintMethodValueUpchrgDis=imprintMethodValueUpchrgDis.replace("CLASS", "").trim();
						 }
						break;
					
					}
				} // end inner while loop
			// productExcelObj.setDescription(productDescription);
			productExcelObj.setPriceType(ApplicationConstants.CONST_PRICE_TYPE_CODE_LIST);
			if(!StringUtils.isEmpty(tradeName)){
				List<TradeName> tradeNames=new ArrayList<TradeName>();
				TradeName tradeObj=new TradeName();
				tradeObj.setName(tradeName);
				tradeNames.add(tradeObj);
				productConfigObj.setTradeNames(tradeNames);
			}
			if(!StringUtils.isEmpty(listOfPrices)){
				////imp code
				priceGrids=	 new ArrayList<PriceGrid>();
				////
				priceGrids = newProPriceGridParser.getPriceGrids(listOfPrices.toString(),listOfQuantity.toString(), discCode,ApplicationConstants.CONST_STRING_CURRENCY_USD,
						priceIncludes,ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, productName,ApplicationConstants.CONST_STRING_EMPTY,priceGrids);	
			}
			
			if(!StringUtils.isEmpty(imprintMethodValue)&& !StringUtils.isEmpty(imprintMethodValueUpchrg)){
				
			priceGrids = newProPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,imprintMethodValueUpchrg.trim(),imprintMethodValueUpchrgDis ,
					"IMMD:"+imprintMethodValue,ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, imprintMethodValue, 
					ApplicationConstants.CONST_STRING_IMMD_CHARGE, ApplicationConstants.CONST_VALUE_TYPE_OTHER, ApplicationConstants.CONST_INT_VALUE_ONE, priceGrids);
		    }
			
			
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
			shipingObj=new ShippingEstimate();
			tradeName=null;
			dimensions=new Dimensions();
			imprintMethodValueUpchrgDis=null;
		}catch(Exception e){
		_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() +"for column"+columnIndex+1);		 
	}
	}
	workbook.close();
	
	 	productExcelObj.setPriceGrids(priceGrids);
	 	productExcelObj.setProductConfigurations(productConfigObj);

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
       productDaoObj.getErrorLog(asiNumber,batchId);
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



	public NewProProductImprintmethodParser getNewProimprintMethodParser() {
		return newProimprintMethodParser;
	}



	public void setNewProimprintMethodParser(
			NewProProductImprintmethodParser newProimprintMethodParser) {
		this.newProimprintMethodParser = newProimprintMethodParser;
	}



	public NewProPriceGridParser getNewProPriceGridParser() {
		return newProPriceGridParser;
	}



	public void setNewProPriceGridParser(NewProPriceGridParser newProPriceGridParser) {
		this.newProPriceGridParser = newProPriceGridParser;
	}

	
	
}
