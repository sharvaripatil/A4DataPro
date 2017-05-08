package com.a4tech.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import com.a4tech.core.excelMapping.ExcelFactory;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.entity.SupplierLoginDetails;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.service.ILoginService;
import com.a4tech.product.service.IMailService;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.ConvertCsvToExcel;

public class FilesParsing {
	private ProductDao productDao;
	private ExcelFactory excelFactory;
	private ConvertCsvToExcel convertCsvToExcel;
	@Autowired
	private ILoginService loginService;
	@Autowired
	private IMailService mailService;
	private static final Logger _LOGGER = Logger.getLogger(FilesParsing.class);

	public void ReadFtpFiles(File[] listOfFiles) {
		_LOGGER.info("Enter ftp file parser");
		for (File file : listOfFiles) {
			String fileName = file.getName();
			String asiNumber = getAsiNumberFile(fileName);
			Workbook workBook = null;
			boolean fileStatus = isFileProcess(fileName, asiNumber);
			if (fileStatus) {
				_LOGGER.info(fileName +" :"+ "file already processed");
				continue;
			}
			String accessToken = getAccessToken(asiNumber);
			if( accessToken == null){
				invalidSupplierDetails(asiNumber);
				continue;
			}
			//This is used to pause mins before processing first file
			try {
				Thread.sleep(1200);
			} catch (InterruptedException exce) {
				_LOGGER.error("Interrupted Sleep method: "+exce.getMessage());
			}
			 IExcelParser excelParserImpl = excelFactory.getExcelParserObject(asiNumber);
			// workBook = convertCsvToExcel.getWorkBook(file);
			 FileInputStream inputStream;
			try {
				inputStream = new FileInputStream(new File(file.getPath()));
				workBook = new XSSFWorkbook(inputStream);
			} catch (FileNotFoundException e) {
				_LOGGER.error("Ftp file is not available in machine: "+e.getMessage());
			} catch (IOException e) {
				
				_LOGGER.error("Ftp file is not available in machine: "+e.getMessage());
			}
			 int batchId = productDao.createBatchId(Integer.parseInt(asiNumber));
			 if(workBook != null){
				 processFileStatusMail(asiNumber, "ProcessStart", batchId);
				 excelParserImpl.readExcel(accessToken, workBook, Integer.parseInt(asiNumber), batchId);
				 productDao.updateFtpFileStatus(fileName, asiNumber,
							ApplicationConstants.CONST_STRING_YES);
				 processFileStatusMail(asiNumber, "ProcessEnd", batchId);
			 }	
			_LOGGER.info(fileName +":"+ "file parsing completed");
		}
	}

	public boolean isFileProcess(String fileName, String asiNumber) {
		String status = productDao.getFtpFileProcessStatus(fileName, asiNumber);
		if (status != null) {
			if ((ApplicationConstants.CONST_STRING_YES).equals(status)) {
				return ApplicationConstants.CONST_BOOLEAN_TRUE;
			}
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	
	public String getAccessToken(String asiNumber){
		   SupplierLoginDetails loginDetails = productDao.getSupplierLoginDetails(asiNumber);
		String accessToken = loginService.doLogin(loginDetails.getAsiNumber(),
				loginDetails.getUserName(), loginDetails.getPassword());
		return accessToken;
	}
    private String getAsiNumberFile(String fileName){
    	String[] names = fileName.split(ApplicationConstants.CONST_DELIMITER_UNDER_SCORE);
    	return names[0];
    }
    private void invalidSupplierDetails(String supplierNo){
 
    	String subject = supplierNo + " "+ "Supplier Details Are Invalid";
    	String body = "Dear Team,"
    			      +"\n \n"+supplierNo+" "+ "supplier login details are Invalid/Expired"
    			      +"\n\nPlease contact ASI team for update login details"
    			      + "\n\n\n\n\n"
    		            +"Thanks and Regards,"
    		  			+"\nA4Tech Team";
    	mailService.supplierLoginFailureMail(supplierNo, body, subject);
    }
    private void processFileStatusMail(String supplierNo,String type,int batchNo){
    	String subject = "";;
    	String body = "";
    	if(type.equals("ProcessStart")){
    		subject = supplierNo +" "+ "File Processing Start";
    		body = "Dear Team,"
  			      +"\n \n"+supplierNo+" "+ "File processing Start"
  			      +"\n\n You will get separate mail once Process completed";
    		mailService.fileProcessStart(body, subject);
    	}else if(type.equals("ProcessEnd")){
    		subject = supplierNo +" "+ "File Process completed";
    		body = "Dear Team,"
  			      +"\n \n"+supplierNo+" "+ "File process completed"
  			      +"\n\nKindly find the attached " +batchNo +".txt Product Error File"
  			+ "\n\n\n\n"
            +"Thanks and Regards,"
  			+"\nA4Tech Team"
  			+"\n\n"
    		+"Note: This is Computer Generated Mail. No need to reply.*";
    		mailService.fileProcessCompleted(body, subject, batchNo);
    	}else{
    		
    	}	
    }
	public ProductDao getProductDao() {
		return productDao;
	}

	public void setProductDao(ProductDao productDao) {
		this.productDao = productDao;
	}

	public ExcelFactory getExcelFactory() {
		return excelFactory;
	}

	public void setExcelFactory(ExcelFactory excelFactory) {
		this.excelFactory = excelFactory;
	}
	public ConvertCsvToExcel getConvertCsvToExcel() {
		return convertCsvToExcel;
	}

	public void setConvertCsvToExcel(ConvertCsvToExcel convertCsvToExcel) {
		this.convertCsvToExcel = convertCsvToExcel;
	}

}
