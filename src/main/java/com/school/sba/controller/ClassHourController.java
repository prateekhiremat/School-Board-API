package com.school.sba.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.requestDTO.ClassHourDTO;
import com.school.sba.requestDTO.ExcelRequestDTO;
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
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/academicProgram/{programId}/class-hours")
	public ResponseEntity<String> autoGenerateClassHour(@PathVariable int programId){
		return classHourService.autoGenerateClassHour(programId);
	}
	
	@PostMapping("/academic-program/{programId}/class-hour/write-excel")
	public ResponseEntity<String> acceptExcelSheet(@PathVariable int programId, @RequestBody ExcelRequestDTO excelRequestDTO){
		return classHourService.acceptExcelSheet(programId, excelRequestDTO);
	}
	
	@PostMapping("/academic-program/{programId}/class-hour/from/{fromDate}/to/{toDate}/write-excel")
	public ResponseEntity<?> writeToExcelSheet(@PathVariable int programId, @PathVariable LocalDate fromDate, @PathVariable LocalDate toDate, @RequestParam MultipartFile file) throws IOException{
		return classHourService.writeToExcelSheet(programId, fromDate, toDate, file);
	}
}
