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

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.AdditionalColor;
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
import com.a4tech.product.service.imple.PostServiceImpl;
import com.a4tech.supplier.service.ISupplierParser;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.goldstarcanada.GoldstarCanadaLookupData;
import parser.harvestIndustrail.HarvestColorParser;
import parser.harvestIndustrail.HarvestPriceGridParser;
import parser.harvestIndustrail.HarvestProductAttributeParser;

public class TotesFactoryExcelMapping implements ISupplierParser{

private static final Logger _LOGGER = Logger.getLogger(TotesFactoryExcelMapping.class);
	
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
		 List<AdditionalColor>additionalcolorList= new ArrayList<>();

		
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
		StringBuilder Priceinclude = new StringBuilder();
		StringBuilder Addcolorcharge = new StringBuilder();


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
			String Setupcharge="";
			String Screencharge="";
			String Repeatcharge="";
			String Additionalcolor="";
			String Setupchargecode="";
			String Screenchargecode="";
			String Repeatchargecode="";
			String Additionalcolorcode="";
			String AddClrRunChg1="";
			String AddClrRunChg2="";
			String AddClrRunChg3="";
			String AddClrRunChg4="";
			String AddClrRunChg5="";
			String AddClrRunChg6="";

			while (iterator.hasNext()) {

				try {
					Row nextRow = iterator.next();
					if (nextRow.getRowNum() < 7)//Totes Factory
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
								if (nextRow.getRowNum() != 7) { //totes factory
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
								
								
									if(!FirstImprintsize1.contains("10")){
									//	imprintSizeList=harvestProductAttributeObj.getimprintsize(ImprintSizevalue);
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
											asiNumber, batchId, environmentType);
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
									 Priceinclude = new StringBuilder();
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
									additionalcolorList = new ArrayList<>();
									Addcolorcharge = new StringBuilder();
									productConfigObj = new ProductConfigurations();
									
																	}
								if (!productXids.contains(xid)) {
									productXids.add(xid.trim());
								}
								existingApiProduct = postServiceImpl
										.getProduct(accessToken,
												xid = xid.replace("\t", ""), environmentType);
								if (existingApiProduct == null) {
									_LOGGER.info("Existing Xid is not available,product treated as new product");
									productExcelObj = new Product();
								} else {
									productExcelObj = new Product();
								//    productConfigObj=existingApiProduct.getProductConfigurations();
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
							    	 
							    	/* Summary=existingApiProduct.getSummary();
							    	 productExcelObj.setSummary(Summary);*/

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
						productName = (String) strTemp.subSequence(0,lenTemp);
					}
					productExcelObj.setName(productName);
				
					break;

				case 5:// CatYear

					break;

				case 6: // ExpirationDate
					/*String ConfDate=cell.getStringCellValue();
					productExcelObj.setPriceConfirmedThru(ConfDate);*/
					
					
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
			//		if (StringUtils.isEmpty(Summary) || Summary.contains("null")) {
						String Newsummary="";
						String summayArr[]=description.split("\\.");
						if(summayArr[0].length()>130)
						{
						 Newsummary=productName;
						}else {
							Newsummary=	summayArr[0];
						}
						productExcelObj.setSummary(Newsummary);
				//	}

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
					//if(exstlist == null){
					//themeValue=themeValue.replace("Outdoor,", "").replace("Summer", "");
				//	if (StringUtils.isEmpty(themeList) || themeList.contains("null")) {
					themeList = new ArrayList<Theme>();
			    	Theme themeObj = new Theme();;
					String Value = "";
					String[] themes = CommonUtility.getValuesOfArray(
							themeValue,
							ApplicationConstants.CONST_DELIMITER_COMMA);
					List<String> themeLookupList = lookupServiceDataObj
							.getTheme(Value);

					for (String themeName : themes) {
						themeName=themeName.toUpperCase().trim();
						if (themeLookupList.contains( themeName) && themeList.size() <5 ) {
							themeObj = new Theme();
							themeObj.setName(themeName);
							themeList.add(themeObj);
						}

					}
				//	}
				//	}
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

					if (!dimensionUnits1.equals("0")) {
						dimensionUnits
								.append(dimensionUnits1.trim())
								.append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}

					break;

				case 18: // Dimension1Type

					String dimensionType1 = CommonUtility
							.getCellValueStrinOrInt(cell);
					dimensionType1 = dimensionType1.replace("\"", "");

					if (!dimensionType1.equals("0")) {
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

					if (!dimensionUnits2.equals("0")) {
						dimensionUnits
								.append(dimensionUnits2.trim())
								.append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}

					break;

				case 21: // Dimension2Type

					String dimensionType2 = CommonUtility
							.getCellValueStrinOrInt(cell);
					dimensionType2 = dimensionType2.replace("\"", "");

					if (!dimensionType2.equals("0")) {
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

					if (!dimensionUnits3.equals("0")) {
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

					if (!dimensionType3.equals("0")) {
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
					String Priceincludeclr=cell.getStringCellValue();
					if(!StringUtils.isEmpty(Priceincludeclr)){
						
						Priceinclude=Priceinclude.append(Priceincludeclr);	
						
					}
					break;
				case 46: // PriceIncludeSide
					String Priceincludside=cell.getStringCellValue();
					if(!StringUtils.isEmpty(Priceincludside)){
						Priceinclude=Priceinclude.append(",").append(Priceincludside);	

						
					}

					break;

				case 47: // PriceIncludeLoc
					String PriceincludLoc=cell.getStringCellValue();
					if(!StringUtils.isEmpty(PriceincludLoc)){
						
						Priceinclude=Priceinclude.append(",").append(PriceincludLoc);	

					}
					break;
				case 48:// SetupChg
					 Setupcharge=cell.getStringCellValue();
					break;

				case 49:// SetupChgCode
					 Setupchargecode=cell.getStringCellValue();


					break;
				case 50:// ScreenChg
					 Screencharge=cell.getStringCellValue();


					break;
				case 51:// ScreenChgCode
					 Screenchargecode=cell.getStringCellValue();


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
					Repeatcharge=cell.getStringCellValue();
					break;
				case 59:// RepeatChgCode
					Repeatchargecode=cell.getStringCellValue();

					break;
				case 60:// AddClrChg
					Additionalcolor=cell.getStringCellValue();

					break;
				case 61:// AddClrChgCode
					Additionalcolorcode=cell.getStringCellValue();


					break;
				case 62:// AddClrRunChg1
					AddClrRunChg1=CommonUtility.getCellValueStrinOrDecimal(cell);

					break;
				case 63:// AddClrRunChg2
					AddClrRunChg2=CommonUtility.getCellValueStrinOrDecimal(cell);


					break;
				case 64:// AddClrRunChg3
					AddClrRunChg3=CommonUtility.getCellValueStrinOrDecimal(cell);


					break;
				case 65:// AddClrRunChg4
					AddClrRunChg4=CommonUtility.getCellValueStrinOrDecimal(cell);

					break;
				case 66:// AddClrRunChg5
					AddClrRunChg5=CommonUtility.getCellValueStrinOrDecimal(cell);
					

				case 67:// AddClrRunChg6
					AddClrRunChg6=CommonUtility.getCellValueStrinOrDecimal(cell);

					break;
				case 68:// AddClrRunChgCode

					break;
				case 69:// IsRecyclable

					break;
				case 70:// IsEnvironmentallyFriendly
					String IsEnvironmentallyFriendly = cell
							.getStringCellValue();
					//if(exstlist == null){
					if (IsEnvironmentallyFriendly
							.equalsIgnoreCase("true") && themeList.size() <5 ) {
						Theme themeObj1 = new Theme();

						themeObj1.setName("ECO & ENVIRONMENTALLY FRIENDLY");

						themeList.add(themeObj1);
					}
					productConfigObj.setThemes(themeList);
					//}
		
					break;
				case 71:// IsNewProd

					break;
				case 72:// NotSuitable

					break;
				case 73:// Exclusive

					break;
					
				case 74://Hazardous	
					
					break;
				
				case 75: // official licensed

					break;
			
				case 76:// IsFood

					break;
				case 77: // IsClothing

					break;

				case 78: // Imprint size1
					 FirstImprintsize1=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(FirstImprintsize1) || FirstImprintsize1 !=  null ){
					 ImprintSizevalue=ImprintSizevalue.append(FirstImprintsize1).append(" ");
					
					 }
					    break;
					    
				case 79: //// Imprint size1 unit
					FirstImprintunit1=CommonUtility.getCellValueStrinOrInt(cell);
					
					 if(!StringUtils.isEmpty(FirstImprintunit1) || FirstImprintunit1 !=  null ){
					FirstImprintunit1=GoldstarCanadaLookupData.Dimension1Units.get(FirstImprintunit1);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprintunit1).append(" ");
					 }	 
					   	break;
					   	
				case 80:   // Imprint size1 Type
					FirstImprinttype1=CommonUtility.getCellValueStrinOrInt(cell);
					
				   if(!StringUtils.isEmpty(FirstImprinttype1) || FirstImprinttype1 !=  null ){
					FirstImprinttype1=GoldstarCanadaLookupData.Dimension1Type.get(FirstImprinttype1);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprinttype1).append(" ").append("x");
				   }
						break;
						
				  
				case 81: // // Imprint size2
					FirstImprintsize2=CommonUtility.getCellValueStrinOrInt(cell);
					
					 if(!StringUtils.isEmpty(FirstImprintsize2) || FirstImprinttype1 != null ){
					ImprintSizevalue=ImprintSizevalue.append(FirstImprintsize2).append(" ");
					 }

					  	break;
					  	
				case 82:	// Imprint size2 Unit
					FirstImprintunit2=CommonUtility.getCellValueStrinOrInt(cell);
					
					
				    if(!StringUtils.isEmpty(FirstImprintunit2) || FirstImprintunit2 !=  null ){
					FirstImprintunit2=GoldstarCanadaLookupData.Dimension1Units.get(FirstImprintunit2);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprintunit2).append(" ");
				    }

					
					    break;
					    
				case 83: // Imprint size2 Type
					FirstImprinttype2=CommonUtility.getCellValueStrinOrInt(cell);
					
				    if(!StringUtils.isEmpty(FirstImprinttype2) || FirstImprinttype2 !=  null ){

					FirstImprinttype2=GoldstarCanadaLookupData.Dimension1Type.get(FirstImprinttype2);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprinttype2).append(" ");
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
				

				case 85: // SecondImprintSize1

					break;

				case 86: // SecondImprintSize1Units

					break;

				case 87: // SecondImprintSize1Type

					break;

				case 88: // SecondImprintSize2

					break;

				case 89: // SecondImprintSize2Units
  

				case 90: // SecondImprintSize2Type
	    
					break;
				case 91: // SecondImprintLoc
					String imprintLocation2 = cell.getStringCellValue();
					if (!imprintLocation2.isEmpty()) {
						ImprintLocation locationObj2 = new ImprintLocation();
						locationObj2.setValue(imprintLocation2.trim());
						listImprintLocation.add(locationObj2);
					}

					break;

				case 92: // DecorationMethod
					decorationMethod = cell.getStringCellValue();
					if (!StringUtils.isEmpty(decorationMethod)) {
						listOfImprintMethods = harvestProductAttributeObj
								.getImprintMethodValues(decorationMethod);
					}

					break;
				case 93: // NoDecoration
					Unimprinted =cell.getStringCellValue();

					break;
				case 94: // NoDecorationOffered
					String Unimprinted1=cell.getStringCellValue();
					if(Unimprinted.contains("True") || Unimprinted1.contains("True")	)
					{
						ImprintMethod imprintObj=new ImprintMethod();		
						imprintObj.setType("Unimprinted");
						imprintObj.setAlias("Unimprinted");
						listOfImprintMethods.add(imprintObj);	
					}
					
					break;
				case 95: // NewPictureURL

					break;
				case 96: // NewPictureFile

					break;
				case 97: // ErasePicture

					break;
				case 98: // NewBlankPictureURL

					break;
				case 99:// NewBlankPictureFile

					break;
				case 100: // EraseBlankPicture

					break;
			
				case 101: // NotPictured

					break;

				case 102:// MadeInCountry
					String madeInCountry = cell.getStringCellValue();
					if (!madeInCountry.isEmpty()) {
						List<Origin> listOfOrigin = harvestProductAttributeObj
								.getOriginValues(madeInCountry);
						productConfigObj.setOrigins(listOfOrigin);
					}

					break;
				case 103: // AssembledInCountry
					
			/*		String AssembledCountry = cell
					.getStringCellValue();
			   if (!AssembledCountry.isEmpty()) {
				   AssembledCountry = harvestProductAttributeObj
						.getCountryCodeConvertName(AssembledCountry);
				productExcelObj.setAdditionalProductInfo("Assembled country is: "
								+ AssembledCountry);
		        	}*/

					break;
				case 104: // DecoratedInCountry
					String decoratedInCountry = cell
					.getStringCellValue();
			if (!decoratedInCountry.isEmpty()) {
				decoratedInCountry = harvestProductAttributeObj
						.getCountryCodeConvertName(decoratedInCountry);
				productExcelObj.setAdditionalImprintInfo("Decorated country is: "
								+ decoratedInCountry);
						
			}
				
					break;
				case 105:// ComplianceList
				 List<String> compliance  = new ArrayList<>();
				 compliance.add("Prop 65");
				 compliance.add("CPSIA");
	             productExcelObj.setComplianceCerts(compliance);	

					break;
				case 106: // ComplianceMemo

					break;
				case 107: // ProdTimeLo
					prodTimeLo = CommonUtility
							.getCellValueStrinOrInt(cell);

					break;

				case 108:// ProdTimeHi
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
				case 109:// RushProdTimeLo
					rushProdTimeLo = cell.getStringCellValue();

					break;

				case 110:// RushProdTimeHi
					String rushProdTimeH = cell.getStringCellValue();
					if (!rushProdTimeH
							.equals(ApplicationConstants.CONST_STRING_ZERO)) {
						rushTime = harvestProductAttributeObj
								.getRushTimeValues(rushProdTimeLo,
										rushProdTimeH);
					}

					break;

				case 111: // Packaging
				String pack = cell.getStringCellValue();
					List<Packaging> listOfPackaging = harvestProductAttributeObj
							.getPackageValues(pack);
					productConfigObj.setPackaging(listOfPackaging);

					break;
				case 112:// CartonL
					cartonL = CommonUtility
							.getCellValueStrinOrInt(cell);

					break;

				case 113:// CartonW
					cartonW = CommonUtility
							.getCellValueStrinOrInt(cell);

					break;
				case 114: // CartonH
					cartonH = CommonUtility
							.getCellValueStrinOrInt(cell);

					break;
				case 115: // WeightPerCarton
					weightPerCarton = CommonUtility
							.getCellValueStrinOrInt(cell);

					break;

				case 116: // UnitsPerCarton
					unitsPerCarton = CommonUtility
							.getCellValueStrinOrInt(cell);
					break;

				case 117: // ShipPointCountry

					break;

				case 118: // ShipPointZip
					String FOBValue = CommonUtility
							.getCellValueStrinOrInt(cell);
					// List<String>fobLookupList =
					// lookupServiceDataObj.getFobPoints(FOBLooup);
					if (FOBValue.contains("11714")) {
						fobPintObj
								.setName("Bethpage, NY 11714 USA");
						FobPointsList.add(fobPintObj);
						productExcelObj.setFobPoints(FobPointsList);
					}

					break;

				case 119: // Comment
					String Comment=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(Comment))
					 {
						 productExcelObj.setDistributorOnlyComments(Comment);
						
					 }
					break;

				case 120: // Verified
				/*	String verified = cell.getStringCellValue();
					if (verified.equalsIgnoreCase("True")) {
						String priceConfimedThruString = "2016-12-31T00:00:00";
						productExcelObj
								.setPriceConfirmedThru(priceConfimedThruString);
					}
*/
					break;
				case 121: // UpdateInventory

					break;
				case 122: // InventoryOnHand

					break;
				case 123: // InventoryOnHandAdd

					break;

				case 124: // InventoryMemo

					break;

			
			}  // end inner while loop
					 
		}
			// set  product configuration objects
			
			 // end inner while loop
			
			//String QuoteRequest=Boolean.toString(quoteUponRequest);
					 priceIncludesValue=Priceinclude.toString().trim();
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

					
					priceGrids =  harvestPriceGridObj.getUpchargePriceGrid("1", Setupcharge,
							Setupchargecode,
									"Imprint Method", "false", "USD",
									decorationMethod,
									"Set-up Charge", "Per Order",
									new Integer(1), "Required","",priceGrids);	//setupcharge
					
				/*	priceGrids =  harvestPriceGridObj.getUpchargePriceGrid("1", Screencharge,
							Screenchargecode,
									"Imprint Method", "false", "USD",
									decorationMethod,
									"Screen Charge", "Other",
									new Integer(1),"Required","", priceGrids);	//screen charge
					*/
					
					if(!Repeatcharge.equalsIgnoreCase("0")){
					 priceGrids =  harvestPriceGridObj.getUpchargePriceGrid("1", Repeatcharge,
							 Repeatchargecode,
							"Imprint Method", "false", "USD",
							decorationMethod,
							"Re-Order Charge", "Per Order",
							new Integer(1), "Optional","",priceGrids);	//repeat charge
					}
			
					 if(!Additionalcolor.equalsIgnoreCase("0"))
						{
							AdditionalColor addcolor=new AdditionalColor();
							addcolor.setName("Additional Colors");
							additionalcolorList.add(addcolor);
							productConfigObj.setAdditionalColors(additionalcolorList);
							priceGrids = harvestPriceGridObj.getUpchargePriceGrid("1", Additionalcolor,
											Additionalcolorcode,
											"Additional Colors", "false", "USD",
											"Additional Colors",
											"Add. Color Charge", "Other",
											new Integer(1),"Optional","per Additional color", priceGrids);	
						}
					 
						if(!AddClrRunChg1.equalsIgnoreCase("0"))
						{
					
							Addcolorcharge=Addcolorcharge.append(AddClrRunChg1).append("___").append(AddClrRunChg2).append("___").
									append(AddClrRunChg3).append("___").append(AddClrRunChg4).append("___").
									append(AddClrRunChg5).append("___").append(AddClrRunChg6);
							
							priceGrids = harvestPriceGridObj.getUpchargePriceGrid("1___2___3___4___5___6",
									Addcolorcharge.toString(),
									"RRRRRR",
									"Additional Colors", "false", "USD",
									"Additional Colors",
									"Add. Color Charge", "Other",
									new Integer(1),"Optional","Per piece, per additional color", priceGrids);	
						}
	

	
				} catch (Exception e) {
					_LOGGER.error("Error while Processing ProductId and cause :"
							+ productExcelObj.getExternalProductId()
							+ " "
							+ e.getMessage());
				}
			}
			workbook.close();

			if (!StringUtils.isEmpty(themeValue)) {
				productConfigObj.setThemes(themeList);
			}
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
			
			if(!FirstImprintsize1.contains("10")){
			//	 imprintSizeList=harvestProductAttributeObj.getimprintsize(ImprintSizevalue);
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
					asiNumber, batchId, environmentType);
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

			Addcolorcharge = new StringBuilder();
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
			 Priceinclude = new StringBuilder();
			productConfigObj = new ProductConfigurations();
			rushTime = new RushTime();
			ImprintSizevalue = new StringBuilder();
			additionalcolorList = new ArrayList<>();

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
