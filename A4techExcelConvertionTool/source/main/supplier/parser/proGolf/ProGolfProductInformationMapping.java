package parser.proGolf;

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

public class ProGolfProductInformationMapping {
	private PostServiceImpl 					postServiceImpl;
	private ProductDao 							productDaoObj;
	private ProGolfInformationAttributeParser 	proInfoAttributeParser;
	private ProGolfPriceGridParser 				proGolfPriceGridParser;

	private static Logger _LOGGER = Logger.getLogger(ProGolfProductInformationMapping.class);

	public Map<String, Product> readMapper(String accessToken, Sheet sheet, Map<String, Product> productsMap) {
		int columnIndex = 0;

		Set<String> productXids = new HashSet<String>();
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		ProductConfigurations productConfigObj = new ProductConfigurations();
		Product existingApiProduct = null;
		Product productExcelObj = new Product();
		String productId = null;
		String headerName = "";
		List<Option> listOfOptions = new ArrayList<>();
		String features = "";
		boolean isRushValue = false;
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
								listOfOptions = new ArrayList<>();
								productConfigObj = new ProductConfigurations();
								isRushValue = false;
								if (!productXids.contains(productId)) {
									if(!StringUtils.isEmpty(productId)){
										if(!"10210032".equals(productId.trim())){// since this product id existing for two same products that cause we need to check product id
											productXids.add(productId);
										}
									}
									
								}
								productExcelObj = new Product();
								if(StringUtils.isEmpty(productId)){
									break;
								}
								existingApiProduct = postServiceImpl.getProduct(accessToken, productId);
								if (existingApiProduct == null) {
									_LOGGER.info("Existing Xid is not available,product treated as new product");
									productExcelObj = new Product();
									productExcelObj.setDistributorOnlyComments("");
									productExcelObj.setProductDataSheet("");// used to reference for base price based on sizes in other tab(Product pricing)
								} else {
									productExcelObj = existingApiProduct;
									productExcelObj = proInfoAttributeParser.keepExistingProductData(productExcelObj);
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
							//PLAIN
							String productNo ="";
							int skuLen = skuOrPrdNo.length();
							if(skuLen> 14){
								productNo = skuOrPrdNo.replaceAll("PLAIN", "P");
							}else{
								productNo = skuOrPrdNo;
							}
							productExcelObj.setAsiProdNo(productNo);
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
							prdName = prdName.replaceAll("[®™]", "");
							productExcelObj.setName(prdName);
							break;
						case "Description":
							String desc = cell.getStringCellValue();
							desc = removeSpecialWords(desc);
							if(desc.contains(productExcelObj.getAsiProdNo())){
								desc = desc.replaceAll(productExcelObj.getAsiProdNo(), "");
							}
							productExcelObj.setDescription(desc);
							break;
						case "Linename":
							String lineName = cell.getStringCellValue();
							if (isLineNameOrTradeName(lineName)) {// yes ,it is
																	// lineName
								List<String> listOfLineNames = Arrays.asList("Pro Golf Premiums Line");
								productExcelObj.setLineNames(listOfLineNames);
							} else {// trade Name
								List<TradeName> listOfTradeName = proInfoAttributeParser.getTradeNames(lineName);
								productConfigObj.setTradeNames(listOfTradeName);
							}
							break;
						case "Categories":
							break;
						case "Search_Keyword":
							String keyWords = cell.getStringCellValue();
							if(!StringUtils.isEmpty(keyWords)){
								List<String> listOfKeywords = Arrays.asList(keyWords.split("\\|"));
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
								List<Color> listOfColors = proInfoAttributeParser.getProductColors(defaultColor);
								productConfigObj.setColors(listOfColors);
							}
							break;
						case "ATTR_Colors":
							String colrs = cell.getStringCellValue();
							if(!StringUtils.isEmpty(colrs)){
								List<Color> listOfColors = proInfoAttributeParser.getProductColors(colrs);
								productConfigObj.setColors(listOfColors);
							}
							break;
						case "ATTR_Size":
							String size = cell.getStringCellValue();
							if(!StringUtils.isEmpty(size)){
								Size sizes = proInfoAttributeParser.getProductSize(size);
								productConfigObj.setSizes(sizes);
							}
							break;
						case "ATTR_Arc":
							String sizeArc = cell.getStringCellValue();
							if(!StringUtils.isEmpty(sizeArc)){
								if(sizeArc.contains("\"")){
									sizeArc = sizeArc.replaceAll("\"", "");
								}
								Size sizes = proInfoAttributeParser.getProductSizeArc(sizeArc.trim());
								productConfigObj.setSizes(sizes);
							}
						case "ATTR_Imprint_Color"://ATTR_Imprint Color
						case "ATTR_Imprint Color":
							String imprintColor = cell.getStringCellValue();
							if (imprintColor.contains("PMS Match at No Charge")) {
								productExcelObj.setDistributorOnlyComments("PMS Match at No Charge");
							}
							break;
						case "ATTR_imprint_Size":
						case "ATTR_Imprint_Size":
						case "ATTR_Imprint Size":
							String imprintSize = cell.getStringCellValue();
							if(!StringUtils.isEmpty(imprintSize)){
								productConfigObj = proInfoAttributeParser.getImprintSizes(imprintSize,productConfigObj);
							}
							break;
						case "ATTR_Width":
							// it is related option
							String optionValue = cell.getStringCellValue();
							if(!StringUtils.isEmpty(optionValue)){
								listOfOptions = proInfoAttributeParser.getProductOption(optionValue, "Product", "Width",
										true, listOfOptions);
								productConfigObj.setOptions(listOfOptions);
							}
							break;
						case "ATTR_Golf_Ball_Model":
							String golfBallModel = cell.getStringCellValue();
							if(!StringUtils.isEmpty(golfBallModel)){
								listOfOptions = proInfoAttributeParser.getProductOption(golfBallModel, "Product",
										"Golf Ball Model", true, productConfigObj.getOptions());
								productConfigObj.setOptions(listOfOptions);
							}
							break;
						case "ATTR_Shape":
							break;
						case "ATTR_Style":// option
							String style = cell.getStringCellValue();
							if(!StringUtils.isEmpty(style)){
								listOfOptions = proInfoAttributeParser.getProductOption(style, "Product", "Style", true,
										productConfigObj.getOptions());
								productConfigObj.setOptions(listOfOptions);
							}
							break;
						case "ATTR_Hand":// option
							String hand = cell.getStringCellValue();
							if(!StringUtils.isEmpty(hand)){
								listOfOptions = proInfoAttributeParser.getProductOption(hand, "Product", "Hand", true,
										productConfigObj.getOptions());
								productConfigObj.setOptions(listOfOptions);
							}
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
								if(features.contains("Pricing is for size")){
									productExcelObj.setProductDataSheet("isSizePrices");
								}
								if (features.contains("Rush|3") || features.contains("Rush |3")) {
									isRushValue = true;
								} else {
									productExcelObj.setProductConfigurations(productConfigObj);
									productExcelObj = proInfoAttributeParser.getFeaturesParser(features,
											productExcelObj);
									productConfigObj = productExcelObj.getProductConfigurations();
									priceGrids = productExcelObj.getPriceGrids();
								}

							}
							break;
						}
					}
					if (isRushValue) {
						RushTime existingRushTimeValue = productConfigObj.getRushTime();
						existingRushTimeValue = proInfoAttributeParser.getProductRushTime("3", existingRushTimeValue);
						productConfigObj.setRushTime(existingRushTimeValue);
						StringBuilder shippingValue = new StringBuilder();
						shippingValue.append("Includes 2nd Day Air Freight").append("|")
								.append("Includes Next Day Air Freight");
						listOfOptions = proInfoAttributeParser.getProductOption(shippingValue.toString(), "Shipping",
								"Shipping Choices", false, productConfigObj.getOptions());
						productConfigObj.setOptions(listOfOptions);
						priceGrids = proGolfPriceGridParser.getUpchargePriceGrid("1", "6", "A", "RUSH", false, "USD",
								"3 business days", "Rush Service Charge", "Other", 1, priceGrids, "","");
						StringBuilder optionCritra1 = new StringBuilder();
						optionCritra1.append("SHOP:Includes 2nd Day Air Freight");
								/*.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID)
								.append("RUSH:3 business days");*/
						priceGrids = proGolfPriceGridParser.getUpchargePriceGrid("1", "12", "A",
								optionCritra1.toString(), false, "USD", "Includes 2nd Day Air Freight", "Shipping Charge", "Other",
								1, priceGrids, "Shipping Choices","");
						StringBuilder optionCritra2 = new StringBuilder();
						optionCritra2.append("SHOP:Includes Next Day Air Freight");
								/*.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID)
								.append("RUSH:3 business days");*/
						priceGrids = proGolfPriceGridParser.getUpchargePriceGrid("1", "16", "A",
								optionCritra2.toString(), false, "USD", "Includes Next Day Air Freight", "Shipping Charge", "Other",
								1, priceGrids, "Shipping Choices","");
						// priceGrids =
					}
					productExcelObj.setPriceType("L");
					isRushValue = false;
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
		if (value.equalsIgnoreCase("Pro Golf Premiums Line")) {
			return true;
		} else
			return false;
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
