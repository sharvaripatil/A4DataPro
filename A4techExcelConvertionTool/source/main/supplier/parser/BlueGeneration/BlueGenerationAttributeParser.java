package parser.BlueGeneration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.AvailableVariations;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Inventory;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.OtherSize;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductSKUConfiguration;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.util.ApplicationConstants;

public class BlueGenerationAttributeParser {
  public Product keepExistingProductData(Product existingProduct){
	  Product newProduct = new Product();
	  ProductConfigurations oldConfig = existingProduct.getProductConfigurations();
	  ProductConfigurations newConfig = new ProductConfigurations();
	  if(!StringUtils.isEmpty(existingProduct.getDescription())){
		  newProduct.setDescription(existingProduct.getDescription());
	  }
	  if(!CollectionUtils.isEmpty(existingProduct.getProductKeywords())){
		  newProduct.setProductKeywords(existingProduct.getProductKeywords());
	  }
	  if(!CollectionUtils.isEmpty(oldConfig.getMaterials())){
		  newConfig.setMaterials(oldConfig.getMaterials());
	  }
	  if(!CollectionUtils.isEmpty(oldConfig.getImprintMethods())){
		  newConfig.setImprintMethods(oldConfig.getImprintMethods());
	  }
	  if(!CollectionUtils.isEmpty(existingProduct.getFobPoints())){
		  newProduct.setFobPoints(existingProduct.getFobPoints());
	  }
	  if(!CollectionUtils.isEmpty(existingProduct.getCatalogs())){
		  newProduct.setCatalogs(existingProduct.getCatalogs());
	  }
	  newProduct.setProductConfigurations(newConfig);
	  return newProduct;
  }
  public Product addFobAndImprintMethodForNewProduct(Product product){
	  ProductConfigurations config = new ProductConfigurations();
	  List<ImprintMethod> imprintMethodList = new ArrayList<>();
	  ImprintMethod imprintMethodObj = new ImprintMethod();
	  imprintMethodObj.setAlias("Unimprinted");
	  imprintMethodObj.setType("Unimprinted");
	  imprintMethodList.add(imprintMethodObj);
	  config.setImprintMethods(imprintMethodList);
	  List<FOBPoint> fobPointList = new ArrayList<>();
	  FOBPoint fobPointObj = new FOBPoint();
	  fobPointObj.setName("Jersey City, NJ 07310 USA");
	  fobPointList.add(fobPointObj);
	  product.setFobPoints(fobPointList);
	  product.setProductConfigurations(config);
	  return product;
  }
  public ShippingEstimate getProductShippingEstimation(String val){
	  ShippingEstimate shippingEst = new ShippingEstimate();
	  List<NumberOfItems> numberOfItemsList = new ArrayList<>();
	  NumberOfItems numberOfItemsObj = new NumberOfItems();
	  numberOfItemsObj.setValue(val);
	  numberOfItemsObj.setUnit("per Carton");
	  numberOfItemsList.add(numberOfItemsObj);
	  shippingEst.setNumberOfItems(numberOfItemsList);
	  return shippingEst;
  }
  public List<Color> getProductColor(Set<String> colorsList){
	  List<Color> listOfColor = new ArrayList<>();
	  Color colorObj = null;
	  for (String colorName : colorsList) {
		colorObj = new Color();
	    String colorGroup = BlueGenerationColorMapping.getColorGroup(colorName);
	    if(colorGroup == null){
	    	colorGroup = "Other";
	    }
	    colorObj.setAlias(colorName);
	    colorObj.setName(colorGroup);
	    listOfColor.add(colorObj);
	}
	  return listOfColor;
  }
 public Size getProductSize(Set<String> sizeVals){
	 Size sizeObj = new Size();
	 Apparel apparealObj = new Apparel();
	 OtherSize otherSize = new OtherSize();
	 List<Value> listOfValues = new ArrayList<>();
	 Value valueObj = null;
	 boolean isOtherSize = false;
	 for (String sizeVal : sizeVals) {
		 valueObj = new Value();
		 if(sizeVal.contains("*")){
			 isOtherSize = true;
		 }
		 if(sizeVal.contains("*")){
			 sizeVal = sizeVal.replaceAll("\\*", " X ");
		 }
		 valueObj.setValue(sizeVal);
		 listOfValues.add(valueObj);
		 
	 }
	 if(isOtherSize){
		 otherSize.setValues(listOfValues);
		 sizeObj.setOther(otherSize);
	 } else{
		 apparealObj.setType("Standard & Numbered");
		 apparealObj.setValues(listOfValues);
		 sizeObj.setApparel(apparealObj);
	 }
	 	 
	 return sizeObj;
 }

	public List<ProductSkus> getProductSkus(String colorVal, String sizeVal, String skuVal,
			List<ProductSkus> existingSkus) {
		ProductSkus productSku = new ProductSkus();
		List<ProductSKUConfiguration> listSkuConfigs = new ArrayList<>();
		ProductSKUConfiguration colorSkuConfig = getSkuConfiguration("Product Color", colorVal);
		if(!sizeVal.equals("wi")){
			ProductSKUConfiguration sizeSkuConfig = null;
			if(sizeVal.contains("*")){
				sizeVal = sizeVal.replaceAll("\\*", " X ");
				sizeSkuConfig = getSkuConfiguration("Size-Other", sizeVal);
			} else {
				 sizeSkuConfig = getSkuConfiguration("Standard & Numbered", sizeVal);
			}
			
			listSkuConfigs.add(sizeSkuConfig);
		}
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
		//skuConfig.set
		if(criteria.equals("Standard & Numbered")){
			Value value = new Value();
			value.setValue(val);
			skuConfig.setValue(Arrays.asList(value));	
		} else{
			skuConfig.setValue(Arrays.asList(val));	
		}
		return skuConfig;
	}
	private Inventory getInventory(){
		Inventory inventory = new Inventory();
		inventory.setInventoryLink("");
		inventory.setInventoryQuantity("");
		inventory.setInventoryStatus("");
		return inventory;
	}
	 public List<Option> getOptions(String optionName,String optionType,Set<String> optionValues,String additionalInfo){
		  ////Option Name = Decorative Edge Options, Option Values = Wave, Swirl, Diamond.
		  Option optionObj=new Option();
		  List<OptionValue> valuesList=new ArrayList<OptionValue>();
		  OptionValue optionValueObj=null;
		  List<Option> listOfOptins = new ArrayList<>();
		  for (String optionVal : optionValues) {
			  optionValueObj = new OptionValue();
			  optionValueObj.setValue(optionVal);
			  valuesList.add(optionValueObj);
		}
		  optionObj.setName(optionName);
		  optionObj.setOptionType(optionType);
		  optionObj.setValues(valuesList);
		  optionObj.setCanOnlyOrderOne(ApplicationConstants.CONST_BOOLEAN_FALSE);
		  optionObj.setRequiredForOrder(ApplicationConstants.CONST_BOOLEAN_FALSE);
		  optionObj.setAdditionalInformation(additionalInfo);
		  listOfOptins.add(optionObj);
		  return listOfOptins;
		  
	  }
	 public List<Availability> getProductAvailablity(List<String> childListOfProTime ,List<String> parentListImprint){
			List<Availability> listOfAvailablity = new ArrayList<>();
			Availability  availabilityObj = new Availability();
			AvailableVariations  AvailableVariObj = null;
			List<AvailableVariations> listOfVariAvail = new ArrayList<>();
			List<Object> listOfParent = null;
			List<Object> listOfChild = null;
			for (String ParentValue : parentListImprint) { //String childValue : childList
				 for (String childValue : childListOfProTime) {//String ParentValue : parentList
					 AvailableVariObj = new AvailableVariations();
					 listOfParent = new ArrayList<>();
					 listOfChild = new ArrayList<>();
					 listOfParent.add(ParentValue.trim());
					 listOfChild.add(childValue.trim()+" business days");
					 AvailableVariObj.setParentValue(listOfParent);
					 AvailableVariObj.setChildValue(listOfChild);
					 listOfVariAvail.add(AvailableVariObj);
				}
			}
			availabilityObj.setAvailableVariations(listOfVariAvail);
			availabilityObj.setParentCriteria("");
			availabilityObj.setChildCriteria("");
			listOfAvailablity.add(availabilityObj);
			return listOfAvailablity;
		}
  public List<AvailableVariations> getProductAvailabilityVariations(String parentVal,String childVal,List<AvailableVariations> listOfAvailabli){
 	 AvailableVariations  AvailableVariObj = new AvailableVariations();
 	 List<Object> listOfParent = new ArrayList<>();
			List<Object> listOfChild = new ArrayList<>();
			listOfParent.add(parentVal.trim());
			 listOfChild.add(childVal.trim()+" business days");
			 AvailableVariObj.setParentValue(listOfParent);
			 AvailableVariObj.setChildValue(listOfChild);
			 listOfAvailabli.add(AvailableVariObj);
 	 return listOfAvailabli;
  }
}
