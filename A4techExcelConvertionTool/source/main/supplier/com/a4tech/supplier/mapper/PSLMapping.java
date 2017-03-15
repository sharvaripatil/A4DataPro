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

import parser.psl.PSLProductAttributeParser;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.BatteryInformation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class PSLMapping implements IExcelParser {

	private static final Logger _LOGGER = Logger.getLogger(PSLMapping.class);
		
		private PostServiceImpl postServiceImpl;
		private ProductDao productDaoObj;
		private LookupServiceData lookupServiceDataObj;
		private PSLProductAttributeParser attributObj;

		
		public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
			
			  List<String> numOfProductsSuccess = new ArrayList<String>();
			  List<String> numOfProductsFailure = new ArrayList<String>();
			
			  Set<String>  productXids = new HashSet<String>();
			  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
			  List<BatteryInformation> batteryInfoList = new ArrayList<BatteryInformation>();
		      List<Packaging> listOfPackaging = new ArrayList<Packaging>();
		      List<ImprintMethod> imprintMethodsList = new ArrayList<ImprintMethod>();
		      List<String> complianceList = new ArrayList<String>();



			  


			  Product productExcelObj = new Product();   
			  ProductConfigurations productConfigObj=new ProductConfigurations();
			  String productName = null;
			  String productId = null;
			  String finalResult = null;
			  Product existingApiProduct = null;
			  int columnIndex =0;
			  String xid = null;
			  Cell cell2Data = null;
			  String ProdNo=null;



			try{
				 
			_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
		    Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = sheet.iterator();
			_LOGGER.info("Started Processing Product");
		
			while (iterator.hasNext()) {
				
				try{
				Row nextRow = iterator.next();
				if (nextRow.getRowNum() < 1)
					continue;
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				if(productId != null){
					productXids.add(productId);
				}
				 boolean checkXid  = false;
			
				
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					/*int*/ columnIndex = cell.getColumnIndex();
					  cell2Data =  nextRow.getCell(3);	
					if(columnIndex + 1 == 1){
						if(cell.getCellType() == Cell.CELL_TYPE_STRING){
							xid = cell.getStringCellValue();
						}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
							xid = String.valueOf((int)cell.getNumericCellValue());
						}else{
						    ProdNo=CommonUtility.getCellValueStrinOrInt(cell2Data);
							xid=ProdNo;
						}
						checkXid = true;
					}else{
						checkXid = false;
					}
					if(checkXid){
						 if(!productXids.contains(xid)){
							 if(nextRow.getRowNum() != 1){
								 System.out.println("Java object converted to JSON String, written to file");
								 	
									productExcelObj.setPriceGrids(priceGrids);
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
									   
							      
									  priceGrids = new ArrayList<PriceGrid>();
									  productConfigObj = new ProductConfigurations();
									
							 }
							 if(!productXids.contains(xid)){
							    	productXids.add(xid.trim());
							    }
//							    existingApiProduct = postServiceImpl.getProduct(accessToken, xid=xid.replace("\t",""));
//							     if(existingApiProduct == null){
//							    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
//							    	 productExcelObj = new Product();
//							     }else{
//							  	    productExcelObj=existingApiProduct;
//									productConfigObj=existingApiProduct.getProductConfigurations();
//							     
//							     }
								//productExcelObj = new Product();
						 }
					}
					
					

					switch (columnIndex + 1) {
					case 1://xid
				    	
						productExcelObj.setExternalProductId(xid);
						
						 break;
					case 2://Page
							
						  break;
					case 3:
						
						  break;
					case 4://Product code
					
						 productExcelObj.setAsiProdNo(ProdNo);	
	     					
					case 5://Product Name
						 productName = cell.getStringCellValue();
							int len=productName.length();
							 if(len>60){
								String strTemp=productName.substring(0, 60);
								int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
								productName=(String) strTemp.subSequence(0, lenTemp);
							}
							productExcelObj.setName(productName);
						
					    break;
						
					case 6://50
						
						
						break;
						
					case 7: //100
						break;
						
					case 8://250
						 
						break;
						
					case 9: //501+
						break;
						
					case 10: //Logo Setup Charge ( G )
						break;
						
					case 11:  //Description
							
						String description = cell.getStringCellValue();
						if(!StringUtils.isEmpty(description)){
						productExcelObj.setDescription(description);
						}else{
							productExcelObj.setDescription(ApplicationConstants.CONST_STRING_EMPTY);
						}
						break;
						
					case 12:  //Category
						
						
						break;
					
					case 13:  //Subcategory
					
						break;

					case 14: //Brand
							
						break;
						
					case 15: //Colors
					
						
						break;
						
					case 16://Product Size (cm)
						
						
						break;
					case 17: //Product Size (inch)
						
						  break;
					
					case 18: //Print Size (cm)
				
						 
						break;
					
					 case 19: //Print Size (inch)
			
					
					
						break;
						
					case 20:  //Print Tech
						String ImprintMethod=cell.getStringCellValue();
						imprintMethodsList=attributObj.getImprintMethodValue(ImprintMethod);
						productConfigObj.setImprintMethods(imprintMethodsList);
	           
						break;
						
					case 21: //Battery
						
						String BatteryInfo=cell.getStringCellValue();
						batteryInfoList=attributObj.getBatteyInfo(BatteryInfo);
						if(!StringUtils.isEmpty(BatteryInfo))
						{
						productConfigObj.setBatteryInformation(batteryInfoList);
						}
						break;
						
					case 22: //Packaging
						String PackageInfo=cell.getStringCellValue();
						listOfPackaging=attributObj.getPackageInfo(PackageInfo);
						if(!StringUtils.isEmpty(PackageInfo))
						{
							
							productConfigObj.setPackaging(listOfPackaging);
						}
						
						break;
						
						
					case 23: //Quantity per Box
				
					   break;
						
					case 24: //Carton Measurements
			
					   break;
					   
					case 25:  // Carton Measurements (inch)
						
						break;
					case 26: //Weight per piece (kg)
						
						break;
					case 27: //Weight per piece (lb)
						
						break;
					case 28://Carton Weight (kg)
						
						break;
					case 29://Carton Weight (lb)
						
						break;
					case 30://Materials
				
						
						   	break;
					case 31:  //HS code
						
						break;
					case 32://Certificates
						String  CertificateValue=cell.getStringCellValue();
						complianceList=attributObj.getCompliance(CertificateValue);
						productExcelObj.setComplianceCerts(complianceList);
						
						break;
				
				}  // end inner while loop
						 
			}
				// set  product configuration objects
				
				 // end inner while loop
				productExcelObj.setPriceType("L");
				/*if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
					priceGrids = pricegridParserObj.getPriceGrids(listOfPrices.toString(), 
							         listOfQuantity.toString(), priceCode, "USD",
							         priceIncludesValue, true, quoteUponRequest, productName,"",priceGrids);	
				}*/		
				}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() );		 
			}
			}
			workbook.close();
			
			 	
			 	
				productExcelObj.setPriceGrids(priceGrids);
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

		public LookupServiceData getLookupServiceDataObj() {
			return lookupServiceDataObj;
		}

		public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
			this.lookupServiceDataObj = lookupServiceDataObj;
		}

		public PSLProductAttributeParser getAttributObj() {
			return attributObj;
		}

		public void setAttributObj(PSLProductAttributeParser attributObj) {
			this.attributObj = attributObj;
		}
		
		
	}
