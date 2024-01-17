package com.school.sba.serviceImpl;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.Enum.UserRole;
import com.school.sba.entity.School;
import com.school.sba.exception.IllegalArgumentException;
import com.school.sba.exception.SchoolNotFoundByIdException;
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
	public ResponseEntity<ResponseStructure<SchoolResponce>> adminCreatesSchool(int userId,
			SchoolRequest schoolRequest) {
		return userRepository.findById(userId).map(user->{
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
//	@Override
//	public ResponseEntity<ResponseStructure<School>> findSchool(int schoolId) {
//		Optional<School> optional = schoolRepository.findById(schoolId);
//		if(optional.isPresent()) {
//			School schl=optional.get();
//			ResponseStructure<School> responseStructure = new ResponseStructure<>();
//			responseStructure.setStatus(HttpStatus.FOUND.value());
//			responseStructure.setMessage("Student Object Found Successfully");
//			responseStructure.setData(schl);
//
//			return new ResponseEntity<ResponseStructure<School>>(responseStructure,HttpStatus.FOUND);
//
//		}else {
//			throw new SchoolNotFoundByIdException("School Not Found!!!");
//		}
//	}
//	@Override
//	public ResponseEntity<ResponseStructure<School>> updateSchool(int schoolId, School updatedSchool) {
//		Optional<School> optional = schoolRepository.findById(schoolId);
//
//		if(optional.isPresent()) {
//			School existinStudent = optional.get();
//			updatedSchool.setSchoolId(existinStudent.getSchoolId());
//			School schl = schoolRepository.save(updatedSchool);
//
//			ResponseStructure<School> responseStructure = new ResponseStructure<>();
//			responseStructure.setStatus(HttpStatus.OK.value());
//			responseStructure.setMessage("School Object Updated Successfully");
//			responseStructure.setData(schl);
//
//			return new ResponseEntity<ResponseStructure<School>>(responseStructure,HttpStatus.OK);	
//		}else {
//			throw new SchoolNotFoundByIdException("School Not Found!!!");
//		}
//	}
//	@Override
//	public ResponseEntity<ResponseStructure<School>> deleteSchool(int schoolId) {
//		Optional<School> optional = schoolRepository.findById(schoolId);
//		if(optional.isPresent()) {
//			School schl = optional.get();
//			schoolRepository.delete(schl);
//			ResponseStructure<School> responseStructure = new ResponseStructure<>();
//			responseStructure.setStatus(HttpStatus.OK.value());
//			responseStructure.setMessage("School Object deleted Successfully");
//			responseStructure.setData(schl);
//			return new ResponseEntity<ResponseStructure<School>>(responseStructure,HttpStatus.OK);
//		}else {
//			throw new SchoolNotFoundByIdException("School Not Found!!!");
//		}
//	}
//	public ResponseEntity<ResponseStructure<List<School>>> findAll(){
//		List<School> sl = schoolRepository.findAll();
//
//		if(sl.isEmpty()) {
//			return null;
//		}else {
//			ResponseStructure<List<School>> responseStructure = new ResponseStructure<>();
//			responseStructure.setStatus(HttpStatus.FOUND.value());
//			responseStructure.setMessage("School Objects Found Successfully");
//			responseStructure.setData(sl);
//			return new ResponseEntity<ResponseStructure<List<School>>>(responseStructure,HttpStatus.FOUND);
//		}
//	}
}