package com.a4tech.core.model;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ExcelFileValidator implements Validator{

	public boolean supports(Class<?> clazz) {
		
		return FileBean.class.isAssignableFrom(clazz);
	}

	public void validate(Object obj, Errors error){
		
		FileBean file	= (FileBean) obj;
		if(file.getFile() != null && file.getFile().getSize() != 0){
			error.rejectValue("file", "missing.file");
		}
	}

}
