package com.a4tech.apparel.product.mapping;

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

import com.a4tech.apparel.product.parser.ApparealAvailabilityParser;
import com.a4tech.apparel.product.parser.ApparelMaterialParser;
import com.a4tech.apparel.product.parser.ApparelPriceGridParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;


public class ApparelProductsExcelMapping {
	
	private static final Logger _LOGGER = Logger.getLogger(ApparelProductsExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private ApparelPriceGridParser apparelPgParser;
	private ApparelMaterialParser apparealMaterialParser;
	private ApparealAvailabilityParser apparealAvailParser;

	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  String productId = null;
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  List<ImprintMethod> productImprintMethods = null;
		  List<String> listOfCategories = null;
		  StringJoiner categories = new StringJoiner(ApplicationConstants.CONST_DELIMITER_COMMA);
		  String[] priceQuantities = null;
		  StringBuilder fullDesciption = new StringBuilder();
		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		
		StringJoiner listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String productName = null;
		int rowNumber ;
		boolean isRepeateRow = false;
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			rowNumber = nextRow.getRowNum();
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
					xid = getProductXid(nextRow);
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 2){
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
								productConfigObj = new ProductConfigurations();
								productImprintMethods = new ArrayList<ImprintMethod>();
								listOfCategories = new ArrayList<String>();
								categories = new StringJoiner(ApplicationConstants.CONST_DELIMITER_COMMA);
								fullDesciption = new StringBuilder();
								
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    }
     						 productExcelObj = postServiceImpl.getProduct(accessToken, xid);
						     if(productExcelObj == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }
							
					 }else{ // skip cases except colors and sizes for repeatedly
						 if(columnIndex+1 != 6 && columnIndex+1 != 8){
							 continue;
						 }
					 }
				}
				

				switch (columnIndex+1) {
				case 1://ExternalProductID
					productId = CommonUtility.getCellValueStrinOrInt(cell);
					productExcelObj.setExternalProductId(productId);
					 break;
				case 2://Catalog Page
					  String catalogPageNo = CommonUtility.getCellValueStrinOrInt(cell);
					 
					  break;
				case 3://Product Name
					 productName = CommonUtility.getCellValueStrinOrInt(cell);
					productExcelObj.setName(productName);	
				    break;
				case 4://Category
					String category = cell.getStringCellValue();
					
				    break;
					
				case 5://Category 2
					String category2 = cell.getStringCellValue();
					
					break;
					
				case 6: // Category 3
					String category3 = cell.getStringCellValue();
					
					break;
					
				case 7://Category 4
					   String category4 = cell.getStringCellValue();
					   
					break;
					
				case 8: // NewProduct
					String isNewProduct = CommonUtility.getCellValueStrinOrInt(cell);
				
					break;
					
				case 9: //Inventory_Link
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
				case 16:
				case 17:
				case 18:
					break;
				case 19: 
				case 22:
				case 25://price Qty
					String priceQty = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(priceQty)){
						listOfQuantity.add(priceQty);
					}
					break;
					
					
				case 21: // list price
				case 24:
				case 27:
					
					String listPrice = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(listPrice)){
						listOfPrices.add(listPrice);
					}
					break;
				case 38: //Description3 (Imprint area)
					String itemWeightValue = CommonUtility.getCellValueDouble(cell);
					List<Value> listOfValue = null;
					List<Values> listOfValues = null;
					if(!StringUtils.isEmpty(itemWeightValue)){
						listOfValue = new ArrayList<>();
						listOfValues = new ArrayList<>();
						Volume volume  = new Volume();
						Values values = new Values();
						Value value = new Value();
						value.setValue(itemWeightValue);
						value.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_WEIGHT);
						listOfValue.add(value);
						values.setValue(listOfValue);
						listOfValues.add(values);
						volume.setValues(listOfValues);
						productConfigObj.setItemWeight(volume);
					}
					
					break;
				
							
			}  // end inner while loop
					 
		}
				productExcelObj.setPriceType("N");
				String qurFlag = "n"; // by default for testing purpose
				productExcelObj.setDescription(fullDesciption.toString());
				String basePriceName = "Bronze,Silver,Gold";
				if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
					priceGrids = apparelPgParser.getPriceGrids(listOfPrices.toString(), 
							priceQuantities, "P", "USD",
							         "", true, qurFlag, basePriceName,"",priceGrids);	
				}
				listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			    listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			
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
	
	public String getProductXid(Row row){
		Cell xidCell =  row.getCell(2);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid)){
		     xidCell = row.getCell(3);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
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

	public ApparelPriceGridParser getApparelPgParser() {
		return apparelPgParser;
	}

	public void setApparelPgParser(ApparelPriceGridParser apparelPgParser) {
		this.apparelPgParser = apparelPgParser;
	}
	public ApparelMaterialParser getApparealMaterialParser() {
		return apparealMaterialParser;
	}

	public void setApparealMaterialParser(
			ApparelMaterialParser apparealMaterialParser) {
		this.apparealMaterialParser = apparealMaterialParser;
	}

	public ApparealAvailabilityParser getApparealAvailParser() {
		return apparealAvailParser;
	}

	public void setApparealAvailParser(
			ApparealAvailabilityParser apparealAvailParser) {
		this.apparealAvailParser = apparealAvailParser;
	}
}
