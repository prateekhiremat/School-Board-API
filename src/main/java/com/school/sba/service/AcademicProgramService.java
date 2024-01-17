package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestDTO.AcademicProgramRequest;
import com.school.sba.responseDTO.AcademicProgramResponse;
import com.school.sba.util.ResponseStructure;

public interface AcademicProgramService {

	ResponseEntity<ResponseStructure<AcademicProgramResponse>> addsAcademicProgram(int schoolId,
			AcademicProgramRequest academicProgramRequest);

}
