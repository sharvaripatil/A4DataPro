package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import parser.EdwardsGarment.EdwardsGarmentAttributeParser;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.OtherSize;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.Price;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EdwardsGarmentMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(EdwardsGarmentMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;
	
	private EdwardsGarmentAttributeParser edwardsGarmentAttributeParser;
	
	@Autowired
	ObjectMapper mapperObj;
	
	@SuppressWarnings("unused")
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		String asiProdNo="";
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  String productId = null;
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  List<String> repeatRows = new ArrayList<>();
		  StringBuilder dimensionValue = new StringBuilder();
		  StringBuilder dimensionUnits = new StringBuilder();
		  StringBuilder dimensionType = new StringBuilder();
		  Dimension finalDimensionObj=new Dimension();
		  StringBuilder listOfQuantity = new StringBuilder();
		  StringBuilder listOfPrices = new StringBuilder();
		  StringBuilder priceIncludes = new StringBuilder();
		  StringBuilder pricesPerUnit = new StringBuilder();
		  StringBuilder ImprintSizevalue = new StringBuilder();
		  StringBuilder ImprintSizevalue2 = new StringBuilder();
		  boolean criteriaFlag=false;
			List<Color> colorList = new ArrayList<Color>();
			List<ImprintLocation> listImprintLocation = new ArrayList<ImprintLocation>();
			List<ImprintMethod> listOfImprintMethods = new ArrayList<ImprintMethod>();
			List<ProductionTime> listOfProductionTime = new ArrayList<ProductionTime>();
			List<String> productKeywords = new ArrayList<String>();
			List<Theme> themeList = new ArrayList<Theme>();
			//List<Catalog> catalogList = new ArrayList<Catalog>();
			List<String> complianceList = new ArrayList<String>();
			List<Material> materiallist = new ArrayList<Material>();	
			Set<String> setImages= new HashSet();
		    List<ImprintSize> imprintSizeList =new ArrayList<ImprintSize>();
		    //List<com.a4tech.lookup.model.Catalog> catalogsList=new ArrayList<>();
		    List<Values> valuesList =new ArrayList<Values>();
			RushTime rushTime  = new RushTime();
			Size size=new Size();
			StringBuilder additionalClrRunChrgPrice = new StringBuilder();
			String        additionalClrRunChrgCode = "";
			String        additionalColorPriceVal = "";
			String        additionalColorCode     = "";
			HashMap<String, String>  productPricePriceMap=new HashMap<String, String>();
			 String xid = null;
		try{
			List<ProductNumber> pnumberList = new ArrayList<ProductNumber>(); 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		String priceCode = null;
		String productName = null;
		String basePriceName=null;
		String quoteUponRequest  = null;
		String quantity = null;
		String cartonL = null;
		String cartonW = null;
		String cartonH = null;
		String weightPerCarton = null;
		String unitsPerCarton = null;
		String decorationMethod =null;
		 Date  priceConfirmedThru =null;
		String FirstImprintsize1=null;
		String FirstImprintunit1=null;
		String FirstImprinttype1=null;
		String FirstImprintsize2=null;
		String FirstImprintunit2=null;
		String FirstImprinttype2=null;
		String SecondImprintsize1=null;
		String SecondImprintunit1=null;
		String SecondImprinttype1=null;
		String SecondImprintsize2=null;
		String SecondImprintunit2=null;
		String SecondImprinttype2=null;
		String CatYear=null;
		Cell cell2Data = null;
		String prodTimeLo = null;
		String FOBValue= null;
		String themeValue=null;
		String priceIncludesValue=null;
		String imprintLocation = null;
		String ProductStatus=null;
		boolean Prod_Status;
		Product existingApiProduct = null;
		int columnIndex = 0;
		Map<String, String>  imprintMethodUpchargeMap = new LinkedHashMap<>();
		HashMap<String, String>  pnumMap = new HashMap<>();
		HashSet<String>  colorSet = new HashSet<>();
		String firstValue="";
		
while (iterator.hasNext()) {
			
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
				 cell2Data =  nextRow.getCell(2);
				if(columnIndex + 1 == 1){
					//xid = CommonUtility.getCellValueStrinOrInt(cell);//getProductXid(nextRow);
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						xid = cell.getStringCellValue();
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						xid = String.valueOf((int)cell.getNumericCellValue());
					}else {
						  String ProdNo=CommonUtility.getCellValueStrinOrInt(cell2Data);
						  xid=ProdNo;

						}
					xid=xid.trim();
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							 System.out.println("Java object converted to JSON String, written to file");
								
							 if(!CollectionUtils.isEmpty(setImages)){
									List<Image> listOfImages = edwardsGarmentAttributeParser.getImages(new ArrayList<String>(setImages));
									productExcelObj.setImages(listOfImages);
								}
							//process colors here
							// process pring here
								/*priceGrids = gillStudiosPriceGridParser.getPriceGrids(listOfPrices.toString(), 
										         listOfQuantity.toString(), priceCode, "USD",
										         priceIncludesValue, true, quoteUponRequest, basePriceName,tempCriteria,pricesPerUnit.toString(),priceGrids);*/	
							 
							    productExcelObj.setPriceGrids(priceGrids);
							 	productExcelObj.setProductConfigurations(productConfigObj);
							 //	if(Prod_Status = false){
							 	_LOGGER.info("Product Data : "
										+ mapperObj.writeValueAsString(productExcelObj));
							 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId);
							 	if(num ==1){
							 		numOfProductsSuccess.add("1");
							 	}else if(num == 0){
							 		numOfProductsFailure.add("0");
							 	}else{
							 		
							 	}
							 	//}
							 	_LOGGER.info("list size>>>>>>>"+numOfProductsSuccess.size());
							 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
							 	repeatRows.clear();
							 	setImages=new HashSet<String>();
								priceGrids = new ArrayList<PriceGrid>();
								listOfPrices = new StringBuilder();
							    listOfQuantity = new StringBuilder();
								productConfigObj = new ProductConfigurations();
								themeList = new ArrayList<Theme>();
								finalDimensionObj = new Dimension();
								 valuesList = new ArrayList<>();
								productKeywords = new ArrayList<String>();
								listOfProductionTime = new ArrayList<ProductionTime>();
								rushTime = new RushTime();
								listImprintLocation = new ArrayList<ImprintLocation>();
								listOfImprintMethods = new ArrayList<ImprintMethod>();
								
								imprintSizeList =new ArrayList<ImprintSize>();
								ImprintSizevalue = new StringBuilder();
								ImprintSizevalue2 = new StringBuilder();
								size=new Size();
								colorList = new ArrayList<Color>();
								 dimensionValue = new StringBuilder();
								 dimensionUnits = new StringBuilder();
								 dimensionType = new StringBuilder();
								 priceIncludes = new StringBuilder();
								 imprintMethodUpchargeMap = new LinkedHashMap<>();
								 priceIncludesValue=null;
								 priceIncludes = new StringBuilder();
								 imprintMethodUpchargeMap = new LinkedHashMap<>();
								 additionalClrRunChrgPrice = new StringBuilder();
							     additionalClrRunChrgCode = "";
							     additionalColorPriceVal = "";
						         additionalColorCode     = "";
						         pricesPerUnit=new StringBuilder();
						         //productSizePriceMap=new HashMap<String, String>();
						         pnumMap=new HashMap<String, String>();
						         colorSet=new HashSet<String>();
						         productPricePriceMap=new HashMap<String, String>();
						         firstValue="";
						         System.out.println("");
						 }
						 if(!productXids.contains(xid)){
						    	productXids.add(xid.trim());
						    	repeatRows.add(xid.trim());
						    }
						    existingApiProduct = postServiceImpl.getProduct(accessToken, xid=xid.replace("\t",""));
						     if(existingApiProduct == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						    	 productExcelObj = edwardsGarmentAttributeParser.getExistingProductData(existingApiProduct, existingApiProduct.getProductConfigurations());
						    	 productConfigObj=productExcelObj.getProductConfigurations();
						     }
					 }
				//}
				}else{
					if(productXids.contains(xid) && repeatRows.size() != 1){
						 if(isRepeateColumn(columnIndex+1)){
							 continue;
						 }
					}
				}

				switch (columnIndex + 1) {
			
				case 1://xid
					   productExcelObj.setExternalProductId(xid.trim());
					
					 break;
				
				 case  2://Customer

				    	break;
				    case  3://Stock

				    	break;
				    case  4://UPC

				    	break;
				    case  5://Class

				    	break;
				    case  6://ClassDescription

				    	break;
				    case  7://Style
				    	 asiProdNo=CommonUtility.getCellValueStrinOrInt(cell);
				    	if(!StringUtils.isEmpty(asiProdNo)){
					     productExcelObj.setAsiProdNo(asiProdNo);	
				    	}
				    	break;
				    case  8://Color

				    	break;
				    case  9://Size1

				    	break;
				    case  10://Size2

				    	break;
				    case  11://ProdName
				    	productName = CommonUtility.getCellValueStrinOrInt(cell);
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
				    case  12://WholesalePrice

				    	break;
				    case  13://MSRP

				    	break;
				    case  14://CustomerPricing

				    	break;
				    case  15://Weight
				    	
				    	
				    	String  shippingWeightValue=CommonUtility.getCellValueStrinOrInt(cell);
				    	 if(!StringUtils.isEmpty(shippingWeightValue)){
				    	 ShippingEstimate ShipingObj=new ShippingEstimate();
						 ShipingObj =edwardsGarmentAttributeParser.getShippingEstimates("", "", "", shippingWeightValue, "",ShipingObj);
						 productConfigObj.setShippingEstimates(ShipingObj);
				    	 }
						 
				    	break;
				    case  16://Height

				    	break;
				    case  17://Length

				    	break;
				    case  18://KeyWords
				    	String keywords = CommonUtility.getCellValueStrinOrInt(cell);
						
						 if(!StringUtils.isEmpty(keywords)){
							 keywords=keywords.replace(" ", ",");
							 List<String> listOfKeywords = new ArrayList<String>();
						String tempKeyWrd[]=keywords.split(ApplicationConstants.CONST_DELIMITER_COMMA);
						listOfKeywords=Arrays.asList(tempKeyWrd);
						productExcelObj.setProductKeywords(listOfKeywords);
						 }
				    	break;
				    case  19://StyleDescription1 // i need to work more on this field
				    	String description =CommonUtility.getCellValueStrinOrInt(cell);
						//description = CommonUtility.removeSpecialSymbols(description,specialCharacters);
				    	description=removeSpecialChar(description);
						int length=description.length();
						 if(length>800){
							String strTemp=description.substring(0, 800);
							int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
							description=(String) strTemp.subSequence(0, lenTemp);
						}
						productExcelObj.setDescription(description);
				    	break;
				    case  20://StyleDescription2
				    	String summary = CommonUtility.getCellValueStrinOrInt(cell);
						 if(!StringUtils.isEmpty(summary)){
						productExcelObj.setSummary(summary);
						 }
				    	break;
				    case  21://ColorDescription
				    	String colorValue=CommonUtility.getCellValueStrinOrInt(cell);
						 if(!StringUtils.isEmpty(colorValue)){
							 List<Color> colors =edwardsGarmentAttributeParser.getProductColors(colorValue);
							 productConfigObj.setColors(colors);
						 }
				    	break;
				    case  22://ComponentContent
				    	String Material=cell.getStringCellValue();
						if (!StringUtils.isEmpty(Material)&& !Material.equalsIgnoreCase("")) {
							materiallist=edwardsGarmentAttributeParser.getMaterialValue(Material);						
							productConfigObj.setMaterials(materiallist);
						}
				    	break;
				    case  23://ShortDescription
				    	productName = CommonUtility.getCellValueStrinOrInt(cell);
				    	if(!StringUtils.isEmpty(productName)){
				    		String temp="";
				    		if(!StringUtils.isEmpty(productExcelObj.getName())){
				    			temp=productExcelObj.getName();
				    		}
				    		if(temp.length()<60){
						int len=productName.length();
						 if(len>60){
							String strTemp=productName.substring(0, 60);
							int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
							productName=(String) strTemp.subSequence(0, lenTemp);
						}
						 temp=temp+productName;
						 if(temp.length()>60){
								String strTemp=temp.substring(0, 60);
								int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
								productName=(String) strTemp.subSequence(0, lenTemp);
							}
						productExcelObj.setName(productName);
				    		}
				    	}
				    	break;
				    case  24://HTML

				    	break;
				    case  25://ThumbName

				    	break;
				    case  26://ThumbPath

				    	break;
				    case  27://ThumbAlternateName

				    	break;
				    case  28://ImageName

				    	break;
				    case  29://ImagePath
				    	String largeImage = CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(largeImage)){
							setImages.add(largeImage);
						}
				    	break;
				    case  30://ImageAlternateName

				    	break;
				    case  31://SwatchPath

				    	break;
				    case  32://Status
				    	break;
				    case  33://UserDefined1

				    	break;
				    case  34://UserDefined2

				    	break;
				    case  35://UserDefined3

				    	break;
				    case  36://UserDefined4

				    	break;
				    case  37://UserDefined5

				    	break;
				}  // end inner while loop
					 
			}// set  product configuration objects
		 // end inner while loop
			
			//ShippingEstimate // i have to work on this thing as well for empty ship obj
		 // if any configurations come they will be here
			productExcelObj.setPriceType("L");
			// i have to process  pricingover here
			 	productExcelObj.setPriceGrids(priceGrids);
			 	criteriaFlag=false;
			 	listOfPrices=new StringBuilder();
			 	listOfQuantity=new StringBuilder();
			 	pricesPerUnit=new StringBuilder();
			 	 imprintMethodUpchargeMap = new LinkedHashMap<>();
				 additionalClrRunChrgPrice = new StringBuilder();
				 themeList = new ArrayList<Theme>();
				 listOfProductionTime = new ArrayList<ProductionTime>();
				 listImprintLocation = new ArrayList<ImprintLocation>();
				 listOfImprintMethods = new ArrayList<ImprintMethod>();
				 pricesPerUnit=new StringBuilder();
			     additionalClrRunChrgCode = "";
			     additionalColorPriceVal = "";
		         additionalColorCode     = "";
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() +"at column+1="+columnIndex);		 
		}
		}
		workbook.close();	
		 if(!CollectionUtils.isEmpty(setImages)){
				List<Image> listOfImages = edwardsGarmentAttributeParser.getImages(new ArrayList<String>(setImages));
				productExcelObj.setImages(listOfImages);
			}
		//productExcelObj.setPriceGrids(priceGrids);
		
	 // i have to process pricing
		productExcelObj.setPriceGrids(priceGrids);
		productExcelObj.setProductConfigurations(productConfigObj);
	
		 	_LOGGER.info("Product Data : "
					+ mapperObj.writeValueAsString(productExcelObj));
		 	//if(Prod_Status = false){
		 	productExcelObj.setPriceType("L");
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId);
		 	if(num ==1){
		 		numOfProductsSuccess.add("1");
		 	}else if(num == 0){
		 		numOfProductsFailure.add("0");
		 	}else{
		 		
		 	}
		 //	}
		 	_LOGGER.info("list size>>>>>>"+numOfProductsSuccess.size());
		 	_LOGGER.info("Failure list size>>>>>>"+numOfProductsFailure.size());
	       finalResult = numOfProductsSuccess.size() + "," + numOfProductsFailure.size();
	       productDaoObj.saveErrorLog(asiNumber,batchId);
	       setImages=new HashSet<String>();
		    priceGrids = new ArrayList<PriceGrid>();
			listOfPrices = new StringBuilder();
		    listOfQuantity = new StringBuilder();
			productConfigObj = new ProductConfigurations();
			themeList = new ArrayList<Theme>();
			finalDimensionObj = new Dimension();
			 valuesList = new ArrayList<>();
			productKeywords = new ArrayList<String>();
			listOfProductionTime = new ArrayList<ProductionTime>();
			rushTime = new RushTime();
			listImprintLocation = new ArrayList<ImprintLocation>();
			listOfImprintMethods = new ArrayList<ImprintMethod>();
			imprintSizeList =new ArrayList<ImprintSize>();
			size=new Size();
			colorList = new ArrayList<Color>();
			ImprintSizevalue = new StringBuilder();
			//DimensionRef=null;
			 dimensionValue = new StringBuilder();
			 dimensionUnits = new StringBuilder();
			 dimensionType = new StringBuilder();
			 priceIncludesValue=null;
			 priceIncludes = new StringBuilder();
			 imprintMethodUpchargeMap = new LinkedHashMap<>();
			 additionalClrRunChrgPrice = new StringBuilder();
			 pricesPerUnit=new StringBuilder();
		     additionalClrRunChrgCode = "";
		     additionalColorPriceVal = "";
	         additionalColorCode     = "";
	         pnumMap=new HashMap<String, String>();
	         colorSet=new HashSet<String>();
	         firstValue="";
	         repeatRows.clear();
	       return finalResult;
		}catch(Exception e){
			_LOGGER.error("Error while Processing excel sheet "+e.getMessage());
			return finalResult;
		}finally{
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet "+e.getMessage());
	
			}
				_LOGGER.info("Complted processing of excel sheet ");
				_LOGGER.info("Total no of product:"+numOfProductsSuccess.size() );
		}
		
	}

	public boolean isRepeateColumn(int columnIndex){
		
		if(columnIndex != 1 && columnIndex != 3 && columnIndex != 4 && columnIndex != 14 && columnIndex != 25 &&columnIndex != 26 && columnIndex !=27 && columnIndex != 28 && columnIndex != 29 && columnIndex != 30  
				&& columnIndex != 31&&columnIndex != 32 && columnIndex !=33 && columnIndex != 34 && columnIndex != 35 && columnIndex != 36 && columnIndex != 37
				&& columnIndex != 38&&columnIndex != 39 && columnIndex !=40 && columnIndex != 41 && columnIndex != 42 && columnIndex != 43
				
				){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	public static String[] getValuesOfArray(String data,String delimiter){
		   if(!StringUtils.isEmpty(data)){
			   return data.split(delimiter);
		   }
		   return null;
	   }
	public static String removeSpecialChar(String tempValue){
		tempValue=tempValue.replaceAll("(</p>|<p>|&rdquo;|&nbsp;|&ldquo;|<span style=color: #ff0000; font-size: small;>|<span style=color: #ff0000;>|</span style=color: #ff0000;>|<em>|</em>|</strong>|<strong>|</span>|<span>|<p class=p1>)", "");
		tempValue=tempValue.replaceAll("\\(","");
		tempValue=tempValue.replaceAll("\\)","");
	return tempValue;

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

	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}

	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}

	public LookupRestService getLookupRestServiceObj() {
		return lookupRestServiceObj;
	}

	public void setLookupRestServiceObj(LookupRestService lookupRestServiceObj) {
		this.lookupRestServiceObj = lookupRestServiceObj;
	}
	
	public EdwardsGarmentAttributeParser getEdwardsGarmentAttributeParser() {
		return edwardsGarmentAttributeParser;
	}

	public void setEdwardsGarmentAttributeParser(
			EdwardsGarmentAttributeParser edwardsGarmentAttributeParser) {
		this.edwardsGarmentAttributeParser = edwardsGarmentAttributeParser;
	}
	
	
}
