package parser.proGolf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Volume;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class ProGolfShippingMapping {
	private static final Logger _LOGGER = Logger.getLogger(ProGolfShippingMapping.class);

	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private ProGolfInformationAttributeParser proGolfAttributeParser;

	public String readMapper(Map<String, Product> productMaps, Sheet sheet, String accessToken,
			Integer asiNumber, int batchId) {

		int columnIndex = 0;

		Set<String> productXids = new HashSet<String>();
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		List<String> repeatRows = new ArrayList<>();
		ProductConfigurations productConfigObj = new ProductConfigurations();
	//	Product productExcelObj = new Product();
		String finalResult = null;
		String productId = null;
		List<String> XidList = new ArrayList<String>();
		String xid = null;
		String prdXid = null;
		String headerName = "";
		try {
			Row headerRow = null;
			Product existingProduct = null;
			Iterator<Row> iterator = sheet.iterator();
			StringJoiner shippingEstamationValues = new StringJoiner(",");
			StringJoiner sizes = new StringJoiner(",");
			String shippingDimentionUnits = "";
			String shippingWeightUnit = "";
			String productWeightVal = "";
			String sizeUnit = "";
			String shippingWeight = "";
			boolean isFirstproduct = true;
			while (iterator.hasNext()) {

				try {
					Row nextRow = iterator.next();

					if (nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO) {
						headerRow = nextRow;
						continue;
					}
					productId = getSkuValue(nextRow);
					if(isFirstproduct){
						existingProduct = productMaps.get(productId);
						productConfigObj = existingProduct.getProductConfigurations();
						isFirstproduct = false;
					}
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					/*if (xid != null) {
						productXids.add(xid);
					}*/
					boolean checkXid = false;
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						columnIndex = cell.getColumnIndex();
						if (columnIndex + 1 == 1) {
							productId = getProductXid(nextRow);
							checkXid = true;
						} else {
							checkXid = false;
						}
						if (checkXid) {
							if (!productXids.contains(productId)) {
								if (nextRow.getRowNum() != 1) {
									existingProduct.setProductConfigurations(productConfigObj);
									String distributorOnlyComments = existingProduct.getDistributorOnlyComments();
									if(distributorOnlyComments.contains("|")){
										distributorOnlyComments = distributorOnlyComments.replaceAll("\\|", "");
										int noOfCounts = StringUtils.countOccurrencesOf(distributorOnlyComments, "Minimum");
										existingProduct.setDistributorOnlyComments(distributorOnlyComments);
									}
									if (!StringUtils.isEmpty(existingProduct.getExternalProductId())) {
										int num = postServiceImpl.postProduct(accessToken, existingProduct, asiNumber,
												batchId);
										if (num == 1) {
											numOfProductsSuccess.add("1");
										} else if (num == 0) {
											numOfProductsFailure.add("0");
										} else {
										}
									}
									_LOGGER.info("list size>>>>>>>" + numOfProductsSuccess.size());
									_LOGGER.info("Failure list size>>>>>>>" + numOfProductsFailure.size());
									repeatRows.clear();
								}
								existingProduct = productMaps.get(productId);
								productConfigObj = existingProduct.getProductConfigurations();
								if (!productXids.contains(productId)) {
									productXids.add(productId);
									repeatRows.add(productId);
								}
								shippingEstamationValues = new StringJoiner(",");
								sizes = new StringJoiner(",");
								shippingDimentionUnits = "";
								shippingWeightUnit = "";
								productWeightVal = "";
							}
						}/* else {
							if (productXids.contains(xid) && repeatRows.size() != 1) {
								if (isRepeateColumn(columnIndex + 1)) {
									continue;
								}
							}
						}*/
						headerName = getHeaderName(columnIndex, headerRow);
						switch (headerName) {

						case "Free_On_Board":
							String fobPointValue = cell.getStringCellValue();
							List<FOBPoint> listOfFobPoint = proGolfAttributeParser.getFobPoint(fobPointValue,
									accessToken);
							if (!CollectionUtils.isEmpty(listOfFobPoint)) {
								existingProduct.setFobPoints(listOfFobPoint);
							}
							break;
						case "Shipping_Qty_per_Carton": // shipping
						case "Carton_LENGTH":
						case "Carton_WIDTH":
						case "Carton_HEIGHT":
							String dimensionValues = cell.getStringCellValue();
							shippingEstamationValues.add(dimensionValues);
							break;
						case "Product_LENGTH": // sizes
						case "Product_WIDTH":
						case "Product_HEIGHT":
							String sizeValue = cell.getStringCellValue();
							sizes.add(sizeValue);
							break;
						case "Carton_Weight":// shipping
							shippingWeight = cell.getStringCellValue();
							break;
						case "Product_Weight":
							productWeightVal = cell.getStringCellValue();
							break;
						case "Carton_Size_Unit": // shipping
							shippingDimentionUnits = cell.getStringCellValue();
							break;
						case "Carton_Weight_Unit":// shipping
							shippingWeightUnit = cell.getStringCellValue();
							break;
						case "Product_Size_Unit":
							sizeUnit = cell.getStringCellValue();
							break;
						case "Product_Weight_Unit": // item weight
							String productWeightUnit = cell.getStringCellValue();
							if (!StringUtils.isEmpty(productWeightVal)) {
								Volume itemWeight = proGolfAttributeParser.getProductItemWeight(productWeightVal,
										productWeightUnit);
								productConfigObj.setItemWeight(itemWeight);
							}
							break;
						} // end inner while loop
					}
					ShippingEstimate shippingEstimObj = proGolfAttributeParser.getProductShippingEstimation(
							shippingEstamationValues.toString(), shippingWeight, shippingDimentionUnits,
							shippingWeightUnit);
				//ProductConfigurations existingConfig = existingProduct.getProductConfigurations();
					productConfigObj.setShippingEstimates(shippingEstimObj);
				//existingProduct.setProductConfigurations(existingConfig);
				
				} catch (Exception e) {
					_LOGGER.error(
							"Error while Processing ProductId and cause :" + existingProduct.getExternalProductId()
									+ " " + e.getMessage() + "at column number(increament by 1):" + columnIndex);
					ErrorMessageList apiResponse = CommonUtility
							.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: " + e.getMessage()
									+ " at column number(increament by 1)" + columnIndex);
					productDaoObj.save(apiResponse.getErrors(), existingProduct.getExternalProductId() + "-Failed",
							asiNumber, batchId);
				}
			}
			ShippingEstimate shippingEstimObj = proGolfAttributeParser.getProductShippingEstimation(
					shippingEstamationValues.toString(), shippingWeight, shippingDimentionUnits,
					shippingWeightUnit);
			productConfigObj.setShippingEstimates(shippingEstimObj);
			existingProduct.setProductConfigurations(productConfigObj);
			String distributorOnlyComments = existingProduct.getDistributorOnlyComments();
			if(distributorOnlyComments.contains("|")){
				distributorOnlyComments = distributorOnlyComments.replaceAll("\\|", "");
				existingProduct.setDistributorOnlyComments(distributorOnlyComments);
			}
			if (!StringUtils.isEmpty(existingProduct.getExternalProductId())) {
				int num = postServiceImpl.postProduct(accessToken, existingProduct, asiNumber, batchId);
				if (num == 1) {
					numOfProductsSuccess.add("1");
				} else if (num == 0) {
					numOfProductsFailure.add("0");
				} else {
				}
			}
			//This code used only for product present single tab remaining tabs absent case only
			//e.g. club_way file product having single tab
			/*for (Map.Entry<String, Product> products: productMaps.entrySet()) {
				String skuValue = products.getKey();
				Product product = products.getValue();
				List<PriceGrid> listOfPricegrid = setDefaultPriceGrid(product.getPriceGrids());
				product.setPriceGrids(listOfPricegrid);
				if(skuValue.equals("2001")){
					String distributorOnlyComments = product.getDistributorOnlyComments();
					if(distributorOnlyComments.contains("|")){
						distributorOnlyComments = distributorOnlyComments.replaceAll("\\|", "");
						product.setDistributorOnlyComments(distributorOnlyComments);
					}
					int num = postServiceImpl.postProduct(accessToken, product, asiNumber, batchId);
					if (num == 1) {
						numOfProductsSuccess.add("1");
					} else if (num == 0) {
						numOfProductsFailure.add("0");
					} else {
					}
				}
			}*/
			_LOGGER.info("list size>>>>>>>" + numOfProductsSuccess.size());
			_LOGGER.info("Failure list size>>>>>>>" + numOfProductsFailure.size());
			repeatRows.clear();
			finalResult = numOfProductsSuccess.size() + "," + numOfProductsFailure.size();
			productDaoObj.saveErrorLog(asiNumber, batchId);
			return finalResult;
		} catch (Exception e) {
			_LOGGER.error("Error while Processing excel sheet ,Error message: " + e.getMessage());
			return finalResult;
		} finally {
			try {

			} catch (Exception e) {
				_LOGGER.error("Error while Processing excel sheet, Error message: " + e.getMessage());

			}
			_LOGGER.info("Complted processing of excel sheet ");
			_LOGGER.info("Total no of product:" + numOfProductsSuccess.size());
		}

	}

	public boolean isRepeateColumn(int columnIndex) {

		if (columnIndex != 1) {
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
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
		}
		return productXid;
	}
	private String getSkuValue(Row row){
		Cell xidCell = row.getCell(ApplicationConstants.CONST_NUMBER_ZERO);
		String skuVal = CommonUtility.getCellValueStrinOrInt(xidCell);
		return skuVal;
	}
  private List<PriceGrid> setDefaultPriceGrid(List<PriceGrid> listOfPriceGrid){
	  if(!CollectionUtils.isEmpty(listOfPriceGrid)){
		  return listOfPriceGrid;
	  } else {
		  listOfPriceGrid = new ArrayList<>();
	  }
	  PriceGrid priceGrid = new PriceGrid();
	  priceGrid.setCurrency("USD");
	  priceGrid.setDescription("");
	  priceGrid.setIsBasePrice(true);
	  priceGrid.setIsQUR(true);
	  priceGrid.setSequence(1);
	  priceGrid.setPrices(new ArrayList<>());
	 // priceGrid.setPriceConfigurations(new ArrayList<>());
	  listOfPriceGrid.add(priceGrid);
	  return listOfPriceGrid;
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

	public ProGolfInformationAttributeParser getProGolfAttributeParser() {
		return proGolfAttributeParser;
	}

	public void setProGolfAttributeParser(ProGolfInformationAttributeParser proGolfAttributeParser) {
		this.proGolfAttributeParser = proGolfAttributeParser;
	}

}
