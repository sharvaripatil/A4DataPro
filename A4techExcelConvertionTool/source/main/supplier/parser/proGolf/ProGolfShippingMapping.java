package parser.proGolf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Volume;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class ProGolfShippingMapping{
	private static final Logger _LOGGER = Logger
			.getLogger(ProGolfShippingMapping.class);

	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private ProGolfInformationAttributeParser proGolfAttributeParser;
	public String readMapper(String accessToken, Workbook workbook,
			Integer asiNumber, int batchId, HashMap<String, Product> SheetMap, HashMap<String, String> productNoMap) {

		int columnIndex = 0;

		Set<String> productXids = new HashSet<String>();
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		List<String> repeatRows = new ArrayList<>();
		ProductConfigurations productConfigObj = new ProductConfigurations();
		Product productExcelObj = new Product();
		String finalResult = null;
		String productId = null;
	    List<String> XidList = new ArrayList<String>();
		String xid = null;
		String productNumPrevSheet=null;

		String prdXid =null;
		String headerName =  "";
		try {
			Row  headerRow = null;
			_LOGGER.info("Total sheets in excel::"
					+ workbook.getNumberOfSheets());
		Sheet sheet = workbook.getSheetAt(1);
		Iterator<Row> iterator = sheet.iterator();
		StringJoiner shippingEstamationValues = new StringJoiner(",");
		StringJoiner sizes = new StringJoiner(",");
		String shippingDimentionUnits = "";
		String shippingWeightUnit     = "";
		String productWeightVal		  = "";
		String sizeUnit				  = "";
		String shippingWeight		  = "";
		while (iterator.hasNext()) {
		
			try {
				Row nextRow = iterator.next();
              
				if (nextRow.getRowNum() < 4){
					headerRow = nextRow;
					continue;
				}
				 Cell cell1 =  nextRow.getCell(0);
	              prdXid =CommonUtility.getCellValueStrinOrInt(cell1);
	        
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				if (xid != null) {
					productXids.add(xid);
				}
				boolean checkXid = false;
				
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();

					 columnIndex = cell.getColumnIndex();

					if (columnIndex + 1 == 1) {
					
						xid = CommonUtility.getCellValueStrinOrInt(cell);//getProductXid(nextRow);
						xid=xid.trim();
						if(prdXid.contains("#N/A") || prdXid.contains(""))
			              {
			            	  
			            	  Cell cell2 =  nextRow.getCell(1);  
			            	  productNumPrevSheet=CommonUtility.getCellValueStrinOrInt(cell2);
			            	  productNumPrevSheet=productNumPrevSheet.substring(0, 8);
			            	  prdXid=productNoMap.get(productNumPrevSheet);
			            	  xid = prdXid;
			              }
						
						checkXid = true;
					} else {
						checkXid = false;
					}
					
					if (checkXid) {
						if (!productXids.contains(xid)) {
						if(nextRow.getRowNum() != 1){
							     productExcelObj.setProductConfigurations(productConfigObj);
							     if(!StringUtils.isEmpty(productExcelObj.getExternalProductId())){
						 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId);
						 	if(num ==1){
						 		numOfProductsSuccess.add("1");
						 	}else if(num == 0){
						 		numOfProductsFailure.add("0");
						 	}else{
						 		
						 	}
							   }
 						 	_LOGGER.info("list size>>>>>>>"+numOfProductsSuccess.size());
						 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
						 	repeatRows.clear();
						 	
							}
							if (!productXids.contains(xid)) {
								productXids.add(xid);
						    	repeatRows.add(xid);
							}	
							 
						 }
				     	}else{

						if(productXids.contains(xid) && repeatRows.size() != 1){
							 if(isRepeateColumn(columnIndex+1)){
								 continue;
							 }
						}
						
						if(!XidList.contains(prdXid))
		                {
//		                   productExcelObj=SheetMap.get(prdXid);
//		    			   productConfigObj=productExcelObj.getProductConfigurations();
			             	
							
							if(SheetMap.containsKey(xid)){//this step is to get product obj of sheet1 if exists in map
						    	
								  productExcelObj=SheetMap.get(xid);// all data including its producconfiguration
								  productConfigObj=productExcelObj.getProductConfigurations();
							    	
							    }// else create new producct and check existing data for it -getproduct,put in map
							 else{
								 Product existingApiProduct =null;
								 existingApiProduct = postServiceImpl.getProduct(accessToken, xid); 
								  /* if(existingApiProduct == null){
								    	 productExcelObj = new Product();
								    	 productExcelObj.setExternalProductId(xid);	
										 SheetMap.put(xid, productExcelObj);
								     }else{*/
								     productExcelObj=existingApiProduct;
									 productConfigObj=productExcelObj.getProductConfigurations();
									 productExcelObj.setExternalProductId(xid);	
									 SheetMap.put(xid, productExcelObj);
											
								  //  }
							 }
							 shippingEstamationValues = new StringJoiner(",");
							 sizes = new StringJoiner(",");
							 shippingDimentionUnits = "";
							 shippingWeightUnit    = "";
							 productWeightVal = "";
							 XidList.add(prdXid);
		                }  
					
				     	}
					headerName = getHeaderName(columnIndex, headerRow);
							switch (headerName) {

							case "Free_On_Board":
								String fobPointValue = cell.getStringCellValue();
								List<FOBPoint> listOfFobPoint =  proGolfAttributeParser.getFobPoint(fobPointValue, accessToken);
								if(!CollectionUtils.isEmpty(listOfFobPoint)){
									productExcelObj.setFobPoints(listOfFobPoint);
								}
								break;
							case "Shipping_Qty_per_Carton": // shipping
							case "Carton_LENGTH":
							case "Carton_WIDTH":
							case "Carton_HEIGHT":
							      String dimensionValues = cell.getStringCellValue(); 
							      shippingEstamationValues.add(dimensionValues);
								break;
							case "Product_LENGTH": // sizes
							case "Product_WIDTH":
							case "Product_HEIGHT":
								String sizeValue = cell.getStringCellValue();
								sizes.add(sizeValue);
								break;
							case "Carton_Weight":// shipping
								shippingWeight = cell.getStringCellValue();
								break;
							case "Product_Weight":
								 productWeightVal = cell.getStringCellValue();
								break;
							case "Carton_Size_Unit": // shipping
								shippingDimentionUnits = cell.getStringCellValue();
								break;
							case "Carton_Weight_Unit":// shipping
								shippingWeightUnit = cell.getStringCellValue();
								break;
							case "Product_Size_Unit":
								sizeUnit = cell.getStringCellValue();
								break;
							case "Product_Weight_Unit": // item weight
								String productWeightUnit = cell.getStringCellValue();
								if(!StringUtils.isEmpty(productWeightVal)){
								Volume itemWeight = proGolfAttributeParser.getProductItemWeight(productWeightVal,
										productWeightUnit);
								productConfigObj.setItemWeight(itemWeight);
								}
								break;

							} // end inner while loop

						}
					ShippingEstimate shippingEstimObj = proGolfAttributeParser.getProductShippingEstimation(
							shippingEstamationValues.toString(), shippingWeight, shippingDimentionUnits,
							shippingWeightUnit);
			} catch(Exception e){
			    _LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);   
			    ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: "
			    +e.getMessage()+" at column number(increament by 1)"+columnIndex);
			    productDaoObj.save(apiResponse.getErrors(),
			      productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);
			    }	 	
		}
		workbook.close();
	     productExcelObj.setProductConfigurations(productConfigObj);
	
	     if(!StringUtils.isEmpty(productExcelObj.getExternalProductId())){

	 	 int num = postServiceImpl.postProduct(accessToken, productExcelObj,
				asiNumber, batchId);
	     }

		_LOGGER.info("list size>>>>>>>"+numOfProductsSuccess.size());
	 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
	 	repeatRows.clear();
		productConfigObj = new ProductConfigurations();
		finalResult = numOfProductsSuccess.size() + "," + numOfProductsFailure.size();
	    productDaoObj.saveErrorLog(asiNumber,batchId);
 	 
		return finalResult;
	} catch (Exception e) {
		_LOGGER.error("Error while Processing excel sheet ,Error message: "
				+ e.getMessage());
		return finalResult;
	} finally {
		try {
			workbook.close();
		} catch (IOException e) {
			_LOGGER.error("Error while Processing excel sheet, Error message: "
					+ e.getMessage());

		}
		_LOGGER.info("Complted processing of excel sheet ");
		_LOGGER.info("Total no of product:" + numOfProductsSuccess.size());
	}

}
  public boolean isRepeateColumn(int columnIndex){
		
		if(columnIndex != 1){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
  private String getHeaderName(int columnIndex,Row headerRow){
		 Cell cell2 =  headerRow.getCell(columnIndex);  
		 String headerName=CommonUtility.getCellValueStrinOrInt(cell2);
		//columnIndex = ProGolfHeaderMapping.getHeaderIndex(headerName);
		return headerName;
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
	public ProGolfInformationAttributeParser getProGolfAttributeParser() {
		return proGolfAttributeParser;
	}
	public void setProGolfAttributeParser(ProGolfInformationAttributeParser proGolfAttributeParser) {
		this.proGolfAttributeParser = proGolfAttributeParser;
	}
	

}
