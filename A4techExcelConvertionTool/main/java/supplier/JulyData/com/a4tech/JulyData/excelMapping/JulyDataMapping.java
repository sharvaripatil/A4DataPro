package com.a4tech.JulyData.excelMapping;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;

import com.a4tech.product.USBProducts.criteria.parser.CatalogParser;
import com.a4tech.product.USBProducts.criteria.parser.PersonlizationParser;
import com.a4tech.JulyData.excelMapping.PriceGridParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductArtworkProcessor;
import com.a4tech.product.USBProducts.criteria.parser.ProductColorParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductImprintColorParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductImprintMethodParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductMaterialParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductNumberParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductOptionParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductOriginParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductPackagingParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductRushTimeParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductSameDayParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductSampleParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductShapeParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductSizeParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductSkuParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductThemeParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductTradeNameParser;
import com.a4tech.product.USBProducts.criteria.parser.ProductionTimeParser;
import com.a4tech.product.USBProducts.criteria.parser.ShippingEstimationParser;
import com.a4tech.product.model.Artwork;
import com.a4tech.product.model.Catalog;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintColor;
import com.a4tech.product.model.ImprintColorValue;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Inventory;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.Personalization;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.ProductSkus;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.SameDayRush;
import com.a4tech.product.model.Samples;
import com.a4tech.product.model.Shape;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.TradeName;
import com.a4tech.product.model.WarrantyInformation;
import com.a4tech.product.service.postImpl.PostServiceImpl;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.LookupData;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JulyDataMapping {
      
      private static final Logger _LOGGER = Logger.getLogger(JulyDataMapping.class);
      PostServiceImpl postServiceImpl = new PostServiceImpl();
      @SuppressWarnings("finally")
      public int readExcel(String accessToken,Workbook workbook){
            
            List<String> numOfProducts = new ArrayList<String>();
      
            //Workbook workbook = null;
            List<String>  productXids = new ArrayList<String>();
              Product productExcelObj = new Product();   
              ProductConfigurations productConfigObj=new ProductConfigurations();
            
              String productId = null;
              
              String priceIncludes = null;
              PriceGridParser priceGridParser = new PriceGridParser();
              
              List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
              String productDescription =null;
              
             
            try{
          Sheet sheet = workbook.getSheetAt(0);
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
                  productXids.add(productId);
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
                                    //xid = cell.getStringCellValue();
                                    /*if(productXids.contains(xid)){
                                          productXids.add(xid);
                                    }else{*/
                                          productXids = new ArrayList<String>();
                                    //}
                                    
                                    checkXid = true;
                              }else{
                                    checkXid = false;
                              }
                              if(checkXid){
                                    if(!productXids.contains(xid)){
                                          if(nextRow.getRowNum() != 1){
                                                System.out.println("Java object converted to JSON String, written to file");
                                                   // Add repeatable sets here
                                                      productExcelObj.setPriceGrids(priceGrids);
                                                      //productConfigObj.setOptions(option);
                                                      productExcelObj.setProductConfigurations(productConfigObj);
                                                      
                                                       
                                                       //productList.add(productExcelObj);
                                                     int num = postServiceImpl.postProduct(accessToken, productExcelObj);
                                                     if(num ==1){
                                                           numOfProducts.add("1");
                                                     }
                                                      //System.out.println(mapper.writeValueAsString(productExcelObj));
                                                     _LOGGER.info("list size>>>>>>>"+numOfProducts.size());
                                                      
                                                      // reset for repeateable set 
                                                      priceGrids = new ArrayList<PriceGrid>();
                                                      productConfigObj = new ProductConfigurations();
                                                      
                                                      pnumberList = new ArrayList<ProductNumber>();
                                                      
                                                      
                                           }
                                              if(!productXids.contains(xid)){
                                                productXids.add(xid);
                                              }
                                                productExcelObj = new Product();
                                    }
                              }
                              if(productXids.size() >1  && !LookupData.isRepeateIndex(String.valueOf(columnIndex+1))){
                                    continue;
                              }

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
                    
                        }
                        
                        
                        //productExcelObj.setProductConfigurations(productConfigObj);l
                  // end inner while loop
                  //if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
                      productExcelObj.setPriceType("L"); 
                        priceGrids = priceGridParser.getPriceGrids(listOfPrices.toString(),listOfNetPrice.toString(), 
                                             listOfQuantity.toString(), listOfDiscount.toString(), "USD",
                                             priceIncludes, true, "Y", productName,"",priceGrids);   
                  //}
                  
                  
                  
                        
                  
                        
                        
                  
                  }catch(Exception e){
                  //e.printStackTrace();
                  _LOGGER.error("Error while Processing Product :"+productExcelObj.getExternalProductId() );          
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
                 int num = postServiceImpl.postProduct(accessToken, productExcelObj);
                 if(num ==1){
                       numOfProducts.add("1");
                 }
                 _LOGGER.info("list size>>>>>>"+numOfProducts.size());
                  //System.out.println(mapper1.writeValueAsString(productExcelObj));
      
            }catch(Exception e){
                  _LOGGER.error("Error while Processing excel sheet ");
                  return 0;
            }finally{
                  try {
                        workbook.close();
                  //inputStream.close();
                  } catch (IOException e) {
                        _LOGGER.error("Error while Processing excel sheet");
      
                  }
                        _LOGGER.info("Complted processing of excel sheet ");
                        _LOGGER.info("Total no of product:"+numOfProducts.size() );
                        return numOfProducts.size();
            }
      
      }

}
