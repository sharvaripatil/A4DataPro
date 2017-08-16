package parser.bellaCanvas;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.BlendMaterial;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.util.CommonUtility;

public class BellaCanvasProductAttributeParser {
	private LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;

	
	public List<Color> getColorCriteria(StringBuilder colorMapping) {
		String ColorValue=colorMapping.toString();
		ColorValue=ColorValue.replace("COLORBLOCK","").replace("HEATHER CVC","").replace("NEON", "")
				.replace("TRIBLEND","").replace("MINERAL WASH", "").replace("SOLID COLORBLOCK", "").
				replace("NEON COLORBLOCK", "").replace("TRIBLEND COLORBLOCK", "").
				replace("ACID WASH", "").replace("SPECKLED", "").replace("STRIPED", "");
		List<Color> listOfProductColors = new ArrayList<>();
		List<Combo> combolist = new ArrayList<>();
		Color colorObj = new Color();
		Combo comboObj=new Combo();
		
		String[] colorValues =ColorValue.split(",");
		
		for (String colorName : colorValues) {
			
			 colorObj = new Color();
			 comboObj=new Combo();
			String OriginalcolorName = colorName.trim();
			String colorLookUpName=BellaCanvasLookupData.COLOR_MAP.get(colorName.trim()).trim();
			if(colorName.contains("/"))
			{
				String comboArr[]=colorLookUpName.split("/");
				colorObj.setAlias(OriginalcolorName);
				colorObj.setName(comboArr[0]);		
				comboObj.setType("secondary");
				comboObj.setName(comboArr[1]);
				combolist.add(comboObj);
				colorObj.setCombos(combolist);
				
			}else
			{
			colorObj.setAlias(OriginalcolorName);
			colorObj.setName(colorLookUpName);	
			}
			listOfProductColors.add(colorObj);
		}	
		return listOfProductColors;
	}
	
	
	
	
	
	public Size getSize(String sizevalue) {
	
		Size sizeObj=new Size();
		Value ValueObj = new Value();
		Apparel appObj=new Apparel();
		String sizeLookUpName=BellaCanvasLookupData.SIZE_MAP.get(sizevalue.trim()).trim();
		List<Value> listOfValue = new ArrayList<>();

		String sizeArr[]=sizeLookUpName.split(",");
		
		//sizeObj.setApparel(apparel);
		
		for (String value : sizeArr) {
	 		 ValueObj = new Value();
	 	     sizeObj.setApparel(appObj);
	 		 appObj.setType("Standard & Numbered");
	 		ValueObj.setValue(value);
	 		listOfValue.add(ValueObj);
	 		}
	 		 appObj.setValues(listOfValue);
	 	     sizeObj.setApparel(appObj);		
		return sizeObj;
	}

	

	public List<Material> getMaterialValue(String material) {
		material=material.replace("²","");
		List<Material> materiallist = new ArrayList<Material>();
		Material materialObj = new Material();
		List<String> listOfLookupMaterial = getMaterialType(material
				.toUpperCase());
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
			else if(listOfLookupMaterial.size()==3)
			{


				
				
				
				
				
				
				
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

























	
	
	
}




