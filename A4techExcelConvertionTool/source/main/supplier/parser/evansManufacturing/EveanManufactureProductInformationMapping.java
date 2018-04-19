package parser.evansManufacturing;

import java.util.ArrayList;
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
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Volume;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class EveanManufactureProductInformationMapping {
	private PostServiceImpl 					postServiceImpl;
	private ProductDao 							productDaoObj;
	private EveanManufactureAttributeParser     eveanAttributeParser;

	private static Logger _LOGGER = Logger.getLogger(EveanManufactureProductInformationMapping.class);

	public Map<String, Product> readMapper(String accessToken, Sheet sheet, Map<String, Product> productsMap,String environmentType) {
		int columnIndex = 0;

		Set<String> productXids = new HashSet<String>();
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		ProductConfigurations productConfigObj = new ProductConfigurations();
		Product existingApiProduct = null;
		Product productExcelObj = new Product();
		String productId = null;
		String headerName = "";
		StringBuilder shippingInfo = new StringBuilder();
		StringBuilder shippingDimension = new StringBuilder();
		StringBuilder imprintSizeAndLoc = new StringBuilder();
		StringBuilder packageValues = new StringBuilder();
		try {
			Iterator<Row> iterator = sheet.iterator();
			while (iterator.hasNext()) {
				try {
					Row nextRow = iterator.next();
					if (nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO) {
						continue;
					}
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					if (productId != null) {
						if(StringUtils.isEmpty(productId)){
							break;
						}
					}
					boolean checkXid = false;

					while (cellIterator.hasNext()) {       
						Cell cell = cellIterator.next();
						// String xid = null;
						columnIndex = cell.getColumnIndex();
						if (columnIndex == 0) {
							productId = getProductXid(nextRow);
							checkXid = true;
						} else {
							checkXid = false;
						}

						/*if (columnIndex + 1 == 1) {
							productId = getProductXid(nextRow);
							checkXid = true;
						}*/ /*
							 * else { checkXid = false; }
							 */
						if (checkXid) {
							if (!productXids.contains(productId)) {
								if (nextRow.getRowNum() != 1) {
									System.out.println("Java object converted to JSON String, written to file");
									//productConfigObj.setOptions(listOfOptions);
									ShippingEstimate shippingEstimation = eveanAttributeParser.getShippingEstimation(
											shippingInfo.toString(), shippingDimension.toString());
									productConfigObj.setShippingEstimates(shippingEstimation);
									if(!imprintSizeAndLoc.toString().isEmpty()){
										productConfigObj = eveanAttributeParser.getImprintLocationAndImprintSize(
												imprintSizeAndLoc.toString(), productConfigObj);
									}
									if(!packageValues.toString().isEmpty()){
									   productExcelObj.setProductConfigurations(productConfigObj);
									   productExcelObj.setPriceGrids(priceGrids);
										productExcelObj = eveanAttributeParser
												.getProductPackaging(packageValues.toString(), productExcelObj);
									} else {
										productExcelObj.setProductConfigurations(productConfigObj);
										productExcelObj.setPriceGrids(priceGrids);	
									}
									productsMap.put(productExcelObj.getExternalProductId(), productExcelObj);

								}
								productConfigObj = new ProductConfigurations();
								productExcelObj = new Product();
								shippingInfo = new StringBuilder();
								shippingDimension = new StringBuilder();
								imprintSizeAndLoc = new StringBuilder();
								packageValues = new StringBuilder();
								existingApiProduct = postServiceImpl.getProduct(accessToken, productId, environmentType);
								if (existingApiProduct == null) {
									_LOGGER.info("Existing Xid is not available,product treated as new product");
									productExcelObj = new Product();
								
								} else {
									productExcelObj = existingApiProduct;
									productExcelObj = eveanAttributeParser.keepExistingProductData(productExcelObj);
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
						/*if (columnIndex == ApplicationConstants.CONST_NUMBER_ZERO) {
							continue;
						}*/
						//headerName = getHeaderName(columnIndex, headerRow);
						switch (columnIndex+1) {
						case 1:
							productExcelObj.setExternalProductId(productId);
							break;
						case 2:// sku
							String skuOrPrdNo = CommonUtility.getCellValueStrinOrInt(cell);
							productExcelObj.setAsiProdNo(skuOrPrdNo);
							productExcelObj.setProductLevelSku(skuOrPrdNo);
							break;
						case 3:// ignore
							// as per client feedback , no need process this field
							break;
						case 4:// product name
							String name = cell.getStringCellValue();
                            productExcelObj.setName(getFinalProductName(name));
							break;
						case 5://description
							String desc = cell.getStringCellValue();
							productExcelObj.setDescription(getFinalDescription(desc));
							break;
						case 6:
						case 7:
						case 8:
							// ignore as per client feedback
							break;
						case 9:// keywords
							String keyword = cell.getStringCellValue();
							if(!StringUtils.isEmpty(keyword)){
								List<String> keyList = eveanAttributeParser.getProductKeywords(keyword);
								productExcelObj.setProductKeywords(keyList);
							}
							break;
						case 10:
							//ignore
							break;
						case 11://prd time
							String prdTime = cell.getStringCellValue();
							if(!StringUtils.isEmpty(prdTime) && !prdTime.equalsIgnoreCase("Please call factory for lead time.")){
								List<ProductionTime> productionTimeList = eveanAttributeParser
										.getProductionTime(prdTime);
								productConfigObj.setProductionTime(productionTimeList);
							}
							break;
						case 12://Qty per Carton
						//	String 
							 String shiQty = CommonUtility.getCellValueStrinOrInt(cell);
			            	  if(!StringUtils.isEmpty(shiQty)){
			            		  shippingInfo.append("shippingQty:").append(shiQty);
			            	  }
							break;
						case 13://Lbs per Carton	
							 String shiWe = CommonUtility.getCellValueDouble(cell);
			            	  if(!StringUtils.isEmpty(shiWe)){
			            		  shippingInfo.append(",").append("shippingWt:").append(shiWe);
			            	  }
							break;
						case 14://Carton LENGTH
							String length = CommonUtility.getCellValueStrinOrInt(cell);
			            	  if(!StringUtils.isEmpty(length)){
			            		  shippingDimension.append(length).append("x");
			            	  }
							break;
						case 15://Carton WIDTH
							String width = CommonUtility.getCellValueStrinOrInt(cell);
			            	  if(!StringUtils.isEmpty(width)){
			            		  shippingDimension.append(width).append("x");;
			            	  }
							break;
						case 16://Carton HEIGHT
							String height = CommonUtility.getCellValueStrinOrInt(cell);
			            	  if(!StringUtils.isEmpty(height)){
			            		  shippingDimension.append(height);
			            	  }
						case 17:
							//IGNORE
							break;
						case 18://priceinclude for base price price
							String priceInclude = cell.getStringCellValue();
							productExcelObj.setDistributorOnlyComments(priceInclude);//this is for base price reference
							break;
						case 19:// run charge
						case 20:
							String runCharge = cell.getStringCellValue();
							productExcelObj.setProductConfigurations(productConfigObj);
							productExcelObj = eveanAttributeParser.getRunUpcharge(productExcelObj, runCharge.trim());
							productConfigObj = productExcelObj.getProductConfigurations();
							priceGrids = productExcelObj.getPriceGrids();
							break;
						case 21:// setup charge
							String setupCharge = cell.getStringCellValue();
							productExcelObj.setProductConfigurations(productConfigObj);
							productExcelObj.setPriceGrids(priceGrids);
							productExcelObj = eveanAttributeParser.getSetUpcharge(productExcelObj, setupCharge,"setupCharge");
							productConfigObj = productExcelObj.getProductConfigurations();
							priceGrids = productExcelObj.getPriceGrids();
							break;
						case 22://Exact Reorder Set-up
							String exactReorderSetup = cell.getStringCellValue();
							productExcelObj.setProductConfigurations(productConfigObj);
							productExcelObj.setPriceGrids(priceGrids);
							productExcelObj = eveanAttributeParser.getSetUpcharge(productExcelObj, exactReorderSetup,"reorderSetup");
							productConfigObj = productExcelObj.getProductConfigurations();
							priceGrids = productExcelObj.getPriceGrids();
							break;
						case 23:
						case 24://Imprint methods
							String imprintMethodVal = cell.getStringCellValue();
							productConfigObj = eveanAttributeParser.getProductImprintMethods(imprintMethodVal, productConfigObj);
							break;
						case 25:// size
						case 26:
							String size = cell.getStringCellValue();
							//5 1/4" H x 4" W (flat) -- special case for testing for same column
							//4 1/4" H x 3 3/8" Diameter (on can)
							Size sizeObj =eveanAttributeParser.getProductSize(size);
							productConfigObj.setSizes(sizeObj);
							break;
						case 27:
						case 28:
						case 29:
						case 30:
							String imprintSizeAndLocVal = cell.getStringCellValue();
							if(!StringUtils.isEmpty(imprintSizeAndLocVal)){
								imprintSizeAndLoc.append(imprintSizeAndLocVal).append(",");
							}
							break;
						case 31:// Additional Product Info
							break;
						case 32:
						case 33://options
						case 34:
							//Ignore as per client feedback
							break;
						case 35://additional ProductIfo
							String additionalPrdInfo = cell.getStringCellValue();
							if(!StringUtils.isEmpty(additionalPrdInfo)){
								productExcelObj.setAdditionalProductInfo(additionalPrdInfo);
							}
							break;
						case 36:
						case 37://packaging
							String packVal = cell.getStringCellValue();
							if(!StringUtils.isEmpty(packVal)){
								packageValues.append(packVal).append(";");
							}
							break;
						case 38:// item weight
							String itemWeightVal = CommonUtility.getCellValueDouble(cell);
							Volume volumn =eveanAttributeParser.getProductItemWeightvolume(itemWeightVal);
							productConfigObj.setItemWeight(volumn);
							break;
						case 39:// fob
							String fobVal = cell.getStringCellValue();
							fobVal = fobVal.replaceAll("[^0-9]", "").trim();
							List<FOBPoint> fobPointList = eveanAttributeParser.getFobPoint(fobVal, accessToken, environmentType);
							productExcelObj.setFobPoints(fobPointList);
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
	private String getFinalProductName(String name){
		if(name.contains("™")){
			name = name.replaceAll("™", "");
		}
		name = CommonUtility.getStringLimitedChars(name,60);
		return name;
	}
	private String getFinalDescription(String desc){
		String fristChar = desc.substring(0,1);
		fristChar = fristChar.replaceAll("[^a-zA-Z0-9]", "");
		if(StringUtils.isEmpty(fristChar)){
			desc = desc.substring(1);	
		} 
		if(desc.contains("•")){
			desc = desc.replaceAll("•", ".").trim();
		}
		return desc;
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
	public void setEveanAttributeParser(EveanManufactureAttributeParser eveanAttributeParser) {
		this.eveanAttributeParser = eveanAttributeParser;
	}
	}
