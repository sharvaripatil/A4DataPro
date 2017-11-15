package com.a4tech.sage.product.mapping;

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
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
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
import com.a4tech.product.model.Value;
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
import com.a4tech.util.CommonUtility;


public class SageProductsExcelMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(SageProductsExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private CatalogParser   catalogParser;
	private PriceGridParser priceGridParser;
	private ImprintMethodParser imprintMethodParser;
	private OriginParser       originParser;
	private RushTimeParser    rushTimeParser;
	private PackagingParser	packagingParser;
	private ShippingEstimateParser shippingEstimateParser;
	Size size=new Size();
	DimensionParser dimParserObj;
	ColorParser colorParserObj ;
	ProductDao productDaoObj;
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  String productId = null;
		  String currencyType = null;
		  String priceQurFlag = null;
		  String priceType    = null;
		  String basePriceName = null;
		
		  String upChargeName = null;
		  String upChargeQur = null;
		  String upchargeType = null;
		  String upChargeDetails = null;
		  String upChargeLevel = null;
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  
		  StringBuilder dimensionValue = new StringBuilder();
		  StringBuilder dimensionUnits = new StringBuilder();
		  StringBuilder dimensionType = new StringBuilder();
		  Dimension finalDimensionObj=new Dimension();
		  Dimension existingDimensiobn;

		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		
		
		StringBuilder listOfQuantity = new StringBuilder();
		StringBuilder listOfPrices = new StringBuilder();
		StringBuilder listOfDiscount = new StringBuilder();
	
		String		priceCode = null;
		StringBuilder pricesPerUnit = new StringBuilder();
		String quoteUponRequest  = null;
		StringBuilder priceIncludes = new StringBuilder();
		String quantity = null;
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
		String ListPrice =null;
		String Discountcode = null;
		String decorationMethod =null;
		
		List<Color> color = new ArrayList<Color>();
		List<ImprintMethod> imprintMethods = new ArrayList<ImprintMethod>();
		String productName = null;
		List<ImprintLocation> listImprintLocation = new ArrayList<ImprintLocation>();
		List<ImprintMethod> listOfImprintMethods = new ArrayList<ImprintMethod>();
		List<ProductionTime> listOfProductionTime = new ArrayList<ProductionTime>();
		RushTime rushTime  = new RushTime();
		List<String> productKeywords = new ArrayList<String>();
		List<Theme> themeList = new ArrayList<Theme>();
		List<Catalog> catalogList = new ArrayList<Catalog>();
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum() < 7)
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
						 if(nextRow.getRowNum() != 7){
							 System.out.println("Java object converted to JSON String, written to file");
							 	productExcelObj.setPriceGrids(priceGrids);
							 	productExcelObj.setProductConfigurations(productConfigObj);
							 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId, environmentType);
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
								catalogList = new ArrayList<Catalog>();
								productKeywords = new ArrayList<String>();
								listOfProductionTime = new ArrayList<ProductionTime>();
								rushTime = new RushTime();
								listImprintLocation = new ArrayList<ImprintLocation>();
								listOfImprintMethods = new ArrayList<ImprintMethod>();
								
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
					 String asiProdNo = null;
					    if(cell.getCellType() == Cell.CELL_TYPE_STRING){ 
					      asiProdNo = String.valueOf(cell.getStringCellValue());
					    }else if
					     (cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
					      asiProdNo = String.valueOf((int)cell.getNumericCellValue());
					     }
					     productExcelObj.setAsiProdNo(asiProdNo);		
					  break;
				case 3://Name
                     productName = cell.getStringCellValue();
					if(!StringUtils.isEmpty(productName)){
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
						}
					
					break;
					
				case 15://size --  value
						String dimensionValue1= null;
					 if(cell.getCellType() == Cell.CELL_TYPE_STRING){	
				         dimensionValue1 =cell.getStringCellValue();
					 }else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
					    dimensionValue1 =String.valueOf((int)cell.getNumericCellValue());
					 }
					   if(dimensionValue1 != null && !dimensionValue1.isEmpty()){
						   dimensionValue.append(dimensionValue1).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					   }
					
					break;
				case 16: //size -- Unit
					  String dimensionUnits1 = null;
					 if(cell.getCellType() == Cell.CELL_TYPE_STRING){	
						 dimensionUnits1 =cell.getStringCellValue();
					 }else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						 dimensionUnits1 =String.valueOf((int)cell.getNumericCellValue());
					 }
					 if(dimensionUnits1 != null && !dimensionUnits1.isEmpty()){
						 dimensionUnits.append(dimensionUnits1).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					 }
					  break;
				
				case 17: //size -- type
					String dimensionType1 =null;
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){	
						dimensionType1 =cell.getStringCellValue();
					 }else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						 dimensionType1 =String.valueOf((int)cell.getNumericCellValue());
					 }
					if(!dimensionType1.isEmpty()){
						dimensionType.append(dimensionType1).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					  
					 
					break;
				
				 case 18: //size
					 String dimensionValue2 =null;
					 if(cell.getCellType() == Cell.CELL_TYPE_STRING){	
						 dimensionValue2 =cell.getStringCellValue();
						 }else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
							 dimensionValue2 =String.valueOf((int)cell.getNumericCellValue());
						 }
					 if(dimensionValue2 != null && !dimensionValue2.isEmpty()){
						 dimensionValue.append(dimensionValue2).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					 }
				
					break;
					
				case 19:  //size
					String dimensionUnits2 =null;
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){	
						dimensionUnits2 =cell.getStringCellValue();
						 }else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
							 dimensionUnits2 =String.valueOf((int)cell.getNumericCellValue());
						 }
					if(dimensionUnits2 != null && !dimensionUnits2.isEmpty()){
						dimensionUnits.append(dimensionUnits2).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					break;
					
				case 20: //size
					String  dimensionType2 = null;
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){	
						dimensionType2 =cell.getStringCellValue();
						 }else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
							 dimensionType2 =String.valueOf((int)cell.getNumericCellValue());
						 }
					if(dimensionType2 != null && !dimensionType2.isEmpty()){
						dimensionType.append(dimensionType2).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					
					break;
					
				case 21: //size
					String dimensionValue3  = null;
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){	
						dimensionValue3 =cell.getStringCellValue();
						 }else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
							 dimensionValue3 =String.valueOf((int)cell.getNumericCellValue());
						 }
					if(dimensionValue3 != null && !dimensionValue3.isEmpty()){
						dimensionValue.append(dimensionValue3).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					break;
					
					
				case 22: //size
					String dimensionUnits3 = null;
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){	
						dimensionUnits3 =cell.getStringCellValue();
						 }else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
							 dimensionUnits3 =String.valueOf((int)cell.getNumericCellValue());
						 }
					if(dimensionUnits3 != null && !dimensionUnits3.isEmpty()){
						 dimensionUnits.append(dimensionUnits3).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
				   break;
					
				case 23: //size
					String dimensionType3 = null;
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){	
						dimensionType3 =cell.getStringCellValue();
						 }else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
							 dimensionType3 =String.valueOf((int)cell.getNumericCellValue());
						 }
					if(dimensionType3 != null && !dimensionType3.isEmpty()){
						dimensionType.append(dimensionType3).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					

					
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
						_LOGGER.info("Error in base price Quantity field "+e.getMessage());
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
					_LOGGER.info("Error in base price prices field "+e.getMessage());
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
						_LOGGER.info("Error in pricePerUnit field "+e.getMessage());
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
						
				case 47:    //setup charge   
					 try{
						  ListPrice = CommonUtility.getCellValueDouble(cell);
					     if(!StringUtils.isEmpty(ListPrice) && !ListPrice.equals("0")){
				        	 listOfPrices.append(ListPrice).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
					}catch (Exception e) {
						 _LOGGER.info("Error in base price prices field "+e.getMessage());							
					}
				  break;
				case 48://setup discount code
					
					try{
						  Discountcode = CommonUtility.getCellValueStrinOrInt(cell);
					     if(!StringUtils.isEmpty(Discountcode) && !Discountcode.equals("0")){
				        	 listOfDiscount.append(Discountcode).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
					}catch (Exception e) {
			        	 _LOGGER.info("Error in pricePerUnit field "+e.getMessage());
					}
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
				String IsEnvironmentallyFriendly = cell.getStringCellValue();
			
					if(IsEnvironmentallyFriendly.equalsIgnoreCase("true"))			
					{ Theme themeObj1 = new Theme();

						themeObj1.setName("Eco Friendly");	

						themeList.add(themeObj1);
					}
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
					if(!imprintLocation.isEmpty()){
						ImprintLocation locationObj = new ImprintLocation();
						locationObj.setValue(imprintLocation);
						listImprintLocation.add(locationObj);
					}
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
					if(!imprintLocation2.isEmpty()){
						ImprintLocation locationObj2 = new ImprintLocation();
						locationObj2.setValue(imprintLocation2);
						listImprintLocation.add(locationObj2);
					}
					break;
				case 89: // DecorationMethod
					 decorationMethod = cell.getStringCellValue();
					listOfImprintMethods = imprintMethodParser.getImprintMethodValues(decorationMethod,listOfImprintMethods);
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
					if(!assembledInCountry.isEmpty()){
						assembledInCountry = originParser.getCountryCodeConvertName(assembledInCountry);
						productExcelObj.setAdditionalProductInfo(assembledInCountry);
					}
					break;
				case 102: //DecoratedInCountry
					String decoratedInCountry = cell.getStringCellValue();
					if(!decoratedInCountry.isEmpty()){
						decoratedInCountry = originParser.getCountryCodeConvertName(decoratedInCountry);
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
			
			}  // end inner while loop
					 
		}
			// set  product configuration objects
			List<String> listOfCategories = new ArrayList<String>();
			productExcelObj.setCategories(listOfCategories);
			ShippingEstimate shipping = shippingEstimateParser.getShippingEstimateValues(cartonL, cartonW,
					                               cartonH, weightPerCarton, unitsPerCarton);
			productConfigObj.setImprintLocation(listImprintLocation);
			productConfigObj.setImprintMethods(listOfImprintMethods);
			productConfigObj.setThemes(themeList);
			productConfigObj.setRushTime(rushTime);
			productConfigObj.setShippingEstimates(shipping);
			productConfigObj.setProductionTime(listOfProductionTime);
			List<Values> valuesList =dimParserObj.getValues(dimensionValue.toString(),
					                                            dimensionUnits.toString(), dimensionType.toString());
            finalDimensionObj.setValues(valuesList);	
			size.setDimension(finalDimensionObj);
			productConfigObj.setSizes(size);
			dimensionValue = new  StringBuilder();
			dimensionUnits = new  StringBuilder();
			dimensionType = new  StringBuilder();
			
			 // end inner while loop
			productExcelObj.setPriceType("L");
			if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
				priceGrids = priceGridParser.getPriceGrids(listOfPrices.toString(), 
						         listOfQuantity.toString(), priceCode, "USD",
						         priceIncludes.toString(), true, quoteUponRequest, productName,"",priceGrids);	
			}
			
		
			if( decorationMethod != null && !decorationMethod.toString().isEmpty())
			{
			
			priceGrids = priceGridParser.getUpchargePriceGrid("1", ListPrice, Discountcode, "Imprint Method", 
							"false", "USD", decorationMethod, "Imprint Method Charge", "Other", new Integer(1), priceGrids);
			}
				
				
				upChargeQur = null;
				priceQurFlag = null;
				listOfPrices = new StringBuilder();
			    listOfQuantity = new StringBuilder();
			    optiontype=null;
			    optionname=null;
			    optionvalues=null;
			    canorder=null;
			    reqfororder=null;
			    optionadditionalinfo=null;
			
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() );		 
		}
		}
		workbook.close();
		
		 	productExcelObj.setPriceGrids(priceGrids);
		 	productExcelObj.setProductConfigurations(productConfigObj);
	
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

	public DimensionParser getDimParserObj() {
		return dimParserObj;
	}

	public void setDimParserObj(DimensionParser dimParserObj) {
		this.dimParserObj = dimParserObj;
	}

	public ColorParser getColorParserObj() {
		return colorParserObj;
	}

	public void setColorParserObj(ColorParser colorParserObj) {
		this.colorParserObj = colorParserObj;
	}
	public ProductDao getProductDaoObj() {
		return productDaoObj;
	}

	public void setProductDaoObj(ProductDao productDaoObj) {
		this.productDaoObj = productDaoObj;
	}
}
