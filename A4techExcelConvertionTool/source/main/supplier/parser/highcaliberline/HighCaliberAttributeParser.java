package parser.highcaliberline;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.a4tech.product.broberry.mapping.BroberryExcelMapping;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Weight;
import com.a4tech.util.ApplicationConstants;

public class HighCaliberAttributeParser {
	private static final Logger _LOGGER = Logger.getLogger(HighCaliberAttributeParser.class);
	public List<Image> getImages(String image){
		List<String> imagesList=new ArrayList<String>();
		imagesList.add(image);
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
	
	
	public List<Color> getColorCriteria(String colorValue) {
		
		Color colorObj = null;
		List<Color> colorList = new ArrayList<Color>();
		//HighCaliberConstants
		try {
		//Map<String, String> HCLCOLOR_MAP=new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		// Map<String, String> HCLCOLOR_MAP =HighCaliberConstants.getHCLCOLOR_MAP();
			List<Combo> comboList = null;
			String value = colorValue;
			String tempcolorArray[]=value.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
			for (String colorVal : tempcolorArray) {
			String strColor=colorVal;
			strColor=strColor.replaceAll("&","/");
			//strColor=strColor.replaceAll(" w/","/");
			//strColor=strColor.replaceAll(" W/","/");
			boolean isCombo = false;
				colorObj = new Color();
				comboList = new ArrayList<Combo>();
    			isCombo = isComboColors(strColor);
    			if(isCombo){
    				if(HighCaliberConstants.HCLCOLOR_MAP.get(strColor.trim())!=null){
    				//if(HCLCOLOR_MAP.get(strColor.trim())!=null){
    					isCombo=false;
    				}
    			}
    			
				if (!isCombo) {
					String colorName=HighCaliberConstants.HCLCOLOR_MAP.get(strColor.trim());
					//String colorName=HCLCOLOR_MAP.get(strColor.trim());
					if(StringUtils.isEmpty(colorName)){
						colorName=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
					}
					colorObj.setName(colorName);
					colorObj.setAlias(colorVal.trim());
					colorList.add(colorObj);
				} else {
					//245-Mid Brown/Navy
					String colorArray[] = strColor.split(ApplicationConstants.CONST_DELIMITER_FSLASH);
					//if(colorArray.length==2){
					String combo_color_1=HighCaliberConstants.HCLCOLOR_MAP.get(colorArray[0].trim());
					//String combo_color_1=HCLCOLOR_MAP.get(colorArray[0].trim());
					if(StringUtils.isEmpty(combo_color_1)){
						combo_color_1=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
					}
					colorObj.setName(combo_color_1);
					colorObj.setAlias(strColor);
					
					Combo comboObj = new Combo();
					String combo_color_2=HighCaliberConstants.HCLCOLOR_MAP.get(colorArray[1].trim());
					//String combo_color_2=HCLCOLOR_MAP.get(colorArray[1].trim());
					if(StringUtils.isEmpty(combo_color_2)){
						combo_color_2=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
					}
					comboObj.setName(combo_color_2.trim());
					comboObj.setType(ApplicationConstants.CONST_STRING_SECONDARY);
					if(colorArray.length==3){
						String combo_color_3=HighCaliberConstants.HCLCOLOR_MAP.get(colorArray[2].trim());
						//String combo_color_3=HCLCOLOR_MAP.get(colorArray[2].trim());
						if(StringUtils.isEmpty(combo_color_3)){
							combo_color_3=ApplicationConstants.CONST_STRING_UNCLASSIFIED_OTHER;
						}
						Combo comboObj2 = new Combo();
						comboObj2.setName(combo_color_3.trim());
						comboObj2.setType(ApplicationConstants.CONST_STRING_TRIM);
						comboList.add(comboObj2);
					}
					comboList.add(comboObj);
					colorObj.setCombos(comboList);
					colorList.add(colorObj);
				 	}
		}
		//}
		} catch (Exception e) {
			_LOGGER.error("Error while processing Color :" + e.getMessage());
			return new ArrayList<Color>();
		}
		_LOGGER.info("Colors Processed");
		return colorList;
		}
	
	private boolean isComboColors(String value) {
		boolean result = false;
		if (value.contains("/")) {
			result = true;
		}
		return result;
	}
	
	public ShippingEstimate getShippingEstimates(String shippinglen,String shippingWid,String shippingH, String shippingWeightValue,
			String noOfitem) {
		ShippingEstimate ItemObject = new ShippingEstimate();
		try{
		List<NumberOfItems> listOfNumberOfItems = new ArrayList<NumberOfItems>();
		List<Weight> listOfWeight = new ArrayList<Weight>();
		NumberOfItems itemObj = new NumberOfItems();
	
			List<Dimensions> dimenlist = new ArrayList<Dimensions>();
			Dimensions dimensionObj = new Dimensions();
			
				if(!StringUtils.isEmpty(shippinglen)){
				dimensionObj.setLength(shippinglen);
				dimensionObj.setLengthUnit("in");
				}
				if(!StringUtils.isEmpty(shippingWid)){
				dimensionObj.setWidth(shippingWid);
				dimensionObj.setWidthUnit("in");
				}
				if(!StringUtils.isEmpty(shippingH)){
				dimensionObj.setHeight(shippingH);
				dimensionObj.setHeightUnit("in");
				}
				dimenlist.add(dimensionObj);
				ItemObject.setDimensions(dimensionObj);
				
				//shippingWeightValue
				if(!StringUtils.isEmpty(shippingWeightValue)){
					if(shippingWeightValue.equalsIgnoreCase("0")){
					
					}else{
						Weight weightObj = new Weight();
						weightObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_WEIGHT);
						weightObj.setValue(shippingWeightValue);
						listOfWeight.add(weightObj);
						ItemObject.setWeight(listOfWeight);
					}
				
				}
				
				//shippingNoofItem
				if(!StringUtils.isEmpty(noOfitem)){
					if(noOfitem.equalsIgnoreCase("0")){
						
					}else{
					itemObj.setUnit(ApplicationConstants.CONST_STRING_SHIPPING_NUMBER_UNIT_CARTON);
					itemObj.setValue(noOfitem);
					listOfNumberOfItems.add(itemObj);
					ItemObject.setNumberOfItems(listOfNumberOfItems);
					}
			
				}
		}catch(Exception e){
			_LOGGER.error("Error while processing Shipping Estimate :"+e.getMessage());
			return new ShippingEstimate();
		}
		return ItemObject;

	}
	
	public List<ProductionTime> getProdTimeCriteria(String prodTimeValue,String DetailsValue,List<ProductionTime> prodTimeList){
		//List<ProductionTime> prodTimeList =new ArrayList<ProductionTime>();
		try{
			ProductionTime prodTimeObj= new ProductionTime();
	 					prodTimeObj.setBusinessDays(prodTimeValue);
	 					prodTimeObj.setDetails(DetailsValue);
	 			prodTimeList.add(prodTimeObj);//}
			}catch(Exception e){
			_LOGGER.error("Error while processing Production Time :"+e.getMessage());
	        return new ArrayList<ProductionTime>();
		   }return prodTimeList;
		}
	
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
	
}
