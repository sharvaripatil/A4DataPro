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

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.service.imple.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.radious.RadiousAttribute;
import parser.radious.RadiousColorParser;
import parser.radious.RadiousPriceGridParser;

public class RadiousMapping implements IExcelParser {
private static final Logger _LOGGER = Logger.getLogger(HarvestIndustrialExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private LookupServiceData lookupServiceDataObj;
	private RadiousColorParser radiousColorObj;
	private RadiousAttribute radiousAttribute;
	private RadiousPriceGridParser radiousPricegrid;

	

	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		Set<String> productXids = new HashSet<String>();
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		List<ImprintLocation> listImprintLocation = new ArrayList<ImprintLocation>();
		List<ImprintMethod> listOfImprintMethods = new ArrayList<ImprintMethod>();
		List<ProductionTime> listOfProductionTime = new ArrayList<ProductionTime>();
		List<String> productKeywords = new ArrayList<String>();
		List<Theme> themeList = new ArrayList<Theme>();
		 List<Theme> exstlist  = new ArrayList<Theme>();
		List<Color> color = new ArrayList<Color>();		
		List<Material> listOfMaterial = new ArrayList<Material>();		


		Product productExcelObj = new Product();
		ProductConfigurations productConfigObj = new ProductConfigurations();
		ShippingEstimate shipping = new ShippingEstimate();
		String productName = null;
		String productId = null;
		String finalResult = null;
		RushTime rushTime = new RushTime();
		String cartonWeight="";
		String cartonWidth="";
		String cartonLength="";
		String cartonHeight="";
		String unitsperCarton="";
		String size="";
		String quantity="";
		String listPrice="";
		StringBuilder listOfQuantity = new StringBuilder();
		StringBuilder listOfPrices = new StringBuilder();
		
		ShippingEstimate shipingEstObj = new ShippingEstimate();
		Size sizeObj=new Size();

		try {

			_LOGGER.info("Total sheets in excel::"
					+ workbook.getNumberOfSheets());
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = sheet.iterator();
			_LOGGER.info("Started Processing Product");

			String themeValue = null;
		
			String unitsPerCarton = null;
			Product existingApiProduct = null;
			Cell cell2Data = null;
			
			
			while (iterator.hasNext()) {

				try {
					Row nextRow = iterator.next();
					if (nextRow.getRowNum() < 1)//
						continue;
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					if (productId != null) {
						productXids.add(productId);
					}
					boolean checkXid = false;

					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						String xid = null;
						int columnIndex = cell.getColumnIndex();
						cell2Data = nextRow.getCell(1);
						if (columnIndex + 1 == 1) {
							if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
								xid = cell.getStringCellValue();
							} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								xid = String.valueOf((int) cell
										.getNumericCellValue());
							} else {
								String ProdNo = CommonUtility
										.getCellValueStrinOrInt(cell2Data);
								xid = ProdNo;

							}
							checkXid = true;
						} else {
							checkXid = false;
						}
						if (checkXid) {
							if (!productXids.contains(xid)) {
								if (nextRow.getRowNum() != 1) { //totes factory
									System.out
											.println("Java object converted to JSON String, written to file");

									/*if (!StringUtils.isEmpty(themeValue)) {
										productConfigObj.setThemes(themeList);
									}*/
									productConfigObj.setImprintLocation(listImprintLocation);
								
								
							
									if (!StringUtils.isEmpty(unitsPerCarton)) {
										productConfigObj
												.setShippingEstimates(shipping);
									}
									productConfigObj
											.setImprintMethods(listOfImprintMethods);

									productExcelObj.setPriceGrids(priceGrids);
									productExcelObj
											.setProductConfigurations(productConfigObj);

									int num = postServiceImpl.postProduct(
											accessToken, productExcelObj,
											asiNumber, batchId, environmentType);
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

									
									priceGrids = new ArrayList<PriceGrid>();
									exstlist = new ArrayList<Theme>();
									listImprintLocation = new ArrayList<ImprintLocation>();
									listOfImprintMethods = new ArrayList<ImprintMethod>();
									listOfProductionTime = new ArrayList<ProductionTime>();
									productKeywords = new ArrayList<String>();
									themeList = new ArrayList<Theme>();
									color = new ArrayList<Color>();									
									rushTime = new RushTime();
								    listOfQuantity = new StringBuilder();
								    listOfPrices = new StringBuilder();
									productConfigObj = new ProductConfigurations();
									
																	}
								if (!productXids.contains(xid)) {
									productXids.add(xid.trim());
								}
								existingApiProduct = postServiceImpl
										.getProduct(accessToken,
												xid = xid.replace("\t", ""), environmentType);
								if (existingApiProduct == null) {
									_LOGGER.info("Existing Xid is not available,product treated as new product");
									productExcelObj = new Product();
								} else {
									productExcelObj = new Product();
									
									List<Image> Img = existingApiProduct.getImages();
									productExcelObj.setImages(Img);
									

							    	 themeList=productConfigObj.getThemes();
							    	 productConfigObj.setThemes(exstlist);
							    	 
							    	 List<String>categoriesList=existingApiProduct.getCategories();
							    	 productExcelObj.setCategories(categoriesList);
							    	 
							    	
							    	 							    	 
								}
								// productExcelObj = new Product();
							}
						}

					
				switch (columnIndex + 1) {
				case 1:// XID
 					productExcelObj.setExternalProductId(xid.trim());

					break;
				case 2:// productcode
					String productCode = cell.getStringCellValue();
					if(productCode.length() > 14)
					{
						productExcelObj.setProductLevelSku(productCode);
					}else{
					productExcelObj.setAsiProdNo(productCode);
					}

					break;
				case 3:// productname
					productName = cell.getStringCellValue();
					if(productName.length() > 60)
					{
						productName=productName.substring(0, 60);
					}
					productExcelObj.setName(productName);
					break;
					
				case 8:// productdescription
					String description = cell.getStringCellValue();
					description=description.replace("<p>", "").replace("</p>", "")
					.replace("&","'").replace("#","'").replace("3","'").replace("9","'")
					.replace(";","'");
					if(description.length() > 800)
					{
						productName=productName.substring(0, 800);
					}
					productExcelObj.setDescription(description);
				
					break;

				case 12: // tagattributes
					String productKeyword = cell.getStringCellValue();
					productKeyword=productKeyword.replace("®", "R").replace(" ’", " '");
					String KeywordArr[] = productKeyword.toLowerCase().split(",");
                   if(!productKeywords.contains(productKeyword.toLowerCase())){
				 	for (String string : KeywordArr) {
					if(!(string.length()>30)){
						productKeywords.add(string);
					}}
					productExcelObj.setProductKeywords(productKeywords);
				    }
					break;

			
				case 25: // Fabric Colors / Patterns  Available
					String colorValue = cell.getStringCellValue();
					if (!StringUtils.isEmpty(colorValue)&& !colorValue.equalsIgnoreCase("N/A")) {
						color = radiousColorObj
								.getColorCriteria(colorValue);
						productConfigObj.setColors(color);
					}


					break;
				case 26: // Material
			       	 String material=cell.getStringCellValue();
					  if (!StringUtils.isEmpty(material)){
					    listOfMaterial=RadiousAttribute.getMaterial(material);
						productConfigObj.setMaterials(listOfMaterial);  
					  }
					break;
				
				case 28://IMPRINT
					String imprintMethod=cell.getStringCellValue();
					if (!StringUtils.isEmpty(imprintMethod)){
						listOfImprintMethods=RadiousAttribute.getImprintMethod(imprintMethod);
						productConfigObj.setImprintMethods(listOfImprintMethods);
					}
										
					break;
					
				case 29://SIZE
				    size=cell.getStringCellValue();
					if (!StringUtils.isEmpty(size)){
						sizeObj=RadiousAttribute.getSize(size);
						productConfigObj.setSizes(sizeObj);
					}
					break;
					
        

				case 31://HEIGHT
					String fullSize=cell.getStringCellValue();
					if (!StringUtils.isEmpty(fullSize)){
                     productExcelObj.setAdditionalProductInfo(fullSize);
						
					}
					break;
				
				case 39://Carton Weight
					String cartonWeight1=CommonUtility.getCellValueStrinOrDecimal(cell);
					if((!StringUtils.isEmpty(cartonWeight1)) && !cartonWeight1.equalsIgnoreCase("0"))
					{
						cartonWeight=cartonWeight1;
						
					}
					break;
				case 40://Carton Width
					String cartonWidth1=CommonUtility.getCellValueStrinOrDecimal(cell);
					if((!StringUtils.isEmpty(cartonWidth1))&& !cartonWidth1.equalsIgnoreCase("0")) {
						cartonWidth=cartonWidth1;
				      }
					
					break;
				case 41://Carton Height
					String cartonHeight1=CommonUtility.getCellValueStrinOrDecimal(cell);
					if((!StringUtils.isEmpty(cartonHeight1)) && !cartonHeight1.equalsIgnoreCase("0")){
						cartonHeight=cartonHeight1;

					}
					break;
				case 42://Carton Length
					String cartonLength1=CommonUtility.getCellValueStrinOrDecimal(cell);
					if((!StringUtils.isEmpty(cartonLength1)) && !cartonLength1.equalsIgnoreCase("0") ){
						cartonLength=cartonLength1;
					}
					
					break;
				case 43://Units Per Carton
					String unitsperCarton1=CommonUtility.getCellValueStrinOrDecimal(cell);
					if((!StringUtils.isEmpty(unitsperCarton1)) && !unitsperCarton1.equalsIgnoreCase("0")){
						unitsperCarton=unitsperCarton1;
					}
					shipingEstObj = radiousAttribute.getShippingEstimates(cartonWeight, cartonWidth,cartonHeight,cartonLength,
					unitsperCarton);
					productConfigObj.setShippingEstimates(shipingEstObj);
						
					break;

				case 48: // ProductionTime_1
				String prodTime= CommonUtility.getCellValueStrinOrInt(cell);
				if (!StringUtils.isEmpty(prodTime)){
				String prodTimeIndays=prodTime.replace("(Qty: 1-25)","").replace("Bus. Days", "").replace("Bus. Day", "").replace("Call", "");
		       	ProductionTime productionTime = new ProductionTime();
		       	productionTime.setBusinessDays(prodTimeIndays);
		       	productionTime.setDetails(prodTime);
				listOfProductionTime.add(productionTime);
				productConfigObj.setProductionTime(listOfProductionTime);

				}
					break;
			
				case 50:// quantity1_1
					quantity = CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(quantity)){			
		        	listOfQuantity.append(quantity);
					}
					break;

				case 52:// price1_1
					listPrice = CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(listPrice)){			
		           	listOfPrices.append(listPrice);
					}

					break;
				case 53:// discountcode1_1

					break;
			
				case 55:// quantity2_1
					quantity = CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(quantity)){			
		        	listOfQuantity.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(quantity);
					}

					break;

				case 57:// price2_1
					listPrice = CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(listPrice)){			
		           	listOfPrices.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(listPrice);
					}
					break;
				case 58:// discountcode2_1

					break;
				
				case 60:// quantity3_1
					quantity = CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(quantity)){			
		        	listOfQuantity.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(quantity);
					}

					break;

				case 62:// price3_1
					listPrice = CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(listPrice)){			
		           	listOfPrices.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(listPrice);
					}

					break;
				case 63:// discountcode3_1

					break;
			
				case 65:// quantity4_1
					quantity = CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(quantity)){			
		        	listOfQuantity.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(quantity);
					}

					break;

				case 67:// price4_1
					listPrice = CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(listPrice)){			
		           	listOfPrices.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(listPrice);
					}
					break;
					
				case 68:// discountcode4_1

					break;
				
				case 70:// quantity5_1
					quantity = CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(quantity)){			
		        	listOfQuantity.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(quantity);
					}
					break;

				case 72:// price5_1
					listPrice = CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(listPrice)){			
		           	listOfPrices.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(listPrice);
					}
					break;
				case 73:// discountcode5_1

					break;
		
				case 75://quantity6_1	
					quantity = CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(quantity)){			
		        	listOfQuantity.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(quantity);
					}
					break;
				
			
				case 77:// price6_1
					listPrice = CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(listPrice)){			
		           	listOfPrices.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(listPrice);
					}
					break;
				case 78: // discountcode6_1

					break;
					    
				case 80: // quantity7_1
					quantity = CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(quantity)){			
		        	listOfQuantity.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(quantity);
					}
					break;

				case 82: // price7_1
					listPrice = CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(listPrice)){			
		           	listOfPrices.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(listPrice);
					}
					  	break;
					  	
				case 83:// discountcode7_1
				
					 break;
				
				case 85:  //quantity8_1
					quantity = CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(quantity)){			
		        	listOfQuantity.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(quantity);
					}
					 break;

				case 87: // price8_1
					listPrice = CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(listPrice)){			
		           	listOfPrices.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(listPrice);
					}
					break;

				case 88: // discountcode8_1

					break;

				
				case 90: // quantity9_1
					quantity = CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(quantity)){			
		        	listOfQuantity.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(quantity);
					}
					break;
				case 92: // price9_1
					listPrice = CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(listPrice)){			
		           	listOfPrices.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(listPrice);
					}
					break;

				case 93: // discountcode9_1
					
					break;
			
				case 95: // quantity10_1
					quantity = CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(quantity)){			
		        	listOfQuantity.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(quantity);
					}
					
					break;
			
				case 97: // price10_1
					listPrice = CommonUtility.getCellValueStrinOrInt(cell);
					if (!StringUtils.isEmpty(listPrice)){			
		           	listOfPrices.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).append(listPrice);
					}
					break;
				case 98: // discountcode10_1

					break;
			
				case 101: // expirationdate_1
					String confirmDate=cell.getStringCellValue();
					if (!StringUtils.isEmpty(confirmDate)){
						productExcelObj
						.setPriceConfirmedThru(confirmDate);
					}

					break;
			
				case 141: // imprintmethod_1
              
					break;

					
				case 142: // imprintlocation_1
					String imprintLocation = cell.getStringCellValue();
						if(!imprintLocation.isEmpty()){
							ImprintLocation locationObj = new ImprintLocation();
							locationObj.setValue(imprintLocation);
							listImprintLocation.add(locationObj);
						}

					break;

				case 145: // setupcharge_1

					break;

				case 157: // qty1_1

					break;

				case 158: // qtyruncharge1_1

					break;

				case 159: // qty2_1

					break;

				case 160: // qtyruncharge2_1

					break;

				case 161: // qty3_1

					break;

				case 162: // qtyruncharge3_1

					break;

				case 163: // qty4_1

					break;

					
				case 164: // qtyruncharge4_1

					break;

				case 165: // qty5_1

					break;

				case 166: // qtyruncharge5_1

					break;

				case 167: // qty6_1

					break;

				case 168: // qtyruncharge6_1

					break;

				case 169: // qty7_1

					break;

				case 170: // qtyruncharge7_1

					break;

				case 171: // qty8_1

					break;

				case 172: // qtyruncharge8_1

					break;

				case 173: // qty9_1

					break;

				case 174: // qtyruncharge9_1

					break;

				case 175: // qty10_1

					break;

				case 176: // qtyruncharge10_1

					break;

				case 177: // rc_discountcode_1

					break;

				case 179: // imprintmethod_2

					break;

				case 180: // imprintlocation_2
					
					break;

				case 183: // setupcharge_2

					break;

				case 195: // qty1_2

					break;

				case 196: // qtyruncharge1_2

					break;

				case 197: // qty2_2

					break;

				case 198: // qtyruncharge2_2

					break;

				case 199: // qty3_2

					break;

				case 200: // qtyruncharge3_2

					break;

				case 201: // qty4_2

					break;

				case 202: // qtyruncharge4_2

					break;

				case 203: // qty5_2

					break;

				case 204: // qtyruncharge5_2

					break;

				case 205: // qty6_2

					break;

				case 206: // qtyruncharge6_2

					break;

				case 207: // qty7_2

					break;

				case 208: // qtyruncharge7_2

					break;

				case 209: // qty8_2

					break;

				case 210: // qtyruncharge8_2

					break;

				case 211: // qty9_2

					break;

				case 212: // qtyruncharge9_2

					break;

				case 213: // qty10_2

					break;

				case 214: // qtyruncharge10_2

					break;

				case 215: // rc_discountcode_2

					break;

								
				}  // end inner while loop
					 
		}
			// set  product configuration objects
			
			 // end inner while loop
			
					productExcelObj.setPriceType("L");
					
	
						priceGrids = radiousPricegrid.getPriceGrids(
								listOfPrices.toString(),
								listOfQuantity.toString(), "R", "USD",
								"", true, "false",
								productName, "", priceGrids);
					

					
					
			/*	priceGrids =  harvestPriceGridObj.getUpchargePriceGrid("1", Screencharge,
							Screenchargecode,
									"Imprint Method", "false", "USD",
									decorationMethod,
									"Screen Charge", "Other",
									new Integer(1),"Required","", priceGrids);	//screen charge
			
					*/
					
					
					 
	
				} catch (Exception e) {
					_LOGGER.error("Error while Processing ProductId and cause :"
							+ productExcelObj.getExternalProductId()
							+ " "
							+ e.getMessage());
				}
			}
			workbook.close();

			if (!StringUtils.isEmpty(themeValue)) {
				productConfigObj.setThemes(themeList);
			}
			productConfigObj.setImprintLocation(listImprintLocation);

			
			productConfigObj.setRushTime(rushTime);

			
			productExcelObj.setPriceGrids(priceGrids);
			productExcelObj.setProductConfigurations(productConfigObj);

			int num = postServiceImpl.postProduct(accessToken, productExcelObj,
					asiNumber, batchId, environmentType);
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
			priceGrids = new ArrayList<PriceGrid>();
			listImprintLocation = new ArrayList<ImprintLocation>();
			listOfImprintMethods = new ArrayList<ImprintMethod>();
			listOfProductionTime = new ArrayList<ProductionTime>();
			productKeywords = new ArrayList<String>();
			themeList = new ArrayList<Theme>();
			color = new ArrayList<Color>();
			productConfigObj = new ProductConfigurations();
			rushTime = new RushTime();
		    listOfQuantity = new StringBuilder();
		    listOfPrices = new StringBuilder();
			exstlist = new ArrayList<Theme>();

			return finalResult;
		} catch (Exception e) {
			_LOGGER.error("Error while Processing excel sheet "
					+ e.getMessage());
			return finalResult;
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet "
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

	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}


	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}

	public RadiousColorParser getRadiousColorObj() {
		return radiousColorObj;
	}


	public void setRadiousColorObj(RadiousColorParser radiousColorObj) {
		this.radiousColorObj = radiousColorObj;
	}


	public RadiousAttribute getRadiousAttribute() {
		return radiousAttribute;
	}


	public void setRadiousAttribute(RadiousAttribute radiousAttribute) {
		this.radiousAttribute = radiousAttribute;
	}


	public RadiousPriceGridParser getRadiousPricegrid() {
		return radiousPricegrid;
	}


	public void setRadiousPricegrid(RadiousPriceGridParser radiousPricegrid) {
		this.radiousPricegrid = radiousPricegrid;
	}


	
}
