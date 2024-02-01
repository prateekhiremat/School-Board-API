package com.school.sba.requestDTO;

import java.time.LocalDateTime;

import com.school.sba.Enum.ClassStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClassHourRequest {
	private LocalDateTime beginsAt;
	private LocalDateTime endsAt;
	private int roomNo;
	@Enumerated(EnumType.STRING)
	private ClassStatus classstatus;
}
