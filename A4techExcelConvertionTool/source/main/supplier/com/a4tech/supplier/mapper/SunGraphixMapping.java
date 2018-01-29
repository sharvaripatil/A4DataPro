package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import parser.primeline.PrimeLineConstants;
import parser.sunGraphix.SunGraphixAttributeParser;
import parser.sunGraphix.SunGraphixConstants;
import parser.sunGraphix.SunGraphixPriceGridParser;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.Personalization;
import com.a4tech.product.model.Price;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;
import com.a4tech.product.riversend.mapping.RiversEndExcelMapping;
import com.a4tech.product.riversend.parser.RiverEndAttributeParser;
import com.a4tech.product.riversend.parser.RiverEndPriceGridParser;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SunGraphixMapping implements IExcelParser{
	private static final Logger _LOGGER = Logger.getLogger(SunGraphixMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private SunGraphixAttributeParser sunGraphixAttributeParser;
	private SunGraphixPriceGridParser sunGraphixPriceGridParser;
	@Autowired
	ObjectMapper mapperObj;
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
		
		StringBuilder FinalKeyword = new StringBuilder();
		StringBuilder AdditionalInfo = new StringBuilder();
		String AddionnalInfo1=null;
		//List<Material> listOfMaterial =new ArrayList<Material>();
		List<String> productKeywords = new ArrayList<String>();
		List<String> listOfCategories = new ArrayList<String>();
		List<ProductSkus> ProductSkusList = new ArrayList<>();
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  Product existingApiProduct = null;
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  List<PriceGrid> priceGrids = null;
		  Set<String> listOfColors = new HashSet<>();
		  String colorCustomerOderCode ="";
		  List<String> repeatRows = new ArrayList<>();
		  Map<String, String> colorIdMap = new HashMap<>();
		  String sizeDsec=null;
		  String sizeItemNo=null;
		  String upc_no;
		  Set<String> colorSet = new HashSet<String>(); 
		  List<Color> colorList = new ArrayList<Color>();
		  HashMap<String, String>  priceGridMap=new HashMap<String, String>();
		  HashMap<String, String>  priceGridMapTemp=new HashMap<String, String>();
		  List<ProductNumber> pnumberList = new ArrayList<ProductNumber>();
		  String productNumber=null;
		  HashSet<String> sizeValuesSet = new HashSet<>();
		  HashSet<String> productOptionSet = new HashSet<String>(); // This Set used for product Availability
		  List<Availability> listOfAvailablity=new ArrayList<Availability>();
		  String sizeValue=null;
		  String Keyword1 =null;
		
		  String productName=null;
		  ShippingEstimate shippingEstObj = new ShippingEstimate();
		  StringBuilder listOfQuantity = new StringBuilder();
		  StringBuilder listOfPrices = new StringBuilder();
		  StringBuilder priceIncludes = new StringBuilder();
		  StringBuilder listOfDiscCodes = new StringBuilder();
		  String priceType="";
		  List<String> imagesList   = new ArrayList<String>();
		  try{
				 
				_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
			    Sheet sheet = workbook.getSheetAt(0);
				Iterator<Row> iterator = sheet.iterator();
				_LOGGER.info("Started Processing Product");
				String currentValue=null;
			    String lastValue=null;
			    String productId = null;
			    String xid = null;
			    int columnIndex=0;
				while (iterator.hasNext()) {
					
					try{
					Row nextRow = iterator.next();
					if(nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO || nextRow.getRowNum() == 1){
						continue;
					}
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					if(xid != null){
						productXids.add(xid);
						repeatRows.add(xid);
					}
					
					 boolean checkXid  = false; //imp line
					//boolean checkXid  = true; //imp line
					//xid=getProductXid(nextRow);
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
								 if(nextRow.getRowNum() != 2){
									 if(!CollectionUtils.isEmpty(imagesList)){
			 								List<Image> listOfImages = sunGraphixAttributeParser.getImages(imagesList);
			 								productExcelObj.setImages(listOfImages);
											}
							 priceGrids = sunGraphixPriceGridParser.getPriceGrids(listOfPrices.toString(),listOfQuantity.toString(), 
									 listOfDiscCodes.toString(),ApplicationConstants.CONST_STRING_CURRENCY_USD,
										"",ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
										"",null,1,null,null,null,productExcelObj.getPriceGrids());
							     productExcelObj.setPriceGrids(priceGrids);
								 productExcelObj.setProductConfigurations(productConfigObj);
								 if(!StringUtils.isEmpty(priceType)){
									 if(priceType.equalsIgnoreCase("LIST")){
										 productExcelObj.setPriceType("L");
									 }else{
										 productExcelObj.setPriceType("N");
									 }
								
								 }else{
									 productExcelObj.setPriceType("L");
								 }
								 /*_LOGGER.info("Product Data : "
											+ mapperObj.writeValueAsString(productExcelObj));*/
								 	/*if(XIDS.contains(productExcelObj.getExternalProductId().trim())){
								 		productExcelObj.setAvailability(new ArrayList<Availability>());
								 	}*/
							 int num = 0;//postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId, environmentType);
							 	if(num ==1){
							 		numOfProductsSuccess.add("1");
							 	}else if(num == 0){
							 		numOfProductsFailure.add("0");
							 	}else{
							 		
							 	}
							 	_LOGGER.info("list size>>>>>>>"+numOfProductsSuccess.size());
							 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
								priceGrids = new ArrayList<PriceGrid>();
								productConfigObj = new ProductConfigurations();
								listOfColors = new HashSet<>();
								
								repeatRows.clear();
								colorSet=new HashSet<String>(); 
								colorList = new ArrayList<Color>();
								
								priceGridMap=new HashMap<String, String>();
								priceGridMapTemp=new HashMap<String, String>();
								pnumberList=new ArrayList<ProductNumber>();
								
								sizeValuesSet = new HashSet<>();
								sizeValuesSet = new HashSet<>();
								 imagesList   = new ArrayList<String>();
								//ProductDataStore.clearSizesBrobery();
								 listOfDiscCodes=new StringBuilder();
							        listOfPrices=new StringBuilder();
							        listOfQuantity=new StringBuilder();
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    	repeatRows.add(xid);
						    }
						    productExcelObj = new Product();
						    existingApiProduct = postServiceImpl.getProduct(accessToken, xid, environmentType); 
						     if(existingApiProduct == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						    	 
						    	 productExcelObj = sunGraphixAttributeParser.getExistingProductData(existingApiProduct, existingApiProduct.getProductConfigurations());
						    	 productConfigObj=productExcelObj.getProductConfigurations();
						    	    //productExcelObj=existingApiProduct;
									//productConfigObj=productExcelObj.getProductConfigurations();
								   // priceGrids = productExcelObj.getPriceGrids();
						     }
							
					 }
				}else{
					if(productXids.contains(xid) && repeatRows.size() != 1){
						 if(isRepeateColumn(columnIndex+1)){
							 continue;
						 }
					}
				}
		
				switch (columnIndex + 1) {
				case 1: //Existing Xids
					productExcelObj.setExternalProductId(xid);
					break;
				case 2: //Item #
					productExcelObj.setExternalProductId(xid);
					break;
				case 3: //Parent #
					productExcelObj.setExternalProductId(xid);
					break;
				case 4: //Product Name
					 productName=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(productName)){
							productName=CommonUtility.getStringLimitedChars(productName, 60);
							productExcelObj.setName(productName);
						}
					break;
				case 5: //Category
					break;
				case 6: //Product Description
					String descripton=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(descripton)){
						 descripton=CommonUtility.getStringLimitedChars(descripton, 800);
						 descripton=CommonUtility.removeRestrictSymbols(descripton);
						 productExcelObj.setDescription(descripton);
						
						 String nameTemp=productExcelObj.getName();
							 if(StringUtils.isEmpty(nameTemp)){
								 productName=CommonUtility.getStringLimitedChars(descripton, 60);
								 productExcelObj.setName(productName);
							 }
							}else{
								productExcelObj.setDescription(productName);
							}
					break;
				case 7: //Summary Description
					String summary = cell.getStringCellValue();
					 if(!StringUtils.isEmpty(summary)){
						 productExcelObj.setSummary(CommonUtility.getStringLimitedChars(summary, 130));
					 }
					break;
				case 8: //Cat.Page"
					break;
				case 9: //Page Size"
					String sizeVaalue=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(sizeVaalue)){
						 Size size =sunGraphixAttributeParser.getSizes( sizeVaalue,new Size(),new Dimension(),new ArrayList<Values>());
						 productConfigObj.setSizes(size);
					 }
					break;
				case 10: //Sheets (Pages)
					String sheetPages=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(sheetPages)){
						 String tempDesc=productExcelObj.getDescription();
						 if(!StringUtils.isEmpty(tempDesc)){
							 if(tempDesc.length()<800){
								 tempDesc=tempDesc+" "+sheetPages;
						 sheetPages=CommonUtility.getStringLimitedChars(tempDesc, 800);
						 sheetPages=CommonUtility.removeRestrictSymbols(sheetPages);
						 productExcelObj.setDescription(sheetPages);
							 }
						 }else{
							 sheetPages=CommonUtility.getStringLimitedChars(sheetPages, 800);
							 sheetPages=CommonUtility.removeRestrictSymbols(sheetPages);
							 productExcelObj.setDescription(sheetPages);
						 }
						 /*String nameTemp=productExcelObj.getName();
							 if(StringUtils.isEmpty(nameTemp)){
								 productName=CommonUtility.getStringLimitedChars(descripton, 60);
								 productExcelObj.setName(productName);
							 }*/
							}/*else{
								productExcelObj.setDescription(productName);
							}*/
					break;
				case 11: //Ruled / Blank Pgs.
					String ruledBlankPg=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(ruledBlankPg)){
						 String tempDesc=productExcelObj.getDescription();
						 if(!StringUtils.isEmpty(tempDesc)){
							 if(tempDesc.length()<800){
								 tempDesc=tempDesc+" "+ruledBlankPg;
								 ruledBlankPg=CommonUtility.getStringLimitedChars(tempDesc, 800);
								 ruledBlankPg=CommonUtility.removeRestrictSymbols(ruledBlankPg);
								 productExcelObj.setDescription(ruledBlankPg);
							 }
						 }else{
							 ruledBlankPg=CommonUtility.getStringLimitedChars(ruledBlankPg, 800);
							 ruledBlankPg=CommonUtility.removeRestrictSymbols(ruledBlankPg);
							 productExcelObj.setDescription(ruledBlankPg);
						 }
							}
					break;
				case 12: //Cover Material"
					String material = cell.getStringCellValue();
					if(!StringUtils.isEmpty(material)){
						material = material.trim();
						if(!StringUtils.isEmpty(material)){
							List<Material> listOfMaterial = sunGraphixAttributeParser.getMaterialList(material);
							productConfigObj.setMaterials(listOfMaterial);
						}
					}
					break;
				case 13: //Hard orFlex."
					String hardFlex=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(hardFlex)){
						 String tempDesc=productExcelObj.getDescription();
						 if(!StringUtils.isEmpty(tempDesc)){
							 if(tempDesc.length()<800){
								 tempDesc=tempDesc+" "+hardFlex;
								 hardFlex=CommonUtility.getStringLimitedChars(tempDesc, 800);
								 hardFlex=CommonUtility.removeRestrictSymbols(hardFlex);
								 productExcelObj.setDescription(hardFlex);
							 }
						 }else{
							 hardFlex=CommonUtility.getStringLimitedChars(hardFlex, 800);
							 hardFlex=CommonUtility.removeRestrictSymbols(hardFlex);
							 productExcelObj.setDescription(hardFlex);
						 }
							}
					break;
				case 14: //Cover Color(s)"
					String colorValue=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(colorValue)){
						 List<Color> colors =sunGraphixAttributeParser.getProductColors(colorValue);
						 productConfigObj.setColors(colors);
					 }
					break;
				case 15: //ImprintMethod"
					String impmtdValue=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(impmtdValue)){
						 productConfigObj= sunGraphixAttributeParser.getImprintMethod(impmtdValue,productExcelObj.getProductConfigurations());
						productExcelObj.setProductConfigurations(productConfigObj);
					    }
					break;
				case 16: //Imprint Area
					String impSize=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(impSize)){
                        
						 List<ImprintSize> listOfImpSize=new ArrayList<ImprintSize>();
						 
						 listOfImpSize=	sunGraphixAttributeParser.getProductImprintSize(impSize);
						 productConfigObj.setImprintSize(listOfImpSize);
					 }
				
					break;
				case 17: //Pers.
					String personalizationVaue=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(personalizationVaue)){
						 personalizationVaue=personalizationVaue.toUpperCase().trim();
						 if(personalizationVaue.equals("YES") || personalizationVaue.contains("PER")){
							 List<Personalization> listPers = new ArrayList<Personalization>();
							 listPers = sunGraphixAttributeParser.getPersonalizationCriteria(ApplicationConstants.CONST_STRING_PERSONALIZATION);
							 productConfigObj.setPersonalization(listPers);
							  
						 }
						/* listPers = personlizationParser.getPersonalizationCriteria(ApplicationConstants.CONST_STRING_PERSONALIZATION);
							productConfigObj.setPersonalization(listPers);*/
					  }
					break;
				case 18: //Pocket (s)
					break;
				case 19: //Ribbon
					break;
				case 20: //Elastic
					break;
				case 21: //Qty1
				case 22: //Qty2
				case 23: //Qty3
				case 24: //Qty4
				case 25: //Qty5
				case 26: //Qty6
					 try{//listOfQuantity//=CommonUtility.getCellValueStrinOrDecimal(cell);
						 String	quantity =CommonUtility.getCellValueStrinOrInt(cell);
						 if(!StringUtils.isEmpty(quantity)&& !quantity.equals("0")){
							 listOfQuantity.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						 }
							
					 }catch (Exception e) {
						_LOGGER.info("Error in base price prices field "+e.getMessage());
					}
					break;
				case 27: //Prc1
				case 28: //Prc2
				case 29: //Prc3
				case 30: //Prc4
				case 31: //Prc5
				case 32: //Prc6
					try{//listOfQuantity//=CommonUtility.getCellValueStrinOrDecimal(cell);
						 String	priceList =CommonUtility.getCellValueStrinOrDecimal(cell);
						 if(!StringUtils.isEmpty(priceList)&& !priceList.equals("0")){
							 listOfPrices.append(priceList).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						 }
							
					 }catch (Exception e) {
						_LOGGER.info("Error in base price prices field "+e.getMessage());
					}
					break;
				case 33: //Currency
					break;
				case 34: //List or Net
					priceType=CommonUtility.getCellValueStrinOrInt(cell);
					if(StringUtils.isEmpty(priceType)){
						priceType="LIST";
					}
					break;
				case 35: //Pricing Codes
					String	discountCode=null;
					discountCode=CommonUtility.getCellValueStrinOrDecimal(cell);
					if(!StringUtils.isEmpty(discountCode) && !discountCode.toUpperCase().equals("NULL")){
						listOfDiscCodes=SunGraphixConstants.SUNDISCOUNTCODE_MAP.get(discountCode.trim());
						if(StringUtils.isEmpty(listOfDiscCodes)){
							listOfDiscCodes.append("Z___Z___Z___Z___Z___Z___Z___Z___Z___Z"); 
						}
						}else{
			        	 listOfDiscCodes.append("Z___Z___Z___Z___Z___Z___Z___Z___Z___Z"); 
			         }
					break;
				case 36: //Die Charge
					break;
				case 37: //Personalization
					break;
				case 38: //Pen Loop
					break;
				case 39: //Ruler
					break;
				case 40: //Dividers
					break;
				case 41: //Filofax Insert Pages 2pgs
					break;
				case 42: //Filofax Insert Pages 4 pgs
					break;
				case 43: //Filofax Insert Pages 6 pgs
					break;
				case 44: //Filofax Insert Pages 8 pgs
					break;
				case 45: //Gift Box
					break;
				case 46: //Mailer
					break;
				case 47: //Insert Fee
					break;
				case 48: //Drop Shipments
					//bottom
					//create option over here
					String shipOption=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(shipOption)){
					List<Option> optionList=sunGraphixAttributeParser.getOptions("Drop Shipping","Drop Shipments","First one is free. Drop ship charge per additional address.",false,false,"Shipping");
					productConfigObj.setOptions(optionList);
					//create pricegrid over here141
					if(shipOption.contains("10.50(G)")){
						priceGrids = sunGraphixPriceGridParser.getPriceGrids(
								"10.50","1","G",
								ApplicationConstants.CONST_STRING_CURRENCY_USD,"",false,
								"false","Drop Shipments","Shipping Option",new Integer(1),"Shipping Charge", "Per Order","Optional",
								new ArrayList<PriceGrid>());
						/*String listOfPrices, String listOfQuan, String discountCodes,
						String currency, String priceInclude, boolean isBasePrice,
						String qurFlag, String priceName, String criterias,Integer sequence,String upChargeType,String upchargeUsageType,String serviceCharge
						List<PriceGrid> existingPriceGrid)*/					
						
						}
					 }
				
					break;
				case 49: //Distributor Comments Only
					String Distributorcomment = cell.getStringCellValue();
					if(!StringUtils.isEmpty(Distributorcomment)){
					//Distributorcomment=removeSpecialChar(Distributorcomment);
					productExcelObj.setDistributorOnlyComments(Distributorcomment);
					}else{
						productExcelObj.setDistributorOnlyComments(ApplicationConstants.CONST_STRING_EMPTY);
					}
					
					break;
				case 50: //FOB
					String fobPoint=cell.getStringCellValue();
					if(!StringUtils.isEmpty(fobPoint)){
						fobPoint=fobPoint.toUpperCase().trim();
						 if(fobPoint.equals("tx") || fobPoint.equals("ny")){
						FOBPoint fobPointObj=new FOBPoint();
						List<FOBPoint> listfobPoints = new ArrayList<FOBPoint>();
						if(fobPoint.equals("tx")){
							fobPointObj.setName("Dallas, TX 75019 USA");
						}else {
							fobPointObj.setName("Champlain, NY 12919 USA");
						}
						listfobPoints.add(fobPointObj);
						productExcelObj.setFobPoints(listfobPoints);
						 }
					}
					break;
				case 51: //Production Time"
					//production Time
					String prodTimeLo = null;
					List<ProductionTime> listOfProductionTime = new ArrayList<ProductionTime>();
					int tempVal;
					ProductionTime productionTime = new ProductionTime();
					prodTimeLo=CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(prodTimeLo)){
					prodTimeLo=prodTimeLo.toLowerCase().trim();
				    prodTimeLo=prodTimeLo.replaceAll(ApplicationConstants.CONST_STRING_DAYS,ApplicationConstants.CONST_STRING_EMPTY);
					productionTime.setBusinessDays(prodTimeLo.trim());
					productionTime.setDetails(ApplicationConstants.CONST_STRING_DAYS);
					listOfProductionTime.add(productionTime);
					productConfigObj.setProductionTime(listOfProductionTime);
					}
					break;
				case 52: //Units / Weight
					String shippingItem=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(shippingItem)){
						 shippingEstObj=sunGraphixAttributeParser.getShippingEstimates(shippingItem,shippingEstObj,"NOI");
						 productConfigObj.setShippingEstimates(shippingEstObj);
					 }
					break;
				case 53: //L
					String shippingLen=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(shippingLen)){
						 shippingEstObj=sunGraphixAttributeParser.getShippingEstimates(shippingLen,shippingEstObj,"NOI");
						 productConfigObj.setShippingEstimates(shippingEstObj);
					 }
					break;
				case 54: //W
					String shippingWT=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(shippingWT)){
						 shippingEstObj=sunGraphixAttributeParser.getShippingEstimates(shippingWT,shippingEstObj,"NOI");
						 productConfigObj.setShippingEstimates(shippingEstObj);
					 }
					break;
				case 55: //H
					String shippingHT=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(shippingHT)){
						 shippingEstObj=sunGraphixAttributeParser.getShippingEstimates(shippingHT,shippingEstObj,"NOI");
						 productConfigObj.setShippingEstimates(shippingEstObj);
					 }
					break;
				case 56: //Book
					String prodItemWT=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(prodItemWT)){
						 Volume itemWeight=new Volume();
						 itemWeight=sunGraphixAttributeParser.getItemWeightvolume(prodItemWT);
					      productConfigObj.setItemWeight(itemWeight);
					 }
					
					break;
				case 57: //Gift Box
					String packGTValue=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(packGTValue)){
						 List<Packaging>          listPackaging               = new ArrayList<Packaging>();
						 listPackaging=sunGraphixAttributeParser.getPackaging("MAILER");
						 productConfigObj.setPackaging(listPackaging);
					 }
					break;
				case 58: //Mailer
					String mailPackValue=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(mailPackValue)){
						 List<Packaging>          listPackagingM               = new ArrayList<Packaging>();
						 if(!CollectionUtils.isEmpty(productConfigObj.getPackaging())){
							 listPackagingM.addAll(productConfigObj.getPackaging());
							}
						 
						 listPackagingM=sunGraphixAttributeParser.getPackaging(mailPackValue);
						 productConfigObj.setPackaging(listPackagingM);
					 }
					break;
				case 59: //Made
					String origin = cell.getStringCellValue();
					if(!StringUtils.isEmpty(origin)){
						if(origin.toUpperCase().equals("MX")){
							origin="Mexico";
						}else if(origin.toUpperCase().equals("CA")){
							origin="Canada";
						}else if(origin.toUpperCase().equals("UK")){
							origin="United Kingdom";
						}
					List<Origin> listOfOrigins = new ArrayList<Origin>();
					Origin origins = new Origin();
					origins.setName(origin);
					listOfOrigins.add(origins);
					productConfigObj.setOrigins(listOfOrigins);
					}
					break;
				case 60: //Assem.
					break;
				case 61: //Decorate
					break;
				case 62: //keyword
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
					//need to work on dis
					break;
				case 63: //main product
					String largeImage = cell.getStringCellValue();
					if(!StringUtils.isEmpty(largeImage)){
						imagesList.add(largeImage);
					}
					//need to work on dis
					break;
				case 64: //Blank Image 1
					//need to work on dis
					// Product images
					String highImage = cell.getStringCellValue();
					if(!StringUtils.isEmpty(highImage)){
						imagesList.add(highImage);
					}
					break;
				}  // end inner while loop					 
			}		
			}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);		 
				ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: "
				+e.getMessage()+" at column number(increament by 1)"+columnIndex);
				productDaoObj.save(apiResponse.getErrors(),
						productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);
				}
		}
		workbook.close();
		 if(!CollectionUtils.isEmpty(imagesList)){
				List<Image> listOfImages = sunGraphixAttributeParser.getImages(imagesList);
				productExcelObj.setImages(listOfImages);
				}
		 priceGrids = sunGraphixPriceGridParser.getPriceGrids(listOfPrices.toString(),listOfQuantity.toString(), 
				 listOfDiscCodes.toString(),ApplicationConstants.CONST_STRING_CURRENCY_USD,
					"",ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
					"",null,1,null,null,null,productExcelObj.getPriceGrids());
		     productExcelObj.setPriceGrids(priceGrids);
			 productExcelObj.setProductConfigurations(productConfigObj);
			 if(!StringUtils.isEmpty(priceType)){
				 if(priceType.equalsIgnoreCase("LIST")){
					 productExcelObj.setPriceType("L");
				 }else{
					 productExcelObj.setPriceType("N");
				 }
			
			 }else{
				 productExcelObj.setPriceType("L");
			 }
		 	int num = 0;//postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId, environmentType);
		 	if(num ==1){
		 		numOfProductsSuccess.add("1");
		 	}else if(num == 0){
		 		numOfProductsFailure.add("0");
		 	}
		 	_LOGGER.info("list size>>>>>>"+numOfProductsSuccess.size());
		 	_LOGGER.info("Failure list size>>>>>>"+numOfProductsFailure.size());
	       finalResult = numOfProductsSuccess.size() + "," + numOfProductsFailure.size();
	    productDaoObj.saveErrorLog(asiNumber,batchId);
		priceGrids = new ArrayList<PriceGrid>();
		productConfigObj = new ProductConfigurations();
		listOfColors = new HashSet<>();
		repeatRows.clear();
		colorSet=new HashSet<String>(); 
		colorList = new ArrayList<Color>();
		AdditionalInfo=new StringBuilder();
		priceGridMap=new HashMap<String, String>();
		priceGridMapTemp=new HashMap<String, String>();
		pnumberList=new ArrayList<ProductNumber>();
		//listOfMaterial=new ArrayList<Material>();
		sizeValuesSet = new HashSet<>();
		listOfAvailablity=new ArrayList<Availability>();
		productOptionSet=new HashSet<String>();
		listOfCategories=new ArrayList<String>();
		FinalKeyword=new StringBuilder();
		//ProductDataStore.clearSizesBrobery();
       ProductSkusList = new ArrayList<ProductSkus>();
        AdditionalInfo= new StringBuilder();
        imagesList   = new ArrayList<String>();
        listOfDiscCodes=new StringBuilder();
        listOfPrices=new StringBuilder();
        listOfQuantity=new StringBuilder();
		return finalResult;
		}catch(Exception e){
			_LOGGER.error("Error while Processing excel sheet " +e.getMessage());
			return finalResult;
		}finally{
			try {
				workbook.close();
			} catch (IOException e) { 
				_LOGGER.error("Error while Processing excel sheet" +e.getMessage());
	
			}
				_LOGGER.info("Complted processing of excel sheet ");
				_LOGGER.info("Total no of product:"+numOfProductsSuccess.size() );
		}
		
	}

	

	public String getProductXid(Row row){
		Cell xidCell =  row.getCell(0);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid) || productXid.trim().equalsIgnoreCase("n/a") || productXid.trim().equalsIgnoreCase("#N/A")){
		     xidCell = row.getCell(2);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		     if(StringUtils.isEmpty(productXid) || productXid.trim().equalsIgnoreCase("n/a") || productXid.trim().equalsIgnoreCase("#N/A")){
		    	 xidCell = row.getCell(1);
			     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		     }
		}
		return productXid;
	}
	
	public boolean isRepeateColumn(int columnIndex){
		
	 /*if(columnIndex != 4&&columnIndex != 5&&columnIndex != 6 && columnIndex != 7 && columnIndex != 8 && columnIndex != 9 &&
				columnIndex != 10 &&columnIndex != 11 &&columnIndex != 12 && columnIndex != 13 && columnIndex != 15 && columnIndex != 16 &&
				columnIndex != 17 &&columnIndex != 18 &&columnIndex != 19 && columnIndex != 20 && columnIndex != 21 && columnIndex != 22 &&
				columnIndex != 23&&columnIndex != 24 && columnIndex != 25 && columnIndex != 26 && columnIndex != 27 && columnIndex != 28 &&
				columnIndex != 29 &&columnIndex != 30 &&columnIndex != 31 && columnIndex != 32 && columnIndex != 33 && columnIndex != 34 && 
				columnIndex != 35 && columnIndex != 36 && columnIndex != 37 && columnIndex != 38 && columnIndex != 39 && columnIndex != 40 &&
				columnIndex != 41 && columnIndex != 42 && columnIndex != 43 && columnIndex != 44 && columnIndex != 45 && columnIndex != 46 && columnIndex != 47 &&
				columnIndex != 48 && columnIndex != 49 && columnIndex != 50 && columnIndex != 51 && columnIndex != 52 && columnIndex != 53 && columnIndex != 54 &&
				columnIndex != 55 && columnIndex != 56 && columnIndex != 57 && columnIndex != 58 && columnIndex != 59 && columnIndex != 60 && columnIndex != 61 &&
				columnIndex != 62 )
	    {*/
		 if(columnIndex != 1 && columnIndex != 2 && columnIndex != 3 && columnIndex != 14 && columnIndex != 63 && columnIndex != 64)
		    {
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	
	public static List<PriceGrid> getPriceGrids(String basePriceName) 
	{
		
		List<PriceGrid> newPriceGrid=new ArrayList<PriceGrid>();
		try{
			Integer sequence = 1;
			List<PriceConfiguration> configuration = null;
			PriceGrid priceGrid = new PriceGrid();
			priceGrid.setCurrency(ApplicationConstants.CONST_STRING_CURRENCY_USD);
			priceGrid.setDescription(basePriceName);
			priceGrid.setPriceIncludes(ApplicationConstants.CONST_STRING_EMPTY);
			priceGrid.setIsQUR(ApplicationConstants.CONST_BOOLEAN_TRUE);
			priceGrid.setIsBasePrice(true);
			priceGrid.setSequence(sequence);
			List<Price>	listOfPrice = new ArrayList<Price>();
			priceGrid.setPrices(listOfPrice);
			priceGrid.setPriceConfigurations(configuration);
			newPriceGrid.add(priceGrid);
	}catch(Exception e){
		_LOGGER.error("Error while processing PriceGrid: "+e.getMessage());
	}
		return newPriceGrid;
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
	
	public static final String CONST_STRING_COMBO_TEXT = "Combo";
	
	public ObjectMapper getMapperObj() {
		return mapperObj;
	}
	
	public void setMapperObj(ObjectMapper mapperObj) {
		this.mapperObj = mapperObj;
	}



	public SunGraphixAttributeParser getSunGraphixAttributeParser() {
		return sunGraphixAttributeParser;
	}



	public void setSunGraphixAttributeParser(
			SunGraphixAttributeParser sunGraphixAttributeParser) {
		this.sunGraphixAttributeParser = sunGraphixAttributeParser;
	}



	public SunGraphixPriceGridParser getSunGraphixPriceGridParser() {
		return sunGraphixPriceGridParser;
	}



	public void setSunGraphixPriceGridParser(
			SunGraphixPriceGridParser sunGraphixPriceGridParser) {
		this.sunGraphixPriceGridParser = sunGraphixPriceGridParser;
	}


}
