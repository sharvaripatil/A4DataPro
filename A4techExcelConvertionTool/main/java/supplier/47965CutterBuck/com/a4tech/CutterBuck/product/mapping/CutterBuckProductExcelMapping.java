package com.a4tech.CutterBuck.product.mapping;

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

import com.a4tech.CutterBuck.product.parser.CutterBuckMaterialParser;
import com.a4tech.CutterBuck.product.parser.CutterBuckSizeParser;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class CutterBuckProductExcelMapping implements IExcelParser{

	private static final Logger _LOGGER = Logger
			.getLogger(CutterBuckProductExcelMapping.class);

	
	private PostServiceImpl postServiceImpl;  
	private ProductDao productDaoObj;
	private  CutterBuckSizeParser cutterBuckSizeParserObj;
	private  CutterBuckMaterialParser cutterBuckMaterialParserObj;


	public String readExcel(String accessToken, Workbook workbook,
			Integer asiNumber, int batchId) {
		int columnIndex = 0;

		Set<String> productXids = new HashSet<String>();
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
	    List<String> lineNamesList=new ArrayList<String>();
		List<Material> listOfMaterial = new ArrayList<>();
		Product existingApiProduct = null;

		ProductConfigurations productConfigObj = new ProductConfigurations();
		Product productExcelObj = new Product();
		String productName = null;
		String finalResult = null;
		String productId = null;
	
		try {

			_LOGGER.info("Total sheets in excel::"
					+ workbook.getNumberOfSheets());
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = sheet.iterator();
			_LOGGER.info("Started Processing Product");

			while (iterator.hasNext()) {

				try {
					Row nextRow = iterator.next();

					if (nextRow.getRowNum() == 0)
						continue;
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					if (productId != null) {
						productXids.add(productId);
					}
					boolean checkXid = false;

					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						String xid = null;
						 columnIndex = cell.getColumnIndex();

						if (columnIndex + 1 == 1) {
							if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
								xid = cell.getStringCellValue();
							} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								xid = String.valueOf((int) cell
										.getNumericCellValue());
							} else {

							}
							checkXid = true;
						} else {
							checkXid = false;
						}
						if (checkXid) {
							if (!productXids.contains(xid)) {
								if (nextRow.getRowNum() != 1) {
									System.out
											.println("Java object converted to JSON String, written to file");
									productExcelObj
											.setProductConfigurations(productConfigObj);
									int num = postServiceImpl.postProduct(
											accessToken, productExcelObj,
											asiNumber, batchId);
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
									productConfigObj = new ProductConfigurations();

								}
								if (!productXids.contains(xid)) {
									productXids.add(xid);
									
									
								}
								productExcelObj = new Product();
								 existingApiProduct = postServiceImpl.getProduct(accessToken, xid); 
							     if(existingApiProduct == null){
							    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
							    	 productExcelObj = new Product();
							     }else{
							    	    productExcelObj=existingApiProduct;
										productConfigObj=productExcelObj.getProductConfigurations();
									    //priceGrids = productExcelObj.getPriceGrids();
							     }
							}
						}

						switch (columnIndex + 1) {
						
						case 1://XID
							
							 productId =  CommonUtility.getCellValueStrinOrInt(cell);
							 productExcelObj.setExternalProductId(productId);
						

						case 2:// Style Number
						
							String asiProdNo = null;
						    if(cell.getCellType() == Cell.CELL_TYPE_STRING){ 
						      asiProdNo = String.valueOf(cell.getStringCellValue());
						    }else if
						     (cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						      asiProdNo = String.valueOf((int)cell.getNumericCellValue());
						     }
						     productExcelObj.setAsiProdNo(asiProdNo);	
					   

							break;
						case 3:// Style Description
							productName = cell.getStringCellValue();
							int len=productName.length();
							 if(len>60){
								String strTemp=productName.substring(0, 60);
								int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
								productName=(String) strTemp.subSequence(0, lenTemp);
							}
							productExcelObj.setName(productName);
					
							break;
						case 4:// WHSL - USD
						
							break;
				
						case 5:// MSRP - USD
							

							break;

						case 6: // Sizes
						
							break;
							
						case 7://Style Long Description	
							String description =CommonUtility.getCellValueStrinOrInt(cell);
							description=description.replaceAll("~", ApplicationConstants.CONST_VALUE_TYPE_SPACE);
							int length=description.length();
							if(length>800){
								String strTemp=description.substring(0, 800);
								int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
								description=(String) strTemp.subSequence(0, lenTemp);
							}
							productExcelObj.setDescription(description);		
						
							break;
							
						case 8://Material Content
							
							String MaterialValue=cell.getStringCellValue();
							listOfMaterial = cutterBuckMaterialParserObj.getMaterialList(MaterialValue);
							productConfigObj.setMaterials(listOfMaterial);	
							
							break;
						case 9://Country of Origin	
							
							String origin = cell.getStringCellValue();
							List<Origin> listOfOrigins = new ArrayList<Origin>();
							Origin origins = new Origin();
							origins.setName(origin);
							listOfOrigins.add(origins);
							productConfigObj.setOrigins(listOfOrigins);
							
							
							
							break;
							
						case 10://Label
							String Linename=cell.getStringCellValue();
							if(Linename.contains("CBUK"))
							{
								lineNamesList.add(Linename);
							}
							else if(Linename.contains("Clique"))
							{
								lineNamesList.add(Linename);
							}
							
							productExcelObj.setLineNames(lineNamesList);

							
							break;
					
						} // end inner while loop

					}
					
					productExcelObj.setPriceType("L");
				
				
				     productId = null;

				

				} catch (Exception e) {
					_LOGGER.error("Error while Processing ProductId and cause :"
							+ productExcelObj.getExternalProductId()
							+ " "
							+ e.getMessage() +"for column"+columnIndex+1);
				}
			}
			workbook.close();

			productExcelObj.setProductConfigurations(productConfigObj);

			int num = postServiceImpl.postProduct(accessToken, productExcelObj,
					asiNumber, batchId);
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
			return finalResult;
		} catch (Exception e) {
			_LOGGER.error("Error while Processing excel sheet ,Error message: "
					+ e.getMessage());
			return finalResult;
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet, Error message: "
						+ e.getMessage());

			}
			_LOGGER.info("Complted processing of excel sheet ");
			_LOGGER.info("Total no of product:" + numOfProductsSuccess.size());
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

	public CutterBuckSizeParser getCutterBuckSizeParserObj() {
		return cutterBuckSizeParserObj;
	}

	public void setCutterBuckSizeParserObj(
			CutterBuckSizeParser cutterBuckSizeParserObj) {
		this.cutterBuckSizeParserObj = cutterBuckSizeParserObj;
	}

	public CutterBuckMaterialParser getCutterBuckMaterialParserObj() {
		return cutterBuckMaterialParserObj;
	}

	public void setCutterBuckMaterialParserObj(
			CutterBuckMaterialParser cutterBuckMaterialParserObj) {
		this.cutterBuckMaterialParserObj = cutterBuckMaterialParserObj;
	}
	
}
