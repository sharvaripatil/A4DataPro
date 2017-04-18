package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;

import com.a4tech.core.errors.ErrorMessageList;
import com.a4tech.excel.service.IExcelParser;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Artwork;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintColor;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.Size;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.goldbond.GoldbondAttributeParser;
import parser.goldbond.GoldbondPriceGridParser;

public class GoldBondExcelMapping implements IExcelParser{
	
	private static final Logger _LOGGER = Logger.getLogger(GoldBondExcelMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private GoldbondAttributeParser gbAttributeParser;
	private GoldbondPriceGridParser gbPriceGridParser;
   
	@Override
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		  Product productExcelObj = new Product();
		  ProductConfigurations productConfiguration = new ProductConfigurations();
 		try{ 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		
		StringJoiner listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfDiscounts = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
		StringJoiner listOfColors    = new StringJoiner(ApplicationConstants.CONST_STRING_COMMA_SEP);
		String xid = null;
		int columnIndex=0;
		// String listPrice = "";
		// String priceQty  = "";
		StringJoiner productDescription = new StringJoiner(" ");
		StringJoiner impritnMethodPrice = new StringJoiner(",");
		List<PriceGrid> listOfPriceGrids = new ArrayList<>();
		StringBuilder imprintColors =  new StringBuilder();
		StringBuilder imageValues =  new StringBuilder();
		while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if(nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO){
				continue;
			}
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if(xid != null){
				productXids.add(xid);
			}
			 boolean checkXid  = false;
			
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				
				 columnIndex = cell.getColumnIndex();
				if(columnIndex + 1 == 1){
					xid = getProductXid(nextRow);
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							 System.out.println("Java object converted to JSON String, written to file");
							 String desc = finalDescriptionValue(productExcelObj.getDescription(), productDescription.toString());
							 productExcelObj.setDescription(desc);
							    List<Color> listOfColor = gbAttributeParser.getProductColors(listOfColors.toString());
							    productConfiguration.setColors(listOfColor);
						        productExcelObj.setComplianceCerts(Arrays.asList("PROP 65"));
							 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId);
							 	if(num ==1){
							 		numOfProductsSuccess.add("1");
							 	}else if(num == 0){
							 		numOfProductsFailure.add("0");
							 	}else{
							 		
							 	}
							 	_LOGGER.info("list size>>>>>>>"+numOfProductsSuccess.size());
							 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
							 	productDescription = new StringJoiner(" ");
							 	impritnMethodPrice = new StringJoiner(",");
							 	listOfPriceGrids = new ArrayList<>();
							 	listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							    listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							    listOfColors    = new StringJoiner(ApplicationConstants.CONST_STRING_COMMA_SEP);
							    imprintColors =  new StringBuilder();
							    imageValues =  new StringBuilder();
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    }
						    productExcelObj = new Product();
     						 productExcelObj = postServiceImpl.getProduct(accessToken, xid);
						     if(productExcelObj == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						    	 productConfiguration = new ProductConfigurations();
						     }else{
						    	 productExcelObj = gbAttributeParser.keepExistingProductData(productExcelObj);
						    	 productConfiguration = productExcelObj.getProductConfigurations();
						     }
							
					 }
				}else{
					
				}
				
				switch (columnIndex+1) {
				case 1: //xid
					 break;
				case 2:
					 
					  break;
				case 3:
					 String asiPrdNo = cell.getStringCellValue();
					  productExcelObj.setAsiProdNo(asiPrdNo);
				    break;
				case 4:// description
					String prdName = cell.getStringCellValue();
					prdName = prdName.replaceAll("[^a-zA-Z0-9%/-! ]", "");
					productExcelObj.setName(prdName);
					break;
				case 5:	
				case 6: 
				case 7:
				case 8: 
				case 9: 
				case 10: 
				case 11:
				case 12:
				case 13:
				case 14://Features
					String description = cell.getStringCellValue();
					if(!StringUtils.isEmpty(description)){
						description = description.replaceAll("[^a-zA-Z0-9%/-! ]", "");
						productDescription.add(description);
					}
					 break;
				case 15:// qty
				case 16:
				case 17:
				case 18:
				case 19: 
					String priceQty = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(priceQty)){
						listOfQuantity.add(priceQty);
					}
					break;
			 
				case 20://netPricing 
				case 21:
				case 22: 
				case 23:
				case 24:
					String netPricing = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(netPricing)){
						listOfPrices.add(netPricing);
					}
					break;
				case 25: // ignore all coulmns until case 48
					break;
					
				case 26:
					 break;
				case 27:
					  
					  break;
				case 28:
					 
				    break;
				case 29:
					
					
				    break;
					
				case 30:
					
					
					break;
					
				case 31: 
				
					
					break;
					
				case 32:
					 
					   
					break;
					
				case 33: 
					
					
					break;
					
				case 34: 
					break;
				case 35: 
					
					
					break;
				case 36:
				    
					break;
					
				case 37:
					break;
				case 38:
					
					
					 break;
				case 39:
					
					
			     break;
				case 40:
			
						
					break;
				case 41:
				
				
					break;
				case 42:
				
				
					break;
				case 43:
					break;
				case 44: 
					break;
				case 45:
					break;
				case 46:
					
					break;
					
				case 47: 
					break;
				case 48:
					break;
				case 49: 
					break;
				case 50: // discount
				case 51:
				case 52:
				case 53:
				case 54:
					String discountCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(discountCode)){
						listOfDiscounts.add(discountCode);
					}
				    break;
					
				case 55: // ignore until cases 104
				
					
					break;
					
				case 56: 
				
					
					break;
					
				case 57:
					 
					   
					break;
					
				case 58: 
				
					
					break;
					
				case 59: 
					break;
				case 60: 
					 
					
					break;
				case 61:
				   
					break;
					
				case 62:
					break;
				case 63:
				
					
					 break;
				case 64:
				
					
			     break;
				case 65:
		
					break;
				case 66:
		
				
					break;
				case 67:
			
				
					break;
				case 68:
					break;
				case 69: 
					break;
				case 70:
					break;
				case 71:
					
					break;
					
				case 72: 
					break;
				case 73:
					break;
				case 74:
					
					break;
				case 75: 
					
					
					
					break;
					
				case 76:
					 break;
				case 77:
					  
					  break;
				case 78:
					 
				    break;
				case 79:
					
					
				    break;
					
				case 80:
					
					
					break;
					
				case 81: 
				
					
					break;
					
				case 82:
					 
					   
					break;
					
				case 83: 
					
					
					break;
					
				case 84: 
					break;
				case 85: 
					
					
					break;
				case 86:
				    
					break;
					
				case 87:
					break;
				case 88:
					
					
					 break;
				case 89:
					
					
			     break;
				case 90:
			
						
					break;
				case 91:
				
				
					break;
				case 92:
				
				
					break;
				case 93:
					break;
				case 94: 
					break;
				case 95:
					break;
				case 96:
					
					break;
					
				case 97: 
					break;
				case 98:
					break;
				case 99:
					
					break;
				case 100: 
					break;
				case 101:
					 break;
				case 102:
					
					  break;
				case 103:
					 
				    break;
				case 104:
					
					break;
				case 105:
					String multiColorCharge = cell.getStringCellValue();
					productExcelObj = gbAttributeParser.getAdditionalColor(productExcelObj, multiColorCharge);
				    
					break;
				case 106: //color
				case 107:
				case 108: 
				case 109: 
				case 110: 
				case 111:
				case 112:
				case 113:
				case 114:
				case 115:
				case 116: 
				case 117:
				case 118: 
				case 119: 
				case 120: 
				case 121:
				case 122:
				case 123:
				case 124:	
				case 125:
				case 126: 
				case 127:
				case 128: 
				case 129: // end colors
				case 130: 
					String color = cell.getStringCellValue();
					if(!StringUtils.isEmpty(color)){
						listOfColors.add(color);
					}
					break;
				case 131:
					String size = cell.getStringCellValue();
					if(!StringUtils.isEmpty(size)){
						Size sizeVals = gbAttributeParser.getProductSize(size);
						productConfiguration.setSizes(sizeVals);
					}
					break;
				case 132: // imprint size
					String imprintSize = cell.getStringCellValue();
					if(StringUtils.isEmpty(imprintSize)){
						List<ImprintSize> listOfImprintSizes = gbAttributeParser.getProductImprintSize(imprintSize);
						productConfiguration.setImprintSize(listOfImprintSizes);
					}
					  break;
				case 133: // related Imprint method upcharge price
					String imprintMethodPrice = CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(imprintMethodPrice) && !imprintMethodPrice.equals("0")){
						 impritnMethodPrice.add(imprintMethodPrice);
					 }
				    break;
				case 134:
					String imprintMethodDisCode = cell.getStringCellValue();
					 if(!StringUtils.isEmpty(imprintMethodDisCode)){
						 impritnMethodPrice.add(imprintMethodDisCode);
					 }
				    break;
					
				case 135:// it is related to additional color
				 String setUpCharge = cell.getStringCellValue();
					break;
					
				case 136: //Multi-Color Imprint - Old
					break;
					
				case 137://Max Imprint Colors
					 // ignore as per feedback
					   
					break;
					
				case 138: 
				  
					
					break;
					
				case 139: // proof charge
					//for pre-production proofs (as per comment)
					 String proofCharge = cell.getStringCellValue();
					   if(!StringUtils.isEmpty(proofCharge) && !proofCharge.equals("0")){
						   // default proof charge: pre-productionProof
						   List<Artwork> listOfArtwork = gbAttributeParser.getProductArtwork("PRE-PRODUCTION PROOF");
						   productConfiguration.setArtwork(listOfArtwork);
								listOfPriceGrids = gbPriceGridParser.getUpchargePriceGrid("1", proofCharge, "Z", "Artwork & Proofs",
										false, "USD", "PRE-PRODUCTION PROOF", "Artwork Charge", "Other", 1,
										listOfPriceGrids,"","");
					   }
					break;
				case 140: // reverse side imprint
					 String addLocation = cell.getStringCellValue();
					 if(!StringUtils.isEmpty(addLocation)){
						 productExcelObj.setPriceGrids(listOfPriceGrids);
						 productExcelObj = gbAttributeParser.getAdditonalLocaAndUpCharge(addLocation, productExcelObj);
						 listOfPriceGrids = productExcelObj.getPriceGrids();
					 }
					break;
				case 141://Assembly
					String assembly = cell.getStringCellValue();
					if(!StringUtils.isEmpty(assembly)){
						 productExcelObj.setPriceGrids(listOfPriceGrids);
						 productExcelObj = gbAttributeParser.getItemAssembledAndUpcharge(assembly.trim(), productExcelObj);
						 listOfPriceGrids = productExcelObj.getPriceGrids();
					}
					 break;
				case 142://produtionTime
					String prdTime = cell.getStringCellValue();
					if(!StringUtils.isEmpty(prdTime)){
						List<ProductionTime> listOfProductionTime = gbAttributeParser.getProductionTime(prdTime);
						productConfiguration.setProductionTime(listOfProductionTime);
					}
					  break;
				case 143:
					 String rushVal = cell.getStringCellValue();
					 if(!StringUtils.isEmpty(rushVal)){
						 productExcelObj.setPriceGrids(listOfPriceGrids);
						 productExcelObj = gbAttributeParser.getRushTime(rushVal, productExcelObj); 
						 listOfPriceGrids = productExcelObj.getPriceGrids();
					 }
				    break;
				case 144://packaging
					String packVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(packVal)){
						 productExcelObj.setPriceGrids(listOfPriceGrids);
						productExcelObj = gbAttributeParser.getProductPackaging(packVal, productExcelObj);
						listOfPriceGrids = productExcelObj.getPriceGrids();
					}
				    break;
					
				case 145://ShippingWt
				    // there is no data for this column
					break;
				case 146: //Prop65Label
					// we need to set value end of the mapping since there is no data 
					// but as per feed back we need to "Prop 65 would be checked yes in Certifications and Compliance"
					break;
				case 147://Pencil Sharp
					 String pencilSharp =cell.getStringCellValue();
					 if(!StringUtils.isEmpty(pencilSharp)){
						productExcelObj.setPriceGrids(listOfPriceGrids);
						productExcelObj = gbAttributeParser.getpencilSharpForOption(pencilSharp, productExcelObj);
						listOfPriceGrids = productExcelObj.getPriceGrids();	
					 }
					break;
				case 148: 
				  String imprintMethodVal = cell.getStringCellValue();
				  if(!StringUtils.isEmpty(imprintMethodVal)){
					  
					  impritnMethodPrice.add(imprintMethodVal);// used to upcharge 
				  }
					break;
				case 149: //Fob Point
					String fobVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(fobVal)){
						List<FOBPoint> listOfFobPoint = gbAttributeParser.getFobPoint(fobVal, accessToken);
						productExcelObj.setFobPoints(listOfFobPoint);
					}
					break;
				case 150://Plate Charge
					// There is no data for this column
					break;
				case 151: //Second Pole Imprint
					// waiting for client feed back since it is difficult to processing values in this column
					 break;
				case 152://Oxidation
					// There is no data for this column
					  break;
				case 153://origin
					// there is no data
				    break;
				case 154://Materials
					String material = cell.getStringCellValue();
					if(!StringUtils.isEmpty(material)){
						productExcelObj.setProductConfigurations(productConfiguration);
						productExcelObj = gbAttributeParser.getProductMaterial(material, productExcelObj);
						productConfiguration = productExcelObj.getProductConfigurations();
					}
				    break;
				case 155://imprint colors
				case 156: 
				case 157:	
				case 158: 
				case 159: 	
				case 160: 
				case 161:
				case 162:
				case 163:
				case 164:
				case 165:
				case 166: 
				case 167:
				case 168: 
				case 169: 
				case 170: 
				case 171:
				case 172:
				case 173:
				case 174:
				case 175:
				case 176: 
				case 177:
				case 178: 
				case 179: 
				case 180: 
				case 181:
				case 182:
				case 183:
				case 184:
				case 185:
				case 186: 
				case 187:
				case 188: 
				case 189:// end Imprint colors 
				String imprintColor = cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintColor)){
					imprintColors.append(imprintColor).append(",");
				}
				break;
				/*case 190: 
				case 191:// Imprint method values 0,1,2 as per client feedback no need to process those values ,
				case 192: // ignore
				case 193:
				case 194:
				case 195:
				case 196: 
				case 197:
				case 198: 
				case 199: 
					break;*/
				case 200: //Price include
					break;
				case 201: // images start
				case 202:
				case 203:
				case 204:	
				case 205:
				case 206: 
				case 207:
				case 208: 
				case 209: 
				case 210: 
				case 211:
				case 212:
				case 213:
				case 214:
				case 215:
				case 216:
				case 217:
				case 218:
				case 219:	
				case 220:
				case 221:
				case 222: 
				case 223:
				case 224:
				case 225: 
				case 226:
				case 227: 
				case 228:
				case 229:
				case 230: // image end
					String img = cell.getStringCellValue();
					if(!StringUtils.isEmpty(img)){
						imageValues.append(img).append(",");
					}
					break;	
			}  // end inner while loop
					 
		}
				productExcelObj.setPriceType("N");
				String qurFlag = "n"; // by default for testing purpose
				listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			    listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
			    if(!StringUtils.isEmpty(imprintColors)){
			    	 ImprintColor imprintColorValues = gbAttributeParser.getImprintColors(imprintColors.toString());
					 productConfiguration.setImprintColors(imprintColorValues);
			    }
			    if(!StringUtils.isEmpty(imageValues)){
			    	List<Image> listOfImages = gbAttributeParser.getImages(imageValues.toString());
				    productExcelObj.setImages(listOfImages);
			    }
			    
			}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);		 
				ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: "
				+e.getMessage()+" at column number(increament by 1)"+columnIndex);
				productDaoObj.save(apiResponse.getErrors(),
						productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);
		}
		}
		workbook.close();
		 List<Color> listOfColor = gbAttributeParser.getProductColors(listOfColors.toString());
		 productConfiguration.setColors(listOfColor);
		 String desc = finalDescriptionValue(productExcelObj.getDescription(), productDescription.toString());
		 productExcelObj.setDescription(desc);
		 productExcelObj.setComplianceCerts(Arrays.asList("PROP 65"));
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
	       productDaoObj.saveErrorLog(asiNumber,batchId);
	       return finalResult;
		}catch(Exception e){
			_LOGGER.error("Error while Processing excel sheet " +e.getMessage());
			return finalResult;
		}finally{
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet" +e.getMessage());
	
			}
				_LOGGER.info("Complted processing of excel sheet ");
				_LOGGER.info("Total no of product:"+numOfProductsSuccess.size() );
		}
		
	}

	public String getProductXid(Row row){
		Cell xidCell =  row.getCell(ApplicationConstants.CONST_INT_VALUE_ONE);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid) || "N/A".equalsIgnoreCase(productXid)){
		     xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_TWO);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}
	private String finalDescriptionValue(String existingDesc,String newDesc){
		if(!StringUtils.isEmpty(existingDesc)){
			newDesc = newDesc + " "+existingDesc;
			return newDesc;
		} else{
			return newDesc;
		}
	}
	public PostServiceImpl getPostServiceImpl() {
		return postServiceImpl;
	}

	public void setPostServiceImpl(PostServiceImpl postServiceImpl) {
		this.postServiceImpl = postServiceImpl;
	}
	public ProductDao getProductDaoObj() {
		return productDaoObj;
	}

	public void setProductDaoObj(ProductDao productDaoObj) {
		this.productDaoObj = productDaoObj;
	}
	public GoldbondAttributeParser getGbAttributeParser() {
		return gbAttributeParser;
	}

	public void setGbAttributeParser(GoldbondAttributeParser gbAttributeParser) {
		this.gbAttributeParser = gbAttributeParser;
	}
	 
		public GoldbondPriceGridParser getGbPriceGridParser() {
			return gbPriceGridParser;
		}

		public void setGbPriceGridParser(GoldbondPriceGridParser gbPriceGridParser) {
			this.gbPriceGridParser = gbPriceGridParser;
		}


	
}
