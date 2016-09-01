package com.a4tech.product.ESPTemplate.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.ImprintMethod;
import com.a4tech.util.ApplicationConstants;

public class ProductImprintMethodParser {

	private Logger _LOGGER = Logger.getLogger(getClass());

	public List<ImprintMethod> getImprintCriteria(String sold_as_Blanks,
			String four_color_process, String imprintValue) {

		List<ImprintMethod> impmthdList = new ArrayList<ImprintMethod>();
		ImprintMethod imprintObj = new ImprintMethod();
		try {

			if (sold_as_Blanks
					.equalsIgnoreCase(ApplicationConstants.CONST_CHAR_Y)) {
				imprintObj = new ImprintMethod();

				imprintObj
						.setAlias(ApplicationConstants.CONST_STRING_UNIMPRINTED);
				imprintObj
						.setType(ApplicationConstants.CONST_STRING_UNIMPRINTED);
				impmthdList.add(imprintObj);

			}
			if (four_color_process
					.equalsIgnoreCase(ApplicationConstants.CONST_CHAR_Y)) {
				imprintObj = new ImprintMethod();

				imprintObj
						.setAlias(ApplicationConstants.CONST_STRING_FULLCOLOR);
				imprintObj
						.setType(ApplicationConstants.CONST_STRING_FULLCOLOR);
				impmthdList.add(imprintObj);

			}
			if (imprintValue
					.contains(ApplicationConstants.CONST_DELIMITER_FSLASH)) {
				String impValue = imprintValue;
				String imprintArr[] = impValue
						.split(ApplicationConstants.CONST_DELIMITER_FSLASH);

				for (String tempImpint : imprintArr) {
					imprintObj = new ImprintMethod();
					imprintObj.setType(tempImpint);
					imprintObj.setAlias(tempImpint);
					impmthdList.add(imprintObj);
				}
			} else {
				imprintObj = new ImprintMethod();
				imprintObj.setType(imprintValue);
				imprintObj.setAlias(imprintValue);
				impmthdList.add(imprintObj);

			}

		} catch (Exception e) {
			_LOGGER.error("Error while processing Imprint Method :"
					+ e.getMessage());
			return null;

		}
		return impmthdList;

	}

}
