package com.a4tech.scheduler;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.a4tech.ftp.service.FtpService;

public class FtpServerScheduler extends QuartzJobBean{
   	
   private static Logger _LOGGER = Logger.getLogger(FtpServerScheduler.class);
   //private FtpDownloadFiles ftpDownloadFiles;
   @Autowired
   private FtpService ftpServices;
	
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		_LOGGER.info("Enter scheduler class");

		//ftpDownloadFiles.getFtpDownloadFiles();
		ftpServices.downloadFiles();
	}
	
	/*public FtpDownloadFiles getFtpDownloadFiles() {
		return ftpDownloadFiles;
	}

	public void setFtpDownloadFiles(FtpDownloadFiles ftpDownloadFiles) {
		this.ftpDownloadFiles = ftpDownloadFiles;
	}*/
}
