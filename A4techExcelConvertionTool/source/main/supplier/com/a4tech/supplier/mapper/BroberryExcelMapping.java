package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
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

import parser.broberry.BroberryProductAttributeParser;
import parser.broberry.BroberryProductMaterialParser;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.dataStore.ProductDataStore;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Price;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.Volume;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BroberryExcelMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(BroberryExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	@Autowired
	ObjectMapper mapperObj;
	private BroberryProductAttributeParser broberryProductAttributeParser;
	private BroberryProductMaterialParser broberryMaterialParserObj;

	/*public enum OPTION_SIZES {
		REG,SHT,TLL,XTL
	};*/
	public enum OPTION_SIZES {//removing extra tall as per revised mapping
		REG,SHT,TLL
	};
	
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
	
		StringBuilder FinalKeyword = new StringBuilder();
		StringBuilder AdditionalInfo = new StringBuilder();
		String AddionnalInfo1=null;
		List<Material> listOfMaterial =new ArrayList<Material>();
		List<String> productKeywords = new ArrayList<String>();
		List<String> listOfCategories = new ArrayList<String>();
		
		Volume itemWeight=new Volume();
	

		String productName = null;
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  Product existingApiProduct = null;
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  List<PriceGrid> priceGrids = null;
		  
		  List<String> repeatRows = new ArrayList<>();
		  String size=null;
		  String dimension=null;
		  String upc_no;
		  Set<String> colorSet = new HashSet<String>(); 
		  List<Color> colorList = new ArrayList<Color>();
		  HashMap<String, String>  productNumberMap=new HashMap<String, String>();
		  List<ProductNumber> pnumberList = new ArrayList<ProductNumber>();
		  String productNumber=null;
		  HashSet<String> sizeValuesSet = new HashSet<>();
		  HashSet<String> productOptionSet = new HashSet<String>(); // This Set used for product Availability
		  List<Availability> listOfAvailablity=new ArrayList<Availability>();
		  String colorValue=null;
		  String sizeValue=null;
		  String finalColorValue =null;
		  
		  String MaterialValue1=null;
		  String MaterialValue2=null;
		  String Keyword1 =null;
		  String specialCharacters = "[™®-’—,]";
		  int columnIndex = 0;
		  boolean existingFlag=false;
		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		String currentValue=null;
	    String lastValue=null;
	    String productId = null;
	    String xid = null;
	    HashMap<String, LinkedList<String>> tempMap=new HashMap<String, LinkedList<String>>();
		LinkedList<String> list=new LinkedList<String>();
		LinkedList<String> listTemp=new LinkedList<String>();
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
				if(columnIndex + 1 == 1){
					xid = CommonUtility.getCellValueStrinOrInt(cell);//getProductXid(nextRow);
					xid=xid.trim();
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					try{
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							 productExcelObj.setAdditionalProductInfo(AdditionalInfo.toString());
							 
							colorList=broberryProductAttributeParser.getColorCriteria(colorSet);
							if(!CollectionUtils.isEmpty(colorList)){
								productConfigObj.setColors(colorList);
							}
							if(!CollectionUtils.isEmpty(productNumberMap)){
								pnumberList=broberryProductAttributeParser.getProductNumer(productNumberMap);
								productExcelObj.setProductNumbers(pnumberList);
							}
							if(!CollectionUtils.isEmpty(sizeValuesSet)){
								productConfigObj.setSizes(broberryProductAttributeParser.getProductSize(new ArrayList<String>(sizeValuesSet)));
							}
								/*if(!CollectionUtils.isEmpty(productOptionSet)){
								productConfigObj.setOptions(broberryProductAttributeParser.getOptions(new ArrayList<String>(productOptionSet)));
								}	
								if(!CollectionUtils.isEmpty(productOptionSet)){
							   listOfAvailablity =broberryProductAttributeParser.getProductAvailablity(ProductDataStore.getSizesBrobery(),productOptionSet);
							 	productExcelObj.setAvailability(listOfAvailablity);
								}*/
								//////////////////////////////////////////
							if(!CollectionUtils.isEmpty(tempMap)){
								String strTemp=getOptionAvail(tempMap);
								if(strTemp.equalsIgnoreCase("NO")){
	
								}else if(strTemp.equalsIgnoreCase("OPTN")){
									if(!CollectionUtils.isEmpty(productOptionSet)){
										productConfigObj.setOptions(broberryProductAttributeParser.getOptions(new ArrayList<String>(productOptionSet),productExcelObj));
										_LOGGER.info("Options Processed");
									}
								}else if(strTemp.equalsIgnoreCase("OPTNAVAIL")){
									if(!CollectionUtils.isEmpty(productOptionSet)){
										productConfigObj.setOptions(broberryProductAttributeParser.getOptions(new ArrayList<String>(productOptionSet),productExcelObj));
									}
									if(!CollectionUtils.isEmpty(productOptionSet)){
										//listOfAvailablity =broberryProductAttributeParser.getProductAvailablity(ProductDataStore.getSizesBrobery(),productOptionSet);
										listOfAvailablity =broberryProductAttributeParser.getProductAvailablity(tempMap);
										productExcelObj.setAvailability(listOfAvailablity);
									}	
									_LOGGER.info("Options & Availablity Processed");
								}
							}
							////////////////////////////////////////////////////////////////////////
							
							  if(!StringUtils.isEmpty(MaterialValue1) || !StringUtils.isEmpty(MaterialValue2)){
								  if(MaterialValue1.toUpperCase().contains(MaterialValue2.toUpperCase())){//100% Acrylic	Acrylic
								  }else{
									  MaterialValue1=MaterialValue1.concat("%%%%").concat(MaterialValue2); 
								  }
								  listOfMaterial = broberryMaterialParserObj.getMaterialList(MaterialValue1);
								  //listOfMaterial = broberryMaterialParserObj.getMaterialList(MaterialValue2,listOfMaterial);
								  productConfigObj.setMaterials(listOfMaterial);
								  }
								 if(!StringUtils.isEmpty(productKeywords)){
								productExcelObj.setProductKeywords(productKeywords);
									}
								/*if(CollectionUtils.isEmpty(priceGrids)){
									productExcelObj.setPriceType(ApplicationConstants.CONST_PRICE_TYPE_CODE_LIST);
									priceGrids=getPriceGrids(productName);
									}
								 	productExcelObj.setPriceGrids(priceGrids);*/
								 /***************///upcharge grid faling for all existing products ,assigning QUR to both new and Existing products
								 priceGrids=getPriceGrids(productName,productExcelObj);
								
								if(!existingFlag){//existingFlag
									 productExcelObj.setPriceType(ApplicationConstants.CONST_PRICE_TYPE_CODE_LIST);
								 }
								 productExcelObj.setPriceGrids(priceGrids);
								 /***************/
								 productExcelObj.setProductConfigurations(productConfigObj);
								 	/*_LOGGER.info("Product Data : "
											+ mapperObj.writeValueAsString(productExcelObj));*/
								 	
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
								productConfigObj = new ProductConfigurations();
								
								repeatRows.clear();
								colorSet=new HashSet<String>(); 
								colorList = new ArrayList<Color>();
								AdditionalInfo=new StringBuilder();
								productNumberMap=new HashMap<String, String>();
								pnumberList=new ArrayList<ProductNumber>();
								listOfMaterial=new ArrayList<Material>();
								sizeValuesSet = new HashSet<>();
								listOfAvailablity=new ArrayList<Availability>();
								listOfCategories=new ArrayList<String>();
								FinalKeyword=new StringBuilder();
								productOptionSet=new HashSet<String>();
								sizeValuesSet = new HashSet<>();
								
						        productKeywords = new ArrayList<String>();
								ProductDataStore.clearSizesBrobery();
								tempMap=new HashMap<String, LinkedList<String>>();
								 list=new LinkedList<String>();
								 listTemp=new LinkedList<String>();
								 
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    	repeatRows.add(xid);
						    }
						    productExcelObj = new Product();
						    existingApiProduct = postServiceImpl.getProduct(accessToken, xid, environmentType); 
						     if(existingApiProduct == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						    	 existingFlag=false;
						     }else{
						    	 _LOGGER.info("Existing Xid available,Processing existing Data");
						    	 productExcelObj=broberryProductAttributeParser.getExistingProductData(existingApiProduct,existingApiProduct.getProductConfigurations());
						    	 productConfigObj=productExcelObj.getProductConfigurations();
								 existingFlag=true;
						     }
							
					 }
				}catch(Exception e){
					_LOGGER.error("error in first block -1"+e.getMessage());
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
				  productNumber=CommonUtility.getCellValueStrinOrInt(cell);
					  break;
				case 3://PRODUCT NAME
					
				    break;
				case 4://DESCRIPTION
					
				    break;
					
				case 5://COLOR
						colorValue=CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(colorValue)){
						colorSet.add(colorValue);
					}
					if(!StringUtils.isEmpty(colorValue)&&!StringUtils.isEmpty(productNumber)){
						productNumberMap.put(productNumber, colorValue);
					}
					break;
					
				case 6: //  DIMENSION
					dimension=CommonUtility.getCellValueStrinOrInt(cell);
					if(StringUtils.isEmpty(dimension)){ 
						dimension=ApplicationConstants.CONST_WORD_EMPTY;
					}
					final String valueop=dimension;
					if(Arrays.stream(OPTION_SIZES.values()).anyMatch((optionName) -> optionName.name().equals(valueop))){
						productOptionSet.add(ApplicationConstants.OPTION_MAP.get(dimension));
					}
					break;
					
				case 7://SIZES
					size=CommonUtility.getCellValueStrinOrInt(cell);
					if(StringUtils.isEmpty(size)){
						size=ApplicationConstants.CONST_WORD_EMPTY;
					}
					
					final String valueop1=dimension;
					if(Arrays.stream(OPTION_SIZES.values()).anyMatch((optionName) -> optionName.name().equals(valueop1))){
						//for (String string : Arr) {
							if(tempMap.containsKey(valueop1)){
							listTemp=tempMap.get(valueop1);
							//listTemp.add(dimension+ApplicationConstants.CONST_CHAR_SMALL_X+size);
							listTemp.add(size);
							tempMap.replace(valueop1, listTemp);
						}else{
							list=new LinkedList<String>();
							//list.add(dimension+ApplicationConstants.CONST_CHAR_SMALL_X+size);
							list.add(size);
							tempMap.put(valueop1,list);
						}
						
						
						//productOptionSet.add(ApplicationConstants.OPTION_MAP.get(dimension));
					}
					
					
					sizeValuesSet.add(dimension+ApplicationConstants.CONST_CHAR_SMALL_X+size);
					break;
					
				case 8: // UPC NO
					// As per revised mapping from client SKU has to be ignored
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
					String category=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(category)){
						 boolean flag=broberryProductAttributeParser.getCategory(category);
						 if(flag){
						 listOfCategories.add(category);
						 }
						 }
					 //productExcelObj.setCategories(listOfCategories);
					break;
				case 13://SUB CATEGORY
					String subCategory=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(subCategory)){
						 boolean flag=broberryProductAttributeParser.getCategory(subCategory);
						 if(flag){
							 listOfCategories.add(subCategory);
						 }
					 }
					//productExcelObj.setCategories(listOfCategories);
				
					 break;
				case 14://FABRIC WT
					String FabricWT=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(FabricWT)&&!FabricWT.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)){
					  itemWeight=broberryProductAttributeParser.getItemWeight(FabricWT);
					 productConfigObj.setItemWeight(itemWeight);
					 }
					
					
			     break;
				case 15://FABRIC CONTENT
				      MaterialValue1=CommonUtility.getCellValueStrinOrInt(cell);
				
					break;
				case 16://FABRICATION
					 MaterialValue2=CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(MaterialValue2)){
					 MaterialValue2=MaterialValue2.toUpperCase();
					}
					break;
				case 17://LSW1
					  AddionnalInfo1=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)&& !StringUtils.isEmpty(AddionnalInfo1)){
						  AdditionalInfo=AdditionalInfo.append("LSW:").append(AddionnalInfo1);
		             }
					break;
				case 18://SILHOUTTE2
					AddionnalInfo1=CommonUtility.getCellValueStrinOrInt(cell);
		             if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)&& !StringUtils.isEmpty(AddionnalInfo1)){
		            	 AdditionalInfo=AdditionalInfo.append("Silhoutte:").append(AddionnalInfo1);
		             }
					break;
				case 19: //SUB DEPT
					  Keyword1 = CommonUtility.getCellValueStrinOrInt(cell);
	                 if(!Keyword1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)&& !StringUtils.isEmpty(Keyword1)){
	                    FinalKeyword.append(Keyword1).append(ApplicationConstants.CONST_STRING_COMMA_SEP);
	                 }
					break;
				
				case 20: //NECKLINE3
					AddionnalInfo1=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED) && !StringUtils.isEmpty(AddionnalInfo1)){
						 AdditionalInfo=AdditionalInfo.append(",Neckline:").append(AddionnalInfo1);
					}
					 break;
				case 21: //DESIGN
					String Keyword2 = CommonUtility.getCellValueStrinOrInt(cell);
	                if(!Keyword2.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)&& !Keyword2.equalsIgnoreCase(Keyword1)){
	                	 if(!StringUtils.isEmpty(Keyword2)){
	                	FinalKeyword.append(Keyword2);
	                	 }
					}
	                String FinalKeyword1=FinalKeyword.toString();
	                String productKeywordArr[] =  FinalKeyword1.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					for (String string : productKeywordArr) {
						productKeywords.add(string);
					}
					productExcelObj.setProductKeywords(productKeywords);
					break;
				case 22: //SEASONALITY4
					AddionnalInfo1=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)&&!StringUtils.isEmpty(AddionnalInfo1)){
						 
					 AdditionalInfo=AdditionalInfo.append("Seasonality:").append(AddionnalInfo1);
						 
					 }
					break;
				case 23: //PLF5
					AddionnalInfo1=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)&&!StringUtils.isEmpty(AddionnalInfo1)){
					AdditionalInfo=AdditionalInfo.append(",PLF:").append(AddionnalInfo1);
					}
					break;
	
				case 24: //CARE INSTNS6
					AddionnalInfo1= CommonUtility.getCellValueStrinOrInt(cell);
					 if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)&&!StringUtils.isEmpty(AddionnalInfo1)){
						 AdditionalInfo=AdditionalInfo.append(",Care Instructions:").append(AddionnalInfo1);
					}
					break;
				case 25: //LONG DESC
					productName = CommonUtility.getCellValueStrinOrInt(cell);
					productName = CommonUtility.removeSpecialSymbols(productName,specialCharacters);
					int len=productName.length();
					 if(len>60){
						String strTemp=productName.substring(0, 60);
						int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
						productName=(String) strTemp.subSequence(0, lenTemp);
					}
					productExcelObj.setName(productName);
					 
					break;
				case 26: //CONS COPY
	
					String description =CommonUtility.getCellValueStrinOrInt(cell);
					description = CommonUtility.removeSpecialSymbols(description,specialCharacters);
					int length=description.length();
					 if(length>800){
						String strTemp=description.substring(0, 800);
						int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
						description=(String) strTemp.subSequence(0, lenTemp);
					}
					productExcelObj.setDescription(description);
					break;
				case 27: //SEARCH KEY

					
	
					break;
				case 28: //LOGISTICS
					//String description = cell.getStringCellValue();

	
					break;
				case 29: //FIT LENG7 
					AddionnalInfo1=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED)&&!StringUtils.isEmpty(AddionnalInfo1)){
						
					     AdditionalInfo=AdditionalInfo.append(",Fix length:").append(AddionnalInfo1);
						
					 }
					break;
				case 30: //RETAIL COPY
					//String description = cell.getStringCellValue();

					break;
				case 31: //LINING8 ,Lining:
					AddionnalInfo1=CommonUtility.getCellValueStrinOrInt(cell);
					if(!AddionnalInfo1.contains(ApplicationConstants.CONST_STRING_UNASSIGNED) &&!StringUtils.isEmpty(AddionnalInfo1)){
					 AdditionalInfo=AdditionalInfo.append(",Lining:").append(AddionnalInfo1);
				    }
					productExcelObj.setAdditionalProductInfo(AdditionalInfo.toString());
					break;
				
				}  // end inner while loop
				///sharvari fields
				///////////
					 
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
		productExcelObj.setAdditionalProductInfo(AdditionalInfo.toString());
	
		colorList=broberryProductAttributeParser.getColorCriteria(colorSet);
		if(!CollectionUtils.isEmpty(colorList)){
		productConfigObj.setColors(colorList);
		}
		
		pnumberList=broberryProductAttributeParser.getProductNumer(productNumberMap);
		if(!CollectionUtils.isEmpty(pnumberList)){
		productExcelObj.setProductNumbers(pnumberList);
		}
		if(!CollectionUtils.isEmpty(sizeValuesSet)){
		productConfigObj.setSizes(broberryProductAttributeParser.getProductSize(new ArrayList<String>(sizeValuesSet)));
		}
	/*	if(!CollectionUtils.isEmpty(productOptionSet)){
		if(productOptionSet.size()==1){	
		
		}else{ 
		
			productConfigObj.setOptions(broberryProductAttributeParser.getOptions(new ArrayList<String>(productOptionSet)));
		
		} 
		}
		if(!CollectionUtils.isEmpty(productOptionSet)){
		listOfAvailablity =broberryProductAttributeParser.getProductAvailablity(ProductDataStore.getSizesBrobery(),productOptionSet);
		productExcelObj.setAvailability(listOfAvailablity);
		}
     */
		//////////////////////////////////////////
		if(!CollectionUtils.isEmpty(tempMap)){
		String strTemp=getOptionAvail(tempMap);
		if(strTemp.equalsIgnoreCase("NO")){
				
		}else if(strTemp.equalsIgnoreCase("OPTN")){
			if(!CollectionUtils.isEmpty(productOptionSet)){
				productConfigObj.setOptions(broberryProductAttributeParser.getOptions(new ArrayList<String>(productOptionSet),productExcelObj));
		}
		}else if(strTemp.equalsIgnoreCase("OPTNAVAIL")){
			if(!CollectionUtils.isEmpty(productOptionSet)){
				productConfigObj.setOptions(broberryProductAttributeParser.getOptions(new ArrayList<String>(productOptionSet),productExcelObj));
			}
			if(!CollectionUtils.isEmpty(tempMap)){
				listOfAvailablity =broberryProductAttributeParser.getProductAvailablity(tempMap);
				//listOfAvailablity =broberryProductAttributeParser.getProductAvailablity(ProductDataStore.getSizesBrobery(),productOptionSet);
				productExcelObj.setAvailability(listOfAvailablity);
				}	
		}
		}
		////////////////////////////////////////////////////////////////////////
		if(!StringUtils.isEmpty(MaterialValue1) || !StringUtils.isEmpty(MaterialValue2)){
			  if(MaterialValue1.toUpperCase().contains(MaterialValue2.toUpperCase())){//100% Acrylic	Acrylic
			  }else{
				  MaterialValue1=MaterialValue1.concat("%%%%").concat(MaterialValue2); 
			  }
			listOfMaterial = broberryMaterialParserObj.getMaterialList(MaterialValue1);
			if(!CollectionUtils.isEmpty(listOfMaterial)){
		    productConfigObj.setMaterials(listOfMaterial);
			}
			}
		  if(!StringUtils.isEmpty(productKeywords)){
			productExcelObj.setProductKeywords(productKeywords);
		  }
		  /*if(CollectionUtils.isEmpty(priceGrids)){
			productExcelObj.setPriceType(ApplicationConstants.CONST_PRICE_TYPE_CODE_LIST);
			priceGrids=getPriceGrids(productName);
			}
		 	productExcelObj.setPriceGrids(priceGrids);*/
		 /***************///upcharge grid faling for all existing products ,assigning QUR to both new and Existing products
		 //productExcelObj.setPriceType(ApplicationConstants.CONST_PRICE_TYPE_CODE_LIST);
		 priceGrids=getPriceGrids(productName,productExcelObj); 
		 
		 if(!existingFlag){//existingFlag
			 productExcelObj.setPriceType(ApplicationConstants.CONST_PRICE_TYPE_CODE_LIST);
		 }
		 productExcelObj.setPriceGrids(priceGrids);
		 /***************/
		 productExcelObj.setProductConfigurations(productConfigObj);
		 	/*_LOGGER.info("Product Data : "
					+ mapperObj.writeValueAsString(productExcelObj));*/
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId, environmentType);
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
		
		repeatRows.clear();
		colorSet=new HashSet<String>(); 
		colorList = new ArrayList<Color>();
		AdditionalInfo=new StringBuilder();
		productNumberMap=new HashMap<String, String>();
		pnumberList=new ArrayList<ProductNumber>();
		listOfMaterial=new ArrayList<Material>();
		sizeValuesSet = new HashSet<>();
		listOfAvailablity=new ArrayList<Availability>();
		productOptionSet=new HashSet<String>();
		listOfCategories=new ArrayList<String>();
		FinalKeyword=new StringBuilder();
		ProductDataStore.clearSizesBrobery();
        AdditionalInfo= new StringBuilder();
        tempMap=new HashMap<String, LinkedList<String>>();
        list=new LinkedList<String>();
		listTemp=new LinkedList<String>();
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
		
		if(columnIndex != 1&&columnIndex != 2&&columnIndex != 5 && columnIndex != 6 && columnIndex != 7 && columnIndex != 8){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	
	public static List<PriceGrid> getPriceGrids(String basePriceName,Product productExcelObj) 
	{
		
		List<PriceGrid> newPriceGrid=new ArrayList<PriceGrid>();
		List<PriceGrid> existPriceGrid=new ArrayList<PriceGrid>();
		
		try{
			if(productExcelObj.getPriceGrids()!=null){
				existPriceGrid=productExcelObj.getPriceGrids();
			}
			
			for (PriceGrid expriceGrid : existPriceGrid) {
				newPriceGrid.add(expriceGrid);
			}
			
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
		_LOGGER.info("PriceGrid Processed");
		return newPriceGrid;
}
	
	public static String getOptionAvail( HashMap<String, LinkedList<String>> tempMap){
		String tempStr="NO";
		if(tempMap.size()>1){
			int len=tempMap.size();List<LinkedList<String>> lList = new ArrayList<LinkedList<String>>(tempMap.values());
			if(len==2){
				LinkedList<String> 	list1=new LinkedList<String>();
				LinkedList<String>  list2=new LinkedList<String>();
				list1=lList.get(0);
				list2=lList.get(1);
				
				if(org.apache.commons.collections.CollectionUtils.isEqualCollection(list1, list2)){
					return "OPTN";
				}else {
					return "OPTNAVAIL";
				}//true
				}else if(len==3){
				LinkedList<String> 	list1=new LinkedList<String>();
				LinkedList<String>  list2=new LinkedList<String>();
				LinkedList<String> 	list3=new LinkedList<String>();
				list1=lList.get(0);
				list2=lList.get(1);
				list3=lList.get(2);
				boolean flag=true;
				
				if(!org.apache.commons.collections.CollectionUtils.isEqualCollection(list1, list2)){
					flag=false;//return false here only no need to check for 3rd list
			//	return "OPTNAVAIL";
				}
				if(flag){
				if(!org.apache.commons.collections.CollectionUtils.isEqualCollection(list2, list3)){
					flag=false;
				}
				}
			
			
				if(!flag){
					return "OPTNAVAIL";
				}else{
					return "OPTN";
				}
				}
			}else if(tempMap.size()==1){
				//return false;
				return "NO";
			}
		return tempStr;
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
}
