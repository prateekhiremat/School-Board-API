package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestDTO.UserRequest;
import com.school.sba.responseDTO.UserResponce;
import com.school.sba.service.UserService;
import com.school.sba.util.ResponseStructure;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
	@Autowired
	private UserService userService;
	@PostMapping("/register")
	public ResponseEntity<ResponseStructure<UserResponce>> saveUser(@RequestBody @Valid UserRequest userRequest){
		return userService.saveUser(userRequest);
	}
	@GetMapping("/{userId}")
	public ResponseEntity<ResponseStructure<UserResponce>> fetchById (@PathVariable int userId){
		return userService.fetchById(userId);
	}
	@DeleteMapping("/{userId}")
	public ResponseEntity<ResponseStructure<UserResponce>> deleteById(@PathVariable int userId){
		return userService.deleteById(userId);
	}
}
