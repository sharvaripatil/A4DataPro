package parser.primeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PrimeLineImprintTabParser {



	private static final Logger _LOGGER = Logger.getLogger(PrimeLineImprintTabParser.class);
	private ProductDao productDaoObj;
	private PrimeLineAttributeParser primeLineAttriObj;
	private PrimeLinePriceGridParser primeLinePriceGridParser;
	@Autowired
	ObjectMapper mapperObj;
	
	public HashMap<String, Product> readImprintTab(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId,
			HashMap<String, Product> sheetMap){
		HashMap<String, Product> sheetMapReturn=new HashMap<String, Product>();
		//HashMap<String, ArrayList<String>> featureMap=new HashMap<String,ArrayList<String>>();
		Product existingApiProduct = null;
		Set<String>  productXids = new HashSet<String>();
		List<String> repeatRows = new ArrayList<>();
		
		try{
		Product	productExcelObj=new Product();
		ProductConfigurations productConfigObj=new ProductConfigurations();
		Sheet sheet = workbook.getSheetAt(3);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Products in imprint tab");
	    String productId = null;
	    String xid = null;
	    int columnIndex=0;
	    String imprintLocVal=null;
	    String imprintSizeVal=null;
	    String imprintMethodVal=null;
	    HashSet<String> imprintSizeSet=new HashSet<String>();
	    HashSet<String> imprintLocSet=new HashSet<String>();
	    HashSet<String> imprintMhtdSet=new HashSet<String>();
	    String setUpChrgValue=null;
	    String runChrgValue=null;
	    Map<String, String> priceMap=new HashMap<String, String>();
	    HashMap<String, HashSet<String>> availMapIMTD=new HashMap<String, HashSet<String>>();
	    HashMap<String, HashSet<String>> availMapIMLOC=new HashMap<String, HashSet<String>>();
	    List<Availability> availibilityList=new ArrayList<Availability>();
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
							 if(!CollectionUtils.isEmpty(imprintMhtdSet)){
								 List<ImprintMethod> listOfImprintMethod= primeLineAttriObj.getImprintMethods(new ArrayList<String>(imprintMhtdSet));
								 productConfigObj.setImprintMethods(listOfImprintMethod);
							    }
							 if(!CollectionUtils.isEmpty(imprintSizeSet)){
								 List<ImprintSize> listOfImprintSizeNew=primeLineAttriObj.getProductImprintSize(new ArrayList<String>(imprintSizeSet));
								 productConfigObj.setImprintSize(listOfImprintSizeNew);
							    }
							 
						    if(!CollectionUtils.isEmpty(imprintLocSet)){
						        List<ImprintLocation> listOfImprintLoc= primeLineAttriObj.getImprintLocationVal(new ArrayList<String>(imprintLocSet));
							 	productConfigObj.setImprintLocation(listOfImprintLoc);
						     	}
						    //if(!CollectionUtils.isEmpty(imprintSizeSet) && !CollectionUtils.isEmpty(imprintMhtdSet)){
						    if(!CollectionUtils.isEmpty(availMapIMTD)){
						    	//availibilityList=primeLineAttriObj.getProductAvailablity(imprintSizeSet,imprintMhtdSet,"Imprint Size" ,"Imprint Method",availibilityList);
						    	availibilityList=primeLineAttriObj.getProductAvailablity(availMapIMTD,"Imprint Method","Imprint Size",availibilityList);
						    }
					    	//if(!CollectionUtils.isEmpty(imprintSizeSet) && !CollectionUtils.isEmpty(imprintLocSet)){
						    if(!CollectionUtils.isEmpty(availMapIMLOC)){
					    		//availibilityList=primeLineAttriObj.getProductAvailablity(imprintSizeSet,imprintLocSet,"Imprint Size" ,"Imprint Location",availibilityList);
					    		availibilityList=primeLineAttriObj.getProductAvailablity(availMapIMLOC,"Imprint Location","Imprint Size",availibilityList);
					    	}
					    	 if(!CollectionUtils.isEmpty(availibilityList)){
					    	productExcelObj.setAvailability(availibilityList);
					    	 }
						    if(!CollectionUtils.isEmpty(priceMap)){
						    	
						    	List<PriceGrid> existingPriceGrids = productExcelObj.getPriceGrids();
						    	for (Entry<String, String> entryVal : priceMap.entrySet()) {
									String priceValues=entryVal.getValue();
									String criteriaValue=entryVal.getKey();
									String upChargeType="Set-up Charge";
									String upchargeUsageType="Other";
									String tempArr[]=priceValues.split("@@@@@");
									int length=tempArr.length;
									
									for (int i = 0; i < length;i++) {
										if(!tempArr[i].equals("EMPTY")){
											if(i==0){
												upChargeType="Set-up Charge";
											}else if(i==1){
												upChargeType="Run Charge";
											}
											existingPriceGrids=primeLinePriceGridParser.getPriceGrids(tempArr[i],"1","V","USD","",
													false, "False", criteriaValue, 
													"Imprint Method", i+1,upChargeType, upchargeUsageType,
													existingPriceGrids);
										}
									}
								}
						    	 productExcelObj.setPriceGrids(existingPriceGrids);
						    }
						    //else here gie quote upon request
						   
						    productExcelObj.setProductConfigurations(productConfigObj);
							 _LOGGER.info("Product Data from sheet 4 ItemImprint tab: "+ mapperObj.writeValueAsString(productExcelObj));
							 sheetMapReturn.put(productId, productExcelObj);
							 //}
								//productConfigObj = new ProductConfigurations();
								repeatRows.clear();
								imprintSizeVal="";
								imprintMethodVal="";
								imprintLocVal="";
								setUpChrgValue="";
								runChrgValue="";
							    imprintSizeSet=new HashSet<String>();
							    imprintLocSet=new HashSet<String>();
							    imprintMhtdSet=new HashSet<String>();
							    priceMap=new HashMap<String, String>();
							    availibilityList=new ArrayList<Availability>();
							    availMapIMLOC=new HashMap<String, HashSet<String>>();
							    availMapIMTD=new HashMap<String, HashSet<String>>();
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
						case 3://IMPRINTID //ignore
							
							break;

						case 4://IMPRINTTYPE //ignore // need to confirm cannot ignore this value
							
							break;
							
						case 5://IMPRINTMETHOD
							//detailValue=CommonUtility.getCellValueStrinOrInt(cell);
							imprintMethodVal=CommonUtility.getCellValueStrinOrInt(cell);
							if(!StringUtils.isEmpty(imprintMethodVal)){
							imprintMhtdSet.add(imprintMethodVal);
							
							}
							break;							
						case 6://IMPRINTSIZE
							imprintSizeVal=CommonUtility.getCellValueStrinOrInt(cell);
							if(!StringUtils.isEmpty(imprintSizeVal)){
							imprintSizeSet.add(imprintSizeVal);
							if(availMapIMTD.containsKey(imprintMethodVal)){
							HashSet<String> tempSet=availMapIMTD.get(imprintMethodVal);
							tempSet.add(imprintSizeVal);
							availMapIMTD.replace(imprintMethodVal, tempSet);
								//availMapIMTD.put(key, value);
							}else{
								HashSet<String> newSet=new HashSet<String>();
								newSet.add(imprintSizeVal);
								availMapIMTD.put(imprintMethodVal, newSet);
							}
							}
							break;
						case 7://IMPRINTLOCATION
							//detailValue=CommonUtility.getCellValueStrinOrInt(cell);
							imprintLocVal=CommonUtility.getCellValueStrinOrInt(cell);
							if(!StringUtils.isEmpty(imprintLocVal)){
							imprintLocSet.add(imprintLocVal);
								
								if(availMapIMLOC.containsKey(imprintLocVal)){
									HashSet<String> tempSetLoc=availMapIMLOC.get(imprintLocVal);
									tempSetLoc.add(imprintSizeVal);
									availMapIMLOC.replace(imprintLocVal, tempSetLoc);
										//availMapIMTD.put(key, value);
									}else{
										HashSet<String> newSetLoc=new HashSet<String>();
										newSetLoc.add(imprintSizeVal);
										availMapIMLOC.put(imprintLocVal, newSetLoc);
									}
							}
							break;
						case 8://IMPRINTNETSETUPCHARGE //ignore
							break;
						case 9://IMPRINTNETRUNNINGCHARGE //ignore
							break;
						case 10://IMPRINTGROSSSETUPCHARGE
							setUpChrgValue=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(setUpChrgValue.equals("0") || setUpChrgValue.equals("0.0") || setUpChrgValue.toUpperCase().equals("NULL") || StringUtils.isEmpty(setUpChrgValue)){
								setUpChrgValue="EMPTY";
							}
							
							break;
						case 11://IMPRINTGROSSRUNNINGCHARGE
							runChrgValue=CommonUtility.getCellValueStrinOrDecimal(cell);
							if(runChrgValue.equals("0") || setUpChrgValue.equals("0.0") || runChrgValue.toUpperCase().equals("NULL") || StringUtils.isEmpty(runChrgValue)){
								runChrgValue="EMPTY";
							}
							String priceValue=setUpChrgValue+"@@@@@"+runChrgValue;
							if(!StringUtils.isEmpty(imprintMethodVal)){
							if(!priceMap.containsKey(imprintMethodVal)){
							priceMap.put(imprintMethodVal, priceValue);
							}
							}
							break;
							
							
				}  // end inner while loop					 
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
		
		if(!CollectionUtils.isEmpty(imprintMhtdSet)){
			 List<ImprintMethod> listOfImprintMethod= primeLineAttriObj.getImprintMethods(new ArrayList<String>(imprintMhtdSet));
			 productConfigObj.setImprintMethods(listOfImprintMethod);
		    }
		 if(!CollectionUtils.isEmpty(imprintSizeSet)){
			 List<ImprintSize> listOfImprintSizeNew=primeLineAttriObj.getProductImprintSize(new ArrayList<String>(imprintSizeSet));
			 productConfigObj.setImprintSize(listOfImprintSizeNew);
		    }
		 
	    if(!CollectionUtils.isEmpty(imprintLocSet)){
	        List<ImprintLocation> listOfImprintLoc= primeLineAttriObj.getImprintLocationVal(new ArrayList<String>(imprintLocSet));
		 	productConfigObj.setImprintLocation(listOfImprintLoc);
	     	}
	  //if(!CollectionUtils.isEmpty(imprintSizeSet) && !CollectionUtils.isEmpty(imprintMhtdSet)){
	    if(!CollectionUtils.isEmpty(availMapIMTD)){
	    	//availibilityList=primeLineAttriObj.getProductAvailablity(imprintSizeSet,imprintMhtdSet,"Imprint Size" ,"Imprint Method",availibilityList);
	    	availibilityList=primeLineAttriObj.getProductAvailablity(availMapIMTD,"Imprint Method","Imprint Size",availibilityList);
	    }
    	//if(!CollectionUtils.isEmpty(imprintSizeSet) && !CollectionUtils.isEmpty(imprintLocSet)){
	    if(!CollectionUtils.isEmpty(availMapIMLOC)){
    		//availibilityList=primeLineAttriObj.getProductAvailablity(imprintSizeSet,imprintLocSet,"Imprint Size" ,"Imprint Location",availibilityList);
    		availibilityList=primeLineAttriObj.getProductAvailablity(availMapIMLOC,"Imprint Location","Imprint Size",availibilityList);
    	}
   	 if(!CollectionUtils.isEmpty(availibilityList)){
   	productExcelObj.setAvailability(availibilityList);
   	 }
	    if(!CollectionUtils.isEmpty(priceMap)){
	    	
	    	List<PriceGrid> existingPriceGrids = productExcelObj.getPriceGrids();
	    	for (Entry<String, String> entryVal : priceMap.entrySet()) {
				String priceValues=entryVal.getValue();
				String criteriaValue=entryVal.getKey();
				String upChargeType="Set-up Charge";
				String upchargeUsageType="Other";
				String tempArr[]=priceValues.split("@@@@@");
				int length=tempArr.length;
				
				for (int i = 0; i < length;i++) {
					if(!tempArr[i].equals("EMPTY")){
						if(i==0){
							upChargeType="Set-up Charge";
						}else if(i==1){
							upChargeType="Run Charge";
						}
						existingPriceGrids=primeLinePriceGridParser.getPriceGrids(tempArr[i],"1","V","USD","",
								false, "False", criteriaValue, 
								"Imprint Method", i+1,upChargeType, upchargeUsageType,
								existingPriceGrids);
					}
				}
			}
	    	 productExcelObj.setPriceGrids(existingPriceGrids);
	    }
	    //else here gie quote upon request
	   
	    productExcelObj.setProductConfigurations(productConfigObj);
		 _LOGGER.info("Product Data from sheet 4 ItemImprint tab: "+ mapperObj.writeValueAsString(productExcelObj));
		 sheetMapReturn.put(productId, productExcelObj);
		 //}
			//productConfigObj = new ProductConfigurations();
			repeatRows.clear();
			imprintSizeVal="";
			imprintMethodVal="";
			imprintLocVal="";
			setUpChrgValue="";
			runChrgValue="";
		    imprintSizeSet=new HashSet<String>();
		    imprintLocSet=new HashSet<String>();
		    imprintMhtdSet=new HashSet<String>();
		    priceMap=new HashMap<String, String>();
		    availibilityList=new ArrayList<Availability>();
		    availMapIMLOC=new HashMap<String, HashSet<String>>();
		    availMapIMTD=new HashMap<String, HashSet<String>>();
		productDaoObj.saveErrorLog(asiNumber,batchId);
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
		if(columnIndex != 1 && columnIndex != 2 && columnIndex != 3 && columnIndex != 4 && columnIndex != 5 && columnIndex != 6 && columnIndex != 7 && columnIndex != 10 && columnIndex != 11){
		//if(columnIndex != 1&&columnIndex != 3&&columnIndex != 4 && columnIndex != 6 && columnIndex != 9 && columnIndex != 24){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
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
	
	public PrimeLineAttributeParser getPrimeLineAttriObj() {
		return primeLineAttriObj;
	}

	public void setPrimeLineAttriObj(PrimeLineAttributeParser primeLineAttriObj) {
		this.primeLineAttriObj = primeLineAttriObj;
	}

	public PrimeLinePriceGridParser getPrimeLinePriceGridParser() {
		return primeLinePriceGridParser;
	}

	public void setPrimeLinePriceGridParser(
			PrimeLinePriceGridParser primeLinePriceGridParser) {
		this.primeLinePriceGridParser = primeLinePriceGridParser;
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
