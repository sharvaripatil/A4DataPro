package parser.bellaCanvas;
import java.util.ArrayList;
import java.util.List;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.Apparel;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;

public class BellaCanvasProductAttributeParser {
	private LookupServiceData lookupServiceDataObj;
	
	public List<Color> getColorCriteria(StringBuilder colorMapping) {
		
		String ColorValue=colorMapping.toString();
		List<Color> listOfProductColors = new ArrayList<>();
		List<Combo> combolist = new ArrayList<>();
		Color colorObj = new Color();
		Combo comboObj=new Combo();
		
		String[] colorValues =ColorValue.split(",");
		
		for (String colorName : colorValues) {
			
			 colorObj = new Color();
			 comboObj=new Combo();
			String OriginalcolorName = colorName.trim();
			String colorLookUpName=BellaCanvasLookupData.COLOR_MAP.get(colorName.trim()).trim();
			if(colorName.contains("/"))
			{
				String comboArr[]=colorLookUpName.split("/");
				colorObj.setAlias(OriginalcolorName);
				colorObj.setName(comboArr[0]);		
				comboObj.setType("secondary");
				comboObj.setName(comboArr[1]);
				combolist.add(comboObj);
				colorObj.setCombos(combolist);
				
			}else
			{
			colorObj.setAlias(OriginalcolorName);
			colorObj.setName(colorLookUpName);	
			}
			listOfProductColors.add(colorObj);
		}	
		return listOfProductColors;
	}
	
	
	
	
	
	public Size getSize(String sizevalue) {
	
		Size sizeObj=new Size();
		Value ValueObj = new Value();
		Apparel appObj=new Apparel();
		String sizeLookUpName=BellaCanvasLookupData.SIZE_MAP.get(sizevalue.trim()).trim();
		List<Value> listOfValue = new ArrayList<>();

		String sizeArr[]=sizeLookUpName.split(",");
		
		//sizeObj.setApparel(apparel);
		
		for (String value : sizeArr) {
	 		 ValueObj = new Value();
	 	     sizeObj.setApparel(appObj);
	 		 appObj.setType("Standard & Numbered");
	 		ValueObj.setValue(value);
	 		listOfValue.add(ValueObj);
	 		}
	 		 appObj.setValues(listOfValue);
	 	     sizeObj.setApparel(appObj);		
		return sizeObj;
	}

	
	
	
	
	
	
	
	
	
	
	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}


	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}























	
	
	
}




