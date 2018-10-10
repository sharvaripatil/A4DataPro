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
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
		
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
		StringBuilder productDescription = new StringBuilder();
		List<PriceGrid> listOfPriceGrids = new ArrayList<>();
		StringBuilder imprintColors =  new StringBuilder();
		String secondPoleImprint = "";
		String basePriceInclude = "";
		String asiPrdNo        = "";
		String multiColorCharge = "";
		String setUpChargeColumnVal = "";
		String descriptionForSummary = "";
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
							 String desc = finalDescriptionValue(productDescription.toString(),asiPrdNo);
							 productExcelObj.setDescription(desc);
							 if(!StringUtils.isEmpty(listOfColors.toString())){
								 List<Color> listOfColor = gbAttributeParser.getProductColors(listOfColors.toString());
								    productConfiguration.setColors(listOfColor); 
							 }
						       // productExcelObj.setComplianceCerts(Arrays.asList("PROP 65"));
									if (!StringUtils.isEmpty(setUpChargeColumnVal)
											&& !setUpChargeColumnVal.equals("N/A")
											&& !setUpChargeColumnVal.contains("Free")
											&& !setUpChargeColumnVal.equals("Free plate charge")) {
										//if (setUpChargeColumnVal.contains("")) {
											productExcelObj.setPriceGrids(listOfPriceGrids);
											productExcelObj.setProductConfigurations(productConfiguration);
											productExcelObj = gbAttributeParser.getSetupCharge(productExcelObj,
													setUpChargeColumnVal);
										//}
									}
									if(StringUtils.isEmpty(productExcelObj.getSummary())){
										descriptionForSummary = CommonUtility.removeSpecificWord(descriptionForSummary, asiPrdNo);
										productExcelObj.setSummary(
												CommonUtility.getStringLimitedChars(descriptionForSummary.trim(), 130));
									}
									if(!StringUtils.isEmpty(productExcelObj.getAdditionalProductInfo())){
										String additionalPrdInfo = productExcelObj.getAdditionalProductInfo();
										//additionalPrdInfo = CommonUtility.removeSpecificWord(additionalPrdInfo, "Velcro");
										if(additionalPrdInfo.contains("Velcro")){
											additionalPrdInfo = additionalPrdInfo.replaceAll("Velcro", "Hook & Loop");
										} else {
											additionalPrdInfo = additionalPrdInfo.replaceAll("velcro", "Hook & Loop");	
										}
										additionalPrdInfo = additionalPrdInfo.replaceAll("®", "");
										productExcelObj.setAdditionalProductInfo(additionalPrdInfo);	
									}
							 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId, environmentType);
							 	if(num ==1){
							 		numOfProductsSuccess.add("1");
							 	}else if(num == 0){
							 		numOfProductsFailure.add("0");
							 	}else{
							 		
							 	}
							 	_LOGGER.info("list size>>>>>>>"+numOfProductsSuccess.size());
							 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
							 	productDescription = new StringBuilder();
							 	listOfPriceGrids = new ArrayList<>();
							 	listOfPrices = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							    listOfQuantity = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							    listOfColors    = new StringJoiner(ApplicationConstants.CONST_STRING_COMMA_SEP);
							    listOfDiscounts = new StringJoiner(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
							    imprintColors =  new StringBuilder();
							    secondPoleImprint = "";
							    basePriceInclude = "";
							    asiPrdNo        = "";
							    multiColorCharge = "";
							    setUpChargeColumnVal = "";
							    descriptionForSummary = "";
							    imageValues =  new StringBuilder();
						 }
						    if(!productXids.contains(xid)){
						    	productXids.add(xid);
						    }
						    productExcelObj = new Product();
     						 productExcelObj = postServiceImpl.getProduct(accessToken, xid, environmentType);
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
					productExcelObj.setExternalProductId(xid);
					 break;
				case 2:
					 // ignore as per feedback
					  break;
				case 3:// code
					  asiPrdNo = CommonUtility.getCellValueStrinOrInt(cell);
					  if(asiPrdNo.length() > 14){
						  productExcelObj.setProductLevelSku(asiPrdNo);
					  } else {
						  productExcelObj.setAsiProdNo(asiPrdNo);  
					  }
				    break;
				case 4:// description
					String prdName = cell.getStringCellValue();
					prdName = prdName.replaceAll("[^a-zA-Z0-9%/?!\"\\-. ]", "");
					if(prdName.toUpperCase().contains(asiPrdNo)){
						prdName = CommonUtility.removeSpecificWord(prdName, asiPrdNo);
						prdName = prdName.replaceAll(asiPrdNo, "");
					}
					productExcelObj.setName(prdName);
					break;
				case 5:	//Feature_1
					descriptionForSummary = cell.getStringCellValue();
					if(!StringUtils.isEmpty(descriptionForSummary)){
						descriptionForSummary = descriptionForSummary.replaceAll("[^a-zA-Z0-9%/?!\"\\- ]", "");
						productDescription.append(descriptionForSummary).append(ApplicationConstants.CONST_DELIMITER_DOT).append(" ");
					}
					break;
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
						description = description.replaceAll("[^a-zA-Z0-9%/?!\"\\- ]", "");
						productDescription.append(description).append(ApplicationConstants.CONST_DELIMITER_DOT).append(" ");
					}
					 break;
				/*case 15:// Images 
				case 16:
				case 17:
				case 18:
				case 19: 
					break;*/
				case 20:// qty
				case 21:
				case 22:
				case 23:
				case 24: 
					String priceQty = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(priceQty) && !priceQty.equals("0")){
						listOfQuantity.add(priceQty);
					}
					break;
			 
				/*case 25://netPricing 
				case 26:
				case 27: 
				case 28:
				case 29:
					String netPricing = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(netPricing) && !netPricing.equals("0.0") && !netPricing.equals("0.00")){
						listOfPrices.add(netPricing);
					}
					break;*/
				/*case 30: // ignore all coulmns until case 48
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
					break;*/
				case 40: 
				case 41:
				case 42:
				case 43:
				case 44:
					String listPricing = CommonUtility.getCellValueDouble(cell);
					if(!StringUtils.isEmpty(listPricing) && !listPricing.equals("0.0") && !listPricing.equals("0.00")){
						listOfPrices.add(listPricing);
					}
			     break;
				/*case 45:
					break;
				case 46:
					break;
				case 47:
					break;
				case 48:
					break;
				case 49: 
					break;
				case 50:
					break;
				case 51:
					
					break;
					
				case 52: 
					break;
				case 53:
					break;
				case 54: 
					break;*/
				case 55: // discount
				case 56:
				case 57:
				case 58:
				case 59:
					String discountCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(discountCode)){
						listOfDiscounts.add(discountCode);
					}
				    break;
					
				/*case 60: // ignore until cases 104
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
					break;
				case 106:
					 break;
				case 107:
					
					  break;
				case 108:
					 
				    break;
				case 109:
					
					break;*/	
			  case 110:
					multiColorCharge = cell.getStringCellValue();
					if(!StringUtils.isEmpty(multiColorCharge) && !multiColorCharge.contains("N/A")){
						productExcelObj.setPriceGrids(listOfPriceGrids);
						productExcelObj.setProductConfigurations(productConfiguration);
						productExcelObj = gbAttributeParser.getAdditionalColor(productExcelObj, multiColorCharge);
						productConfiguration = productExcelObj.getProductConfigurations();
						listOfPriceGrids = productExcelObj.getPriceGrids();
					}
					break;
				case 111: //color
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
				case 129:	
				case 130:
				case 131: 
				case 132:
				case 133: 
				case 134: // end colors
				case 135: 
					String color = cell.getStringCellValue();
					if(!StringUtils.isEmpty(color)){
						listOfColors.add(color);
					}
					break;
				case 136:
					String size = cell.getStringCellValue();
							if (!StringUtils.isEmpty(size) && !size.equals("Various")
									&& !size.equals("Adjustable to size") && !size.contains("One size fits most")) {
						if(isSizeValue(size)){
							Size sizeVals = gbAttributeParser.getProductSize(size);
							productConfiguration.setSizes(sizeVals);
						}
					}
					break;
				case 137: // imprint size
					String imprintSize = cell.getStringCellValue();
					if(!StringUtils.isEmpty(imprintSize)){
						List<ImprintSize> listOfImprintSizes = gbAttributeParser.getProductImprintSize(imprintSize);
						productConfiguration.setImprintSize(listOfImprintSizes);
					}
					  break;
				case 138: // related Imprint method upcharge price
					/*String imprintMethodPrice = CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(imprintMethodPrice) && !imprintMethodPrice.equals("0")){
						 impritnMethodPrice.append(imprintMethodPrice).append(",");
					 }*///Ignore as per feedback EXCIT-759
				    break;
				case 139:
					/*String imprintMethodDisCode = cell.getStringCellValue();
					 if(!StringUtils.isEmpty(imprintMethodDisCode) && !StringUtils.isEmpty(impritnMethodPrice.toString())){
						 impritnMethodPrice.append(imprintMethodDisCode).append(",");
					 }*///Ignore as per feedback EXCIT-759
				    break;
					
				case 140:// it is related to additional color
				  setUpChargeColumnVal = cell.getStringCellValue();
					break;
					
				case 141: //Multi-Color Imprint - Old
					// this column values same as column 105(Multi-Color Setup Charge)
					// no need process same data again
					break;
					
				case 142://Max Imprint Colors
					 // ignore as per feedback
					   
					break;
					 
				case 143: // additional color and upcharge
					 String multiColorRunningCharge = CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(multiColorRunningCharge) && !multiColorRunningCharge.contains("N/A")
						&& !multiColorRunningCharge.equalsIgnoreCase("Free!") && !multiColorRunningCharge.equalsIgnoreCase("Free")){
						 productExcelObj.setPriceGrids(listOfPriceGrids);
						 productExcelObj.setProductConfigurations(productConfiguration);
						productExcelObj = gbAttributeParser.getMultipleColorUpcharge(multiColorRunningCharge, productExcelObj);
						 listOfPriceGrids = productExcelObj.getPriceGrids();
						 productConfiguration = productExcelObj.getProductConfigurations();
					 }
					break;
					
				case 144: // proof charge
					//for pre-production proofs (as per comment)
					 String proofCharge =CommonUtility.getCellValueStrinOrInt(cell);
					   if(!StringUtils.isEmpty(proofCharge) && !proofCharge.equals("0")){
						   // default proof charge: pre-productionProof
						  /* List<Artwork> listOfArtwork = gbAttributeParser
									.getProductArtwork("PRE-PRODUCTION PROOF", productConfiguration.getArtwork());*/
								List<Artwork> listOfArtwork = gbAttributeParser
										.getProductArtwork("PRE-PRODUCTION PROOF");
						   productConfiguration.setArtwork(listOfArtwork);
						   String price = proofCharge;
						   String disc = "Z";
						   if(proofCharge.contains("(")){
							   proofCharge = getPriceAndDiscountCodeValue(proofCharge);
							   String[] vals = CommonUtility.getValuesOfArray(proofCharge, ":"); 
							   price = vals[0];
							   disc = vals[1];
						   }
								listOfPriceGrids = gbPriceGridParser.getUpchargePriceGrid("1", price, disc, "Artwork & Proofs",
										false, "USD","","PRE-PRODUCTION PROOF", "Artwork Charge", "Other", 1,
										listOfPriceGrids,"","");
					   }
					break;
				case 145: // reverse side imprint
					 String addLocation = cell.getStringCellValue();
					 if(!StringUtils.isEmpty(addLocation)){
						 productExcelObj.setPriceGrids(listOfPriceGrids);
						 productExcelObj.setProductConfigurations(productConfiguration);
						 productExcelObj = gbAttributeParser.getAdditonalLocaAndUpCharge(addLocation, productExcelObj);
						 listOfPriceGrids = productExcelObj.getPriceGrids();
						 productConfiguration = productExcelObj.getProductConfigurations();
					 }
					break;
				case 146://Assembly
					String assembly = cell.getStringCellValue();
					if(!StringUtils.isEmpty(assembly)){
						 productExcelObj.setPriceGrids(listOfPriceGrids);
						 productExcelObj.setProductConfigurations(productConfiguration);
						 productExcelObj = gbAttributeParser.getItemAssembledAndUpcharge(assembly.trim(), productExcelObj);
						 listOfPriceGrids = productExcelObj.getPriceGrids();
						 productConfiguration = productExcelObj.getProductConfigurations();
					}
					 break;
				case 147://produtionTime
					String prdTime = cell.getStringCellValue();
					if(!StringUtils.isEmpty(prdTime)){
						List<ProductionTime> listOfProductionTime = gbAttributeParser.getProductionTime(prdTime);
						productConfiguration.setProductionTime(listOfProductionTime);
					}
					  break;
				case 148:
					 String rushVal = cell.getStringCellValue();
					 if(!StringUtils.isEmpty(rushVal)){
						 productExcelObj.setPriceGrids(listOfPriceGrids);
						 productExcelObj.setProductConfigurations(productConfiguration);
						 productExcelObj = gbAttributeParser.getRushTime(rushVal, productExcelObj); 
						 listOfPriceGrids = productExcelObj.getPriceGrids();
						 productConfiguration = productExcelObj.getProductConfigurations();
					 }
				    break;
				case 149://packaging 
					String packVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(packVal)){
						 productExcelObj.setPriceGrids(listOfPriceGrids);
						 productExcelObj.setProductConfigurations(productConfiguration);
						productExcelObj = gbAttributeParser.getProductPackaging(packVal, productExcelObj);
						listOfPriceGrids = productExcelObj.getPriceGrids();
						productConfiguration = productExcelObj.getProductConfigurations();
					}
				    break;
					
				case 150://ShippingWt
				    // there is no data for this column
					break;
				case 151: //Prop65Label
					// we need to set value end of the mapping since there is no data 
					// but as per feed back we need to "Prop 65 would be checked yes in Certifications and Compliance"
					break;
				case 152://Pencil Sharp
					 String pencilSharp =cell.getStringCellValue();
					 if(!StringUtils.isEmpty(pencilSharp)){
						productExcelObj.setPriceGrids(listOfPriceGrids);
						productExcelObj.setProductConfigurations(productConfiguration);
						productExcelObj = gbAttributeParser.getpencilSharpForOption(pencilSharp, productExcelObj);
						listOfPriceGrids = productExcelObj.getPriceGrids();
						productConfiguration = productExcelObj.getProductConfigurations();
					 }
					break;
				case 153: 
				  String imprintMethodVal = cell.getStringCellValue();
				  if(!StringUtils.isEmpty(imprintMethodVal)){
					  productExcelObj.setProductConfigurations(productConfiguration);
					  productExcelObj = gbAttributeParser.getImprintMethods(imprintMethodVal, productExcelObj);
					  productConfiguration = productExcelObj.getProductConfigurations(); 
				  }
					break;
				case 154: //Fob Point
					String fobVal = cell.getStringCellValue();
					if(!StringUtils.isEmpty(fobVal)){
						List<FOBPoint> listOfFobPoint = gbAttributeParser.getFobPoint(fobVal, accessToken,environmentType);
						productExcelObj.setFobPoints(listOfFobPoint);
					}
					break;
				case 155://Plate Charge
					// There is no data for this column
					break;
				case 156: //Second Pole Imprint
					 secondPoleImprint = cell.getStringCellValue();
					 break;
				case 157://Oxidation
					// There is no data for this column
					  break;
				case 158://origin
					// there is no data
				    break;
				case 159://Materials
					String material = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(material)){
						productExcelObj.setProductConfigurations(productConfiguration);
						productExcelObj = gbAttributeParser.getProductMaterial(material, productExcelObj);
						productConfiguration = productExcelObj.getProductConfigurations();
					}
				    break;
				
				case 161:
					String imprintMethodVal1 = cell.getStringCellValue();
					  if(!StringUtils.isEmpty(imprintMethodVal1)){
						  if(imprintMethodVal1.equalsIgnoreCase("NO IMPRINT")){
							  productExcelObj.setProductConfigurations(productConfiguration);
							  productExcelObj = gbAttributeParser.getImprintMethods(imprintMethodVal1, productExcelObj);
							  productConfiguration = productExcelObj.getProductConfigurations();  
						  } else {
							  imprintColors.append(imprintMethodVal1).append(",");
						  }
					  }
					break;
				case 160://imprint colors
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
				case 189:
				case 190:
				case 191: 
				case 192:
				case 193: 
				case 194:// end Imprint colors 
				String imprintColor = cell.getStringCellValue();
				if(!StringUtils.isEmpty(imprintColor)){
					imprintColors.append(imprintColor).append(",");
				}
				break;
				/*case 195: 
				case 196:// Imprint method values 0,1,2 as per client feedback no need to process those values ,
				case 197: // ignore
				case 198:
				case 199:
				case 200:
				case 201: 
				case 202:
				case 203: 
				case 204: 
					break;*/
				case 205: //Price include related to Imprint method
					basePriceInclude = cell.getStringCellValue();
					basePriceInclude = basePriceInclude.replaceAll("®", "").trim();
					basePriceInclude = CommonUtility.getStringLimitedChars(basePriceInclude, 100);
					break;
				case 206: // images start
					break;
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
				case 230: 
				case 231:
				case 232: 
				case 233:
				case 234:
				case 235: // image end
					String img = cell.getStringCellValue();
					if(!StringUtils.isEmpty(img)){
						imageValues.append(img).append(",");
					}
					break;
				/*case 236: // Itemcolors
					//Ignore colors are available in previous columns
					break;*/
			}  // end inner while loop			 
		}
				productExcelObj.setPriceType("L");
			    if(!StringUtils.isEmpty(imprintColors.toString())){
			    	 ImprintColor imprintColorValues = gbAttributeParser.getImprintColors(imprintColors.toString());
					 productConfiguration.setImprintColors(imprintColorValues);
			    }
			    if(!StringUtils.isEmpty(imageValues.toString())){
			    	List<Image> listOfImages = gbAttributeParser.getImages(imageValues.toString());
				    productExcelObj.setImages(listOfImages);
			    } else{
			    	if(productExcelObj.getImages() != null){
			    		List<Image> imageList = removePriceGridConfiguration(productExcelObj.getImages());
					      productExcelObj.setImages(imageList);			    		
			    	}
			    }
			     if(!StringUtils.isEmpty(secondPoleImprint)){////qty,discountCode,Price
			    	 String priceval = getSecondPoleImprintPriceValues(secondPoleImprint);
			    	 String[] priceVals = CommonUtility.getValuesOfArray(priceval, ",");
			    	 listOfPriceGrids = gbPriceGridParser.getBasePriceGrids(priceVals[2],
			    			 priceVals[0], priceVals[1], "USD", "", true, false, "", "",
								listOfPriceGrids,"dozen");
			     }
			        if(!StringUtils.isEmpty(basePriceInclude)){
			           basePriceInclude = CommonUtility.removeSpecificWord(basePriceInclude, "Price Includes");
			        }
			        if(!StringUtils.isEmpty(listOfPrices.toString())){
			        	listOfPriceGrids = gbPriceGridParser.getBasePriceGrids(listOfPrices.toString(),
								listOfQuantity.toString(), listOfDiscounts.toString(), "USD", basePriceInclude, true, false, "", "",
								listOfPriceGrids,"");	
			        } else {
			        	listOfPriceGrids = gbPriceGridParser.getBasePriceGrids(listOfPrices.toString(),
								listOfQuantity.toString(), listOfDiscounts.toString(), "USD", basePriceInclude, true, true, "", "",
								listOfPriceGrids,"");
			        }
					
					/*if(!StringUtils.isEmpty(impritnMethodPrice.toString())){
						String[] imprintMethodPriceVals = impritnMethodPrice.toString().split(",");
						String priceVal = imprintMethodPriceVals[0];
						if(!priceVal.equals("0")){
							String imprintMethodVals = getImprintMethodAlias(productConfiguration.getImprintMethods());
							if(StringUtils.isEmpty(imprintMethodVals)){
					         productConfiguration.setImprintMethods(productImprintMethods("Printed"));
					         imprintMethodVals = "Printed";
							}
							listOfPriceGrids = gbPriceGridParser.getUpchargePriceGrid("1", priceVal,
							imprintMethodPriceVals[1], "Imprint Method", false, "USD","", imprintMethodVals,
							"Set-up Charge", "Per Order", 1, listOfPriceGrids, "", "");
						}
					}*/
					productExcelObj.setPriceGrids(listOfPriceGrids);
			}catch(Exception e){
				_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage()+"at column number(increament by 1):"+columnIndex);		 
				ErrorMessageList apiResponse = CommonUtility.responseconvertErrorMessageList("Product Data issue in Supplier Sheet: "
				+e.getMessage()+" at column number(increament by 1)"+columnIndex);
				productDaoObj.save(apiResponse.getErrors(),
						productExcelObj.getExternalProductId()+"-Failed", asiNumber, batchId);
		}
		}
		workbook.close();
		if(!StringUtils.isEmpty(listOfColors.toString())){
			 List<Color> listOfColor = gbAttributeParser.getProductColors(listOfColors.toString());
			    productConfiguration.setColors(listOfColor); 
		 }
		 String desc = finalDescriptionValue(productDescription.toString(),asiPrdNo);
		 productExcelObj.setDescription(desc);
		 //productExcelObj.setComplianceCerts(Arrays.asList("PROP 65"));
		 if (!StringUtils.isEmpty(setUpChargeColumnVal)
					&& !setUpChargeColumnVal.equals("N/A")
					&& !setUpChargeColumnVal.contains("Free")
					&& !setUpChargeColumnVal.equals("Free plate charge")) {
				//if (setUpChargeColumnVal.contains("")) {
					productExcelObj.setPriceGrids(listOfPriceGrids);
					productExcelObj.setProductConfigurations(productConfiguration);
					productExcelObj = gbAttributeParser.getSetupCharge(productExcelObj,
							setUpChargeColumnVal);
				//}
			}
		 if(StringUtils.isEmpty(productExcelObj.getSummary())){
			 descriptionForSummary = CommonUtility.removeSpecificWord(descriptionForSummary, asiPrdNo);
			 productExcelObj.setSummary(
						CommonUtility.getStringLimitedChars(descriptionForSummary.trim(), 130));
			}
		 if(!StringUtils.isEmpty(productExcelObj.getAdditionalProductInfo())){
				String additionalPrdInfo = productExcelObj.getAdditionalProductInfo();
				//additionalPrdInfo = CommonUtility.removeSpecificWord(additionalPrdInfo, "Velcro");
				if(additionalPrdInfo.contains("Velcro")){
					additionalPrdInfo = additionalPrdInfo.replaceAll("Velcro", "Hook & Loop");
				} else {
					additionalPrdInfo = additionalPrdInfo.replaceAll("velcro", "Hook & Loop");	
				}
				additionalPrdInfo = additionalPrdInfo.replaceAll("®", "");
				productExcelObj.setAdditionalProductInfo(additionalPrdInfo);	
			}
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId, environmentType);
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
		Cell xidCell =  row.getCell(ApplicationConstants.CONST_NUMBER_ZERO);
		String productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		if(StringUtils.isEmpty(productXid) || "N/A".equalsIgnoreCase(productXid) || "#N/A".equalsIgnoreCase(productXid)){
		     xidCell = row.getCell(ApplicationConstants.CONST_INT_VALUE_TWO);
		     productXid = CommonUtility.getCellValueStrinOrInt(xidCell);
		}
		return productXid;
	}
	private String finalDescriptionValue(String newDesc,String productNum){
		if(!StringUtils.isEmpty(newDesc)){
			newDesc = newDesc.replaceAll("velcro", "Hook & Loop");
			if(newDesc.toUpperCase().contains(productNum)){
				newDesc = CommonUtility.removeSpecificWord(newDesc, productNum);
				newDesc = newDesc.replaceAll(productNum, "");
			}
			newDesc = CommonUtility.getStringLimitedChars(newDesc, 800);
			return newDesc;
		} 
		return newDesc;
	}
	private String getSecondPoleImprintPriceValues(String value){//qty,discountCode,Price
		StringBuilder priceValues = new StringBuilder();
		if(value.equalsIgnoreCase("Per dozen, 6-23 dz. @ $3.50 (G); 24-119 dz @ $3.00 (G) 72 dz @ $2.50 (G)")){
			priceValues.append("6").append("___").append("24").append("___").append("72").append(",").append("G").
			append("___").append("G").append("___").append("G").append(",").append("3.50").append("___").append("3.00").append("___").append("2.50");
		} else if(value.equalsIgnoreCase("Add $4.50 (c) per dozen")){
			priceValues.append("1").append(",").append("C").append(",").append("4.50");
		} else if(value.equalsIgnoreCase("Per dozen, 24-119 dz @ $3.00 (C); 120 dz @ $1.00 (C)")){
			priceValues.append("24").append("___").append("120").append(",").
			       append("C").append("___").append("C").append(",").append("3.00").append("___").append("1.00");
		} else if(value.equalsIgnoreCase("Per dozen, 6-23 dz @ $3.50 (G); 24-119 dz @ $3.00 (G); 120 dz @ $2.50 (G)")){
			priceValues.append("6").append("___").append("24").append("___").append("120").append(",").
		       append("G").append("___").append("G").append("___").append("G").append(",").
		       append("3.50").append("___").append("3.00").append("___").append("2.50");
		} else if(value.equalsIgnoreCase("Per dozen, 12-23 dz: N/A; 24-47 dz @ $4.20 (C); 48-119 dz @ $3.35 (C); 120 dz @ $2.50 (C)")){
			priceValues.append("24").append("___").append("48").append("___").append("120").append(",").
		       append("C").append("___").append("C").append("___").append("C").append(",").
		       append("4.20").append("___").append("3.35").append("___").append("2.50");
		} else if(value.equalsIgnoreCase("Per dozen, 12+ dz. @ $3.35 (C)") || 
				           value.equalsIgnoreCase("Per 16 golf balls, 12+ dz. @ $3.35 (C)")){
			priceValues.append("12").append(",").append("C").append(",").append("3.35");
		} else if(value.equalsIgnoreCase("Per dozen, 6-23 dz. @ $3.50 (G); 24-119 dz @ $3.00 (G) 120 dz @ $2.50 (G)")){
			priceValues.append("6").append("___").append("24").append("___").append("120").append(",").
		       append("G").append("___").append("G").append("___").append("G").append(",").
		       append("3.50").append("___").append("3.00").append("___").append("2.50");
		}
		return priceValues.toString();
	}
	/*private String getImprintMethodAlias(List<ImprintMethod> listOfImprintMethod){
		String imprintMethodAlias = listOfImprintMethod.stream().map(ImprintMethod::getAlias)
				.collect(Collectors.joining(","));
		return imprintMethodAlias;
	}*/
	
	private boolean isSizeValue(String sizeVal){
		if(sizeVal.equalsIgnoreCase("Official size") || sizeVal.equalsIgnoreCase("Varies") ||
				sizeVal.equalsIgnoreCase("One size fits most") || sizeVal.equalsIgnoreCase("NFL Official Size")){
			return false;
		}
		return true;
	}
	private String getPriceAndDiscountCodeValue(String val){
		//$15.00 (G)
		val = val.replaceAll("[^0-9A-Za-z.()]", "");
		String discountCode = CommonUtility.extractValueSpecialCharacter("(", ")", val);
		val = val.replaceAll("[^0-9.]", "");
		val = val+":"+discountCode;
		return val;
	}
	/*private List<ImprintMethod> productImprintMethods(String value){
		List<ImprintMethod> imprintMethodList = new ArrayList<>();
		ImprintMethod imprintMethod = new ImprintMethod();
		imprintMethod.setType(value);
		imprintMethod.setAlias(value);
		imprintMethodList.add(imprintMethod);
		return imprintMethodList;
	}*/
	//private boolean
	private List<Image> removePriceGridConfiguration(List<Image> imageOldList){
		List<Image> newImage = new ArrayList<>();
		for (Image image : imageOldList) {
			image.setConfigurations(new ArrayList<>());
			newImage.add(image);
		}
		return newImage;
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
