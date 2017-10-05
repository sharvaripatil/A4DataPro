package com.a4tech.util;

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
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;


public class ConvertCsvToExcel {
	public static final Logger _LOGGER = Logger.getLogger(ConvertCsvToExcel.class);
	public Workbook getExcel(File file){
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
	
	/*@author Venkat
	 *@param  MultipartFile
	 *@description This method is design for convert file into workbook format,also 
	 *                                           csv file format convert into workbook
	 *@return WorkBook
	 */
	public  Workbook getWorkBook(MultipartFile mfile){
	    String fileExtension = CommonUtility.getFileExtension(mfile.getOriginalFilename());
	    File file = convertMultiPartFileIntoFile(mfile);
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
	
	/*@author Venkat
	 *@param  MultipartFile
	 *@description This method is design for convert file into workbook format,also 
	 *                                           csv file format convert into workbook
	 *@return WorkBook
	 */
	public  Workbook getWorkBook(File file){
	    String fileExtension = CommonUtility.getFileExtension(file.getName());
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
}
