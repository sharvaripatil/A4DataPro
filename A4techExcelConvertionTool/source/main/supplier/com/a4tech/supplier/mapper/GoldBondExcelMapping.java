package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.util.StringUtils;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.dataStore.ProductDataStore;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class GoldBondExcelMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(GoldBondExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
    @Override
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
			List<ImprintMethod> imprintMethodsList = new ArrayList<ImprintMethod>();
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  Set<String> listOfColors = new HashSet<>();
		  String colorCustomerOderCode ="";
		  Set<Value> sizeValues = new HashSet<>();
 		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		
		StringJoiner listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String xid = null;
		int columnIndex=0;
		// String listPrice = "";
		// String priceQty  = "";
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
							
							 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId);
							 	if(num ==1){
							 		numOfProductsSuccess.add("1");
							 	}else if(num == 0){
							 		numOfProductsFailure.add("0");
							 	}else{
							 		
							 	}
							 	_LOGGER.info("list size>>>>>>>"+numOfProductsSuccess.size());
							 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
								
								
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
						    	 
						     }
							
					 }
				}else{
					
				}
				
				switch (columnIndex+1) {
				case 1:
					 break;
				case 2:
					
					  break;
				case 3:
					 
				    break;
				case 4:
					
					
				    break;
					
				case 5:
				
					
					break;
					
				case 6: 
				
					
					break;
					
				case 7:
					 
					   
					break;
					
				case 8: 
				
					
					break;
					
				case 9: 
					break;
				case 10: 
					 
					
					break;
				case 11:
				   
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
				case 17:
			
				
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
				case 27:
					  
					  break;
				case 28:
					 
				    break;
				case 29:
					
					
				    break;
					
				case 30:
					
					
					break;
					
				case 31: 
				
					
					break;
					
				case 32:
					 
					   
					break;
					
				case 33: 
					
					
					break;
					
				case 34: 
					break;
				case 35: 
					
					
					break;
				case 36:
				    
					break;
					
				case 37:
					break;
				case 38:
					
					
					 break;
				case 39:
					
					
			     break;
				case 40:
			
						
					break;
				case 41:
				
				
					break;
				case 42:
				
				
					break;
				case 43:
					break;
				case 44: 
					break;
				case 45:
					break;
				case 46:
					
					break;
					
				case 47: 
					break;
				case 48:
					break;
				case 49:
					
					break;
				case 50: 
					
					break;
				case 51:
					 break;
				case 52:
					
					  break;
				case 53:
					 
				    break;
				case 54:
					
					
				    break;
					
				case 55:
				
					
					break;
					
				case 56: 
				
					
					break;
					
				case 57:
					 
					   
					break;
					
				case 58: 
				
					
					break;
					
				case 59: 
					break;
				case 60: 
					 
					
					break;
				case 61:
				   
					break;
					
				case 62:
					break;
				case 63:
				
					
					 break;
				case 64:
				
					
			     break;
				case 65:
		
					break;
				case 66:
		
				
					break;
				case 67:
			
				
					break;
				case 68:
					break;
				case 69: 
					break;
				case 70:
					break;
				case 71:
					
					break;
					
				case 72: 
					break;
				case 73:
					break;
				case 74:
					
					break;
				case 75: 
					
					
					
					break;
					
				case 76:
					 break;
				case 77:
					  
					  break;
				case 78:
					 
				    break;
				case 79:
					
					
				    break;
					
				case 80:
					
					
					break;
					
				case 81: 
				
					
					break;
					
				case 82:
					 
					   
					break;
					
				case 83: 
					
					
					break;
					
				case 84: 
					break;
				case 85: 
					
					
					break;
				case 86:
				    
					break;
					
				case 87:
					break;
				case 88:
					
					
					 break;
				case 89:
					
					
			     break;
				case 90:
			
						
					break;
				case 91:
				
				
					break;
				case 92:
				
				
					break;
				case 93:
					break;
				case 94: 
					break;
				case 95:
					break;
				case 96:
					
					break;
					
				case 97: 
					break;
				case 98:
					break;
				case 99:
					
					break;
				case 100: 
					break;
				case 101:
					 break;
				case 102:
					
					  break;
				case 103:
					 
				    break;
				case 104:
					
					
				    break;
					
				case 105:
				
					
					break;
					
				case 106: 
				
					
					break;
					
				case 107:
					 
					   
					break;
					
				case 108: 
				
					
					break;
					
				case 109: 
					break;
				case 110: 
					 
					
					break;
					
				case 111:
					 break;
				case 112:
					
					  break;
				case 113:
					 
				    break;
				case 114:
					
					
				    break;
					
				case 115:
				
					
					break;
					
				case 116: 
				
					
					break;
					
				case 117:
					 
					   
					break;
					
				case 118: 
				
					
					break;
					
				case 119: 
					break;
				case 120: 
					 
					
					break;
				case 121:
					 break;
				case 122:
					
					  break;
				case 123:
					 
				    break;
				case 124:
					
					
				    break;
					
				case 125:
				
					
					break;
					
				case 126: 
				
					
					break;
					
				case 127:
					 
					   
					break;
					
				case 128: 
				
					
					break;
					
				case 129: 
					break;
				case 130: 
					 
					
					break;
				case 131:
					 break;
				case 132:
					
					  break;
				case 133:
					 
				    break;
				case 134:
					
					
				    break;
					
				case 135:
				
					
					break;
					
				case 136: 
				
					
					break;
					
				case 137:
					 
					   
					break;
					
				case 138: 
				
					
					break;
					
				case 139: 
					break;
				case 140: 
					 
					
					break;
				case 141:
					 break;
				case 142:
					
					  break;
				case 143:
					 
				    break;
				case 144:
					
					
				    break;
					
				case 145:
				
					
					break;
					
				case 146: 
				
					
					break;
					
				case 147:
					 
					   
					break;
					
				case 148: 
				
					
					break;
					
				case 149: 
					break;
				case 150: 
					 
					
					break;
				case 151:
					 break;
				case 152:
					
					  break;
				case 153:
					 
				    break;
				case 154:
					
					
				    break;
					
				case 155:
				
					
					break;
					
				case 156: 
				
					
					break;
					
				case 157:
					 
					   
					break;
					
				case 158: 
				
					
					break;
					
				case 159: 
					break;
				case 160: 
					 
					
					break;
				case 161:
					 break;
				case 162:
					
					  break;
				case 163:
					 
				    break;
				case 164:
					
					
				    break;
					
				case 165:
				
					
					break;
					
				case 166: 
				
					
					break;
					
				case 167:
					 
					   
					break;
					
				case 168: 
				
					
					break;
					
				case 169: 
					break;
				case 170: 
					 
					
					break;
				case 171:
					 break;
				case 172:
					
					  break;
				case 173:
					 
				    break;
				case 174:
					
					
				    break;
					
				case 175:
				
					
					break;
					
				case 176: 
				
					
					break;
					
				case 177:
					 
					   
					break;
					
				case 178: 
				
					
					break;
					
				case 179: 
					break;
				case 180: 
					 
					
					break;
				case 181:
					 break;
				case 182:
					
					  break;
				case 183:
					 
				    break;
				case 184:
					
					
				    break;
					
				case 185:
				
					
					break;
					
				case 186: 
				
					
					break;
					
				case 187:
					 
					   
					break;
					
				case 188: 
				
					
					break;
					
				case 189: 
					break;
				case 190: 
					 
					
					break;
				case 191:
					 break;
				case 192:
					
					  break;
				case 193:
					 
				    break;
				case 194:
					
					
				    break;
					
				case 195:
				
					
					break;
					
				case 196: 
				
					
					break;
					
				case 197:
					 
					   
					break;
					
				case 198: 
				
					
					break;
					
				case 199: 
					break;
				case 200: 
					 
					
					break;
				case 201:
					 break;
				case 202:
					
					  break;
				case 203:
					 
				    break;
				case 204:
					
					
				    break;
					
				case 205:
				
					
					break;
					
				case 206: 
				
					
					break;
					
				case 207:
					 
					   
					break;
					
				case 208: 
				
					
					break;
					
				case 209: 
					break;
				case 210: 
					 
					
					break;
				case 211:
				   
					break;
					
				case 212:
					break;
				case 213:
				
					
					 break;
				case 214:
				
					
			     break;
				case 215:
		
					break;
				case 216:
		
				
					break;
				case 217:
			
				
					break;
				case 218:
					break;
				case 219: 
					break;
				case 220:
					break;
				case 221:
					
					break;
					
				case 222: 
					break;
				case 223:
					break;
				case 224:
					
					break;
				case 225: 
					
					
					
					break;
					
				case 226:
					 break;
				case 227:
					  
					  break;
				case 228:
					 
				    break;
				case 229:
					
					
				    break;
					
				case 230:
					
					
					break;
					
				
							
			}  // end inner while loop
					 
		}
				productExcelObj.setPriceType("N");
				String qurFlag = "n"; // by default for testing purpose
			
			
				/*if(!finalSizes.contains(sizeValue)){
					finalSizes = sizeValue +",";
				}*/
				/*if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
					priceGrids = apparelPgParser.getPriceGrids(listOfPrices.toString(), 
							listOfQuantity.toString(), "P", "USD",
							         "", true, qurFlag, basePriceName,"",priceGrids);	
				}*/
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
		
	
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId);
		 	if(num ==1){
		 		numOfProductsSuccess.add("1");
		 	}else if(num == 0){
		 		numOfProductsFailure.add("0");
		 	}else{
		 		
		 	}
		 	ProductDataStore.clearProductColorSet();
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
		Cell xidCell =  row.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid) || "N/A".equalsIgnoreCase(productXid)){
		     xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_TWO);
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
