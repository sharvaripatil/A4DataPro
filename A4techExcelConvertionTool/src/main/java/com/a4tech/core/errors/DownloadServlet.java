package com.a4tech.core.errors;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.a4tech.core.model.FileBean;

@Controller
public class DownloadServlet  {
	private static final long serialVersionUID = 1L;

	@RequestMapping("/downloadServlet.htm")
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String filename=(String) request.getSession().getAttribute("asiNumber");
		 filename = filename+".txt";
		String filepath = "D:\\A4 ESPUpdate\\ErrorFiles\\";
		response.setContentType("APPLICATION/OCTET-STREAM");
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ filename + "\"");
		FileInputStream fileInputStream = new FileInputStream(filepath
				+ filename);

		int i;
		while ((i = fileInputStream.read()) != -1) {
			out.write(i);
		}
		fileInputStream.close();
		out.close();
	}
}