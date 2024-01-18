package com.school.sba.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.School;
import com.school.sba.exception.IllegalArgumentException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.requestDTO.AcademicProgramRequest;
import com.school.sba.responseDTO.AcademicProgramResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.util.ResponseStructure;
@Service
public class AcademicProgramServiceImpl implements AcademicProgramService {
	@Autowired
	private AcademicProgramRepository academicProgramRepository;
	@Autowired
	private SchoolRepository schoolRepository;
	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addsAcademicProgram(int schoolId,
			AcademicProgramRequest academicProgramRequest) {
		return schoolRepository.findById(schoolId).map(school->{
			AcademicProgram academicProgram = academicProgramRepository.save(mapToAcademicProgram(school, academicProgramRequest));
			school.getAcademicProgram().add(academicProgram);
			schoolRepository.save(school);
			ResponseStructure<AcademicProgramResponse> responseStructure = new ResponseStructure<AcademicProgramResponse>();
			responseStructure.setStatus(HttpStatus.CREATED.value());
			responseStructure.setMessage("AcademicProgram created successfully!!!");
			responseStructure.setData(mapToResponse(academicProgram));
			return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(responseStructure, HttpStatus.CREATED);
		}).orElseThrow(()->new IllegalArgumentException("School Does Not Exist!!!"));
	}
	@Override
	public ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>> fetchAllAcademicProgram(int schoolId) {
		return schoolRepository.findById(schoolId).map(school->{
			List<AcademicProgram> list = school.getAcademicProgram();
			List<AcademicProgramResponse> academicProgramList = new ArrayList<AcademicProgramResponse>();
			for(AcademicProgram a:list) {
				academicProgramList.add(mapToResponse(a));
			}
			ResponseStructure<List<AcademicProgramResponse>> responseStructure = new ResponseStructure<List<AcademicProgramResponse>>();
			responseStructure.setStatus(HttpStatus.OK.value());
			responseStructure.setMessage("AcademicProgram fetched successfully!!!");
			responseStructure.setData(academicProgramList);
			return new ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>>(responseStructure, HttpStatus.CREATED);
		}).orElseThrow(()->new IllegalArgumentException("School Does Not Exist!!!"));
	}
	private AcademicProgramResponse mapToResponse(AcademicProgram academicProgram) {
		return AcademicProgramResponse.builder()
				.programId(academicProgram.getProgramId())
				.beginsAt(academicProgram.getBeginsAt())
				.endsAt(academicProgram.getEndsAt())
				.programName(academicProgram.getProgramName())
				.programType(academicProgram.getProgramType())
				.build();
	}
	private AcademicProgram mapToAcademicProgram(School school, AcademicProgramRequest academicProgramRequest) {
		return AcademicProgram.builder()
				.beginsAt(academicProgramRequest.getBeginsAt())
				.endsAt(academicProgramRequest.getEndsAt())
				.programName(academicProgramRequest.getProgramName())
				.programType(academicProgramRequest.getProgramType())
				.school(school)
				.build();
	}
	
}
