package parser.primeline;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.service.imple.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PrimeLineColorTabParser {

	private static final Logger _LOGGER = Logger.getLogger(PrimeLineColorTabParser.class);
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	@Autowired
	ObjectMapper mapperObj;
	private LookupServiceData lookupServiceDataObj;
	public HashMap<String, Product> readColorTab(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId,
			HashMap<String, Product> sheetMap){
		HashMap<String, Product> sheetMapReturn=new HashMap<String, Product>();
		Product existingApiProduct = null;
		Set<String>  productXids = new HashSet<String>();
		List<String> repeatRows = new ArrayList<>();
		//List<Color> colorList = new ArrayList<Color>();
		Color colorObj = null;
		List<Combo> comboList = null;
		HashSet<String>  colorSet = new HashSet<String>();
		try{
		Product	productExcelObj=new Product();
		ProductConfigurations productConfigObj=new ProductConfigurations();
	    _LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
		Sheet sheet = workbook.getSheetAt(1);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product in color tab");
	    String productId = null;
	    String xid = null;
	    int columnIndex=0;
	    String colorValue=null;
	    while (iterator.hasNext()) {
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum() == 0)
			{
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
					xid = getProductXid(nextRow);//CommonUtility.getCellValueStrinOrInt(cell);//
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							 //int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId);
							 if(!CollectionUtils.isEmpty(colorSet)){
								 List<Color> colorList = new ArrayList<Color>();
								 for (String tempString : colorSet) {
									 String colrNcustVal[]=tempString.split("@@@@@");
									 String colorVal=colrNcustVal[0];
									 String custCodeVal=colrNcustVal[1];
									 colorList= getProductColor(colorVal, custCodeVal, colorList);
									
								}
								 productConfigObj.setColors(colorList);	 
							 }
							 
							 productExcelObj.setProductConfigurations(productConfigObj);
							 _LOGGER.info("Product Data from sheet 2 color tab: "+ mapperObj.writeValueAsString(productExcelObj));
							 sheetMapReturn.put(productId, productExcelObj);
								//productConfigObj = new ProductConfigurations();
								repeatRows.clear();
								//colorList = new ArrayList<Color>();
								colorValue=null;
								colorSet=new HashSet<String>();
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    	repeatRows.add(xid);
						    }
						    existingApiProduct=sheetMap.get(xid);
						     if(existingApiProduct == null){
						    	 _LOGGER.info("Existing Xid is not available in Map,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						    	    productExcelObj=existingApiProduct;
									productConfigObj=existingApiProduct.getProductConfigurations();
									if(productConfigObj==null){
										productConfigObj=new ProductConfigurations();
									}
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
					    case 1://XID
						//productId=xid;//CommonUtility.getCellValueStrinOrInt(cell);
						productExcelObj.setExternalProductId(xid);
						productId=xid;
						break;
						case 2://ITEMID
							
						break;
						case 3://COLOR
							colorValue =  CommonUtility.getCellValueStrinOrInt(cell);
							
							break;

						case 4://CONFIG
							
							String custColorCode =  CommonUtility.getCellValueStrinOrInt(cell);
							if(!StringUtils.isEmpty(custColorCode)){
							
							}else{
								custColorCode="";
							}
							
							if(!StringUtils.isEmpty(colorValue)){
								colorSet.add(colorValue+"@@@@@"+custColorCode);
							}
							break;
				}  // end inner while loop					 
			}		
			}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);		 
				ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet 2: "
				+e.getMessage()+" at column number(increament by 1)"+columnIndex);
				productDaoObj.save(apiResponse.getErrors(),
						productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);
				}
		}
		workbook.close();
		 if(!CollectionUtils.isEmpty(colorSet)){
			 List<Color> colorList = new ArrayList<Color>();
			 for (String tempString : colorSet) {
				 String colrNcustVal[]=tempString.split("@@@@@");
				 String colorVal=colrNcustVal[0];
				 String custCodeVal=colrNcustVal[1];
				 colorList= getProductColor(colorVal, custCodeVal, colorList);
				
			}
			 productConfigObj.setColors(colorList);	 
		 }
		 productExcelObj.setProductConfigurations(productConfigObj);
		 _LOGGER.info("Product Data from sheet 2 color tab: "
					+ mapperObj.writeValueAsString(productExcelObj));
		sheetMapReturn.put(productId, productExcelObj);
	    productDaoObj.saveErrorLog(asiNumber,batchId);
		repeatRows.clear();
		colorValue=null;
		colorSet=new HashSet<String>();
		//colorList = new ArrayList<Color>();
		return sheetMapReturn;
		}catch(Exception e){
			_LOGGER.error("Error while Processing excel sheet " +e.getMessage());
			return sheetMapReturn;
		}finally{
			try {
				workbook.close();
			} catch (IOException e) { 
				_LOGGER.error("Error while Processing excel sheet" +e.getMessage());
			}
				_LOGGER.info("Complted processing of excel sheet ");
				
		}
	}
	
	public List<Color> getProductColor(String colorValue,String custColorCode,List<Color> colorList ){

		String tempcolorArray[]=colorValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		for (String colorVal : tempcolorArray) {
		String strColor=colorVal;
		strColor=strColor.replaceAll("&","/");
		strColor=strColor.replaceAll(" w/","/");
		strColor=strColor.replaceAll(" W/","/");
		boolean isCombo = false;
		Color	colorObj = new Color();
		ArrayList<Combo> comboList = new ArrayList<Combo>();
			isCombo = isComboColors(strColor);
			if (!isCombo) {
				String colorName=PrimeLineConstants.PRIMECOLOR_MAP.get(strColor.trim());
				if(StringUtils.isEmpty(colorName)){
					colorName=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
				}
				colorObj.setName(colorName);
				colorObj.setAlias(colorVal.trim());
				colorObj.setCustomerOrderCode(custColorCode);
				colorList.add(colorObj);
			} else {
				//245-Mid Brown/Navy
				String colorArray[] = strColor.split(ApplicationConstants.CONST_DELIMITER_FSLASH);
				//if(colorArray.length==2){
				String combo_color_1=PrimeLineConstants.PRIMECOLOR_MAP.get(colorArray[0].trim());
				if(StringUtils.isEmpty(combo_color_1)){
					combo_color_1=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
				}
				colorObj.setName(combo_color_1);
				colorObj.setAlias(strColor);
				
				Combo comboObj = new Combo();
				String combo_color_2=PrimeLineConstants.PRIMECOLOR_MAP.get(colorArray[1].trim());
				if(StringUtils.isEmpty(combo_color_2)){
					combo_color_2=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
				}
				comboObj.setName(combo_color_2.trim());
				comboObj.setType(ApplicationConstants.CONST_STRING_SECONDARY);
				if(colorArray.length==3){
					String combo_color_3=PrimeLineConstants.PRIMECOLOR_MAP.get(colorArray[2].trim());
					if(StringUtils.isEmpty(combo_color_3)){
						combo_color_3=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
					}
					Combo comboObj2 = new Combo();
					comboObj2.setName(combo_color_3.trim());
					comboObj2.setType(ApplicationConstants.CONST_STRING_TRIM);
					comboList.add(comboObj2);
				}
				comboList.add(comboObj);
				colorObj.setCombos(comboList);
				colorObj.setCustomerOrderCode(custColorCode);
				colorList.add(colorObj);
			 	}
			}
		return colorList;
		
	}
	
	public String getProductXid(Row row){
		Cell xidCell =  row.getCell(0);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid) || productXid.trim().equalsIgnoreCase("#N/A")){
		     xidCell = row.getCell(1);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}
	
	public boolean isRepeateColumn(int columnIndex){
		if(columnIndex != 1 && columnIndex != 3 && columnIndex != 4){
		//if(columnIndex != 1&&columnIndex != 3&&columnIndex != 4 && columnIndex != 6 && columnIndex != 9 && columnIndex != 24){
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
	
	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}

	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}

	public static String removeSpecialChar(String tempValue){
		tempValue=tempValue.replaceAll("(CLASS|Day|DAYS|Service|Days|Hour|Hours|Week|Weeks|Rush|day|service|days|hour|hours|week|weeks|Rush|R|u|s|h|$)", "");
		tempValue=tempValue.replaceAll("\\(","");
		tempValue=tempValue.replaceAll("\\)","");
	return tempValue;

	}
	private boolean isComboColors(String value) {
		boolean result = false;
		if (value.contains("/")) {
			result = true;
		}
		return result;
	}
	
	


}
