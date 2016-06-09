package com.a4tech.v2.core.model;

import javax.xml.bind.annotation.XmlSeeAlso;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
/*
 * Story: VELOEXTAPI-945(GET)
 * Author: amore, Date: 13/09/2015
 * Description: Commented JsonTypeInfo to avoid its class info definition In final json model. 
 */
//@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="@class")
@JsonSubTypes({@Type(value=StringValue.class, name = "StringValue"), @Type(value=Value.class, name="Value"), @Type(value=ListValue.class, name="ListValue")})
@XmlSeeAlso({StringValue.class, Value.class, ListValue.class})
@JsonIgnoreProperties
public class BaseValue {
 
}
