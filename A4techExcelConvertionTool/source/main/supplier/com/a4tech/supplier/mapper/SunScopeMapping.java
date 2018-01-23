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
import org.omg.CORBA.COMM_FAILURE;
import org.springframework.util.StringUtils;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
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

import parser.sunscope.SunScopeAttributeParser;
import parser.sunscope.SunScopeQuantityMapping;

public class SunScopeMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(SunScopeMapping.class);
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private SunScopeAttributeParser  sunScopeAttributeParser;
	
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
		  StringBuilder shippingWeightAndNoItems = new StringBuilder();
		  StringBuilder shippingDimensions = new StringBuilder();
		  Set<String> imprintSizes = new HashSet<>();
		  String imprintMethod = "";
		  Map<String, StringBuilder> pricesMap = new HashMap<>();
		  Set<String> imprintMethodValues = new HashSet<>();
 		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		//String priceInclude = null;
		String xid = null;
		 columnIndex = 0;
		StringBuilder productDescription = new StringBuilder();
		StringBuilder shippingAdditionalInfo = new StringBuilder();
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
							 ShippingEstimate shippingEstmationValues = sunScopeAttributeParser
										.getShippingEstimation(shippingDimensions.toString(),shippingWeightAndNoItems.toString());
								productConfigObj.setShippingEstimates(shippingEstmationValues);
									List<ImprintSize> imprintSizeList = sunScopeAttributeParser
											.getProductImprintSize(imprintSizes);
									productConfigObj.setImprintSize(imprintSizeList);
									List<ImprintMethod> imprintMethodList = sunScopeAttributeParser
											.getProductImprintMethod(imprintMethodValues);
									productConfigObj.setImprintMethods(imprintMethodList);
									priceGrids = sunScopeAttributeParser.getPriceAndUpcharges(pricesMap, priceGrids);
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
								shippingWeightAndNoItems = new StringBuilder();
								shippingDimensions = new StringBuilder();
								imprintSizes = new HashSet<>();
								imprintMethod = "";
								pricesMap = new HashMap<>();
								imprintMethodValues = new HashSet<>();
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
						    	 productExcelObj = sunScopeAttributeParser.keepExistingProductData(productExcelObj);
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
					String asiPrdNo = cell.getStringCellValue();
					productExcelObj.setAsiProdNo(asiPrdNo);
					  break;
				case 3:
				case 4: 
				case 5:
				case 6:
				case 7:
					//ignore as per feedback
					break;
				case 8://prdName
					productExcelObj.setName(cell.getStringCellValue());
					break;
				case 9:
					productExcelObj.setDescription(cell.getStringCellValue());
					break;
				case 10://colors
					String colorVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(colorVal)){
						List<Color> colorsList = sunScopeAttributeParser.getProductColor(colorVal);
						productConfigObj.setColors(colorsList);
					}
					break;
				case 11://Size
					String sizeVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(sizeVal)){
						Size sizeObj = sunScopeAttributeParser.getProductSize(sizeVal);
						productConfigObj.setSizes(sizeObj);
					}
					break;
				case 12: //item weight
					String itemWeightVal = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(itemWeightVal)){
					  Volume itemWeight = sunScopeAttributeParser.getItemWeightvolume(itemWeightVal);
					  productConfigObj.setItemWeight(itemWeight);
					}
					break;
				case 13://package
					String packageVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(packageVal)){
								List<Packaging> packagingList = sunScopeAttributeParser.getProductPackaging(packageVal);
						productConfigObj.setPackaging(packagingList);
					}
					break;
				case 14:	
					 String shiQty = CommonUtility.getCellValueStrinOrInt(cell);
	            	  if(!StringUtils.isEmpty(shiQty)){
	            		  shippingWeightAndNoItems.append("shippingQty:").append(shiQty);
	            	  }
					break;
				case 15: //shipping wt
					String shiWe = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(shiWe)){
	            		  shippingWeightAndNoItems.append(",").append("shippingWt:").append(shiWe);
	            	  }
					break;
				case 16://shipping Length
					String length = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(length)){
						shippingDimensions.append(length);
	            	  }
					break;
				case 17:
					String width = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(width)){
						shippingDimensions.append("x").append(width);
	            	  }
					  break;
				case 18: 
					String height = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(height)){
						shippingDimensions.append("x").append(height);
	            	  }
					break;
				 case 19:// Imprint Method A
					 imprintMethod = cell.getStringCellValue();
					 imprintMethodValues.add(imprintMethod);
					break;
				case 20: //Base Price A
				case 21: 
				case 22:
				case 23:
				case 24: 
				case 25: 
				case 26:
				case 27:
					pricesMap = getPriceAndQty(columnIndex + 1, cell, pricesMap, imprintMethod,"");
					 break;
				case 28:
				case 41:
				case 54: 
				case 67://imprintsize
					String imprintSize = cell.getStringCellValue();
							if (!StringUtils.isEmpty(imprintSize) && !imprintSize.equals("N.A.")
									&& !imprintSize.equals("Blank")) {
								imprintSizes.add(imprintSize);
							}
					break;
				case 29://price include
					String priceIncludeA = cell.getStringCellValue();
					if(!StringUtils.isEmpty(priceIncludeA)){
						pricesMap = getPriceAndQty(columnIndex + 1, cell, pricesMap, imprintMethod,priceIncludeA);
						//pricesMap = addRunAndSetupColumns(priceIncludeA, "priceInclude", imprintMethod, pricesMap);
					}
					  break;
				case 30: //Run Charge
					String runChargeA = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(runChargeA) && !runChargeA.equals("N.A.")){
						pricesMap = addRunAndSetupColumns(runChargeA, "runCharge", imprintMethod, pricesMap);
					}
					 break;
				case 31://Setup charge
					String setUpCharA = CommonUtility.getCellValueDouble(cell);
							if (!StringUtils.isEmpty(setUpCharA) && !setUpCharA.equals("a")
									&& !setUpCharA.equals("N.A.")) {
								pricesMap = addRunAndSetupColumns(setUpCharA, "setUp", imprintMethod, pricesMap);
						
					}
					 break;
				case 32://ImprintMethod B 
					imprintMethod = cell.getStringCellValue();
					imprintMethodValues.add(imprintMethod);
					  break;
				case 33://Base Price B
				case 34: 
				case 35:
				case 36:
				case 37:
				case 38:
				case 39:
				case 40:
					pricesMap = getPriceAndQty(columnIndex + 1, cell, pricesMap, imprintMethod,"");
					break;
				case 42: //price Include B
					String priceIncludeB = cell.getStringCellValue();
					if(!StringUtils.isEmpty(priceIncludeB)){
						pricesMap = getPriceAndQty(columnIndex + 1, cell, pricesMap, imprintMethod,priceIncludeB);
					}
					  break;
				case 43:// Run Cha B
					String runChargeB = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(runChargeB) && !runChargeB.equals("0.00") && !runChargeB.equals("0")){
						pricesMap = addRunAndSetupColumns(runChargeB, "runCharge", imprintMethod, pricesMap);
					}
					  break;
				case 44: //set up B
					String setUpCharB = CommonUtility.getCellValueDouble(cell);
					if (!StringUtils.isEmpty(setUpCharB) &&!setUpCharB.equals("0.00") && !setUpCharB.equals("0")) {
						pricesMap = addRunAndSetupColumns(setUpCharB, "setUp", imprintMethod, pricesMap);
				
			         }
					 break;
				case 45://imprint Method C
					imprintMethod = cell.getStringCellValue();
					imprintMethodValues.add(imprintMethod);
					break;
				case 46://Base Price C
				case 47:
				case 48:
				case 49:
				case 50:
				case 51:
				case 52: 
				case 53:
					pricesMap = getPriceAndQty(columnIndex + 1, cell, pricesMap, imprintMethod,"");
					  break;
				case 55://price include C
					String priceIncludeC = cell.getStringCellValue();
					if(!StringUtils.isEmpty(priceIncludeC)){
						pricesMap = getPriceAndQty(columnIndex + 1, cell, pricesMap, imprintMethod,priceIncludeC);
					}
					break;
				case 56://Run cha C
					String runChargeC = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(runChargeC) && !runChargeC.equals("0.00") && !runChargeC.equals("0")){
						pricesMap = addRunAndSetupColumns(runChargeC, "runCharge", imprintMethod, pricesMap);
					}
				    break;
				case 57://Set up C
					String setUpCharC = CommonUtility.getCellValueDouble(cell);
					if (!StringUtils.isEmpty(setUpCharC)  && !setUpCharC.equals("0.00") && !setUpCharC.equals("0")) {
						pricesMap = addRunAndSetupColumns(setUpCharC, "setUp", imprintMethod, pricesMap);
			         }
					  break;
				case 58://imprint Method D
					imprintMethod = cell.getStringCellValue();
					imprintMethodValues.add(imprintMethod);
					break;
				case 59://Base Price D
				case 60:
				case 61:
				case 62: 
				case 63:
				case 64: 
				case 65:
				case 66://
					pricesMap = getPriceAndQty(columnIndex + 1, cell, pricesMap, imprintMethod,"");
				    break;
				case 68://price include D
					String priceIncludeD = cell.getStringCellValue();
					if(!StringUtils.isEmpty(priceIncludeD)){
						pricesMap = getPriceAndQty(columnIndex + 1, cell, pricesMap, imprintMethod,priceIncludeD);
					}
					break;
				case 69://Run Cha D
					String runChargeD = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(runChargeD) && !runChargeD.equals("0.00") && !runChargeD.equals("0")){
						pricesMap = addRunAndSetupColumns(runChargeD, "runCharge", imprintMethod, pricesMap);
					}
					break;
				case 70://Set cha D
					String setUpCharD = CommonUtility.getCellValueDouble(cell);
					if (!StringUtils.isEmpty(setUpCharD)) {
						pricesMap = addRunAndSetupColumns(setUpCharD, "setUp", imprintMethod, pricesMap);
	           		}
					break;
			}  // end inner while loop		 
		}
				productExcelObj.setPriceType("L");
				/*if(!StringUtils.isEmpty(priceVal)){
					priceGrids = dacassoPriceGridParser.getBasePriceGrid(priceVal,"1","P", "USD",
							         "", true, false, "","",priceGrids,"","");	
				}*/
				
			}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);		 
				ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: "
				+e.getMessage()+" at column number(increament by 1)"+columnIndex);
				productDaoObj.save(apiResponse.getErrors(),
						productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);
		}
		}
		workbook.close();
		ShippingEstimate shippingEstmationValues = sunScopeAttributeParser
				.getShippingEstimation(shippingDimensions.toString(),shippingWeightAndNoItems.toString());
		productConfigObj.setShippingEstimates(shippingEstmationValues);
			List<ImprintSize> imprintSizeList = sunScopeAttributeParser
					.getProductImprintSize(imprintSizes);
			productConfigObj.setImprintSize(imprintSizeList);
			List<ImprintMethod> imprintMethodList = sunScopeAttributeParser
					.getProductImprintMethod(imprintMethodValues);
			productConfigObj.setImprintMethods(imprintMethodList);
			priceGrids = sunScopeAttributeParser.getPriceAndUpcharges(pricesMap, priceGrids);
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
	private Map<String, StringBuilder> getPriceAndQty(int index,Cell cell,Map<String,StringBuilder> existingMap,String imprintMethod,String priceInclude){
		StringBuilder priceQtyVals = existingMap.get(imprintMethod);
		String price = CommonUtility.getCellValueDouble(cell);
		if(StringUtils.isEmpty(priceInclude) && !StringUtils.isEmpty(price)){
			String qty = SunScopeQuantityMapping.getPriceQty(index);
			String priceQty = qty+":"+price;
			if(priceQtyVals ==  null){
				priceQtyVals = new StringBuilder();
			}
			priceQtyVals.append(priceQty).append("_");
		}
			if(!StringUtils.isEmpty(priceInclude)){
			priceQtyVals.append("###").append(priceInclude);
		}
		existingMap.put(imprintMethod, priceQtyVals);
		return existingMap;
	}

	private Map<String, StringBuilder> addRunAndSetupColumns(String val, String columnType, String imprintMethod,
			Map<String, StringBuilder> existingMap) {
		StringBuilder vals = existingMap.get(imprintMethod);
		if(vals ==  null){
			vals = new StringBuilder();
		}
		vals.append("__").append(columnType).append(":").append(val);
		existingMap.put(imprintMethod, vals);
		return existingMap;
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
	public SunScopeAttributeParser getSunScopeAttributeParser() {
		return sunScopeAttributeParser;
	}
	public void setSunScopeAttributeParser(SunScopeAttributeParser sunScopeAttributeParser) {
		this.sunScopeAttributeParser = sunScopeAttributeParser;
	}
}
