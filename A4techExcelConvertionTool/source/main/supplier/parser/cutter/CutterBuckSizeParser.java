package parser.cutter;
import java.util.ArrayList;
import java.util.List;
import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;


public class CutterBuckSizeParser {
	
	public Size getSizes(String sizeValue) {
		 Size sizeObj = new Size();
		String sizeArr[]=sizeValue.split(",");
		Apparel appObj=new Apparel();
		List<Value> valuelist =  new ArrayList<Value>();
		Value valObj;
	
		
		for (String value : sizeArr) {
			value=value.replaceAll("\n", "").replace("XXXL","3XL").replace("XXL","2XL");

			if(value.length()==4){
			valObj = new Value();	
			appObj.setType("Apparel-Waist/Inseam");
			value=value.trim().substring(0, 2)+"x"+ value.substring(2, value.length()) ;
			 valObj.setValue(value);
			 valuelist.add(valObj);
			}
			else{
		 value=value.replaceAll("\n", "");
		 valObj = new Value();
	     sizeObj.setApparel(appObj);
		 appObj.setType("Standard & Numbered");
		 valObj.setValue(value);
		 valuelist.add(valObj);
			}

		}
		 appObj.setValues(valuelist);
	     sizeObj.setApparel(appObj);
			
		
		return sizeObj;
	}
}