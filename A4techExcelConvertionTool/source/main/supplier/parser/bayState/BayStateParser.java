package parser.bayState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Configurations;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Inventory;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.ProductSKUConfiguration;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class BayStateParser {
  
	private LookupServiceData lookupServiceData;
	
	public Product keepExistingProductData(Product existingProduct){
		  Product newProduct = new Product();
		  ProductConfigurations oldConfig = existingProduct.getProductConfigurations();
		  ProductConfigurations newConfig = new ProductConfigurations();
		  if(!StringUtils.isEmpty(existingProduct.getSummary())){
			  String summary = existingProduct.getSummary();
			  newProduct.setSummary(summary);
		  }
		  if(!CollectionUtils.isEmpty(existingProduct.getCategories())){
			  newProduct.setCategories(existingProduct.getCategories());
		  }
		  if(!CollectionUtils.isEmpty(existingProduct.getLineNames())){
			  newProduct.setLineNames(existingProduct.getLineNames());
		  }
		  if(!CollectionUtils.isEmpty(existingProduct.getFobPoints())){
			  newProduct.setFobPoints(existingProduct.getFobPoints());
		  }
		  if(oldConfig.getImprintColors() != null){
			  newConfig.setImprintColors(oldConfig.getImprintColors());
		  }
		  newProduct.setProductConfigurations(newConfig);
		  return newProduct;
	  }
	public List<Color> getProductColor(String colors){
		  List<Color> colorsList = new ArrayList<>();
		  Color colorObj = null;
		  String[] productcolors = CommonUtility.getValuesOfArray(colors, ",");
		  for (String colorName : productcolors) {
			  if(StringUtils.isEmpty(colorName)){
				  continue;
			  }
			  colorObj = new Color();
			  String colorGroup = BayStateColorAndMaterialMapping.getColorGroup(colorName);
			  if(colorGroup.contains("Combo")){
				  if(colorGroup.contains("Alias")){
					  colorObj = getColorComboWithAlias(colorGroup);
				  } else {
					  colorObj = getColorCombo(colorGroup, colorName); 
				  }
				  
			  } else {
				  if(colorGroup.contains("Alias")){
					  String[] colorVals = colorGroup.split("Alias");
					  colorObj.setName(colorVals[0].replaceAll(":",""));
					  colorObj.setAlias(colorVals[1].replaceAll(":",""));
				  } else {
					  colorObj.setName(colorGroup);
					  colorObj.setAlias(colorName);
				  }
				  
			  }
			  colorsList.add(colorObj);
		}
		  return colorsList;
	  }
	  private Color getColorCombo(String val,String alias){
		  Color colorObj = new Color();
		  List<Combo> comboColorList = new ArrayList<>();
		  String[] comboVals = CommonUtility.getValuesOfArray(val, ":");
		  Combo combo1 = new Combo();
		  Combo combo2 = null;
		  if(val.contains("Trim")){
			  combo2 = new Combo();
			  combo2.setName(comboVals[4]);
			  combo2.setType("Trim");
			  comboColorList.add(combo2);
		  }
		  combo1.setName(comboVals[2]);	  
		  combo1.setType("Secondary");
		  comboColorList.add(combo1);
		 colorObj.setName(comboVals[0]);
		  colorObj.setAlias(alias);
		  colorObj.setCombos(comboColorList);
		 return colorObj;  
	  }
	  private Color getColorComboWithAlias(String val){
		  Color colorObj = new Color();
		  //Medium White:Combo:Dark Black:Secondary:Alias:xxxxxx
		  String[] combos = val.split("Alias");
		  String comboVal = combos[0];
		  String temp = comboVal.substring(comboVal.length() - 1);// fetch last character
		  if(temp.equals(":")){
			  comboVal = comboVal.substring(0, comboVal.length() - 1); // remove lats char. if value is :
		  }
		  String alias = combos[1];
		  alias = alias.replaceAll(":", "");
		  colorObj = getColorCombo(comboVal, alias);
		  return colorObj;
	  }
  public List<ProductSkus> getProductSkus(String colorVal, String skuVal,
			List<ProductSkus> existingSkus) {
		ProductSkus productSku = new ProductSkus();
		colorVal = colorVal.replaceAll("/", "-");
		List<ProductSKUConfiguration> listSkuConfigs = new ArrayList<>();
		ProductSKUConfiguration colorSkuConfig = getSkuConfiguration("Product Color", colorVal);
		listSkuConfigs.add(colorSkuConfig);
		productSku.setSKU(skuVal);
		productSku.setConfigurations(listSkuConfigs);
		Inventory inventory = getInventory();
		productSku.setInventory(inventory);
		productSku.setHazmat("UNSPECIFIED");
		existingSkus.add(productSku);
		return existingSkus;
	}
  public List<ImprintSize> getProductImprintSize(String val) {
		List<ImprintSize> listOfImprintSize = new ArrayList<>();
		ImprintSize imprintSizeObj = null;
		String[] imprintSizes = CommonUtility.getValuesOfArray(val, ";");
		for (String imprSizeName : imprintSizes) {
			imprintSizeObj = new ImprintSize();	
			imprintSizeObj.setValue(imprSizeName);
			listOfImprintSize.add(imprintSizeObj);
		}
		return listOfImprintSize;
	}
  public ShippingEstimate getProductShipping(String dimensionVal,String weight){
	  ShippingEstimate shippingEstimateObj = new ShippingEstimate();
	  Dimensions dimensions = null;
	  if(!dimensionVal.contains(";")){
		  dimensionVal = dimensionVal.replaceAll("[^0-9x]", "");
		  dimensions = getShippingDimensions(dimensionVal);
		  shippingEstimateObj.setDimensions(dimensions);
	  }
	  String[] weights = CommonUtility.getValuesOfArray(weight, "/");//150Pcs/6lbs
	  List<NumberOfItems> listOfNumberOfItems = getShippingNumberofItems(weights[0]);
	  List<Weight> listOfShippingWt = getShippingWeight(weights[1]);
	  shippingEstimateObj.setNumberOfItems(listOfNumberOfItems);
	  shippingEstimateObj.setWeight(listOfShippingWt);
	  return shippingEstimateObj;
  }
  private List<Weight> getShippingWeight(String val){
	  val = val.replaceAll("[^0-9]", "");
		List<Weight> listOfShippingWt = new ArrayList<>();
		Weight weightObj = new Weight();
		weightObj.setValue(val);
		weightObj.setUnit("lbs");
		listOfShippingWt.add(weightObj);
	  return listOfShippingWt;
  }
  private List<NumberOfItems> getShippingNumberofItems(String val){
	val = val.replaceAll("[^0-9]", "");
	List<NumberOfItems> listOfNumberOfItems = new ArrayList<>();
	NumberOfItems numberOfItems = new NumberOfItems();
	numberOfItems.setValue(val);
	numberOfItems.setUnit("Per Case");
	listOfNumberOfItems.add(numberOfItems);
	return listOfNumberOfItems;
}
private Dimensions getShippingDimensions(String val){
	String[] vals = val.split("x");
	int dimensionLength = vals.length;
	Dimensions dimensionsObj = new Dimensions();
	if(dimensionLength == ApplicationConstants.CONST_INT_VALUE_THREE){
		dimensionsObj.setHeight(vals[0].trim());
		dimensionsObj.setWidth(vals[1].trim());
		dimensionsObj.setLength(vals[2].trim());
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
	public List<Packaging> getProductPackaging(String packValue){
		List<Packaging> listOfPackaging = new ArrayList<>();
		Packaging packagingObj = null;
		String[] packValues = CommonUtility.getValuesOfArray(packValue, ",");
		for (String packName : packValues) {
			packagingObj = new Packaging();
			if (packName.equalsIgnoreCase("Polybagg") || packName.equalsIgnoreCase("Polybagged")
					|| packName.equalsIgnoreCase("poly bags")) {
				packName = "Poly Bag";
			} else if(packName.contains("Individually Poly Bagged") || packName.contains("Individually Polybagged")
					|| packName.contains("Individually poly bagged") || packName.contains("Individually polybagged")){
				packName = "Individual Poly Bag";
			} else if(packName.equalsIgnoreCase("Gift Boxed") || packName.equalsIgnoreCase("Gift box")){
				packName = "Gift Boxes";	
			} else if(packName.equalsIgnoreCase("Bulk Pakaging") || packName.equalsIgnoreCase("Bulk.")){
				packName = "Bulk";
			} else {
			}
			packagingObj.setName(packName);
			listOfPackaging.add(packagingObj);
		}
		
		return listOfPackaging;
	}
	public List<ProductionTime> getProductionTime(String prdTime){
		List<ProductionTime> listOfProductionTime = new ArrayList<>();
		ProductionTime productionTimeObj = new ProductionTime();
		prdTime = prdTime.replaceAll("[^0-9]", "").trim();
		productionTimeObj.setBusinessDays(prdTime);
		productionTimeObj.setDetails("");
		listOfProductionTime.add(productionTimeObj);
		return listOfProductionTime;
	}
	public List<Material> getProductMaterial(String mtrl){
		List<Material> materialList = new ArrayList<>();
		Material materialObj = null;
		mtrl = mtrl.replaceAll(";", ",");
		String[] mtrls = CommonUtility.getValuesOfArray(mtrl, ",");
		for (String mtrlName : mtrls) {
			materialObj = new Material();
			String mtrlGroup = BayStateColorAndMaterialMapping.getMaterialGroup(mtrlName);
			if(mtrlGroup == null){
				if(lookupServiceData.isMaterial(mtrlName)){
					mtrlGroup = mtrlName;
				} else {
					mtrlGroup = "Other";	
				}
				materialObj.setName(mtrlGroup);
				materialObj.setAlias(mtrlName);
			} else if(mtrlGroup.contains("Combo") || mtrlName.contains("/") || mtrlName.contains(" and ")){
				if(mtrlGroup.contains("Combo")){
					materialObj = getMaterialWithCombo(mtrlGroup,mtrlName);	
				} else {
					materialObj = getMaterialWithCombo(mtrlName,"");
				}
			} else {
				materialObj.setName(mtrlGroup);
				materialObj.setAlias(mtrlName);
			}
			materialList.add(materialObj);	
	    }
		return materialList;
	}
	private Material getMaterialWithCombo(String val,String alias){
		Material materialObj = new Material();
		Combo comboObj = new Combo();
		String[] combos = null;
		if(val.contains("Combo")){
			combos = val.split(":");
			materialObj.setName(BayStateColorAndMaterialMapping.getMaterialGroup(combos[0]));
			materialObj.setAlias(alias);
			comboObj.setType(combos[1]);
		} else if(val.contains("and")){
			combos = val.split("and");
			materialObj.setName(BayStateColorAndMaterialMapping.getMaterialGroup(combos[0]));
			materialObj.setAlias(val);
			comboObj.setType(BayStateColorAndMaterialMapping.getMaterialGroup(combos[1]));
		} else {// '/'
			combos = val.split("/");
			materialObj.setName(BayStateColorAndMaterialMapping.getMaterialGroup(combos[0]));
			materialObj.setAlias(val.replaceAll("/", "-"));
			comboObj.setType(BayStateColorAndMaterialMapping.getMaterialGroup(combos[1]));
		}
		materialObj.setCombo(comboObj);
		return materialObj;
	}
	public List<Origin> getProductOrigin(String val){
		List<Origin> listOfOrigin = new ArrayList<>();
		Origin originObj = new Origin();
		if(val.equalsIgnoreCase("USA")){
			val = "U.S.A.";
		}
		originObj.setName(val);
		listOfOrigin.add(originObj);
		return listOfOrigin;
	}
	public List<ImprintMethod> getImprintMethods(String val,List<ImprintMethod> existingImprintMethods){
		if(CollectionUtils.isEmpty(existingImprintMethods)){
			existingImprintMethods = new ArrayList<>();
		}
		ImprintMethod imprintMethodObj = new ImprintMethod();
		String imprintMethodType = "";
		if(val.equalsIgnoreCase("Screen Printed")){
			imprintMethodType = "Silkscreen";
		} else if(val.equalsIgnoreCase("Screen")){
			imprintMethodType = "Silkscreen";
		} else if(val.contains("Pad")){
			imprintMethodType = "Pad Print";
		} else if(val.equalsIgnoreCase("Full Color Digital")){
			imprintMethodType = "Full Color";
		} else {
			imprintMethodType = val;
		}
		imprintMethodObj.setType(imprintMethodType);
		imprintMethodObj.setAlias(val);
		existingImprintMethods.add(imprintMethodObj);
		return existingImprintMethods;
	}
	public List<String> getProductCategories(String category){
		List<String> categoryList = new ArrayList<>();
		String[] categories = CommonUtility.getValuesOfArray(category, ",");
		for (String categoryName : categories) {
			if(lookupServiceData.isCategory(categoryName)){
				categoryList.add(categoryName);
			}
		}
		return categoryList;
	}
	public Size getProductSize(String sizeVal){
		Size size = new Size();
		List<Values> valuesList = new ArrayList<>();
		Values valuesObj = new Values();
		Dimension dimensionObj =  new Dimension();
		if(sizeVal.contains("ounces")){// volumn related
			//6 ounces: 6"W x Rim: 2 7/8"W  ,,,12 ounces: 4"W
			Volume volumnObj = new Volume();
			valuesObj = getOverAllSizeValObj(sizeVal.split("ounces")[0].trim(), "oz", "", "");
			valuesList.add(valuesObj);
			volumnObj.setValues(valuesList);
			size.setVolume(volumnObj);
			return size;
		} else if(sizeVal.contains("Pad")){// dimension related
			String val = sizeVal.split("Pad")[0].trim();
			if(val.contains("W") && val.contains("L")){
				val = val.replaceAll("[^0-9xX/ ]", "");
				valuesObj = getOverAllSizeValObj(val, "Width", "Length", "");
			} else if(val.contains("H") && val.contains("W")){
				val = val.replaceAll("[^0-9xX/ ]", "");
				valuesObj = getOverAllSizeValObj(val, "Height", "Width", "");
			}
			
		} else if(sizeVal.contains("Open")){
			String tempSize = sizeVal.split("Open:")[1];
			if(tempSize.contains("H") && tempSize.contains("W")){
				tempSize = tempSize.replaceAll("[^0-9xX/ ]", "");
				valuesObj = getOverAllSizeValObj(tempSize, "Height", "Width", "Length");
			} else {
				tempSize = tempSize.replaceAll("[^0-9xX/ ]", "");
				valuesObj = getOverAllSizeValObj(tempSize, "Length", "", "");
			}
			
		} else if(sizeVal.contains("Product Length") || sizeVal.contains("USB")){
			String[] vals = CommonUtility.getValuesOfArray(sizeVal, ";");
			String lengthVal = "";
			if(vals[0].contains("Product Length") || vals[0].contains("USB")){
				 lengthVal = vals[0].split(":")[1];
				lengthVal = lengthVal.replaceAll("[^0-9/ ]", "").trim();
			} else {
				 lengthVal = vals[1].split(":")[1];
				lengthVal = lengthVal.replaceAll("[^0-9/ ]", "").trim();
				
			}
			valuesObj = getOverAllSizeValObj(lengthVal, "Length", "", "");
		} else if(sizeVal.contains("Spatula")){
			String[] ss = sizeVal.split(";");
			if(ss[0].contains("Spatula")){
				sizeVal = ss[0].replaceAll("[^0-9xX/ ]", "");
				valuesObj = getOverAllSizeValObj(sizeVal, "Width", "Length", "");
			} else if(ss[1].contains("Spatula")){
				sizeVal = ss[1].replaceAll("[^0-9xX/ ]", "");
				valuesObj = getOverAllSizeValObj(sizeVal, "Width", "Length", "");
			}
		} else if(sizeVal.contains("Pen")){
			String tempSize = sizeVal.split("Pen:")[1]; 
			tempSize = tempSize.replaceAll("[^0-9xX/ ]", "");
			valuesObj = getOverAllSizeValObj(tempSize, "Length", "", "");
		} else if(sizeVal.contains("Spoon")){
			String tempSize = sizeVal.split(";")[0]; 
			tempSize = tempSize.replaceAll("[^0-9xX/ ]", "");
			valuesObj = getOverAllSizeValObj(tempSize, "Length", "", "");
		} else if(sizeVal.contains("H") && sizeVal.contains("W")){
			sizeVal = sizeVal.replaceAll("[^0-9xX/ ]", "");
			valuesObj = getOverAllSizeValObj(sizeVal, "Height", "Width", "Length");
		} else if(sizeVal.contains("H")){
			sizeVal = sizeVal.replaceAll("[^0-9xX/ ]", "");
			valuesObj = getOverAllSizeValObj(sizeVal, "Height", "Length", "");
		} else if(sizeVal.contains("W")){
			sizeVal = sizeVal.replaceAll("[^0-9xX/ ]", "");
			valuesObj = getOverAllSizeValObj(sizeVal, "Width", "Length", "");
		} else {
			sizeVal = sizeVal.replaceAll("[^0-9xX/ ]", "");
			valuesObj = getOverAllSizeValObj(sizeVal, "Length", "", "");
		}
		valuesList.add(valuesObj);
		dimensionObj.setValues(valuesList);
		size.setDimension(dimensionObj);
		return size;
	}
	private Values getOverAllSizeValObj(String val,String unit1,String unit2,String unit3){
		//Overall Size: 23.5" x 23.5"
		String[] values = null;
		if(val.contains("x")){
			values = val.split("x");
		} else {
			values = val.split("X");
		}
		Value valObj1 = null;
		Value valObj2 = null;
		Value valObj3 = null;
		List<Value> listOfValue = new ArrayList<>();
		if(values.length == ApplicationConstants.CONST_INT_VALUE_ONE){
			if(unit1.equals("oz")){
				valObj1 = getValueObj(values[0].trim(), "", unit1);
			} else {
				valObj1 = getValueObj(values[0].trim(), unit1, "in");	
			}
			 
			  listOfValue.add(valObj1);
		} else if(values.length == ApplicationConstants.CONST_INT_VALUE_TWO){
			 valObj1 = getValueObj(values[0].trim(), unit1, "in");
			 valObj2 = getValueObj(values[1].trim(), unit2, "in");
			 listOfValue.add(valObj1);
		     listOfValue.add(valObj2);
		} else if(values.length == ApplicationConstants.CONST_INT_VALUE_THREE){
			 valObj1 = getValueObj(values[0].trim(), unit1, "in");
			 valObj2 = getValueObj(values[1].trim(),unit2, "in");
			 if(!StringUtils.isEmpty(values[2].trim())){
				 valObj3 = getValueObj(values[2].trim(), unit3, "in");
				 listOfValue.add(valObj3);
			 }
			 listOfValue.add(valObj1);
		     listOfValue.add(valObj2);
		}
		 Values valuesObj = new Values(); 
		 valuesObj.setValue(listOfValue);
		 return valuesObj;
	}
	private Value getValueObj(String value,String attribute,String unit){
		Value valueObj = new Value();
		if(!StringUtils.isEmpty(attribute)){
			valueObj.setAttribute(attribute);
		}
		valueObj.setUnit(unit);
		valueObj.setValue(value);
		return valueObj;
	}
	public List<AdditionalColor> getAdditionalColors(String val){
		List<AdditionalColor> additionalColorList = new ArrayList<>();
		AdditionalColor additionalColorObj = new AdditionalColor();
		additionalColorObj.setName("Additional Color (max x colors):"+val);
		additionalColorList.add(additionalColorObj);
		return additionalColorList;
	}
	public List<Image> getProductImages(String imgUrl){
		List<Image> imageList = new ArrayList<>();
		Image imageObj = new Image();
		imageObj.setImageURL(imgUrl);
		imageObj.setRank(ApplicationConstants.CONST_INT_VALUE_ONE);
		imageObj.setIsPrimary(ApplicationConstants.CONST_BOOLEAN_TRUE);
        imageObj.setDescription("");
		imageList.add(imageObj);
		return imageList;
	}
	public List<Option> getProductOptions(String optionvalue,String optionType){
		  ////Option Name = Decorative Edge Options, Option Values = Wave, Swirl, Diamond.
		String[] options = CommonUtility.getValuesOfArray(optionvalue, ";");
		  Option optionObj= null;
		  List<OptionValue> valuesList= null;
		  OptionValue optionValueObj=null;
		  List<Option> listOfOptins = new ArrayList<>();
		  for (String option : options) {
			  optionObj = new Option();
			  valuesList=new ArrayList<OptionValue>();
			  String[] optionVal = CommonUtility.getValuesOfArray(option, ":");
			  String[] optionValues = CommonUtility.getValuesOfArray(optionVal[1], ",");
			  for (String optionVals : optionValues) {
				  optionValueObj = new OptionValue();
				  optionValueObj.setValue(optionVals);
				  valuesList.add(optionValueObj);
			}
			  optionObj.setName(optionVal[0]);
			  optionObj.setOptionType(optionType);
			  optionObj.setValues(valuesList);
			  optionObj.setCanOnlyOrderOne(ApplicationConstants.CONST_BOOLEAN_FALSE);
			  optionObj.setRequiredForOrder(ApplicationConstants.CONST_BOOLEAN_FALSE);
			  optionObj.setAdditionalInformation("");
			  listOfOptins.add(optionObj);
		}
		  return listOfOptins;
		  
	  }
	public List<ProductNumber> getProductNumbers(Set<String> colorAndPrdNum){
		List<ProductNumber> listOfProductNumbers = new ArrayList<>();
		ProductNumber productNumberObj = null;
		for (String productNumber : colorAndPrdNum) {
			productNumberObj = new ProductNumber();
			String[] colorAndNumber = CommonUtility.getValuesOfArray(productNumber, ":");
			productNumberObj.setProductNumber(colorAndNumber[0]);
			List<Configurations> listOfConfigurations = getProductNumberConfigurations(colorAndNumber[1]);
			productNumberObj.setConfigurations(listOfConfigurations);
			listOfProductNumbers.add(productNumberObj);
		}	
		return listOfProductNumbers;
	}
	private List<Configurations> getProductNumberConfigurations(String colorVal){
		List<Configurations> listOfConfigurations = new ArrayList<>();
		Configurations config = new Configurations();
		config.setCriteria("Product Color");
		config.setValue(Arrays.asList(colorVal));
		listOfConfigurations.add(config);
		return listOfConfigurations;
	}
	private ProductSKUConfiguration getSkuConfiguration(String criteria,String val){
		ProductSKUConfiguration skuConfig = new ProductSKUConfiguration();
		skuConfig.setCriteria(criteria);
		skuConfig.setValue(Arrays.asList(val));	
		return skuConfig;
	}
	private Inventory getInventory(){
		Inventory inventory = new Inventory();
		inventory.setInventoryLink("");
		inventory.setInventoryQuantity("");
		inventory.setInventoryStatus("");
		return inventory;
	}
	public LookupServiceData getLookupServiceData() {
		return lookupServiceData;
	}
	public void setLookupServiceData(LookupServiceData lookupServiceData) {
		this.lookupServiceData = lookupServiceData;
	}
}
