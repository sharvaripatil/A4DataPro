package com.a4tech.core.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
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
		FileBean bean = (FileBean)obj;
		ValidationUtils.rejectIfEmpty(errors, "asiNumber", "error.asiNumber");
		ValidationUtils.rejectIfEmpty(errors, "userName", "error.userName");
		ValidationUtils.rejectIfEmpty(errors, "password", "error.password");
		
		if(bean.getFile().getSize() ==0){
			errors.rejectValue("file", "error.upload.file");
		}
		//ValidationUtils.rejectIfEmpty(errors, "file",     "error.upload.file");
		/*if(file.getFile()!=null){
            if (file.getFile().getSize() == 0) {
                errors.rejectValue("file", "missing.file");
            }
        }
		if(file.getAsiNumber().isEmpty()){
			errors.rejectValue("asiNumber", "error.asiNumber");
		}*/
		
	}

}
