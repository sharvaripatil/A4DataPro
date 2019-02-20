package parser.klproduct;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.util.ApplicationConstants;

public class KlSizeParser {
	
	public Size getSizes(String sizeValues){
		
		Size size = new Size();
		Apparel apparel = new Apparel();
		List<Value> listOfSizeValues = getSizeValues(sizeValues);
		apparel.setType("Standard & Numbered");
		apparel.setValues(listOfSizeValues);
		size.setApparel(apparel);
		return size;
		
	}
	
	public List<Value> getSizeValues(String sizes){
		List<Value> listOfSizeValues = new ArrayList<Value>();
		Value valueObj = null;
		String[] values = sizes.split(ApplicationConstants.CONST_DELIMITER_COMMA);
		for (String string : values) {
			valueObj = new Value();
			valueObj.setValue(string);
			listOfSizeValues.add(valueObj);
		}
		return listOfSizeValues;
	}

}
