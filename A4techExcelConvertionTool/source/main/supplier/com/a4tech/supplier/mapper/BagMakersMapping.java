package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import parser.BagMakers.BagMakerAttributeParser;
import parser.BagMakers.BagMakersPriceGridParser;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.AdditionalLocation;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BagMakersMapping implements IExcelParser{

	
	private static final Logger _LOGGER = Logger.getLogger(BagMakersMapping.class);
	PostServiceImpl postServiceImpl;
	ProductDao productDaoObj;
	BagMakerAttributeParser bagMakerAttributeParser;
	BagMakersPriceGridParser bagMakersPriceGridParser;
	@Autowired
	ObjectMapper mapperObj;
	
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber,int batchId){
		int columnIndex = 0;
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		
		  Set<String>  listOfProductXids = new HashSet<String>();
		  Product productExcelObj = new Product();  
		  Product existingApiProduct = null;
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  
		  String shippinglen="";
		  String shippingWid="";
		  String shippingH="";
		   String shippingWeightValue="";
		  String noOfitem="";
		  boolean existingFlag=false;
		  String plateScreenCharge="";
		  String plateScreenChargeCode="";
		  String plateReOrderCharge="";
		  String plateReOrderChargeCode="";
		  String extraColorRucnChrg="";
		  String extraLocRunChrg="";
		  String extraLocColorScreenChrg="";
		  StringBuilder listOfQuantity = new StringBuilder();
		  StringBuilder listOfPrices = new StringBuilder();
		  StringBuilder listOfDiscount = new StringBuilder();
		  String basePricePriceInlcude="";
		  String tempQuant1="";
		  try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
	
		Set<String>  productXids = new HashSet<String>();
		 List<String> repeatRows = new ArrayList<>();
		 ShippingEstimate ShipingObj=new ShippingEstimate();
		 Dimensions dimensionObj=new Dimensions();
		 String setUpchrgesVal="";
		 String repeatUpchrgesVal="";
		 String priceInlcudeFinal="";
		 String xid = null;
		while (iterator.hasNext()) {
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum() == 0)
				continue;
			
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if(xid != null){
				productXids.add(xid);
				repeatRows.add(xid);
			}
			
			 boolean checkXid  = false;
			
			 while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					  columnIndex = cell.getColumnIndex();
					if(columnIndex + 1 == 1){
						xid = getProductXid(nextRow);//CommonUtility.getCellValueStrinOrInt(cell);//
						checkXid = true;
					}else{
						checkXid = false;
					}
					if(checkXid){
						 if(!productXids.contains(xid)){
							 if(nextRow.getRowNum() != 1){
								 
								 // Ineed to set pricegrid over here
								   // Add repeatable sets here
								 productExcelObj=bagMakersPriceGridParser.getPricingData(listOfPrices.toString(), listOfQuantity.toString(), listOfDiscount.toString(), basePricePriceInlcude, 
											plateScreenCharge, plateScreenChargeCode,
										    plateReOrderCharge, plateReOrderChargeCode, priceGrids, 
										    productExcelObj, productConfigObj);
								 	productExcelObj.setPriceType("L");
								 	//productExcelObj.setPriceGrids(priceGrids);
								 	productExcelObj.setProductConfigurations(productConfigObj);
								 	/* _LOGGER.info("Product Data : "
												+ mapperObj.writeValueAsString(productExcelObj));
								 	*/
								 	/*if(xidList.contains(productExcelObj.getExternalProductId().trim())){
								 		productExcelObj.setAvailability(new ArrayList<Availability>());
								 	}*/
								 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId);
								 	if(num ==1){
								 		numOfProductsSuccess.add("1");
								 	}else if(num == 0) {
								 		numOfProductsFailure.add("0");
								 	}else{
								 		
								 	}
								 	_LOGGER.info("list size of success>>>>>>>"+numOfProductsSuccess.size());
								 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
									//reset all list and objects over here
									priceGrids = new ArrayList<PriceGrid>();
									productConfigObj = new ProductConfigurations();
									 ShipingObj=new ShippingEstimate();
									 dimensionObj=new Dimensions();
									 setUpchrgesVal="";
									 plateScreenCharge="";
							  		 plateScreenChargeCode="";
							  		 plateReOrderCharge="";
							  		 plateReOrderChargeCode="";
							  		 extraColorRucnChrg="";
							  	     extraLocRunChrg="";
							  	     extraLocColorScreenChrg="";
							  	    listOfQuantity = new StringBuilder();
							  	    listOfPrices = new StringBuilder();
							  	    listOfDiscount = new StringBuilder();
							  	    basePricePriceInlcude="";
							  	    tempQuant1="";

							 }
							    if(!listOfProductXids.contains(xid)){
							    	listOfProductXids.add(xid);
							    }
								 productExcelObj = new Product();
								 existingApiProduct = postServiceImpl.getProduct(accessToken, xid); 
								     if(existingApiProduct == null){
								    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
								    	 productExcelObj = new Product();
								    	 existingFlag=false;
								     }else{//need to confirm what existing data client wnts
								    	    productExcelObj=bagMakerAttributeParser.getExistingProductData(existingApiProduct, existingApiProduct.getProductConfigurations());
											productConfigObj=productExcelObj.getProductConfigurations();
											existingFlag=true;
										   // priceGrids = productExcelObj.getPriceGrids();
								     }
						 }
					}
					
					
					
					switch (columnIndex + 1) {
					case 1://XID
						productExcelObj.setExternalProductId(xid);
						break;
					
					case  2: //Catalogpage
						  String PageNO=CommonUtility.getCellValueStrinOrInt(cell);
						    
						  List<com.a4tech.product.model.Catalog>  newlistCatalog=new ArrayList<com.a4tech.product.model.Catalog>(); 
						     List<com.a4tech.product.model.Catalog>  listCatalog = productExcelObj.getCatalogs();
						     if(!CollectionUtils.isEmpty(listCatalog)){
						     for (com.a4tech.product.model.Catalog catalog : listCatalog) {
								if(catalog.getCatalogName().contains("2017")){
									catalog.setCatalogPage(PageNO);
								}
								newlistCatalog.add(catalog);
						     }
						     productExcelObj.setCatalogs(newlistCatalog);
						     }
						break;
					case  3://Item #
						String asiProdNo=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(asiProdNo)){
							productExcelObj.setAsiProdNo(asiProdNo);
						}
						break;
					case  4://Name (Items in Red are new for 2017)
						String productName = CommonUtility.getCellValueStrinOrInt(cell);
						//productName = CommonUtility.removeSpecialSymbols(productName,specialCharacters);
						if(!StringUtils.isEmpty(productName)){
						int len=productName.length();
						 if(len>60){
							String strTemp=productName.substring(0, 60);
							int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
							productName=(String) strTemp.subSequence(0, lenTemp);
						}
						productExcelObj.setName(productName);
						}
						break;
					case  5://Collection Name ignote
						//String 	
						
						break;
					case  6://Bag Category //ignore

						break;
					case  7://Imprint Method
						String	imprintMethodVal=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(imprintMethodVal)){
						imprintMethodVal=imprintMethodVal.toUpperCase();
						String tempImpArr[]=imprintMethodVal.split(",");
						List<ImprintMethod> listOfImprintMethod= bagMakerAttributeParser.getImprintMethods(Arrays.asList(tempImpArr));
						 productConfigObj.setImprintMethods(listOfImprintMethod);
						
						}
						break;
					case  8://Description
						String description =CommonUtility.getCellValueStrinOrInt(cell);
						//description = CommonUtility.removeSpecialSymbols(description,specialCharacters);
						int length=description.length();
						 if(length>800){
							String strTemp=description.substring(0, 800);
							int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
							description=(String) strTemp.subSequence(0, lenTemp);
						}
						productExcelObj.setDescription(description);
						break;
					case  9://Dimensions
						String dimension =CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(dimension) && !dimension.toUpperCase().contains("TW")
								&& !dimension.toUpperCase().contains("BW") && !dimension.toUpperCase().contains("BG") && !dimension.toUpperCase().contains("MW")
								){
							Size sizeObj=new Size();
							sizeObj=bagMakerAttributeParser.getSizes(dimension);
							productConfigObj.setSizes(sizeObj);
						}
						
						break;
					case  10://Handle Length (Inches)
						String additinalInfo =CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(additinalInfo)){
							productExcelObj.setAdditionalProductInfo(additinalInfo.toString());
						}
						
						break;
					case  11://Imprint Area
						String imprintSize =CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(imprintSize)){
							List<ImprintSize> listOfImprintSize=new ArrayList<ImprintSize>();
							 listOfImprintSize = bagMakerAttributeParser.getImprintSize(imprintSize,listOfImprintSize);
							productConfigObj.setImprintSize(listOfImprintSize);
						
						}
						break;
					case  12://Colors (Items in Red are new for 2017)
						String colorValue=CommonUtility.getCellValueStrinOrInt(cell);
						 if(!StringUtils.isEmpty(colorValue)){
							 List<Color> colors =bagMakerAttributeParser.getProductColors(colorValue);
							 productConfigObj.setColors(colors);
						 }
						break;
					case  13://Product Type
						String summary = cell.getStringCellValue();
						 if(!StringUtils.isEmpty(summary)){
						productExcelObj.setSummary(summary);
						 }
						break;
					case  14://Box Pack
						 //noOfitem=noOfitem.toUpperCase();
						 noOfitem=CommonUtility.getCellValueStrinOrInt(cell);
						 if(!StringUtils.isEmpty(noOfitem.trim())){
							 noOfitem=noOfitem.toLowerCase();
							 if(noOfitem.contains("pk")){
								 noOfitem=noOfitem.substring(0,noOfitem.indexOf("pk"));
							 }
						 ShipingObj =bagMakerAttributeParser.getShippingEstimates("", "", "", "", noOfitem.trim(),dimensionObj,ShipingObj);
						 productConfigObj.setShippingEstimates(ShipingObj);
						}
						break;
					case  15://Weight (lbs)
						 shippingWeightValue=CommonUtility.getCellValueStrinOrInt(cell);
						 if(!StringUtils.isEmpty(shippingWeightValue.trim())){
						 shippingWeightValue=shippingWeightValue.toUpperCase();
						 
						 if(shippingWeightValue.contains("-")){
							 shippingWeightValue=shippingWeightValue.substring(shippingWeightValue.indexOf("-"));
							 shippingWeightValue=shippingWeightValue.replace("SHEETS","");
						 }
						 ShipingObj =bagMakerAttributeParser.getShippingEstimates("", "", "", shippingWeightValue, "",dimensionObj,ShipingObj);
						 productConfigObj.setShippingEstimates(ShipingObj);
						 }
						break;
					case  16://Box Length
						shippinglen=CommonUtility.getCellValueStrinOrInt(cell);
 						if(!StringUtils.isEmpty(shippinglen.trim())){
 						shippinglen=shippinglen.toUpperCase();
 						ShipingObj =bagMakerAttributeParser.getShippingEstimates(shippinglen.trim(), "", "", "", "",dimensionObj,ShipingObj);
 						productConfigObj.setShippingEstimates(ShipingObj);
 						}
						break;
					case  17://Box Width
						shippingWid=CommonUtility.getCellValueStrinOrInt(cell);
 						if(!StringUtils.isEmpty(shippingWid.trim())){
 						
 						ShipingObj =bagMakerAttributeParser.getShippingEstimates("", shippingWid.trim(), "", "", "",dimensionObj,ShipingObj);
 						productConfigObj.setShippingEstimates(ShipingObj);
 						}
						break;
					case  18://Box Depth
						shippingH=CommonUtility.getCellValueStrinOrInt(cell);
 						if(!StringUtils.isEmpty(shippingH.trim())){
 						ShipingObj =bagMakerAttributeParser.getShippingEstimates("", "", shippingH.trim(), "", "",dimensionObj,ShipingObj);
 						productConfigObj.setShippingEstimates(ShipingObj);
 						}
						break;
					case  19://Origin
						String origin = CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(origin)){
							origin=origin.toUpperCase();
							origin=origin.trim();
							String tempDesc=productExcelObj.getDescription();
							if(origin.equalsIgnoreCase("IMPORTED") || origin.contains("MEXICO")){
								tempDesc=tempDesc+origin;
								productExcelObj.setDescription(tempDesc);
							}else{
						List<Origin> listOfOrigins = new ArrayList<Origin>();
						Origin origins = new Origin();
						origins.setName("U.S.A.");
						listOfOrigins.add(origins);
						productConfigObj.setOrigins(listOfOrigins);
							}
							}
						break;
					case  20://Eco Characteristics
						String keywords = CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(keywords)){
						List<String> productKeywords = CommonUtility.getStringAsList(keywords,
                                ApplicationConstants.CONST_DELIMITER_COMMA);
						productExcelObj.setProductKeywords(productKeywords);
						}
						break;
					case  21://Keywords (for Search online)
						String keywords2 = CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(keywords2)){
						List<String> productKeywords2 = CommonUtility.getStringAsList(keywords2,
                                ApplicationConstants.CONST_DELIMITER_COMMA);
						if(!CollectionUtils.isEmpty(productExcelObj.getProductKeywords())){
							productKeywords2.addAll(productExcelObj.getProductKeywords());
						}
						productExcelObj.setProductKeywords(productKeywords2);
						}
						break;
					case  22://Special Notes
						String propOption = CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(propOption)){
							if(propOption.contains("thousand") || propOption.contains("not available") || propOption.contains("2nd")){
								String tempAddInfo=productExcelObj.getAdditionalProductInfo();
								if(!StringUtils.isEmpty(tempAddInfo)){
									propOption=tempAddInfo+" "+propOption;
								}
								productExcelObj.setAdditionalProductInfo(propOption);
							}else if(propOption.contains("bottom")){//bottom
								//create option over here
								List<Option> optionList=bagMakerAttributeParser.getOptions("Optional Bottom Board","Add bottom board");
								productConfigObj.setOptions(optionList);
								//create pricegrid over here
								if(propOption.contains("$0.30(C)")){
									priceGrids = bagMakersPriceGridParser.getPriceGrids(
											"0.30","1","C",
											ApplicationConstants.CONST_STRING_CURRENCY_USD,"",false,
											"false","Add bottom board","Product Option",new Integer(1),"Product Option Charge", "Per Quantity",
											new ArrayList<PriceGrid>());
									/*String listOfPrices, String listOfQuan, String discountCodes,
									String currency, String priceInclude, boolean isBasePrice,
									String qurFlag, String priceName, String criterias,Integer sequence,String upChargeType,String upchargeUsageType,
									List<PriceGrid> existingPriceGrid)*/					
									
								}
							}
						}
						break;
					case  23://Plate/Screen Charge
						plateScreenCharge=CommonUtility.getCellValueDouble(cell);
						if(StringUtils.isEmpty(plateScreenCharge)|| plateScreenCharge.trim().contains("N/A") || plateScreenChargeCode.trim().contains("Free")){
							plateScreenCharge="";
						}
						
						break;
					case  24://Plate/Screen Charge Code
						 plateScreenChargeCode=CommonUtility.getCellValueStrinOrInt(cell);
						 if(StringUtils.isEmpty(plateScreenChargeCode)|| plateScreenChargeCode.trim().contains("N/A") || plateScreenChargeCode.trim().contains("Free")){
							 plateScreenChargeCode="Z";
							}
						break;
					case  25://REORDER Plate/Screen Charge
						 plateReOrderCharge=CommonUtility.getCellValueDouble(cell);
						 if(StringUtils.isEmpty(plateReOrderCharge)|| plateReOrderCharge.trim().contains("N/A") || plateScreenChargeCode.trim().contains("Free")){
							 plateReOrderCharge="";
							}
						break;
					case  26://REORDER Plate/Screen Charge Code
						 plateReOrderChargeCode=CommonUtility.getCellValueStrinOrInt(cell);
						 if(StringUtils.isEmpty(plateReOrderChargeCode)|| plateReOrderChargeCode.trim().contains("N/A") || plateReOrderChargeCode.trim().contains("Free")){
							 plateReOrderChargeCode="Z";
							}
						break;
					case 27:
						String	quantity1=null;
						quantity1=CommonUtility.getCellValueStrinOrInt(cell);
						tempQuant1=quantity1;
					         if(!StringUtils.isEmpty(quantity1) && !quantity1.contains("N/A")){
					        	 if(quantity1.contains("(") && quantity1.contains(")")){
				        			 basePricePriceInlcude=CommonUtility.extractValueSpecialCharacter("(", ")", quantity1);
				        		 }else{
				        			 basePricePriceInlcude="";
				        		 }
					        	 
					        	 quantity1= removeSpecialChar(quantity1);
					        		 if(quantity1.contains("-")){
					        			 quantity1=getQuantValue(quantity1);
					        		 
					        	 }
					        		 
					        		 
					        	 listOfQuantity.append(quantity1.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						break;
					case 28:
						String	listPrice1=null;
						listPrice1=CommonUtility.getCellValueDouble(cell);
						if(!StringUtils.isEmpty(listPrice1) && !listPrice1.contains("N/A")){
				        	 listOfPrices.append(listPrice1.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 29:
						   
						String discCode1 = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(discCode1) && !discCode1.contains("N/A")){
				        	 listOfDiscount.append(discCode1.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
				          
						break;
					case 30:
						String	quantity2=null;
						quantity2=CommonUtility.getCellValueStrinOrInt(cell);
						
						if(tempQuant1.contains("4000")){
							listOfQuantity.append("4001").append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							tempQuant1="";
						}else if(!StringUtils.isEmpty(quantity2) && !quantity2.contains("N/A")){
					        	 quantity2= removeSpecialChar(quantity2);
				        		 if(quantity2.contains("-")){
				        			 quantity2=getQuantValue(quantity2);
				        		 
				        	 }
					        	 listOfQuantity.append(quantity2.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						break;
					case 31:
						String	listPrice2=null;
						listPrice2=CommonUtility.getCellValueDouble(cell);
						if(!StringUtils.isEmpty(listPrice2) && !listPrice2.contains("N/A")){
				        	 listOfPrices.append(listPrice2.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 32:
						String discCode2 = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(discCode2) && !discCode2.contains("N/A")){
				        	 listOfDiscount.append(discCode2.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 33:
						String	quantity3=null;
						quantity3=CommonUtility.getCellValueStrinOrInt(cell);
					         if(!StringUtils.isEmpty(quantity3) && !quantity3.contains("N/A")){
					        	 quantity3= removeSpecialChar(quantity3);
				        		 if(quantity3.contains("-")){
				        			 quantity3=getQuantValue(quantity3);
				        		 
				        	 }
					        	 listOfQuantity.append(quantity3.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						break;
					case 34:
						String	listPrice3=null;
						listPrice3=CommonUtility.getCellValueDouble(cell);
						if(!StringUtils.isEmpty(listPrice3) && !listPrice3.contains("N/A")){
				        	 listOfPrices.append(listPrice3.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 35:
						String discCode3 = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(discCode3) && !discCode3.contains("N/A")){
				        	 listOfDiscount.append(discCode3.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 36:
						String	quantity4=null;
						quantity4=CommonUtility.getCellValueStrinOrInt(cell);
					         if(!StringUtils.isEmpty(quantity4) && !quantity4.contains("N/A")){
					        	 quantity4= removeSpecialChar(quantity4);
				        		 if(quantity4.contains("-")){
				        			 quantity4=getQuantValue(quantity4);
				        		 
				        	 }
					        	 listOfQuantity.append(quantity4.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						break;
					case 37:
						String	listPrice4=null;
						listPrice4=CommonUtility.getCellValueDouble(cell);
						if(!StringUtils.isEmpty(listPrice4) && !listPrice4.contains("N/A")){
				        	 listOfPrices.append(listPrice4.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 38:
						String discCode4  = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(discCode4) && !discCode4.contains("N/A")){
				        	 listOfDiscount.append(discCode4.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 39:
						String	quantity5=null;
						quantity5=CommonUtility.getCellValueStrinOrInt(cell);
					         if(!StringUtils.isEmpty(quantity5) && !quantity5.contains("N/A")){
					        	 quantity5= removeSpecialChar(quantity5);
				        		 if(quantity5.contains("-")){
				        			 quantity5=getQuantValue(quantity5);
				        		 
				        	 }
					        	 listOfQuantity.append(quantity5.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						break;
					case 40:
						String	listPrice5=null;
						listPrice5=CommonUtility.getCellValueDouble(cell);
						if(!StringUtils.isEmpty(listPrice5) && !listPrice5.contains("N/A")){
				        	 listOfPrices.append(listPrice5.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 41:
						String discCode5  = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(discCode5) && !discCode5.contains("N/A")){
				        	 listOfDiscount.append(discCode5.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
				
					
					case  42://Extra Color Run Charge
						 extraColorRucnChrg = CommonUtility.getCellValueDouble(cell);
						 if(extraColorRucnChrg.trim().contains("N/A")){
							 extraColorRucnChrg="";
						 }
						
						break;
					case  43://Extra Color Run Code
						String extraColorRucnChrgCode=CommonUtility.getCellValueStrinOrInt(cell);
						if(extraColorRucnChrgCode.trim().contains("N/A")){
							extraColorRucnChrgCode="Z";
						 }
						if(!StringUtils.isEmpty(extraColorRucnChrg)){
							List<AdditionalColor> listOfAdditioColor = bagMakerAttributeParser.getAdditionalColor("Additional Color");
							productConfigObj.setAdditionalColors(listOfAdditioColor);
							extraColorRucnChrg = extraColorRucnChrg.replace("$", "").trim();
							
							priceGrids=bagMakersPriceGridParser.getPriceGrids(
									extraColorRucnChrg, "1", extraColorRucnChrgCode,
										ApplicationConstants.CONST_STRING_CURRENCY_USD,"",false,
										"false","Additional Color","Additional Colors",new Integer(1),"Run Charge", "Per Quantity",
										priceGrids);	
							
						}
						break;
					case  44://Extra Location Run Charge
						extraLocRunChrg = CommonUtility.getCellValueDouble(cell);
						 if(extraLocRunChrg.trim().contains("N/A")){
							 extraLocRunChrg="";
						 }
						break;
					case  45://Extra Location Run Code
						String extraLocRucnChrgCode=CommonUtility.getCellValueStrinOrInt(cell);
						if(extraLocRucnChrgCode.trim().contains("N/A")){
							extraLocRucnChrgCode="Z";
						 }
						if(!StringUtils.isEmpty(extraLocRunChrg)){
							List<AdditionalLocation> listOfAdditioLoc = bagMakerAttributeParser.getAdditionalLocation("Additional Location");
							productConfigObj.setAdditionalLocations(listOfAdditioLoc);
							extraLocRunChrg = extraLocRunChrg.replace("$", "").trim();
							
							priceGrids=bagMakersPriceGridParser.getPriceGrids(
									extraLocRunChrg, "1", extraLocRucnChrgCode,
										ApplicationConstants.CONST_STRING_CURRENCY_USD,"",false,
										"false","Additional Location","Additional Location",new Integer(1),"Run Charge", "Per Quantity",
										priceGrids);	
							
						}
						break;
					case  46://Extra Color/Location Screen/Plate Charge
						extraLocColorScreenChrg = CommonUtility.getCellValueDouble(cell);
						 if(extraLocColorScreenChrg.trim().contains("N/A")){
							 extraLocColorScreenChrg="";
						 }
						break;
					case  47://Extra Color/Location Screen/Plate Code
						
						String extraLocColorScreenChrgCode=CommonUtility.getCellValueStrinOrInt(cell);
						if(extraLocColorScreenChrgCode.trim().contains("N/A")){
							extraLocColorScreenChrgCode="Z";
						 }
						if(!StringUtils.isEmpty(extraLocColorScreenChrg)){
							if(CollectionUtils.isEmpty(productConfigObj.getAdditionalLocations())){
							List<AdditionalLocation> listOfAdditioLoc = bagMakerAttributeParser.getAdditionalLocation("Additional Location");
							productConfigObj.setAdditionalLocations(listOfAdditioLoc);
							}
							if(CollectionUtils.isEmpty(productConfigObj.getAdditionalColors())){
							List<AdditionalColor> listOfAdditioColor = bagMakerAttributeParser.getAdditionalColor("Additional Color");
							productConfigObj.setAdditionalColors(listOfAdditioColor);
							}
							extraLocRunChrg = extraLocRunChrg.replace("$", "").trim();
							priceGrids=bagMakersPriceGridParser.getPriceGrids(
									extraLocColorScreenChrg, "1", extraLocColorScreenChrgCode,
										ApplicationConstants.CONST_STRING_CURRENCY_USD,"",false,
										"false","Additional Color, Addiotnal Location","ADCL:Additional Color___ADLN:Additional Location",new Integer(1),"Screen Charge", "Per Quantity",
										priceGrids);	
							
						}
						break;
					case  48://Production Time
						String prodTimeLo = null;
						ProductionTime productionTime = new ProductionTime();
						prodTimeLo=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(prodTimeLo)){
					    prodTimeLo=prodTimeLo.replace("day","");
					    prodTimeLo=prodTimeLo.replace("s","");
						productionTime.setBusinessDays(prodTimeLo);
						productionTime.setDetails(ApplicationConstants.CONST_STRING_DAYS);
						List<ProductionTime> productionTimeList=new ArrayList<ProductionTime>();
						productionTimeList.add(productionTime);
						productConfigObj.setProductionTime(productionTimeList);
						}
						break;
					case  49://QCA/CPSIA Product Safety Compliant
						String complnceValuet=CommonUtility.getCellValueStrinOrInt(cell);
						 if(!StringUtils.isEmpty(complnceValuet))
						 {
							  if(complnceValuet.equalsIgnoreCase("yes")){
							  List<String> complianceList = new ArrayList<String>();
					    	  complianceList.add("CPSIA");
					    	  complianceList.add("QCA");
					    	  productExcelObj.setComplianceCerts(complianceList);
							} 
						 }
						break;
					
					}
				} // end inner while loop
			}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);		 
				ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: "
				+e.getMessage()+" at column number(increament by 1)"+columnIndex);
				productDaoObj.save(apiResponse.getErrors(),
						productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);
				}
	}
	workbook.close();
	 	
	 // setting pricing over here
	 // need to create a map over here 
	 //color upcharges
	 //same goes here as well
	 //color & location
	 // same goes here as well
	 // Ineed to set pricegrid over here
     // Add repeatable sets here
		productExcelObj=bagMakersPriceGridParser.getPricingData(listOfPrices.toString(), listOfQuantity.toString(), listOfDiscount.toString(),basePricePriceInlcude,  
																plateScreenCharge, plateScreenChargeCode,
															    plateReOrderCharge, plateReOrderChargeCode, priceGrids, 
															    productExcelObj, productConfigObj);
		
	 	productExcelObj.setPriceType("L");
	 	//productExcelObj.setPriceGrids(priceGrids);
	 	productExcelObj.setProductConfigurations(productConfigObj);
	 	/* _LOGGER.info("Product Data : "
					+ mapperObj.writeValueAsString(productExcelObj));
	 	*/
	 	/*if(xidList.contains(productExcelObj.getExternalProductId().trim())){
	 		productExcelObj.setAvailability(new ArrayList<Availability>());
	 	}*/
	 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId);
	 	if(num ==1){
	 		numOfProductsSuccess.add("1");
	 	}else if(num == 0) {
	 		numOfProductsFailure.add("0");
	 	}else{
	 		
	 	}
	 	_LOGGER.info("list size>>>>>>"+numOfProductsSuccess.size());
	 	_LOGGER.info("Failure list size>>>>>>"+numOfProductsFailure.size());
       finalResult = numOfProductsSuccess.size() + "," + numOfProductsFailure.size();
       
       productDaoObj.saveErrorLog(asiNumber,batchId);

   	//reset all list and objects over here
		priceGrids = new ArrayList<PriceGrid>();
		productConfigObj = new ProductConfigurations();
		ShipingObj=new ShippingEstimate();
		dimensionObj=new Dimensions();
		setUpchrgesVal="";
		plateScreenCharge="";
 		 plateScreenChargeCode="";
 		 plateReOrderCharge="";
 		 plateReOrderChargeCode="";
 		 extraColorRucnChrg="";
 	     extraLocRunChrg="";
 	     extraLocColorScreenChrg="";
 	    listOfQuantity = new StringBuilder();
  	    listOfPrices = new StringBuilder();
  	    listOfDiscount = new StringBuilder();
  	   basePricePriceInlcude="";
  	   tempQuant1="";
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
	
	
	public String getProductXid(Row row){
		Cell xidCell =  row.getCell(0);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid) || productXid.trim().equalsIgnoreCase("#N/A")){
		     xidCell = row.getCell(2);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}
	
	
	
	public static String removeSpecialChar(String tempValue){
		tempValue=tempValue.trim();
		tempValue=tempValue.replaceAll("(Day|Service|Sheets|Packages|Roll|Rolls|Days|Hour|Hours|Week|Weeks|Rush|R|u|s|h)", "");
		tempValue=tempValue.replaceAll("\\(","");
		tempValue=tempValue.replaceAll("\\)","");
	return tempValue;
	}
	
	public static List<String> getImprintAliasList(List<ImprintMethod> listOfImprintMethod){
		ArrayList<String> tempList=new ArrayList<String>();
		
		for (ImprintMethod tempMthd : listOfImprintMethod) {
			String strTemp=tempMthd.getAlias();
			if(strTemp.contains("LASER")|| strTemp.contains("DIRECT")|| strTemp.contains("VINYL")||strTemp.contains("DYE")||strTemp.contains("HEAT")||strTemp.contains("EPOXY")){
				tempList.add(strTemp);
			}
		}
		return tempList;
		
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


	public BagMakerAttributeParser getBagMakerAttributeParser() {
		return bagMakerAttributeParser;
	}


	public void setBagMakerAttributeParser(
			BagMakerAttributeParser bagMakerAttributeParser) {
		this.bagMakerAttributeParser = bagMakerAttributeParser;
	}


	public BagMakersPriceGridParser getBagMakersPriceGridParser() {
		return bagMakersPriceGridParser;
	}


	public void setBagMakersPriceGridParser(
			BagMakersPriceGridParser bagMakersPriceGridParser) {
		this.bagMakersPriceGridParser = bagMakersPriceGridParser;
	}
	public  static String getQuantValue(String value){
		String temp[]=value.split("-");
		return temp[0];
		
	}

}




