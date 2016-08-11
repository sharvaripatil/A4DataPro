package com.a4tech.product.service.postImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.a4tech.core.errors.ErrorMessage;
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
	
	public int postProduct(String authTokens, Product product,int asiNumber ,int batchId) {

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
				productDao.save(apiResponse.getErrors(),product.getExternalProductId(),asiNumber,batchId);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return -1;
			}
			//productDao.errorResponse(rsponse);
			return 0;
		} catch (Exception hce) {
			_LOGGER.error("Exception while posting product to ExternalAPI", hce);
			String serverErrorMsg = hce.getMessage();
			if(serverErrorMsg != null && serverErrorMsg.equalsIgnoreCase("500 Internal Server Error")){
				_LOGGER.info("internal server msg received from ExternalAPI ");
				productDao.responseconvertErrorMessage(serverErrorMsg, product.getExternalProductId(), asiNumber, batchId);
				return 0;
			}else if(hce.getCause() != null){
				String errorMsg = hce.getCause().toString();
				if (errorMsg.contains("java.net.UnknownHostException")
						|| errorMsg.contains("java.net.NoRouteToHostException")){
					productDao.responseconvertErrorMessage(errorMsg, product.getExternalProductId(), asiNumber, batchId);
					return 0;
				}else if(errorMsg.contains("java.net.SocketTimeoutException")){
					productDao.responseconvertErrorMessage(errorMsg, product.getExternalProductId(), asiNumber, batchId);
					return 0;
				}
			}else{
				
			}
			
			/*System.out.println(hce.getCause());
			if (hce.getCause() != null) {
				String errorMsg = hce.getCause().toString();
				if (errorMsg.contains("java.net.UnknownHostException")
						|| errorMsg.contains("java.net.NoRouteToHostException")) {
					ErrorMessageList responseList = new ErrorMessageList();
					List<ErrorMessage> errorList = new ArrayList<ErrorMessage>();
					ErrorMessage errorMsgObj = new ErrorMessage();
					errorMsgObj.setMessage(errorMsg);
					errorList.add(errorMsgObj);
					errorMsgObj
							.setReason("Product is unable to process due to Internet Down");
					responseList.setErrors(errorList);
					productDao.save(responseList.getErrors(),
							product.getExternalProductId(), asiNumber, batchId);
				}
				return 0;
			}*/
			//System.out.println();
			return -1;
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
	public int postProduct(String authToken, Product product, int asiNumber) {
		// TODO Auto-generated method stub
		return 0;
	}


	


}
