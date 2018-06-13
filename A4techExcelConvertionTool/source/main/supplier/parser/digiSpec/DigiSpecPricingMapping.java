package parser.digiSpec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.util.StringUtils;

import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class DigiSpecPricingMapping{
	
	private static final Logger _LOGGER = Logger.getLogger(DigiSpecPricingMapping.class);
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private DigiSpecPriceGridParser digiSpecPriceGridParser;
	
	public String readExcel(Map<String, Product> productsMap,Sheet sheet,String accessToken ,Integer asiNumber ,int batchId, String environmentType){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
 		try{
			 
		/*_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);*/
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		String xid = null;
		int columnIndex = 0;
		boolean isFirstproduct = true;
		String productId = "";
		StringJoiner listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfPrices   = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		while (iterator.hasNext()) {
			
			try {
				Row nextRow = iterator.next();

				if(nextRow.getRowNum() == 0 || nextRow.getRowNum() == 1){
					//headerRow = nextRow;
					continue;
				}
				productId = getProductXid(nextRow);
				if(isFirstproduct){
					productExcelObj = productsMap.get(productId);
					priceGrids = productExcelObj.getPriceGrids();
					isFirstproduct = false;
				}
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				/*if (xid != null) {
					productXids.add(xid);
				}*/
				boolean checkXid = false;
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					columnIndex = cell.getColumnIndex();
					if (columnIndex + 1 == 1) {
						productId = getProductXid(nextRow);
						checkXid = true;
					} else {
						checkXid = false;
					}

				if(checkXid){
					 if(!productXids.contains(productId)){
						 if(nextRow.getRowNum() !=2 ){
							 System.out.println("Java object converted to JSON String, written to file");
							 	productExcelObj.setPriceGrids(priceGrids);
							 	//productExcelObj.setProductConfigurations(productConfigObj);
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
								 listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								 listOfPrices   = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								 listOfDiscount = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						 }
						    if(!productXids.contains(productId)){
						    	productXids.add(productId);
						    }
						    productExcelObj = new Product();
						    productExcelObj = productsMap.get(productId);
						    priceGrids = productExcelObj.getPriceGrids();
					 }
				}
				switch (columnIndex+1) {
				case 40: //price
				case 43:
				case 46:
				case 49:
					String listPrice = CommonUtility.getCellValueDouble(cell);
					listOfPrices.add(listPrice);
					
					break;
				case 39: //quantity
				case 42:
				case 45:
				case 48: 
					String priceQty = CommonUtility.getCellValueStrinOrInt(cell);
					listOfQuantity.add(priceQty);
				    break;
				case 41: //discount code
				case 44:
				case 47:
				case 50: 
					String discountCode = CommonUtility.getCellValueStrinOrInt(cell);
					listOfDiscount.add(discountCode);
					break;
			}  // end inner while loop		 
		}
				productExcelObj.setPriceType("L");
					priceGrids = digiSpecPriceGridParser.getBasePriceGrid(listOfPrices.toString(),
							listOfQuantity.toString(), listOfDiscount.toString(), "USD",
							productExcelObj.getDeliveryOption(), true, false, "", "", priceGrids, "", "");
					productExcelObj.setDeliveryOption("");
				
			}catch(Exception e){

				_LOGGER.error(
						"Error while Processing Product Information sheet  and cause :" + productExcelObj.getExternalProductId()
								+ " " + e.getMessage() + "at column number(increament by 1):" + columnIndex);
		}
		}
		//workbook.close();
       // productExcelObj.setProductConfigurations(productConfigObj);
		 	productExcelObj.setPriceGrids(priceGrids);
		 //	productExcelObj.setProductConfigurations(productConfigObj);
	
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
			/*try {
				//workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet" +e.getMessage());
	
			}
				_LOGGER.info("Complted processing of excel sheet ");
				_LOGGER.info("Total no of product:"+numOfProductsSuccess.size() );*/
		}
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
	public DigiSpecPriceGridParser getDigiSpecPriceGridParser() {
		return digiSpecPriceGridParser;
	}
	public void setDigiSpecPriceGridParser(DigiSpecPriceGridParser digiSpecPriceGridParser) {
		this.digiSpecPriceGridParser = digiSpecPriceGridParser;
	}
	
}
