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



import parser.pslcad.PSLcadPriceGridParser;
import parser.pslcad.PSLcadProductAttributeParser;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.BatteryInformation;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class PSLcadMapping implements IExcelParser {

	private static final Logger _LOGGER = Logger.getLogger(PSLMapping.class);
		
		private PostServiceImpl postServiceImpl;
		private ProductDao productDaoObj;
		private LookupServiceData lookupServiceDataObj;
		private PSLcadProductAttributeParser pslcadattributObj;
		private PSLcadPriceGridParser pslcadPriceGridObj;

		
		public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
			
			  List<String> numOfProductsSuccess = new ArrayList<String>();
			  List<String> numOfProductsFailure = new ArrayList<String>();
			
			  Set<String>  productXids = new HashSet<String>();
			  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
			  List<BatteryInformation> batteryInfoList = new ArrayList<BatteryInformation>();
		      List<Packaging> listOfPackaging = new ArrayList<Packaging>();
		      List<ImprintMethod> imprintMethodsList = new ArrayList<ImprintMethod>();
		      List<String> complianceList = new ArrayList<String>();
		      List<Material> listOfMaterial = new ArrayList<>();
		      List<ImprintLocation> listOfImprintLocation = new ArrayList<>();
		      ImprintLocation imprintLoactionObj=new ImprintLocation();
		      List<ImprintSize> listOfImprintSize= new ArrayList<>();
		      ImprintSize imprintSizeObj=new ImprintSize();
		      Size sizeObj=new Size();
			  List<Color> colorList = new ArrayList<Color>();




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
			  String priceIncludesValue="";
			  String listPrice = null;		
			  StringBuilder listOfPrices = new StringBuilder();
			  StringBuilder shippingEstimation = new StringBuilder();
			  ShippingEstimate shippingEstimationObj=new ShippingEstimate();
			  StringBuilder listQuantity = new StringBuilder();


			try{
				 
			_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
		    Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = sheet.iterator();
			_LOGGER.info("Started Processing Product");
		
			while (iterator.hasNext()) {
				
				try{
				Row nextRow = iterator.next();
				if (nextRow.getRowNum() < 2)
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
						    ProdNo=ProdNo.substring(0, 14);
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
									  
									 batteryInfoList = new ArrayList<BatteryInformation>();
								     listOfPackaging = new ArrayList<Packaging>();
								     imprintMethodsList = new ArrayList<ImprintMethod>();
								     complianceList = new ArrayList<String>();
								     listOfMaterial = new ArrayList<>();
								     listOfImprintLocation = new ArrayList<>();
								     imprintLoactionObj=new ImprintLocation();
								     listOfImprintSize= new ArrayList<>();
								     imprintSizeObj=new ImprintSize();
								     sizeObj=new Size();
									  colorList = new ArrayList<Color>();
									  listOfPrices = new StringBuilder();
									  shippingEstimation = new StringBuilder();
									  shippingEstimationObj=new ShippingEstimate();
									  listQuantity = new StringBuilder();


									
							 }
							 if(!productXids.contains(xid)){
							    	productXids.add(xid.trim());
							    }
						        existingApiProduct = postServiceImpl.getProduct(accessToken, xid=xid.replace("\t",""));
							     if(existingApiProduct == null){
							    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
							    	 productExcelObj = new Product();
							     }else{
							  	  //  productExcelObj=existingApiProduct;
								 //   productConfigObj=existingApiProduct.getProductConfigurations();
							    	 
							    	 List<Image> Img=existingApiProduct.getImages();
							    	 productExcelObj.setImages(Img);
							    	 
							    	 List<Theme>themeList=productConfigObj.getThemes();
							    	 productConfigObj.setThemes(themeList);
							    	 
							    	 List<String>categoriesList=existingApiProduct.getCategories();
							    	 productExcelObj.setCategories(categoriesList);
							    	 
							    	 List<String>keywordList=existingApiProduct.getProductKeywords();
							    	 productExcelObj.setProductKeywords(keywordList);
							    	 
							     }
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
						
					case 6://10	
						listPrice = CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice)){
						 listOfPrices= listOfPrices.append(listPrice).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					     listQuantity=listQuantity.append("10").append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						}
						   break;
					case 7: //25
						listPrice = CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice)){
						 listOfPrices= listOfPrices.append(listPrice).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					     listQuantity=listQuantity.append("25").append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						}
						   break;
					case 8://50
						listPrice = CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice)){
						 listOfPrices= listOfPrices.append(listPrice).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					     listQuantity=listQuantity.append("50").append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						}
						   break;
					case 9: //100
						listPrice = CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice)){
						 listOfPrices= listOfPrices.append(listPrice).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					     listQuantity=listQuantity.append("100").append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						}
						   break;
					case 10: //250
						listPrice = CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice)){
						 listOfPrices= listOfPrices.append(listPrice).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					     listQuantity=listQuantity.append("250").append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						}
						   break;
					case 11: //501
						listPrice = CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice)){
						 listOfPrices= listOfPrices.append(listPrice).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					     listQuantity=listQuantity.append("501").append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						}
						   break;
					case 12: //1001+
						
						listPrice = CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(listPrice)){
						 listOfPrices= listOfPrices.append(listPrice).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					     listQuantity=listQuantity.append("1001").append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						}
						break;	
						
					case 13: //Logo Setup Charge ( G )
						break;
						
						
					case 14://Imprint Details
						String ImprintDetails=cell.getStringCellValue();						
						imprintLoactionObj.setValue(ImprintDetails);
						listOfImprintLocation.add(imprintLoactionObj);
						productConfigObj.setImprintLocation(listOfImprintLocation);
						
						break;
												
					case 15:  //Shipping
						String ShippingInfo=cell.getStringCellValue();
						productExcelObj.setAdditionalShippingInfo(ShippingInfo);
						break;
						
					case 16:  //Description
						String description = cell.getStringCellValue();
						if(!StringUtils.isEmpty(description)){
						productExcelObj.setDescription(description);
						}else{
							productExcelObj.setDescription(ApplicationConstants.CONST_STRING_EMPTY);
						}						
						break;
						
					case 17:  //Category
						
						break;
					
					case 18:  //Subcategory
					
						break;

					case 19: //Brand
							
						break;
						
					case 20: //Colors
						String ColorValue=cell.getStringCellValue();
						if(!StringUtils.isEmpty(ColorValue))
						{
						colorList=pslcadattributObj.getColorCriteria(ColorValue);
						productConfigObj.setColors(colorList);
						}
					
						break;						
					case 21://Product Size (cm)
						
						
						break;
					case 22: //Product Size (inch)
						String ProductSize=cell.getStringCellValue();
						if(!StringUtils.isEmpty(ProductSize)){
							ProductSize=ProductSize.replace("inch", "").replace("max.", "").replace("Ø", "");
						sizeObj=pslcadattributObj.getSizeValue(ProductSize);
						productConfigObj.setSizes(sizeObj);
						}
						
						  break;
					
					case 23: //Print Size (cm)
				
						 
						break;
					
					 case 24: //Print Size (inch)
						 String imprintSize=cell.getStringCellValue();
						 imprintSizeObj.setValue(imprintSize);
						 listOfImprintSize.add(imprintSizeObj);
						 productConfigObj.setImprintSize(listOfImprintSize);
								
						break;
						
					case 25:  //Print Tech
						String ImprintMethod=cell.getStringCellValue();
						imprintMethodsList=pslcadattributObj.getImprintMethodValue(ImprintMethod);
						productConfigObj.setImprintMethods(imprintMethodsList);
	           
						break;
						
					case 26: //Battery
						
						String BatteryInfo=cell.getStringCellValue();
						batteryInfoList=pslcadattributObj.getBatteyInfo(BatteryInfo);
						if(!StringUtils.isEmpty(BatteryInfo))
						{
						productConfigObj.setBatteryInformation(batteryInfoList);
						}
						break;
						
					case 27: //Packaging
						String PackageInfo=cell.getStringCellValue();
						listOfPackaging=pslcadattributObj.getPackageInfo(PackageInfo);
						if(!StringUtils.isEmpty(PackageInfo))
						{
							
							productConfigObj.setPackaging(listOfPackaging);
						}
						
						break;
						
						
					case 28: //Quantity per Box
						String ShippingItems=CommonUtility.getCellValueStrinOrInt(cell);
						shippingEstimation=shippingEstimation.append(ShippingItems).append("@@");
				  
					   break;
						
					case 29: //Carton Measurements
			
					   break;
					   
					case 30:  // Carton Measurements (inch)			
						String ShippingDimension=CommonUtility.getCellValueStrinOrInt(cell);
						ShippingDimension=ShippingDimension.replace("inch", "");
						shippingEstimation=shippingEstimation.append(ShippingDimension).append("@@");


						break;
					case 31: //Weight per piece (kg)
						
						break;
					case 32: //Weight per piece (lb)
						String ShippingWeight=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(ShippingWeight)){
						
						if(ShippingWeight.length() >7){
						ShippingWeight=ShippingWeight.substring(0, 6);
						}
						shippingEstimation=shippingEstimation.append(ShippingWeight);
						shippingEstimationObj=pslcadattributObj.getShippingInfo(shippingEstimation);
						productConfigObj.setShippingEstimates(shippingEstimationObj);

						}
						
						break;
					case 33://Carton Weight (kg)
						
						break;
					case 34://Carton Weight (lb)


						break;
					case 35://Materials
						String MaterialValue=cell.getStringCellValue();
						if(!StringUtils.isEmpty(MaterialValue)){
						listOfMaterial = pslcadattributObj.getMaterialList(MaterialValue);
						productConfigObj.setMaterials(listOfMaterial);
					}
						break;
					case 36:  //HS code
						
						break;
					case 37://Certificates
						String  CertificateValue=cell.getStringCellValue();
						if(!CertificateValue.equalsIgnoreCase("NA")){
						complianceList=pslcadattributObj.getCompliance(CertificateValue);
						productExcelObj.setComplianceCerts(complianceList);
						}
						break;
				
				}  // end inner while loop
						 
			}
				// set  product configuration objects
				
				 // end inner while loop
				productExcelObj.setPriceType("L");
			
			/*if(!StringUtils.isEmpty(listPrice)){
				priceGrids = pslPriceGridObj.getPriceGrids(listOfPrices.toString(), 
						listQuantity.toString(), "C", "USD",
					                       		         priceIncludesValue, true, "true", productName,"");	
				}
			
				{*/
			 priceGrids = pslcadPriceGridObj.getPriceGrids("", 
							"", "", "CAD",
								         priceIncludesValue, true, "true", productName,"");	
			//	}
				}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() +"case" +columnIndex);		 
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
		       
		         priceGrids = new ArrayList<PriceGrid>();
				 productConfigObj = new ProductConfigurations();
				 batteryInfoList = new ArrayList<BatteryInformation>();
			     listOfPackaging = new ArrayList<Packaging>();
			     imprintMethodsList = new ArrayList<ImprintMethod>();
			     complianceList = new ArrayList<String>();
			     listOfMaterial = new ArrayList<>();
			     listOfImprintLocation = new ArrayList<>();
			     imprintLoactionObj=new ImprintLocation();
			     listOfImprintSize= new ArrayList<>();
			     imprintSizeObj=new ImprintSize();
			     sizeObj=new Size();
				 colorList = new ArrayList<Color>();
				  listOfPrices = new StringBuilder();
				  shippingEstimation = new StringBuilder();
				  shippingEstimationObj=new ShippingEstimate();
				  listQuantity = new StringBuilder();
		       		   
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

		public PSLcadProductAttributeParser getPslcadattributObj() {
			return pslcadattributObj;
		}

		public void setPslcadattributObj(PSLcadProductAttributeParser pslcadattributObj) {
			this.pslcadattributObj = pslcadattributObj;
		}

		public PSLcadPriceGridParser getPslcadPriceGridObj() {
			return pslcadPriceGridObj;
		}

		public void setPslcadPriceGridObj(PSLcadPriceGridParser pslcadPriceGridObj) {
			this.pslcadPriceGridObj = pslcadPriceGridObj;
		}

	

	
		
		
	}
