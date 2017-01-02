package parser.cutter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.broberry.parser.BroberryProductMaterialParser;
import com.a4tech.product.model.BlendMaterial;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Material;
import com.a4tech.util.CommonUtility;

public class CutterBuckMaterialParser {
	

	private LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;
	
	public List<Material> getMaterialList(String materialValue1) {
		Material materialObj = new Material();
		List<Material> listOfMaterial = new ArrayList<>();
		
       
		String finalTempAliasVal=materialValue1;
		/*String tempAliasValArr[]=materialValue1.split("%");
		materialValue1=tempAliasValArr[1];*/
		
		//if(tempAliasValArr.length==2){
		//	finalTempAliasVal=tempAliasValArr[1];
		
	
		List<String> listOfLookupMaterial = getMaterialType(materialValue1.toUpperCase());
		if(!listOfLookupMaterial.isEmpty()){
			int numOfMaterials = listOfLookupMaterial.size();
	
			String valuesTempArr[]=null;
			boolean flag=false;
		/*	if(materialValue1.contains("/"))
			{
				valuesTempArr = CommonUtility.getValuesOfArray(materialValue1,"/");
				flag=true;
			}else if(materialValue1.contains(","))
			{
				valuesTempArr = CommonUtility.getValuesOfArray(materialValue1,",");
				flag=true;
			}*/
			
		
			  //if(numOfMaterials == 1){ // this condition used to single material value(E.X 100% Cotton)
			if(numOfMaterials == 1 && !flag){ // this condition used to single material value(E.X 100% Cotton)	  
				
				materialObj = getMaterialValue(listOfLookupMaterial.toString(), finalTempAliasVal);//+" "+finalTempAliasVal);
				  listOfMaterial.add(materialObj);
			  }else if(isBlendMaterial(materialValue1)){   // this condition for blend material
				  
				  if(materialValue1.contains("Cotton/Coton"))
			        {
			        	materialValue1=materialValue1.replace("Cotton/Coton","Cotton");
			        }
					  
				  
				String[] values = materialValue1.split(",");
				/*if(materialValue1.contains("/"))
				{
				 values = CommonUtility.getValuesOfArray(materialValue1,"/");
				}else if(materialValue1.contains("_"))
				{
					 values = CommonUtility.getValuesOfArray(materialValue1,"_");
				}*/
				
		    	 BlendMaterial blentMaterialObj = null;
		    	 List<BlendMaterial> listOfBlendMaterial = new ArrayList<>();
				     if(values.length == 2 || values.length == 3){
				    	 for (String materialValue : values) {
				    		 blentMaterialObj = new BlendMaterial();
				    		 String mtrlType = getMaterialType(materialValue.toUpperCase()).toString();
				    		 if(materialValue.contains("%")){
								  String percentage = materialValue.split("%")[0];
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
								  blentMaterialObj.setPercentage(percentage);
								  listOfBlendMaterial.add(blentMaterialObj);
							  }
				    		
				    	 }
				    	    materialObj.setBlendMaterials(listOfBlendMaterial);
				    		listOfMaterial.add(materialObj);
				     }/*else if(values.length == 4) { // this condition for combo and blend values
				    	 Combo comboObj = new Combo();
			    	
				    	 int secondValuePercentage = 0  ;
				    		 for(int i=0;i<2;i++){
				    		 blentMaterialObj = new BlendMaterial();
							  String mtrlType = getMaterialType(values[i].toUpperCase()).toString();
							  if(values[i].contains("%")){
								  String percentage = values[0].split("%")[0];
								  if(i == 0){
									  secondValuePercentage = 100-Integer.parseInt(percentage);
								  }
								  if(!StringUtils.isEmpty(mtrlType)){
								  mtrlType=CommonUtility.removeCurlyBraces(mtrlType);
								  }
								  if(!StringUtils.isEmpty(mtrlType)){
									  blentMaterialObj.setName(mtrlType);  
								  }else{
									blentMaterialObj.setName("Other Fabric");  
								  }
									 if(i ==0){
										  blentMaterialObj.setPercentage(percentage);
									  }else{
										  blentMaterialObj.setPercentage(Integer.toString(secondValuePercentage));
									  }
								  
								 else if(values[i].contains("%")){
								 blentMaterialObj.setPercentage(Integer.toString(percentage1));
								  }
								  listOfBlendMaterial.add(blentMaterialObj);

							  }
							  else{
								  materialObj.setName(CommonUtility.removeCurlyBraces(mtrlType));
								  materialObj.setAlias(materialValue1+" "+finalTempAliasVal);  
							  }
						} //[70% Modacrylic , 25% Cotton , 5%],[59% Cotton, 39% Polyester, 2% Sp]
				    	 String valuesTemp[]=values[2].split("%");
				    	 
				    	 if( valuesTemp.length==1){
				    		 materialObj.setName("Other");
				    		// materialObj.setAlias(values[1]);//
				    		 materialObj.setAlias(materialValue1+" "+finalTempAliasVal);
				    	 }else if(valuesTemp.length==2){
				    		 //materialObj.setName(valuesTemp[1]);
				    		 //materialObj.setName(values[2].toString());/////////////////imp thingtoday
				    		 List<String> listOfLookupMaterialTemp = getMaterialType(values[2].toString().toUpperCase());
				    		if(!listOfLookupMaterialTemp.isEmpty()){
				    			materialObj.setName(listOfLookupMaterialTemp.get(0));
				    		}else{
				    			materialObj.setName(values[2].toString());
				    		}
				    		
				    		// materialObj.setName(valuesTemp[2]);
				    		 materialObj.setAlias(materialValue1+" "+finalTempAliasVal); 
				    	 }
				    	 comboObj.setBlendMaterials(listOfBlendMaterial);
				    	 comboObj.setName("Blend");
				    	 materialObj.setCombo(comboObj);
				    	 listOfMaterial.add(materialObj);
				     }	        */
			  }
		}else{ // used for Material is not available in lookup, then it goes in Others
			if(materialValue1.equalsIgnoreCase("Unassigned")){
				
			}else{
			materialObj = getMaterialValue("Other", materialValue1+" "+finalTempAliasVal);
			listOfMaterial.add(materialObj);
			}
		}
	//	tempAliasValArr=new String[tempAliasValArr.length];;
		
	//	}
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
	/*public Material getMaterialValue(String name,String alias ,String materialType){
		Material materialObj = new Material();
		 Combo comboObj = null;
		 String[] materials = null;
		 name = CommonUtility.removeCurlyBraces(name);
		if(name.contains(",")){
			materials = name.split(","); 
			materialObj.setName(materials[0]);
			materialObj.setAlias(alias);
			comboObj = new Combo();
        	comboObj.setName(materials[1]);
        	materialObj.setCombo(comboObj);
		}
		return materialObj;
	}*/
/*	public boolean iscombo(String data){ // 70% Modacrylic /25% Cotton /5% 
		  if(data.split("_").length ==3){
			  return true;
		  }
		  else if(data.split("/").length ==3){
			  return true;
		  }
		return false;
	}*/
	public boolean isBlendMaterial(String data){
		//if(data.contains("%"))
		if(data.contains("/"))
		{
			return true;
		}else if(data.contains("_")){
			return true;
		}
		
		/*if(data.split("_").length ==2 ){ //51% Polyester/49% Cocona® 37.5
			return true;
		}else if(data.split("/").length ==2){  //55% Cotton_45% Polyester
			return true;}*/
		return false;
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
