package com.a4tech.v2.service.postImpl;



import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.a4tech.core.model.ExternalAPIResponse;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.service.PostService;
import com.a4tech.v2.core.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PostServiceImpl implements PostService{
		  
	    private Logger              _LOGGER              = Logger.getLogger(getClass());
	   
	    @Autowired
	    private ProductDao productDao;
	    @Autowired
		private RestTemplate restTemplate ;//= new RestTemplate();
	    @Autowired
		ObjectMapper objectMapper;
		private String postApiURL ;//= "https://sandbox-productservice.asicentral.com/v2/product/";
		
		public int postProduct(String authTokens, Product product,int asiNumber) {
			
			//ObjectMapper mapper1 = new ObjectMapper();
			try {
	            
	        	HttpHeaders headers = new HttpHeaders();
	        	headers.add("AuthToken", authTokens);
	        	//headers.setContentType(MediaType.APPLICATION_JSON);
	        	headers .add("Content-Type", "application/json ; charset=utf-8");
	            
	           _LOGGER.info("Product Data : " + objectMapper.writeValueAsString(product));
	            HttpEntity<Product> requestEntity = new HttpEntity<Product>(product, headers);
	            ResponseEntity<ExternalAPIResponse> response = restTemplate.exchange(postApiURL, HttpMethod.POST, requestEntity, ExternalAPIResponse.class);
	            _LOGGER.info("Result : " + response);
	            return 1;
	        } catch(HttpClientErrorException hce){
				String rsponse = hce.getResponseBodyAsString();
				try {
					ExternalAPIResponse apiResponse =  objectMapper.readValue(rsponse, ExternalAPIResponse.class);
					_LOGGER.info("errors>>>>"+apiResponse);
					productDao.save(apiResponse,product.getExternalProductId(),asiNumber);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					_LOGGER.error("unable to connect External API System:"+e.getCause());
					return 0;
				}
				
				return 0;
			} catch (Exception hce) {
				_LOGGER.error("Exception while posting product to Radar API", hce);
				return 0;
			}
	    }


		public ProductDao getProductDao() {
			return productDao;
		}


		public void setProductDao(ProductDao productDao) {
			this.productDao = productDao;
		}


		public RestTemplate getRestTemplate() {
			return restTemplate;
		}


		public void setRestTemplate(RestTemplate restTemplate) {
			this.restTemplate = restTemplate;
		}


		public ObjectMapper getObjectMapper() {
			return objectMapper;
		}


		public void setObjectMapper(ObjectMapper objectMapper) {
			this.objectMapper = objectMapper;
		}


		public String getPostApiURL() {
			return postApiURL;
		}


		public void setPostApiURL(String postApiURL) {
			this.postApiURL = postApiURL;
		}


		@Override
		public int postProduct(String authToken,
				com.a4tech.product.model.Product product, int asiNumber) {
			// TODO Auto-generated method stub
			return 0;
		}


	}
