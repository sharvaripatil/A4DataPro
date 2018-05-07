package parser.goldstarcanada;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.util.ApplicationConstants;

public class GoldstarCanadaRushTimeParser {
	
	public RushTime getRushTimeValues(String rushProdTimeLo, String rushProdTimeH) {
		RushTimeValue RushTimeValue = new RushTimeValue();
		List<RushTimeValue> rushTimeList = new ArrayList<RushTimeValue>();
		RushTime rushtimeObj=new RushTime();
		String FinalrushTime="";
		rushtimeObj.setAvailable(ApplicationConstants.CONST_BOOLEAN_TRUE);
		if(rushProdTimeLo.equalsIgnoreCase(rushProdTimeH)) {
			
			FinalrushTime=rushProdTimeLo;
			
		}else {
		 FinalrushTime=rushProdTimeLo.concat("-").concat(rushProdTimeH);

		}
		RushTimeValue.setBusinessDays(FinalrushTime);
		RushTimeValue.setDetails(ApplicationConstants.CONST_STRING_EMPTY);
		rushTimeList.add(RushTimeValue);
		rushtimeObj.setRushTimeValues(rushTimeList);	
		
		return rushtimeObj;
	}
	

}
