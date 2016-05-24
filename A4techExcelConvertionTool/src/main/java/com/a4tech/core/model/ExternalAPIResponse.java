package com.a4tech.core.model;


import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class ExternalAPIResponse {
    @JsonProperty("StatusCode")
    private HttpStatus           statusCode;
    @JsonProperty("Message")
    private String               message;
    @JsonProperty("AdditionalInfo")
    private Set<String> additionalInfo;

    /**
     * @return the statusCode
     */
    public HttpStatus getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode
     *            the statusCode to set
     */
    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the additionalInfo
     */
    public Set<String> getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * @param additionalInfo
     *            the additionalInfo to set
     */
    public void setAdditionalInfo(Set<String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

}
