package parser.brandwear;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.ShippingEstimate;
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

	public List<ImprintMethod> getImprintMethod(String imprintMethod) {
		List<ImprintMethod> ImprintMethodList = new ArrayList<ImprintMethod>();
		ImprintMethod imprintMethodObj = new ImprintMethod();
		List<String> finalImprintValues = getImprintValue(imprintMethod
				.toUpperCase());

		for (String string : finalImprintValues) {
			imprintMethodObj.setAlias(string);
			imprintMethodObj.setType(string);
			ImprintMethodList.add(imprintMethodObj);
		}
		return ImprintMethodList;
	}

	public List<String> getImprintValue(String value) {
		List<String> imprintLookUpValue = lookupServiceDataObj
				.getImprintMethods();
		List<String> finalImprintValues = imprintLookUpValue.stream()
				.filter(impntName -> value.contains(impntName))
				.collect(Collectors.toList());

		return finalImprintValues;
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
