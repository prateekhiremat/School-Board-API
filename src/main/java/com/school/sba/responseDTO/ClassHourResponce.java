package com.school.sba.responseDTO;

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
public class ClassHourResponce {
	private LocalDateTime beginsAt;
	private LocalDateTime endsAt;
	@Enumerated(EnumType.STRING)
	private ClassStatus classstatus;
	private int roomNo;
}
