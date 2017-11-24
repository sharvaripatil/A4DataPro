package parser.maxplus;
import java.util.ArrayList;
import java.util.List;
import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.NumberOfItems;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Weight;


public class MaxpluProductAttributeParser {
	private LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;
	
	
	public List<Color> getColorCriteria(String colorMapping) {
		String ColorValue=colorMapping.toString();
	
		List<Color> listOfProductColors = new ArrayList<>();
		Color colorObj=new Color();
		String[] colorValues =ColorValue.split("/");
		
		for (String colorName : colorValues) {
	
			colorObj=new Color();
			String OriginalcolorName = colorName.trim();
			String colorLookUpName=MaxplusLookupData.COLOR_MAP.get(colorName.trim()).trim();
			colorObj.setAlias(OriginalcolorName);
			colorObj.setName(colorLookUpName);
			listOfProductColors.add(colorObj);			
		
		}
		return listOfProductColors;
	}	
	
	public List<ImprintMethod>  getImprintMethod(String imprintMethod) {

		List<ImprintMethod> listimprintMethods = new ArrayList<ImprintMethod>();
		ImprintMethod imprintMethodObj=new ImprintMethod();

		String imprintArr[]=imprintMethod.split(",");
		
		for (String ImprintName : imprintArr) {
		
			imprintMethodObj=new ImprintMethod();
			imprintMethodObj.setAlias(ImprintName);
			
			if(ImprintName.equalsIgnoreCase("Digital Print"))
	    	{
			imprintMethodObj.setType("Printed");
			}else if(ImprintName.equalsIgnoreCase("Silkscreen")){
			imprintMethodObj.setType("Silkscreen");
			}else if(ImprintName.equalsIgnoreCase("Laser Engrave"))
			{
			imprintMethodObj.setType("Laser Engraved");
			}
			listimprintMethods.add(imprintMethodObj);
		}	
		return listimprintMethods;
	}
	
	
	public List<ImprintSize> getImprintSize(
			String imprintsize) {
		imprintsize=imprintsize.replace("<br>", "");
	
		List<ImprintSize> listOfImprintSize = new ArrayList<ImprintSize>();

		ImprintSize imprintsizeObj=new ImprintSize();
		String imprintSizeArr[]=imprintsize.split(",");
		
		for (String Imprintsize : imprintSizeArr) {	
			
			imprintsizeObj=new ImprintSize();
			imprintsizeObj.setValue(Imprintsize);
			listOfImprintSize.add(imprintsizeObj);			
	    	}			
		return listOfImprintSize;
	}

	

	 public Size getSize(String size) {
		 String originalSize=size;
		 size=size.replaceAll("[^0-9-|b./x%/ ]","");
		 size=size.replaceAll("-"," ");
		 Size sizeObj=new Size();
	      Dimension dimensionObj=new Dimension();

		 
		 if(originalSize.contains("<br>"))
		 {
		String OutersizeArr[]=size.split("b");
		for (String InnerSize : OutersizeArr) {
			
		 
		 
		 
	     List<Values> listOfValues= new ArrayList<>();
	      Values ValuesObj=new Values();
	      List<Value> listOfValue= new ArrayList<>();
	      Value ValueObj=new Value();



         String productSizeArr[]=InnerSize.split("x");
         
         for (int i=0;i<productSizeArr.length;i++) {
       	  
           //sizeObj=new Size();   
            ValueObj=new Value();
     		ValueObj.setValue(productSizeArr[i].trim());
     		ValueObj.setUnit("in");
     		
     		if(i==0){
   	     	ValueObj.setAttribute("Length");
     		}else if(i==1)
     		{
     		ValueObj.setAttribute("Width");	
     		}else if(i==2)
     		{
         	ValueObj.setAttribute("Height");	
     		}

     		listOfValue.add(ValueObj);
   	
	     	}
     	ValuesObj.setValue(listOfValue);
		listOfValues.add(ValuesObj);	
 		dimensionObj.setValues(listOfValues);
	//	 }
       sizeObj.setDimension(dimensionObj);
		}
		 }
		return sizeObj;
		
	}

	 public ShippingEstimate getShippingestimete(String shippingItem,
				String shippingWeight) {

	        ShippingEstimate shippingEstimateObj=new ShippingEstimate();
			List<NumberOfItems> numberOfItem = new ArrayList<NumberOfItems>();

			NumberOfItems itemObj=new NumberOfItems();
			itemObj.setValue(shippingItem);
			itemObj.setUnit("case");
			numberOfItem.add(itemObj);
			shippingEstimateObj.setNumberOfItems(numberOfItem);
		
					
			List<Weight> listOfWeightObj = new ArrayList<Weight>();
			Weight  weightObj = new Weight();
			weightObj.setValue(shippingWeight);
			weightObj.setUnit("lbs");
			listOfWeightObj.add(weightObj);
			shippingEstimateObj.setWeight(listOfWeightObj);		 
		 
		 return shippingEstimateObj;
		}

	 public List<Image> getImages(String image10) {
			List<Image> listImage= new ArrayList<Image>();
			Image imgObj=new Image();

			String imgArr[]=image10.split(",");
			
			for (String imgName : imgArr) {
				imgObj=new Image();
				imgObj.setImageURL(imgName);
				listImage.add(imgObj);				
			}		
			return listImage;
		}
	 
	 
	 
	 
	 
	 
	 
	 
	 
				
	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}


	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}



	public LookupRestService getLookupRestServiceObj() {
		return lookupRestServiceObj;
	}


	public void setLookupRestServiceObj(LookupRestService lookupRestServiceObj) {
		this.lookupRestServiceObj = lookupRestServiceObj;
	}

	

	



	
}




