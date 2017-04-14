package parser.proGolf;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.AdditionalLocation;
import com.a4tech.product.model.BatteryInformation;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class ProGolfImprintChargesMapping {
	private static final Logger _LOGGER = Logger.getLogger(ProGolfImprintChargesMapping.class);
	private ProGolfPriceGridParser proGolfPriceGridParser;
	private ProGolfInformationAttributeParser proGolfInfoAttriParser;
	
	public Map<String, Product> readMapper(Map<String, Product> productMaps, Sheet sheet) {

		int columnIndex = 0;

		Set<String> productXids = new HashSet<String>();
		List<String> repeatRows = new ArrayList<>();
		ProductConfigurations productConfigObj = new ProductConfigurations();
		Product productExcelObj = new Product();
		String prdXid = null;
		String headerName ="";
		List<String> productIds = new ArrayList<>();
		String imprintLoc = "";
		String imprintLocUpchargeType = "";
		String imprintMethodName = "";
		String productionDays = "";
		
		StringBuilder listOfQuantityImprintLoc = new StringBuilder();
		//StringJoiner listOfQuantityImprintLoc = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfPricesImprintLoc   = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfDiscountImprintLoc = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		List<PriceGrid> existingPriceGrid = null;
		List<ImprintMethod> listOfImprintMethods = null;
		boolean isFirstProduct = true;
		try {
			Row  headerRow = null;
			Iterator<Row> iterator = sheet.iterator();
			Product existingProduct = null;
			while (iterator.hasNext()) {

				try {
					Row nextRow = iterator.next();

					if (nextRow.getRowNum() == 0) {
						headerRow = nextRow;
						continue;
					}
					/*Cell cell1 = nextRow.getCell(1);
					prdXid = CommonUtility.getCellValueStrinOrInt(cell1);*/
					prdXid = getSkuValue(nextRow);
					// this condition used to check xid is present list or not ,
					// if xid present in Map means already fetch product from
					// Map
					/*if (!productIds.contains(prdXid)) {
						existingProduct = productMaps.get(prdXid);
						//productConfigObj = existingProduct.getProductConfigurations();
					}*/
					if(isFirstProduct){
						existingProduct = productMaps.get(prdXid);
						isFirstProduct = false;
					}
					productIds.add(prdXid);
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					/*if (xid != null) {
						productXids.add(xid);
					}*/
					boolean checkXid = false;

					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						columnIndex = cell.getColumnIndex();

						if (columnIndex + 1 == 1) {
							//prdXid = getProductXid(nextRow);
							checkXid = true;
						} else {
							checkXid = false;
						}

						if (checkXid) {
							if (!productXids.contains(prdXid)) {
								if (nextRow.getRowNum() != 1) {
									productExcelObj.setProductConfigurations(productConfigObj);
									existingProduct.setPriceGrids(existingPriceGrid);
									//existingProduct = removeDecorativeASPriceDescription(existingProduct);
									existingProduct = createImprintOptionAndImprintMethod(existingProduct);
									if(isOptionImprintMethodBasePrice(productConfigObj.getOptions())){
										existingProduct = proGolfInfoAttriParser.setBasePriceGridImprintMethodAndOptions(existingProduct);
									} else {
										existingProduct = removeDecorativeASPriceDescription(existingProduct);
									}
									productMaps.put(existingProduct.getProductLevelSku(), existingProduct);
									repeatRows.clear();
									existingProduct = productMaps.get(prdXid);
								}
								 listOfQuantityImprintLoc = new StringBuilder();
								 listOfPricesImprintLoc   	 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								 listOfDiscountImprintLoc	 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								if (!productXids.contains(prdXid)) {
									productXids.add(prdXid);
									repeatRows.add(prdXid);
								}
								existingProduct = productMaps.get(prdXid);
								existingPriceGrid = existingProduct.getPriceGrids();
								productConfigObj = existingProduct.getProductConfigurations();
								if(CollectionUtils.isEmpty(existingPriceGrid)){
									existingPriceGrid = new ArrayList<>();
								}
							}
							 listOfQuantityImprintLoc = new StringBuilder();
							 listOfPricesImprintLoc   	 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							 listOfDiscountImprintLoc	 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
						} else {

							if (productXids.contains(prdXid) && repeatRows.size() != 1) {
								if (isRepeateColumn(columnIndex + 1)) {
									continue;
								}
							}
						}
						headerName = getHeaderName(columnIndex, headerRow);
						switch (headerName) {
						case "Qty_1_Max":
						case "Qty_2_Max":
						case "Qty_3_Max":
						case "Qty_4_Max":
							break;
						case "Matrix":
						case "Max_imprint_color_allowed":
						case "Price_included":
						case "Max_location_allowed":
						case "Location_Price_Included":
						case "full_color":
						case "imprint_area":
							break;
						case "Imprint_position": //Imprint location
							imprintLoc = cell.getStringCellValue();
							if(!StringUtils.isEmpty(imprintLoc)){
								List<ImprintLocation> listOfImprintLoc = proGolfInfoAttriParser.getImprintLocations(
										CommonUtility.getValuesOfArray(imprintLoc, "\\|"), productConfigObj.getImprintLocation());
								productConfigObj.setImprintLocation(listOfImprintLoc);
							}
							break;
						case "Type_of_charge":
							imprintLocUpchargeType = cell.getStringCellValue();
							if("AAA Battery".equalsIgnoreCase(imprintLocUpchargeType)){
								List<BatteryInformation> listOfBattery = getBatteryInformation(imprintLocUpchargeType);
								productConfigObj.setBatteryInformation(listOfBattery);
							} else if("Additional Stitches".equalsIgnoreCase(imprintLocUpchargeType)){
								imprintLocUpchargeType = "Change of Ink/Thread";
							}
							break;
						case "imprint_method":
							imprintMethodName  = cell.getStringCellValue();
							if(!StringUtils.isEmpty(imprintMethodName)){
								listOfImprintMethods = proGolfInfoAttriParser.getProductImprintMethods(
										CommonUtility.getValuesOfArray(imprintMethodName, ","),
										productConfigObj.getImprintMethods());
								productConfigObj.setImprintMethods(listOfImprintMethods);
							}
							
							break;
						
						case "production_days":
							productionDays = cell.getStringCellValue();
							break;
						case "production_unit":
							String produtionUnits = cell.getStringCellValue();
							if(!StringUtils.isEmpty(productionDays)){
								List<ProductionTime> listOfProdutionTime = proGolfInfoAttriParser.getProductionTime(productionDays,
		                                   produtionUnits, productConfigObj.getProductionTime());
								productConfigObj.setProductionTime(listOfProdutionTime);
							}
							break;
						case "Setup_Charge"://Imprint method related to Up charge
							String priceVal = cell.getStringCellValue();
							if(!StringUtils.isEmpty(priceVal)){
								String discountCode = getDiscountCode(priceVal);
								priceVal = priceVal.replaceAll("\\(.*\\)", "").trim();
								existingPriceGrid = proGolfPriceGridParser.getUpchargePriceGrid("1", priceVal,
										discountCode, "IMMD", false, "USD", imprintMethodName, "Set-up Charge",
										"Other", 1, existingPriceGrid,"","");
							}
							break;
						case "Additional_Location_Charge":
							String additionalLocPriceValue = cell.getStringCellValue();
							if(!StringUtils.isEmpty(additionalLocPriceValue)){
								List<AdditionalLocation> listOfAdditioLoc = getAdditionalLocation("Additional Location");
								String loctionCode = getDiscountCode(additionalLocPriceValue);
								additionalLocPriceValue = additionalLocPriceValue.replaceAll("\\(.*\\)", "").trim();
								existingPriceGrid = proGolfPriceGridParser.getUpchargePriceGrid("1", additionalLocPriceValue,
										loctionCode, "ADLN", false, "USD", "Additional Location",
										"Add. Location Charge", "Other", 1, existingPriceGrid,"","");
								productConfigObj.setAdditionalLocations(listOfAdditioLoc);
							}
							break;
						case "Additional_Color_Charge":
							String additionalColorPriceValue = cell.getStringCellValue();
							if(!StringUtils.isEmpty(additionalColorPriceValue)){
								List<AdditionalColor> listOfAdditioColor = getAdditionalColor("Additional Color");
								String additionalColorCode = getDiscountCode(additionalColorPriceValue);
								additionalColorPriceValue = additionalColorPriceValue.replaceAll("\\(.*\\)", "").trim();
								existingPriceGrid = proGolfPriceGridParser.getUpchargePriceGrid("1", additionalColorPriceValue,
										additionalColorCode, "ADCL", false, "USD", "Additional Color",
										"Add. Color Charge", "Other", 1, existingPriceGrid,"","");
								productConfigObj.setAdditionalColors(listOfAdditioColor);
							}
							break;
						case "Rush_Charge":
							//String rushPriceVal = cell.getStringCellValue();
							/*if(!StringUtils.isEmpty(rushPriceVal)){
								String rushDiscCode = getDiscountCode(rushPriceVal);
								RushTime rushTimeValue = productConfigObj.getRushTime();
								if(rushTimeValue == null){
									rushTimeValue = getRushTime();
								}
								rushPriceVal = rushPriceVal.replaceAll("\\(.*\\)", "").trim();
								existingPriceGrid = proGolfPriceGridParser.getUpchargePriceGrid("1", rushPriceVal,
										rushDiscCode, "RUSH", false, "USD", "Rush Service",
										"Rush Service Charge", "Other", 1, existingPriceGrid,"","");
								productConfigObj.setRushTime(rushTimeValue);
							}
							*/
							break;
						case "LTM_Charge":
							String lessThanValue = cell.getStringCellValue();
							if(!StringUtils.isEmpty(lessThanValue)){
								String lessThanDiscountCode = getDiscountCode(lessThanValue);
								lessThanValue = lessThanValue.replaceAll("\\(.*\\)", "").trim();
								productExcelObj.setCanOrderLessThanMinimum(ApplicationConstants.CONST_BOOLEAN_TRUE);
								existingPriceGrid = proGolfPriceGridParser.getUpchargePriceGrid("1", lessThanValue,
										lessThanDiscountCode, "LMIN", false, "USD", "Can order less than minimum",
										"Less Than Minimum Charge", "Other", 1, existingPriceGrid,"","");
							}
							break;
						case "PMS_Charge":
							String pmsChargeVal = cell.getStringCellValue();
							if(!StringUtils.isEmpty(pmsChargeVal)){
								String pmsDiscountCode = getDiscountCode(pmsChargeVal);
								pmsChargeVal = pmsChargeVal.replaceAll("\\(.*\\)", "").trim();
								existingPriceGrid = proGolfPriceGridParser.getUpchargePriceGrid("1", pmsChargeVal,
										pmsDiscountCode, "IMMD", false, "USD", imprintMethodName,
										"PMS Matching Charge", "Other", 1, existingPriceGrid,"","");
							}
							break;
						case "Qty_1_Min":
						case "Qty_2_Min":
						case "Qty_3_Min":
						case "Qty_4_Min":
							String qty = cell.getStringCellValue();
							if(!StringUtils.isEmpty(qty)){
								listOfQuantityImprintLoc.append(qty)
										.append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							}
							
							break;
						case "Price_1":
						case "Price_2":
						case "Price_3":
						case "Price_4":
							String price = cell.getStringCellValue();
							listOfPricesImprintLoc.add(price);
							break;
						case "Code_1":
						case "Code_2":	
						case "Code_3":
						case "Code_4":
							String discountCode1 = cell.getStringCellValue();
							listOfDiscountImprintLoc.add(discountCode1);
							break;
				
						} // end inner while loop

					}
					if(!StringUtils.isEmpty(imprintLocUpchargeType)){
						if(!"AAA Battery".equalsIgnoreCase(imprintLocUpchargeType)){
							if(!listOfPricesImprintLoc.toString().isEmpty()){
								String quantity = listOfQuantityImprintLoc.toString();
								if(quantity.isEmpty()){
									quantity = "1";
								}
								if(!"Not Available as Logo Product".equalsIgnoreCase(imprintLoc)){
									if("Additional Location Charge".equalsIgnoreCase(imprintLocUpchargeType)){
										imprintLocUpchargeType = "Imprint Location Charge";
									}
									existingPriceGrid = proGolfPriceGridParser.getUpchargePriceGrid(quantity,
											listOfPricesImprintLoc.toString(), listOfDiscountImprintLoc.toString(), "IMLO", false,
											"USD", imprintLoc, imprintLocUpchargeType, "Other", 1, existingPriceGrid,"","");
								}
							}
						}
					}
				} catch (Exception e) {
					_LOGGER.error(
							"Error while Processing ProductId and cause :" + productExcelObj.getExternalProductId()
									+ " " + e.getMessage() + "at column number(increament by 1):" + columnIndex);
				}
			}
			repeatRows.clear();
			productExcelObj.setProductConfigurations(productConfigObj);
			existingProduct.setPriceGrids(existingPriceGrid);
			//existingProduct = removeDecorativeASPriceDescription(existingProduct);
			existingProduct = createImprintOptionAndImprintMethod(existingProduct);
			if(isOptionImprintMethodBasePrice(productConfigObj.getOptions())){
				existingProduct = proGolfInfoAttriParser.setBasePriceGridImprintMethodAndOptions(existingProduct);
			} else {
				existingProduct = removeDecorativeASPriceDescription(existingProduct);
			}
			productMaps.put(prdXid, existingProduct);
			return productMaps;
		} catch (Exception e) {
			_LOGGER.error(
					"Error while Processing " + sheet.getSheetName() + "+ sheet ,Error message: " + e.getMessage());
			return productMaps;
		} finally {
		}

	}
	private String getHeaderName(int columnIndex,Row headerRow){
		 Cell cell2 =  headerRow.getCell(columnIndex);  
		 String headerName=CommonUtility.getCellValueStrinOrInt(cell2);
		//columnIndex = ProGolfHeaderMapping.getHeaderIndex(headerName);
		return headerName;
	}
	public boolean isRepeateColumn(int columnIndex) {

		if (columnIndex != 1) {
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	private String  getDiscountCode(String priceval){//250.00(A) ,discount code:A
		String disCode = priceval.replaceAll("[^a-zA-Z]", "").trim();
		if(StringUtils.isEmpty(disCode)){
			disCode = "A";// default discount code
		}
		return disCode;
	}
	/*private RushTime getRushTime(){
		RushTime rushTimeObj = new RushTime();
		rushTimeObj.setAvailable(ApplicationConstants.CONST_BOOLEAN_TRUE);
		List<RushTimeValue> listOfRushTimeValues = new ArrayList<>();
		RushTimeValue rushTimevaluesObj = new RushTimeValue();
		rushTimevaluesObj.setDetails("");
		rushTimevaluesObj.setBusinessDays("");
		listOfRushTimeValues.add(rushTimevaluesObj);
		rushTimeObj.setRushTimeValues(listOfRushTimeValues);
		return rushTimeObj;
	}*/
	private List<AdditionalLocation> getAdditionalLocation(String loctionVal){
		List<AdditionalLocation> listOfAdditionalLoc = new ArrayList<>();
		AdditionalLocation additionalLocObj = new AdditionalLocation();
		additionalLocObj.setName(loctionVal);
		listOfAdditionalLoc.add(additionalLocObj);
		return listOfAdditionalLoc;
	}
	private List<AdditionalColor> getAdditionalColor(String colorVal){
		List<AdditionalColor> listOfAdditionalColor = new ArrayList<>();
		AdditionalColor additionalColorObj = new AdditionalColor();
		additionalColorObj.setName(colorVal);
		listOfAdditionalColor.add(additionalColorObj);
		return listOfAdditionalColor;
	}
	private List<BatteryInformation> getBatteryInformation(String batterySizeVal){
		List<BatteryInformation> listOfBattery = new ArrayList<>();
		BatteryInformation batteryObj = new BatteryInformation();
		batteryObj.setName("BATTERY SIZE");
		batteryObj.setComments(batterySizeVal);
		listOfBattery.add(batteryObj);
		return listOfBattery;
	}
	/*private String getProductXid(Row row) {
		Cell xidCell = row.getCell(ApplicationConstants.CONST_NUMBER_ZERO);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if (StringUtils.isEmpty(productXid)) {
			xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
			productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}*/
	private String getSkuValue(Row row){
		Cell xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
		String skuVal = CommonUtility.getCellValueStrinOrInt(xidCell);
		return skuVal;
	}

	public ProGolfPriceGridParser getProGolfPriceGridParser() {
		return proGolfPriceGridParser;
	}
    private Product removeDecorativeASPriceDescription(Product existingProduct){
    	List<PriceGrid> oldPriceGrids = existingProduct.getPriceGrids();
    	ProductConfigurations config = existingProduct.getProductConfigurations();
    	List<PriceGrid> newPriceGrid = new ArrayList<>();
    	int basePriceCount = countNoOfBasePriceGrids(oldPriceGrids);
    	for (PriceGrid priceGrid : oldPriceGrids) {
			  if(priceGrid.getIsBasePrice() && priceGrid.getDescription().equalsIgnoreCase("decorative")){
				// caller method used to collecting existing imprint methods values if type is decorative
		    		String imprintMethodvals = getImprintMethodValues(config.getImprintMethods());
		    		if(StringUtils.isEmpty(imprintMethodvals)){
		    			List<ImprintMethod> imprintMethods =proGolfInfoAttriParser.
				    			getProductImprintMethods(CommonUtility.getValuesOfArray("Imprint", ","), 
				    					config.getImprintMethods());
		    			config.setImprintMethods(imprintMethods);
		    			existingProduct.setProductConfigurations(config);
		    			imprintMethodvals = "Imprint";
		    		}
		    		priceGrid.setDescription(imprintMethodvals);
		    		//priceGrid.setIsBasePrice(false);
		    		//if(basePriceCount < ApplicationConstants.CONST_INT_VALUE_TWO && priceGrid.getIsBasePrice()){
		    		if(basePriceCount < ApplicationConstants.CONST_INT_VALUE_TWO){
		    			priceGrid.setPriceConfigurations(new ArrayList<>());
		    		} else{
		    			priceGrid.setPriceConfigurations(getPriceConfiguration(imprintMethodvals));
		    		}
		    		
		    		newPriceGrid.add(priceGrid);
			  } else {
				  newPriceGrid.add(priceGrid);
			  }
		}
    	existingProduct.setPriceGrids(newPriceGrid);
    	return existingProduct;
    }
    private Product createImprintOptionAndImprintMethod(Product existingProduct){
    	List<PriceGrid> oldPriceGrids = existingProduct.getPriceGrids();
    	ProductConfigurations config = existingProduct.getProductConfigurations();
    	PriceGrid newPriceGrid = null;
    	for (PriceGrid priceGrid : oldPriceGrids) {
			  if(priceGrid.getIsBasePrice()== true && priceGrid.getDescription().contains("Embroidery")){
				List<Option> options = proGolfInfoAttriParser.getProductOption("Embroidery", "Imprint", "Imprint Setup",
						false, config.getOptions()); 
				config.setOptions(options);
				newPriceGrid = priceGrid;
				break;
			  }
    	}
    	if(newPriceGrid != null){
    		PriceGrid priceGrid1 = new PriceGrid();
    		priceGrid1.setCurrency("USD");
    		priceGrid1.setDescription("Embroidery");
    		priceGrid1.setIsBasePrice(false);
    		priceGrid1.setServiceCharge("Optional");
    		priceGrid1.setUpchargeType("Imprint Option Charge");
    		priceGrid1.setUpchargeUsageType("Other");
    		priceGrid1.setIsQUR(newPriceGrid.getIsQUR());
    		priceGrid1.setPrices(newPriceGrid.getPrices());
    		List<PriceConfiguration> listOfPriceConfigs = new ArrayList<>();
    		PriceConfiguration priceConfigurationObj = new PriceConfiguration(); 
    		priceConfigurationObj.setCriteria("Imprint Option");
    		priceConfigurationObj.setOptionName("Imprint Setup");
    		priceConfigurationObj.setValue(Arrays.asList("Embroidery"));
    		listOfPriceConfigs.add(priceConfigurationObj);
    		priceGrid1.setPriceConfigurations(listOfPriceConfigs);
    		oldPriceGrids.add(priceGrid1);
    	}
    	existingProduct.setProductConfigurations(config);
    	existingProduct.setPriceGrids(oldPriceGrids);
    	return existingProduct;
    }
    private String getImprintMethodValues(List<ImprintMethod> imprintMethods){
  	  if(CollectionUtils.isEmpty(imprintMethods)){
  		  return "";
  	  }
  	  StringJoiner imprintMethodValues = new StringJoiner(",");
  	  for (ImprintMethod imprintMethod : imprintMethods) {
		   if(!imprintMethod.getAlias().equalsIgnoreCase("UNIMPRINTED")){
			   imprintMethodValues.add(imprintMethod.getAlias());
		   }
	}
  		/*String imprintMethodValues = imprintMethods.stream().map(ImprintMethod::getAlias)
  				.collect(Collectors.joining(","));*/
  		return imprintMethodValues.toString();
    }
    private List<PriceConfiguration> getPriceConfiguration(String imprintMethodVals){
    	List<PriceConfiguration> listOfPriceConfiguration = new ArrayList<>();
    	PriceConfiguration priceConfigObj = null;
    	String[] vals = CommonUtility.getValuesOfArray(imprintMethodVals, ",");
    	for (String imprMethodVal : vals) {
    		/*if(imprMethodVal.equalsIgnoreCase("UNIMPRINTED")){
    			continue;
    		}*/
    		priceConfigObj = new PriceConfiguration();
    		priceConfigObj.setCriteria("Imprint Method");
    		priceConfigObj.setValue(Arrays.asList(imprMethodVal));
    		listOfPriceConfiguration.add(priceConfigObj);
		}
    	return listOfPriceConfiguration;
    }
    private int countNoOfBasePriceGrids(List<PriceGrid> priceGrid){
    	int basePriceCount = 0;
    	for (PriceGrid priceGrid2 : priceGrid) {
			if(priceGrid2.getIsBasePrice() == true){
				basePriceCount++;
			}
		}
    	return basePriceCount;
    }
    /*Author : Venkat
     * Description : This method used to check price grid need to participate imprint method and Option or not
     * Param :List<Option> 
     * Return : true/fase,if option value contains "Wilson Staff" it returns True
     * 
     */
    private boolean isOptionImprintMethodBasePrice(List<Option> listOfOption){
    	List<OptionValue> listOfOptionVal = null;
    	if(CollectionUtils.isEmpty(listOfOption)){
    		return false;
    	}
    	for (Option option : listOfOption) {
			  if(option.getOptionType().equalsIgnoreCase("Product")){
				  listOfOptionVal = option.getValues();
				  break;
			  }
		}
    	if(listOfOptionVal != null){
    		if(listOfOptionVal.size() < 2){
    			return false;
    		}
    		for (OptionValue optionValue : listOfOptionVal) {
				 if(optionValue.getValue().contains("Wilson Staff")){
					 return true;
				 }
			}
    	}
    	return false;
    }
	public void setProGolfPriceGridParser(ProGolfPriceGridParser proGolfPriceGridParser) {
		this.proGolfPriceGridParser = proGolfPriceGridParser;
	}
	public ProGolfInformationAttributeParser getProGolfInfoAttriParser() {
		return proGolfInfoAttriParser;
	}
	public void setProGolfInfoAttriParser(ProGolfInformationAttributeParser proGolfInfoAttriParser) {
		this.proGolfInfoAttriParser = proGolfInfoAttriParser;
	}
}
