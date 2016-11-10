package com.a4tech.scheduler;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.a4tech.ftp.FtpDownloadFiles;

public class FtpServerScheduler extends QuartzJobBean{
   	
   private static Logger _LOGGER = Logger.getLogger(FtpServerScheduler.class);
   private FtpDownloadFiles ftpDownloadFiles;
	
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		_LOGGER.info("Enter scheduler class");

		ftpDownloadFiles.getFtpDownloadFiles();
	}
	
	public FtpDownloadFiles getFtpDownloadFiles() {
		return ftpDownloadFiles;
	}

	public void setFtpDownloadFiles(FtpDownloadFiles ftpDownloadFiles) {
		this.ftpDownloadFiles = ftpDownloadFiles;
	}

   
}
