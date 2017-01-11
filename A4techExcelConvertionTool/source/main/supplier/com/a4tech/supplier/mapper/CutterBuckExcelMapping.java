package com.a4tech.supplier.mapper;


import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.util.StringUtils;

import parser.cutter.CutterBuckMaterialParser;
import parser.cutter.CutterBuckPriceGridParser;
import parser.cutter.CutterBuckSheetParser;
import parser.cutter.CutterBuckSizeParser;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.Size;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class CutterBuckExcelMapping implements IExcelParser{



	
	//private PostServiceImpl postServiceImpl;  
	private ProductDao productDaoObj;

	private CutterBuckMaterialParser cutterBuckMaterialParserObj;
	private CutterBuckSizeParser cutterBuckSizeParserObj;
	private CutterBuckPriceGridParser cutterBuckPriceObj;
    private CutterBuckSheetParser cutterBuckSheetObj;
    private HashMap<String, Product> SheetMap =new HashMap<String, Product>();

 
	@Override
	public String readExcel(String accessToken, Workbook workbook,
			Integer asiNumber, int batchId) {
		int columnIndex = 0;

		Set<String> productXids = new HashSet<String>();

	    List<String> lineNamesList=new ArrayList<String>();
		List<Material> listOfMaterial = new ArrayList<>();
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		ProductConfigurations productConfigObj = new ProductConfigurations();

		
		Product productExcelObj = new Product();
		String productName = null;
		String finalResult = null;
		String productId = null;
		String NetCost=null;
		String ListPrice=null;
		
		

		try{

/*
			_LOGGER.info("Total sheets in excel::"
					+ workbook.getNumberOfSheets();*/
			
			int NumberOfSheet =workbook.getNumberOfSheets();
			
			
			for(int i=0;i<2;i++)
			{
			if(i==0){
				Sheet sheet = workbook.getSheetAt(0);
			
			
			
			Iterator<Row> iterator = sheet.iterator();

			while (iterator.hasNext()) {

				try{
					Row nextRow = iterator.next();

					if (nextRow.getRowNum() < 4)
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
									 productExcelObj.setPriceGrids(priceGrids);
								}
								productConfigObj = new ProductConfigurations();
								if (!productXids.contains(xid)) {
									productXids.add(xid);
								}
								productExcelObj = new Product();
							   /*  if(productExcelObj == null){
							    	
							    	 productExcelObj = new Product();
							     }else{
										productConfigObj=productExcelObj.getProductConfigurations();
							     }*/
							}
						}

						switch (columnIndex + 1) {
						
						case 1://XID
							
							 productId =  CommonUtility.getCellValueStrinOrInt(cell);
							 productExcelObj.setExternalProductId(productId);
							 break;
							 
						case 2:// Style Number
						
							String asiProdNo = null;
						    if(cell.getCellType() == Cell.CELL_TYPE_STRING){ 
						      asiProdNo = String.valueOf(cell.getStringCellValue());
						    }else if
						     (cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						      asiProdNo = String.valueOf((int)cell.getNumericCellValue());
						     }
						    if(!StringUtils.isEmpty(productId)&& !productId.contains("#N/A")){
						     productExcelObj.setAsiProdNo(asiProdNo);	
						    }else
						    {
							  productExcelObj.setAsiProdNo(asiProdNo);	
						   	  productExcelObj.setExternalProductId(asiProdNo);
						    }

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
						    NetCost=CommonUtility.getCellValueStrinOrDecimal(cell);
						    NetCost=NetCost.replaceAll("$", "");
							
							break;
				
						case 5:// MSRP - USD
						     ListPrice=CommonUtility.getCellValueStrinOrDecimal(cell);
						    ListPrice=ListPrice.replaceAll("$", "");


							break;

						case 6: // Sizes
						
							 String sizeValue=cell.getStringCellValue();
							 Size sizeObj = new Size();
							 sizeObj=cutterBuckSizeParserObj.getSizes(sizeValue);
						     productConfigObj.setSizes(sizeObj);
							
							break;
							
						case 7://Style Long Description	
							String description =CommonUtility.getCellValueStrinOrInt(cell);
							description=description.replaceAll("~", ApplicationConstants.CONST_VALUE_TYPE_SPACE);
							description=description.replaceAll("½", ApplicationConstants.CONST_STRING_EMPTY);
							description=description.replaceAll("”", ApplicationConstants.CONST_STRING_EMPTY);

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
							if(origin.contains("Vietnam")){
							origin=origin.replaceAll("Vietnam", "VIET NAM");
							}
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
				
					productExcelObj.setPriceType("B");
					 
					priceGrids = cutterBuckPriceObj.getPriceGrids(ListPrice,NetCost, 
					         1, "USD", "", true, "N", productName,"",priceGrids);	
					
						
			      
					workbook.close();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
						
										
						
			productExcelObj.setPriceGrids(priceGrids);
			productExcelObj.setProductConfigurations(productConfigObj);
			SheetMap.put(productId,productExcelObj);
			}
			}
          else{
			cutterBuckSheetObj.readMapper(accessToken, workbook, asiNumber, batchId,SheetMap);
			}
			}
			
			return finalResult;
		}catch(Exception e){
	
		return finalResult;
		}finally{
			try {
				workbook.close();
			} catch (IOException e) {
     	  System.out.println("aa.."+e.getMessage());
			}
		}
		//return finalResult;
				
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

	public HashMap<String, Product> getSheetMap() {
		return SheetMap;
	}

	public void setSheetMap(HashMap<String, Product> sheetMap) {
		SheetMap = sheetMap;
	}

	
			}

