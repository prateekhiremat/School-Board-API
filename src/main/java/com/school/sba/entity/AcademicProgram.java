package com.school.sba.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AcademicProgram {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int programId;
//	private LocalTime beginsAt;
//	private LocalTime endsAt;
}
