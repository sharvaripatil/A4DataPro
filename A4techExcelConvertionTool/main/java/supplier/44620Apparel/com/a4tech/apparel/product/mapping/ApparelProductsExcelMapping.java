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

import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.WarrantyInformation;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;


public class ApparelProductsExcelMapping {
	
	private static final Logger _LOGGER = Logger.getLogger(ApparelProductsExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	
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
		  String desciption;
		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		
		
		StringBuilder listOfQuantity = new StringBuilder();
		StringJoiner listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String productName = null;
		//List<String> productKeywords = new ArrayList<String>();
		List<Theme> themeList = new ArrayList<Theme>();
		int rowNumber ;
		List<WarrantyInformation> listOfWarrnty = new ArrayList<WarrantyInformation>();
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			rowNumber = nextRow.getRowNum();
			if(rowNumber == 0){
				
			}else if(rowNumber == 1){
				continue;
			}else{
				
			}
			/*if (nextRow.getRowNum() <2)
				continue;*/
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if(productId != null){
				productXids.add(productId);
			}
			 boolean checkXid  = false;
			
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				String xid = null;
				int columnIndex = cell.getColumnIndex();
				if(rowNumber == 0){
					if(!CommonUtility.isPriceQuantity(columnIndex+1)){
						continue;
					}
				}
				if(columnIndex + 1 == 1){
					xid = CommonUtility.getCellValueStrinOrInt(cell);
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
							    listOfQuantity = new StringBuilder();
								productConfigObj = new ProductConfigurations();
								themeList = new ArrayList<Theme>();
								productImprintMethods = new ArrayList<ImprintMethod>();
								listOfCategories = new ArrayList<String>();
								categories = new StringJoiner(ApplicationConstants.CONST_DELIMITER_COMMA);
								listOfWarrnty = new ArrayList<WarrantyInformation>();
								fullDesciption = new StringBuilder();
								
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    }
						    Cell index = nextRow.getCell(7);
						    String isNewProduct  = CommonUtility.getCellValueStrinOrInt(index);
						    if("0".equals(isNewProduct)){
						     productExcelObj = postServiceImpl.getProduct(accessToken, xid);
						     if(productExcelObj == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }
						    }else{
						    	productExcelObj = new Product();
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
				case 19:
				case 20:
				case 21:
				case 22:
					
					String priceQty = CommonUtility.getCellValueDouble(cell);
					
					
					case 23: // description
				          desciption = cell.getStringCellValue();
				          fullDesciption.append(desciption);
				   break;
				   
				case 24: //DescriptionList1 (Imprint methods)
					String imprValue = cell.getStringCellValue();
					
					break;
				case 25: //DescriptionList2 (Material)
					String materialVal = cell.getStringCellValue();
					
					break;
				case 26: //DescriptionList3
					desciption = cell.getStringCellValue();
					
					break;
				case 27: //DescriptionList4
					desciption = cell.getStringCellValue();
					
					break;
				case 28: //DescriptionList5
					desciption = cell.getStringCellValue();
					
					break;
				case 29: //DescriptionList6
					desciption = cell.getStringCellValue();
					
					   	break;
				case 30: //DescriptionList7
					desciption = cell.getStringCellValue();
					
					break;
				case 31: //DescriptionList8
					desciption = cell.getStringCellValue();
					
					break;
					
				case 32: //DescriptionList9
					desciption = cell.getStringCellValue();
					
					break;
				case 33:  //DescriptionList10
					desciption = cell.getStringCellValue();
					
					break;
				case 34: //Description1(Production Time)
					String productionTime = cell.getStringCellValue();
					
					break;
				case 35: //Description2
						// Pending 
					break;
				case 36: //Description3 (Imprint colors)
					      
					  break;
				case 37:    //Description3 (Imprint location)  
					
					break;
				case 38: //Description3 (Imprint area)
					 
					break;
				
							
			}  // end inner while loop
					 
		}
			// set  product configuration objects
			if(priceQuantities == null){
				priceQuantities = CommonUtility.getValuesOfArray(listOfQuantity.toString(), 
						                                       ApplicationConstants.CONST_DELIMITER_COMMA);
			}
			if(rowNumber != 0){
				listOfCategories = CommonUtility.getStringAsList(categories.toString(), ApplicationConstants.CONST_DELIMITER_COMMA);
				productExcelObj.setCategories(listOfCategories);
				productConfigObj.setThemes(themeList);
				productConfigObj.setImprintMethods(productImprintMethods); 
				productConfigObj.setWarranty(listOfWarrnty);
				productExcelObj.setPriceType("L");
				String qurFlag = "n"; // by default for testing purpose
				productExcelObj.setDescription(fullDesciption.toString());
				if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
					/*priceGrids = adspicPriceGridParser.getPriceGrids(listOfPrices.toString(), 
							priceQuantities, "R", "USD",
							         "", true, qurFlag, productName,"",priceGrids);	*/
				}
				
			}
			
				listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			    listOfQuantity = new StringBuilder();
			
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
}
