package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestDTO.UserRequest;
import com.school.sba.responseDTO.UserResponce;
import com.school.sba.service.UserService;
import com.school.sba.util.ResponseStructure;

@RestController
@RequestMapping("/users")
public class UserController {
	@Autowired
	private UserService userService;
	@PostMapping("/register")
	public ResponseEntity<ResponseStructure<UserResponce>> saveUser(@RequestBody UserRequest userRequest){
		return userService.saveUser(userRequest);
	}
}
