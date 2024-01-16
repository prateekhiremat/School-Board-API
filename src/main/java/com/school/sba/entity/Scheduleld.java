package com.school.sba.entity;

import java.time.LocalTime;

import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Component
@Entity
@Setter
@Getter
public class Scheduleld {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int scheduleld;
	private LocalTime opensAt;
	private LocalTime closesAt;
	private int classHoursPerDay;
	private LocalTime classHourLength;
	private LocalTime breakTime;
	private LocalTime breakLength;
	private LocalTime lunchTime;
	private LocalTime lunchLength;
}
