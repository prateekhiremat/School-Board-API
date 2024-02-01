package com.school.sba.serviceImpl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.Enum.ClassStatus;
import com.school.sba.Enum.UserRole;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Scheduleld;
import com.school.sba.entity.Subject;
import com.school.sba.entity.User;
import com.school.sba.exception.IllegalArgumentException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.repository.SubjectRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestDTO.ClassHourDTO;
import com.school.sba.responseDTO.ClassHourResponce;
import com.school.sba.service.ClassHourService;
import com.school.sba.util.ResponseStructure;

@Service
public class ClassHourServiceImpl implements ClassHourService {
	@Autowired
	private AcademicProgramRepository academicProgramRepository;
	@Autowired
	private ClassHourRepository classHourRepository;
	@Autowired
	private SubjectRepository subjectRepository;
	@Autowired
	private UserRepository userRepository;
	@Override
	public ResponseEntity<String> generateClassHourForAcademicProgram(int programId) {
		//		classHourRepository.deleteAll();
		return academicProgramRepository.findById(programId).map(academicProgram->{
			Scheduleld scheduleld = academicProgram.getSchool().getScheduleld();
			if(scheduleld != null) {
				int classHoursPerDay = scheduleld.getClassHoursPerDay();
				int classHourLength = (int)scheduleld.getClassHourLengthInMin().toMinutes();

				LocalDateTime currentTime = LocalDateTime.now().with(scheduleld.getOpensAt());
				// Pre-calculate time renges for clarity
				LocalTime lunchTimeStart = scheduleld.getLunchTime();
				LocalTime lunchTimeEnd = lunchTimeStart.plusMinutes(scheduleld.getLunchLengthInMin().toMinutes());
				LocalTime breakTimeStart = scheduleld.getBreakTime();
				LocalTime breakTimeEnd = breakTimeStart.plusMinutes(scheduleld.getBreakLengthInMin().toMinutes());

				for(int day=1;day<=6;day++) {
					for(int hour=0;hour<classHoursPerDay;hour++) {
						ClassHour classHour = new ClassHour();
						//Assign a value to roomNo (assuming it's madatory)
						classHour.setRoomNo(100);

						if ((currentTime.toLocalTime().isAfter(breakTimeStart) && currentTime.toLocalTime().isBefore(breakTimeEnd)) || 
								(currentTime.toLocalTime().equals(breakTimeStart) && currentTime.toLocalTime().equals(breakTimeEnd)) ||
								(currentTime.toLocalTime().isBefore(breakTimeStart) && 
										(currentTime.toLocalTime().isAfter(breakTimeStart)&&currentTime.toLocalTime().isBefore(breakTimeEnd))) ||
								((currentTime.toLocalTime().isAfter(breakTimeStart) && currentTime.toLocalTime().isBefore(lunchTimeEnd)) && 
										currentTime.toLocalTime().isAfter(breakTimeEnd))) {
						    
							classHour.setBeginsAt(currentTime);
						    classHour.setEndsAt(currentTime.plusMinutes(scheduleld.getBreakLengthInMin().toMinutes()));
						    classHour.setClassStatus(ClassStatus.BREAK_TIMINGS);
						    currentTime = classHour.getEndsAt();
						
						} else if ((currentTime.toLocalTime().isAfter(lunchTimeStart) && currentTime.toLocalTime().isBefore(lunchTimeEnd)) || 
								(currentTime.toLocalTime().equals(lunchTimeStart) && currentTime.toLocalTime().equals(lunchTimeEnd)) ||
								(currentTime.toLocalTime().isBefore(lunchTimeStart) && 
										(currentTime.toLocalTime().isAfter(lunchTimeStart)&&currentTime.toLocalTime().isBefore(lunchTimeEnd))) ||
								((currentTime.toLocalTime().isAfter(lunchTimeStart) && currentTime.toLocalTime().isBefore(lunchTimeEnd)) && 
										currentTime.toLocalTime().isAfter(lunchTimeEnd))) {
						    
							classHour.setBeginsAt(currentTime);
						    classHour.setEndsAt(currentTime.plusMinutes(scheduleld.getLunchLengthInMin().toMinutes()));
						    classHour.setClassStatus(ClassStatus.LUNCH_TIMINGS);
						    currentTime = classHour.getEndsAt();
						
						} else {
							
						    LocalDateTime beginsAt = currentTime;
						    LocalDateTime endsAt = beginsAt.plusMinutes(classHourLength);

						    classHour.setBeginsAt(beginsAt);
						    classHour.setEndsAt(endsAt);
						    classHour.setClassStatus(ClassStatus.NOT_SCHEDULED);

						    currentTime = endsAt;
						}

						classHour.setAcademicProgram(academicProgram);
						classHourRepository.save(classHour);
					}
					currentTime = currentTime.plusDays(1).with(scheduleld.getOpensAt());
				}
				return ResponseEntity.status(HttpStatus.CREATED)
						.body("Class Hour generated for the current week successfully");
			}else {
				throw new IllegalArgumentException("School doesn't have schedule");
			}
		}).orElseThrow(() -> new IllegalArgumentException("Invalid Program"));
	}
	@Override
	public ResponseEntity<ResponseStructure<List<ClassHourResponce>>> updateClassHour(List<ClassHourDTO> classHourDtoList) {
		List<ClassHourResponce> updatedClassHourResponses = new ArrayList<>();

		classHourDtoList.forEach(classHourDTO -> {
			ClassHour existingClassHour = classHourRepository.findById(classHourDTO.getClassHourId()).get();
			Subject subject=subjectRepository.findById(classHourDTO.getSubjectId()).get();
			User teacher=userRepository.findById(classHourDTO.getTeacherId()).get();

			if(existingClassHour != null && subject != null && teacher != null && teacher.getUserRole().equals(UserRole.TEACHER)) {

				if((teacher.getSubject()).equals(subject))
					existingClassHour.setSubject(subject);
				else
					throw new IllegalArgumentException("The Teacher is Not Teaching That Subject");
				existingClassHour.setUser(teacher);
				existingClassHour.setRoomNo(classHourDTO.getRoomNo());
				LocalDateTime currentTime = LocalDateTime.now();

				if (existingClassHour.getBeginsAt().isBefore(currentTime) && existingClassHour.getEndsAt().isAfter(currentTime)) {
					existingClassHour.setClassStatus(ClassStatus.ONGOING);
				} else if (existingClassHour.getEndsAt().isBefore(currentTime)) {
					existingClassHour.setClassStatus(ClassStatus.COMPLETED);
				} else {
					existingClassHour.setClassStatus(ClassStatus.UPCOMING);
				}

				existingClassHour=classHourRepository.save(existingClassHour);

				ClassHourResponce classHourResponse = new ClassHourResponce();
				classHourResponse.setBeginsAt(existingClassHour.getBeginsAt());
				classHourResponse.setEndsAt(existingClassHour.getEndsAt());
				classHourResponse.setClassstatus(existingClassHour.getClassStatus());
				classHourResponse.setRoomNo(existingClassHour.getRoomNo());
				updatedClassHourResponses.add(classHourResponse);

			} 
			else {
				throw new IllegalArgumentException("Invalid Teacher Id.");
			}
		});
		ResponseStructure<List<ClassHourResponce>> responseStructure = new ResponseStructure<>();
		responseStructure.setStatus(HttpStatus.CREATED.value());
		responseStructure.setMessage("ClassHours updated successfully!!!!");
		responseStructure.setData(updatedClassHourResponses);

		return new ResponseEntity<ResponseStructure<List<ClassHourResponce>>>(responseStructure, HttpStatus.CREATED);
	}
	public ResponseEntity<String> deleteClassHour(List<ClassHour> classHours){
		for(ClassHour classHour : classHours) {
			classHourRepository.delete(classHour);
		}
		return new ResponseEntity<String>("Deleted Successfully", HttpStatus.OK);
	}
}
