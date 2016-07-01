package com.a4tech.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.a4tech.JulyData.excelMapping.JulyDataMapping;
import com.a4tech.core.model.FileBean;
import com.a4tech.core.validator.FileValidator;
import com.a4tech.product.service.ProductService;
import com.a4tech.service.loginImpl.LoginServiceImpl;
import com.a4tech.usbProducts.excelMapping.UsbProductsExcelMapping;
import com.a4tech.v2.core.excelMapping.ExcelMapping;

@Controller
@RequestMapping({"/","/uploadFile.htm"})
public class FileUpload extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	ProductService productService;
	@Autowired
	FileValidator fileValidator;
	//FileValidator validate = new FileValidator();
	private static String accessToken = null;
	private UsbProductsExcelMapping usbExcelMapping;
	private JulyDataMapping julymapping;
	private ExcelMapping excelMapping;
	@Autowired
	private LoginServiceImpl loginService;
	private static Logger _LOGGER = Logger.getLogger(Class.class);
	@InitBinder
	private void initBinder(WebDataBinder binder){
		binder.setValidator(fileValidator);
	}
	@RequestMapping( method=RequestMethod.GET)
	public String welcomePage(Map<String, Object> model){
		FileBean fileBean = new FileBean();
		model.put("filebean", fileBean);
		return "Home";
	}
	
	@RequestMapping(method= RequestMethod.POST)
	public String fileUpload(@ModelAttribute("filebean") @Valid FileBean fileBean , BindingResult result ,
			final RedirectAttributes redirectAttributes , Model model,HttpServletRequest request){
		_LOGGER.info("Enter Controller Class");
		//LoginServiceImpl loginService  = new LoginServiceImpl();
		 Workbook workbook = null;

		int numOfProducts =0;
		 String asiNumber = fileBean.getAsiNumber();
		 if(result.hasErrors()){
			 return "Home"; 
		 }
		 if(accessToken == null){
         	accessToken = loginService.doLogin("55201",  fileBean.getUserName(),
         													fileBean.getPassword());
         	if(accessToken.equalsIgnoreCase("unAuthorized")){
         		accessToken = null;
         		model.addAttribute("invalidDetails", "");
         		 return "Home";
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
	            	 request.getSession().setAttribute("asiNumber", asiNumber);
	                switch (asiNumber) {
					case "55201"://product v2
				        numOfProducts = excelMapping.readExcel(accessToken,workbook, Integer.valueOf(asiNumber));
		                model.addAttribute("fileName", numOfProducts);
		                return "success";
						//break;
					case "55202"://supplier USB data
							numOfProducts = usbExcelMapping.readExcel(accessToken, workbook, Integer.valueOf(asiNumber));
							model.addAttribute("fileName", numOfProducts);
							return "success";
					case "55203":	//supplier JulyData	
						numOfProducts = julymapping.readExcel(accessToken, workbook,Integer.valueOf(asiNumber));
						model.addAttribute("fileName", numOfProducts);
						return "success";
							
					default:
						break;
					}
	            	
	        }catch(IOException e1){
	        	e1.printStackTrace();
	        }catch (Exception e) {
				// TODO: handle exception
			}
        return "Home";
}
	public FileValidator getFileValidator() {
		return fileValidator;
	}
	public void setFileValidator(FileValidator fileValidator) {
		this.fileValidator = fileValidator;
	}
	public UsbProductsExcelMapping getUsbExcelMapping() {
		return usbExcelMapping;
	}
	public void setUsbExcelMapping(UsbProductsExcelMapping usbExcelMapping) {
		this.usbExcelMapping = usbExcelMapping;
	}
	public JulyDataMapping getJulymapping() {
		return julymapping;
	}
	public void setJulymapping(JulyDataMapping julymapping) {
		this.julymapping = julymapping;
	}
	public LoginServiceImpl getLoginService() {
		return loginService;
	}
	public void setLoginService(LoginServiceImpl loginService) {
		this.loginService = loginService;
	}
	public ProductService getProductService() {
		return productService;
	}
	public void setProductService(ProductService productService) {
		this.productService = productService;
	}
	public ExcelMapping getExcelMapping() {
		return excelMapping;
	}
	public void setExcelMapping(ExcelMapping excelMapping) {
		this.excelMapping = excelMapping;
	}
	
	
}
