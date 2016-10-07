package com.a4tech.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

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
		}
		return null;
	}
}
