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

import parser.radious.RadiousAttribute;
import parser.radious.RadiousColorParser;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Theme;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.CommonUtility;

public class RadiousMapping implements IExcelParser {
private static final Logger _LOGGER = Logger.getLogger(HarvestIndustrialExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private LookupServiceData lookupServiceDataObj;
	private RadiousColorParser radiousColorObj;
	private RadiousAttribute radiousAttribute;
	

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
		
		ShippingEstimate shipingEstObj = new ShippingEstimate();


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
					if (nextRow.getRowNum() < 7)//Totes Factory
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
						cell2Data = nextRow.getCell(2);
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
								if (nextRow.getRowNum() != 7) { //totes factory
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
				case 1:// productcode
					productExcelObj.setExternalProductId(xid.trim());

					break;
				case 2:// productname
					productName = cell.getStringCellValue();
					productExcelObj.setName(productName);
					break;
					
				case 7:// productdescription
					String description = cell.getStringCellValue();
					description=description.replace("<p>", "").replace("</p>", "");
					productExcelObj.setName(description);
				
					break;

				case 11: // tagattributes
					String productKeyword = cell.getStringCellValue();
					productKeyword=productKeyword.replace("®", "R").replace(" ’", " '");
					String KeywordArr[] = productKeyword.toLowerCase().split(",");
                   if(!productKeywords.contains(productKeyword.toLowerCase())){
				 	for (String string : KeywordArr) {
					if(!(string.length()>30)){
						productKeywords.add(string);
					}if(productKeywords.size()==30){
							break;
					}}
					productExcelObj.setProductKeywords(productKeywords);
				    }
					break;

			
				case 24: // Fabric Colors / Patterns  Available
					String colorValue = cell.getStringCellValue();
					if (!StringUtils.isEmpty(colorValue)&& !colorValue.equalsIgnoreCase("N/A")) {
						color = radiousColorObj
								.getColorCriteria(colorValue);
						productConfigObj.setColors(color);
					}


					break;
				case 25: // Material
					
					break;
				
				case 27://IMPRINT
					break;
					
				case 28://SIZE
					break;
				

				case 30://HEIGHT
				
					break;
				
				case 38://Carton Weight
					cartonWeight=CommonUtility.getCellValueStrinOrDecimal(cell);
					
					break;
				case 39://Carton Width
					cartonWidth=CommonUtility.getCellValueStrinOrDecimal(cell);

					
					break;
				case 40://Carton Height
					cartonHeight=CommonUtility.getCellValueStrinOrDecimal(cell);

				
					break;
				case 41://Carton Length
					cartonLength=CommonUtility.getCellValueStrinOrDecimal(cell);

					
					break;
				case 42://Units Per Carton
					unitsperCarton=CommonUtility.getCellValueStrinOrDecimal(cell);
					shipingEstObj = radiousAttribute.getShippingEstimates(cartonWeight, cartonWidth,cartonHeight,cartonLength,
					unitsperCarton);
					productConfigObj.setShippingEstimates(shipingEstObj);
						
					break;

				case 47: // ProductionTime_1
				String prodTime= CommonUtility.getCellValueStrinOrInt(cell);
				if (!StringUtils.isEmpty(prodTime)){
				String prodTimeIndays=prodTime.replace("(Qty: 1-25)","").replace("Bus. Days", "").replace("Call", "");
		       	ProductionTime productionTime = new ProductionTime();
		       	productionTime.setBusinessDays(prodTimeIndays);
		       	productionTime.setDetails(prodTime);
				listOfProductionTime.add(productionTime);
				}
					break;
			
				case 49:// quantity1_1


					break;
				case 50:// quantitytext1_1


					break;
				case 51:// price1_1


					break;
				case 52:// discountcode1_1

					break;
			
				case 54:// quantity2_1

					break;
				case 55:// quantitytext2_1

					break;
				case 56:// price2_1

					break;
				case 57:// discountcode2_1

					break;
				
				case 59:// quantity3_1

					break;
				case 60:// quantitytext3_1

					break;
				case 61:// price3_1


					break;
				case 62:// discountcode3_1

					break;
			
				case 64:// quantity4_1


					break;
				case 65:// quantitytext4_1


					break;
				case 66:// price4_1
					
				case 67:// discountcode4_1

					break;
				
				case 69:// quantity5_1

					break;
				case 70:// quantitytext5_1
				
					break;
				case 71:// price5_1

					break;
				case 72:// discountcode5_1

					break;
		
				case 74://quantity6_1	
					
					break;
				
				case 75: //quantitytext6_1

					break;
			
				case 76:// price6_1

					break;
				case 77: // discountcode6_1

					break;

			
					    
				case 79: // quantity7_1
					
					break;
					
				case 80:   // quantitytext7_1


					break;
						
				  
				case 81: // price7_1
					

					  	break;
					  	
				case 82:	// discountcode7_1
				

					
					    break;
					    
				
					  	
				case 84:  //quantity8_1
					
				
					 break;
				

				case 85: // quantitytext8_1

					break;

				case 86: // price8_1

					break;

				case 87: // discountcode8_1

					break;

				
				case 89: // quantity9_1
  

				case 90: // quantitytext9_1
	    
					break;
				case 91: // price9_1
				
					break;

				case 92: // discountcode9_1
					
					break;
			
				case 94: // quantity10_1
					
					
					break;
				case 95: // quantitytext10_1

					break;
				case 96: // price10_1

					break;
				case 97: // discountcode10_1

					break;
			
				case 100: // expirationdate_1
					String ConfirmDate=cell.getStringCellValue();
					if (!StringUtils.isEmpty(ConfirmDate)){
						productExcelObj
						.setPriceConfirmedThru(ConfirmDate);
					}

					break;
			
				case 140: // imprintmethod_1

					break;

					
				case 141: // imprintlocation_1
					String imprintLocation = cell.getStringCellValue();
						if(!imprintLocation.isEmpty()){
							ImprintLocation locationObj = new ImprintLocation();
							locationObj.setValue(imprintLocation);
							listImprintLocation.add(locationObj);
						}

					break;

				case 144: // setupcharge_1

					break;

				case 156: // qty1_1

					break;

				case 157: // qtyruncharge1_1

					break;

				case 158: // qty2_1

					break;

				case 159: // qtyruncharge2_1

					break;

				case 160: // qty3_1

					break;

				case 161: // qtyruncharge3_1

					break;

				case 162: // qty4_1

					break;

					
				case 163: // qtyruncharge4_1

					break;

				case 164: // qty5_1

					break;

				case 165: // qtyruncharge5_1

					break;

				case 166: // qty6_1

					break;

				case 167: // qtyruncharge6_1

					break;

				case 168: // qty7_1

					break;

				case 169: // qtyruncharge7_1

					break;

				case 170: // qty8_1

					break;

				case 171: // qtyruncharge8_1

					break;

				case 172: // qty9_1

					break;

				case 173: // qtyruncharge9_1

					break;

				case 174: // qty10_1

					break;

				case 175: // qtyruncharge10_1

					break;

				case 176: // rc_discountcode_1

					break;

				case 178: // imprintmethod_2

					break;

				case 179: // imprintlocation_2
					
					break;

				case 182: // setupcharge_2

					break;

				case 194: // qty1_2

					break;

				case 195: // qtyruncharge1_2

					break;

				case 196: // qty2_2

					break;

				case 197: // qtyruncharge2_2

					break;

				case 198: // qty3_2

					break;

				case 199: // qtyruncharge3_2

					break;

				case 200: // qty4_2

					break;

				case 201: // qtyruncharge4_2

					break;

				case 202: // qty5_2

					break;

				case 203: // qtyruncharge5_2

					break;

				case 204: // qty6_2

					break;

				case 205: // qtyruncharge6_2

					break;

				case 206: // qty7_2

					break;

				case 207: // qtyruncharge7_2

					break;

				case 208: // qty8_2

					break;

				case 209: // qtyruncharge8_2

					break;

				case 210: // qty9_2

					break;

				case 211: // qtyruncharge9_2

					break;

				case 212: // qty10_2

					break;

				case 213: // qtyruncharge10_2

					break;

				case 214: // rc_discountcode_2

					break;

								
				}  // end inner while loop
					 
		}
			// set  product configuration objects
			
			 // end inner while loop
			
			//String QuoteRequest=Boolean.toString(quoteUponRequest);
					productExcelObj.setPriceType("L");
				/*	if (listOfPrices != null
							&& !listOfPrices.toString().isEmpty()) {
						priceGrids = harvestPriceGridObj.getPriceGrids(
								listOfPrices.toString(),
								listOfQuantity.toString(), priceCode, "USD",
								priceIncludesValue, true, quoteUponRequest,
								productName, "", priceGrids);
					}

					
					
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

			
			productConfigObj.setProductionTime(listOfProductionTime);
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


	
}
