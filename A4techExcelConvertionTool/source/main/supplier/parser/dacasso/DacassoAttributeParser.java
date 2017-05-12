package parser.dacasso;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class DacassoAttributeParser {

	public List<Origin> getProductOrigin(String originVal) {
		List<Origin> listOfOrigin = new ArrayList<>();
		Origin originObj = new Origin();
		if (originVal.equalsIgnoreCase("United States")) {
			originVal = "U.S.A.";
		}
		originObj.setName(originVal);
		listOfOrigin.add(originObj);
		return listOfOrigin;
	}

	public List<ImprintSize> getProductImprintSize(String val) {
		List<ImprintSize> listOfImprintSize = new ArrayList<>();
		ImprintSize imprintSizeObj = new ImprintSize();
		imprintSizeObj.setValue(val);
		listOfImprintSize.add(imprintSizeObj);
		return listOfImprintSize;
	}

	public List<ImprintLocation> getProductImprintLocation(String locationVal) {
		List<ImprintLocation> listOfImprintLocation = new ArrayList<>();
		ImprintLocation imprintLocObj = null;
		String[] locationVals = CommonUtility.getValuesOfArray(locationVal,
				ApplicationConstants.CONST_STRING_COMMA_SEP);
		for (String locationName : locationVals) {
			imprintLocObj = new ImprintLocation();
			imprintLocObj.setValue(locationName);
			listOfImprintLocation.add(imprintLocObj);
		}
		return listOfImprintLocation;
	}

	public Size getProductSize(String value) {
		Size sizeObj = new Size();
		Dimension dimensionObj = new Dimension();
		List<Values> listOfValues = getSizeOfListValues(value);
		dimensionObj.setValues(listOfValues);
		sizeObj.setDimension(dimensionObj);
		return sizeObj;
	}

	private List<Values> getSizeOfListValues(String siezVal) {
		siezVal = siezVal.replaceAll("\"", "");
		if(siezVal.contains("W")){
			siezVal = siezVal.replaceAll("W", "");
			siezVal = siezVal.replaceAll("H", "");
		}
		String[] sizeVals = CommonUtility.getValuesOfArray(siezVal, "x");
		Value valObj1 = null;
		Value valObj2 = null;
		Value valObj3 = null;
		Values valuesObj = new Values();
		List<Value> listOfValue = new ArrayList<>();
		List<Values> listOfValues = new ArrayList<>();
		if (sizeVals.length == ApplicationConstants.CONST_INT_VALUE_TWO) {
			valObj1 = getValueObj(sizeVals[0].trim(), "Width", ApplicationConstants.CONST_STRING_INCHES);
			valObj2 = getValueObj(sizeVals[1].trim(), "Height", ApplicationConstants.CONST_STRING_INCHES);
			listOfValue.add(valObj1);
			listOfValue.add(valObj2);
		} else if (sizeVals.length == ApplicationConstants.CONST_INT_VALUE_THREE) {
			valObj1 = getValueObj(sizeVals[0].trim(), "Length", ApplicationConstants.CONST_STRING_INCHES);
			valObj2 = getValueObj(sizeVals[1].trim(), "Width", ApplicationConstants.CONST_STRING_INCHES);
			valObj3 = getValueObj(sizeVals[2].trim(), "Height", ApplicationConstants.CONST_STRING_INCHES);
			listOfValue.add(valObj1);
			listOfValue.add(valObj2);
			listOfValue.add(valObj3);
		}
		valuesObj.setValue(listOfValue);
		listOfValues.add(valuesObj);
		return listOfValues;
	}

	private Value getValueObj(String value, String attribute, String unit) {
		Value valueObj = new Value();
		valueObj.setAttribute(attribute);
		valueObj.setUnit(unit);
		valueObj.setValue(value);
		return valueObj;
	}

	public List<Material> getProductMaterial(String materialVal) {
		List<Material> listOfMaterial = new ArrayList<>();
		Material materialObj = new Material();
		materialObj.setAlias(materialVal);
		materialObj.setName(DacassoColorAndMaterialMapping.getMaterialGroup(materialVal));
		listOfMaterial.add(materialObj);
		return listOfMaterial;
	}

	public List<Color> getProductColors(String colorValue) {
		List<Color> listOfProductColors = new ArrayList<>();
		Color colorObj = null;
		String[] colorValues = CommonUtility.getValuesOfArray(colorValue,
				ApplicationConstants.CONST_DELIMITER_SEMICOLON);
		for (String colorName : colorValues) {
			colorObj = new Color();
			colorName = colorName.trim();
			colorObj.setAlias(colorName);
			colorObj.setName(DacassoColorAndMaterialMapping.getColorGroup(colorName));
			listOfProductColors.add(colorObj);
		}
		return listOfProductColors;
	}

	public ShippingEstimate getProductShippingEstimates(String shippingNoOfItems,String dimensions,String weight) {
		ShippingEstimate shippingEstimateObj = new ShippingEstimate();
		List<NumberOfItems> numberOfItems = null;
		Dimensions dimensionsObj = null;
		List<Weight> shippingWeight = null;
		if(!StringUtils.isEmpty(shippingNoOfItems)){
			numberOfItems = getShippingNumberOfItems(shippingNoOfItems);
		}
		if(!StringUtils.isEmpty(dimensions)){
			if(dimensions.contains("Envelope")){
				dimensions = dimensions.replaceAll("Envelope", "");
				dimensions = dimensions.replaceAll("\"", "");
			}
			dimensionsObj = getShippingDimensions(dimensions);
		}
		if(!StringUtils.isEmpty(weight)){
			shippingWeight = getShippingWeight(weight);
		}
		shippingEstimateObj.setNumberOfItems(numberOfItems);
		shippingEstimateObj.setWeight(shippingWeight);
		shippingEstimateObj.setDimensions(dimensionsObj);
		return shippingEstimateObj;
	}

	private List<NumberOfItems> getShippingNumberOfItems(String val) {
		List<NumberOfItems> listOfNumberOfItems = new ArrayList<>();
		NumberOfItems numberOfItemsObj = new NumberOfItems();
		numberOfItemsObj.setValue(val);
		numberOfItemsObj.setUnit("per Case");
		listOfNumberOfItems.add(numberOfItemsObj);
		return listOfNumberOfItems;
	}

	private List<Weight> getShippingWeight(String val) {
		List<Weight> listOfShippingWt = new ArrayList<>();
		Weight weightObj = new Weight();
		weightObj.setValue(val);
		weightObj.setUnit("lbs");
		listOfShippingWt.add(weightObj);
		return listOfShippingWt;
	}

	private Dimensions getShippingDimensions(String val) {
		String[] vals = val.split("x");
		int dimensionLength = vals.length;
		Dimensions dimensionsObj = new Dimensions();
		if(dimensionLength == ApplicationConstants.CONST_INT_VALUE_THREE){
			dimensionsObj.setLength(vals[0].trim());
			dimensionsObj.setWidth(vals[1].trim());
			dimensionsObj.setHeight(vals[2].trim());
			dimensionsObj.setLengthUnit(ApplicationConstants.CONST_STRING_INCHES);
			dimensionsObj.setWidthUnit(ApplicationConstants.CONST_STRING_INCHES);
			dimensionsObj.setHeightUnit(ApplicationConstants.CONST_STRING_INCHES);	
		} else if(dimensionLength == ApplicationConstants.CONST_INT_VALUE_TWO){
			dimensionsObj.setLength(vals[0].trim());
			dimensionsObj.setWidth(vals[1].trim());
			dimensionsObj.setLengthUnit(ApplicationConstants.CONST_STRING_INCHES);
			dimensionsObj.setWidthUnit(ApplicationConstants.CONST_STRING_INCHES);
		} else {
			dimensionsObj.setLength(vals[0].trim());
			dimensionsObj.setLengthUnit(ApplicationConstants.CONST_STRING_INCHES);
		}
		return dimensionsObj;
	}

	public Volume getItemWeight(String val) {
		Volume volumeObj = new Volume();
		List<Values> listOfValues = new ArrayList<>();
		Values valuesObj = new Values();
		List<Value> listOfValue = new ArrayList<>();
		Value valueObj = new Value();
		valueObj.setValue(val.trim());
		valueObj.setUnit("lbs");
		listOfValue.add(valueObj);
		valuesObj.setValue(listOfValue);
		listOfValues.add(valuesObj);
		volumeObj.setValues(listOfValues);
		return volumeObj;
	}
	public List<Packaging> getProductPackaging(String packValue){
		List<Packaging> listOfPackaging = new ArrayList<>();
		Packaging packagingObj = new Packaging();
		packagingObj.setName(packValue);
		listOfPackaging.add(packagingObj);
		return listOfPackaging;
	}
	
	public Product keepExistingProductData(Product existingProduct){
		Product newProduct = new Product();
		ProductConfigurations existingConfig = existingProduct.getProductConfigurations();
		List<String> existingCatagories = existingProduct.getCategories();
		List<Image> existingImages  = existingProduct.getImages();
		List<Theme> existingThemes = existingConfig.getThemes();
		List<String> existingKeywords = existingProduct.getProductKeywords();
		ProductConfigurations newConfig = new ProductConfigurations();
		if(!CollectionUtils.isEmpty(existingKeywords)){
			newProduct.setProductKeywords(existingKeywords);
		}
		if(!CollectionUtils.isEmpty(existingImages)){
			newProduct.setImages(existingImages);
		}
		if(!CollectionUtils.isEmpty(existingCatagories)){
			newProduct.setCategories(existingCatagories);
		}
		if(!CollectionUtils.isEmpty(existingThemes)){
			newConfig.setThemes(existingThemes);
		}
		newProduct.setProductConfigurations(newConfig);
		return newProduct;
	}

}
