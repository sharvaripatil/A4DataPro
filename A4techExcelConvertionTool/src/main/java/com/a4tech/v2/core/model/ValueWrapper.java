package com.a4tech.v2.core.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ValueWrapper {

	@XmlElement(name = "List")
	    private List<Value> list;

	    public ValueWrapper() {/*JAXB requires it */

	    }

	    public ValueWrapper(List<Value> list) {
	        this.list = list;
	    }

	    public List<Value> getList() {
	        return list;
	    }
}
