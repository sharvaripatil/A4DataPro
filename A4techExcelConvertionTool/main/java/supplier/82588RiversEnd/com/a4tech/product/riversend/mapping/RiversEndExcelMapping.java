package com.a4tech.product.riversend.mapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.dataStore.ProductDataStore;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.broberry.mapping.BroberryExcelMapping;
import com.a4tech.product.broberry.mapping.BroberryExcelMapping.OPTION_SIZES;
import com.a4tech.product.broberry.parser.BroberryProductAttributeParser;
import com.a4tech.product.broberry.parser.BroberryProductMaterialParser;
import com.a4tech.product.broberry.parser.BroberrySkuParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Price;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Volume;
import com.a4tech.product.riversend.parser.RiverEndAttributeParser;
import com.a4tech.product.riversend.parser.RiverEndPriceGridParser;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RiversEndExcelMapping  implements IExcelParser{


	private static final Logger _LOGGER = Logger.getLogger(RiversEndExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private RiverEndAttributeParser riverEndAttributeParser;
	private RiverEndPriceGridParser riverEndPriceGridParser;
	@Autowired
	ObjectMapper mapperObj;
	
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
	
		StringBuilder FinalKeyword = new StringBuilder();
		StringBuilder AdditionalInfo = new StringBuilder();
		String AddionnalInfo1=null;
		List<Material> listOfMaterial =new ArrayList<Material>();
		List<String> productKeywords = new ArrayList<String>();
		List<String> listOfCategories = new ArrayList<String>();
		List<ProductSkus> ProductSkusList = new ArrayList<>();
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
		 
		  String sizeDsec=null;
		  String sizeItemNo=null;
		 
		  String upc_no;
		  Set<String> colorSet = new HashSet<String>(); 
		  List<Color> colorList = new ArrayList<Color>();
		  HashMap<String, String>  priceGridMap=new HashMap<String, String>();
		  HashMap<String, String>  priceGridMapTemp=new HashMap<String, String>();
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
		  List<String> imagesList   = new ArrayList<String>();
		  String productName=null;
		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		String currentValue=null;
	    String lastValue=null;
	    String productId = null;
	    String xid = null;
	    int columnIndex=0;
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
			
			 boolean checkXid  = false; //imp line
			//boolean checkXid  = true; //imp line
			//xid=getProductXid(nextRow);
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				
			     columnIndex = cell.getColumnIndex();
				
				if(columnIndex + 1 == 1){
					xid = getProductXid(nextRow);//CommonUtility.getCellValueStrinOrInt(cell);//
					checkXid = true;
				}else{
					checkXid = false;
				}
				
				
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							 if(!CollectionUtils.isEmpty(colorSet)){
							 colorList=riverEndAttributeParser.getColorCriteria(colorSet);
							productConfigObj.setColors(colorList);
							 }
							
								if(!CollectionUtils.isEmpty(priceGridMap)){
								boolean flag=false;
								productExcelObj.setPriceType(ApplicationConstants.CONST_PRICE_TYPE_CODE_NET);
								if(colorSet.size()>1){
									flag=true;
									priceGrids=riverEndPriceGridParser.getPriceGrids(priceGridMapTemp,flag);
								}else{
								priceGrids=riverEndPriceGridParser.getPriceGrids(priceGridMap,flag);
								}
								productExcelObj.setPriceGrids(priceGrids);
								}
							if(!CollectionUtils.isEmpty(sizeValuesSet)){
							  productConfigObj.setSizes(riverEndAttributeParser.getProductSize(new ArrayList<String>(sizeValuesSet)));
							}
							  if(!CollectionUtils.isEmpty(imagesList)){
								List<Image> listOfImages = riverEndAttributeParser.getImages(imagesList);
								productExcelObj.setImages(listOfImages);
								}
							  productExcelObj.setLineNames(new ArrayList<String>());
								 productExcelObj.setProductConfigurations(productConfigObj);
								 	/*_LOGGER.info("Product Data : "
											+ mapperObj.writeValueAsString(productExcelObj));*/
								 	
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
								
								priceGridMap=new HashMap<String, String>();
								priceGridMapTemp=new HashMap<String, String>();
								pnumberList=new ArrayList<ProductNumber>();
								
								sizeValuesSet = new HashSet<>();
								sizeValuesSet = new HashSet<>();
								imagesList=new ArrayList<String>();
								//ProductDataStore.clearSizesBrobery();

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
								   // priceGrids = productExcelObj.getPriceGrids();
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
					case 1://XID
						//productId=xid;//CommonUtility.getCellValueStrinOrInt(cell);
						productExcelObj.setExternalProductId(xid);
						break;
						
					case 2://Mill
						//ignore this column
						break;
					case 3://Item Number
						sizeItemNo=CommonUtility.getCellValueStrinOrInt(cell);
						break;
					case 4://Style Code
						//already processed
						break;
					case 5://Web Description
					  productName = CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(productName)){
						productName=CommonUtility.getStringLimitedChars(productName, 60);
						productExcelObj.setName(productName);
						}
						break;
					case 6://Color Description
						//product color
						colorValue=cell.getStringCellValue();
						if(!StringUtils.isEmpty(colorValue)){
							colorSet.add(colorValue);
						}
						break;
					case 7://Color Code
						//ignore this column
						break;
					case 8://Size Code
						//ignore this column
						break;
					case 9://Size Description
						//product sizes
						String	size=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(size)){
							sizeValuesSet.add(size);
							sizeDsec=size;
						}
						
						break;
					case 10://Item Name
						//ignore this column
						break;
					case 11://Mill Description
						//ignore this column
						break;
					case 12://Class
						//ignore this column
						break;
					case 13://Catalog Description
						String description=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(description)){
						description=CommonUtility.getStringLimitedChars(description, 800);
						productExcelObj.setDescription(description);
						}else{
							productExcelObj.setDescription(productName);
						}
						break;
					case 14://Gender
						//ignore this column
						break;
					case 15://Fabric
						//ignore this column
						break;
					case 16://Web Active
						//ignore this column
						break;
					 case 17://Weight
					    	//item weight
					    	String itemWt=CommonUtility.getCellValueDouble(cell);
					    	if(!StringUtils.isEmpty(itemWt)){
					    		Volume 	itemWeight=	riverEndAttributeParser.getItemWeightvolume(itemWt);
					    		productConfigObj.setItemWeight(itemWeight);
					    	}
						break;
					case 18://Height
						//ignore this column for now as sizes are present
						break;
					case 19://Length
						//ignore this column for now as sizes are present
						break;
					case 20://Width
						//ignore this column for now as sizes are present
						break;
					case 21://GTIN
						//ignore this column 
						break;
					case 22://UPC
						String upcCode = CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(upcCode)){
						 upcCode = CommonUtility.convertExponentValueIntoNumber(upcCode);
							productExcelObj.setUpcCode(upcCode);
						}
						break;
					case 23://Case Pack
						//shipping estimate
						String casePack = CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(casePack)){
						 ShippingEstimate    shippingEstimates =new ShippingEstimate();
						 List<NumberOfItems> numberOfItems=new ArrayList<NumberOfItems>();
						 NumberOfItems items=new NumberOfItems();
						 items.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_NUMBER_UNIT_CASE);
						 items.setValue(casePack);
						 numberOfItems.add(items);
						 shippingEstimates.setNumberOfItems(numberOfItems);
						 productConfigObj.setShippingEstimates(shippingEstimates);
						}
						
						break;
					case 24://Price
						//pricing
						String netPrice=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(netPrice)){
							priceGridMap.put(sizeItemNo, sizeDsec+"___"+netPrice);
							priceGridMapTemp.put(sizeItemNo, colorValue+"%%%"+sizeDsec+"___"+netPrice);
							
							}
						break;
					case 25://Created Date
						//ignore this column 
						break;
					case 26://Active
						//ignore this column 
						break;
					case 27://item Status
						//ignore this column 
						break;
					case 28://Large Image
						// Product images
						String largeImage = cell.getStringCellValue();
						if(!StringUtils.isEmpty(largeImage)){
							imagesList.add(largeImage);
						}
					
						break;
					case 29://High Res Image
						// Product images
						String highImage = cell.getStringCellValue();
						if(!StringUtils.isEmpty(highImage)){
							imagesList.add(highImage);
						}
						break;
					case 30://Product URL //product datasheet
						String productDataSheet=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(productDataSheet)){
						productExcelObj.setProductDataSheet(productDataSheet);
						}
						break;
					case 31://Large
						break;
					case 32://Hi Res
						break;
						
				}  // end inner while loop					 
			}		
			}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);		 
				ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: "
				+e.getMessage()+" at column number(increament by 1)"+columnIndex);
				productDaoObj.save(apiResponse.getErrors(),
						productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);
				}
		}
		workbook.close();
		if(!CollectionUtils.isEmpty(colorSet)){
		colorList=riverEndAttributeParser.getColorCriteria(colorSet);
		productConfigObj.setColors(colorList);
		}
		if(!CollectionUtils.isEmpty(sizeValuesSet)){
		productConfigObj.setSizes(riverEndAttributeParser.getProductSize(new ArrayList<String>(sizeValuesSet)));
		}
		if(!CollectionUtils.isEmpty(priceGridMap)){
			boolean flag=false;
			productExcelObj.setPriceType(ApplicationConstants.CONST_PRICE_TYPE_CODE_NET);
			if(colorSet.size()>1){
				flag=true;
				priceGrids=riverEndPriceGridParser.getPriceGrids(priceGridMapTemp,flag);
			}else{
			priceGrids=riverEndPriceGridParser.getPriceGrids(priceGridMap,flag);
			}
			productExcelObj.setPriceGrids(priceGrids);
			}
		
		if(!CollectionUtils.isEmpty(imagesList)){
			List<Image> listOfImages = riverEndAttributeParser.getImages(imagesList);
			productExcelObj.setImages(listOfImages);
			}
		productExcelObj.setLineNames(new ArrayList<String>());
		 productExcelObj.setProductConfigurations(productConfigObj);
		 	/*_LOGGER.info("Product Data : "
					+ mapperObj.writeValueAsString(productExcelObj));*/
		 	
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
		priceGridMap=new HashMap<String, String>();
		priceGridMapTemp=new HashMap<String, String>();
		pnumberList=new ArrayList<ProductNumber>();
		listOfMaterial=new ArrayList<Material>();
		sizeValuesSet = new HashSet<>();
		listOfAvailablity=new ArrayList<Availability>();
		productOptionSet=new HashSet<String>();
		listOfCategories=new ArrayList<String>();
		FinalKeyword=new StringBuilder();
		//ProductDataStore.clearSizesBrobery();
       ProductSkusList = new ArrayList<ProductSkus>();
        AdditionalInfo= new StringBuilder();
        imagesList=new ArrayList<String>();
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
		Cell xidCell =  row.getCell(0);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid) || productXid.trim().equalsIgnoreCase("#N/A")){
		     xidCell = row.getCell(3);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}
	
	public boolean isRepeateColumn(int columnIndex){
		
		if(columnIndex != 1&&columnIndex != 3&&columnIndex != 4 && columnIndex != 6 && columnIndex != 9 && columnIndex != 24){
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



	public RiverEndAttributeParser getRiverEndAttributeParser() {
		return riverEndAttributeParser;
	}



	public void setRiverEndAttributeParser(
			RiverEndAttributeParser riverEndAttributeParser) {
		this.riverEndAttributeParser = riverEndAttributeParser;
	}



	public RiverEndPriceGridParser getRiverEndPriceGridParser() {
		return riverEndPriceGridParser;
	}



	public void setRiverEndPriceGridParser(
			RiverEndPriceGridParser riverEndPriceGridParser) {
		this.riverEndPriceGridParser = riverEndPriceGridParser;
	}
	
	
}