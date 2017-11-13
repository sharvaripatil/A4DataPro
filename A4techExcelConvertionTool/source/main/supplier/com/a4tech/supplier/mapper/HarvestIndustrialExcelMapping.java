package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

import parser.goldstarcanada.GoldstarCanadaLookupData;
import parser.harvestIndustrail.HarvestColorParser;
import parser.harvestIndustrail.HarvestPriceGridParser;
import parser.harvestIndustrail.HarvestProductAttributeParser;

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
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.Shape;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.Values;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class HarvestIndustrialExcelMapping implements IExcelParser{

private static final Logger _LOGGER = Logger.getLogger(HarvestIndustrialExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private LookupServiceData lookupServiceDataObj;
	private HarvestColorParser harvestColorObj;
	private HarvestPriceGridParser harvestPriceGridObj;
	private HarvestProductAttributeParser harvestProductAttributeObj;

	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		Set<String> productXids = new HashSet<String>();
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		List<ImprintSize> imprintSizeList = new ArrayList<ImprintSize>();
		List<ImprintLocation> listImprintLocation = new ArrayList<ImprintLocation>();
		List<ImprintMethod> listOfImprintMethods = new ArrayList<ImprintMethod>();
		List<ProductionTime> listOfProductionTime = new ArrayList<ProductionTime>();
		List<String> productKeywords = new ArrayList<String>();
		List<Theme> themeList = new ArrayList<Theme>();
		 List<Theme> exstlist  = new ArrayList<Theme>();

		List<Values> valuesList = new ArrayList<Values>();
		List<FOBPoint> FobPointsList = new ArrayList<FOBPoint>();
		List<Color> color = new ArrayList<Color>();		
		List<Shape> shapelist = new ArrayList<Shape>();		


		Product productExcelObj = new Product();
		ProductConfigurations productConfigObj = new ProductConfigurations();
		Dimension finalDimensionObj = new Dimension();
		Size size = new Size();
		FOBPoint fobPintObj = new FOBPoint();
		ShippingEstimate shipping = new ShippingEstimate();
		String productName = null;
		String productId = null;
		String finalResult = null;
		RushTime rushTime = new RushTime();
		StringBuilder listOfQuantity = new StringBuilder();
		StringBuilder listOfPrices = new StringBuilder();
		StringBuilder pricesPerUnit = new StringBuilder();
		StringBuilder dimensionValue = new StringBuilder();
		StringBuilder dimensionUnits = new StringBuilder();
		StringBuilder dimensionType = new StringBuilder();
		StringBuilder ImprintSizevalue = new StringBuilder();

		try {

			_LOGGER.info("Total sheets in excel::"
					+ workbook.getNumberOfSheets());
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = sheet.iterator();
			_LOGGER.info("Started Processing Product");

			String themeValue = null;
			String priceCode = null;
			String quoteUponRequest = null;
			String quantity = null;
			String listPrice = null;
			String pricesUnit = null;
			String cartonL = null;
			String cartonW = null;
			String cartonH = null;
			String weightPerCarton = null;
			String unitsPerCarton = null;
			String decorationMethod = null;
			Product existingApiProduct = null;
			String priceIncludesValue = null;
			String prodTimeLo = null;
			Cell cell2Data = null;
			String rushProdTimeLo = null;
			String Unimprinted = null;
			String FirstImprintsize1=null;
			String FirstImprintunit1=null;
			String FirstImprinttype1=null;
			String FirstImprintsize2=null;
			String FirstImprintunit2=null;
			String FirstImprinttype2=null;
			String imprintLocation = null;
			String Summary=null;
			String asiProdNo =null;

			while (iterator.hasNext()) {

				try {
					Row nextRow = iterator.next();
					if (nextRow.getRowNum() < 1)
						continue;
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					if (productId != null) {
						productXids.add(productId);
					}
					boolean checkXid = false;

					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						String xid = null;
						int columnIndex = cell.getColumnIndex();
						cell2Data = nextRow.getCell(2);
						if (columnIndex + 1 == 1) {
							if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
								xid = cell.getStringCellValue();
							} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								xid = String.valueOf((int) cell
										.getNumericCellValue());
							} else {
								String ProdNo = CommonUtility
										.getCellValueStrinOrInt(cell2Data);
								xid = ProdNo;

							}
							checkXid = true;
						} else {
							checkXid = false;
						}
						if (checkXid) {
							if (!productXids.contains(xid)) {
								if (nextRow.getRowNum() != 1) {
									System.out
											.println("Java object converted to JSON String, written to file");

									/*if (!StringUtils.isEmpty(themeValue)) {
										productConfigObj.setThemes(themeList);
									}*/
									productConfigObj.setImprintLocation(listImprintLocation);
									String DimensionRef = null;
									DimensionRef = dimensionValue.toString();
									if (!StringUtils.isEmpty(DimensionRef)) {
										valuesList = harvestProductAttributeObj
												.getValues(dimensionValue
														.toString(),
														dimensionUnits
																.toString(),
														dimensionType
																.toString());

										finalDimensionObj.setValues(valuesList);
										size.setDimension(finalDimensionObj);
										productConfigObj.setSizes(size);
									}
								
								
									if(!FirstImprintsize1.contains("")){
										imprintSizeList=harvestProductAttributeObj.getimprintsize(ImprintSizevalue);
										 imprintSizeList.removeAll(Collections.singleton(null));
									productConfigObj.setImprintSize(imprintSizeList);
									}
									productConfigObj
											.setProductionTime(listOfProductionTime);
									productConfigObj.setRushTime(rushTime);

									shipping = harvestProductAttributeObj
											.getShippingEstimateValues(cartonL,
													cartonW, cartonH,
													weightPerCarton,
													unitsPerCarton);
									if (!StringUtils.isEmpty(unitsPerCarton)) {
										productConfigObj
												.setShippingEstimates(shipping);
									}
									productConfigObj
											.setImprintMethods(listOfImprintMethods);

									productExcelObj.setPriceGrids(priceGrids);
									productExcelObj
											.setProductConfigurations(productConfigObj);

									int num = postServiceImpl.postProduct(
											accessToken, productExcelObj,
											asiNumber, batchId);
									if (num == 1) {
										numOfProductsSuccess.add("1");
									} else if (num == 0) {
										numOfProductsFailure.add("0");
									} else {

									}
									_LOGGER.info("list size>>>>>>>"
											+ numOfProductsSuccess.size());
									_LOGGER.info("Failure list size>>>>>>>"
											+ numOfProductsFailure.size());

									listOfQuantity = new StringBuilder();
									listOfPrices = new StringBuilder();
									pricesPerUnit = new StringBuilder();
									dimensionValue = new StringBuilder();
									dimensionUnits = new StringBuilder();
									dimensionType = new StringBuilder();

									priceGrids = new ArrayList<PriceGrid>();
									exstlist = new ArrayList<Theme>();
									imprintSizeList = new ArrayList<ImprintSize>();
									listImprintLocation = new ArrayList<ImprintLocation>();
									listOfImprintMethods = new ArrayList<ImprintMethod>();
									listOfProductionTime = new ArrayList<ProductionTime>();
									productKeywords = new ArrayList<String>();
									themeList = new ArrayList<Theme>();
									valuesList = new ArrayList<Values>();
									FobPointsList = new ArrayList<FOBPoint>();
									color = new ArrayList<Color>();									
									rushTime = new RushTime();
									finalDimensionObj = new Dimension();
									size = new Size();
									fobPintObj = new FOBPoint();
									shapelist = new ArrayList<Shape>();	
									shipping = new ShippingEstimate();
									ImprintSizevalue = new StringBuilder();
									productConfigObj = new ProductConfigurations();
									
																	}
								if (!productXids.contains(xid)) {
									productXids.add(xid.trim());
								}
								existingApiProduct = postServiceImpl
										.getProduct(accessToken,
												xid = xid.replace("\t", ""));
								if (existingApiProduct == null) {
									_LOGGER.info("Existing Xid is not available,product treated as new product");
									productExcelObj = new Product();
								} else {
									productExcelObj = new Product();
								    productConfigObj=existingApiProduct.getProductConfigurations();
									List<Image> Img = existingApiProduct
											.getImages();
									productExcelObj.setImages(Img);
									

							    	 themeList=productConfigObj.getThemes();
							    	 if(themeList != null){
							    	 int i= themeList.size();
							    	 if(i > 5)
							    	 {
							    		 
							    		 exstlist = themeList.subList(0, 5);
    		 
							    	 }
							    	 productConfigObj.setThemes(exstlist);
							    	 } 
							    	 List<String>categoriesList=existingApiProduct.getCategories();
							    	 productExcelObj.setCategories(categoriesList);
							    	 
							    	 Summary=existingApiProduct.getSummary();
							    	 productExcelObj.setSummary(Summary);

							    	 List<Option> optionList = new ArrayList<Option>();
							    	 productConfigObj.setOptions(optionList);
							    	 
							    	 productConfigObj.setShapes(shapelist);
							    	 
								}
								// productExcelObj = new Product();
							}
						}

					
				switch (columnIndex + 1) {
				case 1:// ExternalProductID
					productExcelObj.setExternalProductId(xid.trim());

					break;
				case 2:// ProductID

					break;
				case 3:// ItemNum

					 asiProdNo = CommonUtility
							.getCellValueStrinOrInt(cell);
					productExcelObj.setAsiProdNo(asiProdNo);
					break;

				case 4:// Name
					productName = cell.getStringCellValue();
					productName=productName.replace("?","").replace("ã","").replace("¡", "").replace(":", "");
					if(productName.contains(asiProdNo))
					{
						productName=productName.replace(asiProdNo, "");			
					}		
					int len = productName.length();
					if (len > 60) {
						String strTemp = productName.substring(0, 60);
						int lenTemp = strTemp
								.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
						productName = (String) strTemp.subSequence(0,
								lenTemp);
					}
					productExcelObj.setName(productName);

					break;

				case 5:// CatYear

					break;

				case 6: // ExpirationDate
					break;

				case 7:// Discontinued

					break;

				case 8: // Cat1Name

					break;

				case 9: // Cat2Name

					break;

				case 10: // Page1

					break;

				case 11: // Page2

					break;

				case 12: // Description

					String description = cell.getStringCellValue();
					description=description.replace("?","").replace("ã","").replace("¡", "").replace(":", "");
					if (!StringUtils.isEmpty(description)) {
						productExcelObj.setDescription(description);
					} else {
						productExcelObj
								.setDescription(productName);
					}
					if (StringUtils.isEmpty(Summary) || Summary.contains("null")) {
						String Newsummary=null;
						String summayArr[]=description.split("\\.");
						if(summayArr[0].length()>130)
						{
						 Newsummary=summayArr[0].substring(0, 130);
						}else {
							Newsummary=	summayArr[0];
						}
						productExcelObj.setSummary(Newsummary);
					}
					
					

					break;

				case 13: // Keywords

					String productKeyword = cell.getStringCellValue();
					productKeyword=productKeyword.replace("®", "R").replace(" ’", " '");
					String KeywordArr[] = productKeyword.toLowerCase().split(",");
                   if(!productKeywords.contains(productKeyword.toLowerCase())){
				 	for (String string : KeywordArr) {
					if(!(string.length()>30)){
						productKeywords.add(string);
					}if(productKeywords.size()==30){
							break;
					}}
					productExcelObj.setProductKeywords(productKeywords);
				    }

					break;

				case 14: // Colors

					String colorValue = cell.getStringCellValue();
					if (!StringUtils.isEmpty(colorValue)) {
						color = harvestColorObj
								.getColorCriteria(colorValue);
						productConfigObj.setColors(color);
					}

					break;

				case 15:// Themes

					themeValue = cell.getStringCellValue();
					if(exstlist == null){
					themeValue=themeValue.replace("Outdoor,", "").replace("Summer", "");
					if (StringUtils.isEmpty(themeList) || themeList.contains("null")) {
					themeList = new ArrayList<Theme>();
			    	Theme themeObj = new Theme();;
					String Value = "";
					String[] themes = CommonUtility.getValuesOfArray(
							themeValue,
							ApplicationConstants.CONST_DELIMITER_COMMA);
					List<String> themeLookupList = lookupServiceDataObj
							.getTheme(Value);

					for (String themeName : themes) {
						themeName=themeName.toUpperCase();
						if (themeLookupList.contains(themeName) && themeList.size() <5 ) {
							themeObj = new Theme();
							themeObj.setName(themeName);
							themeList.add(themeObj);
						}

					}
					}
					}
					break;
				case 16: // Dimension1

					String dimensionValue1 = CommonUtility
							.getCellValueStrinOrInt(cell);
					dimensionValue1 = dimensionValue1.replace("\"", "");
					if (dimensionValue1 != null
							&& !dimensionValue1.isEmpty()) {
						dimensionValue
								.append(dimensionValue1)
								.append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}

					break;

				case 17: // Dimension1Units

					String dimensionUnits1 = CommonUtility
							.getCellValueStrinOrInt(cell);
					dimensionUnits1 = dimensionUnits1.replace("\"", "");

					if (!dimensionUnits1.contains("0")) {
						dimensionUnits
								.append(dimensionUnits1.trim())
								.append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}

					break;

				case 18: // Dimension1Type

					String dimensionType1 = CommonUtility
							.getCellValueStrinOrInt(cell);
					dimensionType1 = dimensionType1.replace("\"", "");

					if (!dimensionType1.contains("0")) {
						dimensionType
								.append(dimensionType1)
								.append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}

					break;

				case 19: // Dimension2
					String dimensionValue2 = CommonUtility
							.getCellValueStrinOrInt(cell);
					dimensionValue2 = dimensionValue2.replace("\"", "");

					if (dimensionValue2 != null
							&& !dimensionValue2.isEmpty()) {
						dimensionValue
								.append(dimensionValue2)
								.append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}

					break;

				case 20: // Dimension2Units

					String dimensionUnits2 = CommonUtility
							.getCellValueStrinOrInt(cell);
					dimensionUnits2 = dimensionUnits2.replace("\"", "");

					if (!dimensionUnits2.contains("0")) {
						dimensionUnits
								.append(dimensionUnits2.trim())
								.append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}

					break;

				case 21: // Dimension2Type

					String dimensionType2 = CommonUtility
							.getCellValueStrinOrInt(cell);
					dimensionType2 = dimensionType2.replace("\"", "");

					if (!dimensionType2.contains("0")) {
						dimensionType
								.append(dimensionType2)
								.append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}

					break;

				case 22: // Dimension3
					String dimensionValue3 = CommonUtility
							.getCellValueStrinOrInt(cell);
					dimensionValue3 = dimensionValue3.replace("\"", "");

					if (dimensionValue3 != null
							&& !dimensionValue3.isEmpty()) {
						dimensionValue
								.append(dimensionValue3)
								.append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					} else {
						dimensionValue = dimensionValue.append("");
					}

					break;

				case 23: // Dimension3Units

					String dimensionUnits3 = CommonUtility
							.getCellValueStrinOrInt(cell);
					dimensionUnits3 = dimensionUnits3.replace("\"", "");

					if (!dimensionUnits3.contains("0")) {
						dimensionUnits
								.append(dimensionUnits3.trim())
								.append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					} else {
						dimensionUnits = dimensionUnits.append("");
					}

					break;

				case 24: // Dimension3Type
					String dimensionType3 = CommonUtility
							.getCellValueStrinOrInt(cell);
					dimensionType3 = dimensionType3.replace("\"", "");

					if (!dimensionType3.contains("0")) {
						dimensionType
								.append(dimensionType3)
								.append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					} else {
						dimensionType = dimensionType.append("");
					}

					break;
				case 25: // quantity
				case 26:
				case 27:
				case 28:
				case 29:

				case 30:
				quantity = CommonUtility
							.getCellValueStrinOrInt(cell);
					listOfQuantity
							.append(quantity)
							.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

					break;
				case 31: // prices --list price
				case 32:
				case 33:
				case 34:
				case 35:

				case 36: // price code -- discount
				listPrice = CommonUtility
							.getCellValueStrinOrInt(cell);
					listOfPrices
							.append(listPrice)
							.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

					break;

				case 37: // pricesPerUnit
					priceCode = cell.getStringCellValue();
					break;
				case 38:
				case 39:
				case 40:
				case 41:
				case 42:

				case 43:
					pricesUnit = CommonUtility
							.getCellValueStrinOrInt(cell);
					pricesPerUnit
							.append(pricesUnit)
							.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

					break;

				case 44: //
					quoteUponRequest = cell.getStringCellValue();
					break;

				case 45: // priceIncludeClr

					break;
				case 46: // PriceIncludeSide

				/*
					  ImprintOption=cell.getStringCellValue();
					  ImprintOptObj =twintechProductAttributeObj.getImprintOption2
					  (ImprintOption);
					  optionList.add(ImprintOptObj);*/
					

					break;

				case 47: // PriceIncludeLoc
					break;
				case 48:// SetupChg
					break;

				case 49:// SetupChgCode

					break;
				case 50:// ScreenChg

					break;
				case 51:// ScreenChgCode

					break;
				case 52:// PlateChg

					break;
				case 53:// PlateChgCode

					break;
				case 54:// DieChg

					break;
				case 55:// DieChgCode

					break;
				case 56:// ToolingChg

					break;
				case 57:// ToolingChgCode

					break;
				case 58:// RepeatChg

					break;
				case 59:// RepeatChgCode

					break;
				case 60:// AddClrChg

					break;
				case 61:// AddClrChgCode

					break;
				case 62:// AddClrRunChg1

					break;
				case 63:// AddClrRunChg2

					break;
				case 64:// AddClrRunChg3

					break;
				case 65:// AddClrRunChg4

					break;
				case 66:// AddClrRunChg5

					break;
				case 67:// AddClrRunChg6

					break;
				case 68:// AddClrRunChgCode

					break;
				case 69:// IsRecyclable

					break;
				case 70:// IsEnvironmentallyFriendly
					String IsEnvironmentallyFriendly = cell
							.getStringCellValue();
					if(exstlist == null){
					if (IsEnvironmentallyFriendly
							.equalsIgnoreCase("true") && themeList.size() <5 ) {
						Theme themeObj1 = new Theme();

						themeObj1.setName("Eco Friendly");

						themeList.add(themeObj1);
					}
					productConfigObj.setThemes(themeList);
					}
		
					break;
				case 71:// IsNewProd

					break;
				case 72:// NotSuitable

					break;
				case 73:// Exclusive

					break;
				case 74:// IsFood

					break;
				case 75: // IsClothing

					break;

				case 76: // Imprint size1
					 FirstImprintsize1=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(FirstImprintsize1) || FirstImprintsize1 !=  null ){
					 ImprintSizevalue=ImprintSizevalue.append(FirstImprintsize1).append(" ");
					
					 }
					    break;
					    
				case 77: //// Imprint size1 unit
					FirstImprintunit1=CommonUtility.getCellValueStrinOrInt(cell);
					
					 if(!StringUtils.isEmpty(FirstImprintunit1) || FirstImprintunit1 !=  null ){
					FirstImprintunit1=GoldstarCanadaLookupData.Dimension1Units.get(FirstImprintunit1);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprintunit1).append(" ");
					 }	 
					   	break;
					   	
				case 78:   // Imprint size1 Type
					FirstImprinttype1=CommonUtility.getCellValueStrinOrInt(cell);
					
				   if(!StringUtils.isEmpty(FirstImprinttype1) || FirstImprinttype1 !=  null ){
					FirstImprinttype1=GoldstarCanadaLookupData.Dimension1Type.get(FirstImprinttype1);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprinttype1).append(" ").append("x");
				   }
						break;
						
				  
				case 79: // // Imprint size2
					FirstImprintsize2=CommonUtility.getCellValueStrinOrInt(cell);
					
					 if(!StringUtils.isEmpty(FirstImprintsize2) || FirstImprinttype1 != null ){
					ImprintSizevalue=ImprintSizevalue.append(FirstImprintsize2).append(" ");
					 }

					  	break;
					  	
				case 80:	// Imprint size2 Unit
					FirstImprintunit2=CommonUtility.getCellValueStrinOrInt(cell);
					
					
				    if(!StringUtils.isEmpty(FirstImprintunit2) || FirstImprintunit2 !=  null ){
					FirstImprintunit2=GoldstarCanadaLookupData.Dimension1Units.get(FirstImprintunit2);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprintunit2).append(" ");
				    }

					
					    break;
					    
				case 81: // Imprint size2 Type
					FirstImprinttype2=CommonUtility.getCellValueStrinOrInt(cell);
					
				    if(!StringUtils.isEmpty(FirstImprinttype2) || FirstImprinttype2 !=  null ){

					FirstImprinttype2=GoldstarCanadaLookupData.Dimension1Type.get(FirstImprinttype2);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprinttype2).append(" ");
				    }

				   
					
					break;
					  	
				case 82:  // Imprint location
					
					 imprintLocation = cell.getStringCellValue();
					if(!imprintLocation.isEmpty()){
						ImprintLocation locationObj = new ImprintLocation();
						locationObj.setValue(imprintLocation);
						listImprintLocation.add(locationObj);
					}
					 break;
				

				case 83: // SecondImprintSize1
				
					break;

				case 84: // SecondImprintSize1Units
			

					break;

				case 85: // SecondImprintSize1Type
					

					break;

				case 86: // SecondImprintSize2
				
					break;

				case 87: // SecondImprintSize2Units
				
					break;

				case 88: // SecondImprintSize2Type
				
					break;
				case 89: // SecondImprintLoc
				/*	String imprintLocation2 = cell.getStringCellValue();
					if (!imprintLocation2.isEmpty()) {
						ImprintLocation locationObj2 = new ImprintLocation();
						locationObj2.setValue(imprintLocation2.trim());
						listImprintLocation.add(locationObj2);
					}*/

					break;

				case 90: // DecorationMethod
					decorationMethod = cell.getStringCellValue();
					if (!StringUtils.isEmpty(decorationMethod)) {
						listOfImprintMethods = harvestProductAttributeObj
								.getImprintMethodValues(decorationMethod);
					}

					break;
				case 91: // NoDecoration
					Unimprinted =cell.getStringCellValue();

					break;
				case 92: // NoDecorationOffered
					String Unimprinted1=cell.getStringCellValue();
					if(Unimprinted.contains("True") || Unimprinted1.contains("True")	)
					{
						ImprintMethod imprintObj=new ImprintMethod();		
						imprintObj.setType("Unimprinted");
						imprintObj.setAlias("Unimprinted");
						listOfImprintMethods.add(imprintObj);	
					}
					
					break;
				case 93: // NewPictureURL

					break;
				case 94: // NewPictureFile

					break;
				case 95: // ErasePicture

					break;
				case 96: // NewBlankPictureURL

					break;
				case 97:// NewBlankPictureFile

					break;
				case 98: // EraseBlankPicture

					break;
				case 99: // PicExists

					break;
				case 100: // NotPictured

					break;

				case 101:// MadeInCountry
					String madeInCountry = cell.getStringCellValue();
					if (!madeInCountry.isEmpty()) {
						List<Origin> listOfOrigin = harvestProductAttributeObj
								.getOriginValues(madeInCountry);
						productConfigObj.setOrigins(listOfOrigin);
					}

					break;
				case 102: // AssembledInCountry
					
					String AssembledCountry = cell
					.getStringCellValue();
			   if (!AssembledCountry.isEmpty()) {
				   AssembledCountry = harvestProductAttributeObj
						.getCountryCodeConvertName(AssembledCountry);
				productExcelObj.setAdditionalProductInfo("Assembled country is: "
								+ AssembledCountry);
		        	}

					break;
				case 103: // DecoratedInCountry
					String decoratedInCountry = cell
					.getStringCellValue();
			if (!decoratedInCountry.isEmpty()) {
				decoratedInCountry = harvestProductAttributeObj
						.getCountryCodeConvertName(decoratedInCountry);
				productExcelObj.setAdditionalImprintInfo("Decorated country is: "
								+ decoratedInCountry);
						
			}
				
					break;
				case 104:// ComplianceList

					break;
				case 105: // ComplianceMemo

					break;
				case 106: // ProdTimeLo
					prodTimeLo = CommonUtility
							.getCellValueStrinOrInt(cell);

					break;

				case 107:// ProdTimeHi
					String prodTimeHi = CommonUtility
							.getCellValueStrinOrInt(cell);
					ProductionTime productionTime = new ProductionTime();
					if (prodTimeLo.equalsIgnoreCase(prodTimeHi)) {
						productionTime.setBusinessDays(prodTimeHi);
						listOfProductionTime.add(productionTime);
					} else {
						String prodTimeTotal = "";
						prodTimeTotal = prodTimeTotal
								.concat(prodTimeLo).concat("-")
								.concat(prodTimeHi);
						productionTime.setBusinessDays(prodTimeTotal);
						listOfProductionTime.add(productionTime);
					}
					break;
				case 108:// RushProdTimeLo
					rushProdTimeLo = cell.getStringCellValue();

					break;

				case 109:// RushProdTimeHi
					String rushProdTimeH = cell.getStringCellValue();
					if (!rushProdTimeH
							.equals(ApplicationConstants.CONST_STRING_ZERO)) {
						rushTime = harvestProductAttributeObj
								.getRushTimeValues(rushProdTimeLo,
										rushProdTimeH);
					}

					break;

				case 110: // Packaging
					String pack = cell.getStringCellValue();
					List<Packaging> listOfPackaging = harvestProductAttributeObj
							.getPackageValues(pack);
					productConfigObj.setPackaging(listOfPackaging);

					break;
				case 111:// CartonL
					cartonL = CommonUtility
							.getCellValueStrinOrInt(cell);

					break;

				case 112:// CartonW
					cartonW = CommonUtility
							.getCellValueStrinOrInt(cell);

					break;
				case 113: // CartonH
					cartonH = CommonUtility
							.getCellValueStrinOrInt(cell);

					break;
				case 114: // WeightPerCarton
					weightPerCarton = CommonUtility
							.getCellValueStrinOrInt(cell);

					break;

				case 115: // UnitsPerCarton
					unitsPerCarton = CommonUtility
							.getCellValueStrinOrInt(cell);
					break;

				case 116: // ShipPointCountry

					break;

				case 117: // ShipPointZip
					String FOBValue = CommonUtility
							.getCellValueStrinOrInt(cell);
					// List<String>fobLookupList =
					// lookupServiceDataObj.getFobPoints(FOBLooup);
					if (FOBValue.contains("90802")) {
						fobPintObj
								.setName("Los Angeles, CA 90802 USA");
						FobPointsList.add(fobPintObj);
						productExcelObj.setFobPoints(FobPointsList);
					}

					break;

				case 118: // Comment

					break;

				case 119: // Verified
				/*	String verified = cell.getStringCellValue();
					if (verified.equalsIgnoreCase("True")) {
						String priceConfimedThruString = "2016-12-31T00:00:00";
						productExcelObj
								.setPriceConfirmedThru(priceConfimedThruString);
					}
*/
					break;
				case 120: // UpdateInventory

					break;
				case 121: // InventoryOnHand

					break;
				case 122: // InventoryOnHandAdd

					break;

				case 123: // InventoryMemo

					break;

			
			}  // end inner while loop
					 
		}
			// set  product configuration objects
			
			 // end inner while loop
			
			//String QuoteRequest=Boolean.toString(quoteUponRequest);
					productExcelObj.setPriceType("L");
					if (listOfPrices != null
							&& !listOfPrices.toString().isEmpty()) {
						priceGrids = harvestPriceGridObj.getPriceGrids(
								listOfPrices.toString(),
								listOfQuantity.toString(), priceCode, "USD",
								priceIncludesValue, true, quoteUponRequest,
								productName, "", priceGrids);
					}
					else
					{
						
						priceGrids = harvestPriceGridObj.getPriceGrids(
								listOfPrices.toString(),
								listOfQuantity.toString(), priceCode, "USD",
								priceIncludesValue, true, quoteUponRequest,
								productName, "", priceGrids);
						
						
					}
					productConfigObj.setImprintMethods(listOfImprintMethods);

					

				} catch (Exception e) {
					_LOGGER.error("Error while Processing ProductId and cause :"
							+ productExcelObj.getExternalProductId()
							+ " "
							+ e.getMessage());
				}
			}
			workbook.close();

			/*if (!StringUtils.isEmpty(themeValue)) {
				productConfigObj.setThemes(themeList);
			}*/
			productConfigObj.setImprintLocation(listImprintLocation);
			String DimensionRef = null;
			DimensionRef = dimensionValue.toString();
			if (!StringUtils.isEmpty(DimensionRef)) {
				valuesList = harvestProductAttributeObj.getValues(
						dimensionValue.toString(), dimensionUnits.toString(),
						dimensionType.toString());
				finalDimensionObj.setValues(valuesList);
				size.setDimension(finalDimensionObj);
				productConfigObj.setSizes(size);
			}
			
			 if(!FirstImprintsize1.contains("")){
				 imprintSizeList=harvestProductAttributeObj.getimprintsize(ImprintSizevalue);
				 imprintSizeList.removeAll(Collections.singleton(null));
			productConfigObj.setImprintSize(imprintSizeList);
			}
			productConfigObj.setProductionTime(listOfProductionTime);
			productConfigObj.setRushTime(rushTime);

			shipping = harvestProductAttributeObj.getShippingEstimateValues(
					cartonL, cartonW, cartonH, weightPerCarton, unitsPerCarton);
			if (!StringUtils.isEmpty(unitsPerCarton)) {
				productConfigObj.setShippingEstimates(shipping);
			}

			productExcelObj.setPriceGrids(priceGrids);
			productExcelObj.setProductConfigurations(productConfigObj);

			int num = postServiceImpl.postProduct(accessToken, productExcelObj,
					asiNumber, batchId);
			if (num == 1) {
				numOfProductsSuccess.add("1");
			} else if (num == 0) {
				numOfProductsFailure.add("0");
			} else {

			}
			_LOGGER.info("list size>>>>>>" + numOfProductsSuccess.size());
			_LOGGER.info("Failure list size>>>>>>"
					+ numOfProductsFailure.size());
			finalResult = numOfProductsSuccess.size() + ","
					+ numOfProductsFailure.size();
			productDaoObj.saveErrorLog(asiNumber, batchId);

			listOfQuantity = new StringBuilder();
			listOfPrices = new StringBuilder();
			pricesPerUnit = new StringBuilder();
			dimensionValue = new StringBuilder();
			dimensionUnits = new StringBuilder();
			dimensionType = new StringBuilder();
			priceGrids = new ArrayList<PriceGrid>();
			imprintSizeList = new ArrayList<ImprintSize>();
			listImprintLocation = new ArrayList<ImprintLocation>();
			listOfImprintMethods = new ArrayList<ImprintMethod>();
			listOfProductionTime = new ArrayList<ProductionTime>();
			productKeywords = new ArrayList<String>();
			themeList = new ArrayList<Theme>();
			valuesList = new ArrayList<Values>();
			FobPointsList = new ArrayList<FOBPoint>();
			color = new ArrayList<Color>();
			finalDimensionObj = new Dimension();
			size = new Size();
			fobPintObj = new FOBPoint();
			shipping = new ShippingEstimate();
			 shapelist = new ArrayList<Shape>();	
			productConfigObj = new ProductConfigurations();
			rushTime = new RushTime();
			ImprintSizevalue = new StringBuilder();
			exstlist = new ArrayList<Theme>();

			return finalResult;
		} catch (Exception e) {
			_LOGGER.error("Error while Processing excel sheet "
					+ e.getMessage());
			return finalResult;
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet "
						+ e.getMessage());

			}
			_LOGGER.info("Complted processing of excel sheet ");
			_LOGGER.info("Total no of product:" + numOfProductsSuccess.size());
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

	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}


	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}


	public HarvestColorParser getHarvestColorObj() {
		return harvestColorObj;
	}

	public void setHarvestColorObj(HarvestColorParser harvestColorObj) {
		this.harvestColorObj = harvestColorObj;
	}

	public HarvestPriceGridParser getHarvestPriceGridObj() {
		return harvestPriceGridObj;
	}

	public void setHarvestPriceGridObj(HarvestPriceGridParser harvestPriceGridObj) {
		this.harvestPriceGridObj = harvestPriceGridObj;
	}

	public HarvestProductAttributeParser getHarvestProductAttributeObj() {
		return harvestProductAttributeObj;
	}

	public void setHarvestProductAttributeObj(
			HarvestProductAttributeParser harvestProductAttributeObj) {
		this.harvestProductAttributeObj = harvestProductAttributeObj;
	}

	
}
