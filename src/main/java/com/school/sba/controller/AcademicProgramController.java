package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestDTO.AcademicProgramRequest;
import com.school.sba.responseDTO.AcademicProgramResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.util.ResponseStructure;

@RestController
public class AcademicProgramController {
	@Autowired
	private AcademicProgramService academicProgramService;
	@PostMapping("/schools/{schoolId}/academicPrograms")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addsAcademicProgram(int schoolId, @RequestBody AcademicProgramRequest academicProgramRequest){
		System.out.println(schoolId);
		return academicProgramService.addsAcademicProgram(schoolId, academicProgramRequest);
	}
}
