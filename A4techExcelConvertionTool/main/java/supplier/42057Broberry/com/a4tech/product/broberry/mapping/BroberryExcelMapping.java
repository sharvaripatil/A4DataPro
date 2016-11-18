package com.a4tech.product.broberry.mapping;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.broberry.parser.BroberryProductAttributeParser;
import com.a4tech.product.broberry.parser.BroberryProductMaterialParser;
import com.a4tech.product.broberry.parser.BroberrySkuParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.a4tech.v2.core.model.ProductSKUConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BroberryExcelMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(BroberryExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	@Autowired
	ObjectMapper mapperObj;
	private BroberryProductAttributeParser broberryProductAttributeParser;
	private BroberryProductMaterialParser broberryMaterialParserObj;
	private BroberrySkuParser broberrySkuParserObj;

	
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
	
		StringBuilder FinalKeyword = new StringBuilder();
		StringBuilder AdditionalInfo = new StringBuilder();
		String AddionnalInfo1=null;


		List<Material> listOfMaterial =new ArrayList<Material>();
		List<String> productKeywords = new ArrayList<String>();
		List<String> listOfCategories = new ArrayList<String>();
		List<ProductSkus> ProductSkusList = new ArrayList<ProductSkus>();
		Volume itemWeight=new Volume();
		ProductSkus skuObj= new ProductSkus();
		 Set<String> skuSet = new HashSet<String>(); 
	

		String productName = null;
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  Product existingApiProduct = new Product();
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  Set<String> listOfColors = new HashSet<>();
		  String colorCustomerOderCode ="";
		  List<String> repeatRows = new ArrayList<>();
		  Map<String, String> colorIdMap = new HashMap<>();
		  String size=null;
		  String dimension=null;
		  String upc_no;
		  Set<String> colorSet = new HashSet<String>(); 
		  List<Color> colorList = new ArrayList<Color>();
		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		String currentValue=null;
	    String lastValue=null;
	    String productId = null;
	    String xid = null;
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
				
				int columnIndex = cell.getColumnIndex();
				if(columnIndex + 1 == 1){
					xid = CommonUtility.getCellValueStrinOrInt(cell);//getProductXid(nextRow);
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							colorList=broberryProductAttributeParser.getColorCriteria(colorSet);
								if(colorList!=null && !colorList.isEmpty()){
								productConfigObj.setColors(colorList);
								}
								
								 	productExcelObj.setProductConfigurations(productConfigObj);
								 	_LOGGER.info("Product Data : "
											+ mapperObj.writeValueAsString(productExcelObj));
								 	
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
								listOfColors = new HashSet<>();
								repeatRows.clear();
								colorSet=new HashSet<String>(); 
								colorList = new ArrayList<Color>();
								AdditionalInfo=new StringBuilder();
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
						    	    productExcelObj=existingApiProduct;
									productConfigObj=productExcelObj.getProductConfigurations();
								    priceGrids = productExcelObj.getPriceGrids();
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
					case 1:
						productId=CommonUtility.getCellValueStrinOrInt(cell);
						productExcelObj.setExternalProductId(productId);
						break;
			
				case 2:// Material
					
					  break;
				case 3://PRODUCT NAME
					
				    break;
				case 4://DESCRIPTION
					
				    break;
					
				case 5://COLOR
					String	colorValue=cell.getStringCellValue();
					if(!StringUtils.isEmpty(colorValue)){
						colorSet.add(colorValue);
					}
					break;
					
				case 6: //  DIMENSION
					break;
					
				case 7://SIZES
					//
					break;
					
				case 8: // UPC NO
					
				   String productRelationalSku = cell.getStringCellValue();
					 if(!StringUtils.isEmpty(productRelationalSku)){
						skuSet.add(productRelationalSku);
					  }
					
					break;
					
				case 9: //SUGG RETL
					
					break;
				case 10: // MSAP
				
					
					break;
				case 11://DEPT
				   
					break;
				//////////////////////
					//sharvari
				case 12://CATEGORY....product category
					String Category=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(Category)){
						 listOfCategories.add(Category);
					 }
				
					break;
				case 13://SUB CATEGORY
					String SubCategory=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(SubCategory)){
						 listOfCategories.add(SubCategory);
					 }
					productExcelObj.setCategories(listOfCategories);

				
					 break;
				case 14://FABRIC WT
					String FabricWT=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(FabricWT)&&!FabricWT.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)){
					  itemWeight=broberryProductAttributeParser.getItemWeight(FabricWT);
					 productConfigObj.setItemWeight(itemWeight);
					 }
					
					
			     break;
				case 15://FABRIC CONTENT
				     String MaterialValue1=cell.getStringCellValue();
					 if(!StringUtils.isEmpty(MaterialValue1)){
					 listOfMaterial = broberryMaterialParserObj.getMaterialList1(MaterialValue1);
					}
					break;
				case 16://FABRICATION
					String MaterialValue2=cell.getStringCellValue();
					if(!StringUtils.isEmpty(MaterialValue2)){
				    listOfMaterial = broberryMaterialParserObj.getMaterialList2(MaterialValue2,listOfMaterial);
				     productConfigObj.setMaterials(listOfMaterial);

					}
			
					break;
				case 17://LSW1
					  AddionnalInfo1=cell.getStringCellValue();
					 if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)){
		            	 AdditionalInfo=AdditionalInfo.append("LSW:").append(AddionnalInfo1);
		             }
				
					break;
				case 18://SILHOUTTE2
					AddionnalInfo1=cell.getStringCellValue();
		             if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)){
		            	 AdditionalInfo=AdditionalInfo.append(",Silhoutte:").append(AddionnalInfo1);
		             }
					break;
				case 19: //SUB DEPT
					 String Keyword1 = cell.getStringCellValue();
	                 if(!Keyword1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)){
	                FinalKeyword.append(Keyword1).append(ApplicationConstants.CONST_STRING_COMMA_SEP);
	                 }
	                
					break;
				
				case 20: //NECKLINE3
					AddionnalInfo1=cell.getStringCellValue();
					 if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)){
						 AdditionalInfo=AdditionalInfo.append(",Neckline:").append(AddionnalInfo1);
			         }
					 break;
				case 21: //DESIGN
					String Keyword2 = cell.getStringCellValue();
	                if(!Keyword2.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)){
	                	FinalKeyword.append(Keyword2);
					}
	                String FinalKeyword1=FinalKeyword.toString();
	                String productKeywordArr[] =  FinalKeyword1.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					for (String string : productKeywordArr) {
						productKeywords.add(string);
					}
					productExcelObj.setProductKeywords(productKeywords);
					
					break;
				case 22: //SEASONALITY4
					AddionnalInfo1=cell.getStringCellValue();
					 if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)){
					 AdditionalInfo=AdditionalInfo.append(",Seasonality:").append(AddionnalInfo1);
					 }
				
					break;
				case 23: //PLF5
					AddionnalInfo1=cell.getStringCellValue();
					 if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)){
				     AdditionalInfo=AdditionalInfo.append(",PLF:").append(AddionnalInfo1);
					}
					
					break;
	
				case 24: //CARE INSTNS6
					AddionnalInfo1=cell.getStringCellValue();
					 if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)){
					     AdditionalInfo=AdditionalInfo.append(",Care Instructions:").append(AddionnalInfo1);
						}
					
					break;
				case 25: //LONG DESC
					productName = cell.getStringCellValue();
					int len=productName.length();
					 if(len>60){
						String strTemp=productName.substring(0, 60);
						int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
						productName=(String) strTemp.subSequence(0, lenTemp);
					}
					productExcelObj.setName(productName);
					 
	
					break;
				case 26: //CONS COPY
	
					String description = cell.getStringCellValue();
					productExcelObj.setDescription(description);
					
					break;
				case 27: //SEARCH KEY

					
	
					break;
				case 28: //LOGISTICS
					//String description = cell.getStringCellValue();

	
					break;
				case 29: //FIT LENG7 
					AddionnalInfo1=cell.getStringCellValue();
					 if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)){
					     AdditionalInfo=AdditionalInfo.append(",Fix length:").append(AddionnalInfo1);
						}
					
				
					break;
				case 30: //RETAIL COPY
					//String description = cell.getStringCellValue();

					break;
				case 31: //LINING8 ,Lining:
					AddionnalInfo1=cell.getStringCellValue();
					if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)){
					 AdditionalInfo=AdditionalInfo.append(",Lining:").append(AddionnalInfo1);
				     }
					productExcelObj.setAdditionalProductInfo(AdditionalInfo.toString());
					break;
				
				}  // end inner while loop
				///sharvari fields
				///////////
					 
		}
				productExcelObj.setPriceType("N");

				
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() );		 
		}
		}
		workbook.close();
		productExcelObj.setAdditionalProductInfo(AdditionalInfo.toString());
		colorList=broberryProductAttributeParser.getColorCriteria(colorSet);
		if(colorList!=null && !colorList.isEmpty()){
		productConfigObj.setColors(colorList);
		}
		
		 skuObj=broberrySkuParserObj.getProductRelationSkus(skuSet);
		 if(skuObj!=null){
		 productConfigObj.setColors(colorList);
		 }

			productExcelObj.setProductConfigurations(productConfigObj);
		 	productExcelObj.setProductConfigurations(productConfigObj);
		 	_LOGGER.info("Product Data : "
					+ mapperObj.writeValueAsString(productExcelObj));
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
		priceGrids = new ArrayList<PriceGrid>();
		productConfigObj = new ProductConfigurations();
		listOfColors = new HashSet<>();
		listOfMaterial=new ArrayList<Material>();
		repeatRows.clear();
		colorSet=new HashSet<String>(); 
		colorList = new ArrayList<Color>();
		AdditionalInfo=new StringBuilder();
		
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
		Cell xidCell =  row.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid)){
		     xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_TWO);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}
	
	public boolean isRepeateColumn(int columnIndex){
		
		if(columnIndex != 1&&columnIndex != 2&&columnIndex != 5 && columnIndex != 6 && columnIndex != 7){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
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



	public BroberryProductAttributeParser getBroberryProductAttributeParser() {
		return broberryProductAttributeParser;
	}

	public void setBroberryProductAttributeParser(
			BroberryProductAttributeParser broberryProductAttributeParser) {
		this.broberryProductAttributeParser = broberryProductAttributeParser;
	}
	public BroberryProductMaterialParser getBroberryMaterialParserObj() {
		return broberryMaterialParserObj;
	}


	public void setBroberryMaterialParserObj(
			BroberryProductMaterialParser broberryMaterialParserObj) {
		this.broberryMaterialParserObj = broberryMaterialParserObj;
	}


	public BroberrySkuParser getBroberrySkuParserObj() {
		return broberrySkuParserObj;
	}

	public void setBroberrySkuParserObj(BroberrySkuParser broberrySkuParserObj) {
		this.broberrySkuParserObj = broberrySkuParserObj;
	}

}
