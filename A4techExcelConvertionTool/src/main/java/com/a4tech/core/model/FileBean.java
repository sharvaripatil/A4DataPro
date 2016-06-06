package com.a4tech.core.model;

import org.springframework.web.multipart.MultipartFile;

public class FileBean {
	
	private MultipartFile file ;
	private String asiNumber;
	private String userName;
	private String password;
	

	public String getAsiNumber() {
		return asiNumber;
	}

	public void setAsiNumber(String asiNumber) {
		this.asiNumber = asiNumber;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}
	

}
