package com.a4tech.product.service;

import org.apache.poi.ss.usermodel.Workbook;

public interface ProductService {
	
	public int excelProducts(String accessToken,Workbook workBook,int asiNumber);

}
