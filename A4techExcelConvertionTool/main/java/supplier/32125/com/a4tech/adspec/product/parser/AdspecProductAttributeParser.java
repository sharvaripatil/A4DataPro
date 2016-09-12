package com.a4tech.adspec.product.parser;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.Artwork;
import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.ImprintColor;
import com.a4tech.product.model.ImprintColorValue;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.util.ApplicationConstants;

public class AdspecProductAttributeParser {
     
	private LookupServiceData lookupServiceData;

	public List<ImprintMethod> getImprintMethods(String value){
		List<ImprintMethod> listOfImprintMethods = new ArrayList<ImprintMethod>();
		ImprintMethod imprintMethodObj =new ImprintMethod();
		if(lookupServiceData.isImprintMethod(value)){
			imprintMethodObj.setAlias(value);
			imprintMethodObj.setType(value);
		}else{
			imprintMethodObj.setAlias(value);
			imprintMethodObj.setType(ApplicationConstants.CONST_STRING_PRINTED);
		}
		listOfImprintMethods.add(imprintMethodObj);
		return listOfImprintMethods;
		
	}
	
	public List<Material> getProductMaterial(String value){
		List<Material> listOfImprintMethods = new ArrayList<Material>();
		Material materialObj =new Material();
		if(lookupServiceData.isMaterial(value)){
			materialObj.setName(value);
			materialObj.setAlias(value);
		}else{
			materialObj.setName(ApplicationConstants.CONST_VALUE_TYPE_OTHER);
			materialObj.setAlias(value);
		}
		listOfImprintMethods.add(materialObj);
		return listOfImprintMethods;
	}
	
	public List<ProductionTime> getProductionTime(String value){
		List<ProductionTime> listOfProTime = null;
		if(!StringUtils.isEmpty(value)){
			listOfProTime = new ArrayList<ProductionTime>();
			ProductionTime prodTimeObj = new ProductionTime();
			prodTimeObj.setBusinessDays(value);
			prodTimeObj.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
			listOfProTime.add(prodTimeObj);
		}
		return listOfProTime;
	}
	
	public ImprintColor getImprintColors(String value){
		ImprintColor imprintColorObj = null;
		if(!StringUtils.isEmpty(value)){
			 imprintColorObj = new ImprintColor();
			imprintColorObj.setType("COLR");
			List<ImprintColorValue> impcolorValuesList =new ArrayList<ImprintColorValue>();
			ImprintColorValue impclrObj=new ImprintColorValue();
			impclrObj.setName(value);
			impcolorValuesList.add(impclrObj);
			imprintColorObj.setValues(impcolorValuesList);
			return imprintColorObj;
		}
		return imprintColorObj;
	}
	
	public List<Catalog> getProductCatalog(String cataPageNum){
		List<Catalog> listOfCatalog = new ArrayList<Catalog>();
		Catalog catalogObj=new Catalog();
		catalogObj.setCatalogName("2016 Adspec Catalog");
		catalogObj.setCatalogPage(cataPageNum);
		listOfCatalog.add(catalogObj);
		return listOfCatalog;
	}
	
	public List<Artwork> getArtwork(String value){
		List<Artwork> listOfArtwork = null;
		if(!ApplicationConstants.CONST_STRING_ZERO.equals(value)){
			Artwork artworkObj = new Artwork();
			listOfArtwork = new ArrayList<Artwork>();
			artworkObj.setValue("ART SERVICES");
			artworkObj.setComments("template required");
			listOfArtwork.add(artworkObj);
			return listOfArtwork;
		}
		return listOfArtwork;
	}
	public LookupServiceData getLookupServiceData() {
		return lookupServiceData;
	}

	public void setLookupServiceData(LookupServiceData lookupServiceData) {
		this.lookupServiceData = lookupServiceData;
	}
}
