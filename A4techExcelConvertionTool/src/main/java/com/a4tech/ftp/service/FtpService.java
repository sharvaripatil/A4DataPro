package com.a4tech.ftp.service;

import org.springframework.web.multipart.MultipartFile;

public interface FtpService {
  public boolean uploadFile(MultipartFile mFile,String asiNumber,String environmentType);
  public void downloadFiles();
}
