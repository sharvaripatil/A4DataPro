package parser.brandwear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.BlendMaterial;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.OtherSize;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Weight;

public class BrandwearProductAttribure {
	private LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;

	public ShippingEstimate getshippingWeight(String shippingWeight) {

		String ShippingUnit=shippingWeight;
		ShippingEstimate shippingObj = new ShippingEstimate();

		List<Weight> WeightList = new ArrayList<Weight>();
		Weight objWeight = new Weight();
		shippingWeight=shippingWeight.replaceAll("[^0-9/ ]", "").replace("/", "");
		//shippingWeight = shippingWeight.replaceAll("oz. / Item", "").replaceAll("1 lb. / Item", "").replaceAll("oz /Item", "").replaceAll("oz/ Item", "");
		objWeight.setValue(shippingWeight);
		if(ShippingUnit.contains("oz"))
		{
			objWeight.setUnit("oz");

		}else
		{
			objWeight.setUnit("lbs");

		}
		WeightList.add(objWeight);
		shippingObj.setWeight(WeightList);
		return shippingObj;
	}

	
	public List<Color> getColorValue(String colorValue) {
		//if(colorValue.contains("&"))
		//{
 			colorValue=colorValue.replace("&", ",").replace("/", ",");	
		//}
		
		List<Color> colorList = new ArrayList<Color>();
		Color colorObj=new Color();
	    String colorArr[]=colorValue.split(",");
		Combo combovalue = new Combo();

		ArrayList<String> tempColorList = new ArrayList<String>();//Arrays.asList(colorArr)
		for (String string : colorArr) {
			tempColorList.add(string.trim());
		}
				
		 Set<String> setcolor = new HashSet<String>(tempColorList);
		
		 String arraycolor[] = setcolor.toArray(new String[setcolor.size()]);
	    for (String colorsValue : arraycolor) {
	    	colorObj=new Color();	
	    	
	    	if(colorsValue.contains("/"))
	    	{
	    	combovalue = new Combo();
	    	List<Combo> combolist = new ArrayList<Combo>();
	    	String ComboColorArr[]=	colorsValue.split("/");
	    	colorObj.setAlias(colorsValue.trim());
	    	colorObj.setName(BrandwearLookupdata.COLOR_MAP.get(ComboColorArr[0].trim()));
	    	combovalue.setName(BrandwearLookupdata.COLOR_MAP.get(ComboColorArr[1].trim()));
	    	combolist.add(combovalue);
	    	colorObj.setCombos(combolist);
	    	colorList.add(colorObj);
	    	}else
	    	{
	    		colorObj.setAlias(colorsValue);
	    		colorObj.setName(BrandwearLookupdata.COLOR_MAP.get(colorsValue.trim()));
	    		colorList.add(colorObj);
	    		
	    	}
		}
		return colorList;
	}
	
	
	
	public List<ImprintMethod> getImprintMethod(String imprintMethod) {
 		ImprintMethod imprMethod = new ImprintMethod();
		List<ImprintMethod> imprintMethodList = new ArrayList<ImprintMethod>();
		String imprintMethodValueArr[]=imprintMethod.split(",");
		
		for (String imprintMethodValue : imprintMethodValueArr) {
			
			imprMethod = new ImprintMethod();
	  if(imprintMethodValue.contains("print"))
	  {
		  imprMethod.setType("Pad Print");
	  }else if(imprintMethodValue.contains("Embroidery"))
	  {
		  imprMethod.setType("Embroidered");
	  }else if(imprintMethodValue.contains("Silk Screening"))
	  {
		  imprMethod.setType("Silkscreen");

	  }else if(imprintMethodValue.contains("Sublimation"))
	  {
		  imprMethod.setType("Sublimation");
	  }else if(imprintMethodValue.contains("Heat Press"))
	  {
		  imprMethod.setType("Heat Transfer");
	  }else
	  {
		  imprMethod.setType("Other");
	  }
	  imprMethod.setAlias(imprintMethodValue);	
	  imprintMethodList.add(imprMethod);

		}
		return imprintMethodList;
	}


	public List<Material> getMaterial(String materialValues) {
		List<Material> MaterialList = new ArrayList<Material>();
 		Material materialObj = new Material();
		String materialValueArr[] = materialValues.split("--");
		if(materialValueArr[1].contains("Bamboo"))
		{
			materialValueArr[1]=materialValueArr[1].replace("Rayon Bamboo", "Other Fabric");

			materialValueArr[1]=materialValueArr[1].replace("Bamboo", "Other Fabric");
			
		}
		List<String> listOfLookupMaterial = getMaterialType(materialValueArr[1]
				.toUpperCase());
		//listOfLookupMaterial.remove(0);
		//listOfLookupMaterial.remove(2);

		if (StringUtils.isEmpty(materialValueArr[0])) {
			materialObj.setAlias(materialValueArr[1]);
		} else {
			materialObj.setAlias(materialValueArr[0]);
		}
		if (listOfLookupMaterial.size()==1){
			materialObj.setName(listOfLookupMaterial.get(0));
		} else if (listOfLookupMaterial.size()==2) {
			BlendMaterial blendObj=new BlendMaterial();
			BlendMaterial blendObj1=new BlendMaterial();

			materialObj.setName("Blend");
		    List<BlendMaterial> listOfBlend= new ArrayList<>();
		    blendObj.setPercentage("87");
		    blendObj.setName(listOfLookupMaterial.get(0));
		    blendObj1.setPercentage("13");
		    blendObj1.setName(listOfLookupMaterial.get(1));
		    listOfBlend.add(blendObj);
		    listOfBlend.add(blendObj1);
			materialObj.setBlendMaterials(listOfBlend);

			
		} else if (listOfLookupMaterial.size()==3) {
		}
		MaterialList.add(materialObj);
		return MaterialList;
	}

	public List<String> getMaterialType(String value) {
		List<String> listOfLookupMaterials = lookupServiceDataObj
				.getMaterialValues();
		List<String> finalMaterialValues = listOfLookupMaterials.stream()
				.filter(mtrlName -> value.contains(mtrlName))
				.collect(Collectors.toList());
		return finalMaterialValues;
	}

	
	public Size getSizeValue(String sizeValue, String genderName) {
		sizeValue=sizeValue.replace("XXL", "2XL").replace("&", "-");
      Size sizeObj=new Size();
    Apparel appObj=new Apparel();
    List<Value> otherList = new ArrayList<Value>();
    Value valueObj=new Value();
    Value ValueObj=new Value();
  
    OtherSize otherSize=new OtherSize(); 
    ValueObj.setValue(sizeValue);
    otherList.add(ValueObj);
    otherSize.setValues(otherList);
    sizeObj.setOther(otherSize);
    
/*    if(genderName.equalsIgnoreCase("Unisex"))
    {
    	String sizearr[]={"S","M","L","XL","2XL"};
		if(sizeValue.contains("XS"))
		{
			ArrayList<String> tempList = new ArrayList<String>(Arrays.asList(sizearr));
		    tempList.add("XS");
		    sizearr=tempList.toArray(new String[tempList.size()]);
			
		}
 		
 		for (String value : sizearr) {
 		 ValueObj = new Value();
 	     sizeObj.setApparel(appObj);
 		 appObj.setType("Standard & Numbered");
 		ValueObj.setValue(value);
 		listOfValue.add(ValueObj);
 		}
 		 appObj.setValues(listOfValue);
 	     sizeObj.setApparel(appObj);
    	
    }else{
    if(genderName.equalsIgnoreCase("Women's"))
    {
    	appObj.setType("Apparel-Womens");
    }
    else if(genderName.equalsIgnoreCase("Men's"))
    {
    	appObj.setType("Apparel-Mens");
    }   	
    	if(sizeValue.contains("-"))
    	{
    		String sizearr[]={"S","M","L","XL","2XL"};
    		if(sizeValue.contains("XS"))
    		{
    			ArrayList<String> tempList = new ArrayList<String>(Arrays.asList(sizearr));
    		    tempList.add("XS");
    		    sizearr=tempList.toArray(new String[tempList.size()]);
    		}
    		
			for (String sizeName : sizearr) {
    			ValueObj=new Value();
    			ValueObj.setValue(sizeName);
    			listOfValue.add(ValueObj);
			}
    	}else
    	{
			ValueObj.setValue(sizeValue);
			listOfValue.add(ValueObj);
    	}	
    	
    	appObj.setValues(listOfValue);
    	sizeObj.setApparel(appObj);
    }*/	
		return sizeObj;
	}

	
	
	
	
	
	
	
	
	
	
	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}

	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}

	public LookupRestService getLookupRestServiceObj() {
		return lookupRestServiceObj;
	}

	public void setLookupRestServiceObj(LookupRestService lookupRestServiceObj) {
		this.lookupRestServiceObj = lookupRestServiceObj;
	}


	


	

}
