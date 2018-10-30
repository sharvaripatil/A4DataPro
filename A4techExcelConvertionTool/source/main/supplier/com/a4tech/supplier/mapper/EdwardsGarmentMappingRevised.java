package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import parser.EdwardsGarment.EdwardGarmentPriceGridParser;
import parser.EdwardsGarment.EdwardsGarmentAttributeParser;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Price;
import com.a4tech.product.model.PriceConfiguration;
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
import com.fasterxml.jackson.databind.ObjectMapper;

public class EdwardsGarmentMappingRevised implements IExcelParser{

	@Override
	public String readExcel(String accessToken, Workbook workbook,
			Integer asiNumber, int batchId, String environmentType) {
		// TODO Auto-generated method stub
		return null;
	}/*
	
	private static final Logger _LOGGER = Logger.getLogger(EdwardsGarmentMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;
	
	private EdwardsGarmentAttributeParser edwardsGarmentAttributeParser;
	private EdwardGarmentPriceGridParser edwardGarmentPriceGridparser;
	@Autowired
	ObjectMapper mapperObj;
	
	@SuppressWarnings("unused")
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
		
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
		  Dimension finalDimensionObj=new Dimension();
		  StringBuilder listOfQuantity = new StringBuilder();
		  StringBuilder listOfPrices = new StringBuilder();
		  StringBuilder priceIncludes = new StringBuilder();
		  StringBuilder pricesPerUnit = new StringBuilder();
		
			List<Material> materiallist = new ArrayList<Material>();	
			Set<String> setImages= new HashSet();
			
			 String xid = null;
			 
		try{
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		String size1="";
		String size2="";
		String productName = null;
		String cartonL = null;
		String cartonW = null;
		String cartonH = null;
		Cell cell2Data = null;
		Product existingApiProduct = null;
		int columnIndex = 0;
		HashSet<String>  colorSet = new HashSet<>();
		String firstValue="";
		Set<String> setSizes= new HashSet();
		Set<String> skuSet= new HashSet();
		HashSet<String> priceSet= new HashSet();
		
		HashMap<String , HashSet<String>> priceMap=new HashMap<String, HashSet<String>>();
		HashMap<String , HashSet<String>> availMap=new HashMap<String, HashSet<String>>();
		
		String stockValue="";
		int sizeCount=1;
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
				 cell2Data =  nextRow.getCell(6);
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
							//set sku here
							 if(!CollectionUtils.isEmpty(skuSet)){
								 String criteriaTwo="Standard & Numbered";
								 if(sizeCount==1){
									 criteriaTwo="Apparel-Waist/Inseam";
									 }
								 
							List<ProductSkus>	 listProductSkus=edwardsGarmentAttributeParser.getProductSkus(new ArrayList<String>(skuSet),"Product Color",criteriaTwo);
							 productExcelObj.setProductRelationSkus(listProductSkus);
							 }
							//process colors here
							 
								if(!CollectionUtils.isEmpty(colorSet)){
									List<Color> colorList=edwardsGarmentAttributeParser.getProductColors(new ArrayList<String>(colorSet));
									productConfigObj.setColors(colorList);
								}
								if(!CollectionUtils.isEmpty(availMap)){
									HashMap<String , HashSet<String>> availMapTemp=new HashMap<String, HashSet<String>>();
									availMapTemp=(HashMap)availMap.clone();
							boolean flag =edwardsGarmentAttributeParser.getAvailibilityStatus(availMapTemp);
							
							if(flag){
							List<Availability> listOfAvailablity =edwardsGarmentAttributeParser.getProductAvailablity(availMap);
							productExcelObj.setAvailability(listOfAvailablity);
							_LOGGER.info("Availability done for product:"+productExcelObj.getExternalProductId());
							}else{
								_LOGGER.info("No Availability detected for product:"+productExcelObj.getExternalProductId());
							}
								}
								 if(!CollectionUtils.isEmpty(setSizes)){
									 Apparel apparelObj = new Apparel();
									 Size sizeObj=new Size();
									 List<Value> listOfValue = edwardsGarmentAttributeParser.getApparelValuesObj(new ArrayList<String>(setSizes));
									 if(sizeCount==1){
										apparelObj.setType("Apparel-Waist/Inseam");
									 }else{
										 apparelObj.setType("Standard & Numbered");
									 }
									 if(!CollectionUtils.isEmpty(listOfValue)){
										apparelObj.setValues(listOfValue);
										sizeObj.setApparel(apparelObj);
									productConfigObj.setSizes(sizeObj);
									 }
									}
								
									if(CollectionUtils.isEmpty(productConfigObj.getImprintMethods())){
										 List<ImprintMethod> listOfImprintMethod = new ArrayList<>();
										 ImprintMethod	imprintMethodObj = new ImprintMethod();	
										imprintMethodObj.setType("UNIMPRINTED");
										imprintMethodObj.setAlias("UNIMPRINTED");
										listOfImprintMethod.add(imprintMethodObj);
										productConfigObj.setImprintMethods(listOfImprintMethod);
									}
									
							// process pring here
								priceGrids = gillStudiosPriceGridParser.getPriceGrids(listOfPrices.toString(), 
										         listOfQuantity.toString(), priceCode, "USD",
										         priceIncludesValue, true, quoteUponRequest, basePriceName,tempCriteria,pricesPerUnit.toString(),priceGrids);
								 if(!CollectionUtils.isEmpty(priceMap)){
									 List<Price> listOfPricesArr=new ArrayList<Price>();
									 priceGrids = new ArrayList<PriceGrid>();
									 PriceGrid priceGrid = new PriceGrid();
									 for (Entry<String, HashSet<String>> values : priceMap.entrySet()) {
										 
										   String priceVal= values.getKey();
										   HashSet<String> tempSet=values.getValue();
										   ArrayList<String> listOfsizes=new ArrayList<String>(tempSet);
										  //CommonUtility.getValuesOfArray(listOfsizes.toArray().toString(), ",");
										   String sizeValue = String.join(",", listOfsizes);
										   if(!StringUtils.isEmpty(sizeValue) && !sizeValue.equals("0")){
										   priceGrids = edwardGarmentPriceGridparser.getPriceGrids(priceVal,"1", "P",
													 ApplicationConstants.CONST_STRING_CURRENCY_USD,"",ApplicationConstants.CONST_BOOLEAN_TRUE, 
													 ApplicationConstants.CONST_STRING_FALSE,sizeValue,"Size",1,priceGrids);
										   }else{
											  
											   priceGrid.setCurrency(ApplicationConstants.CONST_STRING_CURRENCY_USD);
												priceGrid.setDescription("");
												priceGrid.setPriceIncludes("");
												priceGrid.setIsQUR(ApplicationConstants.CONST_BOOLEAN_FALSE);
												priceGrid.setIsBasePrice(true);
												priceGrid.setSequence(1);
												List<Price> listOfPrice = new ArrayList<Price>();
												//if (!priceGrid.getIsQUR()) {
												listOfPrice = EdwardGarmentPriceGridParser.getSinlgePrices(priceVal, "1", "P",listOfPrice);
												priceGrid.setPrices(listOfPrice);
												priceGrid.setPriceConfigurations(new ArrayList<PriceConfiguration>());
												priceGrids.add(priceGrid);
											   
										   }
										}
								}
									 
									 if(CollectionUtils.isEmpty(priceGrids)){
											priceGrids = edwardGarmentPriceGridparser.getPriceGridsQur();	
										}
								productExcelObj.setPriceType("L");
							    productExcelObj.setPriceGrids(priceGrids);
							 	productExcelObj.setProductConfigurations(productConfigObj);
							 //	if(Prod_Status = false){
							 	_LOGGER.info("Product Data : "
										+ mapperObj.writeValueAsString(productExcelObj));
								
							 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId, environmentType);
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
								colorSet=new HashSet<String>(); 
							 	setImages=new HashSet<String>();
								priceGrids = new ArrayList<PriceGrid>();
								listOfPrices = new StringBuilder();
							    listOfQuantity = new StringBuilder();
								productConfigObj = new ProductConfigurations();
								finalDimensionObj = new Dimension();
								 priceIncludes = new StringBuilder();
								 priceIncludes = new StringBuilder();
						         pricesPerUnit=new StringBuilder();
						         colorSet=new HashSet<String>();
						         setSizes= new HashSet();
						         skuSet=new HashSet<String>();
						         priceSet=new HashSet<String>();
						         priceMap=new HashMap<String, HashSet<String>>();
						         availMap=new HashMap<String, HashSet<String>>();
						         firstValue="";
						         sizeCount=1;
						 }
						 if(!productXids.contains(xid)){
						    	productXids.add(xid.trim());
						    	repeatRows.add(xid.trim());
						    }
						    existingApiProduct = postServiceImpl.getProduct(accessToken, xid=xid.replace("\t",""), environmentType);
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
				    	stockValue=CommonUtility.getCellValueStrinOrInt(cell);
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
				    	size1=CommonUtility.getCellValueStrinOrInt(cell);
				    	if(StringUtils.isEmpty(size1)){
				    		size1="";
				    	}
				    	String listPrice=getProductCellDataPrice(nextRow,12);
				    	if(StringUtils.isEmpty(listPrice)){
				    		listPrice="AAAAA";
				    	}
				    	size2 =getProductCellData(nextRow,9);
				    	String colorValueTemp=getProductCellData(nextRow,21);
				    	if(StringUtils.isEmpty(colorValueTemp)){
				    		colorValueTemp="AAAAA";
				    	}
				    	if(StringUtils.isEmpty(stockValue)){
				    		stockValue="BBBBB";
				    	}
				    	if(!StringUtils.isEmpty(size1) && (!StringUtils.isEmpty(size2))){
							String tempStr="";
							size1=size1.trim();
							size2=size2.trim();
							 if(checkSize2(size2)){
								 sizeCount++;
								tempStr=size1+size2;
								tempStr=tempStr.replaceAll(" ","");
								setSizes.add(tempStr);
								skuSet.add(tempStr+"_____"+colorValueTemp+"_____"+stockValue);
								priceMap=	getPriceMap(priceMap, listPrice, tempStr);
								 if(!StringUtils.isEmpty(tempStr) && !tempStr.equals("0")){
								availMap=getAvailMap(availMap,colorValueTemp,tempStr);
								 }
								
							}else if(containsOnlyNumbers(size2)){
								//if(size1.equals(size2)){
									size1=size1+"x"+size2;
									setSizes.add(size1);
									skuSet.add(size1+"_____"+colorValueTemp+"_____"+stockValue);
									//priceSet.add(size1+"_____"+listPrice);
									priceMap=	getPriceMap(priceMap, listPrice, size1);
									 if(!StringUtils.isEmpty(size1) && !size1.equals("0")){
									availMap=getAvailMap(availMap,colorValueTemp,size1);
									 }
								
							}else{
								sizeCount++;
								setSizes.add(size1);
								skuSet.add(size1+"_____"+colorValueTemp+"_____"+stockValue);
								//priceSet.add(size1+"_____"+listPrice);
								priceMap=	getPriceMap(priceMap, listPrice, size1);
								 if(!StringUtils.isEmpty(size1) && !size1.equals("0")){
								availMap=getAvailMap(availMap,colorValueTemp,size1);
								 }
							}
						}else{
							if(!StringUtils.isEmpty(size1)){
								sizeCount++;
							setSizes.add(size1.trim());
							skuSet.add(size1+"_____"+colorValueTemp+"_____"+stockValue);
							priceMap=	getPriceMap(priceMap, listPrice, size1);
							 if(!StringUtils.isEmpty(size1) && !size1.equals("0")){
							availMap=getAvailMap(availMap,colorValueTemp,size1);
							 }
							//priceSet.add(size1+"_____"+listPrice);
							}
						}
				    	break;
				    case  10://Size2
				    	//size2=CommonUtility.getCellValueStrinOrInt(cell);
				    	if(StringUtils.isEmpty(size2)){
				    	
				    	}
				    	
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
				    	
				    case  15://MapPricing

				    	break;
				    case  16://Weight
				    	
				    	
				    	String  shippingWeightValue=CommonUtility.getCellValueStrinOrInt(cell);
				    	 if(!StringUtils.isEmpty(shippingWeightValue)){
				    	 ShippingEstimate ShipingObj=new ShippingEstimate();
						 ShipingObj =edwardsGarmentAttributeParser.getShippingEstimates("", "", "", shippingWeightValue, "",ShipingObj);
						 productConfigObj.setShippingEstimates(ShipingObj);
				    	 }
						 
				    	break;
				    case  17://Height

				    	break;
				    case  18://Length

				    	break;
				    case  19://KeyWords
				    	String keywords = CommonUtility.getCellValueStrinOrInt(cell);
						 if(!StringUtils.isEmpty(keywords)){
							 keywords=keywords.replace(" ", ",");
							 List<String> listOfKeywords = new ArrayList<String>();
						String tempKeyWrd[]=keywords.split(ApplicationConstants.CONST_DELIMITER_COMMA);
						listOfKeywords=Arrays.asList(tempKeyWrd);
						productExcelObj.setProductKeywords(listOfKeywords);
						 }
				    	break;
				    case  20://StyleDescription1 // i need to work more on this field
				    	String description =CommonUtility.getCellValueStrinOrInt(cell);
						//description = CommonUtility.removeSpecialSymbols(description,specialCharacters);
				    	//description=CommonUtility.removeRestrictSymbols(description);
				    	description = description.replaceAll("\\<.*?\\> ?", "");
				    	description=removeSpecialChar(description);
						int length=description.length();
						 if(length>800){
							String strTemp=description.substring(0, 800);
							int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
							description=(String) strTemp.subSequence(0, lenTemp);
						}
						productExcelObj.setDescription(description);
				    	break;
				    case  21://StyleDescription2
				    	String summary = CommonUtility.getCellValueStrinOrInt(cell);
						 if(!StringUtils.isEmpty(summary)){
						productExcelObj.setSummary(summary);
						 }
				    	break;
				    case  22://ColorDescription
						 String colorValue=CommonUtility.getCellValueStrinOrInt(cell);
							if(!StringUtils.isEmpty(colorValue)){
								if(colorValue.contains("-")){
									String colrArr[]=colorValue.split("-");
								}
								colorSet.add(colorValue);
							}
						 
				    	break;
				    case  23://ComponentContent
				    	String Material=cell.getStringCellValue();
						if (!StringUtils.isEmpty(Material)&& !Material.equalsIgnoreCase("")) {
							materiallist=edwardsGarmentAttributeParser.getMaterialValue(Material);						
							productConfigObj.setMaterials(materiallist);
						}
				    	break;
				    case  24://ShortDescription
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
				    case  25://HTML

				    	break;
				    case  26://ThumbName

				    	break;
				    case  27://ThumbPath

				    	break;
				    case  28://ThumbAlternateName

				    	break;
				    case  29://ImageName

				    	break;
				    case  30://ImagePath
				    	String largeImage = CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(largeImage)){
							setImages.add(largeImage);
						}
				    	break;
				    case  31://ImageAlternateName

				    	break;
				    case  32://SwatchPath

				    	break;
				    case  33://Status
				    	break;
				    case  34://PantoneColor
				    break;
				    case  35:	//HexColor
				    break;
				    case  36:	//Fit
				    break;
				    case  37:	//SizeScale
				    break;
				    case  38:	//Manufacturer
				    break;
				    case  39:	//CloseOutProduct
				    break;
				    case  40:	//Comments
				    	break;
				    case  41://UserDefined1
				    	break;
				    case  42://UserDefined2
				    	break;
				    case  43://UserDefined3
				    	break;
				    case  44://UserDefined4

				    	break;
				    case  45://UserDefined5
				    	break;
				    	
				    case 46://Width
				    	break;
				}  // end inner while loop
				
				
				if(!StringUtils.isEmpty(size1) && (!StringUtils.isEmpty(size2))){
					String tempStr="";
					size1=size1.trim();
					size2=size2.trim();
					 if(checkSize2(size2)){
						tempStr=size1+size2;
						tempStr=tempStr.replaceAll(" ","");
						setSizes.add(tempStr);
					}else if(containsOnlyNumbers(size2)){
						if(size1.equals(size2)){
							setSizes.add(size1);
						}else{
							tempStr=size1+"-"+size2;
							tempStr=tempStr.replaceAll(" ","");
							setSizes.add(tempStr);
						}
						
					}else{
						setSizes.add(size1);
					}
				}else{
					setSizes.add(size1.trim());
				}
				size1="";
				size2="";
			}// set  product configuration objects
		 // end inner while loop
			
			//ShippingEstimate // i have to work on this thing as well for empty ship obj
		 // if any configurations come they will be here
			productExcelObj.setPriceType("L");
			// i have to process  pricingover here
			 	productExcelObj.setPriceGrids(priceGrids);
			 	materiallist = new ArrayList<Material>();	
			 	criteriaFlag=false;
			 	listOfPrices=new StringBuilder();
			 	listOfQuantity=new StringBuilder();
			 	pricesPerUnit=new StringBuilder();
			 	 pricesPerUnit=new StringBuilder();
			     
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() +"at column+1="+columnIndex);		 
		}
		}
		workbook.close();	
		//productExcelObj.setPriceGrids(priceGrids);
			
		 if(!CollectionUtils.isEmpty(setImages)){
				List<Image> listOfImages = edwardsGarmentAttributeParser.getImages(new ArrayList<String>(setImages));
				productExcelObj.setImages(listOfImages);
			}
		//set sku here
		 if(!CollectionUtils.isEmpty(skuSet)){
			 String criteriaTwo="Standard & Numbered";
			 if(sizeCount==1){
				 criteriaTwo="Apparel-Waist/Inseam";
				 }
		List<ProductSkus>	 listProductSkus=edwardsGarmentAttributeParser.getProductSkus(new ArrayList<String>(skuSet),"Product Color",criteriaTwo);
		 productExcelObj.setProductRelationSkus(listProductSkus);
		 }
		//process colors here
		 
			if(!CollectionUtils.isEmpty(colorSet)){
				List<Color> colorList=edwardsGarmentAttributeParser.getProductColors(new ArrayList<String>(colorSet));
				productConfigObj.setColors(colorList);
			}
			if(!CollectionUtils.isEmpty(availMap)){
				HashMap<String , HashSet<String>> availMapTemp=new HashMap<String, HashSet<String>>();
				availMapTemp=(HashMap)availMap.clone();
				boolean flag =edwardsGarmentAttributeParser.getAvailibilityStatus(availMapTemp);
				if(flag){
				List<Availability> listOfAvailablity =edwardsGarmentAttributeParser.getProductAvailablity(availMap);
				productExcelObj.setAvailability(listOfAvailablity);
				_LOGGER.info("Availability done for product:"+productExcelObj.getExternalProductId());
				}else{
					_LOGGER.info("No Availability detected for product:"+productExcelObj.getExternalProductId());
				}
					}
			 if(!CollectionUtils.isEmpty(setSizes)){
				 Apparel apparelObj = new Apparel();
				 Size sizeObj=new Size();
				 List<Value> listOfValue = edwardsGarmentAttributeParser.getApparelValuesObj(new ArrayList<String>(setSizes));
				 if(sizeCount==1){
						apparelObj.setType("Apparel-Waist/Inseam");
					 }else{
						 apparelObj.setType("Standard & Numbered");
					 }
				 if(!CollectionUtils.isEmpty(listOfValue)){
					apparelObj.setValues(listOfValue);
					sizeObj.setApparel(apparelObj);
					productConfigObj.setSizes(sizeObj);
				 }
					
				}
			 if(CollectionUtils.isEmpty(productConfigObj.getImprintMethods())){
				 List<ImprintMethod> listOfImprintMethod = new ArrayList<>();
				 ImprintMethod	imprintMethodObj = new ImprintMethod();	
				imprintMethodObj.setType("UNIMPRINTED");
				imprintMethodObj.setAlias("UNIMPRINTED");
				listOfImprintMethod.add(imprintMethodObj);
				productConfigObj.setImprintMethods(listOfImprintMethod);
			}
			
		// process pring here
			priceGrids = gillStudiosPriceGridParser.getPriceGrids(listOfPrices.toString(), 
					         listOfQuantity.toString(), priceCode, "USD",
					         priceIncludesValue, true, quoteUponRequest, basePriceName,tempCriteria,pricesPerUnit.toString(),priceGrids);
			 if(!CollectionUtils.isEmpty(priceMap)){
				 List<Price> listOfPricesArr=new ArrayList<Price>();
				 priceGrids = new ArrayList<PriceGrid>();
				 PriceGrid priceGrid = new PriceGrid();
				 for (Entry<String, HashSet<String>> values : priceMap.entrySet()) {
					   String priceVal= values.getKey();
					   HashSet<String> tempSet=values.getValue();
					   ArrayList<String> listOfsizes=new ArrayList<String>(tempSet);
					  //CommonUtility.getValuesOfArray(listOfsizes.toArray().toString(), ",");
					   String sizeValue = String.join(",", listOfsizes);
					   if(!StringUtils.isEmpty(sizeValue) && !sizeValue.equals("0")){
					   priceGrids = edwardGarmentPriceGridparser.getPriceGrids(priceVal,"1", "P",
								 ApplicationConstants.CONST_STRING_CURRENCY_USD,"",ApplicationConstants.CONST_BOOLEAN_TRUE, 
								 ApplicationConstants.CONST_STRING_FALSE,sizeValue,"Size",1,priceGrids);
					   }else{
						  
						   priceGrid.setCurrency(ApplicationConstants.CONST_STRING_CURRENCY_USD);
							priceGrid.setDescription("");
							priceGrid.setPriceIncludes("");
							priceGrid.setIsQUR(ApplicationConstants.CONST_BOOLEAN_FALSE);
							priceGrid.setIsBasePrice(true);
							priceGrid.setSequence(1);
							List<Price> listOfPrice = null;
							//if (!priceGrid.getIsQUR()) {
							listOfPrice = EdwardGarmentPriceGridParser.getSinlgePrices(priceVal, "1", "P",listOfPrice);
							priceGrid.setPrices(listOfPrice);
							priceGrid.setPriceConfigurations(new ArrayList<PriceConfiguration>());
							priceGrids.add(priceGrid);
						   
					   }
					}
			 }
				 
				 if(CollectionUtils.isEmpty(priceGrids)){
						priceGrids = edwardGarmentPriceGridparser.getPriceGridsQur();	
					}
		    productExcelObj.setPriceGrids(priceGrids);
		 	productExcelObj.setProductConfigurations(productConfigObj);
		 	productExcelObj.setPriceType("L");
		 	_LOGGER.info("Product Data : "
					+ mapperObj.writeValueAsString(productExcelObj));
		 	//if(Prod_Status = false){
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId, environmentType);
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
			finalDimensionObj = new Dimension();
			 priceIncludes = new StringBuilder();
			 pricesPerUnit=new StringBuilder();
	         colorSet=new HashSet<String>();
	         setSizes= new HashSet();
	         skuSet=new HashSet<String>();
	         priceSet=new HashSet<String>();
	         priceMap=new HashMap<String, HashSet<String>>();
	         availMap=new HashMap<String, HashSet<String>>();
	         firstValue="";
	         sizeCount=1;
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
		if(columnIndex != 1 && columnIndex != 3 && columnIndex != 4 && 
				columnIndex != 9 && columnIndex != 10 
				&&columnIndex != 13 && columnIndex !=16 && 
				columnIndex != 22 && columnIndex != 30
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
		try{
		tempValue=tempValue.replaceAll("(</p>|<p>| <ul>|&rdquo;|&nbsp;|&ldquo;|<span style=color: #ff0000; font-size: small;>| <ul> |<li>|"
				+ "<span style=color: #ff0000;>|</span style=color: #ff0000;>|<em>|</em>|</strong>|<strong>|</span>|<span>|</li>|</ul>|"
				+ "<p class=p1>|<hr>|<STRONG>|</STRONG>|<font color=\"b5b8b9\">|</font>|</strong>|<strong>|<i>|</i>|</a>|</a>|"
				+ "<font color=\"b31b34\">|<BR>|</BR>|<br>|</br>| ¡|ñ|!|<font color=\"ffffff\">|<FONT>|</FONT>|<hr>)", "");
		tempValue=tempValue.replaceAll("\\(","");
		tempValue=tempValue.replaceAll("\\)","");
		tempValue=tempValue.replaceAll(">","");
		tempValue=tempValue.replaceAll("<","");
		tempValue=tempValue.replaceAll("\\{","");
		tempValue=tempValue.replaceAll("\\}","");
		tempValue=tempValue.replaceAll("~","");
		//tempValue=tempValue.replaceAll("//","");
		tempValue=tempValue.replace("a href=","");
		//tempValue=tempValue.replaceAll("{$base_url}","");//{$base_url}
		}catch(Exception e){
			_LOGGER.error("error for replacing description  chars"+e.getMessage());
			return tempValue;
		}
		//tempValue=tempValue.replaceAll("\\","");//{$base_url}
		//tempValue=tempValue.replaceAll("//","");//{$base_url}
		
	return tempValue;

	}
	
	public PostServiceImpl getPostServiceImpl() {
		return postServiceImpl;
	}
	
	 public static boolean containsOnlyNumbers(String str) {
	        
	        //It can't contain only numbers if it's null or empty...
	        if (str == null || str.length() == 0)
	            return false;
	        
	        for (int i = 0; i < str.length(); i++) {
	 
	            //If we find a non-digit character we return false.
	            if (!Character.isDigit(str.charAt(i)))
	                return false;
	        }
	        
	        return true;
	    }
	 public static boolean checkSize2(String str)
	 {
	     String[] words = {"T", "R", "UL","UR"};  
	     return (Arrays.asList(words).contains(str));
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
	public EdwardGarmentPriceGridParser getEdwardGarmentPriceGridparser() {
		return edwardGarmentPriceGridparser;
	}

	public void setEdwardGarmentPriceGridparser(
			EdwardGarmentPriceGridParser edwardGarmentPriceGridparser) {
		this.edwardGarmentPriceGridparser = edwardGarmentPriceGridparser;
	}
	
	public static HashMap<String, HashSet<String>> getPriceMap(HashMap<String , HashSet<String>> priceMap,String listPrice,String size){
		if(CollectionUtils.isEmpty(priceMap)){
			HashSet<String>	priceSet=new HashSet<String>();
			priceSet.add(size);
			priceMap.put(listPrice, priceSet);
		}else{
			if(priceMap.containsKey(listPrice)){
				HashSet<String> priceSetTemp= priceMap.get(listPrice);
				priceSetTemp.add(size);
				priceMap.put(listPrice, priceSetTemp);
			}else{
				HashSet<String> priceSetTemp2= new HashSet<String>();
				//priceSet=new HashSet<String>();
				priceSetTemp2.add(size);
				priceMap.put(listPrice, priceSetTemp2);
				
			}
		}
		return priceMap;
	}
	
	public static HashMap<String, HashSet<String>> getAvailMap(HashMap<String , HashSet<String>> availMap,String color,String size){
		if(CollectionUtils.isEmpty(availMap)){
			HashSet<String>	sizeSet=new HashSet<String>();
			sizeSet.add(size);
			availMap.put(color, sizeSet);
		}else{
			if(availMap.containsKey(color)){
				HashSet<String> sizeSetTemp= availMap.get(color);
				sizeSetTemp.add(size);
				availMap.put(color, sizeSetTemp);
			}else{
				HashSet<String> sizeSetTemp2= new HashSet<String>();
				//priceSet=new HashSet<String>();
				sizeSetTemp2.add(size);
				availMap.put(color, sizeSetTemp2);
				
			}
		}
		return availMap;
	}

	public static String getProductCellData(Row row,int cellNo){
		Cell xidCell =  row.getCell(cellNo);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid) || productXid.trim().equalsIgnoreCase("#N/A")){
		    // xidCell = row.getCell(1);
		     //productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
			productXid="";
		}
		return productXid;
	}
	
	public static HashMap<String,HashSet<String>> getMap(String sizeValue,String colorValue,HashMap<String,HashSet<String>> tempMap){
		
		HashSet<String> listTemp=new HashSet<String>();
		if(tempMap.containsKey(colorValue)){
			listTemp=tempMap.get(colorValue);
			//listTemp.add(dimension+ApplicationConstants.CONST_CHAR_SMALL_X+size);
			listTemp.add(sizeValue);
			tempMap.replace(colorValue, listTemp);
		}else{
			listTemp=new HashSet<String>();
			//list.add(dimension+ApplicationConstants.CONST_CHAR_SMALL_X+size);
			listTemp.add(sizeValue);
			tempMap.put(colorValue,listTemp);
		}
		return null;
		
	}
	public static String getProductCellDataPrice(Row row,int cellNo){
		Cell xidCell =  row.getCell(cellNo);
		String productXid = CommonUtility.getCellValueStrinOrDecimal(xidCell);
		if(StringUtils.isEmpty(productXid) || productXid.trim().equalsIgnoreCase("#N/A")){
		    // xidCell = row.getCell(1);
		     //productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
			productXid="";
		}
		return productXid;
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
*/}
