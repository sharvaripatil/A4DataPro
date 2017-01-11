package com.a4tech.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.a4tech.core.excelMapping.ExcelFactory;
import com.a4tech.core.model.FileBean;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.service.ILoginService;
import com.a4tech.product.service.IMailService;
import com.a4tech.product.service.IProductService;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.a4tech.util.ConvertCsvToExcel;
import com.a4tech.util.LookupData;

@Controller
@RequestMapping({ "/", "/uploadFile.htm" })
public class FileUpload {

	@Autowired
	IProductService               productService;
	@Autowired
	IMailService                  mailService;
	private String                accessToken = null;
	@Autowired
	private ILoginService         loginService;
	private ProductDao            productDao;
	private ConvertCsvToExcel     convertCsvToExcel;
	private ExcelFactory          excelFactory;
	private LookupData            lookupData;

	private static Logger _LOGGER = Logger.getLogger(Class.class);
	
	@RequestMapping(method = RequestMethod.GET)
	public String welcomePage(Map<String, Object> model) {
		FileBean fileBean = new FileBean();
		model.put("filebean", fileBean);
		return ApplicationConstants.CONST_STRING_HOME;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String fileUpload(
			@ModelAttribute("filebean") FileBean fileBean, final RedirectAttributes redirectAttributes,
			                                                             Model model, HttpServletRequest request) {
		_LOGGER.info("Enter Controller Class");
		String finalResult = null;
		String asiNumber = fileBean.getAsiNumber();
		String fileExtenion = CommonUtility.getFileExtension(fileBean.getFile()
																.getOriginalFilename());
		if (!CommonUtility.isValidFormat(fileExtenion)) {
			model.addAttribute(ApplicationConstants.CONST_STRING_INVALID_UPLOAD_FILE, 
                                              ApplicationConstants.CONST_STRING_EMPTY);
			return ApplicationConstants.CONST_STRING_HOME;
		}
		try  {
			accessToken = loginService.doLogin(fileBean.getAsiNumber(),
					fileBean.getUserName(), fileBean.getPassword());
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
			Workbook workbook = convertCsvToExcel.getWorkBook(fileBean.getFile());
			if(workbook == null){
				return ApplicationConstants.CONST_STRING_ERROR_PAGE;
			}
			lookupData.loadLineNames(asiNumber, accessToken);
			lookupData.loadFobPoints(asiNumber, accessToken);
			int batchId = productDao.createBatchId(Integer.parseInt(asiNumber));
			request.getSession().setAttribute("batchId",
					String.valueOf(batchId));
			IExcelParser parserObject = excelFactory.getExcelParserObject(asiNumber);
			if(parserObject != null){ // new implemention
				finalResult = parserObject.readExcel(accessToken, workbook, 
                        Integer.valueOf(asiNumber), batchId);
		    	if (finalResult != null) {
					parseFinalData(finalResult, asiNumber, batchId, redirectAttributes);
				}
		    	return ApplicationConstants.CONST_REDIRECT_URL;
			}
		 } catch (Exception e) {
			_LOGGER.error("Error In FileUpload: " + e.getMessage());
		}
		return ApplicationConstants.CONST_STRING_HOME;
	}
	@RequestMapping(value = "/redirect.htm", method = RequestMethod.GET)
	public String submit(Model model) {
		String noOfSucc = (String) model.asMap().get(ApplicationConstants.SUCCESS_PRODUCTS_COUNT );
		String noOfFail = (String) model.asMap().get(ApplicationConstants.FAILURE_PRODUCTS_COUNT );
		if (noOfSucc == null) {
			model.addAttribute(ApplicationConstants.SUCCESS_PRODUCTS_COUNT , 
					                                    ApplicationConstants.CONST_STRING_ZERO);

		}
		if (noOfFail == null) {
			model.addAttribute(ApplicationConstants.FAILURE_PRODUCTS_COUNT	, 
					                                         ApplicationConstants.CONST_STRING_ZERO);
		}
		return ApplicationConstants.CONST_STRING_SUCCESS;
	}
	
	/*@Author Venkat
	 *@Param String,String,String,RedirectAttributes
	 *@Description this method parse data(success and failed)after receiving final result from  
	 *              excel parser and  send out mail if any products are Failure         
	 * @Retrun Void
	 */
	public void parseFinalData(String result,String asiNumber ,int batchId,
			                                     RedirectAttributes redirectAttributes){
		String[] splitFinalResult = result
				.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		String noOfProductsSuccess = splitFinalResult[ApplicationConstants.CONST_NUMBER_ZERO];
		String noOfProductsFailure = splitFinalResult[ApplicationConstants.CONST_INT_VALUE_ONE];
		redirectAttributes.addFlashAttribute(ApplicationConstants.SUCCESS_PRODUCTS_COUNT ,
																				noOfProductsSuccess);
		redirectAttributes.addFlashAttribute(ApplicationConstants.FAILURE_PRODUCTS_COUNT , 
																				noOfProductsFailure);
	/*	if (!noOfProductsFailure.equals(ApplicationConstants.CONST_STRING_ZERO)) {
			
			boolean isMailSendSuccess = mailService.sendMail(asiNumber, batchId);
			if(isMailSendSuccess){
				redirectAttributes.addFlashAttribute(ApplicationConstants.CONST_STRING_SUCCESS_MSG ,
				                                             ApplicationConstants.MAIL_SEND_SUCCESS_MESSAGE);
			} 
			
		}*/
	}
	public IProductService getProductService() {
		return productService;
	}

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	public ProductDao getProductDao() {
		return productDao;
	}

	public void setProductDao(ProductDao productDao) {
		this.productDao = productDao;
	}
	public ConvertCsvToExcel getConvertCsvToExcel() {
		return convertCsvToExcel;
	}

	public void setConvertCsvToExcel(ConvertCsvToExcel convertCsvToExcel) {
		this.convertCsvToExcel = convertCsvToExcel;
	}
	public ExcelFactory getExcelFactory() {
		return excelFactory;
	}

	public void setExcelFactory(ExcelFactory excelFactory) {
		this.excelFactory = excelFactory;
	}
	public LookupData getLookupData() {
		return lookupData;
	}

	public void setLookupData(LookupData lookupData) {
		this.lookupData = lookupData;
	}

}
