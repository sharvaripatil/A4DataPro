package com.a4tech.product.dao.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name="error_log")
@Entity
public class ErrorEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 794248617055025267L;
	@Id
	@Column(name="ERROR_ID")
	@GeneratedValue
	private Integer errorId;
	@Column(name="ERRORS")
	private String Error;
	@ManyToOne
	@JoinColumn(name="PRODUCT_REF_ID",insertable=true,updatable=false,referencedColumnName = "PRODUCT_ID")
	//@JoinColumn(name="PRODUCT_ID")
	private ProductEntity product;
	
	public String getError() {
		return Error;
	}
	public void setError(String error) {
		Error = error;
	}
	public ProductEntity getProduct() {
		return product;
	}
	public void setProduct(ProductEntity product) {
		this.product = product;
	}
	public Integer getErrorId() {
		return errorId;
	}
	public void setErrorId(Integer errorId) {
		this.errorId = errorId;
	}
}
