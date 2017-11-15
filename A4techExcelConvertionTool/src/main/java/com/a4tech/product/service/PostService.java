package com.a4tech.product.service;

import java.io.IOException;

import com.a4tech.product.model.Product;

public interface PostService {
	
	public int postProduct(String authToken,Product product,int asiNumber,int batchId, String environmentType) throws IOException;
	public Product getProduct(String authToken,String productId, String environmentType);
}