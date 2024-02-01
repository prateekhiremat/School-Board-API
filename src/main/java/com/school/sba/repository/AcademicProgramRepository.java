package com.school.sba.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.school.sba.entity.AcademicProgram;
@Repository
public interface AcademicProgramRepository extends JpaRepository<AcademicProgram, Integer> {

	List<Optional<AcademicProgram>> findByIsDeleted(boolean b);

}
