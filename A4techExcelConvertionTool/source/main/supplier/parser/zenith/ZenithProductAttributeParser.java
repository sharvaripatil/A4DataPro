package parser.zenith;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.Capacity;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimensions;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Weight;


public class ZenithProductAttributeParser {
	private LookupServiceData lookupServiceDataObj;
	
	
	public List<ProductionTime> getProductionTime(String productionTime) {

		String originalProductionTime=productionTime;
		productionTime=productionTime.replace("to", "-").replace("working days", "");
		List<ProductionTime> listOfProductionTime = new ArrayList<>();

		ProductionTime productionTimeObj=new ProductionTime();
		
		productionTimeObj.setBusinessDays(productionTime);
		productionTimeObj.setDetails(originalProductionTime);
		
		listOfProductionTime.add(productionTimeObj);
		
		return listOfProductionTime;
	}

	
	/*public Size getSize(String sizeDimension, String sizeCapacity) {
		
		Size sizeObj=new Size();
		sizeDimension=sizeDimension.replaceAll("[^0-9.%/ ]","");
		sizeCapacity=sizeCapacity.replace("oz", "");		
          
		if(!StringUtils.isEmpty(sizeCapacity))
		{
			Capacity capacityObj=new Capacity();
			capacityObj.setValues(values);
					}
	
		return sizeObj;
	}
	*/
	
	
	
	
	
	
	
	public List<Color> getColorName(String colorName,String colorName1) {

		List<Color> listOfColor= new ArrayList<>();
		Color colorObj=new Color();

		colorObj.setAlias(colorName);
		colorObj.setName(colorName1);
		listOfColor.add(colorObj);
		
		
		return listOfColor;
	}

	public List<Material> getMaterial(String materialName) {

		List<Material> listOfMaterial= new ArrayList<>();	
		Material materialObj=new Material();
		
		materialObj.setAlias(materialName);
		if(materialName.contains("Cotton"))
		{
			materialObj.setName("Cotton");	
		}else if(materialName.contains("Polyster"))
		{
			materialObj.setName("Polyster");	

		}else if(materialName.contains("Foam"))
		{
			materialObj.setName("Foam");	

		}else  if(materialName.contains("Canvas"))
		{
			materialObj.setName("Canvas");	
		}
		
		listOfMaterial.add(materialObj);
		return listOfMaterial;
	}



	public List<Packaging> getPackaging(String packaging) {
		List<Packaging> listOfPackaging= new ArrayList<>();	
		
		Packaging packObj=new Packaging();
		
		if(packaging.contains("Bulk"))
		{
			packObj.setName("Bulk");
		}else if(packaging.contains("poly"))
		{
			packObj.setName("Poly Bag");
		}else
		{
			packObj.setName(packaging);
		}
		
		listOfPackaging.add(packObj);
		
		
		return listOfPackaging;
	}




	public List<FOBPoint> getFobValue(String fobValue) {
		List<FOBPoint> listOfFob= new ArrayList<>();
		FOBPoint fobObj=new FOBPoint();
		
		if(fobValue.contains("37616"))
		{
			fobObj.setName("Afton, TN 37616 USA");	
		}else if(fobValue.contains("30004"))
		{
			fobObj.setName("Alpharetta, GA 30004 USA");	

		}else if(fobValue.contains("75180"))
		{
			fobObj.setName("Batch Springs, TX 75180 USA");	

		}else if(fobValue.contains("76049"))
		{
			fobObj.setName("Granbury, TX 76049 USA");	

		}else if(fobValue.contains("08701"))
		{
			fobObj.setName("Lakewood, NJ 08701 USA");	

		}else if(fobValue.contains("11559"))
		{
			fobObj.setName("Lawrence, NY 11559 USA");	

		}else if(fobValue.contains("07105"))
		{
			fobObj.setName("Newark, NJ 07105");	

		}else if(fobValue.contains("85042"))
		{
			fobObj.setName("Phoenix, AZ 85042 USA");	
		}else if(fobValue.contains("78956"))
		{
			fobObj.setName("Schulenburg, TX 78956 USA");	
		}else if(fobValue.contains("46176"))
		{
			fobObj.setName("Shelbyville, IN 46176 USA");	
		}
		
		listOfFob.add(fobObj);
		
		return listOfFob;
	}

	public ShippingEstimate getShippingEstimation(String noOfItems,
			String shippingWeight, String shippingDimension) {

		ShippingEstimate shippingObj = new ShippingEstimate();
		
		List<NumberOfItems> listOfNumberOfItems = new ArrayList<>();
		NumberOfItems NumberOfItemsObj = new NumberOfItems();
		NumberOfItemsObj.setValue(noOfItems);
		NumberOfItemsObj.setUnit("Other");
		listOfNumberOfItems.add(NumberOfItemsObj);
		shippingObj.setNumberOfItems(listOfNumberOfItems);

		List<Weight> listOfWeight = new ArrayList<>();
		Weight wightObj = new Weight();
		wightObj.setValue(shippingWeight);
		wightObj.setUnit("lbs");
		listOfWeight.add(wightObj);
		shippingObj.setWeight(listOfWeight);

			Dimensions dimensionObj = new Dimensions();
			String shippingDimensioneArr[] = shippingDimension.split("x");

			dimensionObj.setLength(shippingDimensioneArr[0]);
			dimensionObj.setLengthUnit("in");

			dimensionObj.setWidth(shippingDimensioneArr[1]);
			dimensionObj.setWidthUnit("in");

			dimensionObj.setHeight(shippingDimensioneArr[2]);
			dimensionObj.setHeightUnit("in");

			shippingObj.setDimensions(dimensionObj);
		

			
		
		return shippingObj;
	}




	
	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}


	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}



}




