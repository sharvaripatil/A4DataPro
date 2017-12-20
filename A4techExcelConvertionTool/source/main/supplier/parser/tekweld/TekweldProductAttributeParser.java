package parser.tekweld;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;



public class TekweldProductAttributeParser {
	private LookupServiceData lookupServiceDataObj;
	
	
	public Size getProductsize(String productSize) {
		Size sizeObj=new Size();
		String NewProductsize[]=productSize.split("U");
		NewProductsize[0]=NewProductsize[0].replace("Assembled - ", "");
		
		  Dimension dimensionObj=new Dimension();
 
		     List<Values> listOfValues= new ArrayList<>();
		      Values ValuesObj=new Values();
		      List<Value> listOfValue= new ArrayList<>();
		      Value ValueObj=new Value();


	         String productSizeArr[]=NewProductsize[0].split("x");
	         
	         for (int i=0;i<productSizeArr.length;i++) {
	       	  
	            ValueObj=new Value();
	     		ValueObj.setUnit("in");
	     		
	     		if(productSizeArr[i].contains("L")){
	   	     	ValueObj.setAttribute("Length");
	   	        productSizeArr[i]=productSizeArr[i].replace("L", "");
	     		}else if(productSizeArr[i].contains("W"))
	     		{
	     		ValueObj.setAttribute("Width");	
	   	        productSizeArr[i]=productSizeArr[i].replace("W", "");
	     		}else if(productSizeArr[i].contains("H"))
	     		{
	         	ValueObj.setAttribute("Height");	
	   	        productSizeArr[i]=productSizeArr[i].replace("H", "");
	     		}
	     		else if(productSizeArr[i].contains("D"))
	     		{
	         	ValueObj.setAttribute("Depth");	
	   	        productSizeArr[i]=productSizeArr[i].replace("D", "");
	     		}
	     		ValueObj.setValue(productSizeArr[i].replace("\"", "").trim());

	     		listOfValue.add(ValueObj);
	   	
		     	}
	     	ValuesObj.setValue(listOfValue);
			listOfValues.add(ValuesObj);	
	 		dimensionObj.setValues(listOfValues);
	       sizeObj.setDimension(dimensionObj);
			
			 
		return sizeObj;
	}

	

	 
	 
	 
	 
	 
	 
	 
	 
				
	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}


	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}


	



	
}




