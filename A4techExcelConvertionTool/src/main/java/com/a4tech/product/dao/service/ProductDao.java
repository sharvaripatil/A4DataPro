package com.a4tech.product.dao.service;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.a4tech.product.model.Product;


public class ProductDao {
	
	private static Logger _LOGGER = Logger.getLogger(ProductDao.class);
	SessionFactory sessionFactory;
	public void save(Product product){
		Session session = sessionFactory.getCurrentSession();
	}
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
