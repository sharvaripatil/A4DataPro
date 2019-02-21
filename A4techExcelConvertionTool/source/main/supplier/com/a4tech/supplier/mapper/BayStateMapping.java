package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;

import com.a4tech.core.model.ErrorMessageList;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.service.imple.PostServiceImpl;
import com.a4tech.supplier.dataStore.SupplierDataStore;
import com.a4tech.supplier.service.ISupplierParser;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.bayState.BayStateParser;
import parser.bayState.BayStatePriceGridParser;

public class BayStateMapping implements ISupplierParser{
	
	private static final Logger _LOGGER = Logger.getLogger(BayStateMapping.class);
	
	private PostServiceImpl 				postServiceImpl;
	private ProductDao 						productDaoObj;
	private BayStateParser                  bayStateParser;
	private BayStatePriceGridParser         bayStatePriceGridParser;
	
	@Override
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  List<String> repeatRows = new ArrayList<>();
 		try{
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		String xid = null;
		int columnIndex=0;

		 List<ProductSkus> listProductSkus = new ArrayList<>();
		 StringBuilder productSummary = new StringBuilder();
		 String maxImprColors = "";
		 String imprintMethod = "";
		 List<ImprintMethod> imprintMethodList = new ArrayList<>();
		 String shippingDimension = "";
		 String shippingWeight  = "";
		 StringJoiner listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		 StringJoiner listOfPrices   = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		 StringJoiner listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		 String specialQty = "";
		 String specialPrice = "";
		 String specialDis = "";
		 Set<String> productNumbersAndColors = new HashSet<>();
		 String productNumber = "";
		 String color = "";
		 StringBuilder colorsList = new StringBuilder();
		 String description = "";
		while (iterator.hasNext()) {
			try{
			Row nextRow = iterator.next();
			if(nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO){
				continue;
			}
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if(xid != null){
				productXids.add(xid);
				//repeatRows.add(xid);
			}
			 boolean checkXid  = false;
			
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				 columnIndex = cell.getColumnIndex();
				 if (columnIndex == 0) {
						xid = getProductXid(nextRow);
						checkXid = true;
					} else {
						checkXid = false;
					}
				if (columnIndex == 0){
					if(repeatRows.contains(xid)){
						checkXid = false;
					}
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							 System.out.println("Java object converted to JSON String, written to file");
							    
							 if(!StringUtils.isEmpty(productSummary.toString())) {
								 productExcelObj.setSummary(CommonUtility.getStringLimitedChars(productSummary.toString(), 130));
							 } else {
								 if(description.contains(".")) {
									 productExcelObj.setSummary(description.substring(0, description.indexOf(".")+1));
								 } else {
									 productExcelObj.setSummary(description);
								 }
							 }
						  
						     /*if(priceGrids.size() == 1){
						    	 priceGrids = removeBasePriceConfig(priceGrids);
						     }*/
						   if(!StringUtils.isEmpty(colorsList.toString())){
							   List<Color> productColors = bayStateParser.getProductColor(colorsList.toString());
							   productConfigObj.setColors(productColors);
						   }
						     productConfigObj.setImprintMethods(imprintMethodList);
									ShippingEstimate shippingEst = bayStateParser.getProductShipping(shippingDimension,
											shippingWeight);
									productConfigObj.setShippingEstimates(shippingEst);
								/*	if(!CollectionUtils.isEmpty(productNumbersAndColors)){
										List<ProductNumber> listOfProductNumbers = bayStateParser.getProductNumbers(productNumbersAndColors);
										productExcelObj.setProductNumbers(listOfProductNumbers);
										productExcelObj.setAsiProdNo("");
									}*/
							 	productExcelObj.setPriceGrids(priceGrids);
							 	productExcelObj.setProductConfigurations(productConfigObj);
							 	productExcelObj.setProductRelationSkus(listProductSkus);
							 		int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId, environmentType);
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
								SupplierDataStore.clearProductColorSet();
								repeatRows.clear();
								listProductSkus = new ArrayList<>();
								maxImprColors = "";
								imprintMethod = "";
								imprintMethodList = new ArrayList<>();
								shippingDimension = "";
							    shippingWeight  = "";
							    listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								listOfPrices   = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								  specialQty = "";
								  specialPrice = "";
								  specialDis = "";
								  productNumbersAndColors = new HashSet<>();
								  productNumber = "";
								  color = "";
								  colorsList = new StringBuilder();
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    	//repeatRows.add(xid);
						    }
						    productExcelObj = new Product();
     						 productExcelObj = postServiceImpl.getProduct(accessToken, xid, environmentType);
						   
						     if(productExcelObj == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						    	
						     }else{
						    	productExcelObj= bayStateParser.keepExistingProductData(productExcelObj);
						    	 productConfigObj=productExcelObj.getProductConfigurations();
						     }	
					 }
				}else{
					/*if(isRepeateColumn(columnIndex+1)){
						 continue;
					 }*/
					if(productXids.contains(xid) && repeatRows.size() != 0){
						//String values = productNumber + ":"+color;
						/*if(!StringUtils.isEmpty(color)){
							productNumbersAndColors.add(values);
						}*/
						 if(isRepeateColumn(columnIndex+1)){
							 continue;
						 }
						 //color = "";
					}
				}
				
				switch (columnIndex+1) {
				case 1:
					productExcelObj.setExternalProductId(xid);
					 break;
				case 2:// product Number
					productNumber = cell.getStringCellValue();
					productExcelObj.setAsiProdNo(productNumber);
					  break;
				case 3:// Name
					String name = cell.getStringCellValue();
					if(name.contains("|")){
						name = name.replaceAll("\\|", "");
					}
					if(name.contains("â„¢")){
						name = name.replaceAll("â„¢", "a,");
					}
					if(name.contains("~")) {
						name = name.replaceAll("~", "");
					}
					name = CommonUtility.removeNonAlphaNumericInBeggingCharacter(name);
					name = CommonUtility.getStringLimitedChars(name, 60);
					productExcelObj.setName(name);
				    break;
				case 4:// description
					String desc = cell.getStringCellValue();
					desc = getFinalDescription(desc);
					description = desc;
					productExcelObj.setDescription(desc);
				    break;
				case 5:// Callout(Summary)
					String summary = cell.getStringCellValue();
					if(!productSummary.toString().contains(summary)){
						productSummary.append(summary);
					}
					break;
				case 6://Note(Summary)
					String note = cell.getStringCellValue();
					if(!StringUtils.isEmpty(note)){
						productSummary.append(" ").append(note);	
					}
					break;
				case 7://packaging
					String pack = cell.getStringCellValue();
					if(!StringUtils.isEmpty(pack)){
						List<Packaging> listOfPackaging = bayStateParser.getProductPackaging(pack);
						productConfigObj.setPackaging(listOfPackaging);
					}
					break;
				case 8://color
				 color = cell.getStringCellValue();
				 if(!StringUtils.isEmpty(color)){
					// bayStateParser.getc
					 if(color.contains("Organza Bag:")){
						 List<Option> productOption = bayStateParser.getProductOptions(color, "Product");
						 productConfigObj.setOptions(productOption);
					 } else {
						 colorsList.append(color).append(",");
					 }
				 }
					break;
				case 9: // productionTime
					String prdTime = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(prdTime)){
						List<ProductionTime> listOfProductionTime = bayStateParser.getProductionTime(prdTime);
						productConfigObj.setProductionTime(listOfProductionTime);
					}
					break;
				case 10://material
					String mtrl = cell.getStringCellValue();
					 if(!StringUtils.isEmpty(mtrl)){
						 List<Material> listOfMaterial = bayStateParser.getProductMaterial(mtrl); 
						 productConfigObj.setMaterials(listOfMaterial);
					 }
					break;
				case 11://MadeIn
					String origin = cell.getStringCellValue();
							if (!StringUtils.isEmpty(origin) && !origin.equalsIgnoreCase("USA, Supplies: USA & China")
									&& !origin.equalsIgnoreCase("LED Bulb & Batteries China; Everything else USA")) {
								List<Origin> listOfOrigin = bayStateParser.getProductOrigin(origin); 
								 productConfigObj.setOrigins(listOfOrigin);
					}
					break;
				case 12://printMethod
					imprintMethod = cell.getStringCellValue();
					if (!StringUtils.isEmpty(imprintMethod)){
						imprintMethodList = bayStateParser.getImprintMethods(imprintMethod,imprintMethodList);
					}
					break;
				case 13:// category
					//Ignore data since suplier data does not meet ASI standrd
					//String category = cell.getStringCellValue();
					/*if (!StringUtils.isEmpty(category)){
						List<String> categoryList = bayStateParser.getProductCategories(category);
						productExcelObj.setCategories(categoryList);
					}*/
					break;
				case 14:// size
					String size = cell.getStringCellValue();
					if(!StringUtils.isEmpty(size)){
						Size sizeVal = bayStateParser.getProductSize(size);
						productConfigObj.setSizes(sizeVal);
					}
					break;
				case 15://maxImprintColors
				    maxImprColors =  CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(maxImprColors)){
						List<AdditionalColor> additionalColorList = bayStateParser.getAdditionalColors(maxImprColors);
						productConfigObj.setAdditionalColors(additionalColorList);;
					}
					 break;
				case 16://imprintArea
					String imprintArea = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintArea)){
						List<ImprintSize> listOfImprintSize = bayStateParser.getProductImprintSize(imprintArea);
						productConfigObj.setImprintSize(listOfImprintSize);
					}
					  break;
				case 17://setUpcharge
					String setupCharge = cell.getStringCellValue();
					if(!StringUtils.isEmpty(setupCharge)){
						setupCharge = setupCharge.replaceAll("[^0-9.]", "");
						priceGrids = bayStatePriceGridParser.getUpchargePriceGrid("1", setupCharge, "G",
								"Imprint Method", false, "USD", "", imprintMethod, "Set-up Charge", "Other", 1,
								priceGrids, "", "");
					}
				    break;
				case 18://AdditionalImprintRunningCharge
					String addImprintRunnChare = cell.getStringCellValue();
					if(!StringUtils.isEmpty(maxImprColors) && !StringUtils.isEmpty(addImprintRunnChare) && !addImprintRunnChare.equals("N/A")){
						addImprintRunnChare = addImprintRunnChare.replaceAll("[^0-9.]", "");
						String upchargeName = "Additional Color (max x colors):"+maxImprColors;
								priceGrids = bayStatePriceGridParser.getUpchargePriceGrid("1", addImprintRunnChare, "G",
										"Additional Colors", false, "USD", "Per piece, per color. Max "+maxImprColors+" colors.", upchargeName, "Run Charge", "Other", 1,
										priceGrids, "", "");
					}
				    break;
				case 19://LaserRunningCharge
					String laserRunningCharge = cell.getStringCellValue();
					if(!StringUtils.isEmpty(laserRunningCharge) && !laserRunningCharge.equals("N/A")){
						laserRunningCharge = laserRunningCharge.replaceAll("[^0-9.]", "");
						if(!islaserEngravedAvaialble(imprintMethodList, "Laser Engraved")){
							imprintMethodList = bayStateParser.getImprintMethods("Laser Engraved",imprintMethodList);
						}
								priceGrids = bayStatePriceGridParser.getUpchargePriceGrid("1", laserRunningCharge, "G",
										"Imprint Method", false, "USD", "","Laser Engraved", "Run Charge", "Per Quantity", 1,
										priceGrids, "", "");
					}
					break;
				case 20://PMSMatchingCHARGE
					String pmsCharge = cell.getStringCellValue();
					if(!StringUtils.isEmpty(pmsCharge) && !pmsCharge.equals("N/A")){
						String priceInclude = pmsCharge.split("\\)")[1].trim();
						pmsCharge = pmsCharge.replaceAll("[^0-9.]", "");
								priceGrids = bayStatePriceGridParser.getUpchargePriceGrid("1", pmsCharge, "G",
										"Imprint Method", false, "USD",priceInclude,imprintMethod, "PMS Matching Charge", "Other", 1,
										priceGrids, "", "");
					}
					break;
				case 21://ShippingBox
					shippingDimension = cell.getStringCellValue();
					if(!StringUtils.isEmpty(shippingDimension)){
						if(shippingDimension.contains(";")){
							productExcelObj.setAdditionalShippingInfo(shippingDimension);
						}
					}
					break;
				case 22://Shipping
					 shippingWeight = cell.getStringCellValue();
					break;
				case 23: //yearAdded
					// ignore as per feedback
					break;
				case 24://Qty
				case 25:
				case 26:
				case 27:
				case 28:
					String priceQty = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(priceQty)){
						listOfQuantity.add(priceQty);
					}
				    break;
				case 29:// price
				case 30:
				case 31:
				case 32:
				case 33:
					String listPrice = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(listPrice)){
						 if(!listPrice.equalsIgnoreCase("0.0")){
							 listOfPrices.add(listPrice);
						 }	
					}
					break;
				case 34:
				case 35:
				case 36:
				case 37:
				case 38:
					String discount = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(discount)){
						listOfDiscount.add(discount);
					}
				    break;
				case 39:// start
					//String startDate = cell.getStringCellValue();
					// all dates are past ,so n need ]
					/*if(!StringUtils.isEmpty(startDate)){
						if(CommonUtility.isPriceConfirmThroughDate(startDate)){
						}
					}*/
					break;
				case 40:// expire
					String expireDate = "";
					if(HSSFDateUtil.isCellDateFormatted(cell)){
						Date date = cell.getDateCellValue();
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						 expireDate = df.format(date);
						if(CommonUtility.isPriceConfirmThroughDate(expireDate)){
							productExcelObj.setPriceConfirmedThru(expireDate);
						}
					} else{
						expireDate = cell.getStringCellValue();
					}
					/*if(!StringUtils.isEmpty(expireDate)){
						
					}*/
					break;
				case 41://specialQty
					 specialQty = CommonUtility.getCellValueStrinOrInt(cell);
					break;
				case 42://specialPrice
					 specialPrice = CommonUtility.getCellValueDouble(cell);
					break;
				case 43: //specDisc
					 specialDis = CommonUtility.getCellValueStrinOrInt(cell);
					break;
				case 44://specStartDate
					//String specStartDate = cell.getStringCellValue();
					break;
				case 45:// specExpireDate 
					//String specExpireDate = cell.getStringCellValue();
					// case 44 & 45 are dates vaild
					 break;
				case 46://ThumbImage
					//ignore as per feedback
					  
					break;
				case 47:// Large Image
					String image = cell.getStringCellValue();
					if(!StringUtils.isEmpty(image)){
						List<Image> imageList = bayStateParser.getProductImages(image);
						productExcelObj.setImages(imageList);
					}
				    break;
				case 48://imprintTemplate
					//Ignore as per feedback
				    break;
			}  // end inner while loop
					 
		}
				productExcelObj.setPriceType("L");
				if(StringUtils.isEmpty(specialPrice)){
					if(xid.equalsIgnoreCase("548-4885607")){
						priceGrids = bayStatePriceGridParser.getBasePriceGrid(listOfPrices.toString(), 
								listOfQuantity.toString(), listOfDiscount.toString(), "USD",
								         "", true, false, color,"Product Color",priceGrids,"","",productNumber);
						productExcelObj.setAsiProdNo("");
					} else{
						priceGrids = bayStatePriceGridParser.getBasePriceGrid(listOfPrices.toString(), 
								listOfQuantity.toString(), listOfDiscount.toString(), "USD",
								         "", true, false, "","",priceGrids,"","","");
					}
					
				}else {
					priceGrids = bayStatePriceGridParser.getBasePriceGrid(specialPrice, 
							specialQty, specialDis.toString(), "USD",
							         "", true, false, "","",priceGrids,"","","");
				}
					
			    repeatRows.add(xid);
			    listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			    listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			    listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);		 
				ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: "
				+e.getMessage()+" at column number(increament by 1)"+columnIndex);
				productDaoObj.save(apiResponse.getErrors(),
						productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);
		}
		}
		workbook.close();
		
		 if(!StringUtils.isEmpty(productSummary.toString())) {
			 productExcelObj.setSummary(CommonUtility.getStringLimitedChars(productSummary.toString(), 130));
		 } else {
			 if(description.contains(".")) {
				 productExcelObj.setSummary(description.substring(0, description.indexOf(".")+1));
			 } else {
				 productExcelObj.setSummary(description);
			 }
		 }
	  
	     /*if(priceGrids.size() == 1){
	    	 priceGrids = removeBasePriceConfig(priceGrids);
	     }*/
	     productConfigObj.setImprintMethods(imprintMethodList);
				ShippingEstimate shippingEst = bayStateParser.getProductShipping(shippingDimension,
						shippingWeight);
				if(!StringUtils.isEmpty(colorsList.toString())){
					   List<Color> productColors = bayStateParser.getProductColor(colorsList.toString());
					   productConfigObj.setColors(productColors);
				   }
				productConfigObj.setShippingEstimates(shippingEst);
				/*if(!CollectionUtils.isEmpty(productNumbersAndColors)){
					List<ProductNumber> listOfProductNumbers = bayStateParser.getProductNumbers(productNumbersAndColors);
					productExcelObj.setProductNumbers(listOfProductNumbers);
					productExcelObj.setAsiProdNo("");
				}*/
		 	productExcelObj.setPriceGrids(priceGrids);
		 	productExcelObj.setProductConfigurations(productConfigObj);
		 	productExcelObj.setProductRelationSkus(listProductSkus);
		 	
		 	
		 	String descTemp="";
		 	descTemp=productExcelObj.getDescription();
		 	if(descTemp.contains("?")) {
		 		descTemp = descTemp.replaceAll("\\?", "");
		 		System.out.println(descTemp);
		 		productExcelObj.setDescription(descTemp);
			}
		 	
		 	
		 	
		 	
		 		int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId, environmentType);
			 	if(num ==1){
			 		numOfProductsSuccess.add("1");
			 	}else if(num == 0){
			 		numOfProductsFailure.add("0");
			 	}else{
			 		
			 	}		
		 	SupplierDataStore.clearProductColorSet();
		 	_LOGGER.info("list size>>>>>>"+numOfProductsSuccess.size());
		 	_LOGGER.info("Failure list size>>>>>>"+numOfProductsFailure.size());
	       finalResult = numOfProductsSuccess.size() + "," + numOfProductsFailure.size();
	       productDaoObj.saveErrorLog(asiNumber,batchId);
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
		Cell xidCell =  row.getCell(ApplicationConstants.CONST_NUMBER_ZERO);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid)){
		     xidCell = row.getCell(1);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}
	
	public Size getProductSize(List<Value> sizeValues){
		Size size = new Size();
		Apparel appareal = new Apparel();
		appareal.setValues(sizeValues);
		appareal.setType(ApplicationConstants.SIZE_TYPE_STANDARD_AND_NUMBERED);
		size.setApparel(appareal);
		return size;
	}
	
	public boolean isRepeateColumn(int columnIndex){
		/*if (columnIndex != 3  && columnIndex != 6
				&& columnIndex != 8 && columnIndex != 10 && columnIndex != 11) {
				return ApplicationConstants.CONST_BOOLEAN_TRUE;
			}*/
		if (columnIndex != 2  && columnIndex != 8 &&!(columnIndex >=24  && columnIndex <= 38)){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	private List<PriceGrid> removeBasePriceConfig(List<PriceGrid> oldPriceGrid){
		List<PriceGrid> newPricegrid = new ArrayList<>();
		for (PriceGrid priceGrid : oldPriceGrid) {
			if(priceGrid.getIsBasePrice()){
				priceGrid.setPriceConfigurations(new ArrayList<>());
				newPricegrid.add(priceGrid);
			} else {
				newPricegrid.add(priceGrid);	
			}
		}
		return newPricegrid;
	}
	private String getFinalDescription(String desc) {
		desc = desc.replaceAll(";", ".");
		if(desc.contains("â")) {
			desc = desc.replaceAll("â", "");
		}
		if(desc.contains("€")) {
			desc = desc.replaceAll("€", "");
		}
		
		/*if(desc.contains("â€")) {
			desc = desc.replaceAll("â€", "");
		}*/
		if(desc.contains("?")) {
			desc = desc.replaceAll("\\?", "");
		}
		return desc;
	}
	private boolean islaserEngravedAvaialble(List<ImprintMethod> imprintMethodList,String imprintMethodName){
		return imprintMethodList.stream().filter(method -> method.getType().equals(imprintMethodName)).findAny()
				.isPresent();
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
	public BayStateParser getBayStateParser() {
		return bayStateParser;
	}

	public void setBayStateParser(BayStateParser bayStateParser) {
		this.bayStateParser = bayStateParser;
	}
	public BayStatePriceGridParser getBayStatePriceGridParser() {
		return bayStatePriceGridParser;
	}

	public void setBayStatePriceGridParser(BayStatePriceGridParser bayStatePriceGridParser) {
		this.bayStatePriceGridParser = bayStatePriceGridParser;
	}
}
