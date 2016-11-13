package com.a4tech.core.excelMapping;

import com.a4tech.ESPTemplate.product.mapping.ESPTemplateMapping;
import com.a4tech.RFGLine.product.mapping.RFGLineProductExcelMapping;
import com.a4tech.adspec.product.mapping.AdspecProductsExcelMapping;
import com.a4tech.apparel.product.mapping.ApparelProductsExcelMapping;
import com.a4tech.dc.product.mapping.DCProductsExcelMapping;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.kl.product.mapping.KlProductsExcelMapping;
import com.a4tech.product.bbi.mapping.BBIProductsExcelMapping;
import com.a4tech.product.kuku.mapping.KukuProductsExcelMapping;
import com.a4tech.product.newproducts.mapping.NewProductsExcelMapping;
import com.a4tech.sage.product.mapping.SageProductsExcelMapping;

public class ExcelFactory {
	private AdspecProductsExcelMapping adspecMapping;
	private KlProductsExcelMapping klMapping;
	private SageProductsExcelMapping sageExcelMapping;
	private DCProductsExcelMapping dcProductExcelMapping;
	private KukuProductsExcelMapping kukuProductsExcelMapping;
	private RFGLineProductExcelMapping rfgLineProductExcelMapping;
	private BBIProductsExcelMapping bbiProductsExcelMapping;
	private NewProductsExcelMapping newProductsExcelMapping;
	private ApparelProductsExcelMapping apparealExcelMapping;
	private ESPTemplateMapping espTemplateMapping;

	public  IExcelParser getExcelParserObject(String name){
		   if(name.equalsIgnoreCase("Apparel") || name.equals("44620")){
			   return apparealExcelMapping;
		   }else if(name.equalsIgnoreCase("kl") || name.equals("64905")){
			   return klMapping;
		   }else if(name.equalsIgnoreCase("Adspec") || name.equals("32125")){
			   return adspecMapping;
		   }else if(name.equalsIgnoreCase("sage") || name.equals("55204")){
			   return sageExcelMapping;
		   }else if(name.equalsIgnoreCase("dc") || name.equals("55205")){
			   return dcProductExcelMapping;
		   }else if(name.equalsIgnoreCase("kuku") || name.equals("65851")){
			   return kukuProductsExcelMapping;
		   }else if(name.equalsIgnoreCase("rfg") || name.equals("82283")){
			   return rfgLineProductExcelMapping;
		   }else if(name.equalsIgnoreCase("bbi") || name.equals("40445")){
			   return bbiProductsExcelMapping;
		   }else if(name.equalsIgnoreCase("newProducts") || name.equals("91284")){
			   return newProductsExcelMapping;
		   }else if(name.equalsIgnoreCase("espTemplate") || name.equals("91561")){
			   return espTemplateMapping;
			   
		   }
		return null;
	}
	
	public ESPTemplateMapping getEspTemplateMapping() {
		return espTemplateMapping;
	}

	public void setEspTemplateMapping(ESPTemplateMapping espTemplateMapping) {
		this.espTemplateMapping = espTemplateMapping;
	}

	public AdspecProductsExcelMapping getAdspecMapping() {
		return adspecMapping;
	}
	public void setAdspecMapping(AdspecProductsExcelMapping adspecMapping) {
		this.adspecMapping = adspecMapping;
	}
	public KlProductsExcelMapping getKlMapping() {
		return klMapping;
	}
	public void setKlMapping(KlProductsExcelMapping klMapping) {
		this.klMapping = klMapping;
	}
	public SageProductsExcelMapping getSageExcelMapping() {
		return sageExcelMapping;
	}

	public void setSageExcelMapping(SageProductsExcelMapping sageExcelMapping) {
		this.sageExcelMapping = sageExcelMapping;
	}

	public DCProductsExcelMapping getDcProductExcelMapping() {
		return dcProductExcelMapping;
	}

	public void setDcProductExcelMapping(
			DCProductsExcelMapping dcProductExcelMapping) {
		this.dcProductExcelMapping = dcProductExcelMapping;
	}

	public KukuProductsExcelMapping getKukuProductsExcelMapping() {
		return kukuProductsExcelMapping;
	}

	public void setKukuProductsExcelMapping(
			KukuProductsExcelMapping kukuProductsExcelMapping) {
		this.kukuProductsExcelMapping = kukuProductsExcelMapping;
	}

	public RFGLineProductExcelMapping getRfgLineProductExcelMapping() {
		return rfgLineProductExcelMapping;
	}

	public void setRfgLineProductExcelMapping(
			RFGLineProductExcelMapping rfgLineProductExcelMapping) {
		this.rfgLineProductExcelMapping = rfgLineProductExcelMapping;
	}

	public BBIProductsExcelMapping getBbiProductsExcelMapping() {
		return bbiProductsExcelMapping;
	}

	public void setBbiProductsExcelMapping(
			BBIProductsExcelMapping bbiProductsExcelMapping) {
		this.bbiProductsExcelMapping = bbiProductsExcelMapping;
	}

	public NewProductsExcelMapping getNewProductsExcelMapping() {
		return newProductsExcelMapping;
	}

	public void setNewProductsExcelMapping(
			NewProductsExcelMapping newProductsExcelMapping) {
		this.newProductsExcelMapping = newProductsExcelMapping;
	}

	public ApparelProductsExcelMapping getApparealExcelMapping() {
		return apparealExcelMapping;
	}

	public void setApparealExcelMapping(
			ApparelProductsExcelMapping apparealExcelMapping) {
		this.apparealExcelMapping = apparealExcelMapping;
	}

}
