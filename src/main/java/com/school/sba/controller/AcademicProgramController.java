package com.school.sba.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/schools/{schoolId}/academicPrograms")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addsAcademicProgram(@PathVariable int schoolId, @RequestBody AcademicProgramRequest academicProgramRequest){
		return academicProgramService.addsAcademicProgram(schoolId, academicProgramRequest);
	}
	@GetMapping("/schools/{schoolId}/academicPrograms")
	public ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>> fetchAllAcademicProgram(@PathVariable int schoolId){
		return academicProgramService.fetchAllAcademicProgram(schoolId);
	}
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/academicPrograms/{programId}/users/{userId}")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> assignTeachersStudentsToAcademicProgram(@PathVariable int programId, @PathVariable int userId){
		return academicProgramService.assignTeachersStudentsToAcademicProgram(programId, userId);
	}
	@GetMapping("/academic-programs/{programId}/user-roles/{role}/users")
	public ResponseEntity<ResponseStructure<List<String>>> fetchUsersByRoleInAcademicProgram(@PathVariable int programId, @PathVariable String role){
		return academicProgramService.fetchUsersByRoleInAcademicProgram(programId, role);
	}
	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/academicPrograms/delete/{programId}")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> deleteById(@PathVariable int programId){
		return academicProgramService.deleteById(programId);
	}
}
