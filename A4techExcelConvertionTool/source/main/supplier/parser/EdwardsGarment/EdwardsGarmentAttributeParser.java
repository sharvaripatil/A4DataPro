package parser.EdwardsGarment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.AvailableVariations;
import com.a4tech.product.model.BlendMaterial;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductSKUConfiguration;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
public class EdwardsGarmentAttributeParser {

	private static final Logger _LOGGER = Logger.getLogger(EdwardsGarmentAttributeParser.class);
	private LookupServiceData lookupServiceDataObj;
	
public Product getExistingProductData(Product existingProduct , ProductConfigurations existingProductConfig){
		
		ProductConfigurations newProductConfigurations=new ProductConfigurations();
		Product newProduct=new Product();
		
		try{
			//categories
			List<String> listCategories=new ArrayList<String>();
			listCategories=existingProduct.getCategories();
			if(!CollectionUtils.isEmpty(listCategories)){
				newProduct.setCategories(listCategories);
			}
			
		//themes
		List<Theme>	themes=existingProductConfig.getThemes();
		if(!CollectionUtils.isEmpty(themes)){
			newProductConfigurations.setThemes(themes);
		}
		newProduct.setProductConfigurations(newProductConfigurations);
		}catch(Exception e){
			_LOGGER.error("Error while processing Existing Product Data " +e.getMessage());
		}
		 _LOGGER.info("Completed processing Existing Data");
		return newProduct;
		
	}
	
public List<Image> getImages(List<String> imagesList){
	
	List<Image> imgList=new ArrayList<Image>();
	int rank=1;
	for (String imageStr : imagesList) {
		Image ImgObj= new Image();
		if(!imageStr.contains("http://")){//http://
			imageStr="http://"+imageStr;
		}
		
        ImgObj.setImageURL(imageStr);
        if(rank==1){
        ImgObj.setRank(rank);
        ImgObj.setIsPrimary(ApplicationConstants.CONST_BOOLEAN_TRUE);
        }else{
        ImgObj.setRank(rank);
        ImgObj.setIsPrimary(ApplicationConstants.CONST_BOOLEAN_FALSE);
        }
        imgList.add(ImgObj);
        
        rank++;
	}
	
	return imgList;
}

@SuppressWarnings("unused")
//public List<Color> getProductColors(Set <String> colorSet) {//(List<String> skuList)
public List<Color> getProductColors(List<String> colorListValue){
	List<Color> listOfColors = new ArrayList<>();
	try{
	Color colorObj = null;
	List<Color> colorList = new ArrayList<Color>();
	//Iterator<String> colorIterator=colorSet.iterator();
	//while (colorIterator.hasNext()) {
	for(String tempValue :colorListValue){
	String color = tempValue;//(String) colorIterator.next();
	color=color.replaceAll("\\|",",");
	String[] colors =getValuesOfArray(color, ",");
	for (String colorName : colors) {
		colorName=colorName.trim();
		if(StringUtils.isEmpty(colorName)){
			continue;
		}
		colorName=colorName.replaceAll("&","/");
		colorName=colorName.replaceAll(" w/","/");
		colorName=colorName.replaceAll(" with","/");
		colorName=colorName.replaceAll(" W/","/");
		colorName=colorName.replaceAll("w/","/");
		//colorName = colorName.trim();
		
		colorObj = new Color();
		String colorGroup = EdwardGarmentConstants.getColorGroup(colorName.trim());
		//if (colorGroup == null) {
			//if (colorGroup!=null && colorGroup.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
		//if (colorName.contains("/") || colorGroup.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
		if (colorName.contains("/")) {
			if(colorGroup==null){
				colorGroup=colorName;
			}
			colorGroup=colorGroup.replaceAll("&","/");
			colorGroup=colorGroup.replaceAll(" w/","/");
			colorGroup=colorGroup.replaceAll(" W/","/");
			colorGroup=colorGroup.replaceAll("w/","/");
			
			//if (colorName.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
				if(isComboColor(colorGroup)){
					List<Combo> listOfCombo = null;
					String[] comboColors = CommonUtility.getValuesOfArray(colorGroup,
							ApplicationConstants.CONST_DELIMITER_FSLASH);
					String colorFirstName = EdwardGarmentConstants.getColorGroup(comboColors[0].trim());
					colorObj.setName(colorFirstName == null?"Other":colorFirstName);
					int combosSize = comboColors.length;
					if (combosSize == ApplicationConstants.CONST_INT_VALUE_TWO) {
						String colorComboFirstName = EdwardGarmentConstants.getColorGroup(comboColors[1].trim());
						colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
						listOfCombo = getColorsCombo(colorComboFirstName, ApplicationConstants.CONST_STRING_EMPTY,
								combosSize);
					} else{
						String colorComboFirstName = EdwardGarmentConstants.getColorGroup(comboColors[1].trim());
						colorComboFirstName = colorComboFirstName == null?"Other":colorComboFirstName;
						String colorComboSecondName = EdwardGarmentConstants.getColorGroup(comboColors[2].trim());
						colorComboSecondName = colorComboSecondName == null?"Other":colorComboSecondName;
						listOfCombo = getColorsCombo(colorComboFirstName,colorComboSecondName, combosSize);
					}
					//
					//String alias = colorGroup.replaceAll(ApplicationConstants.CONST_DELIMITER_FSLASH, "-");
					////
					colorObj.setAlias(colorGroup);
					colorObj.setCombos(listOfCombo);
				} else {
					String[] comboColors = CommonUtility.getValuesOfArray(colorGroup,
							ApplicationConstants.CONST_DELIMITER_FSLASH);
					String mainColorGroup = EdwardGarmentConstants.getColorGroup(comboColors[0].trim());
					if(mainColorGroup != null){
						colorObj.setName(mainColorGroup);
						colorObj.setAlias(colorName);
					} else {
						colorObj.setName(ApplicationConstants.CONST_VALUE_TYPE_OTHER);
						colorObj.setAlias(colorName);
					}
				}
			/*} else {
				if (colorGroup == null) {
				colorGroup = ApplicationConstants.CONST_VALUE_TYPE_OTHER;
				}
				colorObj.setName(colorGroup);
				colorObj.setAlias(colorName);
			}*/
		} else {
			if (colorGroup == null) {
				colorGroup = ApplicationConstants.CONST_VALUE_TYPE_OTHER;
				}
			colorObj.setName(colorGroup);
			colorObj.setAlias(colorName);
		}
		listOfColors.add(colorObj);
	}
		}
	}catch(Exception e){
		_LOGGER.error("Error while processing color: "+e.getMessage());
	}
	return listOfColors;
}
private List<Combo> getColorsCombo(String firstValue,String secondVal,int comboLength){
	List<Combo> listOfCombo = new ArrayList<>();
	Combo comboObj1 = new Combo();
	Combo comboObj2 = new Combo();
	comboObj1.setName(firstValue);
	comboObj1.setType(ApplicationConstants.CONST_STRING_SECONDARY);
	comboObj2.setName(secondVal);
	comboObj2.setType(ApplicationConstants.CONST_STRING_TRIM);
	if(comboLength == ApplicationConstants.CONST_INT_VALUE_TWO){
		listOfCombo.add(comboObj1);
	} else {
		listOfCombo.add(comboObj1);
		listOfCombo.add(comboObj2);
	}
	return listOfCombo;
}

public static boolean isComboColor(String colorValue){
	String[] colorVals = CommonUtility.getValuesOfArray(colorValue, "/");
	String mainColor       = null;
	String secondaryColor  = null;
	String thirdColor      = null;
	if(colorVals.length == ApplicationConstants.CONST_INT_VALUE_TWO){
		 mainColor = EdwardGarmentConstants.getColorGroup(colorVals[0].trim());
		 secondaryColor = EdwardGarmentConstants.getColorGroup(colorVals[1].trim());
		 if(mainColor != null && secondaryColor != null){
			 return true;
		 }
	} else if(colorVals.length == ApplicationConstants.CONST_INT_VALUE_THREE){
		 mainColor      = EdwardGarmentConstants.getColorGroup(colorVals[0].trim());
		 secondaryColor = EdwardGarmentConstants.getColorGroup(colorVals[1].trim());
		 thirdColor     = EdwardGarmentConstants.getColorGroup(colorVals[2].trim());
		 if(mainColor != null && secondaryColor != null && thirdColor != null){
			 return true;
		 }
	} else{
		
	}
	return false;
	}
	public static String[] getValuesOfArray(String data,String delimiter){
	   if(!StringUtils.isEmpty(data)){
		   return data.split(delimiter);
	   }
	   return null;
   }
	
	public ShippingEstimate getShippingEstimates(String shippinglen,String shippingWid,String shippingH, String shippingWeightValue,
			String noOfitem,ShippingEstimate ShipingObj) {
		//ShippingEstimate ItemObject = new ShippingEstimate();
		try{
		List<NumberOfItems> listOfNumberOfItems = new ArrayList<NumberOfItems>();
		List<Weight> listOfWeight = new ArrayList<Weight>();
		NumberOfItems itemObj = new NumberOfItems();
	
			//List<Dimensions> dimenlist = new ArrayList<Dimensions>();
			Dimensions dimensionObj = new Dimensions();
			
				if(!StringUtils.isEmpty(shippinglen.trim())){
				dimensionObj.setLength(shippinglen.trim());
				dimensionObj.setLengthUnit("in");
				ShipingObj.setDimensions(dimensionObj);
				}
				if(!StringUtils.isEmpty(shippingWid.trim())){
				dimensionObj.setWidth(shippingWid.trim());
				dimensionObj.setWidthUnit("in");
				ShipingObj.setDimensions(dimensionObj);
				}
				if(!StringUtils.isEmpty(shippingH.trim())){
				dimensionObj.setHeight(shippingH.trim());
				dimensionObj.setHeightUnit("in");
				ShipingObj.setDimensions(dimensionObj);
				}
				//dimenlist.add(dimensionObj);
				//ShipingObj.setDimensions(dimensionObj);
				
				//shippingWeightValue
				if(!StringUtils.isEmpty(shippingWeightValue.trim())){
					if(shippingWeightValue.equalsIgnoreCase("0") || shippingWeightValue.equalsIgnoreCase("NO")){
					
					}else{
						Weight weightObj = new Weight();
						weightObj.setUnit("lbs");
						weightObj.setValue(shippingWeightValue);
						listOfWeight.add(weightObj);
						ShipingObj.setWeight(listOfWeight);
					}
				
				}
				
				//shippingNoofItem
				if(!StringUtils.isEmpty(noOfitem.trim())){
					if(noOfitem.equalsIgnoreCase("0") || noOfitem.equalsIgnoreCase("NO")){
						
					}else{
					itemObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_NUMBER_UNIT_CARTON);
					itemObj.setValue(noOfitem);
					listOfNumberOfItems.add(itemObj);
					ShipingObj.setNumberOfItems(listOfNumberOfItems);
					}
				}
		}catch(Exception e){
			_LOGGER.error("Error while processing Shipping Estimate :"+e.getMessage());
			return new ShippingEstimate();
		}
		return ShipingObj;

	}
	
	public List<Material> getMaterialValue(String material) {
		  material=material.replace(";","/");
		  material=material.replace(",","/");
			
		  List<Material> materiallist = new ArrayList<Material>();
		  Material materialObj = new Material();
		  List<String> listOfLookupMaterial = getMaterialType(material
		    .toUpperCase());
		  String tempAliasForblend=material;
		  /*if(listOfLookupMaterial.get(0).equalsIgnoreCase("OTHER"))
		  {
		   listOfLookupMaterial.remove(0);
		   Collections.swap(listOfLookupMaterial, 0, 1);
		   
		  }*/
		 
		  if (!listOfLookupMaterial.isEmpty()) { 
			  if(isBlendMaterial(material)){ 
		   //if (listOfLookupMaterial.size()==2 ||  listOfLookupMaterial.size()==3) {
			   
			   String tempArr[]=material.split("/");
			   List<String> listOfLookupMaterialBlend1 = getMaterialType(tempArr[0]
					    .toUpperCase());
			   if (listOfLookupMaterialBlend1.isEmpty()) { 
				   listOfLookupMaterialBlend1.add("Other Fabric");
			   }
			   List<String> listOfLookupMaterialBlend2 = getMaterialType(tempArr[1]
					    .toUpperCase());
			   if (listOfLookupMaterialBlend2.isEmpty()) { 
				   listOfLookupMaterialBlend2.add("Other Fabric");
			   }
			   String PercentageValue[]=new String [2];
			   if(material.contains("%")){
					   PercentageValue=material.split("%");
					  PercentageValue[0]=PercentageValue[0].replace("[^0-9|.x%/ ]", "");
				  }else{
					  PercentageValue[0]="50";
				  }
			   
					  
					  
		    BlendMaterial blendObj=new BlendMaterial();
		    BlendMaterial blendObj1=new BlendMaterial();
		                int PercentageValue1=100-Integer.parseInt(PercentageValue[0]);
		                String PercentageValue2=Integer.toString(PercentageValue1);
		    materialObj.setName("Blend");
		       List<BlendMaterial> listOfBlend= new ArrayList<>();
		       blendObj.setPercentage(PercentageValue[0]);
		       blendObj.setName(listOfLookupMaterialBlend1.get(0));
		       blendObj1.setPercentage(PercentageValue2);
		       blendObj1.setName(listOfLookupMaterialBlend2.get(0));
		       listOfBlend.add(blendObj);
		       listOfBlend.add(blendObj1);
		       materialObj.setAlias(tempAliasForblend);
		    materialObj.setBlendMaterials(listOfBlend);
		    materiallist.add(materialObj); 
		  // }
		  }else {  
		    materialObj = getMaterialValue(listOfLookupMaterial.toString(),
		      material);
		    materiallist.add(materialObj); 
		   }
		   
		       
		  }   else {  
			  materialObj.setName("Other");
	    		// materialObj.setAlias(values[1]);//
	    		 materialObj.setAlias(material);
			    materiallist.add(materialObj); 
			   }
		  
		  return materiallist;
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
		
		/*if(data.split("_").length ==2 ){ //51% Polyester/49% Cocona� 37.5
			return true;
		}else if(data.split("/").length ==2){  //55% Cotton_45% Polyester
			return true;}*/
		return false;
	}
		
	public static boolean  isComboMtrl(String material){
		boolean flag=false;
		if(material.equalsIgnoreCase("88% Polyvinyl Chloride/12% Ply%%%%WATERPROOF")){
			flag=true;
		}else if(material.equalsIgnoreCase("80% PVC/20% Polyester%%%%WATERPROOF")){
			flag=true;
		}else if(material.equalsIgnoreCase("62% Cotton/38% PVC%%%%WATERPROOF")){
			flag=true;
		}
		/*String MaterialForCombo=materialValue1.replaceAll("%","");
 		materialValue1=materialValue1.replaceAll("%","");
 		materialValue1=materialValue1.replaceAll("88 Polyvinyl Chloride/12 PlyWATERPROOF","Polyester:Vinyl");
	    materialValue1=materialValue1.replaceAll( "80 PVC/20 PolyesterWATERPROOF","PVC:Polyester");
		materialValue1=materialValue1.replaceAll("62 Cotton/38 PVCWATERPROOF","Cotton:PVC");*/
		return flag;
	}
	
	public List<Value> getApparelValuesObj(List<String> sizesList){
		 List<Value> listOfValue = new ArrayList<>();
			try{
				for (String value : sizesList) {
					if(!value.equals("0")){
					Value valObj = new Value();
					valObj.setValue(value);
					listOfValue.add(valObj);
					}
				}
			}catch(Exception e){
				_LOGGER.error("Error while processing size "+e.getMessage());
			}
		 return listOfValue;
		}
	//public List<ProductSkus> getProductSkus(String colorVal,String sizeVal,String skuVal,
	//public List<ProductSkus> getProductSkus(Set<String> skuSet) //List<String> sizesList
	public List<ProductSkus> getProductSkus(List<String> skuList,String criteriaOne,String criteriaTwo)
            {
		//Iterator<String> colorIterator=skuSet.iterator();
		List<ProductSkus> listProductSkus = new ArrayList<>();
		try{
		//while (colorIterator.hasNext()) {
			for (String value : skuList) {
			String skuValue = value;//(String) colorIterator.next();
			String tempArr[]=skuValue.split("_____");
			String size=tempArr[0];
			String colorValue=tempArr[1];
			String skuNo=tempArr[2];
			colorValue=colorValue.replaceAll("&","/");
			colorValue=colorValue.replaceAll(" w/","/");
			colorValue=colorValue.replaceAll(" with","/");
			colorValue=colorValue.replaceAll(" W/","/");
		ProductSkus productSku = new ProductSkus();
		if(!StringUtils.isEmpty(skuNo) && !skuNo.equals("BBBBB")){
			List<ProductSKUConfiguration> listSkuConfigs = new ArrayList<>();
		ProductSKUConfiguration colorSkuConfig = getSkuConfiguration(criteriaOne, colorValue);//"Product Color"
		if(!StringUtils.isEmpty(size) && !size.equals("0")){
			ProductSKUConfiguration sizeSkuConfig = getSkuConfiguration(criteriaTwo, size);//"Standard & Numbered"
			listSkuConfigs.add(sizeSkuConfig);
		}
		listSkuConfigs.add(colorSkuConfig);
		productSku.setSKU(skuNo);
		productSku.setConfigurations(listSkuConfigs);
		listProductSkus.add(productSku);
		}
		}
		}catch(Exception e){
			_LOGGER.error("Error while processing sku "+e.getMessage());
		}
		return listProductSkus;

	}
	private ProductSKUConfiguration getSkuConfiguration(String criteria,String val){
		ProductSKUConfiguration skuConfig = new ProductSKUConfiguration();
		skuConfig.setCriteria(criteria);
		skuConfig.setValue(Arrays.asList(val));
		return skuConfig;
	}
	
	public boolean checkValues(HashSet<String> newSet,HashMap<String,HashSet<String>> tempMap){
		boolean flag=false;
		for ( Map.Entry<String, HashSet<String>> entry : tempMap.entrySet() ) {
		    String k = entry.getKey();
		    HashSet<String> v = entry.getValue();
		    if(!org.apache.commons.collections.CollectionUtils.isEqualCollection(newSet, v)){
				flag=false;
			}
		    else{
		    	flag=true;
		    }
		}
		
		return false;
		
	}
	
	
	public boolean getAvailibilityStatus(HashMap<String,HashSet<String>> tempMap){
		
		boolean flag= false;
		try{
		if(tempMap.size()==1){
			return false;
		}else if(tempMap.size()==2){
			HashSet<String> setTemp1= new HashSet<String>();//tempMap.get(0);
			HashSet<String> setTemp2= new HashSet<String>();//tempMap.get(1);
			int i=1;
			for (Map.Entry<String,HashSet<String>> entry : tempMap.entrySet()) {
				if(i==1){
					setTemp1=entry.getValue();
				}else{
					setTemp2=entry.getValue();
				}
			    // ...
			    i++;
			}

			 if(org.apache.commons.collections.CollectionUtils.isEqualCollection(setTemp1, setTemp2)){
				 return false;
			 }else{
				 return true;
			 }
			 
		}else if(tempMap.size()>2){
			Map.Entry<String,HashSet<String>> entry = tempMap.entrySet().iterator().next();
			HashSet<String> setTemp1= entry.getValue();//tempMap.get(0);
			String oneVal=entry.getKey();
			tempMap.remove(oneVal);
			//tempMap=tempMap.remove
			 for (HashSet<String> setTemp : tempMap.values()){
				 if(org.apache.commons.collections.CollectionUtils.isEqualCollection(setTemp1, setTemp)){
						flag=false;
					}else{
						flag=true;
						break;
					}
		        }
		}else{
			 return false;
		}
		}catch(Exception e){
			_LOGGER.error("Error while comparing avail Map");
		}
		return flag;
    }
	
	
	public List<Availability> getProductAvailablity(HashMap<String,HashSet<String>> tempMap) {	
		List<Availability> listOfAvailablity = new ArrayList<>();
		try{
		Availability  availabilityObj = new Availability();
		AvailableVariations  AvailableVariObj = null;
		List<AvailableVariations> listOfVariAvail = new ArrayList<>();
		List<Object> listOfParent = null;
		List<Object> listOfChild = null;
		
		for (Map.Entry<String,HashSet<String>> entry : tempMap.entrySet()) {
		    String ParentValue = entry.getKey();
		    //ParentValue=ApplicationConstants.OPTION_MAP.get(ParentValue);
		    HashSet<String> childSet = entry.getValue();
		    for (String childValue : childSet) {
				 AvailableVariObj = new AvailableVariations();
				 listOfParent = new ArrayList<>();
				 listOfChild = new ArrayList<>();
				 listOfParent.add(childValue);//childValue
				 listOfChild.add(ParentValue);
				 AvailableVariObj.setParentValue(listOfParent);
				 AvailableVariObj.setChildValue(listOfChild);
				 listOfVariAvail.add(AvailableVariObj);
			}
		    
		}
		availabilityObj.setAvailableVariations(listOfVariAvail);
		availabilityObj.setParentCriteria("Size");//"Product Color"
		availabilityObj.setChildCriteria("Product Color");//"Size"
		listOfAvailablity.add(availabilityObj);
		}catch(Exception e){
		   _LOGGER.error("Error while processing Options :"+e.getMessage());          
	   return new ArrayList<Availability>();
	   
		}
		return listOfAvailablity;
		}
	
public LookupServiceData getLookupServiceDataObj() {
	return lookupServiceDataObj;
}

public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
	this.lookupServiceDataObj = lookupServiceDataObj;
}

	
}
