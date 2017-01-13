package com.a4tech.bambam.product.mapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;

import com.a4tech.bambam.product.parser.BamPriceGridParser;
import com.a4tech.bambam.product.parser.BamProductAttributeParser;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class BambamProductExcelMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(BambamProductExcelMapping.class);
	private PostServiceImpl 				postServiceImpl ;
	private ProductDao 						productDaoObj;
	private BamProductAttributeParser 		bamProductParser;
	private BamPriceGridParser 				bamPriceGridParser;
	@Override
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
 		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		
		StringJoiner listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfPrices   = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String priceInclude = null;
		String xid = null;
		String basePriceName = null;
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if(nextRow.getRowNum() < ApplicationConstants.CONST_INT_VALUE_THREE){
				continue;
			}
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if(xid != null){
				productXids.add(xid);
			}
			 boolean checkXid  = false;
			
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				
				int columnIndex = cell.getColumnIndex();
				if(columnIndex + 1 == 1){
					xid = CommonUtility.getCellValueStrinOrInt(cell);
					if(StringUtils.isEmpty(xid)){
						xid = getProductXid(nextRow);
					}
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 3){
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
								listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							    listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							    listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								productConfigObj = new ProductConfigurations();
																
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    }
						    productExcelObj = new Product();
     						 productExcelObj = postServiceImpl.getProduct(accessToken, xid);
						     if(productExcelObj == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						    	 productConfigObj=productExcelObj.getProductConfigurations();
						     }
							
					 }
				}
				
				switch (columnIndex+1) {
				case 1://xid
					  productExcelObj.setExternalProductId(xid);
					 break;
				case 2://product no
					String prdNo = cell.getStringCellValue().trim();
					productExcelObj.setAsiProdNo(prdNo);
					  break;
				case 3://name
					String prdName = cell.getStringCellValue();
					basePriceName = prdName;
					productExcelObj.setName(prdName);	
				    break;
				case 4://description
					 String desc = cell.getStringCellValue();
					 productExcelObj.setDescription(desc.trim());
					  break;
				case 5://material
					  String keyWords = cell.getStringCellValue();
					  if(!StringUtils.isEmpty(keyWords)){
					  }
					break;
					
				case 6: //  size
					
					break;
				case 7: // imprint size
					String imprintSizeVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintSizeVal)){
						List<ImprintSize> listOfImprintSize = bamProductParser.getImprintSizes(imprintSizeVal);
						productConfigObj.setImprintSize(listOfImprintSize);
					}
					break;
				case 8: // production Time
					
					break;
				case 9: // fob point
					
					break;
				case 10:// origin
					String origin = cell.getStringCellValue();
					if(!StringUtils.isEmpty(origin)){
						List<Origin> listOfOrigin = bamProductParser.getOrigins(origin);
						productConfigObj.setOrigins(listOfOrigin);
					}
					break;
				case 11://category
					String category = cell.getStringCellValue();
					if(!StringUtils.isEmpty(category)){
						List<String> listOfCategories = bamProductParser.getCategories(category);
						productExcelObj.setCategories(listOfCategories);
					}
					break;
				case 12://expiration_date
					//ignore
					break;
					
				case 13: //  quantity
				case 14:
				case 15:
				case 16: 
				case 17: 
				case 18: 
				case 19:
				case 20: 
				case 21: 
				case 22: 
					String priceQty = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(priceQty)){
						listOfQuantity.add(priceQty);
					}
					break;
					
				case 23: // price
				case 24:
				case 25:
				case 26:
				case 27:
				case 28:
				case 29:
				case 30:
				case 31:
				case 32:
					String listPrice = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(listPrice)){
						listOfPrices.add(listPrice);
					}
					break;
					
				case 33: // dis code
				case 34:
				case 35:
				case 36:
				case 37:
				case 38:
				case 39:
				case 40:
				case 41:
				case 42:
					String discount = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(discount)){
						listOfDiscount.add(discount);
					}
					break;
				case 43: 
					
					
					break;
				case 44:
					break;
					
				case 45:
					break;
					
				case 46:
					priceInclude = cell.getStringCellValue();
					 break;
				case 47:
					String color =cell.getStringCellValue();
					if(!StringUtils.isEmpty(color)){
					}
					break;
				case 48:
				String shapeValue=cell.getStringCellValue();
					if(!StringUtils.isEmpty(shapeValue)){
					  
					}
					break;
				case 49: 
				String sizeValue=cell.getStringCellValue();
				   if(!StringUtils.isEmpty(sizeValue)){
					 
				   }
					break;
				case 50: 
				String materialValue=cell.getStringCellValue();
				if(!StringUtils.isEmpty(materialValue)){
					
				}
					break;
				case 51: 
					String tradeNames = cell.getStringCellValue();
					
					break;
				case 52: 
					  String country = cell.getStringCellValue();
					     if(!StringUtils.isEmpty(country)){
					    	
					     }
					break;
				case 53:
					     String prdTime = CommonUtility.getCellValueStrinOrInt(cell);
					     if(!StringUtils.isEmpty(prdTime)){
					    	
					     }
					break;
				case 54:
					String rushValue = cell.getStringCellValue();
					if(!StringUtils.isEmpty(rushValue)){
						
					}
					break;
				case 55: 
					String imprintValue = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintValue)){
						
					}
					break;
				case 56: 
					break;
				case 57: 
					 
					break;				
							
			}  // end inner while loop
					 
		}
				productExcelObj.setPriceType("L");
				String qurFlag = "n"; // by default for testing purpose
				if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
					/*priceGrids = bamPriceGridParser.getPriceGrids(listOfPrices.toString(), 
							listOfQuantity.toString(), listOfDiscount.toString(), "USD",
							         priceInclude, true, qurFlag, basePriceName,"",priceGrids);	*/
				}
				listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			    listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			    listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() );		 
		}
		}
		workbook.close();
         productExcelObj.setPriceGrids(priceGrids);
        productExcelObj.setProductConfigurations(productConfigObj);
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

	private String getProductXid(Row row){
		Cell xidCell =  row.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		return productXid;
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
	public BamProductAttributeParser getBamProductParser() {
		return bamProductParser;
	}

	public void setBamProductParser(BamProductAttributeParser bamProductParser) {
		this.bamProductParser = bamProductParser;
	}
	public BamPriceGridParser getBamPriceGridParser() {
		return bamPriceGridParser;
	}

	public void setBamPriceGridParser(BamPriceGridParser bamPriceGridParser) {
		this.bamPriceGridParser = bamPriceGridParser;
	}

}
