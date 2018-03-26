package parser.cb;
import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Configurations;
import com.a4tech.product.model.Inventory;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.ProductSKUConfiguration;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;

public class CBAttributeParser {

	public List<Color> getColorValue(List<String> colorsList) {
		
		List<Color> colorList = new ArrayList<Color>();
		
		Color colorObj=new Color();
	    String colorArr[]=colorsList.toString().replace("[", "").replace("]", "").split(",");
		Combo combovalue = new Combo();

		
	    for (String colorsValue : colorArr) {
	    	colorObj=new Color();	
	    	
	    	if(colorsValue.contains("/"))
	    	{
	    	combovalue = new Combo();
	    	List<Combo> combolist = new ArrayList<Combo>();
	    	String ComboColorArr[]=	colorsValue.split("/");
	    	colorObj.setAlias(colorsValue.trim());
	    	colorObj.setName(CBlookup.COLOR_MAP.get(ComboColorArr[0].trim()));
	    	combovalue.setName(CBlookup.COLOR_MAP.get(ComboColorArr[1].trim()));
	    	combolist.add(combovalue);
	    	colorObj.setCombos(combolist);
	    	colorList.add(colorObj);
	    	}else
	    	{
	    		colorObj.setAlias(colorsValue);
	    		if(CBlookup.COLOR_MAP.containsKey(colorsValue)){
	    		colorObj.setName(CBlookup.COLOR_MAP.get(colorsValue.trim()));
	    		}else
	    		{
	    			colorObj.setName("Other");
	    		}
	    		colorList.add(colorObj);
	    	}
		}
		return colorList;
	}

	public List<ProductNumber> getProductNO(List<String> productNOList, List<String> colorsList) {
		List<ProductNumber> listOfProductNo = new ArrayList<>();
		List<Configurations> listOfConf= new ArrayList<>();
		List<Object> listOfValue= new ArrayList<>();

		ProductNumber ProdNoObj=new ProductNumber();
		Configurations confObj=new Configurations(); 
		
	    String prodNoArr[]=productNOList.toString().replace("[", "").replace("]", "").split(",");

	    String colorArr[]=colorsList.toString().replace("[", "").replace("]", "").split(",");

	    for(int i=0;i<prodNoArr.length;i++)
	    {
	    	listOfConf= new ArrayList<>();
			ProdNoObj=new ProductNumber();
	    	listOfValue= new ArrayList<>();
			confObj=new Configurations(); 
			
	   		listOfValue.add(colorArr[i]);
			confObj.setCriteria("Product Color");
			confObj.setValue(listOfValue);
			listOfConf.add(confObj);
			
			ProdNoObj.setProductNumber(prodNoArr[i]);
			ProdNoObj.setConfigurations(listOfConf);

			listOfProductNo.add(ProdNoObj);
	    }

		return listOfProductNo;
	}

	public List<ProductSkus> getSKU(List<String> sKUList,
			List<String> productNOList, List<String> colorsList) {

		ProductSkus productSku = new ProductSkus();
		List<ProductSkus> listProductSKU = new ArrayList<>();
		List<ProductSKUConfiguration> listSkuConfigs = new ArrayList<>();
		List<Object> listOfValue= new ArrayList<>();
		ProductSKUConfiguration confgObjColor = new ProductSKUConfiguration();
		
	    String skuArr[]=sKUList.toString().replace("[", "").replace("]", "").split(",");
	    String prodNoArr[]=productNOList.toString().replace("[", "").replace("]", "").split(",");
	    String colorArr[]=colorsList.toString().replace("[", "").replace("]", "").split(",");

	    for(int i=0;i<skuArr.length;i++){
	    	Inventory inventory = getInventory();
	    	listOfValue= new ArrayList<>();

	    	productSku.setSKU(skuArr[i]);
	    	productSku.setHazmat("UNSPECIFIED");
	    	
	    	confgObjColor.setCriteria("Product Color");
	    	listOfValue.add(colorArr[i]);
	    	confgObjColor.setValue(listOfValue);
	    
	    	listSkuConfigs.add(confgObjColor);
	    	
	    	productSku.setConfigurations(listSkuConfigs);
			productSku.setInventory(inventory);
			listProductSKU.add(productSku);

      	}
		
		
		return listProductSKU;
	}
	
	
	private Inventory getInventory(){
		Inventory inventory = new Inventory();
		inventory.setInventoryLink("");
		inventory.setInventoryQuantity("");
		inventory.setInventoryStatus("");
		return inventory;
	}
}
