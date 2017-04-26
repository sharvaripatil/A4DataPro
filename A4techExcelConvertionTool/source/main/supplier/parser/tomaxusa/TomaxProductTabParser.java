package parser.tomaxusa;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.Price;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Values;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
public class TomaxProductTabParser {


	private static final Logger _LOGGER = Logger.getLogger(TomaxProductTabParser.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	@Autowired
	ObjectMapper mapperObj;
	TomaxUsaAttributeParser  tomaxUsaAttributeParser;
	TomaxPriceGridParser tomaxPriceGridParser;
	TomaxSizeParser tomaxUsaSizeParser;
	public static List<String> XIDS = new ArrayList<String>();
	
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
	
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
		 
		  String productName=null;
		  ShippingEstimate	shippingEstObj=new ShippingEstimate();
		  StringBuilder listOfPrices = new StringBuilder();
		  StringBuilder listOfQuantity = new StringBuilder();
		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(1);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		
	    String xid = null;
	    int columnIndex=0;
	    String q1 = null,q2= null,q3= null,q4= null,q5= null;
		while (iterator.hasNext()) {
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum()==0){
			continue;
			}
			
			if (getRowType(nextRow)){
				Cell cell1=nextRow.getCell(4);
				q1=cell1.getStringCellValue();
				q1=q1.toUpperCase().replace("LIST", "");
				q1=q1.trim();
				cell1=nextRow.getCell(5);
				q2=cell1.getStringCellValue();
				q2=q2.toUpperCase().replace("LIST", "");
				q2=q2.trim();
				cell1=nextRow.getCell(6);
				q3=cell1.getStringCellValue();
				q3=q3.toUpperCase().replace("LIST", "");
				q3=q3.trim();
				cell1=nextRow.getCell(7);
				q4=cell1.getStringCellValue();
				q4=q4.toUpperCase().replace("LIST", "");
				q4=q4.toUpperCase().replace("EQP", "");
				q4=q4.toUpperCase().replace("(", "");
				q4=q4.toUpperCase().replace(")", "");
				q4=q4.trim();
				cell1=nextRow.getCell(8);
				q5=cell1.getStringCellValue();
				q5=q5.toUpperCase().replace("LIST", "");
				q5=q5.trim();
				continue;
			}
			if(getRowData(nextRow)){
				continue;
			}
			/*if (nextRow.getRowNum() == 0)
				continue;*/
			
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
						 if(nextRow.getRowNum() != 1){
							 if( !StringUtils.isEmpty(listOfPrices.toString())){
							 priceGrids=new ArrayList<PriceGrid>();
							 priceGrids = tomaxPriceGridParser.getPriceGrids(listOfPrices.toString(),listOfQuantity.toString(), 
										"R",ApplicationConstants.CONST_STRING_CURRENCY_USD,
										"",ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
										productName,null,1,priceGrids);
							 }
							 
							 if(CollectionUtils.isEmpty(priceGrids)){
									priceGrids = tomaxPriceGridParser.getPriceGridsQur();	
								}
							 	productExcelObj.setPriceType("L");
							 	productExcelObj.setPriceGrids(priceGrids);
							 	productExcelObj.setProductConfigurations(productConfigObj);
							 /*_LOGGER.info("Product Data : "
										+ mapperObj.writeValueAsString(productExcelObj));*/
							 
						if(!StringUtils.isEmpty(productExcelObj.getExternalProductId())){
							 int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId);
							 	if(num ==1){
							 		numOfProductsSuccess.add("1");
							 	}else if(num == 0){
							 		numOfProductsFailure.add("0");
							 	}else{
							 		
							 	}
							 	_LOGGER.info("list size>>>>>>>"+numOfProductsSuccess.size());
							 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
						 }
							 	
								priceGrids = new ArrayList<PriceGrid>();
								productConfigObj = new ProductConfigurations();	
								repeatRows.clear();
								listOfPrices = new StringBuilder();
							    listOfQuantity = new StringBuilder();
							    shippingEstObj=new ShippingEstimate();

						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    	repeatRows.add(xid);
						    }
						    productExcelObj = new Product();
						    existingApiProduct = postServiceImpl.getProduct(accessToken, xid); 
						     if(existingApiProduct == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						    	 productExcelObj = tomaxUsaAttributeParser.getExistingProductData(existingApiProduct, existingApiProduct.getProductConfigurations());
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
		
				switch (columnIndex + 1) {
					case 1://XID
						//productId=xid;//CommonUtility.getCellValueStrinOrInt(cell);
						productExcelObj.setExternalProductId(xid);
						break;
						
					case 2://Tomax# (productid) can be disregarded
						/*String productNo=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(productNo)){
						productExcelObj.setAsiProdNo(productNo);
						}*/
						break;
						
					case 3://2016 page
						break;
					case 4://2017
						break;
					case 5:
						String	listPrice1=null;
						listPrice1=CommonUtility.getCellValueStrinOrDecimal(cell);
						listPrice1=listPrice1.replaceAll(" ","");
						if(!StringUtils.isEmpty(listPrice1)){
							listOfPrices.append(listPrice1.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							listOfQuantity.append(q1.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							
				         }
						break;
					case 6://Price 2
						String	listPrice2=null;
						listPrice2=CommonUtility.getCellValueStrinOrDecimal(cell);
						listPrice2=listPrice2.replaceAll(" ","");
						if(!StringUtils.isEmpty(listPrice2)){
							listOfPrices.append(listPrice2.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							listOfQuantity.append(q2.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							
				         }
						break;
					case 7://Price 3
						String	listPrice3=null;
						listPrice3=CommonUtility.getCellValueStrinOrDecimal(cell);
						listPrice3=listPrice3.replaceAll(" ","");
						if(!StringUtils.isEmpty(listPrice3)){
							listOfPrices.append(listPrice3.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							listOfQuantity.append(q3.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							
				         }
						break;
					case 8://Price 4
						String	listPrice4=null;
						listPrice4=CommonUtility.getCellValueStrinOrDecimal(cell);
						listPrice4=listPrice4.replaceAll(" ","");
						if(!StringUtils.isEmpty(listPrice4)){
							listOfPrices.append(listPrice4.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							listOfQuantity.append(q4.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							
				         }
						break;
					case 9://Price 5
						String	listPrice5=null;
						listPrice5=CommonUtility.getCellValueStrinOrDecimal(cell);
						listPrice5=listPrice5.replaceAll(" ","");
						if(!StringUtils.isEmpty(listPrice5)){
							listOfPrices.append(listPrice5.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							listOfQuantity.append(q5.trim()).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
						break;
					case 10:// Product name 
						 productName=CommonUtility.getCellValueStrinOrInt(cell);
						 if(!StringUtils.isEmpty(productName)){
								productName=CommonUtility.getStringLimitedChars(productName, 60);
								productExcelObj.setName(productName);
							}
						
						break;
					case 11://Description
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
					case 12://pc
						String shippingItem=CommonUtility.getCellValueStrinOrInt(cell);
						 if(!StringUtils.isEmpty(shippingItem)){
							 shippingEstObj=tomaxUsaAttributeParser.getShippingEstimates(shippingItem,shippingEstObj,"NOI");
							 productConfigObj.setShippingEstimates(shippingEstObj);
						 }
						break;
					case 13://wt.
						String shippingWt=CommonUtility.getCellValueDouble(cell);
						 if(!StringUtils.isEmpty(shippingWt)){
							 shippingEstObj=tomaxUsaAttributeParser.getShippingEstimates(shippingWt,shippingEstObj,"WT");
							 productConfigObj.setShippingEstimates(shippingEstObj);
						 }
						break;
					case 14://Optional upgrade packaging
						String packageValue=CommonUtility.getCellValueStrinOrInt(cell);
						 if(!StringUtils.isEmpty(packageValue)){
							 List<Packaging> listOfPackage=tomaxUsaAttributeParser.getPackageValues(packageValue);
							 productConfigObj.setPackaging(listOfPackage);
						 }
						
						break;
					case 15://color
						
						String colorValue=CommonUtility.getCellValueStrinOrInt(cell);
						 if(!StringUtils.isEmpty(colorValue)){
							 List<Color> colors =tomaxUsaAttributeParser.getProductColors(colorValue);
							 productConfigObj.setColors(colors);
						 }
						break;
					case 16://item size
						String sizeValue=CommonUtility.getCellValueStrinOrInt(cell);
						 if(!StringUtils.isEmpty(sizeValue)){
							 productConfigObj =tomaxUsaSizeParser.getSizes(sizeValue, productConfigObj);
							 //productConfigObj.setColors(colors);
						 }
						break;
					case 17://imprint size
						String impSize=CommonUtility.getCellValueStrinOrInt(cell);
						 if(!StringUtils.isEmpty(impSize)){
                             impSize=impSize.replaceAll("                                                 ", "");
							 impSize=CommonUtility.getStringLimitedChars(impSize, 750);
							 List<ImprintSize> listOfImpSize=productConfigObj.getImprintSize();
							 if(CollectionUtils.isEmpty(listOfImpSize)){
								 listOfImpSize=new ArrayList<ImprintSize>();
							 }
							 listOfImpSize=tomaxUsaAttributeParser.getImprintSize(impSize,listOfImpSize);
							 productConfigObj.setImprintSize(listOfImpSize);
						 }
						break;
					case 18://carton size
						//String cartonSizeValue=CommonUtility.getCellValueStrinOrInt(cell);
						 /*if(!StringUtils.isEmpty(cartonSizeValue)){//
							 Size existingSizeObj=productConfigObj.getSizes();
							 if(existingSizeObj.getDimension()!=null){
							 existingSizeObj =tomaxUsaSizeParser.getSizes(cartonSizeValue, existingSizeObj, existingSizeObj.getDimension(), existingSizeObj.getDimension().getValues());
							 }else{
						      existingSizeObj =tomaxUsaSizeParser.getSizes(cartonSizeValue, new Size(), new Dimension(), new ArrayList<Values>());
							 }
							 productConfigObj.setSizes(existingSizeObj);
							 }*/
						
						String shippinDim=CommonUtility.getCellValueStrinOrInt(cell);
						 if(!StringUtils.isEmpty(shippinDim)){
							 shippingEstObj=tomaxUsaAttributeParser.getShippingEstimates(shippinDim,shippingEstObj,"SDIM");
							 productConfigObj.setShippingEstimates(shippingEstObj);
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
		 // do criteria processing over here
		 if( !StringUtils.isEmpty(listOfPrices.toString())){
			 priceGrids=new ArrayList<PriceGrid>();
			 priceGrids = tomaxPriceGridParser.getPriceGrids(listOfPrices.toString(),listOfQuantity.toString(), 
						"R",ApplicationConstants.CONST_STRING_CURRENCY_USD,
						"",ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
						productName,null,1,priceGrids);
			 }
			 if(CollectionUtils.isEmpty(priceGrids)){
					priceGrids = tomaxPriceGridParser.getPriceGridsQur();	
				}
			 	productExcelObj.setPriceType("L");
			 	productExcelObj.setPriceGrids(priceGrids);
			 	productExcelObj.setProductConfigurations(productConfigObj);
		 	_LOGGER.info("Product Data : "
					+ mapperObj.writeValueAsString(productExcelObj));
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId);
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
		listOfPrices = new StringBuilder();
	    listOfQuantity = new StringBuilder();
	    shippingEstObj=new ShippingEstimate();
		repeatRows.clear();
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
		if(StringUtils.isEmpty(productXid) || productXid.trim().equalsIgnoreCase("#N/A")){
		     xidCell = row.getCell(1);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}
	
	public boolean isRepeateColumn(int columnIndex){
		
		if(columnIndex != 1&&columnIndex != 3&&columnIndex != 4 && columnIndex != 6 && columnIndex != 9 && columnIndex != 24){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	
	
	public static boolean getRowType(Row nextRow){
		boolean flag=false;
		Cell cell=nextRow.getCell(1);
		String str=cell.getStringCellValue();
		if(str.contains("Tomax")){
			flag=true;
		}/*else{
			flag=false;
		}*/
		return flag;
	}
	
	public static boolean getRowData(Row nextRow){
		boolean flag=false;
		Cell cell=nextRow.getCell(1);
		String str=cell.getStringCellValue();
		str=str.toUpperCase();
		if(str.contains("SPECIAL") || str.contains("CABLE") || str.contains("ITEM") || str.contains("POWER")
				|| str.contains("AUDIO") || str.contains("PATENTED") || str.contains("MOUSE")
				|| str.contains("SPEAKER") || str.contains("GIFT") || str.contains("PACKAGE") || str.contains("USB")
				){
			flag=true;
		}
		return flag;
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
	
	public TomaxUsaAttributeParser getTomaxUsaAttributeParser() {
		return tomaxUsaAttributeParser;
	}
	
	public void setTomaxUsaAttributeParser(
			TomaxUsaAttributeParser tomaxUsaAttributeParser) {
		this.tomaxUsaAttributeParser = tomaxUsaAttributeParser;
	}



	public TomaxPriceGridParser getTomaxPriceGridParser() {
		return tomaxPriceGridParser;
	}



	public void setTomaxPriceGridParser(TomaxPriceGridParser tomaxPriceGridParser) {
		this.tomaxPriceGridParser = tomaxPriceGridParser;
	}



	public TomaxSizeParser getTomaxUsaSizeParser() {
		return tomaxUsaSizeParser;
	}



	public void setTomaxUsaSizeParser(TomaxSizeParser tomaxUsaSizeParser) {
		this.tomaxUsaSizeParser = tomaxUsaSizeParser;
	}



	
	
}
