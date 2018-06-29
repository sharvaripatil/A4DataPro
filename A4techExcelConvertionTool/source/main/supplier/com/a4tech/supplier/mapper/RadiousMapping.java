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
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class RadiousMapping implements IExcelParser {
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
				case 1:// productcode
					productExcelObj.setExternalProductId(xid.trim());

					break;
				case 2:// productname
					productName = cell.getStringCellValue();
					productExcelObj.setName(productName);
					break;
					
				case 7:// productdescription
					String description = cell.getStringCellValue();
					description=description.replace("<p>", "").replace("</p>", "");
					productExcelObj.setName(description);
				
					break;

				case 11: // tagattributes
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

			
				case 24: // Fabric Colors / Patterns  Available
					String colorValue = cell.getStringCellValue();
					if (!StringUtils.isEmpty(colorValue)) {
						color = harvestColorObj
								.getColorCriteria(colorValue);
						productConfigObj.setColors(color);
					}


					break;
				case 25: // Material
					
					break;
				
				case 27://IMPRINT
					break;
					
				case 28://SIZE
					break;
				

				case 30://HEIGHT
				quantity = CommonUtility
							.getCellValueStrinOrInt(cell);
					listOfQuantity
							.append(quantity)
							.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

					break;
				
				case 38://Carton Weight
					break;
				case 39://Carton Width
					break;
				case 40://Carton Height
					break;
				case 41://Carton Length
					break;
				case 42://Units Per Carton
					break;
			

				case 47: // ProductionTime_1
					String PriceincludLoc=cell.getStringCellValue();
					if(!StringUtils.isEmpty(PriceincludLoc)){
						
						Priceinclude=Priceinclude.append(",").append(PriceincludLoc);	

					}
					break;
			

				case 49:// quantity1_1
					 Setupchargecode=cell.getStringCellValue();


					break;
				case 50:// quantitytext1_1
					 Screencharge=cell.getStringCellValue();


					break;
				case 51:// price1_1
					 Screenchargecode=cell.getStringCellValue();


					break;
				case 52:// discountcode1_1

					break;
			
				case 54:// quantity2_1

					break;
				case 55:// quantitytext2_1

					break;
				case 56:// price2_1

					break;
				case 57:// discountcode2_1

					break;
				
				case 59:// quantity3_1
					Repeatchargecode=cell.getStringCellValue();

					break;
				case 60:// quantitytext3_1
					Additionalcolor=cell.getStringCellValue();

					break;
				case 61:// price3_1
					Additionalcolorcode=cell.getStringCellValue();


					break;
				case 62:// discountcode3_1
					AddClrRunChg1=cell.getStringCellValue();

					break;
			
				case 64:// quantity4_1
					AddClrRunChg3=cell.getStringCellValue();


					break;
				case 65:// quantitytext4_1
					AddClrRunChg4=cell.getStringCellValue();


					break;
				case 66:// price4_1
					AddClrRunChg5=cell.getStringCellValue();
					
				case 67:// discountcode4_1

					break;
				
				case 69:// quantity5_1

					break;
				case 70:// quantitytext5_1
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
				case 71:// price5_1

					break;
				case 72:// discountcode5_1

					break;
		
				case 74://quantity6_1	
					
					break;
				
				case 75: //quantitytext6_1

					break;
			
				case 76:// price6_1

					break;
				case 77: // discountcode6_1

					break;

			
					    
				case 79: // quantity7_1
					FirstImprintunit1=CommonUtility.getCellValueStrinOrInt(cell);
					
					 if(!StringUtils.isEmpty(FirstImprintunit1) || FirstImprintunit1 !=  null ){
					FirstImprintunit1=GoldstarCanadaLookupData.Dimension1Units.get(FirstImprintunit1);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprintunit1).append(" ");
					 }	 
					   	break;
					   	
				case 80:   // quantitytext7_1
					FirstImprinttype1=CommonUtility.getCellValueStrinOrInt(cell);
					
				   if(!StringUtils.isEmpty(FirstImprinttype1) || FirstImprinttype1 !=  null ){
					FirstImprinttype1=GoldstarCanadaLookupData.Dimension1Type.get(FirstImprinttype1);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprinttype1).append(" ").append("x");
				   }
						break;
						
				  
				case 81: // price7_1
					FirstImprintsize2=CommonUtility.getCellValueStrinOrInt(cell);
					
					 if(!StringUtils.isEmpty(FirstImprintsize2) || FirstImprinttype1 != null ){
					ImprintSizevalue=ImprintSizevalue.append(FirstImprintsize2).append(" ");
					 }

					  	break;
					  	
				case 82:	// discountcode7_1
					FirstImprintunit2=CommonUtility.getCellValueStrinOrInt(cell);
					
					
				    if(!StringUtils.isEmpty(FirstImprintunit2) || FirstImprintunit2 !=  null ){
					FirstImprintunit2=GoldstarCanadaLookupData.Dimension1Units.get(FirstImprintunit2);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprintunit2).append(" ");
				    }

					
					    break;
					    
				
					  	
				case 84:  //quantity8_1
					
					 imprintLocation = cell.getStringCellValue();
					if(!imprintLocation.isEmpty()){
						ImprintLocation locationObj = new ImprintLocation();
						locationObj.setValue(imprintLocation);
						listImprintLocation.add(locationObj);
					}
					 break;
				

				case 85: // quantitytext8_1

					break;

				case 86: // price8_1

					break;

				case 87: // discountcode8_1

					break;

				
				case 89: // quantity9_1
  

				case 90: // quantitytext9_1
	    
					break;
				case 91: // price9_1
					String imprintLocation2 = cell.getStringCellValue();
					if (!imprintLocation2.isEmpty()) {
						ImprintLocation locationObj2 = new ImprintLocation();
						locationObj2.setValue(imprintLocation2.trim());
						listImprintLocation.add(locationObj2);
					}

					break;

				case 92: // discountcode9_1
					decorationMethod = cell.getStringCellValue();
					if (!StringUtils.isEmpty(decorationMethod)) {
						listOfImprintMethods = harvestProductAttributeObj
								.getImprintMethodValues(decorationMethod);
					}

					break;
			
				case 94: // quantity10_1
					String Unimprinted1=cell.getStringCellValue();
					if(Unimprinted.contains("True") || Unimprinted1.contains("True")	)
					{
						ImprintMethod imprintObj=new ImprintMethod();		
						imprintObj.setType("Unimprinted");
						imprintObj.setAlias("Unimprinted");
						listOfImprintMethods.add(imprintObj);	
					}
					
					break;
				case 95: // quantitytext10_1

					break;
				case 96: // price10_1

					break;
				case 97: // discountcode10_1

					break;
			
				case 100: // expirationdate_1

					break;
			
				case 140: // imprintmethod_1

					break;

					
				case 141: // imprintlocation_1

					break;

				case 144: // setupcharge_1

					break;

				case 156: // qty1_1

					break;

				case 157: // qtyruncharge1_1

					break;

				case 158: // qty2_1

					break;

				case 159: // qtyruncharge2_1

					break;

				case 160: // qty3_1

					break;

				case 161: // qtyruncharge3_1

					break;

				case 162: // qty4_1

					break;

					
				case 163: // qtyruncharge4_1

					break;

				case 164: // qty5_1

					break;

				case 165: // qtyruncharge5_1

					break;

				case 166: // qty6_1

					break;

				case 167: // qtyruncharge6_1

					break;

				case 168: // qty7_1

					break;

				case 169: // qtyruncharge7_1

					break;

				case 170: // qty8_1

					break;

				case 171: // qtyruncharge8_1

					break;

				case 172: // qty9_1

					break;

				case 173: // qtyruncharge9_1

					break;

				case 174: // qty10_1

					break;

				case 175: // qtyruncharge10_1

					break;

				case 176: // rc_discountcode_1

					break;

				case 178: // imprintmethod_2

					break;

				case 179: // imprintlocation_2

					break;

				case 182: // setupcharge_2

					break;

				case 194: // qty1_2

					break;

				case 195: // qtyruncharge1_2

					break;

				case 196: // qty2_2

					break;

				case 197: // qtyruncharge2_2

					break;

				case 198: // qty3_2

					break;

				case 199: // qtyruncharge3_2

					break;

				case 200: // qty4_2

					break;

				case 201: // qtyruncharge4_2

					break;

				case 202: // qty5_2

					break;

				case 203: // qtyruncharge5_2

					break;

				case 204: // qty6_2

					break;

				case 205: // qtyruncharge6_2

					break;

				case 206: // qty7_2

					break;

				case 207: // qtyruncharge7_2

					break;

				case 208: // qty8_2

					break;

				case 209: // qtyruncharge8_2

					break;

				case 210: // qty9_2

					break;

				case 211: // qtyruncharge9_2

					break;

				case 212: // qty10_2

					break;

				case 213: // qtyruncharge10_2

					break;

				case 214: // rc_discountcode_2

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
						if(AddClrRunChg5.equalsIgnoreCase("0"))
						{
							Addcolorcharge=Addcolorcharge.append(AddClrRunChg1).append("___").append(AddClrRunChg2).append("___").
									append(AddClrRunChg3).append("___").append(AddClrRunChg4);
							
							priceGrids = harvestPriceGridObj.getUpchargePriceGrid("1___2___3___4",
									Addcolorcharge.toString(),
									"RRRR",
									"Additional Colors", "false", "USD",
									"Additional Colors",
									"Add. Color Charge", "Other",
									new Integer(1),"Optional","Per piece, per additional color", priceGrids);	
						}
						else
						{
							Addcolorcharge=Addcolorcharge.append(AddClrRunChg1).append("___").append(AddClrRunChg2).append("___").
									append(AddClrRunChg3).append("___").append(AddClrRunChg4).append("___").append(AddClrRunChg5);
							
							
							priceGrids = harvestPriceGridObj.getUpchargePriceGrid("1___2___3___4__5",
									"AddClrRunChg1___AddClrRunChg2___AddClrRunChg3___AddClrRunChg4__AddClrRunChg5",
									"RRRR",
									"Additional Colors", "false", "USD",
									"Additional Colors",
									"Add. Color Charge", "Other",
									new Integer(1),"Optional","Per piece, per additional color", priceGrids);
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
