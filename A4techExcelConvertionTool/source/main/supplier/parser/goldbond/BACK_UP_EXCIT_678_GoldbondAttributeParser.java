package parser.goldbond;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.AdditionalLocation;
import com.a4tech.product.model.Artwork;
import com.a4tech.product.model.BlendMaterial;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.FOBPoint;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintColor;
import com.a4tech.product.model.ImprintColorValue;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.RushTimeValue;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

public class BACK_UP_EXCIT_678_GoldbondAttributeParser {

	private static Logger _LOGGER = Logger.getLogger(GoldbondAttributeParser.class);
	String pattern_remove_specialSymbols = "[^0-9.xX/\\- ]";
	private GoldbondPriceGridParser gbPriceGridParser;
	private LookupServiceData lookupServiceData;
	private static List<String> lookupFobPoints = null;
	public Product keepExistingProductData(Product existingProduct){
		//Please keep the Categories,Images and Themes for existing products.
		Product newProduct = new Product();
		ProductConfigurations newConfiguration = new ProductConfigurations();
		ProductConfigurations existingConfiguration = existingProduct.getProductConfigurations();
		List<Theme> existingThemes = existingConfiguration.getThemes();
		if(!CollectionUtils.isEmpty(existingThemes)){
			newConfiguration.setThemes(existingThemes);
		}
		if(!CollectionUtils.isEmpty(existingProduct.getCategories())){
		  newProduct.setCategories(existingProduct.getCategories());
		}
		if(!CollectionUtils.isEmpty(existingProduct.getImages())){
			newProduct.setImages(existingProduct.getImages());
		}
		//keepExistingUpchargesAndCriteria
		//
		List<PriceGrid> newPriceGrid = new ArrayList<>();
		List<PriceGrid> oldPriceGrids = existingProduct.getPriceGrids();
		for (PriceGrid priceGrid : oldPriceGrids) {
			if(priceGrid.getIsBasePrice() ){
				continue;
			}
            if(priceGrid.getUpchargeType().contains("Sample Charge") || priceGrid.getUpchargeType().contains("Proof Charge") ||
            		priceGrid.getUpchargeType().contains("Less than Minimum Charge") || priceGrid.getUpchargeType().contains("Shipping")){
            	newPriceGrid.add(priceGrid);
            }
		}
		if(existingConfiguration.getSamples() != null){
        	newConfiguration.setSamples(existingConfiguration.getSamples());
        }
        if(existingConfiguration.getArtwork() != null){
        	newConfiguration.setArtwork(existingConfiguration.getArtwork());
        }
        if(existingProduct.isCanOrderLessThanMinimum()){
        	newProduct.setCanOrderLessThanMinimum(existingProduct.isCanOrderLessThanMinimum());
        }
        if(existingConfiguration.getShippingEstimates() != null){
        	newConfiguration.setShippingEstimates(existingConfiguration.getShippingEstimates());
        }
        if(!CollectionUtils.isEmpty(existingConfiguration.getOptions())){
        	List<Option> existingOptions = existingConfiguration.getOptions();
			List<Option> newOptions = existingOptions.stream()
					.filter(option -> "Shipping".equalsIgnoreCase(option.getOptionType()))
					.collect(Collectors.toList());
			newConfiguration.setOptions(newOptions);
        }
        newProduct.setPriceGrids(newPriceGrid);
		 newProduct.setProductConfigurations(newConfiguration);
		return newProduct;
	}
	public Product getMultiColorCharge(Product existingProduct,String value,String upchargeName){
		   String priceVal = "";
		   String discountCode = "";
		   List<PriceGrid> existingPriceGrid = existingProduct.getPriceGrids();
		   if(CollectionUtils.isEmpty(existingPriceGrid)){
			   existingPriceGrid = new ArrayList<>();
		   }
		   ProductConfigurations existingConfiguration = existingProduct.getProductConfigurations();
		   String priceInclude = "";
		    if(value.equalsIgnoreCase("$50.00 (G) per color, applies to repeat orders")){
		    	priceVal = "50";
		    	discountCode = "G";
		    } else if(value.equalsIgnoreCase("$50.00 (G) per color")){
		    	priceVal = "50";
		    	discountCode = "G";
		    } else if(value.equalsIgnoreCase("$15.00 (A) per color")){
		    	priceVal = "15";
		    	discountCode = "A";
		    } else if(value.equalsIgnoreCase("$50.00 (G) per color, 2-color only (CANNOT BE CLOSE REGISTRATION)")){
		    	priceVal = "50";
		    	discountCode = "G";
		    } else if(value.equalsIgnoreCase("$50.00 (G) per color (multi-color imprint not available for two side imprint)")){
		    	priceVal = "50";
		    	discountCode = "G";
		    	priceInclude = "multi-color imprint not available for two side imprint";
		    } else if(value.equalsIgnoreCase("$50.00 (G) per color (up to 2 colors), applies to repeat orders")){
		    	priceVal = "50";
		    	discountCode = "G";
		    } else if(value.equalsIgnoreCase("$50.00 (G) per color (close registration not available)")) {
		    	priceVal = "50";
		    	discountCode = "G";
		    	priceInclude = "close registration not available";
		    } else if(value.contains("$50.00 (G)")){
		    	priceVal = "50";
		    	discountCode = "G";
		    }
		    existingPriceGrid = gbPriceGridParser.getUpchargePriceGrid("1", priceVal, discountCode, "Additional Colors", false,
					"USD",priceInclude, upchargeName, "Set-up Charge", "Per Order", 1, existingPriceGrid,"","");
		    List<AdditionalColor> listOfAdditionalColor = getAdditionalColors(upchargeName);
		    existingConfiguration.setAdditionalColors(listOfAdditionalColor);
		    existingProduct.setProductConfigurations(existingConfiguration);
		    existingProduct.setPriceGrids(existingPriceGrid);
		return existingProduct;
	}
	public List<Color> getProductColors(String colors){
		  String[] allColors = colors.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		  allColors = CommonUtility.removeDuplicateValues(allColors);
		 // List<String> clrs = new ArrayList<>(new HashSet<>(c))
		  List<Color> listOfColors = new ArrayList<>();
		  Color colorObj = null;
		  for (String colorName : allColors) {
			  colorObj = new Color();
			     String alias = colorName;
			     String colorGroup = GoldbondColorAndImprintMethodMapping.getColorGroup(colorName);
			     if(colorGroup == null){
			    	 colorName = colorName.replaceAll("\\(.*?\\)", "").trim();
			    	 colorGroup = GoldbondColorAndImprintMethodMapping.getColorGroup(colorName);
			     }
			     if(colorGroup == null){
			    	 colorGroup = "Other";
			     }
			  if(colorGroup.contains("Combo")){
				  colorObj = getColorCombo(colorGroup, alias, ":");
			  } else{
				  colorObj.setName(colorGroup);
				  colorObj.setAlias(alias);
			  }
			  listOfColors.add(colorObj);
		}
		return listOfColors;
	}
	public Size getProductSize(String sizeValues){
		Size sizeObj = new Size();
		Dimension dimentionObj = new Dimension();
		Values valuesObj = null;
		List<Values> listOfValues = new ArrayList<>();
		if(sizeValues.contains(";")){
			sizeValues = sizeValues.replaceAll(ApplicationConstants.CONST_DELIMITER_SEMICOLON, 
					                                     ApplicationConstants.CONST_STRING_COMMA_SEP);
		}
		String[] sizess = null;
		if(sizeValues.contains("(") && (sizeValues.contains("Diameter") || sizeValues.contains("diameter"))){
			sizess = new String[]{sizeValues};	
		} else {
			sizess = CommonUtility.getValuesOfArray(sizeValues, ApplicationConstants.CONST_STRING_COMMA_SEP);
		}
		for (String sizeVal : sizess) {
			valuesObj = new Values();
			if(sizeVal.equalsIgnoreCase("29.5 Inches")){
				valuesObj = getOverAllSizeValObj("29.5", "Length", "", "");
			} else if(sizeValues.contains("(") && (sizeValues.contains("Diameter") || sizeValues.contains("diameter"))){
				String[] ss = sizeVal.split("\\(");
				String firstVal = ss[0].replaceAll(pattern_remove_specialSymbols, "").trim();
				String secondVal = ss[1].split(",")[0].replaceAll(pattern_remove_specialSymbols, "").trim();
				String finalSizeVal = "";
				if(firstVal.contains("x")){
					finalSizeVal = firstVal;
					finalSizeVal= finalSizeVal.replaceAll("-", " ");
				} else {
					 finalSizeVal = firstVal + "x" + secondVal;
					finalSizeVal= finalSizeVal.replaceAll("-", " ");
					finalSizeVal= finalSizeVal.replaceAll("  ", " ");
				}
				valuesObj = getOverAllSizeValObj(finalSizeVal, "Height", "Dia", "");
			} else if(sizeVal.equalsIgnoreCase("7- 3/4 or larger size heads")){
				valuesObj = getOverAllSizeValObj("7 3/4", "Length", "Dia", "");
			} else if (sizeVal.contains("H") && sizeVal.contains("L") && sizeVal.contains("D")) {
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				sizeVal= sizeVal.replaceAll("-", " ");
				valuesObj = getOverAllSizeValObj(sizeVal, "Height", "Length", "Depth");
			} else if (sizeVal.contains("H") && sizeVal.contains("W") && sizeVal.contains("D")) {
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				sizeVal= sizeVal.replaceAll("-", " ");
				valuesObj = getOverAllSizeValObj(sizeVal, "Height", "Width", "Depth");
			} else if (sizeVal.contains("L") && sizeVal.contains("W") && sizeVal.contains("D")) {
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				sizeVal= sizeVal.replaceAll("-", " ");
				valuesObj = getOverAllSizeValObj(sizeVal, "Length", "Width", "Depth");
			} else if(sizeVal.contains("L") && sizeVal.contains("W") && sizeVal.contains("H")){
				if(sizeVal.split("x")[0].contains("L")){//4-3/4" L x 4" W x 2-1/8" H
					sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
					sizeVal= sizeVal.replaceAll("-", " ");
					valuesObj = getOverAllSizeValObj(sizeVal, "Length", "Width", "Height");
				} else {//36" H x 12" W x 12" L
					sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
					sizeVal= sizeVal.replaceAll("-", " ");
					valuesObj = getOverAllSizeValObj(sizeVal, "Height", "Width", "Length");
				}
			} else if (sizeVal.contains("H") && sizeVal.contains("W")) {
				if(sizeVal.equalsIgnoreCase("PVPWS: 4\" H x 1-5/8\" W PVPCS: 7\" H x 2-1/2\" W")){
					valuesObj = getOverAllSizeValObj("4x1 5/8", "Height", "Width", "");
					listOfValues.add(valuesObj);
					sizeVal = "7x 2 1/2";
				}
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				sizeVal= sizeVal.replaceAll("-", " ");
				valuesObj = getOverAllSizeValObj(sizeVal, "Height", "Width", "");
			} else if(sizeVal.contains("L") && sizeVal.contains("W")){
				if(sizeVal.contains("Approximately")){
					sizeVal = sizeVal.replaceAll("Approximately", "").trim();
				}
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				sizeVal= sizeVal.replaceAll("-", " ");
				valuesObj = getOverAllSizeValObj(sizeVal, "Length", "Width", "");
			} else if(sizeVal.contains("L") && sizeVal.contains("H")){
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				sizeVal= sizeVal.replaceAll("-", " ");
				valuesObj = getOverAllSizeValObj(sizeVal, "Length", "Height", "");
			} else if (sizeVal.contains("H") && (sizeVal.contains("dia") || sizeVal.contains("Dia"))) {
				if(sizeVal.contains("ia.")){
					sizeVal = sizeVal.replaceAll("ia.", "").trim();
				}
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				sizeVal= sizeVal.replaceAll("-", " ");
				valuesObj = getOverAllSizeValObj(sizeVal, "Height", "Dia", "");
			}  else if (sizeVal.contains("arc")) {
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				sizeVal= sizeVal.replaceAll("-", " ");
				valuesObj = getOverAllSizeValObj(sizeVal, "Arc", "", "");
			} else if (sizeVal.contains("Dia") || sizeVal.contains("dia")) {
				if(sizeVal.contains("Approximately")){
					sizeVal = sizeVal.replaceAll("Approximately", "").trim();
				}
				if(sizeVal.contains("ia.")){
					sizeVal = sizeVal.replaceAll("ia.", "").trim();
				}
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				sizeVal= sizeVal.replaceAll("-", " ");
				valuesObj = getOverAllSizeValObj(sizeVal, "Dia", "", "");
			} else if(sizeVal.contains("Wide")){
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				sizeVal= sizeVal.replaceAll("-", " ");
				valuesObj = getOverAllSizeValObj(sizeVal, "Depth", "Width", "");
			} else if (sizeVal.contains("H")) {
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				sizeVal= sizeVal.replaceAll("-", " ");
				valuesObj = getOverAllSizeValObj(sizeVal, "Height", "", "");
			} else if (sizeVal.contains("L") || sizeVal.contains("to")) {
				if(sizeVal.equalsIgnoreCase("5-3/4\" L (excluding gauge)")){
					sizeVal = "5-3/4";
				}
				if(sizeVal.contains("to")){
					sizeVal = sizeVal.split("to")[1];
				}
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				sizeVal= sizeVal.replaceAll("-", " ");
				valuesObj = getOverAllSizeValObj(sizeVal, "Length", "", "");
			} else {
				sizeVal = sizeVal.replaceAll(pattern_remove_specialSymbols, "");
				sizeVal= sizeVal.replaceAll("-", " ");
				valuesObj = getOverAllSizeValObj(sizeVal, "Length", "Width", "Height");
			}
			listOfValues.add(valuesObj);
		}
		dimentionObj.setValues(listOfValues);
		sizeObj.setDimension(dimentionObj);
		return sizeObj;
	}
	
	public List<ImprintSize> getProductImprintSize(String imprSizeVal){
		String imprintsize_remove_specialSymbols = "[^a-zA-Z0-9.x/: ]";
		List<ImprintSize> listOfImprintSizes = new ArrayList<>();
		ImprintSize imprintSizeObj = null;
		if(imprSizeVal.contains(";")){
			imprSizeVal = imprSizeVal.replaceAll(";", ",");
			if(imprSizeVal.contains("<br>")){
				imprSizeVal = imprSizeVal.replaceAll("<br>", ",");
			}
			if(imprSizeVal.contains("<BR>")){
				imprSizeVal = imprSizeVal.replaceAll("<BR>", ",");
			}
		} else if(imprSizeVal.contains("<br>")){
			imprSizeVal = imprSizeVal.replaceAll("<br>", ",");
		} else if(imprSizeVal.contains("<BR>")){
			imprSizeVal = imprSizeVal.replaceAll("<BR>", ",");
		}
		
		String[] values = imprSizeVal.split(",");
		for (String sizeVal : values) {
			imprintSizeObj = new ImprintSize();
			sizeVal = sizeVal.replaceAll(imprintsize_remove_specialSymbols, "");
			if(sizeVal.contains("<")){
				sizeVal = sizeVal.replaceAll("\\<.*?\\>", "").trim();
			}
			imprintSizeObj.setValue(sizeVal);
			listOfImprintSizes.add(imprintSizeObj);
		}
		return listOfImprintSizes;
	}
	
	public Product getAdditonalLocaAndUpCharge(String value,Product existingProduct){
		ProductConfigurations configuration = existingProduct.getProductConfigurations();
		List<AdditionalLocation> locationList = getAdditionalLocation("Reverse Side Imprint");
		List<PriceGrid> existingPriceGrid = existingProduct.getPriceGrids();
		if(value.equalsIgnoreCase("$0.30 (G) ea.")){
			existingPriceGrid = gbPriceGridParser.getUpchargePriceGrid("1", "0.30", "G", "Additional Location", false,
					"USD","", "Reverse Side Imprint", "Add. Location Charge", "Other", 1, existingPriceGrid,"","");
		} else if(value.contains("$50.00 (G) plus $0.30 (G)")){
			existingPriceGrid = gbPriceGridParser.getUpchargePriceGrid("1___1", "50.0___0.30", "G___G", "Additional Location", false,
					"USD","", "Reverse Side Imprint", "Add. Location Charge", "Other", 1, existingPriceGrid,"","");
		} else if(value.equalsIgnoreCase("$0.30 (G) per cube location, ea.")){
			existingPriceGrid = gbPriceGridParser.getUpchargePriceGrid("1", "0.30", "G", "Additional Location", false,
					"USD", "","Reverse Side Imprint", "Add. Location Charge", "Other", 1, existingPriceGrid,"","");
		} else if(value.equalsIgnoreCase("$50.00 (G), applies to repeat orders")){
			existingPriceGrid = gbPriceGridParser.getUpchargePriceGrid("1", "50.0", "G", "Additional Location", false,
					"USD","", "Reverse Side Imprint", "Add. Location Charge", "Other", 1, existingPriceGrid,"","");
		} else if(value.equalsIgnoreCase("$50.00 (G) per logo, plus $0.80 (G) run charge ea. on two back (black) panels")){
			existingPriceGrid = gbPriceGridParser.getUpchargePriceGrid("1", "50.0", "G", "Additional Location", false,
					"USD","", "Reverse Side Imprint", "Add. Location Charge", "Other", 1, existingPriceGrid,"","");
			existingPriceGrid = gbPriceGridParser.getUpchargePriceGrid("1", "0.80", "G", "Additional Location", false,
					"USD","", "Reverse Side Imprint", "Run Charge", "Other", 1, existingPriceGrid,"","");
		}else if(value.equalsIgnoreCase("$50.00 (G) per location plus add $0.30 (G) ea./location")){
			existingPriceGrid = gbPriceGridParser.getUpchargePriceGrid("1___1", "50.0___0.30", "G___G", "Additional Location", false,
					"USD","", "Reverse Side Imprint", "Add. Location Charge", "Other", 1, existingPriceGrid,"","");
		} else {
			
		}
		existingProduct.setPriceGrids(existingPriceGrid);
		configuration.setAdditionalLocations(locationList);
		return existingProduct;
	}
	public Product getItemAssembledAndUpcharge(String val,Product existingProduct){
		List<PriceGrid> existingPriceGrid = existingProduct.getPriceGrids();
		String description = existingProduct.getDescription();
		ProductConfigurations config = existingProduct.getProductConfigurations();
		List<Option> listOfOptions = config.getOptions();
		if(CollectionUtils.isEmpty(listOfOptions)){
			listOfOptions = new ArrayList<>();
		}
		if(StringUtils.isEmpty(description)){
			description = "";
		}
		if(val.equalsIgnoreCase("Included")){
			existingProduct.setAssembledFlag(true);
		} else if(val.equalsIgnoreCase("Lid assembly is included. Straw shipped within gift box")){
			description = description + val+".";
			existingProduct.setAssembledFlag(true);
		} else if(val.equalsIgnoreCase("Included. Straws shipped bulk unassembled")){
			description = description + "Assembly included. Straws shipped bulk unassembled."+".";
			existingProduct.setAssembledFlag(true);
		} else if(val.equalsIgnoreCase("N/A")){
			existingProduct.setAssembledFlag(false);
		} else if(val.equalsIgnoreCase("$0.30 (G) ea.")){
			listOfOptions = getOptions("Item Assembly", "Optional Item Assembly Available","Product",listOfOptions);
			existingPriceGrid = gbPriceGridParser.getUpchargePriceGrid("1", "0.30", "G", "Product Option", false, "USD","",
					"Optional Item Assembly Available", "Product Option Charge", "Other", 1, existingPriceGrid,
					"Item Assembly","");
			config.setOptions(listOfOptions);
			existingProduct.setProductConfigurations(config);
		} 
		existingProduct.setDescription(description);
		existingProduct.setPriceGrids(existingPriceGrid);
		return existingProduct;
	}
	public List<ProductionTime> getProductionTime(String value){
		List<ProductionTime> listOfProductionTime = new ArrayList<>();
		ProductionTime productionTimeObj = new ProductionTime();
		if(value.equalsIgnoreCase("Varies with golf season, normally 5-7 days after art approval")){
			productionTimeObj.setBusinessDays("5-7");
			 productionTimeObj.setDetails("after art approval");
		} else if(value.equalsIgnoreCase("3 day production standard, same day production available upon request")) {
			productionTimeObj.setBusinessDays("3");
			 productionTimeObj.setDetails("standard");
		} else if(value.equalsIgnoreCase("1-Color: 5-7 Days; 4-Color: 10 Days")){
			/*productionTimeObj.setBusinessDays("5-7");
			 productionTimeObj.setDetails("1-Color: 5-7 Days");
			 listOfProductionTime.add(productionTimeObj);*/
		} else if(value.equalsIgnoreCase("3 Day Standard<br>Same Day Optional")){
			productionTimeObj.setBusinessDays("3");
			 productionTimeObj.setDetails("standard");
		} else if(value.contains("Embroidered shirts ship 7-10 days after art ")){
			productionTimeObj.setBusinessDays("7-10");
			 productionTimeObj.setDetails(value);
		} else if(value.equalsIgnoreCase("3 Day Standard<br>Next Day Optional<br>Orders in by 12:00 pm EST<br>Gold Rush rules apply")){
			productionTimeObj.setBusinessDays("3");
			 productionTimeObj.setDetails("Standard");
		} else if(value.contains("weeks") || value.contains("Weeks")){
			 if(value.equalsIgnoreCase("Two Weeks after art approval for Titleist Wrap") ||
					 value.equalsIgnoreCase("Two weeks for sew out then an additional 2 weeks production time after proof approval")){
				 productionTimeObj.setBusinessDays("10");
				 productionTimeObj.setDetails(value);
			 } else {
				 String productionTime = value.replaceAll("[^0-9-]", "");
				 productionTime = CommonUtility.convertProductionTimeWeekIntoDays(productionTime).trim();
				 productionTimeObj.setBusinessDays(productionTime);
				 productionTimeObj.setDetails(value);
			 } 
		} else if(value.contains("days")){
			if(value.equalsIgnoreCase("21 days")){
				 productionTimeObj.setBusinessDays("21");
				 productionTimeObj.setDetails("");
			} else if(value.equalsIgnoreCase("8 days standard")){
				 productionTimeObj.setBusinessDays("8");
				 productionTimeObj.setDetails("standard");
			} else if(value.equalsIgnoreCase("30 days for normal production, 10 day rush option")){
				 productionTimeObj.setBusinessDays("30");
				 productionTimeObj.setDetails("for normal production");
			} else{
				String productionTime = value.replaceAll("[^0-9-]", "");
				 value = value.replaceAll(productionTime, "");
				 value = value.replaceAll("business days", "");
				 value = value.replaceAll("days", "").trim();
				 productionTimeObj.setBusinessDays(productionTime);
				 productionTimeObj.setDetails(value);
			}
		}
		listOfProductionTime.add(productionTimeObj);
		return listOfProductionTime;
	}
	public Product getRushTime(String value,Product existingProduct){
		ProductConfigurations productConfig = existingProduct.getProductConfigurations();
		List<PriceGrid> priceGrids = existingProduct.getPriceGrids();
		if(CollectionUtils.isEmpty(priceGrids)){
			priceGrids = new ArrayList<>();
		}
		RushTime rushTimeObj = new RushTime();
		rushTimeObj.setAvailable(true);
		RushTimeValue rushTimeValueObj = new RushTimeValue();
		List<RushTimeValue> listOfRushTimeVal = new ArrayList<>();
		String rushTimeVal = "";
		String priceUnitName= "";
		if(value.equalsIgnoreCase("3-5 business days after artwork approval plus in-transit time")){
			rushTimeValueObj.setBusinessDays("3-5");
			rushTimeValueObj.setDetails("after artwork approval plus in-transit time");
		} else if(value.equalsIgnoreCase("$8.75 (G) per dozen golf balls including freight. Order delivered 3rd business day")){
			rushTimeValueObj.setBusinessDays("3");
			rushTimeValueObj.setDetails("Order delivered 3rd business day per dozen golf balls including freight.");
			rushTimeVal = "3";
			priceUnitName = "dozen";
		} else if(value.equalsIgnoreCase("$8.75 (G) per 16 golf balls including freight. Order delivered 4th business day")) {
			rushTimeValueObj.setBusinessDays("4");
			rushTimeValueObj.setDetails("Order delivered 4th business day per 16 golf balls including freight.");
			rushTimeVal = "4";
			priceUnitName = "case";
		} else if(value.equalsIgnoreCase("$8.75 (G) per dozen golf balls including freight. Order delivered 4th business day")){
			rushTimeValueObj.setBusinessDays("4");
			rushTimeValueObj.setDetails("Order delivered 4th business day per dozen golf balls including freight.");
			rushTimeVal = "4";
			priceUnitName = "dozen";
		} else if(value.equalsIgnoreCase("$8.75 (G) per dozen golf balls including freight. Order delivered on 2nd business day for repeat orders and 4th business day for new artwork")){
			rushTimeValueObj.setBusinessDays("2-4");
			rushTimeValueObj.setDetails("Order delivered on 2nd business day for repeat orders and 4th business day for new artwork per dozen golf balls including freight.");
			rushTimeVal = "2-4";
			priceUnitName = "dozen";
		}
		if(!StringUtils.isEmpty(rushTimeVal)){
			rushTimeVal = rushTimeVal +" "+ "business days";
			priceGrids = gbPriceGridParser.getUpchargePriceGrid("1", "8.75", "G", "Rush Service", false, "USD","",
					rushTimeVal, "Rush Service Charge", "Other", 1, priceGrids,
					"",priceUnitName);
		}
		listOfRushTimeVal.add(rushTimeValueObj);
		rushTimeObj.setRushTimeValues(listOfRushTimeVal);
		productConfig.setRushTime(rushTimeObj);
		existingProduct.setProductConfigurations(productConfig);
		existingProduct.setPriceGrids(priceGrids);
		return existingProduct;
	}
	public Product getProductPackaging(String value,Product existingProduct){
		ProductConfigurations productConfig = existingProduct.getProductConfigurations();
		List<PriceGrid> listOfPriceGrid = existingProduct.getPriceGrids();
		if(CollectionUtils.isEmpty(listOfPriceGrid)){
			listOfPriceGrid = new ArrayList<>();
		}
		List<Packaging> listOfPackaging = new ArrayList<>();
		Packaging packObj = new Packaging();
		Packaging packObj1 = new Packaging();
		if(value.equalsIgnoreCase("1 pair/polybag, 10 pairs per polybag, 300 pairs/carton")){
			packObj.setName(value);
		} else if(value.equalsIgnoreCase("Each packed in CC-23 Gift Folder unless otherwise specified. Mailer: $0.55 (G) ea. For special packaging options call customer service for quote")){
			packObj.setName("Mailer");
			listOfPriceGrid = gbPriceGridParser.getUpchargePriceGrid("1", "0.55", "G", "Packaging", false, "USD","",
					"Mailer", "Packaging Charge", "Per Quantity", 1, listOfPriceGrid,
					"","");
		} else if(value.equalsIgnoreCase("Bulk. Polybag add $0.15 (G) ea.")){//
			packObj.setName("Bulk");
			packObj1.setName("Poly Bag");
			listOfPackaging.add(packObj1);
			listOfPriceGrid = gbPriceGridParser.getUpchargePriceGrid("1", "0.15", "G", "Packaging", false, "USD","",
					"Poly Bag", "Packaging Charge", "Per Quantity", 1, listOfPriceGrid,
					"","");
		} else if(value.equalsIgnoreCase("Bulk")){
			packObj.setName(value);
		} else if(value.equalsIgnoreCase("Gift boxed")) {
			packObj.setName("Gift Boxes");
		} else if(value.equalsIgnoreCase("Bulk (standard); Polybagged (optional $0.44 (G) ea.)")){
			packObj.setName("Bulk");
			packObj1.setName("Individual Poly Bag");
			listOfPackaging.add(packObj1);
			listOfPriceGrid = gbPriceGridParser.getUpchargePriceGrid("1", "0.44", "G", "Packaging", false, "USD","",
					"Individual Poly Bag", "Packaging Charge", "Per Quantity", 1, listOfPriceGrid,
					"","");
		} else if(value.contains("Individually")){
			 if(value.contains("Bulk")){
				 packObj.setName("Bulk");
			 }
			 if(value.contains("polybagged") || value.contains("poly-bagged") || value.contains("packaged")){
				 packObj1.setName("Individual Poly Bag");
				 listOfPackaging.add(packObj1);
			 } else if(value.contains("gift box")){
				 packObj1.setName("Gift Boxes");
				 listOfPackaging.add(packObj1);
			 } else if(value.contains("shrink-wrapped")){
				 packObj1.setName("Shrink Wrap");
				 listOfPackaging.add(packObj1);
			 } /*else{
				 packObj1.setName("Individual Poly Bag");
				 listOfPackaging.add(packObj1);
			 }*/
		} else {
			if(value.contains("Bulk")){
				packObj.setName("Bulk");
			}
		}
		listOfPackaging.add(packObj);
		productConfig.setPackaging(listOfPackaging);
		existingProduct.setProductConfigurations(productConfig);
		existingProduct.setPriceGrids(listOfPriceGrid);
		return existingProduct;
	}
	public Product getpencilSharpForOption(String value,Product existingProduct){
		ProductConfigurations productConfig = existingProduct.getProductConfigurations();
		List<PriceGrid> priceGrids = existingProduct.getPriceGrids();
		if(CollectionUtils.isEmpty(priceGrids)){
			priceGrids = new ArrayList<>();
		}
		List<Option> listOfOptions = productConfig.getOptions();
		if(CollectionUtils.isEmpty(listOfOptions)){
			listOfOptions = new ArrayList<>();
		}
		listOfOptions = getOptions("Pencil Sharpening", "Optional Pencil Sharpening Available", "Product",
				listOfOptions);
		priceGrids = gbPriceGridParser.getUpchargePriceGrid("1___1", "0.02___20.00", "G___G", "Product Option", false, "USD","",
				"Optional Pencil Sharpening Available", "Product Option Charge", "Other", 1, priceGrids,
				"Pencil Sharpening","");
		productConfig.setOptions(listOfOptions);
		existingProduct.setProductConfigurations(productConfig);
		existingProduct.setPriceGrids(priceGrids);
		return existingProduct;
	}
	public List<FOBPoint> getFobPoint(final String  value,String authToken){
		List<FOBPoint> listOfFobPoint = new ArrayList<>();
		if(lookupFobPoints == null){
			lookupFobPoints = lookupServiceData.getFobPoints(authToken);
		}
		String finalFobValue = lookupFobPoints.stream().filter(fobValue -> fobValue.contains(value))
				                              .collect(Collectors.joining());
		if(!StringUtils.isEmpty(finalFobValue)){
			FOBPoint fobPointObj = new FOBPoint();
			fobPointObj.setName(finalFobValue);
			listOfFobPoint.add(fobPointObj);
		}
		return listOfFobPoint;
	} 
	private List<AdditionalLocation> getAdditionalLocation(String locationVal){
		List<AdditionalLocation> locationList = new ArrayList<>();
		AdditionalLocation additionalLocaObj = new AdditionalLocation();
		additionalLocaObj.setName(locationVal);
		locationList.add(additionalLocaObj);
		return locationList;
	}
	private Value getValueObj(String value,String attribute,String unit){
		Value valueObj = new Value();
		valueObj.setAttribute(attribute);
		valueObj.setUnit(unit);
		valueObj.setValue(value);
		return valueObj;
	}
	private Values getOverAllSizeValObj(String val,String unit1,String unit2,String unit3){
		//Overall Size: 23.5" x 23.5"
		String[] values = null;
		if(val.contains("x")){
			values = val.split("x");
		} else {
			values = val.split("X");
		}
		
		Value valObj1 = null;
		Value valObj2 = null;
		Value valObj3 = null;
		List<Value> listOfValue = new ArrayList<>();
		if(values.length == ApplicationConstants.CONST_INT_VALUE_ONE){
			 valObj1 = getValueObj(values[0].trim(), unit1, "in");
			  listOfValue.add(valObj1);
		} else if(values.length == ApplicationConstants.CONST_INT_VALUE_TWO){
			 valObj1 = getValueObj(values[0].trim(), unit1, "in");
			 valObj2 = getValueObj(values[1].trim(), unit2, "in");
			 listOfValue.add(valObj1);
		     listOfValue.add(valObj2);
		} else if(values.length == ApplicationConstants.CONST_INT_VALUE_THREE){
			 valObj1 = getValueObj(values[0].trim(), unit1, "in");
			 valObj2 = getValueObj(values[1].trim(),unit2, "in");
			 valObj3 = getValueObj(values[2].trim(), unit3, "in");
			 listOfValue.add(valObj1);
		     listOfValue.add(valObj2);
		     listOfValue.add(valObj3);
		}
		 Values valuesObj = new Values(); 
		 valuesObj.setValue(listOfValue);
		 return valuesObj;
	}
	public List<Artwork> getProductArtwork(String value,List<Artwork> listOfArtwork){
		
		if(CollectionUtils.isEmpty(listOfArtwork)){
			listOfArtwork = new ArrayList<>();
			Artwork artworkObj = new Artwork();
			artworkObj.setValue(value);
			artworkObj.setComments("");
			listOfArtwork.add(artworkObj);
		} else{
			if(!listOfArtwork.stream().anyMatch(artwork -> "PRE-PRODUCTION PROOF".equalsIgnoreCase(artwork.getValue()))){
				Artwork artworkObj = new Artwork();
				artworkObj.setValue(value);
				artworkObj.setComments("");
				listOfArtwork.add(artworkObj);
			}
		}
		return listOfArtwork;
	}
	private List<AdditionalColor> getAdditionalColors(String value){
		    List<AdditionalColor> listOfAdditionalColors = new ArrayList<>();
		    AdditionalColor additionalColorObj = new AdditionalColor();
		    additionalColorObj.setName(value);
		    listOfAdditionalColors.add(additionalColorObj);
		    return listOfAdditionalColors;
	}
	private Color getColorCombo(String comboValue,String alias,String separator){
		  //Medium White:Combo:Medium Pink
		Color colorObj = new Color();
		List<Combo> listOfCombos = new ArrayList<>();
		Combo comboObj = new Combo();
		String[] combos = comboValue.split(separator);
		comboObj.setName(combos[2]);
		comboObj.setType("secondary");
		listOfCombos.add(comboObj);
		colorObj.setName(combos[0]);
		colorObj.setAlias(alias);
		colorObj.setCombos(listOfCombos);
		  return colorObj;
	}
	private List<Option> getOptions(String optionName,String optionVal,String optionType,List<Option> listOfOptions){
		List<OptionValue> optionValList = new ArrayList<>();
		OptionValue optionValObj = new OptionValue();
		Option optionObj = new Option();
		optionObj.setName(optionName);
		optionValObj.setValue(optionVal);
		optionValList.add(optionValObj);
		optionObj.setValues(optionValList);
		optionObj.setAdditionalInformation("");
		optionObj.setRequiredForOrder(false);
		optionObj.setCanOnlyOrderOne(false);
		optionObj.setOptionType(optionType);
		listOfOptions.add(optionObj);
		return listOfOptions;
	}
	public Product getProductMaterial(String value,Product existingProduct){
		try{
		ProductConfigurations config = existingProduct.getProductConfigurations();
		String additionalInfo = existingProduct.getAdditionalProductInfo();
		if(StringUtils.isEmpty(additionalInfo)){
			additionalInfo = "";
		}
		List<Material> listOfMaterial = new ArrayList<>();
		Material materialObj = new Material();
		Combo comboObj = null;
		if(value.equalsIgnoreCase("Stoneware")){
			materialObj.setName("Stone");
			materialObj.setAlias(value);
		} else if(value.equalsIgnoreCase("Base: natural sponge rubber; Top: polyester fabric")){
			//Rubber:Combo:Polyester=Natural Sponge Rubber & Polyester Fabric
			comboObj = new Combo();
			comboObj.setName("Polyester");
			materialObj.setName("Rubber");
			materialObj.setAlias("Natural Sponge Rubber & Polyester Fabric");
			materialObj.setCombo(comboObj);
		} else if(value.equalsIgnoreCase("Soft surface rubber") || 
				 value.equalsIgnoreCase("Soft surface recycled rubber") || 
				 value.equalsIgnoreCase("Rubber with adhesive backing")){
			materialObj.setName("Rubber");
			materialObj.setAlias(value);
		} else if(value.equalsIgnoreCase("100% Microfiber Velour Front and 100% Cotton Terry Loops on back. Total fiber content: 70% cotton/30% microfiber polyester") ||
				  value.equalsIgnoreCase("100% Microfiber Velour Front and 100% Cotton Terry Loops on back. Total fiber content 70% cotton/30% microfiber polyester")){
			//Other Fabric:Combo:Cotton=Microfiber Velour & Cotton Loops
			comboObj = new Combo();
			comboObj.setName("Cotton");
			materialObj.setName("Other Fabric");
			materialObj.setAlias("Microfiber Velour & Cotton Loops");
			materialObj.setCombo(comboObj);
		} else if(value.contains("12Pack-Outer: 210D polyester; Inner-2mm foam; Liner: 12c PEVA; 1518PBW: 100% cotton; 234T: bamboo; DVF: ABS; FC-02: foam")
				  || value.contains("Covers: surlyn (DuPont) ionomers; Core: polybutadiene-based rubber compound; Box: cardboard")){
			additionalInfo = CommonUtility.appendStrings(additionalInfo, value, " ");
		} else if(value.equalsIgnoreCase("100% sheared heavyweight cotton") || 
				  value.equalsIgnoreCase("100% cotton")){
			materialObj.setName("Cotton");
			materialObj.setAlias(value);
		} else if(value.equalsIgnoreCase("95% polyester/5% polyamide") || value.equalsIgnoreCase("85% polyester; 15% polyamide") 
				|| value.equalsIgnoreCase("85% polyester, 15% polyamide")){
			List<BlendMaterial> listOfBlendMatrls = null;
			materialObj.setName("BLEND");
			StringBuilder blendVal = new StringBuilder();
			if(value.contains("/")){
				blendVal.append("POLYESTER__95").append(",").append("OTHER FABRIC__5");
				 listOfBlendMatrls = getBlendMaterials(blendVal.toString());
				materialObj.setAlias("Blend: Polyester/Polyamide (95/5)");
			} else{
				blendVal.append("POLYESTER__85").append(",").append("OTHER FABRIC__15");
				 listOfBlendMatrls = getBlendMaterials(blendVal.toString());
				materialObj.setAlias("Blend: Polyester/Polyamide (85/15)");
			}
			materialObj.setBlendMaterials(listOfBlendMatrls);
		} else if(value.equalsIgnoreCase("Bamboo wood") || value.equalsIgnoreCase("Pine wood")){
			materialObj.setName("Wood");
			materialObj.setAlias(value);
		} else if(value.equalsIgnoreCase("60 lbs Stock")){
			
		} else if(value.equalsIgnoreCase("600 Denier Polyester")){
			materialObj.setName("Polyester");
			materialObj.setAlias(value);
		} else if(value.equalsIgnoreCase("Zinc aluminum with steel ball marker")){
			materialObj.setName("Aluminum");
			materialObj.setAlias(value);
		} else {
			if(value.contains(",") || value.contains(";")|| value.contains(":")){
				additionalInfo = CommonUtility.appendStrings(additionalInfo, value, " ");
			} else {
				materialObj.setAlias(value);
				if(lookupServiceData.isMaterial(value.toUpperCase())){
					materialObj.setName(value);
				} else {
					materialObj.setName("Other");
				}
			}
		}
		listOfMaterial.add(materialObj);
		config.setMaterials(listOfMaterial);
		existingProduct.setProductConfigurations(config);
		existingProduct.setAdditionalProductInfo(additionalInfo);
	 } catch(Exception exce){
	    _LOGGER.error("unable to parser to material: "+exce.getMessage());
	  }
		return existingProduct;
	}
	private List<BlendMaterial> getBlendMaterials(String belndVal){
		List<BlendMaterial> listOfBlendMaterials= new ArrayList<>();
		BlendMaterial blendMatrlsObj = null;
		String[] blendVals = CommonUtility.getValuesOfArray(belndVal, ",");
		for (String val : blendVals) {
			String[] vals = CommonUtility.getValuesOfArray(val, "__");
			blendMatrlsObj = new BlendMaterial();
			blendMatrlsObj.setName(vals[0]);
			blendMatrlsObj.setPercentage(vals[1]);
			listOfBlendMaterials.add(blendMatrlsObj);
		}
		return listOfBlendMaterials;
	}
	public ImprintColor getImprintColors(String imprColor){
		ImprintColor imprintColorObj = new ImprintColor();
		List<ImprintColorValue> listOfImprColorValues = new ArrayList<>();
		ImprintColorValue imprintColorValueObj = null;
		String[] imprColors = CommonUtility.getValuesOfArray(imprColor, ",");
		for (String imprColorName : imprColors) {
			if(StringUtils.isEmpty(imprColorName)){
				continue;
			}
			imprintColorValueObj = new ImprintColorValue();
			imprintColorValueObj.setName(imprColorName);
			listOfImprColorValues.add(imprintColorValueObj);
		}
		imprintColorObj.setType("COLR");
		imprintColorObj.setValues(listOfImprColorValues);
		return imprintColorObj;
	}
	public List<Image> getImages(String imgVal){
		List<Image> listOfImages = new ArrayList<>();
		Image imageObj = null;
		String[] imgVals = CommonUtility.getValuesOfArray(imgVal, ",");
		int imageRank = 1;
		for (String imgUrl : imgVals) {
			if(StringUtils.isEmpty(imgUrl)){
				continue;
			}
			imageObj = new Image();
			imageObj.setImageURL(imgUrl);
			imageObj.setIsvirtualized(false);
			imageObj.setRank(imageRank);
			imageObj.setDescription("");
			if(imageRank == 1){
				imageObj.setIsPrimary(true);
			} else {
				imageObj.setIsPrimary(false);
			}
			listOfImages.add(imageObj);
			imageRank++;
		}
		return listOfImages;
	}
	public Product getImprintMethods(String imprMethodVal,Product existingProduct){
		ProductConfigurations config = existingProduct.getProductConfigurations();
		String existingAdditionalImprintInfo = existingProduct.getAdditionalImprintInfo();
		if(StringUtils.isEmpty(existingAdditionalImprintInfo)){
			existingAdditionalImprintInfo = "";
		}
		String additionalImprintInfo = "";
		List<ImprintMethod> listOfImprintMethods = new ArrayList<>();
		ImprintMethod imprintMethodObj = null;
		List<String> tempImprintMethodVals = new ArrayList<>();
		if(imprMethodVal.equalsIgnoreCase("Laser engraved (Black, Blue, Lime, Red); Screen Printed (Silver)")){
			additionalImprintInfo = "";
			tempImprintMethodVals = Arrays.asList("Laser Engraved:Laser Engraved","Silkscreen:Screen Printed");
		} else if(imprMethodVal.equalsIgnoreCase("Laser engraving standard for colors (Pad printing standard on silver and white pens only)")) {
			additionalImprintInfo = "Laser engraving standard for colors. Pad printing standard on silver and white pens only.";
			tempImprintMethodVals = Arrays.asList("Laser Engraved:Laser Engraving","Pad Print:Pad Printing");
		} else if(imprMethodVal.equalsIgnoreCase("Blank standard. Embroidery optional up to 6,000 stiches")){
			additionalImprintInfo = "Blank standard. Embroidery optional up to 6000 stitches.";
			tempImprintMethodVals = Arrays.asList("Unimprinted:Unimprinted","Embroidered:Embroidery");
		} else if(imprMethodVal.equalsIgnoreCase("Screen print on case, car charger and wallet. Laser on LED pen and full color on mobile charger")){
			additionalImprintInfo = "Screen print on case, car charger and wallet. Laser on LED pen and full color on mobile charger.";
			tempImprintMethodVals = Arrays.asList("Silkscreen:Screen Print","Laser Engraved:Laser Engraved","Full Color:Full Color");
		} else if(imprMethodVal.equalsIgnoreCase("Screen print on case and car charger. Full color on mobile charger and wall charger")){
			additionalImprintInfo = imprMethodVal;
		} else if(imprMethodVal.equalsIgnoreCase("Full color heat transfer with 30 day production, 1 or 2 color heat transfer with rush production")){
			tempImprintMethodVals = Arrays.asList("Full Color:Full Color","Heat Transfer:Heat Transfer");
		} else if(imprMethodVal.equalsIgnoreCase("Embroidery up to 6,000 stitches")){
			tempImprintMethodVals = Arrays.asList("Embroidered:Embroidery");
		} else if(imprMethodVal.equalsIgnoreCase("Screen/Pad printed")){
			tempImprintMethodVals = Arrays.asList("Other:Screen/Pad printed");
		} else if(imprMethodVal.equalsIgnoreCase("Pad printed on handle standard - laser engraved available on blade")){
			tempImprintMethodVals = Arrays.asList("Pad Print:Pad printed","Laser Engraved:Laser Engraved");
		} else if(imprMethodVal.equalsIgnoreCase("4-color process print label or 1-color direct imprint")){
			tempImprintMethodVals = Arrays.asList("Full Color:4-color process","Other:direct imprint");
		} else if(imprMethodVal.equalsIgnoreCase("Full color imprint with protective dome")){
			tempImprintMethodVals = Arrays.asList("Full Color:Full color");
		}
		else {
			if(imprMethodVal.equalsIgnoreCase("Pad printed on handle standard; laser engraved available on blade")){
				additionalImprintInfo = imprMethodVal.replaceAll(";", ",");
			} else if(imprMethodVal.equalsIgnoreCase("1-Color screen printed / Multi-color pad printed / Laser Engraving")){
				additionalImprintInfo = "1-Color Screen Print, Multi-Color Pad Print";
			} else if(imprMethodVal.equalsIgnoreCase("1-Color screen printed / Laser Engraving")){
				additionalImprintInfo = "1-Color Screen Print";
			}
			if(imprMethodVal.contains("/")){
				imprMethodVal = imprMethodVal.replaceAll("/", ",");
			}
			if(imprMethodVal.contains(";")){
				imprMethodVal = imprMethodVal.replaceAll(";", ",");
			}
			String[] imprMetodVals = CommonUtility.getValuesOfArray(imprMethodVal, ",");
			String imprintMethodType = "";
			String imprintMethodAlias = "";
			for (String imprMethodName : imprMetodVals) {
				 imprintMethodType = "";
				 imprintMethodAlias = "";
				imprintMethodObj = new ImprintMethod();
				if(imprMethodName.contains("Dye sublimation")){
					imprintMethodType = "Sublimation";
					imprintMethodAlias = "Dye sublimation";
				} else if(imprMethodName.equalsIgnoreCase("N/A")){
					continue;
				} else if(imprMethodName.contains("4-color")){
					imprintMethodType = "Full Color";
					imprintMethodAlias = "4-color Process";
					boolean isImprintMethodPresent = listOfImprintMethods.stream().anyMatch(imprMetho  -> imprMetho.getAlias().contains("4-color Process"));
		    		if(isImprintMethodPresent){
		    			continue;
		    		}
				} else if(imprMethodName.contains("Screen Printed") ||
						      imprMethodName.contains("screen printed")) {
					imprintMethodType = "Silkscreen";
					imprintMethodAlias = "Screen Printed";
				} else if(imprMethodName.contains("pad printed") ||
						  imprMethodName.contains("Pad Printed")){
					imprintMethodType = "Pad Print";
					imprintMethodAlias = "Pad Printed";
				} else if(imprMethodName.contains("Faux Laser")){
					imprintMethodType = "Laser Engraved";
					imprintMethodAlias = "Faux Laser";
				} else if(imprMethodName.contains("Laser Engraving") || 
						    imprMethodName.contains("Laser engraving")) {
					imprintMethodType = "Laser Engraved";
					imprintMethodAlias = "Laser engraving";
				} else if(imprMethodName.contains("ColorfinityHD")){
					imprintMethodType = "Other";
					imprintMethodAlias = "ColorfinityHD";
				} else if(imprMethodName.equalsIgnoreCase("Blank")){
					imprintMethodType = "Unimprinted";
					imprintMethodAlias = "Unimprinted";
				}
				else {
					if(lookupServiceData.isImprintMethod(imprMethodName.toUpperCase())){
						imprintMethodType = imprMethodName;
						imprintMethodAlias = imprMethodName;
					} else {
							imprintMethodType = "Other";
							imprintMethodAlias = imprMethodName;
					}
				}
				imprintMethodObj.setType(imprintMethodType);
				imprintMethodObj.setAlias(imprintMethodAlias);
				listOfImprintMethods.add(imprintMethodObj);
			}

		}
		if(!CollectionUtils.isEmpty(tempImprintMethodVals)){
			listOfImprintMethods = getImprintMethods(tempImprintMethodVals);
		}
		config.setImprintMethods(listOfImprintMethods);
		if(!StringUtils.isEmpty(additionalImprintInfo)){
			if(StringUtils.isEmpty(existingAdditionalImprintInfo)){
				existingAdditionalImprintInfo = additionalImprintInfo;
			} else {
				existingAdditionalImprintInfo = CommonUtility.appendStrings(existingAdditionalImprintInfo,
						additionalImprintInfo, " ");
			}
			
		}
		existingProduct.setAdditionalImprintInfo(existingAdditionalImprintInfo);
		existingProduct.setProductConfigurations(config);
		return existingProduct;
	}
    private List<ImprintMethod> getImprintMethods(List<String> imprintMethodVals){
    	List<ImprintMethod> listOfImprintMethods = new ArrayList<>();
    	ImprintMethod imprintMethodObj = null;
    	for (String imprintMethodVal : imprintMethodVals) {
			String[] imprVals = CommonUtility.getValuesOfArray(imprintMethodVal, ":");
			imprintMethodObj = new ImprintMethod();
			imprintMethodObj.setType(imprVals[0]);
			imprintMethodObj.setAlias(imprVals[1]);
			listOfImprintMethods.add(imprintMethodObj);
		}
    	return listOfImprintMethods;
    }
    public Product getMultipleColorUpcharge(String value,Product existingProduct){
    	ProductConfigurations productConfig = existingProduct.getProductConfigurations();
    	List<PriceGrid> priceGrid = existingProduct.getPriceGrids();
    	List<AdditionalColor> additinalColorList = productConfig.getAdditionalColors();
    	if(CollectionUtils.isEmpty(additinalColorList)){
    		 additinalColorList = getAdditionalColors("additional color available");
    		
    	} else {
    		if(!additinalColorList.stream().anyMatch(color -> "additional color available".equalsIgnoreCase(color.getName()))){
    			AdditionalColor additinalColorObj = new AdditionalColor();
    			additinalColorObj.setName("additional color available");
    			additinalColorList.add(additinalColorObj);
    		}
    	}
    	productConfig.setAdditionalColors(additinalColorList);
    	String priceVal = "";
    	if(value.contains("$0.30 (G)")|| value.contains("$.30 (G)")){
    		priceVal = "0.30";
    	} else if(value.contains("$0.35 (G)")){
    		priceVal = "0.35";
    	} else if(value.contains("$1.00 (G)")){
    		priceVal = "1.00";
    	}
    	if(!StringUtils.isEmpty(priceVal)){
    		priceGrid = gbPriceGridParser.getUpchargePriceGrid("1", priceVal, "G", "Additional Colors", false,
					"USD","", "additional color available", "Run Charge", "Per Quantity", 1, priceGrid,"","");
    	}
    	existingProduct.setPriceGrids(priceGrid);
    	return existingProduct;
    }
    public Product getSetupChargeForImprintMethod(String priceValue,Product existingProduct){
    	ProductConfigurations productConfig = existingProduct.getProductConfigurations();
    	List<PriceGrid> priceGrid = existingProduct.getPriceGrids();
    	List<AdditionalColor> listOfAdditionalColor = null;
    	/*if(CollectionUtils.isEmpty(productConfig.getAdditionalColors())){
    		 listOfAdditionalColor = getAdditionalColors("additional color available");
    		productConfig.setAdditionalColors(listOfAdditionalColor);
    	} else {
    		 listOfAdditionalColor = productConfig.getAdditionalColors();
    		boolean isAddColorAvail = listOfAdditionalColor.stream().anyMatch(addColor  -> addColor.getName().contains("additional color available"));
    		if(!isAddColorAvail){
    			listOfAdditionalColor = getAdditionalColors("additional color available");
        		productConfig.setAdditionalColors(listOfAdditionalColor);
    		}
    	}*/
    	String priceVal = "50";
    	String desc = "";
    	if(priceValue.contains("G")){
    		desc = "G";
    	} else if(priceValue.contains("A")){
    		desc = "A";
    	} else if(priceValue.contains("C")){
    		desc = "C";
    	}
    	priceValue = priceValue.replaceAll("[^0-9.]", "").trim();
    	String imprintMethodVals = getImprintMethodAlias(productConfig.getImprintMethods());
    	if(!StringUtils.isEmpty(priceVal) && !StringUtils.isEmpty(imprintMethodVals)){
    		priceGrid = gbPriceGridParser.getUpchargePriceGrid("1", priceValue, desc, "Imprint Method", false,
					"USD","", imprintMethodVals, "Set-up Charge", "Per Quantity", 1, priceGrid,"","");
    	}
    	existingProduct.setPriceGrids(priceGrid);
    	return existingProduct;
    }
    private String getImprintMethodAlias(List<ImprintMethod> listOfImprintMethod){
		String imprintMethodAlias = listOfImprintMethod.stream().map(ImprintMethod::getAlias)
				.collect(Collectors.joining(","));
		return imprintMethodAlias;
	}
	public GoldbondPriceGridParser getGbPriceGridParser() {
		return gbPriceGridParser;
	}
	public void setGbPriceGridParser(GoldbondPriceGridParser gbPriceGridParser) {
		this.gbPriceGridParser = gbPriceGridParser;
	}
	public LookupServiceData getLookupServiceData() {
		return lookupServiceData;
	}
	public void setLookupServiceData(LookupServiceData lookupServiceData) {
		this.lookupServiceData = lookupServiceData;
	}

	


	
}
