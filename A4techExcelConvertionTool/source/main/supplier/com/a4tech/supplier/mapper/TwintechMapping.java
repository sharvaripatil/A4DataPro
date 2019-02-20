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

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.dao.service.ProductDao;
//import com.a4tech.product.model.Availability;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
//import com.a4tech.product.model.ImprintSize;
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
import com.a4tech.product.service.imple.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.twintech.RoelProductAttributeParser;
import parser.twintech.TwintechColorParser;
import parser.twintech.TwintechPriceGridParser;
import parser.twintech.TwintechProductAttributeParser;

public class TwintechMapping implements IExcelParser {  //EXCIT-752   RO-EL

	private static final Logger _LOGGER = Logger
			.getLogger(TwintechMapping.class);
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private LookupServiceData lookupServiceDataObj;
	private TwintechColorParser twintechColorObj;
	private TwintechPriceGridParser twintechPriceGridObj;
	private TwintechProductAttributeParser twintechProductAttributeObj;
	private RoelProductAttributeParser roelProduct;

	public String readExcel(String accessToken, Workbook workbook,
			Integer asiNumber, int batchId, String environmentType) {

		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		Set<String> productXids = new HashSet<String>();
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		//List<ImprintSize> imprintSizeList = new ArrayList<ImprintSize>();
		List<ImprintLocation> listImprintLocation = new ArrayList<ImprintLocation>();
		List<ImprintMethod> listOfImprintMethods = new ArrayList<ImprintMethod>();
		List<ProductionTime> listOfProductionTime = new ArrayList<ProductionTime>();
		List<String> productKeywords = new ArrayList<String>();
		List<Theme> themeList = new ArrayList<Theme>();
		List<Values> valuesList = new ArrayList<Values>();
	//	List<ImprintLocation> listLocation = new ArrayList<ImprintLocation>();
		List<FOBPoint> FobPointsList = new ArrayList<FOBPoint>();
		List<Color> color = new ArrayList<Color>();
	//	List<Availability> listOfAvailability = new ArrayList<Availability>();

		
		List<String> complianceList = new ArrayList<String>();


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
	//	StringBuilder pricesPerUnit = new StringBuilder();
		StringBuilder dimensionValue = new StringBuilder();
		StringBuilder dimensionUnits = new StringBuilder();
		StringBuilder dimensionType = new StringBuilder();
		StringBuilder priceInclude = new StringBuilder();
		ImprintLocation locationObj=new ImprintLocation();

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
		//	String pricesUnit = null;
			String serviceCharge = null;
			String cartonL = null;
			String cartonW = null;
			String cartonH = null;
			String weightPerCarton = null;
			String unitsPerCarton = null;
			String DiscountcodeUpcharge = null;
			String decorationMethod = null;
			Product existingApiProduct = null;
			String priceIncludesValue = null;
		//	String imprintValue = null;
			String prodTimeLo = null;
			Cell cell2Data = null;
			String rushProdTimeLo = null;
			//String AvailabilityValue=null;

			while (iterator.hasNext()) {

				try {
					Row nextRow = iterator.next();
					//if (nextRow.getRowNum() < 7)
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
						cell2Data = nextRow.getCell(1);
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
							//	if (nextRow.getRowNum() != 7) {
									if (nextRow.getRowNum() != 1) {

									System.out
											.println("Java object converted to JSON String, written to file");

									if (!StringUtils.isEmpty(themeValue)) {
										productConfigObj.setThemes(themeList);
									}
									String DimensionRef = null;
									DimensionRef = dimensionValue.toString();
									if (!StringUtils.isEmpty(DimensionRef)) {
										valuesList = twintechProductAttributeObj
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
								
								
									productConfigObj
											.setProductionTime(listOfProductionTime);
									productConfigObj.setRushTime(rushTime);

									shipping = twintechProductAttributeObj
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
								//	pricesPerUnit = new StringBuilder();
									dimensionValue = new StringBuilder();
									dimensionUnits = new StringBuilder();
									dimensionType = new StringBuilder();

									priceGrids = new ArrayList<PriceGrid>();
								//	imprintSizeList = new ArrayList<ImprintSize>();
									listImprintLocation = new ArrayList<ImprintLocation>();
									listOfImprintMethods = new ArrayList<ImprintMethod>();
									listOfProductionTime = new ArrayList<ProductionTime>();
									productKeywords = new ArrayList<String>();
									themeList = new ArrayList<Theme>();
									valuesList = new ArrayList<Values>();
									FobPointsList = new ArrayList<FOBPoint>();
									color = new ArrayList<Color>();
								//	listOfAvailability = new ArrayList<Availability>();
									priceInclude = new StringBuilder();
									rushTime = new RushTime();
									finalDimensionObj = new Dimension();
									size = new Size();
									fobPintObj = new FOBPoint();
									shipping = new ShippingEstimate();
									productConfigObj = new ProductConfigurations();
								//	AvailabilityValue = "";

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
									productConfigObj=existingApiProduct.getProductConfigurations();
									List<Image> Img = existingApiProduct
											.getImages();
									productExcelObj.setImages(Img);
									
							    	 themeList=productConfigObj.getThemes();
							    	 productConfigObj.setThemes(themeList);
							    	 
							    	 List<String>categoriesList=existingApiProduct.getCategories();
							    	 productExcelObj.setCategories(categoriesList);
							    	 

								}
								// productExcelObj = new Product();
							}
						}

						switch (columnIndex + 1) {
						case 1:// ExternalProductID 

							break;
						case 2:// ProductID
							String XID=CommonUtility
									.getCellValueStrinOrInt(cell);
							productExcelObj.setExternalProductId(XID);                   

							break;
						case 3:// ItemNum

							String asiProdNo = CommonUtility
									.getCellValueStrinOrInt(cell);
							productExcelObj.setAsiProdNo(asiProdNo);
							break;

						case 4:// Name
							productName = cell.getStringCellValue();
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

							/*String priceConfirmedThru = cell
									.getStringCellValue();
							String strArr[] = priceConfirmedThru.split("/");
							priceConfirmedThru = strArr[2] + "/" + strArr[0]
									+ "/" + strArr[1];
							priceConfirmedThru = priceConfirmedThru.replaceAll(
									"/", "-");
							productExcelObj
									.setPriceConfirmedThru(priceConfirmedThru);*/

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
							if (!StringUtils.isEmpty(description)) {
								productExcelObj.setDescription(description);
							} else {
								productExcelObj
										.setDescription(ApplicationConstants.CONST_STRING_EMPTY);
							}

							break;

						case 13: // Keywords

							String productKeyword = cell.getStringCellValue();
							if (!StringUtils.isEmpty(productKeyword)) {
								String productKeywordArr[] = productKeyword
										.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
								for (String string : productKeywordArr) {
									productKeywords.add(string);
								}
								productExcelObj
										.setProductKeywords(productKeywords);
							}

							break;

						case 14: // Colors

						String colorValue = cell.getStringCellValue();
							if (!StringUtils.isEmpty(colorValue)) {
							/*	color = twintechColorObj
										.getColorCriteria(colorValue);*/
								color = roelProduct
										.getColorCriteria(colorValue);
								productConfigObj.setColors(color);
							}

							break;

						case 15:// Themes

							/*themeValue = cell.getStringCellValue();
							Theme themeObj = null;
							String Value = "";
							String[] themes = CommonUtility.getValuesOfArray(
									themeValue,
									ApplicationConstants.CONST_DELIMITER_COMMA);
							List<String> themeLookupList = lookupServiceDataObj
									.getTheme(Value);

							for (String themeName : themes) {
								if (themeLookupList.contains(themeName.trim()
										.toUpperCase())) {
									themeObj = new Theme();
									themeObj.setName(themeName);
									themeList.add(themeObj);
								}

							}*/
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
						/*	pricesUnit = CommonUtility
									.getCellValueStrinOrInt(cell);
							pricesPerUnit
									.append(pricesUnit)
									.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

*/							break;

						case 44: //
					    	quoteUponRequest = cell.getStringCellValue();
							break;

						case 45: // priceIncludeClr
							String Priceinclude1=cell.getStringCellValue();
							 if(!StringUtils.isEmpty(Priceinclude1)){
								 priceInclude=priceInclude.append(Priceinclude1).append(",");
					          	}
							break;
						case 46: // PriceIncludeSide
							String Priceinclude2=cell.getStringCellValue();
							 if(!StringUtils.isEmpty(Priceinclude2)){
								 priceInclude=priceInclude.append(Priceinclude2).append(","); 
							 }
							 
							/*
							 * ImprintOption=cell.getStringCellValue();
							 * ImprintOptObj
							 * =twintechProductAttributeObj.getImprintOption2
							 * (ImprintOption); optionList.add(ImprintOptObj);
							 */
							break;

						case 47: // PriceIncludeLoc
							String Priceinclude3=cell.getStringCellValue();
							 if(!StringUtils.isEmpty(Priceinclude3)){
								 priceInclude=priceInclude.append(Priceinclude3);
							 }
							 priceIncludesValue= priceInclude.toString();
							 
							break;
						case 48:// SetupChg
						serviceCharge = CommonUtility
									.getCellValueDouble(cell);

							break;

						case 49:// SetupChgCode
				    	DiscountcodeUpcharge = CommonUtility
									.getCellValueStrinOrInt(cell);

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
						/*	String IsEnvironmentallyFriendly = cell
									.getStringCellValue();

							if (IsEnvironmentallyFriendly
									.equalsIgnoreCase("true")) {
								Theme themeObj1 = new Theme();

								themeObj1.setName("Eco Friendly");

								themeList.add(themeObj1);
							}*/

							break;
						case 71:// IsNewProd

							break;
						case 72:// NotSuitable

							break;
						case 73:// Exclusive

							break;
							
						case 74:////Hazardous
							break;
							
						case 75:////OfficiallyLicensed
	
							break;
						case 76:// IsFood

							break;
						case 77: // IsClothing

							break;

						case 78: // ImprintSize1

							break;

						case 79: // ImprintSize1Units
	
							break;

						case 80: // ImprintSize1Type

							break;

						case 81: // ImprintSize2
							
							break;

						case 82: // ImprintSize2Units
							
							break;

						case 83: // ImprintSize2Type
						
							break;
						case 84: // ImprintLoc
							
							String ImprintLocation=cell.getStringCellValue();
							if (!StringUtils.isEmpty(ImprintLocation)) {
								locationObj.setValue(ImprintLocation);
								listImprintLocation.add(locationObj);
								productConfigObj.setImprintLocation(listImprintLocation);							
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
						
							break;

						case 90: // SecondImprintSize2Type
						
							break;
						case 91: // SecondImprintLoc
							/*String imprintLocation2 = cell.getStringCellValue();
							if (!imprintLocation2.isEmpty()) {
								ImprintLocation locationObj2 = new ImprintLocation();
								locationObj2.setValue(imprintLocation2.trim());
								listImprintLocation.add(locationObj2);
							}*/

							break;

						case 92: // DecorationMethod
							decorationMethod = cell.getStringCellValue();
							if (!StringUtils.isEmpty(decorationMethod)) {
								listOfImprintMethods = twintechProductAttributeObj
										.getImprintMethodValues(decorationMethod);
							//	productConfigObj.setImprintMethods(listOfImprintMethods);
							}

							break;
						case 93: // NoDecoration

							break;
						case 94: // NoDecorationOffered

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
								List<Origin> listOfOrigin = twintechProductAttributeObj
										.getOriginValues(madeInCountry);
								productConfigObj.setOrigins(listOfOrigin);
							}

							break;
						case 103: // AssembledInCountry

							break;
						case 104: // DecoratedInCountry
							/*String decoratedInCountry = cell
									.getStringCellValue();
							if (!decoratedInCountry.isEmpty()) {
								decoratedInCountry = twintechProductAttributeObj
										.getCountryCodeConvertName(decoratedInCountry);
								productExcelObj
										.setAdditionalProductInfo("Decorated country is: "
												+ decoratedInCountry);
							}*/
							break;
						case 105:// ComplianceList
							String complnceValue=cell.getStringCellValue();
							 if(!StringUtils.isEmpty(complnceValue))
							   {
						    	complianceList=roelProduct.getCompliancecert(complnceValue);
						    	productExcelObj.setComplianceCerts(complianceList);
							   }
							
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
							if (!rushProdTimeLo
									.equals(ApplicationConstants.CONST_STRING_ZERO)) {
								rushTime = twintechProductAttributeObj
										.getRushTimeValues(rushProdTimeLo,
												rushProdTimeH);
							}

							break;

						case 111: // Packaging
							String pack = cell.getStringCellValue();
							List<Packaging> listOfPackaging = twintechProductAttributeObj
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
							if (FOBValue.contains(/*"90720"*/"12901")) {
								fobPintObj.setName(/*"Los Alamitos, CA 90720 USA"*/"Plattsburg, NY 12901 USA");
								FobPointsList.add(fobPintObj);
								productExcelObj.setFobPoints(FobPointsList);
							}

							break;

						case 119: // Comment

							break;

						case 120: // Verified
							String verified = cell.getStringCellValue();
							if (verified.equalsIgnoreCase("True")) {
								String priceConfimedThruString = "2017-12-31T00:00:00";
								productExcelObj
										.setPriceConfirmedThru(priceConfimedThruString);
							}

							break;
						case 121: // UpdateInventory

							break;
						case 122: // InventoryOnHand

							break;
						case 123: // InventoryOnHandAdd

							break;

						case 124: // InventoryMemo

							break;

						} // end inner while loop

					}
					// set product configuration objects

					// end inner while loop

					productExcelObj.setPriceType("L");
					if (listOfPrices != null
							&& !listOfPrices.toString().isEmpty()) {
						priceGrids = twintechPriceGridObj.getPriceGrids(
								listOfPrices.toString(),
								listOfQuantity.toString(), priceCode, "USD",
								priceIncludesValue, true, quoteUponRequest,
								productName, "", priceGrids);
					}

				productConfigObj.setImprintMethods(listOfImprintMethods);

					if (!StringUtils.isEmpty(serviceCharge)) {
						for (int i = 0; i < listOfImprintMethods.size(); i++) {
							String ImprintMethodValue = listOfImprintMethods
									.get(i).getAlias();

							priceGrids = twintechPriceGridObj
									.getUpchargePriceGrid("1", serviceCharge,
											DiscountcodeUpcharge,
											"Imprint Method", "false", "USD",
											ImprintMethodValue,
											"Set-up Charge", "Other",
											new Integer(1), priceGrids);

						}

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
			
			String DimensionRef = null;
			DimensionRef = dimensionValue.toString();
			if (!StringUtils.isEmpty(DimensionRef)) {
				valuesList = twintechProductAttributeObj.getValues(
						dimensionValue.toString(), dimensionUnits.toString(),
						dimensionType.toString());
				finalDimensionObj.setValues(valuesList);
				size.setDimension(finalDimensionObj);
				productConfigObj.setSizes(size);
			}

			productConfigObj.setProductionTime(listOfProductionTime);
			productConfigObj.setRushTime(rushTime);

			shipping = twintechProductAttributeObj.getShippingEstimateValues(
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

			listOfQuantity = new StringBuilder();
			listOfPrices = new StringBuilder();
			//pricesPerUnit = new StringBuilder();
			dimensionValue = new StringBuilder();
			dimensionUnits = new StringBuilder();
			dimensionType = new StringBuilder();
			priceGrids = new ArrayList<PriceGrid>();
		//	imprintSizeList = new ArrayList<ImprintSize>();
			listImprintLocation = new ArrayList<ImprintLocation>();
			listOfImprintMethods = new ArrayList<ImprintMethod>();
			listOfProductionTime = new ArrayList<ProductionTime>();
			productKeywords = new ArrayList<String>();
			themeList = new ArrayList<Theme>();
			valuesList = new ArrayList<Values>();
			FobPointsList = new ArrayList<FOBPoint>();
			color = new ArrayList<Color>();
			priceInclude = new StringBuilder();
		//	listOfAvailability = new ArrayList<Availability>();
			finalDimensionObj = new Dimension();
			size = new Size();
			fobPintObj = new FOBPoint();
			shipping = new ShippingEstimate();
			productConfigObj = new ProductConfigurations();
			rushTime = new RushTime();

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

	public TwintechColorParser getTwintechColorObj() {
		return twintechColorObj;
	}

	public void setTwintechColorObj(TwintechColorParser twintechColorObj) {
		this.twintechColorObj = twintechColorObj;
	}

	public TwintechPriceGridParser getTwintechPriceGridObj() {
		return twintechPriceGridObj;
	}

	public void setTwintechPriceGridObj(
			TwintechPriceGridParser twintechPriceGridObj) {
		this.twintechPriceGridObj = twintechPriceGridObj;
	}

	public TwintechProductAttributeParser getTwintechProductAttributeObj() {
		return twintechProductAttributeObj;
	}

	public void setTwintechProductAttributeObj(
			TwintechProductAttributeParser twintechProductAttributeObj) {
		this.twintechProductAttributeObj = twintechProductAttributeObj;
	}
	
	public RoelProductAttributeParser getRoelProduct() {
		return roelProduct;
	}

	public void setRoelProduct(RoelProductAttributeParser roelProduct) {
		this.roelProduct = roelProduct;
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

}
