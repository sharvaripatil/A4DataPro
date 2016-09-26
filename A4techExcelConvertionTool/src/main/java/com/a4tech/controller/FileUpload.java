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
import com.a4tech.product.service.ILoginService;
import com.a4tech.product.service.IProductService;
import com.a4tech.sage.product.mapping.SageProductsExcelMapping;
import com.a4tech.usbProducts.excelMapping.UsbProductsExcelMapping;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.a4tech.v2.core.excelMapping.V2ExcelMapping;

@Controller
@RequestMapping({ "/", "/uploadFile.htm" })
public class FileUpload extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	IProductService productService;
	private static String accessToken = null;
	private UsbProductsExcelMapping usbExcelMapping;
	private JulyDataMapping julymapping;
	private SageProductsExcelMapping sageExcelMapping;
	private V2ExcelMapping productV2ExcelMapping;
	private ExcelMapping gbDataExcelMapping;
	private DownloadFileController downloadMail;
	private DCProductsExcelMapping dcProductExcelMapping;
	private ESPTemplateMapping espTemplateMapping;
	private KukuProductsExcelMapping kukuProductsExcelMapping;
	private KlProductsExcelMapping klMapping;
	private RFGLineProductExcelMapping rfgLineProductExcelMapping;
	private BBIProductsExcelMapping bbiProductsExcelMapping;
	private AdspecProductsExcelMapping adspecMapping;
	private ILoginService loginService;
	private ProductDao productDao;
	private static Logger _LOGGER = Logger.getLogger(Class.class);
	
	@RequestMapping(method = RequestMethod.GET)
	public String welcomePage(Map<String, Object> model) {
		FileBean fileBean = new FileBean();
		model.put("filebean", fileBean);
		return "home";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String fileUpload(
			@ModelAttribute("filebean") @Valid FileBean fileBean,
			BindingResult result, final RedirectAttributes redirectAttributes,
			Model model, HttpServletRequest request) {
		_LOGGER.info("Enter Controller Class");
		String finalResult = null;
		Workbook workbook = null;

		int numOfProducts = 0;
		String emailMsg = "Email has been sent Successfully !!!";
		String noOfProductsSuccess = null;
		String noOfProductsFailure = null;
		String[] splitFinalResult;
		String asiNumber = fileBean.getAsiNumber();
		if (result.hasErrors()) {
			return "home";
		}

		try (ByteArrayInputStream bis = new ByteArrayInputStream(fileBean
				.getFile().getBytes())) {

			String fileExtension = CommonUtility.getFileExtension(fileBean
					.getFile().getOriginalFilename());
			if (fileExtension.equalsIgnoreCase("xls")) {
				workbook = new HSSFWorkbook(bis);

			} else if (fileExtension.equalsIgnoreCase("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				_LOGGER.info("Invlid upload excel file,Please try one more time");
				model.addAttribute("invalidUploadFile", "");
				return "home";
			}
			accessToken = loginService.doLogin(fileBean.getAsiNumber(),
					fileBean.getUserName(), fileBean.getPassword());
			if (accessToken != null) {
				if (accessToken.equalsIgnoreCase("unAuthorized")) {
					accessToken = null;
					model.addAttribute("invalidDetails", "");
					return "home";
				}
			} else {
				return "errorPage";
			}
			int batchId = productDao.createBatchId(Integer.parseInt(asiNumber));
			request.getSession().setAttribute("batchId",
					String.valueOf(batchId));
			switch (asiNumber) {
			case "55200": // GB Data Excel Mapping
				numOfProducts = gbDataExcelMapping.readExcel(accessToken,
						workbook, Integer.valueOf(asiNumber), batchId);
				model.addAttribute("fileName", numOfProducts);
				downloadMail.sendMail(asiNumber, batchId);
				model.addAttribute("successmsg", emailMsg);
				return "success";
			case "55201":// product v2
				numOfProducts = productV2ExcelMapping.readExcel(accessToken,
						workbook, Integer.valueOf(asiNumber), batchId);
				model.addAttribute("fileName", numOfProducts);
				downloadMail.sendMail(asiNumber, batchId);
				model.addAttribute("successmsg", emailMsg);
				return "success";
				// break;
			case "55202":// supplier USB data(Nov_USB Products)
				finalResult = usbExcelMapping.readExcel(accessToken, workbook,
						Integer.valueOf(asiNumber), batchId);
				if (finalResult != null) {
					splitFinalResult = finalResult
							.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					noOfProductsSuccess = splitFinalResult[0];
					noOfProductsFailure = splitFinalResult[1];
					redirectAttributes.addFlashAttribute(
							"successProductsCount", noOfProductsSuccess);
					redirectAttributes.addFlashAttribute(
							"failureProductsCount", noOfProductsFailure);
					if (!noOfProductsFailure
							.equals(ApplicationConstants.CONST_STRING_ZERO)) {
						redirectAttributes.addFlashAttribute("successmsg",
								emailMsg);
						downloadMail.sendMail(asiNumber, batchId);
					}
				}
				return "redirect:redirect.htm";
			case "55203": // supplier JulyData
				numOfProducts = julymapping.readExcel(accessToken, workbook,
						Integer.valueOf(asiNumber), batchId);
				model.addAttribute("fileName", numOfProducts);
				return "success";
			case "55204": // supplier Sage
				finalResult = sageExcelMapping.readExcel(accessToken, workbook,
						Integer.valueOf(asiNumber), batchId);
				if (finalResult != null) {
					splitFinalResult = finalResult
							.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					noOfProductsSuccess = splitFinalResult[0];
					noOfProductsFailure = splitFinalResult[1];
					redirectAttributes.addFlashAttribute(
							"successProductsCount", noOfProductsSuccess);
					redirectAttributes.addFlashAttribute(
							"failureProductsCount", noOfProductsFailure);
					if (!noOfProductsFailure
							.equals(ApplicationConstants.CONST_STRING_ZERO)) {
						redirectAttributes.addFlashAttribute("successmsg",
								emailMsg);
						downloadMail.sendMail(asiNumber, batchId);
					}
				}
				return "redirect:redirect.htm";

			case "55205": // Distributor Central
				finalResult = dcProductExcelMapping.readExcel(accessToken,
						workbook, Integer.valueOf(asiNumber), batchId);
				if (finalResult != null) {
					splitFinalResult = finalResult
							.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					noOfProductsSuccess = splitFinalResult[0];
					noOfProductsFailure = splitFinalResult[1];
					redirectAttributes.addFlashAttribute(
							"successProductsCount", noOfProductsSuccess);
					redirectAttributes.addFlashAttribute(
							"failureProductsCount", noOfProductsFailure);
					if (!noOfProductsFailure
							.equals(ApplicationConstants.CONST_STRING_ZERO)) {
						redirectAttributes.addFlashAttribute("successmsg",
								emailMsg);
						downloadMail.sendMail(asiNumber, batchId);
					}
				}
				return "redirect:redirect.htm";
			case "91561": // Esptemplate
				finalResult = espTemplateMapping.readExcel(accessToken,
						workbook, Integer.valueOf(asiNumber), batchId);
				if (finalResult != null) {
					splitFinalResult = finalResult
							.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					noOfProductsSuccess = splitFinalResult[0];
					noOfProductsFailure = splitFinalResult[1];
					redirectAttributes.addFlashAttribute(
							"successProductsCount", noOfProductsSuccess);
					redirectAttributes.addFlashAttribute(
							"failureProductsCount", noOfProductsFailure);
					if (!noOfProductsFailure
							.equals(ApplicationConstants.CONST_STRING_ZERO)) {
						redirectAttributes.addFlashAttribute("successmsg",
								emailMsg);
						downloadMail.sendMail(asiNumber, batchId);
					}
				}
				return "redirect:redirect.htm";

			case "65851": // Kuku International
				finalResult = kukuProductsExcelMapping.readExcel(accessToken,
						workbook, Integer.valueOf(asiNumber), batchId);
				if (finalResult != null) {
					splitFinalResult = finalResult
							.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					noOfProductsSuccess = splitFinalResult[0];
					noOfProductsFailure = splitFinalResult[1];
					redirectAttributes.addFlashAttribute(
							"successProductsCount", noOfProductsSuccess);
					redirectAttributes.addFlashAttribute(
							"failureProductsCount", noOfProductsFailure);
					if (!noOfProductsFailure
							.equals(ApplicationConstants.CONST_STRING_ZERO)) {
						redirectAttributes.addFlashAttribute("successmsg",
								emailMsg);
						downloadMail.sendMail(asiNumber, batchId);
					}
				}

				return "redirect:redirect.htm";
			 case "64905":  //Kinline Promos
			    	finalResult = klMapping.readExcel(accessToken, workbook, 
                                        Integer.valueOf(asiNumber), batchId);
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
			 case "40445": // new bbi term
					finalResult = bbiProductsExcelMapping.readExcel(accessToken,
							workbook, Integer.valueOf(asiNumber), batchId);
					if (finalResult != null) {
						splitFinalResult = finalResult
								.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
						noOfProductsSuccess = splitFinalResult[0];
						noOfProductsFailure = splitFinalResult[1];
						redirectAttributes.addFlashAttribute(
								"successProductsCount", noOfProductsSuccess);
						redirectAttributes.addFlashAttribute(
								"failureProductsCount", noOfProductsFailure);
						if (!noOfProductsFailure
								.equals(ApplicationConstants.CONST_STRING_ZERO)) {
							redirectAttributes.addFlashAttribute("successmsg",
									emailMsg);
							downloadMail.sendMail(asiNumber, batchId);
						}
					}

					return "redirect:redirect.htm";
			 case "32125":  //Adspec 
			    	finalResult = adspecMapping.readExcel(accessToken, workbook, 
                                     Integer.valueOf(asiNumber), batchId);
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
			    	
			 case "82283": // new bbi term
					finalResult = rfgLineProductExcelMapping.readExcel(accessToken,
							workbook, Integer.valueOf(asiNumber), batchId);
					if (finalResult != null) {
						splitFinalResult = finalResult
								.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
						noOfProductsSuccess = splitFinalResult[0];
						noOfProductsFailure = splitFinalResult[1];
						redirectAttributes.addFlashAttribute(
								"successProductsCount", noOfProductsSuccess);
						redirectAttributes.addFlashAttribute(
								"failureProductsCount", noOfProductsFailure);
						if (!noOfProductsFailure
								.equals(ApplicationConstants.CONST_STRING_ZERO)) {
							redirectAttributes.addFlashAttribute("successmsg",
									emailMsg);
							downloadMail.sendMail(asiNumber, batchId);
						}
					}
			    	return "redirect:redirect.htm";

			default:
				break;
			}

		} catch (IOException e) {
			_LOGGER.error("Error In FileUpload: " + e.getMessage());
		} catch (Exception e) {
			_LOGGER.error("Error In FileUpload: " + e.getMessage());
		}
		return "home";
	}
	@RequestMapping(value = "/redirect.htm", method = RequestMethod.GET)
	public String submit(Model model) {
		String noOfSucc = (String) model.asMap().get("successProductsCount");
		String noOfFail = (String) model.asMap().get("failureProductsCount");
		if (noOfSucc == null) {
			model.addAttribute("successProductsCount", "0");

		}
		if (noOfFail == null) {
			model.addAttribute("failureProductsCount", "0");
		}
		return "success";
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

	public DownloadFileController getDownloadMail() {
		return downloadMail;
	}

	public void setDownloadMail(DownloadFileController downloadMail) {
		this.downloadMail = downloadMail;
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



}
