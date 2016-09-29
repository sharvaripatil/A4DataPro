package com.a4tech.RFGLine.products.parser;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.AdditionalLocation;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.ImprintColor;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.Size;
import com.a4tech.util.ApplicationConstants;


public class RFGDescrptionParser {

	private RFGSizeParser descsizeParserObj;
	private RFGProductAttributeParser attributeObj;
	private RFGMaterialParser materialParserObj;
	private RFGPriceGridParser rfgPriceGridParserObj;

	public Product getDescription(String Description,Product existingProduct,ProductConfigurations descrproductConfigObj,List<PriceGrid> priceGrids2){
		
	
	String FullDescription=null; 
	String FullDescriptionFirst=Description;
	FullDescriptionFirst=Description.substring(0,Description.indexOf("Bag Size:"));
	if(Description.contains("2 Color")){
	String FullDescriptionSecond=Description.substring(Description.indexOf("2 Color Maximum Imprint"),Description.indexOf("Additional Imprint Color:"));
	FullDescription=FullDescriptionFirst.concat(FullDescriptionSecond);
	FullDescription = FullDescription.replaceAll(ApplicationConstants.CONST_STRING_NEWLINECHARS,ApplicationConstants.CONST_VALUE_TYPE_SPACE);
    existingProduct.setDescription(FullDescription);
	}
	FullDescriptionFirst = FullDescriptionFirst.replaceAll(ApplicationConstants.CONST_STRING_NEWLINECHARS,ApplicationConstants.CONST_VALUE_TYPE_SPACE);
	existingProduct.setDescription(FullDescriptionFirst);
    String tempDesc[]=Description.split(ApplicationConstants.CONST_STRING_NEWLINECHARS);
      
    for (String value : tempDesc) {
    	
    	  if (value.contains("Bag Size"))
    	{
    	String sizeValue[]=value.split("Bag Size:");
    	sizeValue[1]=sizeValue[1].replaceAll("\"",ApplicationConstants.CONST_STRING_EMPTY);
    	Size sizeObj=descsizeParserObj.getSizes(sizeValue[1]);
        descrproductConfigObj.setSizes(sizeObj);
       
    	}
    	  else  if (value.contains("Made from"))
    		  
    	{
    		  String MaterailValue=Description.substring(Description.indexOf("Made from")+10, Description.indexOf("Comes In"));
    		  List<Material> MaterialList = materialParserObj.getMaterialName(MaterailValue);
    		  descrproductConfigObj.setMaterials(MaterialList);
   
    	}
    	
    	  else  if (value.contains("Comes In"))
      	{
    	  String colorValue[]=value.split("Comes In:");
          List<Color> colorList=attributeObj.getColorCriteria(colorValue[1]);
          descrproductConfigObj.setColors(colorList);
     
      	}
    	  else if (value.contains("Imprint Size"))
      	{
    	  String imprintSize[]=value.split("Imprint Size:");
    	  List<ImprintSize> imprintSizeList= attributeObj.getImprintSize(imprintSize[1]);
    	  descrproductConfigObj.setImprintSize(imprintSizeList);
      	}
    	  
    	  else if (value.contains("Standard Imprint Colors"))
    	  {
    	  String imprintColor=null;
    	  imprintColor=Description.substring(Description.indexOf("Standard Imprint Colors:")+25,Description.indexOf("** PMS Colors"));	
    	  ImprintColor imprintColorObj=attributeObj.getImprintColor(imprintColor);
    	  descrproductConfigObj.setImprintColors(imprintColorObj);
    	  }
    	 
    	  else if (value.contains("Rush Available"))
    	  {
    	  List<RushTimeValue> RushTimevalueList = new ArrayList<RushTimeValue>();
    	  RushTime RushTimeObj=new RushTime();
    	  RushTimeObj.setAvailable(true);
    	  RushTimeObj.setRushTimeValues(RushTimevalueList);

    	  }
    	  
    	  else if(value.contains("Additional Imprint Color: "))
    	 {
    	    AdditionalColor additionColorObj=new AdditionalColor();
       	    List<AdditionalColor> AdditionalColorList = new ArrayList<AdditionalColor>();
       	    additionColorObj.setName("Additional Imprint Color");
        	AdditionalColorList.add(additionColorObj);
    		descrproductConfigObj.setAdditionalColors(AdditionalColorList);
    		String Value[]=value.split("\\: \\$");
            String upchargeValue[]=Value[1].split("\\(");
            String upchargeValue2=upchargeValue[0];

      priceGrids2 = rfgPriceGridParserObj.getUpchargePriceGrid("1",upchargeValue2,"","V","Additional Colors",  
					"false", "USD", "Additional Imprint Color",  "Add. Color Charge", "Other", new Integer(2), priceGrids2);
			
    	 }
    	  else if(value.contains("Additional Imprint Location:"))
    	 {
    		 AdditionalLocation additionLocationObj=new AdditionalLocation();
             List<AdditionalLocation> AdditionalLocationList = new ArrayList<AdditionalLocation>();
             additionLocationObj.setName("Additional Imprint Location");
             AdditionalLocationList.add(additionLocationObj);
             descrproductConfigObj.setAdditionalLocations(AdditionalLocationList);
             String Value[]=value.split("\\: \\$");
             String upchargeValue[]=Value[1].split("\\(");
             String upchargeValue3=upchargeValue[0];
            
             priceGrids2 = rfgPriceGridParserObj.getUpchargePriceGrid("1",upchargeValue3,"","V","Additional Location",  
 					"false", "USD", "Additional Imprint Location",  "Add. Location Charge", "Other", new Integer(3), priceGrids2);
    	 }
    	  else if(value.contains("PMS Color Imprint"))
   		  
   	  {
    		  
   		  String OptionValue[]=Description.split("Custom PMS");
   		  String OptionValue1[]=OptionValue[0].split("\\*");
   		  List<Option> optionsList= attributeObj.getOption(OptionValue1[2]);
   		  descrproductConfigObj.setOptions(optionsList);
   		  String valueArr[]=Description.split("Imprint:  \\$");
          String upchargeValue[]=valueArr[1].split("\\(");
          String upchargeValue4=upchargeValue[0];
       
          priceGrids2 = rfgPriceGridParserObj.getUpchargePriceGrid("1",upchargeValue4,"","V","Imprint Option",  
					"false", "USD", "Per Color",  "Imprint Option Charge", "Other", new Integer(4), priceGrids2);
       
   	  }
    	 }
 
    existingProduct.setPriceGrids(priceGrids2); 
    existingProduct.setProductConfigurations(descrproductConfigObj);
   	return existingProduct;
		
	}

	public RFGSizeParser getDescsizeParserObj() {
		return descsizeParserObj;
	}

	public void setDescsizeParserObj(RFGSizeParser descsizeParserObj) {
		this.descsizeParserObj = descsizeParserObj;
	}

	public RFGProductAttributeParser getAttributeObj() {
		return attributeObj;
	}

	public void setAttributeObj(RFGProductAttributeParser attributeObj) {
		this.attributeObj = attributeObj;
	}

	public RFGMaterialParser getMaterialParserObj() {
		return materialParserObj;
	}

	public void setMaterialParserObj(RFGMaterialParser materialParserObj) {
		this.materialParserObj = materialParserObj;
	}

	public RFGPriceGridParser getRfgPriceGridParserObj() {
		return rfgPriceGridParserObj;
	}

	public void setRfgPriceGridParserObj(RFGPriceGridParser rfgPriceGridParserObj) {
		this.rfgPriceGridParserObj = rfgPriceGridParserObj;
	}

	
	
	
}
