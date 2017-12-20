package parser.sunscope;

import java.util.HashMap;
import java.util.Map;

public class SunScopeQuantityMapping {
	public static Map<Integer, String> priceQuantityMap = new HashMap<>();
	static{
		priceQuantityMap.put(20, "25");// Price A
		priceQuantityMap.put(21, "50");
		priceQuantityMap.put(22, "100");
		priceQuantityMap.put(23, "250");
		priceQuantityMap.put(24, "500");
		priceQuantityMap.put(25, "1000");
		priceQuantityMap.put(26, "2500");
		priceQuantityMap.put(27, "5000");
		priceQuantityMap.put(33, "25"); // Price B
		priceQuantityMap.put(34, "50");
		priceQuantityMap.put(35, "100");
		priceQuantityMap.put(36, "250");
		priceQuantityMap.put(37, "500");
		priceQuantityMap.put(38, "1000");
		priceQuantityMap.put(39, "2500");
		priceQuantityMap.put(40, "5000");
		priceQuantityMap.put(46, "25"); // Price C
		priceQuantityMap.put(47, "50");
		priceQuantityMap.put(48, "100");
		priceQuantityMap.put(49, "250");
		priceQuantityMap.put(50, "500");
		priceQuantityMap.put(51, "1000");
		priceQuantityMap.put(52, "2500");
		priceQuantityMap.put(53, "5000");
		priceQuantityMap.put(59, "25"); //price D
		priceQuantityMap.put(60, "50");
		priceQuantityMap.put(61, "100");
		priceQuantityMap.put(62, "250");
		priceQuantityMap.put(63, "500");
		priceQuantityMap.put(64, "1000");
		priceQuantityMap.put(65, "2500");
		priceQuantityMap.put(66, "5000");

	}
	public static String getPriceQty(int priceIndex){
		return priceQuantityMap.get(priceIndex);
	}

}
