package parser.milestone;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.util.ApplicationConstants;

public class MilestoneColorParser {

	public List<Color> getColorCriteria(String colorValue) {
		List<Color> colorlist = new ArrayList<Color>();
	//	Set<Color> colorslist = new HashSet<Color>();
//		String colorArr[] = colorValue
//				.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		Color colorObj = new Color() ;
		Combo combovalue = new Combo();
		
			if (colorValue.contains("/")) {
				String colorArr1[] = colorValue
						.split("/");
			//	for (String value : colorArr1) {
				colorObj = new Color();
				List<Combo> combolist = new ArrayList<Combo>();
				if(colorArr1.length==2){
				    combovalue = new Combo();
					combovalue.setName(MilestoneLookupData.COLOR_MAP.get(colorArr1[1].trim()));
					combovalue.setType("trim");
					combolist.add(combovalue);
				}
				
				else{
					for (int i=1;i<3;i++) {
					 combovalue = new Combo();
					 if(i==1){
					 combovalue.setName(MilestoneLookupData.COLOR_MAP.get(colorArr1[1].trim()));
					 combovalue.setType("secondary");
					 }else if(i==2)
					 {
					 combovalue.setName(MilestoneLookupData.COLOR_MAP.get(colorArr1[2].trim()));
					 combovalue.setType("trim");
				   	 
					 }
					 combolist.add(combovalue);
					}	
				}
				colorObj.setCombos(combolist);
				colorObj.setName(MilestoneLookupData.COLOR_MAP.get(colorArr1[0].trim()));
				colorObj.setAlias(colorValue);
				//colorlist.add(colorObj);
				colorlist.add(colorObj);
			
			}else {
				colorObj = new Color();
				colorObj.setName(colorValue);
				colorObj.setAlias(colorValue);
				colorlist.add(colorObj);
			}
			return colorlist;

		}


      

	}

