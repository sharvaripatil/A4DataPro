package com.a4tech.ftp.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import com.a4tech.ftp.service.FtpService;

public class FtpServiceImpl implements FtpService{
  private FTPClient ftpClient ;
  private String serveraddress ;
  private String username  ;
  private String password  ;
  private String portNo  ;
 
  private Logger _LOGGER = Logger.getLogger(FtpServiceImpl.class);
	@Override
	public boolean uploadFile(MultipartFile mFile ,String asiNumber) {
		_LOGGER.info("Enter the Upload file class");
		try {
			ftpClient.connect(serveraddress,Integer.parseInt(portNo));
			if(!ftpClient.login(username, password)){
				ftpClient.logout();
			}else{
			}
			int reply = ftpClient.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)){
				ftpClient.disconnect();
			}
			//ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			InputStream inputStream =  new BufferedInputStream(mFile.getInputStream());
			String fileName = asiNumber + "_"+mFile.getOriginalFilename();
			boolean fileStatus = ftpClient.storeFile(fileName, inputStream);
			inputStream.close();
			return fileStatus;
		} catch (IOException exe) {
			_LOGGER.error("unable to save file in Ftp Server: "+exe.getMessage());
		}catch (Exception exe) {
			_LOGGER.error("unable to save file in Ftp Server: "+exe.getMessage());
		}
		
		return false;
	}
	@Override
	public void downloadFiles() {
		// TODO Auto-generated method stub
		
	}
	public FTPClient getFtpClient() {
		return ftpClient;
	}

	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}
	public String getServeraddress() {
		return serveraddress;
	}

	public void setServeraddress(String serveraddress) {
		this.serveraddress = serveraddress;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getPortNo() {
		return portNo;
	}

	public void setPortNo(String portNo) {
		this.portNo = portNo;
	}


}
