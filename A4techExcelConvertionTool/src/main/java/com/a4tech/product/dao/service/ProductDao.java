package com.a4tech.product.dao.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.a4tech.core.errors.ErrorMessage;
import com.a4tech.core.model.ExternalAPIResponse;
import com.a4tech.product.dao.entity.ErrorEntity;
import com.a4tech.product.dao.entity.ProductEntity;


public class ProductDao {
	
	private static Logger _LOGGER = Logger.getLogger(ProductDao.class);
	
	SessionFactory sessionFactory;
	String errorFileLocPath;
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
		
		//Criteria crit = session.createCriteria(ProductEntity.class);
		HashMap<String, ArrayList<String>> hashmap = new HashMap<String, ArrayList<String>>();
		Criteria prdCrit = session.createCriteria(ProductEntity.class);
		prdCrit.add(Restrictions.eq("companyId",asiNumber));
		//List results = prdCrit.list();
		  List<ProductEntity> list = prdCrit.list();
	        for(ProductEntity arr : list){
	        	
	            System.out.println();
	            
	            ArrayList<String> arraylist = new ArrayList<String>();
	            //hashmap.put(productEntity2.getProductNumber(), value)
	            for (ErrorEntity productEntity2 : arr.getErrors()) {
	            	arraylist.add(productEntity2.getError());
					System.out.println(productEntity2.getError());
				}
	            hashmap.put(arr.getProductNo(), arraylist);
	        }
	        String errorComp=Integer.toString(asiNumber);
	        File fout = new File("D:\\A4 ESPUpdate\\ErrorFiles\\"+errorComp+".txt");
	    	FileOutputStream fos = new FileOutputStream(fout);
	     
	    	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
	     
	    	/*for (int i = 0; i < 10; i++) {
	    		bw.write("something");
	    		bw.newLine();
	    	}*/
	     
	    	for (Map.Entry<String,ArrayList<String>> entry : hashmap.entrySet()) {
	    	    String key = entry.getKey();
	    	    ArrayList<String> listt = entry.getValue();
	    	    String data="";
	    	    for (String string : listt) {
	    	    	data=string+"|"+data;
				}
	    	    String finalStr="XID: "+key+"   " +data;
	    	    bw.write(finalStr);
	    	    bw.newLine();
	    	}
	    	bw.close();
		
		/*String hql = "select p.PRODUCT_NUMBER,e.ERRORS from a4techconvertiontool.product_log p join  a4techconvertiontool.error_log e on p.PRODUCT_NUMBER = e.PRODUCT_NUMBER where    P.COMPANY_ID='55202'";
		Query query = session.createQuery(hql);
		List results = query.list();*/
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
	
	public void save(ExternalAPIResponse errors ,String productNo ,Integer asiNumber){
		_LOGGER.info("Enter the DAO class");
		Set<ErrorEntity>  listErrorEntity = new HashSet<ErrorEntity>();
		Session session = null;
		Transaction tx  = null;
		ErrorEntity errorEntity = new ErrorEntity();
		ErrorEntity errorEntityTemp = null;
		   Set<String> additionalInfo=errors.getAdditionalInfo();
		   errorEntity.setError(errors.getMessage());
		   errorEntity.setProductNumber(productNo);
		   listErrorEntity.add(errorEntity);
		   if(!additionalInfo.isEmpty() && additionalInfo!=null){
		for (String errorMessage : additionalInfo) {
			errorEntityTemp = new ErrorEntity();
			errorEntityTemp.setError(errorMessage);
			errorEntityTemp.setProductNumber(productNo);
			  listErrorEntity.add(errorEntityTemp);
		}
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
		/*
		//Criteria crit = session.createCriteria(ProductEntity.class);
		HashMap<String, ArrayList<String>> hashmap = new HashMap<String, ArrayList<String>>();
		Criteria prdCrit = session.createCriteria(ProductEntity.class);
		prdCrit.add(Restrictions.eq("companyId",asiNumber));
		//List results = prdCrit.list();
		  List<ProductEntity> list = prdCrit.list();
	        for(ProductEntity arr : list){
	        	
	            System.out.println();
	            
	            ArrayList<String> arraylist = new ArrayList<String>();
	            //hashmap.put(productEntity2.getProductNumber(), value)
	            for (ErrorEntity productEntity2 : arr.getErrors()) {
	            	arraylist.add(productEntity2.getError());
					System.out.println(productEntity2.getError());
				}
	            hashmap.put(arr.getProductNo(), arraylist);
	        }
	        String errorComp=Integer.toString(asiNumber);
	        File fout = new File("D:\\A4 ESPUpdate\\ErrorFiles\\"+errorComp+".txt");
	    	FileOutputStream fos = new FileOutputStream(fout);
	     
	    	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
	     
	    	for (int i = 0; i < 10; i++) {
	    		bw.write("something");
	    		bw.newLine();
	    	}
	     
	    	for (Map.Entry<String,ArrayList<String>> entry : hashmap.entrySet()) {
	    	    String key = entry.getKey();
	    	    ArrayList<String> listt = entry.getValue();
	    	    String data="";
	    	    for (String string : listt) {
	    	    	data=string+"|"+data;
				}
	    	    String finalStr="XID: "+key+"   " +data;
	    	    bw.write(finalStr);
	    	    bw.newLine();
	    	}
	    	bw.close();*/
		
		/*String hql = "select p.PRODUCT_NUMBER,e.ERRORS from a4techconvertiontool.product_log p join  a4techconvertiontool.error_log e on p.PRODUCT_NUMBER = e.PRODUCT_NUMBER where    P.COMPANY_ID='55202'";
		Query query = session.createQuery(hql);
		List results = query.list();*/
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
	
	public void getErrorLog(int asiNumber){
		Session session = null;
	
		try{
			 session = sessionFactory.openSession();
		 
		//Criteria crit = session.createCriteria(ProductEntity.class);
		HashMap<String, ArrayList<String>> hashmap = new HashMap<String, ArrayList<String>>();
		Criteria prdCrit = session.createCriteria(ProductEntity.class);
		prdCrit.add(Restrictions.eq("companyId",asiNumber));
		//List results = prdCrit.list();
		  List<ProductEntity> list = prdCrit.list();
	        for(ProductEntity arr : list){
	        	
	            System.out.println();
	            
	            ArrayList<String> arraylist = new ArrayList<String>();
	            //hashmap.put(productEntity2.getProductNumber(), value)
	            for (ErrorEntity productEntity2 : arr.getErrors()) {
	            	arraylist.add(productEntity2.getError());
					System.out.println(productEntity2.getError());
				}
	            hashmap.put(arr.getProductNo(), arraylist);
	        }
	        String errorComp=Integer.toString(asiNumber);
	        File fout = new File(errorFileLocPath+errorComp+".txt");
	    	FileOutputStream fos = new FileOutputStream(fout);
	     
	    	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
	     
	    	/*for (int i = 0; i < 10; i++) {
	    		bw.write("something");
	    		bw.newLine();
	    	}*/
	     
	    	for (Map.Entry<String,ArrayList<String>> entry : hashmap.entrySet()) {
	    	    String key = entry.getKey();
	    	    ArrayList<String> listt = entry.getValue();
	    	    String data="";
	    	    for (String string : listt) {
	    	    	data=string+"|"+data;
				}
	    	    String finalStr="XID: "+key+"   " +data;
	    	    bw.write(finalStr);
	    	    bw.newLine();
	    	}
	    	bw.close();
		
		/*String hql = "select p.PRODUCT_NUMBER,e.ERRORS from a4techconvertiontool.product_log p join  a4techconvertiontool.error_log e on p.PRODUCT_NUMBER = e.PRODUCT_NUMBER where    P.COMPANY_ID='55202'";
		Query query = session.createQuery(hql);
		List results = query.list();*/
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

	public String getErrorFileLocPath() {
		return errorFileLocPath;
	}

	public void setErrorFileLocPath(String errorFileLocPath) {
		this.errorFileLocPath = errorFileLocPath;
	}
	
	
}
