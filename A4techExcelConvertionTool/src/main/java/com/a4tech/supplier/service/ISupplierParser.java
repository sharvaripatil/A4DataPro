package com.a4tech.supplier.service;

import org.apache.poi.ss.usermodel.Workbook;

public interface ISupplierParser {
	
       public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType);
}
