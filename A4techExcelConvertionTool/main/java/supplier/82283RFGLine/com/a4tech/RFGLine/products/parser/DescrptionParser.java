package com.a4tech.RFGLine.products.parser;


import java.util.List;

import com.a4tech.product.model.Color;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.Size;

public class DescrptionParser {
	
	 private SizeParser descsizeParserObj;
	 private ProductAttributeParser attributeObj;
	
    public Product getDescription(String Description,Product existingProduct){
	ProductConfigurations descrproductConfigObj	=new ProductConfigurations();

	//Description
	String FullDescription=Description;
	FullDescription=FullDescription.substring(0,Description.indexOf("Bag Size:"));			
	FullDescription = FullDescription.replaceAll("(\r\n|\n)", " ");
    existingProduct.setDescription(FullDescription);
   
    String tempDesc[]=Description.split("(\r\n|\n)");
      
    for (String value : tempDesc) {
    	
    	  if (value.contains("Bag Size"))
    	{
    	String sizeValue[]=value.split("Bag Size:");
    	sizeValue[1]=sizeValue[1].replaceAll("\"", "");
    	Size sizeObj=descsizeParserObj.getSizes(sizeValue[1]);
        descrproductConfigObj.setSizes(sizeObj);
       
    	}
    	  if (value.contains("Made from"))
    	{
   
    	}
    	
    	  if (value.contains("Comes In"))
      	{
    	  String colorValue[]=value.split("Comes In:");
          List<Color> colorList=attributeObj.getColorCriteria(colorValue[1]);
          descrproductConfigObj.setColors(colorList);
     
      	}
    	  if (value.contains("Imprint Size"))
      	{
    	  String imprintSize[]=value.split("Imprint Size:");
    	  List<ImprintSize> imprintSizeList= attributeObj.getImprintSize(imprintSize[1]);
    	  descrproductConfigObj.setImprintSize(imprintSizeList);
      	}
    	  
    	  
    	  
    	  
	}
 
    
    
    existingProduct.setProductConfigurations(descrproductConfigObj);
   	return existingProduct;
		
	}

	public SizeParser getDescsizeParserObj() {
		return descsizeParserObj;
	}

	public void setDescsizeParserObj(SizeParser descsizeParserObj) {
		this.descsizeParserObj = descsizeParserObj;
	}

	public ProductAttributeParser getAttributeObj() {
		return attributeObj;
	}

	public void setAttributeObj(ProductAttributeParser attributeObj) {
		this.attributeObj = attributeObj;
	}

	
}
