package com.a4tech.product.dao.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Table(name="Product")
@Entity
public class ProductEntity {
	
	
	
	@Column(name="productNo" ,nullable=false)
	@Id
	private String productNo;
	@Column(name="companyId")
	private String companyId;
	@Column(name="productStatus")
	private boolean productStatus;
	
	@OneToMany(mappedBy="product")
	private List<ErrorEntity> errors;
	public String getProductNo() {
		return productNo;
	}
	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public boolean isProductStatus() {
		return productStatus;
	}
	public void setProductStatus(boolean productStatus) {
		this.productStatus = productStatus;
	}
	public List<ErrorEntity> getErrors() {
		return errors;
	}
	public void setErrors(List<ErrorEntity> errors) {
		this.errors = errors;
	}
}
