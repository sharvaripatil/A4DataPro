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
import org.springframework.util.StringUtils;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Volume;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.dacasso.DacassoAttributeParser;
import parser.dacasso.DacassoPriceGridParser;

public class SunScopeMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(SunScopeMapping.class);
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private DacassoPriceGridParser dacassoPriceGridParser;
	private DacassoAttributeParser dacassoAttributeParser;
	
	@Override
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		int columnIndex=0;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
 		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		//String priceInclude = null;
		String xid = null;
		 columnIndex = 0;
		StringBuilder productDescription = new StringBuilder();
		String priceVal ="";
		//StringBuilder shippingValues = new StringBuilder();
		StringBuilder shippingAdditionalInfo = new StringBuilder();
		String shippingNoOfItems = "";
		String shippingDimension = "";
		String shippingWeight    = "";
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if(nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO){
				continue;
			}
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if(xid != null){
				productXids.add(xid);
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
							    productExcelObj.setAdditionalShippingInfo(shippingAdditionalInfo.toString());
							    productExcelObj.setDescription(productDescription.toString());
							 	productExcelObj.setPriceGrids(priceGrids);
							 	productExcelObj.setProductConfigurations(productConfigObj);
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
								productConfigObj = new ProductConfigurations();
								productDescription = new StringBuilder();
								shippingAdditionalInfo = new StringBuilder();
								shippingNoOfItems = "";
								shippingDimension = "";
								shippingWeight    = "";
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    }
						    productExcelObj = new Product();
     						 productExcelObj = postServiceImpl.getProduct(accessToken, xid, environmentType);
						     if(productExcelObj == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						    	 productExcelObj = dacassoAttributeParser.keepExistingProductData(productExcelObj);
						    	 productConfigObj=productExcelObj.getProductConfigurations();
						    	 if(productConfigObj == null){
						    		 productConfigObj = new ProductConfigurations();
						    	 }
						     }	
					 }
				}
				switch (columnIndex+1) {
				case 1://xid
					productExcelObj.setExternalProductId(xid);
					 break;
				case 2: 
					
					  break;
				case 3:
				
					  break;
				case 4: 
					 break;
				case 5:
					break;
				case 6://
				    break;
				case 7:
					  break;
				case 8:
					break;
				case 9:
					break;
				case 10:
					break;
				case 11://color
					break;
				case 12: 
					break;
				case 13:
					break;

				case 14:	
					break;
					
				case 15: 
					break;
					
				case 16:	
					break;
				case 17://Interior Lining
					
					  break;
				
				case 18: 
					
					break;
				
				 case 19:
					break;
					
				case 20: 
					break;
					
				case 21: 
					break;
					
				case 22:
					break;
				case 23:
				   break;
					
				case 24: 
				   break;
				   
				case 25: 
					break;
				case 26:
					break;
							
			}  // end inner while loop		 
		}
				productExcelObj.setPriceType("L");
				if(!StringUtils.isEmpty(priceVal)){
					priceGrids = dacassoPriceGridParser.getBasePriceGrid(priceVal,"1","P", "USD",
							         "", true, false, "","",priceGrids,"","");	
				}
					ShippingEstimate shippingEstimateValues = dacassoAttributeParser
								.getProductShippingEstimates(shippingNoOfItems, shippingDimension, shippingWeight);
					 productConfigObj.setShippingEstimates(shippingEstimateValues);
				
			}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);		 
				ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: "
				+e.getMessage()+" at column number(increament by 1)"+columnIndex);
				productDaoObj.save(apiResponse.getErrors(),
						productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);
		}
		}
		workbook.close();
		productExcelObj.setAdditionalShippingInfo(shippingAdditionalInfo.toString());
		productExcelObj.setDescription(productDescription.toString());
        productExcelObj.setProductConfigurations(productConfigObj);
		 	productExcelObj.setPriceGrids(priceGrids);
		 	productExcelObj.setProductConfigurations(productConfigObj);
	
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId, environmentType);
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
	public List<Material> getExistingProductMaterials(Product existingProduct){
		ProductConfigurations configuration = existingProduct.getProductConfigurations();
		return configuration.getMaterials();
	}
	private String getProductXid(Row row){
		Cell xidCell = row.getCell(ApplicationConstants.CONST_NUMBER_ZERO);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if (StringUtils.isEmpty(productXid)) {
			xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
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
	public DacassoPriceGridParser getDacassoPriceGridParser() {
		return dacassoPriceGridParser;
	}
	public void setDacassoPriceGridParser(DacassoPriceGridParser dacassoPriceGridParser) {
		this.dacassoPriceGridParser = dacassoPriceGridParser;
	}
	public DacassoAttributeParser getDacassoAttributeParser() {
		return dacassoAttributeParser;
	}
	public void setDacassoAttributeParser(DacassoAttributeParser dacassoAttributeParser) {
		this.dacassoAttributeParser = dacassoAttributeParser;
	}
}
