/*package com.a4tech.controller;

import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.a4tech.util.ApplicationConstants;
import com.mysql.jdbc.Connection;


	@Controller
	@RequestMapping("/checkConnection")
	public class CheckDBConnectionController {
			
		private static Logger _LOGGER = Logger.getLogger(CheckDBConnectionController.class);
		
		  JavaMailSender mailSenderObj;
			String username;
			String dbURL;
			String dbUsername;
			String dbPassword;
			String dbDriver;
		
		@RequestMapping(method= RequestMethod.POST)
		public String fileUpload(Model model,HttpServletRequest request){
			_LOGGER.info("In DB check controller !!");
			String flag="success";
			String error=null;
			  Connection connection = null;
			  try{
		    		Class.forName(dbDriver);
		    		connection = (Connection) DriverManager
		    		.getConnection(dbURL,dbUsername, dbPassword);
			  }catch(SQLException | ClassNotFoundException sqlex){
				  error=sqlex.toString();
				  _LOGGER.error(sqlex.toString());
			  }
			  
			  try{
		    	if (connection != null) {
		    		System.out.println("connection passed");
		    	} else { 
		    		flag="failure";
		    		SimpleMailMessage message = new SimpleMailMessage();  
		    	        message.setFrom(username);  
		    	    message.setTo(ApplicationConstants.SUPPORT_EMAIL_ID_MAP.get("dev_1"));
		    	    message.setTo(ApplicationConstants.SUPPORT_EMAIL_ID_MAP.get("dev_2"));
		    	    message.setTo(ApplicationConstants.SUPPORT_EMAIL_ID_MAP.get("sysadmin_1"));
		    	    message.setCc(ApplicationConstants.SUPPORT_EMAIL_ID_MAP.get("dev_3"));
		    	    message.setCc(ApplicationConstants.SUPPORT_EMAIL_ID_MAP.get("sysadmin_2"));
		    	    message.setSubject("Database Connectivity Failure");  
		    	    message.setText("Falied to connect to MySql database on server 192.168.1.10 on port 3306 for DB a4techconvertiontool"
		    	    		+"\n\n\n Error Log: "+error
							+"\n\n\n\n Please Look into the issue on priority basis !!!");  
		    	    _LOGGER.info("Sending Email for DB Failure");
		    	    
		    	    mailSenderObj.send(message);
		    	     _LOGGER.info("DB Failure Mail Sent Successfully!!");
		    	} 
		    	}catch (Exception mex) {
		    	         _LOGGER.error("Failed to sent mail "+mex.toString());
		    	      }
		    		
			return flag;
		}

		public JavaMailSender getMailSenderObj() {
			return mailSenderObj;
		}

		public void setMailSenderObj(JavaMailSender mailSenderObj) {
			this.mailSenderObj = mailSenderObj;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getDbURL() {
			return dbURL;
		}

		public void setDbURL(String dbURL) {
			this.dbURL = dbURL;
		}

		public String getDbUsername() {
			return dbUsername;
		}

		public void setDbUsername(String dbUsername) {
			this.dbUsername = dbUsername;
		}

		public String getDbPassword() {
			return dbPassword;
		}

		public void setDbPassword(String dbPassword) {
			this.dbPassword = dbPassword;
		}

		public String getDbDriver() {
			return dbDriver;
		}

		public void setDbDriver(String dbDriver) {
			this.dbDriver = dbDriver;
		}	
		
	
	
	}
		
		
		
		
		
		


*/