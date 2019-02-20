package parser.klproduct;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.a4tech.product.model.Color;
import com.a4tech.product.model.Material;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class KlColorAndMaterialParser {
	
	public List<Color> getColors(String productColor){
		List<Color> listOfProductColor = null;
		Color colorObj = null;
		if(!StringUtils.isEmpty(productColor)){
			listOfProductColor = new ArrayList<Color>();
			String[] colors = productColor.split(ApplicationConstants.CONST_DELIMITER_COMMA);
			for (String color : colors) {
				if(CommonUtility.isEmptyOrNull(color)){
					colorObj = new Color();
					colorObj.setName(color);
					colorObj.setAlias(color);
					listOfProductColor.add(colorObj);
				}	
			}
			
		}
		return listOfProductColor;
	}
	
	
	public List<Material> getMaterials(String productMaterial){
		List<Material> listOfProductMaterial = null;
		Material materialObj = null;
		if(!StringUtils.isEmpty(productMaterial)){
			listOfProductMaterial = new ArrayList<Material>();
			materialObj = new Material();
			materialObj.setName(productMaterial);
			materialObj.setAlias(productMaterial);
			listOfProductMaterial.add(materialObj);
		}
		return listOfProductMaterial;
	}


}
