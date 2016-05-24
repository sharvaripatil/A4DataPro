package com.a4tech.controller;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.filter.DelegatingFilterProxy;

public class TestValidationBean {
	
	@NotNull
	@Size
	private String userName;
	private String password;
	private String confirmPassword;
	private String email;
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
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
		

}
