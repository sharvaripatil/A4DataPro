package com.a4tech.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.a4tech.ftp.model.FtpFileUploadBean;
import com.a4tech.ftp.model.FtpLoginBean;
import com.a4tech.ftp.service.FtpService;
import com.a4tech.product.service.ILoginService;
import com.a4tech.product.service.IProductDao;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.a4tech.util.ConvertCsvToExcel;
import com.opencsv.CSVReader;

@RestController
public class FtpController {
	@Autowired
	private ILoginService loginService;
	@Autowired
	private FtpService ftpServices;
	@Autowired
	private IProductDao productDao;
	private static Logger _LOGGER = Logger.getLogger(FtpController.class);
	@RequestMapping(value="ftpLogin")
	public ModelAndView welcomeFtpLogin(){
		
		return new ModelAndView("ftpLogin", "ftpLoginBean", new FtpLoginBean());	
	}
	
	@RequestMapping(value="checkLoginDetails")
	public ModelAndView fileUpload(@ModelAttribute("ftpLoginBean") FtpLoginBean ftpLogin,Model model){
		FtpFileUploadBean ftpFileUploadBean = new FtpFileUploadBean();
		_LOGGER.info("Enter FTP file upload Process controller");
		
		String accessToken = "";
		accessToken = loginService.doLogin(ftpLogin.getAsiNumber(), ftpLogin.getUserName(), ftpLogin.getPassword(), ftpLogin.getEnvironemtType());
		if (accessToken != null) {
			if (ApplicationConstants.CONST_STRING_UN_AUTHORIZED.equals(accessToken)) {
				accessToken = null;
				model.addAttribute(ApplicationConstants.CONST_STRING_INVALID_DETAILS, 
						                                     ApplicationConstants.CONST_STRING_EMPTY);
				return new ModelAndView("ftpLogin", "ftpLoginBean", new FtpLoginBean());	
			}
		if(productDao.isASINumberAvailable(ftpLogin.getAsiNumber(),ftpLogin.getEnvironemtType())){//if true means there is no cridentials in DB ,
			                                                          //then we need to save our DB
			productDao.saveSupplierCridentials(ftpLogin);
		}
		} else {
			return new ModelAndView("ftpLogin", "ftpLoginBean", new FtpLoginBean());	
		}
		//return "ftpLogin";
		ftpFileUploadBean.setAsiNumber(ftpLogin.getAsiNumber());
		ftpFileUploadBean.setEnvironmentType(ftpLogin.getEnvironemtType());
		return new ModelAndView("fileUpload", "ftpFileUploadBean", ftpFileUploadBean);	
	}
	@RequestMapping(value="/uploadFile")
	public ModelAndView processFile(@ModelAttribute("ftpFileUploadBean") FtpFileUploadBean fileUploadBean,Model model){
		_LOGGER.info("Enter FTP file upload Process controller");
		String accessToken = "";
		String asiNumber = fileUploadBean.getAsiNumber();
		String environmentType = fileUploadBean.getEnvironmentType();
		MultipartFile   mfile= fileUploadBean.getFile();
		File file = convertMultiPartFileIntoFile(mfile);
		long fileSize = file.length(); 
		if(fileSize == 0){
			model.addAttribute("invalidFile", "");
			FtpFileUploadBean ftpFileUploadBean = new FtpFileUploadBean();
			ftpFileUploadBean.setAsiNumber(asiNumber);
			ftpFileUploadBean.setEnvironmentType(environmentType);
			return new ModelAndView("fileUpload", "ftpFileUploadBean", ftpFileUploadBean);
		}
		if(StringUtils.isEmpty(asiNumber)){
			model.addAttribute("invalidAsiNum", "");
			return new ModelAndView("fileUpload", "ftpFileUploadBean", new FtpFileUploadBean());
		}
		Workbook wb = getWorkBook(file);
		int noOfColumnsInCurrentFile = getSupplierFileColumnsCount(wb);
		int noOfColumnsFromDataBase = productDao.getSupplierColumnsCount(asiNumber);
		//This is checking supplier file columns is same or not
		if(noOfColumnsInCurrentFile != noOfColumnsFromDataBase){
			model.addAttribute("misMatchCoumns", "misMatchCoumns");
			FtpFileUploadBean ftpFileUploadBean = new FtpFileUploadBean();
			ftpFileUploadBean.setAsiNumber(asiNumber);
			ftpFileUploadBean.setEnvironmentType(environmentType);
			return new ModelAndView("fileUpload", "ftpFileUploadBean", ftpFileUploadBean);
		}
		if (accessToken != null) {
			if (ApplicationConstants.CONST_STRING_UN_AUTHORIZED.equals(accessToken)) {
				accessToken = null;
				model.addAttribute(ApplicationConstants.CONST_STRING_INVALID_DETAILS, 
						                                     ApplicationConstants.CONST_STRING_EMPTY);
				return new ModelAndView(ApplicationConstants.CONST_STRING_HOME);
			}
		} 
	   boolean fileStaus = ftpServices.uploadFile(file,asiNumber,environmentType);
	   if(fileStaus == true){
		   model.addAttribute("ftpMessage", "success");
	   } else{
		   model.addAttribute("ftpMessage", "failure");
	   }
		return new ModelAndView("fileUpload", "ftpFileUploadBean", new FtpFileUploadBean());	
	}
	/*
	 * @author: Venkat
	 * @description : this method used for count number of columns in current supplier file
	 */
 private int getSupplierFileColumnsCount(Workbook workbBook){
	Sheet sheet = workbBook.getSheetAt(0);
	Row headerRow = sheet.getRow(0);
	return headerRow.getLastCellNum();
 }
 private  Workbook getWorkBook(File file){
	    String fileExtension = CommonUtility.getFileExtension(file.getName());
	    ZipSecureFile.setMinInflateRatio(0.001d);
	   // File file = convertMultiPartFileIntoFile(mfile);
	    Workbook workBook = null;
	    if(ApplicationConstants.CONST_STRING_XLS.equalsIgnoreCase(fileExtension)){
	    	try(Workbook workbook1 = new HSSFWorkbook(new FileInputStream(file))) {
				return workbook1;
			} catch (IOException e) {
				_LOGGER.error("unable to file convert into excelsheet"+e);
			}
	     }else if(ApplicationConstants.CONST_STRING_XLSX.equalsIgnoreCase(fileExtension)){
	    	try(Workbook workBook2 = new XSSFWorkbook(file)) {
	    		return workBook2;
			} catch (InvalidFormatException | IOException e) {
				_LOGGER.error("unable to file convert into excelsheet"+e);
			}
	    }else if(ApplicationConstants.CONST_STRING_CSV.equalsIgnoreCase(fileExtension)){
	    	workBook = getExcel(file);
	    	return workBook;
	    }else{
	    	
	    }
		return workBook;
	}
 private File convertMultiPartFileIntoFile(MultipartFile mfile){
		File file = null;
		file = new File(mfile.getOriginalFilename());
		try {
			mfile.transferTo(file);
		} catch (IllegalStateException | IOException e) {
			_LOGGER.error("unable to convert MultiPartFile into File format : "+e);
		}
		
		return file;
	}
 private Workbook getExcel(File file){
	    _LOGGER.info("Enter excel convertor class");
			try (Workbook excel = new HSSFWorkbook();
					CSVReader reader = new CSVReader(new FileReader(file))) {
				String[] nextLine;
				Sheet sheet = excel.createSheet("Data");
				long starTime = Calendar.getInstance().getTimeInMillis();
				int rownum = 0;
				for (; (nextLine = reader.readNext()) != null; rownum++) {
					Row row = sheet.createRow(rownum);
					for (int cellNum = 0; cellNum < nextLine.length; cellNum++) {
						Cell cell = row.createCell(cellNum);
						cell.setCellValue(nextLine[cellNum]);
					}
				}
				long endTime = Calendar.getInstance().getTimeInMillis();
				_LOGGER.info("Total time taken for complate excel convertion: "+(endTime - starTime));
				 _LOGGER.info("completed csv into convertor excel class");
				return excel;
			} catch (FileNotFoundException e) {
				_LOGGER.error("File is not available:: " + e);
			} catch (IOException e) {
				_LOGGER.error("unable to convert Csv into excel: " + e);
			}
			return null;
     }
}
