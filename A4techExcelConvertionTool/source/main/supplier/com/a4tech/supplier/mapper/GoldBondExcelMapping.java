package com.a4tech.supplier.mapper;

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

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.Size;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.goldbond.GoldbondAttributeParser;

public class GoldBondExcelMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(GoldBondExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private GoldbondAttributeParser gbAttributeParser;
    
	@Override
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();
		  ProductConfigurations productConfiguration = new ProductConfigurations();
 		try{ 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		
		StringJoiner listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfDiscounts = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfColors    = new StringJoiner(ApplicationConstants.CONST_STRING_COMMA_SEP);
		String xid = null;
		int columnIndex=0;
		// String listPrice = "";
		// String priceQty  = "";
		StringJoiner productDescription = new StringJoiner(" ");
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
							 String desc = finalDescriptionValue(productExcelObj.getDescription(), productDescription.toString());
							 productExcelObj.setDescription(desc);
							    List<Color> listOfColor = gbAttributeParser.getProductColors(listOfColors.toString());
							    productConfiguration.setColors(listOfColor);
							 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId);
							 	if(num ==1){
							 		numOfProductsSuccess.add("1");
							 	}else if(num == 0){
							 		numOfProductsFailure.add("0");
							 	}else{
							 		
							 	}
							 	_LOGGER.info("list size>>>>>>>"+numOfProductsSuccess.size());
							 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
							 	productDescription = new StringJoiner(" ");
							 	listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							    listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							    listOfColors    = new StringJoiner(ApplicationConstants.CONST_STRING_COMMA_SEP);
								
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    }
						    productExcelObj = new Product();
     						 productExcelObj = postServiceImpl.getProduct(accessToken, xid);
						     if(productExcelObj == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						    	 productConfiguration = new ProductConfigurations();
						     }else{
						    	 productExcelObj = gbAttributeParser.keepExistingProductData(productExcelObj);
						    	 productConfiguration = productExcelObj.getProductConfigurations();
						     }
							
					 }
				}else{
					
				}
				
				switch (columnIndex+1) {
				case 1: //xid
					 break;
				case 2:
					 
					  break;
				case 3:
					 String asiPrdNo = cell.getStringCellValue();
					  productExcelObj.setAsiProdNo(asiPrdNo);
				    break;
				case 4:// description
					String prdName = cell.getStringCellValue();
					prdName = prdName.replaceAll("[^a-zA-Z0-9%/-! ]", "");
					productExcelObj.setName(prdName);
					break;
				case 5:	
				case 6: 
				case 7:
				case 8: 
				case 9: 
				case 10: 
				case 11:
				case 12:
				case 13:
				case 14:
					String description = cell.getStringCellValue();
					if(!StringUtils.isEmpty(description)){
						description = description.replaceAll("[^a-zA-Z0-9%/-! ]", "");
						productDescription.add(description);
					}
					 break;
				case 15:// qty
				case 16:
				case 17:
				case 18:
				case 19: 
					String priceQty = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(priceQty)){
						listOfQuantity.add(priceQty);
					}
					break;
			 
				case 20://netPricing 
				case 21:
				case 22: 
				case 23:
				case 24:
					String netPricing = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(netPricing)){
						listOfPrices.add(netPricing);
					}
					break;
				case 25: // ignore all coulmns until case 48
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
				case 50: // discount
				case 51:
				case 52:
				case 53:
				case 54:
					String discountCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(discountCode)){
						listOfDiscounts.add(discountCode);
					}
				    break;
					
				case 55: // ignore until cases 104
				
					
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
					String multiColorCharge = cell.getStringCellValue();
					productExcelObj = gbAttributeParser.getAdditionalColor(productExcelObj, multiColorCharge);
				    
					break;
				case 106: //color
				case 107:
				case 108: 
				case 109: 
				case 110: 
				case 111:
				case 112:
				case 113:
				case 114:
				case 115:
				case 116: 
				case 117:
				case 118: 
				case 119: 
				case 120: 
				case 121:
				case 122:
				case 123:
				case 124:	
				case 125:
				case 126: 
				case 127:
				case 128: 
				case 129: // end colors
				case 130: 
					String color = cell.getStringCellValue();
					if(!StringUtils.isEmpty(color)){
						listOfColors.add(color);
					}
					break;
				case 131:
					String size = cell.getStringCellValue();
					if(!StringUtils.isEmpty(size)){
						Size sizeVals = gbAttributeParser.getProductSize(size);
						productConfiguration.setSizes(sizeVals);
					}
					break;
				case 132: // imprint size
					String imprintSize = cell.getStringCellValue();
					if(StringUtils.isEmpty(imprintSize)){
						List<ImprintSize> listOfImprintSizes = gbAttributeParser.getProductImprintSize(imprintSize);
						productConfiguration.setImprintSize(listOfImprintSizes);
					}
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
					
				case 139: // proof charge
					//for pre-production proofs (as per comment)
					break;
				case 140: // reverse side imprint
					 
					
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
				case 231:
					
					break;
					
				
							
			}  // end inner while loop
					 
		}
				productExcelObj.setPriceType("N");
				String qurFlag = "n"; // by default for testing purpose
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
		 List<Color> listOfColor = gbAttributeParser.getProductColors(listOfColors.toString());
		 productConfiguration.setColors(listOfColor);
		 String desc = finalDescriptionValue(productExcelObj.getDescription(), productDescription.toString());
		 productExcelObj.setDescription(desc);
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
		Cell xidCell =  row.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid) || "N/A".equalsIgnoreCase(productXid)){
		     xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_TWO);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}
	private String finalDescriptionValue(String existingDesc,String newDesc){
		if(!StringUtils.isEmpty(existingDesc)){
			newDesc = newDesc + " "+existingDesc;
			return newDesc;
		} else{
			return newDesc;
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
	public GoldbondAttributeParser getGbAttributeParser() {
		return gbAttributeParser;
	}

	public void setGbAttributeParser(GoldbondAttributeParser gbAttributeParser) {
		this.gbAttributeParser = gbAttributeParser;
	}


	
}
