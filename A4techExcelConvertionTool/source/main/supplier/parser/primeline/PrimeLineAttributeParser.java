package parser.primeline;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

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
	
	public Product getExistingProductData(Product existingProduct , ProductConfigurations existingProductConfig,String accessToken){
		Product newProduct=new Product();
		try{
		ProductConfigurations newProductConfigurations=new ProductConfigurations();
		
		//currently dont know what existing data needs to be kept.
		//newProduct=existingProduct;
		//newProductConfigurations=existingProductConfig;
		
		newProduct.setProductConfigurations(newProductConfigurations);
		}catch(Exception e){
		_LOGGER.error("Error while processing Existing Product Data " +e.getMessage());
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
		if(!StringUtils.isEmpty(shippingitemValue)){
			NumberOfItems itemObj = new NumberOfItems();
			itemObj.setValue(shippingitemValue);
			itemObj.setUnit("per Case");
			numberOfItems.add(itemObj);
			shippObject.setNumberOfItems(numberOfItems);
		}

		if(!StringUtils.isEmpty(shippingWeightValue)){
			Weight weightObj = new Weight();
			shippingWeightValue=shippingWeightValue.replaceAll("lbs", "").trim();
			weightObj.setValue(shippingWeightValue);
			weightObj.setUnit("lbs");
			weightList.add(weightObj);
			shippObject.setWeight(weightList);
		}

			List<Dimensions> dimenlist = new ArrayList<Dimensions>();
			Dimensions dimensionObj = new Dimensions();
			if(!StringUtils.isEmpty(dimHieght)){
			dimensionObj.setHeight(dimHieght);
			dimensionObj.setHeightUnit("in");
			}
			if(!StringUtils.isEmpty(dimLen)){
			dimensionObj.setLength(dimLen);
			dimensionObj.setLengthUnit("in");
			}
			if(!StringUtils.isEmpty(dimWidth)){
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

	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}

	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}
	
	

}
