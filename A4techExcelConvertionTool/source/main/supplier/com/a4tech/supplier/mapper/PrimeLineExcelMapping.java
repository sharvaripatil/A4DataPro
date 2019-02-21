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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.core.model.ErrorMessageList;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.Price;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.Shape;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.service.imple.PostServiceImpl;
import com.a4tech.supplier.service.ISupplierParser;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

import parser.primeline.PrimeLineAttributeParser;
import parser.primeline.PrimeLineColorTabParser;
import parser.primeline.PrimeLineConstants;
import parser.primeline.PrimeLineFeatureTabParser;
import parser.primeline.PrimeLineImprintTabParser;
import parser.primeline.PrimeLinePriceGridParser;

public class PrimeLineExcelMapping  implements ISupplierParser{
	private static final Logger _LOGGER = Logger.getLogger(PrimeLineExcelMapping.class);
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	@Autowired
	ObjectMapper mapperObj;
	private LookupServiceData lookupServiceDataObj;
	PrimeLineColorTabParser primeLineColorTabParser;
	PrimeLineAttributeParser primeLineAttributeParser;
	PrimeLinePriceGridParser primeLinePriceGridParser;
	PrimeLineFeatureTabParser primeLineFeatureTabParser;
	PrimeLineImprintTabParser primeLineImprintTabParser;
	 private HashMap<String, Product> sheetMap =new HashMap<String, Product>();
	 private HashMap<String, StringBuilder>  priceMap=new HashMap<String, StringBuilder>();
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
		  StringBuilder listOfQuantity = new StringBuilder();
		  StringBuilder listOfPrices = new StringBuilder();
		  StringBuilder listOfDiscCodes = new StringBuilder();
		  String shippingitemValue="";
			String shippingWeightValue="";
			String dimLen="";
			String dimHieght="";
			String dimWidth="";
			List<String> listOfCategories = new ArrayList<>();
			String productName="";
		  
		try{
			_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	 for(int i=0;i<5;i++)
		{
		if(i==0){
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
	    String productId = null;
	    String xid = null;
	    int columnIndex=0;
	    boolean existingFlag=false;
	   // String q1 = null,q2= null,q3= null,q4= null,q5= null,q6=null;
	    String baseDiscCode=null;
	   // List<String> imagesList   = new ArrayList<String>();
		while (iterator.hasNext()) {
			try{
			Row nextRow = iterator.next();
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
							//priceGrids = primeLinePriceGridParser.getPriceGrids(priceMap);	
							if(!CollectionUtils.isEmpty(priceMap)){
							if(priceMap.size()==1){
								for (Map.Entry<String, StringBuilder> priceEntry : priceMap.entrySet()) {
									String tempStr=priceEntry.getValue().toString();
									String name=priceEntry.getKey().toString();
									String tempArr[]=tempStr.split("@@@@@");
									String listPrices=tempArr[0];
									String listQuan=tempArr[1];
									String	disCodes=tempArr[2];
									if(name.contains(":")){
										String tempStrr[]=name.split(":");
										name=tempStrr[1];
									}
									
									priceGrids = primeLinePriceGridParser.getPriceGrids(listPrices,listQuan,disCodes,"USD","",
											true, "False", name, 
											null, 1,null, null,
											priceGrids);//,productExcelObj);
								
								}	
							}else{
								
								List<String> matList=new ArrayList<String>();
								List<String> shpList =new ArrayList<String>();
								List<String> opntnList=new ArrayList<String>();
								List<String> sizList=new ArrayList<String>();
								String criteriaName="";
								String criteriaValue="";
								String optionNamee="";
								
								int count=1;
								for (Map.Entry<String, StringBuilder> priceEntry : priceMap.entrySet()) {
									
									String priceMapKey=priceEntry.getKey().toString();
									String priceMapValue=priceEntry.getValue().toString();
									/*if(name.contains("(") && name.contains(")") ){
									name = name.substring(name.indexOf("(")+1,name.indexOf(")"));
									}*/
									
									//matList.add(name);
									// I have to add different criteria list
									if(PrimeLineConstants.CRITPRICE_MAP.containsKey(priceMapKey)){
									String critMapValue=PrimeLineConstants.CRITPRICE_MAP.get(priceMapKey);
									
									//String tempKeyArr[]=tempStrKey.split(":");
									String pricingArr[]=priceMapValue.split("@@@@@");
									String listPrices=pricingArr[0];
									String listQuan=pricingArr[1];
									String	disCodes=pricingArr[2];
									
									String criteriArray[]=critMapValue.split("#####");
									criteriaName=criteriArray[0];
									criteriaValue=criteriArray[1];
									
									if(criteriaName.toUpperCase().contains("MATERIAL")){//material parsing
										criteriaName="Material";
										matList.add(criteriaValue);
									}else if(criteriaName.toUpperCase().contains("SHAPE")){//shape parsing
										criteriaName="Shape";
										shpList.add(criteriaValue);
									}else if(criteriaName.toUpperCase().contains("OPTION")){//option parsing
										//criteriaName="Product Option";
										if(criteriaName.contains("@@@@@")){
											String tempName[]=criteriaName.split("@@@@@");
											optionNamee=tempName[1];
											}
										opntnList.add(criteriaValue);
									}else if(criteriaName.toUpperCase().contains("CAPACITY")){//sizes(capacity)
										criteriaName="Size";
										sizList.add(criteriaValue);
									}
									priceGrids = primeLinePriceGridParser.getPriceGrids(listPrices,listQuan,disCodes,"USD","",
											true, "False", criteriaValue, 
											criteriaName, count,null, null,
											priceGrids);//,productExcelObj);
									count++;
								  }
								}
								// create material criteria over here
								if(!CollectionUtils.isEmpty(matList)){
								List<Material> listOfProductMaterial =	primeLineAttributeParser.getMaterials(matList);
								productConfigObj.setMaterials(listOfProductMaterial);
								}
								// create shapes criteria over here
								if(!CollectionUtils.isEmpty(shpList)){
								List<Shape> listOfShapes =	primeLineAttributeParser.getProductShapes(shpList);
								productConfigObj.setShapes(listOfShapes);
								}
								// create option criteria over here
								if(!CollectionUtils.isEmpty(opntnList)){
								List<Option> listOfOption =	primeLineAttributeParser.getOptions(opntnList,optionNamee);
								productConfigObj.setOptions(listOfOption);
								}
								// create size criteria over here
								if(!CollectionUtils.isEmpty(sizList)){
								Size sizesObj =	primeLineAttributeParser.getSizes(sizList);
								productConfigObj.setSizes(sizesObj);
								}
							}
						 }else{//QUR
							 priceGrids= getPriceGrids(productName);
						 }
							productExcelObj.setPriceType("L");
							productExcelObj.setPriceGrids(priceGrids);
							productExcelObj.setProductConfigurations(productConfigObj);
							
							_LOGGER.info("Product Data from sheet 1: "
									+ mapperObj.writeValueAsString(productExcelObj));
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
								baseDiscCode=null;
								shippingitemValue="";
								shippingWeightValue="";
								dimLen="";
								dimHieght="";
								dimWidth="";
								listOfCategories=new ArrayList<String>();
								listOfQuantity = new StringBuilder();
								listOfPrices = new StringBuilder();
								listOfDiscCodes=new StringBuilder();
								priceMap=new HashMap<String, StringBuilder>();
								
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    	repeatRows.add(xid);
						    }
						    productExcelObj = new Product();
						    existingApiProduct = postServiceImpl.getProduct(accessToken, xid, null); 
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
							String asiProdNo=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(asiProdNo)){
							productExcelObj.setAsiProdNo(asiProdNo);
							}
						break;
						case 3://ITEMNAME
						    productName=CommonUtility.getCellValueStrinOrDecimal(cell);
						    productName=CommonUtility.removeRestrictSymbols(productName);
						    String tempName=productName;
							int len=tempName.length();
							 if(len>60){
								String strTemp=tempName.substring(0, 60);
								int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
								tempName=(String) strTemp.subSequence(0, lenTemp);
							}
							productExcelObj.setName(tempName);
							break;
						case 4://NEW?
							break;
						case 5://CUSTOM?
							break;
						case 6://DISCOUNT?
							break;
						case 7://RUSH?
							String rushTime=CommonUtility.getCellValueStrinOrDecimal(cell);
							try{
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
								}catch(Exception e){
								_LOGGER.error("Eror while while processing rust time in case 7");
							}
							break;
						case 8://WEBSITEIMAGEPATH
							/*String largeImage = CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(largeImage)){
								//imagesList.add(largeImage);
							List<Image> listOfImages = primeLineAttributeParser.getImages(largeImage);
							productExcelObj.setImages(listOfImages);
							}*/
							break;
						case 9://QUANTITYBREAK1
							String	q1=null;
							q1=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(q1)){
								listOfQuantity.append(q1.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
							
							break;
						case 10://QUANTITYBREAK2
							String	q2=null;
							q2=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(q2)){
								listOfQuantity.append(q2.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
							break;
						case 11://QUANTITYBREAK3
							String	q3=null;
							q3=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(q3)){
								listOfQuantity.append(q3.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
							break;
						case 12://QUANTITYBREAK4
							String	q4=null;
							q4=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(q4)){
								listOfQuantity.append(q4.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
							break;
						case 13://QUANTITYBREAK5
							String	q5=null;
							q5=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(q5)){
								listOfQuantity.append(q5.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
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
							String	listPrice1=null;
							listPrice1=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(listPrice1)){
								listOfPrices.append(listPrice1.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
							break;
						case 20://GROSSPRICE2
							String	listPrice2=null;
							listPrice2=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(listPrice2)){
								listOfPrices.append(listPrice2.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
							break;
						case 21://GROSSPRICE3
							String	listPrice3=null;
							listPrice3=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(listPrice3)){
								listOfPrices.append(listPrice3.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);       	 
					         }
							break;
						case 22://GROSSPRICE4
							String	listPrice4=null;
							listPrice4=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(listPrice4)){
								listOfPrices.append(listPrice4.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID); 	 
					         }
							break;
						case 23://GROSSPRICE5
							String	listPrice5=null;
							listPrice5=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(listPrice5)){
								listOfPrices.append(listPrice5.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);		        	 
					         }
							break;
							
							
						/*case 24://NETPRICE_CAD1
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
							String	discountCode=null;
							discountCode=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(!StringUtils.isEmpty(discountCode) && !discountCode.toUpperCase().equals("NULL")){
								listOfDiscCodes=PrimeLineConstants.DISCOUNTCODE_MAP.get(discountCode.trim());
								if(StringUtils.isEmpty(listOfDiscCodes)){
									listOfDiscCodes.append("Z___Z___Z___Z___Z"); 
								}
								}else{
					        	 listOfDiscCodes.append("Z___Z___Z___Z___Z"); 
					         }
							break;
				}  // end inner while loop					 
			}		
		
			//if(!StringUtils.isEmpty(listOfPrices.toString()) && !StringUtils.isEmpty(listOfQuantity.toString())){
			//priceMap.put(productName, listOfPrices.append("@@@@@").append(listOfQuantity).append("@@@@@").append(listOfDiscCodes));
			if(!StringUtils.isEmpty(listOfPrices.toString())){	
			if(StringUtils.isEmpty(listOfDiscCodes.toString())){
					listOfDiscCodes.append("Z___Z___Z___Z___Z"); 
				}
				if(StringUtils.isEmpty(listOfQuantity.toString())){
					listOfDiscCodes.append("1___1___1___1___1"); 
				}
				priceMap.put(xid+":"+productName, listOfPrices.append("@@@@@").append(listOfQuantity).append("@@@@@").append(listOfDiscCodes));
			}
			
			 listOfQuantity = new StringBuilder();
			 listOfPrices = new StringBuilder();
			 listOfDiscCodes=new StringBuilder();
			}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);		 
				ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet 1: "
				+e.getMessage()+" at column number(increament by 1)"+columnIndex);
				productDaoObj.save(apiResponse.getErrors(),
						productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);
				}
		}
		workbook.close();
		priceGrids=	 new ArrayList<PriceGrid>();
		//priceGrids = primeLinePriceGridParser.getPriceGrids(priceMap);	
		if(!CollectionUtils.isEmpty(priceMap)){
		if(priceMap.size()==1){
			for (Map.Entry<String, StringBuilder> priceEntry : priceMap.entrySet()) {
				String tempStr=priceEntry.getValue().toString();
				String name=priceEntry.getKey().toString();
				String tempArr[]=tempStr.split("@@@@@");
				String listPrices=tempArr[0];
				String listQuan=tempArr[1];
				String	disCodes=tempArr[2];
				if(name.contains(":")){
					String tempStrr[]=name.split(":");
					name=tempStrr[1];
				}
				
				priceGrids = primeLinePriceGridParser.getPriceGrids(listPrices,listQuan,disCodes,"USD","",
						true, "False", name, 
						null, 1,null, null,
						priceGrids);//,productExcelObj);
			
			}	
		}else{
			
			List<String> matList=new ArrayList<String>();
			List<String> shpList =new ArrayList<String>();
			List<String> opntnList=new ArrayList<String>();
			List<String> sizList=new ArrayList<String>();
			String criteriaName="";
			String criteriaValue="";
			String optionNamee="";
			
			int count=1;
			for (Map.Entry<String, StringBuilder> priceEntry : priceMap.entrySet()) {
				
				String priceMapKey=priceEntry.getKey().toString();
				String priceMapValue=priceEntry.getValue().toString();
				/*if(name.contains("(") && name.contains(")") ){
				name = name.substring(name.indexOf("(")+1,name.indexOf(")"));
				}*/
				
				//matList.add(name);
				// I have to add different criteria list
				if(PrimeLineConstants.CRITPRICE_MAP.containsKey(priceMapKey)){
				String critMapValue=PrimeLineConstants.CRITPRICE_MAP.get(priceMapKey);
				
				//String tempKeyArr[]=tempStrKey.split(":");
				String pricingArr[]=priceMapValue.split("@@@@@");
				String listPrices=pricingArr[0];
				String listQuan=pricingArr[1];
				String	disCodes=pricingArr[2];
				
				String criteriArray[]=critMapValue.split("#####");
				criteriaName=criteriArray[0];
				criteriaValue=criteriArray[1];
				
				if(criteriaName.toUpperCase().contains("MATERIAL")){//material parsing
					criteriaName="Material";
					matList.add(criteriaValue);
				}else if(criteriaName.toUpperCase().contains("SHAPE")){//shape parsing
					criteriaName="Shape";
					shpList.add(criteriaValue);
				}else if(criteriaName.toUpperCase().contains("OPTION")){//option parsing
					//criteriaName="Product Option";
					if(criteriaName.contains("@@@@@")){
						String tempName[]=criteriaName.split("@@@@@");
						optionNamee=tempName[1];
						}
					opntnList.add(criteriaValue);
				}else if(criteriaName.toUpperCase().contains("CAPACITY")){//sizes(capacity)
					criteriaName="Size";
					sizList.add(criteriaValue);
				}
				priceGrids = primeLinePriceGridParser.getPriceGrids(listPrices,listQuan,disCodes,"USD","",
						true, "False", criteriaValue, 
						criteriaName, count,null, null,
						priceGrids);//,productExcelObj);
				count++;
			  }
			}
			// create material criteria over here
			if(!CollectionUtils.isEmpty(matList)){
			List<Material> listOfProductMaterial =	primeLineAttributeParser.getMaterials(matList);
			productConfigObj.setMaterials(listOfProductMaterial);
			}
			// create shapes criteria over here
			if(!CollectionUtils.isEmpty(shpList)){
			List<Shape> listOfShapes =	primeLineAttributeParser.getProductShapes(shpList);
			productConfigObj.setShapes(listOfShapes);
			}
			// create option criteria over here
			if(!CollectionUtils.isEmpty(opntnList)){
			List<Option> listOfOption =	primeLineAttributeParser.getOptions(opntnList,optionNamee);
			productConfigObj.setOptions(listOfOption);
			}
			// create size criteria over here
			if(!CollectionUtils.isEmpty(sizList)){
			Size sizesObj =	primeLineAttributeParser.getSizes(sizList);
			productConfigObj.setSizes(sizesObj);
			}
		}
	 }else{//QUR
		 priceGrids= getPriceGrids(productName);
	 }
		productExcelObj.setPriceType("L");
		productExcelObj.setPriceGrids(priceGrids);
		productExcelObj.setProductConfigurations(productConfigObj);
		
		_LOGGER.info("Product Data from sheet 1: "
				+ mapperObj.writeValueAsString(productExcelObj));
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
			baseDiscCode=null;
			shippingitemValue="";
			shippingWeightValue="";
			dimLen="";
			dimHieght="";
			dimWidth="";
			listOfCategories=new ArrayList<String>();
			listOfQuantity = new StringBuilder();
			listOfPrices = new StringBuilder();
			listOfDiscCodes=new StringBuilder();
			priceMap=new HashMap<String, StringBuilder>();
				}else if(i==1){
					sheetMap=primeLineColorTabParser.readColorTab( accessToken, workbook , asiNumber , batchId,sheetMap);
				}else if(i==2){
					sheetMap=primeLineFeatureTabParser.readFeatureTab(accessToken, workbook, asiNumber, batchId, sheetMap);
				}else if(i==3){
					sheetMap=primeLineImprintTabParser.readImprintTab(accessToken, workbook, asiNumber, batchId, sheetMap);
				}else if(i==4){
					finalResult=postingProducts(accessToken,asiNumber,batchId,sheetMap,environmentType);
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

	

	private String postingProducts(String accessToken,Integer asiNumber,int batchId,HashMap<String, Product> sheetMap,String environmentType) {
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		
		for (Map.Entry<String, Product> productEntry : sheetMap.entrySet())
		{
			try{
			Product productExcelObj=productEntry.getValue();
			productExcelObj.setPriceType("L");
			int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId, environmentType);
		 	if(num ==1){
		 		numOfProductsSuccess.add("1");
		 	}else if(num == 0){
		 		numOfProductsFailure.add("0");
		 	}
			}catch(Exception e){
				_LOGGER.error("Error while posting product  "+e.getMessage());
			}
		}
	 	
	 	_LOGGER.info("list size>>>>>>"+numOfProductsSuccess.size());
	 	_LOGGER.info("Failure list size>>>>>>"+numOfProductsFailure.size());
	 	String  finalResult = numOfProductsSuccess.size() + "," + numOfProductsFailure.size();
	 	productDaoObj.saveErrorLog(asiNumber,batchId);
		return finalResult;
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
		if(columnIndex != 1 && columnIndex != 3 && 
				columnIndex != 9 && columnIndex != 10 && columnIndex != 11 && columnIndex != 12 && columnIndex != 13 &&
				columnIndex != 19 && columnIndex != 20 && columnIndex != 21 && columnIndex != 22 && columnIndex != 23 && 
				columnIndex != 43
				){
		//if(columnIndex != 1&&columnIndex != 3&&columnIndex != 4 && columnIndex != 6 && columnIndex != 9 && columnIndex != 24){
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
		_LOGGER.info("PriceGrid Processed");
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



	public PrimeLinePriceGridParser getPrimeLinePriceGridParser() {
		return primeLinePriceGridParser;
	}



	public void setPrimeLinePriceGridParser(
			PrimeLinePriceGridParser primeLinePriceGridParser) {
		this.primeLinePriceGridParser = primeLinePriceGridParser;
	}
	
	
	public PrimeLineFeatureTabParser getPrimeLineFeatureTabParser() {
		return primeLineFeatureTabParser;
	}



	public void setPrimeLineFeatureTabParser(
			PrimeLineFeatureTabParser primeLineFeatureTabParser) {
		this.primeLineFeatureTabParser = primeLineFeatureTabParser;
	}



	public PrimeLineImprintTabParser getPrimeLineImprintTabParser() {
		return primeLineImprintTabParser;
	}



	public void setPrimeLineImprintTabParser(
			PrimeLineImprintTabParser primeLineImprintTabParser) {
		this.primeLineImprintTabParser = primeLineImprintTabParser;
	}



	public static String removeSpecialChar(String tempValue){
		tempValue=tempValue.replaceAll("(CLASS|Day|DAYS|Service|Days|Hour|Hours|Week|Weeks|Rush|day|service|days|hour|hours|week|weeks|Rush|R|u|s|h|®|™|$)", "");
		tempValue=tempValue.replaceAll("\\(","");
		tempValue=tempValue.replaceAll("\\)","");
	return tempValue;

	}
	
	
}
