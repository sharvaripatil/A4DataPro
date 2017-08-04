package parser.towelSpecialties;

import java.util.HashMap;
import java.util.Map;

public class TowelSpecQtyAndColorMapping {
	public static Map<Integer, String> priceQuantityMap = new HashMap<>();
	static{
		priceQuantityMap.put(5, "1");
		priceQuantityMap.put(6, "50");
		priceQuantityMap.put(7, "36");
		priceQuantityMap.put(8, "72");
		priceQuantityMap.put(9, "100");
		priceQuantityMap.put(10, "144");
		priceQuantityMap.put(11, "288");
		priceQuantityMap.put(12, "600");
		priceQuantityMap.put(13, "800");
		priceQuantityMap.put(14, "1000");
		priceQuantityMap.put(15, "1500");
		priceQuantityMap.put(16, "3000");
		priceQuantityMap.put(17, "4800");
		priceQuantityMap.put(18, "5000");
		priceQuantityMap.put(19, "8000");
		priceQuantityMap.put(20, "20000");

	}
	public static String getPriceQty(int priceIndex){
		return priceQuantityMap.get(priceIndex);
	}

}
