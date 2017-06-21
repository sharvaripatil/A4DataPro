package parser.brandwear;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Weight;

public class BrandwearProductAttribure {
	private LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;

	public ShippingEstimate getshippingWeight(String shippingWeight) {

		ShippingEstimate shippingObj = new ShippingEstimate();

		List<Weight> WeightList = new ArrayList<Weight>();
		Weight objWeight = new Weight();
		shippingWeight = shippingWeight.replaceAll("oz. / Item", "");
		objWeight.setValue(shippingWeight);
		objWeight.setUnit("oz");
		WeightList.add(objWeight);
		shippingObj.setWeight(WeightList);
		return shippingObj;
	}

	
	public List<Color> getColorValue(String colorValue) {
		if(colorValue.contains("&"))
		{
			colorValue=colorValue.replace("&", "/");	
		}
		
		List<Color> colorList = new ArrayList<Color>();
		Color colorObj=new Color();
	    String colorArr[]=colorValue.split(",");
		Combo combovalue = new Combo();

	    
	    for (String colorsValue : colorArr) {
	    	colorObj=new Color();	
	    	
	    	if(colorsValue.contains("/"))
	    	{
	    	combovalue = new Combo();
	    	List<Combo> combolist = new ArrayList<Combo>();
	    	String ComboColorArr[]=	colorsValue.split("/");
	    	colorObj.setAlias(colorsValue);
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
	  if(imprintMethodValue.contains("printed"))
	  {
		  imprMethod.setType("Pad Print");
	  }else if(imprintMethodValue.contains("Laser Etched"))
	  {
		  imprMethod.setType("Etched");
	  }else if(imprintMethodValue.contains("Debossed"))
	  {
		  imprMethod.setType("Debossed");

	  }else if(imprintMethodValue.contains("Embossed"))
	  {
		  imprMethod.setType("Embossed");
	  }else if(imprintMethodValue.contains("Laser engraved"))
	  {
		  imprMethod.setType("Laser engraved");
	  }else if(imprintMethodValue.contains("Full Color"))
	  {
		  imprMethod.setType("Full Color");
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
		List<String> listOfLookupMaterial = getMaterialType(materialValueArr[1]
				.toUpperCase());
		if (StringUtils.isEmpty(materialValueArr[0])) {
			materialObj.setAlias(materialValueArr[1]);
		} else {
			materialObj.setAlias(materialValueArr[0]);
		}
		if ("%".length() == 1) {
			materialObj.setName(listOfLookupMaterial.get(0));
		} else if ("%".length() == 2) {
		} else if ("%".length() == 3) {
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

	
	public Size getImprintMethod(String sizeValue, String genderName) {
		sizeValue=sizeValue.replace("XXL", "2XL").replace("&", "-");
      Size sizeObj=new Size();
    Apparel appObj=new Apparel();
    List<Value> listOfValue= new ArrayList<>();
    Value ValueObj=new Value();
    
    if(genderName.equalsIgnoreCase("Unisex"))
    {
 		String sizeArr[]=sizeValue.split("-");
 		
 		for (String value : sizeArr) {
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
    		String sizearr[]=sizeValue.split("-");
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
    }	
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
