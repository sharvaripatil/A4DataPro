package parser.FITSAccessories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Inventory;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductSKUConfiguration;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Weight;
import com.a4tech.util.CommonUtility;

public class FITSAttributeParser {
  
	public Product keepExistingProductData(Product existingProduct){
		  Product newProduct = new Product();
		  ProductConfigurations oldConfig = existingProduct.getProductConfigurations();
		  ProductConfigurations newConfig = new ProductConfigurations();
		  if(!StringUtils.isEmpty(existingProduct.getSummary())){
			  String summary = existingProduct.getSummary();
			  if(summary.contains("velcro")){
				  summary = summary.replaceAll("velcro", "");
			  }
			  newProduct.setSummary(summary);
		  }
		  if(!CollectionUtils.isEmpty(existingProduct.getProductKeywords())){
			  newProduct.setProductKeywords(existingProduct.getProductKeywords());
		  }
		  if(!CollectionUtils.isEmpty(existingProduct.getImages())){
			  newProduct.setImages(existingProduct.getImages());
		  }
		  if(!CollectionUtils.isEmpty(oldConfig.getThemes())){
			  newConfig.setThemes(oldConfig.getThemes());
		  }
		  if(!CollectionUtils.isEmpty(existingProduct.getCategories())){
			  newProduct.setCategories(existingProduct.getCategories());
		  }
		  newProduct.setProductConfigurations(newConfig);
		  return newProduct;
	  }
	public List<ImprintMethod> getProductImprintMethods(String imprMethodVal){
		List<ImprintMethod> imprintMethodList = new ArrayList<>();
		ImprintMethod imprintMethodObj = null;
		String[] imprMethodVals = CommonUtility.getValuesOfArray(imprMethodVal, ",");
		for (String imprMethodName : imprMethodVals) {
			imprintMethodObj = new ImprintMethod();
			String groupName = "";
			if(imprMethodName.contains("Embroidery")){
				groupName = "Embroidered";
			} else if(imprMethodName.equalsIgnoreCase("Deboss")){
				groupName = "Debossed";
			} else if(imprMethodName.equalsIgnoreCase("Printing")){
				groupName = "Printed";
			} else {
				groupName = "Other";
			}
			imprintMethodObj.setAlias(imprMethodName);
			imprintMethodObj.setType(groupName);
			imprintMethodList.add(imprintMethodObj);
		}
		return imprintMethodList;
	}
  public List<Origin> getProductOrigin(String originName){
	  List<Origin> originsList = new ArrayList<>();
	  Origin originObj  = new Origin();
	  if(originName.equals("CN")){
		  originName = "China";
	  } else if(originName.equals("VN")){
		  originName = "Vietnam";
	  }
	  originObj.setName(originName);
	  originsList.add(originObj);
	  return originsList;
  }
  public ShippingEstimate getShippingEstimation(String dimensions,String weight){
	  ShippingEstimate shippingEstimateObj = new ShippingEstimate();
	  List<Weight> listOfShippingWt = getShippingWeight(weight);
      Dimensions shippingDimensions = getShippingDimensions(dimensions);
      shippingEstimateObj.setDimensions(shippingDimensions);
      shippingEstimateObj.setWeight(listOfShippingWt);
      return shippingEstimateObj;
  }
  private Dimensions getShippingDimensions(String val){
	  String[] vals = val.split("x");
	  Dimensions dimensionsObj = new Dimensions();
	  dimensionsObj.setLength(vals[0].trim());
	  dimensionsObj.setWidth(vals[1].trim());
	  dimensionsObj.setHeight(vals[2].trim());
	  dimensionsObj.setLengthUnit("in");
	  dimensionsObj.setWidth("in");
	  dimensionsObj.setHeightUnit("in");
	  return dimensionsObj;
  }
  private List<Weight> getShippingWeight(String val){
	  List<Weight> listOfShippingWt = new ArrayList<>();
	  Weight weightObj = new Weight();
	  weightObj.setValue(val);
	  weightObj.setUnit("lbs");
	  listOfShippingWt.add(weightObj);
	  return listOfShippingWt;
  }
  public List<Color> getProductColor(Set<String> colors){
	  List<Color> colorsList = new ArrayList<>();
	  Color colorObj = null;
	  for (String colorName : colors) {
		  colorObj = new Color();
		  if(colorName.contains("/")){
			  String group = FITSColorMapping.getColorGroup(colorName);
			  /*if("Other".equals(group)){
				  group = FITSColorMapping.getColorGroup(colorName.replaceAll("/", "-"));
				  if(group.equals("Multi Color") || group.equals("Silver Metal") || group.equals("Medium Purple")
						  || group.equals("Pewter Metal") || group.equals("Medium Black") || group.equals("Medium Gray")){
					  colorName = colorName.replaceAll("/", "-");
				  }
			  }*/
			  if(!"Other".equals(group)){
				  colorObj.setAlias(colorName);
				  colorObj.setName(group);  
			  } else {
				  colorObj = getColorCombo(colorName);
			  }
		  } else {
			 String group = FITSColorMapping.getColorGroup(colorName);
			 colorObj.setAlias(colorName);
			 colorObj.setName(group);
		  }
		  colorsList.add(colorObj);
	}
	  return colorsList;
  }
  private Color getColorCombo(String val){
	  Color colorObj = new Color();
	  List<Combo> comboColorList = new ArrayList<>();
	  String[] comboVals = CommonUtility.getValuesOfArray(val, "/");
	  Combo combo1 = new Combo();
	  Combo combo2 = null;
	  if(comboVals.length == 3){
		  combo2 = new Combo();
		  combo2.setName(FITSColorMapping.getColorGroup(comboVals[2]));
		  combo2.setType("trim");
		  comboColorList.add(combo2);
	  }
	  combo1 = new Combo();
	  if(comboVals[0].trim().equalsIgnoreCase("Pink #342")){
		  colorObj.setName("Pink");
	  } else {
		  colorObj.setName(FITSColorMapping.getColorGroup(comboVals[0]));
	  }
	  combo1.setName(FITSColorMapping.getColorGroup(comboVals[1]));
	  combo1.setType("secondary");
	  comboColorList.add(combo1);
	  colorObj.setCombos(comboColorList);
	  colorObj.setAlias(val.replaceAll("/", "-"));
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
}
