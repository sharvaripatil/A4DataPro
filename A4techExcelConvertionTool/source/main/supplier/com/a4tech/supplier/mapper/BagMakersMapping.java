package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.functions.ImReal;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import parser.BagMakers.BagMakerAttributeParser;
import parser.highcaliberline.HighCaliberAttributeParser;
import parser.highcaliberline.HighCaliberPriceGridParser;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.AdditionalLocation;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BagMakersMapping implements IExcelParser{

	
	private static final Logger _LOGGER = Logger.getLogger(BagMakersMapping.class);
	PostServiceImpl postServiceImpl;
	ProductDao productDaoObj;
	BagMakerAttributeParser bagMakerAttributeParser;
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
								
								 if(CollectionUtils.isEmpty(priceGrids)){
										//priceGrids = highCalPriceGridParser.getPriceGridsQur();	
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
						/*productId=CommonUtility.getCellValueStrinOrInt(cell);
						/////imp code 
						existingApiProduct=postServiceImpl.getProduct(accessToken, productId);
						if(existingApiProduct!=null){
							_LOGGER.info("Product "+productId+" is an existing product Using existing product also");
							productExcelObj=existingApiProduct;
							productConfigObj=productExcelObj.getProductConfigurations();
						    priceGrids = productExcelObj.getPriceGrids();
						}else{
							_LOGGER.info("Product "+productId+"Not an existing product ,Creating new product");
							productExcelObj=new Product();
						}*/
						////*/
						productExcelObj.setExternalProductId(xid);
						break;
					
					case  2: //Catalogpage
						String catalogPage=CommonUtility.getCellValueStrinOrDecimal(cell);
						break;
					case  3://Item #
						String asiProdNo=CommonUtility.getCellValueStrinOrDecimal(cell);
						break;
					case  4://Name (Items in Red are new for 2017)
						String productName = CommonUtility.getCellValueStrinOrInt(cell);
						//productName = CommonUtility.removeSpecialSymbols(productName,specialCharacters);
						if(StringUtils.isEmpty(productName)){
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
						//later
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

						break;
					case  15://Weight (lbs)

						break;
					case  16://Box Length

						break;
					case  17://Box Width

						break;
					case  18://Box Depth

						break;
					case  19://Origin

						break;
					case  20://Eco Characteristics

						break;
					case  21://Keywords (for Search online)

						break;
					case  22://Special Notes

						break;
					case  23://Plate/Screen Charge

						break;
					case  24://Plate/Screen Charge Code

						break;
					case  25://REORDER Plate/Screen Charge

						break;
					case  26://REORDER Plate/Screen Charge Code

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

						break;
					case  43://Extra Color Run Code

						break;
					case  44://Extra Location Run Charge

						break;
					case  45://Extra Location Run Code

						break;
					case  46://Extra Color/Location Screen/Plate Charge

						break;
					case  47://Extra Color/Location Screen/Plate Code

						break;
					case  48://Production Time

						break;
					case  49://QCA/CPSIA Product Safety Compliant

					
					
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
	
	
	boolean prod1flag=false;
	 boolean prod2flag=false;
	 boolean rushflag=false;
	 boolean priceFlag=false;
	 String flag="";
	 
	 if(!StringUtils.isEmpty(finalProdTimeVal)&& !StringUtils.isEmpty(listOfPricesProd1.toString())){
		 prod1flag=true;
		 priceFlag=true;
		}else{
			prod1flag=false;
			flag="false;";
		}
	 
	 if(!StringUtils.isEmpty(finalRushTimeVal)&& !StringUtils.isEmpty(listOfPricesRush.toString())){
		 rushflag=true;
		 priceFlag=true;
		}else{
			flag=flag+"false;";
			rushflag=false;
		}
	 
	 if(!StringUtils.isEmpty(finalProdTimeVal2)&& !StringUtils.isEmpty(listOfPricesProd2.toString())){
			////imp code
		 	prod2flag=true;
		 	priceFlag=true;
		}else{
			flag=flag+"false;";
			prod2flag=false;
		}
	 	
	 // setting pricing over here
	 // need to create a map over here 
	 //color upcharges
	 //same goes here as well
	 //color & location
	 // same goes here as well
	 if(CollectionUtils.isEmpty(priceGrids)){
			//priceGrids = highCalPriceGridParser.getPriceGridsQur();	
		}
	 
	 productExcelObj.setPriceType("L");//need to cofirm
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
	 	}else if(num == 0){
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
	
}




