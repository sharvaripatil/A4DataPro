package com.a4tech.supplier.mapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;

import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.Weight;
import com.a4tech.product.service.imple.PostServiceImpl;
import com.a4tech.supplier.service.ISupplierParser;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.gempire.GempirePriceGridParser;
import parser.gempire.GempireProductAttributeParser;

public class GempirepromotionsMapping implements ISupplierParser {
	private static final Logger _LOGGER = Logger
			.getLogger(GempirepromotionsMapping.class);
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private GempireProductAttributeParser prodAttribute;
	private GempirePriceGridParser pricegrid;
	
	@Override
	public String readExcel(String accessToken, Workbook workbook,
			Integer asiNumber, int batchId, String environmentType) {		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		Set<String> productXids = new HashSet<String>();
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();		
		Product productExcelObj = new Product();
		ProductConfigurations productConfigObj = new ProductConfigurations();
		String productName = null;
		String productId = null;
		String finalResult = null;
		Product existingApiProduct = null;
		int columnIndex = 0;
		String xid = null;
		Cell cell2Data = null;
		String ProdNo = null;
		String description1 =null;
		
		List<ProductionTime> ListOfProductiontime = new ArrayList<ProductionTime>();		
		ShippingEstimate shipObj=new ShippingEstimate();
		List<Weight> ListOfWeight = new ArrayList<Weight>();		
		Weight weightObj=new  Weight();
		Size sizeObj=new Size();
		
		String pricename_1="";
		boolean T =true;
		boolean N =false;
    	    StringJoiner listOfQuantity1 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		    StringJoiner listOfPrices1 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		    StringJoiner listOfDiscounts1 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

		    StringJoiner listOfQuantity2 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		    StringJoiner listOfPrices2 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		    StringJoiner listOfDiscounts2 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		    
		    StringJoiner listOfQuantity3 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		    StringJoiner listOfPrices3 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		    StringJoiner listOfDiscounts3 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		    
		    StringJoiner listOfQuantity4 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		    StringJoiner listOfPrices4 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		    StringJoiner listOfDiscounts4 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		    
		    StringJoiner listOfQuantity5 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		    StringJoiner listOfPrices5 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		    StringJoiner listOfDiscounts5 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		List<String> listOfPricename = new ArrayList<>();
		List<String> listQuantity = new ArrayList<>();
		List<String> listPrice = new ArrayList<>();
		List<String> listDiscount = new ArrayList<>();
		try {

			_LOGGER.info("Total sheets in excel::"
					+ workbook.getNumberOfSheets());
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = sheet.iterator();
			_LOGGER.info("Started Processing Product");

			while (iterator.hasNext()) {

				try {
					Row nextRow = iterator.next();
					if (nextRow.getRowNum() < 1)
						continue;
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					if (productId != null) {
						productXids.add(productId);
					}
					boolean checkXid = false;

					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						/* int */columnIndex = cell.getColumnIndex();
						cell2Data = nextRow.getCell(1);
						if (columnIndex + 1 == 1) {
							if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
								xid = cell.getStringCellValue();
							} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								xid = String.valueOf((int) cell
										.getNumericCellValue());
							} else {
								ProdNo = CommonUtility
										.getCellValueStrinOrInt(cell2Data);
								ProdNo = ProdNo.substring(0, 14);
								xid = ProdNo;
							}
							checkXid = true;
						} else {
							checkXid = false;
						}
						if (checkXid) {
							if (!productXids.contains(xid)) {
								if (nextRow.getRowNum() != 1) {
									System.out
											.println("Java object converted to JSON String, written to file");

									productExcelObj.setPriceGrids(priceGrids);
			
									productExcelObj.setProductConfigurations(productConfigObj);
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
									 listOfPricename = new ArrayList<>();	
									 productConfigObj = new ProductConfigurations();
									 listQuantity = new ArrayList<>();
									 listPrice = new ArrayList<>();
								     listDiscount = new ArrayList<>();
								     listOfQuantity1 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
									 listOfPrices1 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
									 listOfDiscounts1 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								     

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
									 productExcelObj=existingApiProduct;
									 productConfigObj=existingApiProduct.getProductConfigurations();
									 
										List<Image> Img = existingApiProduct
												.getImages();
										productExcelObj.setImages(Img);
										
										List<Theme> themeList=productConfigObj.getThemes();
								    	 productConfigObj.setThemes(themeList);
								    	 
								    	 List<String>categoriesList=existingApiProduct.getCategories();
								    	 productExcelObj.setCategories(categoriesList);
									 
								    	 List<String>keywordList=existingApiProduct.getProductKeywords();
								    	 productExcelObj.setProductKeywords(keywordList);
								}
								// productExcelObj = new Product();
							}
							
						}

						switch (columnIndex + 1) {
						case 1://XID

							productExcelObj.setExternalProductId(xid);
							break;

						case 2:// productcode							
							 ProdNo=cell.getStringCellValue();
								if (!StringUtils.isEmpty(ProdNo)) {
									int ProdNolength=ProdNo.length();	
									if(ProdNolength > 14){
										ProdNo=ProdNo.substring(0, 14);
									}
									productExcelObj.setSummary(ProdNo);	
									}
							 productExcelObj.setAsiProdNo(ProdNo);
							break;
							
						case 3: // productname

							productName = cell.getStringCellValue();
							int len=productName.length();
							 if(len>60){
								String strTemp=productName.substring(0, 60);
								int lenTemp= strTemp.lastIndexOf(ApplicationConstants.CONST_VALUE_TYPE_SPACE);
								productName=(String) strTemp.subSequence(0, lenTemp);
							}
							productExcelObj.setName(productName);
							
							break;	
							
						case 8: // productdescription
							 description1 = cell.getStringCellValue();
							 if (!StringUtils.isEmpty(description1)) {
								 description1="";							
								 }
							
						break;	
					
						case 9: // furtherdescription
							String description2 = cell.getStringCellValue();
							if (!StringUtils.isEmpty(description2)) {
								description2="";
							}
							String description="";
							description=description.concat(description1).concat(description2);
							productExcelObj
										.setDescription(description);
							
						
							break;	
							
						case 23: //Size
							String SizeValue=cell.getStringCellValue();
							if (!StringUtils.isEmpty(SizeValue)) {
								sizeObj=prodAttribute.getSize(SizeValue);
								productConfigObj.setSizes(sizeObj);
							}
							break;	
					
						case 25: // Options & Accessories

							
							break;	
					
						case 26: //Set up Charge

							break;	
							
						case 27: // Plating

							

							break;	
							
						case 28: // Comments
							String AdditionaProductInfo=cell.getStringCellValue();
							if (!StringUtils.isEmpty(AdditionaProductInfo)) {
							 productExcelObj.setAdditionalProductInfo(AdditionaProductInfo);
							}
					
							break;	
							
						case 29: // Options & Accessories1

				
							break;	
							
						case 30: // 2nd Side

					
							break;	
							
						case 31: // Keyfetch

						
							break;	
							
						case 32: //Standard Production
							String ProductionTime=cell.getStringCellValue();
							if (!StringUtils.isEmpty(ProductionTime)) {
								ListOfProductiontime=prodAttribute.getProductiontime(ProductionTime);
								productConfigObj.setProductionTime(ListOfProductiontime);
							}
							
							break;	
							
						case 34: // Carton Weight (Lbs)
							String ShippingWeight=cell.getStringCellValue();
							if (!StringUtils.isEmpty(ShippingWeight)) {
								weightObj.setValue(ShippingWeight);
								weightObj.setUnit("lbs");
								ListOfWeight.add(weightObj);
								shipObj.setWeight(ListOfWeight);
								productConfigObj.setShippingEstimates(shipObj);
							}
							
							break;	

					    case 42: //pricename_1
					    	pricename_1=cell.getStringCellValue();
							if (pricename_1.equalsIgnoreCase("")) {
								pricename_1=productName;
							}
					    	listOfPricename.add(pricename_1);
					    	 pricename_1="";
							break;
							
						 case  44://quantity1_1
						 case  49://quantity2_1
						 case  54://quantity3_1
						 case  59://quantity4_1
								String priceQty1 = CommonUtility.getCellValueStrinOrInt(cell);
								if(!StringUtils.isEmpty(priceQty1) && !priceQty1.equals("0")){
									listOfQuantity1.add(priceQty1);
							//		listQuantity.add(listOfQuantity1.toString());

								}
						
								break;
					
						 case  46://price1_1
				    	 case  51://price2_1
					     case  56://price3_1
						 case  61://price4_1
								String listPricing1 = CommonUtility.getCellValueDouble(cell);
								if(!StringUtils.isEmpty(listPricing1) && !listPricing1.equals("0.0") && !listPricing1.equals("0.00")){
									listOfPrices1.add(listPricing1);
							//		listPrice.add(listOfPrices1.toString());
									
								}
								break;
								
						 case  47://discountcode1_1
						 case  52://discountcode2_1
						 case  57://discountcode3_1
						 case  62://discountcode4_1
								String discountCode1 = CommonUtility.getCellValueStrinOrInt(cell);
								if(!StringUtils.isEmpty(discountCode1)){
									listOfDiscounts1.add(discountCode1);
						//			listDiscount.add(listOfDiscounts1.toString());
								}		
								break;
								
								
						 case  103://pricename_2
							 pricename_1=cell.getStringCellValue();
								listOfPricename.add(pricename_1);
							 	 pricename_1="";
								break;
	
						 case  105://quantity1_2
						 case  110://quantity2_2
						 case  115://quantity3_2
						 case  120://quantity4_2
								String priceQty2 = CommonUtility.getCellValueStrinOrInt(cell);
								if(!StringUtils.isEmpty(priceQty2) && !priceQty2.equals("0")){
									listOfQuantity2.add(priceQty2);
							//	listQuantity.add(listOfQuantity2.toString());

								}
								
								break;
						 case  107://price1_2
						 case  112://price2_2
						 case  117://price3_2	
						 case  122://price4_2
							 String listPricing2 = CommonUtility.getCellValueDouble(cell);
								if(!StringUtils.isEmpty(listPricing2) && !listPricing2.equals("0.0") && !listPricing2.equals("0.00")){
									listOfPrices2.add(listPricing2);
							//		listPrice.add(listOfPrices2.toString());

								}
								
								break;
						 case  108://discountcode1_2
						 case  113://discountcode2_2
						 case  118://discountcode3_2
						 case  123://discountcode4_2
							 String discountCode2 = CommonUtility.getCellValueStrinOrInt(cell);
								if(!StringUtils.isEmpty(discountCode2)){
									listOfDiscounts2.add(discountCode2);
							//		listDiscount.add(listOfDiscounts2.toString());

								}
								
								break;
					
						 case  164://pricename_3
							 pricename_1=cell.getStringCellValue();
								listOfPricename.add(pricename_1);
							 	 pricename_1="";
								break;
					
				
						 case  166://quantity1_3	
						 case  171://quantity2_3
						 case  176://quantity3_3
						 case  181://quantity4_3
								String priceQty3 = CommonUtility.getCellValueStrinOrInt(cell);
								if(!StringUtils.isEmpty(priceQty3) && !priceQty3.equals("0")){
									listOfQuantity3.add(priceQty3);
							//		listQuantity.add(listOfQuantity3.toString());

								} 
							 break;
								
								
						 case  168://price1_3
						 case  173://price2_3
						 case  178://price3_3
						 case  183://price4_3
							 String listPricing3 = CommonUtility.getCellValueDouble(cell);
								if(!StringUtils.isEmpty(listPricing3) && !listPricing3.equals("0.0") && !listPricing3.equals("0.00")){
									listOfPrices3.add(listPricing3);
							//		listPrice.add(listOfPrices3.toString());

								}
							 break;
								
						 case  169://discountcode1_3
						 case  174://discountcode2_3
						 case  179://discountcode3_3
						 case  184://discountcode4_3
							 String discountCode3 = CommonUtility.getCellValueStrinOrInt(cell);
								if(!StringUtils.isEmpty(discountCode3)){
									listOfDiscounts3.add(discountCode3);
							//		listDiscount.add(listOfDiscounts3.toString());

								}
								
								break;
			
						 case  225://pricename_4
							 pricename_1=cell.getStringCellValue();
								listOfPricename.add(pricename_1);
							 	 pricename_1="";
								break;
				
									
						 case  227://quantity1_4
						 case  232://quantity2_4
						 case  237://quantity3_4
						 case  242://quantity4_4
							 String priceQty4 = CommonUtility.getCellValueStrinOrInt(cell);
								if(!StringUtils.isEmpty(priceQty4) && !priceQty4.equals("0")){
									listOfQuantity4.add(priceQty4);
							//		listQuantity.add(listOfQuantity4.toString());

								} 
								break;
								
								
						 case  229://price1_4
						 case  234://price2_4
						 case  239://price3_4
						 case  244://price4_4
							 String listPricing4 = CommonUtility.getCellValueDouble(cell);
								if(!StringUtils.isEmpty(listPricing4) && !listPricing4.equals("0.0") && !listPricing4.equals("0.00")){
									listOfPrices4.add(listPricing4);
							//		listPrice.add(listOfPrices4.toString());

								}
								break;
								
								
						 case  230://discountcode1_4
						 case  235://discountcode2_4
						 case  240://discountcode3_4
				    	 case  245://discountcode4_4
				    		 String discountCode4 = CommonUtility.getCellValueStrinOrInt(cell);
								if(!StringUtils.isEmpty(discountCode4)){
									listOfDiscounts4.add(discountCode4);
							//	listDiscount.add(listOfDiscounts4.toString());

								}
								break;
										
						 case  286://pricename_5
							 pricename_1=cell.getStringCellValue();
								listOfPricename.add(pricename_1);
							 	 pricename_1="";
								break;
					
						 case  288://quantity1_5
						 case  293://quantity2_5
						 case  298://quantity3_5
						 case  303://quantity4_5
							 String priceQty5 = CommonUtility.getCellValueStrinOrInt(cell);
								if(!StringUtils.isEmpty(priceQty5) && !priceQty5.equals("0")){
									listOfQuantity5.add(priceQty5);
							//		listQuantity.add(listOfQuantity5.toString());

								} 
								break;
								
						 case  290://price1_5
						 case  295://price2_5
						 case  300://price3_5
						 case  305://price4_5
							 String listPricing5 = CommonUtility.getCellValueDouble(cell);
								if(!StringUtils.isEmpty(listPricing5) && !listPricing5.equals("0.0") && !listPricing5.equals("0.00")){
									listOfPrices5.add(listPricing5);
							//	listPrice.add(listOfPrices5.toString());

								}
								break;
																
						 case  291://discountcode1_5
						 case  296://discountcode2_5
				    	 case  301://discountcode3_5
				    	 case  306://discountcode4_5
				    		 String discountCode5 = CommonUtility.getCellValueStrinOrInt(cell);
								if(!StringUtils.isEmpty(discountCode5)){
									listOfDiscounts5.add(discountCode5);
								//	listDiscount.add(listOfDiscounts5.toString());

								}
								break;

						} // end inner while loop

					}
					// set product configuration objects

					// end inner while loop
					productExcelObj.setPriceType("L");
					listQuantity.add(listOfQuantity1.toString());
					listPrice.add(listOfPrices1.toString());
					listDiscount.add(listOfDiscounts1.toString());
					
					listQuantity.add(listOfQuantity2.toString());
					listPrice.add(listOfPrices2.toString());
					listDiscount.add(listOfDiscounts2.toString());
					
					listQuantity.add(listOfQuantity3.toString());
					listPrice.add(listOfPrices3.toString());
					listDiscount.add(listOfDiscounts3.toString());
					
					listQuantity.add(listOfQuantity4.toString());
					listPrice.add(listOfPrices4.toString());
					listDiscount.add(listOfDiscounts4.toString());
					
					listQuantity.add(listOfQuantity5.toString());
					listPrice.add(listOfPrices5.toString());
					listDiscount.add(listOfDiscounts5.toString());
							
					String names[]=listOfPricename.toArray(new String[listOfPricename.size()]);
					String PriceArr[]=listPrice.toArray(new String[listPrice.size()]);
					String QuantityArr[]=listQuantity.toArray(new String[listQuantity.size()]);
					String discountArr[]=listDiscount.toArray(new String[listDiscount.size()]);

			
					for (int i=0;i<names.length;i++) {
						if(!names[i].equalsIgnoreCase("") && !QuantityArr[i].equalsIgnoreCase("") ){
							priceGrids = pricegrid.getBasePriceGrids(PriceArr[i],
									QuantityArr[i],discountArr[i], "USD",
							         "", T, N,names[i],"",priceGrids);
							}	
					}
			

				} catch (Exception e) {
					_LOGGER.error("Error while Processing ProductId and cause :"
								+ productExcelObj.getExternalProductId()
							+ " "
							+ e.getMessage() + "case" + columnIndex);
				}
			}
			workbook.close();

			
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
			 listOfPricename = new ArrayList<>();	
			 productConfigObj = new ProductConfigurations();
			 listQuantity = new ArrayList<>();
			 listPrice = new ArrayList<>();
		     listDiscount = new ArrayList<>();	
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


	public GempireProductAttributeParser getProdAttribute() {
		return prodAttribute;
	}


	public void setProdAttribute(GempireProductAttributeParser prodAttribute) {
		this.prodAttribute = prodAttribute;
	}


	public GempirePriceGridParser getPricegrid() {
		return pricegrid;
	}


	public void setPricegrid(GempirePriceGridParser pricegrid) {
		this.pricegrid = pricegrid;
	}
	

}
