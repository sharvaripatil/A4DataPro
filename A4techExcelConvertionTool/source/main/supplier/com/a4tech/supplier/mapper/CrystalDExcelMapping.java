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

import parser.crystal.CrystalDProductAttributeParser;

import com.a4tech.apparel.product.mapping.ApparelProductsExcelMapping;
import com.a4tech.dataStore.ProductDataStore;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Volume;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class CrystalDExcelMapping implements IExcelParser {

	private static final Logger _LOGGER = Logger.getLogger(ApparelProductsExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;  
	private ProductDao productDaoObj;
	private CrystalDProductAttributeParser crystalDObj;
	
	
	

	@Override
	public String readExcel(String accessToken, Workbook workbook,
			Integer asiNumber, int batchId) {
	
		
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
	    Product productExcelObj = new Product();   
	    ProductConfigurations productConfigObj=new ProductConfigurations();
	    List<String> repeatRows = new ArrayList<>();
	    
	    List<ImprintSize> imprintSizeList =new ArrayList<ImprintSize>();
	    ImprintSize imprintSizeObj=new ImprintSize();
		StringBuilder FinalImprintSize = new StringBuilder();
		List<Packaging> listOfPackaging = new ArrayList<Packaging>();
		Packaging packageObj= new Packaging();
		Volume itemWeight=new Volume();
		StringBuilder ShippingDimension = new StringBuilder();
		Size sizeObj=new Size();

		
 		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		

		Product existingApiProduct = null;
		String xid = null;
		String productId = null;
		Cell cell2Data = null;
		String ImprintSize1=null;
		String ImprintSize2=null;
		String DimensionValue1=null;
		String DimensionValue2=null;

		
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if(nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO){
				continue;
			}
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if (xid != null) {
				productXids.add(productId);
			}
			 boolean checkXid  = false;
			
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				int columnIndex = cell.getColumnIndex();
				  cell2Data =  nextRow.getCell(1);
			
				
				if(columnIndex + 1 == 1){
					if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
						xid = cell.getStringCellValue();
					} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
						xid = String.valueOf((int) cell
								.getNumericCellValue());
					} else {
					  String ProdNo=CommonUtility.getCellValueStrinOrInt(cell2Data);
					  xid=ProdNo;

					}
				
						checkXid = true;

					}else{
						checkXid = false;
					}
				 if(!StringUtils.isEmpty(xid)){
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							 System.out.println("Java object converted to JSON String, written to file");
							
							    productConfigObj.setImprintSize(imprintSizeList);
							    productConfigObj.setPackaging(listOfPackaging);
							    productConfigObj.setItemWeight(itemWeight);
							    productConfigObj.setSizes(sizeObj);
							 	productExcelObj.setProductConfigurations(productConfigObj);

							 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId);
							 	if(num ==1){
							 		numOfProductsSuccess.add("1");
							 	}else if(num == 0){
							 		numOfProductsFailure.add("0");
							 	}else{
							 		
							 	}
							 	_LOGGER.info("list size>>>>>>>"+numOfProductsSuccess.size());
							 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
						   	  ProductDataStore.clearProductColorSet();
						    	imprintSizeList =new ArrayList<ImprintSize>();
						    	FinalImprintSize=new StringBuilder();
						    	listOfPackaging = new ArrayList<Packaging>();
						    	sizeObj=new Size();
						    	itemWeight=new Volume();
						    	ShippingDimension=new StringBuilder();

								repeatRows.clear();
								
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    	repeatRows.add(xid);
						    }
						 //   productExcelObj = new Product();
						    existingApiProduct = postServiceImpl.getProduct(accessToken, xid);
						     if(existingApiProduct == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						  	   productExcelObj=existingApiProduct;
								productConfigObj=productExcelObj.getProductConfigurations();
						     }
							
					 }
				}
				switch (columnIndex+1) {
				case 1://XID
				   productExcelObj.setExternalProductId(xid);
	
					 break;
				case 2:// Item
					String asiProdNo =  CommonUtility.getCellValueStrinOrInt(cell);
				     productExcelObj.setAsiProdNo(asiProdNo);	

					

					  break;
				case 3://Short Description
					String productName = cell.getStringCellValue();
					int len=productName.length();
					 if(len>60){
						String strTemp=productName.substring(0, 60);
						int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
						productName=(String) strTemp.subSequence(0, lenTemp);
					}
					productExcelObj.setName(productName);
					
				    break;
				case 4://Long Description
					
					String description =CommonUtility.getCellValueStrinOrInt(cell);
					int length=description.length();
					if(length>800){
						String strTemp=description.substring(0, 800);
						int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
						description=(String) strTemp.subSequence(0, lenTemp);
					}
					productExcelObj.setDescription(description);		
				
				    break;
				case 5://Weight
					String ItemWtValue=CommonUtility.getCellValueStrinOrDecimal(cell);
					 if(!StringUtils.isEmpty(ItemWtValue)){
					  itemWeight=crystalDObj.getItemWeight(ItemWtValue);
					 }

					break;
				case 6: //Dimensions1
					DimensionValue1=CommonUtility.getCellValueStrinOrInt(cell);
					
					break;
				case 7://Dimensions2
					DimensionValue2=CommonUtility.getCellValueStrinOrInt(cell);
					  
					break;
					
				case 8: //Dimensions3
				String DimensionValue3=CommonUtility.getCellValueStrinOrInt(cell);
				ShippingDimension=ShippingDimension.append(DimensionValue1).append(",").append(DimensionValue2).
						          append(",").append(DimensionValue3);
				sizeObj=crystalDObj.getSizes(ShippingDimension);
					
					
					break;
					
				case 9: // Image Area1
					 ImprintSize1=cell.getStringCellValue();
					
					break;
				case 10: // Image Area2
					 ImprintSize2=cell.getStringCellValue();

					
					break;
				case 11://Image Area3
					String ImprintSize3=cell.getStringCellValue();
				    FinalImprintSize=FinalImprintSize.append(ImprintSize1).append(",").append(ImprintSize2).
				    		          append(",").append(ImprintSize3);
				    String FinalImprintSizeArr[]=FinalImprintSize.toString().split(",");
				    for (String Value : FinalImprintSizeArr) {
				    	imprintSizeObj=new ImprintSize();
				    	imprintSizeObj.setValue(Value);
					    imprintSizeList.add(imprintSizeObj);

					}
					//FinalImprintSize=null;

				   
					break;
				case 12://Packaging
					String packagingValue=cell.getStringCellValue();
					packageObj.setName(packagingValue);
					listOfPackaging.add(packageObj);
					
					break;
				case 13://Material
					
					 break;
				case 14://Notes1
				
			       break;
				case 15://Notes2
				
				   break;
				case 16://Notes3
				
					break;
				case 17://Notes4
				
					break;
				case 18://Notes5
					
					break;
				case 19://Image Name

					break;
				case 20://Template Name
					
					break;
				case 21://Page #
					
					break;
				case 22://Services

					break;
				case 23://Process

					break;
				case 24://Gallery/CATEGORY

					break;
				case 25://Functionality

					break;
				case 26://Shapes Sizes

					break;
				case 27://Material Type

					break;
				case 28://Imprint Process
					
					break;
				case 29://QTYBRK1
					
					break;
				case 30://QTY1PRICE

					break;
				case 31://QTYBRK2

					break;
				case 32://QTY2PRICE

					break;
				case 33://QTYBRK3
					
					break;
                case 34://QTY3PRICE
					
					break;
		
				
							
				}
				  // end inner while loop
					 
		
				productExcelObj.setPriceType("N");
			}}	
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() );		 
		}
		}
		
		workbook.close();
	
		    productConfigObj.setImprintSize(imprintSizeList);
		    productConfigObj.setPackaging(listOfPackaging);
		    productConfigObj.setItemWeight(itemWeight);
		    productConfigObj.setSizes(sizeObj);
		 	productExcelObj.setProductConfigurations(productConfigObj);

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
	       
	       productConfigObj = new ProductConfigurations();
	       ProductDataStore.clearProductColorSet();
	    	imprintSizeList =new ArrayList<ImprintSize>();
	    	listOfPackaging = new ArrayList<Packaging>();
	    	itemWeight=new Volume();
	    	sizeObj=new Size();
	    	ShippingDimension=new StringBuilder();
	    	
			repeatRows.clear();
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

	public CrystalDProductAttributeParser getCrystalDObj() {
		return crystalDObj;
	}

	public void setCrystalDObj(CrystalDProductAttributeParser crystalDObj) {
		this.crystalDObj = crystalDObj;
	}
	

	
	
	

}
