package com.a4tech.supplier.mapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.Color;
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

import parser.PelicanGraphics.PelicanGraphicAttributeParser;
import parser.PelicanGraphics.PelicanGraphicPriceGridParser;
import parser.goldstarcanada.GoldstarCanadaLookupData;
import parser.sageRMKWorldwide.SageRMKWorldwideAttributeParser;
import parser.sageRMKWorldwide.SageRMKWorldwidePriceGridParser;


public class PelicanGraphicMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(PelicanGraphicMapping.class);
	
	private PostServiceImpl 				postServiceImpl;
	private ProductDao 						productDaoObj;
	private PelicanGraphicAttributeParser   pelicanGraphicAttributeParser;
	private PelicanGraphicPriceGridParser   pelicanGraphicpriceGridParser;

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
		while (iterator.hasNext()) {
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO)
				continue;
			// this value is check first column or not becuase first column is skipped if value is not present
			boolean isFirstColum = true;
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
						 if(nextRow.getRowNum() != 1){
							 System.out.println("Java object converted to JSON String, written to file");
							   if(CollectionUtils.isEmpty(listOfImprintMethods)){
								   listOfImprintMethods = pelicanGraphicAttributeParser.getImprintMethodValues(listOfImprintMethods, "Printed");
							   }
							   productExcelObj.setAdditionalProductInfo(additionalPrdInfo.toString());
							   productConfigObj.setImprintLocation(listImprintLocation);
							   productConfigObj.setImprintMethods(listOfImprintMethods);
								productExcelObj.setProductConfigurations(productConfigObj);
								productExcelObj.setPriceGrids(priceGrids);
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
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid.trim());
						    }
						    existingApiProduct = postServiceImpl.getProduct(accessToken, xid, environmentType);
						     if(existingApiProduct == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						    	 productExcelObj.setExternalProductId(xid);
						     }else{
						  	//   productExcelObj=existingApiProduct;
								//productConfigObj=existingApiProduct.getProductConfigurations();
						    	 productExcelObj = pelicanGraphicAttributeParser.keepExistingProductData(existingApiProduct);
						    	 productConfigObj=productExcelObj.getProductConfigurations();
						    	 productExcelObj.setExternalProductId(xid);
						        /*String confthruDate=existingApiProduct.getPriceConfirmedThru();
						        productExcelObj.setPriceConfirmedThru(confthruDate);*/
						     }
							//productExcelObj = new Product();
					 }
				}
				

				switch (columnIndex + 1) {
			
				case 1://xid
					   productExcelObj.setExternalProductId(xid);
					
					 break;
				case 2://AsiProdNo
					     asiProdNo=CommonUtility.getCellValueStrinOrInt(cell);
					     if(asiProdNo.length() > 14){
					    	 asiProdNo = asiProdNo.replaceAll("-", "");
					    	 asiProdNo = asiProdNo.replaceAll("\\s+","");
					    	 asiProdNo = CommonUtility.getStringLimitedChars(asiProdNo, 14);
					     }
					     productExcelObj.setAsiProdNo(asiProdNo);		
					  break;
				case 3://Name
					 productName = cell.getStringCellValue();
					 productName = getFinalProductName(productName, asiProdNo);
					 productExcelObj.setName(productName);
						break;
				case 4://CatYear(Not used)
					// CatYear=CommonUtility.getCellValueStrinOrInt(cell);
				    break;
					
				case 5://PriceConfirmedThru
					// priceConfirmedThru = cell.getDateCellValue();
					 
					break;
					
				case 6: //  product status ,discontinued
					
					// ProductStatus=cell.getStringCellValue();
					// Prod_Status=cell.getBooleanCellValue();
					 break;
					
				case 7://catalog1
					
					break;
					
				case 8: // catalog2

					break;
					
				case 9: //Catalogs page number, Page1
					// no need to process
					break;
				case 10:  
					//Catalogs(Not used),Page2
					break;
					
				case 11:  //Description
					String description =CommonUtility.getCellValueStrinOrInt(cell);
					description = getFinalDescription(description, asiProdNo);
					productExcelObj.setDescription(description);
									
					break;
				
				case 12:  // keywords
					String productKeyword = cell.getStringCellValue();
					if(!StringUtils.isEmpty(productKeyword)){
								List<String> productKeyWords = pelicanGraphicAttributeParser
										.getProductKeywords(productKeyword);
					 productExcelObj.setProductKeywords(productKeyWords);
					}
					break;

				case 13: //Colors
				String colorValue=cell.getStringCellValue();
				  if(!StringUtils.isEmpty(colorValue)){
					  List<Color> colorsList = pelicanGraphicAttributeParser.getProductColor(colorValue);
						productConfigObj.setColors(colorsList);  
				  }
					break;
					
				case 14: // Themes
					 String themeValue=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(themeValue)){
						 themeList = pelicanGraphicAttributeParser.getProductTheme(themeValue);
						 if(CollectionUtils.isEmpty(themeList)){// from supplier sheet 
							 if(!CollectionUtils.isEmpty(productConfigObj.getThemes())){// check existing themes
								 themeList =  productConfigObj.getThemes();
							 }
						 }
					 }					 
					break;
					
				case 15://size --  value
						String dimensionValue1=CommonUtility.getCellValueStrinOrInt(cell);
					   if(dimensionValue1 != null && !dimensionValue1.isEmpty()){
						   dimensionValue.append(dimensionValue1).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					   }
					
					break;
				case 16: //size -- Unit
					  String dimensionUnits1 = CommonUtility.getCellValueStrinOrInt(cell);
					 if(!dimensionUnits1.equals("0")){
						 dimensionUnits.append(dimensionUnits1.trim()).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					 }
					  break;
				
				case 17: //size -- type
					String dimensionType1 =CommonUtility.getCellValueStrinOrInt(cell);
					if(!dimensionType1.equals("0")){
						dimensionType.append(dimensionType1).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					break;
				
				 case 18: //size
					 String dimensionValue2 =CommonUtility.getCellValueStrinOrInt(cell);
					 if(dimensionValue2 != null && !dimensionValue2.isEmpty()){
						 dimensionValue.append(dimensionValue2).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					 }
					break;
				case 19:  //size
					String dimensionUnits2 =CommonUtility.getCellValueStrinOrInt(cell);
					
					if(!dimensionUnits2.equals("0")){
						dimensionUnits.append(dimensionUnits2.trim()).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					break;
					
				case 20: //size
					String  dimensionType2 = CommonUtility.getCellValueStrinOrInt(cell);

					if(!dimensionType2.equals("0")){
						dimensionType.append(dimensionType2).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					break;
					
				case 21: //size
					String dimensionValue3  =CommonUtility.getCellValueStrinOrInt(cell);
					if(dimensionValue3 != null && !dimensionValue3.isEmpty()){
						dimensionValue.append(dimensionValue3).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					break;
				case 22: //size
					String dimensionUnits3 = CommonUtility.getCellValueStrinOrInt(cell);
					if(!dimensionUnits3.equals("0")){
						 dimensionUnits.append(dimensionUnits3.trim()).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
				   break;
					
				case 23: //size
					String dimensionType3 = CommonUtility.getCellValueStrinOrInt(cell);
					if(!dimensionType3.equals("0")){
						dimensionType.append(dimensionType3).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
				   break;
				   
				case 24:  // Quantities
				case 25: 
				case 26: 
				case 27: 
				case 28:
				case 29:
					String quantity = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(quantity) && !quantity.equals("0")){
			        	 listOfQuantity.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			         }
					   	break;
				case 30:  // prices --list price
				case 31:
				case 32:
				case 33:
				case 34:
				case 35:
					String prices = CommonUtility.getCellValueDouble(cell);
					 if(!StringUtils.isEmpty(prices)&& !prices.equals("0")){
			        	 listOfPrices.append(prices).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			         }
					    break; 
				case 36: // price code -- discount   
				priceCode = cell.getStringCellValue();	
					     break;
				case 37:       // pricesPerUnit
				case 38:
				case 39:
				case 40:
				case 41:
				case 42:
					String pricesPerUnits = CommonUtility.getCellValueDouble(cell);
					 if(!StringUtils.isEmpty(pricesPerUnits)){
			        	 pricesPerUnit.append("1").append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			         }
					//set default value is "1" in PricesPerUnit
					      break;
				case 43:
					     quoteUponRequest = cell.getStringCellValue();
					      break;
				case 44:  // priceIncludeClr    
					      priceIncludes.append(cell.getStringCellValue()).append(" ");
					     break;
				case 45: // priceIncludeSide
						
						priceIncludes.append(cell.getStringCellValue()).append(" ");
						
						break;
				case 46: // priceIncludeLoc
						priceIncludes.append(cell.getStringCellValue());
						priceIncludesValue = CommonUtility.getStringLimitedChars(priceIncludes.toString(), 100);
						break;
						
				case 47:   //Set-up Charge 
					String setupChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!setupChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("setupCharge", setupChargePrice);
					}
				  break;
				case 48://setup charge code					
					String setUpchargeCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(setUpchargeCode) && !setUpchargeCode.equals("0")){
						String priceVal = imprintMethodUpchargeMap.get("setupCharge");
						priceVal = priceVal+"_"+setUpchargeCode;
						imprintMethodUpchargeMap.put("setupCharge", priceVal);
					}
				  break;
				case 49://screen charge
					String screenChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!screenChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("screenCharge", screenChargePrice);
					}
							break;
				case 50://screen charge code
					String screenchargeCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(screenchargeCode) && !screenchargeCode.equals("0")){
						String priceVal = imprintMethodUpchargeMap.get("screenCharge");
						priceVal = priceVal+"_"+screenchargeCode;
						imprintMethodUpchargeMap.put("screenCharge", priceVal);
					}
							break;
				case 51:// plate charge
					String plateChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!plateChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("plateCharge", plateChargePrice);
					}

							break;
				case 52://plateCharge code
					String plateChargeCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(plateChargeCode) && !plateChargeCode.equals("0")){
						String priceVal = imprintMethodUpchargeMap.get("plateCharge");
						priceVal = priceVal+"_"+plateChargeCode;
						imprintMethodUpchargeMap.put("plateCharge", priceVal);
					}
							break;
				case 53:// dieCharge
					String diaChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!diaChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("diaCharge", diaChargePrice);
					}
							break;
				case 54:// die Charge code
					String diaChargeCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(diaChargeCode) && !diaChargeCode.equals("0")){
						String priceVal = imprintMethodUpchargeMap.get("diaCharge");
						priceVal = priceVal+"_"+diaChargeCode;
						imprintMethodUpchargeMap.put("diaCharge", priceVal);
					}
							break;
				case 55://tooling charge
					String toolingChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!toolingChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("toolingCharge", toolingChargePrice);
					}
							break;
				case 56:// tooling charge code
					String toolingChargeCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(toolingChargeCode) &&!toolingChargeCode.equals("0")){
						String priceVal = imprintMethodUpchargeMap.get("toolingCharge");
						priceVal = priceVal+"_"+toolingChargeCode;
						imprintMethodUpchargeMap.put("toolingCharge", priceVal);
					}
							break;
				case 57://repeate charge
					/*String repeateChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!repeateChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("repeateCharge", repeateChargePrice);
					}*/
					// no need to process this column since there is no vaild data
							break; 
				case 58://repeate charge code
							/*String repeateChargeCode = CommonUtility.getCellValueStrinOrInt(cell);
							if(!repeateChargeCode.equals("0")){
								String priceVal = imprintMethodUpchargeMap.get("repeateCharge");
								priceVal = priceVal+"_"+repeateChargeCode;
								imprintMethodUpchargeMap.put("repeateCharge", priceVal);
							}*/
					// no need to process this column since there is no vaild data
						break;
				case 59: // additioNal color
					    additionalColorPriceVal = CommonUtility.getCellValueStrinOrInt(cell);
						 break;
				case 60: // additional color code
					additionalColorCode = cell.getStringCellValue();
						break;
				case 61: 	
				case 62:							
				case 63:	
				case 64:	
				case 65:	
				case 66:
				  String colorCharge = CommonUtility.getCellValueDouble(cell);
				  if(!colorCharge.equalsIgnoreCase("0.00") || !colorCharge.equalsIgnoreCase("0.0") ||
						  !colorCharge.equalsIgnoreCase("0"))
				  additionalClrRunChrgPrice.append(colorCharge).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					break;
				case 67:// additional run charge code
				  String additionalClrRunCode = cell.getStringCellValue();
				      if(!StringUtils.isEmpty(additionalClrRunCode)){
					       additionalClrRunChrgCode = additionalClrRunCode;
				      }    	
				break; 
				case 68:
					       break;
				case 69:
							String IsEnvironmentallyFriendly = cell.getStringCellValue();
							if (IsEnvironmentallyFriendly.equalsIgnoreCase("true")) {
								Theme themeObj = new Theme();
								themeObj.setName("Eco Friendly");
								themeList.add(themeObj);
							}
					break;
				case 70:// not used
					break;
				case 71:// not used
					break;
				case 72:// not used
					break;
				case 73:
					break;
				case 74:
					break;
				case 75: // Imprint size1
					 FirstImprintsize1=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(FirstImprintsize1) && !FirstImprintsize1.equals("0")){
						 ImprintSizevalue.append(FirstImprintsize1).append(" ");
					
					 }
					    break;
					    
				case 76: //// Imprint size1 unit
					FirstImprintunit1=CommonUtility.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(FirstImprintsize1) && !StringUtils.isEmpty(FirstImprintunit1)
									&& !FirstImprintunit1.equals("0")) {
					FirstImprintunit1=GoldstarCanadaLookupData.Dimension1Units.get(FirstImprintunit1);
					     ImprintSizevalue.append(FirstImprintunit1).append(" ");
					 }	 
					   	break;
					   	
				case 77:   // Imprint size1 Type
					FirstImprinttype1=CommonUtility.getCellValueStrinOrInt(cell);
					
							if (!StringUtils.isEmpty(FirstImprintsize1) && !StringUtils.isEmpty(FirstImprinttype1)
									&& !FirstImprinttype1.equals("0")) {
					FirstImprinttype1=GoldstarCanadaLookupData.Dimension1Type.get(FirstImprinttype1);
					  ImprintSizevalue.append(FirstImprinttype1).append(" ");
				   }
					break;
				case 78: // // Imprint size2
					FirstImprintsize2=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(FirstImprintsize2)&& !FirstImprintsize2.equals("0")){
						 ImprintSizevalue.append("x").append(FirstImprintsize2).append(" ");
					 }

					  	break;
					  	
				case 79:	// Imprint size2 Unit
					FirstImprintunit2=CommonUtility.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(FirstImprintsize2) && !StringUtils.isEmpty(FirstImprintunit2)
									&& !FirstImprintunit2.equals("0")) {
					FirstImprintunit2=GoldstarCanadaLookupData.Dimension1Units.get(FirstImprintunit2);
					    ImprintSizevalue.append(FirstImprintunit2).append(" ");
				    }
					    break;
					    
				case 80: // Imprint size2 Type
					FirstImprinttype2=CommonUtility.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(FirstImprintsize2) && !StringUtils.isEmpty(FirstImprinttype2)
									&& !FirstImprinttype2.equals("0")) {
					FirstImprinttype2=GoldstarCanadaLookupData.Dimension1Type.get(FirstImprinttype2);
					    ImprintSizevalue.append(FirstImprinttype2).append(" ");
				    }
					break;
					  	
				case 81:  // Imprint location
					
					 imprintLocation = cell.getStringCellValue();
					if(!imprintLocation.isEmpty()){
						ImprintLocation locationObj = new ImprintLocation();
						locationObj.setValue(imprintLocation);
						listImprintLocation.add(locationObj);
					}
					 break;
				case 82:  // Second Imprintsize1
					SecondImprintsize1=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprintsize1)&& !SecondImprintsize1.equals("0")){
					  ImprintSizevalue.append(SecondImprintsize1).append(" ");
				    }
					   	break;
					   	
				case 83:  // Second Imprintsize1 unit
					SecondImprintunit1=CommonUtility.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(SecondImprintsize1) && !StringUtils.isEmpty(SecondImprintunit1)
									&& !SecondImprintunit1.equals("0")) {
					SecondImprintunit1=GoldstarCanadaLookupData.Dimension1Units.get(SecondImprintunit1);
					   ImprintSizevalue.append(SecondImprintunit1).append(" ");
					}
						break;
				case 84:  // Second Imprintsize1 type
					SecondImprinttype1=CommonUtility.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(SecondImprintsize1) && !StringUtils.isEmpty(SecondImprinttype1)
									&& !SecondImprinttype1.equals("0")) {
					SecondImprinttype1=GoldstarCanadaLookupData.Dimension1Type.get(SecondImprinttype1);
					   ImprintSizevalue.append(SecondImprinttype1).append(" ");
					}
					  break;
					  
				case 85: // Second Imprintsize2
					SecondImprintsize2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprintsize2)&& !SecondImprintsize2.equals("0")){
				       ImprintSizevalue.append("x").append(SecondImprintsize2).append(" ");
				    }
					break;
					
				case 86: //Second Imprintsize2 Unit
					SecondImprintunit2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprintsize2) && !StringUtils.isEmpty(SecondImprintunit2)&& !SecondImprintunit2.equals("0")){
					SecondImprintunit2=GoldstarCanadaLookupData.Dimension1Units.get(SecondImprintunit2);
					    ImprintSizevalue.append(SecondImprintunit2).append(" ");
					}

					break;
					
				case 87: // Second Imprintsize2 type	
					SecondImprinttype2=CommonUtility.getCellValueStrinOrInt(cell);
							if (!StringUtils.isEmpty(SecondImprintsize2) && !StringUtils.isEmpty(SecondImprinttype2)
									&& !SecondImprinttype2.equals("0")) {
					SecondImprinttype2=GoldstarCanadaLookupData.Dimension1Type.get(SecondImprinttype2);
					   ImprintSizevalue.append(SecondImprinttype2).append(" ");
					}					
					  break;
					  
				case 88: // Second Imprint location
					String imprintLocation2 = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintLocation2)){
						ImprintLocation locationObj2 = new ImprintLocation();
						locationObj2.setValue(imprintLocation2);
						listImprintLocation.add(locationObj2);
					}
					break;
				case 89: // DecorationMethod 
					decorationMethod = cell.getStringCellValue();
							listOfImprintMethods = pelicanGraphicAttributeParser
									.getImprintMethodValues(decorationMethod);
					 break; 
					 
				case 90: //NoDecoration
					String noDecoration = cell.getStringCellValue();
				    if(!StringUtils.isEmpty(noDecoration)){
				    	if(noDecoration.equalsIgnoreCase("True")){
									listOfImprintMethods = pelicanGraphicAttributeParser
											.getImprintMethodValues(listOfImprintMethods, "Unimprinted");
				    	}
				    }
					 break;
				case 91: //NoDecorationOffered
					// no need to process this column since above column data also same
					 break;
				case 92: //NewPictureURL
					break;
				case 93:  //NewPictureFile  -- not used
					break;
				case 94: //ErasePicture -- not used
					break;
				case 95: //NewBlankPictureURL
					break;
				case 96: //NewBlankPictureFile -- not used
					break;
				case 97://EraseBlankPicture  -- not used
					break;
					 
				case 98: //PicExists   -- not used
					break;
				case 99: //NotPictured  -- not used
					break;
				case 100: //MadeInCountry
					
					String madeInCountry = cell.getStringCellValue();
					if(!StringUtils.isEmpty(madeInCountry)){
								List<Origin> listOfOrigin = pelicanGraphicAttributeParser
										.getOriginValues(madeInCountry);
						productConfigObj.setOrigins(listOfOrigin);
					}
					break;
				case 101:// AssembledInCountry
			     String additionalProductInfo = cell.getStringCellValue();
			     if(!StringUtils.isEmpty(additionalProductInfo)) {
								additionalPrdInfo.append("AssembledInCountry:").append(
										pelicanGraphicAttributeParser.getCountryCodeConvertName(additionalProductInfo)); 
			       }
					break;
				case 102: //DecoratedInCountry
					String additionalImprintInfo = cell.getStringCellValue();
					 if(!StringUtils.isEmpty(additionalImprintInfo)) {
						 StringBuilder addImprintInfo = new StringBuilder();
								addImprintInfo.append("DecoratedInCountry:").append(
										pelicanGraphicAttributeParser.getCountryCodeConvertName(additionalImprintInfo));
						 productExcelObj.setAdditionalImprintInfo(addImprintInfo.toString());
					   }
					
					break;
				case 103: //ComplianceList  -- No data
					String complnceValuet=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(complnceValuet)) {
				    	complianceList.add(complnceValuet);
				    	productExcelObj.setComplianceCerts(complianceList);
					   }
					break;
					
				case 104://ComplianceMemo  -- No data
					String productDataSheet=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(productDataSheet) && !productDataSheet.equals("0")){
						 productExcelObj.setProductDataSheet(productDataSheet);
					   }
					break;
				case 105: //ProdTimeLo
				   prodTimeLo = CommonUtility.getCellValueStrinOrInt(cell);
					break;
				case 106: //ProdTimeHi
					String prodTimeHi = CommonUtility.getCellValueStrinOrInt(cell);
					if(!prodTimeHi.equals("0")){
								List<ProductionTime> productionTimeList = pelicanGraphicAttributeParser
										.getProductionTime(prodTimeLo, prodTimeHi);
						productConfigObj.setProductionTime(productionTimeList);
					}
					break;
				case 107://RushProdTimeLo
					 rushProdTimeLo  = cell.getStringCellValue();
					 break; 	 
				case 108://RushProdTimeH
					String rushProdTimeH  = cell.getStringCellValue();
					if(!rushProdTimeH.equals(ApplicationConstants.CONST_STRING_ZERO)){
								RushTime productRushTime = pelicanGraphicAttributeParser
										.getProductRushTime(rushProdTimeLo, rushProdTimeH);
					  productConfigObj.setRushTime(productRushTime);
					}
					break;
				case 109://Packaging
					String pack  = cell.getStringCellValue();
					if(!StringUtils.isEmpty(pack)){
								List<Packaging> listOfPackaging = pelicanGraphicAttributeParser.getPackageValues(pack);
						productConfigObj.setPackaging(listOfPackaging);
					}
					break;
				case 110: //CartonL
					 String cartonL  = CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(cartonL) && !cartonL.equals("0")){
						 shippingDimensions.append(cartonL).append(",");
					 }
					break;
				case 111://CartonW
					String cartonW  = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(cartonW) &&!cartonW.equals("0")){
						 shippingDimensions.append(cartonW).append(",");
					 }
					break;
				case 112://CartonH
					String cartonH  = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(cartonH) && !cartonH.equals("0")){
						 shippingDimensions.append(cartonH);
					 }
					break;
				case 113: //WeightPerCarton
					weightPerCarton  =CommonUtility.getCellValueStrinOrInt(cell);
					break;
				case 114: //UnitsPerCarton
					unitsPerCarton  = CommonUtility.getCellValueStrinOrInt(cell);
					break;
				case 115: //ShipPointCountry
					break;
				case 116: //ShipPointZip
					break;
				case 117: //Comment
					String comment = cell.getStringCellValue();
					if(!StringUtils.isEmpty(comment)){
						if(StringUtils.isEmpty(additionalPrdInfo.toString())){
							additionalPrdInfo.append(comment);
						} else {
							additionalPrdInfo.append(", ").append(comment);
						}
					}
					break;
				case 118: //Verified
					//since data is already expired date
					break;
				case 119: //UpdateInventory
					break;
				case 120: //InventoryOnHand
					break;
				case 121: //InventoryOnHandAdd
					break;
					
				case 122: //InventoryMemo
				break;
			}  // end switch		 
		}//end inner while loop
			// set  product configuration objects
			// end inner while loop
			//it is store xid if once mapping completed 
			ProductProcessedList.add(productExcelObj.getExternalProductId());
			productExcelObj.setPriceType("L");
			if(!StringUtils.isEmpty(dimensionValue.toString())){
				Size productSize = pelicanGraphicAttributeParser.getProductSize(dimensionValue.toString(),
		                dimensionUnits.toString(), dimensionType.toString());
				productConfigObj.setSizes(productSize);
			}
			if(!CollectionUtils.isEmpty(themeList)){
				productConfigObj.setThemes(themeList);
			}
			imprintSizeList=pelicanGraphicAttributeParser.getimprintsize(ImprintSizevalue);
			if(FirstImprintsize1 != "" || FirstImprintsize2 !="" ||
					SecondImprintsize1 != "" ||	SecondImprintsize2 !=""){
			productConfigObj.setImprintSize(imprintSizeList);}
					ShippingEstimate shippingEstimation = pelicanGraphicAttributeParser
							.getShippingEstimateValues(shippingDimensions.toString(), weightPerCarton, unitsPerCarton);
					productConfigObj.setShippingEstimates(shippingEstimation);
			if(listOfPrices != null && !listOfPrices.toString().isEmpty()){
				priceGrids = pelicanGraphicpriceGridParser.getBasePriceGrids(listOfPrices.toString(), 
						         listOfQuantity.toString(), priceCode, "USD",
						         priceIncludesValue, true, quoteUponRequest, productName,"",priceGrids);
			} else {
				priceGrids = pelicanGraphicpriceGridParser.getBasePriceGrids(listOfPrices.toString(), 
				         listOfQuantity.toString(), priceCode, "USD",
				         priceIncludesValue, true, "true", productName,"",priceGrids);
			}
			    if(!imprintMethodUpchargeMap.isEmpty()){
			    	priceGrids = pelicanGraphicAttributeParser.getImprintMethodUpcharges(imprintMethodUpchargeMap,
                            listOfImprintMethods, priceGrids);
			    }
					
					List<AdditionalColor> additionalColorList = null;
				if(!StringUtils.isEmpty(additionalClrRunChrgCode)){
					 additionalColorList = pelicanGraphicAttributeParser.getAdditionalColor("Additional Color");
						priceGrids = pelicanGraphicAttributeParser.getAdditionalColorUpcharge(additionalClrRunChrgCode,
								pelicanGraphicAttributeParser.toString(), priceGrids,"Run Charge");
						productConfigObj.setAdditionalColors(additionalColorList);
				}
				if(!StringUtils.isEmpty(additionalColorPriceVal) && !additionalColorPriceVal.equals("0")){
					if(additionalColorList == null){
						additionalColorList = pelicanGraphicAttributeParser.getAdditionalColor("Additional Color");
						productConfigObj.setAdditionalColors(additionalColorList);
					}
					priceGrids = pelicanGraphicAttributeParser.getAdditionalColorUpcharge(additionalColorCode,
							   additionalColorPriceVal, priceGrids,"Add. Color Charge");
				}
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() );		 
		}
		}
		workbook.close();
		 if(CollectionUtils.isEmpty(listOfImprintMethods)){
			   listOfImprintMethods = pelicanGraphicAttributeParser.getImprintMethodValues(listOfImprintMethods, "Printed");
		   }
		 productExcelObj.setAdditionalProductInfo(additionalPrdInfo.toString());
		 productConfigObj.setImprintLocation(listImprintLocation);
		   productConfigObj.setImprintMethods(listOfImprintMethods);
		 	productExcelObj.setProductConfigurations(productConfigObj);
		 	productExcelObj.setPriceGrids(priceGrids);
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
				xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
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
		description = CommonUtility.getStringLimitedChars(description, 800);
	 return description;
	}
	private String getFinalProductName(String productName,String asiProdNo){
		 if(productName.toUpperCase().contains(asiProdNo)){
			 productName = CommonUtility.removeSpecificWord(productName, asiProdNo);
			 productName = productName.replaceAll(asiProdNo, "");
			}
		 productName = CommonUtility.getStringLimitedChars(productName, 60);
		 return productName;
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
	public PelicanGraphicAttributeParser getPelicanGraphicAttributeParser() {
		return pelicanGraphicAttributeParser;
	}

	public void setPelicanGraphicAttributeParser(PelicanGraphicAttributeParser pelicanGraphicAttributeParser) {
		this.pelicanGraphicAttributeParser = pelicanGraphicAttributeParser;
	}

	public PelicanGraphicPriceGridParser getPelicanGraphicpriceGridParser() {
		return pelicanGraphicpriceGridParser;
	}

	public void setPelicanGraphicpriceGridParser(PelicanGraphicPriceGridParser pelicanGraphicpriceGridParser) {
		this.pelicanGraphicpriceGridParser = pelicanGraphicpriceGridParser;
	}
	
}
