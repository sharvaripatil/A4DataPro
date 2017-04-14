package parser.proGolf;

import java.util.HashMap;
import java.util.Map;

public class ProGolfHeaderMapping {
	public static final Map<String,Integer> productInformationHeaderMapping =new HashMap<>();
	static{
		
		productInformationHeaderMapping.put("product_id", 1);
		productInformationHeaderMapping.put("SKU", 2);
		productInformationHeaderMapping.put("Country", 3);
		productInformationHeaderMapping.put("Language", 4);
		productInformationHeaderMapping.put("Currency", 5);
		productInformationHeaderMapping.put("Product_Name", 6);
		productInformationHeaderMapping.put("Description", 7);
		productInformationHeaderMapping.put("Linename", 8);
		productInformationHeaderMapping.put("Categories", 9);
		productInformationHeaderMapping.put("Search_Keyword", 10);
		productInformationHeaderMapping.put("Default_Image", 11);
		productInformationHeaderMapping.put("Default_Image_Color_Code", 12);
		productInformationHeaderMapping.put("Default_Color", 13);
		productInformationHeaderMapping.put("ATTR_Colors", 14);
		productInformationHeaderMapping.put("ATTR_Size", 15);
		productInformationHeaderMapping.put("ATTR_Imprint_Color", 16);
		productInformationHeaderMapping.put("ATTR_imprint_Size", 17);
		productInformationHeaderMapping.put("ATTR_Style", 18);
		productInformationHeaderMapping.put("ATTR_Width", 19);
		productInformationHeaderMapping.put("ATTR_Shape", 20);
		productInformationHeaderMapping.put("Valid_Up_To", 21);
		productInformationHeaderMapping.put("Matrix_Price", 22);
		productInformationHeaderMapping.put("Matrix_Frieght", 23);
		productInformationHeaderMapping.put("vat", 24);
		productInformationHeaderMapping.put("vat_unit", 25);
		productInformationHeaderMapping.put("Packaging_type", 26);
		productInformationHeaderMapping.put("Packaging_Charges", 27);
		productInformationHeaderMapping.put("Packaging_Code", 28);
		productInformationHeaderMapping.put("Video_URL", 29);
		productInformationHeaderMapping.put("Distributor_Central_URL", 30);
		productInformationHeaderMapping.put("Special_Price_Valid_Up_To", 31);
		productInformationHeaderMapping.put("feature_1", 32);
		productInformationHeaderMapping.put("feature_2", 33);
		productInformationHeaderMapping.put("feature_3", 34);
		productInformationHeaderMapping.put("feature_4", 35);
		productInformationHeaderMapping.put("feature_5", 36);
		productInformationHeaderMapping.put("feature_6", 37);
		productInformationHeaderMapping.put("feature_7", 38);
		productInformationHeaderMapping.put("feature_8", 39);
		productInformationHeaderMapping.put("feature_9", 40);
	}
	public static Integer getHeaderIndex(String headerName){
		return productInformationHeaderMapping.get(headerName);
	}
}
