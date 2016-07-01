package com.a4tech.product.service;

import com.a4tech.product.model.Product;

public interface PostService {
	
	public int postProduct(String authToken,Product product,int asiNumber );
}