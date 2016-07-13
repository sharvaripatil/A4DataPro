package com.a4tech.product.dao.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Table(name="product_log")
@Entity
public class ProductEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2027796090534102174L;
	@Id
	@Column(name="PRODUCT_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int productId;
	@Column(name="SUPPLIER_ASI_NUMBER")
	private Integer supplierAsiNumber;
	@Column(name="BATCH_ID")
	private int batchId;
	@Column(name="PRODUCT_NUMBER")
	private String productNo;
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATE_PRODUCT_DATE")
	private Date  createProductDate;
	
	@OneToMany(mappedBy="product")
	@Cascade({CascadeType.SAVE_UPDATE})
	private Set<ErrorEntity> errors = new HashSet<ErrorEntity>();
	
	public String getProductNo() {
		return productNo;
	}
	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}
	
	public Set<ErrorEntity> getErrors() {
		return errors;
	}
	public void setErrors(Set<ErrorEntity> errors) {
		this.errors = errors;
	}
	public int getBatchId() {
		return batchId;
	}
	public void setBatchId(int batchId) {
		this.batchId = batchId;
	}
	public Date getCreateProductDate() {
		return createProductDate;
	}
	public void setCreateProductDate(Date createProductDate) {
		this.createProductDate = createProductDate;
	}
	public Integer getSupplierAsiNumber() {
		return supplierAsiNumber;
	}
	public void setSupplierAsiNumber(Integer supplierAsiNumber) {
		this.supplierAsiNumber = supplierAsiNumber;
	}
	public void addErrorEntity(ErrorEntity entity){
		if(entity!= null){
			getErrors().add(entity);
		}
		entity.setProduct(this);
	}
}
