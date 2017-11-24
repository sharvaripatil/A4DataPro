package com.a4tech.lookup.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Colors {
		@JsonProperty("colors")
	  private List<String> listColor;

		public List<String> getListColor() {
			return listColor;
		}

		public void setListColor(List<String> listColor) {
			this.listColor = listColor;
		}

	}

