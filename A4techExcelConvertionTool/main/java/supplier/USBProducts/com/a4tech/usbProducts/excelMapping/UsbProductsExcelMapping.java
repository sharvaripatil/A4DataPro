package com.a4tech.usbProducts.excelMapping;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import com.a4tech.product.USBProducts.criteria.parser.PriceGridParser;
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
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.LookupData;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UsbProductsExcelMapping {
	
	private static final Logger _LOGGER = Logger.getLogger(UsbProductsExcelMapping.class);
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
		  boolean isProduct = false;
		  String externalProductId = null;
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
		  
		  
		  ProductNumberParser pnumberParser=new ProductNumberParser();
		try{
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		StringBuilder listOfQuantity = new StringBuilder();
		StringBuilder listOfPrices = new StringBuilder();
		StringBuilder listOfDiscount = new StringBuilder();
		StringBuilder basePriceCriteria =  new StringBuilder();
		StringBuilder UpCharQuantity = new StringBuilder();
		StringBuilder UpCharPrices = new StringBuilder();
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
		
		
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum() == 0)
				continue;
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			
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
			ShippingEstimationParser shipinestmt = new ShippingEstimationParser();
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
			 List<Image> imgList = new ArrayList<Image>();
			 List<Catalog> catalogList = new ArrayList<Catalog>();
		        	
		    Image imgObj =new Image(); 
			
			Inventory inventoryObj = new Inventory();
	        Size sizeObj = null;
			ShippingEstimate ShipingItem = null;
			
			String shippingitemValue = null;
			String shippingdimensionValue = null;
			String sizeGroup=null;
			String rushService=null;
			String prodSample=null;
			
			Color colorObj=new Color();
			 productXids.add(externalProductId);
			 boolean checkXid  = false;
			 
			 imprintColors.setType("COLR");
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				String xid = null;
				int columnIndex = cell.getColumnIndex();
				if(columnIndex + 1 == 1){
					int a =(int) cell.getNumericCellValue();
					 xid = Integer.toString(a);
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
							 ObjectMapper mapper = new ObjectMapper();
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
				
				case 108:
					 int shipval= (int) cell.getNumericCellValue();
					 shippingitemValue=Integer.toString(shipval);
					shippingitemValue=shippingitemValue+ApplicationConstants.CONST_DELIMITER_COLON+"per Case";
					break;
					
				case 109:
					int shipwtval= (int) cell.getNumericCellValue();
					shippingWeightValue=Integer.toString(shipwtval);
					  
					break;
					
				case 110:
					String weightinLBS=cell.getStringCellValue();
					shippingWeightValue=weightinLBS+ApplicationConstants.CONST_DELIMITER_COLON+shippingWeightValue;
					ShipingItem = shipinestmt.getShippingEstimates(shippingitemValue, shippingdimensionValue,shippingWeightValue);
					if(ShipingItem.getDimensions()!=null || ShipingItem.getNumberOfItems()!=null || ShipingItem.getWeight()!=null ){
					productConfigObj.setShippingEstimates(ShipingItem);
					}
					break;
				case 111:
					//Weight per Item
					System.out.println(111);
					break;
	
				case 112:
					//Unit of Measure
					break;
				case 113:
					//Sizes

					break;
				case 114:
					//Size Name

					break;
					
				case 115:
					//Size Width

					break;
					
				case 116:
					//Size Length

					break;
					
				case 117:
					//Size Height

					break;
					
				case 118:
					//Lead Time relates to Production Time

					break;
			
				case 119:
					//Rush Lead Time relates to Rush Time
					break;
				
				case 120:
					//Item Type1
					break;
					
				case 121:
					//Item Colors1
					colorValue=cell.getStringCellValue();
					if(!StringUtils.isEmpty(colorValue)){
						color=colorparser.getColorCriteria(colorValue);
				    
					if(color!=null){
					productConfigObj.setColors(color);
					}
					}
					break;
					
				
				
			case 122:
				//Item Type2
				System.out.println(122);
				break;
				
			case 123:
				//Item Colors2
				colorValue=cell.getStringCellValue();
				if(!StringUtils.isEmpty(colorValue)){
					color=colorparser.getColorCriteria(colorValue);
				    
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
					color=colorparser.getColorCriteria(colorValue);
				    
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
					color=colorparser.getColorCriteria(colorValue);
				    
				if(color!=null){
				productConfigObj.setColors(color);
				}
				}
				break;
				
			case 128:
				//imprint Method1
				imprintValue=cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintValue)){
				imprintMethods=imprintMethodParser.getImprintCriteria(imprintValue);
				if(imprintMethods!=null){
				productConfigObj.setImprintMethods(imprintMethods);
				}
				}
				break;

			case 129:
				//Imprint Location1
				break;
				
				
				
			case 130:
				//Imprint Colors1
				imprintColorValue = cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintColorValue)){
					imprintColorsValueList=imprintColorParser.getImprintColorCriteria(imprintColorValue);
				if(imprintColorsValueList!=null){
			    imprintColors.setValues(imprintColorsValueList);
				productConfigObj.setImprintColors(imprintColors);
				
				}
				}
				break;
				
			case 131:
				//imprint method2
				imprintValue=cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintValue)){
				imprintMethods=imprintMethodParser.getImprintCriteria(imprintValue);
				if(imprintMethods!=null){
				productConfigObj.setImprintMethods(imprintMethods);
				}
				}
				 break;
				
			case 132:
				//Imprint Location2
				break;
				
			case 133:
				//Imprint colors2
				imprintColorValue = cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintColorValue)){
					imprintColorsValueList=imprintColorParser.getImprintColorCriteria(imprintColorValue);
				if(imprintColorsValueList!=null){
			    imprintColors.setValues(imprintColorsValueList);
				productConfigObj.setImprintColors(imprintColors);
				
				}
				}
				break;
				
				
			case 134:
				//Imprint method3
				imprintValue=cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintValue)){
				imprintMethods=imprintMethodParser.getImprintCriteria(imprintValue);
				if(imprintMethods!=null){
				productConfigObj.setImprintMethods(imprintMethods);
				}
				}
				break;
				
				
				
			case 135:
				//Imprint location3
				break;
				
				
				
			case 136:
				//Imprint Colors3
				imprintColorValue = cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintColorValue)){
					imprintColorsValueList=imprintColorParser.getImprintColorCriteria(imprintColorValue);
				if(imprintColorsValueList!=null){
			    imprintColors.setValues(imprintColorsValueList);
				productConfigObj.setImprintColors(imprintColors);
				
				}
				}
				break;
				
			case 137:
				//Imprint method 4
				imprintValue=cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintValue)){
				imprintMethods=imprintMethodParser.getImprintCriteria(imprintValue);
				if(imprintMethods!=null){
				productConfigObj.setImprintMethods(imprintMethods);
				}
				}
				break;
				
			case 138:
				//Imprint location4
				break;
				
				
				
			case 139:
				//Imprint Colors4
				imprintColorValue = cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintColorValue)){
					imprintColorsValueList=imprintColorParser.getImprintColorCriteria(imprintColorValue);
				if(imprintColorsValueList!=null){
			    imprintColors.setValues(imprintColorsValueList);
				productConfigObj.setImprintColors(imprintColors);
				
				}
				}
				break;
				
				
			case 140:
				//Imprint method 5
				imprintValue=cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintValue)){
				imprintMethods=imprintMethodParser.getImprintCriteria(imprintValue);
				if(imprintMethods!=null){
				productConfigObj.setImprintMethods(imprintMethods);
				}
				}
				break;
				
			case 141:
				//Imprint location5
				break;
				
				
				
			case 142:
				//Imprint Colors5
				imprintColorValue = cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintColorValue)){
					imprintColorsValueList=imprintColorParser.getImprintColorCriteria(imprintColorValue);
				if(imprintColorsValueList!=null){
			    imprintColors.setValues(imprintColorsValueList);
				productConfigObj.setImprintColors(imprintColors);
				
				}
				}
				break;
				
				
				
			case 143:
				//Imprint method 6
				imprintValue=cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintValue)){
				imprintMethods=imprintMethodParser.getImprintCriteria(imprintValue);
				if(imprintMethods!=null){
				productConfigObj.setImprintMethods(imprintMethods);
				}
				}
				break;
				
			case 144:
				//Imprint location6
				break;
				
				
				
			case 145:
				//Imprint Colors6
				imprintColorValue = cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintColorValue)){
					imprintColorsValueList=imprintColorParser.getImprintColorCriteria(imprintColorValue);
				if(imprintColorsValueList!=null){
			    imprintColors.setValues(imprintColorsValueList);
				productConfigObj.setImprintColors(imprintColors);
				
				}
				}
				break;
				
				
			case 146:
				//Imprint method 7
				imprintValue=cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintValue)){
				imprintMethods=imprintMethodParser.getImprintCriteria(imprintValue);
				if(imprintMethods!=null){
				productConfigObj.setImprintMethods(imprintMethods);
				}
				}
				break;
				
			case 147:
				//Imprint location7
				break;
				
				
				
			case 148:
				//Imprint Colors7
				imprintColorValue = cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintColorValue)){
					imprintColorsValueList=imprintColorParser.getImprintColorCriteria(imprintColorValue);
				if(imprintColorsValueList!=null){
			    imprintColors.setValues(imprintColorsValueList);
				productConfigObj.setImprintColors(imprintColors);
				
				}
				}
				break;
				
			case 149:
				//Selections

				break;
				
			case 150:
				String artwork = cell.getStringCellValue();
				if(!StringUtils.isEmpty(artwork)){
				artworkList=artworkProcessor.getArtworkCriteria(artwork);
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
				artworkList=artworkProcessor.getArtworkCriteria(artworK);
				if(artworkList!=null){
				productConfigObj.setArtwork(artworkList);
				}}
				break;
			
			case 154:
				//FOB Ship From Zip
				break;
				
			case 155:
				//FOB Bill From Zip
				break;
			}  // end inner while loop
			
				
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
			}
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
		 	productExcelObj.setProductRelationSkus(productsku);
		 	productExcelObj.setProductNumbers(pnumberList);
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
