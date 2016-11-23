package com.a4tech.product.broberry.mapping;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.a4tech.product.model.Price;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.dataStore.ProductDataStore;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.broberry.parser.BroberryProductAttributeParser;
import com.a4tech.product.broberry.parser.BroberryProductMaterialParser;
import com.a4tech.product.broberry.parser.BroberrySkuParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.AvailableVariations;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.Size;

public class BroberryExcelMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(BroberryExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	@Autowired
	ObjectMapper mapperObj;
	private BroberryProductAttributeParser broberryProductAttributeParser;
	private BroberryProductMaterialParser broberryMaterialParserObj;
	private BroberrySkuParser broberrySkuParserObj;

	public enum OPTION_SIZES {
		REG,SHT,TLL,XTL
	};

	
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
	
		StringBuilder FinalKeyword = new StringBuilder();
		StringBuilder AdditionalInfo = new StringBuilder();
		String AddionnalInfo1=null;
		List<Material> listOfMaterial =new ArrayList<Material>();
		List<String> productKeywords = new ArrayList<String>();
		List<String> listOfCategories = new ArrayList<String>();
		List<ProductSkus> ProductSkusList = new ArrayList<ProductSkus>();
		Volume itemWeight=new Volume();
		ProductSkus skuObj= new ProductSkus();
	

		String productName = null;
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  Product existingApiProduct = null;
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  List<PriceGrid> priceGrids = null;
		  Set<String> listOfColors = new HashSet<>();
		  String colorCustomerOderCode ="";
		  List<String> repeatRows = new ArrayList<>();
		  Map<String, String> colorIdMap = new HashMap<>();
		  String size=null;
		  String dimension=null;
		  String upc_no;
		  Set<String> colorSet = new HashSet<String>(); 
		  List<Color> colorList = new ArrayList<Color>();
		  HashMap<String, String>  productNumberMap=new HashMap<String, String>();
		  List<ProductNumber> pnumberList = new ArrayList<ProductNumber>();
		  String productNumber=null;
		  HashSet<String> sizeValuesSet = new HashSet<>();
		  HashSet<String> productOptionSet = new HashSet<String>(); // This Set used for product Availability
		  List<Availability> listOfAvailablity=new ArrayList<Availability>();
		  String colorValue=null;
		  String sizeValue=null;
		  String finalColorValue =null;
		  String productRelationalSku =null;
		  String MaterialValue1=null;
		  String MaterialValue2=null;
		  String Keyword1 =null;
		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		String currentValue=null;
	    String lastValue=null;
	    String productId = null;
	    String xid = null;
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
				
				int columnIndex = cell.getColumnIndex();
				if(columnIndex + 1 == 1){
					xid = CommonUtility.getCellValueStrinOrInt(cell);//getProductXid(nextRow);
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							colorList=broberryProductAttributeParser.getColorCriteria(colorSet);
								
								productConfigObj.setColors(colorList);
								pnumberList=broberryProductAttributeParser.getProductNumer(productNumberMap);
								productExcelObj.setProductNumbers(pnumberList);
								productConfigObj.setSizes(broberryProductAttributeParser.getProductSize(new ArrayList<String>(sizeValuesSet)));
								if(!CollectionUtils.isEmpty(productOptionSet)){
								productConfigObj.setOptions(broberryProductAttributeParser.getOptions(new ArrayList<String>(productOptionSet)));
								}	
								if(!CollectionUtils.isEmpty(productOptionSet)){
							   listOfAvailablity =broberryProductAttributeParser.getProductAvailablity(ProductDataStore.getSizesBrobery(),productOptionSet);
							 	productExcelObj.setAvailability(listOfAvailablity);
								}
								skuObj=broberrySkuParserObj.getProductRelationSkus(skuObj, sizeValue, finalColorValue, productRelationalSku);
								 if(!StringUtils.isEmpty(ProductSkusList)){
							    productExcelObj.setProductRelationSkus(ProductSkusList);
									 }
								  listOfMaterial = broberryMaterialParserObj.getMaterialList(MaterialValue1);
								  listOfMaterial = broberryMaterialParserObj.getMaterialList(MaterialValue2,listOfMaterial);
								 if(!StringUtils.isEmpty(listOfMaterial)){
									    productConfigObj.setMaterials(listOfMaterial);
								  }
								 if(!StringUtils.isEmpty(productKeywords)){
								productExcelObj.setProductKeywords(productKeywords);
									}
								if(CollectionUtils.isEmpty(priceGrids)){
									productExcelObj.setPriceType(ApplicationConstants.CONST_PRICE_TYPE_CODE_LIST);
									priceGrids=getPriceGrids(productName);
									}
								 	productExcelObj.setPriceGrids(priceGrids);
								 	productExcelObj.setProductConfigurations(productConfigObj);
								 	_LOGGER.info("Product Data : "
											+ mapperObj.writeValueAsString(productExcelObj));
								 	
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
								listOfColors = new HashSet<>();
								
								repeatRows.clear();
								colorSet=new HashSet<String>(); 
								colorList = new ArrayList<Color>();
								AdditionalInfo=new StringBuilder();
								productNumberMap=new HashMap<String, String>();
								pnumberList=new ArrayList<ProductNumber>();
								listOfMaterial=new ArrayList<Material>();
								sizeValuesSet = new HashSet<>();
								listOfAvailablity=new ArrayList<Availability>();
								listOfCategories=new ArrayList<String>();
								FinalKeyword=new StringBuilder();
								productOptionSet=new HashSet<String>();
								listOfMaterial=new ArrayList<Material>();
								sizeValuesSet = new HashSet<>();
								ProductSkusList = new ArrayList<ProductSkus>();
						        productKeywords = new ArrayList<String>();
								ProductDataStore.clearSizesBrobery();

						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    	repeatRows.add(xid);
						    }
						    productExcelObj = new Product();
						    existingApiProduct = postServiceImpl.getProduct(accessToken, xid); 
						     if(existingApiProduct == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						    	    productExcelObj=existingApiProduct;
									productConfigObj=productExcelObj.getProductConfigurations();
								    priceGrids = productExcelObj.getPriceGrids();
						     }
							
					 }
				}else{
					if(productXids.contains(xid) && repeatRows.size() != 1){
						 if(isRepeateColumn(columnIndex+1)){
							 continue;
						 }
					}
				}
		
				switch (columnIndex + 1) {
					case 1:
						productId=CommonUtility.getCellValueStrinOrInt(cell);
						productExcelObj.setExternalProductId(productId);
						break;
			
				case 2:// Material
				  productNumber=CommonUtility.getCellValueStrinOrInt(cell);
					  break;
				case 3://PRODUCT NAME
					
				    break;
				case 4://DESCRIPTION
					
				    break;
					
				case 5://COLOR
						colorValue=cell.getStringCellValue();
					if(!StringUtils.isEmpty(colorValue)){
						colorSet.add(colorValue);
					}
					if(!StringUtils.isEmpty(colorValue)&&!StringUtils.isEmpty(productNumber)){
						productNumberMap.put(productNumber, colorValue);
					}
					break;
					
				case 6: //  DIMENSION
					dimension=CommonUtility.getCellValueStrinOrInt(cell);
					if(StringUtils.isEmpty(dimension)){ 
						dimension=ApplicationConstants.CONST_WORD_EMPTY;
					}
					final String valueop=dimension;
					if(Arrays.stream(OPTION_SIZES.values()).anyMatch((optionName) -> optionName.name().equals(valueop))){
						productOptionSet.add(ApplicationConstants.OPTION_MAP.get(dimension));
					}
					break;
					
				case 7://SIZES
					size=CommonUtility.getCellValueStrinOrInt(cell);
					if(StringUtils.isEmpty(size)){
						size=ApplicationConstants.CONST_WORD_EMPTY;
					}
					sizeValuesSet.add(dimension+ApplicationConstants.CONST_CHAR_SMALL_X+size);
					break;
					
				case 8: // UPC NO
					
				    productRelationalSku = cell.getStringCellValue();
				    sizeValue=ApplicationConstants.SIZE_MAP.get(dimension+"x"+size);
				    finalColorValue=ApplicationConstants.COLOR_MAP.get(colorValue);
				    ProductSkusList.add(skuObj);
					
					break;
					
				case 9: //SUGG RETL
					
					break;
				case 10: // MSAP
				
					
					break;
				case 11://DEPT
				   
					break;
				//////////////////////
					//sharvari
				case 12://CATEGORY....product category
					String Category=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(Category)){
						 listOfCategories.add(Category);
					 }
				
					break;
				case 13://SUB CATEGORY
					String SubCategory=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(SubCategory)){
						 listOfCategories.add(SubCategory);
					 }
					productExcelObj.setCategories(listOfCategories);

				
					 break;
				case 14://FABRIC WT
					String FabricWT=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(FabricWT)&&!FabricWT.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)){
					  itemWeight=broberryProductAttributeParser.getItemWeight(FabricWT);
					 productConfigObj.setItemWeight(itemWeight);
					 }
					
					
			     break;
				case 15://FABRIC CONTENT
				      MaterialValue1=cell.getStringCellValue();
				
					break;
				case 16://FABRICATION
					 MaterialValue2=cell.getStringCellValue();
					if(!StringUtils.isEmpty(MaterialValue2)){
					 MaterialValue2=MaterialValue2.toUpperCase();
					}
					break;
				case 17://LSW1
					  AddionnalInfo1=cell.getStringCellValue();
					 if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)){
		            	 AdditionalInfo=AdditionalInfo.append("LSW:").append(AddionnalInfo1);
		             }
				
					break;
				case 18://SILHOUTTE2
					AddionnalInfo1=cell.getStringCellValue();
		             if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)){
		            	 AdditionalInfo=AdditionalInfo.append("Silhoutte:").append(AddionnalInfo1);
		             }
					break;
				case 19: //SUB DEPT
					  Keyword1 = cell.getStringCellValue();
	                 if(!Keyword1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)){
	                FinalKeyword.append(Keyword1).append(ApplicationConstants.CONST_STRING_COMMA_SEP);
	                 }
	                
					break;
				
				case 20: //NECKLINE3
					AddionnalInfo1=cell.getStringCellValue();
					 if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED) ){
						 AdditionalInfo=AdditionalInfo.append(",Neckline:").append(AddionnalInfo1);
			         }
					 break;
				case 21: //DESIGN
					String Keyword2 = cell.getStringCellValue();
	                if(!Keyword2.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)&& !Keyword2.equalsIgnoreCase(Keyword1)){
	                	FinalKeyword.append(Keyword2);
					}
	                String FinalKeyword1=FinalKeyword.toString();
	                String productKeywordArr[] =  FinalKeyword1.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					for (String string : productKeywordArr) {
						productKeywords.add(string);
					}
					productExcelObj.setProductKeywords(productKeywords);
					break;
				case 22: //SEASONALITY4
					AddionnalInfo1=cell.getStringCellValue();
					 if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)){
					 AdditionalInfo=AdditionalInfo.append("Seasonality:").append(AddionnalInfo1);
					 }
				
					break;
				case 23: //PLF5
					AddionnalInfo1=cell.getStringCellValue();
					 if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)){
				     AdditionalInfo=AdditionalInfo.append(",PLF:").append(AddionnalInfo1);
					}
					
					break;
	
				case 24: //CARE INSTNS6
					AddionnalInfo1=cell.getStringCellValue();
					 if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)){
					     AdditionalInfo=AdditionalInfo.append(",Care Instructions:").append(AddionnalInfo1);
						}
					
					break;
				case 25: //LONG DESC
					productName = cell.getStringCellValue();
					int len=productName.length();
					 if(len>60){
						String strTemp=productName.substring(0, 60);
						int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
						productName=(String) strTemp.subSequence(0, lenTemp);
					}
					productExcelObj.setName(productName);
					 
	
					break;
				case 26: //CONS COPY
	
					String description = cell.getStringCellValue();
					int length=description.length();
					 if(length>800){
						String strTemp=description.substring(0, 800);
						int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
						description=(String) strTemp.subSequence(0, lenTemp);
					}
					productExcelObj.setDescription(description);
					
					break;
				case 27: //SEARCH KEY

					
	
					break;
				case 28: //LOGISTICS
					//String description = cell.getStringCellValue();

	
					break;
				case 29: //FIT LENG7 
					AddionnalInfo1=cell.getStringCellValue();
					 if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)){
					     AdditionalInfo=AdditionalInfo.append(",Fix length:").append(AddionnalInfo1);
						}
					
				
					break;
				case 30: //RETAIL COPY
					//String description = cell.getStringCellValue();

					break;
				case 31: //LINING8 ,Lining:
					AddionnalInfo1=cell.getStringCellValue();
					if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)){
					 AdditionalInfo=AdditionalInfo.append(",Lining:").append(AddionnalInfo1);
				     }
					productExcelObj.setAdditionalProductInfo(AdditionalInfo.toString());
					break;
				
				}  // end inner while loop
				///sharvari fields
				///////////
					 
			}		
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() );		 
		}
		}
		workbook.close();
		productExcelObj.setAdditionalProductInfo(AdditionalInfo.toString());
	
		colorList=broberryProductAttributeParser.getColorCriteria(colorSet);
		productConfigObj.setColors(colorList);
		
		
		pnumberList=broberryProductAttributeParser.getProductNumer(productNumberMap);
		productExcelObj.setProductNumbers(pnumberList);
		productConfigObj.setSizes(broberryProductAttributeParser.getProductSize(new ArrayList<String>(sizeValuesSet)));
		if(!CollectionUtils.isEmpty(productOptionSet)){
		productConfigObj.setOptions(broberryProductAttributeParser.getOptions(new ArrayList<String>(productOptionSet)));
		}
		if(!CollectionUtils.isEmpty(productOptionSet)){
		listOfAvailablity =broberryProductAttributeParser.getProductAvailablity(ProductDataStore.getSizesBrobery(),productOptionSet);
		productExcelObj.setAvailability(listOfAvailablity);
		}
		if(!StringUtils.isEmpty(ProductSkusList)){
			skuObj=broberrySkuParserObj.getProductRelationSkus(skuObj, sizeValue, finalColorValue, productRelationalSku);
			productExcelObj.setProductRelationSkus(ProductSkusList);
		    }
		  if(!StringUtils.isEmpty(listOfMaterial)){
			listOfMaterial = broberryMaterialParserObj.getMaterialList(MaterialValue1);
		   listOfMaterial = broberryMaterialParserObj.getMaterialList(MaterialValue2,listOfMaterial);
		    productConfigObj.setMaterials(listOfMaterial);
			}
		  if(!StringUtils.isEmpty(productKeywords)){
			productExcelObj.setProductKeywords(productKeywords);
		  }
		if(CollectionUtils.isEmpty(priceGrids)){
			productExcelObj.setPriceType(ApplicationConstants.CONST_PRICE_TYPE_CODE_LIST);
			priceGrids=getPriceGrids(productName);
			}
		 	productExcelObj.setPriceGrids(priceGrids);
		 
		 productExcelObj.setProductConfigurations(productConfigObj);
		 	_LOGGER.info("Product Data : "
					+ mapperObj.writeValueAsString(productExcelObj));
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId);
		 	if(num ==1){
		 		numOfProductsSuccess.add("1");
		 	}else if(num == 0){
		 		numOfProductsFailure.add("0");
		 	}
		 	_LOGGER.info("list size>>>>>>"+numOfProductsSuccess.size());
		 	_LOGGER.info("Failure list size>>>>>>"+numOfProductsFailure.size());
	       finalResult = numOfProductsSuccess.size() + "," + numOfProductsFailure.size();
	    productDaoObj.saveErrorLog(asiNumber,batchId);
		priceGrids = new ArrayList<PriceGrid>();
		productConfigObj = new ProductConfigurations();
		listOfColors = new HashSet<>();
		repeatRows.clear();
		colorSet=new HashSet<String>(); 
		colorList = new ArrayList<Color>();
		AdditionalInfo=new StringBuilder();
		productNumberMap=new HashMap<String, String>();
		pnumberList=new ArrayList<ProductNumber>();
		listOfMaterial=new ArrayList<Material>();
		sizeValuesSet = new HashSet<>();
		listOfAvailablity=new ArrayList<Availability>();
		productOptionSet=new HashSet<String>();
		listOfCategories=new ArrayList<String>();
		FinalKeyword=new StringBuilder();
		ProductDataStore.clearSizesBrobery();
        ProductSkusList = new ArrayList<ProductSkus>();
        AdditionalInfo= new StringBuilder();
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
	
	public boolean isRepeateColumn(int columnIndex){
		
		if(columnIndex != 1&&columnIndex != 2&&columnIndex != 5 && columnIndex != 6 && columnIndex != 7){
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



	public BroberryProductAttributeParser getBroberryProductAttributeParser() {
		return broberryProductAttributeParser;
	}



	public void setBroberryProductAttributeParser(
			BroberryProductAttributeParser broberryProductAttributeParser) {
		this.broberryProductAttributeParser = broberryProductAttributeParser;
	}
	public BroberryProductMaterialParser getBroberryMaterialParserObj() {
		return broberryMaterialParserObj;
	}


	public void setBroberryMaterialParserObj(
			BroberryProductMaterialParser broberryMaterialParserObj) {
		this.broberryMaterialParserObj = broberryMaterialParserObj;
	}


	public BroberrySkuParser getBroberrySkuParserObj() {
		return broberrySkuParserObj;
	}

	public void setBroberrySkuParserObj(BroberrySkuParser broberrySkuParserObj) {
		this.broberrySkuParserObj = broberrySkuParserObj;
	}
	
	
}
