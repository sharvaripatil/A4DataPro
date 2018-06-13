package parser.digiSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.Artwork;
import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.ImprintColor;
import com.a4tech.product.model.ImprintColorValue;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.Samples;
import com.a4tech.product.model.Shape;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.TradeName;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.sun.mail.iap.Literal;

public class DigiSpecAttributeParser {
	private LookupServiceData lookupServiceData;
	private DigiSpecPriceGridParser digiSpecPriceGride;
	
	public Product keepExistingProductData(Product oldProduct){
		Product newProduct = new Product();
		if(!CollectionUtils.isEmpty(oldProduct.getImages())){
			newProduct.setImages(oldProduct.getImages());
		}
		if(!CollectionUtils.isEmpty(oldProduct.getCategories())){
			newProduct.setCategories(oldProduct.getCategories());
		}
		return newProduct;
	}
	public Size getProductSize(String sizeVal){
		  Size sizeObj = new Size();
		  Dimension dimensionObj =  new Dimension();
		  Values valuesObj = new Values();
		  List<Values> valuesList = new ArrayList<>();
		  if(sizeVal.contains("Round")){
			  valuesObj = getSizeForDiameter(sizeVal);
			  valuesList.add(valuesObj);
		  } else if(sizeVal.contains("Square")){ 
			  valuesObj = getOverAllSizeValObj(getSqureSizeValue(sizeVal));
			  valuesList.add(valuesObj);
		  } else{
			  sizeVal = sizeVal.replaceAll("[^0-9xX./ ]", ""); 
			  valuesObj = getOverAllSizeValObj(sizeVal);
			  valuesList.add(valuesObj);
		  }
		  dimensionObj.setValues(valuesList);
		  sizeObj.setDimension(dimensionObj);
		  return sizeObj;
	  }
	  private Values getOverAllSizeValObj(String val){
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
			List<Value> listOfValue = new LinkedList<>();
			if(values.length == ApplicationConstants.CONST_INT_VALUE_ONE){
				valObj1 = getValueObj(values[0].trim(), "Length", "in"); 
				  listOfValue.add(valObj1);
			} else if(values.length == ApplicationConstants.CONST_INT_VALUE_TWO){
				 valObj1 = getValueObj(values[0].trim(), "Length", "in");
				 valObj2 = getValueObj(values[1].trim(), "width", "in");
				 listOfValue.add(valObj1);
			     listOfValue.add(valObj2);
			} else if(values.length == ApplicationConstants.CONST_INT_VALUE_THREE){
				 valObj1 = getValueObj(values[0].trim(), "Length", "in");
				 valObj2 = getValueObj(values[1].trim(),"width", "in");
				 listOfValue.add(valObj1);
			     listOfValue.add(valObj2);
			     if(!StringUtils.isEmpty(values[2].trim())){
					 valObj3 = getValueObj(values[2].trim(), "Height", "in");
					 listOfValue.add(valObj3);
				 }
			}
			 Values valuesObj = new Values(); 
			 valuesObj.setValue(listOfValue);
			 return valuesObj;
		}
	  private Values getSizeForDiameter(String val){//3.6"Round x 1/8" ,Dia:3.6:in;Height:1/8:in
		  List<Value> listOfValue = new ArrayList<>();
		  Values valuesObj = new Values(); 
		  val = val.replaceAll("[^0-9xX./ ]", "");
		  String[] values = val.split("x"); 
		  Value valObj1 = getValueObj(values[0].trim(), "Dia", "in");
		  Value valObj2 = getValueObj(values[1].trim(),"Height", "in");
		  listOfValue.add(valObj1);
		  listOfValue.add(valObj2);
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
    private String getSqureSizeValue(String val){
    	String[] vals = CommonUtility.getValuesOfArray(val, "x");
    	StringBuilder finalVal = new StringBuilder();
    	if(vals[0].contains("Square")){
    		String vall = vals[0].replaceAll("[^0-9./ ]", "").trim();
    		finalVal.append(vall).append("x").append(vall).append("x");
    	}
    	String secondVal = vals[1].replaceAll("[^0-9./ ]", "").trim();
    	finalVal.append(secondVal);
    	return finalVal.toString();
    }
  public List<Shape> getProductShapes(String shapeName){
	  List<Shape> shapeList = new ArrayList<>();
	  Shape shapeObj = new Shape();
	  shapeObj.setName(shapeName);
	  shapeList.add(shapeObj);
	  return shapeList;
  }
  public List<Theme> getProductThemes(String theme){
	  List<Theme> themeList = new ArrayList<>();
	  String[] themes = CommonUtility.getValuesOfArray(theme, ",");
	  Theme themeObj = null;
	  for (String themeName : themes) {
		  themeObj = new Theme();
		if(themeName.isEmpty()){
			continue;
		}
		if(lookupServiceData.isTheme(themeName.toUpperCase())){
			themeObj.setName(themeName);
			themeList.add(themeObj);
		}
	}
	  return themeList;
  }
  
  public List<String> getProductKeywords(String keyword){
	  List<String> keys = Arrays.asList(keyword.split(","));
	  List<String> keywordList = keys.stream().filter(key->!key.isEmpty()).limit(30).collect(Collectors.toList());
	  return keywordList;
  }
  
  public List<Material> getProductMaterial(String material){
	  List<Material> materialList = new ArrayList<>();
	  Material materialObj = null;
	  material = material.replaceAll("[^a-zA-Z-, ]", "");
	  String[] materials = CommonUtility.getValuesOfArray(material, ",");
	  for (String mtrlName : materials) {
		  mtrlName = mtrlName.trim();
		if(mtrlName.isEmpty()){
			continue;
		}
		materialObj = new Material();
		materialObj.setName(DigiSpecColorAndMaterialMapping.getMaterialGroup(mtrlName));
		materialObj.setAlias(mtrlName);
		materialList.add(materialObj);
	}
	  return materialList;
  }
  public List<Color> getProductColor(String color){
	  List<Color> colorList = new ArrayList<>();
	  Color colorObj = null;
	  String[] colors = CommonUtility.getValuesOfArray(color, ",");
	  for (String colorName : colors) {
		  colorName = colorName.trim();
		if(colorName.isEmpty()){
			continue;
		}
		colorObj = new Color();
		colorObj.setName(DigiSpecColorAndMaterialMapping.getColorGroup(colorName));
		colorObj.setAlias(colorName);
		colorList.add(colorObj);
	}
	  return colorList;
  }
  public Samples getProductSample(String value){
	  Samples sampleObj = new Samples();
	  if(value.equalsIgnoreCase("Y")){
		  sampleObj.setProductSampleAvailable(true);  
	  }
	  return sampleObj;
  }
  public List<ProductionTime> getProductionTime(String prdTime){
	  List<ProductionTime> productionTimeList = new ArrayList<>();
	  ProductionTime prdTimeObj = new ProductionTime();
	  prdTimeObj.setBusinessDays(prdTime);
	  productionTimeList.add(prdTimeObj);
	  return productionTimeList;
  }
  public RushTime getProductRushTime(String businessDays){
		RushTime rushTimeObj = new RushTime();
		rushTimeObj.setAvailable(ApplicationConstants.CONST_BOOLEAN_TRUE);
		List<RushTimeValue> rushTimeValueList = new ArrayList<>();
		RushTimeValue rushtimeValue = new RushTimeValue();
		rushtimeValue.setBusinessDays(businessDays);
		rushtimeValue.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
		rushTimeValueList.add(rushtimeValue);
		rushTimeObj.setRushTimeValues(rushTimeValueList);
		return rushTimeObj;
	}
  public List<Catalog> getProductCatalog(String catalogVal){//DIGISPEC:2017:6
	  List<Catalog> catalogList = new ArrayList<>();
	  Catalog catalogObj = new Catalog();
	 String[] catalogVals = CommonUtility.getValuesOfArray(catalogVal, ":");
	 String catalogName = catalogVals[1]+" "+catalogVals[0]+" "+"Catalog";
	 catalogObj.setCatalogName(catalogName);
	 catalogObj.setCatalogPage(catalogVals[2]);
	 catalogList.add(catalogObj);
	  return catalogList;
  }
  public Product getImprintMethodUpcharge(Product product,String imprMethdVal){
	  ProductConfigurations configuration = product.getProductConfigurations();
	  List<PriceGrid> priceGrid = new ArrayList<>();
	  List<ImprintMethod> imprintMethodList = getProductImprintMethods("Four Color Process", new ArrayList<>());
	  String[] upchargeVals = CommonUtility.getValuesOfArray(imprMethdVal, ";");
	  for (String upchargeVal : upchargeVals) {
			if (upchargeVal.equalsIgnoreCase("Four Color Process")
					|| upchargeVal.equalsIgnoreCase("Reorder Set Up-FREE")) {
		    	continue;
		    }
			if(upchargeVal.contains("ReorderSet Up Charge")){
				priceGrid = digiSpecPriceGride.getUpchargePriceGrid("1", "18.75", "V", "Imprint Method", false, "USD", "",
						"Four Color Process", "Re-Order Charge", "Other", 1, priceGrid, "", "");
			} else if(upchargeVal.contains("Set Up Charge")){
				if(upchargeVal.contains("37.50")){
					priceGrid = digiSpecPriceGride.getUpchargePriceGrid("1", "37.50", "V", "Imprint Method", false, "USD", "",
							"Four Color Process", "Set-up Charge", "Other", 1, priceGrid, "", "");
				} else {//56.25
					priceGrid = digiSpecPriceGride.getUpchargePriceGrid("1", "56.25", "V", "Imprint Method", false, "USD", "",
							"Four Color Process", "Set-up Charge", "Other", 1, priceGrid, "", "");
				}
			}
	}
	  
	  configuration.setImprintMethods(imprintMethodList);
	  product.setProductConfigurations(configuration);
	  product.setPriceGrids(priceGrid);
	  return product;
  }
  
  public List<ImprintMethod> getProductImprintMethods(String imprMethodVal,List<ImprintMethod> imprintMethodList){
	  if(CollectionUtils.isEmpty(imprintMethodList)){
		  imprintMethodList = new ArrayList<>();
	  }
		String existingImprintMethods = imprintMethodList.stream().map(imprMethod -> imprMethod.getAlias())
				.collect(Collectors.joining(","));
		String[] imprMethodVals = CommonUtility.getValuesOfArray(imprMethodVal, ",");
		ImprintMethod imprintMethodObj = null;
		String imprintMethodGroup = "";
		for (String imprMethodName : imprMethodVals) {
			imprMethodName = imprMethodName.trim();
			if(StringUtils.isEmpty(imprMethodName)){
				continue;
			}
			if(!existingImprintMethods.contains(imprMethodName)){
				imprintMethodObj = new ImprintMethod();
				if (imprMethodName.equalsIgnoreCase("Four Color Process")
						|| imprMethodName.equalsIgnoreCase("Full Color Process")) {
					imprintMethodGroup = "Full Color";
				} else if(imprMethodName.equals("Unimprinted")) {
					imprintMethodGroup = "Unimprinted";
				} else {
					imprintMethodGroup = "Other";
				}
				imprintMethodObj.setType(imprintMethodGroup);
				imprintMethodObj.setAlias(imprMethodName);
				imprintMethodList.add(imprintMethodObj);
			}
		}
	  return imprintMethodList;
  }
  public List<ImprintSize> getProductImprintSize(String imprSize){
	  List<ImprintSize> imprintSizeList = new ArrayList<>();
	  ImprintSize imprSizeObj =new ImprintSize();
	  imprSizeObj.setValue(imprSize);
	  imprintSizeList.add(imprSizeObj);
	  return imprintSizeList;
  }
  public List<Artwork> getProductArtwork(String artWorkVal){//Art Services,Virtual Proof
	  List<Artwork> artworkList = new ArrayList<>();
	  List<String> artworks = Arrays.asList("Art Services","Virtual Proof");
	  Artwork artworkObj = null;
	  for (String artworkName : artworks) {
		  artworkObj = new Artwork();
		  artworkObj.setValue(artworkName);
		  artworkObj.setComments("");
		  artworkList.add(artworkObj);
	}
	  return artworkList;
  }
  public ImprintColor getProductImprintColor(String imprcolor){
	  ImprintColor imprColorObj = new ImprintColor();
	  List<ImprintColorValue> imprintColorValueList = new ArrayList<>();
	  ImprintColorValue imprClrValObj = new ImprintColorValue();
	  imprClrValObj.setName(imprcolor);
	  imprintColorValueList.add(imprClrValObj);
	  imprColorObj.setValues(imprintColorValueList);
	  imprColorObj.setType(ApplicationConstants.CONST_STRING_IMPRNT_COLR);
	  return imprColorObj;
	  
  }
  public List<TradeName> getProductTradeName(String tradeVal){
	  List<TradeName> tradeNameList = new ArrayList<>();
	  TradeName tradeNameObj = new TradeName();
	  if(lookupServiceData.isTradeName(tradeVal.toUpperCase())){
		  tradeNameObj.setName(tradeVal);
		  tradeNameList.add(tradeNameObj);
	  }
	  return tradeNameList;
  }
  public List<Origin> getProductOrigin(String originVal){
	  List<Origin> originList = new ArrayList<>();
	  Origin originObj = new Origin();
	  originObj.setName(originVal);
	  originList.add(originObj);
	  return originList;
  }
  public Product getProductPackaging(String packVals,Product product){
	  ProductConfigurations configuration = product.getProductConfigurations();
	  List<PriceGrid> priceGrid = product.getPriceGrids();
	  packVals = packVals.replaceAll("\n", "");
	  String[] vals = CommonUtility.getValuesOfArray(packVals, ";");
	  List<String> packagingVals = new ArrayList<>();
	  for (String packVal : vals) {
		  packVal = packVal.trim();
		  if(StringUtils.isEmpty(packVal)){
			  continue;
		  }
		String[] packs = CommonUtility.getValuesOfArray(packVal, ":");
		String packName = packs[0];
		
		if(packName.contains("Bulk")){
			packName = "Bulk";
		} else if(packName.equalsIgnoreCase("Poly Bagging")){
			packName = "Poly Bag";
		} else {
		}
		if(!packName.equals("Bulk")){
			String priceVal = packs[1].replaceAll("[^0-9.]", "");
			priceGrid = digiSpecPriceGride.getUpchargePriceGrid("1", priceVal, "V", "Packaging", false, "USD", "",
					packName, "Packaging Charge", "Per Quantity", 1, priceGrid, "", "");	
		}
		packagingVals.add(packName);	
	}
	  List<Packaging> packagingList = getPackages(packagingVals);
	 configuration.setPackaging(packagingList);
	 product.setProductConfigurations(configuration);
	 product.setPriceGrids(priceGrid);
	  return product;
  }
  private List<Packaging> getPackages(List<String> packVal){
	  List<Packaging> packagingList = new ArrayList<>();
	  Packaging packObj = null;
	  for (String packName : packVal) {
		  packObj = new Packaging();
		  packObj.setName(packName);
		  packagingList.add(packObj);
	}
	  return packagingList;
  }
  public Product getProductOptions(String optionVal,Product product){
	  ProductConfigurations configuration = product.getProductConfigurations();
	  //configuration.setShippingEstimates(shippingEstimates);
	  List<PriceGrid> priceGrid = product.getPriceGrids();
	  List<Option> optionList = null;
	  String[] optionVals = CommonUtility.getValuesOfArray(optionVal, ";");
	  for (String val : optionVals) {
		if(val.contains("Custom Cutting Dies")){
			optionList = getOptions("Custom Cutting Dies", "Imprint", "Custom Cutting Dies Available", "");
			priceGrid = digiSpecPriceGride.getUpchargePriceGrid("1", "150","V", "Imprint Option", false, "USD", "",
					"Custom Cutting Dies Available", "Imprint Option Charge", "Other", 1, priceGrid, "Custom Cutting Dies", "");
		} else if(val.contains("RUSH Service")){
			product.setPriceGrids(priceGrid);
			product = getProductRushTime(val.split(":")[1], product);
			priceGrid = product.getPriceGrids();
			configuration = product.getProductConfigurations();
		} else if(val.contains("Lead-Times")){
			product.setAdditionalProductInfo(val);
		} else if(val.contains("REORDER Lead Time")){
				/*List<ProductionTime> productionTimeList = getProductionTime(val.split("")[1],
						configuration.getProductionTime());
				configuration.setProductionTime(productionTimeList);*/
		}
	}
	  configuration.setOptions(optionList);
	  product.setProductConfigurations(configuration);
	  product.setPriceGrids(priceGrid);
	return product;  
  }
  private List<Option> getOptions(String optionName,String optionType,String optionValue,String additionalInfo){
	  ////Option Name = Decorative Edge Options, Option Values = Wave, Swirl, Diamond.
	  Option optionObj=new Option();
	  List<OptionValue> valuesList=new ArrayList<OptionValue>();
	  OptionValue optionValueObj = new OptionValue();;
	  List<Option> listOfOptins = new ArrayList<>();
	  optionValueObj.setValue(optionValue);
	  valuesList.add(optionValueObj);
	  /*for (String optionVal : optionValues) {
		  optionValueObj = new OptionValue();
		  optionValueObj.setValue(optionVal);
		  valuesList.add(optionValueObj);
	}*/
	  optionObj.setName(optionName);
	  optionObj.setOptionType(optionType);
	  optionObj.setValues(valuesList);
	  optionObj.setCanOnlyOrderOne(ApplicationConstants.CONST_BOOLEAN_FALSE);
	  optionObj.setRequiredForOrder(ApplicationConstants.CONST_BOOLEAN_FALSE);
	  optionObj.setAdditionalInformation(additionalInfo);
	  listOfOptins.add(optionObj);
	  return listOfOptins;
	  
  }
  private Product getProductRushTime(String rushVal,Product product){
	  List<PriceGrid> priceGrid = product.getPriceGrids();
	  RushTime rushTime  = product.getProductConfigurations().getRushTime();
	 List<RushTimeValue> rushTimeValList = null;
	 rushVal = rushVal.replaceAll(", or", ",");
	 if(rushTime == null){
		  rushTime = new RushTime();
		  rushTimeValList = new ArrayList<>();
	  } else {
		  rushTimeValList = rushTime.getRushTimeValues();  
	  }
		String existingRushTimeVals = rushTimeValList.stream().map(RushTimeValue::getBusinessDays)
				.collect(Collectors.joining(","));
	 String[] rushVals = CommonUtility.getValuesOfArray(rushVal, ",");
	 RushTimeValue rushTimeValObj = null;
	 for (String rushTimeVal : rushVals) {// 3-Day $62.50 (V),
		 rushTimeValObj = new RushTimeValue();
		 String[] rushValss = CommonUtility.getValuesOfArray(rushTimeVal, "\\$");
		 String rushValTime = rushValss[0].trim();
		 if(rushValTime.equalsIgnoreCase("1-Day/24 Hour")){
			 rushValTime = "1";
		 } else {
			 rushValTime = rushValss[0].replaceAll("[^0-9]", "");	 
		 }
		 String priceVal = rushValss[1].replaceAll("[^0-9.]", "");
		 
		 priceGrid = digiSpecPriceGride.getUpchargePriceGrid("1", priceVal,"V", "Rush Service", false, "USD", "",
				 rushValTime+" business days", "Rush Service Charge", "Other", 1, priceGrid, "", "");
		 if(!existingRushTimeVals.contains(rushValTime)){
			 rushTimeValObj.setBusinessDays(rushValTime);
			 rushTimeValObj.setDetails("");
			 rushTimeValList.add(rushTimeValObj);	 
		 }
	}
	rushTime.setRushTimeValues(rushTimeValList);  	  
	  product.getProductConfigurations().setRushTime(rushTime);
	  product.setPriceGrids(priceGrid);
	  return product;
  }
  private List<ProductionTime> getProductionTime(String prdTime,List<ProductionTime> productionTimeList){
	  if(CollectionUtils.isEmpty(productionTimeList)){
		  productionTimeList = new ArrayList<>();
	  }
	  ProductionTime prdTimeObj = new ProductionTime();
	  prdTime = prdTime.replaceAll("[^0-9]", "");
	  prdTimeObj.setBusinessDays(prdTime);
	  productionTimeList.add(prdTimeObj);
	  return productionTimeList;
  }
  public ShippingEstimate getProductShippingEstimation(String shippingVal){
		ShippingEstimate shippingEstimObj = new ShippingEstimate();
		String[] shippingValues = CommonUtility.getValuesOfArray(shippingVal, ",");
		List<NumberOfItems> listOfNumberOfItems = getShippingNumberOfItems(shippingValues[0], "per Case");
		Dimensions dimensionsObj = getShippingDimension(shippingValues[1]);
		List<Weight> listOfWeight = getShippingWeight(shippingValues[2], "lbs");
		shippingEstimObj.setWeight(listOfWeight);
		shippingEstimObj.setDimensions(dimensionsObj);
		shippingEstimObj.setNumberOfItems(listOfNumberOfItems);
		return shippingEstimObj;
	}
	
	private List<NumberOfItems> getShippingNumberOfItems(String value,String unit){
		List<NumberOfItems> listOfNumberOfItems = new ArrayList<>();
		NumberOfItems numberOfItemsObj = new NumberOfItems();
		numberOfItemsObj.setValue(value);
		numberOfItemsObj.setUnit(unit);
		listOfNumberOfItems.add(numberOfItemsObj);
		return listOfNumberOfItems;
	}
	private List<Weight> getShippingWeight(String weightVal,String unit){
		List<Weight> listOfWeight = new ArrayList<>();
		Weight weightObj = new Weight();
		weightObj.setUnit("lbs");
		weightObj.setValue(weightVal);
		listOfWeight.add(weightObj);
		return listOfWeight;
	}
	private Dimensions getShippingDimension(String dimension){
		Dimensions dimensionsObj = new Dimensions();//inches LBS
		dimension = dimension.replaceAll("[^0-9.x]", "");
		String[] dimensions = CommonUtility.getValuesOfArray(dimension, "x");
		String unit = "in";// defalut unit
		dimensionsObj.setLength(dimensions[0]);
		dimensionsObj.setLengthUnit(unit);
		dimensionsObj.setWidth(dimensions[1]);
		dimensionsObj.setWidthUnit(unit);
		dimensionsObj.setHeight(dimensions[2]);
		dimensionsObj.setHeightUnit(unit);
		return dimensionsObj;
	}
	public List<ImprintLocation> getProductImprintLocation(String imprLocVal){
		List<ImprintLocation> imprintLocList = new ArrayList<>();
		ImprintLocation imprintLocObj = new ImprintLocation();
		imprintLocObj.setValue(imprLocVal);
		imprintLocList.add(imprintLocObj);
		return imprintLocList;
	}
	
  public void setLookupServiceData(LookupServiceData lookupServiceData) {
		this.lookupServiceData = lookupServiceData;
	}
  public void setDigiSpecPriceGride(DigiSpecPriceGridParser digiSpecPriceGride) {
		this.digiSpecPriceGride = digiSpecPriceGride;
	}
}
