package com.a4tech.product.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name="Errors")
@Entity
public class ErrorEntity {
	@Id
	@Column(name="ErrorId",nullable=false)
	@GeneratedValue
	private Integer id;
	@Column(name="productNo",nullable=false,insertable=false ,updatable=false)
	private String productNo;
	@Column(name="Error")
	private String Error;
	@ManyToOne(fetch = FetchType.LAZY ,optional=false)
	@JoinColumn(name="productNo")
	private ProductEntity product;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		
		this.id = id;
	}
	public String getProductNo() {
		return productNo;
	}
	public void setProductNo(String productNo) {
		this.productNo = productNo;
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
