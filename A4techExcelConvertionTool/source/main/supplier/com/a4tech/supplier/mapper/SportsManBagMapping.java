package com.a4tech.supplier.mapper;

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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.core.model.ErrorMessageList;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.service.imple.PostServiceImpl;
import com.a4tech.supplier.service.ISupplierParser;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.sportsmanBag.SportsManBagAttributeParser;
import parser.sportsmanBag.SportsManBagPriceGridParser;
public class SportsManBagMapping implements ISupplierParser{
	private PostServiceImpl 					postServiceImpl;
	private ProductDao 							productDaoObj;
	private SportsManBagAttributeParser         sportsManAttributeParser;
    private SportsManBagPriceGridParser         sportsManPriceGridParser;
	
	private static Logger _LOGGER = Logger.getLogger(SportsManBagMapping.class);
	/*
	 * EXCIT-435
	 */   
	@Override
	public String readExcel(String accessToken, Workbook workbook, Integer asiNumber, int batchId,
			String environmentType) {
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		int numbersOfSheets = workbook.getNumberOfSheets();
		for(int sheetNo =0;sheetNo<numbersOfSheets;sheetNo++){
			Sheet sheet = workbook.getSheetAt(sheetNo);
			String sheetName = sheet.getSheetName();
		int headerDataRow = getHeaderRowData(sheetName);
		if(sheetName.equalsIgnoreCase("Color Mapping") || sheetName.equalsIgnoreCase("Field Mapping")){
			continue;
		}
		if(headerDataRow == 0){
			_LOGGER.error("unable to find header data row number,please check the sheetName: "+sheetName);
			continue;
		}
		int columnIndex = 0;
		Set<String> productXids = new HashSet<String>();
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		ProductConfigurations productConfigObj = new ProductConfigurations();
		Product existingApiProduct = null;
		Product productExcelObj = new Product();
		String productId = null;
		String headerName = "";
		StringBuilder description = new StringBuilder();
		try {
			Iterator<Row> iterator = sheet.iterator();
			Row headerRow = null;
			while (iterator.hasNext()) {
				try {
					Row nextRow = iterator.next();

					if (nextRow.getRowNum() < headerDataRow-1) {
						headerRow = nextRow;
						continue;
					}
					if(!isValidProductId(nextRow)){//here checking the product xid is vaild or not
						continue;
					}
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					if(productId != null){
						productXids.add(productId);
					}
					boolean checkXid  = false;			
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						columnIndex = cell.getColumnIndex();
						if(columnIndex + 1 == 1){
							productId = getProductXid(nextRow);
							checkXid = true;
						}else{
							checkXid = false;
						}
						if (checkXid) {
							if (!productXids.contains(productId)) {
								if (nextRow.getRowNum() != headerDataRow-1) {
									System.out.println("Java object converted to JSON String, written to file");
									//productConfigObj.setOptions(listOfOptions);
									List<ImprintMethod> imprintMethodList = sportsManAttributeParser.getProductImprintMethods("unimprinted");
									productConfigObj.setImprintMethods(imprintMethodList);
									productExcelObj.setProductConfigurations(productConfigObj);
									productExcelObj.setPriceGrids(priceGrids);
									productExcelObj.setDescription(
												getFinalDescription(description.toString(), productExcelObj));
									String envTypeAndSheetName = environmentType+":"+sheetName;
									int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId, envTypeAndSheetName);
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
									description = new StringBuilder();
								}
								productConfigObj = new ProductConfigurations();
								productExcelObj = new Product();
								if(StringUtils.isEmpty(productId)){
									break;
								}
								existingApiProduct = postServiceImpl.getProduct(accessToken, productId, environmentType);
								if (existingApiProduct == null) {
									_LOGGER.info("Existing Xid is not available,product treated as new product");
									productExcelObj = new Product();
								} else {
									productExcelObj = existingApiProduct;
									productExcelObj = sportsManAttributeParser.keepExistingProductData(productExcelObj);
									productConfigObj = productExcelObj.getProductConfigurations();
									priceGrids = productExcelObj.getPriceGrids();
									if(CollectionUtils.isEmpty(priceGrids)){
										priceGrids = new ArrayList<>();
									}
									productExcelObj.setDistributorOnlyComments("");
									productExcelObj.setProductDataSheet("");
								}
							}
						}
						headerName = getHeaderName(columnIndex, headerRow);
						switch (headerName) {
						case "XID":
							productExcelObj.setExternalProductId(productId);
							break;
						case "STYLE #":
							String prdNo = CommonUtility.getCellValueStrinOrInt(cell);
							if(!StringUtils.isEmpty(prdNo)){
								productExcelObj.setAsiProdNo(CommonUtility.getStringLimitedChars(prdNo, 14));	
							}
							break;
						case "DESCRIPTION":// product Name
							String name = cell.getStringCellValue();
							name = name.replaceAll("®", "");
							productExcelObj.setName(CommonUtility.getStringLimitedChars(name, 60));
							break;
						case "Web Description":// Description
						case "Web Extended Description":
						case "Extended Description":
						case "BUCKRAM/PROFILE/PANEL":
						case "BUCKRAM/PROFILE/PANEL (Bullet #2)":
						case "BRIM":
						case "BRIM (Bullet #3)":	
						case "EXTENDED DESCRIPTION 1":
						case "EXTENDED DESCRIPTION 2":
						case "EXTENDED DESCRIPTION 1 (Bullet #5)":
						case "EXTENDED DESCRIPTION 2 (Bullet #6)":
						case "CLOSURE":
						case "CLOSURE (Bulled #4)":	
						case "NOTE":
							String desc = cell.getStringCellValue();
							description.append(desc.trim()).append(" ");
							break;
						case "FABRIC":
						case "FABRIC (Bullet #1)":
							String materials = cell.getStringCellValue();
							if(!StringUtils.isEmpty(materials)){
								List<Material> materialList = sportsManAttributeParser.getProductMaterial(materials);
								productConfigObj.setMaterials(materialList);	
							}
							break;						
						case "SIZE":
						case "SIZE (Bullet #7)":
							String size = cell.getStringCellValue();
							if(!StringUtils.isEmpty(size)){
								Size sizeObj = sportsManAttributeParser.getProductSize(size, sheetName);
								productConfigObj.setSizes(sizeObj);	
							}
							break;
						case "COLORS":
							String color = cell.getStringCellValue();
							if(!StringUtils.isEmpty(color)){
								List<Color> colorsList = sportsManAttributeParser.getProductColor(color);
								productConfigObj.setColors(colorsList);	
							}
							break;
						case "CASE PACK":
							String shippingEsti = CommonUtility.getCellValueStrinOrInt(cell);
							if(!StringUtils.isEmpty(shippingEsti)){
								 if(!shippingEsti.contains("natural") && !shippingEsti.contains("Natural")){
									 ShippingEstimate shippingEstObj = sportsManAttributeParser.getShippingEstimateValues(shippingEsti);
										productConfigObj.setShippingEstimates(shippingEstObj);	 
								 }
							}
							break;
						case "#SKUS":
							// ignore as per feedback
							break;
						}
					}
					// as per feedback we need assign QUR flag is true for all products
					priceGrids = sportsManPriceGridParser.getBasePriceGrids("1", "1", "Not applicabe", "USD", "", true,
							"true", "", "", priceGrids);				
					productExcelObj.setPriceType("L");
				} catch (Exception e) {
					_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);		 
					ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: "
					+e.getMessage()+" at column number(increament by 1)"+columnIndex);
					productDaoObj.save(apiResponse.getErrors(),
							productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);
				}
				/*productConfigObj.setOptions(listOfOptions);
				productExcelObj.setProductConfigurations(productConfigObj);
				productExcelObj.setPriceGrids(priceGrids);
				productsMap.put(productExcelObj.getProductLevelSku(), productExcelObj);*/

				// test.add(productExcelObj);
			}
			//productConfigObj.setOptions(listOfOptions);
			List<ImprintMethod> imprintMethodList = sportsManAttributeParser.getProductImprintMethods("unimprinted");
			productConfigObj.setImprintMethods(imprintMethodList);
			productExcelObj.setProductConfigurations(productConfigObj);
			productExcelObj.setPriceGrids(priceGrids);
			productExcelObj.setDescription(
					getFinalDescription(description.toString(), productExcelObj));
			String envTypeAndSheetName = environmentType+":"+sheetName;
			int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId, envTypeAndSheetName);
		 	if(num ==1){
		 		numOfProductsSuccess.add("1");
		 	}else if(num == 0){
		 		numOfProductsFailure.add("0");
		 	}else{
		 	}
			priceGrids = new ArrayList<PriceGrid>();
			productConfigObj = new ProductConfigurations();
			description = new StringBuilder();
		} catch (Exception e) {

			//return "";
		} finally {
		}
	}
		try {
			workbook.close();
			
		} catch (IOException e) {
			_LOGGER.error("Unable to close workbook");
		}
		// finally result
		finalResult = numOfProductsSuccess.size() + "," + numOfProductsFailure.size();
		productDaoObj.saveErrorLog(asiNumber,batchId);
		return finalResult;
	}
	private String getHeaderName(int columnIndex, Row headerRow) {
		Cell cell2 = headerRow.getCell(columnIndex);
		String headerName = CommonUtility.getCellValueStrinOrInt(cell2);
		// columnIndex = ProGolfHeaderMapping.getHeaderIndex(headerName);
		return headerName;
	}

	private String getProductXid(Row row) {
		Cell xidCell = row.getCell(ApplicationConstants.CONST_NUMBER_ZERO);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if (StringUtils.isEmpty(productXid) || productXid.equals("#N/A")) {
			xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_TWO);
			productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		} 
		return productXid;
	}
	private int getHeaderRowData(String sheetName){
		int sheetDataRow = 0;
		if(sheetName.equals("SP, Team SP (AU)")){
			sheetDataRow = 6;
		} else if(sheetName.equals("Valucap")){
			sheetDataRow= 7;
		} else if(sheetName.equals("OC")){
			sheetDataRow = 7 ;
		} else if(sheetName.equals("FF, AD")){
			sheetDataRow = 5;
		} else if(sheetName.equals("Dri Duck")){
			sheetDataRow = 6;
		}else if(sheetName.equals("Kati")){
			sheetDataRow = 7;
		} else if(sheetName.equals("YP, Mega")){
			sheetDataRow = 6;
		} else if(sheetName.equals("Bay, Rich")){
			sheetDataRow = 7;
		} else if(sheetName.equals("Bags")){
			sheetDataRow = 7;
		} else if(sheetName.equals("Aprons and Towels")){
			sheetDataRow = 7;
		} else {
			
		}
		return sheetDataRow;
	}
	private boolean isValidProductId(Row row){	
		Cell xidCell = row.getCell(ApplicationConstants.CONST_NUMBER_ZERO);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if (StringUtils.isEmpty(productXid) || productXid.equals("#N/A")) {
			xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_TWO);
			productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
			if (StringUtils.isEmpty(productXid)) {
				return false;
			} else if(!isValidProductId(productXid)){
				return false;
			}
		} 
		return true;
	}
	private boolean isValidProductId(String productXid){
		if (StringUtils.isEmpty(productXid) || productXid.equals("STYLE #")
				|| productXid.equals("TEAM SPORTSMAN (formerly Authentic)") || productXid.equals("ALTERNATIVE")
				|| productXid.equals("ADIDAS") || productXid.equals("MEGA CAP") || productXid.equals("PUMA")
				|| productXid.equals("BAYSIDE") || productXid.equals("VALUBAG") || productXid.equals("SPY")
				|| productXid.equals("BASECAMP") || productXid.equals("CHAMPION") || productXid.equals("AME & LULU")
				|| productXid.equals("CAROLINA SEWN") || productXid.equals("DRI DUCK")
				|| productXid.equals("ADIDAS") || productXid.equals("STORMTECH") || productXid.equals("KATI")
				|| productXid.equals("BAYSIDE") || productXid.equals("HYP")) {
			return false;
			
		}
		return true;
	}
	private String getFinalDescription(String descri ,Product product){
		if(StringUtils.isEmpty(descri)){
			descri = product.getName();
		} else {
			descri = descri.replaceAll("Velcro", "");
			descri = descri.replaceAll("®", "");
			descri = descri.replaceAll("  ", " ");
			if(descri.contains("”")){
				descri = descri.replaceAll("”", "");	
			}
			if(descri.contains("™")){
				descri = descri.replaceAll("™", "");	
			}
		}
		return descri;
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
	public SportsManBagAttributeParser getSportsManAttributeParser() {
		return sportsManAttributeParser;
	}

	public void setSportsManAttributeParser(SportsManBagAttributeParser sportsManAttributeParser) {
		this.sportsManAttributeParser = sportsManAttributeParser;
	}
	public SportsManBagPriceGridParser getSportsManPriceGridParser() {
		return sportsManPriceGridParser;
	}

	public void setSportsManPriceGridParser(SportsManBagPriceGridParser sportsManPriceGridParser) {
		this.sportsManPriceGridParser = sportsManPriceGridParser;
	}

	}
