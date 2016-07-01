package com.a4tech.sage.product.mapping;

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

import com.a4tech.product.model.Artwork;
import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintColorValue;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.Values;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.sage.product.parser.CatalogParser;
import com.a4tech.sage.product.parser.DimensionParser;
import com.a4tech.sage.product.parser.PriceGridParser;
import com.a4tech.sage.product.parser.ColorParser;
import com.a4tech.util.ApplicationConstants;


public class SageProductsExcelMapping {
	
	private static final Logger _LOGGER = Logger.getLogger(SageProductsExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private CatalogParser   catalogParser;
	private PriceGridParser priceGridParser;
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
		List<String>  productXids = new ArrayList<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  //ProductSkuParser skuparserobj=new ProductSkuParser();
		  String productId = null;
		  String currencyType = null;
		  String priceQurFlag = null;
		  String priceType    = null;
		  String basePriceName = null;
		  String priceIncludes = null;
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
		
		List<Color> color = new ArrayList<Color>();
		List<ImprintMethod> imprintMethods = new ArrayList<ImprintMethod>();
		List<Artwork> artworkList = new ArrayList<Artwork>();
		List<ImprintColorValue> imprintColorsValueList = new ArrayList<ImprintColorValue>();
		String productName = null;
		StringBuilder imprintMethodValues = new StringBuilder();
		StringBuilder imprintColorValues = new StringBuilder();
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum() < 7)
				continue;
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			
			
			List<Image> imgList = new ArrayList<Image>();
			
			 productXids.add(productId);
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
				   
				case 24: 
					String latePriceStartDate=cell.getStringCellValue();
					break;
					
				case 25: //Late Quantities
					
					String lateQuantities=cell.getStringCellValue();
					
					break;
					
				case 26:
					String lateNetPrices=cell.getStringCellValue();
					
					break;
					
				case 27: 
					String lateCodes=cell.getStringCellValue();
					
					break;
				case 28:
				case 29:
				case 30:
				case 31:
				case 32:
				case 33:
				case 34:
				case 35:
				case 36:
				case 37:       
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
					quantity = cell.getStringCellValue();
			         if(!StringUtils.isEmpty(quantity)){
			        	 listOfPrices.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			         }
				}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
					double quantity1 = (double)cell.getNumericCellValue();
			         if(!StringUtils.isEmpty(quantity1)){
			        	 listOfPrices.append(quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			         }
				}else{
				}
				  break;
				case 38:
				case 39:
				case 40:
				case 41:
				case 42:
				case 43:
				case 44:
				case 45:
				case 46:
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
				case 49:
				case 50:
				case 51:
				case 52:
				case 53:
				case 54:
				case 55:
				case 56:
				case 57:	
					quantity = cell.getStringCellValue();
			         if(!StringUtils.isEmpty(quantity)){
			        	 listOfDiscount.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			         }
				break; 
				case 58:
				case 59:
				case 60:
				case 61:
				case 62:
				case 63:
				case 64:
				case 65:
				case 66:
				case 67:
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						quantity = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(quantity)){
				        	 listOfQuantity.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						int quantity1 = (int)cell.getNumericCellValue();
				         if(!StringUtils.isEmpty(quantity1)){
				        	 listOfQuantity.append(quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else{
					}
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
				case 75:
				case 76:
				case 77:       
					/*if(cell.getCellType() == Cell.CELL_TYPE_STRING){
					quantity = cell.getStringCellValue();
			         if(!StringUtils.isEmpty(quantity)){
			        	 UpCharPrices.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			         }
				}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
					double quantity1 = (double)cell.getNumericCellValue();
			         if(!StringUtils.isEmpty(quantity1)){
			        	 UpCharPrices.append(quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			         }
				}else{
				}*/
				  break;
				  
				case 78:
				case 79:
				case 80:
				case 81:
				case 82:
				case 83:
				case 84:
				case 85:
				case 86:
				case 87:	
					/*if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						quantity = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(quantity)){
				        	 UpchargeNetPrice.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						double quantity1 = (double)cell.getNumericCellValue();
				         if(!StringUtils.isEmpty(quantity1)){
				        	 UpchargeNetPrice.append(quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else{
					}*/
					  break;
				case 88:
				case 89:
				case 90:
				case 91:
				case 92:
				case 93:
				case 94:
				case 95:
				case 96:
				case 97:
					/*if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						quantity = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(quantity)){
				        	 UpCharDiscount.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						int quantity1 = (int)cell.getNumericCellValue();
				         if(!StringUtils.isEmpty(quantity1)){
				        	 UpCharDiscount.append(quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else{
						
					}*/
					 break; // upcharge discount end
					 
				case 98:
				case 99:
				case 100:
				case 101:
				case 102:
				case 103:
				case 104:
				case 105:
				case 106:
				case 107:
					/*if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						quantity = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(quantity)){
				        	 UpCharQuantity.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						int quantity1 = (int)cell.getNumericCellValue();
				         if(!StringUtils.isEmpty(quantity1)){
				        	 UpCharQuantity.append(quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else{
						
					}*/
					 break; // upcharge quanytity
					 
					 
					 
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
					/*ShipingItem = shipinestmt.getShippingEstimates(shippingitemValue, shippingdimensionValue,shippingWeightValue);
					if(ShipingItem.getDimensions()!=null || ShipingItem.getNumberOfItems()!=null || ShipingItem.getWeight()!=null ){
					productConfigObj.setShippingEstimates(ShipingItem);
					}*/
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
			
					break;
					
				
				
			case 122:
				//Item Type2
				System.out.println(122);
				break;
				
			case 123:
				
				
			case 124:
				//Item Type3
				System.out.println(124);
				break;
				
			case 125:
				
				break;
				
			case 126:
				//Item Type4
				System.out.println(126);
				break;
				
			case 127:
				//Item Colors4
			
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
					/*imprintColorValues=imprintColorParser.getImprintColorValues(imprintColorValue,imprintColorValues);*/
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
				break;
			}  // end inner while loop
					 
					  
		        
				}
				
			//imprintMethods = imprintMethodParser.getImprintCriteria(imprintMethodValues.toString());
			size.setDimension(finalDimensionObj);
			productConfigObj.setImprintMethods(imprintMethods); 
			productConfigObj.setSizes(size);
		
			
			//imprintColorsValueList = imprintColorParser.getImprintColorCriteria(imprintColorValues.toString());
			//imprintColors.setType("COLR");
			//imprintColors.setValues(imprintColorsValueList);
			//productConfigObj.setImprintColors(imprintColors);
			
				//productExcelObj.setProductConfigurations(productConfigObj);l
			 // end inner while loop
			if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
				/*priceGrids = priceGridParser.getPriceGrids(listOfPrices.toString(),listOfNetPrice.toString(), 
						         listOfQuantity.toString(), listOfDiscount.toString(), "USD",
						         priceIncludes, true, "N", productName,"",priceGrids);	*/
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

}
