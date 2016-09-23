package com.a4tech.RFGLine.products.parser;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Material;
import com.a4tech.util.ApplicationConstants;

public class RFGMaterialParser {
	
	public List<Material> getMaterialName(String materialValue) {
		materialValue=materialValue.replaceAll(ApplicationConstants.CONST_STRING_NEWLINECHARS,ApplicationConstants.CONST_STRING_EMPTY);
		List<Material> MaterialList =new ArrayList<Material>();
		Material materialObj=new Material();
		
		 /*if(lookupServiceData.isMaterial(materialValue)){
			   materialObj.setName(materialValue);
			   materialObj.setAlias(materialValue);
			   MaterialList.add(materialObj);
			  }
		 else
			  {*/
		materialObj.setAlias(materialValue);
		materialObj.setName("OTHER FABRIC");
		MaterialList.add(materialObj);
			 // }
    	return MaterialList;
	}
}
