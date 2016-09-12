package com.a4tech.RFGLine.products.parser;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Color;
import com.a4tech.product.model.ImprintColor;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.Size;
import com.a4tech.util.ApplicationConstants;

public class DescrptionParser {

	private SizeParser descsizeParserObj;
	private ProductAttributeParser attributeObj;
	private MaterialParser materialParserObj;

	public Product getDescription(String Description,Product existingProduct,ProductConfigurations descrproductConfigObj){

	String FullDescription=null; 
	String FullDescriptionFirst=Description;
	FullDescriptionFirst=Description.substring(0,Description.indexOf("Bag Size:"));
	String FullDescriptionSecond=Description.substring(Description.indexOf("2 Color Maximum Imprint"),Description.indexOf("Additional Imprint Color:"));
	FullDescription=FullDescriptionFirst.concat(FullDescriptionSecond);
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
    		  String MaterailValue=Description.substring(Description.indexOf("Made from")+10, Description.indexOf("Comes In"));
    		  List<Material> MaterialList = materialParserObj.getMaterialName(MaterailValue);
    		  descrproductConfigObj.setMaterials(MaterialList);
   
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
    	  
    	  if (value.contains("Standard Imprint Colors"))
    	  {
    	  String imprintColor=null;
    	  imprintColor=Description.substring(Description.indexOf("Standard Imprint Colors:")+25,Description.indexOf("** PMS Colors"));	
    	  ImprintColor imprintColorObj=attributeObj.getImprintColor(imprintColor);
    	  descrproductConfigObj.setImprintColors(imprintColorObj);
    	  }
    	 
    	  if (value.contains("Rush Available"))
    	  {
    	  List<RushTimeValue> RushTimevalueList = new ArrayList<RushTimeValue>();
    	  RushTime RushTimeObj=new RushTime();
    	  RushTimeObj.setAvailable(true);
    	  RushTimeObj.setRushTimeValues(RushTimevalueList);

    	  }
    	  
    	  if(value.contains("** PMS Colors"))
    		  
    	  {
    		  String OptionValue[]=Description.split("Custom PMS");
    		  String OptionValue1[]=OptionValue[0].split("\\*");
    		  List<Option> optionsList= attributeObj.getOption(OptionValue1[2]);
    		  descrproductConfigObj.setOptions(optionsList);
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

	public MaterialParser getMaterialParserObj() {
		return materialParserObj;
	}

	public void setMaterialParserObj(MaterialParser materialParserObj) {
		this.materialParserObj = materialParserObj;
	}

	
	
	
}
