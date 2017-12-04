package parser.ballPro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.TradeName;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class BallProProductInformationMapping {
	private PostServiceImpl 					postServiceImpl;
	private ProductDao 							productDaoObj;
	private BallProInformationAttributeParser 	ballProAttributeParser;
	private BallProPriceGridParser 				ballProPriceGridParser;

	private static Logger _LOGGER = Logger.getLogger(BallProProductInformationMapping.class);

	public Map<String, Product> readMapper(String accessToken, Sheet sheet, Map<String, Product> productsMap,String environmentType) {
		int columnIndex = 0;

		Set<String> productXids = new HashSet<String>();
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		ProductConfigurations productConfigObj = new ProductConfigurations();
		Product existingApiProduct = null;
		Product productExcelObj = new Product();
		String productId = null;
		String headerName = "";
		String features = "";
		try {
			Iterator<Row> iterator = sheet.iterator();
			Row headerRow = null;
			while (iterator.hasNext()) {
				try {
					Row nextRow = iterator.next();

					if (nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO) {
						headerRow = nextRow;
						continue;
					}
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					if (productId != null) {
						if(StringUtils.isEmpty(productId)){
							break;
						}
						if(!"10210032".equals(productId.trim())){
							productXids.add(productId);
						}
					}
					boolean checkXid = false;

					while (cellIterator.hasNext()) {       
						Cell cell = cellIterator.next();
						// String xid = null;
						columnIndex = cell.getColumnIndex();
						if (columnIndex == 1) {
							productId = getProductXid(nextRow);
							checkXid = true;
						} else {
							checkXid = false;
						}

						if (columnIndex + 1 == 1) {
							productId = getProductXid(nextRow);
							checkXid = true;
						} /*
							 * else { checkXid = false; }
							 */
						if (checkXid) {
							if (!productXids.contains(productId)) {
								if (nextRow.getRowNum() != 1) {
									System.out.println("Java object converted to JSON String, written to file");
									//productConfigObj.setOptions(listOfOptions);
									productExcelObj.setProductConfigurations(productConfigObj);
									productExcelObj.setPriceGrids(priceGrids);
									productsMap.put(productExcelObj.getProductLevelSku(), productExcelObj);

								}
								productConfigObj = new ProductConfigurations();
								if (!productXids.contains(productId)) {
									productXids.add(productId);
								}
								productExcelObj = new Product();
								if(StringUtils.isEmpty(productId)){
									break;
								}
								existingApiProduct = postServiceImpl.getProduct(accessToken, productId, environmentType);
								if (existingApiProduct == null) {
									_LOGGER.info("Existing Xid is not available,product treated as new product");
									productExcelObj = new Product();
									productExcelObj.setDistributorOnlyComments("");
									productExcelObj.setProductDataSheet("");// used to reference for base price based on sizes in other tab(Product pricing)
								} else {
									productExcelObj = existingApiProduct;
									productExcelObj = ballProAttributeParser.keepExistingProductData(productExcelObj);
									productConfigObj = productExcelObj.getProductConfigurations();
									priceGrids = productExcelObj.getPriceGrids();
									if(CollectionUtils.isEmpty(priceGrids)){
										priceGrids = new ArrayList<>();
									}
									productExcelObj.setDistributorOnlyComments("");
									productExcelObj.setProductDataSheet("");
								}
							}
						}
						if (columnIndex == ApplicationConstants.CONST_NUMBER_ZERO) {
							continue;
						}
						headerName = getHeaderName(columnIndex, headerRow);
						switch (headerName) {
						case "product_id":
							productExcelObj.setExternalProductId(productId);
							break;
						case "SKU":
							String skuOrPrdNo = CommonUtility.getCellValueStrinOrInt(cell);
							productExcelObj.setAsiProdNo(skuOrPrdNo);
							productExcelObj.setProductLevelSku(skuOrPrdNo);
							break;
						case "Country":// ignore
							// as per client feedback , no need process this field
							break;
						case "Language":// Ignore
							// as per client feedback , no need process this field
							break;
						case "Currency":
							// set for all products default currency : USD
							break;
						case "Product_Name":
							String prdName = cell.getStringCellValue();
							prdName = prdName.replaceAll("®", "");
							prdName = prdName.replaceAll("™", "");
							prdName = CommonUtility.getStringLimitedChars(prdName, 60);
							productExcelObj.setName(prdName);
							break;
						case "Description":
							String desc = cell.getStringCellValue();
							desc = removeSpecialWords(desc);
							if(desc.contains(productExcelObj.getAsiProdNo())){
								desc = desc.replaceAll(productExcelObj.getAsiProdNo(), "");
							}
							if(desc.contains("3M")){
								desc = desc.replaceAll("3M", "");
							}
							productExcelObj.setDescription(desc);
							break;
						case "Linename":
							String lineName = cell.getStringCellValue();
							if (isLineNameOrTradeName(lineName)) {// yes ,it is
								List<String> listOfLineNames  = null;   	// lineName
								if(lineName.equalsIgnoreCase("Ball Pro Promotional Group")){
									listOfLineNames = Arrays.asList("Ball Pro Promotional Group");
								}
								productExcelObj.setLineNames(listOfLineNames);
							} else {// trade Name
								List<TradeName> listOfTradeName = ballProAttributeParser.getTradeNames(lineName.trim());
								productConfigObj.setTradeNames(listOfTradeName);
							}
							break;
						case "Categories":
							break;
						case "Search_Keyword":
							String keyWords = cell.getStringCellValue();
							if(!StringUtils.isEmpty(keyWords)){
								List<String> listOfKeywords = ballProAttributeParser.productKeyWords(keyWords);
								productExcelObj.setProductKeywords(listOfKeywords);
							}
							break;
						case "Default_Image":
							break;
						case "Default_Image_Color_Code":
							break;
						case "Default_Color":
							String defaultColor = cell.getStringCellValue();
							if(!StringUtils.isEmpty(defaultColor)){
								// if ATTR_color column values  are not present then only we can use default color
								List<Color> listOfColors = ballProAttributeParser.getProductColors(defaultColor);
								productConfigObj.setColors(listOfColors);
							}
							break;
						case "ATTR_Colors":
							String colrs = cell.getStringCellValue();
							if(!StringUtils.isEmpty(colrs)){
								List<Color> listOfColors = ballProAttributeParser.getProductColors(colrs);
								productConfigObj.setColors(listOfColors);
							}
							break;
						case "ATTR_Size":
							String size = cell.getStringCellValue();
							if(!StringUtils.isEmpty(size)){
								Size sizes = ballProAttributeParser.getProductSize(size);
								productConfigObj.setSizes(sizes);
							}
							break;
						case "ATTR_Imprint_Color":
							String imprintColor = cell.getStringCellValue();
							if (imprintColor.contains("PMS Match at No Charge")) {
								productExcelObj.setDistributorOnlyComments("PMS Match at No Charge");
							}
							break;
 						
						case "ATTR_Shape":
							break;
						case "Valid_Up_To":
							String priceConfirmThroDate = cell.getStringCellValue();
							if (CommonUtility.isPriceConfirmThroughDate(priceConfirmThroDate)) {
								productExcelObj.setPriceConfirmedThru(priceConfirmThroDate);
							}
							break;
						case "Matrix_Price": // ignore
							break;
						case "Matrix_Frieght":// ignore
							break;
						case "vat":// ignore
							break;
						case "vat_unit":// ignore
							break;
						case "Packaging_type":
							// there is no data for this columns
							break;
						case "Packaging_Charges":
							// there is no data for this columns
							break;
						case "Packaging_Code":
							// there is no data for this columns
							break;
						case "Video_URL":// ignore
							break;
						case "Distributor_Central_URL":// ignore
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
							features = cell.getStringCellValue();// isRushValue
																	// = false;
							if (!StringUtils.isEmpty(features)) {
									productExcelObj.setProductConfigurations(productConfigObj);
									productExcelObj = ballProAttributeParser.getFeaturesParser(features,
											productExcelObj);
									productConfigObj = productExcelObj.getProductConfigurations();
									priceGrids = productExcelObj.getPriceGrids();
								

							}
							break;
						}
					}
					productExcelObj.setPriceType("L");
				} catch (Exception e) {
					_LOGGER.error(
							"Error while Processing Product Information sheet  and cause :" + productExcelObj.getExternalProductId()
									+ " " + e.getMessage() + "at column number(increament by 1):" + headerName);
				}
				/*productConfigObj.setOptions(listOfOptions);
				productExcelObj.setProductConfigurations(productConfigObj);
				productExcelObj.setPriceGrids(priceGrids);
				productsMap.put(productExcelObj.getProductLevelSku(), productExcelObj);*/

				// test.add(productExcelObj);
			}
			//productConfigObj.setOptions(listOfOptions);
			productExcelObj.setProductConfigurations(productConfigObj);
			productExcelObj.setPriceGrids(priceGrids);
			productsMap.put(productExcelObj.getProductLevelSku(), productExcelObj);
			return productsMap;
		} catch (Exception e) {

			return productsMap;
		} finally {
		}

	}

	private boolean isLineNameOrTradeName(String value) {
		
		if(value.equalsIgnoreCase("Ball Pro Promotional Group")){
			return true;
		} else {
			return false;
		}
			
	}

	private String getHeaderName(int columnIndex, Row headerRow) {
		Cell cell2 = headerRow.getCell(columnIndex);
		String headerName = CommonUtility.getCellValueStrinOrInt(cell2);
		// columnIndex = ProGolfHeaderMapping.getHeaderIndex(headerName);
		return headerName;
	}

	private String getProductXid(Row row) {
		Cell xidCell = row.getCell(ApplicationConstants.CONST_NUMBER_ZERO);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if (StringUtils.isEmpty(productXid)) {
			xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
			productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		} else {
		    String skuValue = "";
			xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_TWO);
			skuValue = CommonUtility.getCellValueStrinOrInt(xidCell);
			if(skuValue.equalsIgnoreCase(productXid)){
				xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
				productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
			}
		}
		return productXid;
	}
	private String removeSpecialWords(String value){
		value = value.replaceAll("&nbsp;","");
		value = value.replaceAll("&amp;","&");
		value = value.replaceAll("&reg;","(R)");
		value = value.replaceAll("&rdquo;","\"");
		value = value.replaceAll("&trade;","(TM)");
		value = value.replaceAll("&rsquo;","'");
		value = value.replaceAll("&ldquo;","\"");
		value = value.replaceAll("&bull;","");
		if(value.contains(".<br />")){
			value = value.replaceAll("<br />"," ");
		} else if(value.contains(". <br />")){
			value = value.replaceAll("<br />"," ");
		} else if(value.contains("<br />.")){
			value = value.replaceAll("<br />"," ");
		} else if(value.contains("<br /> .")){
			value = value.replaceAll("<br />"," ");
		} else if(value.contains("<br />")){
			value = value.replaceAll("<br />",".");
		}
		value = value.replaceAll("\\<.*?\\>", "");
		value = value.replaceAll("\\|", "");
		value = value.replaceAll("\n", ". ");
		value =  value.replaceAll("\\.+", ".");
		value = CommonUtility.getStringLimitedChars(value, 800);
		return value;
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

	public BallProInformationAttributeParser getBallProAttributeParser() {
		return ballProAttributeParser;
	}

	public void setBallProAttributeParser(BallProInformationAttributeParser ballProAttributeParser) {
		this.ballProAttributeParser = ballProAttributeParser;
	}

	public BallProPriceGridParser getBallProPriceGridParser() {
		return ballProPriceGridParser;
	}

	public void setBallProPriceGridParser(BallProPriceGridParser ballProPriceGridParser) {
		this.ballProPriceGridParser = ballProPriceGridParser;
	}

}
