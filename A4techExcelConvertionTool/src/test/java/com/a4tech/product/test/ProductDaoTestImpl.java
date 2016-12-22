package com.a4tech.product.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.a4tech.product.dao.entity.SupplierLoginDetails;
import com.a4tech.product.service.IProductDaoTest;

public class ProductDaoTestImpl  implements IProductDaoTest{
   
	  private SessionFactory sessionFactory;
      @Override
	  public Integer addSupplierLogin(SupplierLoginDetails loginData){
		  System.out.println("enter test dao class");
		  Session session = null;
		  Transaction transcation = null;
		  try{
			  session = sessionFactory.getCurrentSession();
			  transcation = session.beginTransaction();
			  session.save(loginData);
			  transcation.commit();
              return loginData.getId();
		  }catch(Exception exce){
			  if(transcation != null){
				  transcation.rollback();
				  System.out.println("unable to add supplier details");
			  }
		  }finally{
			  if(session !=null){
					try{
						session.close();
					}catch(Exception ex){
						System.out.println("Error while close session object");
					}
			  }
		  }
		  return  null;
	  }
	  public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
