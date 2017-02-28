package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import parser.primeline.PrimeLineAttributeParser;
import parser.primeline.PrimeLineColorTabParser;
import parser.wholesale.WholeSaleAttributeParser;
import parser.wholesale.WholeSalePriceGridParser;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PrimeLineExcelMapping  implements IExcelParser{
	private static final Logger _LOGGER = Logger.getLogger(PrimeLineExcelMapping.class);
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	@Autowired
	ObjectMapper mapperObj;
	private LookupServiceData lookupServiceDataObj;
	PrimeLineColorTabParser primeLineColorTabParser;
	PrimeLineAttributeParser primeLineAttributeParser;
	 private HashMap<String, Product> sheetMap =new HashMap<String, Product>();
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  Product existingApiProduct = null;
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  List<PriceGrid> priceGrids = null;
		  List<String> repeatRows = new ArrayList<>();
		  Set<String> colorSet = new HashSet<String>(); 
		  List<Color> colorList = new ArrayList<Color>();
		  List<ProductNumber> pnumberList = new ArrayList<ProductNumber>();
		  String productNumber=null;
		  HashSet<String> sizeValuesSet = new HashSet<>();
		  String colorValue=null;
		  String sizeValue=null;
		  String descOne=null;
		  List<ImprintMethod> imprintMethodList = new ArrayList<ImprintMethod>();
		  String imprintMethodValue=null;
		  String impucVal=null;
		  String upcDicountCode=null;
		  String upcPriceIncludes="";
		  StringBuilder listOfQuantity = new StringBuilder();
		  StringBuilder listOfPrices = new StringBuilder();
		  String shippingitemValue="";
			String shippingWeightValue="";
			String shippingdimensionValue="";
			String dimLen="";
			String dimHieght="";
			String dimWidth="";
			List<String> listOfCategories = new ArrayList<>();
		  
		try{
			_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	 for(int i=0;i<2;i++)
		{
		if(i==0){
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
	    String productId = null;
	    String xid = null;
	    int columnIndex=0;
	    boolean existingFlag=false;
	    String q1 = null,q2= null,q3= null,q4= null,q5= null,q6=null;
	    String baseDiscCode=null;
	   // List<String> imagesList   = new ArrayList<String>();
		while (iterator.hasNext()) {
			try{
			Row nextRow = iterator.next();
			/*if (nextRow.getRowNum() == 0){
				Cell cell1=nextRow.getCell(8);
				q1=cell1.getStringCellValue();
				cell1=nextRow.getCell(9);
				q2=cell1.getStringCellValue();
				cell1=nextRow.getCell(10);
				q3=cell1.getStringCellValue();
				cell1=nextRow.getCell(11);
				q4=cell1.getStringCellValue();
				cell1=nextRow.getCell(12);
				q5=cell1.getStringCellValue();
				cell1=nextRow.getCell(13);
				//q6=cell1.getStringCellValue();
			}*/
			if (nextRow.getRowNum() == 0)
				continue;
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
							priceGrids=	 new ArrayList<PriceGrid>();
							/*priceGrids = wholeSalePriceGridParser.getPriceGrids(listOfPrices.toString(),listOfQuantity.toString(), baseDiscCode,ApplicationConstants.CONST_STRING_CURRENCY_USD,"",
										ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, "",
										ApplicationConstants.CONST_STRING_EMPTY,1,null,null,priceGrids);	
							
							if(!StringUtils.isEmpty(imprintMethodValue) && !StringUtils.isEmpty(impucVal) && !StringUtils.isEmpty(upcDicountCode))
								{
								 priceGrids = wholeSalePriceGridParser.getPriceGrids(impucVal,"1",upcDicountCode,ApplicationConstants.CONST_STRING_CURRENCY_USD,upcPriceIncludes, 
										ApplicationConstants.CONST_BOOLEAN_FALSE, ApplicationConstants.CONST_STRING_FALSE, imprintMethodValue,
										"Imprint Method",2,ApplicationConstants.CONST_STRING_SETUP_CHARGE,"Per Order",priceGrids);
								}*/
							productExcelObj.setPriceType("L");
							productExcelObj.setPriceGrids(priceGrids);
							productExcelObj.setProductConfigurations(productConfigObj);
							sheetMap.put(productId,productExcelObj);
							/*
							_LOGGER.info("Product Data from sheet 1: "
									+ mapperObj.writeValueAsString(productExcelObj));*/
							 /*int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId);
							 	if(num ==1){
							 		numOfProductsSuccess.add("1");
							 	}else if(num == 0){
							 		numOfProductsFailure.add("0");
							 	}else{
							 		
							 	}*//*
							 	_LOGGER.info("list size>>>>>>>"+numOfProductsSuccess.size());
							 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());*/
								priceGrids = new ArrayList<PriceGrid>();
								productConfigObj = new ProductConfigurations();
								repeatRows.clear();
								colorSet=new HashSet<String>(); 
								colorList = new ArrayList<Color>();
								pnumberList=new ArrayList<ProductNumber>();
								sizeValuesSet = new HashSet<>();
								descOne=null;
								imprintMethodList = new ArrayList<ImprintMethod>();
								imprintMethodValue=null;
								impucVal=null;
								upcDicountCode=null;
								upcPriceIncludes="";
								listOfPrices = new StringBuilder();
								listOfQuantity=new StringBuilder();
								baseDiscCode=null;
								shippingitemValue="";
								shippingWeightValue="";
								shippingdimensionValue="";
								 dimLen="";
								 dimHieght="";
								 dimWidth="";
								 listOfCategories=new ArrayList<String>();
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
						    	 existingFlag=false;
						     }else{
						    	 _LOGGER.info("Existing Xid available,Processing existing Data");
						    	 productExcelObj=primeLineAttributeParser.getExistingProductData(existingApiProduct,existingApiProduct.getProductConfigurations(),accessToken);
						    	 productConfigObj=productExcelObj.getProductConfigurations();
								 existingFlag=true;
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
						productId=xid;//CommonUtility.getCellValueStrinOrInt(cell);
						productExcelObj.setExternalProductId(xid);
						break;
						case 2://ITEMID
						break;
						case 3://ITEMNAME
							String productName=CommonUtility.getCellValueStrinOrDecimal(cell);
							productExcelObj.setName(productName);
							break;
						case 4://NEW?
							break;
						case 5://CUSTOM?
							break;
						case 6://DISCOUNT?
							break;
						case 7://RUSH?
							String rushTime=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(rushTime)){
								String tempRushVal=rushTime;
									if(rushTime.contains("Week") || rushTime.contains("Weeks")){
										rushTime=removeSpecialChar(rushTime);
										rushTime=rushTime.trim();
										if(rushTime.contains("-")){
											String arrTemp[]=rushTime.split("-");
											rushTime=arrTemp[1];
											int inWeekVal=5 * Integer.parseInt(rushTime);
											rushTime=Integer.toString(inWeekVal);
											
										}else{
											rushTime=removeSpecialChar(rushTime);
											rushTime=rushTime.trim();
											int inWeekVal=5 * Integer.parseInt(rushTime);
											rushTime=Integer.toString(inWeekVal);
										}
									}
									if(rushTime.contains("Hour")|| rushTime.contains("Hours")){
										rushTime="1";
									}
									if(rushTime.contains("Day") || rushTime.contains("Days")){
										rushTime=removeSpecialChar(rushTime);
										rushTime=rushTime.trim();
									}
									rushTime=rushTime.replaceAll("-","");
									RushTime rushObj=primeLineAttributeParser.getRushTimeValues(rushTime.trim(), tempRushVal);
									productConfigObj.setRushTime(rushObj);
								}
							break;
						case 8://WEBSITEIMAGEPATH
							String largeImage = CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(largeImage)){
								//imagesList.add(largeImage);
							List<Image> listOfImages = primeLineAttributeParser.getImages(largeImage);
							productExcelObj.setImages(listOfImages);
							}
							break;
							/*case 9://QUANTITYBREAK1
							break;
						case 10://QUANTITYBREAK2
							break;
						case 11://QUANTITYBREAK3
							break;
						case 12://QUANTITYBREAK4
							break;
						case 13://QUANTITYBREAK5
							break;
						case 14://NETPRICE1
							break;
						case 15://NETPRICE2
							break;
						case 16://NETPRICE3
							break;
						case 17://NETPRICE4
							break;
						case 18://NETPRICE5
							break;
						case 19://GROSSPRICE1
							break;
						case 20://GROSSPRICE2
							break;
						case 21://GROSSPRICE2
							break;
						case 22://GROSSPRICE4
							break;
						case 23://GROSSPRICE5
							break;
						case 24://NETPRICE_CAD1
							break;
						case 25://NETPRICE_CAD2
							break;
						case 26://NETPRICE_CAD3
							break;
						case 27://NETPRICE_CAD4
							break;
						case 28://NETPRICE_CAD5
							break;
						case 29://GROSSPRICE_CAD1
							break;
						case 30://GROSSPRICE_CAD2
							break;
						case 31://GROSSPRICE_CAD2
							break;
						case 32://GROSSPRICE_CAD4
							break;
						case 33://GROSSPRICE_CAD5
							break;
							
							*/
						case 34://PIECESPERCARTON
							shippingitemValue=CommonUtility.getCellValueStrinOrDecimal(cell);
							break;
						case 35://WEIGHTPERCARTON
							shippingWeightValue=CommonUtility.getCellValueStrinOrDecimal(cell);
							break;
						case 36://CARTONLENGTH
							dimLen=CommonUtility.getCellValueStrinOrDecimal(cell);
							
							break;
						case 37://CARTONHEIGHT
							dimHieght=CommonUtility.getCellValueStrinOrDecimal(cell);
							break;
						case 38://CARTONWIDTH
							dimWidth=CommonUtility.getCellValueStrinOrDecimal(cell);
							ShippingEstimate shippObject=primeLineAttributeParser.getShippingEstimates(shippingitemValue,shippingWeightValue,
									  dimLen, dimHieght, dimWidth);
							productConfigObj.setShippingEstimates(shippObject);
							break;
							
						case 39://LxHxW IGNORE filed
							break;
						case 40://AX_CATEGORY
							String category=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(category)){
							primeLineAttributeParser.getCategories(category,listOfCategories);
							}
							break;
						case 41://AX_SUBCATEGORY
							String category1=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(category1)){
							primeLineAttributeParser.getCategories(category1,listOfCategories);
							}
							break;
						case 42://AX_WEBCATEGORY
							String category2=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(category2)){
							primeLineAttributeParser.getCategories(category2,listOfCategories);
							}
							break;
						case 43://DISCOUNTCODE
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
		/*if(!CollectionUtils.isEmpty(colorSet)){
		colorList=riverEndAttributeParser.getColorCriteria(colorSet);
		productConfigObj.setColors(colorList);
		}*/
		/*if(!CollectionUtils.isEmpty(sizeValuesSet)){
		productConfigObj.setSizes(riverEndAttributeParser.getProductSize(new ArrayList<String>(sizeValuesSet)));
		}*/
		
		//productExcelObj.setPriceGrids(priceGrids);
		
		priceGrids=	 new ArrayList<PriceGrid>();
		/*priceGrids = wholeSalePriceGridParser.getPriceGrids(listOfPrices.toString(),listOfQuantity.toString(), baseDiscCode,ApplicationConstants.CONST_STRING_CURRENCY_USD,"",
					ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, "",
					ApplicationConstants.CONST_STRING_EMPTY,1,null,null,priceGrids);	
		
		if(!StringUtils.isEmpty(imprintMethodValue) && !StringUtils.isEmpty(impucVal) && !StringUtils.isEmpty(upcDicountCode))
			{
			 priceGrids = wholeSalePriceGridParser.getPriceGrids(impucVal,"1",upcDicountCode,ApplicationConstants.CONST_STRING_CURRENCY_USD,upcPriceIncludes, 
					ApplicationConstants.CONST_BOOLEAN_FALSE, ApplicationConstants.CONST_STRING_FALSE, imprintMethodValue,
					"Imprint Method",2,ApplicationConstants.CONST_STRING_SETUP_CHARGE,"Per Order",priceGrids);
			}*/
		productExcelObj.setPriceType("L");
		productExcelObj.setPriceGrids(priceGrids);
		productExcelObj.setProductConfigurations(productConfigObj);
		 	/*_LOGGER.info("Product Data : "
					+ mapperObj.writeValueAsString(productExcelObj));*/
		 	/*int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId);
		 	if(num ==1){
		 		numOfProductsSuccess.add("1");
		 	}else if(num == 0){
		 		numOfProductsFailure.add("0");
		 	}
		 	_LOGGER.info("list size>>>>>>"+numOfProductsSuccess.size());
		 	_LOGGER.info("Failure list size>>>>>>"+numOfProductsFailure.size());
	       finalResult = numOfProductsSuccess.size() + "," + numOfProductsFailure.size();
	    productDaoObj.saveErrorLog(asiNumber,batchId);*/
		sheetMap.put(productId,productExcelObj);
		priceGrids = new ArrayList<PriceGrid>();
		productConfigObj = new ProductConfigurations();
		repeatRows.clear();
		colorSet=new HashSet<String>(); 
		colorList = new ArrayList<Color>();
		pnumberList=new ArrayList<ProductNumber>();
		sizeValuesSet = new HashSet<>();
        descOne=null;
        imprintMethodList = new ArrayList<ImprintMethod>();
        imprintMethodValue=null;
        impucVal=null;
		upcDicountCode=null;
		upcPriceIncludes="";
		listOfPrices = new StringBuilder();
		listOfQuantity=new StringBuilder();
		baseDiscCode=null;
		shippingitemValue="";
		shippingWeightValue="";
		shippingdimensionValue="";
		dimLen="";
		 dimHieght="";
		 dimWidth="";
		 listOfCategories=new ArrayList<String>();
				}else if(i==1){
					sheetMap=primeLineColorTabParser.readColorTab( accessToken, workbook , asiNumber , batchId,sheetMap);
				}else if(i==2){
					System.out.println("Hi");
				}else if(i==3){
					
				}
			}
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
		     xidCell = row.getCell(1);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}
	
	public boolean isRepeateColumn(int columnIndex){
		if(columnIndex != 1){
		//if(columnIndex != 1&&columnIndex != 3&&columnIndex != 4 && columnIndex != 6 && columnIndex != 9 && columnIndex != 24){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	
	/*public static List<PriceGrid> getPriceGrids(String basePriceName) 
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
	*/
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
	
	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}

	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}

	
	public PrimeLineColorTabParser getPrimeLineColorTabParser() {
		return primeLineColorTabParser;
	}



	public void setPrimeLineColorTabParser(
			PrimeLineColorTabParser primeLineColorTabParser) {
		this.primeLineColorTabParser = primeLineColorTabParser;
	}
	
	


	public PrimeLineAttributeParser getPrimeLineAttributeParser() {
		return primeLineAttributeParser;
	}



	public void setPrimeLineAttributeParser(
			PrimeLineAttributeParser primeLineAttributeParser) {
		this.primeLineAttributeParser = primeLineAttributeParser;
	}



	public static String removeSpecialChar(String tempValue){
		tempValue=tempValue.replaceAll("(CLASS|Day|DAYS|Service|Days|Hour|Hours|Week|Weeks|Rush|day|service|days|hour|hours|week|weeks|Rush|R|u|s|h|$)", "");
		tempValue=tempValue.replaceAll("\\(","");
		tempValue=tempValue.replaceAll("\\)","");
	return tempValue;

	}
	
	
}
