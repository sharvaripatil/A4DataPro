package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.core.model.ErrorMessageList;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.service.imple.PostServiceImpl;
import com.a4tech.supplier.dataStore.SupplierDataStore;
import com.a4tech.supplier.service.ISupplierParser;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.alfa.AlfaAttributeParser;

public class AlfaMapping implements ISupplierParser{
	
	private static final Logger _LOGGER = Logger.getLogger(AlfaMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private AlfaAttributeParser alfaAttributeParser;
	
	@Override
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  Set<String> listOfColors = new HashSet<>();
 		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		
		StringJoiner listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String xid = null;
		int columnIndex=0;
		 Map<String, String> sizeBasePrice = new LinkedHashMap<>();
		 StringBuilder sizePrices = new StringBuilder();
		// String listPrice = "";
		// String priceQty  = "";
		 String finalSizes = "";
		 String sizeValue  = "";
		 Set<String> setSizes = new HashSet<>();
		 String skuColorVal = "";
		 String upcCode = "";
		 List<ProductSkus> listProductSkus = new ArrayList<>();
		 String productNo = "";
         List<String> uniqueColumnValues = new ArrayList<>();
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if(nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO){
				continue;
			}
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if(xid != null){
				productXids.add(xid);
				//repeatRows.add(xid);
			}
			 boolean checkXid  = false;
			
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				
				 columnIndex = cell.getColumnIndex();
				if(columnIndex + 1 == 1){
					xid = getProductXid(nextRow);
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							 System.out.println("Java object converted to JSON String, written to file");
							 
							 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId, environmentType);
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
								uniqueColumnValues = new ArrayList<>();
								
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    	//repeatRows.add(xid);
						    }
						    productExcelObj = new Product();
     						 productExcelObj = postServiceImpl.getProduct(accessToken, xid, environmentType);
						     if(productExcelObj == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						    	productExcelObj = alfaAttributeParser.getExistingProductData(productExcelObj);
						    	 productConfigObj=productExcelObj.getProductConfigurations();
						    	 productExcelObj.setAvailability(new ArrayList<>());
						    	 priceGrids = productExcelObj.getPriceGrids();
						     }	
					 }
				}else{
					/*if(productXids.contains(xid) && repeatRows.size() != 1){
						 if(isRepeateColumn(columnIndex+1)){
							 continue;
						 }
					}*/
				}
				
				switch (columnIndex+1) {
				case 1://xid
					productExcelObj.setExternalProductId(xid);
					 break;
				case 2:// ASI Prd No
					  productExcelObj.setAsiProdNo(CommonUtility.getCellValueStrinOrInt(cell));
					  break;
				case 3:// db
					//ignore
				    break;
				case 4://html
				//ignore
				    break;
				case 5://inventory_http
					//ignore
					break;
					
				case 6://item_name
					String prdName = cell.getStringCellValue();
					productExcelObj.setName(prdName);
					break;
					
				case 7://lg_image
					//ignore
					break;
					
				case 8: // price_table
					 String prices = cell.getStringCellValue();
					
					break;
					
				case 9: // shipping_http
					//ignore
					break;
				case 10: // sm_image
				    //ignore			
					break;
				case 11://txt
				    //ignore
					break;
					
				case 12://unique_1
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
				case 23:
				case 24:
				case 25:
				case 26: 
				case 27:
				case 28:
				case 29: 
				case 30:
				case 31:
				case 32:
				case 33:
				case 34:
				case 35: //unique_24
					String value = cell.getStringCellValue();
					if(!StringUtils.isEmpty(value)){
						uniqueColumnValues.add(value);
					}
					
				 break;		
			}  // end inner while loop
					 
		}
				productExcelObj.setPriceType("N");
				String qurFlag = "n"; // by default for testing purpose
				//String basePriceName = "Bronze,Silver,Gold";
				
				
				listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			    listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);		 
				ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: "
				+e.getMessage()+" at column number(increament by 1)"+columnIndex);
				productDaoObj.save(apiResponse.getErrors(),
						productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);
		}
		}
		workbook.close();
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId, environmentType);
		 	if(num ==1){
		 		numOfProductsSuccess.add("1");
		 	}else if(num == 0){
		 		numOfProductsFailure.add("0");
		 	}else{
		 		
		 	}
		 	SupplierDataStore.clearProductColorSet();
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
		Cell xidCell =  row.getCell(ApplicationConstants.CONST_NUMBER_ZERO);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid)){
		     xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}
	
	public Size getProductSize(List<Value> sizeValues){
		Size size = new Size();
		Apparel appareal = new Apparel();
		appareal.setValues(sizeValues);
		appareal.setType(ApplicationConstants.SIZE_TYPE_STANDARD_AND_NUMBERED);
		size.setApparel(appareal);
		return size;
	}
	
	public boolean isRepeateColumn(int columnIndex){
		if(columnIndex != 5 && columnIndex != 6 && columnIndex != 8 && columnIndex != 10 
				                                &&!(columnIndex >= 19 && columnIndex <= 27)){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	
	public List<Material> getExistingProductMaterials(Product existingProduct){
		ProductConfigurations configuration = existingProduct.getProductConfigurations();
		return configuration.getMaterials();
	}
    private List<Image> getProductImages(List<Image> images){
    	  if(!CollectionUtils.isEmpty(images)){
    		  return images;
    	  } else{
    		 return new ArrayList<>();
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
	public AlfaAttributeParser getAlfaAttributeParser() {
		return alfaAttributeParser;
	}

	public void setAlfaAttributeParser(AlfaAttributeParser alfaAttributeParser) {
		this.alfaAttributeParser = alfaAttributeParser;
	}
	
}
