package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestDTO.SchoolRequest;
import com.school.sba.responseDTO.SchoolResponce;
import com.school.sba.util.ResponseStructure;

public interface SchoolService {
	
	public ResponseEntity<ResponseStructure<SchoolResponce>> adminCreatesSchool(SchoolRequest schoolRequest);

	public ResponseEntity<ResponseStructure<SchoolResponce>> deleteById(int schoolId);
	
}