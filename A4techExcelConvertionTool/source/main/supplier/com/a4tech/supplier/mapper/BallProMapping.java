package com.a4tech.supplier.mapper;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.model.Product;

import parser.ballPro.BallProImagesMapping;
import parser.ballPro.BallProImprintChargesMapping;
import parser.ballPro.BallProPricingMapping;
import parser.ballPro.BallProProductInformationMapping;
import parser.ballPro.BallProShippingMapping;
import parser.proGolf.ProGolfImagesMapping;
import parser.proGolf.ProGolfImprintChargesMapping;
import parser.proGolf.ProGolfPricingMapping;
import parser.proGolf.ProGolfProductInformationMapping;
import parser.proGolf.ProGolfShippingMapping;
import parser.proGolf.ProGolfVariationMapping;

public class BallProMapping implements IExcelParser{
	private BallProProductInformationMapping   ballProProductInfoMapping;
	private BallProImprintChargesMapping       ballProImprintChargesMapping;
	private BallProPricingMapping 			   ballProProductPricingMapping;
	private BallProImagesMapping    		   ballProProductImagesMapping;
	private BallProShippingMapping  		   ballProProductShippingMapping;
	
	private Logger _LOGGER = Logger.getLogger(BallProMapping.class);
	
	@Override
	public String readExcel(String accessToken, Workbook workbook, Integer asiNumber, int batchId, String environmentType) {
		_LOGGER.info("mapping process start ProGolf supplier File");
		Map<String, Product> productsMap = new LinkedHashMap<>();
		String finalResult = "";
		for (Sheet sheet : workbook) {
			 String sheetName = sheet.getSheetName().trim();
			  _LOGGER.info("Sheet Name::"+sheetName);
			  if("Product Information".equalsIgnoreCase(sheetName)){
				  productsMap=  ballProProductInfoMapping.readMapper(accessToken,sheet,productsMap);
			  } else if("Product Pricing".equalsIgnoreCase(sheetName)){
				 productsMap = ballProProductPricingMapping.readMapper(productsMap, sheet);
			  } else if("Imprint Charges".equalsIgnoreCase(sheetName)){
				 productsMap = ballProImprintChargesMapping.readMapper(productsMap, sheet);
			  } else if("Product Images".equalsIgnoreCase(sheetName)){
				 // no need to mapping for images since supplier images does not meet asi standrd
			  } else if("Product Shipping".equalsIgnoreCase(sheetName)){
				  finalResult = ballProProductShippingMapping.readMapper(productsMap, sheet, accessToken, asiNumber, batchId,environmentType);
			  } else if("Product Variation".equalsIgnoreCase(sheetName)){
				  //productsMap = productVariationMapping.readMapper(productsMap, sheet);
				  // only required while uploading "Callaway 2017 website export - mapped file only"
			  } else{
				  _LOGGER.info("sheet is not processed: "+sheetName);
			  }
			
		}
		return finalResult;
	}
	
	public BallProProductInformationMapping getBallProProductInfoMapping() {
		return ballProProductInfoMapping;
	}

	public void setBallProProductInfoMapping(BallProProductInformationMapping ballProProductInfoMapping) {
		this.ballProProductInfoMapping = ballProProductInfoMapping;
	}

	public BallProImprintChargesMapping getBallProImprintChargesMapping() {
		return ballProImprintChargesMapping;
	}

	public void setBallProImprintChargesMapping(BallProImprintChargesMapping ballProImprintChargesMapping) {
		this.ballProImprintChargesMapping = ballProImprintChargesMapping;
	}

	public BallProPricingMapping getBallProProductPricingMapping() {
		return ballProProductPricingMapping;
	}

	public void setBallProProductPricingMapping(BallProPricingMapping ballProProductPricingMapping) {
		this.ballProProductPricingMapping = ballProProductPricingMapping;
	}

	public BallProImagesMapping getBallProProductImagesMapping() {
		return ballProProductImagesMapping;
	}

	public void setBallProProductImagesMapping(BallProImagesMapping ballProProductImagesMapping) {
		this.ballProProductImagesMapping = ballProProductImagesMapping;
	}

	public BallProShippingMapping getBallProProductShippingMapping() {
		return ballProProductShippingMapping;
	}

	public void setBallProProductShippingMapping(BallProShippingMapping ballProProductShippingMapping) {
		this.ballProProductShippingMapping = ballProProductShippingMapping;
	}

}
