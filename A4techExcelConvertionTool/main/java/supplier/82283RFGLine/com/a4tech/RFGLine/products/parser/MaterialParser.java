package com.a4tech.RFGLine.products.parser;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Material;

public class MaterialParser {
	
	public List<Material> getMaterialName(String materialValue) {
		materialValue=materialValue.replaceAll("(\r\n|\n)", " ");
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
