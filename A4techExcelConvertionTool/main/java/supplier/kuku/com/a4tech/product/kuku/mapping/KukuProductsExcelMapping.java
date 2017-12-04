package com.a4tech.product.kuku.mapping;
import java.io.IOException;
import java.math.BigDecimal;
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
import com.a4tech.product.kuku.criteria.parser.KukuPriceGridParser;
import com.a4tech.product.kuku.criteria.parser.PersonlizationParser;
import com.a4tech.product.kuku.criteria.parser.ProductColorParser;
import com.a4tech.product.kuku.criteria.parser.ProductImprintMethodParser;
import com.a4tech.product.kuku.criteria.parser.ProductMaterialParser;
import com.a4tech.product.kuku.criteria.parser.ProductPackagingParser;
import com.a4tech.product.kuku.criteria.parser.ProductShippingEstimationParser;
import com.a4tech.product.kuku.criteria.parser.ProductSizeParser;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.Personalization;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class KukuProductsExcelMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(KukuProductsExcelMapping.class);
	PostServiceImpl postServiceImpl;
	ProductDao productDaoObj;
	ProductColorParser colorparser;
	ProductSizeParser sizeParser;
	ProductMaterialParser materialParser;
	ProductImprintMethodParser imprintMethodParser;
	ProductShippingEstimationParser shipinestmt;
	ProductPackagingParser packagingParser; 
	PersonlizationParser personlizationParser; 
	KukuPriceGridParser kukuPriceGridParser;
	@SuppressWarnings("finally")
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber,int batchId, String environmentType){
		int columnIndex = 0;
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		
		  Set<String>  listOfProductXids = new HashSet<String>();
		  Product productExcelObj = new Product();  
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  String productId = null;
		  String currencyType = null;
		  String priceQurFlag = null;
		  String priceType    = null;
		  String basePriceName = null;
		  String priceIncludes = null;
		  String upChargeName = null;
		  String upChargeQur = null;
		  String upchargeType = null;
		  String upChargeDetails = null;
		  String upChargeLevel = null;
		  
		  String	imprintMethodValue=null;
		  String	imprintMethodValueUpchrg=null;
		  String listPrice = null;
		  String discCode=null;
		  String priceIncludeUp=null;
		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		StringBuilder listOfQuantity = new StringBuilder();
		StringBuilder listOfPrices = new StringBuilder();
		StringBuilder listOfDiscount = new StringBuilder();
		List<ImprintMethod> imprintMethods = new ArrayList<ImprintMethod>();
		List<ProductionTime> listOfProductionTime = new ArrayList<ProductionTime>();
		List<Personalization> listPers = new ArrayList<Personalization>();
		
		List<Packaging>          listPackaging               = new ArrayList<Packaging>();
		List<Color> colorList = new ArrayList<Color>();
		String productName = null;
		while (iterator.hasNext()) {
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum() == 0)
				continue;
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			 if(productId != null){
				 listOfProductXids.add(productId);
			 }
			 boolean checkXid  = false;
			
			 while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					String xid = null;
					  columnIndex = cell.getColumnIndex();
					if(columnIndex + 1 == 1){
						xid=CommonUtility.getCellValueStrinOrInt(cell);
						checkXid = true;
					}else{
						checkXid = false;
					}
					if(checkXid){
						 if(!listOfProductXids.contains(xid)){
							 if(nextRow.getRowNum() != 1){
								 System.out.println("Java object converted to JSON String, written to file");
								   // Add repeatable sets here
								 	productExcelObj.setPriceGrids(priceGrids);
								 	productExcelObj.setProductConfigurations(productConfigObj);
								 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId, environmentType);
								 	if(num ==1){
								 		numOfProductsSuccess.add("1");
								 	}else if(num == 0) {
								 		numOfProductsFailure.add("0");
								 	}else{
								 		
								 	}
								 	_LOGGER.info("list size of success>>>>>>>"+numOfProductsSuccess.size());
									
									priceGrids = new ArrayList<PriceGrid>();
									productConfigObj = new ProductConfigurations();
									
									
							 }
							    if(!listOfProductXids.contains(xid)){
							    	listOfProductXids.add(xid);
							    }
								productExcelObj = new Product();
						 }
					}
					
					
					
					switch (columnIndex + 1) {
					case 1:
						productId=CommonUtility.getCellValueStrinOrInt(cell);
						productExcelObj.setExternalProductId(productId);
						break;
						
					case 2:
						 productName = cell.getStringCellValue();
						 if(!StringUtils.isEmpty(productName)){
						productExcelObj.setName(productName);
						 }
						break;
			
					case 3:
						String summary = cell.getStringCellValue();
						 if(!StringUtils.isEmpty(summary)){
						productExcelObj.setSummary(summary);
						 }
						break;
				
					case 4:
						// product description
						String productDescription = cell.getStringCellValue();
						 if(!StringUtils.isEmpty(productDescription)){
						productExcelObj.setDescription(productDescription);
						 }
						break;
						
						
					case 5:
						//Keywords
						String keywords = cell.getStringCellValue();
						
						 if(!StringUtils.isEmpty(keywords)){
							 List<String> listOfKeywords = new ArrayList<String>();
						String tempKeyWrd[]=keywords.split(ApplicationConstants.CONST_DELIMITER_COMMA);
						for (String string : tempKeyWrd) {
							listOfKeywords.add(string);
						}
						productExcelObj.setProductKeywords(listOfKeywords);
						 }
						break;
						
					case 6: // ProductColor
					String	colorValue=cell.getStringCellValue();
						if(!StringUtils.isEmpty(colorValue)){
							colorList=colorparser.getColorCriteria(colorValue);
						if(colorList!=null){
						productConfigObj.setColors(colorList);
						}
						}
						break;
					
						
					case 7:
						//Currently shape vale is blank in excel file
					/*String	shapeValue=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(shapeValue)){
						 List<Shape> listOfShapes = new ArrayList<Shape>();
						 Shape shapeObj;
					String tempShape[]=shapeValue.split(ApplicationConstants.CONST_DELIMITER_COMMA);
					for (String string : tempShape) {
						shapeObj=new Shape();
						shapeObj.setName(string);
						listOfShapes.add(shapeObj);
					}
					productConfigObj.setShapes(listOfShapes);
					 }*/
					 break;
						
					case 8: //sizes
						
						String sizeValue = cell.getStringCellValue();
						 if(!StringUtils.isEmpty(sizeValue)){
							 Size sizeObj = new Size();
						sizeObj=sizeParser.getSizes(sizeValue);
						if(sizeObj.getApparel()!=null || sizeObj.getCapacity()!=null || sizeObj.getDimension()!=null||sizeObj.getOther()!=null||sizeObj.getVolume()!=null){
						productConfigObj.setSizes(sizeObj);
						}
						 }
						break;
					case 9:
						//Material
						 
						String materials=cell.getStringCellValue();
						if(!StringUtils.isEmpty(materials)){
						List<Material> listOfMaterialList = materialParser.getMaterialValues(materials);
						productConfigObj.setMaterials(listOfMaterialList);
						 }
						break;
						
					case 10:  //tradenames
						/*//Currently shape vale is blank in excel file
						String tradenames = cell.getStringCellValue();
						if(!StringUtils.isEmpty(tradenames)){
						List<TradeName> listOfTrade = new ArrayList<TradeName>();
						TradeName tradeName1 = new TradeName();
						tradeName1.setName(tradenames);
						listOfTrade.add(tradeName1);
						productConfigObj.setTradeNames(listOfTrade);
						}*/
						break;
						
					case 11:  
						// origin
						String origin = cell.getStringCellValue();
						if(!StringUtils.isEmpty(origin)){
						List<Origin> listOfOrigins = new ArrayList<Origin>();
						Origin origins = new Origin();
						origins.setName(origin);
						listOfOrigins.add(origins);
						productConfigObj.setOrigins(listOfOrigins);
						}
						break;
		
					
					case 12:
						//production Time
						String prodTimeLo = null;
						int tempVal;
						ProductionTime productionTime = new ProductionTime();
						prodTimeLo=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(prodTimeLo)){
					    prodTimeLo=prodTimeLo.replaceAll(ApplicationConstants.CONST_STRING_DAYS,ApplicationConstants.CONST_STRING_EMPTY);
						productionTime.setBusinessDays(prodTimeLo);
						productionTime.setDetails(ApplicationConstants.CONST_STRING_DAYS);
						listOfProductionTime.add(productionTime);
						productConfigObj.setProductionTime(listOfProductionTime);
						}
						break;
					case 13:// rush service
					// already processing rush service for case 24
						break;
						
					case 14:
					 	imprintMethodValue=cell.getStringCellValue();
						 if(!StringUtils.isEmpty(imprintMethodValue)){
							 imprintMethods = imprintMethodParser.getImprintCriteria(imprintMethodValue,imprintMethods);
								productConfigObj.setImprintMethods(imprintMethods); 
						  }
						break;
						
						
					case 15:
						imprintMethodValueUpchrg=CommonUtility.getCellValueStrinOrDecimal(cell);
						break;
						
					case 16:
					 	//safety warnings this is currently blank
						break;
					
					case 17:// production option = protduction time as per client feedback
						String prodTimeValue = null;
						prodTimeValue=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(prodTimeValue)){	
					    listOfProductionTime=	getProductionTime(listOfProductionTime, prodTimeValue);
						productConfigObj.setProductionTime(listOfProductionTime);	
						}
						break;
					case 18:
						String fobPoint=cell.getStringCellValue();
						if(!StringUtils.isEmpty(fobPoint)){
							FOBPoint fobPointObj=new FOBPoint();
							List<FOBPoint> listfobPoints = new ArrayList<FOBPoint>();
							fobPointObj.setName(fobPoint);
							listfobPoints.add(fobPointObj);
							productExcelObj.setFobPoints(listfobPoints);
						}
						break;
						
						
					case 19://liname is currently empty in this file
						break;
						
						
					case 20://shipping wt under shpping estimation
						String shippingWeightValue=cell.getStringCellValue();
						if(!StringUtils.isEmpty(shippingWeightValue)){
						ShippingEstimate shipObj =new ShippingEstimate(); 
						shipObj	 = shipinestmt.getShippingEstimates(shippingWeightValue);
						if( shipObj.getWeight()!=null ){
						productConfigObj.setShippingEstimates(shipObj);
						}
						}
						break;
						
					case 21://packaging
						String packaging=cell.getStringCellValue();
						if(!StringUtils.isEmpty(packaging)){
							listPackaging=packagingParser.getPackagingCriteria(packaging);
							productConfigObj.setPackaging(listPackaging);
							
						}
						break;
						
						
					case 22: //sold as blank=Unimprinted as per client feedback
						String imprinValue=cell.getStringCellValue();
						 if(!StringUtils.isEmpty(imprintMethodValue)&&imprinValue.equalsIgnoreCase(ApplicationConstants.CONST_STRING_YES)){
							 imprintMethods = imprintMethodParser.getImprintCriteria(ApplicationConstants.CONST_STRING_UNIMPRINTED,imprintMethods);
								productConfigObj.setImprintMethods(imprintMethods); 
						  }
						break;
						
					case 23://personalization
						String personalizationVaue=cell.getStringCellValue();
						 if(!StringUtils.isEmpty(personalizationVaue)&&personalizationVaue.equalsIgnoreCase(ApplicationConstants.CONST_STRING_YES)){
							 listPers = personlizationParser.getPersonalizationCriteria(ApplicationConstants.CONST_STRING_PERSONALIZATION);
								productConfigObj.setPersonalization(listPers);
						  }
						break;
					case 24:
						String rushTime = cell.getStringCellValue();
						if(!StringUtils.isEmpty(rushTime)&&rushTime.equalsIgnoreCase(ApplicationConstants.CONST_STRING_YES)){
						RushTime rushTimeObj=new RushTime();
						List<RushTimeValue> rushTimeValues=new ArrayList<RushTimeValue>();
						RushTimeValue rushTimeValue=new RushTimeValue();
						rushTimeValue.setBusinessDays(ApplicationConstants.CONST_STRING_EMPTY);
						rushTimeValue.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
						rushTimeValues.add(rushTimeValue);
						rushTimeObj.setAvailable(ApplicationConstants.CONST_BOOLEAN_TRUE);
						rushTimeObj.setRushTimeValues(rushTimeValues);
						productConfigObj.setRushTime(rushTimeObj);
						}
						break;
						
					case 25: //Four color process=Four color process under imprint method as per client feedback
						String imprinFullClValue=cell.getStringCellValue();
						 if(!StringUtils.isEmpty(imprintMethodValue)&&imprinFullClValue.equalsIgnoreCase(ApplicationConstants.CONST_STRING_YES)){
							 imprintMethods = imprintMethodParser.getImprintCriteria(ApplicationConstants.CONST_STRING_FULLCOLOR,imprintMethods);
								productConfigObj.setImprintMethods(imprintMethods); 
						  }
						break;
						
					case 26:
						//Imprint option is blank for client
						break;
						
					case 27://imprint size
						String imprintSize = cell.getStringCellValue();
						if(!StringUtils.isEmpty(imprintSize)){
							ImprintSize imprintSizeObj = new ImprintSize();
							List<ImprintSize> listImprintSize=new ArrayList<ImprintSize>();
							imprintSizeObj.setValue(imprintSize);
							listImprintSize.add(imprintSizeObj);
							productConfigObj.setImprintSize(listImprintSize);
						}
						break;
					case 28://AdditionalColor/Location
						//currently this field is blank
						break;
					case 29://price include
						//for pricing imprint upcharge
						priceIncludeUp=cell.getStringCellValue();
						
						break;
					
					case 30://themes
						String themeValue = cell.getStringCellValue();
						if(!StringUtils.isEmpty(themeValue)){
						List<Theme> themeList =new ArrayList<Theme>();
						Theme themeObj=null;
						String themeArr[] = themeValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
						for (String tempTheme : themeArr) {
							themeObj=new Theme();
							themeObj.setName(tempTheme);
				 			themeList.add(themeObj);
						}
						productConfigObj.setThemes(themeList);
					}
						break;
						
						
					case 31:// categories
						//As per client feedback Categories rarely match with asi category
						//but has asked to process for current time being
						String categoryName = cell.getStringCellValue();
						if(!StringUtils.isEmpty(categoryName)){
						List<String> listOfCategories = new ArrayList<String>();
						listOfCategories.add(categoryName);
						productExcelObj.setCategories(listOfCategories);
						}
						
					    break;
					    
					case 32:
						String	quantity1=null;
						quantity1=CommonUtility.getCellValueStrinOrInt(cell);
					         if(!StringUtils.isEmpty(quantity1)){
					        	 listOfQuantity.append(quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						break;
					case 33:
						String	listPrice1=null;
						listPrice1=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice1)){
				        	 listOfPrices.append(listPrice1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 34:
						   
						String discCode1 = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(discCode1)){
				        	 listOfDiscount.append(discCode1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
				          
						break;
					case 35:
						String	quantity2=null;
						quantity2=CommonUtility.getCellValueStrinOrInt(cell);
					         if(!StringUtils.isEmpty(quantity2)){
					        	 listOfQuantity.append(quantity2).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						break;
					case 36:
						String	listPrice2=null;
						listPrice2=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice2)){
				        	 listOfPrices.append(listPrice2).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 37:
						String discCode2 = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(discCode2)){
				        	 listOfDiscount.append(discCode2).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 38:
						String	quantity3=null;
						quantity3=CommonUtility.getCellValueStrinOrInt(cell);
					         if(!StringUtils.isEmpty(quantity3)){
					        	 listOfQuantity.append(quantity3).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						break;
					case 39:
						String	listPrice3=null;
						listPrice3=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice3)){
				        	 listOfPrices.append(listPrice3).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 40:
						String discCode3 = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(discCode3)){
				        	 listOfDiscount.append(discCode3).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 41:
						String	quantity4=null;
						quantity4=CommonUtility.getCellValueStrinOrInt(cell);
					         if(!StringUtils.isEmpty(quantity4)){
					        	 listOfQuantity.append(quantity4).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						break;
					case 42:
						String	listPrice4=null;
						listPrice4=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice4)){
				        	 listOfPrices.append(listPrice4).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 43:
						String discCode4  = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(discCode4)){
				        	 listOfDiscount.append(discCode4).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 44:
						String	quantity5=null;
						quantity5=CommonUtility.getCellValueStrinOrInt(cell);
					         if(!StringUtils.isEmpty(quantity5)){
					        	 listOfQuantity.append(quantity5).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						break;
					case 45:
						String	listPrice5=null;
						listPrice5=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice5)){
				        	 listOfPrices.append(listPrice5).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 46:
						String discCode5  = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(discCode5)){
				        	 listOfDiscount.append(discCode5).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 47:
						String	quantity6=null;
						quantity6=CommonUtility.getCellValueStrinOrInt(cell);
					         if(!StringUtils.isEmpty(quantity6)){
					        	 listOfQuantity.append(quantity6).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						break;
						
					case 48:
						String	listPrice6=null;
						listPrice6=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice6)){
				        	 listOfPrices.append(listPrice6).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 49:
						String discCode6  = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(discCode6)){
				        	 listOfDiscount.append(discCode6).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 50:
						String	quantity7=null;
						quantity7=CommonUtility.getCellValueStrinOrInt(cell);
					         if(!StringUtils.isEmpty(quantity7)){
					        	 listOfQuantity.append(quantity7).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						break;
					case 51:
						String	listPrice7=null;
						listPrice7=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice7)){
				        	 listOfPrices.append(listPrice7).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 52:
						String discCode7  = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(discCode7)){
				        	 listOfDiscount.append(discCode7).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 53:
						String	quantity8=null;
						quantity8=CommonUtility.getCellValueStrinOrInt(cell);
					         if(!StringUtils.isEmpty(quantity8)){
					        	 listOfQuantity.append(quantity8).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						break;
					case 54:
						String	listPrice8=null;
						listPrice8=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice8)){
				        	 listOfPrices.append(listPrice8).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 55:
						String discCode8  = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(discCode8)){
				        	 listOfDiscount.append(discCode8).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					}
					
				    
				} // end inner while loop
			
			productExcelObj.setPriceType(ApplicationConstants.CONST_PRICE_TYPE_CODE_LIST);
			
			if(!StringUtils.isEmpty(listOfPrices)){
					 
				priceGrids = kukuPriceGridParser.getPriceGrids(listOfPrices.toString(),listOfQuantity.toString(), listOfDiscount.toString(),ApplicationConstants.CONST_STRING_CURRENCY_USD,
						ApplicationConstants.CONST_STRING_EMPTY,ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, productName,ApplicationConstants.CONST_STRING_EMPTY,priceGrids);	
			}
			if(!StringUtils.isEmpty(imprintMethodValue)&& !StringUtils.isEmpty(imprintMethodValueUpchrg)){
					priceGrids = kukuPriceGridParser.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE,imprintMethodValueUpchrg, ApplicationConstants.CONST_STRING_DISCOUNT_CODE_Z,
							"IMMD:"+imprintMethodValue,ApplicationConstants.CONST_CHAR_N,  ApplicationConstants.CONST_STRING_CURRENCY_USD, priceIncludeUp,imprintMethodValue, 
							ApplicationConstants.CONST_STRING_IMMD_CHARGE, ApplicationConstants.CONST_VALUE_TYPE_OTHER, ApplicationConstants.CONST_INT_VALUE_ONE, priceGrids);
				}
		
			
			upChargeQur = null;
			priceQurFlag = null;
			listOfPrices = new StringBuilder();
			upChargeQur = null;
			priceQurFlag = null;
			listOfPrices = new StringBuilder();
			listOfProductionTime=new ArrayList<ProductionTime>();
			imprintMethods = new ArrayList<ImprintMethod>();
			priceIncludes=null;
			priceIncludeUp=null;
		}catch(Exception e){
		_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() +"for column"+columnIndex+1);		 
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
	 	}
	 	_LOGGER.info("list size>>>>>>"+numOfProductsSuccess.size());
	 	_LOGGER.info("Failure list size>>>>>>"+numOfProductsFailure.size());
       finalResult = numOfProductsSuccess.size() + "," + numOfProductsFailure.size();
       productDaoObj.saveErrorLog(asiNumber,batchId);
       return finalResult;
	}catch(Exception e){
		_LOGGER.error("Error while Processing excel sheet ,Error message: "+e.getMessage()+"for column"+columnIndex+1);
		return finalResult;
	}finally{
		try {
			workbook.close();
		} catch (IOException e) {
			_LOGGER.error("Error while Processing excel sheet, Error message: "+e.getMessage()+"for column" +columnIndex+1);

		}
			_LOGGER.info("Complted processing of excel sheet ");
			_LOGGER.info("Total no of product:"+numOfProductsSuccess.size() );
	}
	
}

	public static List<ProductionTime> getProductionTime(List<ProductionTime> listProductionTime,String prodTimeValue){
		prodTimeValue=prodTimeValue.replaceAll("days","days,");
		ProductionTime productionTimeObj=null;
		//System.out.println(str);
		String prodArr[]=prodTimeValue.split(",");
		for (String prodVal : prodArr) {
			if(prodVal.contains("express")){
				productionTimeObj= new ProductionTime();
				prodVal=prodVal.replaceAll("By express", "");
				prodVal=prodVal.replaceAll("working days", "");
				prodVal=prodVal.replaceAll(":","").trim();
				productionTimeObj.setBusinessDays(prodVal);
				productionTimeObj.setDetails("By express");
				listProductionTime.add(productionTimeObj);
			}else if(prodVal.contains("sea")){
				productionTimeObj= new ProductionTime();
				prodVal=prodVal.replaceAll("By sea", "");
				prodVal=prodVal.replaceAll("working days", "");
				prodVal=prodVal.replaceAll("days", "");
				prodVal=prodVal.replaceAll(":","").trim();
				productionTimeObj.setBusinessDays(prodVal);
				productionTimeObj.setDetails("By sea");
				listProductionTime.add(productionTimeObj);
			}else if(prodVal.contains("air")){
				productionTimeObj= new ProductionTime();
				prodVal=prodVal.replaceAll("By air", "");
				prodVal=prodVal.replaceAll("days", "");
				prodVal=prodVal.replaceAll(":","").trim();
				productionTimeObj.setBusinessDays(prodVal);
				productionTimeObj.setDetails("By air");
				listProductionTime.add(productionTimeObj);
			}
			
		}
		return listProductionTime;
	}
	public ProductDao getProductDaoObj() {
		return productDaoObj;
	}

	public void setProductDaoObj(ProductDao productDaoObj) {
		this.productDaoObj = productDaoObj;
	}

	public PostServiceImpl getPostServiceImpl() {
		return postServiceImpl;
	}

	public void setPostServiceImpl(PostServiceImpl postServiceImpl) {
		this.postServiceImpl = postServiceImpl;
	}
	
	public ProductColorParser getColorparser() {
		return colorparser;
	}

	public void setColorparser(ProductColorParser colorparser) {
		this.colorparser = colorparser;
	}

	public ProductSizeParser getSizeParser() {
		return sizeParser;
	}

	public void setSizeParser(ProductSizeParser sizeParser) {
		this.sizeParser = sizeParser;
	}

	public ProductMaterialParser getMaterialParser() {
		return materialParser;
	}

	public void setMaterialParser(ProductMaterialParser materialParser) {
		this.materialParser = materialParser;
	}

	public ProductImprintMethodParser getImprintMethodParser() {
		return imprintMethodParser;
	}

	public void setImprintMethodParser(
			ProductImprintMethodParser imprintMethodParser) {
		this.imprintMethodParser = imprintMethodParser;
	}

	public ProductShippingEstimationParser getShipinestmt() {
		return shipinestmt;
	}

	public void setShipinestmt(ProductShippingEstimationParser shipinestmt) {
		this.shipinestmt = shipinestmt;
	}

	public ProductPackagingParser getPackagingParser() {
		return packagingParser;
	}

	public void setPackagingParser(ProductPackagingParser packagingParser) {
		this.packagingParser = packagingParser;
	}

	public PersonlizationParser getPersonlizationParser() {
		return personlizationParser;
	}

	public void setPersonlizationParser(PersonlizationParser personlizationParser) {
		this.personlizationParser = personlizationParser;
	}

	public KukuPriceGridParser getKukuPriceGridParser() {
		return kukuPriceGridParser;
	}

	public void setKukuPriceGridParser(KukuPriceGridParser kukuPriceGridParser) {
		this.kukuPriceGridParser = kukuPriceGridParser;
	}
}
