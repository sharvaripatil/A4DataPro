package parser.alfa;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class AlfaAttributeParser {

	public Product getExistingProductData(Product existingProduct){
		Product newProduct = new Product();
		ProductConfigurations existingConfig = existingProduct.getProductConfigurations();
		ProductConfigurations newConfig = new ProductConfigurations();
		if(!CollectionUtils.isEmpty(existingProduct.getImages())){
			newProduct.setImages(existingProduct.getImages());
		}
		if(existingProduct.getInventory() != null){
			newProduct.getInventory().setInventoryLink(existingProduct.getInventory().getInventoryLink());
		}
		if(!CollectionUtils.isEmpty(existingConfig.getColors())){
			newConfig.setColors(existingConfig.getColors());
		}
		if(!CollectionUtils.isEmpty(existingProduct.getFobPoints())){
			newProduct.setFobPoints(existingProduct.getFobPoints());
		}
		if(!CollectionUtils.isEmpty(existingProduct.getLineNames())){
			newProduct.setLineNames(existingProduct.getLineNames());
		}
		if(!CollectionUtils.isEmpty(existingProduct.getProductKeywords())){
			newProduct.setProductKeywords(existingProduct.getProductKeywords());
		}
		if(!CollectionUtils.isEmpty(existingProduct.getCategories())){
			newProduct.setCategories(existingProduct.getCategories());
		}
		if(!CollectionUtils.isEmpty(existingConfig.getImprintMethods())){
			newConfig.setImprintMethods(existingConfig.getImprintMethods());
		}
		newProduct.setProductConfigurations(newConfig); 
		return newProduct;
	}
	public List<PriceGrid> getPrices(String priceVal){
		
		
		return null;
	}
	
	public Product getUniqueCoulmnValuesData(List<String> listOfValues,Product product){
		ProductConfigurations prdConfiguration = product.getProductConfigurations();
		List<PriceGrid> priceGrid = product.getPriceGrids();
		for (String value : listOfValues) {
			if(value.contains("Available Colors")){
				value = value.substring(value.indexOf("|") + 1);
				List<Color> colorsList = getProductColor(value);
			} else if(value.contains("Product Material")){
				value = value.substring(value.indexOf("|") + 1);
				List<Material> materialList = getProductMaterial(value);
			} else if(value.contains("Product Dimensions")){
				value = value.substring(value.indexOf("|") + 1);
			}
		}
		return product;
	}
	private List<Color> getProductColor(String color){
		List<Color> listOfColor = new ArrayList<>();
		Color colorObj = null;
		String[] colors = CommonUtility.getValuesOfArray(color, ApplicationConstants.CONST_STRING_COMMA_SEP);
		for (String colorValue : colors) {
			   colorObj = new Color();
			   if(!StringUtils.isEmpty(colorValue)){
				   String colorGroup = AlfaColorMapping.getColorGroup(colorValue);
				   if(colorGroup == null){
					   colorGroup = "Other";
				   }
				   colorObj.setName(colorGroup);
				   colorObj.setAlias(colorValue);
				   listOfColor.add(colorObj);
			   }
		}
		return listOfColor;
	}
	
	private List<Material> getProductMaterial(String mtrlVal){
		mtrlVal = mtrlVal.replaceAll(";", ",");
		String[] mtrlVals = CommonUtility.getValuesOfArray(mtrlVal, ",");
		List<Material> materialList = new ArrayList<>();
		Material materialObj = null;
		for (String materialName : mtrlVals) {
			materialObj = new Material();
			if(materialName.contains("/")){
				String[] materials = CommonUtility.getValuesOfArray(materialName, "/");
				Combo comboObj = new Combo();
				materialObj.setName(materials[0]);
				comboObj.setName(materials[1]);
				materialObj.setCombo(comboObj);
				materialName = materialName.replaceAll("/", "-");
				materialObj.setAlias(materialName);
			} else{
				materialObj.setName(materialName);
				materialObj.setAlias(materialName);
			}
			materialList.add(materialObj);
		}
		return materialList;
	}
	private Size  getProductSize(String sizeVal){
		sizeVal = sizeVal.replaceAll(";", ",");
		String[] sizeVals = CommonUtility.getValuesOfArray(sizeVal, ",");
		Size sizeOb = new Size();
		Dimension dimensionObj = new Dimension();
		List<Values> listOfValues = new ArrayList<>();
		Values valuesObj = new Values();
		List<Value> listOfValue = new ArrayList<>();
		Value valueObj = null;
		for (String sizeValue : sizeVals) {
			valueObj = new Value();
			sizeValue = sizeValue.replaceAll("\"", "").trim();
			if(sizeValue.equals("N/A")){
				continue;
			}else if(sizeValue.contains("H") && (sizeValue.contains("Dia") || sizeValue.contains("dia"))){
				
			} else if(sizeValue.contains("W") && sizeValue.contains("H") && sizeValue.contains("D")){
				
			} else if (sizeValue.contains("H") && sizeValue.contains("Base")
					&& (sizeValue.contains("R") || sizeValue.contains("Rim"))) {
				
			}else if(sizeValue.contains("H") && sizeValue.contains("R")){
				
			}
		}
		return null;
	}
}
