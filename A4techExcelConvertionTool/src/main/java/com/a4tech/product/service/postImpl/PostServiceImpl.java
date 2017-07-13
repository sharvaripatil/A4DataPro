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
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.a4tech.core.errors.ErrorMessage;
import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.core.model.ExternalAPIResponse;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Product;
import com.a4tech.product.service.PostService;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PostServiceImpl implements PostService {

	private Logger _LOGGER = Logger.getLogger(getClass());
	
    private ProductDao productDao;
	private RestTemplate restTemplate;
	private String postApiURL ;
	private String getProductUrl;
	@Autowired
	ObjectMapper mapperObj;
	
	public int postProduct(String authTokens, Product product,int asiNumber ,int batchId) throws IOException {

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("AuthToken", authTokens);
			headers.add("Content-Type", "application/json ; charset=utf-8");
			_LOGGER.info("Product Data : "
					+ mapperObj.writeValueAsString(product));
			HttpEntity<Product> requestEntity = new HttpEntity<Product>(
					product, headers);
			ResponseEntity<ExternalAPIResponse> response = restTemplate
					.exchange(postApiURL, HttpMethod.POST, requestEntity,
							ExternalAPIResponse.class);
			_LOGGER.info("Result : " + response);
			return 1;
		} catch (HttpClientErrorException hce) {
			String response = hce.getResponseBodyAsString();
			try {
				_LOGGER.info("ASI Error Response Msg :" + response);
				ErrorMessageList apiResponse = mapperObj.readValue(response,
						ErrorMessageList.class);
				productDao.save(apiResponse.getErrors(),
						product.getExternalProductId(), asiNumber, batchId);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				_LOGGER.error("unable to connect External API System:"
						+ e.getCause());
				return -1;
			}
			return 0;

		} catch (HttpServerErrorException serverEx) {
			String serverResponse = "";
			boolean flag=false;
			ErrorMessageList apiResponse = new ErrorMessageList();
			List<ErrorMessage> errorsList=new ArrayList<ErrorMessage>();
			ErrorMessage errorMessageObj=new ErrorMessage();
			if(!serverEx.getResponseBodyAsString().isEmpty()){
				serverResponse = serverEx.getResponseBodyAsString();
			}else{
				serverResponse=serverEx.getMessage();
				
				
				errorMessageObj.setMessage(serverResponse);
				errorMessageObj.setReason(serverResponse);
				errorsList.add(errorMessageObj);
				apiResponse.setErrors(errorsList);
				flag=true;
			}
			
			try {
				if(!flag){
				apiResponse = mapperObj.readValue(serverResponse,
						ErrorMessageList.class);
				}
				
				productDao.save(apiResponse.getErrors(),
						product.getExternalProductId(), asiNumber, batchId);
				_LOGGER.info("Error JSON:" + apiResponse);

			} catch (JsonParseException | JsonMappingException e) {
				_LOGGER.error("Error while reading ErrorMessageList object to JSON:"
						+ e.getCause());
			}
			return 0;

		} catch (Exception hce) {
			_LOGGER.error("Exception while posting product to ExternalAPI", hce);
			String serverErrorMsg = hce.getMessage();
			if (serverErrorMsg != null
					&& serverErrorMsg
							.equalsIgnoreCase("500 Internal Server Error")) {
				_LOGGER.info("internal server msg received from ExternalAPI ");
				ErrorMessageList errorMsgList = CommonUtility
						.responseconvertErrorMessageList(serverErrorMsg);
				productDao.save(errorMsgList.getErrors(),
						product.getExternalProductId(), asiNumber, batchId);
				return 0;
			} else if (hce.getCause() != null) {
				String errorMsg = hce.getCause().toString();
				if (errorMsg.contains("java.net.UnknownHostException")
						|| errorMsg.contains("java.net.NoRouteToHostException")
						|| errorMsg.contains("java.net.SocketTimeoutException")) {
					ErrorMessageList errorMsgList = CommonUtility
							.responseconvertErrorMessageList(errorMsg);
					productDao.save(errorMsgList.getErrors(),
							product.getExternalProductId(), asiNumber, batchId);
					return 0;
				}
			} else {

			}
			return -1;
		}

	}
	
	public Product getProduct(String authToken,String productId){
	try{
		 HttpHeaders headers = new HttpHeaders();
		 headers.add("AuthToken", authToken);
		 headers.add("Content-Type", "application/json");
		 HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
	     ResponseEntity<Product> getResponse  =	restTemplate.exchange(getProductUrl, HttpMethod.GET, requestEntity, 
	    		                                                                            Product.class ,productId);
	     Product product = getResponse.getBody();
	    _LOGGER.info("Product from API::"
				+ mapperObj.writeValueAsString(product));
	    return product;  
	  }catch(HttpClientErrorException hce){
		_LOGGER.error("HttpClientError ::"+hce.getMessage());
	  }catch(Exception e){
		_LOGGER.error("Exception ::"+e.getMessage());
	 }
	return null;
  }
	
	public int deleteProduct(String authTokens, String productId,int asiNumber ,int batchId) throws IOException {

		try {String deleteProductUrl="https://sandbox-productservice.asicentral.com/api/v4/product/";
			//productId="3558-55093AWDD";
			 HttpHeaders headers = new HttpHeaders();
			 headers.add("Accept",  "application/json");
			 headers.add("Content-Type", "application/json");
			 headers.add("AuthToken", authTokens);
			
			 HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		     ResponseEntity<ErrorMessageList> getResponse  =	restTemplate.exchange(deleteProductUrl+productId, HttpMethod.DELETE, requestEntity, 
		    		 ErrorMessageList.class);
				_LOGGER.info("Result : " + getResponse);
		    _LOGGER.info("Delete Response from ASI::"
					+ mapperObj.writeValueAsString(getResponse));
		     return 1; 
		  } catch (HttpClientErrorException hce) {
			String response = hce.getResponseBodyAsString();
			try {
				_LOGGER.info("ASI Error Response Msg :" + response);
				ErrorMessageList apiResponse = mapperObj.readValue(response,
						ErrorMessageList.class);
				productDao.save(apiResponse.getErrors(),
						productId, asiNumber, batchId);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				_LOGGER.error("unable to connect External API System:"
						+ e.getCause());
				return -1;
			}
			return 0;

		} catch (HttpServerErrorException serverEx) {
			String serverResponse = serverEx.getResponseBodyAsString();

			ErrorMessageList apiResponse;
			try {
				apiResponse = mapperObj.readValue(serverResponse,
						ErrorMessageList.class);
				productDao.save(apiResponse.getErrors(),
						productId, asiNumber, batchId);
				_LOGGER.info("Error JSON:" + apiResponse);

			} catch (JsonParseException | JsonMappingException e) {
				_LOGGER.error("Error while reading ErrorMessageList object to JSON:"
						+ e.getCause());
			}
			return 0;

		} catch (Exception hce) {
			_LOGGER.error("Exception while posting product to ExternalAPI", hce);
			String serverErrorMsg = hce.getMessage();
			if (serverErrorMsg != null
					&& serverErrorMsg
							.equalsIgnoreCase("500 Internal Server Error")) {
				_LOGGER.info("internal server msg received from ExternalAPI ");
				ErrorMessageList errorMsgList = CommonUtility
						.responseconvertErrorMessageList(serverErrorMsg);
				productDao.save(errorMsgList.getErrors(),
						productId, asiNumber, batchId);
				return 0;
			} else if (hce.getCause() != null) {
				String errorMsg = hce.getCause().toString();
				if (errorMsg.contains("java.net.UnknownHostException")
						|| errorMsg.contains("java.net.NoRouteToHostException")
						|| errorMsg.contains("java.net.SocketTimeoutException")) {
					ErrorMessageList errorMsgList = CommonUtility
							.responseconvertErrorMessageList(errorMsg);
					productDao.save(errorMsgList.getErrors(),
							productId, asiNumber, batchId);
					return 0;
				}
			} else {

			}
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
	public String getGetProductUrl() {
		return getProductUrl;
	}

	public void setGetProductUrl(String getProductUrl) {
		this.getProductUrl = getProductUrl;
	}
}
