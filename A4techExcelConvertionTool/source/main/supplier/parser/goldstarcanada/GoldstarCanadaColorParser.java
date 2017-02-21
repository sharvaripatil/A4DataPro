package parser.goldstarcanada;

import java.util.ArrayList;
import java.util.List;


import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.util.ApplicationConstants;

public class GoldstarCanadaColorParser {

	public List<Color> getColorCriteria(String colorValue) {
		List<Color> colorlist = new ArrayList<Color>();
		//Set<Color> colorslist = new HashSet<Color>();
 		String colorArr[] = colorValue
				.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		Color colorObj = null;
		for (String outervalue : colorArr) {
		
			colorObj =  new Color();
			if (outervalue.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
				

				String colorArr1[] = outervalue
						.split(ApplicationConstants.CONST_DELIMITER_FSLASH);
				colorArr1[0]=colorArr1[0].trim();
				colorArr1[1]=colorArr1[1].trim();

				List<Combo> combolist = new ArrayList<Combo>();
			for (int i=0;i<1;i++) {
			    	colorObj =  new Color();
					Combo combovalue = new Combo();
					combovalue.setName(GoldstartCanadaApplicationConstatnt.COLOR_MAP.get(colorArr1[1]).trim());
					combovalue.setType("trim");
					combolist.add(combovalue);
				
				colorObj.setCombos(combolist);
				colorObj.setName(GoldstartCanadaApplicationConstatnt.COLOR_MAP.get(colorArr1[0]).trim());
				colorObj.setAlias(outervalue);
				//colorlist.add(colorObj);
				//colorlist.add(colorObj);
			}
			} else {
				outervalue=outervalue.trim();
				colorObj.setName(GoldstartCanadaApplicationConstatnt.COLOR_MAP.get(outervalue));
				colorObj.setAlias(outervalue);
				//colorlist.add(colorObj);
				
			}
			colorlist.add(colorObj);
		}
     //   colorlist.addAll(colorslist);
		return colorlist;

	}
}
