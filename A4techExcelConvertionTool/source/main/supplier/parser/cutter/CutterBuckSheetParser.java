package parser.cutter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.supplier.mapper.CutterBuckExcelMapping;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class CutterBuckSheetParser /* implements IExcelParser */{
	private static final Logger _LOGGER = Logger
			.getLogger(CutterBuckSheetParser.class);

	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private CBColorProductNumberParser cbColorProductNumberObj;

	public String readMapper(String accessToken, Workbook workbook,
			Integer asiNumber, int batchId, HashMap<String, Product> SheetMap, HashMap<String, String> productNoMap) {

		CutterBuckExcelMapping cutterBuckObj = new CutterBuckExcelMapping();
	
		  
		  
		int columnIndex = 0;

		Set<String> productXids = new HashSet<String>();
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
	    Set<String> colorSet = new HashSet<String>(); 
		List<String> repeatRows = new ArrayList<>();
		ProductConfigurations productConfigObj = new ProductConfigurations();
		Product productExcelObj = new Product();
		String finalResult = null;
		String productId = null;
		HashMap<String, String> productNumberMap = new HashMap<String, String>();
		List<ProductNumber> pnumberList = new ArrayList<ProductNumber>();
	    List<Color> colorList = new ArrayList<Color>();
	    List<String> XidList = new ArrayList<String>();
		String xid = null;
		String colorValue =null;
		String productNumber=null;
		 String prdXid =null;

		try {

			_LOGGER.info("Total sheets in excel::"
					+ workbook.getNumberOfSheets());
		int NumberOfSheet =workbook.getNumberOfSheets();
		Sheet sheet = workbook.getSheetAt(1);
		Iterator<Row> iterator = sheet.iterator();
		
		while (iterator.hasNext()) {
		
			try {
				Row nextRow = iterator.next();
               
           
				if (nextRow.getRowNum() < 4)
					continue;
				 Cell cell1 =  nextRow.getCell(0);
	              prdXid = cell1.getStringCellValue();
	                if(!XidList.contains(prdXid))
	                {
	                   productExcelObj=SheetMap.get(prdXid);
	    			   productConfigObj=productExcelObj.getProductConfigurations();
	                }  
	                
	                
	            
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				if (productId != null) {
					productXids.add(productId);
				}
				boolean checkXid = false;
				
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
				
					 columnIndex = cell.getColumnIndex();

					if (columnIndex + 1 == 1) {
						xid = CommonUtility.getCellValueStrinOrInt(cell);//getProductXid(nextRow);
						xid=xid.trim();
						
						checkXid = true;
					} else {
						checkXid = false;
					}
					
					if (checkXid) {
						if (!productXids.contains(xid)) {
						if(nextRow.getRowNum() != 1){
								
							if(!CollectionUtils.isEmpty(colorList)){
									productConfigObj.setColors(colorList);
								}
								if(!CollectionUtils.isEmpty(pnumberList)){
								productExcelObj.setProductNumbers(pnumberList);
								}
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
						 	repeatRows.clear();
						   colorList = new ArrayList<Color>();
						 	pnumberList =  new ArrayList<ProductNumber>();
							productConfigObj = new ProductConfigurations();
						 	
						 
						 	
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
					}
							switch (columnIndex + 1) {

							case 1:// XID
						   productId = CommonUtility.getCellValueStrinOrInt(cell);
							   productExcelObj.setExternalProductId(productId);	
							
								break;

							case 2:// MaterialNumber
								 productNumber=CommonUtility.getCellValueStrinOrInt(cell);
								break;

							case 3:// ColorName
								
								  colorValue = CommonUtility.getCellValueStrinOrInt(cell);
								 if(!StringUtils.isEmpty(colorValue)){
									colorSet.add(colorValue);
								}
								colorList=cbColorProductNumberObj.getColorCriteria(colorSet);
								productConfigObj.setColors(colorList);

								
								if(!StringUtils.isEmpty(colorValue)&&!StringUtils.isEmpty(productNumber)){
									productNumberMap.put(productNumber, colorValue);
								}
								pnumberList=cbColorProductNumberObj.getProductNumer(productNumberMap);
								productExcelObj.setProductNumbers(pnumberList);


								break;

							} // end inner while loop

						}
				
			   //  productId = null;

			} catch (Exception e) {
				_LOGGER.error("Error while Processing ProductId and cause :"
						+ productExcelObj.getExternalProductId()
						+ " "
						+ e.getMessage() +"for column"+columnIndex+1);
			}
		
		 	XidList.add(prdXid);
		 	
			if(!CollectionUtils.isEmpty(colorList)){
				productConfigObj.setColors(colorList);
			}
			if(!CollectionUtils.isEmpty(pnumberList)){
			productExcelObj.setProductNumbers(pnumberList);
			}
		     productExcelObj.setProductConfigurations(productConfigObj);
			
		
		}
	
		workbook.close();
	
	
		
		int num = postServiceImpl.postProduct(accessToken, productExcelObj,
				asiNumber, batchId);
		_LOGGER.info("list size>>>>>>" + numOfProductsSuccess.size());
		_LOGGER.info("Failure list size>>>>>>"
				+ numOfProductsFailure.size());
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
  

  

	public CBColorProductNumberParser getCbColorProductNumberObj() {
		return cbColorProductNumberObj;
	}

	public void setCbColorProductNumberObj(
			CBColorProductNumberParser cbColorProductNumberObj) {
		this.cbColorProductNumberObj = cbColorProductNumberObj;
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
