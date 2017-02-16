package com.a4tech.core.excelMapping;



import com.a4tech.ESPTemplate.product.mapping.ESPTemplateMapping;
import com.a4tech.RFGLine.product.mapping.RFGLineProductExcelMapping;
import com.a4tech.adspec.product.mapping.AdspecProductsExcelMapping;
import com.a4tech.apparel.product.mapping.ApparelProductsExcelMapping;
import com.a4tech.bambam.product.mapping.BambamProductExcelMapping;
import com.a4tech.bestDeal.product.mapping.BestDealProductsExcelMapping;
import com.a4tech.dc.product.mapping.DCProductsExcelMapping;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.kl.product.mapping.KlProductsExcelMapping;
import com.a4tech.product.bbi.mapping.BBIProductsExcelMapping;
import com.a4tech.product.broberry.mapping.BroberryExcelMapping;
import com.a4tech.product.kuku.mapping.KukuProductsExcelMapping;
import com.a4tech.product.newproducts.mapping.NewProductsExcelMapping;
import com.a4tech.product.riversend.mapping.RiversEndExcelMapping;
import com.a4tech.sage.product.mapping.SageProductsExcelMapping;
import com.a4tech.supplier.mapper.CutterBuckExcelMapping;
import com.a4tech.supplier.mapper.HighCaliberLineExcelMapping;

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
	private BroberryExcelMapping broberryExcelMapping;
	private BestDealProductsExcelMapping bdProdcutsMapping;
	private RiversEndExcelMapping riversEndExcelMapping;
	private BambamProductExcelMapping bamExcelMapping;
    private CutterBuckExcelMapping cbExcelMapping;
 //   private CutterBuckSheetParser cbSheetParser;
	private HighCaliberLineExcelMapping hcLineExcelMapping;
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
		   }else if(name.equalsIgnoreCase("broberry") || name.equals("42057")){
			   return broberryExcelMapping;  
		   }else if(name.equalsIgnoreCase("bestDeal") || name.equals("47791")){
			   return bdProdcutsMapping;
		   }else if(name.equalsIgnoreCase("riversend") || name.equals("82588")){
			   return riversEndExcelMapping;
		   }else if(name.equalsIgnoreCase("cbExcel") || name.equals("47965")){
			   return cbExcelMapping;
		   }
		 /*  else if(name.equalsIgnoreCase("sheetParser") || name.equals("47965")){
			   return cbSheetParser;
		   }*/
		   else if(name.equalsIgnoreCase("bambam") || name.equals("38228")){
			   return bamExcelMapping;
		   }else if(name.equalsIgnoreCase("highCaliberLine") || name.equals("43442")){
			   return hcLineExcelMapping;
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

	public BroberryExcelMapping getBroberryExcelMapping() {
		return broberryExcelMapping;
	}

	public void setBroberryExcelMapping(BroberryExcelMapping broberryExcelMapping) {
		this.broberryExcelMapping = broberryExcelMapping;
	}
	public BestDealProductsExcelMapping getBdProdcutsMapping() {
		return bdProdcutsMapping;
	}

	public void setBdProdcutsMapping(BestDealProductsExcelMapping bdProdcutsMapping) {
		this.bdProdcutsMapping = bdProdcutsMapping;
	}

	public RiversEndExcelMapping getRiversEndExcelMapping() {
		return riversEndExcelMapping;
	}

	public void setRiversEndExcelMapping(RiversEndExcelMapping riversEndExcelMapping) {
		this.riversEndExcelMapping = riversEndExcelMapping;
	}

	public BambamProductExcelMapping getBamExcelMapping() {
		return bamExcelMapping;
	}

	public void setBamExcelMapping(BambamProductExcelMapping bamExcelMapping) {
		this.bamExcelMapping = bamExcelMapping;
	}

	public CutterBuckExcelMapping getCbExcelMapping() {
		return cbExcelMapping;
	}

	public void setCbExcelMapping(CutterBuckExcelMapping cbExcelMapping) {
		this.cbExcelMapping = cbExcelMapping;
	}
	
	public HighCaliberLineExcelMapping getHcLineExcelMapping() {
		return hcLineExcelMapping;
	}

	public void setHcLineExcelMapping(HighCaliberLineExcelMapping hcLineExcelMapping) {
		this.hcLineExcelMapping = hcLineExcelMapping;
	}
/*	public CutterBuckSheetParser getCbSheetParser() {
		return cbSheetParser;
	}

	public void setCbSheetParser(CutterBuckSheetParser cbSheetParser) {
		this.cbSheetParser = cbSheetParser;
	}*/



}