package com.a4tech.product.service.imple;

import org.apache.poi.ss.usermodel.Workbook;



import com.a4tech.product.service.ProductService;
import com.a4tech.v2.core.excelMapping.ExcelMapping;

public class ProductServiceImpl implements ProductService{

	public ExcelMapping excelMapping  = new ExcelMapping();
	public int excelProducts(String AccessToken,Workbook workBook ) {
		// TODO Auto-generated method stub
		return excelMapping.readExcel(AccessToken,workBook);
	}
}
