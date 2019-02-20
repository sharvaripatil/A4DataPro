package com.a4tech.product.test.service;

import java.util.List;

import com.a4tech.product.dao.entity.SupplierProductColors;
import com.a4tech.product.dao.entity.SupplierLoginDetails;
import com.a4tech.product.dao.entity.SupplierProductColors;

public interface IProductDaoTest {
	public Integer addSupplierLogin(SupplierLoginDetails loginData);
	public void  getSupplierLoginDetails();
	public void saveSupplierProductColors(SupplierProductColors colors);
	public List<SupplierProductColors> getSupplierColorsById(Integer asiNumber);
	public void saveSupplierColors(List<SupplierProductColors> colorsList);
}
