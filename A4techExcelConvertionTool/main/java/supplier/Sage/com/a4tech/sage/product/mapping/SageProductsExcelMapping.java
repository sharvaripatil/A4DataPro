package com.a4tech.sage.product.mapping;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.Artwork;
import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintColorValue;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Option;
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
import com.a4tech.sage.product.parser.CatalogParser;
import com.a4tech.sage.product.parser.ColorParser;
import com.a4tech.sage.product.parser.DimensionParser;
import com.a4tech.sage.product.parser.ImprintMethodParser;
import com.a4tech.sage.product.parser.OriginParser;
import com.a4tech.sage.product.parser.PackagingParser;
import com.a4tech.sage.product.parser.PriceGridParser;
import com.a4tech.sage.product.parser.RushTimeParser;
import com.a4tech.sage.product.parser.ShippingEstimateParser;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.LookupData;


public class SageProductsExcelMapping {
	
	private static final Logger _LOGGER = Logger.getLogger(SageProductsExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private CatalogParser   catalogParser;
	private PriceGridParser priceGridParser;
	private ImprintMethodParser imprintMethodParser;
	private OriginParser       originParser;
	private RushTimeParser    rushTimeParser;
	private PackagingParser	packagingParser;
	private ShippingEstimateParser shippingEstimateParser;
	List<String> productKeywords = new ArrayList<String>();
	List<Theme> themeList = new ArrayList<Theme>();
	List<Catalog> catalogList = new ArrayList<Catalog>();
	Size size=new Size();
	DimensionParser dimParserObj= new DimensionParser();
	ColorParser colorParserObj =  new ColorParser();
	
	public int readExcel(String accessToken,Workbook workbook ,Integer asiNumber){
		
		List<String> numOfProducts = new ArrayList<String>();
		FileInputStream inputStream = null;
		//Workbook workbook = null;
		//List<String>  productXids = new ArrayList<String>();
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  //ProductSkuParser skuparserobj=new ProductSkuParser();
		  String productId = null;
		  String currencyType = null;
		  String priceQurFlag = null;
		  String priceType    = null;
		  String basePriceName = null;
		  //String priceIncludes = null;
		  //PriceGridParser priceGridParser = new PriceGridParser();
		  String upChargeName = null;
		  String upChargeQur = null;
		  String upchargeType = null;
		  String upChargeDetails = null;
		  String upChargeLevel = null;
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  
		  double dimensionValue =0;
		  double dimensionUnits = 0 ;
		  double dimensionType = 0 ;
		  Dimension finalDimensionObj=new Dimension();
		  //ProductNumberParser pnumberParser=new ProductNumberParser();
		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		
		
		StringBuilder listOfQuantity = new StringBuilder();
		StringBuilder listOfPrices = new StringBuilder();
		StringBuilder listOfNetPrice = new StringBuilder();
		StringBuilder listOfDiscount = new StringBuilder();
		StringBuilder basePriceCriteria =  new StringBuilder();
		StringBuilder UpCharQuantity = new StringBuilder();
		StringBuilder UpCharPrices = new StringBuilder();
		StringBuilder UpchargeNetPrice = new StringBuilder();
		StringBuilder UpCharDiscount = new StringBuilder();
		StringBuilder UpCharCriteria = new StringBuilder();
		String		priceCode = null;
		StringBuilder pricesPerUnit = new StringBuilder();
		String quoteUponRequest  = null;
		StringBuilder priceIncludes = new StringBuilder();
		String quantity = null;
		String SKUCriteria1 =null;
		String SKUCriteria2 =null;
		String skuvalue  =null;
		String Inlink  =null;
		String Instatus  =null;
		String InQuantity=null;
		String productNumberCriteria1=null;
		String productNumberCriteria2=null;
		String productNumber=null;
		List<Option> option=new ArrayList<Option>();
		Option optionobj= new Option();
		//ProductOptionParser optionparserobj=new ProductOptionParser();
		String optiontype =null;
		String optionname =null;
		String optionvalues =null;
		String optionadditionalinfo =null;
		String canorder =null;
		String reqfororder =null;
		String shippingWeightValue=null;
		String imprintValue=null;
		String imprintColorValue=null;
		String cartonL = null;
		String cartonW = null;
		String cartonH = null;
		String weightPerCarton = null;
		String unitsPerCarton = null;
		
		List<Color> color = new ArrayList<Color>();
		List<ImprintMethod> imprintMethods = new ArrayList<ImprintMethod>();
		List<Artwork> artworkList = new ArrayList<Artwork>();
		List<ImprintColorValue> imprintColorsValueList = new ArrayList<ImprintColorValue>();
		String productName = null;
		StringBuilder imprintMethodValues = new StringBuilder();
		StringBuilder imprintColorValues = new StringBuilder();
		List<ImprintLocation> listImprintLocation = new ArrayList<ImprintLocation>();
		List<ImprintMethod> listOfImprintMethods = new ArrayList<ImprintMethod>();
		List<ProductionTime> listOfProductionTime = new ArrayList<ProductionTime>();
		RushTime rushTime  = new RushTime();
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum() < 7)
				continue;
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			
			
			List<Image> imgList = new ArrayList<Image>();
			
			if(productId != null){
				productXids.add(productId);
			}
			 //String productName = null;
			 boolean checkXid  = false;
			 ShippingEstimate ShipingItem = null;
				
				String shippingitemValue = null;
				String shippingdimensionValue = null;
				 //imprintColors.setType("COLR");
			
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
							   // Add repeatable sets here
							 	productExcelObj.setPriceGrids(priceGrids);
							 	//productConfigObj.setOptions(option);
							 	productExcelObj.setProductConfigurations(productConfigObj);
							 	//productList.add(productExcelObj);
							 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber);
							 	if(num ==1){
							 		numOfProducts.add("1");
							 	}
								//System.out.println(mapper.writeValueAsString(productExcelObj));
							 	_LOGGER.info("list size>>>>>>>"+numOfProducts.size());
								
								// reset for repeateable set 
								priceGrids = new ArrayList<PriceGrid>();
								productConfigObj = new ProductConfigurations();
								option=new ArrayList<Option>();
								
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    }
							productExcelObj = new Product();
					 }
				}
				

				switch (columnIndex + 1) {
				case 1://ExternalProductID
			     if(cell.getCellType() == Cell.CELL_TYPE_STRING){	
                    	productId = String.valueOf(cell.getStringCellValue());
					}else if
					(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						productId = String.valueOf((int)cell.getNumericCellValue());
					}
					productExcelObj.setExternalProductId(productId);
					
					 break;
				case 2://AsiProdNo
					int asiProdNo = 0;
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						try{
							asiProdNo = Integer.parseInt(cell.getStringCellValue());
							productExcelObj.setAsiProdNo(Integer.toString(asiProdNo));
						}catch(NumberFormatException nfe){
							
						}
					  }else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						  asiProdNo = (int) cell.getNumericCellValue();
						  productExcelObj.setAsiProdNo(Integer.toString(asiProdNo));
					  }
					
					  break;
				case 3://Name
                    String name = cell.getStringCellValue();
					if(!StringUtils.isEmpty(name)){
					productExcelObj.setName(cell.getStringCellValue());
					}else{
						productExcelObj.setName(ApplicationConstants.CONST_STRING_EMPTY);
					}
     					
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
					   String catalogValue = cell.getStringCellValue();
						if(!StringUtils.isEmpty(catalogValue)){
						Catalog catalogObj=new Catalog();
						catalogObj.setCatalogName(catalogValue);
						catalogList.add(catalogObj);
						productExcelObj.setCatalogs(catalogList);
						}
					
					break;
					
				case 8: // Catalogs(Not used)
					
					
					break;
					
				case 9: //Catalogs page number
							
					break;
					
				case 10:  
						//Catalogs(Not used)
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
					String themeValue=cell.getStringCellValue();
					if(!StringUtils.isEmpty(themeValue)){
						Theme themeObj=null;
						String themeValueArr[] = themeValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
						for (String string : themeValueArr) {
							themeObj=new Theme();
							themeObj.setName(string);
							themeList.add(themeObj);
							}
					       productConfigObj.setThemes(themeList);
						}
					
					break;
					
				case 15://size --  value
					 dimensionValue =cell.getNumericCellValue();
					
					break;
				case 16: //size -- Unit
					 dimensionUnits =cell.getNumericCellValue();
					//String unit1=String.valueOf(Dimension1Units);
			
					  break;
				
				case 17: //size -- type
					 dimensionType =cell.getNumericCellValue();
					 if(dimensionType !=0 )
					 {
					 List<Values> valuesList =
						  dimParserObj.getValues(dimensionValue, dimensionUnits, dimensionType);
                     finalDimensionObj.setValues(valuesList);
					 }
                  
					break;
				
				 case 18: //size
					 dimensionValue =cell.getNumericCellValue();
					
					break;
					
				case 19:  //size
					dimensionUnits =cell.getNumericCellValue();
					//String unit2=String.valueOf(Dimension2Units);

					
					break;
					
				case 20: //size
					 dimensionType =cell.getNumericCellValue();
					 List<Values> valuesList1 =
							  dimParserObj.getValues(dimensionValue, dimensionUnits, dimensionType);
	                     finalDimensionObj.setValues(valuesList1);
	                  
					
					break;
					
				case 21: //size
			      dimensionValue =cell.getNumericCellValue();
					
					break;
					
				case 22: //size
					 dimensionUnits =cell.getNumericCellValue();
					

					
				   break;
					
				case 23: //size
					dimensionType =cell.getNumericCellValue();
					 dimensionType =cell.getNumericCellValue();
					 List<Values> valuesList2 =
							  dimParserObj.getValues(dimensionValue, dimensionUnits, dimensionType);
	                     finalDimensionObj.setValues(valuesList2);

					
				   break;
				   
				case 24:  // Quantities
				case 25: 
				case 26: 
				case 27: 
				case 28:
				case 29:
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
						_LOGGER.info("Error in base price Quantity field");
					}
					
					   	break;
				case 30:  // prices --list price
				case 31:
				case 32:
				case 33:
				case 34:
				case 35:
				 try{
					 if(cell.getCellType() == Cell.CELL_TYPE_STRING){
							quantity = cell.getStringCellValue();
					         if(!StringUtils.isEmpty(quantity)&& !quantity.equals("0")){
					        	 listOfPrices.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
							double quantity1 = (double)cell.getNumericCellValue();
					         if(!StringUtils.isEmpty(quantity1)){
					        	 listOfPrices.append(quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						}else{
						}  
				 }catch (Exception e) {
					_LOGGER.info("Error in base price prices field");
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
					try{
						
					}catch (Exception e) {
						_LOGGER.info("Error in pricePerUnit field");
					}
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						quantity = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(quantity) && !quantity.equals("0")){
				        	 pricesPerUnit.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						double quantity1 = (double)cell.getNumericCellValue();
				         if(!StringUtils.isEmpty(quantity1)){
				        	 pricesPerUnit.append(quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else{
					}  
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
						break;
						
				case 47:       
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
					quantity = cell.getStringCellValue();
			         if(!StringUtils.isEmpty(quantity)){
			        	 listOfNetPrice.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			         }
				}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
					double quantity1 = (double)cell.getNumericCellValue();
			         if(!StringUtils.isEmpty(quantity1)){
			        	 listOfNetPrice.append(quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			         }
				}else{
				}
				  break;
				case 48:
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
				case 69:
				Boolean IsEnvironmentallyFriendly = cell.getBooleanCellValue();
					Theme themeObj1 = new Theme();
					String str =new String();
					if(IsEnvironmentallyFriendly == true)			
					{	
						themeObj1.setName("Eco Friendly");	
					}
					else
					{
						themeObj1.setName("");
					}
					themeList.add(themeObj1);
					productConfigObj.setThemes(themeList);
					break;
				case 70:
				case 71:
				case 72:
				case 73:
				case 74:
				case 75: // Imprint size1
					
					    break;
					    
				case 76: //// Imprint size1 unit
					   	break;
					   	
				case 77:   // Imprint size1 Type    
						break;
						
				  
				case 78: // // Imprint size2
					  	break;
					  	
				case 79:	// Imprint size2 Unit
					    break;
					    
				case 80: // Imprint size2 Type
					  	break;
					  	
				case 81:  // Imprint location
					
					String imprintLocation = cell.getStringCellValue();
					ImprintLocation locationObj = new ImprintLocation();
					locationObj.setValue(imprintLocation);
					listImprintLocation.add(locationObj);
					    break;
					    
				case 82:  // Second Imprintsize1
					   	break;
					   	
				case 83:  // Second Imprintsize1 unit
						break;
						
				case 84:  // Second Imprintsize1 type
					  break;
					  
				case 85: // Second Imprintsize2
					break;
					
				case 86: //Second Imprintsize2 Unit
					break;
					
				case 87: // Second Imprintsize2 type	
					  break;
					  
				case 88: // Second Imprint location
					String imprintLocation2 = cell.getStringCellValue();
					ImprintLocation locationObj2 = new ImprintLocation();
					locationObj2.setValue(imprintLocation2);
					listImprintLocation.add(locationObj2);
					break;
				case 89: // DecorationMethod
					String decorationMethod = cell.getStringCellValue();
					listOfImprintMethods = imprintMethodParser.getImprintMethodValues(decorationMethod,
							                                                           listOfImprintMethods);
					 break; 
					 
				case 90: //NoDecoration
					String noDecoration = cell.getStringCellValue();
					if(noDecoration.equalsIgnoreCase(ApplicationConstants.CONST_STRING_TRUE)){
						listOfImprintMethods = imprintMethodParser.getImprintMethodValues(noDecoration,
                                listOfImprintMethods);
					}
					
					 break;
				case 91: //NoDecorationOffered
					String noDecorationOffered = cell.getStringCellValue();
					if(noDecorationOffered.equalsIgnoreCase(ApplicationConstants.CONST_STRING_TRUE)){
						listOfImprintMethods = imprintMethodParser.getImprintMethodValues(noDecorationOffered,
                                listOfImprintMethods);
					}
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
						List<Origin> listOfOrigin = originParser.getOriginValues(madeInCountry);
						productConfigObj.setOrigins(listOfOrigin);
					}
					break;
					
				case 101:// AssembledInCountry
					String assembledInCountry = cell.getStringCellValue();
					assembledInCountry = originParser.getCountryCodeConvertName(assembledInCountry);
					if(!assembledInCountry.isEmpty() && assembledInCountry != null){
						productExcelObj.setAdditionalProductInfo(assembledInCountry);
					}
					break;
				case 102: //DecoratedInCountry
					String decoratedInCountry = cell.getStringCellValue();
					decoratedInCountry = originParser.getCountryCodeConvertName(decoratedInCountry);
					if(!decoratedInCountry.isEmpty() && decoratedInCountry != null){
						productExcelObj.setAdditionalProductInfo(decoratedInCountry);
					}
					break;
				case 103: //ComplianceList  -- No data
					break;
				case 104://ComplianceMemo  -- No data
					break;
				case 105: //ProdTimeLo
					String prodTimeLo = null;
					ProductionTime productionTime = new ProductionTime();
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						prodTimeLo = cell.getStringCellValue();
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						prodTimeLo = String.valueOf(cell.getNumericCellValue());
					}else{
						
					}
					productionTime.setBusinessDays(prodTimeLo);
					listOfProductionTime.add(productionTime);
					break;
				case 106: //ProdTimeHi
					String prodTimeHi = null;
					ProductionTime productionTime1 = new ProductionTime();
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						prodTimeHi = cell.getStringCellValue();
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						prodTimeHi = String.valueOf(cell.getNumericCellValue());
					}else{
						
					}
					productionTime1.setBusinessDays(prodTimeHi);
					listOfProductionTime.add(productionTime1);
					break;
				case 107://RushProdTimeLo
					String rushProdTimeLo  = cell.getStringCellValue();
					if(!rushProdTimeLo.equals(ApplicationConstants.CONST_STRING_ZERO)){
						rushTime = rushTimeParser.getRushTimeValues(rushProdTimeLo, rushTime);
					}
					
					 break; 	 
				case 108://RushProdTimeH
					String rushProdTimeH  = cell.getStringCellValue();
					if(!rushProdTimeH.equals(ApplicationConstants.CONST_STRING_ZERO)){
						rushTime = rushTimeParser.getRushTimeValues(rushProdTimeH, rushTime);
					}
					break;
					
				case 109://Packaging
				
					String pack  = cell.getStringCellValue();
					List<Packaging> listOfPackaging = packagingParser.getPackageValues(pack);
					productConfigObj.setPackaging(listOfPackaging);
					break;
					
				case 110: //CartonL
					 cartonL  = cell.getStringCellValue();
					
					break;
				case 111://CartonW
					cartonW  = cell.getStringCellValue();
					break;
	
				case 112://CartonH
					cartonH  = cell.getStringCellValue();
					break;
				case 113: //WeightPerCarton
					weightPerCarton  = cell.getStringCellValue();
					break;
				case 114: //UnitsPerCarton
					unitsPerCarton  = cell.getStringCellValue();
					break;
					
				case 115: //ShipPointCountry

					break;
					
				case 116: //ShipPointZip
					
					break;
					
				case 117: //Comment
					

					break;
					
				case 118: //Verified
					break;
			
				case 119: //UpdateInventory
					
					break;
				
				case 120: //InventoryOnHand
					
					break;
					
				case 121: //InventoryOnHandAdd
					break;
					
				case 122: //InventoryMemo
				break;
				
			/*case 123: // not required
				//Item Colors2
				colorValue=cell.getStringCellValue();
				if(!StringUtils.isEmpty(colorValue)){
					//color=colorparser.getColorCriteria(colorValue);
				    
				if(color!=null){
				productConfigObj.setColors(color);
				}
				}
				break;
				
			case 124:
				//Item Type3
				System.out.println(124);
				break;
				
			case 125:
				//Item Colors3
				colorValue=cell.getStringCellValue();
				if(!StringUtils.isEmpty(colorValue)){
					//color=colorparser.getColorCriteria(colorValue);
				    
				if(color!=null){
				productConfigObj.setColors(color);
				}
				}
				break;
				
			case 126:
				//Item Type4
				System.out.println(126);
				break;
				
			case 127:
				//Item Colors4
				colorValue=cell.getStringCellValue();
				if(!StringUtils.isEmpty(colorValue)){
					//color=colorparser.getColorCriteria(colorValue);
				    
				if(color!=null){
				productConfigObj.setColors(color);
				}
				}
				break;
				
			case 128:
				//imprint Method1
				imprintValue=cell.getStringCellValue();
				  if(!StringUtils.isEmpty(imprintValue)){
					  imprintMethodValues.append(imprintValue + ",");
				  }
				break;

			case 129:
				//Imprint Location1
				break;
				
				
				
			case 130:
				//Imprint Colors1
				imprintColorValue = cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintColorValue)){
					//imprintColorValues=imprintColorParser.getImprintColorValues(imprintColorValue,imprintColorValues);
				}
				break;
				
			case 131:
				//imprint method2
				imprintValue=cell.getStringCellValue();
				
				 if(!StringUtils.isEmpty(imprintValue)){
					  imprintMethodValues.append(imprintValue + ",");
				  }
				 break;
				
			case 132:
				//Imprint Location2
				break;
				
			case 133:
				//Imprint colors2 (it is related imprint method values)
				imprintColorValue = cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintColorValue)){
					//imprintColorValues=imprintColorParser.getImprintColorValues(imprintColorValue,imprintColorValues);
					if(imprintColorValue.equalsIgnoreCase("Laser Engraved")){
						imprintMethodValues.append(imprintColorValue + ",");
					}	
				}
				break;
				
				
			case 134:
				//Imprint method3
				imprintValue=cell.getStringCellValue();
				 if(!StringUtils.isEmpty(imprintValue)){
					  imprintMethodValues.append(imprintValue + ",");
				  }
				break;
				
				
				
			case 135:
				//Imprint location3
				break;
				
				
				
			case 136:
				//Imprint Colors3
				imprintColorValue = cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintColorValue)){
					//imprintColorValues=imprintColorParser.getImprintColorValues(imprintColorValue,imprintColorValues);
				}
				break;
				
			case 137:
				//Imprint method 4
				imprintValue=cell.getStringCellValue();
				 if(!StringUtils.isEmpty(imprintValue)){
					  imprintMethodValues.append(imprintValue + ",");
				  }
				break;
				
			case 138:
				//Imprint location4
				break;
				
				
				
			case 139:
				//Imprint Colors4
				imprintColorValue = cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintColorValue)){
					//imprintColorValues=imprintColorParser.getImprintColorValues(imprintColorValue,imprintColorValues);
				}
				break;
				
				
			case 140:
				//Imprint method 5
				imprintValue=cell.getStringCellValue();
				 if(!StringUtils.isEmpty(imprintValue)){
					  imprintMethodValues.append(imprintValue + ",");
				  }
				break;
				
			case 141:
				//Imprint location5
				break;
				
				
				
			case 142:
				//Imprint Colors5
				imprintColorValue = cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintColorValue)){
					//imprintColorValues=imprintColorParser.getImprintColorValues(imprintColorValue,imprintColorValues);
				}
				break;
				
				
				
			case 143:
				//Imprint method 6
				imprintValue=cell.getStringCellValue();
				 if(!StringUtils.isEmpty(imprintValue)){
					  imprintMethodValues.append(imprintValue + ",");
				  }
				break;
				
			case 144:
				//Imprint location6
				break;
				
				
				
			case 145:
				//Imprint Colors6
				imprintColorValue = cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintColorValue)){
					//imprintColorValues=imprintColorParser.getImprintColorValues(imprintColorValue,imprintColorValues);
     			}
				break;
				
				
			case 146:
				//Imprint method 7
				imprintValue=cell.getStringCellValue();
				 if(!StringUtils.isEmpty(imprintValue)){
					  imprintMethodValues.append(imprintValue + ",");
				  }
				break;
				
			case 147:
				//Imprint location7
				break;
				
				
				
			case 148:
				//Imprint Colors7
				imprintColorValue = cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintColorValue)){
					//imprintColorValues=imprintColorParser.getImprintColorValues(imprintColorValue,imprintColorValues);
				}
				break;
				
			case 149:
				//Selections

				break;
				
			case 150:
				String artwork = cell.getStringCellValue();
				if(!StringUtils.isEmpty(artwork)){
				//artworkList=artworkProcessor.getArtworkCriteria(artwork);
				if(artworkList!=null){
				productConfigObj.setArtwork(artworkList);
				}}
				break;
				
				
			case 151:
				break;
				
			case 152:
				
			//Option Charges
				break;
				//productExcelObj.setProductConfigurations(productConfigObj);l
				
			case 153:
				//Additional Product Information
				String artworK = cell.getStringCellValue();
				if(!StringUtils.isEmpty(artworK)){
				//artworkList=artworkProcessor.getArtworkCriteria(artworK);
				if(artworkList!=null){
				productConfigObj.setArtwork(artworkList);
				}}
				break;
			
			case 154:
				//FOB Ship From Zip
				break;
				
			case 155:
				//FOB Bill From Zip
				break;*/   // not required for 123 cases onwards
			}  // end inner while loop
					 
		}
			// set  product configuration objects
			List<String> listOfCategories = new ArrayList<String>();
			listOfCategories.add("USB/FLASH DRIVES");
			productExcelObj.setCategories(listOfCategories);
			String productDescription ="Phone Holder USB 2.0 Flash Drive";
			//productDescription = "Phone Holder USB 2.0 Flash Drive";
			productExcelObj.setDescription(productDescription);
			ShippingEstimate shipping = shippingEstimateParser.getShippingEstimateValues(cartonL, cartonW,
					                               cartonH, weightPerCarton, unitsPerCarton);
			productConfigObj.setImprintLocation(listImprintLocation);
			productConfigObj.setImprintMethods(listOfImprintMethods);
			productConfigObj.setRushTime(rushTime);
			productConfigObj.setShippingEstimates(shipping);
			productConfigObj.setProductionTime(listOfProductionTime);
			size.setDimension(finalDimensionObj);
			productConfigObj.setImprintMethods(imprintMethods); 
			productConfigObj.setSizes(size);
			//imprintColorsValueList = imprintColorParser.getImprintColorCriteria(imprintColorValues.toString());
			//imprintColors.setType("COLR");
			//imprintColors.setValues(imprintColorsValueList);
			//productConfigObj.setImprintColors(imprintColors);
			
				//productExcelObj.setProductConfigurations(productConfigObj);l
			 // end inner while loop
			productExcelObj.setPriceType("L");
			if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
				priceGrids = priceGridParser.getPriceGrids(listOfPrices.toString(), 
						         listOfQuantity.toString(), priceCode, "USD",
						         priceIncludes.toString(), true, quoteUponRequest, productName,"",priceGrids);	
			}
			
			 
				/*if(UpCharCriteria != null && !UpCharCriteria.toString().isEmpty()){
					priceGrids = priceGridParser.getUpchargePriceGrid(UpCharQuantity.toString(), UpCharPrices.toString(), UpCharDiscount.toString(), UpCharCriteria.toString(), 
							 upChargeQur, currencyType, upChargeName, upchargeType, upChargeLevel, new Integer(1), priceGrids);
				}*/
				
				/*if(!StringUtils.isEmpty(optionname) && !StringUtils.isEmpty(optiontype) && !StringUtils.isEmpty(optionvalues) ){
					optionobj=optionparserobj.getOptions(optiontype, optionname, optionvalues, canorder, reqfororder, optionadditionalinfo);
					option.add(optionobj);		
					productConfigObj.setOptions(option);	
				}*/
				
				upChargeQur = null;
				UpCharCriteria = new StringBuilder();
				priceQurFlag = null;
				listOfPrices = new StringBuilder();
				UpCharPrices = new StringBuilder();
				UpCharDiscount = new StringBuilder();
				UpCharQuantity = new StringBuilder();
				skuvalue = null;
			    Inlink = null;
			    Instatus = null;
			    InQuantity = null;
			    SKUCriteria1 = null;
			    SKUCriteria2 = null;
			    productNumberCriteria1=null; 
			    productNumberCriteria2=null;
			    productNumber=null;
			    optiontype=null;
			    optionname=null;
			    optionvalues=null;
			    canorder=null;
			    reqfororder=null;
			    optionadditionalinfo=null;
			
			}catch(Exception e){
			//e.printStackTrace();
			_LOGGER.error("Error while Processing Product :"+productExcelObj.getExternalProductId() );		 
		}
		}
		workbook.close();
		//inputStream.close();
		   // Add repeatable sets here
		 	productExcelObj.setPriceGrids(priceGrids);
		 	productExcelObj.setProductConfigurations(productConfigObj);
		 	/*productExcelObj.setProductRelationSkus(productsku);
		 	productExcelObj.setProductNumbers(pnumberList);*/
		 	//productList.add(productExcelObj);
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber);
		 	if(num ==1){
		 		numOfProducts.add("1");
		 	}
		 	_LOGGER.info("list size>>>>>>"+numOfProducts.size());
			//System.out.println(mapper1.writeValueAsString(productExcelObj));
	
		}catch(Exception e){
			_LOGGER.error("Error while Processing excel sheet ");
			return 0;
		}finally{
			try {
				workbook.close();
			//inputStream.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet");
	
			}
				_LOGGER.info("Complted processing of excel sheet ");
				_LOGGER.info("Total no of product:"+numOfProducts.size() );
				return numOfProducts.size();
		}
		
	
	}
	
	public PostServiceImpl getPostServiceImpl() {
		return postServiceImpl;
	}

	public void setPostServiceImpl(PostServiceImpl postServiceImpl) {
		this.postServiceImpl = postServiceImpl;
	}
	public CatalogParser getCatalogParser() {
		return catalogParser;
	}

	public void setCatalogParser(CatalogParser catalogParser) {
		this.catalogParser = catalogParser;
	}

	public PriceGridParser getPriceGridParser() {
		return priceGridParser;
	}

	public void setPriceGridParser(PriceGridParser priceGridParser) {
		this.priceGridParser = priceGridParser;
	}
	public ImprintMethodParser getImprintMethodParser() {
		return imprintMethodParser;
	}

	public void setImprintMethodParser(ImprintMethodParser imprintMethodParser) {
		this.imprintMethodParser = imprintMethodParser;
	}
	public OriginParser getOriginParser() {
		return originParser;
	}

	public void setOriginParser(OriginParser originParser) {
		this.originParser = originParser;
	}
	public RushTimeParser getRushTimeParser() {
		return rushTimeParser;
	}

	public void setRushTimeParser(RushTimeParser rushTimeParser) {
		this.rushTimeParser = rushTimeParser;
	}
	public PackagingParser getPackagingParser() {
		return packagingParser;
	}

	public void setPackagingParser(PackagingParser packagingParser) {
		this.packagingParser = packagingParser;
	}
	public ShippingEstimateParser getShippingEstimateParser() {
		return shippingEstimateParser;
	}

	public void setShippingEstimateParser(
			ShippingEstimateParser shippingEstimateParser) {
		this.shippingEstimateParser = shippingEstimateParser;
	}
}
