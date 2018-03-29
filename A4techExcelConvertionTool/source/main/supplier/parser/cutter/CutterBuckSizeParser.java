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
			
			if(!valuelist.contains(value)){
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