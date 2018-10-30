package com.a4tech.supplier.mapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.Image;
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
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.DouglasBridge.DouglasBridgeAttributeParser;
import parser.DouglasBridge.DouglasBridgePriceGridParser;
import parser.goldstarcanada.GoldstarCanadaLookupData;


public class DouglasBridgeMapper implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(DouglasBridgeMapper.class);
	
	private PostServiceImpl 				postServiceImpl;
	private ProductDao 						productDaoObj;
	private DouglasBridgeAttributeParser   douglasBridgeAttributeParser;
	private DouglasBridgePriceGridParser   douglasBridgePriceGridParser;

	@Override
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  String productId = null;
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  
		  StringBuilder dimensionValue = new StringBuilder();
		  StringBuilder dimensionUnits = new StringBuilder();
		  StringBuilder dimensionType = new StringBuilder();
		  StringBuilder listOfQuantity = new StringBuilder();
		  StringBuilder listOfPrices = new StringBuilder();
		  StringBuilder priceIncludes = new StringBuilder();
		  StringBuilder pricesPerUnit = new StringBuilder();
		  StringBuilder ImprintSizevalue = new StringBuilder();
	
			//List<Color> colorList = new ArrayList<Color>();
			List<ImprintLocation> listImprintLocation = new ArrayList<ImprintLocation>();
			List<ImprintMethod> listOfImprintMethods = new ArrayList<ImprintMethod>();
			List<Theme> themeList = new ArrayList<Theme>();
			List<String> complianceList = new ArrayList<String>();		
		    List<ImprintSize> imprintSizeList =new ArrayList<ImprintSize>();
			StringBuilder additionalClrRunChrgPrice = new StringBuilder();
			String        additionalClrRunChrgCode = "";
			String        additionalColorPriceVal = "";
			String        additionalColorCode     = "";
			List<String>  ProductProcessedList   = new ArrayList<>();
			String asiProdNo = "";
			List<String> repeatRows = new ArrayList<>();
		try{ 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		String priceCode = null;
		String productName = null;
		String quoteUponRequest  = null;
		//String quantity = null;
		StringBuilder shippingDimensions = new StringBuilder();
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
		String prodTimeLo = null;
		String priceIncludesValue=null;
		String imprintLocation = null;
		Product existingApiProduct = null;
		String rushProdTimeLo = "";
		StringBuilder additionalPrdInfo = new StringBuilder();
	   Map<String, String>  imprintMethodUpchargeMap = new LinkedHashMap<>();
	   String xid = null;
	   StringBuilder themesValues = new StringBuilder();
	   StringBuilder keywords = new StringBuilder();
		while (iterator.hasNext()) {
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum() < 7)
				continue;
			// this value is check first column or not becuase first column is skipped if value is not present
			boolean isFirstColum = true;
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
					xid = getProductXid(nextRow,false);
					if(ProductProcessedList.contains(xid)){
						xid = getProductXid(nextRow,true);
					}
					checkXid = true;
					isFirstColum = false;
				}else{
					checkXid = false;
					if(isFirstColum){
						xid = getProductXid(nextRow,false);
						if(ProductProcessedList.contains(xid)){
							xid = getProductXid(nextRow,true);
						}
						checkXid = true;
						isFirstColum = false;
					}
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 7){
							 System.out.println("Java object converted to JSON String, written to file");
							 
							 ///////////////////
							 ProductProcessedList.add(productExcelObj.getExternalProductId());
								productExcelObj.setPriceType("L");
								if(CollectionUtils.isEmpty(listOfImprintMethods)){
									   listOfImprintMethods = douglasBridgeAttributeParser.getImprintMethodValues(listOfImprintMethods, "Unimprinted");
								   }
								if(!StringUtils.isEmpty(dimensionValue.toString())){
									Size productSize = douglasBridgeAttributeParser.getProductSize(dimensionValue.toString(),
							                dimensionUnits.toString(), dimensionType.toString());
									productConfigObj.setSizes(productSize);
								}
								if(!CollectionUtils.isEmpty(themeList)){
									productConfigObj.setThemes(themeList);
								}
								imprintSizeList=douglasBridgeAttributeParser.getimprintsize(ImprintSizevalue);
								if(FirstImprintsize1 != "" || FirstImprintsize2 !="" ||
										SecondImprintsize1 != "" ||	SecondImprintsize2 !=""){
								productConfigObj.setImprintSize(imprintSizeList);}
										ShippingEstimate shippingEstimation = douglasBridgeAttributeParser
												.getShippingEstimateValues(shippingDimensions.toString(), weightPerCarton, unitsPerCarton);
										productConfigObj.setShippingEstimates(shippingEstimation);
								if(listOfPrices != null && !listOfPrices.toString().isEmpty()){
									priceGrids = douglasBridgePriceGridParser.getBasePriceGrids(listOfPrices.toString(), 
											         listOfQuantity.toString(), priceCode, "USD",
											         priceIncludesValue, true, quoteUponRequest, productName,"",priceGrids);
								} else {
									priceGrids = douglasBridgePriceGridParser.getBasePriceGrids(listOfPrices.toString(), 
									         listOfQuantity.toString(), priceCode, "USD",
									         priceIncludesValue, true, "true", productName,"",priceGrids);
								}
								    if(!imprintMethodUpchargeMap.isEmpty()){
								    	priceGrids = douglasBridgeAttributeParser.getImprintMethodUpcharges(imprintMethodUpchargeMap,
					                            listOfImprintMethods, priceGrids);
								    }
										
										List<AdditionalColor> additionalColorList = null;
									if(!StringUtils.isEmpty(additionalClrRunChrgCode)){
										 additionalColorList = douglasBridgeAttributeParser.getAdditionalColor("Additional Color");
											priceGrids = douglasBridgeAttributeParser.getAdditionalColorUpcharge(additionalClrRunChrgCode,
													additionalClrRunChrgPrice.toString(), priceGrids,"Run Charge",listOfQuantity.toString());
											productConfigObj.setAdditionalColors(additionalColorList);
									}
									if(!StringUtils.isEmpty(additionalColorPriceVal) && !additionalColorPriceVal.equals("0")){
										if(additionalColorList == null){
											additionalColorList = douglasBridgeAttributeParser.getAdditionalColor("Additional Color");
											productConfigObj.setAdditionalColors(additionalColorList);
										}
										priceGrids = douglasBridgeAttributeParser.getAdditionalColorUpcharge(additionalColorCode,
												   additionalColorPriceVal, priceGrids,"Add. Color Charge","1");
									}
							   
							   themeList = douglasBridgeAttributeParser.getProductTheme(themesValues.toString());
								 if(CollectionUtils.isEmpty(themeList)){// from supplier sheet 
									 if(!CollectionUtils.isEmpty(productConfigObj.getThemes())){// check existing themes
										 themeList =  productConfigObj.getThemes();
									 }
								 }
								 List<String> keywordList = douglasBridgeAttributeParser.getProductKeywords(keywords.toString());
								 productExcelObj.setProductKeywords(keywordList);
							   productConfigObj.setThemes(themeList);
							   productExcelObj.setAdditionalProductInfo(additionalPrdInfo.toString());
							   productConfigObj.setImprintLocation(listImprintLocation);
							   productConfigObj.setImprintMethods(listOfImprintMethods);
								productExcelObj.setProductConfigurations(productConfigObj);
								productExcelObj.setPriceGrids(priceGrids);
								String name = productExcelObj.getName();
								if(name.contains("Simply Smashing") && name.contains("Compressed T")){
									productExcelObj.setName("Simply Smashing Compressed T-Shirt");
								}
								if(productExcelObj.getImages() != null){
									List<Image> imageList = removeImageConfiguration(productExcelObj.getImages());
									productExcelObj.setImages(imageList);	
								}
								String desc = productExcelObj.getDescription();
								if(StringUtils.isEmpty(desc)){
									productExcelObj.setDescription(productExcelObj.getName());
									productExcelObj.setSummary(productExcelObj.getName());
								 } else {
									 if(desc.contains(".")){
											productExcelObj.setSummary(desc.substring(0, desc.indexOf(".")+1));	
										} else {
											productExcelObj.setSummary(desc);
										}
										 if(productExcelObj.getSummary().length() > 130){
											 productExcelObj.setSummary(productExcelObj.getName());
										 }	 
								 }
							 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId, environmentType);
							 	//ProductProcessedList.add(productExcelObj.getExternalProductId());
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
								listImprintLocation = new ArrayList<ImprintLocation>();
								listOfImprintMethods = new ArrayList<ImprintMethod>();
								imprintSizeList =new ArrayList<ImprintSize>();
								ImprintSizevalue = new StringBuilder();
								 dimensionValue = new StringBuilder();
								 dimensionUnits = new StringBuilder();
								 dimensionType = new StringBuilder();
								 priceIncludes = new StringBuilder();
								 priceIncludesValue=null;
								 rushProdTimeLo = "";
								 shippingDimensions = new StringBuilder();
								 imprintMethodUpchargeMap = new LinkedHashMap<>();
								 additionalClrRunChrgPrice = new StringBuilder();
							     additionalClrRunChrgCode = "";
							     additionalColorPriceVal = "";
						         additionalColorCode     = "";
						         asiProdNo = "";
						         additionalPrdInfo = new StringBuilder();
						         repeatRows.clear();
						         themesValues = new StringBuilder();
						         keywords = new StringBuilder();
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid.trim());
						    	repeatRows.add(xid.trim());
						    }
						    existingApiProduct = postServiceImpl.getProduct(accessToken, xid, environmentType);
						     if(existingApiProduct == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						    	 productExcelObj.setExternalProductId(xid);
						     }else{
						  	//   productExcelObj=existingApiProduct;
								//productConfigObj=existingApiProduct.getProductConfigurations();
						    	 productExcelObj = douglasBridgeAttributeParser.keepExistingProductData(existingApiProduct);
						    	 productConfigObj=productExcelObj.getProductConfigurations();
						    	 productExcelObj.setExternalProductId(xid);
						        /*String confthruDate=existingApiProduct.getPriceConfirmedThru();
						        productExcelObj.setPriceConfirmedThru(confthruDate);*/
						     }
							//productExcelObj = new Product();
					 }
				} else {
					if(productXids.contains(xid) && repeatRows.size() != 1){
						 if(isRepeateColumn(columnIndex+1)){
							 continue;
						 }
					}
				}
				

				switch (columnIndex + 1) {
			
				case 1://xid
					   productExcelObj.setExternalProductId(xid);
					
					 break;
				case 2: //productId
					//ignore
					break;
				case 3://AsiProdNo
					     asiProdNo=CommonUtility.getCellValueStrinOrInt(cell);
					     if(asiProdNo.length() > 14){
					    	 asiProdNo = asiProdNo.replaceAll("-", "");
					    	 asiProdNo = asiProdNo.replaceAll("\\s+","");
					    	 asiProdNo = CommonUtility.getStringLimitedChars(asiProdNo, 14);
					     }
					     productExcelObj.setAsiProdNo(asiProdNo);		
					  break;
				case 4://Name
					 productName = cell.getStringCellValue();
					 productName = getFinalProductName(productName, asiProdNo);
					 productExcelObj.setName(productName);
						break;
				case 5://CatYear(Not used)
					// CatYear=CommonUtility.getCellValueStrinOrInt(cell);
				    break;
					
				case 6://PriceConfirmedThru
					// priceConfirmedThru = cell.getDateCellValue();
					String ConfDate=cell.getStringCellValue();
				     productExcelObj.setPriceConfirmedThru(ConfDate);
					break;
					
				case 7: //  product status ,discontinued
					
					// ProductStatus=cell.getStringCellValue();
					// Prod_Status=cell.getBooleanCellValue();
					 break;
					
				case 8://catalog1
					
					break;
					
				case 9: // catalog2

					break;
					
				case 10: //Catalogs page number, Page1
					// no need to process
					break;
				case 11:  
					//Catalogs(Not used),Page2
					break;
					
				case 12:  //Description
					String description =CommonUtility.getCellValueStrinOrInt(cell);
					description = getFinalDescription(description, asiProdNo);
					productExcelObj.setDescription(description);
					
					break;
				
				case 13:  // keywords
					String productKeyword = cell.getStringCellValue();
					if(!StringUtils.isEmpty(productKeyword)){
						productKeyword = productKeyword.trim();
						keywords.append(productKeyword).append(",");
						/*		List<String> productKeyWords = douglasBridgeAttributeParser
										.getProductKeywords(productKeyword);
					 productExcelObj.setProductKeywords(productKeyWords);*/
					}
					break;

				case 14: //Colors
				String colorValue=cell.getStringCellValue();
				  if(!StringUtils.isEmpty(colorValue)){
					  List<Color> colorsList = douglasBridgeAttributeParser.getProductColor(colorValue);
						productConfigObj.setColors(colorsList);  
				  }
					break;
					
				case 15: // Themes
					 String themeValue=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(themeValue)){
						 themesValues.append(themeValue).append(",");
					 }					 
					break;
					
				case 16://size --  value
						String dimensionValue1=CommonUtility.getCellValueStrinOrInt(cell);
					   if(dimensionValue1 != null && !dimensionValue1.isEmpty()){
						   dimensionValue.append(dimensionValue1).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					   }
					
					break;
				case 17: //size -- Unit
					  String dimensionUnits1 = CommonUtility.getCellValueStrinOrInt(cell);
					 if(!dimensionUnits1.equals("0")){
						 dimensionUnits.append(dimensionUnits1.trim()).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					 }
					  break;
				
				case 18: //size -- type
					String dimensionType1 =CommonUtility.getCellValueStrinOrInt(cell);
					if(!dimensionType1.equals("0")){
						dimensionType.append(dimensionType1).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					break;
				
				 case 19: //size
					 String dimensionValue2 =CommonUtility.getCellValueStrinOrInt(cell);
					 if(dimensionValue2 != null && !dimensionValue2.isEmpty()){
						 dimensionValue.append(dimensionValue2).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					 }
					break;
				case 20:  //size
					String dimensionUnits2 =CommonUtility.getCellValueStrinOrInt(cell);
					
					if(!dimensionUnits2.equals("0")){
						dimensionUnits.append(dimensionUnits2.trim()).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					break;
					
				case 21: //size
					String  dimensionType2 = CommonUtility.getCellValueStrinOrInt(cell);

					if(!dimensionType2.equals("0")){
						dimensionType.append(dimensionType2).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					break;
					
				case 22: //size
					String dimensionValue3  =CommonUtility.getCellValueStrinOrInt(cell);
					if(dimensionValue3 != null && !dimensionValue3.isEmpty()){
						dimensionValue.append(dimensionValue3).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					break;
				case 23: //size
					String dimensionUnits3 = CommonUtility.getCellValueStrinOrInt(cell);
					if(!dimensionUnits3.equals("0")){
						 dimensionUnits.append(dimensionUnits3.trim()).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
				   break;
					
				case 24: //size
					String dimensionType3 = CommonUtility.getCellValueStrinOrInt(cell);
					if(!dimensionType3.equals("0")){
						dimensionType.append(dimensionType3).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
				   break;
				   
				case 25:  // Quantities
				case 26: 
				case 27: 
				case 28: 
				case 29:
				case 30:
					String quantity = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(quantity) && !quantity.equals("0")){
			        	 listOfQuantity.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			         }
					   	break;
				case 31:  // prices --list price
				case 32:
				case 33:
				case 34:
				case 35:
				case 36:
					String prices = CommonUtility.getCellValueDouble(cell);
					 if(!StringUtils.isEmpty(prices)&& !prices.equals("0")){
			        	 listOfPrices.append(prices).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			         }
					    break; 
				case 37: // price code -- discount   
				priceCode = cell.getStringCellValue();	
					     break;
				case 38:       // pricesPerUnit
				case 39:
				case 40:
				case 41:
				case 42:
				case 43:
					String pricesPerUnits = CommonUtility.getCellValueDouble(cell);
					 if(!StringUtils.isEmpty(pricesPerUnits)){
			        	 pricesPerUnit.append("1").append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			         }
					//set default value is "1" in PricesPerUnit
					      break;
				case 44:
					     quoteUponRequest = cell.getStringCellValue();
					      break;
				case 45:  // priceIncludeClr    
					      priceIncludes.append(cell.getStringCellValue()).append(" ");
					     break;
				case 46: // priceIncludeSide
						
						priceIncludes.append(cell.getStringCellValue()).append(" ");
						
						break;
				case 47: // priceIncludeLoc
						priceIncludes.append(cell.getStringCellValue());
						priceIncludesValue = CommonUtility.getStringLimitedChars(priceIncludes.toString(), 100);
						break;
						
				case 48:   //Set-up Charge 
					String setupChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!setupChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("setupCharge", setupChargePrice);
					}
				  break;
				case 49://setup charge code					
					String setUpchargeCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(setUpchargeCode) && !setUpchargeCode.equals("0")){
						String priceVal = imprintMethodUpchargeMap.get("setupCharge");
						priceVal = priceVal+"_"+setUpchargeCode;
						imprintMethodUpchargeMap.put("setupCharge", priceVal);
					}
				  break;
				case 50://screen charge
					String screenChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!screenChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("screenCharge", screenChargePrice);
					}
							break;
				case 51://screen charge code
					String screenchargeCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(screenchargeCode) && !screenchargeCode.equals("0")){
						String priceVal = imprintMethodUpchargeMap.get("screenCharge");
						priceVal = priceVal+"_"+screenchargeCode;
						imprintMethodUpchargeMap.put("screenCharge", priceVal);
					}
							break;
				case 52:// plate charge
					String plateChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!plateChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("plateCharge", plateChargePrice);
					}

							break;
				case 53://plateCharge code
					String plateChargeCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(plateChargeCode) && !plateChargeCode.equals("0")){
						String priceVal = imprintMethodUpchargeMap.get("plateCharge");
						priceVal = priceVal+"_"+plateChargeCode;
						imprintMethodUpchargeMap.put("plateCharge", priceVal);
					}
							break;
				case 54:// dieCharge
					String diaChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!diaChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("diaCharge", diaChargePrice);
					}
							break;
				case 55:// die Charge code
					String diaChargeCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(diaChargeCode) && !diaChargeCode.equals("0")){
						String priceVal = imprintMethodUpchargeMap.get("diaCharge");
						priceVal = priceVal+"_"+diaChargeCode;
						imprintMethodUpchargeMap.put("diaCharge", priceVal);
					}
							break;
				case 56://tooling charge
					String toolingChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!toolingChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("toolingCharge", toolingChargePrice);
					}
							break;
				case 57:// tooling charge code
					String toolingChargeCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(toolingChargeCode) &&!toolingChargeCode.equals("0")){
						String priceVal = imprintMethodUpchargeMap.get("toolingCharge");
						priceVal = priceVal+"_"+toolingChargeCode;
						imprintMethodUpchargeMap.put("toolingCharge", priceVal);
					}
							break;
				case 58://repeate charge
					String repeateChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(repeateChargePrice) && !repeateChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("repeateCharge", repeateChargePrice);
					}
					// no need to process this column since there is no vaild data
							break; 
				case 59://repeate charge code
							String repeateChargeCode = CommonUtility.getCellValueStrinOrInt(cell);
							if(!StringUtils.isEmpty(repeateChargeCode) && !repeateChargeCode.equals("0")){
								String priceVal = imprintMethodUpchargeMap.get("repeateCharge");
								priceVal = priceVal+"_"+repeateChargeCode;
								imprintMethodUpchargeMap.put("repeateCharge", priceVal);
							}
					// no need to process this column since there is no vaild data
						break;
				case 60: // additioNal color
					    additionalColorPriceVal = CommonUtility.getCellValueStrinOrInt(cell);
						 break;
				case 61: // additional color code
					additionalColorCode = cell.getStringCellValue();
						break;
				case 62: 	
				case 63:							
				case 64:	
				case 65:	
				case 66:	
				case 67:
				  String colorCharge = CommonUtility.getCellValueDouble(cell);
				  if(!colorCharge.equalsIgnoreCase("0.00") && !colorCharge.equalsIgnoreCase("0.0") &&
						  !colorCharge.equalsIgnoreCase("0"))
				  additionalClrRunChrgPrice.append(colorCharge).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					break;
				case 68:// additional run charge code
				  String additionalClrRunCode = cell.getStringCellValue();
				      if(!StringUtils.isEmpty(additionalClrRunCode)){
					       additionalClrRunChrgCode = additionalClrRunCode;
				      }    	
				break; 
				case 69://is recycle
					       break;
				case 70:
							String IsEnvironmentallyFriendly = cell.getStringCellValue();
							if (IsEnvironmentallyFriendly.equalsIgnoreCase("true")) {
								Theme themeObj = new Theme();
								themeObj.setName("Eco & Environmentally Friendly");
								themeList.add(themeObj);
							}
					break;
				case 71:// IsNewProd
					break;
				case 72://NotSuitable
					break;
				case 73:// Exclusive
					break;
				case 74://Hazardous
					break;
				case 75://OfficiallyLicensed
					break;
				case 76://IsFood
					break;
				case 77://IsClothing
					break;
				case 78: // Imprint size1
					 FirstImprintsize1=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(FirstImprintsize1) && !FirstImprintsize1.equals("0")){
						 ImprintSizevalue.append(FirstImprintsize1).append(" ");
					
					 }
					    break;
					    
				case 79: //// Imprint size1 unit
					FirstImprintunit1=CommonUtility.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(FirstImprintsize1) && !StringUtils.isEmpty(FirstImprintunit1)
									&& !FirstImprintunit1.equals("0")) {
					FirstImprintunit1=GoldstarCanadaLookupData.Dimension1Units.get(FirstImprintunit1);
					     ImprintSizevalue.append(FirstImprintunit1).append(" ");
					 }	 
					   	break;
					   	
				case 80:   // Imprint size1 Type
					FirstImprinttype1=CommonUtility.getCellValueStrinOrInt(cell);
					
							if (!StringUtils.isEmpty(FirstImprintsize1) && !StringUtils.isEmpty(FirstImprinttype1)
									&& !FirstImprinttype1.equals("0")) {
					FirstImprinttype1=GoldstarCanadaLookupData.Dimension1Type.get(FirstImprinttype1);
					  ImprintSizevalue.append(FirstImprinttype1).append(" ");
				   }
					break;
				case 81: // // Imprint size2
					FirstImprintsize2=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(FirstImprintsize2)&& !FirstImprintsize2.equals("0")){
						 ImprintSizevalue.append("x").append(FirstImprintsize2).append(" ");
					 }

					  	break;
					  	
				case 82:	// Imprint size2 Unit
					FirstImprintunit2=CommonUtility.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(FirstImprintsize2) && !StringUtils.isEmpty(FirstImprintunit2)
									&& !FirstImprintunit2.equals("0")) {
					FirstImprintunit2=GoldstarCanadaLookupData.Dimension1Units.get(FirstImprintunit2);
					    ImprintSizevalue.append(FirstImprintunit2).append(" ");
				    }
					    break;
					    
				case 83: // Imprint size2 Type
					FirstImprinttype2=CommonUtility.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(FirstImprintsize2) && !StringUtils.isEmpty(FirstImprinttype2)
									&& !FirstImprinttype2.equals("0")) {
					FirstImprinttype2=GoldstarCanadaLookupData.Dimension1Type.get(FirstImprinttype2);
					    ImprintSizevalue.append(FirstImprinttype2).append(" ");
				    }
					break;
					  	
				case 84:  // Imprint location
					
					 imprintLocation = cell.getStringCellValue();
					if(!imprintLocation.isEmpty()){
						ImprintLocation locationObj = new ImprintLocation();
						locationObj.setValue(imprintLocation);
						listImprintLocation.add(locationObj);
					}
					 break;
				case 85:  // Second Imprintsize1
					SecondImprintsize1=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprintsize1)&& !SecondImprintsize1.equals("0")){
					  ImprintSizevalue.append(SecondImprintsize1).append(" ");
				    }
					   	break;
					   	
				case 86:  // Second Imprintsize1 unit
					SecondImprintunit1=CommonUtility.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(SecondImprintsize1) && !StringUtils.isEmpty(SecondImprintunit1)
									&& !SecondImprintunit1.equals("0")) {
					SecondImprintunit1=GoldstarCanadaLookupData.Dimension1Units.get(SecondImprintunit1);
					   ImprintSizevalue.append(SecondImprintunit1).append(" ");
					}
						break;
				case 87:  // Second Imprintsize1 type
					SecondImprinttype1=CommonUtility.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(SecondImprintsize1) && !StringUtils.isEmpty(SecondImprinttype1)
									&& !SecondImprinttype1.equals("0")) {
					SecondImprinttype1=GoldstarCanadaLookupData.Dimension1Type.get(SecondImprinttype1);
					   ImprintSizevalue.append(SecondImprinttype1).append(" ");
					}
					  break;
					  
				case 88: // Second Imprintsize2
					SecondImprintsize2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprintsize2)&& !SecondImprintsize2.equals("0")){
				       ImprintSizevalue.append("x").append(SecondImprintsize2).append(" ");
				    }
					break;
					
				case 89: //Second Imprintsize2 Unit
					SecondImprintunit2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprintsize2) && !StringUtils.isEmpty(SecondImprintunit2)&& !SecondImprintunit2.equals("0")){
					SecondImprintunit2=GoldstarCanadaLookupData.Dimension1Units.get(SecondImprintunit2);
					    ImprintSizevalue.append(SecondImprintunit2).append(" ");
					}

					break;
					
				case 90: // Second Imprintsize2 type	
					SecondImprinttype2=CommonUtility.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(SecondImprintsize2) && !StringUtils.isEmpty(SecondImprinttype2)
									&& !SecondImprinttype2.equals("0")) {
					SecondImprinttype2=GoldstarCanadaLookupData.Dimension1Type.get(SecondImprinttype2);
					   ImprintSizevalue.append(SecondImprinttype2).append(" ");
					}					
					  break;
					  
				case 91: // Second Imprint location
					String imprintLocation2 = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintLocation2)){
						ImprintLocation locationObj2 = new ImprintLocation();
						locationObj2.setValue(imprintLocation2);
						listImprintLocation.add(locationObj2);
					}
					break;
				case 92: // DecorationMethod 
					decorationMethod = cell.getStringCellValue();
					if(!StringUtils.isEmpty(decorationMethod)){
						listOfImprintMethods = douglasBridgeAttributeParser
								.getImprintMethodValues(decorationMethod);
					}
					 break; 
					 
				case 93: //NoDecoration
					String noDecoration = cell.getStringCellValue();
				    if(!StringUtils.isEmpty(noDecoration)){
				    	if(noDecoration.equalsIgnoreCase("True")){
									listOfImprintMethods = douglasBridgeAttributeParser
											.getImprintMethodValues(listOfImprintMethods, "Unimprinted");
				    	}
				    }
					 break;
				case 94: //NoDecorationOffered
					// no need to process this column since above column data also same
					 break;
				case 95: //NewPictureURL
					break;
				case 96:  //NewPictureFile  -- not used
					break;
				case 97: //ErasePicture -- not used
					break;
				case 98: //NewBlankPictureURL
					break;
				case 99: //NewBlankPictureFile -- not used
					break;
				case 100://EraseBlankPicture  -- not used
					break;
				case 101: //NotPictured  -- not used
					break;
				case 102: //MadeInCountry
					
					String madeInCountry = cell.getStringCellValue();
					if(!StringUtils.isEmpty(madeInCountry)){
								List<Origin> listOfOrigin = douglasBridgeAttributeParser
										.getOriginValues(madeInCountry);
						productConfigObj.setOrigins(listOfOrigin);
					}
					break;
				case 103:// AssembledInCountry
			     String   additionalProductInfo = cell.getStringCellValue();
			     if(!StringUtils.isEmpty(additionalProductInfo)) {
								additionalPrdInfo.append("AssembledInCountry:").append(
										douglasBridgeAttributeParser.getCountryCodeConvertName(additionalProductInfo)); 
			       }
					break;
				case 104: //DecoratedInCountry
					String additionalImprintInfo = cell.getStringCellValue();
					 if(!StringUtils.isEmpty(additionalImprintInfo)) {
						 StringBuilder addImprintInfo = new StringBuilder();
								addImprintInfo.append("DecoratedInCountry:").append(
										douglasBridgeAttributeParser.getCountryCodeConvertName(additionalImprintInfo));
						 productExcelObj.setAdditionalImprintInfo(addImprintInfo.toString());
					   }
					
					break;
				case 105: //ComplianceList  -- No data
					String complnceValuet=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(complnceValuet)) {
				    	complianceList.add(complnceValuet);
				    	productExcelObj.setComplianceCerts(complianceList);
					   }
					break;
					
				case 106://ComplianceMemo  -- No data
					String productDataSheet=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(productDataSheet) && !productDataSheet.equals("0")){
						 productExcelObj.setProductDataSheet(productDataSheet);
					   }
					break;
				case 107: //ProdTimeLo
				   prodTimeLo = CommonUtility.getCellValueStrinOrInt(cell);
					break;
				case 108: //ProdTimeHi
					String prodTimeHi = CommonUtility.getCellValueStrinOrInt(cell);
					if(!prodTimeHi.equals("0")){
								List<ProductionTime> productionTimeList = douglasBridgeAttributeParser
										.getProductionTime(prodTimeLo, prodTimeHi);
						productConfigObj.setProductionTime(productionTimeList);
					}
					break;
				case 109://RushProdTimeLo
					 rushProdTimeLo  = cell.getStringCellValue();
					 break; 	 
				case 110://RushProdTimeH
					String rushProdTimeH  = cell.getStringCellValue();
					if(!rushProdTimeH.equals(ApplicationConstants.CONST_STRING_ZERO)){
								RushTime productRushTime = douglasBridgeAttributeParser
										.getProductRushTime(rushProdTimeLo, rushProdTimeH);
					  productConfigObj.setRushTime(productRushTime);
					}
					break;
				case 111://Packaging
					String pack  = cell.getStringCellValue();
					if(!StringUtils.isEmpty(pack)){
								List<Packaging> listOfPackaging = douglasBridgeAttributeParser.getPackageValues(pack);
						productConfigObj.setPackaging(listOfPackaging);
					}
					break;
				case 112: //CartonL
					 String cartonL  = CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(cartonL) && !cartonL.equals("0")){
						 shippingDimensions.append(cartonL).append(",");
					 }
					break;
				case 113://CartonW
					String cartonW  = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(cartonW) &&!cartonW.equals("0")){
						 shippingDimensions.append(cartonW).append(",");
					 }
					break;
				case 114://CartonH
					String cartonH  = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(cartonH) && !cartonH.equals("0")){
						 shippingDimensions.append(cartonH);
					 }
					break;
				case 115: //WeightPerCarton
					weightPerCarton  =CommonUtility.getCellValueStrinOrInt(cell);
					break;
				case 116: //UnitsPerCarton
					unitsPerCarton  = CommonUtility.getCellValueStrinOrInt(cell);
					break;
				case 117: //ShipPointCountry
					break;
				case 118: //ShipPointZip
					String fobVal = cell.getStringCellValue();
					List<FOBPoint> listOfFobPoint = douglasBridgeAttributeParser.getFobPoint(fobVal, accessToken, environmentType);
					if(!CollectionUtils.isEmpty(listOfFobPoint)){
						productExcelObj.setFobPoints(listOfFobPoint);
					}
					break;
				case 119: //Comment
					String comment = cell.getStringCellValue();
					if(!StringUtils.isEmpty(comment)){
						if(productConfigObj.getRushTime() == null){
									RushTime rushTime = douglasBridgeAttributeParser
											.getProductRushTime(productConfigObj.getRushTime());
									productConfigObj.setRushTime(rushTime);
						}
						if(comment.contains("Rush quotes available,")){
							comment = comment.replaceAll("Rush quotes available,", "");
						}else if(comment.contains("Rush service available,")){
							comment = comment.replaceAll("Rush service available,", "");
						}else if(comment.contains("Rush service available.")){
							comment = comment.replaceAll("Rush service available.", "");
						}
						if(StringUtils.isEmpty(additionalPrdInfo.toString())){
							additionalPrdInfo.append(comment.trim());
						} else {
							additionalPrdInfo.append(", ").append(comment.trim());
						}
					}
					break;
				case 120: //Verified
					//since data is already expired date
					break;
				case 121: //UpdateInventory
					break;
				case 122: //InventoryOnHand
					break;
				case 123: //InventoryOnHandAdd
					break;
					
				case 124: //InventoryMemo
				break;
			}  // end switch		 
		}//end inner while loop
			// set  product configuration objects
			// end inner while loop
			//it is store xid if once mapping completed 
			
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() );		 
		}
		}
		workbook.close();
		/////////////////////
		ProductProcessedList.add(productExcelObj.getExternalProductId());
		productExcelObj.setPriceType("L");
		if(CollectionUtils.isEmpty(listOfImprintMethods)){
			   listOfImprintMethods = douglasBridgeAttributeParser.getImprintMethodValues(listOfImprintMethods, "Unimprinted");
		   }
		if(!StringUtils.isEmpty(dimensionValue.toString())){
			Size productSize = douglasBridgeAttributeParser.getProductSize(dimensionValue.toString(),
	                dimensionUnits.toString(), dimensionType.toString());
			productConfigObj.setSizes(productSize);
		}
		if(!CollectionUtils.isEmpty(themeList)){
			productConfigObj.setThemes(themeList);
		}
		imprintSizeList=douglasBridgeAttributeParser.getimprintsize(ImprintSizevalue);
		if(FirstImprintsize1 != "" || FirstImprintsize2 !="" ||
				SecondImprintsize1 != "" ||	SecondImprintsize2 !=""){
		productConfigObj.setImprintSize(imprintSizeList);}
				ShippingEstimate shippingEstimation = douglasBridgeAttributeParser
						.getShippingEstimateValues(shippingDimensions.toString(), weightPerCarton, unitsPerCarton);
				productConfigObj.setShippingEstimates(shippingEstimation);
		if(listOfPrices != null && !listOfPrices.toString().isEmpty()){
			priceGrids = douglasBridgePriceGridParser.getBasePriceGrids(listOfPrices.toString(), 
					         listOfQuantity.toString(), priceCode, "USD",
					         priceIncludesValue, true, quoteUponRequest, productName,"",priceGrids);
		} else {
			priceGrids = douglasBridgePriceGridParser.getBasePriceGrids(listOfPrices.toString(), 
			         listOfQuantity.toString(), priceCode, "USD",
			         priceIncludesValue, true, "true", productName,"",priceGrids);
		}
		    if(!imprintMethodUpchargeMap.isEmpty()){
		    	priceGrids = douglasBridgeAttributeParser.getImprintMethodUpcharges(imprintMethodUpchargeMap,
                        listOfImprintMethods, priceGrids);
		    }
				
				List<AdditionalColor> additionalColorList = null;
			if(!StringUtils.isEmpty(additionalClrRunChrgCode)){
				 additionalColorList = douglasBridgeAttributeParser.getAdditionalColor("Additional Color");
					priceGrids = douglasBridgeAttributeParser.getAdditionalColorUpcharge(additionalClrRunChrgCode,
							additionalClrRunChrgPrice.toString(), priceGrids,"Run Charge",listOfQuantity.toString());
					productConfigObj.setAdditionalColors(additionalColorList);
			}
			if(!StringUtils.isEmpty(additionalColorPriceVal) && !additionalColorPriceVal.equals("0")){
				if(additionalColorList == null){
					additionalColorList = douglasBridgeAttributeParser.getAdditionalColor("Additional Color");
					productConfigObj.setAdditionalColors(additionalColorList);
				}
				priceGrids = douglasBridgeAttributeParser.getAdditionalColorUpcharge(additionalColorCode,
						   additionalColorPriceVal, priceGrids,"Add. Color Charge","1");
			}
		/////////////
			themeList = douglasBridgeAttributeParser.getProductTheme(themesValues.toString());
			 if(CollectionUtils.isEmpty(themeList)){// from supplier sheet 
				 if(!CollectionUtils.isEmpty(productConfigObj.getThemes())){// check existing themes
					 themeList =  productConfigObj.getThemes();
				 }
			 }
			 List<String> keywordList = douglasBridgeAttributeParser.getProductKeywords(keywords.toString());
			 productExcelObj.setProductKeywords(keywordList);
		   productConfigObj.setThemes(themeList);
		   productExcelObj.setAdditionalProductInfo(additionalPrdInfo.toString());
		   productConfigObj.setImprintLocation(listImprintLocation);
		   productConfigObj.setImprintMethods(listOfImprintMethods);
			productExcelObj.setProductConfigurations(productConfigObj);
			productExcelObj.setPriceGrids(priceGrids);
			String name = productExcelObj.getName();
			if(name.contains("Simply Smashing") && name.contains("Compressed T")){
				productExcelObj.setName("Simply Smashing Compressed T-Shirt");
			}
			if(productExcelObj.getImages() != null){
				List<Image> imageList = removeImageConfiguration(productExcelObj.getImages());
				productExcelObj.setImages(imageList);	
			}
			String desc = productExcelObj.getDescription();
			if(StringUtils.isEmpty(desc)){
				productExcelObj.setDescription(productExcelObj.getName());
				productExcelObj.setSummary(productExcelObj.getName());
			 } else {
				 if(desc.contains(".")){
						productExcelObj.setSummary(desc.substring(0, desc.indexOf(".")+1));	
					} else {
						productExcelObj.setSummary(desc);
					}
					 if(productExcelObj.getSummary().length() > 130){
						 productExcelObj.setSummary(productExcelObj.getName());
					 }	 
			 }
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
			_LOGGER.error("Error while Processing excel sheet "+e.getMessage());
			return finalResult;
		}finally{
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet "+e.getMessage());
	
			}
				_LOGGER.info("Complted processing of excel sheet ");
				_LOGGER.info("Total no of product:"+numOfProductsSuccess.size() );
		}	
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
	private String getFinalDescription(String description, String asiProdNo){
		if(description.toUpperCase().contains(asiProdNo)){
			description = CommonUtility.removeSpecificWord(description, asiProdNo);
			description = description.replaceAll(asiProdNo, "");
		}
		description = description.replaceAll("Velcro", "");
		description = douglasBridgeAttributeParser.removeLineNames(description);
		description = CommonUtility.getStringLimitedChars(description, 800);
	 return description;
	}
	private String getFinalProductName(String productName,String asiProdNo){
		 if(productName.toUpperCase().contains(asiProdNo)){
			 productName = CommonUtility.removeSpecificWord(productName, asiProdNo);
			 productName = productName.replaceAll(asiProdNo, "");
			}
		 productName = productName.replaceAll("Velcro", "");
		 productName = douglasBridgeAttributeParser.removeLineNames(productName);
		 productName = CommonUtility.getStringLimitedChars(productName, 60);
		 return productName;
	}
	public boolean isRepeateColumn(int columnIndex){
		if(columnIndex != 13 && columnIndex != 15){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	private List<Image> removeImageConfiguration(List<Image> existingImages){
		List<Image> ima = existingImages;
		existingImages = existingImages.stream().map(image->{
			image.setConfigurations(new ArrayList<>());
			return image;
		}).collect(Collectors.toList());
		
		return existingImages;
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
	public DouglasBridgeAttributeParser getDouglasBridgeAttributeParser() {
		return douglasBridgeAttributeParser;
	}

	public void setDouglasBridgeAttributeParser(DouglasBridgeAttributeParser douglasBridgeAttributeParser) {
		this.douglasBridgeAttributeParser = douglasBridgeAttributeParser;
	}

	public DouglasBridgePriceGridParser getDouglasBridgePriceGridParser() {
		return douglasBridgePriceGridParser;
	}

	public void setDouglasBridgePriceGridParser(DouglasBridgePriceGridParser douglasBridgePriceGridParser) {
		this.douglasBridgePriceGridParser = douglasBridgePriceGridParser;
	}


}
