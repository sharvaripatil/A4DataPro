package com.a4tech.product.dao.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="Batch_log")
@Entity
public class BatchEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6563299694063575185L;
	@Id
	@Column(name = "Batch_Id")
	@GeneratedValue(strategy= GenerationType.AUTO)
	Integer batchId;
	@Column(name="Asi_Number")
	Integer asiNumber;
	public Integer getBatchId() {
		return batchId;
	}
	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}
	public Integer getAsiNumber() {
		return asiNumber;
	}
	public void setAsiNumber(Integer asiNumber) {
		this.asiNumber = asiNumber;
	}

}
