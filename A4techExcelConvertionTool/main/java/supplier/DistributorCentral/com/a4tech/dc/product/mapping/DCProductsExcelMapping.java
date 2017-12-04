package com.a4tech.dc.product.mapping;

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

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.DCProducts.parser.ShippingEstimationParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.Shape;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.product.DCProducts.parser.DCPriceGridParser;
import com.a4tech.product.DCProducts.parser.DimensionAndShapeParser;
import com.a4tech.product.DCProducts.parser.ProductOriginParser;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;


public class DCProductsExcelMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(DCProductsExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	ProductDao productDaoObj;
	DCPriceGridParser dcPriceGridParser;
	private DimensionAndShapeParser dimensionAndShapeParser;
	private ShippingEstimationParser shippingEstimationParser;
	private ProductOriginParser originParser;

	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  String productId = null;
		  String currencyType = null;
		  String priceQurFlag = null;
		  String priceType    = null;
		  String basePriceName = null;
		
		  String upChargeName = null;
		  String upChargeQur = null;
		  String upchargeType = null;
		  String upChargeDetails = null;
		  String upChargeLevel = null;
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  ShippingEstimate shippingItem = null;
		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		
		
		StringBuilder listOfQuantity = new StringBuilder();
		StringBuilder listOfPrices = new StringBuilder();
		StringBuilder listOfNetPrice = new StringBuilder();
		StringBuilder listOfDiscount = new StringBuilder();
		StringBuilder basePriceCriteria =  new StringBuilder();
		StringBuilder UpCharQuantity = new StringBuilder();
		StringBuilder UpCharPrices = new StringBuilder();
		StringBuilder UpchargeNetPrice = new StringBuilder();
		StringBuilder UpCharDiscount = new StringBuilder();
		StringBuilder UpCharCriteria = new StringBuilder();
		String		priceCode = null;
		StringBuilder pricesPerUnit = new StringBuilder();
		String quoteUponRequest  = null;
		StringBuilder priceIncludes = new StringBuilder();
		String quantity = null;
		String listPrice = null;
		String netPrice = null;
		String discCode=null;
		String productName = null;
		String CountryOfManufactureGUID = null;
		String asiProdNo =null;
		 List<Image> imgList = new ArrayList<Image>();
		List<Origin> originList =new ArrayList<Origin>();
		List<String> categories = new ArrayList<String>();	
		List<ProductionTime> listOfProductionTime = new ArrayList<ProductionTime>();
		List<Origin> origin = new ArrayList<Origin>();
		int columnIndex = 0;
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
		 
			if (nextRow.getRowNum() ==0)
				continue;
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if(productId != null){
				productXids.add(productId);
			}
			 boolean checkXid  = false;
			
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				String xid = null;
				  columnIndex = cell.getColumnIndex();
				
				if(columnIndex + 1 == 1){
					xid=CommonUtility.getCellValueStrinOrInt(cell);
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							 System.out.println("Java object converted to JSON String, written to file");
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
								
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    }
							productExcelObj = new Product();
					 }
				}
				
				switch (columnIndex + 1) {

				case 1://SuplItemNo
					 
					asiProdNo=CommonUtility.getCellValueStrinOrInt(cell);
					     productExcelObj.setAsiProdNo(asiProdNo);	
			   
					 break;
				case 2://ItemName
				    productName = cell.getStringCellValue();
				    String tempArr[]=null;
					if(!StringUtils.isEmpty(productName)){
					productExcelObj.setName(cell.getStringCellValue());
					 tempArr=productName.split(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
					xid=asiProdNo+ApplicationConstants.CONST_VALUE_TYPE_SPACE+tempArr[0];
					productExcelObj.setExternalProductId(xid);
					}else{
						productExcelObj.setName(ApplicationConstants.CONST_STRING_EMPTY);
						xid=asiProdNo+ApplicationConstants.CONST_VALUE_TYPE_SPACE+asiNumber;
						productExcelObj.setExternalProductId(xid);
					}
						
					  break;
				case 3://SuplDisplayNo
                  
     					break;
				case 4://Description
					String description = cell.getStringCellValue();
					if(!StringUtils.isEmpty(description)){
						description=removeSpecialChar(description);
					productExcelObj.setDescription(description);
					}else{
						productExcelObj.setDescription(ApplicationConstants.CONST_STRING_EMPTY);
					}
					
				    break;
					
				case 5://AddInfo
					String additionalProductInfo = cell.getStringCellValue();
					if(!StringUtils.isEmpty(additionalProductInfo)){
					additionalProductInfo=removeSpecialChar(additionalProductInfo);
					productExcelObj.setAdditionalProductInfo(additionalProductInfo);
					}else{
						productExcelObj.setAdditionalProductInfo(ApplicationConstants.CONST_STRING_EMPTY);
					}
					
					break;
					
				case 6: //  DistributorOnlyInfo
					String Distributorcomment = cell.getStringCellValue();
					if(!StringUtils.isEmpty(Distributorcomment)){
					Distributorcomment=removeSpecialChar(Distributorcomment);
					productExcelObj.setDistributorOnlyComments(Distributorcomment);
					}else{
						productExcelObj.setDistributorOnlyComments(ApplicationConstants.CONST_STRING_EMPTY);
					}
					
					
					break;
					

				case 7://Categories
					String category = cell.getStringCellValue();
					if(!StringUtils.isEmpty(category)){
					String categoryArr[] = category.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					for (String string : categoryArr) {
						categories.add(string);
					}
					productExcelObj.setCategories(categories);
					}
					   
						
					break;
					
				case 8: // Sizes and Shapes
					String dimensionAndShape = cell.getStringCellValue();
					if(dimensionAndShape != null && !dimensionAndShape.isEmpty()){
						String shape = dimensionAndShape.substring(dimensionAndShape.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE)+1);
						List<Shape> listOfShapes = dimensionAndShapeParser.getShapes(shape);
						if(listOfShapes != null){
							productConfigObj.setShapes(listOfShapes);
						}
					}
					break;
					
				case 9: //DisplayWeight
					String shippingWeightValue = cell.getStringCellValue();
					if(shippingWeightValue != null && !shippingWeightValue.isEmpty()){
					shippingItem = shippingEstimationParser.getShippingEstimates(shippingWeightValue);
					if(shippingItem.getDimensions()!=null || shippingItem.getNumberOfItems()!=null || shippingItem.getWeight()!=null ){
					productConfigObj.setShippingEstimates(shippingItem);
					}
					}
					break;	
					
				case 10:  
						//OriginationZipCode
					break;
					
				case 11:  //CountryOfManufactureGUID
					CountryOfManufactureGUID=cell.getStringCellValue();
					
					break;
				
				case 12:  // CountryOfManufactureName
					
					String CountryOfManufactureName=cell.getStringCellValue();
					if(!StringUtils.isEmpty(CountryOfManufactureName)){
					originList=originParser.getOriginCriteria(CountryOfManufactureGUID,CountryOfManufactureName);
					if(!originList.isEmpty()){
					productConfigObj.setOrigins(originList);
					}
					}
					break;

				case 13: //OptionsByOptionNumber
					
					break;
					
				case 14: //OptionsByOptionName
					
					break;
					
				case 15://OptionsByOptionStyleNumber
					
					break;
				case 16: //HasImage
					
					  break;
				
				case 17: //ImageLink
					String ImageLink = cell.getStringCellValue();
					if(!StringUtils.isEmpty(ImageLink)){
                     Image ImgObj= new Image();
                     ImgObj.setImageURL(ImageLink);
                     ImgObj.setRank(ApplicationConstants.CONST_INT_VALUE_ONE);
                     ImgObj.setIsPrimary(ApplicationConstants.CONST_BOOLEAN_TRUE);
                     
                     imgList.add(ImgObj);
                     productExcelObj.setImages(imgList);		
					}
					break;
				
				 case 18: //ProductionTime
					 String prodTimeLo = null;
						ProductionTime productionTime = new ProductionTime();
						prodTimeLo=CommonUtility.getCellValueStrinOrInt(cell);
						productionTime.setBusinessDays(prodTimeLo.replace(".0", ApplicationConstants.CONST_STRING_EMPTY).trim());
						productionTime.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
						listOfProductionTime.add(productionTime);
					   productConfigObj.setProductionTime(listOfProductionTime);
				
					break;
		//--------------------------------------------------------------			

				case 20:
				case 21:
				case 22:
				case 23: 
				case 24:  // Quantities
				case 25: 
				case 26: 
				case 27: 
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						quantity = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(quantity)){
				        	 listOfQuantity.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						int quantityInt = (int)cell.getNumericCellValue();
				         if(!StringUtils.isEmpty(quantityInt)){
				        	 listOfQuantity.append(quantityInt).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else{
					}
					break;
				case 28:
				case 29:
				case 30:  
				case 31:
				case 32:
				case 33:
				case 34:
				case 35:
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						netPrice = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(netPrice)){
				        	 listOfNetPrice.append(netPrice).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						double netPriceInt = (double)cell.getNumericCellValue();
				         if(!StringUtils.isEmpty(netPriceInt)){
				        	 listOfNetPrice.append(netPriceInt).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else{
					}
					    break; 
				case 36:
				case 37:       
				case 38:
				case 39:
				case 40:
				case 41:
				case 42:
				case 43:

					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						listPrice = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(listPrice)){
				        	 listOfPrices.append(listPrice).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						double listPriceD = (double)cell.getNumericCellValue();
				         if(!StringUtils.isEmpty(listPriceD)){
				        	 listOfPrices.append(listPriceD).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else{
					}
					      break;
				case 44:
				case 45: 
				case 46:
				case 47:     
				case 48:
				case 49:
				case 50:
				case 51:
					discCode = cell.getStringCellValue();
			         if(!StringUtils.isEmpty(discCode)){
			        	 listOfDiscount.append(discCode).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			         }
			         break;
							}  // end inner while loop
					 
		}
			 // end inner while loop
			productExcelObj.setPriceType(ApplicationConstants.CONST_PRICE_TYPE_CODE_LIST);
			if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
				priceGrids = dcPriceGridParser.getPriceGrids(listOfPrices.toString(),listOfNetPrice.toString(), 
						         listOfQuantity.toString(), listOfDiscount.toString(),ApplicationConstants.CONST_STRING_CURRENCY_USD,
						         ApplicationConstants.CONST_STRING_EMPTY, ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_CHAR_N, productName,ApplicationConstants.CONST_STRING_EMPTY,priceGrids);	
			}
			
			 
				/*if(UpCharCriteria != null && !UpCharCriteria.toString().isEmpty()){
					priceGrids = priceGridParser.getUpchargePriceGrid(UpCharQuantity.toString(), UpCharPrices.toString(), UpCharDiscount.toString(), UpCharCriteria.toString(), 
							 upChargeQur, currencyType, upChargeName, upchargeType, upChargeLevel, new Integer(1), priceGrids);
				}*/
				
				
				upChargeQur = null;
				UpCharCriteria = new StringBuilder();
				priceQurFlag = null;
				listOfPrices = new StringBuilder();
				UpCharPrices = new StringBuilder();
				UpCharDiscount = new StringBuilder();
				UpCharQuantity = new StringBuilder();
				listOfProductionTime=new ArrayList<ProductionTime>();
				categories=new ArrayList<String>();
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() +"columnindex"+ columnIndex);		 
		}
		}
		workbook.close();
		
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
			_LOGGER.error("Error while Processing excel sheet ,Error message: "+e.getMessage());
			return finalResult;
		}finally{
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet, Error message: "+e.getMessage());
	
			}
				_LOGGER.info("Complted processing of excel sheet ");
				_LOGGER.info("Total no of product:"+numOfProductsSuccess.size() );
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

	public DCPriceGridParser getDcPriceGridParser() {
		return dcPriceGridParser;
	}

	public void setDcPriceGridParser(DCPriceGridParser dcPriceGridParser) {
		this.dcPriceGridParser = dcPriceGridParser;
	}
	 
	public DimensionAndShapeParser getDimensionAndShapeParser() {
		return dimensionAndShapeParser;
	}

	public void setDimensionAndShapeParser(
				    DimensionAndShapeParser dimensionAndShapeParser) {
		this.dimensionAndShapeParser = dimensionAndShapeParser;
	}

	public ShippingEstimationParser getShippingEstimationParser() {
		return shippingEstimationParser;
	}

	public void setShippingEstimationParser(
			ShippingEstimationParser shippingEstimationParser) {
		this.shippingEstimationParser = shippingEstimationParser;
	}

	public ProductOriginParser getOriginParser() {
		return originParser;
	}

	public void setOriginParser(ProductOriginParser originParser) {
		this.originParser = originParser;
	}
	
	  /*
	 author :amey more
	 purpose:this method is to eliminate special chars from the string in description,additional info,DistributorOnlyInfo   
	 * */
		
		public static String removeSpecialChar(String tempValue){
			tempValue=tempValue.replaceAll("(<hr>|<STRONG>|</STRONG>|<font color=\"b5b8b9\">|</font>|</strong>|<strong>|<i>|</i>|<font color=\"b31b34\">|<BR>|</BR>|<br>|</br>| ¡|ñ|!|<font color=\"ffffff\">|<FONT>|</FONT>|<hr>)", "");
			return tempValue;
			
		}
}
