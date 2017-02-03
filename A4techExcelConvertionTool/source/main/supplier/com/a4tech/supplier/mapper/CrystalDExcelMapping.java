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

import parser.crystal.CrystalDMaterialParser;
import parser.crystal.CrystalDPriceGridParser;
import parser.crystal.CrystalDProductAttributeParser;

import com.a4tech.apparel.product.mapping.ApparelProductsExcelMapping;
import com.a4tech.dataStore.ProductDataStore;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.Personalization;
import com.a4tech.product.model.PriceGrid;
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
	private CrystalDMaterialParser crymaterialObj;
	private CrystalDPriceGridParser cdpriceObj;
	
	

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
		List<Material> listOfMaterial = new ArrayList<>();
		List<ImprintMethod> imprintMethodsList = new ArrayList<ImprintMethod>();
		List<ImprintMethod> exstimprintMethodsList = new ArrayList<ImprintMethod>();
		List<Option> optionList = new ArrayList<Option>();
		StringBuilder listOfQuantity = new StringBuilder();
		StringBuilder listOfPrices = new StringBuilder();
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		StringBuilder listOfNotes = new StringBuilder();
		List<Personalization> listPersonlization=new ArrayList<Personalization>();

		
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
		String Quantity1=null;
		String Quantity2=null;
		String Quantity3=null;
		String ListPrice1=null;
		String ListPrice2=null;
		String ListPrice3=null;
		String PriceInclude=null;
		String productName =null;
		String Notes1=null;
		String Notes2=null;
		String Notes3=null;
		String Notes4=null;
		String Notes5=null;
		String AllNotes=null;
		String AddInfo=null;
		String description =null;
		String MaterialValue=null;

		


		
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
							    productConfigObj.setMaterials(listOfMaterial);	
							    productConfigObj.setImprintMethods(imprintMethodsList);
							    productConfigObj.setOptions(optionList);
							    productExcelObj.setPriceGrids(priceGrids);
							    if(!StringUtils.isEmpty(listPersonlization)){
							     productConfigObj.setPersonalization(listPersonlization);
								 }
							    productExcelObj.setAdditionalProductInfo(AddInfo);
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
						    	listOfMaterial = new ArrayList<>();
						    	imprintMethodsList = new ArrayList<ImprintMethod>();
						    	exstimprintMethodsList=new ArrayList<ImprintMethod>();
						    	optionList = new ArrayList<Option>();
						    	priceGrids = new ArrayList<PriceGrid>();
						    	listOfQuantity = new StringBuilder();
								listOfPrices = new StringBuilder();
								listPersonlization=new ArrayList<Personalization>();
								listOfNotes = new StringBuilder();
								AddInfo=null;
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
					 productName = cell.getStringCellValue();
					int len=productName.length();
					 if(len>60){
						String strTemp=productName.substring(0, 60);
						int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
						productName=(String) strTemp.subSequence(0, lenTemp);
					}
					productExcelObj.setName(productName);
					
				    break;
				case 4://Long Description
					
				    description =CommonUtility.getCellValueStrinOrInt(cell);
					int length=description.length();
					if(length>800){
						String strTemp=description.substring(0, 800);
						int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
						description=(String) strTemp.subSequence(0, lenTemp);
					}
					//productExcelObj.setDescription(description);		
				
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
					 MaterialValue=cell.getStringCellValue();
					//listOfMaterial = crymaterialObj.getMaterialList(MaterialValue);
					
					
					 break;
				case 14://Notes1
					Notes1=cell.getStringCellValue();
					if(Notes1.contains("Price"))
					{
						PriceInclude=Notes1;
					} if(!Notes1.contains("Price") && !Notes1.contains("Personalization") && !Notes1.contains("Colorfill") )
					{
						AddInfo=Notes1.concat(" ");
					}
				
			       break;
				case 15://Notes2 PriceInclude
					 Notes2=cell.getStringCellValue();
					 if(Notes2.contains("Price"))
						{
							PriceInclude=Notes2;
						}
					  if(!Notes2.contains("Price") && !Notes2.contains("Personalization") && !Notes2.contains("Colorfill") )
						{
							AddInfo=AddInfo.concat( Notes2).concat(" ");
						}
				
				   break;
				case 16://Notes3
					Notes3=cell.getStringCellValue();
					if(Notes3.contains("Price"))
					{
						PriceInclude=Notes3;
					}
					 if(!Notes3.contains("Price") && !Notes3.contains("Personalization") && !Notes3.contains("Colorfill") )
					{
						AddInfo=AddInfo.concat( Notes3).concat(" ");
					}
				
					break;
				case 17://Notes4
					Notes4=cell.getStringCellValue();
					if(Notes4.contains("Price"))
					{
						PriceInclude=Notes4;
					}
					 else if(!Notes4.contains("Price") && !Notes4.contains("Personalization") && !Notes4.contains("Colorfill") )
					{
						AddInfo=AddInfo.concat( Notes4).concat(" ");
					}
				
					break;
				case 18://Notes5
					Notes5=cell.getStringCellValue();
					if(!Notes5.contains("Price") && !Notes5.contains("Personalization") && !Notes5.contains("Colorfill") )
					{
						AddInfo=AddInfo.concat( Notes5);
					}
					listOfNotes=listOfNotes.append(Notes1).append(Notes2).append(Notes3).append(Notes4).append(Notes5);
				    AllNotes=listOfNotes.toString();
					listPersonlization=crystalDObj.getPeronalization(AllNotes);
					
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
					String ImprintProcess=cell.getStringCellValue();
					ImprintMethod imprintMethodObj= null;
					if(ImprintProcess.contains("3d") || ImprintProcess.contains("wood") ){
						 imprintMethodObj= new ImprintMethod();
						imprintMethodObj.setAlias(ImprintProcess);	
						imprintMethodObj.setType("Other");
						exstimprintMethodsList.add(imprintMethodObj);
					}
				

					break;
				case 24://Gallery/CATEGORY

					break;
				case 25://Functionality

					break;
				case 26://Shapes Sizes
					
					String description1 =CommonUtility.getCellValueStrinOrInt(cell);
					int length1=description1.length();
					if(length1>800){
						String strTemp=description.substring(0, 800);
						int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
						description1=(String) strTemp.subSequence(0, lenTemp);
					}
					description=description.concat("").concat(description1);
					productExcelObj.setDescription(description);
					
					

					break;
				case 27://Material Type
					String MaterialType=cell.getStringCellValue();
					MaterialValue=MaterialValue.concat(",").concat(MaterialType);
					listOfMaterial = crymaterialObj.getMaterialList(MaterialValue);

					break;
				case 28://Imprint Process
					String ImprintMethodValue=CommonUtility.getCellValueStrinOrInt(cell);
					if(ImprintMethodValue.contains("Optional Colorfill"))
					{
						String TempImprintOptionValue=("Optional Colorfill");
						optionList=crystalDObj.getImprintOption(TempImprintOptionValue);
						ImprintMethodValue=ImprintMethodValue.replaceAll("Optional Colorfill,","");
					}
					imprintMethodsList = crystalDObj.getImprintMethod(ImprintMethodValue,exstimprintMethodsList);
					break;
				case 29://QTYBRK1
					 Quantity1=CommonUtility.getCellValueStrinOrInt(cell);

					
					break;
				case 30://QTY1PRICE
                    ListPrice1=CommonUtility.getCellValueStrinOrDecimal(cell);


					break;
			    case 31://QTYBRK2
					 Quantity2=CommonUtility.getCellValueStrinOrInt(cell);


					break;
				case 32://QTY2PRICE
					 ListPrice2=CommonUtility.getCellValueStrinOrDecimal(cell);


					break;
				case 33://QTYBRK3
					 Quantity3=CommonUtility.getCellValueStrinOrInt(cell);
					 listOfQuantity=listOfQuantity.append(Quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).
							 append(Quantity2).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(Quantity3);
					
					break;
                case 34://QTY3PRICE
					 ListPrice3=CommonUtility.getCellValueStrinOrDecimal(cell);
					 listOfPrices=listOfPrices.append(ListPrice1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).
							 append(ListPrice2).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(ListPrice3);

					
					break;
		
				
							
				}
				  // end inner while loop
				 }	 
			}
				productExcelObj.setPriceType("L");
				
				priceGrids = cdpriceObj.getPriceGrids(listOfPrices.toString(),
				         listOfQuantity.toString(), "R", "USD",
				         PriceInclude, true, "N",productName ,""/*,priceGrids*/);
				
				
				if(AllNotes.contains("Personalization extra"))
				{
					priceGrids = cdpriceObj.getUpchargePriceGrid("1","11.67","R","Personalization",  
							"false", "USD", "Personalization",  "Personalization", "Other", new Integer(1),priceGrids);		
					
				}
				if(AllNotes.contains("Shown with Colorfill"))
				{
					priceGrids = cdpriceObj.getUpchargePriceGrid("1","11.67","R","Imprint Option",  
							"false", "USD", "Optional Colorfill",  "Imprint Option Charge", "Other", new Integer(1),priceGrids);		
					
				}
				
			
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() );		 
		}
		}
		
		workbook.close();
	
		    productConfigObj.setImprintSize(imprintSizeList);
		    productConfigObj.setPackaging(listOfPackaging);
		    productConfigObj.setItemWeight(itemWeight);
		    productConfigObj.setSizes(sizeObj);
		    productConfigObj.setMaterials(listOfMaterial);	
		    productConfigObj.setImprintMethods(imprintMethodsList);
		    productConfigObj.setOptions(optionList);
		    productExcelObj.setPriceGrids(priceGrids);
		    if(!StringUtils.isEmpty(listPersonlization)){
		    productConfigObj.setPersonalization(listPersonlization);
		    }
		    productExcelObj.setAdditionalProductInfo(AddInfo);
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
	    	listOfMaterial = new ArrayList<>();
	    	imprintMethodsList = new ArrayList<ImprintMethod>();
	    	exstimprintMethodsList=new ArrayList<ImprintMethod>();
	    	optionList = new ArrayList<Option>();
	    	priceGrids = new ArrayList<PriceGrid>();
	    	listOfQuantity = new StringBuilder();
			listOfPrices = new StringBuilder();
			listPersonlization=new ArrayList<Personalization>();
			listOfNotes = new StringBuilder();
			AddInfo=null;
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

	public CrystalDMaterialParser getCrymaterialObj() {
		return crymaterialObj;
	}

	public void setCrymaterialObj(CrystalDMaterialParser crymaterialObj) {
		this.crymaterialObj = crymaterialObj;
	}

	public CrystalDPriceGridParser getCdpriceObj() {
		return cdpriceObj;
	}

	public void setCdpriceObj(CrystalDPriceGridParser cdpriceObj) {
		this.cdpriceObj = cdpriceObj;
	}
	
	
	

}
