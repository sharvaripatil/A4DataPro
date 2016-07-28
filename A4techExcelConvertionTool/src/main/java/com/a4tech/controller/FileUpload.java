package com.a4tech.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.a4tech.JulyData.excelMapping.JulyDataMapping;
import com.a4tech.core.excelMapping.ExcelMapping;
import com.a4tech.core.model.FileBean;
import com.a4tech.core.validator.FileValidator;
import com.a4tech.dc.product.mapping.DCProductsExcelMapping;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.service.ProductService;
import com.a4tech.sage.product.mapping.SageProductsExcelMapping;
import com.a4tech.service.loginImpl.LoginServiceImpl;
import com.a4tech.usbProducts.excelMapping.UsbProductsExcelMapping;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.v2.core.excelMapping.V2ExcelMapping;

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
	private SageProductsExcelMapping sageExcelMapping;
	private V2ExcelMapping productV2ExcelMapping;
	private ExcelMapping gbDataExcelMapping;
	private DownloadFileController downloadMail;
	private DCProductsExcelMapping dCProductsExcelMapping;
	@Autowired
	private LoginServiceImpl loginService;
	private ProductDao productDao;
	private static Logger _LOGGER = Logger.getLogger(Class.class);
	@InitBinder
	private void initBinder(WebDataBinder binder){
		binder.setValidator(fileValidator);
	}
	@RequestMapping( method=RequestMethod.GET)
	public String welcomePage(Map<String, Object> model){
		FileBean fileBean = new FileBean();
		model.put("filebean", fileBean);
		return "home";
	}
	
	
	@RequestMapping(method= RequestMethod.POST)
	public String fileUpload(@ModelAttribute("filebean") @Valid FileBean fileBean , BindingResult result ,
			final RedirectAttributes redirectAttributes , Model model,HttpServletRequest request){
		_LOGGER.info("Enter Controller Class");
		/*String asiNumber = request.getParameter("asiNumber");
		String userName = request.getParameter("asiNumber");
		String password = request.getParameter("asiNumber");
		//MultipartFile file = (MultipartFile) request.; */		
		String finalResult = null;
		//LoginServiceImpl loginService  = new LoginServiceImpl();
		 Workbook workbook = null;

		int numOfProducts =0;
		String emailMsg="Email has been sent Successfully !!!";
		String noOfProductsSuccess = null;
		String noOfProductsFailure = null;
		String[] splitFinalResult;
		String asiNumber = fileBean.getAsiNumber();
		 if(result.hasErrors()){
			 return "home"; 
		 }
		 if(accessToken == null){
         	accessToken = loginService.doLogin("55201",  fileBean.getUserName(),
         													fileBean.getPassword());
         	if(accessToken != null){
         		if(accessToken.equalsIgnoreCase("unAuthorized")){
             		accessToken = null;
             		model.addAttribute("invalidDetails", "");
             		 return "home";
             	}
         	}else{
         		return "errorPage";
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
	            int batchId = productDao.createBatchId(Integer.parseInt(asiNumber));
	            	 request.getSession().setAttribute("batchId", String.valueOf(batchId));
	                switch (asiNumber) {
	                case "55200": // GB Data Excel Mapping
	                	 	numOfProducts = gbDataExcelMapping.readExcel(accessToken,workbook, Integer.valueOf(asiNumber),batchId);
			                model.addAttribute("fileName", numOfProducts);
			                downloadMail.sendMail(asiNumber, batchId);
							model.addAttribute("successmsg", emailMsg);
			                return "success";
					case "55201"://product v2
				        numOfProducts = productV2ExcelMapping.readExcel(accessToken,workbook, Integer.valueOf(asiNumber),batchId);
		                model.addAttribute("fileName", numOfProducts);
		                downloadMail.sendMail(asiNumber, batchId);
						model.addAttribute("successmsg", emailMsg);
		                return "success";
						//break;
					case "55202"://supplier USB data(Nov_USB Products)
						finalResult = usbExcelMapping.readExcel(accessToken, workbook, Integer.valueOf(asiNumber),batchId);
						if(finalResult != null){
							splitFinalResult = finalResult.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
							noOfProductsSuccess = splitFinalResult[0];
							noOfProductsFailure = splitFinalResult[1];
							redirectAttributes.addFlashAttribute("successProductsCount", noOfProductsSuccess);
				    		redirectAttributes.addFlashAttribute("failureProductsCount", noOfProductsFailure);
							if(!noOfProductsFailure.equals(ApplicationConstants.CONST_STRING_ZERO)){
								redirectAttributes.addFlashAttribute("successmsg", emailMsg);
								downloadMail.sendMail(asiNumber, batchId);
							}
						}
						return "redirect:redirect.htm";
					case "55203":	//supplier JulyData	
						numOfProducts = julymapping.readExcel(accessToken, workbook,Integer.valueOf(asiNumber),batchId);
						model.addAttribute("fileName", numOfProducts);
						return "success";
				    case "55204":	//supplier Sage
				    	finalResult = sageExcelMapping.readExcel(accessToken, workbook, Integer.valueOf(asiNumber),batchId);
				    	if(finalResult != null){
				    		splitFinalResult = finalResult.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
				    		noOfProductsSuccess = splitFinalResult[0];
				    		noOfProductsFailure = splitFinalResult[1];
				    		redirectAttributes.addFlashAttribute("successProductsCount", noOfProductsSuccess);
				    		redirectAttributes.addFlashAttribute("failureProductsCount", noOfProductsFailure);
				    		if(!noOfProductsFailure.equals(ApplicationConstants.CONST_STRING_ZERO)){
				    			redirectAttributes.addFlashAttribute("successmsg", emailMsg);
				    			downloadMail.sendMail(asiNumber, batchId);
				    		}
				       }
						return "redirect:redirect.htm";
						
				    case "55205":  //Distributor Central
				    	finalResult = dCProductsExcelMapping.readExcel(asiNumber, workbook, 
				    			                                          Integer.valueOf(asiNumber), batchId);
				    	return "redirect:redirect.htm";
					default:
						break;
					}
	            	
	        }catch(IOException e1){
	        	
	        }catch (Exception e) {
				// TODO: handle exception
			}
        return "home";
}
	@RequestMapping(value="/redirect.htm",method = RequestMethod.GET)
	 public String submit(Model model){
	   String noOfSucc = (String)model.asMap().get("successProductsCount");
	   String noOfFail = (String)model.asMap().get("failureProductsCount");
	   if(noOfSucc == null){
		   model.addAttribute("successProductsCount", "0");
   		
	   }
	   if(noOfFail == null){
		 model.addAttribute("failureProductsCount", "0");
	   }
	    return "success";
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
	public SageProductsExcelMapping getSageExcelMapping() {
		return sageExcelMapping;
	}
	public void setSageExcelMapping(SageProductsExcelMapping sageExcelMapping) {
		this.sageExcelMapping = sageExcelMapping;
	}
	public ProductDao getProductDao() {
		return productDao;
	}
	public void setProductDao(ProductDao productDao) {
		this.productDao = productDao;
	}
	public V2ExcelMapping getProductV2ExcelMapping() {
		return productV2ExcelMapping;
	}
	public void setProductV2ExcelMapping(V2ExcelMapping productV2ExcelMapping) {
		this.productV2ExcelMapping = productV2ExcelMapping;
	}
	public ExcelMapping getGbDataExcelMapping() {
		return gbDataExcelMapping;
	}
	public void setGbDataExcelMapping(ExcelMapping gbDataExcelMapping) {
		this.gbDataExcelMapping = gbDataExcelMapping;
	}
	public DownloadFileController getDownloadMail() {
		return downloadMail;
	}
	public void setDownloadMail(DownloadFileController downloadMail) {
		this.downloadMail = downloadMail;
	}
	public DCProductsExcelMapping getdCProductsExcelMapping() {
		return dCProductsExcelMapping;
	}
	public void setdCProductsExcelMapping(
			DCProductsExcelMapping dCProductsExcelMapping) {
		this.dCProductsExcelMapping = dCProductsExcelMapping;
	}
	
}
