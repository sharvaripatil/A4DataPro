package com.a4tech.sage.product.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.Catalog;



public class CatalogParser {
	private Logger              _LOGGER              = Logger.getLogger(getClass());
	public List<Catalog> getCatalogs(String catalogValue) {
		_LOGGER.info("Enter Catalog Parser class");
		List<Catalog> catalogList = new ArrayList<Catalog>();

		return catalogList;
	}

}
