package com.a4tech.ftp.model;

import org.springframework.web.multipart.MultipartFile;

public class FtpFileUploadBean {
	private MultipartFile file ;
	private String asiNumber;

	public String getAsiNumber() {
		return asiNumber;
	}

	public void setAsiNumber(String asiNumber) {
		this.asiNumber = asiNumber;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}
}
