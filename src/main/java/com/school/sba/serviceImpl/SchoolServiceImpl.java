package com.school.sba.serviceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.school.sba.Enum.UserRole;
import com.school.sba.entity.School;
import com.school.sba.exception.IllegalArgumentException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestDTO.SchoolRequest;
import com.school.sba.responseDTO.SchoolResponce;
import com.school.sba.service.SchoolService;
import com.school.sba.util.ResponseStructure;

@Service
public class SchoolServiceImpl implements SchoolService{
	@Autowired
	private SchoolRepository schoolRepository;
	@Autowired
	private UserRepository userRepository;
	@Override
	public ResponseEntity<ResponseStructure<SchoolResponce>> adminCreatesSchool(SchoolRequest schoolRequest) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findByUserName(name).map(user->{
			if(user.isDeleated()==true)
				throw new UserNotFoundByIdException("UserId has already been deleated!!!");
			if(user.getUserRole().equals(UserRole.ADMIN)) {
				if(user.getSchool()==null) {
					School school = schoolRepository.save(mapToSchool(schoolRequest));
					user.setSchool(school);
					userRepository.save(user);
					ResponseStructure<SchoolResponce> responseStructure = new ResponseStructure<SchoolResponce>();
					responseStructure.setStatus(HttpStatus.CREATED.value());
					responseStructure.setMessage("Student Object Created Successfully");
					responseStructure.setData(mapToResponse(school));
					return new ResponseEntity<ResponseStructure<SchoolResponce>>(responseStructure,HttpStatus.CREATED);
				}else
					throw new IllegalArgumentException("Admin has already accessed to school");
			}else
				throw new IllegalArgumentException("Only Admin can access to school");
		}).orElseThrow(()-> new UserNotFoundByIdException("UserId does not exist!!!"));
	}
	@Override
	public ResponseEntity<ResponseStructure<SchoolResponce>> deleteById(int schoolId) {
		return schoolRepository.findById(schoolId).map(school -> {
			if(school.isDeleted()==false) {
				school.setDeleted(true);
			}else {
				throw new IllegalArgumentException("Doesn't exist");
			}
			ResponseStructure<SchoolResponce> responseStructure = new ResponseStructure<SchoolResponce>();
			responseStructure.setStatus(HttpStatus.OK.value());
			responseStructure.setMessage("Deleted successfully!!!");
			responseStructure.setData(mapToResponse(schoolRepository.save(school)));
			return new ResponseEntity<ResponseStructure<SchoolResponce>>(responseStructure, HttpStatus.OK);
		}).orElseThrow(()-> new IllegalArgumentException("School Does Not Exist"));
	}
	private School mapToSchool(SchoolRequest schoolRequest) {
		return School.builder()
				.schoolName(schoolRequest.getSchoolName())
				.schoolEmailId(schoolRequest.getSchoolEmailId())
				.schoolAddress(schoolRequest.getSchoolAddress())
				.schoolContactNo(schoolRequest.getSchoolContactNo())
				.build();
	}
	private SchoolResponce mapToResponse(School school) {
		return SchoolResponce.builder()
				.schoolId(school.getSchoolId())
				.schoolName(school.getSchoolName())
				.schoolAddress(school.getSchoolAddress())
				.schoolContactNo(school.getSchoolContactNo())
				.schoolEmailId(school.getSchoolEmailId())
				.build();
	}
}