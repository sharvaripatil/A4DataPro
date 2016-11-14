package com.a4tech.product.broberry.mapping;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.broberry.parser.BroberryProductAttributeParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BroberryExcelMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(BroberryExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	@Autowired
	ObjectMapper mapperObj;
	private BroberryProductAttributeParser broberryProductAttributeParser;
	
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
	
		String productName = null;
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  Product existingApiProduct = new Product();
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  Set<String> listOfColors = new HashSet<>();
		  String colorCustomerOderCode ="";
		  List<String> repeatRows = new ArrayList<>();
		  Map<String, String> colorIdMap = new HashMap<>();
		  String size=null;
		  String dimension=null;
		  String upc_no;
		  Set<String> colorSet = new HashSet<String>(); 
		  List<Color> colorList = new ArrayList<Color>();
		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		String currentValue=null;
	    String lastValue=null;
	    String productId = null;
	    String xid = null;
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if(nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO){
				continue;
			}
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if(xid != null){
				productXids.add(xid);
				repeatRows.add(xid);
			}
			 boolean checkXid  = false;
			
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				
				int columnIndex = cell.getColumnIndex();
				if(columnIndex + 1 == 1){
					xid = CommonUtility.getCellValueStrinOrInt(cell);//getProductXid(nextRow);
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							colorList=broberryProductAttributeParser.getColorCriteria(colorSet);
								if(colorList!=null && !colorList.isEmpty()){
								productConfigObj.setColors(colorList);
								}
								
								 	productExcelObj.setProductConfigurations(productConfigObj);
								 	_LOGGER.info("Product Data : "
											+ mapperObj.writeValueAsString(productExcelObj));
								 	
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
								listOfColors = new HashSet<>();
								repeatRows.clear();
								colorSet=new HashSet<String>(); 
								colorList = new ArrayList<Color>();
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    	repeatRows.add(xid);
						    }
						    productExcelObj = new Product();
						    existingApiProduct = postServiceImpl.getProduct(accessToken, xid); 
						     if(existingApiProduct == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						    	    productExcelObj=existingApiProduct;
									productConfigObj=productExcelObj.getProductConfigurations();
								    priceGrids = productExcelObj.getPriceGrids();
						     }
							
					 }
				}else{
					if(productXids.contains(xid) && repeatRows.size() != 1){
						 if(isRepeateColumn(columnIndex+1)){
							 continue;
						 }
					}
				}
		
				switch (columnIndex + 1) {
					case 1:
						productId=CommonUtility.getCellValueStrinOrInt(cell);
						productExcelObj.setExternalProductId(productId);
						break;
			
				case 2:// Material
					  break;
				case 3://PRODUCT NAME
					
				    break;
				case 4://DESCRIPTION
					
				    break;
					
				case 5://COLOR
					String	colorValue=cell.getStringCellValue();
					if(!StringUtils.isEmpty(colorValue)){
						colorSet.add(colorValue);
					}
					break;
					
				case 6: //  DIMENSION
					break;
					
				case 7://SIZES
					break;
					
				case 8: // UPC NO
					break;
					
				case 9: //SUGG RETL
					
					break;
				case 10: // MSAP
				
					
					break;
				case 11://DEPT
				   
					break;
					
				case 12://CATEGORY....product category
					
					break;
				case 13://SUB CATEGORY
					
					 break;
				case 14://FABRIC WT
					
					
			     break;
				case 15://FABRIC CONTENT
				
					break;
				case 16://FABRICATION
			
					break;
				case 17://LSW
				
					break;
				case 18://SILHOUTTE
					
					break;
				case 19: //SUB DEPT
					String Keyword1 = cell.getStringCellValue();

					
					break;
				
				case 20: //NECKLINE
					
					break;
				case 21: //DESIGN
					String Keyword2 = cell.getStringCellValue();

					
					
					break;
				case 22: //SEASONALITY
	
					break;
				case 23: //PLF
	
					break;
	
				case 24: //CARE INSTNS
	
					break;
				case 25: //LONG DESC
					productName = cell.getStringCellValue();
					int len=productName.length();
					 if(len>60){
						String strTemp=productName.substring(0, 60);
						int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
						productName=(String) strTemp.subSequence(0, lenTemp);
					}
					productExcelObj.setName(productName);
					 
	
					break;
				case 26: //CONS COPY
	
					String description = cell.getStringCellValue();
					productExcelObj.setDescription(description);
					
					break;
				case 27: //SEARCH KEY
					String AdditionInfo = cell.getStringCellValue();

					
	
					break;
				case 28: //LOGISTICS
					//String description = cell.getStringCellValue();

	
					break;
				case 29: //FIT LENG
					//String description = cell.getStringCellValue();

					break;
				case 30: //RETAIL COPY
					//String description = cell.getStringCellValue();

					break;
				case 31: //LINING
					//String description = cell.getStringCellValue();

					break;
				}  // end inner while loop
					 
		}
				productExcelObj.setPriceType("N");
			
				
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() );		 
		}
		}
		workbook.close();
		colorList=broberryProductAttributeParser.getColorCriteria(colorSet);
		if(colorList!=null && !colorList.isEmpty()){
		productConfigObj.setColors(colorList);
		}
			productExcelObj.setProductConfigurations(productConfigObj);
		 	productExcelObj.setProductConfigurations(productConfigObj);
		 	_LOGGER.info("Product Data : "
					+ mapperObj.writeValueAsString(productExcelObj));
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
		priceGrids = new ArrayList<PriceGrid>();
		productConfigObj = new ProductConfigurations();
		listOfColors = new HashSet<>();
		repeatRows.clear();
		colorSet=new HashSet<String>(); 
		colorList = new ArrayList<Color>();
		
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
		Cell xidCell =  row.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid)){
		     xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_TWO);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}
	
	public boolean isRepeateColumn(int columnIndex){
		
		if(columnIndex != 1&&columnIndex != 2&&columnIndex != 5 && columnIndex != 6 && columnIndex != 7){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
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
	
	public static final String CONST_STRING_COMBO_TEXT = "Combo";
	
	public ObjectMapper getMapperObj() {
		return mapperObj;
	}



	public void setMapperObj(ObjectMapper mapperObj) {
		this.mapperObj = mapperObj;
	}



	public BroberryProductAttributeParser getBroberryProductAttributeParser() {
		return broberryProductAttributeParser;
	}



	public void setBroberryProductAttributeParser(
			BroberryProductAttributeParser broberryProductAttributeParser) {
		this.broberryProductAttributeParser = broberryProductAttributeParser;
	}
	
	
}
