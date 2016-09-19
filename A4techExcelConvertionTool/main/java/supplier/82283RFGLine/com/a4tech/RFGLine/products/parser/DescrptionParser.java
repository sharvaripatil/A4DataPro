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


public class DescrptionParser {

	private SizeParser descsizeParserObj;
	private ProductAttributeParser attributeObj;
	private MaterialParser materialParserObj;
	private PriceGridParser rfgPriceGridParserObj;

	public Product getDescription(String Description,Product existingProduct,ProductConfigurations descrproductConfigObj,List<PriceGrid> priceGrids2){
		
	
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
    	  
    	 
    	  
    	 if(value.contains("Additional Imprint Color: "))
    	 {
    	    AdditionalColor additionColorObj=new AdditionalColor();
       	    List<AdditionalColor> AdditionalColorList = new ArrayList<AdditionalColor>();
       	    additionColorObj.setName("Additional Imprint Color");
        	AdditionalColorList.add(additionColorObj);
    		descrproductConfigObj.setAdditionalColors(AdditionalColorList);
    		String Value[]=value.split("\\: \\$");
            String upchargeValue[]=Value[1].split("\\(");
            String upchargeValue2=upchargeValue[0];

    priceGrids2 = rfgPriceGridParserObj.getUpchargePriceGrid("1",upchargeValue2,"","v","Additional Color",  
					"false", "USD", "Additional Imprint Color",  "Imprint Color Charge", "Other", new Integer(2), priceGrids2);
			
    existingProduct.setPriceGrids(priceGrids2);
    	 }
    	 if(value.contains("Additional Imprint Location:"))
    	 {
    		 AdditionalLocation additionLocationObj=new AdditionalLocation();
             List<AdditionalLocation> AdditionalLocationList = new ArrayList<AdditionalLocation>();
             additionLocationObj.setName("Additional Imprint Location");
             AdditionalLocationList.add(additionLocationObj);
             descrproductConfigObj.setAdditionalLocations(AdditionalLocationList);
             String Value[]=value.split("\\: \\$");
             String upchargeValue[]=Value[1].split("\\(");
             String upchargeValue3=upchargeValue[0];
            
             priceGrids2 = rfgPriceGridParserObj.getUpchargePriceGrid("1",upchargeValue3,"","v","Additional Location",  
 					"false", "USD", "Additional Imprint Location",  "Imprint Location Charge", "Other", new Integer(3), priceGrids2);
             
    	 }
    	 if(value.contains("PMS Color"))
   		  
   	  {
   		  String OptionValue[]=Description.split("Custom PMS");
   		  String OptionValue1[]=OptionValue[0].split("\\*");
   		  List<Option> optionsList= attributeObj.getOption(OptionValue1[2]);
   		  descrproductConfigObj.setOptions(optionsList);
   		  String valueArr[]=Description.split("Imprint:  \\$");
          String upchargeValue[]=valueArr[1].split("\\(");
          String upchargeValue4=upchargeValue[0];
       
          priceGrids2 = rfgPriceGridParserObj.getUpchargePriceGrid("1",upchargeValue4,"","v","Imprint Option",  
					"false", "USD", "Per Color",  "Imprint Option Charge", "Other", new Integer(4), priceGrids2);
           
       
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

	public PriceGridParser getRfgPriceGridParserObj() {
		return rfgPriceGridParserObj;
	}

	public void setRfgPriceGridParserObj(PriceGridParser rfgPriceGridParserObj) {
		this.rfgPriceGridParserObj = rfgPriceGridParserObj;
	}

	
	
	
}
