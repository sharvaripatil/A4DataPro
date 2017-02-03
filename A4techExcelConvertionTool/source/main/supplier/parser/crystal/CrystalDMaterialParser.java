package parser.crystal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Material;
import com.a4tech.util.CommonUtility;

public class CrystalDMaterialParser {

	private LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;

	public List<Material> getMaterialList(String materialValue1) {

		Material materialObj = new Material();
		List<Material> listOfMaterial = new ArrayList<>();

		String finalTempAliasVal = materialValue1;

		List<String> listOfLookupMaterial = getMaterialType(materialValue1
				.toUpperCase());

		int numOfMaterials = listOfLookupMaterial.size();

		if (!listOfLookupMaterial.isEmpty()) {

			if (materialValue1.contains("/")) { // combo for 1 value in lookup
												
				if (numOfMaterials == 1) {
					materialObj = new Material();
					materialObj = getMaterialValue(
							listOfLookupMaterial.toString(), finalTempAliasVal);
					materialObj.setAlias(finalTempAliasVal);
					Combo comboObj = new Combo();
					comboObj.setName("Other");
					materialObj.setCombo(comboObj);

					listOfMaterial.add(materialObj);
					//return listOfMaterial;
					
				} else if (numOfMaterials == 2) { // combo for 2 value in lookup
					materialObj = new Material();

					String Combo1 = listOfLookupMaterial.get(0);
					String Combo2 = listOfLookupMaterial.get(1);
					Combo comboObj = new Combo();
					materialObj.setName(Combo1);
					materialObj.setAlias(finalTempAliasVal);
					comboObj.setName(Combo2);
					materialObj.setCombo(comboObj);

					listOfMaterial.add(materialObj);
					//return listOfMaterial;

				}
			} else { //only sing value present in lookup
				materialObj = getMaterialValue(listOfLookupMaterial.toString(),
						finalTempAliasVal);
				listOfMaterial.add(materialObj);
			}

		} else { // used for Material is not available in lookup, then it goes in Others
					
			materialObj = getMaterialValue("Other", finalTempAliasVal);
			listOfMaterial.add(materialObj);

		}

		return listOfMaterial;
	}

	public List<String> getMaterialType(String value) {
		List<String> listOfLookupMaterials = lookupServiceDataObj
				.getMaterialValues();
		List<String> finalMaterialValues = listOfLookupMaterials.stream()
				.filter(mtrlName -> value.contains(mtrlName))
				.collect(Collectors.toList());

		return finalMaterialValues;
	}

	public Material getMaterialValue(String name, String alias) {
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
