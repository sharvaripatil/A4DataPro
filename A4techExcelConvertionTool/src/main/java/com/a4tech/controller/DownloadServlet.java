package com.a4tech.controller;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.a4tech.util.ApplicationConstants;

@Controller
public class DownloadServlet  {
	
	private static Logger _LOGGER = Logger.getLogger(DownloadServlet.class);
	
	@RequestMapping("/downloadServlet.htm")
	protected void getDownloadFile(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		String filename=(String) request.getSession().getAttribute("asiNumber");
		filename = filename+".txt";
		response.setContentType("APPLICATION/OCTET-STREAM");
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ filename + "\"");
		int lineNum;
		try(PrintWriter out = response.getWriter();
				FileInputStream fileInputStream = new FileInputStream(ApplicationConstants.CONST_STRING_DOWNLOAD_FILE_PATH
				+ filename)){
			while ((lineNum = fileInputStream.read()) != ApplicationConstants.CONST_NEGATIVE_NUMBER_ONE) {
				out.write(lineNum);
			}
		}catch (FileNotFoundException e) {
			_LOGGER.fatal("Error log file is not available");
		}
	}
}