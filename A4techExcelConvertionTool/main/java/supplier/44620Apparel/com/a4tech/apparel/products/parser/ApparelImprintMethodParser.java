package com.a4tech.apparel.products.parser;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.ImprintMethod;

public class ApparelImprintMethodParser {

	public List<ImprintMethod> getImprintMethodValues(String imprintMethodValue) {
		List<ImprintMethod> imprintMethodsList = new ArrayList<ImprintMethod>();

		ImprintMethod imprMethod = null;

		if (imprintMethodValue.contains(",")) {
			String imprintMethodArr[] = imprintMethodValue.split(",");

			for (String value : imprintMethodArr) {
				imprMethod = new ImprintMethod();
				imprMethod.setAlias(value);
				imprMethod.setType("OTHER");
				imprintMethodsList.add(imprMethod);
			}
		} else if (imprintMethodValue.contains("&")) {
			String imprintMethodArr[] = imprintMethodValue.split("&");

			for (String value : imprintMethodArr) {
				imprMethod = new ImprintMethod();
				imprMethod.setAlias(value);
				imprMethod.setType("OTHER");
				imprintMethodsList.add(imprMethod);
			}
		} else if (imprintMethodValue.contains("and")) {
			String imprintMethodArr[] = imprintMethodValue.split("and");

			for (String value : imprintMethodArr) {
				imprMethod = new ImprintMethod();
				imprMethod.setAlias(value);
				imprMethod.setType("OTHER");
				imprintMethodsList.add(imprMethod);
			}
		} else if (imprintMethodValue.contains("or"))

		{
			String imprintMethodArr[] = imprintMethodValue.split("or");

			for (String value : imprintMethodArr) {
				imprMethod = new ImprintMethod();
				imprMethod.setAlias(value);
				imprMethod.setType("OTHER");
				imprintMethodsList.add(imprMethod);
			}
		} else {

			imprMethod = new ImprintMethod();
			imprMethod.setAlias(imprintMethodValue);
			imprMethod.setType("OTHER");
			imprintMethodsList.add(imprMethod);

		}

		return imprintMethodsList;
	}

}
