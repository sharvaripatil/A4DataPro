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
import org.springframework.util.StringUtils;

import parser.milestone.MilestoneColorParser;
import parser.milestone.MilestoneLookupData;
import parser.milestone.MilestonePriceGridParser;
import parser.milestone.ProductAttributeParser;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.Values;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class MilestoneExcelMapping implements IExcelParser {

private static final Logger _LOGGER = Logger.getLogger(MilestoneExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private LookupServiceData lookupServiceDataObj;

	private MilestoneColorParser colorParserObj;
	private ProductAttributeParser attrtiParserObj;
	private MilestonePriceGridParser pricegridParserObj;




	
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
		
		  List<String> numOfProductsSuccess = new ArrayList<String>();
		  List<String> numOfProductsFailure = new ArrayList<String>();
		
		  Set<String>  productXids = new HashSet<String>();
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  List<ImprintSize> imprintSizeList =new ArrayList<ImprintSize>();
		  List<ImprintLocation> listImprintLocation = new ArrayList<ImprintLocation>();
		  List<ImprintMethod> listOfImprintMethods = new ArrayList<ImprintMethod>();
		  List<ProductionTime> listOfProductionTime = new ArrayList<ProductionTime>();
		  List<String> productKeywords = new ArrayList<String>();
		  List<Theme> themeList = new ArrayList<Theme>();
		  List<Values> valuesList =new ArrayList<Values>();
		  List<FOBPoint> FobPointsList = new ArrayList<FOBPoint>();
		  List<Color> color = new ArrayList<Color>();


		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  Dimension finalDimensionObj=new Dimension();
		  Size size=new Size();
		  FOBPoint fobPintObj=new FOBPoint();
		  ShippingEstimate shipping = new ShippingEstimate();
		  String productName = null;
		  String productId = null;
		  String finalResult = null;
		  Option ImprintOptObj=new Option();
		  List<Option> optionList = new ArrayList<Option>();

		  
			StringBuilder listOfQuantity = new StringBuilder();
			StringBuilder listOfPrices = new StringBuilder();
			StringBuilder pricesPerUnit = new StringBuilder();
			StringBuilder dimensionValue = new StringBuilder();
			StringBuilder dimensionUnits = new StringBuilder();
			StringBuilder dimensionType = new StringBuilder();
			StringBuilder ImprintSizevalue = new StringBuilder();


		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
	
		String themeValue=null;
		String priceCode = null;
		String quoteUponRequest=null;
		String quantity = null;
		String listPrice = null;
		String pricesUnit = null;
		String serviceCharge = null;
		String cartonL = null;
		String cartonW = null;
		String cartonH = null;
		String weightPerCarton = null;
		String unitsPerCarton = null;
		String DiscountcodeUpcharge = null;
		String decorationMethod =null;
		Product existingApiProduct=null;
		String priceIncludesValue=null;
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
		String imprintLocation = null;
		String prodTimeLo = null;
		String ImprintOption=null;
		
		
	
		
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum() < 1)
				continue;
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
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						xid = cell.getStringCellValue();
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						xid = String.valueOf((int)cell.getNumericCellValue());
					}else{
						
					}
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							 System.out.println("Java object converted to JSON String, written to file");
							 	
							 	if(!StringUtils.isEmpty(themeValue) ){
								productConfigObj.setThemes(themeList);
								}
							 	String DimensionRef=null;
								DimensionRef=dimensionValue.toString();
								if(!StringUtils.isEmpty(DimensionRef)){
								valuesList =attrtiParserObj.getValues(dimensionValue.toString(),
						                dimensionUnits.toString(), dimensionType.toString());
								
						        finalDimensionObj.setValues(valuesList);	
								size.setDimension(finalDimensionObj);
								productConfigObj.setSizes(size);
								}
								imprintSizeList=attrtiParserObj.getimprintsize(ImprintSizevalue);
								if(FirstImprintsize1 != "" || FirstImprintsize2 !="" ||
										SecondImprintsize1 != "" ||	SecondImprintsize2 !=""){
								productConfigObj.setImprintSize(imprintSizeList);}
								productConfigObj.setProductionTime(listOfProductionTime);	

								shipping = attrtiParserObj.getShippingEstimateValues(cartonL, cartonW,
					                    cartonH, weightPerCarton, unitsPerCarton);
								if(!unitsPerCarton.contains("0")){
								productConfigObj.setShippingEstimates(shipping);
								}
								productConfigObj.setImprintMethods(listOfImprintMethods);
								
								productExcelObj.setPriceGrids(priceGrids);
							 	productExcelObj.setProductConfigurations(productConfigObj);

							 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId);
							 	if(num ==1){
							 		numOfProductsSuccess.add("1");
							 	}else if(num == 0){
							 		numOfProductsFailure.add("0");
							 	}else{
							 		
							 	}
							 	_LOGGER.info("list size>>>>>>>"+numOfProductsSuccess.size());
							 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
								   
						         ImprintSizevalue = new StringBuilder();
								 listOfQuantity = new StringBuilder();
								 listOfPrices = new StringBuilder();
								 pricesPerUnit = new StringBuilder();
								 dimensionValue = new StringBuilder();
								 dimensionUnits = new StringBuilder();
								 dimensionType = new StringBuilder();
						       
								  priceGrids = new ArrayList<PriceGrid>();
								  imprintSizeList =new ArrayList<ImprintSize>();
								  listImprintLocation = new ArrayList<ImprintLocation>();
								  listOfImprintMethods = new ArrayList<ImprintMethod>();
								  listOfProductionTime = new ArrayList<ProductionTime>();
								  productKeywords = new ArrayList<String>();
								  themeList = new ArrayList<Theme>();
								  valuesList =new ArrayList<Values>();
								  FobPointsList = new ArrayList<FOBPoint>();
								  color = new ArrayList<Color>();
								  ImprintOptObj=new Option();
							      optionList = new ArrayList<Option>();
								  
								
								  
								  finalDimensionObj=new Dimension();
								  size=new Size();
								  fobPintObj=new FOBPoint();
								  shipping = new ShippingEstimate();
								  productConfigObj = new ProductConfigurations();
								
						 }
						 if(!productXids.contains(xid)){
						    	productXids.add(xid.trim());
						    }
						    existingApiProduct = postServiceImpl.getProduct(accessToken, xid=xid.replace("\t",""));
						     if(existingApiProduct == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						  	    // productExcelObj=existingApiProduct;
								//productConfigObj=existingApiProduct.getProductConfigurations();
						        List<Image> Img=existingApiProduct.getImages();
						    	productExcelObj.setImages(Img);
						     
						     }
							   //productExcelObj = new Product();
					 }
				}

				switch (columnIndex + 1) {
				case 1://ExternalProductID
			    	productId = CommonUtility.getCellValueStrinOrInt(cell);
					productExcelObj.setExternalProductId(productId);
					
					 break;
				case 2://AsiProdNo
					 String asiProdNo = CommonUtility.getCellValueStrinOrInt(cell);
					 productExcelObj.setAsiProdNo(asiProdNo);		
					  break;
				case 3://Name
					 productName = cell.getStringCellValue();
						int len=productName.length();
						 if(len>60){
							String strTemp=productName.substring(0, 60);
							int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
							productName=(String) strTemp.subSequence(0, lenTemp);
						}
						productExcelObj.setName(productName);
     					
				case 4://CatYear(Not used)
					
				    break;
					
				case 5://PriceConfirmedThru
					String priceConfirmedThru = cell.getStringCellValue();
					String strArr[]=priceConfirmedThru.split("/");
					priceConfirmedThru=strArr[2]+"/"+strArr[0]+"/"+strArr[1];
					priceConfirmedThru=priceConfirmedThru.replaceAll("/", "-");
					productExcelObj.setPriceConfirmedThru(priceConfirmedThru);

					break;
					
				case 6: //  product status
					break;
					
				case 7://Catalogs
					break;
					
				case 8: // Catalogs(Not used)
					break;
					
				case 9: //Catalogs page number
					break;
					
				case 10: //Catalogs(Not used) 
					break;
					
				case 11:  //Description
					String description = cell.getStringCellValue();
					if(!StringUtils.isEmpty(description)){
					productExcelObj.setDescription(description);
					}else{
						productExcelObj.setDescription(ApplicationConstants.CONST_STRING_EMPTY);
					}
					break;
				
				case 12:  // keywords
					String productKeyword = cell.getStringCellValue();
					if(!StringUtils.isEmpty(productKeyword)){
					String productKeywordArr[] = productKeyword.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					for (String string : productKeywordArr) {
						productKeywords.add(string);
					}
					productExcelObj.setProductKeywords(productKeywords);
					}
					break;

				case 13: //Colors
					String colorValue=cell.getStringCellValue();
					if(!StringUtils.isEmpty(colorValue)){
						color=colorParserObj.getColorCriteria(colorValue);
						productConfigObj.setColors(color);
					}	
					break;
					
				case 14: // Themes
					 themeValue=cell.getStringCellValue();
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
					break;
					
				case 15://size --  value
					String dimensionValue1=CommonUtility.getCellValueStrinOrInt(cell);
					dimensionValue1=dimensionValue1.replace("\"", "");
					   if(dimensionValue1 != null && !dimensionValue1.isEmpty()){
						   dimensionValue.append(dimensionValue1).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					   }
					break;
				case 16: //size -- Unit
					  String dimensionUnits1 = CommonUtility.getCellValueStrinOrInt(cell);
					  dimensionUnits1=dimensionUnits1.replace("\"", "");

						 if(!dimensionUnits1.contains("0")){
							 dimensionUnits.append(dimensionUnits1.trim()).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
						 }
					  break;
				
				case 17: //size -- type
					String dimensionType1 =CommonUtility.getCellValueStrinOrInt(cell);
					dimensionType1=dimensionType1.replace("\"", "");

					if(!dimensionType1.contains("0")){
						dimensionType.append(dimensionType1).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					break;
				
				 case 18: //size
					 String dimensionValue2 =CommonUtility.getCellValueStrinOrInt(cell);
					 dimensionValue2=dimensionValue2.replace("\"", "");

					 if(dimensionValue2 != null && !dimensionValue2.isEmpty()){
						 dimensionValue.append(dimensionValue2).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					 }
					break;
					
				case 19:  //size
                    String dimensionUnits2 =CommonUtility.getCellValueStrinOrInt(cell);
                    dimensionUnits2=dimensionUnits2.replace("\"", "");

					if(!dimensionUnits2.contains("0")){
						dimensionUnits.append(dimensionUnits2.trim()).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					break;
					
				case 20: //size
					String  dimensionType2 = CommonUtility.getCellValueStrinOrInt(cell);
					dimensionType2=dimensionType2.replace("\"", "");

					if(!dimensionType2.contains("0")){
						dimensionType.append(dimensionType2).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					break;
					
				case 21: //size
					String dimensionValue3  =CommonUtility.getCellValueStrinOrInt(cell);
					dimensionValue3=dimensionValue3.replace("\"", "");

					if(dimensionValue3 != null && !dimensionValue3.isEmpty()){
						dimensionValue.append(dimensionValue3).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}else{
						dimensionValue=dimensionValue.append("");
					}
					
					break;
					
				case 22: //size
					String dimensionUnits3 = CommonUtility.getCellValueStrinOrInt(cell);
					dimensionUnits3=dimensionUnits3.replace("\"", "");

					if(!dimensionUnits3.contains("0")){
						 dimensionUnits.append(dimensionUnits3.trim()).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}else
					{
						dimensionUnits=dimensionUnits.append("");
					}
				   break;
					
				case 23: //size
					String dimensionType3 = CommonUtility.getCellValueStrinOrInt(cell);
					dimensionType3=dimensionType3.replace("\"", "");

					if(!dimensionType3.contains("0")){
						dimensionType.append(dimensionType3).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}else
					{
						dimensionType=dimensionType.append("");
					}
				   break;
				   
				case 24:  // Quantities
				case 25: 
				case 26: 
				case 27: 
				case 28:
				case 29:
					   quantity = CommonUtility.getCellValueStrinOrInt(cell);
					   listOfQuantity.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					  
					   	break;
				case 30:  // prices --list price
				case 31:
				case 32:
				case 33:
				case 34:
				case 35:
					   listPrice = CommonUtility.getCellValueStrinOrInt(cell);
					   listOfPrices.append(listPrice).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					    
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
						pricesUnit = CommonUtility.getCellValueStrinOrInt(cell);
				        pricesPerUnit.append(pricesUnit).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				
					      break;
				case 43:
					     quoteUponRequest = cell.getStringCellValue();
					      break;
				case 44:  // priceIncludeClr
					    
					     break;
				case 45: // priceIncludeSide
						
						break;
				case 46: // priceIncludeLoc
				    ImprintOption=cell.getStringCellValue();
					ImprintOptObj=attrtiParserObj.getImprintOption2(ImprintOption);
			        optionList.add(ImprintOptObj);
			  					
					break;
						
				case 47:    //setup charge   
				 
					 serviceCharge = CommonUtility.getCellValueDouble(cell);
				  break;
				case 48://setup discount code
				
					 DiscountcodeUpcharge = CommonUtility.getCellValueStrinOrInt(cell);
				  break;
				  
				case 49:
							break;
				case 50:
							break;
				case 51:
							break;
				case 52:
							break;
				case 53:
							break;
				case 54:
							break;
				case 55:
							break;
				case 56:
							break;
				case 57:	
							break; 
				case 58:
							break;
				case 59:
							break;
				case 60:
							break;
				case 61:
							break;
				case 62:
							break;
				case 63:
							break;
				case 64:
							break;
				case 65:
							break;
				case 66:
							break;
				case 67:
			          		break; 
				case 68:
					       break;
				case 69:
					/*String IsEnvironmentallyFriendly = cell.getStringCellValue();
					
					if(IsEnvironmentallyFriendly.equalsIgnoreCase("true"))			
					{ Theme themeObj1 = new Theme();

						themeObj1.setName("Eco Friendly");	

						themeList.add(themeObj1);
					}*/
					break;
				case 70:
					break;
				case 71:
					break;
				case 72:
					break;
				case 73:
					break;
				case 74:
					break;
				case 75: // Imprint size1
					 FirstImprintsize1=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(FirstImprintsize1) || FirstImprintsize1 !=  ""){
					 ImprintSizevalue=ImprintSizevalue.append(FirstImprintsize1).append(" ");
					
					 }
					 break;
					    
				case 76: //// Imprint size1 unit
					FirstImprintunit1=CommonUtility.getCellValueStrinOrInt(cell);
					
					 if(!StringUtils.isEmpty(FirstImprintunit1) || FirstImprintunit1 !=  ""){
					FirstImprintunit1=MilestoneLookupData.Dimension1Units.get(FirstImprintunit1);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprintunit1).append(" ");;
					 }	 
					   	break;
					   	
				case 77:   // Imprint size1 Type    
					FirstImprinttype1=CommonUtility.getCellValueStrinOrInt(cell);
					
					   if(!StringUtils.isEmpty(FirstImprinttype1) || FirstImprinttype1 !=  ""){
						FirstImprinttype1=MilestoneLookupData.Dimension1Type.get(FirstImprinttype1);
						ImprintSizevalue=ImprintSizevalue.append(FirstImprinttype1).append(" ").append("x");
					   }
						break;
						
				  
				case 78: // // Imprint size2
					FirstImprintsize2=CommonUtility.getCellValueStrinOrInt(cell);
					
					 if(!StringUtils.isEmpty(FirstImprintsize2) || FirstImprintsize2 != "" ){
					ImprintSizevalue=ImprintSizevalue.append(FirstImprintsize2).append(" ");
					 }
					  	break;
					  	
				case 79:	// Imprint size2 Unit
                     FirstImprintunit2=CommonUtility.getCellValueStrinOrInt(cell);
					
					
				    if(!StringUtils.isEmpty(FirstImprintunit2) || FirstImprintunit2 !=  "" ){
					FirstImprintunit2=MilestoneLookupData.Dimension1Units.get(FirstImprintunit2);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprintunit2).append(" ");
				    }

					    break;
					    
				case 80: // Imprint size2 Type
                    FirstImprinttype2=CommonUtility.getCellValueStrinOrInt(cell);
					
				    if(!StringUtils.isEmpty(FirstImprinttype2) || FirstImprinttype2 !=  "" ){

					FirstImprinttype2=MilestoneLookupData.Dimension1Type.get(FirstImprinttype2);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprinttype2).append(" ");
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
					
				    if(!StringUtils.isEmpty(SecondImprintsize1) || SecondImprintsize1 !=  "" ){

					ImprintSizevalue=ImprintSizevalue.append(SecondImprintsize1).append(" ");
				    }
					   	break;
					   	
				case 83:  // Second Imprintsize1 unit
					SecondImprintunit1=CommonUtility.getCellValueStrinOrInt(cell);
					
				    if(!StringUtils.isEmpty(SecondImprintunit1) || SecondImprintunit1 != "" ){
					SecondImprintunit1=MilestoneLookupData.Dimension1Units.get(SecondImprintunit1);
					ImprintSizevalue=ImprintSizevalue.append(SecondImprintunit1).append(" ");

					}
						break;
						
				case 84:  // Second Imprintsize1 type
					SecondImprinttype1=CommonUtility.getCellValueStrinOrInt(cell);
					
				    if(!StringUtils.isEmpty(SecondImprinttype1) || SecondImprinttype1 !=  "" ){
					SecondImprinttype1=MilestoneLookupData.Dimension1Type.get(SecondImprinttype1);
					ImprintSizevalue=ImprintSizevalue.append(SecondImprinttype1).append(" ").append("x");

					}
					  break;
					  
				case 85: // Second Imprintsize2
					SecondImprintsize2=CommonUtility.getCellValueStrinOrInt(cell);
					
				    if(!StringUtils.isEmpty(SecondImprintsize2) || SecondImprintsize2 !=  "" ){
				    ImprintSizevalue=ImprintSizevalue.append(SecondImprintsize2).append(" ");
				    
				    }

					break;
					
				case 86: //Second Imprintsize2 Unit
					SecondImprintunit2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprintunit2) || SecondImprintunit2 !=  "" ){
					SecondImprintunit2=MilestoneLookupData.Dimension1Units.get(SecondImprintunit2);
					ImprintSizevalue=ImprintSizevalue.append(SecondImprintunit2).append(" ");
					}

					break;
					
				case 87: // Second Imprintsize2 type
					SecondImprinttype2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprinttype2) || SecondImprinttype2 != "" ){
					SecondImprinttype2=MilestoneLookupData.Dimension1Type.get(SecondImprinttype2);
					ImprintSizevalue=ImprintSizevalue.append(SecondImprinttype2).append(" ");
					}
					  break;
					  
				case 88: // Second Imprint location
				String imprintLocation2 = cell.getStringCellValue();
					if(!imprintLocation2.isEmpty()){
						ImprintLocation locationObj2 = new ImprintLocation();
						locationObj2.setValue(imprintLocation2.trim());
						listImprintLocation.add(locationObj2);
					}
					break;
				case 89: // DecorationMethod
				   decorationMethod = cell.getStringCellValue();
				   if(!StringUtils.isEmpty(decorationMethod))
				   {
					   listOfImprintMethods = attrtiParserObj.getImprintMethodValues(decorationMethod);
				   }
					
					
					break; 
					 
				case 90: //NoDecoration
				
				    break;
				case 91: //NoDecorationOffered

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
					if(!madeInCountry.isEmpty()){
						List<Origin> listOfOrigin = attrtiParserObj.getOriginValues(madeInCountry);
						productConfigObj.setOrigins(listOfOrigin);
					}
					break;
					
					case 101:// AssembledInCountry
				
					break;
				case 102: //DecoratedInCountry
					String decoratedInCountry = cell.getStringCellValue();
					if(!decoratedInCountry.isEmpty()){
						decoratedInCountry = attrtiParserObj.getCountryCodeConvertName(decoratedInCountry);
 						productExcelObj.setAdditionalProductInfo("Decorated country is: " +decoratedInCountry);
					}
					break;
				case 103: //ComplianceList  -- No data
					break;
				case 104://ComplianceMemo  -- No data
					break;
				case 105: //ProdTimeLo
					   prodTimeLo = CommonUtility.getCellValueStrinOrInt(cell);
			
					break;
				case 106: //ProdTimeHi
					String prodTimeHi = CommonUtility.getCellValueStrinOrInt(cell);
					ProductionTime productionTime = new ProductionTime();
				

					if(prodTimeLo.equalsIgnoreCase(prodTimeHi))
					{
						productionTime.setBusinessDays(prodTimeHi);
						listOfProductionTime.add(productionTime);
					}
					else
					{
						String prodTimeTotal="";
						prodTimeTotal=prodTimeTotal.concat(prodTimeLo).concat("-").concat(prodTimeHi);
						productionTime.setBusinessDays(prodTimeTotal);
						listOfProductionTime.add(productionTime);
					}
					break;
					
			     case 107://RushProdTimeLo
					 break; 	 
				case 108://RushProdTimeH
					break;
					
				case 109://Packaging
				
					String pack  = cell.getStringCellValue();
					List<Packaging> listOfPackaging = attrtiParserObj.getPackageValues(pack);
					productConfigObj.setPackaging(listOfPackaging);
					break;
					
				case 110: //CartonL
					cartonL  = CommonUtility.getCellValueStrinOrInt(cell);
					
					break;
				case 111://CartonW
					cartonW  = CommonUtility.getCellValueStrinOrInt(cell);
					break;
	
				case 112://CartonH
					cartonH  = CommonUtility.getCellValueStrinOrInt(cell);
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
					String FOBValue=CommonUtility.getCellValueStrinOrInt(cell);
					String FOBLooup=null;
					List<String>fobLookupList = lookupServiceDataObj.getFobPoints(FOBLooup,environmentType);
					if(fobLookupList.contains(FOBValue))
					{
						fobPintObj.setName(FOBValue);
						FobPointsList.add(fobPintObj);
						productExcelObj.setFobPoints(FobPointsList);
					}
					
					break;
					
				case 117: //Comment
					
					break;
					
				case 118: //Verified
				   String verified=cell.getStringCellValue();
					if(verified.equalsIgnoreCase("True")){
					String priceConfimedThruString="2017-12-31T00:00:00";
					productExcelObj.setPriceConfirmedThru(priceConfimedThruString);
					}
					break;
			
			    case 119: //UpdateInventory
					
					break;
				case 120: //InventoryOnHand
					
					break;
				case 121: //InventoryOnHandAdd
					
					break;
				case 122: //InventoryMemo
					
				    break;
			
			}  // end inner while loop
					 
		}
			// set  product configuration objects
			
			 // end inner while loop
			
			
			
			productExcelObj.setPriceType("L");
			if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
				priceGrids = pricegridParserObj.getPriceGrids(listOfPrices.toString(), 
						         listOfQuantity.toString(), priceCode, "USD",
						         priceIncludesValue, true, quoteUponRequest, productName,"",priceGrids);	
			}
			
		  productConfigObj.setOptions(optionList);
		   
		  priceGrids = pricegridParserObj.getUpchargePriceGrid("1", "5", DiscountcodeUpcharge, "Imprint Option", 
					"false", "USD", "Line Charge", "Imprint Option Charge", "Other", new Integer(1), priceGrids);
		  
		  
		  productConfigObj.setImprintMethods(listOfImprintMethods);
		  if(decorationMethod.contains("or")){
			String ImprintMthoArr[] =decorationMethod.split("or");
			 for (String value : ImprintMthoArr) {
			
				 priceGrids = pricegridParserObj.getUpchargePriceGrid("1", serviceCharge, DiscountcodeUpcharge, "Imprint Method", 
							"false", "USD", value, "Set-up Charge", "Other", new Integer(1), priceGrids);
			  } 
						
		  }else
		  {
			  priceGrids = pricegridParserObj.getUpchargePriceGrid("1", serviceCharge, DiscountcodeUpcharge, "Imprint Method", 
						"false", "USD", decorationMethod, "Set-up Charge", "Other", new Integer(1), priceGrids); 
		  }
						
			
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() );		 
		}
		}
		workbook.close();
		
		 	
		 	if(!StringUtils.isEmpty(themeValue) ){
			productConfigObj.setThemes(themeList);
			}
		 	
		 	String DimensionRef=null;
			DimensionRef=dimensionValue.toString();
			if(!StringUtils.isEmpty(DimensionRef)){
			valuesList =attrtiParserObj.getValues(dimensionValue.toString(),
	                dimensionUnits.toString(), dimensionType.toString());
	        finalDimensionObj.setValues(valuesList);	
			size.setDimension(finalDimensionObj);
			productConfigObj.setSizes(size);
			}
			
			imprintSizeList=attrtiParserObj.getimprintsize(ImprintSizevalue);
		   
			if(FirstImprintsize1 != "" || FirstImprintsize2 !="" ||
					SecondImprintsize1 != "" ||	SecondImprintsize2 !=""){
			productConfigObj.setImprintSize(imprintSizeList);}
			
			productConfigObj.setProductionTime(listOfProductionTime);
			
			shipping = attrtiParserObj.getShippingEstimateValues(cartonL, cartonW,
                       cartonH, weightPerCarton, unitsPerCarton);
			if(!unitsPerCarton.contains("0")){
			productConfigObj.setShippingEstimates(shipping);
			}
			
			//productConfigObj.setImprintMethods(listOfImprintMethods);
			
			productExcelObj.setPriceGrids(priceGrids);
		 	productExcelObj.setProductConfigurations(productConfigObj);
		 	
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId);
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
	       
	       
	       
	       
	   
	         ImprintSizevalue = new StringBuilder();
			 listOfQuantity = new StringBuilder();
			 listOfPrices = new StringBuilder();
			 pricesPerUnit = new StringBuilder();
			 dimensionValue = new StringBuilder();
			 dimensionUnits = new StringBuilder();
			 dimensionType = new StringBuilder();
	       
			  priceGrids = new ArrayList<PriceGrid>();
			  imprintSizeList =new ArrayList<ImprintSize>();
			  listImprintLocation = new ArrayList<ImprintLocation>();
			  listOfImprintMethods = new ArrayList<ImprintMethod>();
			  listOfProductionTime = new ArrayList<ProductionTime>();
			  productKeywords = new ArrayList<String>();
			  themeList = new ArrayList<Theme>();
			  valuesList =new ArrayList<Values>();
			  FobPointsList = new ArrayList<FOBPoint>();
			  color = new ArrayList<Color>();
			  ImprintOptObj=new Option();
		      optionList = new ArrayList<Option>();
			  
			  finalDimensionObj=new Dimension();
			  size=new Size();
			  fobPintObj=new FOBPoint();
			  shipping = new ShippingEstimate();
			  productConfigObj = new ProductConfigurations();

	       
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


	public MilestoneColorParser getColorParserObj() {
		return colorParserObj;
	}

	public void setColorParserObj(MilestoneColorParser colorParserObj) {
		this.colorParserObj = colorParserObj;
	}

	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}

	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}

	public ProductAttributeParser getAttrtiParserObj() {
		return attrtiParserObj;
	}

	public void setAttrtiParserObj(ProductAttributeParser attrtiParserObj) {
		this.attrtiParserObj = attrtiParserObj;
	}

	public MilestonePriceGridParser getPricegridParserObj() {
		return pricegridParserObj;
	}

	public void setPricegridParserObj(MilestonePriceGridParser pricegridParserObj) {
		this.pricegridParserObj = pricegridParserObj;
	}


	
	
	
	
}
