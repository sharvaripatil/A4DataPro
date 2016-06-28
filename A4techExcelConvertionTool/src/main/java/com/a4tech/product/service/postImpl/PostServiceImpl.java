package com.a4tech.product.service.postImpl;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.core.model.ExternalAPIResponse;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Product;
import com.a4tech.product.service.LoginService;
import com.a4tech.product.service.PostService;
import com.a4tech.service.loginImpl.LoginServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PostServiceImpl implements PostService {

	private Logger _LOGGER = Logger.getLogger(getClass());
    private ProductDao productDao;
	private RestTemplate restTemplate;
	private String postApiURL ;
	@Autowired
	ObjectMapper mapper1;

	public int postProduct(String authTokens, Product product,Integer asiNumber) {

		try {

			//productDao.save(product);
			HttpHeaders headers = new HttpHeaders();
			headers.add("AuthToken", authTokens);
			// headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("Content-Type", "application/json ; charset=utf-8");
			//ObjectMapper mapper1 = new ObjectMapper();
			_LOGGER.info("Product Data : "
					+ mapper1.writeValueAsString(product));
			HttpEntity<Product> requestEntity = new HttpEntity<Product>(
					product, headers);
			ResponseEntity<ExternalAPIResponse> response = restTemplate
					.exchange(postApiURL, HttpMethod.POST, requestEntity,
							ExternalAPIResponse.class);
			_LOGGER.info("Result : " + response);
			return 1;
		}catch(HttpClientErrorException hce){
			String rsponse = hce.getResponseBodyAsString();
			try {
				ErrorMessageList apiResponse =  mapper1.readValue(rsponse, ErrorMessageList.class);
				_LOGGER.info("errors>>>>"+apiResponse);
				productDao.save(apiResponse.getErrors(),product.getExternalProductId(),asiNumber);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//productDao.errorResponse(rsponse);
			return 0;
		} catch (Exception hce) {
			_LOGGER.error("Exception while posting product to Radar API", hce);
			return 0;
		}
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	public String getPostApiURL() {
		return postApiURL;
	}

	public void setPostApiURL(String postApiURL) {
		this.postApiURL = postApiURL;
	}
	public ProductDao getProductDao() {
		return productDao;
	}

	public void setProductDao(ProductDao productDao) {
		this.productDao = productDao;
	}

	@Override
	public int postProduct(String authToken, Product product) {
		// TODO Auto-generated method stub
		return 0;
	}

}
