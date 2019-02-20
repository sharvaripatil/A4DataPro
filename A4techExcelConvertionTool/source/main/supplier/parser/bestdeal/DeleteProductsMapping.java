package parser.bestdeal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class DeleteProductsMapping {
	
	private static final Logger _LOGGER = Logger.getLogger(DeleteProductsMapping.class);

	public List<String> getAllXids(Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
		  List<String> xidsList = new ArrayList<>();
 		try{
	    Sheet sheet = workbook.getSheetAt(ApplicationConstants.CONST_NUMBER_ZERO);
		Iterator<Row> iterator = sheet.iterator();
		String xid = null;
		
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if(nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO){
				continue;
			}
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				
				int columnIndex = cell.getColumnIndex();
				if(columnIndex  ==  ApplicationConstants.CONST_NUMBER_ZERO){
					Cell xidCell = nextRow.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
				     xid = CommonUtility.getCellValueStrinOrInt(xidCell);
					//xid = CommonUtility.getCellValueStrinOrInt(cell);
				}
				switch (columnIndex+ApplicationConstants.CONST_INT_VALUE_ONE) {
				case 1://xid
					  xidsList.add(xid);
					 break;
			
			}  // end inner while loop
					 
		}
				
			}catch(Exception e){
		}
		}
		workbook.close();
	       return xidsList;
		}catch(Exception e){
			_LOGGER.error("Error while Processing excel sheet " +e.getMessage());
			return xidsList;
		}finally{
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet" +e.getMessage());
	
			}
				_LOGGER.info("Complted processing of excel sheet ");
				
		}
		
	}

	


}
