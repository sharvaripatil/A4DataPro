package parser.proGolf;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.util.StringUtils;

import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.TradeName;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class ProGolfProductInformationMapping{
	private PostServiceImpl             postServiceImpl;  
	private ProductDao                  productDaoObj;
	private ProGolfInformationAttributeParser proInfoAttributeParser;
	private ProGolfPriceGridParser            proGolfPriceGridParser;
	
	private HashMap<String, Product> SheetMap =new HashMap<String, Product>();
    private HashMap<String, String> ProductNoMap =new HashMap<String, String>();
   private static Logger _LOGGER = Logger.getLogger(ProGolfProductInformationMapping.class);
	public Map<String, Product> readMapper(String accessToken,Sheet sheet,Map<String, Product> productsMap) {
		int columnIndex = 0;

		Set<String> productXids = new HashSet<String>();
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		ProductConfigurations productConfigObj = new ProductConfigurations();
		Product existingApiProduct = null;
		Product productExcelObj = new Product();
		String productId = null;
		String asiProdNo = null;
		String headerName ="";
		List<Option> listOfOptions = new ArrayList<>();
		String features = "";
		boolean isRushValue = false;
		try{
			Iterator<Row> iterator = sheet.iterator();
			Row  headerRow = null;
			while (iterator.hasNext()) {
				try{
					Row nextRow = iterator.next();

					if (nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO){
						headerRow = nextRow;
						continue;
					}	
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					if (productId != null) {
						productXids.add(productId);
					}
					boolean checkXid = false;

					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						cell.getStringCellValue();
						String xid = null;
						 columnIndex = cell.getColumnIndex();

						if (columnIndex + 1 == 1) {
							xid = getProductXid(nextRow);
							checkXid = true;
						} else {
							checkXid = false;
						}
						if (checkXid) {
							if (!productXids.contains(xid)) {
								if (nextRow.getRowNum() != 1) {
									System.out
											.println("Java object converted to JSON String, written to file");
									productConfigObj.setOptions(listOfOptions);
									productExcelObj
											.setProductConfigurations(productConfigObj);
									 productExcelObj.setPriceGrids(priceGrids);
									 productsMap.put(xid, productExcelObj);

								}
								listOfOptions = new ArrayList<>();
								productConfigObj = new ProductConfigurations();
								isRushValue = false;
								if (!productXids.contains(xid)) {
									productXids.add(xid);
								}
								productExcelObj = new Product();
								existingApiProduct = postServiceImpl.getProduct(accessToken, xid);
								 _LOGGER.info("Existing Xid is not available,product treated as new product");
								   if(existingApiProduct == null){
								    	 productExcelObj = new Product();
								    	 productExcelObj.setDistributorOnlyComments("");
								     }else{
								    	  productExcelObj=existingApiProduct;
										  productConfigObj=productExcelObj.getProductConfigurations();
										  priceGrids = productExcelObj.getPriceGrids();
										  productExcelObj.setDistributorOnlyComments("");
								    }
							}
						}
						if(columnIndex == ApplicationConstants.CONST_NUMBER_ZERO){
							continue;
						}
						headerName = getHeaderName(columnIndex, headerRow);
						switch(headerName){
						case "product_id":
							break;
						case "SKU":
							break;
						case "Country":
							break;
						case "Language":
							break;
						case "Currency":
							break;
						case "Product_Name":
							String prdName = cell.getStringCellValue();
							productExcelObj.setName(prdName);
							break;
						case "Description":
							 String desc  = cell.getStringCellValue();
							 desc = desc.replaceAll("[<p></p>&nbsp]", "");
							 productExcelObj.setDescription(desc);
							break;
						case "Linename":
							String lineName = cell.getStringCellValue();
							if(isLineNameOrTradeName(lineName)){// yes ,it is lineName
							List<String> listOfLineNames = Arrays.asList("Pro Golf Premiums Line");
							productExcelObj.setLineNames(listOfLineNames);
							} else{// trade Name
								List<TradeName> listOfTradeName = proInfoAttributeParser.getTradeNames(lineName);
								productConfigObj.setTradeNames(listOfTradeName);
							}
							break;
						case "Categories":
							break;
						case "Search_Keyword":
							 String keyWords = cell.getStringCellValue();
								List<String> listOfKeywords = Arrays.asList(keyWords.split("\\|"));
								productExcelObj.setProductKeywords(listOfKeywords);
							break;
						case "Default_Image": 
							break;
						case "Default_Image_Color_Code":
							break;
						case "Default_Color":
							break;
						case "ATTR_Colors":
							String colrs = cell.getStringCellValue();
							List<Color> listOfColors = proInfoAttributeParser.getProductColors(colrs);
							productConfigObj.setColors(listOfColors);
							break;
						case "ATTR_Size":
							String size = cell.getStringCellValue();
							Size sizes = proInfoAttributeParser.getProductSize(size);
							productConfigObj.setSizes(sizes);
							break;
						case "ATTR_Imprint_Color":
							String imprintColor = cell.getStringCellValue();
							if(imprintColor.contains("PMS Match at No Charge")){
								productExcelObj.setDistributorOnlyComments("PMS Match at No Charge");
							}
							break;
						case "ATTR_imprint_Size":
							String imprintSize = cell.getStringCellValue();
							List<ImprintSize> listImprSzies = proInfoAttributeParser.getImprintSizes(imprintSize);
							productConfigObj.setImprintSize(listImprSzies);
							break;
						case "ATTR_Width":
							// it is related option
							String optionValue = cell.getStringCellValue();
							 listOfOptions = proInfoAttributeParser.getProductOption(optionValue, "Product",
									"Width",true,listOfOptions);
							//productConfigObj.setOptions(listOfOptions);
							break;
						case "ATTR_Golf_Ball_Model":
							String golfBallModel = cell.getStringCellValue();
							 listOfOptions = proInfoAttributeParser.getProductOption(golfBallModel, "Product",
										"Golf Ball Model",true,listOfOptions);
							break;
						case "ATTR_Shape":
							break;
						case "ATTR_Style"://option
							String style = cell.getStringCellValue();
							listOfOptions = proInfoAttributeParser.getProductOption(style, "Product",
									"Style",true,listOfOptions);
							break;
						case "ATTR_Hand" ://option
							String hand = cell.getStringCellValue();
							listOfOptions = proInfoAttributeParser.getProductOption(hand, "Product",
									"Hand",true,listOfOptions);
							break;
						case "Valid_Up_To":
							String priceConfirmThroDate = cell.getStringCellValue();
							if(CommonUtility.isPriceConfirmThroughDate(priceConfirmThroDate)){
								productExcelObj.setPriceConfirmedThru(priceConfirmThroDate);
							}
							break;
						case "Matrix_Price": //ignore
							break;
						case "Matrix_Frieght"://ignore
							break;
						case "vat"://ignore
							break;
						case "vat_unit"://ignore
							break;
						case "Packaging_type":
							//there is no data for this columns
							break;
						case "Packaging_Charges":
							//there is no data for this columns
							break;
						case "Packaging_Code":
							//there is no data for this columns
							break;
						case "Video_URL"://ignore
							break;
						case "Distributor_Central_URL"://ignore
							break;
						case "Special_Price_Valid_Up_To":// ignore
							break;
						case "feature_1":
						case "feature_2":
						case "feature_3":
						case "feature_4":
						case "feature_5":
						case "feature_6":
						case "feature_7":
						case "feature_8":
						case "feature_9":
							features = cell.getStringCellValue();//isRushValue = false;
							if(features.contains("Rush|3")){
								isRushValue = true;
							} else{
								productExcelObj.setProductConfigurations(productConfigObj);
								productExcelObj  = proInfoAttributeParser.getFeaturesParser(features, productExcelObj);
								productConfigObj = productExcelObj.getProductConfigurations();
							}
							
							break;
						
						}					   
						}
				    if(isRushValue){
				    	RushTime existingRushTimeValue = productConfigObj.getRushTime();
				    	existingRushTimeValue = proInfoAttributeParser.getProductRushTime("3", existingRushTimeValue);
				    	productConfigObj.setRushTime(existingRushTimeValue);
				    	StringBuilder shippingValue = new StringBuilder();
				    	shippingValue.append("Includes 2nd Day Air Freight").append("|").append("Includes Next Day Air Freight");
						listOfOptions = proInfoAttributeParser.getProductOption(shippingValue.toString(), "Shipping",
								"Shipping Choices", false, listOfOptions);
						priceGrids = proGolfPriceGridParser.getUpchargePriceGrid("1", "6", "A", "RUSH", false, "USD",
								"3 business days", "Rush Service Charge", "Other", 1, priceGrids,"");
						StringBuilder optionCritra1 = new StringBuilder();
						optionCritra1.append("SHOP:Includes 2nd Day Air Freight").append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).
						append("RUSH:3 business days");
						priceGrids = proGolfPriceGridParser.getUpchargePriceGrid("1", "12", "A", optionCritra1.toString(), false, "USD",
								"3 business days", "Other Charge", "Optional", 1, priceGrids,"Shipping Choices");
						StringBuilder optionCritra2 = new StringBuilder();
						optionCritra1.append("SHOP:Includes Next Day Air Freight").append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID).
						append("RUSH:3 business days");
						priceGrids = proGolfPriceGridParser.getUpchargePriceGrid("1", "16", "A", optionCritra2.toString(), false, "USD",
								"3 business days", "Other Charge", "Optional", 1, priceGrids,"Shipping Choices");
						//priceGrids = 
				    }
					productExcelObj.setPriceType("L");
					isRushValue = false;
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			productExcelObj.setPriceGrids(priceGrids);
			productConfigObj.setOptions(listOfOptions);
			productExcelObj.setProductConfigurations(productConfigObj);
			
	        // test.add(productExcelObj);
	        SheetMap.put(productId,productExcelObj);	
	        ProductNoMap.put(asiProdNo,productId);
			}
			return productsMap;
		}catch(Exception e){
	
		return productsMap;
		}finally{
		}
				
		}
	private boolean isLineNameOrTradeName(String value){
		if(value.equalsIgnoreCase("Pro Golf Premiums Line")){
			return true;
		}
		else return false;
	}
	
	private String getHeaderName(int columnIndex,Row headerRow){
		 Cell cell2 =  headerRow.getCell(columnIndex);  
		 String headerName=CommonUtility.getCellValueStrinOrInt(cell2);
		//columnIndex = ProGolfHeaderMapping.getHeaderIndex(headerName);
		return headerName;
	}
	private String getProductXid(Row row){
		Cell xidCell =  row.getCell(ApplicationConstants.CONST_NUMBER_ZERO);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid)){
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
		public ProGolfInformationAttributeParser getProInfoAttributeParser() {
			return proInfoAttributeParser;
		}
		public void setProInfoAttributeParser(ProGolfInformationAttributeParser proInfoAttributeParser) {
			this.proInfoAttributeParser = proInfoAttributeParser;
		}
		public ProGolfPriceGridParser getProGolfPriceGridParser() {
			return proGolfPriceGridParser;
		}
		public void setProGolfPriceGridParser(ProGolfPriceGridParser proGolfPriceGridParser) {
			this.proGolfPriceGridParser = proGolfPriceGridParser;
		}
	}

