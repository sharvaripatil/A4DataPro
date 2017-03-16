package com.a4tech.ftp.model;

import org.springframework.web.multipart.MultipartFile;

public class FtpFileUploadBean {
	private MultipartFile file ;

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}
}
