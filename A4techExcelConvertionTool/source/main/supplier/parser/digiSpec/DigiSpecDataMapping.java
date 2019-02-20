package parser.digiSpec;

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
import com.a4tech.product.model.Artwork;
import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.ImprintColor;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Inventory;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.Samples;
import com.a4tech.product.model.Shape;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.TradeName;
import com.a4tech.product.service.imple.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class DigiSpecDataMapping{
	
	private static final Logger _LOGGER = Logger.getLogger(DigiSpecDataMapping.class);
	private PostServiceImpl           postServiceImpl;
	private ProductDao                productDaoObj;
	private DigiSpecAttributeParser   digiAttributeParser;
	
	public Map<String, Product> readExcel(String accessToken,Sheet sheet, Map<String, Product> productsMap ,String environmentType){
		
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
 		try{
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		String xid = null;
		int columnIndex = 0;
		String priceVal ="";
		StringBuilder shippingValues = new StringBuilder();
		//StringBuilder rushTimeVals = new StringBuilder();
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if(nextRow.getRowNum() == 0 || nextRow.getRowNum() == 1){
				continue;
			}
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if(xid != null){
				productXids.add(xid);
			}
			boolean checkXid  = false;			
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				columnIndex = cell.getColumnIndex();
				if(columnIndex + 1 == 1){
					xid = getProductXid(nextRow);
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() !=2 ){
							 System.out.println("Java object converted to JSON String, written to file");
							ShippingEstimate shippingEstimation = digiAttributeParser.getProductShippingEstimation(shippingValues.toString());
							productConfigObj.setShippingEstimates(shippingEstimation);
							 	productExcelObj.setPriceGrids(priceGrids);
							 	if(CollectionUtils.isEmpty(productConfigObj.getImprintMethods())){
										List<ImprintMethod> imprMethodList = digiAttributeParser
												.getProductImprintMethods("Unimprinted",
														productConfigObj.getImprintMethods());
										productConfigObj.setImprintMethods(imprMethodList);
							 	}
							 	productExcelObj.setProductConfigurations(productConfigObj);
							 	productsMap.put(productExcelObj.getExternalProductId(), productExcelObj);
								priceGrids = new ArrayList<PriceGrid>();
								productConfigObj = new ProductConfigurations();
								shippingValues = new StringBuilder();
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    }
						    productExcelObj = new Product();
     						 productExcelObj = postServiceImpl.getProduct(accessToken, xid, environmentType);
						     if(productExcelObj == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						    	 productExcelObj = digiAttributeParser.keepExistingProductData(productExcelObj);
						    	 productConfigObj=productExcelObj.getProductConfigurations();
						    	 if(productConfigObj == null){
						    		 productConfigObj = new ProductConfigurations();
						    	 }
						     }	
					 }
				}
				switch (columnIndex+1) {
				case 1://xid
					//String productXid = CommonUtility.getCellValueStrinOrInt(cell);
					productExcelObj.setExternalProductId(xid);
					 break;
				case 2://productNo
					productExcelObj.setAsiProdNo(cell.getStringCellValue());
					break;
				case 3:
				case 4:
				case 5: // ignore 
					break;
				case 6://Sizes
					String size = cell.getStringCellValue();
			      Size sizeObj = digiAttributeParser.getProductSize(size);
			      productConfigObj.setSizes(sizeObj);
				    break;
				case 7://shapes
					String shape = cell.getStringCellValue();
					if(!StringUtils.isEmpty(shape)){
						List<Shape> shapeList = digiAttributeParser.getProductShapes(shape);
						productConfigObj.setShapes(shapeList);
					}
					  break;
				case 8://product name
					String prdName = cell.getStringCellValue();
					productExcelObj.setName(removeSpecialSymbols(prdName));
					break;
				case 9://Categories
				
					break;
				case 10://theme
					String theme = cell.getStringCellValue();
					if(!StringUtils.isEmpty(theme)){
						List<Theme> themeList = digiAttributeParser.getProductThemes(theme);
						 productConfigObj.setThemes(themeList);	
					}
					 
					break;
				case 11://summary
					String summary = cell.getStringCellValue();
					productExcelObj.setSummary(removeSpecialSymbols(summary));
					break;
				case 12: //description
					String desc = cell.getStringCellValue();
					productExcelObj.setDescription(removeSpecialSymbols(desc));
					break;
				case 13: //keywords
				String keywords = cell.getStringCellValue();
				if(!StringUtils.isEmpty(keywords)){
					List<String> keywordList = digiAttributeParser.getProductKeywords(keywords);
					productExcelObj.setProductKeywords(keywordList);	
				}
					break;

				case 14://Materials
					String material = cell.getStringCellValue();
					if(!StringUtils.isEmpty(material)){
						List<Material> materialList = digiAttributeParser.getProductMaterial(material);
						productConfigObj.setMaterials(materialList);	
					}
					
					break;
					
				case 15: //product colors
					String color = cell.getStringCellValue();
					if(!StringUtils.isEmpty(color)){
						List<Color> colorList = digiAttributeParser.getProductColor(color);
						 productConfigObj.setColors(colorList);	
					}
					break;
					
				case 16://Product Sample
					String productSample = cell.getStringCellValue();
					if(!StringUtils.isEmpty(productSample)){
						Samples samples = digiAttributeParser.getProductSample(productSample);
					    productConfigObj.setSamples(samples);	
					}
					break;
				case 17://spec sample
					// parsing is not required because this column present in "N" value only 
					  break;
				case 18: //Production Time (working days)
					String prdTime = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(prdTime)){
						List<ProductionTime> productionTimeList = digiAttributeParser.getProductionTime(prdTime);
						productConfigObj.setProductionTime(productionTimeList);	
					}
					break;
				
				 case 19: //rush service available
					 String rushSerAvail = cell.getStringCellValue();
					 /*if(!StringUtils.isEmpty(rushSerAvail)){
					 }*/
					break;
					
				case 20: //Rush service
					String rushTime = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(rushTime)){
						RushTime rushTimeObj = digiAttributeParser.getProductRushTime(rushTime);
						productConfigObj.setRushTime(rushTimeObj);
					}
					break;
				case 21: //Same Day (rush) Service Available
					// parsing is not required because this column present in "N" value only
					break;
				case 22: //Catalog page
					String catalog = cell.getStringCellValue();
					if(!StringUtils.isEmpty(catalog)){
						List<Catalog> catalogList = digiAttributeParser.getProductCatalog(catalog);
						productExcelObj.setCatalogs(catalogList);	
					}
					
					break;
				case 23://Imprint Methods + Imprint Charge-Misc
					String imprCharge = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprCharge)){
						productExcelObj.setProductConfigurations(productConfigObj);
						productExcelObj = digiAttributeParser.getImprintMethodUpcharge(productExcelObj, imprCharge);
						productConfigObj = productExcelObj.getProductConfigurations();
						priceGrids       = productExcelObj.getPriceGrids();	
					}
					
				   break;
				case 24: //Imprint Methods
					String imprMethod = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprMethod)){
						List<ImprintMethod> imprintMethodList = digiAttributeParser
								.getProductImprintMethods(imprMethod, productConfigObj.getImprintMethods());
						productConfigObj.setImprintMethods(imprintMethodList);
					}
						
				   break;
				   
				case 25:  //Additional Colors/Locations
					//there is no data for this columns
					break;
				case 26: //imprint area
					String imprArea = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprArea)){
						List<ImprintLocation> imprintLocList = digiAttributeParser.getProductImprintLocation(imprArea);
						productConfigObj.setImprintLocation(imprintLocList);	
					}
					break;
				case 27://Full Color Process
							String fullColorProcess = cell.getStringCellValue();
							if (!StringUtils.isEmpty(fullColorProcess)) {
								if (fullColorProcess.equalsIgnoreCase("Y")) {
									List<ImprintMethod> imprinMethodList = digiAttributeParser.getProductImprintMethods(
											"Full Color Process", productConfigObj.getImprintMethods());
									productConfigObj.setImprintMethods(imprinMethodList);
								}
							}
					break;
				case 28:// personolization
					// parsing is not required because this column present in "N" value only
					break;
				case 29://sold unimprinted
					// parsing is not required because this column present in "N" value only
					break;
				case 30://Price Includes  (100)
					String priceInclude = cell.getStringCellValue();
					productExcelObj.setDeliveryOption(priceInclude);//this is used to reference purpose only
					break;
				case 31://artwork
					String artwork = cell.getStringCellValue();
					List<Artwork> artworkList = digiAttributeParser.getProductArtwork(artwork);
					productConfigObj.setArtwork(artworkList);
					break;
				case 32://Imprint Colors
					String imprColors = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprColors)){
						ImprintColor imprintColor = digiAttributeParser.getProductImprintColor(imprColors);
						productConfigObj.setImprintColors(imprintColor);	
					}
					break;
				case 33://Imprint options
					String imprintOption = cell.getStringCellValue();
					productExcelObj.setAdditionalProductInfo(imprintOption);
					break;
				case 34://Trade Names
					String tradeName = cell.getStringCellValue();
					if(!StringUtils.isEmpty(tradeName)){
						if(tradeName.contains("™")){
							tradeName = tradeName.replaceAll("™", " (TM)");
						}
						tradeName = removeSpecialSymbols(tradeName);
						List<TradeName> tradeNameList = digiAttributeParser.getProductTradeName(tradeName);
						productConfigObj.setTradeNames(tradeNameList);
					}
					break;
				case 35://fob point
					// no need process because only one value present this column i.e"Nevada",
					                                           //this value not present on foblookup table
					break;
				case 36://origin
					String origin = cell.getStringCellValue();
					if(!StringUtils.isEmpty(origin)){
						List<Origin> originList = digiAttributeParser.getProductOrigin(origin);
						productConfigObj.setOrigins(originList);	
					}
					break;
				case 37://Packaging
					String packVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(packVal)){
						productExcelObj.setProductConfigurations(productConfigObj);
						productExcelObj.setPriceGrids(priceGrids);
						productExcelObj = digiAttributeParser.getProductPackaging(packVal, productExcelObj);
						productConfigObj = productExcelObj.getProductConfigurations();
						priceGrids       = productExcelObj.getPriceGrids();	
					}
					break;
				case 38://product options
					String productdOptions = cell.getStringCellValue();
					if(!StringUtils.isEmpty(productdOptions)){
						productExcelObj.setProductConfigurations(productConfigObj);
						productExcelObj.setPriceGrids(priceGrids);
						productExcelObj = digiAttributeParser.getProductOptions(productdOptions, productExcelObj);
						productConfigObj = productExcelObj.getProductConfigurations();
						priceGrids       = productExcelObj.getPriceGrids();	
					}
					break;
				case 39://Shipping_Items
					shippingValues.append(CommonUtility.getCellValueStrinOrInt(cell)).append(",");
					break;
				case 40://Shipping_dimension
					shippingValues.append(cell.getStringCellValue()).append(",");
					break;
				case 41://Shipping_weight
					shippingValues.append(CommonUtility.getCellValueStrinOrInt(cell));
					break;
				case 42://Product_SKU
					productExcelObj.setProductLevelSku(cell.getStringCellValue());
					break;
				case 43://Product_Inventory_Link (Deprecated)
					//process is not required
					break;
				case 44://Product_Inventory_Status
					Inventory inventory = new Inventory();
					inventory.setInventoryStatus(cell.getStringCellValue());
					productExcelObj.setInventory(inventory);
					break;
				case 45://Product_Inventory_Quantity
					//process is not required
					break;
				case 46://Product_IsHazmat
					//process is not required
					break;
				case 47://Product_IsCloseOut
					//process is not required
					break;
				case 48://Size_Group
					//process is not required
					break;
				case 49://Linename
					String lineName = cell.getStringCellValue();
					//DIGISPEC® data is available
					productExcelObj.setLineNames(Arrays.asList("DIGISPEC Mouse Pads"));
					break;
				case 50://Imprint Size
					String imprintSize = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintSize)){
						List<ImprintSize> imprintSizeList = digiAttributeParser.getProductImprintSize(imprintSize);
						productConfigObj.setImprintSize(imprintSizeList);	
					}
					break;
				case 51://Additional_Color
					//process is not required since null value is present in column
					break;
				case 52://Additional_Location
					//process is not required since null value is present in column
					break;
				case 53://Shipper_Bills_By
					String shipperBillBy = cell.getStringCellValue();
					if(!StringUtils.isEmpty(shipperBillBy)){
						productExcelObj.setShipperBillsBy("Weight of the Package");
					}
					break;
				case 54://Ship_Plain_Box
					String shipPlainBox = cell.getStringCellValue();
					if(!StringUtils.isEmpty(shipPlainBox)){
						if(shipPlainBox.equalsIgnoreCase("Y")){
							productExcelObj.setCanShipInPlainBox(true);
						}
					}
					break;
				case 55://Comp_Cert
					String compliance = cell.getStringCellValue();
					//Prop 65, CPSIA data is available
					productExcelObj.setComplianceCerts(Arrays.asList("CPSIA"));
					break;
				case 56://Product_Data_Sheet
					String productDataSheet = cell.getStringCellValue();
					productExcelObj.setProductDataSheet(productDataSheet);
					break;
				case 57://Distibutor_Only
					productExcelObj.setDistributorOnlyComments(removeSpecialSymbols(cell.getStringCellValue()));
					
					break;
				case 58://currency
					//default value 
					break;
				case 59://Less_Than_Min
					//process is not required since N value is present in column
					break;
				case 60://Price_Type
					//default value i.e List
					break;
				case 61://Confirmed_Thru_Date
					//it is past date ,no need to require set this data
					break;
			}  // end inner while loop		 
		}
				productExcelObj.setPriceType("L");
				if(!StringUtils.isEmpty(priceVal)){
					/*priceGrids = dacassoPriceGridParser.getBasePriceGrid(priceVal,"1","P", "USD",
							         "", true, false, "","",priceGrids,"","");*/	
				}
					
				
			}catch(Exception e){

				_LOGGER.error(
						"Error while Processing Product Information sheet  and cause :" + productExcelObj.getExternalProductId()
								+ " " + e.getMessage() + "at column number(increament by 1):" + columnIndex);
		}
		}
		//workbook.close();
			ShippingEstimate shippingEstimation = digiAttributeParser.getProductShippingEstimation(shippingValues.toString());
			productConfigObj.setShippingEstimates(shippingEstimation);
		 	productExcelObj.setPriceGrids(priceGrids);
		 	if(CollectionUtils.isEmpty(productConfigObj.getImprintMethods())){
				List<ImprintMethod> imprMethodList = digiAttributeParser
						.getProductImprintMethods("Unimprinted",
								productConfigObj.getImprintMethods());
				productConfigObj.setImprintMethods(imprMethodList);
	 	}
		 	productExcelObj.setProductConfigurations(productConfigObj);
		 	productsMap.put(productExcelObj.getExternalProductId(), productExcelObj);
	
		 /*	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId, environmentType);
		 	if(num ==1){
		 		numOfProductsSuccess.add("1");
		 	}else if(num == 0){
		 		numOfProductsFailure.add("0");
		 	}else{
		 		
		 	}
		 	_LOGGER.info("list size>>>>>>"+numOfProductsSuccess.size());
		 	_LOGGER.info("Failure list size>>>>>>"+numOfProductsFailure.size());
	       finalResult = numOfProductsSuccess.size() + "," + numOfProductsFailure.size();
	       productDaoObj.saveErrorLog(asiNumber,batchId);*/
	       return productsMap;
		}catch(Exception e){
			_LOGGER.error("Error while Processing excel sheet " +e.getMessage());
			return productsMap;
		}finally{
		}
	}
	public List<Material> getExistingProductMaterials(Product existingProduct){
		ProductConfigurations configuration = existingProduct.getProductConfigurations();
		return configuration.getMaterials();
	}
	private String getProductXid(Row row){
		Cell xidCell = row.getCell(ApplicationConstants.CONST_NUMBER_ZERO);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if (StringUtils.isEmpty(productXid)) {
			xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
			productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}
	private String removeSpecialSymbols(String val){
		val = val.replaceAll("®", "");
		val = val.replaceAll("™", "");
		return val;
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
	public void setDigiAttributeParser(DigiSpecAttributeParser digiAttributeParser) {
		this.digiAttributeParser = digiAttributeParser;
	}
}
