package com.a4tech.product.service.postImpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.a4tech.core.model.ExternalAPIResponse;
import com.a4tech.product.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.spi.inject.Inject;

public class ProductClient {
	
	private Logger _LOGGER = Logger.getLogger(getClass());
	RestTemplate newRestTemplate;
	private String postApiURL = "https://sandbox-productservice.asicentral.com/v3/product/";
	
	public ProductClient(){
		
	}
	public int postProduct(String authTokens, Product product) {

		try {
			
			HttpHeaders headers = new HttpHeaders();
			headers.add("AuthToken", authTokens);
			// headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("Content-Type", "application/json ; charset=utf-8");
			ObjectMapper mapper1 = new ObjectMapper();
			_LOGGER.info("Product Data : "
					+ mapper1.writeValueAsString(product));
			HttpEntity<Product> requestEntity = new HttpEntity<Product>(
					product, headers);
			ResponseEntity<ExternalAPIResponse> response = newRestTemplate.exchange(postApiURL, HttpMethod.POST, requestEntity,
							ExternalAPIResponse.class);
			_LOGGER.info("Result : " + response);
			return 1;
		} catch (Exception hce) {
			_LOGGER.error("Exception while posting product to Radar API", hce);
			return 0;
		}
	}
	public RestTemplate getNewRestTemplate() {
		return newRestTemplate;
	}
	public void setNewRestTemplate(RestTemplate newRestTemplate) {
		_LOGGER.info("inside setter");
		this.newRestTemplate = newRestTemplate;
		_LOGGER.info(newRestTemplate);
	}
}
