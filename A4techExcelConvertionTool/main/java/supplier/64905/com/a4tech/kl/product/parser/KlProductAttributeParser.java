package com.a4tech.kl.product.parser;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.Sample;
import com.a4tech.product.model.Samples;
import com.a4tech.util.ApplicationConstants;
import com.mysql.fabric.xmlrpc.base.Array;
import com.sun.jersey.core.impl.provider.entity.XMLJAXBElementProvider.App;

public class KlProductAttributeParser {
  
	public List<ImprintSize> getImprintSize(String imprintMethValue){
		List<ImprintSize> listOfImprintSize = null;
		if(!StringUtils.isEmpty(imprintMethValue)){
			listOfImprintSize = new ArrayList<ImprintSize>();
			ImprintSize imprSizeObj = new ImprintSize();
			imprSizeObj.setValue(imprintMethValue);
			listOfImprintSize.add(imprSizeObj);
		}
		
		return listOfImprintSize;
	}
	
	public Samples getSamples(String productSample,String specSample){
		Samples sample = new Samples();
		if(productSample.equalsIgnoreCase(ApplicationConstants.CONST_CHAR_Y)){
			sample.setProductSampleAvailable(ApplicationConstants.CONST_BOOLEAN_TRUE);
		}
		if(specSample.equalsIgnoreCase(ApplicationConstants.CONST_CHAR_Y)){
			sample.setSpecSampleAvailable(ApplicationConstants.CONST_BOOLEAN_TRUE);
		}
		sample.setProductSampleInfo(ApplicationConstants.CONST_STRING_EMPTY);
		sample.setSpecInfo(ApplicationConstants.CONST_STRING_EMPTY);
		return sample;
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
	
	public RushTime getRushTime(String rushAvailable,String rushTime){
		RushTime rushTimeObj = null;
		if(rushAvailable.equalsIgnoreCase(ApplicationConstants.CONST_CHAR_Y)){
			rushTimeObj = new RushTime();
			rushTimeObj.setAvailable(ApplicationConstants.CONST_BOOLEAN_TRUE);
			RushTimeValue rushTimeValueObj = new RushTimeValue();
			List<RushTimeValue> listOfrushTimeValue = new ArrayList<RushTimeValue>();
			rushTimeValueObj.setBusinessDays(rushTime);
			rushTimeValueObj.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
			listOfrushTimeValue.add(rushTimeValueObj);
			rushTimeObj.setRushTimeValues(listOfrushTimeValue);
		}
		return rushTimeObj;
		
	}
	
	public List<Packaging> getPackaging(String packValue){
		List<Packaging> listOfPackaging = null;
		if(!StringUtils.isEmpty(packValue)){
			listOfPackaging = new ArrayList<Packaging>();
			Packaging packObj = new Packaging();
			packObj.setName(packValue);
			listOfPackaging.add(packObj);
		}
		return listOfPackaging;
	}
	
	public List<Image> getProductImages(String data){
		List<Image> imgList = new ArrayList<Image>();
		Image imgObj = null;
	 if(!StringUtils.isEmpty(data)){
		   imgObj = new Image();
			imgObj.setImageURL(data);
			imgObj.setRank(ApplicationConstants.CONST_INT_VALUE_ONE);
			imgObj.setIsPrimary(true);
			imgList.add(imgObj);
	   }	
	 return imgList;
	}
}
