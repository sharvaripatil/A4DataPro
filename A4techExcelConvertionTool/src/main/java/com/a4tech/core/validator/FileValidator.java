package com.a4tech.core.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.a4tech.core.model.FileBean;
@Component
public class FileValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		
		return FileBean.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		// TODO Auto-generated method stub
		FileBean file = (FileBean) obj;
		if(file.getFile()!=null){
            if (file.getFile().getSize() == 0) {
                errors.rejectValue("file", "missing.file");
            }
        }
		
	}

}
