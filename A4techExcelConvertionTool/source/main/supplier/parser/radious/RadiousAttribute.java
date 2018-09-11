package parser.radious;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Weight;


public class RadiousAttribute {

	private static LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;
	
	public ShippingEstimate getShippingEstimates(String cartonWeight,
			String cartonWidth, String cartonHeight, String cartonLength, String unitsperCarton) {

		
		ShippingEstimate ItemObject = new ShippingEstimate();
		
		List<NumberOfItems> listOfNumberOfItems = new ArrayList<NumberOfItems>();

		List<Object> shipingEstObj1 = new ArrayList<Object>();
		    NumberOfItems itemObj = new NumberOfItems();
		    if(!StringUtils.isEmpty(unitsperCarton)){
			itemObj.setUnit("Carton");
			itemObj.setValue(unitsperCarton);
			shipingEstObj1.add(itemObj);
			ItemObject.setNumberOfItems(listOfNumberOfItems);
		    }
	
	
		List<Weight> listOfWeight = new ArrayList<Weight>();
	    if(!StringUtils.isEmpty(cartonWeight)){

			Weight weightObj = new Weight();
			weightObj.setUnit("lbs");
			weightObj.setValue(cartonWeight);
			listOfWeight.add(weightObj);
			ItemObject.setWeight(listOfWeight);
	    }
	
		
			List<Dimensions> dimenlist = new ArrayList<Dimensions>();
			Dimensions dimensionObj = new Dimensions();
		 
			
		    if(!StringUtils.isEmpty(cartonLength)){

	        dimensionObj.setHeight(cartonHeight);
			dimensionObj.setHeightUnit("in");
					
			dimensionObj.setLength(cartonLength);
			dimensionObj.setLengthUnit("in");

			dimensionObj.setWidth(cartonWidth);
			dimensionObj.setWidthUnit("in");

			dimenlist.add(dimensionObj);
			ItemObject.setDimensions(dimensionObj);
		    }
			
		
		return ItemObject;

	}

	public static List<Material> getMaterial(String material) {

		List<Material> listOfMaterial = new ArrayList<Material>();		

		Material materialObj=new Material();
		material=material.replace("/", "").trim();
	    String materailArr[]=material.split(",");

	    for (String materialName : materailArr) {

	    	
	    	
	    	materialObj.setAlias(materialName);
	    	materialObj.setName("Other");
		}
	
	    listOfMaterial.add(materialObj);
	    
		return listOfMaterial;
	}

	
	
	public static Size getSize(String size) {

		Size sizeObj=new Size();
		String originalSize=size;
		size=size.replaceAll("[^0-9\"\'.]","").replace("\"", "").replace("\'", "");

		 Dimension dimensionObj=new Dimension();
		 
	     List<Values> listOfValues= new ArrayList<>();
	      Values ValuesObj=new Values();
	      List<Value> listOfValue= new ArrayList<>();
	      Value ValueObj=new Value();
		
		if(originalSize.contains("x")){
		String sizeArr[]=size.split("x");
		
		for (int i=0;i<sizeArr.length;i++) {
			
			    ValueObj=new Value();
	     		ValueObj.setUnit("in");
	     		
	     		if(!StringUtils.isEmpty(sizeArr[0])){
	     		ValueObj.setValue(sizeArr[0]);
	   	     	ValueObj.setAttribute("Height");
	     		}else if(!StringUtils.isEmpty(sizeArr[1])){
	    	     ValueObj.setValue(sizeArr[1]);
	    	   	 ValueObj.setAttribute("Length");
	    	     }else if(!StringUtils.isEmpty(sizeArr[2])){
		    	 ValueObj.setValue(sizeArr[2]);
		    	  ValueObj.setAttribute("Width");
		    	 }

	     		listOfValue.add(ValueObj);
	   	
		     	}
			
		}
		 else
		{
			    ValueObj=new Value();
	     		ValueObj.setUnit("ft");
	     		ValueObj.setValue(size);
	   	     	ValueObj.setAttribute("Height");
	     		listOfValue.add(ValueObj);

		}
		
	   	ValuesObj.setValue(listOfValue);
				listOfValues.add(ValuesObj);	
		 		dimensionObj.setValues(listOfValues);
		       sizeObj.setDimension(dimensionObj);
		return sizeObj;
	}

	public static List<ImprintMethod> getImprintMethod(String imprintMethod) {
		
		List<ImprintMethod> listOfImprintMethods = new ArrayList<ImprintMethod>();
		ImprintMethod imprintMethodObj=new ImprintMethod();
		
		
		imprintMethodObj.setAlias(imprintMethod);

		if(imprintMethod.contains("Unprinted"))
		{

			imprintMethodObj.setType("Unimprinted");

		}else {
			imprintMethodObj.setType("Printed");
		}

		listOfImprintMethods.add(imprintMethodObj);
		
		return listOfImprintMethods;
	}	
	
}
