package com.a4tech.product.criteria.parser;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Catalog;



public class CatalogParser {
	public static List<Catalog> getCatalogs(String catalogValue) {
		Catalog catalog = null;
		String catalogArr[] = catalogValue.split(",");
		List<Catalog> catalogList = new ArrayList<Catalog>();

		for (int i = 0; i <= catalogArr.length - 1; i++) {
			catalog = new Catalog();
			catalogValue = catalogArr[i];
			String[] catalogInfo = catalogValue.split(":");
			if (catalogInfo.length == 3) {
				catalog.setCatalogName(catalogInfo[0]);
				catalog.setCatalogPage(catalogInfo[2]);
			} else if (catalogInfo.length == 2) {
				catalog.setCatalogName(catalogInfo[0]);
				catalog.setCatalogPage("");
			}
			catalogList.add(catalog);
		}

		return catalogList;
	}

}
