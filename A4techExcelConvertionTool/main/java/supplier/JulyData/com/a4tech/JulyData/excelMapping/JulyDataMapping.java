package com.a4tech.JulyData.excelMapping;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;

import com.a4tech.product.USBProducts.criteria.parser.CatalogParser;
import com.a4tech.product.USBProducts.criteria.parser.PersonlizationParser;
import com.a4tech.JulyData.excelMapping.PriceGridParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductArtworkProcessor;
import com.a4tech.product.USBProducts.criteria.parser.ProductColorParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductImprintColorParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductImprintMethodParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductMaterialParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductNumberParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductOptionParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductOriginParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductPackagingParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductRushTimeParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductSameDayParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductSampleParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductShapeParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductSizeParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductSkuParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductThemeParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductTradeNameParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductionTimeParser;
import com.a4tech.product.USBProducts.criteria.parser.ShippingEstimationParser;
import com.a4tech.product.model.Artwork;
import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintColor;
import com.a4tech.product.model.ImprintColorValue;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Inventory;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Personalization;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.SameDayRush;
import com.a4tech.product.model.Samples;
import com.a4tech.product.model.Shape;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.TradeName;
import com.a4tech.product.model.WarrantyInformation;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.LookupData;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JulyDataMapping {
	
	private static final Logger _LOGGER = Logger.getLogger(JulyDataMapping.class);
	PostServiceImpl postServiceImpl = new PostServiceImpl();
	@SuppressWarnings("finally")
	public int readExcel(String accessToken,Workbook workbook){
		
		List<String> numOfProducts = new ArrayList<String>();
		FileInputStream inputStream = null;
		//Workbook workbook = null;
		List<String>  productXids = new ArrayList<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  ProductSkuParser skuparserobj=new ProductSkuParser();
		  String productId = null;
		  String currencyType = null;
		  String priceQurFlag = null;
		  String priceType    = null;
		  String basePriceName = null;
		  String priceIncludes = null;
		  PriceGridParser priceGridParser = new PriceGridParser();
		  String upChargeName = null;
		  String upChargeQur = null;
		  String upchargeType = null;
		  String upChargeDetails = null;
		  String upChargeLevel = null;
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  String productDescription =null;
		  
		  ProductNumberParser pnumberParser=new ProductNumberParser();
		try{
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
		String quantity = null;
		String SKUCriteria1 =null;
		String SKUCriteria2 =null;
		String skuvalue  =null;
		String Inlink  =null;
		String Instatus  =null;
		String InQuantity=null;
		ProductSkus skuObj= new ProductSkus();
		List<ProductSkus> productsku=new ArrayList<ProductSkus>();
		
		String productNumberCriteria1=null;
		String productNumberCriteria2=null;
		String productNumber=null;
		ProductNumber		pnumObj=new ProductNumber();
		List<ProductNumber> pnumberList=new ArrayList<ProductNumber>();
		
		List<Option> option=new ArrayList<Option>();
		Option optionobj= new Option();
		ProductOptionParser optionparserobj=new ProductOptionParser();
		String optiontype =null;
		String optionname =null;
		String optionvalues =null;
		String optionadditionalinfo =null;
		String canorder =null;
		String reqfororder =null;
		String shippingWeightValue=null;
		String colorValue=null;
		String imprintValue=null;
		ImprintColor imprintColors = new ImprintColor();
		String imprintColorValue=null;
		Color colorObj=new Color();
		ShippingEstimationParser shipinestmt = new ShippingEstimationParser();
		RushTime rushTime =new RushTime();
		SameDayRush sameDayObj=new SameDayRush();
		Samples samples=new Samples();
		
		List<Color> color = new ArrayList<Color>();
		List<String> origin = new ArrayList<String>();
		List<String> lineNames = new ArrayList<String>();
		List<String> categories = new ArrayList<String>();
		List<String> productKeywords = new ArrayList<String>();
		List<String> complianceCerts = new ArrayList<String>();
		List<String> safetyWarnings = new ArrayList<String>();
		List<Personalization> personalizationlist = new ArrayList<Personalization>();
		List<String> packaging = new ArrayList<String>();
		List<String> themes = new ArrayList<String>();
		List<String> tradeName = new ArrayList<String>();
		List<ImprintMethod> imprintMethods = new ArrayList<ImprintMethod>();
		List<Artwork> artworkList = new ArrayList<Artwork>();
		List<Shape> shapeList=new ArrayList<Shape>();
		List<ProductionTime> productionTimeList = new ArrayList<ProductionTime>();
		List<Material> materialList=new ArrayList<Material>();
		List<ImprintColorValue> imprintColorsValueList = new ArrayList<ImprintColorValue>();
		
		
		ProductColorParser colorparser=new ProductColorParser();
		ProductOriginParser originParser=new ProductOriginParser();
		ProductRushTimeParser rushTimeParser=new ProductRushTimeParser();
		ProductSameDayParser sameDayParser=new ProductSameDayParser();
		ProductSampleParser sampleParser =new ProductSampleParser();
		PersonlizationParser personalizationParser=new PersonlizationParser();
		CatalogParser catlogparser=new CatalogParser();
		 
		ProductSizeParser sizeParser=new ProductSizeParser();
		ProductPackagingParser packagingParser=new ProductPackagingParser();
		ProductTradeNameParser tradeNameParser=new ProductTradeNameParser();
		ProductImprintMethodParser imprintMethodParser=new ProductImprintMethodParser();
		ProductArtworkProcessor artworkProcessor=new ProductArtworkProcessor();
		ProductShapeParser shapeParser=new ProductShapeParser();
		ProductionTimeParser productionTimeParser =new ProductionTimeParser();
		ProductThemeParser themeParser=new ProductThemeParser();
		ProductMaterialParser materialParser=new ProductMaterialParser();
		ProductImprintColorParser imprintColorParser =new ProductImprintColorParser();
		String productName = null;
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum() == 0)
				continue;
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			
			
			List<Image> imgList = new ArrayList<Image>();
			
			 productXids.add(productId);
			 //String productName = null;
			 boolean checkXid  = false;
			 ShippingEstimate ShipingItem = null;
				
				String shippingitemValue = null;
				String shippingdimensionValue = null;
				String sizeGroup=null;
				String rushService=null;
				String prodSample=null;
				
				
				 
				 
				 imprintColors.setType("COLR");
			
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				String xid = null;
				int columnIndex = cell.getColumnIndex();
				if(columnIndex + 1 == 1){
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						xid = String.valueOf((int)cell.getNumericCellValue());
					}else{
						
					}
					 //xid = cell.getStringCellValue();
					 if(productXids.contains(xid)){
						 productXids.add(xid);
					 }else{
						 productXids = new ArrayList<String>();
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
							 	productExcelObj.setProductRelationSkus(productsku);
							 	productExcelObj.setProductNumbers(pnumberList);
							 	//productList.add(productExcelObj);
							 	int num = postServiceImpl.postProduct(accessToken, productExcelObj);
							 	if(num ==1){
							 		numOfProducts.add("1");
							 	}
								//System.out.println(mapper.writeValueAsString(productExcelObj));
							 	_LOGGER.info("list size>>>>>>>"+numOfProducts.size());
								
								// reset for repeateable set 
								priceGrids = new ArrayList<PriceGrid>();
								productConfigObj = new ProductConfigurations();
								productsku = new ArrayList<ProductSkus>();
								pnumberList = new ArrayList<ProductNumber>();
								option=new ArrayList<Option>();
								
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    }
							productExcelObj = new Product();
					 }
				}
				if(productXids.size() >1  && !LookupData.isRepeateIndex(String.valueOf(columnIndex+1))){
					continue;
				}

				switch (columnIndex + 1) {
				case 1:
					//NOR SITE
					break;
					
				case 2:
					//EFF STATUS
					break;
		
				case 3:
					//NOR_INTRO_DT
     				break;
			
				case 4:
					//PRODUCT CATAGORY
					String categoryName = cell.getStringCellValue();
					List<String> listOfCategories = new ArrayList<String>();
					listOfCategories.add(categoryName);
					productExcelObj.setCategories(listOfCategories);
				    break;
					
				case 5:
					   // NOR_PROD_SUBCAT
					break;
					
				case 6: //PRODUCT ID
                    if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						productId = String.valueOf((int)cell.getNumericCellValue());
					}else{
						
					}
					productExcelObj.setExternalProductId(productId);
				case 7:
					    // product description
                    productDescription = cell.getStringCellValue();
					productExcelObj.setDescription(productDescription);
					break;
					
				case 8: 
					// ATTRIBUTE_ID
					break;
					
				case 9://product Name
					productName = cell.getStringCellValue();
					productExcelObj.setName(productName);
						
					break;
					
				
			}  // end inner while loop		  
		        
				}
				
				
				//productExcelObj.setProductConfigurations(productConfigObj);l
			 // end inner while loop
			//if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
				priceGrids = priceGridParser.getPriceGrids(listOfPrices.toString(),listOfNetPrice.toString(), 
						         listOfQuantity.toString(), listOfDiscount.toString(), "USD",
						         priceIncludes, true, "N", productName,"",priceGrids);	
			//}
			
			 
				/*if(UpCharCriteria != null && !UpCharCriteria.toString().isEmpty()){
					priceGrids = priceGridParser.getUpchargePriceGrid(UpCharQuantity.toString(), UpCharPrices.toString(), UpCharDiscount.toString(), UpCharCriteria.toString(), 
							 upChargeQur, currencyType, upChargeName, upchargeType, upChargeLevel, new Integer(1), priceGrids);
				}
				*/
				if(!StringUtils.isEmpty(skuvalue)){
					skuObj=skuparserobj.getProductRelationSkus(SKUCriteria1, SKUCriteria2, skuvalue, Inlink, Instatus,InQuantity);
					productsku.add(skuObj);
				}
				
				if(!StringUtils.isEmpty(productNumber)){
					pnumObj=pnumberParser.getProductNumer(productNumberCriteria1, productNumberCriteria2, productNumber);
					if(pnumObj!=null){
					pnumberList.add(pnumObj);
					}
				}
				
				if(!StringUtils.isEmpty(optionname) && !StringUtils.isEmpty(optiontype) && !StringUtils.isEmpty(optionvalues) ){
					optionobj=optionparserobj.getOptions(optiontype, optionname, optionvalues, canorder, reqfororder, optionadditionalinfo);
					option.add(optionobj);		
					productConfigObj.setOptions(option);	
				}
				
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
		ObjectMapper mapper = new ObjectMapper();
		//System.out.println("Final product JSON, written to file");
		 ObjectMapper mapper1 = new ObjectMapper();
		   // Add repeatable sets here
		 	productExcelObj.setPriceGrids(priceGrids);
		 	productExcelObj.setProductConfigurations(productConfigObj);
		 	/*productExcelObj.setProductRelationSkus(productsku);
		 	productExcelObj.setProductNumbers(pnumberList);*/
		 	//productList.add(productExcelObj);
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj);
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

}
