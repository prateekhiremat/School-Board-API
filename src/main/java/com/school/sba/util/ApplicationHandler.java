package com.school.sba.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.school.sba.exception.SchoolNotFoundByIdException;

@RestControllerAdvice
public class ApplicationHandler {
@ExceptionHandler
	public ResponseEntity<ResponseStructure<String>> schoolNotFoundById(SchoolNotFoundByIdException ex){
		
	ResponseStructure<String> responseStructure = new ResponseStructure<>();
	responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
	responseStructure.setMessage(ex.getMessage());
	responseStructure.setData("Student Object with the given Id doesn't exists!!");
	
	return new ResponseEntity<ResponseStructure<String>>(responseStructure, HttpStatus.NOT_FOUND);
	}
}