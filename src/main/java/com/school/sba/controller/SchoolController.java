package com.school.sba.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.entity.School;
import com.school.sba.requestDTO.SchoolRequest;
import com.school.sba.responseDTO.SchoolResponce;
import com.school.sba.service.SchoolService;
import com.school.sba.util.ResponseStructure;

@RestController
public class SchoolController{
	@Autowired
	private SchoolService schoolService;

	@PostMapping("/users/{userId}/schools")
	public ResponseEntity<ResponseStructure<SchoolResponce>> adminCreatesSchool(@PathVariable int userId, @RequestBody SchoolRequest schoolRequest) {
		return schoolService.adminCreatesSchool(userId, schoolRequest);
	}
//	@GetMapping("/schools/{schoolId}")
//	public ResponseEntity<ResponseStructure<School>> findSchool(@PathVariable int schoolId) {
//		return schoolService.findSchool(schoolId);
//	}
//	@PutMapping("/schools/{schoolId}")
//	public ResponseEntity<ResponseStructure<School>> updateSchool(@PathVariable int schoolId, @RequestBody School school) {
//		return schoolService.updateSchool(schoolId, school);
//	}
//	@DeleteMapping("/schools/{schoolId}")
//	public ResponseEntity<ResponseStructure<School>> deleteSchool(@PathVariable int schoolId) {
//		return schoolService.deleteSchool(schoolId);
//	}
//	@GetMapping("/schools/findAll")
//	public ResponseEntity<ResponseStructure<List<School>>> findAll() {
//		return schoolService.findAll();
//	}

}