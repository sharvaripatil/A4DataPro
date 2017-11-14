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
import com.a4tech.dataStore.ProductDataStore;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.InternationalMerchConcepts.MerchAttributeParser;

public class InternationlMerchMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(InternationlMerchMapping.class);
	
	private PostServiceImpl 				postServiceImpl;
	private ProductDao 						productDaoObj;
	
	private MerchAttributeParser           merchAttributeParser;
	
	@Override
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		Product productExcelObj = new Product();   
		ProductConfigurations productConfigObj=new ProductConfigurations();
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		List<String> repeatRows = new ArrayList<>();
		StringBuilder shippingDimention = new StringBuilder();
		String shippingWeight = null;
		String noOfItems = null;
 		try{
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		String xid = null;
		int columnIndex=0;		 
		List<ProductSkus> listProductSkus = new ArrayList<>();
		StringBuilder description = new StringBuilder();
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
				 if (columnIndex == 0) {
						xid = getProductXid(nextRow);
						checkXid = true;
					} else {
						checkXid = false;
					}
				/*if(columnIndex + 1 == 1){
					xid = getProductXid(nextRow);
					checkXid = true;
				}else{
					checkXid = false;
				}*/if (columnIndex == 0){
					if(repeatRows.contains(xid)){
						checkXid = false;
					}
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							 System.out.println("Java object converted to JSON String, written to file");
							     
						     if(priceGrids.size() == 1){
						    	 priceGrids = removeBasePriceConfig(priceGrids);
						     }
						       if(!StringUtils.isEmpty(description.toString())){
						    	   if(description.length()> 800){
						    		   String finalDesc = CommonUtility.getStringLimitedChars(description.toString(), 800);
						    			   String additionalInfo = description.substring(801);   
						    			   productExcelObj.setDescription(finalDesc);
						    			   productExcelObj.setAdditionalProductInfo(additionalInfo);
						    	   } else {
						    		   productExcelObj.setDescription(description.toString());
						    	   }
						       }
									ShippingEstimate shippingEstimation = merchAttributeParser
											.getProductShippingEstimates(noOfItems, shippingDimention.toString(), shippingWeight);
									productConfigObj.setShippingEstimates(shippingEstimation);
									productExcelObj.setPriceGrids(priceGrids);
							 	productExcelObj.setProductConfigurations(productConfigObj);
							 	productExcelObj.setProductRelationSkus(listProductSkus);
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
								ProductDataStore.clearProductColorSet();
								repeatRows.clear();
								listProductSkus = new ArrayList<>();
								shippingDimention = new StringBuilder();
								shippingWeight = null;
								noOfItems = null;
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    	//repeatRows.add(xid);
						    }
						    productExcelObj = new Product();
     						 productExcelObj = postServiceImpl.getProduct(accessToken, xid);
						   
						     if(productExcelObj == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						    	// productExcelObj= blueGenerationattributeParser.keepExistingProductData(productExcelObj);
						    	 productConfigObj=productExcelObj.getProductConfigurations();
						     }	
					 }
				}else{
					/*if(isRepeateColumn(columnIndex+1)){
						 continue;
					 }*/
					if(productXids.contains(xid) && repeatRows.size() != 0){
						 if(isRepeateColumn(columnIndex+1)){
							 continue;
						 }
					}
				}
				
				switch (columnIndex+1) {
				case 1: //xid
					productExcelObj.setExternalProductId(xid);
					 break;
				case 2:
					 String prdNumber = cell.getStringCellValue();
					 productExcelObj.setAsiProdNo(prdNumber);
					  break;
				case 3:// name
					String pName= cell.getStringCellValue();
					productExcelObj.setName(pName);
				    break;
				case 4:
				case 5:	
				case 6: 
				case 7://Ignore as per feedback
					break;
				case 8: 
				case 9: //related to description
					String desc = cell.getStringCellValue();
					description.append(desc).append(" ");
					break;
				case 10: 
					// there is no data in file
					break;
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
				case 23:
				case 24:// Ignore as per feedback 
					break;
				case 25:
					break;
				case 26:// Imprint size
					String imprintSize = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintSize)){
						List<ImprintSize> imprintSizeList = merchAttributeParser.getProductImprintSize(imprintSize);
						productConfigObj.setImprintSize(imprintSizeList);
					}
					break;
				case 27: 
					//Ignore as per feedback i.e Size column
					break;
				case 28:// COLOR
					String color = cell.getStringCellValue();
					if(!StringUtils.isEmpty(color)){
						List<Color> colorList = merchAttributeParser.getProductColor(color);
						productConfigObj.setColors(colorList);
					}
					break;
				case 29:// Option
					String optionVal = cell.getStringCellValue();
							if (StringUtils.isEmpty(optionVal)) {
								List<Option> listOfOptins = merchAttributeParser.getProductOption("Ink Refill",
										"Product", optionVal);
								productConfigObj.setOptions(listOfOptins);
							}
					break;
				case 30: // Imprint Methods
					String imprintMethodVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintMethodVal)){
						productExcelObj.setProductConfigurations(productConfigObj);
						productExcelObj = merchAttributeParser.getProductImprintMethod(imprintMethodVal, productExcelObj);
						productConfigObj = productExcelObj.getProductConfigurations();
						priceGrids = productExcelObj.getPriceGrids();
					}
					break;
				case 31://packaging
					String packVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(packVal)){
						productExcelObj.setProductConfigurations(productConfigObj);
						productExcelObj.setPriceGrids(priceGrids);
						productExcelObj = merchAttributeParser.getProductPackaging(packVal, productExcelObj);
						productConfigObj = productExcelObj.getProductConfigurations();
						priceGrids = productExcelObj.getPriceGrids();
					}
					 break;
				case 32:  //Ignore
					  break;
				case 33://Standard productionTime
					String prdTime = cell.getStringCellValue();
					if(!StringUtils.isEmpty(prdTime)){
						productExcelObj.setProductConfigurations(productConfigObj);
						productExcelObj.setPriceGrids(priceGrids);
						productExcelObj = merchAttributeParser.getProductionTime(prdTime, productExcelObj);
						productConfigObj = productExcelObj.getProductConfigurations();
					}
				    break;
				case 34://Rush Time
					String rushTime = cell.getStringCellValue();
					if(!StringUtils.isEmpty(rushTime) && !rushTime.equals("N/A")){
						productExcelObj.setProductConfigurations(productConfigObj);
						productExcelObj.setPriceGrids(priceGrids);
						productExcelObj = merchAttributeParser.getProductPackaging(rushTime, productExcelObj);
						productConfigObj = productExcelObj.getProductConfigurations();
						priceGrids = productExcelObj.getPriceGrids();
					}
				    break;
				case 35:// Embroidery Production Time
					 //missing case
					 break;
				case 36://price Include (it is case 36)
					String priceInclude = cell.getStringCellValue();
					break;
				case 37: //setup charge
					String setupChargeVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(setupChargeVal)){
						productExcelObj.setPriceGrids(priceGrids);
								productExcelObj = merchAttributeParser.getUpchargeImprintMethdoColumns(setupChargeVal,
										productExcelObj, "Set-up Charge");
						priceGrids = productExcelObj.getPriceGrids();
					}
					break;
				case 38://Addl Color-Location
					String addColorLoc = cell.getStringCellValue();
					if(!StringUtils.isEmpty(addColorLoc)){
						productExcelObj.setProductConfigurations(productConfigObj);
						productExcelObj.setPriceGrids(priceGrids);
						merchAttributeParser.getUpchargeAdditionalColorAndLocation(addColorLoc, productExcelObj);
						productConfigObj = productExcelObj.getProductConfigurations();
						priceGrids = productExcelObj.getPriceGrids();
					}
					break;
				case 39: //Screen Re-Order Setup
					String screenReorderVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(screenReorderVal)){
						productExcelObj.setProductConfigurations(productConfigObj);
						productExcelObj.setPriceGrids(priceGrids);
						merchAttributeParser.getUpchargeBasedOnScreenReOrderSetup(screenReorderVal, productExcelObj);
						productConfigObj = productExcelObj.getProductConfigurations();
						priceGrids = productExcelObj.getPriceGrids();
					}
					break;
				case 40: //Less Than Minimum
					String lessThanVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(lessThanVal)){
						productExcelObj.setPriceGrids(priceGrids);
						merchAttributeParser.getUpchargeBasedOnLessThanMin(lessThanVal, productExcelObj);
						priceGrids = productExcelObj.getPriceGrids();
					}
					break;
				case 41: //Logo Modification
					String logoModificationVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(logoModificationVal)){
						productExcelObj.setProductConfigurations(productConfigObj);
						productExcelObj.setPriceGrids(priceGrids);
						merchAttributeParser.getUpchargeBasedOnLogoModification(logoModificationVal, productExcelObj);
						productConfigObj = productExcelObj.getProductConfigurations();
						priceGrids = productExcelObj.getPriceGrids();
					}
					break;
				case 42://setup
					//this column data equals to screen setup column(Case 37)
					//that's cause ignore this column
					break;
				case 43://Addl-Color Loc
					//this column data equals to screen setup column(Case 38)
					//that's cause ignore this column
					break;
				case 44://max imprint
					String maxImprint = cell.getStringCellValue();
					if(!StringUtils.isEmpty(maxImprint)){
						description.append(",").append(" ").append(maxImprint);
					}
			        break;
				case 45://tape charge
					String tapeCharge = cell.getStringCellValue();
					if(!StringUtils.isEmpty(tapeCharge)){
						productExcelObj.setProductConfigurations(productConfigObj);
						productExcelObj.setPriceGrids(priceGrids);
						merchAttributeParser.getUpchargeBasedOnScreenReOrderSetup(tapeCharge, productExcelObj);
						productConfigObj = productExcelObj.getProductConfigurations();
						priceGrids = productExcelObj.getPriceGrids();
					}
					break;
				case 46:
				case 47:
				case 48:
				case 49: 
					//ignore as per feedback
					break;
				case 50://shipping weight
					shippingWeight = cell.getStringCellValue();
					
					break;
				case 51://shipping width
					String shiWidth = cell.getStringCellValue();
					if(!StringUtils.isEmpty(shiWidth)){
						shippingDimention.append(shiWidth).append("x");
					}
					break;
				case 52: //shipping height
					String shiheight = cell.getStringCellValue();
					if(!StringUtils.isEmpty(shiheight)){
						shippingDimention.append(shiheight).append("x");;
					}
					break;
				case 53:// shipping length
					String shiLength = cell.getStringCellValue();
					if(!StringUtils.isEmpty(shiLength)){
						shippingDimention.append(shiLength);
					}
					break;
				case 54: // shipping items
					 noOfItems = cell.getStringCellValue();
					break;
				case 55: // discount
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
				case 111: //color
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
				case 231:
					break;
				case 232: 
					break;
				case 233:
					break;
				case 234:
					break;
				case 235: 
					break;
				case 236: 
					break;
				case 237: 
					break;
				case 238:
					break;
				case 239: 
					break;
				case 240:
					break;
				case 241:
					break;
				case 242: 
					break;
				case 243: 
					break;
				case 244: 
					break;
				case 245:
					break;
				case 246: 
					break;
				case 247:
					break;
				case 248:
					break;
				case 249: 
					break;
				case 250: 
					break;
				case 251: 
					break;
				case 252:
					break;
				case 253: 
					break;
				case 254:
					break;
				case 255:
					break;
				case 256: 
					break;
				case 257: 
					break;
				case 258: 
					break;
				case 259:
					break;
				case 260: 
					break;
				case 261:
					break;
				case 262:
					break;
				case 263: 
					break;
			}  // end inner while loop
					 
		}
				productExcelObj.setPriceType("L");
				String finalCriteria = "";
			    repeatRows.add(xid);
			}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);		 
				ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: "
				+e.getMessage()+" at column number(increament by 1)"+columnIndex);
				productDaoObj.save(apiResponse.getErrors(),
						productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);
		}
		}
		workbook.close();
		 if(priceGrids.size() == 1){
	    	 priceGrids = removeBasePriceConfig(priceGrids);
	     }
		 	productExcelObj.setPriceGrids(priceGrids);
		 	productExcelObj.setProductConfigurations(productConfigObj);
		 	productExcelObj.setProductRelationSkus(listProductSkus);
		 		int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId);
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
		Cell xidCell =  row.getCell(ApplicationConstants.CONST_NUMBER_ZERO);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid)){
		     xidCell = row.getCell(8);
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
		if (columnIndex != 3  && columnIndex != 6
				&& columnIndex != 8 && columnIndex != 10 && columnIndex != 11) {
				return ApplicationConstants.CONST_BOOLEAN_TRUE;
			}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	
	
	private List<PriceGrid> removeBasePriceConfig(List<PriceGrid> oldPriceGrid){
		List<PriceGrid> newPricegrid = new ArrayList<>();
		for (PriceGrid priceGrid : oldPriceGrid) {
			if(priceGrid.getIsBasePrice()){
				priceGrid.setPriceConfigurations(new ArrayList<>());
				newPricegrid.add(priceGrid);
			} else {
				newPricegrid.add(priceGrid);	
			}
		}
		return newPricegrid;
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
	public MerchAttributeParser getMerchAttributeParser() {
		return merchAttributeParser;
	}

	public void setMerchAttributeParser(MerchAttributeParser merchAttributeParser) {
		this.merchAttributeParser = merchAttributeParser;
	}

}
