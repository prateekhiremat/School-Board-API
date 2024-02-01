package com.school.sba.controller;

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

import com.school.sba.requestDTO.UserRequest;
import com.school.sba.responseDTO.UserResponce;
import com.school.sba.service.UserService;
import com.school.sba.util.ResponseStructure;

import jakarta.validation.Valid;

@RestController
public class UserController {
	@Autowired
	private UserService userService;
	@PostMapping("/users/register")
	public ResponseEntity<ResponseStructure<UserResponce>> saveAdmin(@RequestBody @Valid UserRequest userRequest){
		return userService.saveAdmin(userRequest);
	}
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/users")
	public ResponseEntity<ResponseStructure<UserResponce>> saveTeacherStudent(@RequestBody @Valid UserRequest userRequest){
		return userService.saveTeacherStudent(userRequest);
	}
	@GetMapping("/users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponce>> fetchById (@PathVariable int userId){
		return userService.fetchById(userId);
	}
	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponce>> deleteById(@PathVariable int userId){
		return userService.deleteById(userId);
	}
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/users/subjects/{subjectId}/users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponce>> addSubjectToTheTeacher(@PathVariable int subjectId, @PathVariable int userId){
		return userService.addSubjectToTheTeacher(subjectId, userId);
	}
}
