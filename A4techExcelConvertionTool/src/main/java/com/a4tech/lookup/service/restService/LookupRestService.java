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

public class LookupRestService {
	
   private static Logger _LOGGER = Logger.getLogger(LookupRestService.class);
   private RestTemplate restTemplate ;
   private String imprintMethodUrl;
   private String materialLookupUrl;

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
			 _LOGGER.error("unable to get ImprintMethods from API"+exce.getCause());
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
			 _LOGGER.error("unable to get Materials from API"+exce.getCause());
		 }
		return null;
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
		

}
