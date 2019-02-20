package com.a4tech.product.test;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.a4tech.product.dao.entity.SupplierProductColors;
import com.a4tech.product.dao.entity.SupplierLoginDetails;
import com.a4tech.product.dao.entity.SupplierProductColors;
import com.a4tech.product.test.service.IProductDaoTest;
@Component
public class ProductDaoTestImpl implements IProductDaoTest {

	private SessionFactory sessionFactory;

	@Override
	public Integer addSupplierLogin(SupplierLoginDetails loginData) {
		System.out.println("enter test dao class");
		Session session = null;
		Transaction transcation = null;
		try {
			session = sessionFactory.getCurrentSession();
			transcation = session.beginTransaction();
			session.save(loginData);
			transcation.commit();
			return loginData.getId();
		} catch (Exception exce) {
			if (transcation != null) {
				transcation.rollback();
				System.out.println("unable to add supplier details");
			}
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (Exception ex) {
					System.out.println("Error while close session object");
				}
			}
		}
		return null;
	}

	@Override
	public void getSupplierLoginDetails() {
		Session session = null;
		Transaction transaction = null;
		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
			Criteria criteria = session.createCriteria(SupplierLoginDetails.class);
			criteria.add(Restrictions.eq("asiNumber", "14"));
			SupplierLoginDetails data = (SupplierLoginDetails) criteria.uniqueResult();
			System.out.println("Data::" + data.getUserName());
		} catch (Exception exce) {
			System.out.println("unable to fetch supplier login details::" + exce.getMessage());
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (Exception exce) {
					System.out.println("unable to close session connection: " + exce.getMessage());
				}
			}
		}

	}

	@Override
	public void saveSupplierProductColors(SupplierProductColors colors) {
		Session session = null;
		Transaction transaction = null;
		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
			session.save(colors);
			transaction.commit();
		} catch (Exception e) {
			System.out.println("unable to save product colors::" + e.getMessage());
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (Exception exce) {
					System.out.println("unable to close session connection: " + exce.getMessage());
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierProductColors> getSupplierColorsById(Integer asiNumber) {
		Session session = null;
		Transaction transaction = null;
		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
			Criteria criteria = session.createCriteria(SupplierProductColors.class);
			criteria.add(Restrictions.eq("asiNumber", asiNumber));
			List<SupplierProductColors> shippingData = criteria.list();
			transaction.commit();
			return shippingData;
		} catch (Exception ex) {
			System.out.println("unable to get shipping order data from DB based on date: "+ex.getCause());
			if (transaction != null) {
				transaction.rollback();
			}
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (Exception ex) {
				}
			}
		}
		return new ArrayList<>();
	}


	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void saveSupplierColors(List<SupplierProductColors> colorsList) {
	Session session = null;
	Transaction tran = null;
	try {
		session = sessionFactory.openSession();
		tran = session.beginTransaction();
		for(int cnt=0; cnt<colorsList.size();cnt++) {
			session.save(colorsList.get(cnt));
			if( cnt % 50 == 0) {
				session.flush();
				session.clear();
			}
		}
		tran.commit();
	} catch (Exception e) {
	System.out.println(e.getMessage());
	}
		
	}

}
