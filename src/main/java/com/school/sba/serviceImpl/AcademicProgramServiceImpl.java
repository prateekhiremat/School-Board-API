package com.school.sba.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.Enum.UserRole;
import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.School;
import com.school.sba.entity.Subject;
import com.school.sba.entity.User;
import com.school.sba.exception.IllegalArgumentException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.repository.UserRepository;
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
	@Autowired
	private UserRepository userRepository;
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
			return new ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>>(responseStructure, HttpStatus.OK);
		}).orElseThrow(()->new IllegalArgumentException("School Does Not Exist!!!"));
	}
	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> assignTeachersStudentsToAcademicProgram(
			int programId, int userId) {
		AcademicProgram academicProgram = academicProgramRepository.findById(programId).get();
		List<Subject> subjects = academicProgram.getSubjects();
		return userRepository.findById(userId).map(user->{
			if(user.isDeleated()==true)throw new UserNotFoundByIdException("UserId has already been deleated!!!");
			
			if(user.getUserRole().equals(UserRole.ADMIN))throw new IllegalArgumentException("Admin cannot be added to AcademicProgram");
			else if(user.getUserRole().equals(UserRole.TEACHER) && !subjects.contains(user.getSubject())) {
					throw new IllegalArgumentException("Teacher's subject Does not match");
			}
			else {
//				user.getAcademicProgram().add(academicProgram);
				academicProgram.getUser().add(user);
//				userRepository.save(user);
				academicProgramRepository.save(academicProgram);
				List<String> lists = new ArrayList<String>();
				List<User> list = academicProgram.getUser();
				for(User user2:list) {
					lists.add(user2.getUserName());
				}
				ResponseStructure<AcademicProgramResponse> responseStructure = new ResponseStructure<AcademicProgramResponse>();
				responseStructure.setStatus(HttpStatus.OK.value());
				responseStructure.setMessage("Assigned AcademicProgram successfully!!!");
				responseStructure.setData(mapToResponseList(academicProgram, lists));
				return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(responseStructure, HttpStatus.OK);
			}
		}).orElseThrow(()->new IllegalArgumentException("User Does Not Exist!!!"));
	}
	private AcademicProgramResponse mapToResponseList(AcademicProgram academicProgram, List<String> lists) {
		return AcademicProgramResponse.builder()
				.programId(academicProgram.getProgramId())
				.beginsAt(academicProgram.getBeginsAt())
				.endsAt(academicProgram.getEndsAt())
				.programName(academicProgram.getProgramName())
				.programType(academicProgram.getProgramType())
				.user(lists)
				.build();
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
	@Override
	public ResponseEntity<ResponseStructure<List<String>>> fetchUsersByRoleInAcademicProgram(int programId,
			String role) {
		if(role.equals("ADMIN"))
			throw new IllegalArgumentException("Admin Cannot be Fetched");
		List<String> usersByRole = new ArrayList<String>();
		userRepository.findByUserRoleAndAcademicProgram(UserRole.valueOf(role.toUpperCase()), academicProgramRepository.findById(programId).get())
		.forEach(user->{
			usersByRole.add(user.get().getUserName());
		});;
		if(usersByRole.isEmpty())
			throw new IllegalArgumentException("Cannot be Fetched");
		ResponseStructure<List<String>> responseStructure = new ResponseStructure<List<String>>();
		responseStructure.setStatus(HttpStatus.OK.value());
		responseStructure.setMessage("Fetched successfully!!!");
		responseStructure.setData(usersByRole);
		return new ResponseEntity<ResponseStructure<List<String>>>(responseStructure, HttpStatus.OK);
	}
	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> deleteById(int programId) {
		return academicProgramRepository.findById(programId).map(academicProgramById -> {
			if(academicProgramById.isDeleted()==false) {
				academicProgramById.setDeleted(true);
			}else {
				throw new IllegalArgumentException("Doesn't exist");
			}
			ResponseStructure<AcademicProgramResponse> responseStructure = new ResponseStructure<AcademicProgramResponse>();
			responseStructure.setStatus(HttpStatus.OK.value());
			responseStructure.setMessage("Deleted successfully!!!");
			responseStructure.setData(mapToResponse(academicProgramRepository.save(academicProgramById)));
			return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(responseStructure, HttpStatus.OK);
		}).orElseThrow(()-> new IllegalArgumentException("Academic Program Does Not Exist"));
	}
}
