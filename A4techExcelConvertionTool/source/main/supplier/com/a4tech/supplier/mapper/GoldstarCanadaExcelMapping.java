package com.a4tech.supplier.mapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.Values;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;


public class GoldstarCanadaExcelMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(GoldstarCanadaExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;
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
	
			List<Color> colorList = new ArrayList<Color>();
			List<ImprintLocation> listImprintLocation = new ArrayList<ImprintLocation>();
			List<ImprintMethod> listOfImprintMethods = new ArrayList<ImprintMethod>();
			List<ProductionTime> listOfProductionTime = new ArrayList<ProductionTime>();
			List<String> productKeywords = new ArrayList<String>();
			List<Theme> themeList = new ArrayList<Theme>();
			List<Catalog> catalogList = new ArrayList<Catalog>();
			List<String> complianceList = new ArrayList<String>();
			List<Image> listOfImages= new ArrayList<Image>();
		    List<ImprintSize> imprintSizeList =new ArrayList<ImprintSize>();
		    List<com.a4tech.lookup.model.Catalog> catalogsList=new ArrayList<>();
		    List<Values> valuesList =new ArrayList<Values>();
			RushTime rushTime  = new RushTime();
			Size size=new Size();
		    Catalog catlogObj=new Catalog();
			List<FOBPoint> FobPointsList = new ArrayList<FOBPoint>();
			FOBPoint fobPintObj=new FOBPoint();
			List<Option> ProdoptionList = new ArrayList<Option>();
			List<OptionValue> ProdvaluesList = new ArrayList<OptionValue>();
			OptionValue ProdoptionValueObj=new OptionValue();
			Option ProdoptionObj=new Option();
		  
		    
		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		
		
		
		
	
		String priceCode = null;
		String productName = null;
		String quoteUponRequest  = null;
		String quantity = null;
		String cartonL = null;
		String cartonW = null;
		String cartonH = null;
		String weightPerCarton = null;
		String unitsPerCarton = null;
		String decorationMethod =null;
		 Date  priceConfirmedThru =null;
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
		String CatYear=null;
		Cell cell2Data = null;
		String prodTimeLo = null;
		String FOBValue= null;
		String themeValue=null;
		String priceIncludesValue=null;
		String imprintLocation = null;
		String ProductStatus=null;
		boolean Prod_Status;
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
				  cell2Data =  nextRow.getCell(2);
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
								imprintSizeList=gcImprintSizeParser.getimprintsize(ImprintSizevalue);
								if(imprintSizeList!=null){
								productConfigObj.setImprintSize(imprintSizeList);}
							//	productExcelObj.setImages(listOfImages);
								productConfigObj.setColors(colorList);
								if(!StringUtils.isEmpty(FobPointsList)){
								productExcelObj.setFobPoints(FobPointsList);
								}

								
							 	productExcelObj.setPriceGrids(priceGrids);
							 	productExcelObj.setProductConfigurations(productConfigObj);
							 //	if(Prod_Status = false){

							 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId);
							 	if(num ==1){
							 		numOfProductsSuccess.add("1");
							 	}else if(num == 0){
							 		numOfProductsFailure.add("0");
							 	}else{
							 		
							 	}
							 //	}
							 	_LOGGER.info("list size>>>>>>>"+numOfProductsSuccess.size());
							 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
								priceGrids = new ArrayList<PriceGrid>();
								listOfPrices = new StringBuilder();
							    listOfQuantity = new StringBuilder();
								productConfigObj = new ProductConfigurations();
								themeList = new ArrayList<Theme>();
								finalDimensionObj = new Dimension();
								 valuesList = new ArrayList<>();
								catalogList = new ArrayList<Catalog>();
								productKeywords = new ArrayList<String>();
								listOfProductionTime = new ArrayList<ProductionTime>();
								rushTime = new RushTime();
								listImprintLocation = new ArrayList<ImprintLocation>();
								listOfImprintMethods = new ArrayList<ImprintMethod>();
								listOfImages= new ArrayList<Image>();
								imprintSizeList =new ArrayList<ImprintSize>();
								ImprintSizevalue = new StringBuilder();
								size=new Size();
								colorList = new ArrayList<Color>();
								FobPointsList = new ArrayList<FOBPoint>();
								ProdoptionList = new ArrayList<Option>();
								 dimensionValue = new StringBuilder();
								 dimensionUnits = new StringBuilder();
								 dimensionType = new StringBuilder();
								 priceIncludes = new StringBuilder();
								 priceIncludesValue=null;
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid.trim());
						    }
						    existingApiProduct = postServiceImpl.getProduct(accessToken, xid=xid.replace("\t",""));
						     if(existingApiProduct == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						  	//   productExcelObj=existingApiProduct;
							//	productConfigObj=existingApiProduct.getProductConfigurations();
						        String confthruDate=existingApiProduct.getPriceConfirmedThru();
						        productExcelObj.setPriceConfirmedThru(confthruDate);
						        
						    	 List<Image> Img=existingApiProduct.getImages();
						    	 productExcelObj.setImages(Img);
						    	 
						    	 themeList=productConfigObj.getThemes();
						    	 productConfigObj.setThemes(themeList);
						    	 
						    	 List<Availability> AvailibilityList=null;
						    	 productExcelObj.setAvailability(AvailibilityList);
						    	 
						    	 List<ProductNumber> ProductNumberList=null;
						    	 productExcelObj.setProductNumbers(ProductNumberList);
						    	 
						    	 List<String>categoriesList=existingApiProduct.getCategories();
						    	 productExcelObj.setCategories(categoriesList);
						     
						     }
							//productExcelObj = new Product();
					 }
				}
				

				switch (columnIndex + 1) {
			
				case 1://xid
					   productExcelObj.setExternalProductId(xid.trim());
					
					 break;
				
				case 2://ProductID
					
					 break;
				case 3://AsiProdNo
					 String asiProdNo=CommonUtility.getCellValueStrinOrInt(cell);
					     productExcelObj.setAsiProdNo(asiProdNo);		
					  break;
				case 4://Name
					 productName = cell.getStringCellValue();
						int len=productName.length();
						 if(len>60){
							String strTemp=productName.substring(0, 60);
							int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
							productName=(String) strTemp.subSequence(0, lenTemp);
						}
						productExcelObj.setName(productName);
						break;
				case 5://CatYear(Not used)
					 CatYear=CommonUtility.getCellValueStrinOrInt(cell);
					
					
				    break;
					
				case 6://PriceConfirmedThru
				//	 priceConfirmedThru = cell.getDateCellValue();
		
					break;
					
				case 7: //  product status ,discontinued
					
					// ProductStatus=cell.getStringCellValue();
					// Prod_Status=cell.getBooleanCellValue();
					 break;
					
				case 8://Category
					
					break;
					
				case 9: // Category

					break;
					
				case 10: //Catalogs page number, Page1
					String PageNO=CommonUtility.getCellValueStrinOrInt(cell);
					String value=null;
					catalogsList = lookupServiceDataObj.getCatalog(value);
					if(CatYear.contains("2017")){
						catlogObj.setCatalogName("2017 Goldstar Canada");
						catlogObj.setCatalogPage(PageNO);
						catalogList.add(catlogObj);
						productExcelObj.setCatalogs(catalogList);
					}
					

					break;
					
				case 11:  
						//Catalogs(Not used),Page2
					
					break;
					
				case 12:  //Description
					String description =CommonUtility.getCellValueStrinOrInt(cell);
					description=description.replaceAll("™", "");
					description=description.replaceAll("®", "");
					description=description.replaceAll("soft touch", "");
					
					int length=description.length();
					if(length>800){
						String strTemp=description.substring(0, 800);
						int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
						description=(String) strTemp.subSequence(0, lenTemp);
					}
					productExcelObj.setDescription(description);
									
					break;
				
				case 13:  // keywords
					String productKeyword = cell.getStringCellValue();
					if(!StringUtils.isEmpty(productKeyword)){
					String productKeywordArr[] = productKeyword.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					for (String string : productKeywordArr) {
						productKeywords.add(string);
					}
					productExcelObj.setProductKeywords(productKeywords);
					}
					break;

				case 14: //Colors
				String colorValue=cell.getStringCellValue();
					if(!StringUtils.isEmpty(colorValue)){
						colorList=gccolorparser.getColorCriteria(colorValue);
						productConfigObj.setColors(colorList);
					}	
					break;
					
				case 15: // Themes
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
					
				case 16://size --  value
						String dimensionValue1=CommonUtility.getCellValueStrinOrInt(cell);
					   if(dimensionValue1 != null && !dimensionValue1.isEmpty()){
						   dimensionValue.append(dimensionValue1).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
						 
					   }
					
					break;
				case 17: //size -- Unit
					  String dimensionUnits1 = CommonUtility.getCellValueStrinOrInt(cell);
					 if(!dimensionUnits1.contains("0")){
						 dimensionUnits.append(dimensionUnits1.trim()).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					 }
					  break;
				
				case 18: //size -- type
					String dimensionType1 =CommonUtility.getCellValueStrinOrInt(cell);
					if(!dimensionType1.contains("0")){
						dimensionType.append(dimensionType1).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					  
					 
					break;
				
				 case 19: //size
					 String dimensionValue2 =CommonUtility.getCellValueStrinOrInt(cell);
					 if(dimensionValue2 != null && !dimensionValue2.isEmpty()){
						 dimensionValue.append(dimensionValue2).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					 }
				
					break;
					
				case 20:  //size
					String dimensionUnits2 =CommonUtility.getCellValueStrinOrInt(cell);
					
					if(!dimensionUnits2.contains("0")){
						dimensionUnits.append(dimensionUnits2.trim()).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					break;
					
				case 21: //size
					String  dimensionType2 = CommonUtility.getCellValueStrinOrInt(cell);

					if(!dimensionType2.contains("0")){
						dimensionType.append(dimensionType2).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					
					break;
					
				case 22: //size
					String dimensionValue3  =CommonUtility.getCellValueStrinOrInt(cell);
					if(dimensionValue3 != null && !dimensionValue3.isEmpty()){
						dimensionValue.append(dimensionValue3).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}else{
						dimensionValue=dimensionValue.append("");
					}
					break;
					
					
				case 23: //size
					String dimensionUnits3 = CommonUtility.getCellValueStrinOrInt(cell);
					if(!dimensionUnits3.contains("0")){
						 dimensionUnits.append(dimensionUnits3.trim()).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}else
					{
						dimensionUnits=dimensionUnits.append("");
					}
				   break;
					
				case 24: //size
					String dimensionType3 = CommonUtility.getCellValueStrinOrInt(cell);
					if(!dimensionType3.contains("0")){
						dimensionType.append(dimensionType3).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}else
					{
						dimensionType=dimensionType.append("");
					}
					

					
				   break;
				   
				case 25:  // Quantities
				case 26: 
				case 27: 
				case 28: 
				case 29:
				case 30:
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
				case 31:  // prices --list price
				case 32:
				case 33:
				case 34:
				case 35:
				case 36:
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
				case 37: // price code -- discount
					    
				priceCode = cell.getStringCellValue();	
					     break;
				case 38:       // pricesPerUnit
				case 39:
				case 40:
				case 41:
				case 42:
				case 43:
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
				case 44:
					     quoteUponRequest = cell.getStringCellValue();
					      break;
				case 45:  // priceIncludeClr
					    
					      priceIncludes.append(cell.getStringCellValue()).append(" ");
					     break;
				case 46: // priceIncludeSide
						
						priceIncludes.append(cell.getStringCellValue()).append(" ");
						
						break;
				case 47: // priceIncludeLoc
						priceIncludes.append(cell.getStringCellValue());
						int PriceIncludeLength=priceIncludes.length();
						if(PriceIncludeLength>100){
							
						priceIncludesValue=	priceIncludes.toString().substring(0,100);
						}else
						{
							priceIncludesValue=priceIncludes.toString();
						}
						
						break;
						
				case 48:    //setup charge   
					/* try{
						  ListPrice = CommonUtility.getCellValueDouble(cell);
					     if(!StringUtils.isEmpty(ListPrice) && !ListPrice.equals("0")){
				        	 listOfPrices.append(ListPrice).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
					}catch (Exception e) {
						 _LOGGER.info("Error in base price prices field "+e.getMessage());							
					}*/
				  break;
				case 49://setup discount code
					
					/*try{
						  Discountcode = CommonUtility.getCellValueStrinOrInt(cell);
					     if(!StringUtils.isEmpty(Discountcode) && !Discountcode.equals("0")){
				        	 listOfDiscount.append(Discountcode).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
					}catch (Exception e) {
			        	 _LOGGER.info("Error in pricePerUnit field "+e.getMessage());
					}*/
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
					       break;
				case 70:
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
				case 71:
					break;
				case 72:
					break;
				case 73:
					break;
				case 74:
					break;
				case 75:
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
					 
					 
				case 83:  // Second Imprintsize1
					
					SecondImprintsize1=CommonUtility.getCellValueStrinOrInt(cell);
					
				    if(!StringUtils.isEmpty(SecondImprintsize1) || SecondImprintsize1 !=  null ){

					ImprintSizevalue=ImprintSizevalue.append(SecondImprintsize1).append(" ");
				    }
					   	break;
					   	
				case 84:  // Second Imprintsize1 unit
					SecondImprintunit1=CommonUtility.getCellValueStrinOrInt(cell);
					
				    if(!StringUtils.isEmpty(SecondImprintunit1) || SecondImprintunit1 != null ){
					SecondImprintunit1=GoldstarCanadaLookupData.Dimension1Units.get(SecondImprintunit1);
					ImprintSizevalue=ImprintSizevalue.append(SecondImprintunit1).append(" ");

					}
					
						break;
						
				case 85:  // Second Imprintsize1 type
					SecondImprinttype1=CommonUtility.getCellValueStrinOrInt(cell);
					
				    if(!StringUtils.isEmpty(SecondImprinttype1) || SecondImprinttype1 !=  null ){
					SecondImprinttype1=GoldstarCanadaLookupData.Dimension1Type.get(SecondImprinttype1);
					ImprintSizevalue=ImprintSizevalue.append(SecondImprinttype1).append(" ").append("x");

					}
				
					  break;
					  
				case 86: // Second Imprintsize2
					SecondImprintsize2=CommonUtility.getCellValueStrinOrInt(cell);
					
				    if(!StringUtils.isEmpty(SecondImprintsize2) || SecondImprintsize2 !=  null ){
				    ImprintSizevalue=ImprintSizevalue.append(SecondImprintsize2).append(" ");
				    
				    }

					
					break;
					
				case 87: //Second Imprintsize2 Unit
					SecondImprintunit2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprintunit2) || SecondImprintunit2 !=  null ){
					SecondImprintunit2=GoldstarCanadaLookupData.Dimension1Units.get(SecondImprintunit2);
					ImprintSizevalue=ImprintSizevalue.append(SecondImprintunit2).append(" ");

					}

					break;
					
				case 88: // Second Imprintsize2 type	
					SecondImprinttype2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprinttype2) || SecondImprinttype2 != null ){
					SecondImprinttype2=GoldstarCanadaLookupData.Dimension1Type.get(SecondImprinttype2);
					ImprintSizevalue=ImprintSizevalue.append(SecondImprinttype2).append(" ");

					}
					
				/*	  ImprintSizevalue=append(FirstImprintunit1).
					  append(" ").append(FirstImprinttype1).append("x").append(FirstImprintsize2).append(" ").
					  append(FirstImprintunit2).append(" ").append(FirstImprinttype2).append(",").
					  append(SecondImprintsize1).append(" ").append(SecondImprintunit1).
					  append(" ").append(SecondImprinttype1).append("x").append(SecondImprintsize2).append(" ").
					  append(SecondImprintunit2).append(" ").append(SecondImprinttype2);
					*/
					  break;
					  
				case 89: // Second Imprint location
					String imprintLocation2 = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintLocation2)){
						ImprintLocation locationObj2 = new ImprintLocation();
						locationObj2.setValue(imprintLocation2);
						listImprintLocation.add(locationObj2);
					}
					break;
				case 90: // DecorationMethod
					 decorationMethod = cell.getStringCellValue();
					listOfImprintMethods = gcimprintMethodParser.getImprintMethodValues(decorationMethod,listOfImprintMethods);
					 break; 
					 
				case 91: //NoDecoration
					String noDecoration = cell.getStringCellValue();
					if(noDecoration.equalsIgnoreCase(ApplicationConstants.CONST_STRING_TRUE)){
						listOfImprintMethods = gcimprintMethodParser.getImprintMethodValues(noDecoration,
                                listOfImprintMethods);
					}
					
					 break;
				case 92: //NoDecorationOffered
					String noDecorationOffered = cell.getStringCellValue();
					if(noDecorationOffered.equalsIgnoreCase(ApplicationConstants.CONST_STRING_TRUE)){
						listOfImprintMethods = gcimprintMethodParser.getImprintMethodValues(noDecorationOffered,
                                listOfImprintMethods);
					}
					 break;
				case 93: //NewPictureURL
					/*String ImageValue1=cell.getStringCellValue();
					 Image image = new Image();
					 if(!StringUtils.isEmpty(ImageValue1)){
					// Image image = new Image();
				      image.setImageURL(ImageValue1);
				      image.setIsPrimary(true);
				      image.setRank(1);
				      listOfImages.add(image);
					  }*/
					break;
				case 94:  //NewPictureFile  -- not used
					break;
				case 95: //ErasePicture -- not used
					break;
				case 96: //NewBlankPictureURL
					/*String ImageValue2=cell.getStringCellValue();
					if(!StringUtils.isEmpty(ImageValue2))
					{
						  image = new Image();
					      image.setImageURL(ImageValue2);
					      image.setIsPrimary(false);
					      image.setRank(2);
					      listOfImages.add(image);
					}*/
				
					break;
				case 97: //NewBlankPictureFile -- not used
					break;
				case 98://EraseBlankPicture  -- not used
					break;
					 
				case 99: //PicExists   -- not used
					break;
				case 100: //NotPictured  -- not used
					break;
				case 101: //MadeInCountry
					
					String madeInCountry = cell.getStringCellValue();
					if(!madeInCountry.isEmpty()){
						List<Origin> listOfOrigin = gcOriginParser.getOriginValues(madeInCountry);
						productConfigObj.setOrigins(listOfOrigin);
					}
					break;
					
				case 102:// AssembledInCountry
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
				case 104: //ComplianceList  -- No data
					String complnceValuet=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(complnceValuet))
					   {
				    	complianceList.add(complnceValuet);
				    	productExcelObj.setComplianceCerts(complianceList);
					   }
					break;
					
				case 105://ComplianceMemo  -- No data
					String productDataSheet=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(productDataSheet))
					   {
						 productExcelObj.setProductDataSheet(productDataSheet);
					   }
					break;
					
				case 106: //ProdTimeLo
				   prodTimeLo = CommonUtility.getCellValueStrinOrInt(cell);
					/*ProductionTime productionTime = new ProductionTime();
					
					productionTime.setBusinessDays(prodTimeLo);
					listOfProductionTime.add(productionTime);*/
					break;
				case 107: //ProdTimeHi
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
				case 108://RushProdTimeLo
					String rushProdTimeLo  = cell.getStringCellValue();
					if(!rushProdTimeLo.equals(ApplicationConstants.CONST_STRING_ZERO)){
						rushTime = gcRushTimeParser.getRushTimeValues(rushProdTimeLo, rushTime);
					}
					
					 break; 	 
				case 109://RushProdTimeH
					String rushProdTimeH  = cell.getStringCellValue();
					if(!rushProdTimeH.equals(ApplicationConstants.CONST_STRING_ZERO)){
						rushTime = gcRushTimeParser.getRushTimeValues(rushProdTimeH, rushTime);
					}
					break;
					
				case 110://Packaging
				
					String pack  = cell.getStringCellValue();
					List<Packaging> listOfPackaging = gcPackagingParser.getPackageValues(pack);
					productConfigObj.setPackaging(listOfPackaging);
					break;
					
				case 111: //CartonL
					 cartonL  = CommonUtility.getCellValueStrinOrInt(cell);
					
					break;
				case 112://CartonW
					cartonW  = CommonUtility.getCellValueStrinOrInt(cell);
					break;
	
				case 113://CartonH
					cartonH  = CommonUtility.getCellValueStrinOrInt(cell);
					break;
				case 114: //WeightPerCarton
					weightPerCarton  =CommonUtility.getCellValueStrinOrInt(cell);
					break;
				case 115: //UnitsPerCarton
					unitsPerCarton  = CommonUtility.getCellValueStrinOrInt(cell);
					break;
					
				case 116: //ShipPointCountry

					break;
					
				case 117: //ShipPointZip
					
					break;
					
				case 118: //Comment
					 FOBValue=CommonUtility.getCellValueStrinOrInt(cell);
					//String FOBLooup=null;
					//List<String>fobLookupList = lookupServiceDataObj.getFobPoints(FOBLooup);
				
					if(FOBValue.contains("CA"))
					{
						fobPintObj.setName("San Diego, CA 92131 USA");
						FobPointsList.add(fobPintObj);
					}
					else if(FOBValue.contains("TN"))
					{
						fobPintObj.setName("Shelbyville, TN 37162 USA");
						FobPointsList.add(fobPintObj);
					}
					if(FOBValue.contains("02"))
					{
						
						ProdoptionObj.setOptionType("Product");
						ProdoptionObj.setName("Pencil Sharpening");
						ProdoptionValueObj.setValue("Pencil Sharpening Available");
						ProdvaluesList.add(ProdoptionValueObj);
						ProdoptionObj.setValues(ProdvaluesList);
						ProdoptionList.add(ProdoptionObj);
						 productConfigObj.setOptions(ProdoptionList);
					}
						
						
						
						
					break;
					
				case 119: //Verified
					String verified=cell.getStringCellValue();
					if(verified.equalsIgnoreCase("True")){
					String priceConfimedThruString="2017-12-31T00:00:00";
					productExcelObj.setPriceConfirmedThru(priceConfimedThruString);
					}
					break;
			
				case 120: //UpdateInventory
					
					break;
				
				case 121: //InventoryOnHand
					
					break;
					
				case 122: //InventoryOnHandAdd
					break;
					
				case 123: //InventoryMemo
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
			
		
		  if(FOBValue.contains("02"))
			{
			priceGrids = gcPricegridParser.getUpchargePriceGrid("1", "2", "C", "Product Option", 
							"false", "USD", "Pencil Sharpening Available", "Product Option Charge", "Other", new Integer(1), priceGrids);
		 	productExcelObj.setPriceGrids(priceGrids);

			}
		  else{
			  
			 	productExcelObj.setPriceGrids(priceGrids);
		  }
				
				
			
			    
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() );		 
		}
		}
		workbook.close();
	
		ShippingEstimate shipping = gcShippingParser.getShippingEstimateValues(cartonL, cartonW,
				                               cartonH, weightPerCarton, unitsPerCarton);
		productConfigObj.setImprintLocation(listImprintLocation);
		productConfigObj.setImprintMethods(listOfImprintMethods);
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
		
		imprintSizeList=gcImprintSizeParser.getimprintsize(ImprintSizevalue);
		 imprintSizeList.removeAll(Collections.singleton(null));
		if(!StringUtils.isEmpty(imprintSizeList)){
		productConfigObj.setImprintSize(imprintSizeList);
		}
		//productExcelObj.setImages(listOfImages);
		productConfigObj.setColors(colorList);
		if(!StringUtils.isEmpty(FobPointsList)){
		productExcelObj.setFobPoints(FobPointsList);
		}
	   

		 	productExcelObj.setProductConfigurations(productConfigObj);
	
		 	
		 	//if(Prod_Status = false){
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId);
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
			catalogList = new ArrayList<Catalog>();
			productKeywords = new ArrayList<String>();
			listOfProductionTime = new ArrayList<ProductionTime>();
			rushTime = new RushTime();
			listImprintLocation = new ArrayList<ImprintLocation>();
			listOfImprintMethods = new ArrayList<ImprintMethod>();
			listOfImages= new ArrayList<Image>();
			imprintSizeList =new ArrayList<ImprintSize>();
			size=new Size();
			colorList = new ArrayList<Color>();
			FobPointsList = new ArrayList<FOBPoint>();
			ImprintSizevalue = new StringBuilder();
			ProdoptionList = new ArrayList<Option>();
			DimensionRef=null;
			 dimensionValue = new StringBuilder();
			 dimensionUnits = new StringBuilder();
			 dimensionType = new StringBuilder();
			 priceIncludesValue=null;
			 priceIncludes = new StringBuilder();
			 
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
