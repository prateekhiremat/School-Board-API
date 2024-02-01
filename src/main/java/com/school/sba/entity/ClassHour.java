package com.school.sba.entity;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.school.sba.Enum.ClassStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Component
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClassHour {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int classHourId;
	private LocalDateTime beginsAt;
	private LocalDateTime endsAt;
	private int roomNo;
	@Enumerated(EnumType.STRING)
	private ClassStatus classStatus;
	@ManyToOne
	private User user;
	@ManyToOne
	private Subject subject;
	@ManyToOne
	private AcademicProgram academicProgram;
}
