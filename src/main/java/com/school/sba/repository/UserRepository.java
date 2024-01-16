package com.school.sba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.school.sba.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
