package parser.proGolf;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.Values;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.sun.mail.util.LineOutputStream;

public class ProGolfImprintChargesMapping {
	private static final Logger _LOGGER = Logger.getLogger(ProGolfImprintChargesMapping.class);
	private ProGolfPriceGridParser proGolfPriceGridParser;
	private ProGolfInformationAttributeParser proGolfInfoAttriParser;
	
	public Map<String, Product> readMapper(HashMap<String, Product> productMaps, Sheet sheet) {

		int columnIndex = 0;

		Set<String> productXids = new HashSet<String>();
		List<String> repeatRows = new ArrayList<>();
		ProductConfigurations productConfigObj = new ProductConfigurations();
		Product productExcelObj = new Product();
		String productId = null;
		String xid = null;
		String prdXid = null;
		String headerName ="";
		List<String> productIds = new ArrayList<>();
		String imprintLoc = "";
		String imprintLocUpchargeType = "";
		String imprintMethodName = "";
		String productionDays = "";
		StringJoiner listOfQuantityImprintMethod = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfPricesImprintMethod   = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfDiscountImprintMethod = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfQuantityImprintLoc = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfPricesImprintLoc   = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfDiscountImprintLoc = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		List<PriceGrid> existingPriceGrid = null;
		List<ImprintMethod> listOfImprintMethods = null;
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
					Cell cell1 = nextRow.getCell(1);
					prdXid = CommonUtility.getCellValueStrinOrInt(cell1);
					// this condition used to check xid is present list or not ,
					// if xid present in Map means already fetch product from
					// Map
					if (!productIds.contains(prdXid)) {
						existingProduct = productMaps.get(prdXid);
					}
					productIds.add(prdXid);
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					if (xid != null) {
						productXids.add(xid);
					}
					boolean checkXid = false;

					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						columnIndex = cell.getColumnIndex();

						if (columnIndex + 1 == 1) {
							Cell cell2 = nextRow.getCell(1);
							prdXid = CommonUtility.getCellValueStrinOrInt(cell2);
							checkXid = true;
						} else {
							checkXid = false;
						}

						if (checkXid) {
							if (!productXids.contains(xid)) {
								if (nextRow.getRowNum() != 1) {
									productExcelObj.setProductConfigurations(productConfigObj);
									if (!StringUtils.isEmpty(productExcelObj.getExternalProductId())) {
									}
									productMaps.put(prdXid, existingProduct);
									repeatRows.clear();

								}
								 listOfQuantityImprintMethod = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								 listOfPricesImprintMethod   = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								 listOfDiscountImprintMethod = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								 listOfQuantityImprintLoc 	 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								 listOfPricesImprintLoc   	 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								 listOfDiscountImprintLoc	 = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
								if (!productXids.contains(xid)) {
									productXids.add(xid);
									repeatRows.add(xid);
								}
								
								existingProduct = productMaps.get(xid);
								existingPriceGrid = existingProduct.getPriceGrids();
								if(CollectionUtils.isEmpty(existingPriceGrid)){
									existingPriceGrid = new ArrayList<>();
								}
							}
						} else {

							if (productXids.contains(xid) && repeatRows.size() != 1) {
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
							List<ImprintLocation> listOfImprintLoc = proGolfInfoAttriParser.getImprintLocations(
									CommonUtility.getValuesOfArray(imprintLoc, "\\|"), productConfigObj.getImprintLocation());
							productConfigObj.setImprintLocation(listOfImprintLoc);
							break;
						case "Type_of_charge":
							imprintLocUpchargeType = cell.getStringCellValue();
							if("AAA Battery".equalsIgnoreCase(imprintLocUpchargeType)){
								List<BatteryInformation> listOfBattery = getBatteryInformation(imprintLocUpchargeType);
							} else if("Additional Stitches".equalsIgnoreCase(imprintLocUpchargeType)){
								imprintLocUpchargeType = "Change of Ink/Thread";
							}
							break;
						case "imprint_method":
							imprintMethodName  = cell.getStringCellValue();
							listOfImprintMethods = proGolfInfoAttriParser.getProductImprintMethods(
									CommonUtility.getValuesOfArray(imprintMethodName, ","),
									productConfigObj.getImprintMethods());
							break;
						
						case "production_days":
							productionDays = cell.getStringCellValue();
							break;
						case "production_unit":
							String produtionUnits = cell.getStringCellValue();
							List<ProductionTime> listOfProdutionTime = proGolfInfoAttriParser.getProductionTime(productionDays,
									                                   produtionUnits, productConfigObj.getProductionTime());
							productConfigObj.setProductionTime(listOfProdutionTime);
							break;
						case "Setup_Charge"://Imprint method related to Up charge
							String priceVal = cell.getStringCellValue();
							if(!StringUtils.isEmpty(priceVal)){
								String discountCode = getDiscountCode(priceVal);
								priceVal = priceVal.replaceAll("\\(.*\\)", "").trim();
								existingPriceGrid = proGolfPriceGridParser.getUpchargePriceGrid("1", priceVal,
										discountCode, "IMMD", false, "USD", imprintMethodName, "Set-up Charge",
										"Other", 1, existingPriceGrid,"");
							}
							break;
						case "Additional_Location_Charge":
							String additionalLocPriceValue = cell.getStringCellValue();
							List<AdditionalLocation> listOfAdditioLoc = getAdditionalLocation("Additional Location");
							String loctionCode = getDiscountCode(additionalLocPriceValue);
							additionalLocPriceValue = additionalLocPriceValue.replaceAll("\\(.*\\)", "").trim();
							existingPriceGrid = proGolfPriceGridParser.getUpchargePriceGrid("1", additionalLocPriceValue,
									loctionCode, "ADLN", false, "USD", "Additional Location",
									"Add. Location Charge", "Other", 1, existingPriceGrid,"");
							productConfigObj.setAdditionalLocations(listOfAdditioLoc);
							break;
						case "Additional_Color_Charge":
							String additionalColorPriceValue = cell.getStringCellValue();
							List<AdditionalColor> listOfAdditioColor = getAdditionalColor("Additional Color");
							String additionalColorCode = getDiscountCode(additionalColorPriceValue);
							additionalColorPriceValue = additionalColorPriceValue.replaceAll("\\(.*\\)", "").trim();
							existingPriceGrid = proGolfPriceGridParser.getUpchargePriceGrid("1", additionalColorPriceValue,
									additionalColorCode, "ADCL", false, "USD", "Additional Color",
									"Add. Color Charge", "Other", 1, existingPriceGrid,"");
							productConfigObj.setAdditionalColors(listOfAdditioColor);
							break;
						case "Rush_Charge":
							String rushPriceVal = cell.getStringCellValue();
							String rushDiscCode = getDiscountCode(rushPriceVal);
							RushTime rushTimeValue = getRushTime();
							rushPriceVal = rushPriceVal.replaceAll("\\(.*\\)", "").trim();
							existingPriceGrid = proGolfPriceGridParser.getUpchargePriceGrid("1", rushPriceVal,
									rushDiscCode, "RUSH", false, "USD", "Rush Service",
									"Rush Service Charge", "Other", 1, existingPriceGrid,"");
							productConfigObj.setRushTime(rushTimeValue);
							break;
						case "LTM_Charge":
							String lessThanValue = cell.getStringCellValue();
							String lessThanDiscountCode = getDiscountCode(lessThanValue);
							lessThanValue = lessThanValue.replaceAll("\\(.*\\)", "").trim();
							productExcelObj.setCanOrderLessThanMinimum(ApplicationConstants.CONST_BOOLEAN_TRUE);
							existingPriceGrid = proGolfPriceGridParser.getUpchargePriceGrid("1", lessThanValue,
									lessThanDiscountCode, "LMIN", false, "USD", "Can order less than minimum",
									"Less Than Minimum Charge", "Other", 1, existingPriceGrid,"");
							break;
						case "PMS_Charge":
							String pmsChargeVal = cell.getStringCellValue();
							String pmsDiscountCode = getDiscountCode(pmsChargeVal);
							pmsChargeVal = pmsChargeVal.replaceAll("\\(.*\\)", "").trim();
							existingPriceGrid = proGolfPriceGridParser.getUpchargePriceGrid("1", pmsChargeVal,
									pmsDiscountCode, "IMMD", false, "USD", imprintMethodName,
									"PMS Matching Charge", "Other", 1, existingPriceGrid,"");
							break;
						case "Qty_1_Min":
						case "Qty_2_Min":
						case "Qty_3_Min":
						case "Qty_4_Min":
							String qty = cell.getStringCellValue();
							listOfQuantityImprintLoc.add(qty);
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
					if(!"AAA Battery".equalsIgnoreCase(imprintLocUpchargeType)){
						existingPriceGrid = proGolfPriceGridParser.getUpchargePriceGrid(listOfQuantityImprintLoc.toString(),
								listOfPricesImprintLoc.toString(), listOfDiscountImprintLoc.toString(), "IMLO", false,
								"USD", imprintMethodName, imprintLocUpchargeType, "Other", 1, existingPriceGrid,"");
					}
				} catch (Exception e) {
					_LOGGER.error(
							"Error while Processing ProductId and cause :" + productExcelObj.getExternalProductId()
									+ " " + e.getMessage() + "at column number(increament by 1):" + columnIndex);
				}
			}

			productExcelObj.setProductConfigurations(productConfigObj);

			if (!StringUtils.isEmpty(productExcelObj.getExternalProductId())) {

			}
			repeatRows.clear();
			productConfigObj = new ProductConfigurations();
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
	private RushTime getRushTime(){
		RushTime rushTimeObj = new RushTime();
		rushTimeObj.setAvailable(ApplicationConstants.CONST_BOOLEAN_TRUE);
		List<RushTimeValue> listOfRushTimeValues = new ArrayList<>();
		RushTimeValue rushTimevaluesObj = new RushTimeValue();
		rushTimevaluesObj.setDetails("");
		rushTimevaluesObj.setBusinessDays("");
		listOfRushTimeValues.add(rushTimevaluesObj);
		rushTimeObj.setRushTimeValues(listOfRushTimeValues);
		return rushTimeObj;
	}
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
	public ProGolfPriceGridParser getProGolfPriceGridParser() {
		return proGolfPriceGridParser;
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
