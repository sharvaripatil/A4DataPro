package parser.maxplus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.AvailableVariations;
import com.a4tech.product.model.BlendMaterial;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.util.CommonUtility;

public class MaxpluProductAttributeParser {
	private LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;
	
	
		

	public List<Material> getMaterialValue(String material) {
		material=material.replace("viscose", "Other Fabric");
		material=material.replace("²","");
		List<Material> materiallist = new ArrayList<Material>();
		Material materialObj = new Material();
		List<String> listOfLookupMaterial = getMaterialType(material
				.toUpperCase());
		if(listOfLookupMaterial.get(0).equalsIgnoreCase("OTHER"))
		{
			listOfLookupMaterial.remove(0);
			Collections.swap(listOfLookupMaterial, 0, 1);
			
		}
		String PercentageValue[]=material.split("%");
		PercentageValue[0]=PercentageValue[0].replace("[^0-9|.x%/ ]", "");
		
		if (!listOfLookupMaterial.isEmpty()) {	
			
			if (listOfLookupMaterial.size()==2) {
				BlendMaterial blendObj=new BlendMaterial();
				BlendMaterial blendObj1=new BlendMaterial();
                int PercentageValue1=100-Integer.parseInt(PercentageValue[0]);
                String PercentageValue2=Integer.toString(PercentageValue1);
				materialObj.setName("Blend");
			    List<BlendMaterial> listOfBlend= new ArrayList<>();
			    blendObj.setPercentage(PercentageValue[0]);
			    blendObj.setName(listOfLookupMaterial.get(0));
			    blendObj1.setPercentage(PercentageValue2);
			    blendObj1.setName(listOfLookupMaterial.get(1));
			    listOfBlend.add(blendObj);
			    listOfBlend.add(blendObj1);
			    materialObj.setAlias(material);
				materialObj.setBlendMaterials(listOfBlend);
			
			}
			else
			{		
				materialObj = getMaterialValue(listOfLookupMaterial.toString(),
						material);
			}
				materiallist.add(materialObj);	
							
		}		
		
		return materiallist;
	}	
	
	public List<String> getMaterialType(String value){
		List<String> listOfLookupMaterials = lookupServiceDataObj.getMaterialValues();
		List<String> finalMaterialValues = listOfLookupMaterials.stream()
				                                  .filter(mtrlName -> value.contains(mtrlName))
				                                  .collect(Collectors.toList());
                                                 
				
		return finalMaterialValues;	
	}
		
	public Material getMaterialValue(String name,String alias){
		Material materialObj = new Material();
		name = CommonUtility.removeCurlyBraces(name);
		materialObj.setName(name);
		materialObj.setAlias(alias);
		return materialObj;
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



 

	public List<Availability> getAvailability(Size sizeObj,
			List<Color> colorlist) {

		List<Availability> listOfavaibility = new ArrayList<Availability>();

		Availability availabilityObj=new Availability();
				
		List<AvailableVariations>  avaiVaraitionList= new ArrayList<AvailableVariations>(); 
		AvailableVariations VariationObj=new AvailableVariations();
		
		List<Object>  colorObjectList= new ArrayList<Object>(); 
		List<Object>  sizeList= new ArrayList<Object>(); 
		
		for (Object object : colorlist) {
			colorObjectList.add(object);
		}
		
		List<Value> sizevalue=sizeObj.getApparel().getValues();
		
		List<Object>  colorsObjectList= new ArrayList<Object>(); 

		
		availabilityObj.setParentCriteria("Product Color");
		availabilityObj.setChildCriteria("Size");
		

		for(int i=0;i<colorObjectList.size();i++){
			
			 for(int j=0;j<sizevalue.size();j++){
			  String ColorArr=colorlist.get(i).getAlias().toString();		 
		
			  String SizeArr=sizevalue.get(j).getValue().toString();
				
			   VariationObj = new AvailableVariations();
			  colorsObjectList= new ArrayList<Object>(); 
			  sizeList= new ArrayList<Object>(); 
				
				
				  colorsObjectList.add(ColorArr);
				  sizeList.add(SizeArr);
				  VariationObj.setParentValue(colorsObjectList);
				  VariationObj.setChildValue(sizeList);
				   avaiVaraitionList.add(VariationObj);
			 }
		//	}		
			
		}
		
		availabilityObj.setAvailableVariations(avaiVaraitionList);
	    listOfavaibility.add(availabilityObj);
		 
		return listOfavaibility;
	}

























	
	
	
}




