package com.a4tech.supplier.mapper;

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







import parser.bambam.BamLookupData;
import parser.bambam.BamOptionParser;
import parser.bambam.BamPriceGridParser;
import parser.bambam.BamProductAttributeParser;
import parser.bambam.BamSizeParser;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.ImprintColor;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.a4tech.util.LookupData;

public class BambamProductExcelMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(BambamProductExcelMapping.class);
	private PostServiceImpl 				postServiceImpl ;
	private ProductDao 						productDaoObj;
	private BamProductAttributeParser 		bamProductParser;
	private BamPriceGridParser 				bamPriceGridParser;
	private BamSizeParser					bamSizeParser;
	private BamLookupData					bamLookupData;
	private BamOptionParser					bamOptionParser;
	
	@Override
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		bamLookupData.loadFobPoints(String.valueOf(asiNumber), accessToken,environmentType);
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
 		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		
		StringJoiner listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringBuilder listOfPrices   = new StringBuilder();
		StringJoiner listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String priceInclude = "";
		String xid = null;
		String basePriceName = "";
		List<FOBPoint> listOfFobPoint = new ArrayList<>();
		String upchargeImprintMethodValue = "";
		String imprintSetUpcharge = "";
		String imprintSetUpchargeCode = "";
		List<ImprintMethod> listOfImprintMethods = new ArrayList<>();
		List<String> repeateProducts = new ArrayList<>();
		boolean isOptionValue = false;
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if(nextRow.getRowNum() < ApplicationConstants.CONST_INT_VALUE_THREE){
				continue;
			}
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if(xid != null){
				productXids.add(xid);
			}
			 boolean checkXid  = false;
			 String productId = getProductXid(nextRow, 0);
			 if(StringUtils.isEmpty(productId)){
				 productId = getProductXid(nextRow, 1);
			 }
			 if(repeateProducts.contains(productId)){
				 continue;
			 }
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				
				int columnIndex = cell.getColumnIndex();
				if(columnIndex + 1 == 1){
					xid = CommonUtility.getCellValueStrinOrInt(cell);
					if(StringUtils.isEmpty(xid)){
						xid = getProductXid(nextRow);
					}
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 3){
							 System.out.println("Java object converted to JSON String, written to file");
							 	productExcelObj.setPriceGrids(priceGrids);
							 	productExcelObj.setProductConfigurations(productConfigObj);
							 	productExcelObj.setFobPoints(listOfFobPoint);
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
								listOfPrices = new StringBuilder();
							    listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							    listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								productConfigObj = new ProductConfigurations();
								listOfFobPoint = new ArrayList<>();
								upchargeImprintMethodValue = "";
								imprintSetUpchargeCode = "";
								imprintSetUpcharge = "";
								listOfImprintMethods = new ArrayList<>();
								isOptionValue = false;
																
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    }
						    repeateProducts.add(xid);
						    productExcelObj = new Product();
     						 productExcelObj = postServiceImpl.getProduct(accessToken, xid, environmentType);
						     if(productExcelObj == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						    	 productConfigObj = new ProductConfigurations();
						    	 productExcelObj.setProductConfigurations(productConfigObj);
						    	 priceGrids = new ArrayList<PriceGrid>();
						    	 productExcelObj.setPriceGrids(priceGrids);
						    	 
						     }else{
						    	 productConfigObj=productExcelObj.getProductConfigurations();
						    	 productExcelObj.setAvailability(new ArrayList<>());
						    	 productExcelObj.setLineNames(new ArrayList<>());
						    	 priceGrids = new ArrayList<PriceGrid>();
						    	 productExcelObj.setPriceGrids(priceGrids);
						     }
							
					 }
				}
				
				switch (columnIndex+1) {
				case 1://xid
					  productExcelObj.setExternalProductId(xid);
					 break;
				case 2://product no
					String prdNo = cell.getStringCellValue().trim();
					productExcelObj.setAsiProdNo(prdNo);
					  break;
				case 3://name
					String prdName = cell.getStringCellValue();
					prdName = CommonUtility.getStringLimitedChars(prdName, 60);
					if(prdName.contains("\"")){
						prdName = prdName.replaceAll("\"", "");
					}
					if(prdName.contains("iPhone")){
						prdName = prdName.replaceAll("iPhone", "");
					}
					if(prdName.contains("”")){
						prdName = prdName.replaceAll("”", "");
					}
					basePriceName = prdName;
					productExcelObj.setName(prdName);	
				    break;
				case 4://description
					 String desc = cell.getStringCellValue();
					 desc = desc.replaceAll("’", "");
					 if(desc.contains("iPhone")){
						 desc = desc.replaceAll("iPhone", "");
					 }
					 productExcelObj.setDescription(desc.trim());
					  break;
				case 5://material
					  String material = cell.getStringCellValue();
					  if(!StringUtils.isEmpty(material)){
						  productExcelObj = bamProductParser.getProductMaterial(material, productExcelObj);
					  }
					break;
					
				case 6: //  size
					String sizes = cell.getStringCellValue();
					if(!StringUtils.isEmpty(sizes)){
						productExcelObj = bamSizeParser.getProductSizes(sizes.trim(), productExcelObj);
					}
					break;
				case 7: // imprint size
					String imprintSizeVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintSizeVal)){
						List<ImprintSize> listOfImprintSize = bamProductParser.getImprintSizes(imprintSizeVal);
						productConfigObj.setImprintSize(listOfImprintSize);
					}
					break;
				case 8: // production Time
					String prdTime = cell.getStringCellValue();
					if(!StringUtils.isEmpty(prdTime)){
						productExcelObj = bamProductParser.getProductionTimes(prdTime, productExcelObj);
					}
					break;
				case 9: // fob point
					// ignore since there is no data for this column
					break;
				case 10:// origin
					String origin = cell.getStringCellValue();
					if(!StringUtils.isEmpty(origin)){
						List<Origin> listOfOrigin = bamProductParser.getOrigins(origin);
						productConfigObj.setOrigins(listOfOrigin);
					}
					break;
				case 11://category
					String category = cell.getStringCellValue();
					if(!StringUtils.isEmpty(category)){
						List<String> listOfCategories = bamProductParser.getCategories(category);
						productExcelObj.setCategories(listOfCategories);
					}
					break;
				case 12://expiration_date
					//ignore
					break;
					
				case 13: //  quantity
				case 14:
				case 15:
				case 16: 
				case 17: 
				case 18: 
				case 19:
				case 20: 
				case 21: 
				case 22: 
					String priceQty = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(priceQty)){
						listOfQuantity.add(priceQty);
					}
					break;
					
				case 23: // price
				case 24:
				case 25:
				case 26:
				case 27:
				case 28:
				case 29:
				case 30:
				case 31:
				case 32:
					String listPrice = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(listPrice)){
						 if(!listPrice.equalsIgnoreCase("0.0")){
							 listOfPrices.append(listPrice).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						 }
						
					}
					break;
					
				case 33: // dis code
				case 34:
				case 35:
				case 36:
				case 37:
				case 38:
				case 39:
				case 40:
				case 41:
				case 42:
					String discount = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(discount)){
						listOfDiscount.add(discount);
					}
					break;
				case 43: // keywords
					String keywords = cell.getStringCellValue();
					if(!StringUtils.isEmpty(keywords)){
						 List<String> listOfKeywords = bamProductParser.getProductKeywords(keywords);
				       productExcelObj.setProductKeywords(listOfKeywords);  
					}
					
					break;
				case 44: // setUp price list
					imprintSetUpcharge = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintSetUpcharge)){
						if(imprintSetUpcharge.contains("Not applicable") || imprintSetUpcharge.contains("Not Applicable")
								|| imprintSetUpcharge.equalsIgnoreCase("Not")){
							imprintSetUpcharge = "";
						} else if(isBasePriceInclude(imprintSetUpcharge.trim())){
							priceInclude = CommonUtility.appendStrings(priceInclude, imprintSetUpcharge, " ");
							imprintSetUpcharge = "";
						} else {
							imprintSetUpcharge = retrivePriceValImprintmethod(imprintSetUpcharge);
						}
					} 
					
					break;
					
				case 45: // setUp price code
					imprintSetUpchargeCode = cell.getStringCellValue();
					
					break;
					
				case 46: //colors
					String color =cell.getStringCellValue();
					if(!StringUtils.isEmpty(color)){
						productExcelObj = bamProductParser.getProductColor(color.trim(), productExcelObj);
					}
					 break;
				case 47: // colors imprint
					String colorImprint =cell.getStringCellValue();
					if(!StringUtils.isEmpty(colorImprint)){
						ImprintColor imprintColor = bamProductParser.getProductImprintColors(colorImprint);
						productConfigObj.setImprintColors(imprintColor);
					}
					break;
				case 48: // imprint method
				String imprintMethodVal=cell.getStringCellValue();
				listOfImprintMethods  = bamProductParser.getImprintMethods(imprintMethodVal,listOfImprintMethods);
				//productConfigObj.setImprintMethods(listOfImprintMethods);
					  if(StringUtils.isEmpty(imprintMethodVal) || imprintMethodVal.contains("No Imprint") 
							         || imprintMethodVal.contains("N/A")){
						  upchargeImprintMethodValue = getImprintMethodValue(imprintMethodVal);
					  } else {
						  upchargeImprintMethodValue = imprintMethodVal;
					  }
					   
					break;
				case 49: //Imprint loc
				String imprintLocation=cell.getStringCellValue();
				   if(!StringUtils.isEmpty(imprintLocation)){
								List<ImprintLocation> listOfImprintLoc = bamProductParser
										.getProductImprintLocation(imprintLocation);
					   productConfigObj.setImprintLocation(listOfImprintLoc);
				   }
					break;
				case 50: //option1
				String option1=cell.getStringCellValue();
				if(!StringUtils.isEmpty(option1)){
					 priceInclude = bamProductParser.getPriceInclude(option1);
					 if(StringUtils.isEmpty(priceInclude)){
						 isOptionValue = true;
						 productExcelObj = bamOptionParser.getOptionSheetData(option1, productExcelObj,listOfImprintMethods);
					 }
				}
					break;
				case 51: //option2
					String option2 = cell.getStringCellValue();
					if(!StringUtils.isEmpty(option2)){
						priceInclude = bamProductParser.getPriceInclude(option2);
						 if(StringUtils.isEmpty(priceInclude)){
							 isOptionValue = true;
							 productExcelObj = bamOptionParser.getOptionSheetData(option2, productExcelObj,listOfImprintMethods);
						 }
					}
					break;
				case 52: //option3
					  String option3 = cell.getStringCellValue();
					     if(!StringUtils.isEmpty(option3)){
					    	 priceInclude = bamProductParser.getPriceInclude(option3);
							 if(StringUtils.isEmpty(priceInclude)){
								 isOptionValue = true;
								 productExcelObj = bamOptionParser.getOptionSheetData(option3, productExcelObj,listOfImprintMethods);
							 }
					     }
					break;
				case 53: // option4
					     String option4 = CommonUtility.getCellValueStrinOrInt(cell);
					    	 if(!StringUtils.isEmpty(option4)){
						    	 priceInclude = bamProductParser.getPriceInclude(option4);
								 if(StringUtils.isEmpty(priceInclude)){
									 isOptionValue = true;
									 productExcelObj = bamOptionParser.getOptionSheetData(option4, productExcelObj,listOfImprintMethods);
								 }
					     }
					break;
				case 54://fob point
					String fobValue = cell.getStringCellValue();
					if(!StringUtils.isEmpty(fobValue)){
								listOfFobPoint = bamProductParser.getFobPoints(fobValue, String.valueOf(asiNumber),
										listOfFobPoint);
					}
					break;
				case 55: // fob point
					String imprintValue = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintValue)){
						
					}
					break;
				case 56:  //fobpoint
					String fobValues = cell.getStringCellValue();
					if(!StringUtils.isEmpty(fobValues)){
						listOfFobPoint = bamProductParser.getFobPoints(fobValues, String.valueOf(asiNumber),
								listOfFobPoint);
			}
					break;
				case 57:  // catalog
					 // supplier has mention catalog is not available in ASI lookup table(mediation)
					// ignore this column
					break;				
							
			}  // end inner while loop
					 
		}
				productExcelObj.setPriceType("L");
				String qurFlag = "n"; // by default for testing purpose
				priceGrids = productExcelObj.getPriceGrids();
				if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
					priceGrids = bamPriceGridParser.getBasePriceGrids(listOfPrices.toString(), 
							listOfQuantity.toString(), listOfDiscount.toString(), "USD",
							         priceInclude, true, qurFlag, basePriceName,"",priceGrids);	
				} else{
					priceGrids = bamPriceGridParser.getBasePriceGrids(listOfPrices.toString(), 
							listOfQuantity.toString(), listOfDiscount.toString(), "USD",
							         priceInclude, true, "y", basePriceName,"",priceGrids);	
				}
				if(!StringUtils.isEmpty(imprintSetUpcharge)){
					if(StringUtils.isEmpty(upchargeImprintMethodValue)){
						 listOfImprintMethods = getImprintMethod(productConfigObj.getImprintMethods());
						//productConfigObj.setImprintMethods(listofImprintMethods);
						upchargeImprintMethodValue = "Printed";
					}
					priceGrids = bamPriceGridParser.getUpchargePriceGrid("1", imprintSetUpcharge, imprintSetUpchargeCode, "IMMD",
							qurFlag, "USD", upchargeImprintMethodValue, "Set-up Charge", ApplicationConstants.CONST_VALUE_TYPE_OTHER, 
							ApplicationConstants.CONST_INT_VALUE_ONE, 
							"", priceGrids);
					if(!isOptionValue){
						productConfigObj.setImprintMethods(listOfImprintMethods);
					}
					if("Printed".equalsIgnoreCase(upchargeImprintMethodValue)){
						productConfigObj.setImprintMethods(listOfImprintMethods);
					}
				}
				listOfPrices = new StringBuilder();
			    listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			    listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() );		 
		}
		}
		workbook.close();
         productExcelObj.setPriceGrids(priceGrids);
        productExcelObj.setProductConfigurations(productConfigObj);
		 	productExcelObj.setPriceGrids(priceGrids);
		 	productExcelObj.setProductConfigurations(productConfigObj);
		 	productExcelObj.setFobPoints(listOfFobPoint);
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId, environmentType);
		 	if(num ==1){
		 		numOfProductsSuccess.add("1");
		 	}else if(num == 0){
		 		numOfProductsFailure.add("0");
		 	}else{
		 		
		 	}
		 	_LOGGER.info("list size>>>>>>"+numOfProductsSuccess.size());
		 	_LOGGER.info("Failure list size>>>>>>"+numOfProductsFailure.size());
	       finalResult = numOfProductsSuccess.size() + "," + numOfProductsFailure.size();
	       repeateProducts.clear();
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
   private String getImprintMethodValue(String name){
	   if(name.contains("No Imprint") || name.contains("N/A")){
		   name = "UNIMPRINTED";
	   } else if(StringUtils.isEmpty(name)){
		   name = "Printed";
	   } else {
		   
	   }
	   return name;
   }
	private String getProductXid(Row row){
		Cell xidCell =  row.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		return productXid;
	}
	private String getProductXid(Row row,int columnIndex){
		Cell xidCell =  row.getCell(columnIndex);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		return productXid;
	}
	private boolean isBasePriceInclude(String value){
		if(value.equalsIgnoreCase("Includes Entire Exterior") || value.equalsIgnoreCase("Included within parameters") ||
			value.equalsIgnoreCase("Banner Setup Included") || value.equalsIgnoreCase("Unit price includes 1 color Screen Print") ||
			value.equalsIgnoreCase("Included") || value.equalsIgnoreCase("1st design included") ||
			value.equalsIgnoreCase("Depends on decoration method selected") || 
			value.equalsIgnoreCase("Price includes free set up, no mold charge.") ||
			value.equalsIgnoreCase("Includes 1 color within parameters") ||
			value.equalsIgnoreCase("Included for 1st design") ||
			value.equalsIgnoreCase("Unit price includes 1 color/1 side")){
			return true;
       }
		return false;
	}
	private String retrivePriceValImprintmethod(String val){
		try{
			if(val.equalsIgnoreCase("$70.00; after 1st color 32.00 per color up to 5th color")){
				val = "70.00";
			} else {
				String[] prices = val.split(" ");
				val = prices[0];
				val = val.replace("$", "");
			}
			
		}catch(Exception exce){
			_LOGGER.error("unable to retrive imprintmethod setupprice value: "+exce.getMessage());
			return "";
		}
		return val;
	}
	private List<ImprintMethod> getImprintMethod(List<ImprintMethod> existingImprintMethods){
		 if(CollectionUtils.isEmpty(existingImprintMethods)){
			 existingImprintMethods = new ArrayList<>();
		 }
		 ImprintMethod imprintMethodObj = new ImprintMethod();
		 imprintMethodObj.setType("Printed");
		 imprintMethodObj.setAlias("Printed");
		 existingImprintMethods.add(imprintMethodObj);
		 return existingImprintMethods;
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
	public BamPriceGridParser getBamPriceGridParser() {
		return bamPriceGridParser;
	}

	public void setBamPriceGridParser(BamPriceGridParser bamPriceGridParser) {
		this.bamPriceGridParser = bamPriceGridParser;
	}
	public BamSizeParser getBamSizeParser() {
		return bamSizeParser;
	}

	public void setBamSizeParser(BamSizeParser bamSizeParser) {
		this.bamSizeParser = bamSizeParser;
	}
	public BamLookupData getBamLookupData() {
		return bamLookupData;
	}

	public void setBamLookupData(BamLookupData bamLookupData) {
		this.bamLookupData = bamLookupData;
	}
	public BamOptionParser getBamOptionParser() {
		return bamOptionParser;
	}

	public void setBamOptionParser(BamOptionParser bamOptionParser) {
		this.bamOptionParser = bamOptionParser;
	}



}
