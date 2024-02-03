package com.school.sba.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.school.sba.Enum.UserRole;
import com.school.sba.entity.School;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Component
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledJobes {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SchoolRepository schoolRepository;
	@Autowired
	private AcademicProgramRepository academicProgramRepository;
	//1000L*60 = 1minute
	//1000l*60*60 = 1hour
//	@Scheduled(fixedDelay = 1000L*60)
	@Transactional
	public void delete() {
		userRepository.findByIsDeleated(true).forEach(user -> {
			if(!user.get().getUserRole().equals(UserRole.ADMIN) && user.get().isDeleated()==true)
				userRepository.delete(user.get());
		});
		schoolRepository.findByIsDeleted(true).forEach(school -> {
			if(school.get().isDeleted()==true) {
				schoolToUser(school.get());
				schoolRepository.delete(school.get());
			}
		});
		academicProgramRepository.findByIsDeleted(true).forEach(program -> {
			if(program.get().isDeleted()==true) {
				academicProgramRepository.delete(program.get());
			}
		});
	}

	private void schoolToUser(School school) {
		userRepository.findBySchool(school).forEach(user -> {
			if(user.getUserRole().equals(UserRole.ADMIN)) {
				user.setSchool(null);
				userRepository.save(user);
			}else {
				userRepository.delete(user);
			}
		});;
	}

}
