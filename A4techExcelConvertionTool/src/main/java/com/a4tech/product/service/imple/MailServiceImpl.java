package com.a4tech.product.service.imple;

import java.util.Arrays;

import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.a4tech.product.service.IMailService;
import com.a4tech.util.ApplicationConstants;

public class MailServiceImpl implements IMailService{
	
	private static Logger _LOGGER = Logger.getLogger(MailServiceImpl.class);
	@Autowired
    private JavaMailSender mailSender;
    private String  senderMailName;
    /*
     * (non-Javadoc)
     * @see com.a4tech.product.service.IMailService#sendMail(java.lang.String, int)
     * @description this method is send mails to supplier based on supplierASI No
     * @return boolean, if mail has been send success then return true else return false
     */
	@Override
	public boolean sendMail(String supplierId, int batchId) {
		String fileName= batchId+ApplicationConstants.CONST_STRING_DOT_TXT;
		try {
				FileSystemResource  file = new FileSystemResource(ApplicationConstants.CONST_STRING_DOWNLOAD_FILE_PATH+ fileName);
		      MimeMessage mimeMessage = mailSender.createMimeMessage();
		      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		      helper.setFrom(senderMailName);
		      //helper.setTo("venkateswarlu.nidamanuri@a4technology.com");
		      String[] toAddress = {"venkateswarlu.nidamanuri@a4technology.com","amey.more@a4technology.com"};
		      helper.setTo(toAddress);
		     // helper.setCc(ApplicationConstants.SUPPLIER_EMAIL_ID_MAP.get(supplierId));
		      helper.setSubject(supplierId +" "+ "Supplier Error Report");
		      helper.setText("Kindly find the attached " +batchId +".txt Product Error File"
		             + "\n\n\n\n Note: This is a System Generated Message Kindly Do not reply back");
		       helper.addAttachment(file.getFilename(), file);
			_LOGGER.info("Sending Email to : "
					+ Arrays.toString(toAddress));
			   mailSender.send(mimeMessage);
		       _LOGGER.info("Mail Sent Successfully !!!");
		      } catch (javax.mail.MessagingException e) {
			    _LOGGER.error("Mail Not Sent Successfully,Error Msg:"+e.toString());
			    	return false;
			 }catch (Exception e) {
			   _LOGGER.error("Mail Not Sent Successfully,Error Msg:"+e.toString());
			     return false;
			}
		return true;
	}
	public String getSenderMailName() {
		return senderMailName;
	}
	public void setSenderMailName(String senderMailName) {
		this.senderMailName = senderMailName;
	}

}
