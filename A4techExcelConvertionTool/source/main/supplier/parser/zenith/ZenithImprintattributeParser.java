package parser.zenith;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.AdditionalColor;

public class ZenithImprintattributeParser {

	public static List<AdditionalColor> getaddColor(String addColor) {

		List<AdditionalColor> listOfAddColor= new ArrayList<>();
		AdditionalColor colObj=new AdditionalColor();

		colObj.setName("max color:" +addColor);
		listOfAddColor.add(colObj);
		
		return listOfAddColor;
	}

}
