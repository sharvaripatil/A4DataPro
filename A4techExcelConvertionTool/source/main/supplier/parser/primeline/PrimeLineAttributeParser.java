package parser.primeline;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.Capacity;
import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.Personalization;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.SameDayRush;
import com.a4tech.product.model.Shape;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.TradeName;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;

public class PrimeLineAttributeParser {
	
	private static final Logger _LOGGER = Logger.getLogger(PrimeLineAttributeParser.class);
	private LookupServiceData lookupServiceDataObj;
	
	public RushTime getRushTimeValues(String rushTimeValue,String details){
		RushTime rushObj=new RushTime();
		try{ 
		List<RushTimeValue> rushValueTimeList =new ArrayList<RushTimeValue>();
		RushTimeValue rushValueObj=new RushTimeValue();
 		rushValueObj.setBusinessDays(rushTimeValue);
 		rushValueObj.setDetails(details);
 		rushValueTimeList.add(rushValueObj);
 		rushObj.setAvailable(true);
		rushObj.setRushTimeValues(rushValueTimeList);
		}catch(Exception e){
			_LOGGER.error("Error while processing RushTime :"+e.getMessage());             
		   	return new RushTime();
		   }
		return rushObj;
		
	}
	
	public List<Image> getImages(String imagesVal){
		List<String> imagesList=new ArrayList<String>();
		imagesList.add(imagesVal);
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
	
	//Shape
	public List<Shape> getProductShapes(List<String> listShp){
		List<Shape> listOfShape = new ArrayList<>();
		Shape shapeObj = new Shape();
		for (String shape : listShp) {
			if(lookupServiceDataObj.isShape(shape.toUpperCase())){
				shapeObj = new Shape();
				shapeObj.setName(shape);
				listOfShape.add(shapeObj);
			}
		}
		return listOfShape;
	}
	
	///options
		public  List<Option> getOptions(List<String> opntnList) {
			List<Option> optionList=new ArrayList<>();
			try{
				
				List<Option> optionExistList=new ArrayList<Option>();
				/*if(productExcelObj.getProductConfigurations()!=null){
					if(productExcelObj.getProductConfigurations().getOptions()!=null){
					optionExistList=productExcelObj.getProductConfigurations().getOptions();
					}
				}*/
			
			Option optionObj=new Option();
			   
				   for (Option option : optionExistList) {
					   optionList.add(option);
				}
				   
				   List<OptionValue> valuesList=new ArrayList<OptionValue>();
					 OptionValue optionValueObj=null;
					  for (String optionDataValue: opntnList) {
						  optionValueObj=new OptionValue();
						  optionValueObj.setValue(optionDataValue.trim());
						  valuesList.add(optionValueObj);
					  }
						  optionObj.setOptionType("Product");
						  optionObj.setName("Style");
						  optionObj.setValues(valuesList); 
						  optionObj.setAdditionalInformation("");
						  optionObj.setCanOnlyOrderOne(false);
						  optionObj.setRequiredForOrder(true);
						  optionList.add(optionObj);
					  //}
					  
			   }catch(Exception e){
				   _LOGGER.error("Error while processing Options :"+e.getMessage());          
			      return new ArrayList<Option>();
			      
			     }
			  return optionList;
			  
		 }
	
				//////sizes
				public Size getSizes(List<String> sizList)
				{
					Size sizeObj = new Size();
					for (String sizeStr : sizList) {
			 			Capacity capacityObj = new Capacity();
			 			String capacityArr[] = sizeStr.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
			 			List<Value> capacityvalueList = new ArrayList<Value>();
			               for (String value : capacityArr) {
			 				String capacityArr1[] = value.split(ApplicationConstants.CONST_DELIMITER_COLON);
			 				Value valObjc = new Value();
			 				valObjc.setValue(capacityArr1[0]);
			 				valObjc.setUnit(capacityArr1[1]);
			 				capacityvalueList.add(valObjc);
			 				capacityObj.setValues(capacityvalueList);
			 			}
			 			sizeObj.setCapacity(capacityObj);
					}
					
					
					
					return sizeObj;
		}
		
		
	public Product getExistingProductData(Product existingProduct , ProductConfigurations existingProductConfig,String accessToken){
		
		ProductConfigurations newProductConfigurations=new ProductConfigurations();
		Product newProduct=new Product();
		//PriceGrid newPriceGrid =new PriceGrid();
		//if(!StringUtils.isEmpty(productKeywords)){
		List<String> listCategories=new ArrayList<String>();
		try{
			if(existingProductConfig==null){
				return new Product();
			}
			//Material
			List<Material> listMaterial= existingProductConfig.getMaterials();
			if(!CollectionUtils.isEmpty(listMaterial)){
				newProductConfigurations.setMaterials(listMaterial);
			}
			//Image
			List<Image> images=existingProduct.getImages();
			if(!CollectionUtils.isEmpty(images)){
				newProduct.setImages(images);
			}
			//Rush
			if(existingProductConfig.getRushTime()!=null){
			RushTime rushTime=existingProductConfig.getRushTime();
			if(rushTime.isAvailable()){
				newProductConfigurations.setRushTime(rushTime);
			}
			}
			//Production
			List<ProductionTime> productionTime=existingProductConfig.getProductionTime();
			if(!CollectionUtils.isEmpty(productionTime)){
				 newProductConfigurations.setProductionTime(productionTime);
			}
			//Same Day
			if(existingProductConfig.getSameDayRush()!=null){
			SameDayRush sameDayRush=existingProductConfig.getSameDayRush();
			if(sameDayRush.isAvailable()){
				newProductConfigurations.setSameDayRush(sameDayRush);
			}
			}
			//Sold Unimprinted
			List<ImprintMethod> imprintMethods=existingProductConfig.getImprintMethods();
			if(!CollectionUtils.isEmpty(imprintMethods)){// i have to keep unimprinted over here
				for (ImprintMethod imprintMethod : imprintMethods) {
					if(imprintMethod.getType().equalsIgnoreCase("UNIMPRINTED")){
						List<ImprintMethod> imprintMethodList=new ArrayList<ImprintMethod>();
						imprintMethodList.add(imprintMethod);
						newProductConfigurations.setImprintMethods(imprintMethodList);
					}
				}
			}
			
			//Personalization
			List<Personalization>    listPersonalization=existingProductConfig.getPersonalization();
			if(!CollectionUtils.isEmpty(listPersonalization)){
				newProductConfigurations.setPersonalization(listPersonalization);
			}
			
			//Categories
			listCategories=existingProduct.getCategories();
			if(!CollectionUtils.isEmpty(listCategories)){
				newProduct.setCategories(listCategories);
			}
		 
		newProduct.setProductConfigurations(newProductConfigurations);
		}catch(Exception e){
			_LOGGER.error("Error while processing Existing Product Data " +e.getMessage());
			newProduct.setProductConfigurations(newProductConfigurations);
			return newProduct;
		}
		 _LOGGER.info("Completed processing Existing Data");
		return newProduct;
	}	
	
	public ShippingEstimate getShippingEstimates(String shippingitemValue,
		String shippingWeightValue,String dimLen,String dimHieght,String dimWidth) {
		ShippingEstimate shippObject = new ShippingEstimate();
		try{
		List<NumberOfItems> numberOfItems=new ArrayList<NumberOfItems>();
		List<Weight>       weightList=new ArrayList<Weight>();
		if(!StringUtils.isEmpty(shippingitemValue) && !shippingitemValue.equalsIgnoreCase("NULL")){//
			NumberOfItems itemObj = new NumberOfItems();
			itemObj.setValue(shippingitemValue);
			itemObj.setUnit("per Case");
			numberOfItems.add(itemObj);
			shippObject.setNumberOfItems(numberOfItems);
		}

		if(!StringUtils.isEmpty(shippingWeightValue) && !shippingitemValue.equalsIgnoreCase("NULL")){
			Weight weightObj = new Weight();
			shippingWeightValue=shippingWeightValue.replaceAll("lbs", "").trim();
			weightObj.setValue(shippingWeightValue);
			weightObj.setUnit("lbs");
			weightList.add(weightObj);
			shippObject.setWeight(weightList);
		}

			List<Dimensions> dimenlist = new ArrayList<Dimensions>();
			Dimensions dimensionObj = new Dimensions();
			if(!StringUtils.isEmpty(dimHieght) && !shippingitemValue.equalsIgnoreCase("NULL")){
			dimensionObj.setHeight(dimHieght);
			dimensionObj.setHeightUnit("in");
			}
			if(!StringUtils.isEmpty(dimLen) && !shippingitemValue.equalsIgnoreCase("NULL")){
			dimensionObj.setLength(dimLen);
			dimensionObj.setLengthUnit("in");
			}
			if(!StringUtils.isEmpty(dimWidth) && !shippingitemValue.equalsIgnoreCase("NULL")){
			if(dimWidth.contains("(")){
				dimWidth=dimWidth.replaceAll("\\(.*?\\)", "").trim();
			}
			dimensionObj.setWidth(dimWidth);
			dimensionObj.setWidthUnit("in");
			}
			dimenlist.add(dimensionObj);
			shippObject.setDimensions(dimensionObj);
		}catch(Exception e){
			_LOGGER.error("Error while processing Shipping Estimate :"+e.getMessage());
			return new ShippingEstimate();
		}
		return shippObject;
	}
	
	public List<String> getCategories(String category,List<String> listOfCategories){
		   //List<String> listOfCategories = new ArrayList<>();
		  // String[] categories = CommonUtility.getValuesOfArray(category, ApplicationConstants.CONST_STRING_COMMA_SEP);
		  // for (String categoryName : categories) {
			   if(lookupServiceDataObj.isCategory(category.toUpperCase())){
				   listOfCategories.add(category);
			   }
		//}
		   return listOfCategories;
	}
	
	public List<Material> getMaterials(List<String> materialList){
		
		List<Material> listOfProductMaterial = new ArrayList<Material>();
		Material materialObj = null;
		//if(!StringUtils.isEmpty(productMaterial)){
		for (String productMaterial : materialList) {
			String tempStr=productMaterial;
			materialObj = new Material();
			productMaterial=getTypeOfMaterial(productMaterial);
			//materialObj.setName(productMaterial);
			materialObj.setName("Leather");// i have to work on this
			materialObj.setAlias(tempStr);
			listOfProductMaterial.add(materialObj);
		}//}
		return listOfProductMaterial;
	}
	
	private String getTypeOfMaterial(String matrlVal){
	   	  List<String> lookupMaterials = lookupServiceDataObj.getMaterialValues();
	   	  for (String mtrlLookupName : lookupMaterials) {
	   		   if(matrlVal.contains(mtrlLookupName)){
	   			   return mtrlLookupName;
	   		   }		  
	   	}
	   	  if(matrlVal.contains("FABRIC")){
	   		  return "Other Fabric";
	   	  }
	   	  return ApplicationConstants.CONST_VALUE_TYPE_OTHER;
	   }

	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}

	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}
	
	

}
