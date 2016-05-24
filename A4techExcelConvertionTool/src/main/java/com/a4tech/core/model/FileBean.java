package com.a4tech.core.model;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class FileBean {
	//@NotNull(message="File should not be empty")
	MultipartFile file ;
	
	//CommonsMultipartFile files;

	/*public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}*/

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}
	

}
