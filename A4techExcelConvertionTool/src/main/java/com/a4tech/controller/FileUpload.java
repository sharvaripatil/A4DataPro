package com.a4tech.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.a4tech.ESPTemplate.product.mapping.ESPTemplateMapping;
import com.a4tech.JulyData.excelMapping.JulyDataMapping;
import com.a4tech.RFGLine.product.mapping.RFGLineProductExcelMapping;
import com.a4tech.adspec.product.mapping.AdspecProductsExcelMapping;
import com.a4tech.core.excelMapping.ExcelMapping;
import com.a4tech.core.model.FileBean;
import com.a4tech.dc.product.mapping.DCProductsExcelMapping;
import com.a4tech.kl.product.mapping.KlProductsExcelMapping;
import com.a4tech.product.bbi.mapping.BBIProductsExcelMapping;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.kuku.mapping.KukuProductsExcelMapping;
import com.a4tech.product.newproducts.mapping.NewProductsExcelMapping;
import com.a4tech.product.service.ILoginService;
import com.a4tech.product.service.IMailService;
import com.a4tech.product.service.IProductService;
import com.a4tech.sage.product.mapping.SageProductsExcelMapping;
import com.a4tech.usbProducts.excelMapping.UsbProductsExcelMapping;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.a4tech.v2.core.excelMapping.V2ExcelMapping;

@Controller
@RequestMapping({ "/", "/uploadFile.htm" })
public class FileUpload {

	@Autowired
	IProductService productService;
	@Autowired
	IMailService    mailService;
	private String accessToken = null;
	private UsbProductsExcelMapping usbExcelMapping;
	private JulyDataMapping julymapping;
	private SageProductsExcelMapping sageExcelMapping;
	private V2ExcelMapping productV2ExcelMapping;
	private ExcelMapping gbDataExcelMapping;
	private DCProductsExcelMapping dcProductExcelMapping;
	private ESPTemplateMapping espTemplateMapping;
	private KukuProductsExcelMapping kukuProductsExcelMapping;
	private KlProductsExcelMapping klMapping;
	private RFGLineProductExcelMapping rfgLineProductExcelMapping;
	private BBIProductsExcelMapping bbiProductsExcelMapping;
	private AdspecProductsExcelMapping adspecMapping;
	private NewProductsExcelMapping newProductsExcelMapping;
	private ILoginService loginService;
	private ProductDao productDao;
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
		Workbook workbook = null;

		int numOfProducts = 0;
		String asiNumber = fileBean.getAsiNumber();

		try (ByteArrayInputStream bis = new ByteArrayInputStream(fileBean
				.getFile().getBytes())) {

			String fileExtension = CommonUtility.getFileExtension(fileBean
					.getFile().getOriginalFilename());
			if (ApplicationConstants.CONST_STRING_XLS.equalsIgnoreCase(fileExtension)) {
				workbook = new HSSFWorkbook(bis);

			} else if (ApplicationConstants.CONST_STRING_XLSX.equalsIgnoreCase(fileExtension)) {
				workbook = new XSSFWorkbook(bis);
			} else {
				_LOGGER.info("Invlid upload excel file,Please try one more time");
				model.addAttribute(ApplicationConstants.CONST_STRING_INVALID_UPLOAD_FILE, 
						                                          ApplicationConstants.CONST_STRING_EMPTY);
				return ApplicationConstants.CONST_STRING_HOME;
			}
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
			int batchId = productDao.createBatchId(Integer.parseInt(asiNumber));
			request.getSession().setAttribute("batchId",
					String.valueOf(batchId));
			switch (asiNumber) {
			case "55200": // GB Data Excel Mapping
				numOfProducts = gbDataExcelMapping.readExcel(accessToken,
						workbook, Integer.valueOf(asiNumber), batchId);
				model.addAttribute(ApplicationConstants.CONST_STRING_FILE_NAME, numOfProducts);
				mailService.sendMail(asiNumber, batchId);
				model.addAttribute(ApplicationConstants.CONST_STRING_SUCCESS_MSG , 
						                                  ApplicationConstants.MAIL_SEND_SUCCESS_MESSAGE);
				return ApplicationConstants.CONST_STRING_SUCCESS;
			case "55201":// product v2
				numOfProducts = productV2ExcelMapping.readExcel(accessToken,
						workbook, Integer.valueOf(asiNumber), batchId);
				model.addAttribute(ApplicationConstants.CONST_STRING_FILE_NAME, numOfProducts);
				mailService.sendMail(asiNumber, batchId);
				model.addAttribute(ApplicationConstants.CONST_STRING_SUCCESS_MSG , 
						                                  ApplicationConstants.MAIL_SEND_SUCCESS_MESSAGE);
				return ApplicationConstants.CONST_STRING_SUCCESS;
				// break;
			case "55202":// supplier USB data(Nov_USB Products)
				finalResult = usbExcelMapping.readExcel(accessToken, workbook,
						Integer.valueOf(asiNumber), batchId);
				if (finalResult != null) {
					parseFinalData(finalResult, asiNumber, batchId, redirectAttributes);
				}
				return ApplicationConstants.CONST_REDIRECT_URL;
			case "55203": // supplier JulyData
				numOfProducts = julymapping.readExcel(accessToken, workbook,
						Integer.valueOf(asiNumber), batchId);
				model.addAttribute(ApplicationConstants.CONST_STRING_FILE_NAME, numOfProducts);
				return ApplicationConstants.CONST_STRING_SUCCESS;
			case "55204": // supplier Sage
				finalResult = sageExcelMapping.readExcel(accessToken, workbook,
						Integer.valueOf(asiNumber), batchId);
				if (finalResult != null) {
					parseFinalData(finalResult, asiNumber, batchId, redirectAttributes);
				}
				return ApplicationConstants.CONST_REDIRECT_URL;

			case "55205": // Distributor Central
				finalResult = dcProductExcelMapping.readExcel(accessToken,
						workbook, Integer.valueOf(asiNumber), batchId);
				if (finalResult != null) {
					parseFinalData(finalResult, asiNumber, batchId, redirectAttributes);
				}
				return ApplicationConstants.CONST_REDIRECT_URL;
			case "91561": // Esptemplate
				finalResult = espTemplateMapping.readExcel(accessToken,
						workbook, Integer.valueOf(asiNumber), batchId);
				if (finalResult != null) {
					parseFinalData(finalResult, asiNumber, batchId, redirectAttributes);
				}
				return ApplicationConstants.CONST_REDIRECT_URL;

			case "65851": // Kuku International
				finalResult = kukuProductsExcelMapping.readExcel(accessToken,
						workbook, Integer.valueOf(asiNumber), batchId);
				if (finalResult != null) {
					parseFinalData(finalResult, asiNumber, batchId, redirectAttributes);
				}

				return ApplicationConstants.CONST_REDIRECT_URL;
			 case "64905":  //Kinline Promos
			    	finalResult = klMapping.readExcel(accessToken, workbook, 
                                        Integer.valueOf(asiNumber), batchId);
			    	if(finalResult != null){
			    		parseFinalData(finalResult, asiNumber, batchId, redirectAttributes);
			       }
			    	return ApplicationConstants.CONST_REDIRECT_URL;
			 case "40445": // new bbi term
					finalResult = bbiProductsExcelMapping.readExcel(accessToken,
							workbook, Integer.valueOf(asiNumber), batchId);
					if (finalResult != null) {
						parseFinalData(finalResult, asiNumber, batchId, redirectAttributes);
					}

					return ApplicationConstants.CONST_REDIRECT_URL;
			 case "32125":  //Adspec 
			    	finalResult = adspecMapping.readExcel(accessToken, workbook, 
                                     Integer.valueOf(asiNumber), batchId);
			    	if(finalResult != null){
			    		parseFinalData(finalResult, asiNumber, batchId, redirectAttributes);
			       }
			    	return ApplicationConstants.CONST_REDIRECT_URL;
			    	
			 case "82283": // RFG Line
					finalResult = rfgLineProductExcelMapping.readExcel(accessToken,
							workbook, Integer.valueOf(asiNumber), batchId);
					
					if (finalResult != null) {
						parseFinalData(finalResult, asiNumber, batchId, redirectAttributes);
					}
			    	return ApplicationConstants.CONST_REDIRECT_URL;
			    	
			 case "91284":  //NewProducts
			    	finalResult = newProductsExcelMapping.readExcel(accessToken, workbook, 
                               Integer.valueOf(asiNumber), batchId);
			    	if (finalResult != null) {
						parseFinalData(finalResult, asiNumber, batchId, redirectAttributes);
					}
			    	return ApplicationConstants.CONST_REDIRECT_URL;

			default:
				break;
			}

		} catch (IOException e) {
			_LOGGER.error("Error In FileUpload: " + e.getMessage());
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
		if (!noOfProductsFailure.equals(ApplicationConstants.CONST_STRING_ZERO)) {
			
			/*boolean isMailSendSuccess = mailService.sendMail(asiNumber, batchId);
			if(isMailSendSuccess){
				redirectAttributes.addFlashAttribute(ApplicationConstants.CONST_STRING_SUCCESS_MSG ,
				                                             ApplicationConstants.MAIL_SEND_SUCCESS_MESSAGE);
			} */  // for testing purpose
			
		}
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

	public ILoginService getLoginService() {
		return loginService;
	}

	public void setLoginService(ILoginService loginService) {
		this.loginService = loginService;
	}

	public IProductService getProductService() {
		return productService;
	}

	public void setProductService(IProductService productService) {
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
	public DCProductsExcelMapping getDcProductExcelMapping() {
	return dcProductExcelMapping;
	}

	public void setDcProductExcelMapping(
		DCProductsExcelMapping dcProductExcelMapping) {
	this.dcProductExcelMapping = dcProductExcelMapping;
	}

	public KukuProductsExcelMapping getKukuProductsExcelMapping() {
		return kukuProductsExcelMapping;
	}

	public void setKukuProductsExcelMapping(
			KukuProductsExcelMapping kukuProductsExcelMapping) {
		this.kukuProductsExcelMapping = kukuProductsExcelMapping;
	}

	public ESPTemplateMapping getEspTemplateMapping() {
		return espTemplateMapping;
	}

	public void setEspTemplateMapping(ESPTemplateMapping espTemplateMapping) {
		this.espTemplateMapping = espTemplateMapping;
	}
	public KlProductsExcelMapping getKlMapping() {
		return klMapping;
	}

	public void setKlMapping(KlProductsExcelMapping klMapping) {
		this.klMapping = klMapping;
	}
	public AdspecProductsExcelMapping getAdspecMapping() {
		return adspecMapping;
	}

	public void setAdspecMapping(AdspecProductsExcelMapping adspecMapping) {
		this.adspecMapping = adspecMapping;
	}

	public BBIProductsExcelMapping getBbiProductsExcelMapping() {
		return bbiProductsExcelMapping;
	}

	public void setBbiProductsExcelMapping(
			BBIProductsExcelMapping bbiProductsExcelMapping) {
		this.bbiProductsExcelMapping = bbiProductsExcelMapping;
	}

	public RFGLineProductExcelMapping getRfgLineProductExcelMapping() {
		return rfgLineProductExcelMapping;
	}

	public void setRfgLineProductExcelMapping(
			RFGLineProductExcelMapping rfgLineProductExcelMapping) {
		this.rfgLineProductExcelMapping = rfgLineProductExcelMapping;
	}

	public NewProductsExcelMapping getNewProductsExcelMapping() {
		return newProductsExcelMapping;
	}

	public void setNewProductsExcelMapping(
			NewProductsExcelMapping newProductsExcelMapping) {
		this.newProductsExcelMapping = newProductsExcelMapping;
	}

}
