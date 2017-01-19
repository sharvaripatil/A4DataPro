package com.a4tech.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

public class FtpDownloadFiles {
	private String serveraddress ;
	private String username  ;
	private String password  ;
	private FilesParsing fileParsing;
	FTPClient fClient = null;
	private static Logger _LOGGER = Logger.getLogger(FtpDownloadFiles.class);
	public void getFtpDownloadFiles(){
	   _LOGGER.info("Enter the Ftp server reader method");
		 fClient  = new FTPClient();
		OutputStream output = null;
		try {
			fClient.connect(serveraddress);
		if(!fClient.login(username, password)){
			fClient.logout();
		}else{
		}
		int reply = fClient.getReplyCode();
		if(!FTPReply.isPositiveCompletion(reply)){
			fClient.disconnect();
		
		}
		fClient.enterLocalPassiveMode();
	FTPFile[] allFiles = fClient.listFiles();//C:\A4ESPUpdate\UploadFiles
	
	for(FTPFile ftpFile : allFiles) {
		if(!ftpFile.isFile()){
			continue;
		}
		if(ftpFile.isDirectory()){
			//ftpFile.get
		}
         output = new FileOutputStream("D:\\A4 ESPUpdate\\FtpFiles" + "/" + ftpFile.getName());
         //get the file from the remote system
         fClient.retrieveFile(ftpFile.getName(), output);   
	}
	ftpServerDisconnect();
	File[] listOfFiles = getAllFiles();
	_LOGGER.info("Ftp files Count::"+listOfFiles.length);
	fileParsing.ReadFtpFiles(listOfFiles);
	
	} catch (IOException e) {
		_LOGGER.error("unable to connect to Ftp server: "+e.getMessage());
	}catch (Exception e) {
      _LOGGER.error("unable to connect to Ftp server: "+e.getMessage());
	}finally{
		if(output != null){
			try {
				output.close();
			} catch (Exception exce) {
				_LOGGER.error("unable to close output stream: "+exce.getMessage());
			}
		}
	}
 }
	
public File[] getAllFiles()
{  
	File[] listOfFiles = null;
	try{
		File file = new File("D:\\A4 ESPUpdate\\FtpFiles");
		 listOfFiles =file.listFiles();
	}catch(Exception exce){
		_LOGGER.error("Ftp Files are not available : "+exce.getMessage());
		return listOfFiles;
	}
	return listOfFiles;
}

public void ftpServerDisconnect(){
	if(this.fClient.isConnected()){
		try {
			this.fClient.logout();
			this.fClient.disconnect();
		} catch (IOException exce) {
			_LOGGER.error("unable to close FTP server: "+exce.getMessage());
		}
	
	}
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
	public FilesParsing getFileParsing() {
		return fileParsing;
	}

	public void setFileParsing(FilesParsing fileParsing) {
		this.fileParsing = fileParsing;
	}
}
	
