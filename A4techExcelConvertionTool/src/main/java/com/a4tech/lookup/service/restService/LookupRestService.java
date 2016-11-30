package com.a4tech.lookup.service.restService;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.a4tech.lookup.model.ImprintMethods;
import com.a4tech.lookup.model.Materials;
import com.a4tech.lookup.model.Origin;
import com.a4tech.lookup.model.Shapes;

public class LookupRestService {
	
   private static Logger _LOGGER = Logger.getLogger(LookupRestService.class);
   private RestTemplate restTemplate ;
   private String imprintMethodUrl;
   private String materialLookupUrl;
   private String shapesLookupUrl;
   private String originLookupUrl;
   
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

}
