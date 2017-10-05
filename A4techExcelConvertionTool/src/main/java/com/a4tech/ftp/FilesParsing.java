package com.a4tech.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.a4tech.core.excelMapping.ExcelFactory;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.entity.SupplierLoginDetails;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.service.ILoginService;
import com.a4tech.product.service.IMailService;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.a4tech.util.ConvertCsvToExcel;
import com.opencsv.CSVReader;

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
			String fileName = "";
			try{
			 fileName = file.getName();
			String asiNumber = getAsiNumberFile(fileName);
			Workbook workBook = null;
			/*boolean fileStatus = isFileProcess(fileName, asiNumber);
			if (fileStatus) {
				_LOGGER.info(fileName +" :"+ "file already processed");
				continue;
			}*/
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
				String fileExtension = CommonUtility.getFileExtension(file.getPath());
				if (ApplicationConstants.CONST_STRING_XLS.equalsIgnoreCase(fileExtension)
						|| ApplicationConstants.CONST_STRING_XLSX.equalsIgnoreCase(fileExtension)) {
					workBook = getWorkBook(inputStream, fileExtension);	
				} else if(ApplicationConstants.CONST_STRING_CSV.equalsIgnoreCase(fileExtension)){
					workBook = getcsvIntoWorkbook(file.getPath());
				}
				//workBook = new XSSFWorkbook(inputStream);
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
           }catch (Exception exce) {
			_LOGGER.error("Unable to process supplier file: "+fileName);
			mailService.fileProcessFail(fileName);
		}
	 }// end for llop
		
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
		   if(loginDetails != null){
			   String accessToken = loginService.doLogin(loginDetails.getAsiNumber(),
						loginDetails.getUserName(), loginDetails.getPassword());
			   return accessToken;
		   } else {
			   return null;
		   }
	}
    private String getAsiNumberFile(String fileName){
    	String[] names = fileName.split(ApplicationConstants.CONST_DELIMITER_UNDER_SCORE);
    	return names[0];
    }
    private void invalidSupplierDetails(String supplierNo){
 
    	String subject = supplierNo + " "+ "Supplier Details Are Invalid/Not Available";
    	String body = "Dear Team,"
    			      +"\n \n"+supplierNo+" "+ "supplier login details are Invalid/Expired/Not Available"
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
  			      +"\n\n You will get separate mail once Process completed"+
  			     "\n\n\n\n"
  	            +"Thanks and Regards,"
  	  			+"\nA4Tech Team"
  	  			+"\n\n"
  	    		+"Note: This is Computer Generated Mail. No need to reply.*";
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
    
    /*@author Venkat
	 *@param  MultipartFile
	 *@description This method is design for convert file into workbook format,also 
	 *                                           csv file format convert into workbook
	 *@return WorkBook
	 */
	public  Workbook getWorkBook(FileInputStream inputStream, String fileExtension){
	    //String fileExtension = CommonUtility.getFileExtension(mfile.getOriginalFilename());
	   // File file = convertMultiPartFileIntoFile(mfile);
	    Workbook workBook = null;
	    if(ApplicationConstants.CONST_STRING_XLS.equalsIgnoreCase(fileExtension)){
	    	try(Workbook workbook1 = new HSSFWorkbook(inputStream)) {
				return workbook1;
			} catch (IOException e) {
				_LOGGER.error("unable to file convert into HSSFWorkbook: "+e);
			}
	     }else if(ApplicationConstants.CONST_STRING_XLSX.equalsIgnoreCase(fileExtension)){
	    	try(Workbook workBook2 = new XSSFWorkbook(inputStream)) {
	    		return workBook2;
			} catch (IOException e) {
				_LOGGER.error("unable to file convert into XSSFWorkbook: "+e);
			}
	    }/*else if(ApplicationConstants.CONST_STRING_CSV.equalsIgnoreCase(fileExtension)){
	    	workBook = getExcel(file);
	    	return workBook;
	    }*/else{
	    	
	    }
		return workBook;
	}
	public Workbook getcsvIntoWorkbook(String  pathName){
	    _LOGGER.info("Enter excel convertor class");
			try (Workbook excel = new HSSFWorkbook();
					CSVReader reader = new CSVReader(new FileReader(new File(pathName)))) {
				String[] nextLine;
				Sheet sheet = excel.createSheet("Data");
				long starTime = Calendar.getInstance().getTimeInMillis();
				int rownum = 0;
				for (; (nextLine = reader.readNext()) != null; rownum++) {
					Row row = sheet.createRow(rownum);
					for (int cellNum = 0; cellNum < 38; cellNum++) {
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
			} catch (Exception e) {
				_LOGGER.error("unable to convert Csv into excel: " + e);
			}
			return null;
		}
	/*@author Venkat
	 *@param MultipartFile
	 *@description This method used for converting MultiPartFile format into File format
	 *@return  File format
	 */
	public File convertMultiPartFileIntoFile(MultipartFile mfile){
		File file = null;
		file = new File(mfile.getOriginalFilename());
		try {
			mfile.transferTo(file);
		} catch (IllegalStateException | IOException e) {
			_LOGGER.error("unable to convert MultiPartFile into File format : "+e);
		}
		
		return file;
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
