package com.a4tech.product.service;

import java.io.IOException;
import java.util.List;

import com.a4tech.product.dao.entity.SupplierProductColors;
import com.a4tech.product.model.Product;

public interface ProductService {
	
	public int postProduct(String authToken,Product product,int asiNumber,int batchId, String environmentType) throws IOException;
	public Product getProduct(String authToken,String productId, String environmentType);
	public List<SupplierProductColors> getSupplierColorsByAsiNumber(Integer asiNumber);
}