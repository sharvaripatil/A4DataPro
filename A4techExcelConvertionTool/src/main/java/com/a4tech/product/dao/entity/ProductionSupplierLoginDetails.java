package com.a4tech.product.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="prod_supplier_login_details")
public class ProductionSupplierLoginDetails extends BaseSupplierLoginDetails{
	/*@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	@Column(name="ASI_NUMBER")
	private String  asiNumber;
	@Column(name="USER_NAME")
	private String  userName;
	@Column(name="PASSWORD")
	private String  password;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getAsiNumber() {
		return asiNumber;
	}
	public void setAsiNumber(String asiNumber) {
		this.asiNumber = asiNumber;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
*/
}
