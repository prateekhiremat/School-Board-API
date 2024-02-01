package com.school.sba.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClassHourDTO {
	private int classHourId;
	private int subjectId;
	private int teacherId;
	private int roomNo;
}
