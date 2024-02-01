package com.school.sba.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestDTO.ClassHourDTO;
import com.school.sba.responseDTO.ClassHourResponce;
import com.school.sba.service.ClassHourService;
import com.school.sba.util.ResponseStructure;

@RestController
public class ClassHourController {
	@Autowired
	private ClassHourService classHourService;
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/academicProgram/{programId}/class-hours")
	public ResponseEntity<String> generateClassHourForAcademicProgram(@PathVariable int programId){
		return classHourService.generateClassHourForAcademicProgram(programId);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/class-hours")
	public ResponseEntity<ResponseStructure<List<ClassHourResponce>>> updateClassHour(@RequestBody List<ClassHourDTO> classHourDTO){
		return classHourService.updateClassHour(classHourDTO);
	}
}
