package parser.goldstarcanada;

import java.util.ArrayList;
import java.util.List;
import com.a4tech.product.model.ImprintSize;

public class GoldstarCanadaImprintsizeParser {

	public List<ImprintSize> getimprintsize(StringBuilder firstImprintSize,String imprintlocation) {
	    List<ImprintSize> imprintSizeList =new ArrayList<ImprintSize>();
	    ImprintSize impsizeobj;
		if(imprintlocation.contains("Imprint")){
		
		String imprintsize1=imprintlocation.substring(8, 18);
		   impsizeobj=new ImprintSize();
		   impsizeobj.setValue(imprintsize1);
		   imprintSizeList.add(impsizeobj);

		}
		
		String ImprintSizeValue=firstImprintSize.toString().replace("null x null","");
		ImprintSizeValue=ImprintSizeValue.replace("null", "");	    
		String ImprintsizeArr[]=ImprintSizeValue.split(",");
		
		
	   for (String Value : ImprintsizeArr) {
		   impsizeobj=new ImprintSize();
		   impsizeobj.setValue(Value);
		   imprintSizeList.add(impsizeobj);
	      }		
		
		
		
		return imprintSizeList;
	}
	
	
	

}
