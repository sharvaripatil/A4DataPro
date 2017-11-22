package parser.gempire;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;

public class GempireProductAttributeParser {

	public List<ProductionTime> getProductiontime(
			String ProductionTime) {
		List<ProductionTime> ListOfProductiontime = new ArrayList<ProductionTime>();		
		ProductionTime prodTimeObj=new ProductionTime();
		if(ProductionTime.contains("four")){
			prodTimeObj.setBusinessDays("4");
		}
		else if(ProductionTime.contains("Six")){
			prodTimeObj.setBusinessDays("6");
		}
		else if(ProductionTime.contains("Ten")){
			prodTimeObj.setBusinessDays("10");
		}
		else if(ProductionTime.contains("Fifteen")){
			prodTimeObj.setBusinessDays("15");
		}	
		else{			
			prodTimeObj.setBusinessDays("42");
		}
		prodTimeObj.setDetails(ProductionTime);	
		
		ListOfProductiontime.add(prodTimeObj);	
		return ListOfProductiontime;
	}

	public Size getSize(String sizeValue) {
		sizeValue=sizeValue.replace("[^a-zA-Z%\"\\ ]", "");
		Size sizeObj=new Size();
		Dimension dimObj=new Dimension();
		List<Values> ListOfValues = new ArrayList<Values>();
		Values valuesObj=new Values();
		List<Value> ListOfValue = new ArrayList<Value>();
		Value valueObj=new Value();
		
		String SizeArr[]=sizeValue.split("x");
		for(int i=0;i<SizeArr.length;i++){
			valueObj=new Value();
			if(i==1){
			valueObj.setAttribute("Width");
			}else
			{
			valueObj.setAttribute("Height");	
			}
			valueObj.setValue(SizeArr[i]);
			valueObj.setUnit("in");	
			ListOfValue.add(valueObj);
		}
	
	
		valuesObj.setType("Dimension");
		valuesObj.setValue(ListOfValue);
		ListOfValues.add(valuesObj);
		dimObj.setValues(ListOfValues);
		sizeObj.setDimension(dimObj);

		return sizeObj;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
