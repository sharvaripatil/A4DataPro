package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
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

import parser.tomaxusa.TomaxUsaAttributeParser;
import parser.tomaxusa.TomaxUsaPricegridParser;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Price;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TomaxUsaMapping implements IExcelParser{
	private static final Logger _LOGGER = Logger.getLogger(TomaxUsaMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	@Autowired
	ObjectMapper mapperObj;
	
	TomaxUsaAttributeParser tomaxUsaAttributeParser;
	TomaxUsaPricegridParser tomaxUsaPricegridParser;
	
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
	
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		ArrayList<String>  xidSet = new ArrayList<String>();
		List<String> repeatRows = new ArrayList<>();
		HashSet<String> tempSet=new HashSet<String>();
		  
		try{
			 for(int i=0;i<2;i++){
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
			if(i==0){
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = sheet.iterator();
			_LOGGER.info("Started Processing Product");
		    String productId = null;
		    String xid = null;
		    int columnIndex=0;
			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				if (nextRow.getRowNum() == 0)
					continue;
				
				
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
								//sheetMap.put(productId,productExcelObj);
							 	}
							    if(!productXids.contains(xid)){
							    	productXids.add(xid);
							    	repeatRows.add(xid);
							    }
							  //
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
							//productId=xid;
						    	
						    	if(!StringUtils.isEmpty(xid))
								{
								if(xid.toUpperCase().contains("TOMAX")){
									
								}else{
								xidSet.add(xid);
								}
								}
							break;
						
					}  // end inner while loop					 
				}		
			}
			if(!StringUtils.isEmpty(xid))
			{
			if(xid.toUpperCase().contains("TOMAX")){
				
			}else{
			xidSet.add(xid);
			}
			}
			tempSet=new HashSet<String>(xidSet);
			// do deletion thing over here
			System.out.println(tempSet);
			System.out.println(tempSet.size() +" - Size");
			xidSet=new ArrayList<String>();
			xidSet.add("TestProdSD1");
			xidSet.add("3558-TES30ASD");
			xidSet.add("TestProdSD3");
			xidSet.add("TestProdSD4");
			
			for (String string : xidSet) {
				int num=	postServiceImpl.deleteProduct(accessToken, string, asiNumber, batchId);
	 	if(num ==1){
	 		numOfProductsSuccess.add("1");
	 	}else if(num == 0){
	 		numOfProductsFailure.add("0");
	 	}
			}
		 	_LOGGER.info("list size>>>>>>"+numOfProductsSuccess.size());
		 	_LOGGER.info("Failure list size>>>>>>"+numOfProductsFailure.size());
	       //finalResult = numOfProductsSuccess.size() + "," + numOfProductsFailure.size();
	       productDaoObj.saveErrorLog(asiNumber,batchId);
				
			postServiceImpl.deleteProduct(accessToken, productId, asiNumber, batchId);
			}else if(i==1){
				
			}
				}
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
		Cell xidCell =  row.getCell(0);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid) || productXid.trim().equalsIgnoreCase("#N/A")){
		     xidCell = row.getCell(1);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}
	
	public boolean isRepeateColumn(int columnIndex){
		if(columnIndex != 1&&columnIndex != 2){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	
	public static List<PriceGrid> getPriceGrids(String basePriceName) 
	{
		
		List<PriceGrid> newPriceGrid=new ArrayList<PriceGrid>();
		try{
			Integer sequence = 1;
			List<PriceConfiguration> configuration = null;
			PriceGrid priceGrid = new PriceGrid();
			priceGrid.setCurrency(ApplicationConstants.CONST_STRING_CURRENCY_USD);
			priceGrid.setDescription(basePriceName);
			priceGrid.setPriceIncludes(ApplicationConstants.CONST_STRING_EMPTY);
			priceGrid.setIsQUR(ApplicationConstants.CONST_BOOLEAN_TRUE);
			priceGrid.setIsBasePrice(true);
			priceGrid.setSequence(sequence);
			List<Price>	listOfPrice = new ArrayList<Price>();
			priceGrid.setPrices(listOfPrice);
			priceGrid.setPriceConfigurations(configuration);
			newPriceGrid.add(priceGrid);
	}catch(Exception e){
		_LOGGER.error("Error while processing PriceGrid: "+e.getMessage());
	}
		return newPriceGrid;
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



	public TomaxUsaAttributeParser getTomaxUsaAttributeParser() {
		return tomaxUsaAttributeParser;
	}



	public void setTomaxUsaAttributeParser(
			TomaxUsaAttributeParser tomaxUsaAttributeParser) {
		this.tomaxUsaAttributeParser = tomaxUsaAttributeParser;
	}



	public TomaxUsaPricegridParser getTomaxUsaPricegridParser() {
		return tomaxUsaPricegridParser;
	}



	public void setTomaxUsaPricegridParser(
			TomaxUsaPricegridParser tomaxUsaPricegridParser) {
		this.tomaxUsaPricegridParser = tomaxUsaPricegridParser;
	}
	
}
