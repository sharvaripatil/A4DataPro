package parser.adspec;

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
import com.a4tech.product.model.WarrantyInformation;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

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
		if(!StringUtils.isEmpty(value.trim())){
			listOfProTime = new ArrayList<ProductionTime>();
			ProductionTime prodTimeObj = new ProductionTime();
			String prdTime = getProductionTimeValue(value);
			prodTimeObj.setBusinessDays(prdTime);
			prodTimeObj.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
			listOfProTime.add(prodTimeObj);
		}
		return listOfProTime;
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
	
	public List<WarrantyInformation> getProductWarrantyInfo(String Warrantyvalue,
			                        List<WarrantyInformation> listOfWarranty,String typeOfValue,boolean isWarrantyAvailable){
		WarrantyInformation warrntyObj = null;
		if(ApplicationConstants.CONST_STRING_LIFE_TIME.equalsIgnoreCase(Warrantyvalue)){
			warrntyObj = new WarrantyInformation();
			warrntyObj.setComments(ApplicationConstants.CONST_STRING_LIFE_TIME);
			listOfWarranty.add(warrntyObj);
		}else if(CommonUtility.isBlank(Warrantyvalue) && 
				            ApplicationConstants.CONST_STRING_GRAPHIC.equals(typeOfValue)){
			warrntyObj = new WarrantyInformation();
			warrntyObj.setName("graphic warranty is 90 day for outdoor use and 1 year for indoor use");
			listOfWarranty.add(warrntyObj);
		}else if(ApplicationConstants.CONST_STRING_VALUE_ONE.equals(Warrantyvalue) &&
				              ApplicationConstants.CONST_STRING_GRAPHIC.equals(typeOfValue)){
			warrntyObj = new WarrantyInformation();
			warrntyObj.setName("graphic warranty is "+Warrantyvalue + " "+"Year");
			listOfWarranty.add(warrntyObj);
			
		}else{
			warrntyObj = new WarrantyInformation();
			warrntyObj.setName(ApplicationConstants.CONST_STRING_WARRANTY_LENGTH);
			warrntyObj.setComments(Warrantyvalue + " "+"Year(s)");
			listOfWarranty.add(warrntyObj);
		}
		if(!isWarrantyAvailable){
			warrntyObj = new WarrantyInformation();
			warrntyObj.setName(ApplicationConstants.CONST_STRING_WARRANTY_AVAILABLE);
			listOfWarranty.add(warrntyObj);
		}
		return listOfWarranty;	
	}
	
	
	public String getUpchargeNameForWarranty(List<WarrantyInformation> listOfWarrnty ){
		   for (WarrantyInformation warrantyInfo : listOfWarrnty) {
			     if(ApplicationConstants.CONST_STRING_WARRANTY_LENGTH.equals(warrantyInfo.getName())){
			    	 return ApplicationConstants.CONST_STRING_WARRANTY_LENGTH;
			     }else if(ApplicationConstants.CONST_STRING_LIFE_TIME.equals(warrantyInfo.getComments())){
			    	 return ApplicationConstants.CONST_STRING_LIFE_TIME;
			     }else{
			    	 
			     }	     
		}
		
		return "";
	}
	
	
	public String getProductionTimeValue(String value){
		String prdTime = value.split("[daysDays]")[0];
		return prdTime.trim();
	}
	public LookupServiceData getLookupServiceData() {
		return lookupServiceData;
	}

	public void setLookupServiceData(LookupServiceData lookupServiceData) {
		this.lookupServiceData = lookupServiceData;
	}
}
