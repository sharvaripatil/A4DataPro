package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

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
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Volume;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.TeamworkAthletic.TeamworkAthleticAttributeParser;

public class TeamworkAthleticMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(TeamworkAthleticMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private TeamworkAthleticAttributeParser teamWorkAttParser;
	
	@Override
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
			List<ImprintMethod> imprintMethodsList = new ArrayList<ImprintMethod>();
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		 
		  List<String> repeatRows = new ArrayList<>();
		  Map<String, String> colorIdMap = new HashMap<>();
 		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		
		StringJoiner listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		String xid = null;
		int columnIndex=0;
		 String productNo = "";
		 Set<String> images = new HashSet<>();
		 StringBuilder description = new StringBuilder();
		 List<String> keyWords = new ArrayList<>();
		 List<String> sizeList = new ArrayList<>();
		 Set<String> colorsList = new HashSet<>();
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
				 if (columnIndex == 1) {
						xid = getProductXid(nextRow);
						checkXid = true;
					} else {
						checkXid = false;
					}
				if(columnIndex + 1 == 1){
					xid = getProductXid(nextRow);
					checkXid = true;
				}/*else{
					checkXid = false;
				}*/
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							 System.out.println("Java object converted to JSON String, written to file");
							 
									productExcelObj.setDescription(
											CommonUtility.getStringLimitedChars(description.toString(), 800));
							 	List<String> keyWordsList = teamWorkAttParser.getProductKeywords(keyWords);
							 	Size sizeObj = teamWorkAttParser.getProductSize(sizeList);
							 	productExcelObj.setProductKeywords(keyWordsList);
							 	productConfigObj.setSizes(sizeObj);
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
								listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							    listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								productConfigObj = new ProductConfigurations();
								images = new HashSet<>();
								description = new StringBuilder();
								keyWords = new ArrayList<>();
								sizeList = new ArrayList<>();
								colorsList = new HashSet<>();
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    	repeatRows.add(xid);
						    }
						    productExcelObj = new Product();
     						 productExcelObj = postServiceImpl.getProduct(accessToken, xid, environmentType);
						     if(productExcelObj == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						    	 //productExcelObj = appaAttributeParser.getExistingProductData(productExcelObj);
						    	 productConfigObj=productExcelObj.getProductConfigurations();
						    	 productExcelObj.setAvailability(new ArrayList<>());
						    	 priceGrids = new ArrayList<PriceGrid>();
						     }
					 }
				}else{
					if(productXids.contains(xid) && repeatRows.size() != 1){
						 if(isRepeateColumn(columnIndex+1)){
							 continue;
						 }
					}
				}
				
				switch (columnIndex+1) {
				case 1://XID
					 break;
				case 4:// asiPrdNo
					String asiPrdNo = CommonUtility.getCellValueStrinOrInt(cell);
					productExcelObj.setAsiProdNo(asiPrdNo);
					  break;
				case 5://name
					String name = cell.getStringCellValue();
					  productExcelObj.setName(name);	
				    break;
				case 7://color
					String color = cell.getStringCellValue();
					if(!StringUtils.isEmpty(color)){
						colorsList.add(color);
					}
				    break;
					
				case 9://size
					String size = cell.getStringCellValue();
					if(!StringUtils.isEmpty(size)){
						size = teamWorkAttParser.getExactSizeValue(size);
						sizeList.add(size);
					}
					break;
				case 10://price
					String priceVal=CommonUtility.getCellValueDouble(cell);
					break;
				case 12: //  images
					String image = cell.getStringCellValue();
					images.add(image);// implemetation done
					break;
					
				case 24://keyword 
				case 25: // keyword
				case 26: // keyword
				case 27: //keyword 
				case 28://keyword
					String desc = cell.getStringCellValue();
					description.append(desc).append(" ");
					break;
				case 29:
				case 30://keyword
				case 31:
				case 32://keyword
				case 33://keyword
				String keyword=cell.getStringCellValue();
				keyWords.add(keyword);
					break;
				case 34://Shipping Weight
				String shippingWt=CommonUtility.getCellValueDouble(cell);
				ShippingEstimate shippingEstimate = teamWorkAttParser.getShippingEstimateValues(shippingWt);
				productConfigObj.setShippingEstimates(shippingEstimate);
				
					break;
				case 35://ItemWeight
					String itemWeightValue = CommonUtility.getCellValueDouble(cell);
					if (!StringUtils.isEmpty(itemWeightValue) && !itemWeightValue.equals("0")
							&& !itemWeightValue.equals("0.0")) {
				Volume volume = teamWorkAttParser.getItemWeightvolume(itemWeightValue);
				productConfigObj.setItemWeight(volume);
			}
							
			}  // end inner while loop
					 
		}
				productExcelObj.setPriceType("N");
				String qurFlag = "n"; // by default for testing purpose
				//String basePriceName = "Bronze,Silver,Gold";
				
			}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);		 
				ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: "
				+e.getMessage()+" at column number(increament by 1)"+columnIndex);
				productDaoObj.save(apiResponse.getErrors(),
						productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);
		}
		}
		workbook.close();
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId, environmentType);
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
		if(StringUtils.isEmpty(productXid) || "N/A".equalsIgnoreCase(productXid)){
		     xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_TWO);
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
		if(columnIndex != 4 && columnIndex != 7 && columnIndex != 9 && columnIndex != 10 
				                                &&!(columnIndex >= 12 && columnIndex <= 27)){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	
	public List<Material> getExistingProductMaterials(Product existingProduct){
		ProductConfigurations configuration = existingProduct.getProductConfigurations();
		return configuration.getMaterials();
	}
    private List<Image> getProductImages(List<Image> images){
    	  if(!CollectionUtils.isEmpty(images)){
    		  return images;
    	  } else{
    		 return new ArrayList<>();
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
	public TeamworkAthleticAttributeParser getTeamWorkAttParser() {
		return teamWorkAttParser;
	}

	public void setTeamWorkAttParser(TeamworkAthleticAttributeParser teamWorkAttParser) {
		this.teamWorkAttParser = teamWorkAttParser;
	}


}
