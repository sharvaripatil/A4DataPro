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
	
	@SuppressWarnings("finally")
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
		  String productId = null;
		  String shippinglen="";
		  String shippingWid="";
		  String shippingH="";
		   String shippingWeightValue="";
		  String noOfitem="";
		  boolean existingFlag=false;
		  String prodTime="";
		  String finalProdTimeVal="";
		  String rushTime="";
		  String finalRushTimeVal="";
		  String prodTimeVal2="";
		  String finalProdTimeVal2="";
		  String plateScreenCharge="";
		  String plateScreenChargeCode="";
		  String plateReOrderCharge="";
		  String plateReOrderChargeCode="";
		  String extraColorRucnChrg="";
		  String extraLocRunChrg="";
		  String extraLocColorScreenChrg="";
		  try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
	
		Set<String>  productXids = new HashSet<String>();
		StringBuilder listOfQuantityProd1 = new StringBuilder();
		StringBuilder listOfPricesProd1 = new StringBuilder();
		
		StringBuilder listOfQuantityRush = new StringBuilder();
		StringBuilder listOfPricesRush = new StringBuilder();
		StringBuilder listOfQuantityProd2 = new StringBuilder();
		StringBuilder listOfPricesProd2 = new StringBuilder();
		
		 List<String> repeatRows = new ArrayList<>();
		 List<ProductionTime> prodTimeList=new ArrayList<ProductionTime>();
		 ShippingEstimate ShipingObj=new ShippingEstimate();
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
								 if(!StringUtils.isEmpty(plateScreenCharge)){
									 plateScreenCharge=plateScreenCharge.toUpperCase();
									 if(plateScreenCharge.contains("NO SET UP") || plateScreenCharge.contains("MULTI")){
										 productExcelObj.setDistributorOnlyComments(plateScreenCharge);
									 }else{
										 List<ImprintMethod> tempList=productConfigObj.getImprintMethods();
										 if(!CollectionUtils.isEmpty(tempList)){
											 plateScreenCharge=plateScreenCharge.replaceAll("$", "");
											 for (ImprintMethod imprintMethod : tempList) {
												//get alias over here
												 String tempALias=imprintMethod.getAlias();
												 priceGrids=bagMakersPriceGridParser.getPriceGrids(
														 plateScreenCharge, "1", plateScreenChargeCode,
															ApplicationConstants.CONST_STRING_CURRENCY_USD,"Plate/Screen Charge",false,
															"false",tempALias,"Imprint Method",new Integer(1),"Screen Charge", "Per Quantity",
															priceGrids);	
											}
										 }
										 
									 }
								 }
								 if(!StringUtils.isEmpty(plateReOrderCharge)){
									 plateReOrderCharge=plateReOrderCharge.toUpperCase();
									 if(plateReOrderCharge.contains("NO SET UP") || plateReOrderCharge.contains("MULTI")){
										 productExcelObj.setDistributorOnlyComments(plateReOrderCharge);
									 }else{
										 List<ImprintMethod> tempList=productConfigObj.getImprintMethods();
										 if(!CollectionUtils.isEmpty(tempList)){
											 plateReOrderCharge=plateReOrderCharge.replaceAll("$", "");
											 for (ImprintMethod imprintMethod : tempList) {
												//get alias over here
												 String tempALias=imprintMethod.getAlias();
												 priceGrids=bagMakersPriceGridParser.getPriceGrids(
														 plateReOrderCharge, "1", plateReOrderChargeCode,
															ApplicationConstants.CONST_STRING_CURRENCY_USD,"Reorder Plate/Screen Charge",false,
															"false",tempALias,"Imprint Method",new Integer(1),"Re-order Charge", "Per Quantity",
															priceGrids);	
											}
										 }
										 
									 }
								 }
								 
								 if(CollectionUtils.isEmpty(priceGrids)){
										priceGrids = bagMakersPriceGridParser.getPriceGridsQur();	
									}
								   // Add repeatable sets here
								 	productExcelObj.setPriceType("L");
								 	productExcelObj.setPriceGrids(priceGrids);
								 	productExcelObj.setProductConfigurations(productConfigObj);
								 	 _LOGGER.info("Product Data : "
												+ mapperObj.writeValueAsString(productExcelObj));
								 	
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
									listOfQuantityProd1 = new StringBuilder();
									 listOfPricesProd1 = new StringBuilder();
									 listOfQuantityRush = new StringBuilder();
									 listOfPricesRush = new StringBuilder();
									 listOfQuantityProd2 = new StringBuilder();
									 listOfPricesProd2 = new StringBuilder();
									 prodTime="";
									 finalProdTimeVal="";
									 rushTime="";
									 finalRushTimeVal="";
									 prodTimeVal2="";
									 finalProdTimeVal2="";
									 prodTimeList=new ArrayList<ProductionTime>();
									 ShipingObj=new ShippingEstimate();
									 setUpchrgesVal="";
									 plateScreenCharge="";
							  		 plateScreenChargeCode="";
							  		 plateReOrderCharge="";
							  		 plateReOrderChargeCode="";
							  		 extraColorRucnChrg="";
							  	     extraLocRunChrg="";
							  	     extraLocColorScreenChrg="";	
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
							bagMakerAttributeParser.getSizes(dimension);
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
						 ShipingObj =bagMakerAttributeParser.getShippingEstimates("", "", "", "", noOfitem,ShipingObj);
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
						 ShipingObj =bagMakerAttributeParser.getShippingEstimates("", "", "", shippingWeightValue, "",ShipingObj);
						 productConfigObj.setShippingEstimates(ShipingObj);
						 }
						break;
					case  16://Box Length
						shippinglen=CommonUtility.getCellValueStrinOrInt(cell);
 						if(!StringUtils.isEmpty(shippinglen.trim())){
 						shippinglen=shippinglen.toUpperCase();
 						ShipingObj =bagMakerAttributeParser.getShippingEstimates(shippinglen.trim(), "", "", "", "",ShipingObj);
 						productConfigObj.setShippingEstimates(ShipingObj);
 						}
						break;
					case  17://Box Width
						shippingWid=CommonUtility.getCellValueStrinOrInt(cell);
 						if(!StringUtils.isEmpty(shippingWid.trim())){
 						
 						ShipingObj =bagMakerAttributeParser.getShippingEstimates("", shippingWid, "", "", "",ShipingObj);
 						productConfigObj.setShippingEstimates(ShipingObj);
 						}
						break;
					case  18://Box Depth
						shippingH=CommonUtility.getCellValueStrinOrInt(cell);
 						if(!StringUtils.isEmpty(shippingH.trim())){
 						ShipingObj =bagMakerAttributeParser.getShippingEstimates("", "", shippingH, "", "",ShipingObj);
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
						origins.setName("U.S.A");
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
						plateScreenCharge=CommonUtility.getCellValueStrinOrInt(cell);
						if(StringUtils.isEmpty(plateScreenCharge)|| plateScreenCharge.trim().contains("N/A")){
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
						 plateReOrderCharge=CommonUtility.getCellValueStrinOrInt(cell);
						 if(StringUtils.isEmpty(plateReOrderCharge)|| plateReOrderCharge.trim().contains("N/A")){
							 plateReOrderCharge="";
							}
						break;
					case  26://REORDER Plate/Screen Charge Code
						 plateReOrderChargeCode=CommonUtility.getCellValueStrinOrInt(cell);
						 if(StringUtils.isEmpty(plateReOrderChargeCode)|| plateReOrderChargeCode.trim().contains("N/A") || plateReOrderChargeCode.trim().contains("Free")){
							 plateReOrderChargeCode="Z";
							}
						break;
					case  27://Quantity_1

						break;
					case  28://Price_1

						break;
					case  29://Code_1

						break;
					case  30://Quantity_2

						break;
					case  31://Price_2

						break;
					case  32://Code_2

						break;
					case  33://Quantity_3

						break;
					case  34://Price_3

						break;
					case  35://Code_3

						break;
					case  36://Quantity_4

						break;
					case  37://Price_4

						break;
					case  38://Code_4

						break;
					case  39://Quantity_5

						break;
					case  40://Price_5

						break;
					case  41://Code_5

						break;
					case  42://Extra Color Run Charge
						 extraColorRucnChrg = CommonUtility.getCellValueStrinOrInt(cell);
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
						extraLocRunChrg = CommonUtility.getCellValueStrinOrInt(cell);
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
						extraLocColorScreenChrg = CommonUtility.getCellValueStrinOrInt(cell);
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
						
						break;
					case  49://QCA/CPSIA Product Safety Compliant

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
	 if(!StringUtils.isEmpty(plateScreenCharge)){
		 plateScreenCharge=plateScreenCharge.toUpperCase();
		 if(plateScreenCharge.contains("NO SET UP") || plateScreenCharge.contains("MULTI")){
			 productExcelObj.setDistributorOnlyComments(plateScreenCharge);
		 }else{
			 List<ImprintMethod> tempList=productConfigObj.getImprintMethods();
			 if(!CollectionUtils.isEmpty(tempList)){
				 plateScreenCharge=plateScreenCharge.replaceAll("$", "");
				 for (ImprintMethod imprintMethod : tempList) {
					//get alias over here
					 String tempALias=imprintMethod.getAlias();
					 priceGrids=bagMakersPriceGridParser.getPriceGrids(
							 plateScreenCharge, "1", plateScreenChargeCode,
								ApplicationConstants.CONST_STRING_CURRENCY_USD,"Plate/Screen Charge",false,
								"false",tempALias,"Imprint Method",new Integer(1),"Screen Charge", "Per Quantity",
								priceGrids);	
				}
			 }
			 
		 }
	 }
	 if(!StringUtils.isEmpty(plateReOrderCharge)){
		 plateReOrderCharge=plateReOrderCharge.toUpperCase();
		 if(plateReOrderCharge.contains("NO SET UP") || plateReOrderCharge.contains("MULTI")){
			 productExcelObj.setDistributorOnlyComments(plateReOrderCharge);
		 }else{
			 List<ImprintMethod> tempList=productConfigObj.getImprintMethods();
			 if(!CollectionUtils.isEmpty(tempList)){
				 plateReOrderCharge=plateReOrderCharge.replaceAll("$", "");
				 for (ImprintMethod imprintMethod : tempList) {
					//get alias over here
					 String tempALias=imprintMethod.getAlias();
					 priceGrids=bagMakersPriceGridParser.getPriceGrids(
							 plateReOrderCharge, "1", plateReOrderChargeCode,
								ApplicationConstants.CONST_STRING_CURRENCY_USD,"Reorder Plate/Screen Charge",false,
								"false",tempALias,"Imprint Method",new Integer(1),"Re-order Charge", "Per Quantity",
								priceGrids);	
				}
			 }
			 
		 }
	 }
	 
	 if(CollectionUtils.isEmpty(priceGrids)){
			priceGrids = bagMakersPriceGridParser.getPriceGridsQur();	
		}
	   // Add repeatable sets here
	 	productExcelObj.setPriceType("L");
	 	productExcelObj.setPriceGrids(priceGrids);
	 	productExcelObj.setProductConfigurations(productConfigObj);
	 	 _LOGGER.info("Product Data : "
					+ mapperObj.writeValueAsString(productExcelObj));
	 	
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
		listOfQuantityProd1 = new StringBuilder();
		 listOfPricesProd1 = new StringBuilder();
		 listOfQuantityRush = new StringBuilder();
		 listOfPricesRush = new StringBuilder();
		 listOfQuantityProd2 = new StringBuilder();
		 listOfPricesProd2 = new StringBuilder();
		 prodTime="";
		 finalProdTimeVal="";
		 rushTime="";
		 finalRushTimeVal="";
		 prodTimeVal2="";
		 finalProdTimeVal2="";
		prodTimeList=new ArrayList<ProductionTime>();
		ShipingObj=new ShippingEstimate();
		setUpchrgesVal="";
		plateScreenCharge="";
 		 plateScreenChargeCode="";
 		 plateReOrderCharge="";
 		 plateReOrderChargeCode="";
 		 extraColorRucnChrg="";
 	     extraLocRunChrg="";
 	     extraLocColorScreenChrg="";
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
		tempValue=tempValue.replaceAll("(Day|Service|Days|Hour|Hours|Week|Weeks|Rush|R|u|s|h)", "");
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

}




