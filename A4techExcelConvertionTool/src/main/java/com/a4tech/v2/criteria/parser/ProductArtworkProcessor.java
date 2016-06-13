package com.a4tech.v2.criteria.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.smartcardio.ATR;

import org.apache.log4j.Logger;

import com.a4tech.util.ApplicationConstants;
import com.a4tech.v2.core.model.Artwork;
import com.a4tech.v2.core.model.ImprintColorValue;

public class ProductArtworkProcessor {

	private Logger              _LOGGER              = Logger.getLogger(getClass());
	
	public List<Artwork> getArtworkCriteria(String artwork){
		List<Artwork> artworkList=new  ArrayList<Artwork>();
		Set<Artwork> artworkOfSet =  new HashSet<Artwork>();
		try{
		Artwork artObj=null;
		String artArr[]= artwork.split("\\|");
		
	     for (String tempArt : artArr) {
			artObj=new Artwork();
			String tempValues[]=tempArt.split(ApplicationConstants.CONST_DELIMITER_COLON);
			
			if(tempValues.length==2){
				artObj.setValue(tempValues[0].trim());
				artObj.setComments(tempValues[1].trim());
			}else if(tempValues.length==1){
				artObj.setValue(tempValues[0].trim());
				artObj.setComments(ApplicationConstants.CONST_STRING_EMPTY);
			}
			artworkOfSet.add(artObj);
			
		}
			artworkList.addAll(artworkOfSet);
           }catch(Exception e){
        	   _LOGGER.error("Error while processing Artwork :"+e.getMessage());
        	   return null;
	   	
	   }
		return artworkList;
		
	}
}
