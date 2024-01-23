package com.school.sba.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.school.sba.repository.UserRepository;
@Service
public class CustomerUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUserName(username).map(user ->{
			return new CustomerUserDetails(user);
		}).orElseThrow(() -> new UsernameNotFoundException("User Not Exist"));
	}

}
