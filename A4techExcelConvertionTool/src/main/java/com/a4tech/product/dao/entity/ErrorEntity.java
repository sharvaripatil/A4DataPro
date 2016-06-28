package com.a4tech.product.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name="error_log")
@Entity
public class ErrorEntity {
	@Id
	@Column(name="ERROR_ID")
	@GeneratedValue
	private Integer id;
	@Column(name="PRODUCT_NUMBER",nullable=false)
	private String productNumber;
	@Column(name="ERRORS")
	private String Error;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="PRODUCT_NUMBER",insertable=false,updatable=false)
	private ProductEntity product;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		
		this.id = id;
	}
	
	public String getProductNumber() {
		return productNumber;
	}
	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}
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

}
