package com.a4tech.ftp;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

import com.a4tech.core.excelMapping.ExcelFactory;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.a4tech.util.ConvertCsvToExcel;

public class FilesParsing {
	private ProductDao productDao;
	private ExcelFactory excelFactory;
	private ConvertCsvToExcel convertCsvToExcel;
	
	private static final Logger _LOGGER = Logger.getLogger(FilesParsing.class);

	public void ReadFtpFiles(File[] listOfFiles) {
		//_LOGGER.info("Enter ftp file read parser");
		for (File file : listOfFiles) {
			String fileName = file.getName();
			String asiNumber = getAsiNumberFile(fileName);
			//String asiNumber = "";// get Asi Number from fileName (e.g.
									// 45907_appara.csv)
			Workbook workBook = null;
			boolean fileStatus = isFileProcess(fileName, asiNumber);
			if (fileStatus) {
				_LOGGER.info(fileName +" :"+ "file already processed");
				continue;
			}
			//IExcelParser excelParserImpl = excelFactory.getExcelParserObject(fileName);
			 String fileExtension = CommonUtility.getFileExtension(file.getName());
			 workBook = convertCsvToExcel.getWorkBook(file);
			 int batchId = productDao.createBatchId(Integer.parseInt(asiNumber));
			/* if(workBook != null){
				 excelParserImpl.readExcel("", workBook, Integer.parseInt(asiNumber), batchId);
			 }*/
            
			productDao.updateFtpFileStatus(fileName, asiNumber,
					ApplicationConstants.CONST_STRING_YES);
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
    private String getAsiNumberFile(String fileName){
    	String[] names = fileName.split("_");
    	return names[0];
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
