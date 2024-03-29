package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestDTO.SchoolRequest;
import com.school.sba.responseDTO.SchoolResponce;
import com.school.sba.service.SchoolService;
import com.school.sba.util.ResponseStructure;

@RestController
public class SchoolController{
	@Autowired
	private SchoolService schoolService;
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/users/schools")
	public ResponseEntity<ResponseStructure<SchoolResponce>> adminCreatesSchool(@RequestBody SchoolRequest schoolRequest) {
		return schoolService.adminCreatesSchool(schoolRequest);
	}
	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/users/schools/{schoolId}")
	public ResponseEntity<ResponseStructure<SchoolResponce>> deleteById(@PathVariable int schoolId){
		return schoolService.deleteById(schoolId);
	}
}