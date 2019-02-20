package com.a4tech.JulyData.excelMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.a4tech.dataStore.ProductDataStore;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.service.imple.PostServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JulyDataMapping {
      
      private static final Logger _LOGGER = Logger.getLogger(JulyDataMapping.class);
      PostServiceImpl postServiceImpl = new PostServiceImpl();
      @SuppressWarnings("finally")
      public int readExcel(String accessToken,Workbook workbook,Integer asiNumber,int batchId){
            
              List<String> numOfProducts = new ArrayList<String>();
              Product productExcelObj = new Product();   
              ProductConfigurations productConfigObj=new ProductConfigurations();
            
              String productId = null;
              
              String priceIncludes = null;
              PriceGridParser priceGridParser = new PriceGridParser();
              
              List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
              String productDescription =null;
              int numOfSheets =0;
             
            try{
            	 for (Sheet sheet : workbook) {
            		 numOfSheets= numOfSheets+1;
            List<String>  productXids = new ArrayList<String>();
            Iterator<Row> iterator = sheet.iterator();
            _LOGGER.info("Started Processing Product");
            StringBuilder listOfQuantity = new StringBuilder();
            StringBuilder listOfPrices = new StringBuilder();
            StringBuilder listOfNetPrice = new StringBuilder();
            StringBuilder listOfDiscount = new StringBuilder();
            List<ProductNumber> pnumberList=new ArrayList<ProductNumber>();
            
            
            String productName = null;
            while (iterator.hasNext()) {
                   
                  try{
                  Row nextRow = iterator.next();
                  if (nextRow.getRowNum() == 0)
                        continue;
                  Iterator<Cell> cellIterator = nextRow.cellIterator();
                  //productXids.add(productId);
                  //String productName = null;
                  boolean checkXid  = false;
                  while (cellIterator.hasNext()) {
                              Cell cell = cellIterator.next();
                              String xid = null;
                              int columnIndex = cell.getColumnIndex();
                              if(columnIndex + 1 == 1){
                                    if(cell.getCellType() == Cell.CELL_TYPE_STRING){
                                    	xid=cell.getStringCellValue();
                                    }else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                                          xid = String.valueOf((int)cell.getNumericCellValue());
                                    }else{
                                          
                                    }
                                         // productXids = new ArrayList<String>();
                                    checkXid = true;
                              }else{
                                    checkXid = false;
                              }
                              if(checkXid){
                                    if(!productXids.contains(xid)){
                                          if(nextRow.getRowNum() != 1){
                                                
                                                   // Add repeatable sets here
                                                      productExcelObj.setPriceGrids(priceGrids);
                                                      productExcelObj.setProductConfigurations(productConfigObj);
                                                      ProductDataStore.setProduct(productId, productExcelObj);
                                                      int num =0;
                                                      if(num ==1){
                                                           numOfProducts.add("1");
                                                     }
                                                     _LOGGER.info("list size>>>>>>>"+numOfProducts.size());
                                                      
                                                      // reset for repeateable set 
                                                      priceGrids = new ArrayList<PriceGrid>();
                                                      productConfigObj = new ProductConfigurations();
                                                      
                                                      pnumberList = new ArrayList<ProductNumber>();
                                                      
                                                      
                                           }
                                              if(!productXids.contains(xid)){
                                                productXids.add(xid);
                                              }
                                                if(numOfSheets == 2 || numOfSheets == 3){
                                                	productExcelObj = ProductDataStore.getProduct(xid);
                                                }else{
                                                	productExcelObj = new Product();
                                                }
                                                
                                    }
                              }
                              
                    if(numOfSheets == 1){
                        switch (columnIndex + 1) {
                        case 1:
                              if(cell.getCellType() == Cell.CELL_TYPE_STRING){
                                    productId = cell.getStringCellValue();//String.valueOf((int)cell.getNumericCellValue());
                              }else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                                    productId = String.valueOf((int)cell.getNumericCellValue());
                              }else{
                                    
                              }
                              productExcelObj.setExternalProductId(productId);
                              break;
                              
                        case 2:
                              //EFF STATUS
                              break;
            
                        case 3:
                              //NOR_INTRO_DT
                        break;
                  
                        case 4:
                              //PRODUCT CATAGORY
                              String categoryName = cell.getStringCellValue();
                              List<String> listOfCategories = new ArrayList<String>();
                              listOfCategories.add(categoryName);
                              productExcelObj.setCategories(listOfCategories);
                            break;
                              
                        case 5:
                                 // NOR_PROD_SUBCAT
                              break;
                              
                        case 6: //NOR SITE
                    
                              break;
                        case 7:
                                  // product description
                    productDescription = cell.getStringCellValue();
                              productExcelObj.setDescription(productDescription);
                              break;
                              
                        case 8: 
                              // ATTRIBUTE_ID
                              break;
                              
                        case 9://product Name
                              productName = cell.getStringCellValue();
                              productExcelObj.setName(productName);
                                    
                              break;
                              
                        
                  }  // end inner while loop            
               }  else if(numOfSheets == 2){
            	   switch (columnIndex + 1) {
                   case 1:
                        /* if(cell.getCellType() == Cell.CELL_TYPE_STRING){
                               productId = cell.getStringCellValue();//String.valueOf((int)cell.getNumericCellValue());
                         }else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                               productId = String.valueOf((int)cell.getNumericCellValue());
                         }else{
                               
                         }
                         productExcelObj.setExternalProductId(productId);*/
                	   continue;
                         //break;
                         
                   case 2:
                         //EFF STATUS
                	   continue;
                         //break;
       
                   case 3:
                         //NOR_INTRO_DT
                	   continue;
                   //break;
             
                   case 4:
                	   continue;
                         
                   case 5:
                            // NOR_PROD_SUBCAT
                         //break;
                	   continue;
                         
                   case 6: //NOR SITE
               
                         //break;
                	   continue;
                   case 7:
                	   continue;
                         
                   case 8: 
                         // ATTRIBUTE_ID
                         break;
                         
                   case 9://price description
                         String priceDescription = cell.getStringCellValue();      
                         break;
                   case 10://price description
                       String retailPrice = cell.getStringCellValue();      
                       break;
                   case 11://price description
                       String discountCode = cell.getStringCellValue();      
                       break;
            	   }        
            	   
               } else if(numOfSheets == 3){
            	   
            	   switch (columnIndex + 1) {
                   case 1:
                         /*if(cell.getCellType() == Cell.CELL_TYPE_STRING){
                               productId = cell.getStringCellValue();//String.valueOf((int)cell.getNumericCellValue());
                         }else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                               productId = String.valueOf((int)cell.getNumericCellValue());
                         }else{
                               
                         }
                         productExcelObj.setExternalProductId(productId);
                         break;*/
                	   continue;
                         
                   case 2:
                         //EFF STATUS
                         //break;
                	   continue;
       
                   case 3:
                         //NOR_INTRO_DT
                	    continue;
                  // break;
             
                   case 4:
                        
                	   continue;
                         
                   case 5:
                            // NOR_PROD_SUBCAT
                         //break;
                	   continue;
                         
                   case 6: //NOR SITE
                        continue;
                         //break;
                   case 7:
                        
                	   continue;
                	                        
                   case 8: 
                         // ATTRIBUTE_ID
                         //break;
                	   continue;
                         
                   case 9://imprint color
                         String impritnColor = cell.getStringCellValue();       
                         break;
                   case 10://imprint size
                       String impritnSize = cell.getStringCellValue();       
                       break;
                   case 11://size
                       String size = cell.getStringCellValue();       
                       break;
                         
               }
                        }
                      
                  }
                  if(numOfSheets ==2){
                	  productExcelObj.setPriceType("L"); 
                      priceGrids = priceGridParser.getPriceGrids(listOfPrices.toString(),listOfNetPrice.toString(), 
                                           listOfQuantity.toString(), listOfDiscount.toString(), "USD",
                                           priceIncludes, true, "Y", productName,"",priceGrids);   
                  }
                  
                  }catch(Exception e){
                  //e.printStackTrace();
                  _LOGGER.error("Error while Processing Product :"+productExcelObj.getExternalProductId()+"Error: " + e.getMessage());          
            }
            }
         }
            workbook.close();
            //inputStream.close();
            ObjectMapper mapper = new ObjectMapper();
            //System.out.println("Final product JSON, written to file");
            ObjectMapper mapper1 = new ObjectMapper();
               // Add repeatable sets here
                 productExcelObj.setPriceGrids(priceGrids);
                 productExcelObj.setProductConfigurations(productConfigObj);
                 /*productExcelObj.setProductRelationSkus(productsku);
                 productExcelObj.setProductNumbers(pnumberList);*/
                 //productList.add(productExcelObj);
                 //int num = postServiceImpl.postProduct(accessToken, productExcelObj);
                 int num =0;
                 if(num ==1){
                       numOfProducts.add("1");
                 }
                 _LOGGER.info("list size>>>>>>"+numOfProducts.size());
                  //System.out.println(mapper1.writeValueAsString(productExcelObj));
            }catch(Exception e){
                  _LOGGER.error("Error while Processing excel sheet "+e.getMessage());
                  return 0;
            }finally{
                  try {
                        workbook.close();
                  //inputStream.close();
                  } catch (IOException e) {
                        _LOGGER.error("Error while Processing excel sheet "+e.getMessage() );
      
                  }
                        _LOGGER.info("Complted processing of excel sheet ");
                        _LOGGER.info("Total no of product:"+numOfProducts.size() );
                        return numOfProducts.size();
            }
      
      }

}
