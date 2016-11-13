package com.a4tech.product.dao.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table
@Entity(name="ftpserverfiles_log")
public class FtpServerFileEntity {
	@Column(name="FSID")
	@Id
	@GeneratedValue(strategy =GenerationType.AUTO)
	private Integer id;
	@Column(name="FILE_NAME")
	private String fileName;
	@Column(name="SUPPLIER_ASI_NUMBER")
	private String supplierAsiNumber;
	@Column(name="FILE_STATUS")
	private String fileStatus;
	@Column(name="FILE_PROCESS_DATE")
	private Date   fileProcessDate;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getSupplierAsiNumber() {
		return supplierAsiNumber;
	}
	public void setSupplierAsiNumber(String supplierAsiNumber) {
		this.supplierAsiNumber = supplierAsiNumber;
	}
	public String getFileStatus() {
		return fileStatus;
	}
	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}
	public Date getFileProcessDate() {
		return fileProcessDate;
	}
	public void setFileProcessDate(Date fileProcessDate) {
		this.fileProcessDate = fileProcessDate;
	}

}
