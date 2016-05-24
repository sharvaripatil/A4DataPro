package com.a4tech.core.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "access_token"
})
@XmlRootElement(name = "Login")
public class AccessBean {
    
    @JsonProperty("AccessToken")
    private String accessToken;
    
    @JsonProperty("TokenExpirationTime")
    private String tokenExpirationTime;
    
    @XmlElement(name = "AccessToken")
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @XmlElement(name = "TokenExpirationTime")
    public String getTokenExpirationTime() {
        return tokenExpirationTime;
    }

    public void setTokenExpirationTime(String tokenExpirationTime) {
        this.tokenExpirationTime = tokenExpirationTime;
    }


}
