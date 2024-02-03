package com.school.sba.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.requestDTO.ClassHourDTO;
import com.school.sba.requestDTO.ExcelRequestDTO;
import com.school.sba.responseDTO.ClassHourResponce;
import com.school.sba.util.ResponseStructure;

public interface ClassHourService {

	ResponseEntity<String> generateClassHourForAcademicProgram(int programId);

	public ResponseEntity<ResponseStructure<List<ClassHourResponce>>> updateClassHour(List<ClassHourDTO> classHourDtoList);

	ResponseEntity<String> autoGenerateClassHour(int programId);

	ResponseEntity<String> acceptExcelSheet(int programId, ExcelRequestDTO excelRequestDTO);

	ResponseEntity<?> writeToExcelSheet(int programId, LocalDate fromDate, LocalDate toDate, MultipartFile file) throws IOException;

}
