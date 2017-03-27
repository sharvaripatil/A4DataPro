package parser.crystal;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.AvailableVariations;
import com.a4tech.product.model.Configurations;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.Personalization;
import com.a4tech.product.model.Shape;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;

public class CrystalDProductAttributeParser {
	private LookupServiceData lookupServiceDataObj;

	public  Volume getItemWeight(String ItemWT) {
		
		Volume itemWeight=new Volume();
		List<Values> valuesList = new ArrayList<Values>(); 
		List<Value> valueList = new ArrayList<Value>(); 

		  Value valueObj=new Value(); 
		  Values valuesObj=new Values(); 
		  valueObj.setValue(ItemWT);
		  valueObj.setUnit("lbs");
		  valueList.add(valueObj);
		  valuesObj.setValue(valueList);
		  valuesList.add(valuesObj);
		  itemWeight.setValues(valuesList); 

		return itemWeight;
	}

	

	public Size getSizes(StringBuilder shippingDimension)
	{
	  Size sizeObj=new Size();
	  String ShippingDimensionValue=shippingDimension.toString();
	  ShippingDimensionValue=ShippingDimensionValue.replaceAll("\"", "").replaceAll("Height includes chain and key ring", "").replaceAll("Width includes chain and key ring", "");
	  ShippingDimensionValue=ShippingDimensionValue.replaceAll(",,", ",");
	  String DimValueArr[]=ShippingDimensionValue.split(",");
	  
	  Dimension dimensionObj= new Dimension();
	  List<Values> valuesList = new ArrayList<Values>();
	  List<Value> valuelist =  new ArrayList<Value>();
	  Values valuesObj = new Values();
	  Value valObj=null;
		 
	  for (String OutreLoop:DimValueArr) {
		//  dimensionObj= new Dimension();
		  valuesObj=new  Values();
		  valuelist =  new ArrayList<Value>();
		  
	   String SizeValueArr[]=OutreLoop.split("x");
	
	   int i=0;
	   for (String Value : SizeValueArr) {
			 valObj=new Value();
			 i++;
		   
		if(Value.contains("W"))
		{
			valObj.setAttribute("Width");
			Value=Value.replaceAll("[^//0-9x-]", "").replaceAll("-", " ");
			valObj.setValue(Value);
		}
		else if(Value.contains("H"))
		{
			valObj.setAttribute("Height");
			Value=Value.replaceAll("[^//0-9x-]", "").replaceAll("-", " ");
			valObj.setValue(Value);
		}
		else if(Value.contains("Dia"))
		{
			valObj.setAttribute("Diameter");
			Value=Value.replaceAll("[^//0-9x-]", "").replaceAll("-", " ");
			valObj.setValue(Value);
		}
		else if(Value.contains("D"))
		{
			valObj.setAttribute("Depth");
			Value=Value.replaceAll("[^//0-9x-]", "").replaceAll("-", " ");
			valObj.setValue(Value);
		}
		else if(Value.contains("L"))
		{
			valObj.setAttribute("Length");
			Value=Value.replaceAll("[^//0-9x-]", "").replaceAll("-", " ");
			valObj.setValue(Value);
		}
		
		else
		{
			
			Value=Value.replaceAll("[^//0-9x-]", "").replaceAll("-", " ");
			valObj.setValue(Value);
			if(i==1)
			{
				valObj.setAttribute("Length");
			}
			else if(i==2)
			{
				valObj.setAttribute("Width");
			}
			else if(i==3)
			{
				valObj.setAttribute("Height");
			}
		}
		valObj.setUnit("in");
		valuelist.add(valObj);   
	     valuesObj.setValue(valuelist);
	    
	     }
	   
	     valuesList.add(valuesObj);
	     dimensionObj.setValues(valuesList);
	  }
	  sizeObj.setDimension(dimensionObj);
	return sizeObj;
	}



	public List<ImprintMethod> getImprintMethod(String imprintMethodValue, List<ImprintMethod> exstimprintMethodsList) {
		
		imprintMethodValue=imprintMethodValue.replaceAll("™","");
		if(imprintMethodValue.contains("Blank"))
		{
			imprintMethodValue=imprintMethodValue.replaceAll("Blank-Imprint Extra","UNIMPRINTED");
		}
		//List<ImprintMethod> listOfImprintMethod = new ArrayList<>();
		ImprintMethod imprintMethodObj= new ImprintMethod();
		String ImprintValue[]=imprintMethodValue.split(",");
		
		for (String Value : ImprintValue) {
			imprintMethodObj = new ImprintMethod();
			imprintMethodObj.setType(CrystalDApplicationConstant.IMPRINT_METHOD_MAP.get(Value.trim()));
			imprintMethodObj.setAlias(Value);
			//imprintMethodObj.setAlias(CrystalDApplicationConstant.IMPRINT_METHOD_MAP.get(Value.trim()));
			exstimprintMethodsList.add(imprintMethodObj);
		}
	
		return exstimprintMethodsList;
	}




	public List<Personalization> getPeronalization(String allNotes) {
		
		List<Personalization> listPersonlization=new ArrayList<Personalization>();
		if(allNotes.contains("Personalization")){
			Personalization perslznObj=new Personalization();
			perslznObj.setAlias("PERSONALIZATION");
			perslznObj.setType("PERSONALIZATION");
			listPersonlization.add(perslznObj);
		}
		return listPersonlization;
	}


	public Option getImprintOption1(String ImprintOption) {
		
		List<OptionValue> ImprintOptList1 = new ArrayList<OptionValue>();
		OptionValue ImprintOptValueObj=new OptionValue();
		Option ImprintOptObj=new Option();
		
		ImprintOptObj.setOptionType("Imprint");
		ImprintOptObj.setName("Copy Change Option");
		ImprintOptValueObj.setValue("Copy Change");
		ImprintOptList1.add(ImprintOptValueObj);
		ImprintOptObj.setValues(ImprintOptList1);
		
		return ImprintOptObj;
	}
	


	public Option getImprintOption2(String tempImprintOptionValue) {
		//List<Option> optionList = new ArrayList<Option>();
		List<OptionValue> valuesList = new ArrayList<OptionValue>();
		OptionValue optionValueObj=new OptionValue();
		Option optionObj=new Option();
		
		optionObj.setOptionType("Imprint");
		optionObj.setName("Additional Imprinting Option");
		optionValueObj.setValue(tempImprintOptionValue.trim());
		valuesList.add(optionValueObj);
		optionObj.setValues(valuesList);
	//	optionList.add(optionObj);
		
		return optionObj;
	}


	/*public Option getImprintOptOption(String ImprintOptOption) {
		//List<Option> ImprintOptList = new ArrayList<Option>();
		List<OptionValue> ImprintOptList1 = new ArrayList<OptionValue>();
		OptionValue ImprintOptValueObj=new OptionValue();
		Option ImprintOptObj=new Option();
		
		ImprintOptObj.setOptionType("Imprint");
		ImprintOptObj.setName("Copy Change Option");
		ImprintOptValueObj.setValue("Copy Change");
		ImprintOptList1.add(ImprintOptValueObj);
		ImprintOptObj.setValues(ImprintOptList1);
	//	ImprintOptObj.setAdditionalInformation(productOption);
	//	ProdoptionList.add(ProdoptionObj);

		return ImprintOptObj;
	}*/
	
	
	public List<Availability> getProductAvailability(List<ImprintLocation> imprintLocationList, 
		List<ImprintSize> imprintSizeList){
		
		List<Availability>  availabilityList= new ArrayList<Availability>(); 
		Availability avaibltyObj=new Availability();
		
		List<AvailableVariations>  avaiVaraitionList= new ArrayList<AvailableVariations>(); 
		AvailableVariations VariationObj=new AvailableVariations();
		
		List<Object>  locationList= new ArrayList<Object>(); 
		List<Object>  sizeList= new ArrayList<Object>(); 

		avaibltyObj.setParentCriteria("Imprint Location");
		avaibltyObj.setChildCriteria("Imprint Size");
		
	  for(int i=0;i<imprintLocationList.size();i++){
		String LocArr=imprintLocationList.get(i).getValue().toString().trim();
		String SizeArr=imprintSizeList.get(i).getValue().toString().trim();

		//for (ImprintLocation LocationVal : imprintLocationList) { //String childValue : childList
			// for (ImprintSize sizeValue : imprintSizeList) {//String ParentValue : parentList
				 VariationObj = new AvailableVariations();
				 locationList = new ArrayList<>();
				 sizeList = new ArrayList<>();
				 locationList.add(LocArr);
				 sizeList.add(SizeArr);
				 VariationObj.setParentValue(locationList);
				 VariationObj.setChildValue(sizeList);
				 avaiVaraitionList.add(VariationObj);
	}
			//}
	//	}
		
		avaibltyObj.setAvailableVariations(avaiVaraitionList);
		availabilityList.add(avaibltyObj);
		
		return availabilityList;	
	}
	
	
	
	public List<Shape> getShapeList(String Shape){
	List<Shape> shapeList=new ArrayList<Shape>();
	Shape shapeObj=new Shape();
	
	Shape=Shape.toUpperCase();
	Shape=Shape.replaceAll("Gem", "circle");
	Shape=Shape.replaceAll("/", ",");
	List<String> lookupShapeList=lookupServiceDataObj.getShapeValues();
	if(Shape.contains(",")){
	String ShapeArr[]=Shape.split(",");
	for (String value : ShapeArr) {
		if(lookupShapeList.contains(value.toUpperCase().trim()))
		{
			shapeObj=new Shape();
			shapeObj.setName(value);
			shapeList.add(shapeObj);
		}
	}
	}else
	{   if(lookupShapeList.contains(Shape))
		{
			shapeObj.setName(Shape);
			shapeList.add(shapeObj);
		}
	}
	return shapeList;
	}


	
	public List<Image> getImage(List<Image> Img, Size sizeObj) {
		Image  imgObj=new Image();
		List<Image> ImgList=new ArrayList<Image>();
		List<Configurations> confList=new ArrayList<>();
		Configurations confObj=new Configurations();
		
	    
		for (int i = 0; i < Img.size(); i++) {
			List<Value> valueObj=sizeObj.getDimension().getValues().get(i).getValue(); 	
			
			List<Object>valueObjList=new ArrayList<>();            	
        	imgObj=new Image();
			
			confList=new ArrayList<>();
		    confObj=new Configurations();
			confObj.setCriteria("Size");
		    valueObjList=new ArrayList<>();
			Object obj= new Object();
			if(valueObj.size() == 2){
				for(int j=0;j<2;j++)
				{
					obj= new Object();
					obj=valueObj.get(j);
					valueObjList.add(obj);
				}
			}
			else if (valueObj.size() == 3){
				for(int j=0;j<3;j++)
				{
					obj= new Object();
					obj=valueObj.get(j);
					valueObjList.add(obj);
				}
			}
		
			 confObj.setValue(valueObjList);
			 confList.add(confObj);
			 
			 imgObj.setConfigurations(confList);
			 imgObj.setDescription(Img.get(i).getDescription());
			 imgObj.setImageURL(Img.get(i).getImageURL());
			 imgObj.setIsPrimary(Img.get(i).getIsPrimary());
			 imgObj.setIsvirtualized(Img.get(i).getIsvirtualized());
			 imgObj.setRank(Img.get(i).getRank());
			 ImgList.add(imgObj);
			 confList=new ArrayList<>();
		     confObj=new Configurations();
			}
	    		
		return ImgList;
	}

	
	
	
	
	

	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}



	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}



	


	
	
	
}
