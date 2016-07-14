package com.a4tech.controller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.a4tech.util.ApplicationConstants;


@Controller
@RequestMapping("/sendEmails")
public class DownloadFileController {
	
	private static Logger _LOGGER = Logger.getLogger(DownloadFileController.class);
	
	    String username;
	    String password;
	    String domain;
	    String portNo;
	
	@RequestMapping(method = RequestMethod.GET)
	public String doSendEmail(HttpServletRequest request,
			HttpServletResponse response,Model model) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		 
		String batchId=(String) request.getSession().getAttribute("batchId");
		  
		 
		String fileName= batchId+".txt";
		String filepath =ApplicationConstants.CONST_STRING_DOWNLOAD_FILE_PATH;
		//String emailMsg="No Error File Found for Supplier "+supplierId +" ,Email not sent!!!";
		
		File f = new File(filepath+ fileName);
		boolean flag=false;
		  if(f.exists()){
			  flag=true;
		  }
		  response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ fileName + "\"");
			int lineNum;
			try(PrintWriter out = response.getWriter();
					FileInputStream fileInputStream = new FileInputStream(ApplicationConstants.CONST_STRING_DOWNLOAD_FILE_PATH
					+ fileName)){
				while ((lineNum = fileInputStream.read()) != ApplicationConstants.CONST_NEGATIVE_NUMBER_ONE) {
					out.write(lineNum);
				}
			}catch (FileNotFoundException e) {
				_LOGGER.fatal("Error log file is not available");
			}
        return "success";    
			
	}
	
	public void sendMail(String supplierId,int batchId){
		String fileName= batchId+".txt";
		try {
				Properties props = new Properties();
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.host", domain);
				props.put("mail.smtp.port", portNo);

				Session session = Session.getInstance(props,
				  new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				  });
		    DataSource source = new FileDataSource(ApplicationConstants.CONST_STRING_DOWNLOAD_FILE_PATH+ 
		    		                                                                             fileName);
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(ApplicationConstants.SUPPLIER_EMAIL_ID_MAP.get(supplierId)));
			message.setSubject("Product Error Batch File");
			  // Create the message part
	         BodyPart messageBodyPart = new MimeBodyPart();

	         // Now set the actual message
	         messageBodyPart.setText("Kindly find the attached " +batchId +" Product Error File"
	        		 + "\n\n\n\n Note: This is a System Generated Message Kindly Do not reply back");
	        
	         Multipart multipart = new MimeMultipart();
	         multipart.addBodyPart(messageBodyPart);
	         messageBodyPart = new MimeBodyPart();
	        
	         messageBodyPart.setDataHandler(new DataHandler(source));
	         messageBodyPart.setFileName(String.valueOf(batchId));
	         multipart.addBodyPart(messageBodyPart);
	        message.setContent(multipart);
			Transport.send(message);
			_LOGGER.info("Email Sent Successfully to Suppier batchId " +batchId+" On Email Id: "+ApplicationConstants.SUPPLIER_EMAIL_ID_MAP.get(supplierId));
		}catch(Exception e){
			_LOGGER.error("Error While Sending Email To Supplier "+batchId +e.toString());
		}
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPortNo() {
		return portNo;
	}

	public void setPortNo(String portNo) {
		this.portNo = portNo;
	}
}