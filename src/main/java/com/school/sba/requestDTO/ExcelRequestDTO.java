package com.school.sba.requestDTO;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExcelRequestDTO {
	private LocalDate fromDate;
	private LocalDate toDate;
	private String filePath;
}
