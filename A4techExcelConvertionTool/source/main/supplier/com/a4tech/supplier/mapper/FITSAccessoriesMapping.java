package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.dataStore.ProductDataStore;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.FITSAccessories.FITSAttributeParser;
import parser.FITSAccessories.FITSPriceGridParser;

public class FITSAccessoriesMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(FITSAccessoriesMapping.class);
	
	private PostServiceImpl 				postServiceImpl;
	private ProductDao 						productDaoObj;
	private FITSAttributeParser             fitsAttributeParser;
	private FITSPriceGridParser             fitsPriceGridParser;
	
	@Override
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  List<String> repeatRows = new ArrayList<>();
		  int rowNumber ;
 		try{
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		String xid = null;
		int columnIndex=0;
		 List<ProductSkus> listProductSkus = new ArrayList<>();
		 String skuValue = "";
		 String colorVal = "";
		 String shippingDimensions = "";
		 String shippingWt ="";
		 Set<String> colorsList = new HashSet<>();
		 String imprintMethod = "";
		while (iterator.hasNext()) {
			try{
			Row nextRow = iterator.next();
			rowNumber = nextRow.getRowNum();
			if(nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO){
				continue;
			}
		 Short colorCode=getCellBackgroundColor(nextRow.getCell(3));
		 if(colorCode == 46){//purpole color
			 // waiting for client feedback
			 continue;
		 } else if(colorCode == 43){// yellow color
			// process yellow color rows(US Pricing)
		 } else if(colorCode == 64 || colorCode == null){// white color
			 if(StringUtils.isEmpty(imprintMethod)){
				 imprintMethod = getImprintMethod(nextRow);
			 }
			 continue;// ignore white color rows as per client feedback
			 //CA Pricing
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
						 if(nextRow.getRowNum() != 2){// change value based on color
							 System.out.println("Java object converted to JSON String, written to file");
							 	//productExcelObj.setPriceGrids(priceGrids);
							 if(!shippingDimensions.equalsIgnoreCase("0.00x0.00x0.00")){
								 ShippingEstimate shippingEst = fitsAttributeParser
											.getShippingEstimation(shippingDimensions, shippingWt);
									productConfigObj.setShippingEstimates(shippingEst);
							 }
							 List<Color> colorList = fitsAttributeParser.getProductColor(colorsList);
							 productConfigObj.setColors(colorList);
							 	productExcelObj.setProductConfigurations(productConfigObj);
							 	productExcelObj.setProductRelationSkus(listProductSkus);
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
								productConfigObj = new ProductConfigurations();
								repeatRows.clear();
								listProductSkus = new ArrayList<>();
								shippingDimensions = "";
							    shippingWt ="";
							    colorsList = new HashSet<>();
							    imprintMethod = "";
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    	//repeatRows.add(xid);
						    }
						    productExcelObj = new Product();
     						 productExcelObj = postServiceImpl.getProduct(accessToken, xid);
						   
						     if(productExcelObj == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						    	 productExcelObj = fitsAttributeParser.keepExistingProductData(productExcelObj);
						    	 productConfigObj=productExcelObj.getProductConfigurations();
						     }	
					 }
				}else{
					if(productXids.contains(xid) && repeatRows.size() != 0){
						 if(isRepeateColumn(columnIndex+1)){
							 continue;
						 }
					}
				}
				switch (columnIndex+1) {
				case 1://xid
					 productExcelObj.setExternalProductId(xid);
					 break;
				case 2:// prd Name
					String name = cell.getStringCellValue();
					name = getProductNumberAndName(name);
					String[] names = CommonUtility.getValuesOfArray(name, ",");
					productExcelObj.setName(names[1]);
					productExcelObj.setAsiProdNo(names[0]);
					  break;
				case 3:// sku
					try{
						skuValue = cell.getStringCellValue();
					} catch (Exception e) {//1518234800
						skuValue = CommonUtility.getCellValueStrinOrDecimal(cell);
						skuValue = CommonUtility.convertExponentValueIntoNumber(skuValue);
					}
				    break;
				case 4://categories
					// ignore this values does not meet ASI Standrd 
				    break;
				case 5: //CDN-MSRP
					String price = CommonUtility.getCellValueDouble(cell);					
					priceGrids = fitsPriceGridParser.getBasePriceGrid(price, "1", "P", "USD", "", true, false,
									"", "", priceGrids, "", "", "");
					productExcelObj.setPriceGrids(priceGrids);
					break;
				case 6: //Shipping dimension
					 shippingDimensions = cell.getStringCellValue();
					break;
				case 7://shipping wt(carton_weight)
					shippingWt = CommonUtility.getCellValueDouble(cell);
					break;
				case 8: // color
					colorVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(colorVal)){
						colorsList.add(colorVal);
					}
					break;
				case 9: // country
					String origin = cell.getStringCellValue();
					if(!StringUtils.isEmpty(origin)){
						List<Origin> originsList = fitsAttributeParser.getProductOrigin(origin);
						productConfigObj.setOrigins(originsList);
					}
					
					break;
				case 10://decoration(Imprint method)
					 //imprintMethod = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintMethod)){
								List<ImprintMethod> imprintMethodList = fitsAttributeParser
										.getProductImprintMethods(imprintMethod);
								productConfigObj.setImprintMethods(imprintMethodList);
					}
					break;
				case 11://description
					String desc = cell.getStringCellValue();
					desc = getDescription(desc);
					productExcelObj.setDescription(desc);
					break;
				case 12://new
					// ignore as per feedback
					break;
				case 13://new
					// ignore as per feedback
					break;
				case 14://new
					// ignore as per feedback
					break;
			}  // end inner while loop
					 
		}
				productExcelObj.setPriceType("L");
				if(!StringUtils.isEmpty(colorVal) && !StringUtils.isEmpty(skuValue) ){
					listProductSkus = fitsAttributeParser.getProductSkus(colorVal, skuValue, listProductSkus);
				}
				repeatRows.add(xid);
			}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);		 
				ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: "
				+e.getMessage()+" at column number(increament by 1)"+columnIndex);
				productDaoObj.save(apiResponse.getErrors(),
						productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);
		}
		}
		workbook.close();
		 //	productExcelObj.setPriceGrids(priceGrids);
		 if(!shippingDimensions.equalsIgnoreCase("0.00x0.00x0.00")){
			 ShippingEstimate shippingEst = fitsAttributeParser
						.getShippingEstimation(shippingDimensions, shippingWt);
				productConfigObj.setShippingEstimates(shippingEst);
		 }
		 List<Color> colorList = fitsAttributeParser.getProductColor(colorsList);
		 productConfigObj.setColors(colorList);
		 	productExcelObj.setProductConfigurations(productConfigObj);
		 	productExcelObj.setProductRelationSkus(listProductSkus);
		 		int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId);
			 	if(num ==1){
			 		numOfProductsSuccess.add("1");
			 	}else if(num == 0){
			 		numOfProductsFailure.add("0");
			 	}else{
			 		
			 	}		
		 	ProductDataStore.clearProductColorSet();
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
		if(StringUtils.isEmpty(productXid) || productXid.equalsIgnoreCase("#N/A")){
		     xidCell = row.getCell(1);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		     productXid = getProductNumberAndName(productXid);
		     String[] names = CommonUtility.getValuesOfArray(productXid, ",");
		     productXid = names[0];
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
		if (columnIndex != 3  && columnIndex != 8 ) {
				return ApplicationConstants.CONST_BOOLEAN_TRUE;
			}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	private Short getCellBackgroundColor(Cell cell){
		CellStyle style = cell.getCellStyle();
	 Short colorCode= style.getFillForegroundColor();
	return colorCode;
	 
	}
	private String getDescription(String desc){
		desc = desc.replaceAll("</li><li>", " ");
		desc = desc.replaceAll("\\<.*?\\> ?", "");
		desc = desc.replaceAll("velcro", " ");
		desc = desc.replaceAll("\n", " ");
		desc = desc.trim().replaceAll(" +", " ");
		return desc;
	}
	private String getProductNumberAndName(String val){
		String productNumber = val.replaceAll("[^0-9]", "").trim();
		String name = val.replaceAll("[0-9]", "").trim();
		String finalData = productNumber + ","+name;
		return finalData;
	}
	private String getImprintMethod(Row row){
		Cell cell = row.getCell(9);//imprint method column
		return cell.getStringCellValue();
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
	public FITSAttributeParser getFitsAttributeParser() {
		return fitsAttributeParser;
	}

	public void setFitsAttributeParser(FITSAttributeParser fitsAttributeParser) {
		this.fitsAttributeParser = fitsAttributeParser;
	}
	public FITSPriceGridParser getFitsPriceGridParser() {
		return fitsPriceGridParser;
	}

	public void setFitsPriceGridParser(FITSPriceGridParser fitsPriceGridParser) {
		this.fitsPriceGridParser = fitsPriceGridParser;
	}



}
