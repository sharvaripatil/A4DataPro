package com.a4tech.usbProducts.excelMapping;

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

import com.a4tech.product.criteria.parser.CatalogParser;
import com.a4tech.product.criteria.parser.PersonlizationParser;
import com.a4tech.product.criteria.parser.PriceGridParser;
import com.a4tech.product.criteria.parser.ProductArtworkProcessor;
import com.a4tech.product.criteria.parser.ProductColorParser;
import com.a4tech.product.criteria.parser.ProductImprintMethodParser;
import com.a4tech.product.criteria.parser.ProductMaterialParser;
import com.a4tech.product.criteria.parser.ProductNumberParser;
import com.a4tech.product.criteria.parser.ProductOptionParser;
import com.a4tech.product.criteria.parser.ProductOriginParser;
import com.a4tech.product.criteria.parser.ProductPackagingParser;
import com.a4tech.product.criteria.parser.ProductRushTimeParser;
import com.a4tech.product.criteria.parser.ProductSameDayParser;
import com.a4tech.product.criteria.parser.ProductSampleParser;
import com.a4tech.product.criteria.parser.ProductShapeParser;
import com.a4tech.product.criteria.parser.ProductSizeParser;
import com.a4tech.product.criteria.parser.ProductSkuParser;
import com.a4tech.product.criteria.parser.ProductThemeParser;
import com.a4tech.product.criteria.parser.ProductTradeNameParser;
import com.a4tech.product.criteria.parser.ProductionTimeParser;
import com.a4tech.product.criteria.parser.ShippingEstimationParser;
import com.a4tech.product.criteria.parser.UsbProductsPriceGridParser;
import com.a4tech.product.model.Artwork;
import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Image;
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
		  String productId = null;
		  String currencyType = null;
		  String priceQurFlag = null;
		  String priceType    = null;
		  String basePriceName = null;
		  String priceIncludes = null;
		  PriceGridParser priceGridParser = new PriceGridParser();
		  UsbProductsPriceGridParser usbPriceParser = new UsbProductsPriceGridParser();
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
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum() == 0)
				continue;
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			
			
			List<Image> imgList = new ArrayList<Image>();
			String productName = null;
			 productXids.add(productId);
			 boolean checkXid  = false;
			 
			
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
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						productId = String.valueOf((int)cell.getNumericCellValue());
					}else{
						
					}
					productExcelObj.setExternalProductId(productId);
					break;
					
				case 2:
					 productName = cell.getStringCellValue();
					productExcelObj.setName(productName);
					
					break;
		
				case 3:
     					break;
			
				case 4:
					String categoryName = cell.getStringCellValue();
					List<String> listOfCategories = new ArrayList<String>();
					//listOfCategories.add(categoryName);
					listOfCategories.add("USB/FLASH DRIVES");
					productExcelObj.setCategories(listOfCategories);
				    break;
					
				case 5:
					   // brand 
					break;
					
				case 6: // brand name
					
					String brandName = cell.getStringCellValue();
					List<TradeName> listOfBrands = new ArrayList<TradeName>();
					TradeName tradeName1 = new TradeName();
					tradeName1.setName(brandName);
					listOfBrands.add(tradeName1);
					productConfigObj.setTradeNames(listOfBrands);
					break;
					
				case 7:
					    // product description
					String productDescription = cell.getStringCellValue();
					productDescription = "Phone Holder USB 2.0 Flash Drive";
					productExcelObj.setDescription(productDescription);
					break;
					
				case 8: // pricegrid related
					 priceIncludes = cell.getStringCellValue();
					productExcelObj.setPriceType("L"); 
					productExcelObj.setDescription(productName);
					break;
					
				case 9:
							//Sub-Taxonomy
					break;
					
				case 10:  
						//Sub-Taxonomy Name
					break;
					
				case 11:  //Keywords
					String Keywords = cell.getStringCellValue();
					List<String> listOfKeywords = new ArrayList<String>();
					if(Keywords.contains(ApplicationConstants.CONST_DELIMITER_AMPERSAND)){
						listOfKeywords.addAll(Arrays.asList(Keywords.split(ApplicationConstants.CONST_DELIMITER_AMPERSAND)));
					}else if(Keywords.contains("USBs")){
						String[] keys = Keywords.split("USBs");
						for (String key : keys) {
							    if(key.contains("USB")){
							    	listOfKeywords.addAll(Arrays.asList(key.split("USB")));
							    }else{
							    	listOfKeywords.add(key);
							    }
						}
					}
					productExcelObj.setProductKeywords(listOfKeywords);
					break;
				
				case 12:  // origin
					
					String origin1 = cell.getStringCellValue();
					List<Origin> listOfOrigins = new ArrayList<Origin>();
					Origin origins = new Origin();
					origins.setName(origin1);
					listOfOrigins.add(origins);
					productConfigObj.setOrigins(listOfOrigins);
					
					break;

				case 13: //Features
					String productFeatures = cell.getStringCellValue();
					List<WarrantyInformation> listOfWarrantyInfo = new ArrayList<WarrantyInformation>();
					WarrantyInformation warranty = null;
					if(productFeatures.contains(ApplicationConstants.CONST_DELIMITER_PIPE)){
						 String[] features = productFeatures.split(ApplicationConstants.CONST_DELIMITER_SPLITTING_PIPE);
						 for (String feature : features) {
							  warranty = new WarrantyInformation();
							  if(feature.contains("*")){
								  feature = feature.replace("*", "");
							  }
							  warranty.setName(feature);
							 listOfWarrantyInfo.add(warranty);
						}
						
					}else{
						 warranty = new WarrantyInformation();
						 warranty.setName(productFeatures);
						listOfWarrantyInfo.add(warranty);
					}
					
					warranty = new WarrantyInformation();
					warranty.setName("WARRANTY AVAILABLE");
					listOfWarrantyInfo.add(warranty);
					productConfigObj.setWarranty(listOfWarrantyInfo);
					
					break;
					
				case 14: // small image url
					String smallImageUrl=cell.getStringCellValue();
					Image image = new Image();
					      image.setImageURL(smallImageUrl);
					      image.setIsPrimary(true);
					      image.setRank(1);
					     imgList.add(image);
					
					break;
					
				case 15:
					String mediumImageUrl=cell.getStringCellValue();
					 image = new Image();
				      image.setImageURL(mediumImageUrl);
				      image.setIsPrimary(false);
				      image.setRank(2);
				     imgList.add(image);
					
					break;
				case 16: 
					  String largeImageUrl = cell.getStringCellValue();
					  image = new Image();
				      image.setImageURL(largeImageUrl);
				      image.setIsPrimary(false);
				      image.setRank(3);
				     imgList.add(image);
					  break;
				
				case 17: 
					
					String zoomImageUrl = cell.getStringCellValue();
					 image = new Image();
				      image.setImageURL(zoomImageUrl);
				      image.setIsPrimary(false);
				      image.setRank(4);
				     imgList.add(image);
				    productExcelObj.setImages(imgList);
					break;
				
				 case 18: 
					String material=cell.getStringCellValue();
					List<Material> listOfMaterialList =  new ArrayList<Material>();
					String[] materials = material.split(ApplicationConstants.CONST_DELIMITER_COMMA);
					for (String matr : materials) {
						Material matl = new Material();
						matl.setName(matr);
						matl.setAlias(matr);
						listOfMaterialList.add(matl);
					}
					productConfigObj.setMaterials(listOfMaterialList);
					
					break;
					
				case 19:  
					String priceMsg=cell.getStringCellValue();
					
					break;
					
				case 20: 
					String priceStartDate=cell.getStringCellValue();
					
					
					break;
					
				case 21: 
					String quantit=cell.getStringCellValue();
					
					break;
					
				case 22: 
					String netPrice=cell.getStringCellValue();
				   break;
					
				case 23: 
					String code=cell.getStringCellValue();

					
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
					  
		        
				}
				
				//productExcelObj.setProductConfigurations(productConfigObj);l
			}  // end inner while loop
			if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
				priceGrids = usbPriceParser.getPriceGrids(listOfPrices.toString(),listOfNetPrice.toString(), 
						         listOfQuantity.toString(), listOfDiscount.toString(), "USD",
						         priceIncludes, true, "N", productName,"",priceGrids);	
			}
			
			 
				if(UpCharCriteria != null && !UpCharCriteria.toString().isEmpty()){
					priceGrids = usbPriceParser.getUpchargePriceGrid(UpCharQuantity.toString(), UpCharPrices.toString(), UpCharDiscount.toString(), UpCharCriteria.toString(), 
							 upChargeQur, currencyType, upChargeName, upchargeType, upChargeLevel, new Integer(1), priceGrids);
				}
				
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
