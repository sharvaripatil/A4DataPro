package com.a4tech.supplier.mapper;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.model.Product;

import parser.evansManufacturing.EveanManufacturePricingMapping;
import parser.evansManufacturing.EveanManufactureProductInformationMapping;
import parser.evansManufacturing.EveanManufactureVariationMapping;
/*
 * EXCIT-700
 */
public class EveanManufacturingCanadaMapping implements IExcelParser{
	
	private EveanManufactureProductInformationMapping eveanProductInfoMapping;
	private EveanManufactureVariationMapping          eveanVariationMapping;
	private EveanManufacturePricingMapping            eveanPriceMapping;
	
	private Logger _LOGGER = Logger.getLogger(EveanManufacturingCanadaMapping.class);
	
	@Override
	public String readExcel(String accessToken, Workbook workbook, Integer asiNumber, int batchId, String environmentType) {
		_LOGGER.info("mapping process start Evans Manufacturing Canada supplier File");
		Map<String, Product> productsMap = new LinkedHashMap<>();
		String finalResult = "";
		for (Sheet sheet : workbook) {
			 String sheetName = sheet.getSheetName().trim();
			  _LOGGER.info("Sheet Name::"+sheetName);
			  if("Products Information".equalsIgnoreCase(sheetName)){
				  productsMap=  eveanProductInfoMapping.readMapper(accessToken,sheet,productsMap,environmentType);
			  } else if("Variations".equalsIgnoreCase(sheetName)){
				 productsMap = eveanVariationMapping.readMapper(productsMap, sheet);
			  } else if("Product Pricing".equalsIgnoreCase(sheetName)){
				productsMap = eveanPriceMapping.readMapper(productsMap, sheet, accessToken, asiNumber, batchId,
						environmentType); 
			  } else{
				  _LOGGER.info("sheet is not processed: "+sheetName);
			  }
		}
		
		return finalResult;
	}
	public void setEveanProductInfoMapping(EveanManufactureProductInformationMapping eveanProductInfoMapping) {
		this.eveanProductInfoMapping = eveanProductInfoMapping;
	}

	public void setEveanVariationMapping(EveanManufactureVariationMapping eveanVariationMapping) {
		this.eveanVariationMapping = eveanVariationMapping;
	}

	public void setEveanPriceMapping(EveanManufacturePricingMapping eveanPriceMapping) {
		this.eveanPriceMapping = eveanPriceMapping;
	}


}
