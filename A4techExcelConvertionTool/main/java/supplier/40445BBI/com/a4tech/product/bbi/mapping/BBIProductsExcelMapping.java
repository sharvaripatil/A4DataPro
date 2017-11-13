package com.a4tech.product.bbi.mapping;
import java.io.IOException;
import java.math.BigDecimal;
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
import com.a4tech.product.bbi.criteria.parser.BBIPriceGridParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.bbi.criteria.parser.ProductImprintMethodParser;
import com.a4tech.product.bbi.criteria.parser.ProductSizeParser;
import com.a4tech.product.model.Artwork;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.ImprintColor;
import com.a4tech.product.model.ImprintColorValue;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Inventory;
import com.a4tech.product.model.Material;
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

public class BBIProductsExcelMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(BBIProductsExcelMapping.class);
	PostServiceImpl postServiceImpl;
	ProductDao productDaoObj;
	ProductSizeParser sizeParser;
	ProductImprintMethodParser imprintMethodParser;
	BBIPriceGridParser bbiPriceGridParser;
	@SuppressWarnings("finally")
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber,int batchId, String environmentType){
		int columnIndex = 0;
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		
		  Set<String>  listOfProductXids = new HashSet<String>();
		  Product productExcelObj = new Product();  
		  Product existingApiProduct = new Product();
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
		
		String productName = "TempProduct";
		String q1 = null,q2= null,q3= null,q4= null,q5= null,q6=null;
		while (iterator.hasNext()) {
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum() == 0){
				Cell cell1=nextRow.getCell(2);
				q1=cell1.getStringCellValue();
				
				cell1=nextRow.getCell(3);
				q2=cell1.getStringCellValue();
				cell1=nextRow.getCell(4);
				q3=cell1.getStringCellValue();
				cell1=nextRow.getCell(5);
				q4=cell1.getStringCellValue();
				cell1=nextRow.getCell(6);
				q5=cell1.getStringCellValue();
				cell1=nextRow.getCell(7);
				q6=cell1.getStringCellValue();
				
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
					if(columnIndex + 1 == 1){
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
						break;
						
					case 2:
						// product description
						 productDescription = cell.getStringCellValue();
						 if(!StringUtils.isEmpty(productDescription)){
						productDescription=productDescription.replace("\"", "");
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
			
					case 3:
						String	listPrice1=null;
						listPrice1=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice1)){
				        	 listOfPrices.append(listPrice1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 listOfQuantity.append(q1.replace("Qty","").trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
				
					case 4:
						String	listPrice2=null;
						listPrice2=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice2)){
				        	 listOfPrices.append(listPrice2).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 listOfQuantity.append(q2.replace("Qty","").trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
						
					case 5:
						String	listPrice3=null;
						listPrice3=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice3)){
				        	 listOfPrices.append(listPrice3).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 listOfQuantity.append(q3.replace("Qty","").trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
						
						
					case 6: 
						String	listPrice4=null;
						listPrice4=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice4)){
				        	 listOfPrices.append(listPrice4).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 listOfQuantity.append(q4.replace("Qty","").trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					
						
					case 7:
						String	listPrice5=null;
						listPrice5=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice5)){
				        	 listOfPrices.append(listPrice5).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 listOfQuantity.append(q5.replace("Qty","").trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
						
					case 8: 
						String	listPrice6=null;
						listPrice6=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice6)){
				        	 listOfPrices.append(listPrice6).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				        	 listOfQuantity.append(q6.replace("Qty","").trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 9:
						 discCode = cell.getStringCellValue();
				         
						break;
						
					case 10: 
						priceIncludes = cell.getStringCellValue();
						break;
						
					case 11:  
						String sizeValue = cell.getStringCellValue();
						 if(!StringUtils.isEmpty(sizeValue)){
							 Size sizeObj = new Size();
						sizeObj=sizeParser.getSizes(sizeValue);
						if(sizeObj.getApparel()!=null || sizeObj.getCapacity()!=null || sizeObj.getDimension()!=null||sizeObj.getOther()!=null||sizeObj.getVolume()!=null){
						productConfigObj.setSizes(sizeObj);
						}
						 }
						break;
		
					
					case 12:
						//images cannot process current format
						break;
					case 13: 
					 //images cannot process current format
						break;
						
					case 14:
					 	imprintMethodValue=cell.getStringCellValue();
						 if(!StringUtils.isEmpty(imprintMethodValue)){
							 imprintMethods = imprintMethodParser.getImprintCriteria(imprintMethodValue,imprintMethods);
								productConfigObj.setImprintMethods(imprintMethods); 
						  }
						break;
						
						
					case 15:
						//artwork and other upcharges.
						//Paper Proof:15 (V)       Logo Set-up:82.50 (V) Copy set-up:32.50 (V)
						artWork= cell.getStringCellValue();
						if(!StringUtils.isEmpty(artWork)){
							try{
						artWork=artWork.replace(")", "),");
						String tempArr[]=artWork.split(ApplicationConstants.CONST_DELIMITER_COMMA);
						for (int index = 0; index < tempArr.length; index++) {
						if(index==0){
							artWorkChrg=tempArr[0];
						}
						else if(index==1){
							logoCharg=tempArr[1];
						}
						else if(index==2){
							copyChrg=tempArr[2];
						}
						}
						 artWrkVal=artWorkChrg;
						artWrkVal=artWrkVal.substring(0, artWrkVal.indexOf(ApplicationConstants.CONST_DELIMITER_COLON));
						List<Artwork> artwork=new ArrayList<Artwork>();
						Artwork artObj=new Artwork();
						artObj.setValue(artWrkVal);
						artObj.setComments(ApplicationConstants.CONST_STRING_EMPTY);
						artwork.add(artObj);
						productConfigObj.setArtwork(artwork);
							}catch(Exception e){
								_LOGGER.error("Error while processing artWork: "+e.getMessage());
							}
					}
						break;
						
					case 16:
					 	//additional deco cost for imprint method
						imprintMethodValueUpchrg=cell.getStringCellValue();
						break;
					
					case 17:// imprint color
						String imprintColor= cell.getStringCellValue();
						if(!StringUtils.isEmpty(imprintColor)){
							ImprintColor imprintColorObj = new ImprintColor();
							 List<ImprintColorValue> values = new ArrayList<>();
							 ImprintColorValue valuObj=new ImprintColorValue();
							 valuObj.setName(imprintColor);
							 values.add(valuObj);
							 imprintColorObj.setType(ApplicationConstants.CONST_STRING_IMPRNT_COLR);
							imprintColorObj.setValues(values);
							productConfigObj.setImprintColors(imprintColorObj);
						}
						break;
					case 18:
						String imprintSize = cell.getStringCellValue();
						if(!StringUtils.isEmpty(imprintSize)){
							ImprintSize imprintSizeObj = new ImprintSize();
							List<ImprintSize> listImprintSize=new ArrayList<ImprintSize>();
							imprintSizeObj.setValue(imprintSize);
							listImprintSize.add(imprintSizeObj);
							productConfigObj.setImprintSize(listImprintSize);
						}
						break;
						
						
					case 19://imprint location
						String imprintLocation = cell.getStringCellValue();
						if(!StringUtils.isEmpty(imprintLocation)){
							List<ImprintLocation> values=new ArrayList<ImprintLocation>();
							ImprintLocation locObj=new ImprintLocation();
							locObj.setValue(imprintLocation);
							values.add(locObj);
							productConfigObj.setImprintLocation(values);
						}
						break;
						
						
					case 20:
						//production time
						String prodTimeLo = null;
						int tempVal;
						ProductionTime productionTime = new ProductionTime();
						
						prodTimeLo=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(prodTimeLo)){
					    prodTimeLo=prodTimeLo.replaceAll(ApplicationConstants.CONST_STRING_DAYS,ApplicationConstants.CONST_STRING_EMPTY);
						productionTime.setBusinessDays(prodTimeLo);
						productionTime.setDetails(ApplicationConstants.CONST_STRING_DAYS);
						listOfProductionTime.add(productionTime);
						productConfigObj.setProductionTime(listOfProductionTime);
						}
						break;
						
					case 21://Product Inventory Link
						String inventoryLink = cell.getStringCellValue();
						if(!StringUtils.isEmpty(inventoryLink)){
							Inventory invenObj=new Inventory();
							invenObj.setInventoryLink(inventoryLink);
							productExcelObj.setInventory(invenObj);
						}
						
						break;
					}
				} // end inner while loop
			
			productExcelObj.setPriceType(ApplicationConstants.CONST_PRICE_TYPE_CODE_LIST);
			
			if(!StringUtils.isEmpty(listOfPrices)){
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
			}
			
			if(!StringUtils.isEmpty(imprintMethodValue)&&!StringUtils.isEmpty(copyChrg)){
				discCode=copyChrg.substring(copyChrg.indexOf("(")+1, copyChrg.indexOf(")"));
				copyChrg=copyChrg.substring(copyChrg.indexOf(":")+1, copyChrg.indexOf("("));
				
				priceGrids = bbiPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,copyChrg.trim(), discCode,
						"IMMD:"+imprintMethodValue,ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, imprintMethodValue, 
						ApplicationConstants.CONST_STRING_COPY_CHARGE, ApplicationConstants.CONST_VALUE_TYPE_OTHER, 4, priceGrids);
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
       productDaoObj.saveErrorLog(asiNumber,batchId);
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

	public ProductSizeParser getSizeParser() {
		return sizeParser;
	}

	public void setSizeParser(ProductSizeParser sizeParser) {
		this.sizeParser = sizeParser;
	}

	public ProductImprintMethodParser getImprintMethodParser() {
		return imprintMethodParser;
	}

	public void setImprintMethodParser(
			ProductImprintMethodParser imprintMethodParser) {
		this.imprintMethodParser = imprintMethodParser;
	}

	public BBIPriceGridParser getBbiPriceGridParser() {
		return bbiPriceGridParser;
	}

	public void setBbiPriceGridParser(BBIPriceGridParser bbiPriceGridParser) {
		this.bbiPriceGridParser = bbiPriceGridParser;
	}
	
}
