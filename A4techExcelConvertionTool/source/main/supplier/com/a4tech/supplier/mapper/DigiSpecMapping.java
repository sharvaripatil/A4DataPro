package com.a4tech.supplier.mapper;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.a4tech.product.model.Product;
import com.a4tech.supplier.service.ISupplierParser;

import parser.digiSpec.DigiSpecDataMapping;
import parser.digiSpec.DigiSpecPricingMapping;

public class DigiSpecMapping implements ISupplierParser{
	private DigiSpecDataMapping     digiSpecDataMapping;
	private DigiSpecPricingMapping  digiSpecPricingMapping;
	
	private Logger _LOGGER = Logger.getLogger(DigiSpecMapping.class);
	
	@Override
	public String readExcel(String accessToken, Workbook workbook, Integer asiNumber, int batchId, String environmentType) {
		_LOGGER.info("mapping process start ProGolf supplier File");
		Map<String, Product> productsMap = new LinkedHashMap<>();
		String finalResult = "";
		for (Sheet sheet : workbook) {
			 String sheetName = sheet.getSheetName().trim();
			  _LOGGER.info("Sheet Name::"+sheetName);
			  if("Data NO Pricing".equalsIgnoreCase(sheetName)){
				  productsMap=  digiSpecDataMapping.readExcel(accessToken, sheet, productsMap, environmentType);
			  } else if("Data with Pricing".equalsIgnoreCase(sheetName)){
				finalResult = digiSpecPricingMapping.readExcel(productsMap, sheet, accessToken, asiNumber, batchId,
						environmentType);
			  } else{
				  _LOGGER.info("sheet is not processed: "+sheetName);
			  }
			
		}
		return finalResult;
	}

	public void setDigiSpecDataMapping(DigiSpecDataMapping digiSpecDataMapping) {
		this.digiSpecDataMapping = digiSpecDataMapping;
	}

	public void setDigiSpecPricingMapping(DigiSpecPricingMapping digiSpecPricingMapping) {
		this.digiSpecPricingMapping = digiSpecPricingMapping;
	}
}
