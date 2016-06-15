package com.a4tech.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.a4tech.JulyData.excelMapping.JulyDataMapping;
import com.a4tech.core.model.FileBean;
import com.a4tech.core.validator.FileValidator;
import com.a4tech.product.service.ProductService;
import com.a4tech.service.loginImpl.LoginServiceImpl;
import com.a4tech.usbProducts.excelMapping.UsbProductsExcelMapping;
import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

@Controller
@RequestMapping({"/","/uploadFile.htm"})
public class FileUpload {
	
	@Autowired
	ProductService productService;
	@Autowired
	FileValidator fileValidator;
	FileValidator validate = new FileValidator();
	private static String accessToken = null;
	
	private static Logger _LOGGER = Logger.getLogger(Class.class);
	@InitBinder
	private void initBinder(WebDataBinder binder){
		binder.setValidator(fileValidator);
	}
	@RequestMapping( method=RequestMethod.GET)
	public String welcomePage(Map<String, Object> model){
		FileBean fileBean = new FileBean();
		model.put("filebean", fileBean);
		return "Index";
	}
	@RequestMapping(method= RequestMethod.POST)
	public String fileUpload(@ModelAttribute("filebean") @Valid FileBean fileBean , BindingResult result ,
			final RedirectAttributes redirectAttributes , Model model){
		_LOGGER.info("Enter Controller Class");
		LoginServiceImpl loginService  = new LoginServiceImpl();
		 Workbook workbook = null;
		UsbProductsExcelMapping usbExcelMapping = new UsbProductsExcelMapping();
		JulyDataMapping Julymapping =new JulyDataMapping();
		int numOfProducts =0;
		 String asiNumber = fileBean.getAsiNumber();
		 if(result.hasErrors()){
			 return "Index"; 
		 }
		 if(accessToken == null){
         	accessToken = loginService.doLogin("55201",  fileBean.getUserName(),
         													fileBean.getPassword());
         	if(accessToken.equalsIgnoreCase("unAuthorized")){
         		accessToken = null;
         		model.addAttribute("invalidDetails", "");
         		 return "Index";
         	}
         }
		try (ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFile().getBytes())){
	       
	            	if (fileBean.getFile().getOriginalFilename().endsWith("xls")) {
	                workbook = new HSSFWorkbook(bis);
	                
	            	} else if (fileBean.getFile().getOriginalFilename().endsWith("xlsx")) {
	            		workbook = new XSSFWorkbook(bis);
	            	}else{
	 	               _LOGGER.info("Invlid upload excel file,Please try one more time");
	            	}
	                
	                switch (asiNumber) {
					case "55201"://product v2
				        numOfProducts = productService.excelProducts(accessToken,workbook);
		                model.addAttribute("fileName", numOfProducts);
		                return "success";
						//break;
					case "55202"://supplier USB data
						
							numOfProducts = usbExcelMapping.readExcel(accessToken, workbook);
							model.addAttribute("fileName", numOfProducts);
							return "success";
					case "55203":	//supplier JulyData	
						numOfProducts = Julymapping.readExcel(accessToken, workbook);
						model.addAttribute("fileName", numOfProducts);
						return "success";
							
					default:
						break;
					}
	            	
	        }catch(IOException e1){
	        	
	        }catch (Exception e) {
				// TODO: handle exception
			}
        return "Index";
}
	public FileValidator getFileValidator() {
		return fileValidator;
	}
	public void setFileValidator(FileValidator fileValidator) {
		this.fileValidator = fileValidator;
	}
}
