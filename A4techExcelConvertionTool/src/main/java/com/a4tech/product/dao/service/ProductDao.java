package com.a4tech.product.dao.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.a4tech.core.errors.ErrorMessage;
import com.a4tech.product.dao.entity.ErrorEntity;
import com.a4tech.product.dao.entity.ProductEntity;


public class ProductDao {
	
	private static Logger _LOGGER = Logger.getLogger(ProductDao.class);
	
	SessionFactory sessionFactory;
	public void save(List<ErrorMessage> errors ,String productNo ,Integer asiNumber){
		_LOGGER.info("Enter the DAO class");
		Set<ErrorEntity>  listErrorEntity = new HashSet<ErrorEntity>();
		Session session = null;
		Transaction tx  = null;
		ErrorEntity errorEntity = null;
		for (ErrorMessage errorMessage : errors) {
			  errorEntity = new ErrorEntity();
			  errorEntity.setError(errorMessage.getReason());
			  errorEntity.setProductNumber(productNo);
			  listErrorEntity.add(errorEntity);
		}
		ProductEntity productEntity = new ProductEntity();
		productEntity.setCompanyId(asiNumber);
		productEntity.setProductNo(productNo);
		productEntity.setProductStatus(false);
		productEntity.setErrors(listErrorEntity);
	try{
		 session = sessionFactory.openSession();
		 tx =  session.beginTransaction();
		 session.saveOrUpdate(productEntity);
		tx.commit();
	}catch(Exception ex){
		_LOGGER.info("Error in dao block");
	}finally{
		if(session !=null){
			try{
				session.close();
			}catch(Exception ex){
				_LOGGER.info("Error while close session object");
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
