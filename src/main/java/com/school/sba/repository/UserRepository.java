package com.school.sba.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.school.sba.Enum.UserRole;
import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.School;
import com.school.sba.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	public Optional<User> findByUserName(String userName);
	
	public List<Optional<User>> findByUserRoleAndAcademicProgram(UserRole userRole, AcademicProgram academicProgram);

	public List<User> findBySchool(School school);

	public List<Optional<User>> findByIsDeleated(boolean b);
}
