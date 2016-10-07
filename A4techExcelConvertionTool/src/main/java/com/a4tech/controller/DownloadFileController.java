package com.a4tech.controller;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
	public String gerErrorLogFile(HttpServletRequest request,
			HttpServletResponse response,Model model) throws ServletException, IOException {
		response.setContentType("text/html");
		String batchId=(String) request.getSession().getAttribute("batchId"); 
		String fileName= batchId+".txt";
		  response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ fileName + "\"");
			String finalFilePath = ApplicationConstants.CONST_STRING_DOWNLOAD_FILE_PATH+fileName;
			try(PrintWriter out = response.getWriter()){
			List<String> fileLine = Files.readAllLines(Paths.get(finalFilePath));
				for (String line : fileLine) {
					out.print(line);
				}
			}catch (FileNotFoundException e) {
				_LOGGER.error("Error log file is not available:"+e);
			}
        return "success";    
			
	}
}
