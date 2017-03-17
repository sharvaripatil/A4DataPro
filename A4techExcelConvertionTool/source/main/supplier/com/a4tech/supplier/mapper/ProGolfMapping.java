package com.a4tech.supplier.mapper;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Product;
import com.a4tech.product.service.postImpl.PostServiceImpl;

import parser.proGolf.ProGolfImprintChargesMapping;
import parser.proGolf.ProGolfImagesMapping;
import parser.proGolf.ProGolfProductInformationMapping;
import parser.proGolf.ProGolfPricingMapping;
import parser.proGolf.ProGolfShippingMapping;
import parser.proGolf.ProGolfVariationMapping;

public class ProGolfMapping implements IExcelParser{
	private ProGolfProductInformationMapping   prodInfoMapping;
	private ProGolfImprintChargesMapping       imprintChargesMapping;
	private ProGolfPricingMapping 		productPricingMapping;
	private ProGolfVariationMapping 	productVariationMapping;
	private ProGolfImagesMapping    	productImagesMapping;
	private ProGolfShippingMapping  	productShippingMapping;
	
	private Logger _LOGGER = Logger.getLogger(ProGolfMapping.class);
	
	@Override
	public String readExcel(String accessToken, Workbook workbook, Integer asiNumber, int batchId) {
		_LOGGER.info("mapping process start ProGolf supplier File");
		int processSheetNo =1;
		Map<String, Product> productsMap = new LinkedHashMap<>();
		for (Sheet sheet : workbook) {
			 String sheetName = sheet.getSheetName().trim();
			  _LOGGER.info("Sheet Name::"+sheetName);
			 /* if(sheetName.contains("mapping") || sheetName.contains("Mapping") 
					                        || sheetName.equalsIgnoreCase("Color Mapping")){
				  continue;
			  }
			  */
			  if("Product Information".equalsIgnoreCase(sheetName)){
				  productsMap=  prodInfoMapping.readMapper(accessToken,sheet,productsMap);
			  } else if("Product Pricing".equalsIgnoreCase(sheetName)){
				  
			  } else if("Imprint Charges".equalsIgnoreCase(sheetName)){
				  
			  } else if("Product Images".equalsIgnoreCase(sheetName)){
				  
			  } else if("Product Shipping".equalsIgnoreCase(sheetName)){
				  
			  } else if("Product Variation".equalsIgnoreCase(sheetName)){
				  
			  } else{
				  _LOGGER.info("sheet is not processed: "+sheetName);
			  }
			  processSheetNo++;
			
		}
		return "";
	}
	
	public ProGolfProductInformationMapping getProdInfoMapping() {
		return prodInfoMapping;
	}
	public void setProdInfoMapping(ProGolfProductInformationMapping prodInfoMapping) {
		this.prodInfoMapping = prodInfoMapping;
	}
	public ProGolfImprintChargesMapping getImprintChargesMapping() {
		return imprintChargesMapping;
	}
	public void setImprintChargesMapping(ProGolfImprintChargesMapping imprintChargesMapping) {
		this.imprintChargesMapping = imprintChargesMapping;
	}
	public ProGolfPricingMapping getProductPricingMapping() {
		return productPricingMapping;
	}
	public void setProductPricingMapping(ProGolfPricingMapping productPricingMapping) {
		this.productPricingMapping = productPricingMapping;
	}
	public ProGolfVariationMapping getProductVariationMapping() {
		return productVariationMapping;
	}
	public void setProductVariationMapping(ProGolfVariationMapping productVariationMapping) {
		this.productVariationMapping = productVariationMapping;
	}
	public ProGolfImagesMapping getProductImagesMapping() {
		return productImagesMapping;
	}
	public void setProductImagesMapping(ProGolfImagesMapping productImagesMapping) {
		this.productImagesMapping = productImagesMapping;
	}
	public ProGolfShippingMapping getProductShippingMapping() {
		return productShippingMapping;
	}
	public void setProductShippingMapping(ProGolfShippingMapping productShippingMapping) {
		this.productShippingMapping = productShippingMapping;
	}

}
