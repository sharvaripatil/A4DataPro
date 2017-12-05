package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.Shape;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.InternationalMerchConcepts.MerchAttributeParser;
import parser.InternationalMerchConcepts.MerchColorMapping;

public class InternationlMerchMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(InternationlMerchMapping.class);
	
	private PostServiceImpl 				postServiceImpl;
	private ProductDao 						productDaoObj;
	private MerchAttributeParser           merchAttributeParser;
	
	@Override
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
		
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
		StringJoiner grid_1Quantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner grid_1Prices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner grid_1Discounts = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner grid_2Quantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner grid_2Prices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner grid_2Discounts = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String priceGridName_1 = "";
		String priceGridName_2 = "";
		String priceInclude_1 = "";
		String priceInclude_2 = "";
		String priceInclude_upcharge = "";
		List<String> colorsList = new ArrayList<>();
		String prdNumber = "";
		Map<String, String> productNumbersMap = new HashMap<>();
		List<String> supplierImprSizeList = new ArrayList<>();
		List<String> supplierSizeValList  = new ArrayList<>();
		String sizeValue = "";
		String imprintSizeVal = "";
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
						if(xid.contains("A-OR0421")){
							xid = specialCaseXid(xid, nextRow);
						}
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
							     
						     /*if(priceGrids.size() == 1){
						    	 priceGrids = removeBasePriceConfig(priceGrids);
						     }*/
						       if(!StringUtils.isEmpty(description.toString())){
						    	   String finalDescription = removeSpecialCharDescription(description.toString());
						    	   if(finalDescription.length()> 800){
						    		   String finalDesc = CommonUtility.getStringLimitedChars(finalDescription.toString(), 800);
						    			   String additionalInfo = finalDescription.substring(801);   
						    			   productExcelObj.setDescription(finalDesc);
						    			   productExcelObj.setAdditionalProductInfo(additionalInfo);
						    	   } else {
						    		   productExcelObj.setDescription(finalDescription.toString());
						    	   }
						       }
						       if(xid.contains("A-OR0421")){
						    	   List<Shape> shapeList  = getProductShape(xid);
						    	   productConfigObj.setShapes(shapeList);
						       }
						       if(!CollectionUtils.isEmpty(productNumbersMap)){
						    	   if(productNumbersMap.size() > 1){// if product contain repeat rows only
						    		   List<ProductNumber> productNumberList = merchAttributeParser
												.getProductNumbers(productNumbersMap);
										productExcelObj.setProductNumbers(productNumberList);
										productExcelObj.setAsiProdNo("");
						    	   }	
						       }
						       if(!CollectionUtils.isEmpty(colorsList)){
						    	   List<Color> colorList = merchAttributeParser.getProductColor(colorsList);
						    	   productConfigObj.setColors(colorList);
						       }
									List<ImprintSize> imprintSizeList = merchAttributeParser
											.getProductImprintSize(supplierImprSizeList);
								productConfigObj.setImprintSize(imprintSizeList);
								Size sizeVals = merchAttributeParser.getProductSize(supplierSizeValList);
								productConfigObj.setSizes(sizeVals);
								if(!merchAttributeParser.isSpecialBasePriceGrid(xid)){
									if(!StringUtils.isEmpty(grid_1Prices.toString())){
										// grid based on Debossed 
							    	   String imprintMethodType = "";
							    	   if(!StringUtils.isEmpty(grid_2Prices.toString())){
							    		   imprintMethodType = "Debossed";
							    	   } 
										productExcelObj.setProductConfigurations(productConfigObj);
										productExcelObj.setPriceGrids(priceGrids);
											productExcelObj = merchAttributeParser.getBasePriceColumns(grid_1Quantity.toString(),
													grid_1Prices.toString(), grid_1Discounts.toString(), priceGridName_1, priceInclude_upcharge,
													productExcelObj, imprintMethodType);
										productConfigObj = productExcelObj.getProductConfigurations();
										priceGrids = productExcelObj.getPriceGrids();
									}
									if(!StringUtils.isEmpty(grid_2Prices.toString())){
										// grid based on Silkscreen 
										productExcelObj.setProductConfigurations(productConfigObj);
										productExcelObj.setPriceGrids(priceGrids);
										productExcelObj = merchAttributeParser.getBasePriceColumns(grid_2Quantity.toString(),
												grid_2Prices.toString(), grid_2Discounts.toString(), priceGridName_2, priceInclude_2,
												productExcelObj, "Silkscreen");
										productConfigObj = productExcelObj.getProductConfigurations();
										priceGrids = productExcelObj.getPriceGrids();
									}
								}
									ShippingEstimate shippingEstimation = merchAttributeParser
											.getProductShippingEstimates(noOfItems, shippingDimention.toString(), shippingWeight);
									productConfigObj.setShippingEstimates(shippingEstimation);
									productExcelObj.setPriceGrids(priceGrids);
							 	productExcelObj.setProductConfigurations(productConfigObj);
							 	productExcelObj.setProductRelationSkus(listProductSkus);
							 	productExcelObj.setDeliveryOption("");// it is used for imprintmethod value reference,that's cause again reset value
							 	                                          //because there is no deliveryoption values
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
								ProductDataStore.clearProductColorSet();
								repeatRows.clear();
								listProductSkus = new ArrayList<>();
								shippingDimention = new StringBuilder();
								shippingWeight = null;
								noOfItems = null;
								 grid_1Quantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								 grid_1Prices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								 grid_1Discounts = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								 grid_2Quantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								 grid_2Prices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								 grid_2Discounts = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								 priceGridName_1 = "";
								 priceGridName_2 = "";
								  priceInclude_1 = "";
								  priceInclude_2 = "";
								  priceInclude_upcharge = "";
								  colorsList = new ArrayList<>();
								  prdNumber = "";
								  productNumbersMap = new HashMap<>();
								  description = new StringBuilder();
								  supplierImprSizeList = new ArrayList<>();
								  supplierSizeValList  = new ArrayList<>();
								  sizeValue = "";
								  imprintSizeVal = "";
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
						    	productExcelObj= merchAttributeParser.keepExistingProductData(productExcelObj);
						    	 productConfigObj=productExcelObj.getProductConfigurations();
						     }	
					 }
				}else{
					/*if(isRepeateColumn(columnIndex+1)){
						 continue;
					 }*/
					if(productXids.contains(xid) && repeatRows.size() != 0){
						 if(isRepeateColumn(columnIndex+1,xid)){
							 continue;
						 }
					}
				}
				
				switch (columnIndex+1) {
				case 1: //xid
					productExcelObj.setExternalProductId(xid);
					 break;
				case 2:
					 prdNumber = cell.getStringCellValue();
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
				case 25:// dimension
					String sizeVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(sizeVal)){
						supplierSizeValList.add(sizeVal);
					}
					break;
				case 26:// Imprint size
					String imprintSize = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintSize)){
						supplierImprSizeList.add(imprintSize);
					}
					break;
				case 27: 
					//Ignore as per feedback i.e Size column
					break;
				case 28:// COLOR
					String color = cell.getStringCellValue();
					if(!StringUtils.isEmpty(color)){
						//colorName = color;
						if(!colorsList.contains(color)){
							
						}
						colorsList.add(color);
						if(!color.contains(",")){
							productNumbersMap.put(prdNumber, color);
						}
					}
					break;
				case 29:// Option
					String optionVal = cell.getStringCellValue();
							if (!StringUtils.isEmpty(optionVal)) {
								List<Option> listOfOptins = merchAttributeParser.getProductOption("Ink Refill",
										"Product", optionVal);
								productConfigObj.setOptions(listOfOptins);
							}
					break;
				case 30: // Imprint Methods
					String imprintMethodVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintMethodVal)){
						productExcelObj.setProductConfigurations(productConfigObj);
								productExcelObj = merchAttributeParser.getProductImprintMethod(imprintMethodVal,
										productExcelObj);
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
					 //there is no data this column
					 break;
				case 36://price Include (it is case 36)
					priceInclude_upcharge = cell.getStringCellValue();
					if(!StringUtils.isEmpty(priceInclude_upcharge) && priceInclude_upcharge.contains("Price includes")){
						priceInclude_upcharge = priceInclude_upcharge.replaceAll("Price includes", "").trim();	
					}
					break;
				case 37: //screen setup charge
					String setupChargeVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(setupChargeVal)){
						productExcelObj.setProductConfigurations(productConfigObj);
						productExcelObj.setPriceGrids(priceGrids);
								productExcelObj = merchAttributeParser.getUpchargeImprintMethdoColumns(setupChargeVal,
										productExcelObj, "Set-up Charge","");
						priceGrids = productExcelObj.getPriceGrids();
						productConfigObj = productExcelObj.getProductConfigurations();
					}
					break;
				case 38://Addl Color-Location
					String addColorLoc = cell.getStringCellValue();
					if(!StringUtils.isEmpty(addColorLoc)){
						productExcelObj.setProductConfigurations(productConfigObj);
						productExcelObj.setPriceGrids(priceGrids);
								productExcelObj = merchAttributeParser
										.getUpchargeAdditionalColorAndLocation(addColorLoc, productExcelObj);
						productConfigObj = productExcelObj.getProductConfigurations();
						priceGrids = productExcelObj.getPriceGrids();
					}
					break;
				case 39: //Screen Re-Order Setup
					String screenReorderVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(screenReorderVal)){
						productExcelObj.setProductConfigurations(productConfigObj);
						productExcelObj.setPriceGrids(priceGrids);
								productExcelObj = merchAttributeParser
										.getUpchargeBasedOnScreenReOrderSetup(screenReorderVal, productExcelObj);
						productConfigObj = productExcelObj.getProductConfigurations();
						priceGrids = productExcelObj.getPriceGrids();
					}
					break;
				case 40: //Less Than Minimum
					String lessThanVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(lessThanVal)){
						productExcelObj.setPriceGrids(priceGrids);
								productExcelObj = merchAttributeParser.getUpchargeBasedOnLessThanMin(lessThanVal,
										productExcelObj);
						priceGrids = productExcelObj.getPriceGrids();
					}
					break;
				case 41: //Logo Modification
					String logoModificationVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(logoModificationVal)){
						productExcelObj.setProductConfigurations(productConfigObj);
						productExcelObj.setPriceGrids(priceGrids);
								productExcelObj = merchAttributeParser
										.getUpchargeBasedOnLogoModification(logoModificationVal, productExcelObj);
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
								productExcelObj = merchAttributeParser.getUpchargeBasedOnScreenReOrderSetup(tapeCharge,
										productExcelObj);
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
					shippingWeight = CommonUtility.getCellValueStrinOrInt(cell);
					
					break;
				case 51://shipping width
					String shiWidth =  CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(shiWidth)){
						shippingDimention.append(shiWidth).append("x");
					}
					break;
				case 52: //shipping height
					String shiheight = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(shiheight)){
						shippingDimention.append(shiheight).append("x");;
					}
					break;
				case 53:// shipping length
					String shiLength = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(shiLength)){
						shippingDimention.append(shiLength);
					}
					break;
				case 54: // shipping items
					 noOfItems = CommonUtility.getCellValueStrinOrInt(cell);
					break;
				case 55: 
				case 56:
					// ignore as per feedback
					break;
				case 57://pricename 1
					priceGridName_1 = cell.getStringCellValue();
					break;
				case 58:
					break;
				case 59:
				    break;
				case 60:// base pricegrid_1 QTY
				case 65:
				case 70:
				case 75:
				case 80:
				case 85:
				case 90:
				case 95:
				case 100:
				case 105:
					//grid_1 qty
					String priceQty = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(priceQty) && !priceQty.equals("0")){
						grid_1Quantity.add(priceQty);
					}
					break;
				case 61: 
					break;
				case 62: // grid_1 Prices
				case 67:
				case 72:
				case 77:
				case 82:
				case 87:
				case 92:
				case 97:
				case 102:
				case 107:
					String prices = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(prices)){
						grid_1Prices.add(prices);
					}
					break;
				case 63: //gird_1 discount
				case 68:
				case 73:
				case 78:
				case 83:
				case 88:
				case 93:
				case 98:
				case 103:
				case 108:
					String discount = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(discount)){
						grid_1Discounts.add(discount);
					}
					break;
				case 64: 
					break;
				case 66:
					break;
				case 69:
			        break;
				case 71:
					break;
				case 74: 
					break;
				case 76:
					break;
				case 79:
					break;
				case 81:
					 break;
				case 84:
				    break;
				case 86: 	
					break;
				case 89: 
					break;
				case 91:
					break;
				case 94:
			        break;
				case 96:
					break;
				case 99: 
					break;
				case 101:
					break;
				case 104:
					break;
				case 106:
					 break;
				case 109:
					break;	
			   case 110:
					break;
				case 111: //color
					break;
				case 112://price include 1
					priceInclude_1 = cell.getStringCellValue();
					break;
				case 113: 
					break;
				case 114: 
					break;
				case 115: 
					break;
				case 116:
					break;
				case 117://price name 2
					priceGridName_2 = cell.getStringCellValue();
					break;
				case 118:
					break;
				case 119:
					break;
				case 120://grid_2 QTY
				case 125:
				case 130:
				case 135:
				case 140:
				case 145:
				case 150:
				case 155:
				case 160:
				case 165:
					String priceQty1 = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(priceQty1) && !priceQty1.equals("0")){
						grid_2Quantity.add(priceQty1);
					}
					break;
				case 121: 
					break;
				case 122:// grid_2 Prices
				case 127:
				case 132:
				case 137:
				case 142:
				case 147:
				case 152:
				case 157:
				case 162:
				case 167:
					String prices1 = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(prices1)){
						grid_2Prices.add(prices1);
					}
					break;
				case 123://grid_2 discount
				case 128:
				case 133:
				case 138:
				case 143:
				case 148:
				case 153:
				case 158:
				case 163:
				case 168:
					String discount1 = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(discount1)){
						grid_2Discounts.add(discount1);
					}
					break;
				case 124: 
					break;
				case 126:
					break;
				case 129:
					break;
				case 131: 
					break;
				case 134: 
					break;
				case 136:
					break;
				case 139:
				    break;
				case 141: 
					break;
				case 144: 
					break;
				case 146:
					 break;
				case 149: 
				    break;
				case 151:
					break;
				case 154:	
					break;
				case 156:
					 break;

				case 159:
				    break;
				case 161: 
					break;
				case 164: 
					break;
				case 166:
					break;
				case 169:
					break;
				case 170:
					break;
				case 171: 
					break;
				case 172:// priceinclude 2
					priceInclude_2 = cell.getStringCellValue();
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
			if(merchAttributeParser.isSpecialBasePriceGrid(xid)){
				if(!StringUtils.isEmpty(grid_1Prices.toString())){
							    	   String imprintMethodType = "";
		    	   if(!StringUtils.isEmpty(grid_2Prices.toString())){
		    		   imprintMethodType = "Debossed";
		    	   } 
					productExcelObj.setProductConfigurations(productConfigObj);
					productExcelObj.setPriceGrids(priceGrids);
					// changes are required for price configuration,
						productExcelObj = merchAttributeParser.getBasePriceColumns(grid_1Quantity.toString(),
								grid_1Prices.toString(), grid_1Discounts.toString(), priceGridName_1, priceInclude_upcharge,
								productExcelObj, imprintMethodType);
					productConfigObj = productExcelObj.getProductConfigurations();
					priceGrids = productExcelObj.getPriceGrids();
				}	
			}
			
				productExcelObj.setPriceType("L");
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
		/* if(priceGrids.size() == 1){
	    	 priceGrids = removeBasePriceConfig(priceGrids);
	     }*/
		if(!StringUtils.isEmpty(description.toString())){
			 String finalDescription = removeSpecialCharDescription(description.toString());
	    	   if(finalDescription.length()> 800){
	    		   String finalDesc = CommonUtility.getStringLimitedChars(finalDescription.toString(), 800);
	    			   String additionalInfo = finalDescription.substring(801);   
	    			   productExcelObj.setDescription(finalDesc);
	    			   productExcelObj.setAdditionalProductInfo(additionalInfo);
	    	   } else {
	    		   productExcelObj.setDescription(finalDescription.toString());
	    	   }
	       }
	       if(xid.contains("A-OR0421")){
	    	   List<Shape> shapeList  = getProductShape(xid);
	    	   productConfigObj.setShapes(shapeList);
	       }
	       if(!CollectionUtils.isEmpty(productNumbersMap)){
	    	   if(productNumbersMap.size() > 1){// if product contain repeat rows only
	    		   List<ProductNumber> productNumberList = merchAttributeParser
							.getProductNumbers(productNumbersMap);
					productExcelObj.setProductNumbers(productNumberList);
					productExcelObj.setAsiProdNo("");
	    	   }	
	       }
	       if(!CollectionUtils.isEmpty(colorsList)){
	    	   List<Color> colorList = merchAttributeParser.getProductColor(colorsList);
	    	   productConfigObj.setColors(colorList);
	       }
	       if(!merchAttributeParser.isSpecialBasePriceGrid(xid)){
				if(!StringUtils.isEmpty(grid_1Prices.toString())){
					// grid based on Debossed 
		    	   String imprintMethodType = "";
		    	   if(!StringUtils.isEmpty(grid_2Prices.toString())){
		    		   imprintMethodType = "Debossed";
		    	   } 
					productExcelObj.setProductConfigurations(productConfigObj);
					productExcelObj.setPriceGrids(priceGrids);
						productExcelObj = merchAttributeParser.getBasePriceColumns(grid_1Quantity.toString(),
								grid_1Prices.toString(), grid_1Discounts.toString(), priceGridName_1, priceInclude_upcharge,
								productExcelObj, imprintMethodType);
					productConfigObj = productExcelObj.getProductConfigurations();
					priceGrids = productExcelObj.getPriceGrids();
				}
				if(!StringUtils.isEmpty(grid_2Prices.toString())){
					// grid based on Silkscreen 
					productExcelObj.setProductConfigurations(productConfigObj);
					productExcelObj.setPriceGrids(priceGrids);
					productExcelObj = merchAttributeParser.getBasePriceColumns(grid_2Quantity.toString(),
							grid_2Prices.toString(), grid_2Discounts.toString(), priceGridName_2, priceInclude_2,
							productExcelObj, "Silkscreen");
					productConfigObj = productExcelObj.getProductConfigurations();
					priceGrids = productExcelObj.getPriceGrids();
				}
			}
				ShippingEstimate shippingEstimation = merchAttributeParser
						.getProductShippingEstimates(noOfItems, shippingDimention.toString(), shippingWeight);
				productConfigObj.setShippingEstimates(shippingEstimation);
				productExcelObj.setPriceGrids(priceGrids);
		 	productExcelObj.setProductConfigurations(productConfigObj);
		 	productExcelObj.setProductRelationSkus(listProductSkus);
		 	productExcelObj.setDeliveryOption("");// it is used for imprintmethod value reference,that's cause again reset value
                                                    //because there is no deliveryoption values
		 		int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId, environmentType);
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
		if(StringUtils.isEmpty(productXid) || productXid.equals("#N/A")){
		     xidCell = row.getCell(1);
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
	public boolean isRepeateColumn(int columnIndex,String xid){
  //merchAttributeParser.
		if(merchAttributeParser.isSpecialBasePriceGrid(xid)){
			if (columnIndex != 2 && columnIndex != 28 && columnIndex != 25 && columnIndex != 26
					&& (columnIndex >= 60 || columnIndex < 84)) {
				return ApplicationConstants.CONST_BOOLEAN_TRUE;
			}	
		} else {
			if (columnIndex != 2 && columnIndex != 28 ){
				return ApplicationConstants.CONST_BOOLEAN_TRUE;
			}
		}
		
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	private String specialCaseXid(String xid,Row row){
		Cell xidCell =  row.getCell(7);
		String description = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(description.contains("tree-shaped")){
			xid = xid+"tree";
		} else if(description.contains("orb-shaped")){
			xid = xid+"orb";
		} else if(description.contains("bell-shaped")){
			xid = xid+"bell";
		}  else if(description.contains("round")){
			xid = xid+"round";
		} else {
			xid = xid+"round";
		}
		return xid;
	}
	private List<Shape> getProductShape(String val){
		if(val.contains("tree")){
			val = "tree";
		} else if(val.contains("orb")){
			val = "orb";
		} else if(val.contains("round")){
			val="round";
		} else {// this is first case
			val = "bell";
		}
		 List<Shape> shapeList = merchAttributeParser.getProductShape(val);
		 return shapeList;
	}
	private String removeSpecialCharDescription(String desc){
		desc = desc.replaceAll("’", "'");
		desc = desc.replaceAll("”", "\"");
		desc = desc.replaceAll("¼", "1/4");
		desc = desc.replaceAll("½", "1/2");
		return desc;
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
