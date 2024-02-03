package com.school.sba.serviceImpl;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Scheduleld;
import com.school.sba.entity.School;
import com.school.sba.exception.IllegalArgumentException;
import com.school.sba.repository.ScheduleldRepository;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.requestDTO.ScheduleldRequest;
import com.school.sba.responseDTO.ScheduleldResponse;
import com.school.sba.service.ScheduleldService;
import com.school.sba.util.ResponseStructure;
@Service
public class ScheduleldServiceImpl implements ScheduleldService {
	@Autowired
	private ScheduleldRepository scheduleldRepository;
	@Autowired
	private SchoolRepository schoolRepository;
	@Override
	public ResponseEntity<ResponseStructure<ScheduleldResponse>> adminCreatesSchedule(int schoolId,
			ScheduleldRequest scheduleldRequest) {
		return schoolRepository.findById(schoolId).map(school->{
			if(school.getScheduleld()==null) {
				Scheduleld scheduleld = scheduleldRepository.save(mapToScheduleld(school,scheduleldRequest));
//				validateSchedule(scheduleld);
//				validateBreakAndLunch(scheduleld);
				school.setScheduleld(scheduleld);
				schoolRepository.save(school);
				ResponseStructure<ScheduleldResponse> responseStructure = new ResponseStructure<ScheduleldResponse>();
				responseStructure.setStatus(HttpStatus.CREATED.value());
				responseStructure.setMessage("Scheduleld successfully!!!!");
				responseStructure.setData(mapToResponse(scheduleld));
				return new ResponseEntity<ResponseStructure<ScheduleldResponse>>(responseStructure, HttpStatus.CREATED);
			}else {
				throw new IllegalArgumentException("School Schedule Already Exist");
			}
		}).orElseThrow(()->new IllegalArgumentException("School Does Not Exist!!!"));
	}
	@Override
	public ResponseEntity<ResponseStructure<ScheduleldResponse>> findScheduleBySchoolId(int schoolId) {
		return schoolRepository.findById(schoolId).map(school->{
			ResponseStructure<ScheduleldResponse> responseStructure = new ResponseStructure<ScheduleldResponse>();
			responseStructure.setStatus(HttpStatus.FOUND.value());
			responseStructure.setMessage("Scheduleld Fetched successfully!!!!");
			responseStructure.setData(mapToResponse(school.getScheduleld()));
			return new ResponseEntity<ResponseStructure<ScheduleldResponse>>(responseStructure, HttpStatus.FOUND);
		}).orElseThrow(()->new IllegalArgumentException("School Does Not Exist!!!"));
	}
	@Override
	public ResponseEntity<ResponseStructure<ScheduleldResponse>> updateScheduleById(int scheduleldId,
			ScheduleldRequest scheduleldRequest) {
		return scheduleldRepository.findById(scheduleldId).map(scheduleld->{
			scheduleldRepository.save(mapToUpdate(scheduleldId, scheduleldRequest));
			ResponseStructure<ScheduleldResponse> responseStructure = new ResponseStructure<ScheduleldResponse>();
			responseStructure.setStatus(HttpStatus.OK.value());
			responseStructure.setMessage("Scheduleld Updated successfully!!!!");
			responseStructure.setData(mapToResponse(scheduleld));
			return new ResponseEntity<ResponseStructure<ScheduleldResponse>>(responseStructure, HttpStatus.CREATED);
		}).orElseThrow(()->new IllegalArgumentException("Scheduleld Does Not Exist!!!"));
	}
	private Scheduleld mapToUpdate(int scheduleldId, ScheduleldRequest scheduleldRequest) {
		return Scheduleld.builder()
				.scheduleld(scheduleldId)
				.breakLengthInMin(Duration.ofMinutes(scheduleldRequest.getBreakLengthInMin()))
				.breakTime(scheduleldRequest.getBreakTime())
				.classHourLengthInMin(Duration.ofMinutes(scheduleldRequest.getClassHourLengthInMin()))
				.classHoursPerDay(scheduleldRequest.getClassHoursPerDay())
				.closesAt(scheduleldRequest.getClosesAt())
				.opensAt(scheduleldRequest.getOpensAt())
				.lunchLengthInMin(Duration.ofMinutes(scheduleldRequest.getLunchLengthInMin()))
				.lunchTime(scheduleldRequest.getLunchTime())
				.build();
	}
	private ScheduleldResponse mapToResponse(Scheduleld scheduleld) {
		return ScheduleldResponse.builder()
				.scheduleld(scheduleld.getScheduleld())
				.breakLengthInMin((int)scheduleld.getBreakLengthInMin().toMinutes())
				.breakTime(scheduleld.getBreakTime())
				.classHourLengthInMin((int)scheduleld.getClassHourLengthInMin().toMinutes())
				.classHoursPerDay(scheduleld.getClassHoursPerDay())
				.closesAt(scheduleld.getClosesAt())
				.opensAt(scheduleld.getOpensAt())
				.lunchLengthInMin((int)scheduleld.getLunchLengthInMin().toMinutes())
				.lunchTime(scheduleld.getLunchTime())
				.build();
	}
	private Scheduleld mapToScheduleld(School school, ScheduleldRequest scheduleldRequest) {
		return Scheduleld.builder()
				.breakLengthInMin(Duration.ofMinutes(scheduleldRequest.getBreakLengthInMin()))
				.breakTime(scheduleldRequest.getBreakTime())
				.classHourLengthInMin(Duration.ofMinutes(scheduleldRequest.getClassHourLengthInMin()))
				.classHoursPerDay(scheduleldRequest.getClassHoursPerDay())
				.closesAt(scheduleldRequest.getClosesAt())
				.opensAt(scheduleldRequest.getOpensAt())
				.lunchLengthInMin(Duration.ofMinutes(scheduleldRequest.getLunchLengthInMin()))
				.lunchTime(scheduleldRequest.getLunchTime())
				.build();
	}
	public ResponseEntity<ResponseStructure<ScheduleldResponse>> deleteSchedule(Scheduleld scheduleld){
		scheduleldRepository.delete(scheduleld);
		ResponseStructure<ScheduleldResponse> responseStructure = new ResponseStructure<ScheduleldResponse>();
		responseStructure.setStatus(HttpStatus.OK.value());
		responseStructure.setMessage("Deleted successfully!!!!");
		responseStructure.setData(mapToResponse(scheduleld));
		return new ResponseEntity<ResponseStructure<ScheduleldResponse>>(responseStructure, HttpStatus.OK);
	}

	private void validateSchedule(Scheduleld scheduleld) {
		LocalTime opensAt = scheduleld.getOpensAt();
		LocalTime closesAt = scheduleld.getClosesAt();

		long totalMinutes = opensAt.until(closesAt, ChronoUnit.MINUTES);
		long expectedTotalTime =((scheduleld.getClassHoursPerDay()-2) * scheduleld.getClassHourLengthInMin().toMinutes()) +
				scheduleld.getBreakLengthInMin().toMinutes() + scheduleld.getLunchLengthInMin().toMinutes();

		if(totalMinutes != expectedTotalTime)
			throw new IllegalArgumentException("Total hour do not add up correctly");
	}

	private void validateBreakAndLunch(Scheduleld scheduleld) {
		LocalTime breakTime = scheduleld.getBreakTime();
		LocalTime lunchTime = scheduleld.getLunchTime();
		LocalTime opensAt = scheduleld.getOpensAt();
		Duration classHourLengthInMin = scheduleld.getClassHourLengthInMin();
		long totalBreak = opensAt.until(breakTime, ChronoUnit.MINUTES);
		long totalLunch = opensAt.until(lunchTime, ChronoUnit.MINUTES);
		totalLunch = totalBreak - scheduleld.getLunchLengthInMin().toMinutes();

		if(!(totalBreak%(classHourLengthInMin.toMinutes())==0 && totalLunch%classHourLengthInMin.toMinutes() ==0))
			throw new IllegalArgumentException("Total hour do not add up correctly");
	}
}
