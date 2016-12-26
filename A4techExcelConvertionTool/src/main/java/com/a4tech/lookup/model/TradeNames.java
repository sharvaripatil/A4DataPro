package com.a4tech.lookup.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TradeNames {
	@JsonProperty("tradeNames")
	private List<String> tradeNames = null;

	public List<String> getTradeNames() {
		return tradeNames;
	}

	public void setTradeNames(List<String> tradeNames) {
		this.tradeNames = tradeNames;
	}
}
