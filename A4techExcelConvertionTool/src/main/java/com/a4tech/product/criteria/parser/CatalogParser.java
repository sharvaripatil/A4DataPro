package com.a4tech.product.criteria.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.Catalog;



public class CatalogParser {
	private Logger              _LOGGER              = Logger.getLogger(getClass());
	public List<Catalog> getCatalogs(String catalogValue) {
		List<Catalog> catalogList = new ArrayList<Catalog>();
		try{
		Catalog catalog = null;
		String catalogArr[] = catalogValue.split(",");
	

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
		}
		catch(Exception e){
			_LOGGER.error("Error while processing catalog :"+e.getMessage());
		}

		return catalogList;
	}

}
