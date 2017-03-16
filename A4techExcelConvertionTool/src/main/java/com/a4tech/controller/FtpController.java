package com.a4tech.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.a4tech.ftp.model.FtpFileUploadBean;
import com.a4tech.ftp.model.FtpLoginBean;
import com.a4tech.product.service.ILoginService;
import com.a4tech.util.ApplicationConstants;

@RestController
public class FtpController {
	private static Logger _LOGGER = Logger.getLogger(FtpController.class);
	@Autowired
	private ILoginService loginService;
	@RequestMapping("/loginFtp")
	public ModelAndView welcomeFtpLogin(){
		FtpLoginBean ftpLoginBean = new FtpLoginBean();
		return new ModelAndView("ftpLogin", "ftpLoginBean", ftpLoginBean);	
	}
	
	@RequestMapping("/fileUpload")
	public ModelAndView fileUpload(){
		FtpFileUploadBean ftpFileUploadBean = new FtpFileUploadBean();
		return new ModelAndView("fileUpload", "ftpFileUploadBean", ftpFileUploadBean);	
	}
	@RequestMapping("/processFile")
	public String processFile(@ModelAttribute("ftpLoginBean") FtpLoginBean ftpLogin,Model model){
		_LOGGER.info("Enter FTP file upload Process controller");
		String accessToken = "";
		accessToken = loginService.doLogin(ftpLogin.getAsiNumber(), ftpLogin.getUserName(), ftpLogin.getPassword());
		if (accessToken != null) {
			if (ApplicationConstants.CONST_STRING_UN_AUTHORIZED.equals(accessToken)) {
				accessToken = null;
				model.addAttribute(ApplicationConstants.CONST_STRING_INVALID_DETAILS, 
						                                     ApplicationConstants.CONST_STRING_EMPTY);
				return ApplicationConstants.CONST_STRING_HOME;
			}
		} else {
			return ApplicationConstants.CONST_STRING_ERROR_PAGE;
		}
		return "ftpLogin";
	}

}
