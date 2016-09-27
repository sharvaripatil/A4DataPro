package com.a4tech.kl.product.mapping;

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

import com.a4tech.kl.product.parser.KlColorAndMaterialParser;
import com.a4tech.kl.product.parser.KlPriceGridParser;
import com.a4tech.kl.product.parser.KlProductAttributeParser;
import com.a4tech.kl.product.parser.KlSizeParser;
import com.a4tech.product.criteria.parser.ProductImprintMethodParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Inventory;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.Personalization;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.Samples;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;


public class KlProductsExcelMapping {
	
	private static final Logger _LOGGER = Logger.getLogger(KlProductsExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private KlPriceGridParser klPriceGridPar ;
	private KlProductAttributeParser attributeparser;
	ProductDao productDaoObj;
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  String productId = null;
		 /* String currencyType = null;
		  String priceQurFlag = null;
		  String priceType    = null;
		  String basePriceName = null;
		
		  String upChargeName = null;
		  String upChargeQur = null;
		  String upchargeType = null;
		  String upChargeDetails = null;
		  String upChargeLevel = null;*/
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  List<ImprintMethod> productImprintMethods = null;

		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		
		
		StringBuilder listOfQuantity = new StringBuilder();
		StringBuilder listOfPrices = new StringBuilder();
		StringBuilder listOfDiscount = new StringBuilder();
		/*StringBuilder basePriceCriteria =  new StringBuilder();
		StringBuilder UpCharQuantity = new StringBuilder();
		StringBuilder UpCharPrices = new StringBuilder();
		StringBuilder UpchargeNetPrice = new StringBuilder();
		StringBuilder UpCharDiscount = new StringBuilder();
		StringBuilder UpCharCriteria = new StringBuilder();
		String optiontype =null;
		String optionname =null;
		String optionvalues =null;
		String optionadditionalinfo =null;
		String canorder =null;
		String reqfororder =null;*/
		//StringBuilder pricesPerUnit = new StringBuilder();
		//String quoteUponRequest  = null;
		String priceIncludes = null;
		String quantity = null;
		String productName = null;
		RushTime rushTime  = new RushTime();
		//List<String> productKeywords = new ArrayList<String>();
		List<Theme> themeList = new ArrayList<Theme>();
		String productSample = null;
		String rushServiceAvail = null;
		String imprintUpchargePrice = null;
		String imprintMethodName = null;
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum() == 0)
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
				if(columnIndex == 0){
					continue;
				}
				if(columnIndex  == 1){
					xid = CommonUtility.getCellValueStrinOrInt(cell);
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
								productImprintMethods = new ArrayList<ImprintMethod>();
								//productKeywords = new ArrayList<String>();
								rushTime = new RushTime();
								rushServiceAvail = null;
								imprintUpchargePrice = null;
								imprintMethodName = null;
								
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    }
							productExcelObj = new Product();
					 }
				}
				

				switch (columnIndex) {
				case 1://ExternalProductID
					productId = CommonUtility.getCellValueStrinOrInt(cell);
					productExcelObj.setExternalProductId(productId);
					
					 break;
				case 2://Product Name
					    productName = CommonUtility.getCellValueStrinOrInt(cell);
					     productExcelObj.setName(productName);	
					  break;
				case 3://Product Number
					String asiProdNo = CommonUtility.getCellValueStrinOrInt(cell);
				    productExcelObj.setAsiProdNo(asiProdNo);	
				    break;
				case 4://description
					String description = cell.getStringCellValue();
					productExcelObj.setDescription(description);
					
				    break;
					
				case 5://summery
					String Summary = cell.getStringCellValue();
					
					productExcelObj.setSummary(Summary);

					break;
					
				case 6: //  product Image 
					String image = cell.getStringCellValue();
					//List<Image> productImageList = attributeparser.getProductImages(image);
					//productExcelObj.setImages(productImageList);
					break;
					
				case 7://Category
					   String Category = cell.getStringCellValue();
					   List<String> categories = CommonUtility.getStringAsList(Category,
							   										ApplicationConstants.CONST_DELIMITER_COMMA);
					   productExcelObj.setCategories(categories);
					break;
					
				case 8: // Keywords
					String Keywords = cell.getStringCellValue();
					
					List<String> productKeywords = CommonUtility.getStringAsList(Keywords,
								                                ApplicationConstants.CONST_DELIMITER_COMMA);
					productExcelObj.setProductKeywords(productKeywords);
					break;
					
				case 9: //Inventory_Link
					String inventoryLink = cell.getStringCellValue();
					Inventory inventory = new Inventory();
					if(!StringUtils.isEmpty(inventoryLink)){
						inventory.setInventoryLink(inventoryLink);
						productExcelObj.setInventory(inventory);
					}
					break;
					
				case 10:  //Product_Color
					String ProductColor = cell.getStringCellValue();
					KlColorAndMaterialParser colorParser = new KlColorAndMaterialParser();
					List<Color> productColors = colorParser.getColors(ProductColor);
					productConfigObj.setColors(productColors);
					break;
					
				case 11:  //Material
					String material = cell.getStringCellValue();
					KlColorAndMaterialParser materialParser = new KlColorAndMaterialParser();
					List<Material> productMaterial = materialParser.getMaterials(material);
					productConfigObj.setMaterials(productMaterial);
					break;
				
				case 12:  // size group
					   // There is no data for this column
					break;

				case 13: //size
					String size=cell.getStringCellValue();
					KlSizeParser sizeParser = new KlSizeParser();
					Size productSizes = sizeParser.getSizes(size);
					productConfigObj.setSizes(productSizes);
					break;
					
				case 14: // Shape
					// There is no data for this column
					  
					break;
					
				case 15://Theme
					// There is no data for this column
					
					break;
				case 16: //Trade Name
					// There is no data for this column
					  break;
				
				case 17: //Origin
					String origin =cell.getStringCellValue();
					List<Origin> productOrigin = new ArrayList<Origin>();
					Origin originObj = new Origin();
					originObj.setName(origin);
					productOrigin.add(originObj);
					productConfigObj.setOrigins(productOrigin);
					 
					break;
				
				 case 18: //Option_Type
					// There is no data for this column
				
					break;
					
				case 19:  //Option_Name
					// There is no data for this column
					break;
					
				case 20: //Can_order_only_one
					// There is no data for this column
					
					break;
					
				case 21: //Req_for_orde
					// There is no data for this column
					break;
					
					
				case 22: //Imprint_Method
					imprintMethodName = cell.getStringCellValue();
					ProductImprintMethodParser imprintMethodParser = new ProductImprintMethodParser();
					 productImprintMethods = imprintMethodParser.
							                                     getImprintCriteria(imprintMethodName);
				   break;
					
				case 23: //Min_Qty
					// Q1 column No:57   cell = row.getCell(colIndex);
					Cell q1Index = nextRow.getCell(57);
					int minQty1 = Integer.parseInt(CommonUtility.getCellValueStrinOrInt(cell));
					int q1Value = Integer.parseInt(CommonUtility.getCellValueStrinOrInt(q1Index));
					/*if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						minQty = cell.getStringCellValue();
						minQty1 = Integer.parseInt(minQty);
						
						q1Data = q1Index.getStringCellValue();
						q1Value  = Integer.parseInt(q1Data); 
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						 minQty1 = (int)cell.getNumericCellValue();
						 q1Value = (int)q1Index.getNumericCellValue();
				         
					}else{
					}*/
					try{
						 if(minQty1 < q1Value){
							 productExcelObj.setCanOrderLessThanMinimum(ApplicationConstants.CONST_BOOLEAN_TRUE);
						 }
					}catch (Exception e) {
						productExcelObj.setCanOrderLessThanMinimum(ApplicationConstants.CONST_BOOLEAN_FALSE);
					}
				   break;
				   
				case 24:  //Artwork
					// There is no data for this column
					break;
				case 25: //Imprint_Color
					// There is no data for this column
					break;
				case 26: //Sold_Unimprinted
					String soldUnimprinted = cell.getStringCellValue();
					if(soldUnimprinted.equalsIgnoreCase(ApplicationConstants.CONST_CHAR_Y)){
						ImprintMethod imprMthod = new ImprintMethod();
						imprMthod.setAlias(ApplicationConstants.CONST_STRING_UNIMPRINTED);
						imprMthod.setType(ApplicationConstants.CONST_STRING_UNIMPRINTED);
						productImprintMethods.add(imprMthod);
					}
					break;
				case 27: //Personalization
					String personalization = cell.getStringCellValue();
					if(personalization.equalsIgnoreCase(ApplicationConstants.CONST_CHAR_Y)){
						List<Personalization> listOfPersonalization = new ArrayList<Personalization>();
						Personalization personaliation = new Personalization();
						personaliation.setAlias(ApplicationConstants.CONST_STRING_PERSONALIZATION);
						personaliation.setType(ApplicationConstants.CONST_STRING_PERSONALIZATION);
						listOfPersonalization.add(personaliation);
						productConfigObj.setPersonalization(listOfPersonalization);
					}
					break;
				case 28: //Imprint_Size
					String imprintSizeValue = cell.getStringCellValue();
					List<ImprintSize> listOfImprintSize = attributeparser.getImprintSize(imprintSizeValue);
					productConfigObj.setImprintSize(listOfImprintSize);
					break;
				case 29: //Imprint Location
					  // There is no data for this column
					
					   	break;
				case 30:  // Additional_Color
					// there is no data for this column
					break;
				case 31: //Additional_location
					// there is no data for this column
					break;
					
				case 32: //Product_Sample
					 productSample = cell.getStringCellValue();
					
					//productConfigObj.setSamples(samples);
					break;
				case 33: //Spec_Sample
					String specSample = cell.getStringCellValue();
					Samples productSamples = attributeparser.getSamples(productSample, specSample);
					productConfigObj.setSamples(productSamples);
					break;
				case 34: // production time
					String productionTime = cell.getStringCellValue();
					List<ProductionTime> listOfProTime = attributeparser.getProductionTime(productionTime);
					if(listOfProTime != null){
						productConfigObj.setProductionTime(listOfProTime);
					}
					break;
				case 35: // Rush Service available
					rushServiceAvail = cell.getStringCellValue();
					break;
				case 36: // Rush Service Time
					    
					String rushServiceTime= cell.getStringCellValue();
					   RushTime productRushTime = attributeparser.getRushTime(rushServiceAvail, rushServiceTime);
					   productConfigObj.setRushTime(productRushTime);
					break;
				case 37:       // sameDayService
					// there is no data for this column
					break;
				case 38: //Packaging
					String packaging = cell.getStringCellValue();
					List<Packaging> listOfPackaging = attributeparser.getPackaging(packaging);
					productConfigObj.setPackaging(listOfPackaging);
					break;
				case 39: // Shipping_Items
					// there is no data for this column
					break;
				case 40://Shipping_Dimensions
					// there is no data for this column
					break;
				case 41: //Shipping_Weight
					// there is no data for this column
					break;
				case 42: // Shipper_Bills_By
					// there is no data for this column
					break;
				case 43: // Shipping_Info
					// there is no data for this column
					break;
				case 44:  // Ship_Plain_Box
					    
					// there is no data for this column
					break;
				case 45: //  Comp_Cert
						
					// there is no data for this column
					break;
				case 46: // Product_Data_Sheet
					// there is no data for this column
					break;
						
				case 47:   //Safety_Warnings    
					// there is no data for this column
					break;
				case 48: //Additional_Info
					// there is no data for this column
					break;
				case 49: //Distibutor_Only
					// there is no data for this column
					break;
				case 50: //Disclaimer
					// there is no data for this column
					break;
				case 51: //Base_Price_Name
					// there is no data for this column
					break;
				case 52://Base_Price_Criteria_1
					// there is no data for this column
					break;
				case 53: // Base_Price_Criteria_2
					// there is no data for this column
							break;
				case 54: // price Includes
					priceIncludes = cell.getStringCellValue();
							break;
				case 55:  //Set up charge 
					imprintUpchargePrice = CommonUtility.getCellValueDouble(cell);
					     /*if(cell.getCellType() == Cell.CELL_TYPE_STRING){
					    	 imprintUpchargePrice  = cell.getStringCellValue();
							}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
								 double imprUpchargeValue = cell.getNumericCellValue();
								 imprintUpchargePrice = String.valueOf(imprUpchargeValue);
						         
							}else{
							}*/
							break;
				case 56: // Option_Values
					// waiting for client response
							break;
				case 57:
				case 58:
				case 59:
				case 60:
				case 61:
				case 62:
				case 63:
				case 64:
				case 65:
				case 66:
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
			          
				case 67:
				case 68:
				case 69:
				case 70:
				case 71:
				case 72:
				case 73:
				case 74:
				case 75:
				case 76:       
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
					   	
				case 77:
				case 78:
				case 79:
				case 80:
				case 81:
				case 82:
				case 83:
				case 84:
				case 85:
				case 86:	
					quantity = cell.getStringCellValue();
			         if(!StringUtils.isEmpty(quantity)){
			        	 listOfDiscount.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			         }
					  break;
					  
					  	
				case 87: // Product_Number_Price
					// wating for client response
					  break;
					  
				case 88: // There is no data for column
					
					break;
				case 89: 
					 break; 
					 
				case 90: 
					 break;
				case 91: 
					 break;
				case 92:
					break;
				case 93:  
					break;
				case 94: 
					break;
				case 95: 
					break;
				case 96: 
					break;
				case 97:
					break;
					 
				case 98: 
					break;
				case 99:
					break;
				case 100: 
					
					
					break;
					
				case 101:
					break;
				case 102: 
					break;
				case 103: 
					break;
				case 104:
					break;
				case 105: 
					break;
				case 106: 
					break;
				case 107:
					 break; 	 
				case 108:
					break;
					
				case 109:
					break;
					
				case 110: 
					
					break;
				case 111:
					break;
	
				case 112:
					break;
				case 113: 
					break;
				case 114: 
					break;
					
				case 115: 
					break;
					
				case 116:
					
					break;
					
				case 117: 
					

					break;
					
				case 118: 
					break;
			
				case 119: 
					
					break;
				
				case 120: 
					
					break;
					
				case 121:
					break;
					
				case 122: 
				break;
				case 123:
				case 124:
				case 125:
				case 126:
				case 127:
				case 128:
				case 129:
				case 130:
				case 131:
				case 132:	
				case 133:
				case 134:
				case 135:	
				case 136:
					// there is no data for this column
					break;
			
			}  // end inner while loop
					 
		}
			// set  product configuration objects
			//List<String> listOfCategories = new ArrayList<String>();
			//listOfCategories.add("USB/FLASH DRIVES");
			//productExcelObj.setCategories(listOfCategories);
			productConfigObj.setThemes(themeList);
			productConfigObj.setImprintMethods(productImprintMethods); 
			//productConfigObj.setSizes(size);
			 // end inner while loop
			productExcelObj.setPriceType("L");
			String qurFlag = "n"; // by default for testing purpose
			if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
				priceGrids = klPriceGridPar.getPriceGrids(listOfPrices.toString(), 
						         listOfQuantity.toString(), listOfDiscount.toString(), "USD",
						         priceIncludes.toString(), true, qurFlag, productName,"",priceGrids);	
			}
			
			   /*  This upcharge related to Imprint method only ,if we need use upcharge for other product attribute
			    *  then we need remove hard code values from calling function parameters   
			    * 
			    */
				if(imprintMethodName != null && !imprintMethodName.isEmpty()){
					priceGrids = klPriceGridPar.getUpchargePriceGrid(ApplicationConstants.CONST_STRING_VALUE_ONE , 
							imprintUpchargePrice,ApplicationConstants.CONST_STRING_DISCOUNT_CODE_Z,
							ApplicationConstants.CONST_STRING_IMPRINT_METHOD,ApplicationConstants.CONST_CHAR_N, 
							ApplicationConstants.CONST_STRING_CURRENCY_USD, imprintMethodName, ApplicationConstants.CONST_STRING_IMMD_CHARGE, 
						  ApplicationConstants.CONST_STRING_EMPTY, new Integer(1), priceGrids);
				}
				//upChargeQur = null;
				//UpCharCriteria = new StringBuilder();
				//priceQurFlag = null;
				listOfPrices = new StringBuilder();
			    listOfQuantity = new StringBuilder();
				/*UpCharPrices = new StringBuilder();
				UpCharDiscount = new StringBuilder();
				UpCharQuantity = new StringBuilder();
			    optiontype=null;
			    optionname=null;
			    optionvalues=null;
			    canorder=null;
			    reqfororder=null;
			    optionadditionalinfo=null;*/
			
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
	       productDaoObj.getErrorLog(asiNumber,batchId);
	       return finalResult;
		}catch(Exception e){
			_LOGGER.error("Error while Processing excel sheet ");
			return finalResult;
		}finally{
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet");
	
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
	public ProductDao getProductDaoObj() {
		return productDaoObj;
	}

	public void setProductDaoObj(ProductDao productDaoObj) {
		this.productDaoObj = productDaoObj;
	}
	public KlPriceGridParser getKlPriceGridPar() {
		return klPriceGridPar;
	}

	public void setKlPriceGridPar(KlPriceGridParser klPriceGridPar) {
		this.klPriceGridPar = klPriceGridPar;
	}
	public KlProductAttributeParser getAttributeparser() {
		return attributeparser;
	}

	public void setAttributeparser(KlProductAttributeParser attributeparser) {
		this.attributeparser = attributeparser;
	}

}
