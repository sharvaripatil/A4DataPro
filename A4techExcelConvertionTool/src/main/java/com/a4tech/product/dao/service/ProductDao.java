package com.a4tech.product.dao.service;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.a4tech.product.model.Product;


public class ProductDao {
	
	private static Logger _LOGGER = Logger.getLogger(ProductDao.class);
	SessionFactory sessionFactory;
	public void save(Product product){
		
		_LOGGER.info("inside session");
		Session session = sessionFactory.getCurrentSession();
	}
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public void setSessionFactory(SessionFactory sessionFactory) {
		_LOGGER.info("inside sessionfactory");
		this.sessionFactory = sessionFactory;
		_LOGGER.info(sessionFactory);
	}
}
