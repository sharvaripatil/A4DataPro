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

import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Volume;
import com.a4tech.product.service.imple.PostServiceImpl;
import com.a4tech.supplier.service.ISupplierParser;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.dacasso.DacassoAttributeParser;
import parser.dacasso.DacassoPriceGridParser;

public class DacassoMapping implements ISupplierParser{
	
	private static final Logger _LOGGER = Logger.getLogger(DacassoMapping.class);
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private DacassoPriceGridParser dacassoPriceGridParser;
	private DacassoAttributeParser dacassoAttributeParser;
	
	@Override
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
 		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		//String priceInclude = null;
		String xid = null;
		int columnIndex = 0;
		StringBuilder productDescription = new StringBuilder();
		String priceVal ="";
		//StringBuilder shippingValues = new StringBuilder();
		StringBuilder shippingAdditionalInfo = new StringBuilder();
		String shippingNoOfItems = "";
		String shippingDimension = "";
		String shippingWeight    = "";
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if(nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO){
				continue;
			}
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if(xid != null){
				productXids.add(xid);
			}
			boolean checkXid  = false;			
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				columnIndex = cell.getColumnIndex();
				if(columnIndex + 1 == 1){
					xid = getProductXid(nextRow);
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							 System.out.println("Java object converted to JSON String, written to file");
							    productExcelObj.setAdditionalShippingInfo(shippingAdditionalInfo.toString());
							    productExcelObj.setDescription(productDescription.toString());
							 	productExcelObj.setPriceGrids(priceGrids);
							 	productExcelObj.setProductConfigurations(productConfigObj);
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
								productDescription = new StringBuilder();
								shippingAdditionalInfo = new StringBuilder();
								shippingNoOfItems = "";
								shippingDimension = "";
								shippingWeight    = "";
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    }
						    productExcelObj = new Product();
     						 productExcelObj = postServiceImpl.getProduct(accessToken, xid, environmentType);
						     if(productExcelObj == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						    	 productExcelObj = dacassoAttributeParser.keepExistingProductData(productExcelObj);
						    	 productConfigObj=productExcelObj.getProductConfigurations();
						    	 if(productConfigObj == null){
						    		 productConfigObj = new ProductConfigurations();
						    	 }
						     }	
					 }
				}
				switch (columnIndex+1) {
				case 1://xid
					//String productXid = CommonUtility.getCellValueStrinOrInt(cell);
					productExcelObj.setExternalProductId(xid);
					 break;
				case 2: //PrdNo
					String productNo = CommonUtility.getCellValueStrinOrDecimal(cell);
					productExcelObj.setAsiProdNo(productNo);
					  break;
				case 3: // prdName&Description
					String productName = cell.getStringCellValue();
					productName = productName.replaceAll("\n", "").trim();
					productExcelObj.setName(CommonUtility.getStringLimitedChars(productName, 60));
					productDescription.append(productName);
					  break;
				case 4: //SKU
					productExcelObj.setProductLevelSku(CommonUtility.getCellValueStrinOrInt(cell));
					 break;
				case 5://price Val
					priceVal = CommonUtility.getCellValueDouble(cell);
					break;
				case 6://
				//no need to process for category values as per feedback
				    break;
				case 7://Backing
					 productDescription.append(",").append("Backing:").append(cell.getStringCellValue());
					  break;
				case 8://Case Pack Qty(NumberOfItems)
					String numberOfItems = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(numberOfItems)){
						shippingNoOfItems = numberOfItems;
					}
					break;
				case 9://Case Pack Size(Dimensions)
					//ignore columns as per client feedback
					break;
				case 10://Case Pack Weight (Weight)
					//ignore columns as per client feedback
					break;
				case 11://color
					String colorVal = cell.getStringCellValue();
					List<Color> listOfColors = dacassoAttributeParser.getProductColors(colorVal);
					productConfigObj.setColors(listOfColors);
					break;
				case 12: //Origin
					String origin = cell.getStringCellValue();
					List<Origin> listOfOrigins = dacassoAttributeParser.getProductOrigin(origin);
					productConfigObj.setOrigins(listOfOrigins);
					break;
				case 13: //Exterior Material
				String material = cell.getStringCellValue();
				List<Material> listOfMaterials = dacassoAttributeParser.getProductMaterial(material);
				productConfigObj.setMaterials(listOfMaterials);
					break;

				case 14://Factory Package Size
					shippingAdditionalInfo.append("Factory Package Size:").append(cell.getStringCellValue());	
					break;
					
				case 15: //Package Type
					String packageing = cell.getStringCellValue();
					List<Packaging> listOfPackaging = dacassoAttributeParser.getProductPackaging(packageing);
					productConfigObj.setPackaging(listOfPackaging);
					break;
					
				case 16://Factory Package Weight
					String factoryPackageWeight = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(shippingAdditionalInfo.toString())){
						shippingAdditionalInfo.append(",").append("Factory Package Weight:")
						.append(factoryPackageWeight);
					} else {
						shippingAdditionalInfo.append("Factory Package Weight:")
						.append(factoryPackageWeight);
					}
							
					break;
				case 17://Interior Lining
					productDescription.append(",").append("Interior Lining:").append(cell.getStringCellValue());
					  break;
				
				case 18: //Maximum Imprint Size
					String imprintSize = cell.getStringCellValue();
							List<ImprintSize> listOfImprintSize = dacassoAttributeParser
									.getProductImprintSize(imprintSize);
					productConfigObj.setImprintSize(listOfImprintSize);
					break;
				
				 case 19: //Preferred Imprint Location
					 String imprintLoc = cell.getStringCellValue();
							List<ImprintLocation> listOfImprintLocation = dacassoAttributeParser
									.getProductImprintLocation(imprintLoc);
						productConfigObj.setImprintLocation(listOfImprintLocation);
					break;
					
				case 20: //Product Size
					String sizeVal = cell.getStringCellValue();
					Size sizeVals = dacassoAttributeParser.getProductSize(sizeVal);
					productConfigObj.setSizes(sizeVals);
					break;
					
				case 21: //ItemWeight
					String itemWeight = CommonUtility.getCellValueDouble(cell);
					Volume listOfItemWeight = dacassoAttributeParser.getItemWeight(itemWeight);
					productConfigObj.setItemWeight(listOfItemWeight);
					break;
					
				case 22: //Series
					// Ignore as per feedback
					break;
				case 23://Shipping Package Size
					String dimensions = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(dimensions)){
						shippingDimension = dimensions;
					}
				   break;
					
				case 24: //Shipping Package Weight
					String weight =CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(weight)){
						shippingWeight = weight;
					}
				   break;
				   
				case 25:  //Stitching
					productDescription.append(",").append("Stitching:").append(cell.getStringCellValue());
					break;
				case 26: //Style Code
					// Ignore as per feedback
					
					break;
							
			}  // end inner while loop		 
		}
				productExcelObj.setPriceType("L");
				if(!StringUtils.isEmpty(priceVal)){
					priceGrids = dacassoPriceGridParser.getBasePriceGrid(priceVal,"1","P", "USD",
							         "", true, false, "","",priceGrids,"","");	
				}
					ShippingEstimate shippingEstimateValues = dacassoAttributeParser
								.getProductShippingEstimates(shippingNoOfItems, shippingDimension, shippingWeight);
					 productConfigObj.setShippingEstimates(shippingEstimateValues);
				
			}catch(Exception e){

				_LOGGER.error(
						"Error while Processing Product Information sheet  and cause :" + productExcelObj.getExternalProductId()
								+ " " + e.getMessage() + "at column number(increament by 1):" + columnIndex);
		}
		}
		workbook.close();
		productExcelObj.setAdditionalShippingInfo(shippingAdditionalInfo.toString());
		productExcelObj.setDescription(productDescription.toString());
        productExcelObj.setProductConfigurations(productConfigObj);
		 	productExcelObj.setPriceGrids(priceGrids);
		 	productExcelObj.setProductConfigurations(productConfigObj);
	
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId, environmentType);
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
	public List<Material> getExistingProductMaterials(Product existingProduct){
		ProductConfigurations configuration = existingProduct.getProductConfigurations();
		return configuration.getMaterials();
	}
	private String getProductXid(Row row){
		Cell xidCell = row.getCell(ApplicationConstants.CONST_NUMBER_ZERO);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if (StringUtils.isEmpty(productXid)) {
			xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
			productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
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
	public DacassoPriceGridParser getDacassoPriceGridParser() {
		return dacassoPriceGridParser;
	}
	public void setDacassoPriceGridParser(DacassoPriceGridParser dacassoPriceGridParser) {
		this.dacassoPriceGridParser = dacassoPriceGridParser;
	}
	public DacassoAttributeParser getDacassoAttributeParser() {
		return dacassoAttributeParser;
	}
	public void setDacassoAttributeParser(DacassoAttributeParser dacassoAttributeParser) {
		this.dacassoAttributeParser = dacassoAttributeParser;
	}
}
