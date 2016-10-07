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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.a4tech.util.ApplicationConstants;

@Controller
@RequestMapping("/downloadFile")
public class DownloadFileController {
	
	private static Logger _LOGGER = Logger.getLogger(DownloadFileController.class);
	
	@RequestMapping(method = RequestMethod.GET)
	public String doSendEmail(HttpServletRequest request,
			HttpServletResponse response,Model model) throws ServletException {
		response.setContentType("text/html");
		String batchId=(String) request.getSession().getAttribute("batchId"); 
		String fileName= batchId+".txt";
		  response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ fileName + "\"");
			int lineNum;
			try(PrintWriter out = response.getWriter();
					FileInputStream fileInputStream = new FileInputStream(ApplicationConstants.CONST_STRING_DOWNLOAD_FILE_PATH
					+ fileName)){
				while ((lineNum = fileInputStream.read()) != ApplicationConstants.CONST_NEGATIVE_NUMBER_ONE) {
					out.write(lineNum);
				}
			}catch (FileNotFoundException e) {
				_LOGGER.error("Error log file is not available:"+e.getMessage());
			} catch (IOException ex) {
				_LOGGER.error("Error log file is not available:"+ex.getMessage());
			}
        return "success";    
			
	}
}
