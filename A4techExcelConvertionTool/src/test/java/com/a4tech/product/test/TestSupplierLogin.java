/*package com.a4tech.product.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.a4tech.product.dao.entity.SupplierSandboxLoginDetails;
@ContextConfiguration(locations ="classpath:application-config-test.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestSupplierLogin {
	private ProductDaoTest daoTest;
	
	@Test
	@Transactional
	@Rollback(true)
	public void addSandboxSupplierLogin(){
		SupplierSandboxLoginDetails sbloginDetails = new SupplierSandboxLoginDetails();
		sbloginDetails.setAsiNumber("12345");
		sbloginDetails.setUserName("admin");
		sbloginDetails.setPassword("password");
		Integer id = daoTest.addSupplierLogin(sbloginDetails);
		System.out.println("id value: "+id);
		long excepted = 1l;
		Assert.assertEquals(1l, excepted);
		
	}
	public ProductDaoTest getDaoTest() {
		return daoTest;
	}
	public void setDaoTest(ProductDaoTest daoTest) {
		this.daoTest = daoTest;
	}
}
*/