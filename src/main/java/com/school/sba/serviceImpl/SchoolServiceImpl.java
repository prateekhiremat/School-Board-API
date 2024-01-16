package com.school.sba.serviceImpl;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.School;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.service.SchoolService;
import com.school.sba.util.ResponseStructure;

@Service
public class SchoolServiceImpl implements SchoolService{
	@Autowired
	private SchoolRepository schoolRepository;
	@Override
	public ResponseEntity<ResponseStructure<School>> addSchool(School school) {
		School sch = schoolRepository.save(school);

		ResponseStructure<School> responseStructure = new ResponseStructure<>();
		responseStructure.setStatus(HttpStatus.CREATED.value());
		responseStructure.setMessage("Student Object Created Successfully");
		responseStructure.setData(sch);

		return new ResponseEntity<ResponseStructure<School>>(responseStructure,HttpStatus.CREATED);
	}
	@Override
	public ResponseEntity<ResponseStructure<School>> findSchool(int schoolId) {
		Optional<School> optional = schoolRepository.findById(schoolId);
		if(optional.isPresent()) {
			School schl=optional.get();
			ResponseStructure<School> responseStructure = new ResponseStructure<>();
			responseStructure.setStatus(HttpStatus.FOUND.value());
			responseStructure.setMessage("Student Object Found Successfully");
			responseStructure.setData(schl);

			return new ResponseEntity<ResponseStructure<School>>(responseStructure,HttpStatus.FOUND);

		}else {
			throw new SchoolNotFoundByIdException("School Not Found!!!");
		}
	}
	@Override
	public ResponseEntity<ResponseStructure<School>> updateSchool(int schoolId, School updatedSchool) {
		Optional<School> optional = schoolRepository.findById(schoolId);

		if(optional.isPresent()) {
			School existinStudent = optional.get();
			updatedSchool.setSchoolId(existinStudent.getSchoolId());
			School schl = schoolRepository.save(updatedSchool);

			ResponseStructure<School> responseStructure = new ResponseStructure<>();
			responseStructure.setStatus(HttpStatus.OK.value());
			responseStructure.setMessage("School Object Updated Successfully");
			responseStructure.setData(schl);

			return new ResponseEntity<ResponseStructure<School>>(responseStructure,HttpStatus.OK);	
		}else {
			throw new SchoolNotFoundByIdException("School Not Found!!!");
		}
	}
	@Override
	public ResponseEntity<ResponseStructure<School>> deleteSchool(int schoolId) {
		Optional<School> optional = schoolRepository.findById(schoolId);

		if(optional.isPresent()) {
			School schl = optional.get();
			schoolRepository.delete(schl);

			ResponseStructure<School> responseStructure = new ResponseStructure<>();
			responseStructure.setStatus(HttpStatus.OK.value());
			responseStructure.setMessage("School Object deleted Successfully");
			responseStructure.setData(schl);

			return new ResponseEntity<ResponseStructure<School>>(responseStructure,HttpStatus.OK);
		}else {
			throw new SchoolNotFoundByIdException("School Not Found!!!");
		}
	}

	public ResponseEntity<ResponseStructure<List<School>>> findAll(){
		List<School> sl = schoolRepository.findAll();

		if(sl.isEmpty()) {
			return null;

		}else {


			ResponseStructure<List<School>> responseStructure = new ResponseStructure<>();
			responseStructure.setStatus(HttpStatus.FOUND.value());
			responseStructure.setMessage("School Objects Found Successfully");
			responseStructure.setData(sl);

			return new ResponseEntity<ResponseStructure<List<School>>>(responseStructure,HttpStatus.FOUND);

		}
	}
}