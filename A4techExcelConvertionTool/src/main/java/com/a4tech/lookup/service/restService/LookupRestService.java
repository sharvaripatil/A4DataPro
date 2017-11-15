package com.a4tech.lookup.service.restService;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.a4tech.lookup.model.Catalog;
import com.a4tech.lookup.model.Catalogs;
import com.a4tech.lookup.model.Categories;
import com.a4tech.lookup.model.FobPoints;
import com.a4tech.lookup.model.ImprintMethods;
import com.a4tech.lookup.model.LineName;
import com.a4tech.lookup.model.Materials;
import com.a4tech.lookup.model.Origin;
import com.a4tech.lookup.model.Packages;
import com.a4tech.lookup.model.Shapes;
import com.a4tech.lookup.model.Themes;
import com.a4tech.lookup.model.TradeNames;

public class LookupRestService {
	
   private static Logger _LOGGER = Logger.getLogger(LookupRestService.class);
   private RestTemplate restTemplate ;
   private String imprintMethodUrl;
   private String materialLookupUrl;
   private String shapesLookupUrl;
   private String originLookupUrl;
   private String fobPointLookupUrl;
   private String lineNamesLookupUrl;
   private String tradeNameLookupUrl;
   private String categoriesLookupUrl;
   private String catalogLookupUrl;
   private String themeLookupUrl;
   private String packagesLookupUrl;
  /* @Autowired
   private Environment environemt;*/
	public List<String> getImprintMethodData(){
		 try{
			 HttpHeaders headers = new HttpHeaders();
			 headers.add("Content-Type", "application/json");
			 HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
			 ResponseEntity<ImprintMethods> response = restTemplate.exchange(imprintMethodUrl, HttpMethod.GET, 
					 																requestEntity, ImprintMethods.class);
			 ImprintMethods data = response.getBody();
			 return data.getImprintValues();
		 }catch(Exception exce){
			 _LOGGER.error("unable to get ImprintMethods lookup data: "+exce.getCause());
		 }
		return null;
	}
	
	public List<String> getMaterialsData(){
		 try{
			 HttpHeaders headers = new HttpHeaders();
			 headers.add("Content-Type", "application/json");
			 HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
			 ResponseEntity<Materials> response = restTemplate.exchange(materialLookupUrl, HttpMethod.GET, 
					                                                requestEntity, Materials.class);
			 Materials data = response.getBody();
			 return data.getMaterialValues();
		 }catch(Exception exce){
			 _LOGGER.error("unable to get Materials lookup data: "+exce.getCause());
		 }
		return null;
	}
	
	public List<String> getShapesData(){
		 try{
			 HttpHeaders headers = new HttpHeaders();
			 headers.add("Content-Type", "application/json");
			 HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
			 ResponseEntity<Shapes> response = restTemplate.exchange(shapesLookupUrl, HttpMethod.GET, 
					                                                requestEntity, Shapes.class);
			 Shapes data = response.getBody();
			 return data.getShapeValues();
		 }catch(Exception exce){
			 _LOGGER.error("unable to get shapes lookup data: "+exce.getCause());
		 }
		return null;
	}
	
	public List<String> getOrigins(){
		 try{
			 HttpHeaders headers = new HttpHeaders();
			 headers.add("Content-Type", "application/json");
			 HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
			 ResponseEntity<Origin> response = restTemplate.exchange(originLookupUrl, HttpMethod.GET, 
					                                                requestEntity, Origin.class);
			 Origin data = response.getBody();
			 return data.getListOfOrigins();
		 }catch(Exception exce){
			 _LOGGER.error("unable to get Origins lookup data: "+exce.getCause());
		 }
		return null;
	}
	public List<String> getFobPoints(String authToken,String environemtType){
		HttpHeaders headers = new HttpHeaders();
		try{
			headers.add("Accept", "application/json");
			headers.add("Content-Type", "application/json");
	        headers.add("AuthToken", authToken);
	        if(environemtType.equals("Sand")){
	        	fobPointLookupUrl = "https://sandbox-productservice.asicentral.com/api/v4/lookup/fobpoints";
	        } else{
	        	fobPointLookupUrl = "https://productservice.asicentral.com/api/v4/lookup/fobpoints";
	        }
	        HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
	       ResponseEntity<FobPoints> response = restTemplate.exchange(fobPointLookupUrl, HttpMethod.GET, 
	                                                                 requestEntity, FobPoints.class);
	       FobPoints data = response.getBody();
	      return data.getFobPoints();	
		}catch(Exception exce){
			_LOGGER.error("unable to get FOB Points lookup Data:"+exce.getMessage());
		}
		return null;
	}
	public List<String> getTradeNames(String tradeName){
		HttpHeaders headers = new HttpHeaders();
		try{
			headers.add("Accept", "application/json");
			headers.add("Content-Type", "application/json");
	        HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
	       ResponseEntity<TradeNames> response = restTemplate.exchange(tradeNameLookupUrl, HttpMethod.GET, 
	                                                                 requestEntity, TradeNames.class,tradeName);
	       TradeNames data = response.getBody();
	      return data.getTradeNames();
		}catch(Exception exce){
			_LOGGER.error("unable to get tradeName lookup Data:"+exce.getMessage());
		}
		return null;
	}
	public List<String> getLineNames(String authToken,String environemtType){
		HttpHeaders headers = new HttpHeaders();
		try{
			headers.add("Accept", "application/json");
			headers.add("Content-Type", "application/json");
	        headers.add("AuthToken", authToken);
	        if(environemtType.equals("Sand")){
	        	lineNamesLookupUrl = "https://sandbox-productservice.asicentral.com/api/v4/lookup/selectedlinenames";
	        } else {
	        	lineNamesLookupUrl = "https://productservice.asicentral.com/api/v4/lookup/selectedlinenames";
	        }
	        HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
	       ResponseEntity<LineName> response = restTemplate.exchange(lineNamesLookupUrl, HttpMethod.GET, 
	                                                                 requestEntity, LineName.class);
	       LineName data = response.getBody();
	      return data.getLineNames();
		}catch(Exception exce){
			_LOGGER.error("unable to get LineNames lookup Data:"+exce.getMessage());
		}
		return null;
	}
	public List<String> getCategories(){
		 try{
			 HttpHeaders headers = new HttpHeaders();
			 headers.add("Content-Type", "application/json");
			 HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
			 ResponseEntity<Categories> response = restTemplate.exchange(categoriesLookupUrl, HttpMethod.GET, 
					                                                requestEntity, Categories.class);
			 Categories data = response.getBody();
			 return data.getCategories();
		 }catch(Exception exce){
			 _LOGGER.error("unable to get Categories lookup data: "+exce.getCause());
		 }
		return null;
	}
	public List<Catalog> getCatalogs(String authToken,String environemtType){
		HttpHeaders headers = new HttpHeaders();
		try{
			if(environemtType.equals("Sand")){
				catalogLookupUrl = "https://sandbox-productservice.asicentral.com/api/v4/lookup/mediacitations";
			} else{
				catalogLookupUrl = "https://productservice.asicentral.com/api/v4/lookup/mediacitations";
			}
			headers.add("Accept", "application/json");
			headers.add("Content-Type", "application/json");
	        headers.add("AuthToken", authToken);
	        HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
	        ResponseEntity<Catalogs> response = restTemplate.exchange(catalogLookupUrl, HttpMethod.GET, 
                        requestEntity, Catalogs.class);
	       
	       Catalogs data = response.getBody();
	      return data.getListOfCatalog();
		}catch(Exception exce){
			_LOGGER.error("unable to get Supplier Catalog lookup Data:"+exce.getMessage());
		}
		return null;
	}
	
	public List<String> getTheme() {
		try{
			 HttpHeaders headers = new HttpHeaders();
			 headers.add("Content-Type", "application/json");
			 HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
			 ResponseEntity<Themes> response = restTemplate.exchange(themeLookupUrl, HttpMethod.GET, 
					                                                requestEntity, Themes.class);
			 Themes data = response.getBody();
			 return data.getThemes();
		 }catch(Exception exce){
			 _LOGGER.error("unable to get thems lookup data: "+exce.getCause());
		 }
		return null;
	}
	public List<String> getPackages(){
		 try{
			 HttpHeaders headers = new HttpHeaders();
			 headers.add("Content-Type", "application/json");
			 HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
			 ResponseEntity<Packages> response = restTemplate.exchange(packagesLookupUrl, HttpMethod.GET, 
					                                                requestEntity, Packages.class);
			 Packages data = response.getBody();
			 return data.getListOfPackages();
		 }catch(Exception exce){
			 _LOGGER.error("unable to get Packages lookup data: "+exce.getCause());
		 }
		 return new ArrayList<>();
	}
	
	public String getOriginLookupUrl() {
		return originLookupUrl;
	}

	public void setOriginLookupUrl(String originLookupUrl) {
		this.originLookupUrl = originLookupUrl;
	}

	public String getImprintMethodUrl() {
		return imprintMethodUrl;
	}

	public void setImprintMethodUrl(String imprintMethodUrl) {
		this.imprintMethodUrl = imprintMethodUrl;
	}
	public String getMaterialLookupUrl() {
		return materialLookupUrl;
	}

	public void setMaterialLookupUrl(String materialLookupUrl) {
		this.materialLookupUrl = materialLookupUrl;
	}

		public RestTemplate getRestTemplate() {
			return restTemplate;
		}

		public void setRestTemplate(RestTemplate restTemplate) {
			this.restTemplate = restTemplate;
		};
		public String getShapesLookupUrl() {
			return shapesLookupUrl;
		}

		public void setShapesLookupUrl(String shapesLookupUrl) {
			this.shapesLookupUrl = shapesLookupUrl;
		}
		 public String getFobPointLookupUrl() {
				return fobPointLookupUrl;
		}

		public void setFobPointLookupUrl(String fobPointLookupUrl) {
			this.fobPointLookupUrl = fobPointLookupUrl;
		}  
		public String getLineNamesLookupUrl() {
			return lineNamesLookupUrl;
		}
		public void setLineNamesLookupUrl(String lineNamesLookupUrl) {
			this.lineNamesLookupUrl = lineNamesLookupUrl;
		}
		public String getTradeNameLookupUrl() {
			return tradeNameLookupUrl;
		}

		public void setTradeNameLookupUrl(String tradeNameLookupUrl) {
			this.tradeNameLookupUrl = tradeNameLookupUrl;
		}
		public String getCategoriesLookupUrl() {
			return categoriesLookupUrl;
		}

		public void setCategoriesLookupUrl(String categoriesLookupUrl) {
			this.categoriesLookupUrl = categoriesLookupUrl;
		}
		public String getCatalogLookupUrl() {
			return catalogLookupUrl;
		}

		public void setCatalogLookupUrl(String catalogLookupUrl) {
			this.catalogLookupUrl = catalogLookupUrl;
		}

		public String getThemeLookupUrl() {
			return themeLookupUrl;
		}

		public void setThemeLookupUrl(String themeLookupUrl) {
			this.themeLookupUrl = themeLookupUrl;
		}
		public String getPackagesLookupUrl() {
			return packagesLookupUrl;
		}

		public void setPackagesLookupUrl(String packagesLookupUrl) {
			this.packagesLookupUrl = packagesLookupUrl;
		}
		
}
