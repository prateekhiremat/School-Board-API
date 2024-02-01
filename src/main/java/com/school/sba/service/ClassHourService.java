package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestDTO.ClassHourDTO;
import com.school.sba.responseDTO.ClassHourResponce;
import com.school.sba.util.ResponseStructure;

public interface ClassHourService {

	ResponseEntity<String> generateClassHourForAcademicProgram(int programId);

	public ResponseEntity<ResponseStructure<List<ClassHourResponce>>> updateClassHour(List<ClassHourDTO> classHourDtoList);

}
