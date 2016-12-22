/*package com.a4tech.product.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.a4tech.product.dao.entity.SupplierSandboxLoginDetails;
import com.a4tech.product.service.IProductDaoTest;
@ContextConfiguration(locations ="classpath:application-config-test.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestSupplierLogin {
	@Autowired
	private IProductDaoTest productDao;

	@Test
	public void addSandboxSupplierLogin(){
		System.out.println("enter test");
		Integer id = null;
		SupplierSandboxLoginDetails sbloginDetails = new SupplierSandboxLoginDetails();
		sbloginDetails.setAsiNumber("12345");
		sbloginDetails.setUserName("admin");
		sbloginDetails.setPassword("password");
	try{
		 id = productDao.addSupplierLogin(sbloginDetails);
	}catch(Exception e){
		System.out.println("exce:"+e.getMessage());
	}
		
		System.out.println("id value: "+id);
		long excepted = 1l;
		Assert.assertEquals(1l, excepted);
		
	}
	
	public IProductDaoTest getProductDao() {
		return productDao;
	}

	public void setProductDao(IProductDaoTest productDao) {
		this.productDao = productDao;
	}
	
}
*/