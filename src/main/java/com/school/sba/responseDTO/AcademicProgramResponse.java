package com.school.sba.responseDTO;

import java.time.LocalTime;

import com.school.sba.Enum.ProgramType;
import com.school.sba.entity.School;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AcademicProgramResponse {
	private int programId;
	private String programName;
	private ProgramType programType;
	private LocalTime beginsAt;
	private LocalTime endsAt;
}
