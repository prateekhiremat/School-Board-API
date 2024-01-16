package com.school.sba.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.Enum.UserRole;
import com.school.sba.entity.User;
import com.school.sba.exception.AdminFoundException;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestDTO.UserRequest;
import com.school.sba.responseDTO.UserResponce;
import com.school.sba.service.UserService;
import com.school.sba.util.ResponseStructure;

@Service
public class UserServiceImp implements UserService {
	@Autowired
	private UserRepository userRepository;
	@Override
	public ResponseEntity<ResponseStructure<UserResponce>> saveUser(UserRequest userRequest) {
		List<User> findAll = userRepository.findAll();
		//		findAll.forEach(t -> t.getUserRole().compareTo(UserRole.ADMIN));
		for(User u : findAll) {
			if(u.getUserRole().toString()=="ADMIN")
				if(userRequest.getUserRole().toString()=="ADMIN")
					throw new AdminFoundException("Admin already exist");
		}
		User user = userRepository.save(mapToUser(userRequest));
		ResponseStructure<UserResponce> responseStructure = new ResponseStructure<UserResponce>();
		responseStructure.setStatus(HttpStatus.CREATED.value());
		responseStructure.setMessage("Saved Successfully!!!");
		responseStructure.setData(mapToResponse(user));
		return new ResponseEntity<ResponseStructure<UserResponce>>(responseStructure, HttpStatus.CREATED);
	}
	private UserResponce mapToResponse(User user) {
		return UserResponce.builder().userId(user.getUserId())
				.userName(user.getUserName())
				.userEmail(user.getUserEmail())
				.userContactNo(user.getUserContactNo())
				.userRole(user.getUserRole())
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.build();
	}
	private User mapToUser(UserRequest userRequest) {
		return User.builder().userName(userRequest.getUserName())
				.userContactNo(userRequest.getUserContactNo())
				.userEmail(userRequest.getUserEmail())
				.password(userRequest.getPassword())
				.firstName(userRequest.getFirstName())
				.lastName(userRequest.getLastName())
				.userRole(userRequest.getUserRole())
				.build();
	}

}
