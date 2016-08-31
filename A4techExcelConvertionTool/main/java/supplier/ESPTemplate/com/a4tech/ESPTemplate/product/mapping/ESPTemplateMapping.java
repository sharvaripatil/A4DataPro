package com.a4tech.ESPTemplate.product.mapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;

import com.a4tech.product.ESPTemplate.parser.ColorParser;
import com.a4tech.product.ESPTemplate.parser.OriginParser;
import com.a4tech.product.ESPTemplate.parser.PriceGridParser;
import com.a4tech.product.ESPTemplate.parser.ProductImprintMethodParser;
import com.a4tech.product.ESPTemplate.parser.ProductMaterialParser;
import com.a4tech.product.ESPTemplate.parser.ShippingEstimationParser;
import com.a4tech.product.ESPTemplate.parser.SizeParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.Personalization;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.Size;
import com.a4tech.product.ESPTemplate.parser.RushTimeParser;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;


public class ESPTemplateMapping {
	private static final Logger _LOGGER = Logger.getLogger(ESPTemplateMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private OriginParser originParser;
	private RushTimeParser    rushTimeParser;
	private ProductImprintMethodParser productImprintMethodParser;
	private ShippingEstimationParser shippingEstimationParser;
	private ColorParser colorParserObj ;
	private ProductMaterialParser ProductMaterialParserObj;
	private PriceGridParser PriceGridParserObj;
	private SizeParser sizeParserObj;
	


	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
		
		
		Set<String>  productXids = new HashSet<String>();
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();

	
		
		    String PriceIncludes=null;
            String imprintValue=null;
            String ImprintchargesString =null;
            int rushTime = 0;
			String productId = null;
			String Sold_as_Blanks=null;
			String Four_color_process=null;
			String productName = null;
			String finalResult = null;
			int Qty1 = 0;
			int Qty2 = 0;
			int Qty3 = 0;
			int Qty4 = 0;
			int Qty5 = 0;
			int Qty6 = 0;
			int Qty7 = 0;
			int Qty8 = 0;
			double price1 = 0;
			double price2 = 0;
			double price3 = 0;
			double price4 = 0;
			double price5 = 0;
			double price6 = 0;
			double price7 = 0;
			double price8 = 0;
			String code1 = null;
			String code2 = null;
			String code3 = null;
			String code4 = null;
			String code5 = null;
			String code6 = null;
			String code7 = null;
			String code8 = null;
			
			StringBuilder listOfQuantity = new StringBuilder();
			StringBuilder listOfPrices = new StringBuilder();
			StringBuilder listOfDiscount = new StringBuilder();
		  
		
		
		  Product productExcelObj = new Product();   
		  FOBPoint fobPointObj=new FOBPoint();
		  ProductConfigurations productConfigObj=new ProductConfigurations();
          RushTime RushTimeObj=new RushTime();
          ShippingEstimationParser shipinestmt = new ShippingEstimationParser();
          ImprintSize imprintSizeObj = new ImprintSize();
          ShippingEstimate ShipingItem = new ShippingEstimate();
          SizeParser sizeParser=new SizeParser();
          Size sizeObj=new Size();
          
       	
          
		try{
			 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
		 
			if (nextRow.getRowNum() ==0)
				continue;
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if(productId != null){
				productXids.add(productId);
			}
			 boolean checkXid  = false;
			
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				String xid = null;
				int columnIndex = cell.getColumnIndex();
				
				if(columnIndex + 1 == 1){
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						xid = cell.getStringCellValue();
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						xid = String.valueOf((int)cell.getNumericCellValue());
					}else{
						
					}
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							 System.out.println("Java object converted to JSON String, written to file");
							 	productExcelObj.setPriceGrids(priceGrids);
							 	productExcelObj.setProductConfigurations(productConfigObj);
							 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId);
							 	if(num ==1){
							 		numOfProductsSuccess.add("1");
							 	}else if(num == 0){
							 		numOfProductsFailure.add("0");
							 	}else{
							 		
							 	}
							 	_LOGGER.info("list size>>>>>>>"+numOfProductsSuccess.size());
							 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
							 	
								priceGrids = new ArrayList<PriceGrid>();
								productConfigObj = new ProductConfigurations();
								
								
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    }
							productExcelObj = new Product();
					 }
				}
				
				switch (columnIndex + 1) {

				case 1://ExternalProductId
					
					    if(cell.getCellType() == Cell.CELL_TYPE_STRING){ 
					    	productId = String.valueOf(cell.getStringCellValue().trim());
					    }else if
					     (cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
					    	productId = String.valueOf((int)cell.getNumericCellValue());
					     }
					     productExcelObj.setExternalProductId(productId);	
			   
					 break;
				case 2://Product Name 
				    productName = cell.getStringCellValue();
					if(!StringUtils.isEmpty(productName)){
					productExcelObj.setName(cell.getStringCellValue());
					}else{
						productExcelObj.setName(ApplicationConstants.CONST_STRING_EMPTY);
					}
						
					  break;
				case 3://Summary 
					String summary = cell.getStringCellValue();
					if(!StringUtils.isEmpty(summary)){
					productExcelObj.setSummary(summary);
					}else{
						productExcelObj.setSummary(ApplicationConstants.CONST_STRING_EMPTY);
					}
					
     					break;
				case 4://key words
					String productKeyword = cell.getStringCellValue();
					List<String> productKeywords = new ArrayList<String>();
					if(productKeyword.contains(ApplicationConstants.CONST_DELIMITER_FSLASH )){
				    productKeyword=productKeyword.replace(ApplicationConstants.CONST_DELIMITER_FSLASH ,ApplicationConstants.CONST_STRING_COMMA_SEP);
					}
					String productKeywordArr[] = productKeyword.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					for (String string : productKeywordArr) 
					{
						productKeywords.add(string);
					}
					productExcelObj.setProductKeywords(productKeywords);
					
				    break;
					
				case 5://Description 
					String description = cell.getStringCellValue();
					if(!StringUtils.isEmpty(description)){
					productExcelObj.setDescription(description);
					}else{
						productExcelObj.setDescription(ApplicationConstants.CONST_STRING_EMPTY);
					}
					break;
					
				case 6: //  Product Colors
					String colorValue=cell.getStringCellValue();
					List<Color> colorList = new ArrayList<Color>();
					if(!StringUtils.isEmpty(colorValue)){
					colorList=colorParserObj.getColorCriteria(colorValue);
					if(colorList!=null){
					productConfigObj.setColors(colorList);
					}
					}
					
					break;
					

				case 7://Shapes
					//Field is blank

					   
						
					break;
					
				case 8: // Sizes
					String sizeValue=cell.getStringCellValue();
					if(!StringUtils.isEmpty(sizeValue)){
					sizeObj=sizeParser.getSizes(sizeValue);
                    productConfigObj.setSizes(sizeObj);
					}
                    
					break;
					
				case 9: //Materials
					String materialValue=cell.getStringCellValue();
					List<Material> materialobj=new ArrayList<Material>();
					if(!StringUtils.isEmpty(materialValue)){
						materialobj=ProductMaterialParserObj.getMaterialCriteria(materialValue);
					productConfigObj.setMaterials(materialobj);
					}
					
					break;	
					
				case 10: //Origin
					
					String origin = cell.getStringCellValue();
					if(!origin.isEmpty()){
						List<Origin> listOfOrigin = originParser.getOriginValues(origin);
						productConfigObj.setOrigins(listOfOrigin);
					} 
					
					break;
					
				case 11:  //Production Time 
					//production Time
				      String prodTimeLo = null;
				      int productionTimeValue;
				      ProductionTime productionTime = new ProductionTime();
				  	 List<ProductionTime> productionTimeList = new ArrayList<ProductionTime>();
				      if(cell.getCellType() == Cell.CELL_TYPE_STRING){
				       prodTimeLo = cell.getStringCellValue();
				      }else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
				    	  productionTimeValue=  (int) cell.getNumericCellValue();
				       prodTimeLo = Integer.toString(productionTimeValue);
				      }
				      if(!StringUtils.isEmpty(prodTimeLo)){
				       prodTimeLo=prodTimeLo.replaceAll("days","");
				      productionTime.setBusinessDays(prodTimeLo);
				      productionTime.setDetails("days");
				      productionTimeList.add(productionTime);
				      productConfigObj.setProductionTime(productionTimeList);
				      }
				      break;
					
					
				
				case 12:  // Rush Time 
				 rushTime  = (int) cell.getNumericCellValue();
			
					break;

				case 13: //Product Options
					//Field is blank
			
					break;
					
				case 14: //FOB Point 
					String FOBPoint =cell.getStringCellValue();
				
					List<FOBPoint> fobPointList = new ArrayList<FOBPoint>();
					if(!StringUtils.isEmpty(FOBPoint)){
					fobPointObj.setName(FOBPoint);
					fobPointList.add(fobPointObj);
					productExcelObj.setFobPoints(fobPointList);
					}
					break;
					
				case 15://Shipping Weight
				
				String shippingWeightValue=null;
				if(cell.getCellType() == Cell.CELL_TYPE_STRING){ 
					shippingWeightValue = String.valueOf(cell.getStringCellValue());
			    }else if
			     (cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
			    	shippingWeightValue = String.valueOf((int)cell.getNumericCellValue());
			     }
			   	ShipingItem = shipinestmt.getShippingEstimates((shippingWeightValue));
				productConfigObj.setShippingEstimates(ShipingItem);
					
					break;
				case 16: //Packaging
					
					String packagingValue=cell.getStringCellValue();
					List<Packaging> packaging = new ArrayList<Packaging>();
					if(!StringUtils.isEmpty(packagingValue)){
					Packaging pack = new Packaging();
					pack.setName(packagingValue);
					packaging.add(pack);
					productConfigObj.setPackaging(packaging);
					}
					
					  break;
				
				case 17: //Sold as Blanks? Y/N
					
					 Sold_as_Blanks =cell.getStringCellValue();
				
			
					break;
				
				 case 18: //Personalization available? Y/N
					 String persnavailable=cell.getStringCellValue();
              		Personalization perObj = new Personalization();
            		List<Personalization> personalizationlist = new ArrayList<Personalization>();
					 if (persnavailable.equalsIgnoreCase("Y"))
					 {
						 perObj.setAlias(ApplicationConstants.CONST_STRING_PERSONALIZATION); 
						 perObj.setType(ApplicationConstants.CONST_STRING_PERSONALIZATION);
						 personalizationlist.add(perObj);
						 productConfigObj.setPersonalization(personalizationlist);
					 }
				
			
					break;
				 case 19:
					 String rushService = cell.getStringCellValue();
						if(!StringUtils.isEmpty(rushService)){
					 RushTimeObj=rushTimeParser.getRushTimeValues(Integer.toString(rushTime), rushService);
					 productConfigObj.setRushTime(RushTimeObj);
						}
					 break;

				case 20:
					//Four color process available? Y/N
					 Four_color_process =cell.getStringCellValue();
					break;
				case 21:
					 imprintValue=cell.getStringCellValue();
					List<ImprintMethod> imprintMethods = new ArrayList<ImprintMethod>();
					if(!StringUtils.isEmpty(imprintValue)){
					imprintMethods=productImprintMethodParser.getImprintCriteria(Sold_as_Blanks,Four_color_process,imprintValue);
					if(imprintMethods!=null){
					productConfigObj.setImprintMethods(imprintMethods);
					}
					}
					break;
				case 22:
					
					//Imprinting Charges
					int Imprintcharges=(int) cell.getNumericCellValue();
					  ImprintchargesString = Integer.toString(Imprintcharges);
					
					
					break;
				case 23: 
					//Imprint Colors
					//Field is blank
					
					
					break;
				case 24:  
					//Imprint Options
					//Field is blank
					break;
				case 25: 
					
					//Imprint size
					String Imprintsize=cell.getStringCellValue();
					List<ImprintSize> imprintSizeList = new ArrayList<ImprintSize>();
					if(!StringUtils.isEmpty(Imprintsize)){
					imprintSizeObj.setValue(Imprintsize);
					imprintSizeList.add(imprintSizeObj);
			    	productConfigObj.setImprintSize(imprintSizeList);
					}
				break;
					
				case 26:
					//AdditionalColor/Location
					//Field is blank
					break;
				case 27: 
		         	//Price Includes:
					
					
					 PriceIncludes=cell.getStringCellValue();
					break;
				case 28:
					Qty1=(int) cell.getNumericCellValue();
					listOfQuantity.append(Qty1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

					break;
				case 29:
					price1=cell.getNumericCellValue();;
					listOfPrices.append(price1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					break;
				case 30:  
					code1=cell.getStringCellValue();
					listOfDiscount.append(code1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					break;
				case 31:
					Qty2=(int) cell.getNumericCellValue();
					listOfQuantity.append(Qty2).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

					break;
				case 32:
					//Price 2
					price2=cell.getNumericCellValue();;
					listOfPrices.append(price2).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

					break;
				case 33:
					//Code 2
					code2=cell.getStringCellValue();
					listOfDiscount.append(code2).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

					break;
				case 34:
					Qty3=(int) cell.getNumericCellValue();;
					listOfQuantity.append(Qty3).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

					break;
				case 35://Price 3
					price3=cell.getNumericCellValue();;
					listOfPrices.append(price3).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

					
					
					    break; 
				case 36://Code3
					code3=cell.getStringCellValue();
					listOfDiscount.append(code3).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					
					break;
                case 37://Qty4
					Qty4=(int) cell.getNumericCellValue();;;
					listOfQuantity.append(Qty4).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);


					
					break;
				case 38:  
					//Price4
					price4=cell.getNumericCellValue();;
					listOfPrices.append(price4).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

					break;
				case 39:
					//Code4
					code4=cell.getStringCellValue();
					listOfDiscount.append(code4).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

					break;
				case 40:
					Qty5=(int) cell.getNumericCellValue();;;
					listOfQuantity.append(Qty5).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);


					break;
				case 41:
					//Price5
					price5=cell.getNumericCellValue();;
					listOfPrices.append(price5).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

					break;
				case 42:
					//Code5
					code5=cell.getStringCellValue();
					listOfDiscount.append(code5).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);


					break;
				case 43:
					Qty6=(int) cell.getNumericCellValue();;;
					listOfQuantity.append(Qty6).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);


					
					break;
				case 44://Price6
					price6=cell.getNumericCellValue();;
					listOfPrices.append(price6).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);


					      break;
				case 45://Code6
					code6=cell.getStringCellValue();
					listOfDiscount.append(code6).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);


					
					 break;
				case 46: //Qty7
					Qty7=(int) cell.getNumericCellValue();;
					listOfQuantity.append(Qty7).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);


					break;
				case 47://Price7
					price7=cell.getNumericCellValue();;
					listOfPrices.append(price7).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

					
					break;
				case 48:  //Code7
					code7=cell.getStringCellValue();
					listOfDiscount.append(code7).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);


					
					break;
				case 49://Qty8
					Qty8=(int)cell.getNumericCellValue();;
					listOfQuantity.append(Qty8).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

					
					
					break;
			
				case 50://Price8
					price8=cell.getNumericCellValue();;
					listOfPrices.append(price8).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);

					
					break;
					
				case 51://Code8
					code8=cell.getStringCellValue();
					listOfDiscount.append(code8).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
                     break;
			   
							}  // end inner while loop
					 
		}
			// set  product configuration objects
			
			
			 // end inner while loop
			productExcelObj.setPriceType("L");
		
				if(!StringUtils.isEmpty(imprintValue)){		
				// base price parser
				priceGrids = PriceGridParserObj.getPriceGrids(listOfPrices.toString(), 
						         listOfQuantity.toString(), listOfDiscount.toString(), "USD",
						         PriceIncludes , true, "false", productName,"",priceGrids);	
			}
			
			if(!StringUtils.isEmpty(imprintValue)){
					priceGrids = PriceGridParserObj.getUpchargePriceGrid("1", ImprintchargesString.toString(), "Z", "IMMD:"+imprintValue, 
							"false", "USD", imprintValue,  "Imprint Method Charge", "Other", new Integer(1), priceGrids);
				}
				
			
			listOfPrices = new StringBuilder();
			 PriceIncludes=null;
             imprintValue=null;
             ImprintchargesString =null;
             productId = null;
			 Sold_as_Blanks=null;
			 Four_color_process=null;
		     productName = null;
			 finalResult = null;
		
				
				
			
			}catch(Exception e){

				_LOGGER.error("Error while Processing Product and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() );		 
		}
		}
		workbook.close();
		
		 	productExcelObj.setPriceGrids(priceGrids);
		 	productExcelObj.setProductConfigurations(productConfigObj);

	
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId);
		 	if(num ==1){
		 		numOfProductsSuccess.add("1");
		 	}else if(num == 0){
		 		numOfProductsFailure.add("0");
		 	}else{
		 		
		 	}
		 	_LOGGER.info("list size>>>>>>"+numOfProductsSuccess.size());
		 	_LOGGER.info("Failure list size>>>>>>"+numOfProductsFailure.size());
	       finalResult = numOfProductsSuccess.size() + "," + numOfProductsFailure.size();
	       productDaoObj.getErrorLog(asiNumber,batchId);
	       return finalResult;
		}catch(Exception e){
			_LOGGER.error("Error while Processing excel sheet ,Error message: "+e.getMessage());
			return finalResult;
		}finally{
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet, Error message: "+e.getMessage());
	
			}
				_LOGGER.info("Complted processing of excel sheet ");
				_LOGGER.info("Total no of product:"+numOfProductsSuccess.size() );
		}
		
	}
	
	
	public ProductDao getProductDaoObj() {
		return productDaoObj;
	}

	public void setProductDaoObj(ProductDao productDaoObj) {
		this.productDaoObj = productDaoObj;
	}


	public ShippingEstimationParser getShippingEstimationParser() {
		return shippingEstimationParser;
	}

	public void setShippingEstimationParser(
			ShippingEstimationParser shippingEstimationParser) {
		this.shippingEstimationParser = shippingEstimationParser;
	}

	public OriginParser getOriginParser() {
		return originParser;
	}

	public void setOriginParser(OriginParser originParser) {
		this.originParser = originParser;
	}

	public RushTimeParser getRushTimeParser() {
		return rushTimeParser;
	}

	public void setRushTimeParser(RushTimeParser rushTimeParser) {
		this.rushTimeParser = rushTimeParser;
	}

	public ProductImprintMethodParser getProductImprintMethodParser() {
		return productImprintMethodParser;
	}

	public void setProductImprintMethodParser(
			ProductImprintMethodParser productImprintMethodParser) {
		this.productImprintMethodParser = productImprintMethodParser;
	}

	public ColorParser getColorParserObj() {
		return colorParserObj;
	}

	public void setColorParserObj(ColorParser colorParserObj) {
		this.colorParserObj = colorParserObj;
	}

	public ProductMaterialParser getProductMaterialParserObj() {
		return ProductMaterialParserObj;
	}

	public void setProductMaterialParserObj(
			ProductMaterialParser productMaterialParserObj) {
		ProductMaterialParserObj = productMaterialParserObj;
	}

	public PriceGridParser getPriceGridParserObj() {
		return PriceGridParserObj;
	}

	public void setPriceGridParserObj(PriceGridParser priceGridParserObj) {
		PriceGridParserObj = priceGridParserObj;
	}

	public SizeParser getSizeParserObj() {
		return sizeParserObj;
	}

	public void setSizeParserObj(SizeParser sizeParserObj) {
		this.sizeParserObj = sizeParserObj;
	}

	public PostServiceImpl getPostServiceImpl() {
		return postServiceImpl;
	}

	public void setPostServiceImpl(PostServiceImpl postServiceImpl) {
		this.postServiceImpl = postServiceImpl;
	}
	
	
	
	
}
	