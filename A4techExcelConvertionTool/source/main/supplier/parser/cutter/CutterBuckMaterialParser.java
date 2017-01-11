package parser.cutter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.model.BlendMaterial;
import com.a4tech.product.model.Material;
import com.a4tech.util.CommonUtility;

public class CutterBuckMaterialParser {
	

	private LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;
	
	public List<Material> getMaterialList(String materialValue1) {
		Material materialObj = new Material();
		List<Material> listOfMaterial = new ArrayList<>();
	
		
       
		String finalTempAliasVal=materialValue1;
		 if(materialValue1.contains("cotton fleece"))
	        {
	        	materialValue1=materialValue1.replace("cotton fleece","Cotton");
	        }
		
		List<String> listOfLookupMaterial = getMaterialType(materialValue1.toUpperCase());
		if(!listOfLookupMaterial.isEmpty()){
			int numOfMaterials = listOfLookupMaterial.size();
	

			boolean flag=false;
	
					if(numOfMaterials == 1 && !flag){ // this condition used to single material value(E.X 100% Cotton)	  
				
				materialObj = getMaterialValue(listOfLookupMaterial.toString(), finalTempAliasVal);//
				  listOfMaterial.add(materialObj);
				  
				  
			  }else if(numOfMaterials == 2 || numOfMaterials == 3){   // this condition for blend material
				  
				String[] values = materialValue1.split(",");

				  if(materialValue1.contains("Cotton/Coton"))
			        {
			        	materialValue1=materialValue1.replace("Cotton/Coton","Cotton");
			        }
				 
				
				
		    	 BlendMaterial blentMaterialObj = null;
		    	 List<BlendMaterial> listOfBlendMaterial = new ArrayList<>();
		    
		    	  if(values.length == 2 || values.length == 3 ||values.length == 4 ){
		    		  if(values.length == 4 && values[2].contains("Spandex"))
		    		  {
		    			  values[2]=values[2].replaceAll(values[2], "");
		    		  }
		    		 
		    		  int percentage3=0;
				    	 for (String materialValue : values) {
				    		 blentMaterialObj = new BlendMaterial();
				    		 String mtrlType = getMaterialType(materialValue.toUpperCase()).toString();
				    		
				    		 if(materialValue.contains("%")){
						 		  String percentage1 = materialValue.split("%")[0];
						 		 if(materialValue==values[0])
								  {
						           int percentage2 = Integer.parseInt(percentage1);
								   percentage3=(100-percentage2);
								  }
							
								  materialObj.setName("BLEND");
								  materialObj.setAlias(finalTempAliasVal); 
								  
								  if(!StringUtils.isEmpty(mtrlType)){
									  mtrlType=CommonUtility.removeCurlyBraces(mtrlType);
								  }
								  if(!StringUtils.isEmpty(mtrlType)){
								  blentMaterialObj.setName(mtrlType);
								  }else{
									  blentMaterialObj.setName("Other Fabric"); 
								  }
								  blentMaterialObj.setPercentage(percentage1);
								  if(materialValue==values[1])
								  {
									  blentMaterialObj.setPercentage(Integer.toString(percentage3));
								  }
								  
								
								  listOfBlendMaterial.add(blentMaterialObj);
							  }
				    		
				    	 }
				    	    materialObj.setBlendMaterials(listOfBlendMaterial);
				    		listOfMaterial.add(materialObj);
				     }
				     
				     
				     
				     
			  }
		}else{ // used for Material is not available in lookup, then it goes in Others
			if(materialValue1.equalsIgnoreCase("Unassigned")){
				
			}else{
			materialObj = getMaterialValue("Other",finalTempAliasVal);
			listOfMaterial.add(materialObj);
			}
		}
		
	return listOfMaterial;
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
