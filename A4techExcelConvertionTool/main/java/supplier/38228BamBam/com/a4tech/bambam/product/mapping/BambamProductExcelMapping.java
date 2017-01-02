package com.a4tech.bambam.product.mapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.bambam.product.parser.BamMaterialParser;
import com.a4tech.bambam.product.parser.BamPriceGridParser;
import com.a4tech.bambam.product.parser.BamProductAttributeParser;
import com.a4tech.bambam.product.parser.BamShippingEstimationParser;
import com.a4tech.bambam.product.parser.BamSizeParser;
import com.a4tech.bambam.product.parser.BamUtility;
import com.a4tech.bambam.product.parser.BamPackagingParser;
import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.dataStore.ProductDataStore;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.AdditionalLocation;
import com.a4tech.product.model.Artwork;
import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintColor;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Inventory;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
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
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.TradeName;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class BambamProductExcelMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(BambamProductExcelMapping.class);
	private PostServiceImpl 				postServiceImpl ;
	private ProductDao 						productDaoObj;
	private BamProductAttributeParser 		bamProductParser;
	private BamMaterialParser  				bamMaterialParser;
	private BamSizeParser     				bamSizeParser;
	private BamShippingEstimationParser 	bamShippingParser;
	private BamPriceGridParser 				bamPriceGridParser;
	private BamPackagingParser 				bamPackagingParser;		
	
	@Override
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		ImprintColor imprintColors = new ImprintColor();
		List<String> numOfProducts = new ArrayList<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  String externalProductId = null;
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
		  String serviceCharge = null;
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  int columnIndex = 0;
		try{
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		StringJoiner listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringBuilder basePriceCriteria =  new StringBuilder();
		StringJoiner UpCharQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner UpCharPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner UpCharDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
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
		String optiontype =null;
		String optionname =null;
		String optionvalues =null;
		String optionadditionalinfo =null;
		String canorder =null;
		String reqfororder =null;
		List<String> repeatRows = new ArrayList<>();
		String xid = null;
		List<Option> listOfOptions = new ArrayList<>();
		while (iterator.hasNext()) {
			Inventory inventoryObj = new Inventory();
			String shippingitemValue = null;
			String shippingdimensionValue = null;
			String sizeGroup=null;
			String rushService=null;
			String prodSample=null;
			String packagingValue = null;
			String shippingWeightValue = null;
			try{
				Row nextRow = iterator.next();
				if(nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO){
					continue;
				}
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				if(xid != null){
					productXids.add(xid);
					repeatRows.add(xid);
				}
				 boolean checkXid  = false;
				
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					
					columnIndex = cell.getColumnIndex();
					if(columnIndex == 0 || columnIndex == 1){
						continue;
					}
					if(columnIndex == 2){
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
								 	productConfigObj.setOptions(listOfOptions);
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
									listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								    listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								    listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								    upChargeQur = null;
									UpCharCriteria = new StringBuilder();
									priceQurFlag = null;
									UpCharPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
									UpCharDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
									UpCharQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
									basePriceCriteria = new StringBuilder();
									basePriceName = null;
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
									productConfigObj = new ProductConfigurations();
									ProductDataStore.clearProductColorSet();
									repeatRows.clear();
									BamUtility.clearOptions();
									listOfOptions = new ArrayList<>();
									priceIncludes = null;
									currencyType = null;
									priceType = null;
									upChargeName = null;
									upchargeType = null;
									upChargeLevel = null;
									serviceCharge = null;
									packagingValue = null;
									
							 }
							    if(!productXids.contains(xid)){
							    	productXids.add(xid);
							    	repeatRows.add(xid);
							    }
							    productExcelObj = new Product();
	     						 productExcelObj = postServiceImpl.getProduct(accessToken, xid);
							     if(productExcelObj == null){
							    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
							    	 productExcelObj = new Product();
							     }else{
							    	 productConfigObj=productExcelObj.getProductConfigurations();
							     }
								
						 }
					}else{
						if(productXids.contains(xid) && repeatRows.size() != 1){
							 if(isRepeateColumn(columnIndex+1)){
								 continue;
							 }
						}
					}
				switch (columnIndex) {
				case 2:
					 externalProductId = cell.getStringCellValue();
					productExcelObj.setExternalProductId(externalProductId);
					break;
					
				case 3:
					String name = cell.getStringCellValue();
					
					if(!StringUtils.isEmpty(name)){
					productExcelObj.setName(name.trim());
					}else{
						productExcelObj.setName(ApplicationConstants.CONST_STRING_EMPTY);
					}
					break;
		
				case 4:
					String asiProdNo = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(asiProdNo)){
						productExcelObj.setAsiProdNo(asiProdNo.trim());
					}
					break;
			
				case 5:
					String productLevelSku = cell.getStringCellValue();
					if(!StringUtils.isEmpty(productLevelSku)){
					productExcelObj.setProductLevelSku(productLevelSku.trim());
					}else{
						productExcelObj.setProductLevelSku(ApplicationConstants.CONST_STRING_EMPTY);
					}
				    break;
					
				case 6:
					String inventoryLink = cell.getStringCellValue();
					if(!StringUtils.isEmpty(inventoryLink)){
					inventoryObj.setInventoryLink(inventoryLink.trim());
					productExcelObj.setInventory(inventoryObj);
					}
					break;
					
				case 7:
					String inventoryStatus = cell.getStringCellValue();
					if(!StringUtils.isEmpty(inventoryStatus)){
					inventoryObj.setInventoryStatus(inventoryStatus.trim());
					productExcelObj.setInventory(inventoryObj);
					}
					break;
					
				case 8:
					//int inventoryQuantity =0;
					String inventoryQuantity = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(inventoryQuantity)){
						inventoryObj.setInventoryQuantity(inventoryQuantity.trim());
						productExcelObj.setInventory(inventoryObj);
						}
					break;
					
				case 9:
					String description = cell.getStringCellValue();
					if(!StringUtils.isEmpty(description)){
					productExcelObj.setDescription(description.trim());
					}else{
						productExcelObj.setDescription(ApplicationConstants.CONST_STRING_EMPTY);
					}
					break;
					
				case 10:
					String summary = cell.getStringCellValue();
					if(!StringUtils.isEmpty(summary)){
					productExcelObj.setSummary(summary.trim());
					}else{
						productExcelObj.setSummary(ApplicationConstants.CONST_STRING_EMPTY);
					}
					break;
					
					
				case 11:
					String image = cell.getStringCellValue();
							if (!StringUtils.isEmpty(image)) {
								List<Image> listOfImages = bamProductParser
										.getProductImages(image);
								productExcelObj.setImages(listOfImages);
							}
					break;
				case 12:
					String catalogValue = cell.getStringCellValue();
					if(!StringUtils.isEmpty(catalogValue)){
						List<Catalog> catalogList = bamProductParser.getCatalogs(catalogValue);
						if(!CollectionUtils.isEmpty(catalogList)){
							productExcelObj.setCatalogs(catalogList);
						}
					}
					break;
				case 13:
					String category = cell.getStringCellValue();
							if (!StringUtils.isEmpty(category)) {
								List<String> listOfCategories = CommonUtility
										.getStringAsList(category);
								productExcelObj.setCategories(listOfCategories);
							}
					break;

				case 14:
					String productKeyword = cell.getStringCellValue();
					if(!StringUtils.isEmpty(productKeyword)){
						List<String> listOfKeywords = CommonUtility
								.getStringAsList(productKeyword);
					productExcelObj.setProductKeywords(listOfKeywords);
					}
					break;
					
				case 15:
					String colorValue=cell.getStringCellValue();
					if(!StringUtils.isEmpty(colorValue)){
						List<Color> colorList = bamProductParser.getColorValues(colorValue);
						if(!CollectionUtils.isEmpty(colorList)){
							productConfigObj.setColors(colorList);
						}
					}
					break;
					
				case 16:
					String materialValue=cell.getStringCellValue();
							if (!StringUtils.isEmpty(materialValue)) {
								List<Material> materialList = bamMaterialParser
										.getMaterialCriteria(materialValue
												.trim());
								productConfigObj.setMaterials(materialList);
							}
				case 17:
					 sizeGroup = cell.getStringCellValue();	
				break;
				
				case 18:
					
					String sizeValue = cell.getStringCellValue();
					Size sizeObj = bamSizeParser.getSizes(sizeGroup.trim(),sizeValue.trim());
					productConfigObj.setSizes(sizeObj);
					
					break;
				
				 case 19:
					String shapeValue=cell.getStringCellValue();
							if (!StringUtils.isEmpty(shapeValue)) {
								List<Shape> shapeList = bamProductParser.getShapeCriteria(shapeValue.trim());
								productConfigObj.setShapes(shapeList);
							}
					break;
					
				case 20:
					String themeValue=cell.getStringCellValue();
							if (!StringUtils.isEmpty(themeValue)) {
								List<Theme> themes = bamProductParser.getThemeCriteria(themeValue.trim());
								productConfigObj.setThemes(themes);
							}
					break;
					
				case 21:
					String tradeValue=cell.getStringCellValue();
							if (!StringUtils.isEmpty(tradeValue)) {
						List<TradeName> tradeName = bamProductParser.getTradeNames(tradeValue.trim());
								productConfigObj.setTradeNames(tradeName);
							}
					break;
					
				case 22:
					String originValue=cell.getStringCellValue();
							if (!StringUtils.isEmpty(originValue)) {
								List<Origin> origin = bamProductParser.getProductOrigins(originValue);
								productConfigObj.setOrigins(origin);
							}
					break;
					
				case 23:
					 optiontype=cell.getStringCellValue();
				   break;
					
				case 24:
					 optionname=cell.getStringCellValue();

					
				   break;
				   
				case 25:
					 optionvalues=cell.getStringCellValue();
					 List<String> listOfOptionValues = BamUtility.getOptions(optiontype);
					 if(!CollectionUtils.isEmpty(listOfOptionValues)){
						 listOfOptionValues.add(optionvalues);
					 }else{
						 listOfOptionValues = new ArrayList<>();
						 listOfOptionValues.add(optionvalues);
					 }
					 BamUtility.saveOptions(optiontype, listOfOptionValues);
					break;
					
				case 26:
					 canorder=cell.getStringCellValue();	
					
					
					break;
					
				case 27:
					 reqfororder=cell.getStringCellValue();	
					
					break;
					
				case 28:
					optionadditionalinfo=cell.getStringCellValue();	
					
					break;
					
				case 29:
					String imprintValue=cell.getStringCellValue();
							if (!StringUtils.isEmpty(imprintValue)) {
								List<ImprintMethod> imprintMethods = bamProductParser.getImprintMethodValues(imprintValue.trim());
								productConfigObj
										.setImprintMethods(imprintMethods);
							}
					break;
					
				case 30:
					String lineName = cell.getStringCellValue();
							if (!StringUtils.isEmpty(lineName)) {
								List<String> listOfLineNames = bamProductParser
										.getProductLines(lineName.trim(),asiNumber);
								productExcelObj.setLineNames(listOfLineNames);
								/*if(!CollectionUtils.isEmpty(listOfLineNames)){
									productExcelObj.setLineNames(listOfLineNames);
								}*/							
							}
					break;
					
				case 31:
					String artwork = cell.getStringCellValue();
							if (!StringUtils.isEmpty(artwork)) {
								List<Artwork> artworkList	= bamProductParser.getArtworkValues(artwork.trim());
								productConfigObj.setArtwork(artworkList);
							}
					break;
					
				case 32:
					String imprintColor = cell.getStringCellValue();
							if (!StringUtils.isEmpty(imprintColor)) {
								imprintColors = bamProductParser.getImprintColorValues(imprintColor.trim());
								productConfigObj.setImprintColors(imprintColors);
							}
					 
					break;
				case 33: //unimprinted
					String unImprinted = cell.getStringCellValue();
					
				case 34://
					String persnlization = cell.getStringCellValue();
							if (!StringUtils.isEmpty(persnlization)) {
								List<Personalization> personalizationlist = bamProductParser.getPersonalizationValues(persnlization.trim());
								productConfigObj
										.setPersonalization(personalizationlist);
							}
					break;
				case 35: // imprintSize
					String imprintSize = cell.getStringCellValue(); 
					if(!StringUtils.isEmpty(imprintSize)){
						 List<ImprintSize> listOfImprSize = bamProductParser.getImprintSize(imprintSize);
						 productConfigObj.setImprintSize(listOfImprSize);
					}
					break;
				case 36:// imprintLocation
					String imprintLocation = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintLocation)){
						 List<ImprintLocation> listOfImprLoc = bamProductParser.getImprintLocation(imprintLocation);
						 productConfigObj.setImprintLocation(listOfImprLoc);
					}
					break;
				case 37: // additionalColor
					String additionalColor = cell.getStringCellValue(); 
					if(!StringUtils.isEmpty(additionalColor)){
						 List<AdditionalColor> listOfAddColr = bamProductParser.getAdditionalColor(additionalColor);
						 productConfigObj.setAdditionalColors(listOfAddColr);
					}
					break;
				case 38: // additionalLocation
					String additionalLocation = cell.getStringCellValue(); 
					if(!StringUtils.isEmpty(additionalLocation)){
						 List<AdditionalLocation> listOfaddLoc = bamProductParser.getAdditionalLocation(additionalLocation);
						 productConfigObj.setAdditionalLocations(listOfaddLoc);
					}
					break;	
				case 39:
					prodSample = cell.getStringCellValue();
					break;
					
				case 40:
					String specSample  = cell.getStringCellValue();
					boolean flag=false;
					if(!StringUtils.isEmpty(specSample)){
						flag=true;
					}
					Samples	sampleObj = bamProductParser.getProductSampleValues(
									prodSample.trim(), specSample.trim(), flag);
					productConfigObj.setSamples(sampleObj);
					
					break;
					
				case 41:
					String productionTime = cell.getStringCellValue();
							if (!StringUtils.isEmpty(productionTime)) {
								List<ProductionTime> productionTimeList = bamProductParser
										.getProductionTimeValues(productionTime
												.trim());
								productConfigObj
										.setProductionTime(productionTimeList);
							}
					break;		
				case 42:
					 rushService = cell.getStringCellValue();
					break;
					
				case 43:
							if (!StringUtils.isEmpty(rushService)
									&& !rushService
											.equalsIgnoreCase(ApplicationConstants.CONST_CHAR_N)) {
								String rushTimeValue = cell
										.getStringCellValue();
								RushTime rushTime = bamProductParser.getRushTimeValues(rushTimeValue.trim());
								productConfigObj.setRushTime(rushTime);
							}
					break;
				
				case 44:
					String sameDayService=cell.getStringCellValue();
							if (!StringUtils.isEmpty(sameDayService)) {
								SameDayRush sdayObj = bamProductParser.getSameDaySeviceValues(sameDayService.trim());
								productConfigObj.setSameDayRush(sdayObj);
							}
					break;
					
				case 45:
					 packagingValue=cell.getStringCellValue();
					break;
				case 46:
					shippingitemValue = cell.getStringCellValue();
					break;
					
				case 47:
					shippingdimensionValue = cell.getStringCellValue();
                	break;
					
				case 48:
					 shippingWeightValue = cell.getStringCellValue();
					ShippingEstimate ShipingItem = bamShippingParser.getShippingEstimatesValues(
									shippingitemValue.trim(),
									shippingdimensionValue.trim(),
									shippingWeightValue.trim());
							productConfigObj.setShippingEstimates(ShipingItem);
					
					break;
					
				case 49:
					String shipperBillsBy = cell.getStringCellValue();
							if (!StringUtils.isEmpty(shipperBillsBy)) {
								productExcelObj
										.setShipperBillsBy(shipperBillsBy.trim());
							} else {
								productExcelObj
										.setShipperBillsBy(ApplicationConstants.CONST_STRING_EMPTY);
							}
					break;
					
				case 50:
					String additionalShippingInfo = cell.getStringCellValue();
							if (!StringUtils.isEmpty(additionalShippingInfo)) {
								productExcelObj
										.setAdditionalShippingInfo(additionalShippingInfo
												.trim());
							} else {
								productExcelObj
										.setAdditionalShippingInfo(ApplicationConstants.CONST_STRING_EMPTY);
							}
					break;
					
				case 51:
					String canShipInPlainBox = cell.getStringCellValue();
					if(!StringUtils.isEmpty(canShipInPlainBox)){
					if (canShipInPlainBox.trim().equalsIgnoreCase(ApplicationConstants.CONST_CHAR_Y)) {
						productExcelObj.setCanShipInPlainBox(true);
					} else {
						productExcelObj.setCanShipInPlainBox(false);
					}
					}else{ productExcelObj.setCanShipInPlainBox(false);
					}
					break;
					
				case 52:
					String complianceCert = cell.getStringCellValue();
							if (!StringUtils.isEmpty(complianceCert)) {
								complianceCert = complianceCert.trim();
								List<String> complianceCerts = CommonUtility
										.getStringAsList(complianceCert);
								productExcelObj
										.setComplianceCerts(complianceCerts);
							}
					break;
					
				case 53:
					String productDataSheet = cell.getStringCellValue();
							if (!StringUtils.isEmpty(productDataSheet)) {
								productExcelObj
										.setProductDataSheet(productDataSheet.trim());
							} else {
								productExcelObj
										.setProductDataSheet(ApplicationConstants.CONST_STRING_EMPTY);
							}
					break;
					
				case 54:
					String safetyWarning = cell.getStringCellValue();
							if (!StringUtils.isEmpty(safetyWarning)) {
								safetyWarning = safetyWarning.trim();
								List<String> safetyWarnings = CommonUtility.getStringAsList(safetyWarning);
								productExcelObj
										.setSafetyWarnings(safetyWarnings);
							}
					break;

				case 55:
					String additionalProductInfo = cell.getStringCellValue();
							if (!StringUtils.isEmpty(additionalProductInfo)) {
								productExcelObj
										.setAdditionalProductInfo(additionalProductInfo
												.trim());
							} else {
								productExcelObj
										.setAdditionalProductInfo(ApplicationConstants.CONST_STRING_EMPTY);
							}
					break;

				case 56:
					String distributorOnlyComments = cell.getStringCellValue();
							if (!StringUtils.isEmpty(distributorOnlyComments)) {
								productExcelObj
										.setDistributorOnlyComments(distributorOnlyComments
												.trim());
							} else {
								productExcelObj
										.setDistributorOnlyComments(ApplicationConstants.CONST_STRING_EMPTY);
							}
					break;

				case 57:
					String productDisclaimer = cell.getStringCellValue();
							if (!StringUtils.isEmpty(productDisclaimer)) {
								productExcelObj
										.setProductDisclaimer(productDisclaimer
												.trim());
							} else {
								productExcelObj
										.setProductDisclaimer(ApplicationConstants.CONST_STRING_EMPTY);
							}
					break;
					
				case 58:
					basePriceName = cell.getStringCellValue();
					break;
					
				case 59:
					String criteria1 = cell.getStringCellValue();
					if(!StringUtils.isEmpty(criteria1)){
						basePriceCriteria.append(criteria1.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					}
					break;
			
				case 60:
					String criteria2 = cell.getStringCellValue();
					if(!StringUtils.isEmpty(criteria2)){
						basePriceCriteria.append(criteria2.trim());
					}
				case 61:
				case 62:
				case 63:
				case 64:
				case 65:
				case 66:
				case 67:
				case 68:
				case 69:
				case 70:
					quantity = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(quantity)){
						listOfQuantity.add(quantity.trim());
					}
			          break;
				case 71:
				case 72:
				case 73:
				case 74:
				case 75:
				case 76:
				case 77:
				case 78:
				case 79:
				case 80: 
					quantity = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(quantity)){
						listOfPrices.add(quantity.trim());
					}
				  break;
				case 81:
				case 82:
				case 83:
				case 84:
				case 85:
				case 86:
				case 87:
				case 88:
				case 89:
				case 90:	
					quantity = cell.getStringCellValue();
			         if(!StringUtils.isEmpty(quantity)){
			        	 listOfDiscount.add(quantity.trim());
			         }
					  break;
				case 91: //Product numberPrice
					break;
				case 92:
					   priceIncludes = cell.getStringCellValue();
					   break;
				case 93:
					 priceQurFlag = cell.getStringCellValue();
					priceQurFlag = checkPriceQur(priceQurFlag);
					 break;
				case 94:
					 currencyType = cell.getStringCellValue();
					 break;
				case 95:
				  String lessThan = cell.getStringCellValue();
				  boolean canLess = isLessthanMinimum(lessThan);
				  productExcelObj.setCanOrderLessThanMinimum(canLess);
					break;

				case 96:
					 priceType = cell.getStringCellValue();
					 priceType = getPriceType(priceType);
					 productExcelObj
						.setPriceType(priceType);
					break;
				case 97:
					upChargeName = cell.getStringCellValue();
					break;//upcharge name
				
				case 98:
					String upCriteria1= cell.getStringCellValue();
					if(!StringUtils.isEmpty(upCriteria1)){
						UpCharCriteria.append(upCriteria1.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					}
					break;//upcharge criteria_1
				
				case 99:
					String upCriteria2= cell.getStringCellValue();
					if(!StringUtils.isEmpty(upCriteria2)){
						UpCharCriteria.append(upCriteria2.trim());
					}
					break;//upcharge criteria_2
				
				case 100:
					upchargeType = cell.getStringCellValue();
					break;//upcharge type
				
				case 101:
					upChargeLevel = cell.getStringCellValue();
					break;//upcharge level
				case 102: //service charge
					serviceCharge = cell.getStringCellValue();
					break;
				case 103:
				case 104:
				case 105:
				case 106:
				case 107:
				case 108:
				case 109:
				case 110:
				case 111:
				case 112:
					quantity = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(quantity)){
						UpCharQuantity.add(quantity.trim());
					}
					 break; // upcharge quanytity
					
				case 113:
				case 114:
				case 115:
				case 116:
				case 117:
				case 118:
				case 119:
				case 120:
				case 121:
				case 122:
					quantity = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(quantity)){
						UpCharPrices.add(quantity.trim());
					}
					 break; // upcharge prices
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
					quantity = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(quantity)){
						UpCharDiscount.add(quantity.trim());
					}
					 break; // upcharge discount
				case 133:
					upChargeDetails = cell.getStringCellValue();
					break;// upcharge details
				case 134:
					    upChargeQur = cell.getStringCellValue();
					    upChargeQur = checkPriceQur(upChargeQur);
					break;// QUR Flag
				case 135:
					String priceConfirmedThru = cell.getStringCellValue();
					//mmddyy in excel  //yymmdd in api
					String strArr[]=priceConfirmedThru.split("/");
					priceConfirmedThru=strArr[2]+"/"+strArr[0]+"/"+strArr[1];
					priceConfirmedThru=priceConfirmedThru.replaceAll("/", "-");
					 
					priceConfirmedThru=priceConfirmedThru+"T00:00:00";
					 	productExcelObj.setPriceConfirmedThru(priceConfirmedThru);
					break;
					
				case 136:
					  productNumberCriteria1 = cell.getStringCellValue();
				
					break;
				case 137:
					  productNumberCriteria2 = cell.getStringCellValue();
					
					break;
				case 138:
					productNumber = cell.getStringCellValue();
					break;
				case 139:
				   SKUCriteria1 = cell.getStringCellValue();
					break;
					
				case 140:
				   SKUCriteria2 = cell.getStringCellValue();
					break;
				case 141:
					   
						break;
				case 142:
					   
						break;
				case 143: // sku
					skuvalue = cell.getStringCellValue();
						break;
				case 144:
					Inlink = cell.getStringCellValue();
					break;
					
				case 145:
					 Instatus = cell.getStringCellValue();
					break;
				case 146: // inventory quan
					String InQuantity1 = CommonUtility.getCellValueStrinOrInt(cell);
							skuObj = bamProductParser.getProductRelationSkus(
									SKUCriteria1, SKUCriteria2, skuvalue,
									Inlink, Instatus, InQuantity1);
					break;
					
				
				case 147: // distributorViewOnly
					String distributorViewOnly = cell.getStringCellValue();
							if (!StringUtils.isEmpty(distributorViewOnly)
									&& distributorViewOnly
											.trim()
											.equalsIgnoreCase(
													ApplicationConstants.CONST_CHAR_Y)) {
								productExcelObj.setSeoFlag(true);

							} else {
								productExcelObj.setSeoFlag(false);
							}
					break;	
				case 148: // operation
					break;
					
				case 149: // carrier info
					String seoFlag = cell.getStringCellValue();
							if (!StringUtils.isEmpty(seoFlag)
									&& seoFlag.trim().equalsIgnoreCase(
											ApplicationConstants.CONST_CHAR_Y)) {
								productExcelObj.setSeoFlag(true);
							} else {
								productExcelObj.setSeoFlag(false);
							}
					break;
				case 150: 
					break;
				case 151: 
					break;
				case 152: 
					break;
				case 153: 
					break;
				case 154: 
					break;
				case 155: 
					break;
				case 156: 
					break;
				case 157: 
					break;
				case 158: 
					break;
				case 159: 
					break;
				case 160: 
					break;
				case 161: 
					break;
				case 162: 
					break;
				}
				
				//productExcelObj.setProductConfigurations(productConfigObj);l
			}  // end inner while loop
			if(!StringUtils.isEmpty(packagingValue)){
				boolean shippingProce = isProcessPackageShipping(shippingWeightValue, 
						                                         shippingdimensionValue, shippingitemValue);
				productConfigObj = bamPackagingParser.getPackagingAndShipping(packagingValue.trim(),
		                 												productConfigObj,shippingProce);
			}
			if(( (listOfPrices != null && !listOfPrices.toString().isEmpty()) || (priceQurFlag != null && priceQurFlag.equalsIgnoreCase("Y")))){
				priceQurFlag = checkPriceQur(priceQurFlag);
				priceGrids = bamPriceGridParser.getBasePriceGrids(
								listOfPrices.toString(),
								listOfQuantity.toString(),
								listOfDiscount.toString(), currencyType,
								priceIncludes, true, priceQurFlag,
								basePriceName, basePriceCriteria.toString(),
								priceGrids);
			}
				if(UpCharCriteria != null && !UpCharCriteria.toString().isEmpty()){
					upChargeQur = checkPriceQur(upChargeQur);
					priceGrids =	bamPriceGridParser.getUpchargePriceGrid(
								UpCharQuantity.toString(),
								UpCharPrices.toString(),
								UpCharDiscount.toString(),
								UpCharCriteria.toString(), upChargeQur,
								currencyType, upChargeName, upchargeType,
								upChargeLevel, new Integer(1), serviceCharge,priceGrids);
				}
				
				if(!StringUtils.isEmpty(skuvalue)){
						skuObj = bamProductParser.getProductRelationSkus(
								SKUCriteria1, SKUCriteria2, skuvalue, Inlink,
								Instatus, InQuantity);
					productsku.add(skuObj);
				}
				
				if(!StringUtils.isEmpty(productNumber)){
						pnumObj = bamProductParser.getProductNumbers(
								productNumberCriteria1, productNumberCriteria2,
								productNumber);
					if(pnumObj!=null){
					pnumberList.add(pnumObj);
					}
				}
				
					if (!StringUtils.isEmpty(optionname)
							&& !StringUtils.isEmpty(optiontype)
							&& !StringUtils.isEmpty(optionvalues)) {
						listOfOptions = bamProductParser.getOptions(optiontype.trim(),
								optionname.trim(), optionvalues.trim(), canorder,
								reqfororder, optionadditionalinfo,listOfOptions);
					}
				
				upChargeQur = null;
				UpCharCriteria = new StringBuilder();
				priceQurFlag = null;
				listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				UpCharPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				UpCharDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				UpCharQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
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
			    basePriceCriteria = new StringBuilder();
			
			}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);		 
	 			ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: "
	 			+e.getMessage()+" at column number(increament by 1)"+columnIndex);
	 			productDaoObj.save(apiResponse.getErrors(),
						productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);		 
		}
		}
		workbook.close();
		   // Add repeatable sets here
		productConfigObj.setOptions(listOfOptions);
		 	productExcelObj.setPriceGrids(priceGrids);
		 	productExcelObj.setProductConfigurations(productConfigObj);
		 	productExcelObj.setProductRelationSkus(productsku);
		 	productExcelObj.setProductNumbers(pnumberList);
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
			 productDaoObj.saveErrorLog(asiNumber,batchId);
			 return finalResult;
		}catch(Exception e){
			_LOGGER.error("Error while Processing excel sheet ,Error: "+e.getMessage());
			return finalResult;
		}finally{
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet ,Error: "+e.getMessage());
	
			}
			_LOGGER.info("Complted processing of excel sheet ");
			_LOGGER.info("Total no of product:"+numOfProducts.size() );
		}
	
	}
	
    public boolean isRepeateColumn(int columnIndex){
		
    	if((columnIndex >=24 && columnIndex <=29) || columnIndex == 42 || columnIndex == 59 || 
    			columnIndex == 60 || (columnIndex >=62 && columnIndex <=95) || 
    			(columnIndex >=97 && columnIndex <=135)){
    		return ApplicationConstants.CONST_BOOLEAN_FALSE;
    	}
    	return ApplicationConstants.CONST_BOOLEAN_TRUE;
	}
    
    private String checkPriceQur(String qurValue){
    	if(qurValue == null){
    		qurValue = ApplicationConstants.CONST_CHAR_N;
    	}
    	return qurValue;
    }
    
    private String getPriceType(String priceType){
    	if (!StringUtils.isEmpty(priceType)) {
			priceType = priceType.trim();
			if (priceType.equalsIgnoreCase("List")) {
				return ApplicationConstants.CONST_PRICE_TYPE_CODE_LIST;
			} else if (priceType.equalsIgnoreCase("Net")) {
				return ApplicationConstants.CONST_PRICE_TYPE_CODE_NET;
			}
		} 
    	return ApplicationConstants.CONST_PRICE_TYPE_CODE_LIST;
    }
    private boolean isLessthanMinimum(String value){
    	if(!StringUtils.isEmpty(value)){
    		value = value.trim();
    		 if(ApplicationConstants.CONST_CHAR_Y.equalsIgnoreCase(value)){
    			 return ApplicationConstants.CONST_BOOLEAN_TRUE;
			  }else{
				  return ApplicationConstants.CONST_BOOLEAN_FALSE;
			  }
    	}else {
    		return ApplicationConstants.CONST_BOOLEAN_FALSE;
    	}
    }
    
    private boolean isProcessPackageShipping(String shiWt,String Shidime,String shiItems){ // 11 33 null   // null null 99
    	if(StringUtils.isEmpty(shiWt) || StringUtils.isEmpty(Shidime) || StringUtils.isEmpty(shiItems)){
    		return ApplicationConstants.CONST_BOOLEAN_FALSE;
    	}
    	return ApplicationConstants.CONST_BOOLEAN_TRUE;
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
	public BamProductAttributeParser getBamProductParser() {
		return bamProductParser;
	}

	public void setBamProductParser(BamProductAttributeParser bamProductParser) {
		this.bamProductParser = bamProductParser;
	}

	public BamMaterialParser getBamMaterialParser() {
		return bamMaterialParser;
	}
	public void setBamMaterialParser(BamMaterialParser bamMaterialParser) {
		this.bamMaterialParser = bamMaterialParser;
	}

	public BamSizeParser getBamSizeParser() {
		return bamSizeParser;
	}
	public void setBamSizeParser(BamSizeParser bamSizeParser) {
		this.bamSizeParser = bamSizeParser;
	}

	public BamShippingEstimationParser getBamShippingParser() {
		return bamShippingParser;
	}

	public void setBamShippingParser(BamShippingEstimationParser bamShippingParser) {
		this.bamShippingParser = bamShippingParser;
	}
	public BamPriceGridParser getBamPriceGridParser() {
		return bamPriceGridParser;
	}

	public void setBamPriceGridParser(BamPriceGridParser bamPriceGridParser) {
		this.bamPriceGridParser = bamPriceGridParser;
	}
	public BamPackagingParser getBamPackagingParser() {
		return bamPackagingParser;
	}

	public void setBamPackagingParser(BamPackagingParser bamPackagingParser) {
		this.bamPackagingParser = bamPackagingParser;
	}

}
