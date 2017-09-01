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

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.dataStore.ProductDataStore;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Color;
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

import parser.BlueGeneration.BlueGenerationAttributeParser;
import parser.BlueGeneration.BlueGenerationPriceGridParser;

public class BlueGenerationMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(BlueGenerationMapping.class);
	
	private PostServiceImpl 				postServiceImpl;
	private ProductDao 						productDaoObj;
	private BlueGenerationPriceGridParser 	blueGenerationpriceGridParser;
	private BlueGenerationAttributeParser   blueGenerationattributeParser;
	
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
		String xid = null;
		int columnIndex=0;
		 Set<String> colorsList = new HashSet<>();
		 Set<String> sizeList = new HashSet<>();
		 String color = "";
		 String size = "";
		 String upcCode ="";
		 String priceDescription = "";
		 String priceVal = "";
		 List<ProductSkus> listProductSkus = new ArrayList<>();
		 String itemNumber = "";
		 StringBuilder priceSizeVals = new StringBuilder();
		 String basepriceVal = "";
		 int firstsize = 1;
		 List<String> basePriceSpecial = new ArrayList<>();
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
							     
						 List<Color> listOfColor =  blueGenerationattributeParser.getProductColor(colorsList);
						 if(!CollectionUtils.isEmpty(sizeList)){
							 Size sizes = blueGenerationattributeParser.getProductSize(sizeList);
							 productConfigObj.setSizes(sizes);
						 }
						 productConfigObj.setColors(listOfColor);
						     if(priceGrids.size() == 1){
						    	 priceGrids = removeBasePriceConfig(priceGrids);
						     }
							 	productExcelObj.setPriceGrids(priceGrids);
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
								ProductDataStore.clearProductColorSet();
								repeatRows.clear();
								listProductSkus = new ArrayList<>();
								
								colorsList = new HashSet<>();
								sizeList = new HashSet<>();
								itemNumber = "";
								firstsize = 1;
								basePriceSpecial = new ArrayList<>();
								priceSizeVals = new StringBuilder();
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
						    	 productExcelObj = blueGenerationattributeParser.addFobAndImprintMethodForNewProduct(productExcelObj);
						     }else{
						    	 productExcelObj= blueGenerationattributeParser.keepExistingProductData(productExcelObj);
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
				// ignore
					  break;
				case 3:// item number
					 itemNumber = cell.getStringCellValue();
					if(!StringUtils.isEmpty(itemNumber)){
						upcCode = itemNumber;
						itemNumber = getItemNumber(itemNumber);
					}
				    break;
				case 4://upc//Ignore as per client feedback
				    break;
				case 5: //name
					String name = cell.getStringCellValue();
					productExcelObj.setName(name);
					if(StringUtils.isEmpty(productExcelObj.getDescription())){
						productExcelObj.setDescription(name);
					}
					break;
				case 6: //Short Description
					priceDescription = cell.getStringCellValue();
					priceDescription = priceDescription.replaceAll("’", "'");
					break;
				case 7://netBank
					//ignore
					break;
				case 8: //retail
					priceVal = CommonUtility.getCellValueDouble(cell);
					
					break;
				case 9: // style
					String asiPrdNo = CommonUtility.getCellValueStrinOrInt(cell);
					productExcelObj.setAsiProdNo(asiPrdNo);
					break;
				case 10://size
					 size = cell.getStringCellValue();
					if(!StringUtils.isEmpty(size)){
						if(isDimensionColumnValue(size)){// this is check size required from dimension column or not
							Cell dimensionCell = nextRow.getCell(12);
							String dimensionSizeColumn = dimensionCell.getStringCellValue();
							size = getFinalSizeValue(size, dimensionSizeColumn);
						} else if(size.contains("W") || size.contains("T")){
							size = "wi";
						}
						if(!size.equals("wi")){
							sizeList.add(size.trim());
						}
					}
					break;
				case 11://coloname
					color = cell.getStringCellValue();
					if(!StringUtils.isEmpty(color)){
						colorsList.add(color.trim());
					}
					break;
				case 12://colorcode
					break;
				case 13:// dimensions(Size)
					break;
				case 14:// pc per carton
					String shippingVal = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(shippingVal)){
								ShippingEstimate shippingEsti = blueGenerationattributeParser
										.getProductShippingEstimation(shippingVal);
							productConfigObj.setShippingEstimates(shippingEsti);
					}
					break;
				 
			}  // end inner while loop
					 
		}
				productExcelObj.setPriceType("L");
				String finalCriteria = "";
				if(size.equals("wi")){
					 finalCriteria = "PRCL:"+color;
				} else {
					 finalCriteria = "Size:"+size+ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID+"PRCL:"+color;
				}
				   if(size.contains("*")){// this if loop only for special case for sizes i.e 2*12,2*OB...
				
					   if(firstsize == 1){
						   priceSizeVals.append(size);
						   basepriceVal = priceVal;
						   firstsize++;
					   } else {
						   if(basepriceVal.equals(priceVal)){
							   priceSizeVals.append(",") .append(size);
						   } else {
							   finalCriteria = "Size:"+priceSizeVals.toString();
							   if(!basePriceSpecial.contains(basepriceVal)){// avoid duplicate 
								   priceGrids = blueGenerationpriceGridParser.getBasePriceGrid(basepriceVal, "1", "P", "USD", "", true,
											false, priceSizeVals.toString(), finalCriteria, priceGrids, "", "", "");  
								   basePriceSpecial.add(basepriceVal);
							   }
							   priceSizeVals = new StringBuilder();
							   firstsize =1;// reset firstsize for another price grid
							   priceSizeVals.append(size).append(",");
						   }
					   }
				   } else {
					   priceGrids = blueGenerationpriceGridParser.getBasePriceGrid(priceVal, "1", "P", "USD", "", true,
								false, priceDescription, finalCriteria, priceGrids, "", "", itemNumber);   
				   }
					
					if(!StringUtils.isEmpty(upcCode)){
						listProductSkus = blueGenerationattributeParser.getProductSkus(color, size, 
		                        upcCode, listProductSkus);
					}
				
			    repeatRows.add(xid);
			    upcCode = "";
			}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);		 
				ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: "
				+e.getMessage()+" at column number(increament by 1)"+columnIndex);
				productDaoObj.save(apiResponse.getErrors(),
						productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);
		}
		}
		workbook.close();
		List<Color> listOfColor =  blueGenerationattributeParser.getProductColor(colorsList);
		if(!CollectionUtils.isEmpty(sizeList)){
			Size sizes = blueGenerationattributeParser.getProductSize(sizeList);
			 productConfigObj.setSizes(sizes);
		}
		 productConfigObj.setColors(listOfColor);
		 if(priceGrids.size() == 1){
	    	 priceGrids = removeBasePriceConfig(priceGrids);
	     }
		 	productExcelObj.setPriceGrids(priceGrids);
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
		if(StringUtils.isEmpty(productXid)){
		     xidCell = row.getCell(8);
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
		if (columnIndex != 3  && columnIndex != 6
				&& columnIndex != 8 && columnIndex != 10 && columnIndex != 11) {
				return ApplicationConstants.CONST_BOOLEAN_TRUE;
			}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	private String getItemNumber(String val){
		val = val.replaceAll("-", "").trim();
		if(val.length() > 14){
			//trim Solid/SOLID/print/PRINT words from given value
			if(val.contains("Solid")){
				val = CommonUtility.removeSpecificWord(val, "Solid");	
			} else if(val.contains("SOLID")){
				val = CommonUtility.removeSpecificWord(val, "SOLID");
			} else if(val.contains("print")){
				val = CommonUtility.removeSpecificWord(val, "print");
			} else if(val.contains("PRINT")){
				val = CommonUtility.removeSpecificWord(val, "PRINT");
			} else if(val.contains("STRIPE")){
				val = CommonUtility.removeSpecificWord(val, "STRIPE");
			} else if(val.contains("TIPIVO")){
				val = CommonUtility.removeSpecificWord(val, "TIPIVO");
			} else if(val.contains("TIPWHI")){
				val = CommonUtility.removeSpecificWord(val, "TIPWHI");
			} else if(val.contains("TIPNAV")){
				val = CommonUtility.removeSpecificWord(val, "TIPNAV");
			} else if(val.contains("TIPBLA")){
				val = CommonUtility.removeSpecificWord(val, "TIPBLA");
			} else if(val.contains("STPNAV")){
				val = CommonUtility.removeSpecificWord(val, "STPNAV");
			} else if(val.contains("STPNAT")){
				val = CommonUtility.removeSpecificWord(val, "STPNAT");
			} else if(val.contains("STPLIG")){
				val = CommonUtility.removeSpecificWord(val, "STPLIG");
			} else if(val.contains("STPGRN")){
				val = CommonUtility.removeSpecificWord(val, "STPGRN");
			} else if(val.contains("STPBLU")){
				val = CommonUtility.removeSpecificWord(val, "STPBLU");
			} else if(val.contains("TRMBLA")){
					val = CommonUtility.removeSpecificWord(val, "TRMBLA");
			} else if(val.contains("TRMGOL")){
					val = CommonUtility.removeSpecificWord(val, "TRMGOL");
			} else if(val.contains("TRMWHI")){
					val = CommonUtility.removeSpecificWord(val, "TRMWHI");
			} else if(val.contains("TRMGRA")){
				val = CommonUtility.removeSpecificWord(val, "TRMGRA");
		   } else if(val.contains("TRMBLK")){
			   val = CommonUtility.removeSpecificWord(val, "TRMBLK");
		   } else if(val.contains("TIPBUR")){
			   val = CommonUtility.removeSpecificWord(val, "TIPBUR");
		   }
			val = val.replaceAll("--", "-").trim();
		}
		return val;
	}
	private boolean isDimensionColumnValue(String value){
		if (value.contains("S0") || value.contains("S1") || value.contains("S2") || value.contains("W2")
				|| value.contains("W3") || value.contains("W4") || value.contains("W5")) {
			return true;
		}
		return false;
	}
	private String getFinalSizeValue(String sizeColumnValue,String dimensionSizeCoulmnVlaue){
		
		if(sizeColumnValue.contains("W")){
			sizeColumnValue = sizeColumnValue.replaceAll("W", "");
		} else {
			sizeColumnValue = sizeColumnValue.replaceAll("S", "");
		}
		sizeColumnValue= sizeColumnValue.replaceFirst("^0+(?!$)", "");
		if(!dimensionSizeCoulmnVlaue.equals("OB")){
			dimensionSizeCoulmnVlaue = dimensionSizeCoulmnVlaue.replaceAll("L", "");
			dimensionSizeCoulmnVlaue= dimensionSizeCoulmnVlaue.replaceFirst("^0+(?!$)", "");
		}
		sizeColumnValue = CommonUtility.appendStrings(sizeColumnValue, dimensionSizeCoulmnVlaue, "*");
		return sizeColumnValue;
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

	public BlueGenerationPriceGridParser getBlueGenerationpriceGridParser() {
		return blueGenerationpriceGridParser;
	}

	public void setBlueGenerationpriceGridParser(BlueGenerationPriceGridParser blueGenerationpriceGridParser) {
		this.blueGenerationpriceGridParser = blueGenerationpriceGridParser;
	}

	public BlueGenerationAttributeParser getBlueGenerationattributeParser() {
		return blueGenerationattributeParser;
	}

	public void setBlueGenerationattributeParser(BlueGenerationAttributeParser blueGenerationattributeParser) {
		this.blueGenerationattributeParser = blueGenerationattributeParser;
	}
	
}
