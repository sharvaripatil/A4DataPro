package com.a4tech.apparel.product.mapping;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.apparel.products.parser.ApparealAvailabilityParser;
import com.a4tech.apparel.products.parser.ApparealProductAttributeParser;
import com.a4tech.apparel.products.parser.ApparelMaterialParser;
import com.a4tech.apparel.products.parser.ApparelPriceGridParser;
import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.dataStore.ProductDataStore;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.Personalization;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductSKUConfiguration;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Volume;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class ApparelProductsExcelMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(ApparelProductsExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private ApparelPriceGridParser apparelPgParser;
	private ApparelMaterialParser apparealMaterialParser;
	private ApparealAvailabilityParser apparealAvailParser;
	private ApparealProductAttributeParser appaAttributeParser;
    @Override
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
		
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
		  Set<String> productSizeValues = new HashSet<String>(); // This Set used for product Availability
		  List<String> repeatRows = new ArrayList<>();
		  Map<String, String> colorIdMap = new HashMap<>();
 		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		
		StringJoiner listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String xid = null;
		int columnIndex=0;
		 Map<String, String> sizeBasePrice = new LinkedHashMap<>();
		 StringBuilder sizePrices = new StringBuilder();
		// String listPrice = "";
		// String priceQty  = "";
		 String finalSizes = "";
		 String sizeValue  = "";
		 Set<String> setSizes = new HashSet<>();
		 String skuColorVal = "";
		 String upcCode = "";
		 List<ProductSkus> listProductSkus = new ArrayList<>();
		 String productNo = "";
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if(nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO){
				continue;
			}
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if(xid != null){
				productXids.add(xid);
				repeatRows.add(xid);
			}
			 boolean checkXid  = false;
			
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				
				 columnIndex = cell.getColumnIndex();
				 if (columnIndex == 1) {
						xid = getProductXid(nextRow);
						checkXid = true;
					} else {
						checkXid = false;
					}
				if(columnIndex + 1 == 1){
					xid = getProductXid(nextRow);
					checkXid = true;
				}/*else{
					checkXid = false;
				}*/
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							 System.out.println("Java object converted to JSON String, written to file");
							 List<Color> listOfColor = appaAttributeParser.getProductColors(listOfColors,colorIdMap);
							 List<Personalization> listOfPersonalization  = appaAttributeParser.
									                 getPersonalizationList(productConfigObj.getPersonalization());
							 priceGrids = apparelPgParser.sizePrices(sizeBasePrice, priceGrids);
							 productConfigObj.setPersonalization(listOfPersonalization);
							    productConfigObj.setColors(listOfColor);
							 	productExcelObj.setPriceGrids(priceGrids);
							 	productConfigObj.setSizes(getProductSize(new ArrayList<Value>(sizeValues)));
							 	productExcelObj.setProductConfigurations(productConfigObj);
							 	productExcelObj.setProductRelationSkus(listProductSkus);
							 	List<Availability> listOfAvailablity = apparealAvailParser.
							 			getProductAvailablity(ProductDataStore.getColorNames(), 
							 					                              productSizeValues);
							 	productExcelObj.setAvailability(listOfAvailablity);
							 	List<Image> listOfImage = getProductImages(productExcelObj.getImages());
							 	productExcelObj.setImages(listOfImage);
									productExcelObj = appaAttributeParser.getExistingProductData(productExcelObj,
											productConfigObj.getImprintMethods());
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
								productConfigObj = new ProductConfigurations();
								listOfColors = new HashSet<>();
								sizeValues = new HashSet<>();
								listOfColor = new ArrayList<>();
								imprintMethodsList = new ArrayList<>();
								productSizeValues = new HashSet<>();
								ProductDataStore.clearProductColorSet();
								repeatRows.clear();
								colorIdMap.clear();
								sizeBasePrice = new LinkedHashMap<>();
								setSizes = new HashSet<>(); 
								listProductSkus = new ArrayList<>();
								productNo = "";
								
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    	repeatRows.add(xid);
						    }
						    productExcelObj = new Product();
     						 productExcelObj = postServiceImpl.getProduct(accessToken, xid, environmentType);
						     if(productExcelObj == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						    	 productExcelObj = appaAttributeParser.getExistingProductData(productExcelObj);
						    	 productConfigObj=productExcelObj.getProductConfigurations();
						    	 productExcelObj.setAvailability(new ArrayList<>());
						    	 priceGrids = new ArrayList<PriceGrid>();
						     }
							
					 }
				}else{
					if(productXids.contains(xid) && repeatRows.size() != 1){
						 if(isRepeateColumn(columnIndex+1)){
							 continue;
						 }
					}
				}
				
				switch (columnIndex+1) {
				case 1://not required
					 break;
				case 2:// ASI Xid
					  productExcelObj.setExternalProductId(xid);
					  break;
				case 3://style
					productNo = CommonUtility.getCellValueStrinOrInt(cell);
					  productExcelObj.setAsiProdNo(productNo);	
				    break;
				case 4://Product name
					String prdName = cell.getStringCellValue();
					prdName = prdName.replaceAll("[^a-zA-Z0-9 ]", "");
					/*if(prdName.contains(ApplicationConstants.SQUARE_SYMBOL)){
						prdName = CommonUtility.removeSpecialSymbols(prdName, 
								                                      ApplicationConstants.SQUARE_SYMBOL);
					}
					if(prdName.contains("®")){
						prdName = prdName.replaceAll("®", "");
					}*/
					if(prdName.contains("Olympian")){
						prdName = prdName.replaceAll("Olympian", "");
					}
					productExcelObj.setName(prdName.trim());
				    break;
					
				case 5://Col
					colorCustomerOderCode = CommonUtility.getCellValueStrinOrInt(cell);
					
					break;
					
				case 6: //  colorName
					String colorName = cell.getStringCellValue();
					if(!StringUtils.isEmpty(colorName)){
						colorName = colorName.trim();
						/*if(colorName.contains(ApplicationConstants.COLOR_NAME_NAVY)){
							colorName = colorName.replace(ApplicationConstants.COLOR_NAME_NAVY, 
									                       ApplicationConstants.COLOR_NAME_NAVY_BLUE);
						}*/
						listOfColors.add(colorName);
						colorIdMap.put(colorName, colorCustomerOderCode);
						skuColorVal = colorName;
						String colorNameVal ="";
						if(colorName.contains("/")){
							colorNameVal = colorName.replaceAll("/", "-");
						} else {
							colorNameVal = colorName;
						}
						ProductDataStore.saveColorNames(colorNameVal);
					}
					break;
					
				case 7://Category 
					   String category = cell.getStringCellValue();
					   if(!StringUtils.isEmpty(category)){
						   List<String> listOfCategories = appaAttributeParser.
						                      getProductCategories(category);
						   if(!CollectionUtils.isEmpty(listOfCategories)){
							   productExcelObj.setCategories(listOfCategories);   
						   }
					   }
					break;
					
				case 8: // size
					 //sizeValue = cell.getStringCellValue();
					 sizeValue = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(sizeValue)){
						sizeValue = sizeValue.trim();
						if(sizeValue.equalsIgnoreCase("XXL")){
							sizeValue = "2XL";
						} else if(sizeValue.equalsIgnoreCase("XXS")){
							sizeValue = "2XS";
						}
						productSizeValues.add(sizeValue);
						sizeValues = appaAttributeParser.getSizeValues(sizeValue, sizeValues);
					}
					break;
					
				case 9: // size group i.e Standard & Numbered
					break;
				case 10: // UPC code
					try{
					//double upcVal= cell.getNumericCellValue();
					//BigDecimal bigDecimal = new BigDecimal(upcVal);
					//long number = bigDecimal.longValue();
					  String value = CommonUtility.getCellValueStrinOrDecimal(cell);
					  //value=CommonUtility.convertExponentValueIntoNumber(value);
					  upcCode = value;
					}catch(Exception e){
						
					}
					 /*upcCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(upcCode)){
						 upcCode = CommonUtility.convertExponentValueIntoNumber(upcCode);
						productExcelObj.setUpcCode("");
					}*/
					
					break;
				case 11:
				    String  productDescription = cell.getStringCellValue();
				    productDescription = productDescription.replaceAll("[^a-zA-Z0-9.%,'\\- ]", "");
				    productDescription=productDescription.replace("ozyd2","oz/yd2");
				    productDescription=productDescription.replace("ozlyd","oz/yd2");//ozlyd
				    //oz/yd2
				   /* if(productDescription.contains(ApplicationConstants.SQUARE_SYMBOL)){
				    	productDescription = CommonUtility.removeSpecialSymbols(productDescription, 
                                                                    ApplicationConstants.SQUARE_SYMBOL);
				    }
				    if(productDescription.contains("²")){
				    	productDescription = productDescription.replaceAll("²", "");
				    }
				    if(productDescription.contains("®")){
				    	productDescription = productDescription.replaceAll("®", "");
					}*/
				    productDescription = productDescription.trim();
				    if(productDescription.contains(productNo.trim())){
				    	productDescription = productDescription.replaceAll(productNo, "");
				    }
					productExcelObj.setDescription(productDescription.trim());
					 
					break;
					
				case 12:
					break;
				case 13:
					String material = cell.getStringCellValue();
					if(!StringUtils.isEmpty(material)){
						material = material.trim();
						if(!StringUtils.isEmpty(material)){
							List<Material> listOfMaterial = apparealMaterialParser.getMaterialList(material);
							productConfigObj.setMaterials(listOfMaterial);
						}
					}
					 break;
				case 14:
					String imprintValue =cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintValue)){
						imprintValue = imprintValue.trim();
						if(!StringUtils.isEmpty(imprintValue)){
							productExcelObj.setProductConfigurations(productConfigObj);
							productExcelObj = appaAttributeParser.setImrintSizeAndLocation(
                                    imprintValue, productExcelObj);
							productConfigObj = productExcelObj.getProductConfigurations();
						}
					} 
			     break;
				case 15:
				String imprintMethod=cell.getStringCellValue();
							if (!StringUtils.isEmpty(imprintMethod)) {
								productConfigObj = appaAttributeParser
										.getImprintMethod(imprintMethod,productConfigObj);
								//productConfigObj.setImprintMethods(imprintMethodsList);
							}
					break;
				case 16:
				String packagingValue=cell.getStringCellValue();
				 if(!StringUtils.isEmpty(packagingValue)){
					 List<Packaging> listOfPackaging = appaAttributeParser.
							                           getProductPackaging(packagingValue);
					 productConfigObj.setPackaging(listOfPackaging);
				 }
					break;
				case 17:
				String productionTimeValue=cell.getStringCellValue();
				if(!StringUtils.isEmpty(productionTimeValue)){
					productConfigObj = appaAttributeParser.
							                           getProductionTimeList(productionTimeValue,productConfigObj);
					//productConfigObj.setProductionTime(listOfPrdTime);
				}
					break;
				case 18:
					break;
				case 19: 
				case 22:
				case 25://price Qty
					String priceQty = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(priceQty)){
						listOfQuantity.add(priceQty);
					}
					break;
					
					
				case 21: // list price
				case 24:
				case 27:
					
					String listPrice = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(listPrice)){
						listOfPrices.add(listPrice);
					}
					break;
				case 38: //Description3 (Imprint area)
					String itemWeightValue = CommonUtility.getCellValueDouble(cell);
							if (!StringUtils.isEmpty(itemWeightValue) && !itemWeightValue.equals("0")
									&& !itemWeightValue.equals("0.0")) {
						Volume volume = appaAttributeParser.getItemWeightvolume(itemWeightValue);
						productConfigObj.setItemWeight(volume);
					}
					
					break;
				
							
			}  // end inner while loop
					 
		}
				productExcelObj.setPriceType("N");
				String qurFlag = "n"; // by default for testing purpose
				//String basePriceName = "Bronze,Silver,Gold";
				String tempPrices = listOfPrices.toString() +"%%%"+listOfQuantity.toString();
				sizeValue = sizeValue.trim();
				boolean isDuplicate = setSizes.add(sizeValue);
				if(isDuplicate){
					sizeBasePrice.put(sizeValue, tempPrices);
				}
				if(!StringUtils.isEmpty(upcCode)){
					if(skuColorVal.contains("/")){
						skuColorVal = skuColorVal.replaceAll("/", "-");
					}
					listProductSkus = appaAttributeParser.getProductSkus(skuColorVal, sizeValue, 
							                                                       upcCode, listProductSkus);
				}
			
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
		List<Color> listOfColor = appaAttributeParser.getProductColors(listOfColors,colorIdMap);
		List<Personalization> listOfPersonalization  = appaAttributeParser.
                getPersonalizationList(productConfigObj.getPersonalization());
		 priceGrids = apparelPgParser.sizePrices(sizeBasePrice, priceGrids);
         productConfigObj.setPersonalization(listOfPersonalization);
         productConfigObj.setColors(listOfColor);
         productExcelObj.setPriceGrids(priceGrids);
         productExcelObj.setProductRelationSkus(listProductSkus);
         productConfigObj.setSizes(getProductSize(new ArrayList<Value>(sizeValues)));
        productExcelObj.setProductConfigurations(productConfigObj);
        List<Availability> listOfAvailablity = apparealAvailParser.
        		getProductAvailablity(ProductDataStore.getColorNames(), 
        				productSizeValues);
        	productExcelObj.setAvailability(listOfAvailablity);
		 	productExcelObj.setPriceGrids(priceGrids);
		 	productExcelObj.setProductConfigurations(productConfigObj);
		 	List<Image> listOfImage = getProductImages(productExcelObj.getImages());
		 	productExcelObj.setImages(listOfImage);
		 	productExcelObj = appaAttributeParser.getExistingProductData(productExcelObj,
					productConfigObj.getImprintMethods());
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId, environmentType);
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
    private List<Image> getProductImages(List<Image> images){
    	  if(!CollectionUtils.isEmpty(images)){
    		  return images;
    	  } else{
    		 return new ArrayList<>();
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

	public ApparelPriceGridParser getApparelPgParser() {
		return apparelPgParser;
	}

	public void setApparelPgParser(ApparelPriceGridParser apparelPgParser) {
		this.apparelPgParser = apparelPgParser;
	}
	public ApparelMaterialParser getApparealMaterialParser() {
		return apparealMaterialParser;
	}

	public void setApparealMaterialParser(
			ApparelMaterialParser apparealMaterialParser) {
		this.apparealMaterialParser = apparealMaterialParser;
	}

	public ApparealAvailabilityParser getApparealAvailParser() {
		return apparealAvailParser;
	}

	public void setApparealAvailParser(
			ApparealAvailabilityParser apparealAvailParser) {
		this.apparealAvailParser = apparealAvailParser;
	}
	public ApparealProductAttributeParser getAppaAttributeParser() {
		return appaAttributeParser;
	}

	public void setAppaAttributeParser(
			ApparealProductAttributeParser appaAttributeParser) {
		this.appaAttributeParser = appaAttributeParser;
	}
}
