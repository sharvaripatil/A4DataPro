package com.a4tech.product.dao.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Table(name="product_log")
@Entity
public class ProductEntity {
	
	@Column(name="PRODUCT_NUMBER" ,nullable=false,unique=true)
	@Id
	private String productNo;
	@Column(name="COMPANY_ID")
	private Integer companyId;
	@Column(name="PRODUCT_STATUS")
	private boolean productStatus;
	
	@OneToMany(mappedBy="product" )
	@Cascade({CascadeType.SAVE_UPDATE})
	private Set<ErrorEntity> errors = new HashSet<ErrorEntity>();
	public String getProductNo() {
		return productNo;
	}
	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	public boolean isProductStatus() {
		return productStatus;
	}
	public void setProductStatus(boolean productStatus) {
		this.productStatus = productStatus;
	}
	public Set<ErrorEntity> getErrors() {
		return errors;
	}
	public void setErrors(Set<ErrorEntity> errors) {
		this.errors = errors;
	}
	
}
