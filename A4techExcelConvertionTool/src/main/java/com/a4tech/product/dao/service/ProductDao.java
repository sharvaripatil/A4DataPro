package com.a4tech.product.dao.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.StringUtils;

import com.a4tech.core.errors.ErrorMessage;
import com.a4tech.core.model.ExternalAPIResponse;
import com.a4tech.ftp.model.FtpLoginBean;
import com.a4tech.product.dao.entity.BatchEntity;
import com.a4tech.product.dao.entity.ErrorEntity;
import com.a4tech.product.dao.entity.FtpServerFileEntity;
import com.a4tech.product.dao.entity.ProductEntity;
import com.a4tech.product.dao.entity.SupplierLoginDetails;
import com.a4tech.product.service.IProductDao;
import com.a4tech.util.ApplicationConstants;


public class ProductDao implements IProductDao{
	
	private static Logger _LOGGER = Logger.getLogger(ProductDao.class);
	
	SessionFactory sessionFactory;
	String 		  errorFileLocPath;
	@Override
	public void save(ExternalAPIResponse errors ,String productNo ,Integer asiNumber){
		_LOGGER.info("Enter the DAO class");
		Set<ErrorEntity>  listErrorEntity = new HashSet<ErrorEntity>();
		Session session = null;
		Transaction tx  = null;
		ErrorEntity errorEntity = new ErrorEntity();
		ErrorEntity errorEntityTemp = null;
		   Set<String> additionalInfo=errors.getAdditionalInfo();
		   errorEntity.setError(errors.getMessage());
		   listErrorEntity.add(errorEntity);
		   if(!StringUtils.isEmpty(additionalInfo)){
		for (String errorMessage : additionalInfo) {
			errorEntityTemp = new ErrorEntity();
			errorEntityTemp.setError(errorMessage);
			  listErrorEntity.add(errorEntityTemp);
		}
		   }
		ProductEntity productEntity = new ProductEntity();
		productEntity.setSupplierAsiNumber(asiNumber);
		productEntity.setProductNo(productNo);
		//productEntity.setProductStatus(false);
		productEntity.setErrors(listErrorEntity);
	try{
		 session = sessionFactory.openSession();
		 tx =  session.beginTransaction();
		 session.saveOrUpdate(productEntity);
		tx.commit();
	}catch(Exception ex){
		_LOGGER.error("Error in dao block : "+ ex.getMessage());
		if(tx != null){
			tx.rollback();
		}	
	}finally{
		if(session !=null){
			try{
				session.close();
			}catch(Exception ex){
				_LOGGER.warn("Error while close session object");
			}	
		}
	}	
	}
	@Override
	public int createBatchId(int asiNumber){
		_LOGGER.info("Inside batch Id method");
		Session session = null;
		Transaction tx  = null;
		int batchId = 0;
		try{
			session = sessionFactory.openSession();
			BatchEntity batchEntity = new BatchEntity();
			batchEntity.setAsiNumber(asiNumber);
			tx =  session.beginTransaction();
			batchId = (int) session.save(batchEntity);
			tx.commit();
		}catch(Exception ex){
			_LOGGER.error("unable to insert batch ids");
			if(tx != null){
				tx.rollback();
			}	
		}finally{
			if(session !=null){
				try{
					session.close();
				}catch(Exception seex){
					_LOGGER.warn("Error while close session object for create batch id");
				}		
			}
		}
		
		return batchId;
	}
	@Override
	public void save(List<ErrorMessage> errors ,String productNo ,Integer asiNumber,int batchId){
		_LOGGER.info("Enter the DAO class ");
		Session session = null;
		Transaction tx  = null;
		ErrorEntity errorEntity = null;
		ProductEntity productEntity = new ProductEntity();
		boolean flag=false;
		for (ErrorMessage errorMessage : errors) {
			if(errorMessage.getReason() == null){
				continue;
			}
				errorEntity = new ErrorEntity();
				//String tempError=errorMessage.getReason();
				String tempMessage=errorMessage.getMessage();
				if(tempMessage.contains("Your product could not be saved")|| tempMessage.toLowerCase().contains("internal server error")){
					flag=true;
				}
				errorEntity.setError(errorMessage.getReason());
				productEntity.addErrorEntity(errorEntity);	  
		}
		if(flag){
			productNo=productNo+"-Failed: ";
		}else{
			productNo=productNo+"-Saved Successfully: ";
		}
		productEntity.setSupplierAsiNumber(asiNumber);
		productEntity.setProductNo(productNo);
		productEntity.setBatchId(batchId);
		productEntity.setCreateProductDate(Calendar.getInstance().getTime());
	try{
		 session = sessionFactory.openSession();
		 tx =  session.beginTransaction();
		 session.save(productEntity);
		 tx.commit();
		
	}catch(Exception ex){
		_LOGGER.error("Error in dao block : "+ex.getCause());
		if(tx != null){
			tx.rollback();
		}	
	}finally{
		if(session !=null){
			try{
				session.close();
			}catch(Exception ex){
				_LOGGER.warn("Error while close session object");
			}
			
		}
	}	
 }
	
	@SuppressWarnings("unchecked")
	public void saveErrorLog(int asiNumber ,int batchId){
		Session session = null;
		
		try{
			 session = sessionFactory.openSession();
			 HashMap<String, ArrayList<String>> hashmap = new HashMap<String, ArrayList<String>>();
		Criteria prdCrit = session.createCriteria(ProductEntity.class);
		prdCrit.add(Restrictions.eq("batchId",batchId));
		  List<ProductEntity> list = prdCrit.list();
	        for(ProductEntity arr : list){
	        	if(StringUtils.isEmpty(arr.getProductNo())){
	        		continue;
	        	}
	            ArrayList<String> arraylist = new ArrayList<String>();
	           for (ErrorEntity productEntity2 : arr.getErrors()) {
	            	arraylist.add(productEntity2.getError());
				}
	            hashmap.put(arr.getProductNo(), arraylist);
	        }
	        String errorComp=Integer.toString(batchId);
			try (FileOutputStream fos = new FileOutputStream(new File(errorFileLocPath+errorComp+
					                                              ApplicationConstants.CONST_STRING_DOT_TXT));
					BufferedWriter bw = new BufferedWriter(
							new OutputStreamWriter(fos))) {
				for (Map.Entry<String, ArrayList<String>> entry : hashmap
						.entrySet()) {
					String key = entry.getKey();
					ArrayList<String> listt = entry.getValue();
					StringBuilder data = new StringBuilder();
					for (String error : listt) {
						data.append(error).append(
								ApplicationConstants.CONST_DELIMITER_PIPE);
					}
					data.setLength(data.length() - 1);
					String finalStr = "ProductID: " + key + " " + data;
					bw.write(finalStr);
					bw.append(System.lineSeparator());
				}
			}
		
		/*String hql = "select p.PRODUCT_NUMBER,e.ERRORS from a4techconvertiontool.product_log p join  a4techconvertiontool.error_log e on p.PRODUCT_NUMBER = e.PRODUCT_NUMBER where    P.COMPANY_ID='55202'";
		Query query = session.createQuery(hql);
		List results = query.list();*/
	}catch(Exception ex){
		_LOGGER.error("Error in dao block : "+ex.getMessage());
	}finally{
		if(session !=null){
			try{
				session.close();
			}catch(Exception ex){
				_LOGGER.warn("Error while close session object");
			}
			}
		}
	}
	@Override
	public String getFtpFileProcessStatus(String fileName,String asiNumber){
		  Session session = null;
		  //Transaction transaction = null;
		  try{
			  session = sessionFactory.openSession();
			 // transaction = session.beginTransaction();
			  Criteria criteria = session.createCriteria(FtpServerFileEntity.class);
	            criteria.add(Restrictions.eq("fileName", fileName));
	            criteria.setProjection(Projections.property("fileStatus"));
	            String data =  (String) criteria.uniqueResult();
	          //  criteria.
	           criteria.list();
	            return data;
		  }catch(Exception exce){
			  _LOGGER.error("unable to fetch ftpFile status::"+exce.getMessage());
		  }finally{
			  if(session != null){
				  try{
					  session.close();
				  }catch(Exception exce){
					  _LOGGER.error("unable to close session connection: "+exce.getMessage());
				  }
			  }
		  }
		  return null;
	}
	@Override
	public void updateFtpFileStatus(String fileName,String asiNumber,String fileStatus){
		   Session session = null;
		   Transaction transaction  = null;
		   try{
			    session = sessionFactory.openSession();
			    transaction = session.beginTransaction();
			    FtpServerFileEntity fileEntity = new FtpServerFileEntity();
			    fileEntity.setFileName(fileName);
			    fileEntity.setFileStatus(fileStatus);
			    fileEntity.setSupplierAsiNumber(asiNumber);
			    fileEntity.setFileProcessDate(Calendar.getInstance().getTime());
			    session.saveOrUpdate(fileEntity);
			    transaction.commit();
		   }catch(Exception exe){
			     _LOGGER.error("unable to upadte ftp file status: "+exe.getMessage());
			     if(transaction != null){
			    	 transaction.rollback();
			     }
		   }finally{
			    if(session != null){
			    	 try{
			    		 session.close();
			    	 }catch(Exception exce){
			    		 _LOGGER.error("unable to close session connection: "+exce.getMessage());
			    	 }
			    }
		   }
	}
	@Override
	public SupplierLoginDetails getSupplierLoginDetails(String asiNumber){
		  Session session = null;
		 // Transaction transaction = null;
		  try{
			  session = sessionFactory.openSession();
			//  transaction = session.beginTransaction();
			  Criteria criteria = session.createCriteria(SupplierLoginDetails.class);
	            criteria.add(Restrictions.eq("asiNumber", asiNumber));
	            SupplierLoginDetails data =  (SupplierLoginDetails) criteria.uniqueResult();
	            return data;
		  }catch(Exception exce){
			  _LOGGER.error("unable to fetch supplier login details::"+exce.getMessage());
		  }finally{
			  if(session != null){
				  try{
					  session.close();
				  }catch(Exception exce){
					  _LOGGER.error("unable to close session connection: "+exce.getMessage());
				  }
			  }
		  }
		  return null;
	}
	@Override
	public void saveSupplierCridentials(FtpLoginBean ftpLoginBean){
		  Session session = null;
		  Transaction transaction = null;
		  SupplierLoginDetails loginDetails = new SupplierLoginDetails();
		  try{
			  session = sessionFactory.openSession();
			  transaction = session.beginTransaction();
			  loginDetails.setAsiNumber(ftpLoginBean.getAsiNumber());
			  loginDetails.setUserName(ftpLoginBean.getUserName());
			  loginDetails.setPassword(ftpLoginBean.getPassword());
			  session.save(loginDetails);
			  transaction.commit();
		  }catch(Exception exce){
			  _LOGGER.error("unable to save supplier cridentials::"+exce.getMessage());
			  if(transaction != null){
				  transaction.rollback();
			  }
		  }finally{
			  if(session != null){
				  try{
					  session.close();
				  }catch(Exception exce){
					  _LOGGER.error("unable to close session connection: "+exce.getMessage());
				  }
			  }
		  }
		
	}
	@Override
	public boolean isASINumberAvailable(String asiNumber) {
		SupplierLoginDetails supplierLoginDetails = getSupplierLoginDetails(asiNumber);
		if(supplierLoginDetails == null){
			return true;
		}
		return false;
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
