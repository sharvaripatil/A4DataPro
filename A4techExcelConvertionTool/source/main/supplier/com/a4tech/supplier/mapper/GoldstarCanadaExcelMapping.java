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
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimension;
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
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;


public class GoldstarCanadaExcelMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(GoldstarCanadaExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private GoldstarCanadaDimensionParser gcdimensionObj;
	private GoldstarCanadaImprintMethodParser gcimprintMethodParser;
	private GoldstarCanadaOriginParser gcOriginParser;
	private GoldstarCanadaRushTimeParser gcRushTimeParser;
	private GoldstarCanadaPackagingParser gcPackagingParser;
	private GoldstarCanadaShippingEstimateParser gcShippingParser;
	private GoldstarCanadaPriceGridParser gcPricegridParser;
	private GoldstarCanadaImprintsizeParser gcImprintSizeParser;
	private GoldstarCanadaColorParser gccolorparser;

	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
		
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
		String priceConfirmedThru =null;
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
	    StringBuilder ImprintSizevalue = new StringBuilder();
	  
		
		
		
		
		
		
		
		List<Color> colorList = new ArrayList<Color>();
		String productName = null;
		List<ImprintLocation> listImprintLocation = new ArrayList<ImprintLocation>();
		List<ImprintMethod> listOfImprintMethods = new ArrayList<ImprintMethod>();
		List<ProductionTime> listOfProductionTime = new ArrayList<ProductionTime>();
		RushTime rushTime  = new RushTime();
		List<String> productKeywords = new ArrayList<String>();
		List<Theme> themeList = new ArrayList<Theme>();
		List<Catalog> catalogList = new ArrayList<Catalog>();
		List<String> complianceList = new ArrayList<String>();
		Size size=new Size();
		List<Image> listOfImages= new ArrayList<Image>();
	    List<ImprintSize> imprintSizeList =new ArrayList<ImprintSize>();
		Product existingApiProduct = null;
		
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
								listOfImages= new ArrayList<Image>();
								imprintSizeList =new ArrayList<ImprintSize>();
								colorList = new ArrayList<Color>();
								
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    }
						    
						     if(existingApiProduct == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
//						  	   productExcelObj=existingApiProduct;
								productConfigObj=existingApiProduct.getProductConfigurations();
						    	String confthruDate=existingApiProduct.getPriceConfirmedThru();
						    	List<Color> colorL=productConfigObj.getColors();
						    	 List<Image> Img=existingApiProduct.getImages();
						    	 productExcelObj.setPriceConfirmedThru(confthruDate);
						    	 productExcelObj.setImages(Img);
						    	 productConfigObj.setColors(colorL);
						     
						     }
							//productExcelObj = new Product();
					 }
				}
				

				switch (columnIndex + 1) {
				case 1://ExternalProductID
					   productExcelObj.setExternalProductId(xid);
					
					 break;
				case 2://AsiProdNo
					 String asiProdNo=CommonUtility.getCellValueStrinOrInt(cell);
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
					/* priceConfirmedThru = cell.getStringCellValue();*/
		
					break;
					
				case 6: //  product status ,discontinued
					break;
					
				case 7://Cat1Name
					
					break;
					
				case 8: // Cat2Name

					break;
					
				case 9: //Catalogs page number, Page1

					break;
					
				case 10:  
						//Catalogs(Not used),Page2
					
					break;
					
				case 11:  //Description
					String description =CommonUtility.getCellValueStrinOrInt(cell);
					int length=description.length();
					if(length>800){
						String strTemp=description.substring(0, 800);
						int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
						description=(String) strTemp.subSequence(0, lenTemp);
					}
					productExcelObj.setDescription(description);
									
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
						colorList=gccolorparser.getColorCriteria(colorValue);
						productConfigObj.setColors(colorList);
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
					/*try{
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
					*/
					   	break;
				case 30:  // prices --list price
				case 31:
				case 32:
				case 33:
				case 34:
				case 35:
				 /*try{
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
				}*/
					
					    break; 
				case 36: // price code -- discount
					    
/*						priceCode = cell.getStringCellValue();
*/					     break;
				case 37:       // pricesPerUnit
				case 38:
				case 39:
				case 40:
				case 41:
				case 42:
				/*	try{
						
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
					}  */
					      break;
				case 43:
					 //    quoteUponRequest = cell.getStringCellValue();
					      break;
				case 44:  // priceIncludeClr
					    
					    //  priceIncludes.append(cell.getStringCellValue()).append(" ");
					     break;
				case 45: // priceIncludeSide
						
						//priceIncludes.append(cell.getStringCellValue()).append(" ");
						break;
				case 46: // priceIncludeLoc
					//	priceIncludes.append(cell.getStringCellValue());
						break;
						
				case 47:    //setup charge   
					/* try{
						  ListPrice = CommonUtility.getCellValueDouble(cell);
					     if(!StringUtils.isEmpty(ListPrice) && !ListPrice.equals("0")){
				        	 listOfPrices.append(ListPrice).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
					}catch (Exception e) {
						 _LOGGER.info("Error in base price prices field "+e.getMessage());							
					}*/
				  break;
				case 48://setup discount code
					
					/*try{
						  Discountcode = CommonUtility.getCellValueStrinOrInt(cell);
					     if(!StringUtils.isEmpty(Discountcode) && !Discountcode.equals("0")){
				        	 listOfDiscount.append(Discountcode).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
					}catch (Exception e) {
			        	 _LOGGER.info("Error in pricePerUnit field "+e.getMessage());
					}*/
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
					FirstImprintsize1=cell.getStringCellValue();
					
					
					    break;
					    
				case 76: //// Imprint size1 unit
					FirstImprintunit1=cell.getStringCellValue();
					FirstImprintunit1=GoldstarCanadaLookupData.Dimension1Units.get(FirstImprintunit1);
					   	break;
					   	
				case 77:   // Imprint size1 Type
					FirstImprinttype1=cell.getStringCellValue();
					FirstImprinttype1=GoldstarCanadaLookupData.Dimension1Type.get(FirstImprinttype1);
						break;
						
				  
				case 78: // // Imprint size2
					FirstImprintsize2=cell.getStringCellValue();
					  	break;
					  	
				case 79:	// Imprint size2 Unit
					FirstImprintunit2=cell.getStringCellValue();
					FirstImprintunit2=GoldstarCanadaLookupData.Dimension1Units.get(FirstImprintunit2);

					
					    break;
					    
				case 80: // Imprint size2 Type
					FirstImprinttype2=cell.getStringCellValue();
					FirstImprinttype2=GoldstarCanadaLookupData.Dimension1Type.get(FirstImprinttype2);

				   
					
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
					SecondImprintsize1=cell.getStringCellValue();
				
					   	break;
					   	
				case 83:  // Second Imprintsize1 unit
					SecondImprintunit1=cell.getStringCellValue();
					if(!StringUtils.isEmpty(SecondImprintunit1)){
					SecondImprintunit1=GoldstarCanadaLookupData.Dimension1Units.get(SecondImprintunit1);
					}
					
						break;
						
				case 84:  // Second Imprintsize1 type
					SecondImprinttype1=cell.getStringCellValue();
					if(!StringUtils.isEmpty(SecondImprinttype1)){
					SecondImprinttype1=GoldstarCanadaLookupData.Dimension1Type.get(SecondImprinttype1);
					}
					
					  break;
					  
				case 85: // Second Imprintsize2
					SecondImprintsize2=cell.getStringCellValue();
					break;
					
				case 86: //Second Imprintsize2 Unit
					SecondImprintunit2=cell.getStringCellValue();
					if(!StringUtils.isEmpty(SecondImprintunit2)){
					SecondImprintunit2=GoldstarCanadaLookupData.Dimension1Units.get(SecondImprintunit2);
					}

					break;
					
				case 87: // Second Imprintsize2 type	
					SecondImprinttype2=cell.getStringCellValue();
					if(!StringUtils.isEmpty(SecondImprinttype2)){
					SecondImprinttype2=GoldstarCanadaLookupData.Dimension1Type.get(SecondImprinttype2);
					}
					
					  ImprintSizevalue=ImprintSizevalue.append(FirstImprintsize1).append(" ").append(FirstImprintunit1).
					  append(" ").append(FirstImprinttype1).append("x").append(FirstImprintsize2).append(" ").
					  append(FirstImprintunit2).append(" ").append(FirstImprinttype2).append(",").
					  append(SecondImprintsize1).append(" ").append(SecondImprintunit1).
					  append(" ").append(SecondImprinttype1).append("x").append(SecondImprintsize2).append(" ").
					  append(SecondImprintunit2).append(" ").append(SecondImprinttype2);
					
					  imprintSizeList=gcImprintSizeParser.getimprintsize(ImprintSizevalue);
					
					
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
					listOfImprintMethods = gcimprintMethodParser.getImprintMethodValues(decorationMethod,listOfImprintMethods);
					 break; 
					 
				case 90: //NoDecoration
					String noDecoration = cell.getStringCellValue();
					if(noDecoration.equalsIgnoreCase(ApplicationConstants.CONST_STRING_TRUE)){
						listOfImprintMethods = gcimprintMethodParser.getImprintMethodValues(noDecoration,
                                listOfImprintMethods);
					}
					
					 break;
				case 91: //NoDecorationOffered
					String noDecorationOffered = cell.getStringCellValue();
					if(noDecorationOffered.equalsIgnoreCase(ApplicationConstants.CONST_STRING_TRUE)){
						listOfImprintMethods = gcimprintMethodParser.getImprintMethodValues(noDecorationOffered,
                                listOfImprintMethods);
					}
					 break;
				case 92: //NewPictureURL
					String ImageValue1=cell.getStringCellValue();
					 Image image = new Image();
					 if(!StringUtils.isEmpty(ImageValue1)){
					// Image image = new Image();
				      image.setImageURL(ImageValue1);
				      image.setIsPrimary(true);
				      image.setRank(1);
				      listOfImages.add(image);
					  }
					break;
				case 93:  //NewPictureFile  -- not used
					break;
				case 94: //ErasePicture -- not used
					break;
				case 95: //NewBlankPictureURL
					String ImageValue2=cell.getStringCellValue();
					if(!StringUtils.isEmpty(ImageValue2))
					{
						  image = new Image();
					      image.setImageURL(ImageValue2);
					      image.setIsPrimary(false);
					      image.setRank(2);
					      listOfImages.add(image);
					}
				
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
						List<Origin> listOfOrigin = gcOriginParser.getOriginValues(madeInCountry);
						productConfigObj.setOrigins(listOfOrigin);
					}
					break;
					
				case 101:// AssembledInCountry
			     String additionalProductInfo = cell.getStringCellValue();
			     if(!StringUtils.isEmpty(additionalProductInfo))
			       {
			    	productExcelObj.setAdditionalProductInfo(additionalProductInfo); 
			       }
				
					break;
				case 102: //DecoratedInCountry
					String additionalImprintInfo = cell.getStringCellValue();
					 if(!StringUtils.isEmpty(additionalImprintInfo))
					   {
						 productExcelObj.setAdditionalImprintInfo(additionalImprintInfo);
					   }
					
					break;
				case 103: //ComplianceList  -- No data
					String complnceValuet=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(complnceValuet))
					   {
				    	complianceList.add(complnceValuet);
				    	productExcelObj.setComplianceCerts(complianceList);
					   }
					break;
					
				case 104://ComplianceMemo  -- No data
					String productDataSheet=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(productDataSheet))
					   {
						 productExcelObj.setProductDataSheet(productDataSheet);
					   }
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
						rushTime = gcRushTimeParser.getRushTimeValues(rushProdTimeLo, rushTime);
					}
					
					 break; 	 
				case 108://RushProdTimeH
					String rushProdTimeH  = cell.getStringCellValue();
					if(!rushProdTimeH.equals(ApplicationConstants.CONST_STRING_ZERO)){
						rushTime = gcRushTimeParser.getRushTimeValues(rushProdTimeH, rushTime);
					}
					break;
					
				case 109://Packaging
				
					String pack  = cell.getStringCellValue();
					List<Packaging> listOfPackaging = gcPackagingParser.getPackageValues(pack);
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
					String verified=cell.getStringCellValue();
					if(verified.equalsIgnoreCase("True")){
					String strArr[]=priceConfirmedThru.split("/");
					priceConfirmedThru=strArr[2]+"/"+strArr[0]+"/"+strArr[1];
					priceConfirmedThru=priceConfirmedThru.replaceAll("/", "-");
					 
					productExcelObj.setPriceConfirmedThru(priceConfirmedThru);
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
			List<String> listOfCategories = new ArrayList<String>();
			productExcelObj.setCategories(listOfCategories);
			ShippingEstimate shipping = gcShippingParser.getShippingEstimateValues(cartonL, cartonW,
					                               cartonH, weightPerCarton, unitsPerCarton);
			productConfigObj.setImprintLocation(listImprintLocation);
			productConfigObj.setImprintMethods(listOfImprintMethods);
			productConfigObj.setThemes(themeList);
			productConfigObj.setRushTime(rushTime);
			productConfigObj.setShippingEstimates(shipping);
			productConfigObj.setProductionTime(listOfProductionTime);
			List<Values> valuesList =gcdimensionObj.getValues(dimensionValue.toString(),
					                                            dimensionUnits.toString(), dimensionType.toString());
            finalDimensionObj.setValues(valuesList);	
			size.setDimension(finalDimensionObj);
			productConfigObj.setSizes(size);
			productConfigObj.setImprintSize(imprintSizeList);
			productExcelObj.setImages(listOfImages);
			productConfigObj.setColors(colorList);
			dimensionValue = new  StringBuilder();
			dimensionUnits = new  StringBuilder();
			dimensionType = new  StringBuilder();
			
			 // end inner while loop
			productExcelObj.setPriceType("L");
			if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
				priceGrids = gcPricegridParser.getPriceGrids(listOfPrices.toString(), 
						         listOfQuantity.toString(), priceCode, "USD",
						         priceIncludes.toString(), true, quoteUponRequest, productName,"",priceGrids);	
			}
			
		
			if( decorationMethod != null && !decorationMethod.toString().isEmpty())
			{
			
			priceGrids = gcPricegridParser.getUpchargePriceGrid("1", ListPrice, Discountcode, "Imprint Method", 
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
			    listOfImages= new ArrayList<Image>();
			
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() );		 
		}
		}
		workbook.close();
		
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


	
	
	
}
