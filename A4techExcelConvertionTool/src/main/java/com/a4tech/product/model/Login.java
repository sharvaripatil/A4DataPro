package com.a4tech.product.model;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Login {

	 
    @JsonProperty("Asi")
    private String asi_number;
    
    @JsonProperty("Username")
    private String username;
    
    @JsonProperty("Password")
    private String password;
    
    @XmlElement(name = "Asi")
    public String getAsi_number() {
        return asi_number;
    }

    public void setAsi_number(String asi_number) {
        this.asi_number = asi_number;
    }

    @XmlElement(name = "Username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @XmlElement(name = "Password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String toString() {
        return "[ Asi = " + getAsi_number() + ", Username = " + getUsername() + ", Password = " + getPassword() + " ]";
    }
	
}
