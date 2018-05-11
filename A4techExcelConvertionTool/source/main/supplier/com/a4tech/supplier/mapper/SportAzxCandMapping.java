package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import parser.SportAzxCanada.SportsAzxCanColorParser;
import parser.SportsAzxUsa.SportsUsaAttributeParser;
import parser.SportsAzxUsa.SportsUsaPriceGridParser;
import parser.gillstudios.GillStudiosLookupData;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
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
import com.a4tech.product.model.Values;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class SportAzxCandMapping implements IExcelParser{



	private static final Logger _LOGGER = Logger.getLogger(SolidDimensionMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;
	private SportsUsaAttributeParser sportsUsaAttributeParser;
	private SportsUsaPriceGridParser sportsUsaPriceGridParser;
	private SportsAzxCanColorParser sportsAzxCanColorParser;

	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId,String environmentType){
		//List<String>  ProductProcessedList   = new ArrayList<>();
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		Set<String>  imprintSizeSet = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  String productId = null;
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  int columnIndex=0;
		  StringBuilder dimensionValue = new StringBuilder();
		  StringBuilder dimensionUnits = new StringBuilder();
		  StringBuilder dimensionType = new StringBuilder();
		  Dimension finalDimensionObj=new Dimension();
		  StringBuilder listOfQuantity = new StringBuilder();
		  StringBuilder listOfPrices = new StringBuilder();
		  StringBuilder priceIncludes = new StringBuilder();
		  StringBuilder pricesPerUnit = new StringBuilder();
		  StringBuilder ImprintSizevalue = new StringBuilder();
		  StringBuilder ImprintSizevalue2 = new StringBuilder();
		  //StringBuilder listofUpcharges = new StringBuilder();
		  
			//List<Color> colorList = new ArrayList<Color>();
			List<ImprintLocation> listImprintLocation = new ArrayList<ImprintLocation>();
			List<ImprintMethod> listOfImprintMethods = new ArrayList<ImprintMethod>();
			List<ProductionTime> listOfProductionTime = new ArrayList<ProductionTime>();
			//List<String> productKeywords = new ArrayList<String>();
			List<Theme> themeList = new ArrayList<Theme>();
			List<String> complianceList = new ArrayList<String>();
		    List<ImprintSize> imprintSizeList =new ArrayList<ImprintSize>();
		    List<Values> valuesList =new ArrayList<Values>();
			RushTime rushTime  = new RushTime();
			Size size=new Size();
			List<FOBPoint> FobPointsList = new ArrayList<FOBPoint>();
			FOBPoint fobPintObj=new FOBPoint();
			 List<String>categoriesList= new ArrayList<String>();
			 //List<AdditionalColor>additionalcolorList= new ArrayList<>();
			 Map<String, String>  imprintMethodUpchargeMap = new LinkedHashMap<>();
			 StringBuilder additionalClrRunChrgPrice = new StringBuilder();
				String        additionalClrRunChrgCode = "";
				String        additionalColorPriceVal = "";
				String        additionalColorCode     = "";
		try{
			 HashMap<String, String> priceMap=new HashMap<String, String>();
			 HashSet<String> sizeSet=new HashSet ();
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
			
			
			
		
			
		String priceCode = null;
		String productName = null;
		String quoteUponRequest  = null;
		String quantity = null;
		String price=null;
		String priceunit=null;
		String cartonL = null;
		String cartonW = null;
		String cartonH = null;
		String weightPerCarton = null;
		String unitsPerCarton = null;
		String decorationMethod =null;
		String FirstImprintsize1=null;
		String FirstImprintunit1=null;
		String FirstImprinttype1=null;
		String FirstImprintsize2=null;
		String FirstImprintunit2=null;
		String FirstImprinttype2=null;
		String SecondImprintsize1=null;
		String SecondImprintunit1=null;
		String SecondImprinttype1=null;
		String SecondImprintsize2=null;
		String SecondImprintunit2=null;
		String SecondImprinttype2=null;
		Cell cell2Data = null;
		String prodTimeLo = null;
		String FOBValue= null;
		String themeValue=null;
		String priceIncludesValue=null;
		String imprintLocation = null;
		String Category1=null;
		String rushProdTimeLo=null;
		Product existingApiProduct = null;
		String Setupcharge=null;
		String Setupcode=null;
		String Addcolorcharge=null;
		String Upchargecode="";
		String Addclearcode=null;
		
		String xid = null;
		List<String> repeatRows = new ArrayList<>();
		while (iterator.hasNext()) {
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum() < 1)
				continue;
			// this value is check first column or not becuase first column is skipped if value is not present
			boolean isFirstColum = true;
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if(productId != null){
				productXids.add(xid);
				//repeatRows.add(xid);
			}
			boolean checkXid  = false;
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				//String xid = null;
				 columnIndex = cell.getColumnIndex();
				if(columnIndex + 1 == 1){
					xid = getProductXid(nextRow);
					
					checkXid = true;
					isFirstColum = false;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							// System.out.println("Java object converted to JSON String, written to file");
							 if(CollectionUtils.isEmpty(listOfImprintMethods)){
								 listOfImprintMethods = sportsUsaAttributeParser.addUNImprintMethod(listOfImprintMethods);
								 }	
							 if(!priceMap.isEmpty()){
								 if(priceMap.size()>1){	
										Iterator mapItr = priceMap.entrySet().iterator();
									    while (mapItr.hasNext()) {
									       Map.Entry values = (Map.Entry)mapItr.next();
									        /*priceMap.put(dimensionValue.toString(), listOfPrices+"@@@@@"+listOfQuantity+"@@@@@"+priceCode+"@@@@@"
									 				 +priceIncludesValue+"@@@@@"+quoteUponRequest+"@@@@@"+productName+"@@@@@"+pricesPerUnit.toString());*/
									       /*  priceMap.put(tempDimension+"#####"+dimensionUnits.toString()+"#####"+dimensionType.toString(), listOfPrices+"@@@@@"+listOfQuantity+"@@@@@"+priceCode+"@@@@@"
									 				 +priceIncludesValue+"@@@@@"+quoteUponRequest+"@@@@@"+tempName+"@@@@@"+pricesPerUnit.toString()
									 				+"#@#@#"+ImprintSizevalue);*/
									       String pricingValue= (String) values.getValue();
									       String priceArry[]=pricingValue.split("#@#@#");//#@#@#
									       
									       String sizeImpArry[]=priceArry[0].split("@@@@@");
									     
									       String sizeValue=(String) values.getKey();
									       String lPrices=sizeImpArry[0];
									       String lQuantity=sizeImpArry[1];
									       String pCode=sizeImpArry[2];
									       String pIncludesVa=sizeImpArry[3];
									       String qUponReq=sizeImpArry[4];
									       if(!qUponReq.equalsIgnoreCase("true")){
									       String pName=sizeImpArry[5];
									       String pPerUnit=sizeImpArry[6];
									       // i have to work on this multiple pricing for sizing
									       String gridName=  pName+","+priceArry[1] ;
									       if(gridName.contains("_____")){
									    	   gridName=gridName.replace("_____", "");
									       }
									       
									       priceGrids = sportsUsaPriceGridParser.getPriceGrids(lPrices, 
									    		   lQuantity, pCode, "USD",pIncludesVa, true,
									    		   qUponReq, gridName,"Size",pPerUnit,priceGrids,sizeValue+":"+priceArry[1]);
									       }else{
									    	   priceGrids = sportsUsaPriceGridParser.getPriceGridsQur();	
									       }
									      /* (String listOfPrices,
									   		    String listOfQuan, String discountCodes,
									   			String currency, String priceInclude, boolean isBasePrice,
									   			String qurFlag, String priceName, String criterias,String priceUnitArr,
									   			List<PriceGrid> existingPriceGrid)*/
									    }				
									}else{		
										Map.Entry<String, String> entry = priceMap.entrySet().iterator().next();
										 String pricingValue= entry.getValue();
									       String priceArry[]=pricingValue.split("#@#@#");
									       String sizeImpArry[]=priceArry[0].split("@@@@@");
									      String lPrices=sizeImpArry[0];
									       String lQuantity=sizeImpArry[1];
									       String pCode=sizeImpArry[2];
									       String pIncludesVa=sizeImpArry[3];
									       String qUponReq=sizeImpArry[4];
									       ///////////////////////////////
									       if(!qUponReq.equalsIgnoreCase("true")){
									       String pName=sizeImpArry[5];
									       String pPerUnit=sizeImpArry[6];
									       // i have to work on this multiple pricing for sizing
									       priceGrids = sportsUsaPriceGridParser.getPriceGrids(lPrices, 
									    		   lQuantity, pCode, "USD",pIncludesVa, true,
									    		   qUponReq, "","",pPerUnit,priceGrids,null);
											/*priceGrids = sportsUsaPriceGridParser.getPriceGrids(listOfPrices.toString(), 
										                 listOfQuantity.toString(), priceCode, "USD",priceIncludesValue, true,
										                 quoteUponRequest, productName,"",pricesPerUnit.toString(),priceGrids,null);*/
									       }else{
									    	   priceGrids = sportsUsaPriceGridParser.getPriceGridsQur();	
									       }
									       ///////////////////////////////
									}				
														
									}					
								
							 if(!CollectionUtils.isEmpty(imprintSizeSet)){
								 for (String impsizeValue : imprintSizeSet) {
									 imprintSizeList=sportsUsaAttributeParser.getimprintsize(impsizeValue,imprintSizeList);
								}
								 productConfigObj.setImprintSize(imprintSizeList);
							 }
								
							//impmtd upchrg
								if(!imprintMethodUpchargeMap.isEmpty()){// here i ahve to check for imprint methods
							    	priceGrids = sportsUsaAttributeParser.getImprintMethodUpcharges(imprintMethodUpchargeMap,
						                    listOfImprintMethods, priceGrids);
							    }
								
								// additional colors & upcgrg
								List<AdditionalColor> additionalColorList = null;
								if(!StringUtils.isEmpty(additionalClrRunChrgCode) && !StringUtils.isEmpty(additionalClrRunChrgPrice.toString())){
									 additionalColorList = sportsUsaAttributeParser.getAdditionalColor("Additional Color");
										priceGrids = sportsUsaAttributeParser.getAdditionalColorRunUpcharge(additionalClrRunChrgCode,listOfQuantity.toString(),
												   additionalClrRunChrgPrice.toString(), priceGrids,"Run Charge");
										if(!CollectionUtils.isEmpty(additionalColorList)){
										productConfigObj.setAdditionalColors(additionalColorList);
										}
								}
								if(!StringUtils.isEmpty(additionalColorPriceVal) && !additionalColorPriceVal.equals("0")){
									if(additionalColorList == null){
										additionalColorList = sportsUsaAttributeParser.getAdditionalColor("Additional Color");
										productConfigObj.setAdditionalColors(additionalColorList);
									}
									priceGrids = sportsUsaAttributeParser.getAdditionalColorUpcharge(additionalColorCode,
											   additionalColorPriceVal, priceGrids,"Add. Color Charge");
									//(String discountCode,String quantity,String prices,List<PriceGrid> existingPriceGrid,String upchargeType){
								}
								ShippingEstimate shipping = sportsUsaAttributeParser.getShippingEstimateValues(cartonL, cartonW,
										                               cartonH, weightPerCarton, unitsPerCarton);
								productConfigObj.setImprintLocation(listImprintLocation);
								productConfigObj.setImprintMethods(listOfImprintMethods);
								if(!StringUtils.isEmpty(themeValue) ){
								productConfigObj.setThemes(themeList);
								}
								productConfigObj.setRushTime(rushTime);
								productConfigObj.setShippingEstimates(shipping);
								productConfigObj.setProductionTime(listOfProductionTime);
								
								if(!CollectionUtils.isEmpty(sizeSet)){
								for (String sizeValue : sizeSet) {
									String sizeValueArr[]=sizeValue.split("#####");
									valuesList =sportsUsaAttributeParser.getValues(sizeValueArr[0],
											sizeValueArr[1], sizeValueArr[2],valuesList);
									
							       
								}
								 finalDimensionObj.setValues(valuesList);	
									size.setDimension(finalDimensionObj);
									productConfigObj.setSizes(size);
								
								}
								
								//productConfigObj.setColors(colorList);
								if(!StringUtils.isEmpty(FobPointsList)){
								productExcelObj.setFobPoints(FobPointsList);
								}
								 if(CollectionUtils.isEmpty(priceGrids)){
										priceGrids = sportsUsaPriceGridParser.getPriceGridsQur();	
									}
							 	productExcelObj.setPriceGrids(priceGrids);
							 	productExcelObj.setProductConfigurations(productConfigObj);

							 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId,environmentType);
							 	if(num ==1){
							 		numOfProductsSuccess.add("1");
							 	}else if(num == 0){
							 		numOfProductsFailure.add("0");
							 	}else{
							 		
							 	}
							 	_LOGGER.info("list size>>>>>>>"+numOfProductsSuccess.size());
							 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
								priceGrids = new ArrayList<PriceGrid>();
								listOfPrices = new StringBuilder();
							    listOfQuantity = new StringBuilder();
								productConfigObj = new ProductConfigurations();
								themeList = new ArrayList<Theme>();
								finalDimensionObj = new Dimension();
								 valuesList = new ArrayList<>();
								//productKeywords = new ArrayList<String>();
								listOfProductionTime = new ArrayList<ProductionTime>();
								rushTime = new RushTime();
								listImprintLocation = new ArrayList<ImprintLocation>();
								listOfImprintMethods = new ArrayList<ImprintMethod>();
								imprintSizeList =new ArrayList<ImprintSize>();
								ImprintSizevalue = new StringBuilder();
								size=new Size();
								//colorList = new ArrayList<Color>();
								FobPointsList = new ArrayList<FOBPoint>();
								 dimensionValue = new StringBuilder();
								 dimensionUnits = new StringBuilder();
								 dimensionType = new StringBuilder();
								 priceIncludes = new StringBuilder();
								 priceIncludesValue=null;
								 //additionalcolorList= new ArrayList<>();
								 //listofUpcharges = new StringBuilder();
								 imprintMethodUpchargeMap = new LinkedHashMap<>();
								 additionalClrRunChrgPrice = new StringBuilder();
							     additionalClrRunChrgCode = "";
							     additionalColorPriceVal = "";
						         additionalColorCode     = "";
						         pricesPerUnit=new StringBuilder();
						         ImprintSizevalue = new StringBuilder();
								 ImprintSizevalue2 = new StringBuilder();
								 priceMap=new HashMap<String, String>();
								 sizeSet=new HashSet<String>();
								 priceIncludesValue=new String();
								 imprintSizeSet = new HashSet<String>();
								 complianceList = new ArrayList<String>();
								 repeatRows.clear();
							        
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid.trim());
						    }
						    existingApiProduct = postServiceImpl.getProduct(accessToken, xid=xid.replace("\t",""), environmentType);
						     if(existingApiProduct == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						    	 productExcelObj = sportsUsaAttributeParser.getExistingProductData(existingApiProduct, existingApiProduct.getProductConfigurations(),accessToken,environmentType);
						    	 productConfigObj=productExcelObj.getProductConfigurations();
						     }
					 }
				}/*else{
					if(productXids.contains(xid) && repeatRows.size() != 0){
						 if(isRepeateColumn(columnIndex+1)){
							 continue;
						 }
					}
				}*/
				

				switch (columnIndex + 1) {
				
				case 1://xid
					 if(!StringUtils.isEmpty(xid)){
						   productExcelObj.setExternalProductId(xid);
					   /*productExcelObj.setExternalProductId(xid.trim());
					}else{
						xid = getProductXid(nextRow);
						  productExcelObj.setExternalProductId(xid.trim());*/
					}
					 //System.out.println("case 1");
					 break;
			
					 
				case 2://ItemNum
					 String asiProdNo=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(asiProdNo)){
					int Nolength=asiProdNo.length();
					 if(Nolength>14){
							String strTemp=asiProdNo.substring(0, 14);
						     productExcelObj.setAsiProdNo(strTemp);		
					 }else{
				     productExcelObj.setAsiProdNo(asiProdNo);		
					 }
					 if(!StringUtils.isEmpty(xid)){
						   productExcelObj.setExternalProductId(xid.trim());
						}else{
							xid = getProductXid(nextRow,true);
							  productExcelObj.setExternalProductId(xid.trim());
						}
					 }
					// System.out.println("case 2");
					 break;
				case 3://Name
					
					 productName = cell.getStringCellValue();
					 if(!StringUtils.isEmpty(productName)){
						int len=productName.length();
						 if(len>60){
							String strTemp=productName.substring(0, 60);
							int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
							productName=(String) strTemp.subSequence(0, lenTemp);
						}
						productExcelObj.setName(productName);	
					 }
					// System.out.println("case 3");
					  break;
				case 4://CatYear

					break;
				case 5://ExpirationDate

				    break;
				case 6://Discontinued
		
					break;
				case 7: //Cat1Name
					
					String category=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(category)){
						 List<String> listOfCategories = new ArrayList<String>();
						 boolean flag=sportsUsaAttributeParser.getCategory(category);
						 if(flag){
						 listOfCategories.add(category);
						 productExcelObj.setCategories(categoriesList);
						 }
						 }
					 //System.out.println("case 7");
					 break;
				case 8://Cat2Name
					String category2=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(category2)){
						 List<String> listOfCategories = new ArrayList<String>();
						 boolean flag=sportsUsaAttributeParser.getCategory(category2);
						 if(flag){
						 listOfCategories.add(category2);
						 if(!CollectionUtils.isEmpty(productExcelObj.getCategories())){
							 listOfCategories.addAll(productExcelObj.getCategories());
						 }
						 productExcelObj.setCategories(categoriesList);
						 }
						 }
					break;
					
					
				case 9: //page1
					//Category1=cell.getStringCellValue();
				
					 break;
					 
				case 10: //page2
					//Category1=cell.getStringCellValue();
				
					 break;
					
				case 11: // Description
					String description =CommonUtility.getCellValueStrinOrInt(cell);
					//description=description.replaceAll("™", "");
					///description=description.replaceAll("®", "");
					//description=description.replaceAll("soft touch", "");
					if(!StringUtils.isEmpty(description)){
					description=CommonUtility.removeRestrictSymbols(description);
					String strSummary=description;
					
					int length=description.length();
					if(length>800){
						String strTemp=description.substring(0, 800);
						int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
						description=(String) strTemp.subSequence(0, lenTemp);
					}
					productExcelObj.setDescription(description);
					if(length>130){
					 strSummary=strSummary.substring(0, 130);
					}
					productExcelObj.setSummary(strSummary);
					}
					break;
					
				case 12: //Keywords
					String productKeyword = cell.getStringCellValue();
					if(!StringUtils.isEmpty(productKeyword)){
					//String productKeywordArr[] = productKeyword.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					/*for (String string : productKeywordArr) {
						productKeywords.add(string);
					}*/
					HashSet<String> keyWDSet=new HashSet<String>();
					List<String> listOfValues=new ArrayList<>();
					listOfValues=CommonUtility.getStringAsList(productKeyword);
					for (String string : listOfValues) {
						keyWDSet.add(string.toUpperCase().trim());
					}
					//
					listOfValues=new ArrayList<String>(keyWDSet);
					productExcelObj.setProductKeywords(listOfValues);
					}
					break;
					
				case 13:  //Colors
					String colorValue=cell.getStringCellValue();
					if(!StringUtils.isEmpty(colorValue)){
						List<Color>	colorList=sportsAzxCanColorParser.getProductColors(colorValue, productExcelObj.getExternalProductId());
						productConfigObj.setColors(colorList);
					}	
						
					
					break;
					
				case 14:  //Themes

					 themeValue=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(themeValue)){
					 themeList = new ArrayList<Theme>();
					//if(!StringUtils.isEmpty(themeValue)){
						Theme themeObj=null;
						String Value=null;
						List<String>themeLookupList = lookupServiceDataObj.getTheme(Value);
						String themeValueArr[] = themeValue.toUpperCase().split(ApplicationConstants.CONST_STRING_COMMA_SEP);
						for (String themvalue : themeValueArr) {
							themeObj=new Theme();
							if(themeLookupList.contains(themvalue.trim())){
							themeObj.setName(themvalue.trim());
							themeList.add(themeObj);
							}
							}
					 }
									
					break;
				
				case 15:  // Dimension1

					String dimensionValue1=CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(dimensionValue1) && !dimensionValue1.equals("0")){
						   dimensionValue.append(dimensionValue1).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
						 
					   
					}
					break;

				case 16: //Dimension1Units

					String dimensionUnits1 = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(dimensionUnits1) && !dimensionUnits1.equals("0")){
						 dimensionUnits.append(dimensionUnits1.trim()).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					 }
					break;
					
				case 17: // Dimension1Type

					String dimensionType1 =CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(dimensionType1) && !dimensionType1.equals("0")){
						dimensionType.append(dimensionType1).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					  
					break;
					
				case 18://Dimension2

					String dimensionValue2 =CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(dimensionValue2) && !dimensionValue2.equals("0")){
						 dimensionValue.append(dimensionValue2).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					 }
					
					break;
				case 19: //Dimension2Units

					  String dimensionUnits2 = CommonUtility.getCellValueStrinOrInt(cell);
					  if(!StringUtils.isEmpty(dimensionUnits2) && !dimensionUnits2.equals("0")){
						 dimensionUnits.append(dimensionUnits2.trim()).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					 }
					  break;
				
				case 20: //Dimension2Type
					String  dimensionType2 = CommonUtility.getCellValueStrinOrInt(cell);

					if(!StringUtils.isEmpty(dimensionType2) && !dimensionType2.equals("0")){
						dimensionType.append(dimensionType2).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					
					 
					break;
				
				 case 21: //Dimension3
					 String dimensionValue3  =CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(dimensionValue3) && !dimensionValue3.equals("0")){
							dimensionValue.append(dimensionValue3).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
						}else{
							dimensionValue=dimensionValue.append("");
						}
				
					break;
					
				case 22:  //Dimension3Units

					String dimensionUnits3 = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(dimensionUnits3) && !dimensionUnits3.equals("0")){
						 dimensionUnits.append(dimensionUnits3.trim()).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}else
					{
						dimensionUnits=dimensionUnits.append("");
					}
					break;
					
				case 23: //Dimension3Type

					String dimensionType3 = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(dimensionType3) && !dimensionType3.equals("0")){
						dimensionType.append(dimensionType3).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}else
					{
						dimensionType=dimensionType.append("");
					}
					break;
					
				case 24: //Qty1
				case 25: //Qty2
				case 26: //Qty3
				case 27: //Qty4
				case 28: //Qty5
				case 29: //Qty6
					try{
						if(cell.getCellType() == Cell.CELL_TYPE_STRING){
							quantity = cell.getStringCellValue();
					         if(!StringUtils.isEmpty(quantity) && !quantity.equals("0")){
					        	 listOfQuantity.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
							int quantity1 = (int)cell.getNumericCellValue();
					         if(!StringUtils.isEmpty(quantity1) && quantity1 !=0){
					        	 listOfQuantity.append(quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						}else{
						}
					}catch (Exception e) {
						_LOGGER.info("Error in base price Quantity field "+e.getMessage());
					}
					break;
				case 30://Prc1
				case 31://Prc2
				case 32://Prc3
				case 33://Prc4
				case 34://Prc5
				case 35://Prc6
					try{
						 if(cell.getCellType() == Cell.CELL_TYPE_STRING){
							 price = cell.getStringCellValue();
						         if(!StringUtils.isEmpty(price)&& !price.equals("0")){
						        	 listOfPrices.append(price).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						         }
							}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
								double price1 = (double)cell.getNumericCellValue();
						         if(!StringUtils.isEmpty(price1)){
						        	 listOfPrices.append(price1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						         }
							}else{
							}  
					 }catch (Exception e) {
						_LOGGER.info("Error in base price prices field "+e.getMessage());
					}
						
						    break; 
				case 36://PrCode
					priceCode = cell.getStringCellValue();	
					
					 if(!StringUtils.isEmpty(priceCode))
					 {
					if(priceCode.contains("C"))
					{
						priceCode="C";
					}
					else if(priceCode.contains("R"))
					{
						priceCode="R";

					}else if(priceCode.contains("P"))
					{
						priceCode="P";

					}else{
						
					}
			     	}
				    break; 

				case 37://PiecesPerUnit1
				case 38://PiecesPerUnit2
				case 39: // PiecesPerUnit3
				case 40: // PiecesPerUnit4
				case 41://PiecesPerUnit5
				case 42://PiecesPerUnit6
					try{
					
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						priceunit = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(priceunit) && !priceunit.equals("0")){
				        	 pricesPerUnit.append(priceunit).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						double priceunit1 = (double)cell.getNumericCellValue();
				         if(!StringUtils.isEmpty(priceunit1)){
				        	 pricesPerUnit.append(priceunit1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else{
					}  				
					}catch (Exception e) {
						_LOGGER.info("Error in pricePerUnit field "+e.getMessage());
					}
					break;
				case 43://QuoteUponRequest
				     quoteUponRequest = cell.getStringCellValue();

				     break;
				case 44://PriceIncludeClr
				      priceIncludes.append(cell.getStringCellValue()).append(" ");
				      break;

				case 45://PriceIncludeSide
					priceIncludes.append(cell.getStringCellValue()).append(" ");
					break;
				case 46://PriceIncludeLoc
					priceIncludes.append(cell.getStringCellValue());
					int PriceIncludeLength=priceIncludes.length();
					if(PriceIncludeLength>100){
						
					priceIncludesValue=	priceIncludes.toString().substring(0,100);
					}else
					{
						priceIncludesValue=priceIncludes.toString();
					}
					//System.out.println("case 46");
					      break;
				case 47:  //SetupChg
					String setupChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(setupChargePrice) && !setupChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("setupCharge", setupChargePrice);
					}
					
					     break;
				case 48: // SetupChgCode
					String setUpchargeCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(setUpchargeCode) && !setUpchargeCode.equals("0")){
						String priceVal = imprintMethodUpchargeMap.get("setupCharge");
						priceVal = priceVal+"_"+setUpchargeCode;
						imprintMethodUpchargeMap.put("setupCharge", priceVal);
					}
						break;
			
				
				case 49://ScreenChg
					String screenChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(screenChargePrice) && !screenChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("screenCharge", screenChargePrice);
					}
					break;
						
				case 50://ScreenChgCode
					String screenchargeCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(screenchargeCode) && !screenchargeCode.equals("0")){
						String priceVal = imprintMethodUpchargeMap.get("screenCharge");
						priceVal = priceVal+"_"+screenchargeCode;
						imprintMethodUpchargeMap.put("screenCharge", priceVal);
					}
					
					break;
								
				case 51://PlateChg
					String plateChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(plateChargePrice) && !plateChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("plateCharge", plateChargePrice);
					}
					
					break;	
					
            	case 52://PlateChgCode
            		String plateChargeCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(plateChargeCode) && !plateChargeCode.equals("0")){
						String priceVal = imprintMethodUpchargeMap.get("plateCharge");
						priceVal = priceVal+"_"+plateChargeCode;
						imprintMethodUpchargeMap.put("plateCharge", priceVal);
					}
					
					break;			
						
            	case 53://DieChg
            		String dieChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(dieChargePrice) && !dieChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("dieCharge", dieChargePrice);
					}
					
					break;			
					
					
            	case 54://DieChgCode
            		String diaChargeCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(diaChargeCode) && !diaChargeCode.equals("0")){
						String priceVal = imprintMethodUpchargeMap.get("dieCharge");
						priceVal = priceVal+"_"+diaChargeCode;
						imprintMethodUpchargeMap.put("dieCharge", priceVal);
					}

					
					break;		
					
               case 55://ToolingChg
            	   String toolingChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(toolingChargePrice) && !toolingChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("toolingCharge", toolingChargePrice);
					}
            	   
					break;		
					
               case 56://ToolingChgCode
            	   String toolingChargeCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(toolingChargeCode) && !toolingChargeCode.equals("0")){
						String priceVal = imprintMethodUpchargeMap.get("toolingCharge");
						priceVal = priceVal+"_"+toolingChargeCode;
						imprintMethodUpchargeMap.put("toolingCharge", priceVal);
					}
       			
					break;	
					
               case 57://RepeatChg

          			
					break;
					
               case 58://RepeatChgCode


					break;
					
				case 59://AddClrChg
					 additionalColorPriceVal = CommonUtility.getCellValueStrinOrInt(cell);

							break;
				case 60://AddClrChgCode
					additionalColorCode =  CommonUtility.getCellValueStrinOrInt(cell);
							break; 
				case 61://AddClrRunChg1
				case 62://AddClrRunChg2
				case 63://AddClrRunChg3
				case 64://AddClrRunChg4
				case 65://AddClrRunChg5
				case 66://AddClrRunChg6
					 String colorChargePrice = CommonUtility.getCellValueDouble(cell);
					 if(!StringUtils.isEmpty(colorChargePrice) && !colorChargePrice.equals("0")){
					  additionalClrRunChrgPrice.append(colorChargePrice).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					  }
							break;
				case 67://AddClrRunChgCode
					 String additionalClrRunCode =  CommonUtility.getCellValueStrinOrInt(cell);
				      if(!StringUtils.isEmpty(additionalClrRunCode)){
					       additionalClrRunChrgCode = additionalClrRunCode;
				      }  
							break;
				case 68://IsRecyclable

							break;
				case 69://IsEnvironmentallyFriendly
					String IsEnvironmentallyFriendly =  CommonUtility.getCellValueStrinOrInt(cell);
					boolean flag=true;
						if(IsEnvironmentallyFriendly.equalsIgnoreCase("true"))			
						{ 
							List<Theme> themeListTempp = productConfigObj.getThemes();
							 if(!CollectionUtils.isEmpty(themeListTempp)){
								 for (Theme theme : themeListTempp) {
									if(theme.getName().toUpperCase().contains("ECO")){
										flag=false;
									}
								}
							 }
							///
							 if(CollectionUtils.isEmpty(themeList)){
								 themeList = new ArrayList<Theme>();
								 }
							 if(flag){
							Theme themeObj1 = new Theme();
							themeObj1.setName("ECO & ENVIRONMENTALLY FRIENDLY");
							themeList.add(themeObj1);
							 if(!CollectionUtils.isEmpty(themeListTempp)){
								 themeList.addAll(themeListTempp);
							 }
							 }
						}
						//System.out.println("case 69");
							break;
				case 70://IsNewProd

			          		break; 
				case 71://NotSuitable
					
					       break;
				case 72://Exclusive
					
					break;
				case 73://Hazardous
					String hazardous =  CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(hazardous)){
					if(hazardous.equalsIgnoreCase("true"))			
					{ 
						productExcelObj.setHazmat("TRUE");
					}
					}
					break;
				case 74://OfficiallyLicensed
					
					break;
				case 75://IsFood
					
					break;
				case 76://IsClothing
					
					break;
				case 77: // Imprint size1
					 FirstImprintsize1=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(FirstImprintsize1.trim()) && !FirstImprintsize1.equals("0")){
					 ImprintSizevalue=ImprintSizevalue.append(FirstImprintsize1).append(" ");
					
					 }
					    break;
					    
				case 78: //// Imprint size1 unit
					FirstImprintunit1=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(FirstImprintunit1.trim()) && !FirstImprintunit1.equals("0")){
					FirstImprintunit1=GillStudiosLookupData.Dimension1Units.get(FirstImprintunit1);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprintunit1).append(" ");
					 }	 
					   	break;
					   	
				case 79:   // Imprint size1 Type
					FirstImprinttype1=CommonUtility.getCellValueStrinOrInt(cell);
				   if(!StringUtils.isEmpty(FirstImprinttype1.trim()) && !FirstImprinttype1.equals("0")){
					FirstImprinttype1=GillStudiosLookupData.Dimension1Type.get(FirstImprinttype1);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprinttype1).append(" ").append("x");
				   }
						break;
						
				  
				case 80: // // Imprint size2
					FirstImprintsize2=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(FirstImprintsize2.trim()) && !FirstImprintsize2.equals("0")){
						 ImprintSizevalue=ImprintSizevalue.append(FirstImprintsize2).append(" ");
					 }
					 
					  	break;
					  	
				case 81:	// Imprint size2 Unit
					FirstImprintunit2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(FirstImprintunit2.trim()) && !FirstImprintunit2.equals("0")){
					FirstImprintunit2=GillStudiosLookupData.Dimension1Units.get(FirstImprintunit2);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprintunit2).append(" ");
				    }
					    break;
					    
				case 82: // Imprint size2 Type
					FirstImprinttype2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(FirstImprinttype2.trim()) && !FirstImprinttype2.equals("0")){
					FirstImprinttype2=GillStudiosLookupData.Dimension1Type.get(FirstImprinttype2);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprinttype2).append(" ");
				    }
					break;
					
					    
				case 83: //ImprintLoc
					
					String imprintLocation2 =  CommonUtility.getCellValueStrinOrInt(cell);
					try{//////testing values
					if(!StringUtils.isEmpty(imprintLocation2) && !imprintLocation2.equals("0")){
						ImprintLocation locationObj2 = new ImprintLocation();
						locationObj2.setValue(imprintLocation2);
						listImprintLocation.add(locationObj2);
					}
					}catch (Exception e) {
						_LOGGER.error(e.getMessage());
						_LOGGER.error(e.getClass());
						_LOGGER.error(e.getCause());
					}
					
					break;
					  	
				case 84:  // Second Imprintsize1
					
					SecondImprintsize1=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprintsize1.trim()) && !SecondImprintsize1.equals("0")){
					ImprintSizevalue2=ImprintSizevalue2.append(SecondImprintsize1).append(" ");
				    }
					   	break;
					   	
				case 85:  // Second Imprintsize1 unit
					SecondImprintunit1=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprintunit1.trim()) && !SecondImprintunit1.equals("0")){
					SecondImprintunit1=GillStudiosLookupData.Dimension1Units.get(SecondImprintunit1);
					ImprintSizevalue2=ImprintSizevalue2.append(SecondImprintunit1).append(" ");
					
					}
					
						break;
						
				case 86:  // Second Imprintsize1 type
					SecondImprinttype1=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprinttype1.trim())  && !SecondImprinttype1.equals("0")){
					SecondImprinttype1=GillStudiosLookupData.Dimension1Type.get(SecondImprinttype1);
					ImprintSizevalue2=ImprintSizevalue2.append(SecondImprinttype1).append(" ").append("x");
					
					}
				    
					  break;
					  
				case 87: // Second Imprintsize2
					SecondImprintsize2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprintsize2.trim())  && !SecondImprintsize2.equals("0")){
				    ImprintSizevalue2=ImprintSizevalue2.append(SecondImprintsize2).append(" ");
				    
				    }
				    
					
					break;
					
				case 88: //Second Imprintsize2 Unit
					SecondImprintunit2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprintunit2.trim()) && !SecondImprintunit2.equals("0")){
					SecondImprintunit2=GillStudiosLookupData.Dimension1Units.get(SecondImprintunit2);
					ImprintSizevalue2=ImprintSizevalue2.append(SecondImprintunit2).append(" ");
					
					}
				    
					break;
					
				case 89: // Second Imprintsize2 type	
					SecondImprinttype2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprinttype2.trim()) && !SecondImprinttype2.equals("0")){
					SecondImprinttype2=GillStudiosLookupData.Dimension1Type.get(SecondImprinttype2);
					ImprintSizevalue2=ImprintSizevalue2.append(SecondImprinttype2).append(" ");
					
					}
					
				/*	  ImprintSizevalue=append(FirstImprintunit1).
					  append(" ").append(FirstImprinttype1).append("x").append(FirstImprintsize2).append(" ").
					  append(FirstImprintunit2).append(" ").append(FirstImprinttype2).append(",").
					  append(SecondImprintsize1).append(" ").append(SecondImprintunit1).
					  append(" ").append(SecondImprinttype1).append("x").append(SecondImprintsize2).append(" ").
					  append(SecondImprintunit2).append(" ").append(SecondImprinttype2);
					*/
					  break;
					
				case 90: //SecondImprintLoc
					
					String imprintLocation3 =  CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(imprintLocation3) && !imprintLocation3.equals("0")){
						ImprintLocation locationObj2 = new ImprintLocation();
						locationObj2.setValue(imprintLocation3);
						listImprintLocation.add(locationObj2);
					}
					  break;
					  
				case 91: //DecorationMethod
					decorationMethod = cell.getStringCellValue();
					 if(!StringUtils.isEmpty(decorationMethod)){
						listOfImprintMethods = sportsUsaAttributeParser.getImprintMethodValues(decorationMethod,listOfImprintMethods);
					 }
						
					break;
				case 92: // NoDecoration
					String noDecoration = cell.getStringCellValue();
					 if(!StringUtils.isEmpty(noDecoration)){
					if(noDecoration.equalsIgnoreCase(ApplicationConstants.CONST_STRING_TRUE)){
						listOfImprintMethods = sportsUsaAttributeParser.addUNImprintMethod(listOfImprintMethods);
						}
					 }
					 break; 
					 
				case 93: //NoDecorationOffered
					/*String noDecorationOffered = cell.getStringCellValue();
					if(noDecorationOffered.equalsIgnoreCase(ApplicationConstants.CONST_STRING_TRUE)){
						listOfImprintMethods = sportsUsaAttributeParser.getImprintMethodValues(noDecorationOffered,
                                listOfImprintMethods);
					}*/
					
					
					 break;
					 
					 
					 
				case 94://NewPictureURL
					
					
					break;
					
				case 95://NewPictureFile
					
					
					break;
					
				case 96://ErasePicture

					
					break;
					
				case 97://NewBlankPictureURL

					
			
					break;
					
				case 98://NewBlankPictureFile


					
					break;
				
					
				case 99://EraseBlankPicture

			
					break;
					
					
	            case 100://NotPictured
	            	

					
					break;
					
					
				case 101: //MadeInCountry
					String madeInCountry = cell.getStringCellValue();
					if(!madeInCountry.isEmpty()){
						List<Origin> listOfOrigin = sportsUsaAttributeParser.getOriginValues(madeInCountry);
						productConfigObj.setOrigins(listOfOrigin);
					}
					break;
				case 102: //AssembledInCountry
				     String additionalProductInfo = cell.getStringCellValue();
				     if(!StringUtils.isEmpty(additionalProductInfo))
				       {
				    	productExcelObj.setAdditionalProductInfo(additionalProductInfo); 
				       }
					break;
				case 103: //DecoratedInCountry

					String additionalImprintInfo = cell.getStringCellValue();
					 if(!StringUtils.isEmpty(additionalImprintInfo))
					   {
						 productExcelObj.setAdditionalImprintInfo(additionalImprintInfo);
					   }
					
				
					break;
					
				case 104:// ComplianceList
					String complnceValuet=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(complnceValuet))
					   {
				    	complianceList.add(complnceValuet);
				    	productExcelObj.setComplianceCerts(complianceList);
					   }
				
					break;
				case 105: //ComplianceMemo

					break;
				case 106: //ProdTimeLo
					   prodTimeLo = CommonUtility.getCellValueStrinOrInt(cell);
				
					break;
					
				case 107://ProdTimeHi
					String prodTimeHi = CommonUtility.getCellValueStrinOrInt(cell);
					ProductionTime productionTime = new ProductionTime();
					if (!prodTimeHi
							.equals(ApplicationConstants.CONST_STRING_ZERO)) {

					if(prodTimeLo.equalsIgnoreCase(prodTimeHi))
					{
						productionTime = new ProductionTime();
						productionTime.setBusinessDays(prodTimeHi);
						listOfProductionTime.add(productionTime);
					}
					else
					{	
						productionTime = new ProductionTime();
						String prodTimeTotal="";
						if(prodTimeLo.equals("0") || prodTimeHi.equals("0")){//prodTimeHi
							if(!prodTimeLo.equals("0")){
							productionTime = new ProductionTime();
							productionTime.setBusinessDays(prodTimeLo);
							listOfProductionTime.add(productionTime);
							}
							if(!prodTimeHi.equals("0")){
								productionTime = new ProductionTime();
								productionTime.setBusinessDays(prodTimeHi);
								listOfProductionTime.add(productionTime);
							}
						}else{
							productionTime = new ProductionTime();
						prodTimeTotal=prodTimeTotal.concat(prodTimeLo).concat("-").concat(prodTimeHi);
						productionTime.setBusinessDays(prodTimeTotal);
						listOfProductionTime.add(productionTime);
						}
					
					}
					}else if (!prodTimeLo.equals(ApplicationConstants.CONST_STRING_ZERO)) {
						productionTime = new ProductionTime();
						productionTime.setBusinessDays(prodTimeLo);
						listOfProductionTime.add(productionTime);
					}
					break;
					
				case 108: //RushProdTimeLo
				    rushProdTimeLo  = cell.getStringCellValue();
				
					break;
				case 109: //RushProdTimeHi
					String rushProdTimeH  = cell.getStringCellValue();
					if (!rushProdTimeLo
							.equals(ApplicationConstants.CONST_STRING_ZERO)) {
						rushTime = sportsUsaAttributeParser.getRushTimeValues(rushProdTimeLo,
										rushProdTimeH);
					}

					
					
					break;
				case 110://Packaging
					String packageValue=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(packageValue)){
						 if(packageValue.toUpperCase().contains("GIFT")){
							 packageValue="Gift Boxes";
						 }
						 List<Packaging> listOfPackage=sportsUsaAttributeParser.getPackageValues(packageValue);
						 productConfigObj.setPackaging(listOfPackage);
					 }
					 //System.out.println("case 110");
					 break; 	 
				case 111://CartonL

					 cartonL  = CommonUtility.getCellValueStrinOrInt(cell);

					break;
					
				case 112://CartonW
					cartonW  = CommonUtility.getCellValueStrinOrInt(cell);
				
					break;
					
				case 113: //CartonH
					cartonH  = CommonUtility.getCellValueStrinOrInt(cell);

					
					break;
				case 114://WeightPerCarton
					weightPerCarton  =CommonUtility.getCellValueStrinOrInt(cell);
					break;
	
				case 115://UnitsPerCarton
					unitsPerCarton  = CommonUtility.getCellValueStrinOrInt(cell);
					break;
				case 116: //ShipPointCountry
					String shipInCountry  = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(shipInCountry))
					 {
						productExcelObj.setAdditionalShippingInfo(shipInCountry);
					 }
				
					break;
				case 117: //ShipPointZip
				  	 FOBValue=CommonUtility.getCellValueStrinOrInt(cell);
						
							 if(!StringUtils.isEmpty(FOBValue))
							 {
								if(FOBValue.contains("L7L 0J8"))
								{
									fobPintObj=new FOBPoint();
									fobPintObj.setName("Burlington, ON L7L 0J8 Canada");
									FobPointsList.add(fobPintObj);

								}
								 
								 productExcelObj.setAdditionalProductInfo(FOBValue);
							 }
					
					break;
					
				case 118: //Comment
		
					 
					break;
					
				case 119: //Verified
					String verified=cell.getStringCellValue();
					if(verified.equalsIgnoreCase("True")){
					String priceConfimedThruString="2018-12-31T00:00:00";
					productExcelObj.setPriceConfirmedThru(priceConfimedThruString);
					}
					
					break;
					
					
				case 120://UpdateInventory

					
					break;
					
				case 121://InventoryOnHand


					break;
					
				case 122://InventoryOnHandAdd


					break;
					
				case 123://InventoryMemo

					
					break;
				
				
			}  // end inner while loop
					 
		}
			// set  product configuration objects
			
			
			 // end inner while loop
			productExcelObj.setPriceType("L");
			String tempDimension=null;
			tempDimension=dimensionValue.toString();
			if(!StringUtils.isEmpty(tempDimension)){
				/*imprintSize.add(ImprintSizevalue.toString());
				imprintSize.add(ImprintSizevalue2.toString());*/
				/*if(!StringUtils.isEmpty(ImprintSizevalue2.toString())){
					//ImprintSizevalue=ImprintSizevalue.append("___").append(ImprintSizevalue2);
					ImprintSizevalue=ImprintSizevalue.append(ImprintSizevalue2);
				}*/
				if(!StringUtils.isEmpty(ImprintSizevalue.toString())){
					imprintSizeSet.add(ImprintSizevalue.toString());
				}
				if(!StringUtils.isEmpty(ImprintSizevalue2.toString())){
					imprintSizeSet.add(ImprintSizevalue2.toString());
				}
					
					//imprintSize.add(ImprintSizevalue.toString());
					sizeSet.add(tempDimension+"#####"+dimensionUnits.toString()+"#####"+dimensionType.toString());
					//}
					String tempName=tempDimension.replace("@@", "\" x");
					if(tempName.endsWith("x")){
						tempName = tempName.substring(0,tempName.length() - 1);
						
					}
					if(!StringUtils.isEmpty(listOfPrices)){
						priceMap.put(tempDimension+"#####"+dimensionUnits.toString()+"#####"+dimensionType.toString(), listOfPrices+"@@@@@"+listOfQuantity+"@@@@@"+priceCode+"@@@@@"
				 				 +priceIncludesValue+"@@@@@"+quoteUponRequest+"@@@@@"+tempName+"@@@@@"+pricesPerUnit.toString()
				 				+"#@#@#"+ImprintSizevalue+"_____"+ImprintSizevalue2);
					}
			listOfPrices=new StringBuilder();
			listOfQuantity=new StringBuilder();
			priceCode=new String();
			//priceIncludesValue=new String();
			quoteUponRequest=new String();
			productName=new String();
			pricesPerUnit=new StringBuilder();
			dimensionValue=new StringBuilder();
			dimensionUnits=new StringBuilder();
			dimensionType=new StringBuilder();
			ImprintSizevalue=new StringBuilder();
			ImprintSizevalue2=new StringBuilder();
			}else{
				/*if(!StringUtils.isEmpty(ImprintSizevalue2.toString())){
					//ImprintSizevalue=ImprintSizevalue.append("___").append(ImprintSizevalue2);
					ImprintSizevalue=ImprintSizevalue.append(ImprintSizevalue2);
				}
				imprintSize.add(ImprintSizevalue.toString());*/
				if(!StringUtils.isEmpty(ImprintSizevalue.toString())){
					imprintSizeSet.add(ImprintSizevalue.toString());
				}
				if(!StringUtils.isEmpty(ImprintSizevalue2.toString())){
					imprintSizeSet.add(ImprintSizevalue2.toString());
				}
				if(!StringUtils.isEmpty(listOfPrices)){
				priceMap.put("BASEPRICE", listOfPrices+"@@@@@"+listOfQuantity+"@@@@@"+priceCode+"@@@@@"
		 				 +priceIncludesValue+"@@@@@"+quoteUponRequest+"@@@@@"+productName+"@@@@@"+pricesPerUnit.toString());
				}
				listOfPrices=new StringBuilder();
				listOfQuantity=new StringBuilder();
				priceCode=new String();
				//priceIncludesValue=new String();
				quoteUponRequest=new String();
				productName=new String();
				pricesPerUnit=new StringBuilder();
				ImprintSizevalue=new StringBuilder();
				ImprintSizevalue2=new StringBuilder();
			}
			repeatRows.add(xid);
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+" "+e.getClass()+"column index "+columnIndex);// 		 
		}
		}
		workbook.close();
		
		// System.out.println("Java object converted to JSON String, written to file");
		 if(CollectionUtils.isEmpty(listOfImprintMethods)){
			 listOfImprintMethods = sportsUsaAttributeParser.addUNImprintMethod(listOfImprintMethods);
			 }	
		 if(!priceMap.isEmpty()){
			 if(priceMap.size()>1){	
					Iterator mapItr = priceMap.entrySet().iterator();
				    while (mapItr.hasNext()) {
				       Map.Entry values = (Map.Entry)mapItr.next();
				        /*priceMap.put(dimensionValue.toString(), listOfPrices+"@@@@@"+listOfQuantity+"@@@@@"+priceCode+"@@@@@"
				 				 +priceIncludesValue+"@@@@@"+quoteUponRequest+"@@@@@"+productName+"@@@@@"+pricesPerUnit.toString());*/
				       /*  priceMap.put(tempDimension+"#####"+dimensionUnits.toString()+"#####"+dimensionType.toString(), listOfPrices+"@@@@@"+listOfQuantity+"@@@@@"+priceCode+"@@@@@"
				 				 +priceIncludesValue+"@@@@@"+quoteUponRequest+"@@@@@"+tempName+"@@@@@"+pricesPerUnit.toString()
				 				+"#@#@#"+ImprintSizevalue);*/
				       String pricingValue= (String) values.getValue();
				       String priceArry[]=pricingValue.split("#@#@#");//#@#@#
				       
				       String sizeImpArry[]=priceArry[0].split("@@@@@");
				     
				       String sizeValue=(String) values.getKey();
				       String lPrices=sizeImpArry[0];
				       String lQuantity=sizeImpArry[1];
				       String pCode=sizeImpArry[2];
				       String pIncludesVa=sizeImpArry[3];
				       String qUponReq=sizeImpArry[4];
				       if(!qUponReq.equalsIgnoreCase("true")){
				       String pName=sizeImpArry[5];
				       String pPerUnit=sizeImpArry[6];
				       // i have to work on this multiple pricing for sizing
				       String gridName=  pName+","+priceArry[1] ;
				       if(gridName.contains("_____")){
				    	   gridName=gridName.replace("_____", "");
				       }
				       
				       priceGrids = sportsUsaPriceGridParser.getPriceGrids(lPrices, 
				    		   lQuantity, pCode, "USD",pIncludesVa, true,
				    		   qUponReq, gridName,"Size",pPerUnit,priceGrids,sizeValue+":"+priceArry[1]);
				       }else{
				    	   priceGrids = sportsUsaPriceGridParser.getPriceGridsQur();	
				       }
				      /* (String listOfPrices,
				   		    String listOfQuan, String discountCodes,
				   			String currency, String priceInclude, boolean isBasePrice,
				   			String qurFlag, String priceName, String criterias,String priceUnitArr,
				   			List<PriceGrid> existingPriceGrid)*/
				    }				
				}else{		
					Map.Entry<String, String> entry = priceMap.entrySet().iterator().next();
					 String pricingValue= entry.getValue();
				       String priceArry[]=pricingValue.split("#@#@#");
				       String sizeImpArry[]=priceArry[0].split("@@@@@");
				      String lPrices=sizeImpArry[0];
				       String lQuantity=sizeImpArry[1];
				       String pCode=sizeImpArry[2];
				       String pIncludesVa=sizeImpArry[3];
				       String qUponReq=sizeImpArry[4];
				       ///////////////////////////////
				       if(!qUponReq.equalsIgnoreCase("true")){
				       String pName=sizeImpArry[5];
				       String pPerUnit=sizeImpArry[6];
				       // i have to work on this multiple pricing for sizing
				       priceGrids = sportsUsaPriceGridParser.getPriceGrids(lPrices, 
				    		   lQuantity, pCode, "USD",pIncludesVa, true,
				    		   qUponReq, "","",pPerUnit,priceGrids,null);
						/*priceGrids = sportsUsaPriceGridParser.getPriceGrids(listOfPrices.toString(), 
					                 listOfQuantity.toString(), priceCode, "USD",priceIncludesValue, true,
					                 quoteUponRequest, productName,"",pricesPerUnit.toString(),priceGrids,null);*/
				       }else{
				    	   priceGrids = sportsUsaPriceGridParser.getPriceGridsQur();	
				       }
				       ///////////////////////////////
				}				
									
				}					
			
		 if(!CollectionUtils.isEmpty(imprintSizeSet)){
			 for (String impsizeValue : imprintSizeSet) {
				 imprintSizeList=sportsUsaAttributeParser.getimprintsize(impsizeValue,imprintSizeList);
			}
			 productConfigObj.setImprintSize(imprintSizeList);
		 }
			
		//impmtd upchrg
			if(!imprintMethodUpchargeMap.isEmpty()){// here i ahve to check for imprint methods
		    	priceGrids = sportsUsaAttributeParser.getImprintMethodUpcharges(imprintMethodUpchargeMap,
	                    listOfImprintMethods, priceGrids);
		    }
			
			// additional colors & upcgrg
			List<AdditionalColor> additionalColorList = null;
			if(!StringUtils.isEmpty(additionalClrRunChrgCode) && !StringUtils.isEmpty(additionalClrRunChrgPrice.toString())){
				 additionalColorList = sportsUsaAttributeParser.getAdditionalColor("Additional Color");
					priceGrids = sportsUsaAttributeParser.getAdditionalColorRunUpcharge(additionalClrRunChrgCode,listOfQuantity.toString(),
							   additionalClrRunChrgPrice.toString(), priceGrids,"Run Charge");
					if(!CollectionUtils.isEmpty(additionalColorList)){
					productConfigObj.setAdditionalColors(additionalColorList);
					}
			}
			if(!StringUtils.isEmpty(additionalColorPriceVal) && !additionalColorPriceVal.equals("0")){
				if(additionalColorList == null){
					additionalColorList = sportsUsaAttributeParser.getAdditionalColor("Additional Color");
					productConfigObj.setAdditionalColors(additionalColorList);
				}
				priceGrids = sportsUsaAttributeParser.getAdditionalColorUpcharge(additionalColorCode,
						   additionalColorPriceVal, priceGrids,"Add. Color Charge");
				//(String discountCode,String quantity,String prices,List<PriceGrid> existingPriceGrid,String upchargeType){
			}
			ShippingEstimate shipping = sportsUsaAttributeParser.getShippingEstimateValues(cartonL, cartonW,
					                               cartonH, weightPerCarton, unitsPerCarton);
			productConfigObj.setImprintLocation(listImprintLocation);
			productConfigObj.setImprintMethods(listOfImprintMethods);
			if(!StringUtils.isEmpty(themeValue) ){
			productConfigObj.setThemes(themeList);
			}
			productConfigObj.setRushTime(rushTime);
			productConfigObj.setShippingEstimates(shipping);
			productConfigObj.setProductionTime(listOfProductionTime);
			
			if(!CollectionUtils.isEmpty(sizeSet)){
			for (String sizeValue : sizeSet) {
				String sizeValueArr[]=sizeValue.split("#####");
				valuesList =sportsUsaAttributeParser.getValues(sizeValueArr[0],
						sizeValueArr[1], sizeValueArr[2],valuesList);
				
		       
			}
			 finalDimensionObj.setValues(valuesList);	
				size.setDimension(finalDimensionObj);
				productConfigObj.setSizes(size);
			
			}
			
			//productConfigObj.setColors(colorList);
			if(!StringUtils.isEmpty(FobPointsList)){
			productExcelObj.setFobPoints(FobPointsList);
			}
			 if(CollectionUtils.isEmpty(priceGrids)){
					priceGrids = sportsUsaPriceGridParser.getPriceGridsQur();	
				}
		 	productExcelObj.setPriceGrids(priceGrids);
		 	productExcelObj.setProductConfigurations(productConfigObj);
		 	
			int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId, environmentType);
		 	if(num ==1){
		 		numOfProductsSuccess.add("1");
		 	}else if(num == 0){
		 		numOfProductsFailure.add("0");
		 	}else{
		 		
		 	}
		 //	}
		 	_LOGGER.info("list size>>>>>>"+numOfProductsSuccess.size());
		    _LOGGER.info("Failure list size>>>>>>"+numOfProductsFailure.size());
	       finalResult = numOfProductsSuccess.size() + "," + numOfProductsFailure.size();
	       productDaoObj.saveErrorLog(asiNumber,batchId);
		    
	   	priceGrids = new ArrayList<PriceGrid>();
		listOfPrices = new StringBuilder();
	    listOfQuantity = new StringBuilder();
		productConfigObj = new ProductConfigurations();
		themeList = new ArrayList<Theme>();
		finalDimensionObj = new Dimension();
		 valuesList = new ArrayList<>();
		//productKeywords = new ArrayList<String>();
		listOfProductionTime = new ArrayList<ProductionTime>();
		rushTime = new RushTime();
		listImprintLocation = new ArrayList<ImprintLocation>();
		listOfImprintMethods = new ArrayList<ImprintMethod>();
		imprintSizeList =new ArrayList<ImprintSize>();
		ImprintSizevalue = new StringBuilder();
		size=new Size();
		//colorList = new ArrayList<Color>();
		FobPointsList = new ArrayList<FOBPoint>();
		 dimensionValue = new StringBuilder();
		 dimensionUnits = new StringBuilder();
		 dimensionType = new StringBuilder();
		 priceIncludes = new StringBuilder();
		 priceIncludesValue=null;
		 //additionalcolorList= new ArrayList<>();
		 //listofUpcharges = new StringBuilder();
		 imprintMethodUpchargeMap = new LinkedHashMap<>();
		 additionalClrRunChrgPrice = new StringBuilder();
	     additionalClrRunChrgCode = "";
	     additionalColorPriceVal = "";
         additionalColorCode     = "";
         pricesPerUnit=new StringBuilder();
         priceMap=new HashMap<String, String>();
         sizeSet=new HashSet<String>();
         priceIncludesValue=new String();
         imprintSizeSet = new HashSet<String>();
         complianceList = new ArrayList<String>();
         repeatRows.clear();
	       return finalResult;
		}catch(Exception e){
			_LOGGER.error("Error while Processing excel sheet "+e.getLocalizedMessage() +"for case +1="+columnIndex);
			return finalResult;
		}finally{
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet "+e.getLocalizedMessage() +"for case +1="+columnIndex);
				
			}
				_LOGGER.info("Complted processing of excel sheet ");
				_LOGGER.info("Total no of product:"+numOfProductsSuccess.size() );
		}
		
	}
	
	

	

	public SportsUsaAttributeParser getSportsUsaAttributeParser() {
		return sportsUsaAttributeParser;
	}





	public void setSportsUsaAttributeParser(
			SportsUsaAttributeParser sportsUsaAttributeParser) {
		this.sportsUsaAttributeParser = sportsUsaAttributeParser;
	}





	public SportsUsaPriceGridParser getSportsUsaPriceGridParser() {
		return sportsUsaPriceGridParser;
	}





	public void setSportsUsaPriceGridParser(
			SportsUsaPriceGridParser sportsUsaPriceGridParser) {
		this.sportsUsaPriceGridParser = sportsUsaPriceGridParser;
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

	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}

	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}

	public LookupRestService getLookupRestServiceObj() {
		return lookupRestServiceObj;
	}

	public void setLookupRestServiceObj(LookupRestService lookupRestServiceObj) {
		this.lookupRestServiceObj = lookupRestServiceObj;
	}
	
	public SportsAzxCanColorParser getSportsAzxCanColorParser() {
		return sportsAzxCanColorParser;
	}





	public void setSportsAzxCanColorParser(
			SportsAzxCanColorParser sportsAzxCanColorParser) {
		this.sportsAzxCanColorParser = sportsAzxCanColorParser;
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
	private String getProductXid(Row row,boolean isRepeatProduct){
		Cell xidCell = null;
		String productXid = "";
		if(isRepeatProduct){
			xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_TWO);
			productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		} else {
			xidCell = row.getCell(ApplicationConstants.CONST_NUMBER_ZERO);
			productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
			if (StringUtils.isEmpty(productXid)) {
				xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_TWO);
				productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
			}
		}
		
		return productXid.trim();
	}
public boolean isRepeateColumn(int columnIndex){
		
		if(columnIndex != 16 &&columnIndex != 17 &&columnIndex != 18 && columnIndex != 19 && columnIndex != 20 && columnIndex != 21 &&  columnIndex != 22 &&  columnIndex != 23 &&  columnIndex != 24 &&   columnIndex != 25 &&  columnIndex != 26 &&  columnIndex != 27 &&
		   columnIndex != 28 && columnIndex != 29 &&columnIndex != 30 && columnIndex != 31 && columnIndex != 32 && columnIndex != 33 &&  columnIndex != 34 &&  columnIndex != 35 &&  columnIndex != 36 &&   columnIndex != 37 &&  columnIndex != 38 &&  columnIndex != 39 && columnIndex != 40 &&
		   columnIndex != 41 && columnIndex != 42 &&columnIndex != 43 && columnIndex != 44 && columnIndex != 45 && columnIndex != 46 &&
		   columnIndex != 78 && columnIndex != 79 &&columnIndex != 80 && columnIndex != 81 && columnIndex != 82 && columnIndex != 83 &&
		   columnIndex != 85 && columnIndex != 86 &&columnIndex != 87 && columnIndex != 88 && columnIndex != 89 && columnIndex != 90){//&&  columnIndex != 47
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}

}
