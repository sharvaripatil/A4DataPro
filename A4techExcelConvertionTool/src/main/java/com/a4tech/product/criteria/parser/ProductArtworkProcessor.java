package com.a4tech.product.criteria.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.a4tech.product.model.Artwork;
import com.a4tech.util.ApplicationConstants;

public class ProductArtworkProcessor {

	private Logger              _LOGGER              = Logger.getLogger(getClass());
	
	public List<Artwork> getArtworkCriteria(String artwork){
		List<Artwork> artworkList=new  ArrayList<Artwork>();
		try{
		
		
		Artwork artObj=null;
		String artArr[]= artwork.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		
	     for (String tempArt : artArr) {
			artObj=new Artwork();
			String tempValues[]=tempArt.split(ApplicationConstants.CONST_DELIMITER_COLON);
			
			if(tempValues.length==2){
				artObj.setValue(tempValues[0]);
				artObj.setComments(tempValues[1]);
			}else if(tempValues.length==1){
				artObj.setValue(tempValues[0]);
				artObj.setComments(ApplicationConstants.CONST_STRING_EMPTY);
			}
			
			artworkList.add(artObj);
			
		}

           }catch(Exception e){
        	   _LOGGER.error("Error while processing Artwork :"+e.getMessage());
        	   return new  ArrayList<Artwork>();
	   	
	   }
		return artworkList;
		
	}
}
