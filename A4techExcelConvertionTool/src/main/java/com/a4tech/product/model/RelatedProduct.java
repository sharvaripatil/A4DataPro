package com.a4tech.product.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class RelatedProduct {
	@JsonProperty("ExternalProductId")
	private String externalProductId;
	@JsonProperty("RelationType")
	private String relationType;

	public String getExternalProductId() {
		return externalProductId;
	}

	public void setExternalProductId(String externalProductId) {
		this.externalProductId = externalProductId;
	}

	public String getRelationType() {
		return relationType;
	}

	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}
}
