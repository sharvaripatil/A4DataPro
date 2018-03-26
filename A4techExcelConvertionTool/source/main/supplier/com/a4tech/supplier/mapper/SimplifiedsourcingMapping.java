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

import parser.goldstarcanada.GoldstarCanadaColorParser;
import parser.goldstarcanada.GoldstarCanadaDimensionParser;
import parser.goldstarcanada.GoldstarCanadaImprintMethodParser;
import parser.goldstarcanada.GoldstarCanadaImprintsizeParser;
import parser.goldstarcanada.GoldstarCanadaLookupData;
import parser.goldstarcanada.GoldstarCanadaOriginParser;
import parser.goldstarcanada.GoldstarCanadaPackagingParser;
import parser.goldstarcanada.GoldstarCanadaPriceGridParser;
import parser.goldstarcanada.GoldstarCanadaRushTimeParser;
import parser.goldstarcanada.GoldstarCanadaShippingEstimateParser;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Origin;
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

public class SimplifiedsourcingMapping implements IExcelParser{ 


	private static final Logger _LOGGER = Logger.getLogger(SimplifiedsourcingMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;
	private GoldstarCanadaDimensionParser gcdimensionObj;//done
	private GoldstarCanadaImprintMethodParser gcimprintMethodParser;//done
	private GoldstarCanadaOriginParser gcOriginParser;//done
	private GoldstarCanadaRushTimeParser gcRushTimeParser;//done
	private GoldstarCanadaPackagingParser gcPackagingParser;//done
	private GoldstarCanadaShippingEstimateParser gcShippingParser;//done
	private GoldstarCanadaPriceGridParser gcPricegridParser;//done
	private GoldstarCanadaImprintsizeParser gcImprintSizeParser;//done
	private GoldstarCanadaColorParser gccolorparser;

	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId,String environmentType){
		
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
		  Dimension finalDimensionObj=new Dimension();
		  StringBuilder listOfQuantity = new StringBuilder();
		  StringBuilder listOfPrices = new StringBuilder();
		  StringBuilder priceIncludes = new StringBuilder();
		  StringBuilder pricesPerUnit = new StringBuilder();
		  StringBuilder ImprintSizevalue = new StringBuilder();
		  StringBuilder listofUpcharges = new StringBuilder();

			List<Color> colorList = new ArrayList<Color>();
			List<ImprintLocation> listImprintLocation = new ArrayList<ImprintLocation>();
			List<ImprintMethod> listOfImprintMethods = new ArrayList<ImprintMethod>();
			List<ProductionTime> listOfProductionTime = new ArrayList<ProductionTime>();
			List<String> productKeywords = new ArrayList<String>();
			List<Theme> themeList = new ArrayList<Theme>();
			List<String> complianceList = new ArrayList<String>();
		    List<ImprintSize> imprintSizeList =new ArrayList<ImprintSize>();
		    List<Values> valuesList =new ArrayList<Values>();
			RushTime rushTime  = new RushTime();
			Size size=new Size();
			List<FOBPoint> FobPointsList = new ArrayList<FOBPoint>();
			FOBPoint fobPintObj=new FOBPoint();
			 List<String>categoriesList= new ArrayList<String>();
			 List<AdditionalColor>additionalcolorList= new ArrayList<>();

		try{
			 
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
				  cell2Data =  nextRow.getCell(1);
				if(columnIndex + 1 == 1){
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						xid = cell.getStringCellValue();
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						xid = String.valueOf((int)cell.getNumericCellValue());
					}else {
						  String ProdNo=CommonUtility.getCellValueStrinOrInt(cell2Data);
						  xid=ProdNo;

						}
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							 System.out.println("Java object converted to JSON String, written to file");
							 
							
								ShippingEstimate shipping = gcShippingParser.getShippingEstimateValues(cartonL, cartonW,
										                               cartonH, weightPerCarton, unitsPerCarton);
								productConfigObj.setImprintLocation(listImprintLocation);
								productConfigObj.setImprintMethods(listOfImprintMethods);
								if(!StringUtils.isEmpty(themeValue) ){
								productConfigObj.setThemes(themeList);
								}
								productConfigObj.setRushTime(rushTime);
								productConfigObj.setShippingEstimates(shipping);
								productConfigObj.setProductionTime(listOfProductionTime);
								String DimensionRefernce=null;
								DimensionRefernce=dimensionValue.toString();
								if(!StringUtils.isEmpty(DimensionRefernce)){
								valuesList =gcdimensionObj.getValues(dimensionValue.toString(),
                                        dimensionUnits.toString(), dimensionType.toString());
								
						        finalDimensionObj.setValues(valuesList);	
								size.setDimension(finalDimensionObj);
								productConfigObj.setSizes(size);
								}
								imprintSizeList=gcImprintSizeParser.getimprintsize(ImprintSizevalue,imprintLocation);
								if(imprintSizeList!=null){
								productConfigObj.setImprintSize(imprintSizeList);}
								productConfigObj.setColors(colorList);
								if(!StringUtils.isEmpty(FobPointsList)){
								productExcelObj.setFobPoints(FobPointsList);
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
								productKeywords = new ArrayList<String>();
								listOfProductionTime = new ArrayList<ProductionTime>();
								rushTime = new RushTime();
								listImprintLocation = new ArrayList<ImprintLocation>();
								listOfImprintMethods = new ArrayList<ImprintMethod>();
								imprintSizeList =new ArrayList<ImprintSize>();
								ImprintSizevalue = new StringBuilder();
								size=new Size();
								colorList = new ArrayList<Color>();
								FobPointsList = new ArrayList<FOBPoint>();
								 dimensionValue = new StringBuilder();
								 dimensionUnits = new StringBuilder();
								 dimensionType = new StringBuilder();
								 priceIncludes = new StringBuilder();
								 priceIncludesValue=null;
								 additionalcolorList= new ArrayList<>();
								 listofUpcharges = new StringBuilder();
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid.trim());
						    }
						    existingApiProduct = postServiceImpl.getProduct(accessToken, xid=xid.replace("\t",""), environmentType);
						     if(existingApiProduct == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						  	//   productExcelObj=existingApiProduct;
							//	productConfigObj=existingApiProduct.getProductConfigurations();
						 
						    	 List<Image> Img=existingApiProduct.getImages();
						    	 productExcelObj.setImages(Img);
						    	 
						    	 themeList=productConfigObj.getThemes();
						    	 productConfigObj.setThemes(themeList);
						    	 
						    	 categoriesList=existingApiProduct.getCategories();
						    	 productExcelObj.setCategories(categoriesList);
						    	 
						    	 String Summary=existingApiProduct.getSummary();
						    	 productExcelObj.setSummary(Summary);
						     
						     }
							//productExcelObj = new Product();
					 }
				}
				

				switch (columnIndex + 1) {
			
				case 1://xid
					   productExcelObj.setExternalProductId(xid.trim());
					
					 break;
					 
				case 2://ItemNum
					 String asiProdNo=CommonUtility.getCellValueStrinOrInt(cell);
					int Nolength=asiProdNo.length();
					 if(Nolength>14){
							String strTemp=asiProdNo.substring(0, 14);
						     productExcelObj.setAsiProdNo(strTemp);		
					 }else{
				     productExcelObj.setAsiProdNo(asiProdNo);		
					 }

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
					  break;
				case 4://CatYear

					break;
				case 5://ExpirationDate

				    break;
				case 6://Discontinued
		
					break;
				case 7: //Cat1Name
					Category1=cell.getStringCellValue();
				
					 break;
				case 8://Cat2Name
					String Category2=cell.getStringCellValue();
					if(!StringUtils.isEmpty(categoriesList)){
						if(Category1.equalsIgnoreCase("Lanyards")){
						categoriesList.add(Category1);}
						if(Category2.equalsIgnoreCase("Lanyards")){
						categoriesList.add(Category2);}
						productExcelObj.setCategories(categoriesList);
					}
					break;
					
					
				case 9: //page1
					Category1=cell.getStringCellValue();
				
					 break;
					 
				case 10: //page2
					Category1=cell.getStringCellValue();
				
					 break;
					
				case 11: // Description
					String description =CommonUtility.getCellValueStrinOrInt(cell);
					description=description.replaceAll("™", "");
					description=description.replaceAll("®", "");
					description=description.replaceAll("soft touch", "");
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
					break;
					
				case 12: //Keywords
					String productKeyword = cell.getStringCellValue();
					if(!StringUtils.isEmpty(productKeyword)){
					String productKeywordArr[] = productKeyword.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					for (String string : productKeywordArr) {
						productKeywords.add(string);
					}
					productExcelObj.setProductKeywords(productKeywords);
					}
					break;
					
				case 13:  //Colors
					String colorValue=cell.getStringCellValue();
					if(!StringUtils.isEmpty(colorValue)){
						colorList=gccolorparser.getColorCriteria(colorValue);
						productConfigObj.setColors(colorList);
					}	
						
					
					break;
					
				case 14:  //Themes

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
				
				case 15:  // Dimension1

					String dimensionValue1=CommonUtility.getCellValueStrinOrInt(cell);
					   if(dimensionValue1 != null && !dimensionValue1.isEmpty()){
						   dimensionValue.append(dimensionValue1).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
						 
					   
					}
					break;

				case 16: //Dimension1Units

					String dimensionUnits1 = CommonUtility.getCellValueStrinOrInt(cell);
					 if(!dimensionUnits1.equals("0")){
						 dimensionUnits.append(dimensionUnits1.trim()).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					 }
					break;
					
				case 17: // Dimension1Type

					String dimensionType1 =CommonUtility.getCellValueStrinOrInt(cell);
					if(!dimensionType1.equals("0")){
						dimensionType.append(dimensionType1).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					  
					break;
					
				case 18://Dimension2

					String dimensionValue2 =CommonUtility.getCellValueStrinOrInt(cell);
					 if(dimensionValue2 != null && !dimensionValue2.isEmpty()){
						 dimensionValue.append(dimensionValue2).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					 }
					
					break;
				case 19: //Dimension2Units

					  String dimensionUnits2 = CommonUtility.getCellValueStrinOrInt(cell);
					 if(!dimensionUnits2.equals("0")){
						 dimensionUnits.append(dimensionUnits2.trim()).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					 }
					  break;
				
				case 20: //Dimension2Type
					String  dimensionType2 = CommonUtility.getCellValueStrinOrInt(cell);

					if(!dimensionType2.equals("0")){
						dimensionType.append(dimensionType2).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					
					 
					break;
				
				 case 21: //Dimension3
					 String dimensionValue3  =CommonUtility.getCellValueStrinOrInt(cell);
						if(dimensionValue3 != null && !dimensionValue3.isEmpty()){
							dimensionValue.append(dimensionValue3).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
						}else{
							dimensionValue=dimensionValue.append("");
						}
				
					break;
					
				case 22:  //Dimension3Units

					String dimensionUnits3 = CommonUtility.getCellValueStrinOrInt(cell);
					if(!dimensionUnits3.equals("0")){
						 dimensionUnits.append(dimensionUnits3.trim()).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}else
					{
						dimensionUnits=dimensionUnits.append("");
					}
					break;
					
				case 23: //Dimension3Type

					String dimensionType3 = CommonUtility.getCellValueStrinOrInt(cell);
					if(!dimensionType3.equals("0")){
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
					
					      break;
				case 47:  //SetupChg
					Setupcharge=cell.getStringCellValue();
					
					     break;
				case 48: // SetupChgCode
					Setupcode=cell.getStringCellValue();	
						break;
			
				
				case 49://ScreenChg
		
					break;
						
				case 50://ScreenChgCode

					
					break;
								
				case 51://PlateChg

					
					break;	
					
            	case 52://PlateChgCode

					
					break;			
						
            	case 53://DieChg

					
					break;			
					
					
            	case 54://DieChgCode


					
					break;		
					
               case 55://ToolingChg

			
					break;		
					
               case 56://ToolingChgCode

       			
					break;	
					
               case 57://RepeatChg

          			
					break;
					
               case 58://RepeatChgCode


					break;
					
				case 59://AddClrChg
					Addcolorcharge=cell.getStringCellValue();

							break;
				case 60://AddClrChgCode
	
							break; 
				case 61://AddClrRunChg1
				case 62://AddClrRunChg2
				case 63://AddClrRunChg3
				case 64://AddClrRunChg4
				case 65://AddClrRunChg5
				case 66://AddClrRunChg6
					try{
						
						if(cell.getCellType() == Cell.CELL_TYPE_STRING){
							quantity = cell.getStringCellValue();
					         if(!StringUtils.isEmpty(quantity) && !quantity.equals("0")){
					        	 listofUpcharges.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
							double quantity1 = (double)cell.getNumericCellValue();
					         if(!StringUtils.isEmpty(quantity1)){
					        	 listofUpcharges.append(quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						}else{
						}  				
						}catch (Exception e) {
							_LOGGER.info("Error in pricePerUnit field "+e.getMessage());
						}
							break;
				case 67://AddClrRunChgCode
					 Addclearcode=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(Addclearcode))
					 {//PPPPR
					if(Addclearcode.contains("P"))
					{
						Upchargecode="P";
					}
					else if(Addclearcode.contains("R"))
					{
						Upchargecode="R";

					}
			     	}
							break;
				case 68://IsRecyclable

							break;
				case 69://IsEnvironmentallyFriendly
					 String IsEnvironmentallyFriendly ="";
					  if(cell.getCellType() == Cell.CELL_TYPE_STRING){
					   IsEnvironmentallyFriendly = cell.getStringCellValue();

					}else
					{
						 boolean boovar = cell.getBooleanCellValue();
						 IsEnvironmentallyFriendly = String.valueOf(boovar);
					}
						if(IsEnvironmentallyFriendly.equalsIgnoreCase("true"))			
						{ Theme themeObj1 = new Theme();

							themeObj1.setName("Eco & Environmentally Friendly");	

							themeList.add(themeObj1);
						}

							break;
				case 70://IsNewProd

			          		break; 
				case 71://NotSuitable

					       break;
				case 72://Exclusive

					break;
				case 73://Hazardous

					break;
				case 74://OfficiallyLicensed

					break;
				case 75://IsFood

					break;
				case 76://IsClothing

					break;
				case 77://ImprintSize1

					 FirstImprintsize1=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(FirstImprintsize1) || FirstImprintsize1 !=  "0" ){
					 ImprintSizevalue=ImprintSizevalue.append(FirstImprintsize1).append(" ");
					
					 }
					    break;
				case 78: //ImprintSize1Units
					FirstImprintunit1=CommonUtility.getCellValueStrinOrInt(cell);
					
					 if(!StringUtils.isEmpty(FirstImprintunit1) || FirstImprintunit1 !=  "0" ){
					FirstImprintunit1=GoldstarCanadaLookupData.Dimension1Units.get(FirstImprintunit1);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprintunit1).append(" ");
					 }	 
					   	break;
					   	
					    
				case 79: //ImprintSize1Type
					FirstImprinttype1=CommonUtility.getCellValueStrinOrInt(cell);
					
					   if(!StringUtils.isEmpty(FirstImprinttype1) || FirstImprinttype1 !=  "0" ){
						FirstImprinttype1=GoldstarCanadaLookupData.Dimension1Type.get(FirstImprinttype1);
						ImprintSizevalue=ImprintSizevalue.append(FirstImprinttype1).append(" ").append("x");
					   }
							break;
		
				case 80:   //ImprintSize2
					FirstImprintsize2=CommonUtility.getCellValueStrinOrInt(cell);
					
					 if(!StringUtils.isEmpty(FirstImprintsize2) || FirstImprinttype1 != "0" ){
					ImprintSizevalue=ImprintSizevalue.append(FirstImprintsize2).append(" ");
					 }
				
				  
				case 81: // ImprintSize2Units

			      FirstImprintunit2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(FirstImprintunit2) || FirstImprintunit2 !=  "0" ){
					FirstImprintunit2=GoldstarCanadaLookupData.Dimension1Units.get(FirstImprintunit2);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprintunit2).append(" ");
				    }


					  	break;
					  	
				case 82:	// ImprintSize2Type
		     	FirstImprinttype2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(FirstImprinttype2) || FirstImprinttype2 !=  "0" ){

					FirstImprinttype2=GoldstarCanadaLookupData.Dimension1Type.get(FirstImprinttype2);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprinttype2).append(" ");
				    }

					    break;
					    
				case 83: //ImprintLoc

					 imprintLocation = cell.getStringCellValue();
						if(imprintLocation.contains("Full Bleed")){
							ImprintLocation locationObj = new ImprintLocation();
							locationObj.setValue("Background: Full Bleed");
							listImprintLocation.add(locationObj);
						}
					
					
					break;
					  	
				case 84:  // SecondImprintSize1

                	SecondImprintsize1=CommonUtility.getCellValueStrinOrInt(cell);
					
				    if(!StringUtils.isEmpty(SecondImprintsize1) || SecondImprintsize1 !=  "0" ){

					ImprintSizevalue=ImprintSizevalue.append(SecondImprintsize1).append(" ");
				    }
					 break;
					 
					 
				case 85:  //SecondImprintSize1Units

	              SecondImprintunit1=CommonUtility.getCellValueStrinOrInt(cell);
					
				    if(!StringUtils.isEmpty(SecondImprintunit1) || SecondImprintunit1 != "0" ){
					SecondImprintunit1=GoldstarCanadaLookupData.Dimension1Units.get(SecondImprintunit1);
					ImprintSizevalue=ImprintSizevalue.append(SecondImprintunit1).append(" ");

					}
				
					   	break;
					   	
				case 86:  //SecondImprintSize1Type
					SecondImprinttype1=CommonUtility.getCellValueStrinOrInt(cell);
					
				    if(!StringUtils.isEmpty(SecondImprinttype1) || SecondImprinttype1 !=  "0" ){
					SecondImprinttype1=GoldstarCanadaLookupData.Dimension1Type.get(SecondImprinttype1);
					ImprintSizevalue=ImprintSizevalue.append(SecondImprinttype1).append(" ").append("x");

					}
				
					
						break;
						
				case 87:  // SecondImprintSize2
					SecondImprintsize2=CommonUtility.getCellValueStrinOrInt(cell);
					
				    if(!StringUtils.isEmpty(SecondImprintsize2) || SecondImprintsize2 !=  "0" ){
				    ImprintSizevalue=ImprintSizevalue.append(SecondImprintsize2).append(" ");
				    
				    }

				
				
					  break;
					  
				case 88: // SecondImprintSize2Units

					SecondImprintunit2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprintunit2) || SecondImprintunit2 !=  "0" ){
					SecondImprintunit2=GoldstarCanadaLookupData.Dimension1Units.get(SecondImprintunit2);
					ImprintSizevalue=ImprintSizevalue.append(SecondImprintunit2).append(" ");

					}
					
					break;
					
				case 89: //SecondImprintSize2Type

					SecondImprinttype2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprinttype2) || SecondImprinttype2 != "0" ){
					SecondImprinttype2=GoldstarCanadaLookupData.Dimension1Type.get(SecondImprinttype2);
					ImprintSizevalue=ImprintSizevalue.append(SecondImprinttype2).append(" ");

					}
					

					break;
					
				case 90: //SecondImprintLoc
	
					String imprintLocation2 = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintLocation2)){
						ImprintLocation locationObj2 = new ImprintLocation();
						locationObj2.setValue(imprintLocation2);
						listImprintLocation.add(locationObj2);
					}
					  break;
					  
				case 91: //DecorationMethod

					 decorationMethod = cell.getStringCellValue();
						listOfImprintMethods = gcimprintMethodParser.getImprintMethodValues(decorationMethod,listOfImprintMethods);
						
					break;
				case 92: // NoDecoration
					String noDecoration = cell.getStringCellValue();
					if(noDecoration.equalsIgnoreCase(ApplicationConstants.CONST_STRING_TRUE)){
						listOfImprintMethods = gcimprintMethodParser.getImprintMethodValues(noDecoration,
                                listOfImprintMethods);
					}
					 break; 
					 
				case 93: //NoDecorationOffered
					String noDecorationOffered = cell.getStringCellValue();
					if(noDecorationOffered.equalsIgnoreCase(ApplicationConstants.CONST_STRING_TRUE)){
						listOfImprintMethods = gcimprintMethodParser.getImprintMethodValues(noDecorationOffered,
                                listOfImprintMethods);
					}
					
					
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
						List<Origin> listOfOrigin = gcOriginParser.getOriginValues(madeInCountry);
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
						prodTimeTotal=prodTimeTotal.concat(prodTimeLo).concat("-").concat(prodTimeHi);
						productionTime.setBusinessDays(prodTimeTotal);
						listOfProductionTime.add(productionTime);
					
					}
					}
					break;
					
				case 108: //RushProdTimeLo
				    rushProdTimeLo  = cell.getStringCellValue();
				
					break;
				case 109: //RushProdTimeHi
					String rushProdTimeH  = cell.getStringCellValue();
					if (!rushProdTimeLo
							.equals(ApplicationConstants.CONST_STRING_ZERO)) {
						rushTime = gcRushTimeParser
								.getRushTimeValues(rushProdTimeLo,
										rushProdTimeH);
					}

					
					
					break;
				case 110://Packaging

					String pack  = cell.getStringCellValue();
					 if(!StringUtils.isEmpty(pack))
					 {
					productExcelObj.setAdditionalShippingInfo(pack);
					 }
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

				
					break;
				case 117: //ShipPointZip
				  	 FOBValue=CommonUtility.getCellValueStrinOrInt(cell);
						
							 if(!StringUtils.isEmpty(FOBValue))
							 {
								if(FOBValue.contains("L7L 0J8"))
								{
									fobPintObj=new FOBPoint();
									fobPintObj.setName("Burlington, ON L7L 5J8 CAN");
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
			
			if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
				priceGrids = gcPricegridParser.getPriceGrids(listOfPrices.toString(), 
						         listOfQuantity.toString(), priceCode, "USD",
						         priceIncludesValue, true, quoteUponRequest, productName,"",priceGrids);	
			}
			
	
				String ImprintMethodValue = listOfImprintMethods
						.get(0).getAlias();

				priceGrids = gcPricegridParser
						.getUpchargePriceGrid("1", Setupcharge,
								Setupcode,
								"Imprint Method", "false", "USD",
								ImprintMethodValue,
								"Set-up Charge", "Other",
								new Integer(1), priceGrids);
								
	
			if(!Addcolorcharge.equalsIgnoreCase("0"))
			{
				AdditionalColor addcolor=new AdditionalColor();
				addcolor.setName("Additional Colors");
				additionalcolorList.add(addcolor);
				productConfigObj.setAdditionalColors(additionalcolorList);
				priceGrids = gcPricegridParser
						.getUpchargePriceGrid("1", Addcolorcharge,
								"V",
								"Additional Colors", "false", "USD",
								"Additional Colors",
								"Set-up Charge", "Other",
								new Integer(1), priceGrids);	
			}
			
		     if(!StringUtils.isEmpty(Addclearcode))
		     {
				priceGrids = gcPricegridParser
						.getUpchargePriceGrid("1", listofUpcharges.toString(),
								Upchargecode,
								"Additional Colors", "false", "USD",
								"Additional Colors",
								"Run Charge", "Other",
								new Integer(1), priceGrids);	
				
			}
			
			
			
			
			productExcelObj.setPriceGrids(priceGrids);
		  
			    
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() );		 
		}
		}
		workbook.close();
	
		ShippingEstimate shipping = gcShippingParser.getShippingEstimateValues(cartonL, cartonW,
				                               cartonH, weightPerCarton, unitsPerCarton);
		productConfigObj.setImprintLocation(listImprintLocation);
	
	//	if(!StringUtils.isEmpty(themeValue) ){
		productConfigObj.setThemes(themeList);
	//	}
		productConfigObj.setRushTime(rushTime);
		productConfigObj.setShippingEstimates(shipping);
		productConfigObj.setProductionTime(listOfProductionTime);	
		String DimensionRef=null;
		DimensionRef=dimensionValue.toString();
		if(!StringUtils.isEmpty(DimensionRef)){
		valuesList =gcdimensionObj.getValues(dimensionValue.toString(),
                dimensionUnits.toString(), dimensionType.toString());
		
        finalDimensionObj.setValues(valuesList);	
		size.setDimension(finalDimensionObj);
		productConfigObj.setSizes(size);
		}
		productConfigObj.setImprintMethods(listOfImprintMethods);

		
		imprintSizeList=gcImprintSizeParser.getimprintsize(ImprintSizevalue,imprintLocation);
		 imprintSizeList.removeAll(Collections.singleton(null));
		 
		 
		 if(!StringUtils.isEmpty(FirstImprintsize1) || FirstImprintsize1 !=  "0" ){
		productConfigObj.setImprintSize(imprintSizeList);
		}
		//productExcelObj.setImages(listOfImages);
		productConfigObj.setColors(colorList);
		if(!StringUtils.isEmpty(FobPointsList)){
		productExcelObj.setFobPoints(FobPointsList);
		}
	   

		 	productExcelObj.setProductConfigurations(productConfigObj);
	
		 	
		 	//if(Prod_Status = false){
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
			productKeywords = new ArrayList<String>();
			listOfProductionTime = new ArrayList<ProductionTime>();
			rushTime = new RushTime();
			listImprintLocation = new ArrayList<ImprintLocation>();
			listOfImprintMethods = new ArrayList<ImprintMethod>();
			imprintSizeList =new ArrayList<ImprintSize>();
			size=new Size();
			colorList = new ArrayList<Color>();
			FobPointsList = new ArrayList<FOBPoint>();
			ImprintSizevalue = new StringBuilder();
			DimensionRef=null;
			dimensionValue = new StringBuilder();
			dimensionUnits = new StringBuilder();
			dimensionType = new StringBuilder();
			priceIncludesValue=null;
			priceIncludes = new StringBuilder();
			additionalcolorList= new ArrayList<>();
			listofUpcharges = new StringBuilder();
			 
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
	
	public GoldstarCanadaImprintsizeParser getGcImprintSizeParser() {
		return gcImprintSizeParser;
	}

	public void setGcImprintSizeParser(
			GoldstarCanadaImprintsizeParser gcImprintSizeParser) {
		this.gcImprintSizeParser = gcImprintSizeParser;
	}

	public GoldstarCanadaShippingEstimateParser getGcShippingParser() {
		return gcShippingParser;
	}

	public void setGcShippingParser(
			GoldstarCanadaShippingEstimateParser gcShippingParser) {
		this.gcShippingParser = gcShippingParser;
	}

	public GoldstarCanadaPriceGridParser getGcPricegridParser() {
		return gcPricegridParser;
	}

	public void setGcPricegridParser(GoldstarCanadaPriceGridParser gcPricegridParser) {
		this.gcPricegridParser = gcPricegridParser;
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

	public GoldstarCanadaDimensionParser getGcdimensionObj() {
		return gcdimensionObj;
	}

	public void setGcdimensionObj(GoldstarCanadaDimensionParser gcdimensionObj) {
		this.gcdimensionObj = gcdimensionObj;
	}

	public GoldstarCanadaImprintMethodParser getGcimprintMethodParser() {
		return gcimprintMethodParser;
	}

	public void setGcimprintMethodParser(
			GoldstarCanadaImprintMethodParser gcimprintMethodParser) {
		this.gcimprintMethodParser = gcimprintMethodParser;
	}

	public GoldstarCanadaOriginParser getGcOriginParser() {
		return gcOriginParser;
	}

	public void setGcOriginParser(GoldstarCanadaOriginParser gcOriginParser) {
		this.gcOriginParser = gcOriginParser;
	}

	public GoldstarCanadaRushTimeParser getGcRushTimeParser() {
		return gcRushTimeParser;
	}

	public void setGcRushTimeParser(GoldstarCanadaRushTimeParser gcRushTimeParser) {
		this.gcRushTimeParser = gcRushTimeParser;
	}

	public GoldstarCanadaPackagingParser getGcPackagingParser() {
		return gcPackagingParser;
	}

	public void setGcPackagingParser(GoldstarCanadaPackagingParser gcPackagingParser) {
		this.gcPackagingParser = gcPackagingParser;
	}

	public GoldstarCanadaColorParser getGccolorparser() {
		return gccolorparser;
	}

	public void setGccolorparser(GoldstarCanadaColorParser gccolorparser) {
		this.gccolorparser = gccolorparser;
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


}

