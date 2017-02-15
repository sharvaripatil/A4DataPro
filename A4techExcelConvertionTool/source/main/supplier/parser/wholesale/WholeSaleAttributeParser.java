package parser.wholesale;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.broberry.parser.BroberryProductAttributeParser;
import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.TradeName;
import com.a4tech.util.ApplicationConstants;

public class WholeSaleAttributeParser {
	private static final Logger _LOGGER = Logger.getLogger(WholeSaleAttributeParser.class);
	private LookupServiceData lookupServiceDataObj;
	public Product getExistingProductData(Product existingProduct , ProductConfigurations existingProductConfig,String accessToken){
		
		ProductConfigurations newProductConfigurations=new ProductConfigurations();
		Product newProduct=new Product();
		//PriceGrid newPriceGrid =new PriceGrid();
		
		//if(!StringUtils.isEmpty(productKeywords)){
		List<String> listCategories=new ArrayList<String>();
		try{
			listCategories=existingProduct.getCategories();
			if(!CollectionUtils.isEmpty(listCategories)){
				newProduct.setCategories(listCategories);
			}
			List<Image> images=existingProduct.getImages();
			if(!CollectionUtils.isEmpty(images)){
				newProduct.setImages(images);
			}
			List<Theme> themes=existingProductConfig.getThemes();
			if(!CollectionUtils.isEmpty(themes)){
				newProductConfigurations.setThemes(themes);
			}
			List<String> productKeywords=existingProduct.getProductKeywords();
			if(!CollectionUtils.isEmpty(productKeywords)){
				newProduct.setProductKeywords(productKeywords);
			}
			
			List<FOBPoint> fobPoints=existingProduct.getFobPoints();
			if(!CollectionUtils.isEmpty(fobPoints)){
				newProduct.setFobPoints(fobPoints);
			}else{
				List<String> fobPointsTemp=lookupServiceDataObj.getFobPoints(accessToken);
				String tempValue=null;
				for (String string : fobPointsTemp) {
					if(string.toUpperCase().contains("NY")){
						tempValue=string;
					}
				}
				if(!StringUtils.isEmpty(tempValue)){
					fobPoints=new ArrayList<FOBPoint>();
					FOBPoint fobObj=new FOBPoint();
					fobObj.setName(tempValue);
					fobPoints.add(fobObj);
					newProduct.setFobPoints(fobPoints);
				}	
			}
			
		newProduct.setProductConfigurations(newProductConfigurations);
		}catch(Exception e){
			_LOGGER.error("Error while processing Existing Product Data " +e.getMessage());
		}
		 _LOGGER.info("Completed processing Existing Data");
		return newProduct;
		
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
	
	public List<ImprintMethod> getImprintCriteria(String imprintValue,List<ImprintMethod> impmthdList){
		ImprintMethod imprintObj=null;
		try{
			if(imprintValue.equalsIgnoreCase("UNIMPRT")){
				imprintObj=new ImprintMethod();
				imprintObj.setType("UNIMPRINTED");
	 			imprintObj.setAlias("UNIMPRINTED");
	 			impmthdList.add(imprintObj);
	 			return impmthdList;
			}
			
				String imprintArr[] = imprintValue.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
				for (String imprintMethod : imprintArr) {
					imprintObj=new ImprintMethod();
					   if(lookupServiceDataObj.isImprintMethod(imprintMethod.trim().toUpperCase())){
						   imprintObj.setType(imprintMethod.trim());
					   } else {
						   imprintObj.setType("Other");
					   }
		 			
		 			imprintObj.setAlias(imprintMethod.trim());
		 			impmthdList.add(imprintObj);
				}
		}catch(Exception e){
			_LOGGER.error("Error while processing Imprint Method :"+e.getMessage());             
		   	return null;
		   }
		return impmthdList;
		
	}
	public LookupServiceData getLookupServiceDataObj() {
	return lookupServiceDataObj;
	}
	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
	this.lookupServiceDataObj = lookupServiceDataObj;
	}
	
	
	}
