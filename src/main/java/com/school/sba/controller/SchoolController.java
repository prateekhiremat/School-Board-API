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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.entity.School;
import com.school.sba.service.SchoolService;
import com.school.sba.util.ResponseStructure;

@RestController
@RequestMapping("/schools")
public class SchoolController{
	@Autowired
	private SchoolService service;

	@PostMapping
	public ResponseEntity<ResponseStructure<School>> addSchool(@RequestBody School school) {
		return service.addSchool(school);
	}
	@GetMapping("/{schoolId}")
	public ResponseEntity<ResponseStructure<School>> findSchool(@PathVariable int schoolId) {
		return service.findSchool(schoolId);
	}
	@PutMapping("/{schoolId}")
	public ResponseEntity<ResponseStructure<School>> updateSchool(@PathVariable int schoolId, @RequestBody School school) {
		return service.updateSchool(schoolId, school);
	}
	@DeleteMapping("/{schoolId}")
	public ResponseEntity<ResponseStructure<School>> deleteSchool(@PathVariable int schoolId) {
		return service.deleteSchool(schoolId);
	}
	@GetMapping("/findAll")
	public ResponseEntity<ResponseStructure<List<School>>> findAll() {
		return service.findAll();
	}
}