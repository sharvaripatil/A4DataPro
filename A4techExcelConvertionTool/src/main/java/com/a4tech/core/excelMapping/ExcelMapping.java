package com.a4tech.core.excelMapping;

import java.io.FileInputStream;
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

import com.a4tech.product.criteria.parser.CatalogParser;
import com.a4tech.product.criteria.parser.PersonlizationParser;
import com.a4tech.product.criteria.parser.PriceGridParser;
import com.a4tech.product.criteria.parser.ProductArtworkProcessor;
import com.a4tech.product.criteria.parser.ProductColorParser;
import com.a4tech.product.criteria.parser.ProductImprintColorParser;
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
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Artwork;
import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintColor;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Inventory;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Option;
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
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.service.loginImpl.LoginServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExcelMapping {
	
	private static final Logger _LOGGER = Logger.getLogger(ExcelMapping.class);
	PostServiceImpl postServiceImpl ;
	ProductDao productDaoObj;
	@SuppressWarnings("finally")
	public int readExcel(String accessToken,Workbook workbook,int asiNumber,int batchId){
		ImprintColor imprintColors = new ImprintColor();
		List<String> numOfProducts = new ArrayList<String>();
		FileInputStream inputStream = null;
		LoginServiceImpl loginService = new LoginServiceImpl();
		Set<String>  listOfXids = new HashSet<String>();
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
			List<Packaging> packaging = new ArrayList<Packaging>();
			List<String> themes = new ArrayList<String>();
			List<String> tradeName = new ArrayList<String>();
			List<ImprintMethod> imprintMethods = new ArrayList<ImprintMethod>();
			List<Artwork> artworkList = new ArrayList<Artwork>();
			List<Shape> shapeList=new ArrayList<Shape>();
			List<ProductionTime> productionTimeList = new ArrayList<ProductionTime>();
			List<Material> materialList=new ArrayList<Material>();
			
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
			
			if(externalProductId != null){
				listOfXids.add(externalProductId);
			}
			 boolean checkXid  = false;
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				String xid = null;
				int columnIndex = cell.getColumnIndex();
				if(columnIndex + 1 == 1){
					 xid = cell.getStringCellValue();
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!listOfXids.contains(xid)){
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
							 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId);
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
						    if(!listOfXids.contains(xid)){
						    	listOfXids.add(xid);
						    }
							productExcelObj = new Product();
					 }
				}
				switch (columnIndex + 1) {
				case 1:
					 externalProductId = cell.getStringCellValue();
					productExcelObj.setExternalProductId(externalProductId);
					break;
					
				case 2:
					String name = cell.getStringCellValue();
					
					if(!StringUtils.isEmpty(name)){
					productExcelObj.setName(cell.getStringCellValue());
					}else{
						productExcelObj.setName(ApplicationConstants.CONST_STRING_EMPTY);
					}
					break;
		
				case 3:
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
			
				case 4:
					String productLevelSku = cell.getStringCellValue();
					if(!StringUtils.isEmpty(productLevelSku)){
					productExcelObj.setProductLevelSku(productLevelSku);
					}else{
						productExcelObj.setProductLevelSku(ApplicationConstants.CONST_STRING_EMPTY);
					}
				    break;
					
				case 5:
					String inventoryLink = cell.getStringCellValue();
					if(!StringUtils.isEmpty(inventoryLink)){
					inventoryObj.setInventoryLink(inventoryLink);
					productExcelObj.setInventory(inventoryObj);
					}
					break;
					
				case 6:
					String inventoryStatus = cell.getStringCellValue();
					if(!StringUtils.isEmpty(inventoryStatus)){
					inventoryObj.setInventoryStatus(inventoryStatus);
					productExcelObj.setInventory(inventoryObj);
					}
					break;
					
				case 7:
					int inventoryQuantity =0;
					if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						inventoryQuantity = (int) cell.getNumericCellValue();
					}else if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						try{
							inventoryQuantity = Integer.parseInt(cell.getStringCellValue());
						}catch(NumberFormatException nfe){
							_LOGGER.error("Product Level Inventory Quantity::Invalid value");
						}
						
					}
					if(!StringUtils.isEmpty(Integer.toString(inventoryQuantity))){
					inventoryObj.setInventoryQuantity(Integer.toString(inventoryQuantity));
					productExcelObj.setInventory(inventoryObj);
					}
					break;
					
				case 8:
					String description = cell.getStringCellValue();
					if(!StringUtils.isEmpty(description)){
					productExcelObj.setDescription(description);
					}else{
						productExcelObj.setDescription(ApplicationConstants.CONST_STRING_EMPTY);
					}
					break;
					
				case 9:
					String summary = cell.getStringCellValue();
					if(!StringUtils.isEmpty(summary)){
					productExcelObj.setSummary(summary);
					}else{
						productExcelObj.setSummary(ApplicationConstants.CONST_STRING_EMPTY);
					}
					break;
					
					
				case 10:

					
					String image = cell.getStringCellValue();
					if(!StringUtils.isEmpty(image)){
					String imgArr[] = image.split(",");
					for(int i =0;i<=imgArr.length-1;i++)
					{
						if(imgArr[0] != null){
					imgObj.setImageURL(imgArr[0]);
					imgObj.setRank(i++);
					imgObj.setIsPrimary(true);
						}
						else if (imgArr[1] != null){
						 imgObj.setImageURL(imgArr[1]);
						 imgObj.setRank(i++);
						 imgObj.setIsPrimary(false);
					}
						imgList.add(imgObj);
					}
					}else{
						productExcelObj.setImages(imgList);					
				}
					
					break;
					
				case 11:
					String catalogValue = cell.getStringCellValue();
					if(!StringUtils.isEmpty(catalogValue)){
					catalogList=catlogparser.getCatalogs(catalogValue);
					}
					else{
						productExcelObj.setCatalogs(catalogList);				
				}
					
					break;
					
					
				
				case 12:
					String category = cell.getStringCellValue();
					if(!StringUtils.isEmpty(category)){
					String categoryArr[] = category.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					for (String string : categoryArr) {
						categories.add(string);
					}
					productExcelObj.setCategories(categories);
					}
					break;

				case 13:
					String productKeyword = cell.getStringCellValue();
					if(!StringUtils.isEmpty(productKeyword)){
					String productKeywordArr[] = productKeyword.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					for (String string : productKeywordArr) {
						productKeywords.add(string);
					}
					productExcelObj.setProductKeywords(productKeywords);
					}
					break;
					
				case 14:
					String colorValue=cell.getStringCellValue();
					if(!StringUtils.isEmpty(colorValue)){
					color=colorparser.getColorCriteria(colorValue);
					productConfigObj.setColors(color);
					
					}
					break;
					
				case 15:
					String materialValue=cell.getStringCellValue();
					if(!StringUtils.isEmpty(materialValue)){
					materialList= materialParser.getMaterialCriteria(materialValue);
					
					productConfigObj.setMaterials(materialList);
					
					}
					
				case 16:
					 sizeGroup = cell.getStringCellValue();	
				break;
				
				case 17:
					
					String sizeValue = cell.getStringCellValue();
					sizeObj=sizeParser.getSizes(sizeGroup,sizeValue);
					
					productConfigObj.setSizes(sizeObj);
					
					break;
				
				 case 18:
					String shapeValue=cell.getStringCellValue();
					if(!StringUtils.isEmpty(shapeValue)){
					shapeList=shapeParser.getShapeCriteria(shapeValue);
					
					productConfigObj.setShapes(shapeList);
					
					}
					break;
					
				case 19:
					String themeValue=cell.getStringCellValue();
					if(!StringUtils.isEmpty(themeValue)){
					themes=themeParser.getThemeCriteria(themeValue);
					
					//productConfigObj.setThemes(themes);
					
					}
					break;
					
				case 20:
					String tradeValue=cell.getStringCellValue();
					if(!StringUtils.isEmpty(tradeValue)){
					//tradeName=tradeNameParser.getTradeNameCriteria(tradeValue);
					
					//productConfigObj.setTradeNames(tradeName);
					
					}
					//System.out.println(columnIndex + "tradeName " + tradeName);
					break;
					
				case 21:
					String originValue=cell.getStringCellValue();
					if(!StringUtils.isEmpty(originValue)){
					//origin=originParser.getOriginCriteria(originValue);
					
					//productConfigObj.setOrigins(origin);
					
					}
					break;
					
				case 22:
					 optiontype=cell.getStringCellValue();
				   break;
					
				case 23:
					 optionname=cell.getStringCellValue();

					
				   break;
				   
				case 24:
					 optionvalues=cell.getStringCellValue();					
					break;
					
				case 25:
					 canorder=cell.getStringCellValue();	
					
					
					break;
					
				case 26:
					 reqfororder=cell.getStringCellValue();	
					
					break;
					
				case 27:
					optionadditionalinfo=cell.getStringCellValue();	
					
					break;
					
				case 28:
					String imprintValue=cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintValue)){
					imprintMethods=imprintMethodParser.getImprintCriteria(imprintValue);
					
					productConfigObj.setImprintMethods(imprintMethods);
					
					}
					break;
					
				case 29:
					String lineName = cell.getStringCellValue();
					if(!StringUtils.isEmpty(lineName)){
					String lineNameArr[] = lineName.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					for (String string : lineNameArr) {
						lineNames.add(string);
					}
					productExcelObj.setLineNames(lineNames);
					}
					break;
					
				case 30:
					String artwork = cell.getStringCellValue();
					if(!StringUtils.isEmpty(artwork)){
					artworkList=artworkProcessor.getArtworkCriteria(artwork);
					
					productConfigObj.setArtwork(artworkList);
					}
					break;
					
				case 31:
					String imprintColor = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintColor)){
					imprintColors=imprintColorParser.getImprintColorCriteria(imprintColor);
					
					productConfigObj.setImprintColors(imprintColors);
					//System.out.println(columnIndex + "imprintColors " + imprintColors);
					}
					 
					break;
					
				case 33:
					String persnlization = cell.getStringCellValue();
					if(!StringUtils.isEmpty(persnlization)){
					personalizationlist = personalizationParser.getPersonalization(persnlization);
					productConfigObj.setPersonalization(personalizationlist);
					}
					break;
					
					
				case 38:
					prodSample = cell.getStringCellValue();
					break;
					
				case 39:
					String specSample  = cell.getStringCellValue();
					boolean flag=false;
					if(!StringUtils.isEmpty(specSample)){
						flag=true;
					}
					samples=sampleParser.getSampleCriteria(prodSample, specSample,flag);
					
					productConfigObj.setSamples(samples);
					
					break;
					
				case 40:
					String productionTime = cell.getStringCellValue();
					if(!StringUtils.isEmpty(productionTime)){
					productionTimeList=productionTimeParser.getProdTimeCriteria(productionTime);
					
					productConfigObj.setProductionTime(productionTimeList);
					
				}
					break;	
					
					
				case 41:
					 rushService = cell.getStringCellValue();
					break;
					
				case 42:
					if(!StringUtils.isEmpty(rushService) && !rushService.equalsIgnoreCase(ApplicationConstants.CONST_CHAR_N)){
					String rushTimeValue = cell.getStringCellValue();
					rushTime=rushTimeParser.getRushTimeCriteria(rushTimeValue);
					
					productConfigObj.setRushTime(rushTime);
					}
					break;
				
				case 43:
					String sameDayService=cell.getStringCellValue();
					if(!StringUtils.isEmpty(sameDayService)){
						sameDayObj=sameDayParser.getSameDayRush(sameDayService);
						productConfigObj.setSameDayRush(sameDayObj);
						}
					break;
					
				case 44:
					String packagingValue=cell.getStringCellValue();
					if(!StringUtils.isEmpty(packagingValue)){
						  packaging=packagingParser.getPackagingCriteria(packagingValue);
							 productConfigObj.setPackaging(packaging);
					     
					}
					break;
				case 45:
					shippingitemValue = cell.getStringCellValue();
					break;
					
				case 46:
					shippingdimensionValue = cell.getStringCellValue();
                	break;
					
				case 47:
					String shippingWeightValue = cell.getStringCellValue();
					ShipingItem = shipinestmt.getShippingEstimates(shippingitemValue, shippingdimensionValue,shippingWeightValue);
					productConfigObj.setShippingEstimates(ShipingItem);
					
					break;
					
				case 48:
					String shipperBillsBy = cell.getStringCellValue();
					if(!StringUtils.isEmpty(shipperBillsBy.trim())){
					productExcelObj.setShipperBillsBy(cell.getStringCellValue());
					}else{
						productExcelObj.setShipperBillsBy(ApplicationConstants.CONST_STRING_EMPTY);
					}
					break;
					
				case 49:
					String additionalShippingInfo = cell.getStringCellValue();
					if(!StringUtils.isEmpty(additionalShippingInfo)){
					productExcelObj.setAdditionalShippingInfo(additionalShippingInfo);
					}else{
						productExcelObj.setAdditionalShippingInfo(ApplicationConstants.CONST_STRING_EMPTY);	
					}
					break;
					
				case 50:
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
					
				case 51:
					String complianceCert = cell.getStringCellValue();
					if(!StringUtils.isEmpty(complianceCert)){
					String complianceCertArr[] = complianceCert.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					for (String string : complianceCertArr) {
						complianceCerts.add(string);
					} 
					productExcelObj.setComplianceCerts(complianceCerts);
					}
					break;
					
				case 52:
					String productDataSheet = cell.getStringCellValue();
					if(!StringUtils.isEmpty(productDataSheet)){
					productExcelObj.setProductDataSheet(productDataSheet);
					}else{
						productExcelObj.setProductDataSheet(ApplicationConstants.CONST_STRING_EMPTY);
					}
					break;
					
				case 53:
					String safetyWarning = cell.getStringCellValue();
					if(!StringUtils.isEmpty(safetyWarning)){
					String safetyWarningsArr[] = safetyWarning.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					for (String string : safetyWarningsArr) {
						safetyWarnings.add(string.trim());
					} 
					productExcelObj.setSafetyWarnings(safetyWarnings);
					}
					break;

				case 54:
					String additionalProductInfo = cell.getStringCellValue();
					if(!StringUtils.isEmpty(additionalProductInfo)){
					productExcelObj.setAdditionalProductInfo(additionalProductInfo);
					}else{
						productExcelObj.setAdditionalProductInfo(ApplicationConstants.CONST_STRING_EMPTY);
					}
					break;

				case 55:
					String distributorOnlyComments = cell.getStringCellValue();
					if(!StringUtils.isEmpty(distributorOnlyComments)){
					productExcelObj.setDistributorOnlyComments(distributorOnlyComments);
					}
					else{
						productExcelObj.setDistributorOnlyComments(ApplicationConstants.CONST_STRING_EMPTY);
					}
					break;

				case 56:
					String productDisclaimer = cell.getStringCellValue();
					if(!StringUtils.isEmpty(productDisclaimer)){
					productExcelObj.setProductDisclaimer(productDisclaimer);
					}
					else{
						productExcelObj.setProductDisclaimer(ApplicationConstants.CONST_STRING_EMPTY);
					} 
					break;
					
				case 57:
					basePriceName = cell.getStringCellValue();
					break;
					
				case 58:
					String criteria1 = cell.getStringCellValue();
					if(!StringUtils.isEmpty(criteria1)){
						basePriceCriteria.append(criteria1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					}
					break;
			
				case 59:
					String criteria2 = cell.getStringCellValue();
					if(!StringUtils.isEmpty(criteria2)){
						basePriceCriteria.append(criteria2);
					}
				case 60:
				case 61:
				case 62:
				case 63:
				case 64:
				case 65:
				case 66:
				case 67:
				case 68:
				case 69:
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
				case 70:
				case 71:
				case 72:
				case 73:
				case 74:
				case 75:
				case 76:
				case 77:
				case 78:
				case 79:       
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
				case 80:
				case 81:
				case 82:
				case 83:
				case 84:
				case 85:
				case 86:
				case 87:
				case 88:
				case 89:	
					quantity = cell.getStringCellValue();
			         if(!StringUtils.isEmpty(quantity)){
			        	 listOfDiscount.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			         }
					  break;
				case 91:
					   priceIncludes = cell.getStringCellValue();
					   break;
				case 92:
					 priceQurFlag = cell.getStringCellValue();
					 break;
				case 93:
					 currencyType = cell.getStringCellValue();
					 break;
				case 94:
					if(cell.getCellType() ==  Cell.CELL_TYPE_BOOLEAN){
						if(!StringUtils.isEmpty(String.valueOf(cell.getBooleanCellValue()))){
							productExcelObj.setCanOrderLessThanMinimum(cell.getBooleanCellValue());
							}else
							{
								productExcelObj.setCanOrderLessThanMinimum(false);
							} 
					}else{
						productExcelObj.setCanOrderLessThanMinimum(false);
					}
					break;

				case 95:
					 priceType = cell.getStringCellValue();
					if(!StringUtils.isEmpty(priceType)){
						if(priceType.equalsIgnoreCase("List")){
					productExcelObj.setPriceType(ApplicationConstants.CONST_PRICE_TYPE_CODE_LIST);
						}else if(priceType.equalsIgnoreCase("Net")){
							productExcelObj.setPriceType(ApplicationConstants.CONST_PRICE_TYPE_CODE_NET);
						}
					}else{
						productExcelObj.setPriceType(ApplicationConstants.CONST_STRING_EMPTY);
					}
					break;

				case 96:
					boolean breakOutByPrice = cell.getBooleanCellValue();
					if(!StringUtils.isEmpty(String.valueOf(breakOutByPrice))){
					productExcelObj.setBreakOutByPrice(breakOutByPrice);
					}else{
						productExcelObj.setBreakOutByPrice(false);	
					}
					break;
				
				case 97:
					upChargeName = cell.getStringCellValue();
					break;//upcharge name
				
				case 98:
					String upCriteria1= cell.getStringCellValue();
					if(!StringUtils.isEmpty(upCriteria1)){
						UpCharCriteria.append(upCriteria1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					}
					break;//upcharge criteria_1
				
				case 99:
					String upCriteria2= cell.getStringCellValue();
					if(!StringUtils.isEmpty(upCriteria2)){
						UpCharCriteria.append(upCriteria2);
					}
					break;//upcharge criteria_2
				
				case 100:
					upchargeType = cell.getStringCellValue();
					break;//upcharge type
				
				case 101:
					upChargeLevel = cell.getStringCellValue();
					break;//upcharge level
					
				case 102:
				case 103:
				case 104:
				case 105:
				case 106:
				case 107:
				case 108:
				case 109:
				case 110:
				case 111:
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
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
						
					}
					 break; // upcharge quanytity
					
				case 112:
				case 113:
				case 114:
				case 115:
				case 116:
				case 117:
				case 118:
				case 119:
				case 120:
				case 121:
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						quantity = cell.getStringCellValue();
				         if(!StringUtils.isEmpty(quantity)){
				        	 UpCharPrices.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						int quantity1 = (int)cell.getNumericCellValue();
				         if(!StringUtils.isEmpty(quantity1)){
				        	 UpCharPrices.append(quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}else{
						
					}
					 break; // upcharge prices
				case 122:
				case 123:
				case 124:
				case 125:
				case 126:
				case 127:
				case 128:
				case 129:
				case 130:
				case 131:
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
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
						
					}
					 break; // upcharge discount
				case 132:
					upChargeDetails = cell.getStringCellValue();
					break;// upcharge details
				case 133:
					    upChargeQur = cell.getStringCellValue();
					break;// QUR Flag
				case 134:
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
				case 140:
				   SKUCriteria1 = cell.getStringCellValue();
					break;
					
				case 141:
				   SKUCriteria2 = cell.getStringCellValue();
					break;
					
				case 144:
					skuvalue = cell.getStringCellValue();
					break;
					
				case 145:
					Inlink = cell.getStringCellValue();
					break;
					
					
				case 146:
				    Instatus = cell.getStringCellValue();
					break;
					
				
				case 147:
					//int InQuantity= cell.getStringCellValue();
					int InQuantity1 = 0;
					if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						InQuantity1 = (int) cell.getNumericCellValue();
					}else if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						try{
							InQuantity1 = Integer.parseInt(cell.getStringCellValue());
						}catch(NumberFormatException nfe){
							_LOGGER.error("Criteria Level Inventory Quantity::Invalid value");
						}
						
					}
					skuObj=skuparserobj.getProductRelationSkus(SKUCriteria1, SKUCriteria2, skuvalue, Inlink, Instatus,Integer.toString(InQuantity1));
					
					break;	
				case 148:
					String distributorViewOnly = cell.getStringCellValue();
					if(!StringUtils.isEmpty(distributorViewOnly) && distributorViewOnly.trim().equalsIgnoreCase(ApplicationConstants.CONST_CHAR_Y)) {
						productExcelObj.setSeoFlag(true);
						
					} else {
						productExcelObj.setSeoFlag(false);
					}
					break;
					
				case 149:
					String seoFlag = cell.getStringCellValue();
					if(!StringUtils.isEmpty(seoFlag) && seoFlag.trim().equalsIgnoreCase(ApplicationConstants.CONST_CHAR_Y)) {
						productExcelObj.setSeoFlag(true);
					} else {
						productExcelObj.setSeoFlag(false);
					}
					break;
				}
				
				//productExcelObj.setProductConfigurations(productConfigObj);l
			}  // end inner while loop
			if(( (listOfPrices != null && !listOfPrices.toString().isEmpty()) || (priceQurFlag != null && priceQurFlag.equalsIgnoreCase("Y")))){
				priceGrids = priceGridParser.getPriceGrids(listOfPrices.toString(), listOfQuantity.toString(), listOfDiscount.toString(), currencyType,
						priceIncludes, true, priceQurFlag, basePriceName,basePriceCriteria.toString(),priceGrids);	
			}
				if(UpCharCriteria != null && !UpCharCriteria.toString().isEmpty()){
					priceGrids = priceGridParser.getUpchargePriceGrid(UpCharQuantity.toString(), UpCharPrices.toString(), UpCharDiscount.toString(), UpCharCriteria.toString(), 
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
			_LOGGER.error("Error while Processing Product :"+productExcelObj.getExternalProductId() +" Error: "+e.getMessage() );		 
		}
		}
		workbook.close();
		ObjectMapper mapper = new ObjectMapper();
		 ObjectMapper mapper1 = new ObjectMapper();
		   // Add repeatable sets here
		 	productExcelObj.setPriceGrids(priceGrids);
		 	productExcelObj.setProductConfigurations(productConfigObj);
		 	productExcelObj.setProductRelationSkus(productsku);
		 	productExcelObj.setProductNumbers(pnumberList);
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId);
		 	if(num ==1){
		 		numOfProducts.add("1");
		 	}
		 	_LOGGER.info("list size>>>>>>"+numOfProducts.size());
			System.out.println(mapper1.writeValueAsString(productExcelObj));
		 	
		 	
		}catch(Exception e){
			_LOGGER.error("Error while Processing excel sheet ,Error: "+e.getMessage());
			return 0;
		}finally{
			
			productDaoObj.saveErrorLog(asiNumber,batchId);
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet ,Error: "+e.getMessage());
	
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
	public ProductDao getProductDaoObj() {
		return productDaoObj;
	}
	public void setProductDaoObj(ProductDao productDaoObj) {
		this.productDaoObj = productDaoObj;
	}

	
	
}
