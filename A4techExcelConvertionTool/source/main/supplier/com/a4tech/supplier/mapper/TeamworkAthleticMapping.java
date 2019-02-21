package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.core.model.ErrorMessageList;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Color;
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
import com.a4tech.product.service.imple.PostServiceImpl;
import com.a4tech.supplier.dataStore.SupplierDataStore;
import com.a4tech.supplier.service.ISupplierParser;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.TeamworkAthletic.TeamworkAthleticAttributeParser;
import parser.TeamworkAthletic.TeamworkAthleticPriceGridParser;

public class TeamworkAthleticMapping implements ISupplierParser{
	
	private static final Logger _LOGGER = Logger.getLogger(TeamworkAthleticMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private TeamworkAthleticAttributeParser teamWorkAttParser;
	private TeamworkAthleticPriceGridParser   teamWorkPriceGridParser;
	
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
		 String colorVal = "";
		 String sizeVal = "";
		 String priceVal = "";
		 boolean isXidRow = false;
		 List<String> itemWeightList = new LinkedList<>();
		 int rowXid = 1;
		 Set<String> sizeAndColorList = new HashSet<>();
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if(nextRow.getRowNum() <= 2){
				continue;
			}
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if(xid != null && rowXid == 3){
				productXids.add(xid);
				//repeatRows.add(xid);
			}
			 boolean checkXid  = false;
			
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				
				 columnIndex = cell.getColumnIndex();
				 if (columnIndex == 0) {
					  if(isXidRow(nextRow)){
						  xid = getProductXid(nextRow);
						  isXidRow = true;
						  break;
					  }
						checkXid = true;
					} else {
						checkXid = false;
					}
				if(columnIndex + 1 == 1){
					if(isXidRow(nextRow)){
						  xid = getProductXid(nextRow);
						  isXidRow = true;
						  break;
					  }
					checkXid = true;
				}/*else{
					checkXid = false;
				}*/
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 4){
							 System.out.println("Java object converted to JSON String, written to file");
							 String descri = description.toString();
							 if(descri.contains("Velcro")){
								 descri = descri.replaceAll("Velcro", ""); 
							 }
							  descri = CommonUtility.getStringLimitedChars(descri, 800);
									productExcelObj.setDescription(descri);
							 	List<String> keyWordsList = teamWorkAttParser.getProductKeywords(keyWords);
							 	Size sizeObj = teamWorkAttParser.getProductSize(sizeList);
							 	List<Color> listOfProductColor = teamWorkAttParser.getProductColor(colorsList);
							 	List<Image> listOfImages = teamWorkAttParser.getImages(images);
							 	productExcelObj.setImages(listOfImages);
							 	productExcelObj.setProductKeywords(keyWordsList);
							 	productConfigObj.setSizes(sizeObj);
							 	productConfigObj.setColors(listOfProductColor);
							 	productExcelObj.setPriceType("L");
                                List<ImprintMethod> imprMethodList = productConfigObj.getImprintMethods();
                                if(CollectionUtils.isEmpty(imprMethodList)){
                                	imprMethodList = teamWorkAttParser.getImprintMethodValues("Unimprinted");
                                	productConfigObj.setImprintMethods(imprMethodList);
                                }
                                if(!CollectionUtils.isEmpty(itemWeightList)){
                                	String itemWtVal = "";
                                	if(itemWeightList.size() == 1){
                                		itemWtVal = itemWeightList.get(0);
                                	} else if(itemWeightList.size() > 1){
                                		itemWtVal = itemWeightList.get(0);
                                		itemWeightList.remove(0);
                                		String remainingItemWeights = itemWeightList.stream().collect(Collectors.joining(","));
                                		productExcelObj.setAdditionalShippingInfo("Additional Shipping Values:"+remainingItemWeights);
                                	}
                                	Volume volume = teamWorkAttParser.getItemWeightvolume(itemWtVal);
                    				productConfigObj.setItemWeight(volume);
                                }
							 	productExcelObj.setProductConfigurations(productConfigObj);
							 	if(priceGrids.size() == 1){
							 		priceGrids = removePriceConfiguration(priceGrids, sizeVal);
							 	}
							 	productExcelObj.setPriceGrids(priceGrids);
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
								colorVal = "";
								sizeVal = "";
								priceVal = "";
								itemWeightList =  new LinkedList<>();
								//rowXid = 1;
								repeatRows.clear();
								sizeAndColorList = new HashSet<>();
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    	//repeatRows.add(xid);
						    }
						    productExcelObj = new Product();
     						 productExcelObj = postServiceImpl.getProduct(accessToken, xid, environmentType);
						     if(productExcelObj == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						    	 productExcelObj = teamWorkAttParser.keepExistingProductData(productExcelObj);
						    	 productConfigObj=productExcelObj.getProductConfigurations();
						    	 productExcelObj.setAvailability(new ArrayList<>());
						    	 priceGrids = new ArrayList<PriceGrid>();
						     }
						     /*if(productXids.size() > 1){
						    	 isXidRow = true;
							     break;
						     }*/
					 }
				}else{
					if(productXids.contains(xid) && repeatRows.size() != 0){
						 if(isRepeateColumn(columnIndex+1)){
							 continue;
						 }
					}
				}
				
				switch (columnIndex+1) {
				case 1://XID
					productExcelObj.setExternalProductId(xid);
					 break;
				case 4:// asiPrdNo
					String asiPrdNo = CommonUtility.getCellValueStrinOrInt(cell);
					productNo = asiPrdNo;
					//productExcelObj.setAsiProdNo(asiPrdNo);
					  break;
				case 5://name
					String name = cell.getStringCellValue();
					if(!StringUtils.isEmpty(name)){
						productExcelObj.setName(CommonUtility.getStringLimitedChars(name, 60));
					}
				    break;
				case 7://color
					 colorVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(colorVal)){
						colorVal = colorVal.replaceAll("/", "-");
						colorsList.add(colorVal);
					}
				    break;
					
				case 9://size
					 sizeVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(sizeVal)){
						sizeVal = teamWorkAttParser.getExactSizeValue(sizeVal);
						sizeList.add(sizeVal);
					}
					break;
				case 10://price
					 priceVal=CommonUtility.getCellValueDouble(cell);
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
					description.append(desc).append(". ");
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
				case 37://ItemWeight
					String itemWeightValue = CommonUtility.getCellValueDouble(cell);
					if (!StringUtils.isEmpty(itemWeightValue) && !itemWeightValue.equals("0")
							&& !itemWeightValue.equals("0.0")) {
						if(!itemWeightList.contains(itemWeightValue)){
							itemWeightList.add(itemWeightValue);
						}
						
			}
							
			}  // end inner while loop
					 
		}
			if(!isXidRow){
				StringBuilder finalCriterai = new StringBuilder();
				if(!StringUtils.isEmpty(colorVal)){// color & Size combination
					finalCriterai.append("Size:").append(sizeVal)
					.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append("PRCL:")
					.append(colorVal);	
				} else { // only size if color is absent
					finalCriterai.append("Size:").append(sizeVal);
				}
				String finalCriteraiValue = finalCriterai.toString();
			if(!sizeAndColorList.contains(finalCriteraiValue)){
				priceGrids = teamWorkPriceGridParser.getBasePriceGrids(priceVal, "1", "P", "USD",
						"", true, "False", "", finalCriteraiValue, priceGrids,productNo);
				sizeAndColorList.add(finalCriteraiValue);
			}
				repeatRows.add(xid);
			}
				
			isXidRow = false;  
			rowXid++;
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
		String descri = description.toString();
		 if(descri.contains("Velcro")){
			 descri = descri.replaceAll("Velcro", ""); 
		 }
		  descri = CommonUtility.getStringLimitedChars(descri, 800);
				productExcelObj.setDescription(descri);
 	List<String> keyWordsList = teamWorkAttParser.getProductKeywords(keyWords);
 	Size sizeObj = teamWorkAttParser.getProductSize(sizeList);
 	List<Color> listOfProductColor = teamWorkAttParser.getProductColor(colorsList);
 	List<Image> listOfImages = teamWorkAttParser.getImages(images);
 	productExcelObj.setImages(listOfImages);
 	productExcelObj.setProductKeywords(keyWordsList);
 	productConfigObj.setSizes(sizeObj);
 	productConfigObj.setColors(listOfProductColor);
 	productExcelObj.setPriceType("L");
 	List<ImprintMethod> imprMethodList = productConfigObj.getImprintMethods();
    if(CollectionUtils.isEmpty(imprMethodList)){
    	imprMethodList = teamWorkAttParser.getImprintMethodValues("Unimprinted");
    	productConfigObj.setImprintMethods(imprMethodList);
    }
    if(!CollectionUtils.isEmpty(itemWeightList)){
    	String itemWtVal = "";
    	if(itemWeightList.size() == 1){
    		itemWtVal = itemWeightList.get(0);
    	} else if(itemWeightList.size() > 1){
    		itemWtVal = itemWeightList.get(0);
    		itemWeightList.remove(0);
    		String remainingItemWeights = itemWeightList.stream().collect(Collectors.joining(","));
    		productExcelObj.setAdditionalShippingInfo("Additional Shipping Values:"+remainingItemWeights);
    	}
    	Volume volume = teamWorkAttParser.getItemWeightvolume(itemWtVal);
		productConfigObj.setItemWeight(volume);
    }
 	productExcelObj.setProductConfigurations(productConfigObj);
 	if(priceGrids.size() == 1){
 		priceGrids = removePriceConfiguration(priceGrids, sizeVal);
 	}
 	productExcelObj.setPriceGrids(priceGrids);
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId, environmentType);
		 	if(num ==1){
		 		numOfProductsSuccess.add("1");
		 	}else if(num == 0){
		 		numOfProductsFailure.add("0");
		 	}else{
		 		
		 	}
		 	SupplierDataStore.clearProductColorSet();
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
		Cell xidCell =  row.getCell(0);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid) || "N/A".equalsIgnoreCase(productXid)){
		     xidCell = row.getCell(3);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}
	private boolean isXidRow(Row row){
		Cell xidCell =  row.getCell(8);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		boolean isxidRow = false;
		if(StringUtils.isEmpty(productXid) || "N/A".equalsIgnoreCase(productXid)){
			isxidRow = true;
		}
		return isxidRow;
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
		if(columnIndex != 4 && columnIndex != 7 && columnIndex != 9 && columnIndex != 10 & columnIndex !=37 ){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	private List<PriceGrid> removePriceConfiguration(List<PriceGrid> pricesList, String sizeVal){
		List<PriceGrid> newPricesList = new ArrayList<>();
		for (PriceGrid priceGrid : pricesList) {
			priceGrid.setPriceConfigurations(new ArrayList<>());
			priceGrid.setDescription(sizeVal);
			newPricesList.add(priceGrid);
		}
		return newPricesList;
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

	public TeamworkAthleticPriceGridParser getTeamWorkPriceGridParser() {
		return teamWorkPriceGridParser;
	}

	public void setTeamWorkPriceGridParser(TeamworkAthleticPriceGridParser teamWorkPriceGridParser) {
		this.teamWorkPriceGridParser = teamWorkPriceGridParser;
	}


}
