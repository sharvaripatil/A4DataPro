package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.dataStore.ProductDataStore;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.AvailableVariations;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Volume;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.towelSpecialties.TowelSpecAttributeParser;
import parser.towelSpecialties.TowelSpecPriceGridParser;
import parser.towelSpecialties.TowelSpecQtyAndColorMapping;

public class TowelSpecialtiesMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(TowelSpecialtiesMapping.class);
	
	private PostServiceImpl 			postServiceImpl;
	private ProductDao 					productDaoObj;
	private TowelSpecAttributeParser 	towelSpecAttributeParser;
	private TowelSpecPriceGridParser    towelSpecPriceParser;
	
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
 		try{
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		StringJoiner listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfDiscounts = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String xid = null;
		int columnIndex=0;
		 StringBuilder shippingValues = new StringBuilder();
		 String imprintMethodNameUpcharge = "";
		 List<ProductionTime> listOfProductionTime = new ArrayList<>();
		 List<ImprintMethod> listOfImprintMethod = new ArrayList<>();
		 List<ImprintSize> listOfImprintSize = new ArrayList<>();
		 String asiPrdNo = "";
		 String imprintInclude = "";
		 List<AvailableVariations> listOfAvailVariations= new ArrayList<>();
		 String parentVal = "";
		 String childVal = "";
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
				/*if(columnIndex + 1 == 1){
					xid = getProductXid(nextRow);
					checkXid = true;
				}else{
					checkXid = false;
				}*/if (columnIndex == 0){
					if(repeatRows.contains(xid)){
						checkXid = false;
					}
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							 System.out.println("Java object converted to JSON String, written to file");
							   productConfigObj.setImprintSize(listOfImprintSize);
							   productConfigObj.setProductionTime(listOfProductionTime);
							   ShippingEstimate shippingEstmationValues = towelSpecAttributeParser
										.getShippingEstimation(shippingValues.toString());
								productConfigObj.setShippingEstimates(shippingEstmationValues);
								productConfigObj.setImprintMethods(listOfImprintMethod);
									List<String> productionTimeListChildList = listOfProductionTime.stream()
											.map(ProductionTime::getBusinessDays).collect(Collectors.toList());
									List<String> imprintMethodParentList = listOfImprintMethod.stream()
											.map(ImprintMethod::getAlias).collect(Collectors.toList());
									/*List<Availability> productAvailability = towelSpecAttributeParser
											.getProductAvailablity(productionTimeListChildList,
													imprintMethodParentList);*/
									List<Availability> productAvailability = new ArrayList<>();
									Availability  availabilityObj = new Availability();
									availabilityObj.setAvailableVariations(listOfAvailVariations);
									//availabilityObj.setParentCriteria(ApplicationConstants.CONST_STRING_PRODUCT_COLOR);
									//availabilityObj.setChildCriteria(ApplicationConstants.CONST_STRING_SIZE);
									availabilityObj.setParentCriteria("Imprint Method");
									availabilityObj.setChildCriteria("Production Time");
									productAvailability.add(availabilityObj);
									productExcelObj.setAvailability(productAvailability);
								if(repeatRows.size() == 1){
									// remove sinagle base price configuration
									priceGrids = removeBasePriceConfig(priceGrids);
								}
							 	productExcelObj.setPriceGrids(priceGrids);
							 	productExcelObj.setProductConfigurations(productConfigObj);
							 	if(!StringUtils.isEmpty(productExcelObj.getName()) && !StringUtils.isEmpty(productExcelObj.getDescription())){
							 		int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId);
								 	if(num ==1){
								 		numOfProductsSuccess.add("1");
								 	}else if(num == 0){
								 		numOfProductsFailure.add("0");
								 	}else{
								 		
								 	}		
							 	}

							 	_LOGGER.info("list size>>>>>>>"+numOfProductsSuccess.size());
							 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
							 	priceGrids = new ArrayList<PriceGrid>();
								listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							    listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							    listOfDiscounts = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								productConfigObj = new ProductConfigurations();
								shippingValues = new StringBuilder();
								ProductDataStore.clearProductColorSet();
								repeatRows.clear();
								imprintMethodNameUpcharge = "";
								listOfProductionTime = new ArrayList<>();
								listOfImprintMethod = new ArrayList<>();
								listOfImprintSize = new ArrayList<>();
								asiPrdNo = "";
								imprintInclude = "";
								listOfAvailVariations= new ArrayList<>();
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
						    	 productExcelObj= towelSpecAttributeParser.keepExistingProductData(productExcelObj);
						    	 productConfigObj=productExcelObj.getProductConfigurations();
						     }
							
					 }
				}else{
					/*if(isRepeateColumn(columnIndex+1)){
						 continue;
					 }*/
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
				case 2:// prdNo
					asiPrdNo  = cell.getStringCellValue();
					 productExcelObj.setAsiProdNo(asiPrdNo);
					  break;
				case 3:// desc
					String desc = cell.getStringCellValue();
					desc = desc.replaceAll("™", "");
					if(desc.contains("asiPrdNo")){
						desc = desc.replaceAll(asiPrdNo, "");
					}
					productExcelObj.setName(desc);// if product name is not provided ,it is used as prdName
					productExcelObj.setDescription(desc);
				    break;
				case 4:
					//Ignore
				    break;
				case 5: // Base prices
				case 6: 
				case 7:
				case 8: 
				case 9: 
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
				case 16:
				case 17:
				case 18:
				case 19:
				case 20:
					String qty = TowelSpecQtyAndColorMapping.getPriceQty(columnIndex+1);
					String price = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(price)){
						listOfQuantity.add(qty);
						listOfPrices.add(price);
						listOfDiscounts.add("C"); // default discount value for this supplier
					}
					break;
				case 21://Production Time
					String productionTime = cell.getStringCellValue();
					if(!StringUtils.isEmpty(productionTime)){
						childVal = getProductionTime(productionTime);
								listOfProductionTime = towelSpecAttributeParser
										.getProductionTime(productionTime,listOfProductionTime);
								//productConfigObj.setProductionTime(listOfProductionTime);
					}
					break;
				case 22: //Imprint Methods
					String imprintMethodName = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintMethodName)){
								 listOfImprintMethod = towelSpecAttributeParser
										.getImprintMethods(imprintMethodName,listOfImprintMethod);
								if(imprintMethodName.contains("(")){
									// no need to replace character
								} else if(imprintMethodName.contains("/")){
									imprintMethodName = imprintMethodName.replaceAll("/", ",");
								} else {
									
								}
								if (imprintMethodName.contains("Blank")){
									imprintMethodName = "Unimprinted";
								}
						imprintMethodNameUpcharge = imprintMethodName; 
						parentVal = imprintMethodName;
					}
					
					break;
				case 23:// setup charges
					String setUpCharge = cell.getStringCellValue();
					if(!StringUtils.isEmpty(setUpCharge) && !setUpCharge.equalsIgnoreCase("Call")){
						//imprintInclude = row	
							Cell imprintIncludeCell = nextRow.getCell(24);
								imprintInclude = CommonUtility
										.getStringLimitedChars(imprintIncludeCell.getStringCellValue(), 100);
						productExcelObj.setProductConfigurations(productConfigObj);
						productExcelObj.setPriceGrids(priceGrids);
								productExcelObj = towelSpecAttributeParser.getUpchargesBasedOnImprintMethod(setUpCharge,
										imprintMethodNameUpcharge, imprintInclude,productExcelObj);
						if(setUpCharge.contains("Over 8,000 stitches")){
									listOfImprintMethod = towelSpecAttributeParser.getImprintMethods(
											"Embroidery digitization charge up to 8000 stitches,Embroidery digitization charge over 8000 stitches",
											listOfImprintMethod);
						}
						productConfigObj = productExcelObj.getProductConfigurations();
						priceGrids = productExcelObj.getPriceGrids();
					}
					break;
				case 25://imprint include
					
					break;
              case 26: //Imprint size
            	  String imprintSize = cell.getStringCellValue();
            	  if(!StringUtils.isEmpty(imprintSize)){
            		  listOfImprintSize = towelSpecAttributeParser.getImprintSizes(imprintSize,listOfImprintSize);
            		 // productConfigObj.setImprintSize(listOfImprintSize);
            	  }
					break;
              case 27: // additinal Info
            	  String additionalInfo = cell.getStringCellValue();
            	  productExcelObj.setAdditionalProductInfo(additionalInfo);
					break;
              case 28:
            	  break;
              case 29://shipping size
            	  String shiDimention = cell.getStringCellValue();
            	  if(!StringUtils.isEmpty(shiDimention)){
            		  shippingValues.append("shippingDimention:").append(shiDimention);
            	  }
					break;
              case 30://shipping weight
            	  String shiWe = CommonUtility.getCellValueDouble(cell);
            	  if(!StringUtils.isEmpty(shiWe)){
            		  shippingValues.append(",").append("shippingWt:").append(shiWe);
            	  }
            	  break;
              case 31:// shipping qty
            	  String shiQty = CommonUtility.getCellValueStrinOrInt(cell);
            	  if(!StringUtils.isEmpty(shiQty)){
            		  shippingValues.append(",").append("shippingQty:").append(shiQty);
            	  }
            	  break;
              case 32:
            	  break;
              case 33: // itemWeight
            	  String itemWeight = CommonUtility.getCellValueDouble(cell);
            	  if(!StringUtils.isEmpty(itemWeight)){
					  Volume volume = towelSpecAttributeParser.getItemWeightvolume(itemWeight);
            		  productConfigObj.setItemWeight(volume);
            	  }
            	  break;
              case 34://ignore
              case 35:
              case 36:
              case 37:
              case 38://ignore
            	  break;
              case 39://sku
            	  String sku = cell.getStringCellValue();
            	  if(!StringUtils.isEmpty(sku)){
            		  productExcelObj.setProductLevelSku(sku);
            	  }
            	  break;
              case 40://name
            	  String prdName = cell.getStringCellValue();
            	  if(!StringUtils.isEmpty(prdName)){
            		  prdName = getProductName(prdName,asiPrdNo);
                	  productExcelObj.setName(prdName);  
            	  }
            	  break;
              case 41:// ignore
              case 42:
              case 43:
              case 44:
              case 45:
              case 46: //ignore
            	  break;
              case 47: // colors
            	  String colorName = cell.getStringCellValue();
            	  if(!StringUtils.isEmpty(colorName)){
            		  String colorGroup = "";
            		  if(colorName.equalsIgnoreCase("Navy")){
            			  colorGroup = "Dark Blue";
            		  }
            		  List<Color> listOfColor = towelSpecAttributeParser.getProductColor(colorName, colorGroup);
            		  productConfigObj.setColors(listOfColor);
            	  }
            	  break;
			}  // end inner while loop
					 
		}
				productExcelObj.setPriceType("L");
					priceGrids = towelSpecPriceParser.getBasePriceGrid(listOfPrices.toString(),
							listOfQuantity.toString(), listOfDiscounts.toString(), "USD", "", true, false,
							imprintMethodNameUpcharge, "Imprint Method", priceGrids, "", "");
					listOfAvailVariations = towelSpecAttributeParser.getProductAvailabilityVariations(parentVal,
							childVal, listOfAvailVariations);
				//String basePriceName = "Bronze,Silver,Gold";
				listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			    listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			    listOfDiscounts = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
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
		productConfigObj.setImprintSize(listOfImprintSize);
		   productConfigObj.setProductionTime(listOfProductionTime);
		   ShippingEstimate shippingEstmationValues = towelSpecAttributeParser
					.getShippingEstimation(shippingValues.toString());
			productConfigObj.setShippingEstimates(shippingEstmationValues);
			productConfigObj.setImprintMethods(listOfImprintMethod);
			List<String> productionTimeListChildList = listOfProductionTime.stream()
					.map(ProductionTime::getBusinessDays).collect(Collectors.toList());
			List<String> imprintMethodParentList = listOfImprintMethod.stream()
					.map(ImprintMethod::getAlias).collect(Collectors.toList());
		/*	List<Availability> productAvailability = towelSpecAttributeParser
					.getProductAvailablity(productionTimeListChildList,
							imprintMethodParentList);*/
			List<Availability> productAvailability = new ArrayList<>();
			Availability  availabilityObj = new Availability();
			availabilityObj.setAvailableVariations(listOfAvailVariations);
			//availabilityObj.setParentCriteria(ApplicationConstants.CONST_STRING_PRODUCT_COLOR);
			//availabilityObj.setChildCriteria(ApplicationConstants.CONST_STRING_SIZE);
			availabilityObj.setParentCriteria("Imprint Method");
			availabilityObj.setChildCriteria("Production Time");
			productAvailability.add(availabilityObj);
			productExcelObj.setAvailability(productAvailability);
			//productExcelObj.setAvailability(productAvailability);
			if(repeatRows.size() == 1){
				// remove sinagle base price configuration
				priceGrids = removeBasePriceConfig(priceGrids);
			}
		 	productExcelObj.setPriceGrids(priceGrids);
		 	productExcelObj.setProductConfigurations(productConfigObj);
		 	if(!StringUtils.isEmpty(productExcelObj.getName()) && !StringUtils.isEmpty(productExcelObj.getDescription())){
		 		int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId);
			 	if(num ==1){
			 		numOfProductsSuccess.add("1");
			 	}else if(num == 0){
			 		numOfProductsFailure.add("0");
			 	}else{
			 		
			 	}		
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
		if(StringUtils.isEmpty(productXid) || "N/A".equalsIgnoreCase(productXid)){
		     xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}
		public boolean isRepeateColumn(int columnIndex){
		if (!(columnIndex >= 5 && columnIndex <= 20) && columnIndex != 21 && columnIndex != 22 && columnIndex != 23
				&& columnIndex != 25 && columnIndex != 26) {
				return ApplicationConstants.CONST_BOOLEAN_TRUE;
			}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
		private String getProductName(String prdName,String asiPrdNo){
			prdName = prdName.replaceAll("â„","a,");
      	  prdName = prdName.replaceAll("®","");
      	  if(prdName.contains("¢,")){
      		  prdName = prdName.replaceAll("¢,","");
      	  } else{
      		  prdName = prdName.replaceAll("¢","");
      	  }
      	  if((prdName.substring(0, 1).matches("[^A-Za-z0-9]"))){
      		  prdName = prdName.substring(1).trim();
      	  }
      	if(prdName.contains(asiPrdNo)){
  		  prdName = prdName.replaceAll(asiPrdNo, "");
  		  if(prdName.contains("(")){
  			  prdName = prdName.replaceAll("\\(.?\\)", "").trim();	
  			}
  			if(prdName.endsWith(",")){
  				prdName = prdName.substring(0, prdName.length()-1);
  			}
  	  }
      	  return prdName;
		}
		public PostServiceImpl getPostServiceImpl() {
		return postServiceImpl;
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
   private String getProductionTime(String prdTime){
	   if(prdTime.contains("week") || prdTime.contains("weeks")){
			if(prdTime.contains("-")){
				prdTime = prdTime.replaceAll("[^0-9- ]", "");
				prdTime = CommonUtility.convertProductionTimeWeekIntoDays(prdTime, "-");
			} else if(prdTime.contains("to")){
				prdTime = prdTime.replaceAll("[^0-9to ]", "");
				prdTime = CommonUtility.convertProductionTimeWeekIntoDays(prdTime, "to");
			}
		} else { //Days
			prdTime = prdTime.replaceAll("[^0-9- ]", "");
		}
		prdTime = prdTime.trim();
		return prdTime;
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
	public TowelSpecAttributeParser getTowelSpecAttributeParser() {
		return towelSpecAttributeParser;
	}
	public void setTowelSpecAttributeParser(TowelSpecAttributeParser towelSpecAttributeParser) {
		this.towelSpecAttributeParser = towelSpecAttributeParser;
	}
	public TowelSpecPriceGridParser getTowelSpecPriceParser() {
		return towelSpecPriceParser;
	}

	public void setTowelSpecPriceParser(TowelSpecPriceGridParser towelSpecPriceParser) {
		this.towelSpecPriceParser = towelSpecPriceParser;
	}
}
