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
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.sage.product.parser.CatalogParser;
import com.a4tech.sage.product.parser.ColorParser;
import com.a4tech.sage.product.parser.DimensionParser;
import com.a4tech.sage.product.parser.ImprintMethodParser;
import com.a4tech.sage.product.parser.OriginParser;
import com.a4tech.sage.product.parser.PackagingParser;
import com.a4tech.product.DCProducts.parser.DCPriceGridParser;
import com.a4tech.product.DCProducts.parser.ProductOriginParser;
import com.a4tech.sage.product.parser.RushTimeParser;
import com.a4tech.sage.product.parser.ShippingEstimateParser;
import com.a4tech.util.ApplicationConstants;


public class DCProductsExcelMapping {
	
	private static final Logger _LOGGER = Logger.getLogger(DCProductsExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	ProductDao productDaoObj;
	DCPriceGridParser dcPriceGridParser;
	 
	 
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
		
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
		
		
		 List<Image> imgList = new ArrayList<Image>();
		List<Origin> originList =new ArrayList<Origin>();
		List<String> categories = new ArrayList<String>();	
		List<ProductionTime> listOfProductionTime = new ArrayList<ProductionTime>();
		List<Origin> origin = new ArrayList<Origin>();
		ProductOriginParser originParser=new ProductOriginParser();
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
				int columnIndex = cell.getColumnIndex();
				
				if(columnIndex + 1 == 1){
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						xid = cell.getStringCellValue();
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						xid = String.valueOf((int)cell.getNumericCellValue());
					}else{
						
					}
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
								
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    }
							productExcelObj = new Product();
					 }
				}
				
				switch (columnIndex + 1) {

				case 1://SuplItemNo
					 String asiProdNo = null;
					    if(cell.getCellType() == Cell.CELL_TYPE_STRING){ 
					      asiProdNo = String.valueOf(cell.getStringCellValue());
					    }else if
					     (cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
					      asiProdNo = String.valueOf((int)cell.getNumericCellValue());
					     }
					     productExcelObj.setAsiProdNo(asiProdNo);	
			   
					 break;
				case 2://ItemName
				    productName = cell.getStringCellValue();
					if(!StringUtils.isEmpty(productName)){
					productExcelObj.setName(cell.getStringCellValue());
					}else{
						productExcelObj.setName(ApplicationConstants.CONST_STRING_EMPTY);
					}
						
					  break;
				case 3://SuplDisplayNo
                  
     					break;
				case 4://Description
					String description = cell.getStringCellValue();
					if(!StringUtils.isEmpty(description)){
					productExcelObj.setDescription(description);
					}else{
						productExcelObj.setDescription(ApplicationConstants.CONST_STRING_EMPTY);
					}
					
				    break;
					
				case 5://AddInfo
					String additionalProductInfo = cell.getStringCellValue();
					if(!StringUtils.isEmpty(additionalProductInfo)){
					productExcelObj.setAdditionalProductInfo(additionalProductInfo);
					}else{
						productExcelObj.setAdditionalProductInfo(ApplicationConstants.CONST_STRING_EMPTY);
					}
					
					break;
					
				case 6: //  DistributorOnlyInfo
					String Distributorcomment = cell.getStringCellValue();
					if(!StringUtils.isEmpty(Distributorcomment)){
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
					
				case 8: // Size
					
					
					break;
					
				case 9: //DisplayWeight
							
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
					productConfigObj.setOrigins(originList);
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
						if(cell.getCellType() == Cell.CELL_TYPE_STRING){
							prodTimeLo = cell.getStringCellValue();
						}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
							prodTimeLo = String.valueOf(cell.getNumericCellValue());
						}else{
							
						}
						productionTime.setBusinessDays(prodTimeLo);
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
			// set  product configuration objects
			List<String> listOfCategories = new ArrayList<String>();
			listOfCategories.add("USB/FLASH DRIVES");
			productExcelObj.setCategories(listOfCategories);
			String productDescription ="Phone Holder USB 2.0 Flash Drive";
			productExcelObj.setDescription(productDescription);
			
			
			 // end inner while loop
			productExcelObj.setPriceType("L");
			if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
				priceGrids = dcPriceGridParser.getPriceGrids(listOfPrices.toString(),listOfNetPrice.toString(), 
						         listOfQuantity.toString(), listOfDiscount.toString(), "USD",
						         "", true, "N", productName,"",priceGrids);	
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
			
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() );		 
		}
		}
		workbook.close();
		
		 	productExcelObj.setPriceGrids(priceGrids);
		 	productExcelObj.setProductConfigurations(productConfigObj);
	
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
	       productDaoObj.getErrorLog(asiNumber,batchId);
	       return finalResult;
		}catch(Exception e){
			_LOGGER.error("Error while Processing excel sheet ");
			return finalResult;
		}finally{
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet");
	
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
	
	
}
