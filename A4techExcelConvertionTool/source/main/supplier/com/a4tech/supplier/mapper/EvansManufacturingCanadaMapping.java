package com.a4tech.supplier.mapper;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.a4tech.product.model.Product;
import com.a4tech.supplier.service.ISupplierParser;

public class EvansManufacturingCanadaMapping implements ISupplierParser{
	
	private static final Logger _LOGGER = Logger.getLogger(EvansManufacturingCanadaMapping.class);

	@Override
	public String readExcel(String accessToken, Workbook workbook, Integer asiNumber, int batchId,
			String environmentType) {
		_LOGGER.info("mapping process start ProGolf supplier File");
		Map<String, Product> productsMap = new LinkedHashMap<>();
		String finalResult = "";
		for (Sheet sheet : workbook) {
			 String sheetName = sheet.getSheetName().trim();
			  _LOGGER.info("Sheet Name::"+sheetName);
			  if("Products Information".equalsIgnoreCase(sheetName)){
				  
			  } else if("Variations".equalsIgnoreCase(sheetName)){
				  
			  } else if("Product Pricing".equalsIgnoreCase(sheetName)){
				  
			  }
		}
		return finalResult;
	}
	

	
}
