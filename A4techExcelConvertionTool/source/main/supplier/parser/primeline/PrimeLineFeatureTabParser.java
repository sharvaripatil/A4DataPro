package parser.primeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PrimeLineFeatureTabParser {


	private static final Logger _LOGGER = Logger.getLogger(PrimeLineFeatureTabParser.class);
	private ProductDao productDaoObj;
	private PrimeLineAttributeParser primeLineAttriObj;
	@Autowired
	ObjectMapper mapperObj;
	
	public HashMap<String, Product> readFeatureTab(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId,
			HashMap<String, Product> sheetMap){
		HashMap<String, Product> sheetMapReturn=new HashMap<String, Product>();
		//HashMap<String, ArrayList<String>> featureMap=new HashMap<String,ArrayList<String>>();
		Product existingApiProduct = null;
		Set<String>  productXids = new HashSet<String>();
		List<String> repeatRows = new ArrayList<>();
		  
		try{
		Product	productExcelObj=new Product();
		ProductConfigurations productConfigObj=new ProductConfigurations();
		Sheet sheet = workbook.getSheetAt(2);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
	    String productId = null;
	    String xid = null;
	    int columnIndex=0;
	    String detailTypeValue=null;
	    String detailValue=null;
	    while (iterator.hasNext()) {
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum() == 0)
			{
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
				
			     columnIndex = cell.getColumnIndex();
				
				if(columnIndex + 1 == 1){
					xid = getProductXid(nextRow);//CommonUtility.getCellValueStrinOrInt(cell);//
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							 //int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId);
							 //process all the detail type values over here send here
							 //for the primeline attribute parser processing  based on the detailvalue type
							 //if(!StringUtils.isEmpty(detailTypeValue) && !StringUtils.isEmpty(detailValue)){
							 //productExcelObj=primeLineAttriObj.featureCritriaparser(productExcelObj,productConfigObj,detailTypeValue,detailValue);
							 _LOGGER.info("Product Data from sheet 3 feature tab: "+ mapperObj.writeValueAsString(productExcelObj));
							 sheetMapReturn.put(productId, productExcelObj);
							 //}
								//productConfigObj = new ProductConfigurations();
								repeatRows.clear();
								
								detailTypeValue=null;
								detailValue=null;
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    	repeatRows.add(xid);
						    }
						    existingApiProduct=sheetMap.get(xid);
						     if(existingApiProduct == null){
						    	 _LOGGER.info("Existing Xid is not available in Map,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						    	    productExcelObj=existingApiProduct;
									productConfigObj=productExcelObj.getProductConfigurations();
									if(productConfigObj==null){
										productConfigObj=new ProductConfigurations();
									}
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
					    case 1://XID
						//productId=xid;//CommonUtility.getCellValueStrinOrInt(cell);
						productExcelObj.setExternalProductId(xid);
						productId=xid;
						break;
						case 2://ITEMID
							
						break;
						case 3://DETAILTYPE
							detailTypeValue=CommonUtility.getCellValueStrinOrInt(cell);
							break;

						case 4://DETAIL
							 detailValue=CommonUtility.getCellValueStrinOrInt(cell);
							 if(!StringUtils.isEmpty(detailTypeValue) && !StringUtils.isEmpty(detailValue)){
							 productExcelObj=primeLineAttriObj.featureCritriaparser(productExcelObj,productConfigObj,detailTypeValue,detailValue);
							 }
							break;
				}  // end inner while loop					 
			}		
			}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);		 
				ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: "
				+e.getMessage()+" at column number(increament by 1)"+columnIndex);
				productDaoObj.save(apiResponse.getErrors(),
						productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);
				}
		}
		workbook.close();
		
		// productConfigObj.setColors(colorList);
		//if(!StringUtils.isEmpty(detailTypeValue) && !StringUtils.isEmpty(detailValue)){
			 //productExcelObj=primeLineAttriObj.featureCritriaparser(productExcelObj,productConfigObj,detailTypeValue,detailValue);
			 _LOGGER.info("Product Data from sheet 3 feature tab: "+ mapperObj.writeValueAsString(productExcelObj));
			 sheetMapReturn.put(productId, productExcelObj);
			 //}
				//productConfigObj = new ProductConfigurations();
		repeatRows.clear();
		detailTypeValue=null;
		detailValue=null;
		productDaoObj.saveErrorLog(asiNumber,batchId);
		repeatRows.clear();
		detailTypeValue=null;
		//colorList = new ArrayList<Color>();
		return sheetMapReturn;
		}catch(Exception e){
			_LOGGER.error("Error while Processing excel sheet " +e.getMessage());
			return sheetMapReturn;
		}finally{
			try {
				workbook.close();
			} catch (IOException e) { 
				_LOGGER.error("Error while Processing excel sheet" +e.getMessage());
			}
				_LOGGER.info("Complted processing of excel sheet ");
				
		}
	}
	
	public String getProductXid(Row row){
		Cell xidCell =  row.getCell(0);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid) || productXid.trim().equalsIgnoreCase("#N/A")){
		     xidCell = row.getCell(1);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}
	
	public boolean isRepeateColumn(int columnIndex){
		if(columnIndex != 1 && columnIndex != 2 && columnIndex != 3 && columnIndex != 4){
		//if(columnIndex != 1&&columnIndex != 3&&columnIndex != 4 && columnIndex != 6 && columnIndex != 9 && columnIndex != 24){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
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
	
	public PrimeLineAttributeParser getPrimeLineAttriObj() {
		return primeLineAttriObj;
	}

	public void setPrimeLineAttriObj(PrimeLineAttributeParser primeLineAttriObj) {
		this.primeLineAttriObj = primeLineAttriObj;
	}

	public static String removeSpecialChar(String tempValue){
		tempValue=tempValue.replaceAll("(CLASS|Day|DAYS|Service|Days|Hour|Hours|Week|Weeks|Rush|day|service|days|hour|hours|week|weeks|Rush|R|u|s|h|$)", "");
		tempValue=tempValue.replaceAll("\\(","");
		tempValue=tempValue.replaceAll("\\)","");
	return tempValue;

	}
	private boolean isComboColors(String value) {
		boolean result = false;
		if (value.contains("/")) {
			result = true;
		}
		return result;
	}
}
