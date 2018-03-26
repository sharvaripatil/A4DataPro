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
import org.springframework.util.StringUtils;

import parser.cb.CBAttributeParser;
import parser.cutter.CBColorProductNumberParser;
import parser.cutter.CutterBuckMaterialParser;
import parser.cutter.CutterBuckPriceGridParser;
import parser.cutter.CutterBuckSheetParser;
import parser.cutter.CutterBuckSizeParser;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.Size;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class CbMapping implements IExcelParser{

	private static final Logger _LOGGER = Logger
			.getLogger(CbMapping.class);

	private LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;
	private PostServiceImpl postServiceImpl;  
	private ProductDao productDaoObj;
	private CutterBuckMaterialParser cutterBuckMaterialParserObj;
	private CBColorProductNumberParser cbColorProductNumberObj;
	private CutterBuckSizeParser cutterBuckSizeParserObj;
	private CutterBuckPriceGridParser cutterBuckPriceObj;
	private CutterBuckSheetParser cutterBuckSheetObj;
	private CBAttributeParser cbAttribute;

	public String readExcel(String accessToken, Workbook workbook,
			Integer asiNumber, int batchId, String environmentType) {
		int columnIndex = 0;

		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		Set<String> productXids = new HashSet<String>();
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		ProductConfigurations productConfigObj = new ProductConfigurations();
		List<Origin> listOfOrigin   = new ArrayList<Origin>();
		List<Image> listOfImage   = new ArrayList<Image>();
		Image imgObj=new Image();
		List<Material> listOfMaterial = new ArrayList<>();
	    List<Color> colorList = new ArrayList<Color>();
	    List<String> repeatRows = new ArrayList<>();
		List<String> colorsList = new ArrayList<>(); 
		List<ProductNumber> listOfProductNo = new ArrayList<>();
		List<String> ProductNOList = new ArrayList<>();
		List<String> SKUList = new ArrayList<>();
		List<ProductSkus> ProductRelationalSKU = new ArrayList<>();

		
		Origin origin = new Origin();	
		StringBuilder Description = new StringBuilder();
		
		Product existingApiProduct = null;
		Product productExcelObj = new Product();
		String finalResult = null;
		String productId = null;
		String Image1=null;
		String Description1=null;
		String ListPrice=null;
		String ListPrice1=null;
		String colorValue =null;
		try {

			_LOGGER.info("Total sheets in excel::"
					+ workbook.getNumberOfSheets());
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = sheet.iterator();
			_LOGGER.info("Started Processing Product");

		
			String xid = null;
			Cell cell2Data = null;
			
    		while (iterator.hasNext()) {

				try {
					Row nextRow = iterator.next();
					if(nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO){
						continue;
					}
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					if(xid != null){
						productXids.add(xid);
						repeatRows.add(xid);
					}
					boolean checkXid = false;

					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
					 //   xid = null;
					    columnIndex = cell.getColumnIndex();
					    cell2Data = nextRow.getCell(0);
						if (columnIndex + 1 == 1) {
							if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
								xid = cell.getStringCellValue().trim();
							} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								xid = String.valueOf((int) cell
										.getNumericCellValue());
							} else {
								String newXID=cell2Data.toString();
								if(newXID.contains(","))
								{
									String newXIDArr[]=newXID.split(",");
									xid = newXIDArr[1].replace("\"", "").replace(")", "");
								}
								else{
									xid = newXID;
								}
							}
							checkXid = true;
						} else {
							checkXid = false;
						}
						if (checkXid) {
							try{
							
							if (!productXids.contains(xid)) {
							//	if (nextRow.getRowNum() != 7) {
									if (nextRow.getRowNum() != 1) {

									System.out
											.println("Java object converted to JSON String, written to file");
			
									
								//	productExcelObj.setPriceGrids(priceGrids);
									colorList=cbAttribute.getColorValue(colorsList);
									productConfigObj.setColors(colorList);
									
									listOfProductNo=cbAttribute.getProductNO(ProductNOList,colorsList);
									productExcelObj.setProductNumbers(listOfProductNo);
									
									ProductRelationalSKU=cbAttribute.getSKU(SKUList,ProductNOList,colorsList);
									productExcelObj.setProductRelationSkus(ProductRelationalSKU);
									
									productExcelObj.setDescription(Description.toString());
									productExcelObj.setProductConfigurations(productConfigObj);
									
									
						

									int num = postServiceImpl.postProduct(
											accessToken, productExcelObj,
											asiNumber, batchId, environmentType);
									if (num == 1) {
										numOfProductsSuccess.add("1");
									} else if (num == 0) {
										numOfProductsFailure.add("0");
									} else {

									}
									_LOGGER.info("list size>>>>>>>"
											+ numOfProductsSuccess.size());
									_LOGGER.info("Failure list size>>>>>>>"
											+ numOfProductsFailure.size());

								 	repeatRows.clear();
									priceGrids = new ArrayList<PriceGrid>();
									Description = new StringBuilder();
									colorsList = new ArrayList<>(); 
									listOfOrigin   = new ArrayList<Origin>();
									listOfProductNo = new ArrayList<>();
								    ProductNOList = new ArrayList<>();
								    SKUList = new ArrayList<>();
									productConfigObj = new ProductConfigurations();
								
								}
								if (!productXids.contains(xid)) {
									productXids.add(xid);
									repeatRows.add(xid);

								}
							  
								productExcelObj=new Product();
								existingApiProduct = postServiceImpl
										.getProduct(accessToken,
												xid, environmentType);
											
								if (existingApiProduct == null) {
									_LOGGER.info("Existing Xid is not available,product treated as new product");
									productExcelObj = new Product();
								} else {
								 	 
							    	 List<String>categoriesList=existingApiProduct.getCategories();
							    	 productExcelObj.setCategories(categoriesList);
							    	 
							    	 String Summary=existingApiProduct.getSummary();
							    	 productExcelObj.setSummary(Summary);
							    	 
							    	 
								     List<FOBPoint>fobList=existingApiProduct.getFobPoints();
							    	 productExcelObj.setFobPoints(fobList);
							    	 
									 List<ImprintMethod>imprintMethodList=existingApiProduct.getProductConfigurations().getImprintMethods();
							    	 productConfigObj.setImprintMethods(imprintMethodList);

								}
								// productExcelObj = new Product();
							}
						}catch(Exception e){
							_LOGGER.error("error in first block -1"+e.getMessage());
						}
						}

						else{
							if(productXids.contains(xid) && repeatRows.size() != 1){
								 if(isRepeateColumn(columnIndex+1)){
									 continue;
								 }
							}
						}

						switch (columnIndex + 1) {
						
					  case 1://XIDs Mapped						
						productId =  CommonUtility.getCellValueStrinOrInt(cell);
							 if(!StringUtils.isEmpty(productId)){
							 productExcelObj.setExternalProductId(productId);
					      	}else{
					      		 productId=xid;
								 productExcelObj.setExternalProductId(productId);
						      		
					      	}
							break;
						case 2://Collection

							break;
						case 3://Size

							break;
						case 4://UPC
							
							String SKUValue=CommonUtility.getCellValueStrinOrInt(cell);
							 if(!StringUtils.isEmpty(SKUValue)){
							SKUList.add(SKUValue);
							}
							break;
						case 5:// Material Number
							String ProductNoCriteria=cell.getStringCellValue();
							 if(!ProductNOList.contains(ProductNoCriteria)){
								 ProductNOList.add(ProductNoCriteria);	 
							 }	 
							
							break;
						case 6: // WHSL
						//	ListPrice=cell.getStringCellValue();

							break;
						 case 7://Color Name
							  colorValue = CommonUtility.getCellValueStrinOrInt(cell);
							   if(!colorsList.contains(colorValue)){
						       colorsList.add(colorValue);
							   }
							 
							break;
						 case 8://Label
							break;
						  case 9://Low Res Image
		                //    Image1=cell.getCellFormula();
							
							break;
						   case 10://High Res Image
						    String Image2=cell.getStringCellValue();
						    imgObj.setImageURL(Image1);
						    listOfImage.add(imgObj);
						    imgObj.setImageURL(Image2);
						    listOfImage.add(imgObj);
						    productExcelObj.setImages(listOfImage);
						
							break;
							
					       case 11://Sizes Available

								 String sizeValue=cell.getStringCellValue();
								 Size sizeObj = new Size();
								 sizeObj=cutterBuckSizeParserObj.getSizes(sizeValue);
							     productConfigObj.setSizes(sizeObj);
								
							
							break;
					        case 12://Season

							
							break;
							
	                    	case 13://Year

							
							break;
	                    	case 14://Item Description
	                         Description1=cell.getStringCellValue();   	
							 if(!StringUtils.isEmpty(Description1)){
							Description=Description.append(Description1 );
							productExcelObj.setName(Description1);
							 }
	                        
							break;
							
	                    	case 15://Style Number
	                       String PoductlevelNo=cell.getStringCellValue();
	                       productExcelObj.setAsiProdNo(PoductlevelNo);
								
							break;
							
	                    	case 16://MSRP
						//	ListPrice1=cell.getStringCellValue();

     						break;
							
	                    	case 17://Material Content
	                    	String MaterialValue=cell.getStringCellValue();
	                    	listOfMaterial = cutterBuckMaterialParserObj.getMaterialList(MaterialValue);
							productConfigObj.setMaterials(listOfMaterial);	

								
							break;
							
	                    	case 18://Attribute Description
		                     String Description2=cell.getStringCellValue();   	
		                     if(!StringUtils.isEmpty(Description2)){
							 Description=Description.append(",").append(Description2);
							 }


							break;
							
	                    	case 19://CareBleach
		                     String Description3=cell.getStringCellValue();   	
		                     if(!StringUtils.isEmpty(Description3)){
								 Description=Description.append(",").append(Description3);
							 }

							break;
							
	                    	case 20://Care Drying Instructions
			                 String Description4=cell.getStringCellValue();   	
		                     if(!StringUtils.isEmpty(Description4)){
							 Description=Description.append(",").append(Description4);

		                     }
								
							break;
							
	                    	case 21://Care Washing Instructions
				              String Description5=cell.getStringCellValue();   	
			                  if(!StringUtils.isEmpty(Description5)){
							 Description=Description.append(",").append(Description5);
			                  }
			                  
							break;
							
	                    	case 22://Country Of Origin
	                    		String originValue=cell.getStringCellValue();
	                    		if(originValue.contains("Vietnam"))
	                    		{
	                    			originValue=originValue.replace("Vietnam","Viet nam");
	                    		}
	                    		origin.setName(originValue.toUpperCase());
	                    		listOfOrigin.add(origin);
	                    		productConfigObj.setOrigins(listOfOrigin);
								
							break;

						
							
						} // end inner while loop

					}
					// set product configuration objects

					// end inner while loop

					
					
				} catch (Exception e) {
					_LOGGER.error("Error while Processing ProductId and cause :"
							+ productExcelObj.getExternalProductId()
							+ " "
							+ e.getMessage());
				}
			}
			workbook.close();

			/* 
			priceGrids = cutterBuckPriceObj.getPriceGrids(ListPrice,ListPrice1, 
			         1, "USD", "", true, "N", Description1,"",priceGrids);	
			*/
			
			colorList=cbAttribute.getColorValue(colorsList);
			productConfigObj.setColors(colorList);
			
			listOfProductNo=cbAttribute.getProductNO(ProductNOList,colorsList);
			productExcelObj.setProductNumbers(listOfProductNo);
			
			ProductRelationalSKU=cbAttribute.getSKU(SKUList,ProductNOList,colorsList);
			productExcelObj.setProductRelationSkus(ProductRelationalSKU);

			productExcelObj.setPriceGrids(priceGrids);
			productExcelObj.setDescription(Description.toString());
			productExcelObj.setProductConfigurations(productConfigObj);

			int num = postServiceImpl.postProduct(accessToken, productExcelObj,
					asiNumber, batchId, environmentType);
			if (num == 1) {
				numOfProductsSuccess.add("1");
			} else if (num == 0) {
				numOfProductsFailure.add("0");
			} else {

			}
			_LOGGER.info("list size>>>>>>" + numOfProductsSuccess.size());
			_LOGGER.info("Failure list size>>>>>>"
					+ numOfProductsFailure.size());
			finalResult = numOfProductsSuccess.size() + ","
					+ numOfProductsFailure.size();
			productDaoObj.saveErrorLog(asiNumber, batchId);

			priceGrids = new ArrayList<PriceGrid>();
			Description = new StringBuilder();
			colorsList = new ArrayList<>(); 
			listOfOrigin   = new ArrayList<Origin>();
			listOfProductNo = new ArrayList<>();
		    ProductNOList = new ArrayList<>();
		    SKUList = new ArrayList<>();
			productConfigObj = new ProductConfigurations();


			return finalResult;
		} catch (Exception e) {
			_LOGGER.error("Error while Processing excel sheet "
					+ e.getMessage());
			return finalResult;
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet "
						+ e.getMessage());

			}
			_LOGGER.info("Complted processing of excel sheet ");
			_LOGGER.info("Total no of product:" + numOfProductsSuccess.size());
		}

	}
		
	public ProductDao getProductDaoObj() {
		return productDaoObj;
	}

	public void setProductDaoObj(ProductDao productDaoObj) {
		this.productDaoObj = productDaoObj;
	}

	public CutterBuckMaterialParser getCutterBuckMaterialParserObj() {
		return cutterBuckMaterialParserObj;
	}

	public void setCutterBuckMaterialParserObj(
			CutterBuckMaterialParser cutterBuckMaterialParserObj) {
		this.cutterBuckMaterialParserObj = cutterBuckMaterialParserObj;
	}

	public PostServiceImpl getPostServiceImpl() {
		return postServiceImpl;
	}
	public void setPostServiceImpl(PostServiceImpl postServiceImpl) {
		this.postServiceImpl = postServiceImpl;
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

	public CBColorProductNumberParser getCbColorProductNumberObj() {
		return cbColorProductNumberObj;
	}

	public void setCbColorProductNumberObj(
			CBColorProductNumberParser cbColorProductNumberObj) {
		this.cbColorProductNumberObj = cbColorProductNumberObj;
	}

	public CutterBuckSizeParser getCutterBuckSizeParserObj() {
		return cutterBuckSizeParserObj;
	}

	public void setCutterBuckSizeParserObj(
			CutterBuckSizeParser cutterBuckSizeParserObj) {
		this.cutterBuckSizeParserObj = cutterBuckSizeParserObj;
	}

	public CutterBuckPriceGridParser getCutterBuckPriceObj() {
		return cutterBuckPriceObj;
	}

	public void setCutterBuckPriceObj(CutterBuckPriceGridParser cutterBuckPriceObj) {
		this.cutterBuckPriceObj = cutterBuckPriceObj;
	}

	public CutterBuckSheetParser getCutterBuckSheetObj() {
		return cutterBuckSheetObj;
	}

	public void setCutterBuckSheetObj(CutterBuckSheetParser cutterBuckSheetObj) {
		this.cutterBuckSheetObj = cutterBuckSheetObj;
	}

	public CBAttributeParser getCbAttribute() {
		return cbAttribute;
	}

	public void setCbAttribute(CBAttributeParser cbAttribute) {
		this.cbAttribute = cbAttribute;
	}

	public boolean isRepeateColumn(int columnIndex){
		
		if(columnIndex != 1 && columnIndex != 3 && columnIndex != 4 && columnIndex != 5 && columnIndex != 7 && columnIndex != 11 
				){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	
			}

