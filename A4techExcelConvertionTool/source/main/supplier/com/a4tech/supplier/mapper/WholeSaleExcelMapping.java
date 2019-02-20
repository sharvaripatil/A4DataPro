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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.service.imple.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

import parser.wholesale.WholeSaleAttributeParser;
import parser.wholesale.WholeSalePriceGridParser;

public class WholeSaleExcelMapping  implements IExcelParser{
	private static final Logger _LOGGER = Logger.getLogger(WholeSaleExcelMapping.class);
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	@Autowired
	ObjectMapper mapperObj;
	WholeSaleAttributeParser wholeSaleAttributeParser;
	WholeSalePriceGridParser wholeSalePriceGridParser;
	private LookupServiceData lookupServiceDataObj;
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
	
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
		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
	    String productId = null;
	    String xid = null;
	    int columnIndex=0;
	    boolean existingFlag=false;
	    String q1 = null,q2= null,q3= null,q4= null,q5= null,q6=null;
	    String baseDiscCode=null;
		while (iterator.hasNext()) {
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum() == 0){
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
				q6=cell1.getStringCellValue();
				
			}
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
							priceGrids = wholeSalePriceGridParser.getPriceGrids(listOfPrices.toString(),listOfQuantity.toString(), baseDiscCode,ApplicationConstants.CONST_STRING_CURRENCY_USD,"",
										ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, "",
										ApplicationConstants.CONST_STRING_EMPTY,1,null,null,priceGrids);	
							
							if(!StringUtils.isEmpty(imprintMethodValue) && !StringUtils.isEmpty(impucVal) && !StringUtils.isEmpty(upcDicountCode))
								{
								 priceGrids = wholeSalePriceGridParser.getPriceGrids(impucVal,"1",upcDicountCode,ApplicationConstants.CONST_STRING_CURRENCY_USD,upcPriceIncludes, 
										ApplicationConstants.CONST_BOOLEAN_FALSE, ApplicationConstants.CONST_STRING_FALSE, imprintMethodValue,
										"Imprint Method",2,ApplicationConstants.CONST_STRING_SETUP_CHARGE,"Per Order",priceGrids);
								}
							productExcelObj.setPriceType("L");
							productExcelObj.setPriceGrids(priceGrids);
							productExcelObj.setProductConfigurations(productConfigObj);
							/*_LOGGER.info("Product Data : "
									+ mapperObj.writeValueAsString(productExcelObj));*/
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
								//ProductDataStore.clearSizesBrobery();

						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    	repeatRows.add(xid);
						    }
						    productExcelObj = new Product();
						    existingApiProduct = postServiceImpl.getProduct(accessToken, xid, environmentType); 
						     if(existingApiProduct == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						    	 existingFlag=false;
						     }else{
						    	 _LOGGER.info("Existing Xid available,Processing existing Data");
						    	 productExcelObj=wholeSaleAttributeParser.getExistingProductData(existingApiProduct,existingApiProduct.getProductConfigurations(),accessToken,environmentType);
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
						//productId=xid;//CommonUtility.getCellValueStrinOrInt(cell);
						productExcelObj.setExternalProductId(xid);
						break;
						case 2://Item#
							//product number based on color
						break;

						case 3://Description
							String productName = CommonUtility.getCellValueStrinOrInt(cell);
							//productName = CommonUtility.removeSpecialSymbols(productName,specialCharacters);
							int len=productName.length();
							 if(len>60){
								String strTemp=productName.substring(0, 60);
								int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
								productName=(String) strTemp.subSequence(0, lenTemp);
							}
							productExcelObj.setName(productName);
							 
						break;

						case 4://Category
							//already processed while doing get..
						break;

						case 5://MKT DECRIPTION
							descOne=CommonUtility.getCellValueStrinOrInt(cell);
							if(!StringUtils.isEmpty(descOne)){
								int length=descOne.length();
								 if(length>800){
									String strTemp=descOne.substring(0, 800);
									int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
									descOne=(String) strTemp.subSequence(0, lenTemp);
								}
								productExcelObj.setDescription(descOne);
							}
						break;

						case 6://FEATURES
							String descTwo=CommonUtility.getCellValueStrinOrInt(cell);
							if(!StringUtils.isEmpty(descTwo)){
								if(!StringUtils.isEmpty(descOne)){
									descTwo=descOne+descTwo;
								}
							if(!StringUtils.isEmpty(descTwo)){
								int length=descTwo.length();
								 if(length>800){
									String strTemp=descTwo.substring(0, 800);
									int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
									descTwo=(String) strTemp.subSequence(0, lenTemp);
								}
							}
							productExcelObj.setDescription(descTwo);
							}
						break;

						case 7://Print Method
						imprintMethodValue=CommonUtility.getCellValueStrinOrInt(cell);
						if(!existingFlag)
						{
							
							 if(!StringUtils.isEmpty(imprintMethodValue)&& !imprintMethodValue.equalsIgnoreCase("BLANK")){
								 if(imprintMethodValue.contains("Laser")){
									 imprintMethodValue="Laser Engraved";
									}else if(imprintMethodValue.toUpperCase().contains("TRANSFOR")){
										imprintMethodValue="Printed";	
									}
						   // imprintMethodValue=CommonUtility.getCellValueStrinOrInt(cell);
						    imprintMethodList=wholeSaleAttributeParser.getImprintCriteria(imprintMethodValue, imprintMethodList);
						    productConfigObj.setImprintMethods(imprintMethodList);
						}
						}else{
							imprintMethodValue=null;
						}
						break;

						case 8://Imprint Area
							String impSize=CommonUtility.getCellValueStrinOrInt(cell);
							if(!StringUtils.isEmpty(impSize)){
								List<ImprintSize> imprintSizeList=new ArrayList<ImprintSize>();
								ImprintSize impsObj=new ImprintSize();
								impsObj.setValue(impSize);
								imprintSizeList.add(impsObj);
								productConfigObj.setImprintSize(imprintSizeList);
							}
							
						break;

						case 9://QTY/ 6
							String	listPrice1=null;
							listPrice1=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(listPrice1)){
					        	 listOfPrices.append(listPrice1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					        	 listOfQuantity.append(q1.toUpperCase().replace("QTY/","").trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						break;

						case 10://QTY/50
							String	listPrice2=null;
							listPrice2=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(listPrice2)){
					        	 listOfPrices.append(listPrice2).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					        	 listOfQuantity.append(q2.toUpperCase().replace("QTY/","").trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						break;

						case 11://QTY/100
							String	listPrice3=null;
							listPrice3=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(listPrice3)){
					        	 listOfPrices.append(listPrice3).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					        	 listOfQuantity.append(q3.toUpperCase().replace("QTY/","").trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						break;

						case 12://QTY/250
							String	listPrice4=null;
							listPrice4=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(listPrice4)){
					        	 listOfPrices.append(listPrice4).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					        	 listOfQuantity.append(q4.toUpperCase().replace("QTY/","").trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						break;

						case 13://QTY/500
							String	listPrice5=null;
							listPrice5=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(listPrice5)){
					        	 listOfPrices.append(listPrice5).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					        	 listOfQuantity.append(q5.toUpperCase().replace("QTY/","").trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						break;

						case 14://QTY/1000
							String	listPrice6=null;
							listPrice6=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(listPrice6)){
					        	 listOfPrices.append(listPrice6).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					        	 listOfQuantity.append(q6.toUpperCase().replace("QTY/","").trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						break;

						case 15://Price Class 
							baseDiscCode = cell.getStringCellValue();
							 if(!StringUtils.isEmpty(baseDiscCode)){
								 baseDiscCode=baseDiscCode.toUpperCase().replace("CLASS", "").trim();
							 }
						break;

						case 16://Setup
							//upcharges for imprint method
							 impucVal=CommonUtility.getCellValueStrinOrDecimal(cell);//getCellValueStrinOrInt(cell);
							 if(!StringUtils.isEmpty(impucVal)){
								 impucVal=removeSpecialChar(impucVal.toUpperCase());
							 }
							
						break;

						case 17://Setup Class
							 upcDicountCode=CommonUtility.getCellValueStrinOrInt(cell);
							 if(!StringUtils.isEmpty(upcDicountCode)){
								 upcDicountCode=removeSpecialChar(upcDicountCode.toUpperCase());
							 }
							
						break;

						case 18://Notes
							 upcPriceIncludes=CommonUtility.getCellValueStrinOrInt(cell);
						break;

						case 19://Production Time
						String	prodTime =CommonUtility.getCellValueStrinOrInt(cell);
						 List<ProductionTime> prodTimeList=new ArrayList<ProductionTime>();
						 String detailsValue="";
							if(!StringUtils.isEmpty(prodTime)){
								if(prodTime.toLowerCase().contains("week")){
									prodTime=removeSpecialChar(prodTime);
									if(prodTime.contains("-")){
										String arrTemp[]=prodTime.split("-");
										prodTime=arrTemp[1];
										int inWeekVal=5 * Integer.parseInt(prodTime.trim());
										prodTime=Integer.toString(inWeekVal);
										detailsValue="Day";
									}else{
										prodTime=removeSpecialChar(prodTime);
										int inWeekVal=5 * Integer.parseInt(prodTime.trim());
										prodTime=Integer.toString(inWeekVal);
										detailsValue="Day";
									}
								}
								
								if(prodTime.toLowerCase().contains("hour")){
									prodTime=removeSpecialChar(prodTime);
									prodTime="1";
									detailsValue="Day";
								}
								
								if(prodTime.toLowerCase().contains("day")){
									prodTime=removeSpecialChar(prodTime);
									detailsValue="Day";
								}
								
							    prodTimeList=wholeSaleAttributeParser.getProdTimeCriteria(prodTime.trim(), detailsValue,prodTimeList);
								productConfigObj.setProductionTime(prodTimeList);
							}
							break;
						
						
						case 20://F.O.B
							if(!existingFlag)
							{
							List<String> fobPointsTemp=lookupServiceDataObj.getFobPoints(accessToken,environmentType);
							if(!CollectionUtils.isEmpty(fobPointsTemp)){
							String tempValue=null;
							for (String string : fobPointsTemp) {
								if(string.toUpperCase().contains("NY")){
									tempValue=string;
								}
							}
							if(!StringUtils.isEmpty(tempValue)){
								List<FOBPoint>	fobPoints=new ArrayList<FOBPoint>();
								FOBPoint fobObj=new FOBPoint();
								fobObj.setName(tempValue);
								fobPoints.add(fobObj);
								productExcelObj.setFobPoints(fobPoints);
								//fobPoints.add(e)
							}
							}
							}
							break;
							
						case 21://BLANKS
							
							String unImprintVal=CommonUtility.getCellValueStrinOrInt(cell);
							 if(!StringUtils.isEmpty(unImprintVal)&& !unImprintVal.equalsIgnoreCase("BLANK")){
							if(unImprintVal.equalsIgnoreCase("Yes")){
						    imprintMethodList=wholeSaleAttributeParser.getImprintCriteria("UNIMPRT", imprintMethodList);
						    productConfigObj.setImprintMethods(imprintMethodList);
							}
							 }
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
		priceGrids = wholeSalePriceGridParser.getPriceGrids(listOfPrices.toString(),listOfQuantity.toString(), baseDiscCode,ApplicationConstants.CONST_STRING_CURRENCY_USD,"",
					ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, "",
					ApplicationConstants.CONST_STRING_EMPTY,1,null,null,priceGrids);	
		
		if(!StringUtils.isEmpty(imprintMethodValue) && !StringUtils.isEmpty(impucVal) && !StringUtils.isEmpty(upcDicountCode))
			{
			 priceGrids = wholeSalePriceGridParser.getPriceGrids(impucVal,"1",upcDicountCode,ApplicationConstants.CONST_STRING_CURRENCY_USD,upcPriceIncludes, 
					ApplicationConstants.CONST_BOOLEAN_FALSE, ApplicationConstants.CONST_STRING_FALSE, imprintMethodValue,
					"Imprint Method",2,ApplicationConstants.CONST_STRING_SETUP_CHARGE,"Per Order",priceGrids);
			}
		productExcelObj.setPriceType("L");
		productExcelObj.setPriceGrids(priceGrids);
		productExcelObj.setProductConfigurations(productConfigObj);
		 	/*_LOGGER.info("Product Data : "
					+ mapperObj.writeValueAsString(productExcelObj));*/
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId, environmentType);
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



	public WholeSaleAttributeParser getWholeSaleAttributeParser() {
		return wholeSaleAttributeParser;
	}



	public void setWholeSaleAttributeParser(
			WholeSaleAttributeParser wholeSaleAttributeParser) {
		this.wholeSaleAttributeParser = wholeSaleAttributeParser;
	}
	
	
	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}



	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}
	
	

	public WholeSalePriceGridParser getWholeSalePriceGridParser() {
		return wholeSalePriceGridParser;
	}



	public void setWholeSalePriceGridParser(
			WholeSalePriceGridParser wholeSalePriceGridParser) {
		this.wholeSalePriceGridParser = wholeSalePriceGridParser;
	}



	public static String removeSpecialChar(String tempValue){
		tempValue=tempValue.replaceAll("(CLASS|Day|DAYS|Service|Days|Hour|Hours|Week|Weeks|Rush|day|service|days|hour|hours|week|weeks|Rush|R|u|s|h|$)", "");
		tempValue=tempValue.replaceAll("\\(","");
		tempValue=tempValue.replaceAll("\\)","");
	return tempValue;

	}
	
	
}
