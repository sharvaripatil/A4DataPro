package com.a4tech.ESPTemplate.product.mapping;

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

import com.a4tech.product.ESPTemplate.parser.OriginParser;
import com.a4tech.product.DCProducts.parser.DCPriceGridParser;
import com.a4tech.product.DCProducts.parser.ShippingEstimationParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;


public class ESPTemplateMapping {
	private static final Logger _LOGGER = Logger.getLogger(ESPTemplateMapping.class);
	
	private PostServiceImpl postServiceImpl;
	ProductDao productDaoObj;
	//DCPriceGridParser dcPriceGridParser;
	private OriginParser originParser;

	private ShippingEstimationParser shippingEstimationParser;

	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  String productId = null;
		  String currencyType = null;
		  String priceQurFlag = null;
		  String priceType    = null;
		  String basePriceName = null;
		
		  String upChargeName = null;
		  String upChargeQur = null;
		  String upchargeType = null;
		  String upChargeDetails = null;
		  String upChargeLevel = null;
		  
		  
			String RushTime=null;
			
			
			 List<RushTime> rushTimeList = new ArrayList<RushTime>();

		 List<ProductionTime> productionTimeList = new ArrayList<ProductionTime>();
		 ProductionTime prodTimeObj=new ProductionTime() ;
		  List<String> productKeywords = new ArrayList<String>();
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  ShippingEstimate shippingItem = null;
		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		
		
		StringBuilder listOfQuantity = new StringBuilder();
		StringBuilder listOfPrices = new StringBuilder();
		StringBuilder listOfNetPrice = new StringBuilder();
		StringBuilder listOfDiscount = new StringBuilder();
		StringBuilder basePriceCriteria =  new StringBuilder();
		StringBuilder UpCharQuantity = new StringBuilder();
		StringBuilder UpCharPrices = new StringBuilder();
		StringBuilder UpchargeNetPrice = new StringBuilder();
		StringBuilder UpCharDiscount = new StringBuilder();
		StringBuilder UpCharCriteria = new StringBuilder();
		String		priceCode = null;
		StringBuilder pricesPerUnit = new StringBuilder();
		String quoteUponRequest  = null;
		StringBuilder priceIncludes = new StringBuilder();
		String quantity = null;
		String listPrice = null;
		String netPrice = null;
		String discCode=null;
		String productName = null;

		
		

		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
		 
			if (nextRow.getRowNum() ==0)
				continue;
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if(productId != null){
				productXids.add(productId);
			}
			 boolean checkXid  = false;
			
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				String xid = null;
				int columnIndex = cell.getColumnIndex();
				
				if(columnIndex + 1 == 1){
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						xid = cell.getStringCellValue();
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						xid = String.valueOf((int)cell.getNumericCellValue());
					}else{
						
					}
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							 System.out.println("Java object converted to JSON String, written to file");
							 	productExcelObj.setPriceGrids(priceGrids);
							 	productExcelObj.setProductConfigurations(productConfigObj);
							 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId);
							 	if(num ==1){
							 		numOfProductsSuccess.add("1");
							 	}else if(num == 0){
							 		numOfProductsFailure.add("0");
							 	}else{
							 		
							 	}
							 	_LOGGER.info("list size>>>>>>>"+numOfProductsSuccess.size());
							 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
								priceGrids = new ArrayList<PriceGrid>();
								productConfigObj = new ProductConfigurations();
								
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    }
							productExcelObj = new Product();
					 }
				}
				
				switch (columnIndex + 1) {

				case 1://ExternalProductId
					 String asiProdNo = null;
					    if(cell.getCellType() == Cell.CELL_TYPE_STRING){ 
					      asiProdNo = String.valueOf(cell.getStringCellValue().trim());
					    }else if
					     (cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
					      asiProdNo = String.valueOf((int)cell.getNumericCellValue());
					     }
					     productExcelObj.setAsiProdNo(asiProdNo);	
			   
					 break;
				case 2://Product Name 
				    productName = cell.getStringCellValue();
					if(!StringUtils.isEmpty(productName)){
					productExcelObj.setName(cell.getStringCellValue());
					}else{
						productExcelObj.setName(ApplicationConstants.CONST_STRING_EMPTY);
					}
						
					  break;
				case 3://Summary 
					String summary = cell.getStringCellValue();
					if(!StringUtils.isEmpty(summary)){
					productExcelObj.setSummary(summary);
					}else{
						productExcelObj.setSummary(ApplicationConstants.CONST_STRING_EMPTY);
					}
					
     					break;
				case 4://key words
					String productKeyword = cell.getStringCellValue();
					if(!StringUtils.isEmpty(productKeyword)){
					String productKeywordArr[] = productKeyword.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					for (String string : productKeywordArr) {
						productKeywords.add(string);
					}
					productExcelObj.setProductKeywords(productKeywords);
					}
				    break;
					
				case 5://Description 
					String description = cell.getStringCellValue();
					if(!StringUtils.isEmpty(description)){
					productExcelObj.setDescription(description);
					}else{
						productExcelObj.setDescription(ApplicationConstants.CONST_STRING_EMPTY);
					}
					break;
					
				case 6: //  Product Colors
					String ShipWeight1 = cell.getStringCellValue();

					
					
					break;
					

				case 7://Shapes
					//Field is blank

					   
						
					break;
					
				case 8: // Sizes
					String ShipWidth1 = cell.getStringCellValue();

					break;
					
				case 9: //Materials
					String ShipHeight1 = cell.getStringCellValue();
					/*if(shippingWeightValue != null && !shippingWeightValue.isEmpty()){
					shippingItem = shippingEstimationParser.getShippingEstimates(shippingWeightValue);
					if(shippingItem.getDimensions()!=null || shippingItem.getNumberOfItems()!=null || shippingItem.getWeight()!=null ){
					productConfigObj.setShippingEstimates(shippingItem);
					}
					}*/
					break;	
					
				case 10: //Origin
					
					String origin = cell.getStringCellValue();
					if(!origin.isEmpty()){
						List<Origin> listOfOrigin = originParser.getOriginValues(origin);
						productConfigObj.setOrigins(listOfOrigin);
					} 
					
					break;
					
				case 11:  //Production Time 
					String productionTime=null;
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						productionTime = cell.getStringCellValue().trim();
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						productionTime = String.valueOf(cell.getNumericCellValue()).trim();
					}
					prodTimeObj.setBusinessDays(productionTime);
					productionTimeList.add(prodTimeObj);
					productConfigObj.setProductionTime(productionTimeList);
					
					break;
				
				case 12:  // Rush Time 
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
					RushTime = cell.getStringCellValue();
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						RushTime = String.valueOf(cell.getNumericCellValue());
					}
					
					break;

				case 13: //Product Options
					//Field is blank
			
					break;
					
				case 14: //FOB Point 
					
					break;
					
				case 15://Shipping Weight
					
					break;
				case 16: //Packaging
					
					  break;
				
				case 17: //Sold as Blanks? Y/N
					
					break;
				
				 case 18: //Personalization available? Y/N
					 Boolean rushService = cell.getBooleanCellValue();
				
					break;
				 case 19:
				 
					 /*RushTime rt=new RushTime();
					//Rush service available? Y/N
					 Boolean rushService = cell.getBooleanCellValue();
				     rt.setAvailable(rushService);
				     if(RushTime!=null){
					  productConfigObj.setRushTime(rushTimeList);
					}*/

					 break;

				case 20:
					//Four color process available? Y/N
					break;
				case 21:
					//Imprint Methods
					break;
				case 22:
					
					//Imprinting Charges
					break;
				case 23: 
					//Imprint Colors
					//Field is blank
					
					
					break;
				case 24:  
					//Imprint Options
					//Field is blank
					break;
				case 25: 
					
					//Imprint size
					
					break;
					
				case 26:
					//AdditionalColor/Location
					//Field is blank
					break;
				case 27: 
					//Price Includes: 
			/*
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						quantity = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(quantity)){
				        	 listOfQuantity.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						int quantityInt = (int)cell.getNumericCellValue();
				         if(!StringUtils.isEmpty(quantityInt)){
				        	 listOfQuantity.append(quantityInt).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else{
					}*/
					break;
				case 28:
					//Qty 1
					break;
				case 29:
					//Price 1
					break;
				case 30:  
					//Code 1
					break;
				case 31:
					//Qty2
					break;
				case 32:
					//Price 2
					break;
				case 33:
					//Code 2
					break;
				case 34:
					//Qty3
					break;
				case 35://Price 3
					/*if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						netPrice = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(netPrice)){
				        	 listOfNetPrice.append(netPrice).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						double netPriceInt = (double)cell.getNumericCellValue();
				         if(!StringUtils.isEmpty(netPriceInt)){
				        	 listOfNetPrice.append(netPriceInt).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else{
					}*/
					    break; 
				case 36://Code3
					
					
					break;
case 37://Qty4
					
					
					break;
				case 38:  
					//Price4
					break;
				case 39:
					//Code4
					break;
				case 40:
					//Qty5
					break;
				case 41:
					//Price5
					break;
				case 42:
					//Code5
					break;
				case 43:
					//Qty6
					
					break;
				case 44://Price6

					/*if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						listPrice = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(listPrice)){
				        	 listOfPrices.append(listPrice).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						double listPriceD = (double)cell.getNumericCellValue();
				         if(!StringUtils.isEmpty(listPriceD)){
				        	 listOfPrices.append(listPriceD).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else{
					}*/
					      break;
				case 45://Code6
					
					 break;
				case 46: //Qty7
					
					break;
				case 47://Price7
					
					break;
				case 48:  //Code7
					
					break;
				case 49://Qty8
					break;
			
				case 50://Price8
					
					break;
					
				case 51://Code8
					/*discCode = cell.getStringCellValue();
			         if(!StringUtils.isEmpty(discCode)){
			        	 listOfDiscount.append(discCode).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			         }*/
			         break;
			   
							}  // end inner while loop
					 
		}
			// set  product configuration objects
			List<String> listOfCategories = new ArrayList<String>();
			listOfCategories.add("USB/FLASH DRIVES");
			productExcelObj.setCategories(listOfCategories);
			String productDescription ="Phone Holder USB 2.0 Flash Drive";
			productExcelObj.setDescription(productDescription);
			
			
			 // end inner while loop
			/*productExcelObj.setPriceType("L");
			if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
				priceGrids = dcPriceGridParser.getPriceGrids(listOfPrices.toString(),listOfNetPrice.toString(), 
						         listOfQuantity.toString(), listOfDiscount.toString(), "USD",
						         "", true, "N", productName,"",priceGrids);	
			}
			*/
			 
				/*if(UpCharCriteria != null && !UpCharCriteria.toString().isEmpty()){
					priceGrids = priceGridParser.getUpchargePriceGrid(UpCharQuantity.toString(), UpCharPrices.toString(), UpCharDiscount.toString(), UpCharCriteria.toString(), 
							 upChargeQur, currencyType, upChargeName, upchargeType, upChargeLevel, new Integer(1), priceGrids);
				}*/
				
				
				upChargeQur = null;
				UpCharCriteria = new StringBuilder();
				priceQurFlag = null;
				listOfPrices = new StringBuilder();
				UpCharPrices = new StringBuilder();
				UpCharDiscount = new StringBuilder();
				UpCharQuantity = new StringBuilder();
			
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() );		 
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
			_LOGGER.error("Error while Processing excel sheet ,Error message: "+e.getMessage());
			return finalResult;
		}finally{
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet, Error message: "+e.getMessage());
	
			}
				_LOGGER.info("Complted processing of excel sheet ");
				_LOGGER.info("Total no of product:"+numOfProductsSuccess.size() );
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

	
	public ShippingEstimationParser getShippingEstimationParser() {
		return shippingEstimationParser;
	}

	public void setShippingEstimationParser(
			ShippingEstimationParser shippingEstimationParser) {
		this.shippingEstimationParser = shippingEstimationParser;
	}

	public OriginParser getOriginParser() {
		return originParser;
	}

	public void setOriginParser(OriginParser originParser) {
		this.originParser = originParser;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
	