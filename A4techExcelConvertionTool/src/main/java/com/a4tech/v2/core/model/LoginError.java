/**
 * 
 */
package com.a4tech.v2.core.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Rahul K
 * 
 */
@JsonInclude(Include.NON_NULL)
@XmlRootElement
public class LoginError {

    @JsonProperty("Error")
    private String error;

    /**
     * 
     */
    public LoginError() {
    }

    public LoginError(String error) {
        this.error = error;
    }

    /**
     * @return the error
     */
    @XmlElement(name = "Error")
    public String getError() {
        return error;
    }

    /**
     * @param error
     *            the error to set
     */
    public void setError(String error) {
        this.error = error;
    }

}
