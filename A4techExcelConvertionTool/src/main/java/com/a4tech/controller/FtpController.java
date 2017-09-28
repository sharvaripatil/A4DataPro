package com.a4tech.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.a4tech.ftp.model.FtpFileUploadBean;
import com.a4tech.ftp.model.FtpLoginBean;
import com.a4tech.ftp.service.FtpService;
import com.a4tech.product.service.ILoginService;
import com.a4tech.product.service.IProductDao;
import com.a4tech.util.ApplicationConstants;

@RestController
public class FtpController {
	private static Logger _LOGGER = Logger.getLogger(FtpController.class);
	@Autowired
	private ILoginService loginService;
	@Autowired
	private FtpService ftpServices;
	@Autowired
	private IProductDao productDao;
	@RequestMapping(value="ftpLogin")
	public ModelAndView welcomeFtpLogin(){
		return new ModelAndView("ftpLogin", "ftpLoginBean", new FtpLoginBean());	
	}
	
	@RequestMapping(value="checkLoginDetails")
	public ModelAndView fileUpload(@ModelAttribute("ftpLoginBean") FtpLoginBean ftpLogin,Model model){
		FtpFileUploadBean ftpFileUploadBean = new FtpFileUploadBean();
		_LOGGER.info("Enter FTP file upload Process controller");
		String accessToken = "";
		accessToken = loginService.doLogin(ftpLogin.getAsiNumber(), ftpLogin.getUserName(), ftpLogin.getPassword());
		if (accessToken != null) {
			if (ApplicationConstants.CONST_STRING_UN_AUTHORIZED.equals(accessToken)) {
				accessToken = null;
				model.addAttribute(ApplicationConstants.CONST_STRING_INVALID_DETAILS, 
						                                     ApplicationConstants.CONST_STRING_EMPTY);
				return new ModelAndView("ftpLogin", "ftpLoginBean", new FtpLoginBean());	
			}
		if(productDao.isASINumberAvailable(ftpLogin.getAsiNumber())){
			productDao.saveSupplierCridentials(ftpLogin);
		}
		} else {
			return new ModelAndView("ftpLogin", "ftpLoginBean", new FtpLoginBean());	
		}
		//return "ftpLogin";
		ftpFileUploadBean.setAsiNumber(ftpLogin.getAsiNumber());
		return new ModelAndView("fileUpload", "ftpFileUploadBean", ftpFileUploadBean);	
	}
	@RequestMapping(value="/uploadFile")
	public ModelAndView processFile(@ModelAttribute("ftpFileUploadBean") FtpFileUploadBean fileUploadBean,Model model){
		_LOGGER.info("Enter FTP file upload Process controller");
		String accessToken = "";
		String asiNumber = fileUploadBean.getAsiNumber();
		MultipartFile   file= fileUploadBean.getFile();
		long fileSize = file.getSize();
		if(fileSize == 0){
			model.addAttribute("invalidFile", "");
			FtpFileUploadBean ftpFileUploadBean = new FtpFileUploadBean();
			ftpFileUploadBean.setAsiNumber(asiNumber);
			return new ModelAndView("fileUpload", "ftpFileUploadBean", ftpFileUploadBean);
		}
		if(StringUtils.isEmpty(asiNumber)){
			model.addAttribute("invalidAsiNum", "");
			return new ModelAndView("fileUpload", "ftpFileUploadBean", new FtpFileUploadBean());
		}
		/*if(StringUtils.isEmpty(asiNumber)){
			model.addAttribute("invalidAsiNum", "");
			return new ModelAndView("fileUpload", "ftpFileUploadBean", new FtpFileUploadBean());
		}*/
		if (accessToken != null) {
			if (ApplicationConstants.CONST_STRING_UN_AUTHORIZED.equals(accessToken)) {
				accessToken = null;
				model.addAttribute(ApplicationConstants.CONST_STRING_INVALID_DETAILS, 
						                                     ApplicationConstants.CONST_STRING_EMPTY);
				return new ModelAndView(ApplicationConstants.CONST_STRING_HOME);
			}
		}
	   boolean fileStaus = ftpServices.uploadFile(file,asiNumber);
	   if(fileStaus == true){
		   model.addAttribute("ftpMessage", "success");
	   } else{
		   model.addAttribute("ftpMessage", "failure");
	   }
		return new ModelAndView("fileUpload", "ftpFileUploadBean", new FtpFileUploadBean());	
	}
}
