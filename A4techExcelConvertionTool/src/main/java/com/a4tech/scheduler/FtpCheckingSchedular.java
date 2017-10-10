package com.a4tech.scheduler;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class FtpCheckingSchedular extends QuartzJobBean{
	private FTPClient ftpClient = new FTPClient();
	  private String serveraddress = "219.91.244.206";
	  private String username = "A4Tech\\test2" ;
	  private String password = "admin@123" ;
	  private String portNo ="21" ;
	  /*@Autowired
	  private IMailService mailService;*/
  private Logger _LOGGER = Logger.getLogger(FtpCheckingSchedular.class);
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
			ftpClient.connect(serveraddress,Integer.parseInt(portNo));
			if(!ftpClient.login(username, password)){
				ftpClient.logout();
			}else{
			}
			_LOGGER.info("FTP Server is working properly");
		}catch (Exception exec) {
			_LOGGER.fatal("Unable to connect FTP SERVER,Please check server");
			//ftpServerFailure();
		}
	}
}
