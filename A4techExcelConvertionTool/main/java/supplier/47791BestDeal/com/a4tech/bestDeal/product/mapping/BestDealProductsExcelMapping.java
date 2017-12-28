package com.a4tech.bestDeal.product.mapping;

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

import com.a4tech.bestDeal.product.parser.BestDealAttributeParser;
import com.a4tech.bestDeal.product.parser.BestDealPriceGridParser;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.Shape;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class BestDealProductsExcelMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(BestDealProductsExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private BestDealAttributeParser productAttributeParser;
	private BestDealPriceGridParser priceGridParser;
	
	@Override
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  productAttributeParser.getProductTradeName("BannerBams");
 		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		
		StringJoiner listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfPrices   = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String priceInclude = null;
		String xid = null;
		String basePriceName = null;
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
				
				int columnIndex = cell.getColumnIndex();
				if(columnIndex  == 4){
					xid = CommonUtility.getCellValueStrinOrInt(cell);
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
								listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							    listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							    listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								productConfigObj = new ProductConfigurations();
																
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
						    	 productConfigObj=productExcelObj.getProductConfigurations();
						     }
							
					 }
				}
				
				switch (columnIndex) {
				case 4://xid
					  productExcelObj.setExternalProductId(xid);
					 break;
				case 5://Name
					String prdName = cell.getStringCellValue().trim();
					basePriceName = prdName;
					productExcelObj.setName(prdName);
					  break;
				case 6://summery
					String summary = cell.getStringCellValue();
					productExcelObj.setSummary(summary);	
				    break;
				case 7://description
					 String desc = cell.getStringCellValue();
					 productExcelObj.setDescription(desc.trim());
					  break;
				case 8://keywords
					  String keyWords = cell.getStringCellValue();
					  if(!StringUtils.isEmpty(keyWords)){
						  List<String> listOfKeywords = productAttributeParser.getProductKeywords(keyWords);
						  productExcelObj.setProductKeywords(listOfKeywords);
					  }
					break;
					
				case 9: //  ignore
					
					break;
					
				case 10: //  quantity
				case 13:
				case 16:
				case 19: 
				case 22: 
				case 25: 
				case 28:
				case 31: 
					String priceQty = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(priceQty)){
						listOfQuantity.add(priceQty);
					}
					break;
					
				case 11: // price
				case 14:
				case 17:
				case 20:
				case 23:
				case 26:
				case 29:
				case 32:
					String listPrice = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(listPrice)){
						listOfPrices.add(listPrice);
					}
					break;
					
				case 12: // dis code
				case 15:
				case 18:
				case 21:
				case 24:
				case 27:
				case 30:
				case 33:
					String discount = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(discount)){
						listOfDiscount.add(discount);
					}
					break;
				case 34: // ignore
					
					
					break;
				case 35://ignore
					break;
					
				case 36:// ignore
					break;
					
				case 37:
					priceInclude = cell.getStringCellValue();
					 break;
				case 38:
					String color =cell.getStringCellValue();
					if(!StringUtils.isEmpty(color)){
						List<Color> listOfColor = productAttributeParser.getProductColor(color.trim());
						productConfigObj.setColors(listOfColor);
					}
					break;
				case 39:
				String shapeValue=cell.getStringCellValue();
					if(!StringUtils.isEmpty(shapeValue)){
					   List<Shape>	listOfShape = productAttributeParser.getProductShapes(shapeValue);
					   if(listOfShape!= null){
						   productConfigObj.setShapes(listOfShape);
					   }
					}
					break;
				case 40: // size
				String sizeValue=cell.getStringCellValue();
				   if(!StringUtils.isEmpty(sizeValue)){
					   Size size = productAttributeParser.getProductSize(sizeValue);
					   productConfigObj.setSizes(size);
				   }
					break;
				case 41: // material
				String materialValue=cell.getStringCellValue();
				if(!StringUtils.isEmpty(materialValue)){
					List<Material> listOfMaterial = productAttributeParser.getProductMaterial(materialValue);
					productConfigObj.setMaterials(listOfMaterial);
				}
					break;
				case 42: // trade Names ignore
					String tradeNames = cell.getStringCellValue();
					/*if(!StringUtils.isEmpty(tradeNames)){
						List<TradeName> listOfTradeNames = productAttributeParser.getProductTradeName(tradeNames);
						productConfigObj.setTradeNames(listOfTradeNames);
					}*/
					break;
				case 43: //origin
					  String country = cell.getStringCellValue();
					     if(!StringUtils.isEmpty(country)){
					    	 List<Origin> listOfOrigin = productAttributeParser.getProductOrigin(country);
					    	 if(listOfOrigin != null){
					    		 productConfigObj.setOrigins(listOfOrigin);
					    	 } 
					     }
					break;
				case 44: // production Time
					     String prdTime = CommonUtility.getCellValueStrinOrInt(cell);
					     if(!StringUtils.isEmpty(prdTime)){
					    	 List<ProductionTime> listOfPrdTime = productAttributeParser.getProductionTime(prdTime);
					    	 productConfigObj.setProductionTime(listOfPrdTime);
					     }
					break;
				case 45:// rush service
					String rushValue = cell.getStringCellValue();
					if(!StringUtils.isEmpty(rushValue)){
						productExcelObj = productAttributeParser.getProductRushTimeAndUpCharge
								                                 (productExcelObj, rushValue,productConfigObj,priceGrids);
					}
					break;
				case 46: // imprint method
					String imprintValue = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintValue)){
						List<ImprintMethod> listOfImprMethod = productAttributeParser.getProductImprintMethods
								                                                                  (imprintValue);
						productConfigObj.setImprintMethods(listOfImprMethod);
					}
					break;
				case 47: // price include same as case 34
					break;
				case 48: // Safety Warnings
					  // there is no data for this column
					break;
				case 49: // product options
					// there is no data for this column
					break;
				case 50: // Product Notes
					String values = cell.getStringCellValue();
					if(!StringUtils.isEmpty(values)){
						productExcelObj = productAttributeParser.getproductLineNameShippingFob
																			(values, productExcelObj,productConfigObj);
					}
					break;
				case 51: //imprint Options
					    // there is no data for this column
					break;
				case 52: // imprint notes
					String imprintSize = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintSize)){
						productExcelObj = productAttributeParser.getImprintSizeAndAddlocation(imprintSize, productExcelObj);
						priceGrids = productAttributeParser.getAdditionalUpchargeGPriceGrid(imprintSize,
								                                                                  productExcelObj.getPriceGrids());
						productExcelObj.setPriceGrids(priceGrids);
					}
					break;
				case 53: //Price Notes
					String priceNotes = cell.getStringCellValue();
					if(!StringUtils.isEmpty(priceNotes)){
						productExcelObj.setCanOrderLessThanMinimum(ApplicationConstants.CONST_BOOLEAN_TRUE);
						priceGrids = productAttributeParser.getLessThanMiniUpCharge(priceNotes, 
								                                           productExcelObj.getPriceGrids());
					}
					
					break;
								
							
			}  // end inner while loop
					 
		}
				productExcelObj.setPriceType("L");
				String qurFlag = "n"; // by default for testing purpose
				if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
					priceGrids = priceGridParser.getPriceGrids(listOfPrices.toString(), 
							listOfQuantity.toString(), listOfDiscount.toString(), "USD",
							         priceInclude, true, qurFlag, basePriceName,"",priceGrids);	
				}
				listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			    listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			    listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() );		 
		}
		}
		workbook.close();
         productExcelObj.setPriceGrids(priceGrids);
        productExcelObj.setProductConfigurations(productConfigObj);
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

	public String getProductXid(Row row){
		Cell xidCell =  row.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid)){
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
		
		if(columnIndex != 5 && columnIndex != 6 && columnIndex != 8){
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
	
	 public BestDealAttributeParser getProductAttributeParser() {
			return productAttributeParser;
		}

		public void setProductAttributeParser(
				BestDealAttributeParser productAttributeParser) {
			this.productAttributeParser = productAttributeParser;
		}
		public BestDealPriceGridParser getPriceGridParser() {
			return priceGridParser;
		}

		public void setPriceGridParser(BestDealPriceGridParser priceGridParser) {
			this.priceGridParser = priceGridParser;
		}

}
