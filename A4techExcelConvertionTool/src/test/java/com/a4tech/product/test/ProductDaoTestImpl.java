package com.a4tech.product.test;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

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
      @Override
    public void getSupplierLoginDetails() {
    	  Session session = null;
		  Transaction transaction = null;
		  try{
			  session = sessionFactory.openSession();
			  transaction = session.beginTransaction();
			  Criteria criteria = session.createCriteria(SupplierLoginDetails.class);
	            criteria.add(Restrictions.eq("asiNumber", "14"));
	            SupplierLoginDetails data =  (SupplierLoginDetails) criteria.uniqueResult();
	            System.out.println("Data::"+data.getUserName());
		  }catch(Exception exce){
			 System.out.println("unable to fetch supplier login details::"+exce.getMessage());
		  }finally{
			  if(session != null){
				  try{
					  session.close();
				  }catch(Exception exce){
					  System.out.println("unable to close session connection: "+exce.getMessage());
				  }
			  }
		  }
		  
	}
	  public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
