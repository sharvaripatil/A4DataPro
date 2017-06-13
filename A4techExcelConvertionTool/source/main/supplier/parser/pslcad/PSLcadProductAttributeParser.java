package parser.pslcad;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.model.BatteryInformation;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class PSLcadProductAttributeParser {

	private LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;

	public List<BatteryInformation> getBatteyInfo(String BatteryInfo) {
		List<BatteryInformation> batteryInfoList = new ArrayList<BatteryInformation>();
		BatteryInformation batinfoObj = new BatteryInformation();
		batinfoObj.setName(BatteryInfo);
		batteryInfoList.add(batinfoObj);
		return batteryInfoList;
	}

	public List<Packaging> getPackageInfo(String packageInfo) {
		List<Packaging> listOfPackaging = new ArrayList<Packaging>();
		Packaging packageObj = new Packaging();
		packageObj.setName(packageInfo);
		listOfPackaging.add(packageObj);
		return listOfPackaging;
	}

	public List<ImprintMethod> getImprintMethodValue(String Imprintmethod) {
		List<ImprintMethod> imprintMethodsList = new ArrayList<ImprintMethod>();
		Imprintmethod = Imprintmethod.replace("engraved", "").replace(
				"Silk screen", "SILKSCREEN");
		ImprintMethod imprMethod = new ImprintMethod();
		List<String> finalImprintValues = getImprintValue(Imprintmethod
				.toUpperCase());
		String imprintMethodArr[] = Imprintmethod.split(",");
		for (String Value : imprintMethodArr) {
			if (Value.contains("logo") /* || Value.contains("print") */) {
				ImprintMethod imprMethod1 = new ImprintMethod();
				imprMethod1.setAlias(Value);
				imprMethod1.setType("OTHER");
				imprintMethodsList.add(imprMethod1);
			} else if (Value.contains("Laser")) {
				ImprintMethod imprMethod2 = new ImprintMethod();
				imprMethod2.setAlias("LASER ENGRAVED");
				imprMethod2.setType("LASER ENGRAVED");
				imprintMethodsList.add(imprMethod2);
			}
		}
		for (String innerValue : finalImprintValues) {
			imprMethod = new ImprintMethod();
			imprMethod.setAlias(innerValue);
			imprMethod.setType(innerValue);
			imprintMethodsList.add(imprMethod);
		}
		return imprintMethodsList;
	}

	public List<String> getCompliance(String certificateValue) {
		List<String> complianceList = new ArrayList<String>();
		String complainceArr[] = certificateValue.split(",");
		for (String Value : complainceArr) {
			complianceList.add(Value);
		}
		return complianceList;
	}

	public List<Material> getMaterialList(String materialValue) {
		String MaterialCombo = materialValue;
		List<Material> listOfMaterial = new ArrayList<>();
		Material materialObj = new Material();
		MaterialCombo = MaterialCombo.replace("+", ",")
				.replace("Cloth", "Other Fabric").replace("/", ",");
		if (MaterialCombo.contains(",")) {
			String MaterialArr[] = MaterialCombo.split(",");
			MaterialCombo = "";
			MaterialCombo = MaterialCombo.concat(MaterialArr[0]).concat(",")
					.concat(MaterialArr[1]);
		}
		if (MaterialCombo.contains("Abs plastic")
				|| MaterialCombo.contains("Abs plastics")) {
			List<String> listOfLookupMaterial = getMaterialType(MaterialCombo
					.toUpperCase());
			materialObj = new Material();
			String Combo1 = "Other";
			String Combo2 = listOfLookupMaterial.get(2);
			Combo comboObj = new Combo();
			materialObj.setName(Combo1);
			materialObj.setAlias(materialValue);
			comboObj.setName(Combo2);
			materialObj.setCombo(comboObj);
			listOfMaterial.add(materialObj);
		} else {
			if (MaterialCombo.contains("Abs") || MaterialCombo.contains("ABS")) {
				MaterialCombo = MaterialCombo.replace("Abs", "Abs  ").replace(
						"ABS", "ABS  ");
			}
			List<String> listOfLookupMaterial = getMaterialType(MaterialCombo
					.toUpperCase());
			int numOfMaterials = listOfLookupMaterial.size();
			if (!listOfLookupMaterial.isEmpty()) {
				if (numOfMaterials == 1) {
					materialObj = new Material();
					materialObj = getMaterialValue(
							listOfLookupMaterial.toString(), MaterialCombo);
					listOfMaterial.add(materialObj);
				} else {
					materialObj = new Material();
					String Combo1 = listOfLookupMaterial.get(0);
					String Combo2 = listOfLookupMaterial.get(1);
					Combo comboObj = new Combo();
					materialObj.setName(Combo1);
					materialObj.setAlias(materialValue);
					comboObj.setName(Combo2);
					if (materialValue.contains(" Cloth")) {
						comboObj.setName(listOfLookupMaterial.get(2));
					}
					materialObj.setCombo(comboObj);
					listOfMaterial.add(materialObj);
				}
			} else {
				materialObj = getMaterialValue("Other", materialValue);
				listOfMaterial.add(materialObj);
			}
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

	public List<String> getImprintValue(String value) {
		List<String> imprintLookUpValue = lookupServiceDataObj
				.getImprintMethods();
		List<String> finalImprintValues = imprintLookUpValue.stream()
				.filter(impntName -> value.contains(impntName))
				.collect(Collectors.toList());
		return finalImprintValues;
	}

	public Size getSizeValue(String productSize) {
		String SizeforUnit = productSize;
		String SizeUnit = null;
		if (SizeforUnit.contains("inch")) {
			SizeUnit = "in";
		} else if (SizeforUnit.contains("cm")) {
			SizeUnit = "cm";
		}
		productSize = productSize.replace("inch", "").replace("cm", "");
		Size sizeObj = new Size();
		List<Values> listOfValues = new ArrayList<>();
		Values ValuesObj = new Values();
		List<Value> listOfValue = new ArrayList<>();
		Value ValueObj = new Value();
		Dimension dimensionObj = new Dimension();
		if (productSize.contains("dia")) {

			productSize = productSize.replace("dia", "");
			ValueObj = new Value();
			ValueObj.setUnit(SizeUnit);
			if (productSize.contains("x")) {
				String productSizeArr[] = productSize.split("x");
				for (int sizeValue = 0; sizeValue < productSizeArr.length; sizeValue++) {
					ValueObj = new Value();
					ValueObj.setUnit(SizeUnit);
					ValueObj.setValue(productSizeArr[sizeValue]);
					if (sizeValue == 0) {
						ValueObj.setAttribute("Length");
					} else if (sizeValue == 1) {
						ValueObj.setAttribute("Dia");
					}
					listOfValue.add(ValueObj);

				}

			} else {
				ValueObj.setValue(productSize);
				ValueObj.setAttribute("Dia");
				listOfValue.add(ValueObj);

			}
		} else if (productSize.contains("x")) {
			String productSizeArr[] = productSize.split("x");
			for (int sizeValue = 0; sizeValue < productSizeArr.length; sizeValue++) {
				// sizeObj=new Size();
				ValueObj = new Value();
				ValueObj.setValue(productSizeArr[sizeValue]);
				ValueObj.setUnit(SizeUnit);

				if (sizeValue == 0) {
					ValueObj.setAttribute("Length");
				} else if (sizeValue == 1) {
					ValueObj.setAttribute("Width");
				} else if (sizeValue == 2) {
					ValueObj.setAttribute("Height");
				}
				listOfValue.add(ValueObj);
			}
		}
		ValuesObj.setValue(listOfValue);
		listOfValues.add(ValuesObj);
		dimensionObj.setValues(listOfValues);
		sizeObj.setDimension(dimensionObj);

		return sizeObj;
	}

	public List<Color> getColorCriteria(String colorValue) {
		List<Color> listOfProductColors = new ArrayList<>();

		Color colorObj = new Color();
		colorValue = colorValue.replace("/", ",");
		String[] colorValues = CommonUtility.getValuesOfArray(colorValue,
				ApplicationConstants.CONST_DELIMITER_COMMA);
		for (String colorName : colorValues) {
			colorObj = new Color();
			colorName = colorName.trim();
			colorObj.setAlias(colorName);
			colorObj.setName(PSLLookupColor.COLOR_MAP.get(colorName.trim()));
			listOfProductColors.add(colorObj);
		}

		return listOfProductColors;
	}

	public ShippingEstimate getShippingInfo(StringBuilder shippingEstimation) {
		ShippingEstimate shippingEstimationObj = new ShippingEstimate();
		String shippingArr[] = shippingEstimation.toString().split("@@");
		List<NumberOfItems> listOfNumberOfItems = new ArrayList<>();
		NumberOfItems NumberOfItemsObj = new NumberOfItems();
		NumberOfItemsObj.setValue(shippingArr[0]);
		NumberOfItemsObj.setUnit("Box");
		listOfNumberOfItems.add(NumberOfItemsObj);
		shippingEstimationObj.setNumberOfItems(listOfNumberOfItems);

		List<Weight> listOfWeight = new ArrayList<>();
		Weight wightObj = new Weight();
		wightObj.setValue(shippingArr[2]);
		wightObj.setUnit("lbs");
		listOfWeight.add(wightObj);
		shippingEstimationObj.setWeight(listOfWeight);

		if (!shippingArr[1].contains("null")) {
			Dimensions dimensionObj = new Dimensions();
			String shippingDimensioneArr[] = shippingArr[1].split("x");

			dimensionObj.setLength(shippingDimensioneArr[0]);
			dimensionObj.setLengthUnit("in");

			dimensionObj.setWidth(shippingDimensioneArr[1]);
			dimensionObj.setWidthUnit("in");

			dimensionObj.setHeight(shippingDimensioneArr[2]);
			dimensionObj.setHeightUnit("in");

			shippingEstimationObj.setDimensions(dimensionObj);
		}

		return shippingEstimationObj;
	}

	public Volume getItemweight(String productWeight) {
		Volume itemWeightObj = new Volume();
		List<Values> listOfValues = new ArrayList<>();
		Values valuesObj = new Values();
		List<Value> listOfValue = new ArrayList<>();
		Value valueObj = new Value();
		valueObj.setValue(productWeight);
		valueObj.setUnit("lbs");
		listOfValue.add(valueObj);
		valuesObj.setValue(listOfValue);
		listOfValues.add(valuesObj);
		itemWeightObj.setValues(listOfValues);
		return itemWeightObj;
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
