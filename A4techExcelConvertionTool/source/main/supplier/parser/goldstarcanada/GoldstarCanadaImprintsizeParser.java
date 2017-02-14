package parser.goldstarcanada;

import java.util.ArrayList;
import java.util.List;
import com.a4tech.product.model.ImprintSize;

public class GoldstarCanadaImprintsizeParser {

	public List<ImprintSize> getimprintsize(StringBuilder firstImprintSize) {
		
	    List<ImprintSize> imprintSizeList =new ArrayList<ImprintSize>();
	    ImprintSize impsizeobj=new ImprintSize();
	    
		String ImprintsizeArr[]=firstImprintSize.toString().split(",");
		
		
	   for (String Value : ImprintsizeArr) {
		   impsizeobj=new ImprintSize();
		   impsizeobj.setValue(Value);
		   imprintSizeList.add(impsizeobj);
	      }
		
	     
		
		
		
		
		return imprintSizeList;
	}
	
	
	

}
